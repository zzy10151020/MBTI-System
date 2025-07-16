import service from './axios'
import type { 
  ApiResponse,
  LoginRequest, 
  LoginResponse, 
  RegisterRequest, 
  RegisterResponse,
  User,
  CheckUsernameResponse,
  CheckEmailResponse
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
    const response = await service.post<any, ApiResponse<LoginResponse>>('/api/auth/login', data)
    // 登录成功后保存用户信息到localStorage
    if (response.success && response.data) {
      localStorage.setItem('userInfo', JSON.stringify(response.data.user))
    }
    return response.data
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
   * 退出登录
   */
  async logout(): Promise<void> {
    try {
      await service.post('/api/auth/logout')
    } catch (error) {
      console.error('注销请求失败:', error)
    } finally {
      // 无论请求是否成功，都清除本地数据
      localStorage.removeItem('userInfo')
    }
  },

  /**
   * 检查用户名是否存在
   * @param username 用户名
   */
  async checkUsername(username: string): Promise<CheckUsernameResponse> {
    const response = await service.get<any, ApiResponse<CheckUsernameResponse>>(`/api/auth/checkUsername?username=${username}`)
    return response.data
  },

  /**
   * 检查邮箱是否存在
   * @param email 邮箱
   */
  async checkEmail(email: string): Promise<CheckEmailResponse> {
    const response = await service.get<any, ApiResponse<CheckEmailResponse>>(`/api/auth/checkEmail?email=${email}`)
    return response.data
  },

  /**
   * 检查是否已登录
   */
  isLoggedIn(): boolean {
    const userInfo = localStorage.getItem('userInfo')
    return !!userInfo
  },

  /**
   * 获取当前用户信息
   */
  getCurrentUser(): User | null {
    const userInfo = localStorage.getItem('userInfo')
    return userInfo ? JSON.parse(userInfo) : null
  },

  /**
   * 设置用户信息
   * @param user 用户信息
   */
  setUserInfo(user: User): void {
    localStorage.setItem('userInfo', JSON.stringify(user))
  },

  /**
   * 清除用户信息
   */
  clearUserInfo(): void {
    localStorage.removeItem('userInfo')
  }
}

export default authApi
