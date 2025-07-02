import axios from 'axios'
import type { AxiosInstance, AxiosResponse, InternalAxiosRequestConfig } from 'axios'
import { useUiStateStore } from '@/stores/uiStateStore'

// 创建 axios 实例
const service: AxiosInstance = axios.create({
  baseURL: 'http://localhost:8080', // API 的基础URL
  timeout: 15000, // 请求超时时间
  headers: {
    'Content-Type': 'application/json;charset=utf-8',
  },
})

// 请求拦截器
service.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // 添加请求日志
    console.log('发送API请求:', {
      url: config.url,
      method: config.method,
      baseURL: config.baseURL,
      fullURL: `${config.baseURL}${config.url}`,
      headers: config.headers,
      data: config.data
    })
    
    // 从localStorage获取token
    const token = localStorage.getItem('token')
    // 根据接口文档，需要在请求头添加 Bearer Token
    if (token && config.headers) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    console.error('请求错误: ', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  (response: AxiosResponse) => {
    // 添加调试日志
    console.log('API响应详情:', {
      url: response.config.url,
      method: response.config.method,
      status: response.status,
      headers: response.headers,
      data: response.data
    })
    
    // 登录接口特殊处理 - 根据接口文档，登录成功直接返回token等字段
    if (response.config.url?.includes('/api/auth/login')) {
      if (response.data && response.data.token) {
        console.log('登录成功，返回token数据')
        return response.data
      } else {
        console.error('登录接口响应格式异常:', response.data)
        // 继续往下处理
      }
    }
    
    // 标准API响应格式 {success: true, data: {}, message: ""}
    if (response.data && response.data.success === true) {
      console.log('标准API响应，返回data字段')
      return response.data
    }
    
    // HTTP 200但业务失败的情况
    if (response.status === 200 && response.data && response.data.success === false) {
      const errorMsg = response.data.message || '业务操作失败'
      console.error('业务失败:', errorMsg)
      return Promise.reject(new Error(errorMsg))
    }
    
    // 如果是200状态码但格式不标准，尝试直接返回
    if (response.status >= 200 && response.status < 300) {
      console.warn('非标准响应格式，直接返回原始数据:', response.data)
      return response.data
    }
    
    // 其他情况视为错误
    const errorMsg = response.data?.message || `请求失败 (${response.status})`
    console.error('响应错误:', errorMsg)
    return Promise.reject(new Error(errorMsg))
  },
  (error) => {
    console.error('========== Axios 响应错误详情 ==========')
    console.error('错误对象:', error)
    console.error('错误消息:', error.message)
    
    if (error.response) {
      // 服务器响应了，但状态码不在 2xx 范围内
      console.error('响应状态码:', error.response.status)
      console.error('响应状态文本:', error.response.statusText)
      console.error('响应头:', error.response.headers)
      console.error('响应数据:', error.response.data)
      console.error('请求配置:', error.config)
      
      switch (error.response.status) {
        case 401:
          localStorage.removeItem('token')
          const uiStateStore = useUiStateStore()
          uiStateStore.openLogin()
          console.error('❌ 401: 认证失败，已清除token并打开登录窗口')
          break
        case 403:
          console.error('❌ 403: 权限不足，无法执行此操作')
          break
        case 404:
          console.error('❌ 404: 请求的资源不存在')
          break
        case 500:
          console.error('❌ 500: 服务器内部错误，请稍后重试')
          break
        default:
          console.error(`❌ ${error.response.status}: 请求失败`)
      }
    } else if (error.request) {
      // 请求已发送但没有收到响应
      console.error('❌ 网络错误: 请求已发送但服务器无响应')
      console.error('请求对象:', error.request)
      console.error('请求配置:', error.config)
    } else {
      // 请求配置出错
      console.error('❌ 请求配置错误:', error.message)
      console.error('错误配置:', error.config)
    }
    
    console.error('========================================')
    
    return Promise.reject(error)
  }
)

export default service