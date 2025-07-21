import service from './axios'
import type {
  LoginRequest,
  RegisterRequest,
  CheckUsernameRequest,
  CheckEmailRequest,
} from './types'

export const authApi = {
  // 用户登录
  login: async (data: LoginRequest): Promise<any> => {
    const response = await service.post('/api/auth/login', data)
    return response.data!
  },

  // 用户注册
  register: async (data: RegisterRequest): Promise<any> => {
    const response = await service.post('/api/auth/register', data)
    return response.data!
  },

  // 用户登出
  logout: async (): Promise<any> => {
    const response = await service.post('/api/auth/logout')
    return response.data!
  },

  // 检查用户名是否存在
  checkUsername: async (data: CheckUsernameRequest): Promise<any> => {
    const response = await service.post('/api/auth/check-username', data)
    return response.data!
  },

  // 检查邮箱是否存在
  checkEmail: async (data: CheckEmailRequest): Promise<any> => {
    const response = await service.post('/api/auth/check-email', data)
    return response.data!
  },
}

export default authApi
