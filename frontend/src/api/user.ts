import service from './axios'
import type { 
  ApiResponse,
  User,
  UpdateUserRequest,
  DeleteUserRequest
} from './types'

/**
 * 用户管理相关API
 */
export const userApi = {
  /**
   * 根据用户ID获取用户信息
   * @param userId 用户ID
   */
  async getUserById(userId: number): Promise<User> {
    const response = await service.get<any, ApiResponse<User>>(`/api/user/${userId}`)
    return response.data!
  },

  /**
   * 获取当前用户个人资料
   */
  async getProfile(): Promise<User> {
    const response = await service.post<any, ApiResponse<User>>('/api/user/profile')
    return response.data!
  },

  /**
   * 更新当前用户信息
   * @param data 更新数据
   */
  async updateProfile(data: UpdateUserRequest): Promise<User> {
    const response = await service.post<any, ApiResponse<User>>('/api/user', data)
    return response.data!
  },

  /**
   * 获取用户列表（管理员功能）
   */
  async getUserList(): Promise<User[]> {
    const response = await service.get<any, ApiResponse<User[]>>('/api/user/list')
    return response.data!
  },

  /**
   * 删除用户（管理员功能）
   * @param data 删除用户请求数据
   */
  async deleteUser(data: DeleteUserRequest): Promise<void> {
    await service.delete('/api/user', { data })
  }
}

export default userApi
