<template>
  <transition name="mask-fade">
    <div class="register-mask" @click="handleMaskClick">
      <div class="register-container" @click.stop>
        <div class="register-header">
          <h2 class="register-title">创建账户</h2>
          <p class="register-subtitle">加入MBTI测试系统，开始探索你的性格</p>
          <button class="close-btn" @click="closeRegister">
            <el-icon><Close /></el-icon>
          </button>
        </div>

        <div class="register-content">
          <el-form
            ref="registerFormRef"
            :model="registerForm"
            :rules="registerRules"
            label-position="top"
            size="large"
            @submit.prevent="handleRegister"
          >
            <el-form-item label="用户名" prop="username">
              <el-input
                v-model="registerForm.username"
                placeholder="请输入用户名"
                :prefix-icon="User"
                clearable
                @keyup.enter="handleRegister"
              />
            </el-form-item>

            <el-form-item label="邮箱" prop="email">
              <el-input
                v-model="registerForm.email"
                placeholder="请输入邮箱地址"
                :prefix-icon="Message"
                type="email"
                clearable
                @keyup.enter="handleRegister"
              />
            </el-form-item>

            <el-form-item label="密码" prop="password">
              <el-input
                v-model="registerForm.password"
                placeholder="请输入密码"
                :prefix-icon="Lock"
                type="password"
                show-password
                clearable
                @keyup.enter="handleRegister"
              />
            </el-form-item>

            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input
                v-model="registerForm.confirmPassword"
                placeholder="请再次输入密码"
                :prefix-icon="Lock"
                type="password"
                show-password
                clearable
                @keyup.enter="handleRegister"
              />
            </el-form-item>

            <el-form-item class="register-actions">
              <el-button
                type="primary"
                size="large"
                class="register-btn"
                :loading="loading"
                @click="handleRegister"
                native-type="submit"
              >
                {{ loading ? '注册中...' : '立即注册' }}
              </el-button>
            </el-form-item>
          </el-form>

          <div class="register-footer">
            <p class="login-link">
              已有账户？
              <a @click="switchToLogin">立即登录</a>
            </p>
            
            <div class="terms-notice">
              <p>注册即表示您同意我们的
                <a href="#" @click.prevent>《用户协议》</a>
                和
                <a href="#" @click.prevent>《隐私政策》</a>
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </transition>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { Close, User, Message, Lock } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/userStore'
import { useUiStateStore } from '@/stores/uiStateStore'

const userStore = useUserStore()
const uiStateStore = useUiStateStore()

// 表单引用和状态
const registerFormRef = ref<any>(null)
const loading = ref(false)

// 表单数据
const registerForm = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: ''
})

// 表单验证规则
const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 50, message: '用户名长度应在3-50个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_\u4e00-\u9fa5]+$/, message: '用户名只能包含字母、数字、下划线和中文', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email' as const, message: '请输入正确的邮箱格式', trigger: ['blur', 'change'] }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 100, message: '密码长度应在6-100个字符', trigger: 'blur' },
    { pattern: /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d@$!%*#?&]/, message: '密码至少包含一个字母和一个数字', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule: any, value: any, callback: any) => {
        if (value !== registerForm.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 事件处理
const handleRegister = async () => {
  try {
    // 表单验证
    await registerFormRef.value?.validate()
    
    loading.value = true
    
    // 调用注册API
    const success = await userStore.register(
      registerForm.username, 
      registerForm.password, 
      registerForm.email
    )
    
    if (success) {
      ElMessage.success('注册成功！请登录')
      
      // 清空表单
      resetForm()
      
      // 关闭注册弹窗，打开登录弹窗
      uiStateStore.closeRegister()
      setTimeout(() => {
        uiStateStore.openLogin()
      }, 300)
    }
  } catch (error) {
    console.error('注册表单验证失败:', error)
  } finally {
    loading.value = false
  }
}

const handleMaskClick = () => {
  closeRegister()
}

const closeRegister = () => {
  resetForm()
  uiStateStore.closeRegister()
}

const switchToLogin = () => {
  resetForm()
  uiStateStore.closeRegister()
  setTimeout(() => {
    uiStateStore.openLogin()
  }, 300)
}

const resetForm = () => {
  registerForm.username = ''
  registerForm.email = ''
  registerForm.password = ''
  registerForm.confirmPassword = ''
  registerFormRef.value?.resetFields()
}
</script>

<style scoped>
/* 遮罩层 */
.register-mask {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
  backdrop-filter: blur(4px);
}

/* 注册容器 */
.register-container {
  width: 90%;
  max-width: 45rem;
  background-color: var(--color-background);
  border-radius: 1.6rem;
  box-shadow: 0 1rem 3rem rgba(0, 0, 0, 0.3);
  overflow: hidden;
  position: relative;
  border: 1px solid var(--color-border);
}

/* 注册头部 */
.register-header {
  padding: 3rem 3rem 2rem;
  text-align: center;
  background: linear-gradient(135deg, var(--primary-teal) 0%, var(--primary-teal-dark) 100%);
  color: #ffffff;
  position: relative;
}

.register-title {
  font-size: 2.4rem;
  font-weight: 600;
  margin: 0 0 0.8rem 0;
}

.register-subtitle {
  font-size: 1.4rem;
  margin: 0;
  opacity: 0.9;
  font-weight: 400;
}

.close-btn {
  position: absolute;
  top: 1.5rem;
  right: 1.5rem;
  background: rgba(255, 255, 255, 0.2);
  border: none;
  border-radius: 50%;
  width: 3rem;
  height: 3rem;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  color: #ffffff;
}

.close-btn:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: scale(1.05);
}

/* 注册内容 */
.register-content {
  padding: 3rem;
}

/* 表单样式 */
:deep(.el-form-item__label) {
  color: var(--color-text-primary) !important;
  font-weight: 600 !important;
  font-size: 1.4rem !important;
}

:deep(.el-input__wrapper) {
  border-radius: 0.8rem !important;
  box-shadow: 0 0 0 1px var(--color-border) inset !important;
  transition: all 0.3s ease !important;
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px var(--primary-teal) inset !important;
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px var(--primary-teal) inset !important;
}

:deep(.el-input__inner) {
  font-size: 1.4rem !important;
  padding: 1.2rem 1.5rem !important;
}

/* 注册按钮 */
.register-actions {
  margin-top: 2rem;
}

.register-btn {
  width: 100% !important;
  height: 4.8rem !important;
  font-size: 1.6rem !important;
  font-weight: 600 !important;
  border-radius: 0.8rem !important;
  background: linear-gradient(135deg, var(--primary-teal) 0%, var(--primary-teal-dark) 100%) !important;
  border: none !important;
  transition: all 0.3s ease !important;
}

:deep(.register-btn:hover) {
  transform: translateY(-0.2rem) !important;
  box-shadow: 0 0.8rem 2rem rgba(32, 178, 170, 0.4) !important;
}

:deep(.register-btn:active) {
  transform: translateY(0) !important;
}

/* 注册底部 */
.register-footer {
  text-align: center;
  margin-top: 2rem;
}

.login-link {
  margin: 0 0 1.5rem 0;
  color: var(--color-text-secondary);
  font-size: 1.3rem;
}

.login-link a {
  color: var(--primary-teal);
  text-decoration: none;
  font-weight: 600;
  cursor: pointer;
  transition: color 0.3s ease;
}

.login-link a:hover {
  color: var(--primary-teal-dark);
  text-decoration: underline;
}

.terms-notice {
  font-size: 1.2rem;
  color: var(--color-text-soft);
  line-height: 1.6;
}

.terms-notice a {
  color: var(--primary-teal);
  text-decoration: none;
  cursor: pointer;
  transition: color 0.3s ease;
}

.terms-notice a:hover {
  color: var(--primary-teal-dark);
  text-decoration: underline;
}

/* 动画效果 */
.mask-fade-enter-active,
.mask-fade-leave-active {
  transition: all 0.3s ease;
}

.mask-fade-enter-from,
.mask-fade-leave-to {
  opacity: 0;
}

.mask-fade-enter-active .register-container,
.mask-fade-leave-active .register-container {
  transition: all 0.3s ease;
}

.mask-fade-enter-from .register-container,
.mask-fade-leave-to .register-container {
  transform: scale(0.8) translateY(-3rem);
  opacity: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .register-container {
    width: 95%;
    max-width: none;
    margin: 2rem;
  }
  
  .register-header {
    padding: 2rem 2rem 1.5rem;
  }
  
  .register-title {
    font-size: 2rem;
  }
  
  .register-subtitle {
    font-size: 1.3rem;
  }
  
  .register-content {
    padding: 2rem;
  }
  
  :deep(.el-input__inner) {
    font-size: 1.3rem !important;
    padding: 1rem 1.2rem !important;
  }
  
  .register-btn {
    height: 4.2rem !important;
    font-size: 1.4rem !important;
  }
}

@media (max-width: 480px) {
  .register-container {
    width: 98%;
    margin: 1rem;
  }
  
  .register-header {
    padding: 1.5rem 1.5rem 1rem;
  }
  
  .register-title {
    font-size: 1.8rem;
  }
  
  .register-subtitle {
    font-size: 1.2rem;
  }
  
  .register-content {
    padding: 1.5rem;
  }
  
  .close-btn {
    width: 2.5rem;
    height: 2.5rem;
    top: 1rem;
    right: 1rem;
  }
  
  .terms-notice {
    font-size: 1.1rem;
  }
}
</style>
