/**
 * API调试工具
 * 用于在浏览器控制台中手动测试API
 */

import { authApi } from '@/api'

// 在window对象上挂载调试函数，方便在控制台使用
declare global {
  interface Window {
    testLogin: (username: string, password: string) => Promise<void>
    testApi: any
  }
}

// 测试登录API
window.testLogin = async (username: string, password: string) => {
  try {
    console.log('测试登录API...')
    console.log('请求参数:', { username, password: '***' })
    
    const result = await authApi.login({ username, password })
    console.log('登录成功，响应数据:', result)
    
    if (result.token) {
      console.log('Token:', result.token)
      authApi.setToken(result.token)
      console.log('Token已保存到localStorage')
    } else {
      console.error('响应中没有token字段')
    }
  } catch (error) {
    console.error('登录测试失败:', error)
  }
}

// 挂载所有API到window对象，方便调试
window.testApi = {
  auth: authApi,
  // 可以在这里添加其他API模块
}

console.log('API调试工具已加载！')
console.log('使用方法：')
console.log('  window.testLogin("username", "password") - 测试登录')
console.log('  window.testApi.auth - 访问认证API')

export {}
