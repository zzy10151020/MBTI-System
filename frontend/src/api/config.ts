// API基础配置
export const API_CONFIG = {
  TIMEOUT: 15000,
  // 根据环境动态配置
  get baseURL() {
    return process.env.NODE_ENV === 'development' 
      ? '' // 开发环境使用相对路径，通过Vite代理
      : 'https://your-production-api.com/mbti-system'
  }
}
