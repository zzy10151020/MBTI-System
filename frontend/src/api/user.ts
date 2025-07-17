import service from './axios'
import type { 
  ApiResponse,
  User,
  UpdateUserRequest,
  ChangePasswordRequest
} from './types'

/**
 * 用户管理相关API
 */
export const userApi = {
  /**
   * 获取当前用户信息
   */
  async getProfile(): Promise<User> {
    // 从localStorage获取用户ID
    const userInfo = localStorage.getItem('userInfo')
    if (!userInfo) {
      throw new Error('用户未登录，请先登录')
    }
    
    try {
      const user = JSON.parse(userInfo)
      if (!user.userId) {
        throw new Error('用户ID不存在，请重新登录')
      }
      
      const response = await service.get<any, ApiResponse<User>>(`/api/user/profile?userId=${user.userId}`)
      return response.data
    } catch (error) {
      if (error instanceof SyntaxError) {
        // localStorage中的数据格式有问题
        localStorage.removeItem('userInfo')
        throw new Error('用户信息格式错误，请重新登录')
      }
      throw error
    }
  },

  /**
   * 更新当前用户信息
   * @param data 更新数据
   */
  async updateProfile(data: { email?: string }): Promise<User> {
    // 获取当前用户ID
    const userInfo = localStorage.getItem('userInfo')
    if (!userInfo) {
      throw new Error('用户未登录，请先登录')
    }
    
    const user = JSON.parse(userInfo)
    const requestData: UpdateUserRequest = {
      userId: user.userId,
      ...data,
      operationType: 'UPDATE'
    }
    const response = await service.post<any, ApiResponse<User>>('/api/user', requestData)
    return response.data
  },

  /**
   * 修改密码
   * @param data 修改密码数据
   */
  async changePassword(data: { currentPassword: string; newPassword: string }): Promise<void> {
    const requestData: ChangePasswordRequest = {
      ...data,
      operationType: 'UPDATE'
    }
    await service.post<any, ApiResponse<void>>('/api/user', requestData)
  },

  /**
   * 获取用户列表（管理员权限）
   */
  async getUserList(): Promise<User[]> {
    const response = await service.get<any, ApiResponse<User[]>>('/api/user')
    return response.data
  },

  /**
   * 删除用户（管理员权限）
   * @param userId 用户ID
   */
  async deleteUser(userId: number): Promise<void> {
    const requestData = {
      userId,
      operationType: 'DELETE'
    }
    await service.post<any, ApiResponse<void>>('/api/user', requestData)
  }
}

export default userApi
