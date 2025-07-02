<template>
  <div class="header-user-section">
    <!-- 已登录用户显示头像和菜单 -->
    <div 
      v-if="isLoggedIn" 
      class="user-avatar" 
      @click="toggleAvatarMenu"
      @mouseenter="isAvatarHover = true"
      @mouseleave="isAvatarHover = false"
    >
      <el-avatar :size="48" class="avatar">
        {{ userInitial }}
      </el-avatar>
      <span class="username">{{ username }}</span>
    </div>

    <!-- 未登录用户显示登录注册按钮 -->
    <div v-else class="auth-buttons">
      <el-button type="default" size="small" @click="handleSignup">
        <span>注册</span>
      </el-button>
      <el-button type="primary" size="small" @click="handleLogin">
        <span>登录</span>
      </el-button>
    </div>

    <!-- 用户简要信息悬停提示 -->
    <div v-if="isAvatarHover && !isAvatarClick && isLoggedIn" class="user-brief-info">
      <span>{{ username }}</span>
    </div>

    <!-- 用户头像菜单 -->
    <div v-if="isAvatarClick && isLoggedIn" class="user-profile-menu">
      <div class="menu-header">
        <div class="menu-avatar">
          <el-avatar :size="75" class="menu-avatar-img">
            <span style="font-size: 2.5rem;">{{ userInitial }}</span>
          </el-avatar>
        </div>
        <div class="user-info">
          <span class="username">{{ username }}</span>
          <span class="greeting">· 你好！</span>
        </div>
      </div>
      <div class="close-button" @click="closeAvatarMenu">
        <el-icon><Close /></el-icon>
      </div>
      <div class="menu-list">
        <div class="menu-item main-action" @click="goToUserSpace">
          <span>您的个人空间</span>
        </div>
        <div class="menu-divider"></div>
        <div class="menu-actions">
          <div class="menu-item action-item">
            <el-icon class="action-icon"><Switch /></el-icon>
            <span>更改账号</span>
          </div>
          <div class="menu-item action-item logout" @click="handleLogout">
            <el-icon class="action-icon"><SwitchButton /></el-icon>
            <span>登出</span>
          </div>
        </div>
        <div class="menu-footer">
          <span>隐私权政策</span>
          <span class="dot">•</span>
          <span>服务条款</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { Close, Switch, SwitchButton } from '@element-plus/icons-vue'
import { useUiStateStore } from '@/stores/uiStateStore'
import { useUserStore } from '@/stores/userStore'

const router = useRouter()
const uiStateStore = useUiStateStore()
const userStore = useUserStore()

// 响应式状态
const isAvatarHover = ref(false)
const isAvatarClick = ref(false)

// 计算属性
const isLoggedIn = computed(() => userStore.isLoggedIn)
const username = computed(() => userStore.user?.username ?? '')
const userInitial = computed(() => {
  return username.value ? username.value.charAt(0).toUpperCase() : 'U'
})

// 事件处理方法
const toggleAvatarMenu = () => {
  isAvatarClick.value = !isAvatarClick.value
  isAvatarHover.value = false
}

const closeAvatarMenu = () => {
  isAvatarClick.value = false
}

const handleLogin = () => {
  uiStateStore.openLogin()
}

const handleSignup = () => {
  uiStateStore.openRegister()
}

const handleLogout = () => {
  userStore.logout()
  isAvatarClick.value = false
  
  // 退出登录后跳转到不带 uid 的路由
  const currentRoute = router.currentRoute.value
  if (currentRoute.name === 'home') {
    router.push({ name: 'home' })
  } else if (currentRoute.name === 'questionnaires') {
    router.push({ name: 'questionnaires' })
  } else if (currentRoute.name === 'results') {
    router.push({ name: 'questionnaires' }) // 退出后返回问卷页
  } else if (currentRoute.name === 'test') {
    router.push({ name: 'questionnaires' }) // 退出后返回问卷页
  } else if (currentRoute.name === 'profile') {
    router.push({ name: 'questionnaires' }) // 退出后返回问卷页
  }
}

const goToUserSpace = () => {
  // 首先关闭头像菜单
  isAvatarClick.value = false
  
  // 确保用户已登录且有用户信息
  if (!userStore.isLoggedIn || !userStore.user?.userId) {
    console.warn('用户未登录或用户信息不完整')
    return
  }
  
  // 关闭登录弹窗（如果有的话）
  uiStateStore.closeLogin()
  
  // 跳转到用户个人资料页面
  router.push({ 
    name: 'profile', 
    params: { uid: userStore.user.userId.toString() } 
  }).then(() => {
    console.log('成功跳转到个人资料页面')
  }).catch((error) => {
    console.error('跳转失败:', error)
  })
}
</script>

<style scoped>
/* 头像用户区域 - 继承Header的样式 */
.header-user-section {
  position: relative;
  flex-shrink: 0;
  min-width: 15rem; /* 150px */
  display: flex;
  justify-content: flex-end;
  align-items: center;
}

/* 用户头像区域 */
.user-avatar {
  display: flex;
  align-items: center;
  gap: 0.8rem;
  cursor: pointer;
  padding: 0.5rem 1rem;
  border-radius: 0.8rem; /* 8px */
  transition: background-color 0.3s ease;
}

.user-avatar:hover {
  background-color: var(--color-background-soft);
}

.avatar {
  background-color: var(--primary-teal) !important;
  color: #ffffff !important;
  font-weight: bold;
  font-size: 1.4rem; /* 14px */
}

.username {
  font-size: 1.4rem; /* 14px */
  color: var(--color-text-secondary);
  font-weight: 500;
}

/* 认证按钮区域 */
.auth-buttons {
  display: flex;
  gap: 0.5rem;
  align-items: center;
}

.auth-buttons .el-button {
  width: 4rem;
  height: 2rem;
}

.auth-buttons .el-button span {
  font-size: 1rem;
  font-weight: 500;
}

/* Element Plus 按钮样式覆盖 */
:deep(.el-button--primary) {
  background-color: var(--primary-teal) !important;
  border-color: var(--primary-teal) !important;
  color: #ffffff !important;
}

:deep(.el-button--primary:hover) {
  background-color: var(--primary-teal-dark) !important;
  border-color: var(--primary-teal-dark) !important;
  color: #ffffff !important;
}

:deep(.el-button--primary:focus) {
  background-color: var(--primary-teal) !important;
  border-color: var(--primary-teal) !important;
  color: #ffffff !important;
}

:deep(.el-button--default) {
  background-color: transparent !important;
  border-color: var(--primary-teal) !important;
  color: var(--primary-teal) !important;
}

:deep(.el-button--default:hover) {
  background-color: var(--primary-teal-light) !important;
  border-color: var(--primary-teal-dark) !important;
  color: var(--primary-teal-darker) !important;
}

:deep(.el-button--default:focus) {
  background-color: transparent !important;
  border-color: var(--primary-teal) !important;
  color: var(--primary-teal) !important;
}

/* 悬停信息提示 */
.user-brief-info {
  position: absolute;
  top: 4rem;
  left: 4rem;
  width: 6rem;
  height: auto;
  padding: 0 1rem;
  background-color: var(--color-background);
  border-radius: 0.8rem;
  box-shadow: 0 0.2rem 0.5rem rgba(0, 0, 0, 0.1);
  border: 1px solid var(--color-border);
  z-index: 1000;
}

.user-brief-info span {
  font-size: 1.2rem;
  font-weight: bold;
  color: var(--color-text-primary);
}

/* 用户菜单 */
.user-profile-menu {
  position: absolute;
  top: 5rem;
  right: -1rem;
  width: 24rem;
  height: auto;
  background-color: var(--color-background);
  border-radius: 1rem;
  box-shadow: 0 0.2rem 0.8rem rgba(0, 0, 0, 0.15);
  border: 1px solid var(--color-border);
  z-index: 1000;
  overflow: hidden;
  padding: 0;
  color: var(--color-text-primary);
}

.menu-header {
  width: 100%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: center;
  padding: 2rem 1.5rem 0 1.5rem;
}

.menu-avatar-img {
  background-color: var(--primary-teal) !important;
  color: #ffffff !important;
  border: 0.2rem solid var(--color-border);
}

.menu-header .user-info {
  display: flex;
  justify-content: center;
  align-items: baseline;
}

.user-info .username {
  font-size: 1.6rem;
  font-weight: normal;
  color: var(--color-text-primary);
}

.user-info .greeting {
  font-size: 1.4rem;
  margin-left: 0.5rem;
  color: var(--color-text-secondary);
}

.close-button {
  position: absolute;
  top: 1.5rem;
  right: 1.5rem;
  cursor: pointer;
  font-size: 1.8rem;
  color: var(--color-text-soft);
  transition: color 0.3s ease;
}

.close-button:hover {
  color: var(--primary-teal);
}

.menu-list {
  padding: 0;
}

.menu-item.main-action {
  width: fit-content;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0.5rem 1rem;
  cursor: pointer;
  border-radius: 2rem;
  border: 1px solid var(--color-border);
  margin: 1rem auto;
  background-color: transparent;
  transition: background-color 0.3s ease;
}

.menu-item.main-action:hover {
  background-color: var(--color-background-soft);
}

.menu-item.main-action span {
  color: var(--primary-teal);
  font-size: 1rem;
  font-weight: 500;
  text-align: center;
  padding: 0 1rem;
}

.menu-actions {
  display: flex;
  padding: 1.5rem 2rem;
  gap: 1rem;
}

.menu-item.action-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: var(--color-text-primary);
  cursor: pointer;
  padding-top: 1rem;
  border-radius: 0.8rem;
  transition: background-color 0.3s ease;
}

.menu-item.action-item:hover {
  background-color: var(--color-background-soft);
}

.action-icon {
  font-size: 1.2rem;
  margin-bottom: 0.8rem;
  color: var(--color-text-soft);
}

.menu-item span {
  font-size: 1rem;
  text-align: center;
  color: var(--color-text-primary);
}

.menu-divider {
  height: 1px;
  background-color: var(--color-border);
  margin: 0;
}

.menu-footer {
  display: flex;
  justify-content: center;
  padding-bottom: 1rem;
  font-size: 1rem;
  color: var(--color-text-soft);
}

.menu-footer .dot {
  margin: 0 0.5rem;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-user-section {
    min-width: 10rem; /* 100px */
  }
  
  .username {
    display: none; /* 移动端隐藏用户名 */
  }
  
  .auth-buttons {
    gap: 0.5rem;
  }
  
  :deep(.el-button) {
    padding: 0.5rem 1rem !important;
    font-size: 1.1rem !important; /* 11px */
  }

  .user-profile-menu {
    width: 35rem;
    right: -5rem;
  }
}

@media (max-width: 480px) {
  .user-profile-menu {
    width: 30rem;
    right: -8rem;
  }
  
  .menu-header {
    padding: 3rem 1rem 0 1rem;
  }
  
  .user-info .username {
    font-size: 2.5rem;
  }
  
  .user-info .greeting {
    font-size: 1.8rem;
  }
}
</style>