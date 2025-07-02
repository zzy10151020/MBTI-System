import { defineStore } from 'pinia'
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { authApi, userApi, type User, type LoginRequest } from '@/api'

export const useUserStore = defineStore('user', () => {
  // 状态
  const user = ref<User | null>(null)
  const isLoggedIn = ref(false)
  const loading = ref(false)

  // 检查登录状态
  const checkLoginStatus = () => {
    const token = authApi.getToken()
    isLoggedIn.value = !!token
    return isLoggedIn.value
  }

  // 登录
  const login = async (username: string, password: string): Promise<boolean> => {
    try {
      loading.value = true
      
      console.log('开始登录:', { username, password: '***' })
      const loginData: LoginRequest = { username, password }
      const result = await authApi.login(loginData)
      
      console.log('登录响应:', result)
      
      // 检查响应数据格式
      if (!result.token) {
        console.error('登录响应中缺少token:', result)
        ElMessage.error('登录响应格式错误')
        return false
      }
      
      // 保存token
      authApi.setToken(result.token)
      isLoggedIn.value = true
      
      // 获取用户详细信息
      await fetchUserProfile()
      
      ElMessage.success('登录成功！')
      return true
    } catch (error: any) {
      console.error('登录失败详细信息:', {
        error,
        message: error.message,
        response: error.response,
        stack: error.stack
      })
      ElMessage.error(error.message || '登录失败，请检查用户名和密码')
      return false
    } finally {
      loading.value = false
    }
  }

  // 注册
  const register = async (username: string, password: string, email: string): Promise<boolean> => {
    try {
      loading.value = true
      
      const result = await authApi.register({ username, password, email })
      
      ElMessage.success('注册成功！请登录')
      return true
    } catch (error: any) {
      console.error('注册失败:', error)
      ElMessage.error(error.message || '注册失败，请重试')
      return false
    } finally {
      loading.value = false
    }
  }

  // 获取用户信息
  const fetchUserProfile = async (): Promise<void> => {
    try {
      if (!authApi.isLoggedIn()) {
        return
      }
      
      user.value = await userApi.getProfile()
    } catch (error: any) {
      console.error('获取用户信息失败:', error)
      // 如果是401错误，说明token过期，自动登出
      if (error.message?.includes('401') || error.message?.includes('unauthorized')) {
        logout()
      }
    }
  }

  // 更新用户信息
  const updateProfile = async (email: string): Promise<boolean> => {
    try {
      loading.value = true
      
      const updatedUser = await userApi.updateProfile({ email })
      user.value = updatedUser
      
      ElMessage.success('更新成功！')
      return true
    } catch (error: any) {
      console.error('更新用户信息失败:', error)
      ElMessage.error(error.message || '更新失败，请重试')
      return false
    } finally {
      loading.value = false
    }
  }

  // 登出
  const logout = (): void => {
    authApi.logout()
    user.value = null
    isLoggedIn.value = false
    ElMessage.success('已退出登录')
  }

  // 初始化时检查登录状态
  const initialize = async (): Promise<void> => {
    if (checkLoginStatus()) {
      await fetchUserProfile()
    }
  }

  return {
    // 状态
    user,
    isLoggedIn,
    loading,
    
    // 方法
    login,
    register,
    logout,
    fetchUserProfile,
    updateProfile,
    checkLoginStatus,
    initialize
  }
})
