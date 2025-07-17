import axios from 'axios'
import type { AxiosInstance, AxiosResponse, InternalAxiosRequestConfig } from 'axios'
import { useUiStateStore } from '@/stores/uiStateStore'
import { API_CONFIG } from './config'

// 创建 axios 实例
const service: AxiosInstance = axios.create({
  baseURL: API_CONFIG.baseURL, // 使用统一的API配置
  timeout: API_CONFIG.TIMEOUT, // 使用统一的超时配置
  withCredentials: true, // 启用Session Cookie
  headers: {
    'Content-Type': 'application/json;charset=utf-8',
  },
})

// 请求拦截器
service.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // Session认证不需要手动添加Authorization header
    // Cookie会自动携带JSESSIONID
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
    // 标准API响应格式 {success: true, data: {}, message: "", timestamp: number}
    if (response.data && response.data.success === true) {
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
      return response.data
    }
    
    // 其他情况视为错误
    const errorMsg = response.data?.message || `请求失败 (${response.status})`
    console.error('响应错误:', errorMsg)
    return Promise.reject(new Error(errorMsg))
  },
  (error) => {
    console.error('网络请求错误:', error.message)
    
    if (error.response) {
      // 服务器响应了，但状态码不在 2xx 范围内
      switch (error.response.status) {
        case 401:
          // Session过期，清除本地存储并打开登录窗口
          localStorage.removeItem('userInfo')
          const uiStateStore = useUiStateStore()
          uiStateStore.openLogin()
          console.error('❌ 401: Session过期或未登录，已打开登录窗口')
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
    } else {
      // 请求配置出错
      console.error('❌ 请求配置错误:', error.message)
    }
    
    return Promise.reject(error)
  }
)

export default service