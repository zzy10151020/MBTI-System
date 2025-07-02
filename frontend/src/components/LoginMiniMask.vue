<template>
  <div class="mask-container">
    <div class="login-container">
      <h2>用户登录</h2>
      <div class="close-btn">
        <el-icon @click="uiState.closeLogin"><Close /></el-icon>
      </div>
      <el-form :model="login_form" ref="loginFormRef" 
        label-position="top" class="login-form">

        <el-form-item label="用户名" prop="username">
          <el-input v-model="login_form.username" placeholder="请输入用户名"
            :prefix-icon="User" clearable maxlength="20"/>
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input v-model="login_form.password" type="password" placeholder="请输入密码" 
            :prefix-icon="Lock" show-password maxlength="20"/>
        </el-form-item>

        <div class="form-actions">
          <el-button type="primary" @click="handleLogin" :loading="loading">
            登录
          </el-button>
          <el-button @click="resetForm">重置</el-button>
        </div>

        <div class="form-footer">
          <el-link type="primary" underline="never">忘记密码？</el-link>
          <el-link type="primary" @click="switchToSignup" underline="never">
            注册账号
          </el-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Close } from '@element-plus/icons-vue'
import type { FormInstance } from 'element-plus'
import { useUserStore } from '@/stores/userStore'
import { useUiStateStore } from '@/stores/uiStateStore'

const userStore = useUserStore()
const uiState = useUiStateStore()
const router = useRouter()
const emit = defineEmits(['update:visible'])

const login_form = reactive({
  username: '',
  password: ''
})

const loginFormRef = ref<FormInstance>()
// 使用userStore的loading状态
const loading = computed(() => userStore.loading)

const handleLogin = async () => {
  // 简单的表单验证
  if (!login_form.username.trim()) {
    ElMessage.warning('请输入用户名')
    return
  }
  if (!login_form.password.trim()) {
    ElMessage.warning('请输入密码')
    return
  }

  const success = await userStore.login(login_form.username, login_form.password)
  if (success) {
    uiState.closeLogin()
    
    // 等待一小段时间确保用户信息已经加载完成
    await new Promise(resolve => setTimeout(resolve, 100))
    
    // 登录成功后，如果用户信息已加载并且当前路由没有uid，则跳转到带uid的路由
    if (userStore.user?.userId) {
      const currentRoute = router.currentRoute.value
      const currentUid = currentRoute.params.uid
      
      // 如果当前路由没有uid参数，则根据当前路由名称跳转到对应的带uid路由
      if (!currentUid) {
        const uid = userStore.user.userId.toString()
        if (currentRoute.name === 'home') {
          router.replace({ name: 'home', params: { uid } })
        } else if (currentRoute.name === 'questionnaires') {
          router.replace({ name: 'questionnaires', params: { uid } })
        }
      }
    }
  }
}

const resetForm = () => {
  if (!loginFormRef.value) return
  loginFormRef.value.resetFields()
}

const switchToSignup = () => {
  uiState.openRegister()
}
</script>

<style scoped>
.mask-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}

.close-btn {
  position: absolute;
  top: 1.6rem;
  right: 1.6rem;
  cursor: pointer;
  font-size: 2rem;
  color: var(--color-text-soft);
  transition: color 0.3s ease;
}

.close-btn:hover {
  color: var(--primary-teal);
}

.login-container {
  background: #ffffff;
  padding: 2rem 4rem;
  border-radius: 1.92rem;
  box-shadow: 0 0.64rem 1.6rem rgba(0, 0, 0, 0.1);
  width: 28rem;
  height: 24rem;
  position: absolute;
  left: 50%;
  top: 45%;
  transform: translate(-50%, -50%);
}

.login-form {
  margin-top: 1rem;
}

h2 {
  color: var(--primary-teal);
  text-align: center;
  font: bold 3rem 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.form-actions {
  margin-top: 2rem;
  display: flex;
  gap: 1.6rem;
  justify-content: center;
}

.form-footer {
  margin-top: 1rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.form-footer :deep(.el-link) {
  font-size: 1.44em;
  transition: all 0.3s ease;
}

.el-menu--horizontal {
  margin-top: 0.8rem;
  --el-menu-horizontal-height: 3.2rem;
}

.el-menu--horizontal .el-menu-item {
  width: 11.2rem;
}

/* 按钮主色调覆盖 */
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

:deep(.el-button--primary:active) {
  background-color: var(--primary-teal-dark) !important;
  border-color: var(--primary-teal-dark) !important;
}

/* 输入框聚焦时的主色调 */
:deep(.el-input__wrapper:focus-within) {
  box-shadow: 0 0 0 1px var(--primary-teal) inset !important;
}

:deep(.el-input:focus-within .el-input__wrapper) {
  box-shadow: 0 0 0 1px var(--primary-teal) inset !important;
}

/* 链接主色调 */
:deep(.el-link.el-link--primary) {
  color: var(--primary-teal) !important;
}

:deep(.el-link.el-link--primary:hover) {
  color: var(--primary-teal-dark) !important;
}
</style>