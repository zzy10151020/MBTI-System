// 统一导出所有API模块
export { authApi } from './auth'
export { userApi } from './user'
export { questionnaireApi } from './questionnaire'
export { testApi } from './test'
export { questionApi } from './question'

// 导出类型
export type * from './types'

// 导出axios实例（如果需要直接使用）
export { default as request } from './axios'

// API基础配置
export const API_CONFIG = {
  BASE_URL: 'http://localhost:8080',
  TIMEOUT: 15000,
  // 可以根据环境动态配置
  get baseURL() {
    return process.env.NODE_ENV === 'development' 
      ? 'http://localhost:8080' 
      : 'https://your-production-api.com'
  }
}
