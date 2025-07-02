import service from './axios'
import type { 
  ApiResponse,
  LoginRequest, 
  LoginResponse, 
  RegisterRequest, 
  RegisterResponse 
} from './types'

/**
 * 认证相关API
 */
export const authApi = {
  /**
   * 用户登录
   * @param data 登录请求数据
   */
  async login(data: LoginRequest): Promise<LoginResponse> {
    const response = await service.post<any, LoginResponse>('/api/auth/login', data)
    return response // 登录接口直接返回数据，不需要解包
  },

  /**
   * 用户注册
   * @param data 注册请求数据
   */
  async register(data: RegisterRequest): Promise<RegisterResponse> {
    const response = await service.post<any, ApiResponse<RegisterResponse>>('/api/auth/register', data)
    return response.data
  },

  /**
   * 退出登录 (客户端清除token)
   */
  logout(): void {
    localStorage.removeItem('token')
  },

  /**
   * 检查是否已登录
   */
  isLoggedIn(): boolean {
    const token = localStorage.getItem('token')
    return !!token
  },

  /**
   * 获取当前用户token
   */
  getToken(): string | null {
    return localStorage.getItem('token')
  },

  /**
   * 设置token
   * @param token JWT令牌
   */
  setToken(token: string): void {
    localStorage.setItem('token', token)
  }
}

export default authApi
