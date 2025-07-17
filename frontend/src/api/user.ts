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
    const response = await service.get<any, ApiResponse<User>>('/api/user')
    return response.data
  },

  /**
   * 更新当前用户信息
   * @param data 更新数据
   */
  async updateProfile(data: UpdateUserRequest): Promise<User> {
    const requestData = {
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
  async changePassword(data: ChangePasswordRequest): Promise<void> {
    const requestData = {
      ...data,
      operationType: 'UPDATE'
    }
    await service.post<any, ApiResponse<void>>('/api/user/changePassword', requestData)
  },

  /**
   * 获取用户列表（管理员权限）
   */
  async getUserList(): Promise<User[]> {
    const response = await service.get<any, ApiResponse<User[]>>('/api/user/list')
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
