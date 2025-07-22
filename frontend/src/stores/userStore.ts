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
    console.group('用户Session状态')
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
    const userInfo = getCurrentUser()
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
        console.warn('检测到本地用户信息但无session cookie，可能session已过期')
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
        password
      }
      const result = await authApi.login(loginData)

      if (result.data.userId === null || result.data.sessionId === null) {
        console.error('登录响应中缺少用户信息:', result.message)
        ElMessage.error(`${result.message}`)
        return false
      }
      // 等待一小段时间确保cookie设置完成
      await new Promise(resolve => setTimeout(resolve, 100))
      // 验证session cookie是否正确设置
      if (!CookieHelper.hasValidSession()) {
        console.error('登录成功但未检测到session cookie')
        ElMessage.error('登录状态异常，请重试')
        return false
      }
      // 保存用户信息到本地存储和store
      user.value = result
      isLoggedIn.value = true
      setUserInfo(result)
      await fetchUserProfile() // 刷新用户信息
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
      if (!checkLogin()) {
        return
      }
      
      const result = await userApi.getProfile()
      user.value = result.data
      setUserInfo(result)
    } catch (error: any) {
      console.error('获取用户信息失败:', error)
      // 如果是401错误，说明Session过期，自动登出
      if (error.message?.includes('401') || error.message?.includes('unauthorized')) {
        logout()
      }
    }
  }

  // 更新用户信息
  const updateUser = async (email: string): Promise<any> => {
    try {
      loading.value = true
      
      const result = await userApi.updateUser({ email })
      user.value = result.data
      setUserInfo(result)
      ElMessage.success('更新成功！')
      return result
    } catch (error: any) {
      console.error('更新用户信息失败:', error)
      ElMessage.error(error.message || '更新失败，请重试')
      return {} as User
    } finally {
      loading.value = false
    }
  }

  // 根据ID更新用户信息
  const updateUserById = async (userId: number, data: Partial<User>): Promise<boolean> => {
    try {
      loading.value = true

      const result = await userApi.updateUserById({ updateUserId: userId, ...data })
      if (!result.data) {
        console.error('更新用户信息响应中缺少用户信息:', result)
        ElMessage.error(result.message || '更新用户信息失败，请重试')
        return false
      }
      return true
    } catch (error: any) {
      console.error('更新用户信息失败:', error)
      ElMessage.error(error.message || '更新用户信息失败，请重试')
      return false
    } finally {
      loading.value = false
    }
  }

  // 修改密码
  const changePassword = async (oldPassword: string, newPassword: string): Promise<boolean> => {
    try {
      loading.value = true

      const result = await userApi.updateUser({ currentPassword: oldPassword, newPassword })
      if (!result.data) {
        console.error('修改密码响应中缺少用户信息:', result)
        ElMessage.error(result.message || '修改密码失败，请重试')
        return false
      }
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
      // 清除本地存储的用户信息
      clearUserInfo()
      // 更新状态
      user.value = null
      isLoggedIn.value = false
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
    try {
      // 每次刷新都用 session 校验
      const result = await userApi.getProfile()
      if (result.data) {
        user.value = result.data
        isLoggedIn.value = true
        setUserInfo(result)
      } else {
        user.value = null
        isLoggedIn.value = false
        clearUserInfo()
      }
    } catch (error: any) {
      // 401未登录或session失效
      user.value = null
      isLoggedIn.value = false
      clearUserInfo()
    }
  }

  // 检查用户名是否存在
  const checkUsernameExists = async (username: string): Promise<boolean> => {
    try {
      const result = await authApi.checkUsername({ username })
      return result.data
    } catch (error: any) {
      console.error('检查用户名失败:', error)
      return false
    }
  }

  // 检查邮箱是否存在
  const checkEmailExists = async (email: string): Promise<boolean> => {
    try {
      const result = await authApi.checkEmail({ email })
      return result.data
    } catch (error: any) {
      console.error('检查邮箱失败:', error)
      return false
    }
  }

  // 获取用户列表
  const getUserList = async (): Promise<User[]> => {
    try {
      const result = await userApi.getUserList()
      return result.data
    } catch (error: any) {
      console.error('获取用户列表失败:', error)
      ElMessage.error(error.message || '获取用户列表失败，请稍后重试')
      return []
    }
  }

  // 删除用户
  const deleteUser = async (userId: number): Promise<void> => {
    try {
      loading.value = true
      await userApi.deleteUser({ deleteUserId: userId })
    } catch (error: any) {
      console.error('删除用户失败:', error)
      ElMessage.error(error.message || '删除用户失败，请稍后重试')
    } finally {
      loading.value = false
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

  /**
   * 检查是否已登录
   */
  const checkLogin = (): boolean => {
    const userInfo = localStorage.getItem('userInfo')
    return !!userInfo
  }

  /**
   * 获取当前用户信息
   */
  const getCurrentUser = (): User | null => {
    const userInfo = localStorage.getItem('userInfo')
    return userInfo ? JSON.parse(userInfo) : null
  }

  /**
   * 设置用户信息
   * @param user 用户信息
   */
  const setUserInfo = (user: User): void => {
    localStorage.setItem('userInfo', JSON.stringify(user))
  }

  /**
   * 清除用户信息
   */
  const clearUserInfo = (): void => {
    localStorage.removeItem('userInfo')
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
    updateUser,
    updateUserById,
    checkLoginStatus,
    initialize,
    changePassword,
    checkUsernameExists,
    checkEmailExists,
    getUserList,
    deleteUser,
    getUserRole,
    isAdmin,
    debugSession,
    checkLogin,
    getCurrentUser,
    setUserInfo,
    clearUserInfo
  }
}, {
  persist: {
    key: 'userStore',
    storage: localStorage
  }
})
