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

// 导出API配置
export { API_CONFIG } from './config'
