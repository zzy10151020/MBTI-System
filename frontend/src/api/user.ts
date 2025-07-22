import service from './axios'
import type { 
  UpdateUserRequest,
  DeleteUserRequest,
} from './types'

export const userApi = {
  // 根据用户ID获取用户信息
  getUserById: async (userId: number): Promise<any> => {
    const response = await service.get(`/api/user/${userId}`)
    return response!
  },

  // 更新用户信息
  updateUser: async (data: UpdateUserRequest): Promise<any> => {
    const response = await service.post('/api/user', data)
    return response!
  },

  // 根据ID更新用户信息（管理员）
  updateUserById: async (data: UpdateUserRequest): Promise<any> => {
    const response = await service.post('/api/user/byId', data)
    return response!
  },

  // 获取当前登录用户的个人资料
  getProfile: async (): Promise<any> => {
    const response = await service.get('/api/user/profile')
    return response!
  },

  // 获取用户列表（管理员）
  getUserList: async (): Promise<any> => {
    const response = await service.get('/api/user/list')
    return response!
  },

  // 删除用户
  deleteUser: async (data: DeleteUserRequest): Promise<any> => {
    const response = await service.delete('/api/user', { data })
    return response!
  }
}

export default userApi
