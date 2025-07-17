import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { authApi, userApi, type User, type LoginRequest } from '@/api'
import { CookieHelper } from '@/utils/cookieHelper'

export const useUserStore = defineStore('user', () => {
  // 状态
  const user = ref<User | null>(null)
  const isLoggedIn = ref(false)
  const loading = ref(false)

  // 计算属性：session状态
  const sessionId = computed(() => CookieHelper.getSessionId())
  const hasValidSession = computed(() => CookieHelper.hasValidSession())
  const sessionStatus = computed(() => ({
    sessionId: sessionId.value,
    hasSession: hasValidSession.value,
    isLoggedIn: isLoggedIn.value,
    isValid: hasValidSession.value && isLoggedIn.value
  }))

  // Session调试方法（保留但默认不输出）
  const debugSession = () => {
    // 可在console控制台中进行调试
    console.group('🔐 用户Session状态')
    console.log('Pinia用户状态:', {
      user: user.value,
      isLoggedIn: isLoggedIn.value,
      loading: loading.value
    })
    CookieHelper.debugCookies()
    console.log('综合状态:', sessionStatus.value)
    console.groupEnd()
  }

  // 检查登录状态
  const checkLoginStatus = () => {
    const userInfo = authApi.getCurrentUser()
    const hasSession = CookieHelper.hasValidSession()
    
    // 只有在有session cookie的情况下才认为是登录状态
    isLoggedIn.value = !!userInfo && hasSession
    
    if (userInfo && hasSession) {
      user.value = userInfo
    } else {
      // 如果没有session或用户信息不一致，清理状态
      user.value = null
      isLoggedIn.value = false
      if (userInfo && !hasSession) {
        console.warn('⚠️ 检测到本地用户信息但无session cookie，可能session已过期')
        localStorage.removeItem('userInfo')
      }
    }
    
    return isLoggedIn.value
  }

  // 登录
  const login = async (username: string, password: string): Promise<boolean> => {
    try {
      loading.value = true
      
      const loginData: LoginRequest = { 
        username, 
        password, 
        operationType: 'QUERY' 
      }
      const result = await authApi.login(loginData)
      
      // 检查响应数据格式 - 新的Session认证返回用户信息和sessionId
      if (!result.user) {
        console.error('登录响应中缺少用户信息:', result)
        ElMessage.error('登录响应格式错误')
        return false
      }
      
      // 等待一小段时间确保cookie设置完成
      await new Promise(resolve => setTimeout(resolve, 100))
      
      // 验证session cookie是否正确设置
      if (!CookieHelper.hasValidSession()) {
        console.error('⚠️ 登录成功但未检测到session cookie')
        ElMessage.error('登录状态异常，请重试')
        return false
      }
      
      // 保存用户信息到本地存储和store
      user.value = result.user
      isLoggedIn.value = true
      
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
      // 同时更新本地存储
      authApi.setUserInfo(user.value)
    } catch (error: any) {
      console.error('获取用户信息失败:', error)
      // 如果是401错误，说明Session过期，自动登出
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
      // 同时更新本地存储
      authApi.setUserInfo(updatedUser)
      
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

  // 修改密码
  const changePassword = async (oldPassword: string, newPassword: string): Promise<boolean> => {
    try {
      loading.value = true
      
      await userApi.changePassword({ currentPassword: oldPassword, newPassword })
      
      ElMessage.success('密码修改成功！')
      return true
    } catch (error: any) {
      console.error('修改密码失败:', error)
      ElMessage.error(error.message || '修改密码失败，请重试')
      return false
    } finally {
      loading.value = false
    }
  }

  // 登出
  const logout = async (): Promise<void> => {
    try {
      // 调用后端注销接口
      await authApi.logout()
    } catch (error) {
      console.error('注销请求失败:', error)
    } finally {
      // 无论后端请求是否成功，都清理本地状态
      user.value = null
      isLoggedIn.value = false
      ElMessage.success('已退出登录')
    }
  }

  // 初始化时检查登录状态
  const initialize = async (): Promise<void> => {
    if (checkLoginStatus()) {
      await fetchUserProfile()
    }
  }

  // 检查用户名是否存在
  const checkUsernameExists = async (username: string): Promise<boolean> => {
    try {
      const result = await authApi.checkUsername(username)
      return result.exists
    } catch (error: any) {
      console.error('检查用户名失败:', error)
      return false
    }
  }

  // 检查邮箱是否存在
  const checkEmailExists = async (email: string): Promise<boolean> => {
    try {
      const result = await authApi.checkEmail(email)
      return result.exists
    } catch (error: any) {
      console.error('检查邮箱失败:', error)
      return false
    }
  }

  // 获取当前用户角色
  const getUserRole = (): string | null => {
    return user.value?.role || null
  }

  // 检查是否为管理员
  const isAdmin = (): boolean => {
    return user.value?.role === 'ADMIN'
  }

  return {
    // 状态
    user,
    isLoggedIn,
    loading,
    
    // 计算属性：session相关
    sessionId,
    hasValidSession,
    sessionStatus,
    
    // 方法
    login,
    register,
    logout,
    fetchUserProfile,
    updateProfile,
    checkLoginStatus,
    initialize,
    changePassword,
    checkUsernameExists,
    checkEmailExists,
    getUserRole,
    isAdmin,
    debugSession
  }
})
