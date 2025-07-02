import service from './axios'
import type { 
  ApiResponse,
  User,
  UpdateProfileRequest,
  PageResponse,
  PageParams
} from './types'

/**
 * 用户管理相关API
 */
export const userApi = {
  /**
   * 获取当前用户信息
   */
  async getProfile(): Promise<User> {
    const response = await service.get<any, ApiResponse<User>>('/api/users/profile')
    return response.data
  },

  /**
   * 更新当前用户信息
   * @param data 更新数据
   */
  async updateProfile(data: UpdateProfileRequest): Promise<User> {
    const response = await service.put<any, ApiResponse<User>>('/api/users/profile', data)
    return response.data
  },

  /**
   * 获取用户列表（管理员权限）
   * @param params 分页参数
   */
  async getUserList(params: PageParams = {}): Promise<PageResponse<User>> {
    const { page = 0, size = 10 } = params
    const response = await service.get<any, ApiResponse<PageResponse<User>>>(
      `/api/users?page=${page}&size=${size}`
    )
    return response.data
  },

  /**
   * 删除用户（管理员权限）
   * @param userId 用户ID
   */
  async deleteUser(userId: number): Promise<void> {
    await service.delete<any, ApiResponse<void>>(`/api/users/${userId}`)
  }
}

export default userApi
