<template>
  <div class="not-found-container">
    <div class="not-found-card">
      <div class="error-content">
        <div class="error-icon">
          <el-icon><WarningFilled /></el-icon>
        </div>
        
        <h1 class="error-title">404</h1>
        <h2 class="error-subtitle">页面未找到</h2>
        <p class="error-description">
          抱歉，您访问的页面不存在或已被移除。
        </p>
        
        <div class="error-actions">
          <el-button type="primary" size="large" @click="goHome">
            <el-icon><HomeFilled /></el-icon>
            返回首页
          </el-button>
          
          <el-button size="large" @click="goBack">
            <el-icon><Back /></el-icon>
            返回上页
          </el-button>
        </div>
        
        <div class="suggestions">
          <h3>您可以尝试：</h3>
          <ul>
            <li><a @click="goToQuestionnaires">浏览问卷测试</a></li>
            <li><a @click="goToResults" v-if="userStore.isLoggedIn">查看测试结果</a></li>
            <li><a @click="goToProfile" v-if="userStore.isLoggedIn">个人中心</a></li>
          </ul>
        </div>
      </div>
      
      <div class="error-illustration">
        <div class="illustration-content">
          <div class="float-element element-1"></div>
          <div class="float-element element-2"></div>
          <div class="float-element element-3"></div>
          <div class="main-shape"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { WarningFilled, HomeFilled, Back } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/userStore'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// 计算当前用户的uid
const currentUid = computed(() => {
  return route.params.uid || userStore.user?.userId?.toString()
})

// 导航方法
const goHome = () => {
  if (currentUid.value) {
    router.push({ name: 'home', params: { uid: currentUid.value } })
  } else {
    router.push({ name: 'home' })
  }
}

const goBack = () => {
  if (window.history.length > 1) {
    router.go(-1)
  } else {
    goHome()
  }
}

const goToQuestionnaires = () => {
  if (currentUid.value) {
    router.push({ name: 'questionnaires', params: { uid: currentUid.value } })
  } else {
    router.push({ name: 'questionnaires' })
  }
}

const goToResults = () => {
  if (currentUid.value) {
    router.push({ name: 'results', params: { uid: currentUid.value } })
  } else {
    router.push({ name: 'results' })
  }
}

const goToProfile = () => {
  if (currentUid.value) {
    router.push({ name: 'profile', params: { uid: currentUid.value } })
  } else {
    router.push({ name: 'profile' })
  }
}
</script>

<style scoped>
.not-found-container {
  width: 100%;
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 2rem;
  background: linear-gradient(135deg, var(--color-background-soft) 0%, var(--primary-teal-light) 100%);
}

.not-found-card {
  max-width: 90rem;
  width: 100%;
  background-color: var(--color-background);
  border-radius: 2rem;
  box-shadow: 0 1rem 4rem rgba(0, 0, 0, 0.15);
  padding: 4rem;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 4rem;
  align-items: center;
  border: 1px solid var(--color-border);
}

/* 错误内容区域 */
.error-content {
  text-align: center;
}

.error-icon {
  font-size: 8rem;
  color: #f56565;
  margin-bottom: 2rem;
}

.error-title {
  font-size: 8rem;
  font-weight: bold;
  color: var(--primary-teal);
  margin: 0 0 1rem 0;
  line-height: 1;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
}

.error-subtitle {
  font-size: 2.4rem;
  color: var(--color-text-primary);
  margin: 0 0 1.5rem 0;
  font-weight: 600;
}

.error-description {
  font-size: 1.6rem;
  color: var(--color-text-secondary);
  margin: 0 0 3rem 0;
  line-height: 1.6;
}

.error-actions {
  display: flex;
  gap: 1.5rem;
  justify-content: center;
  margin-bottom: 3rem;
}

.error-actions .el-button {
  font-size: 1.4rem !important;
  padding: 1.2rem 2.4rem !important;
  border-radius: 0.8rem !important;
  font-weight: 600 !important;
}

/* 建议区域 */
.suggestions {
  text-align: left;
  max-width: 30rem;
  margin: 0 auto;
}

.suggestions h3 {
  font-size: 1.6rem;
  color: var(--color-text-primary);
  margin: 0 0 1rem 0;
}

.suggestions ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.suggestions li {
  margin-bottom: 0.8rem;
}

.suggestions a {
  color: var(--primary-teal);
  text-decoration: none;
  font-size: 1.4rem;
  cursor: pointer;
  transition: color 0.3s ease;
}

.suggestions a:hover {
  color: var(--primary-teal-dark);
  text-decoration: underline;
}

/* 插图区域 */
.error-illustration {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 40rem;
}

.illustration-content {
  position: relative;
  width: 30rem;
  height: 30rem;
}

.main-shape {
  width: 20rem;
  height: 20rem;
  background: linear-gradient(135deg, var(--primary-teal) 0%, var(--primary-teal-dark) 100%);
  border-radius: 50%;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  opacity: 0.8;
  animation: pulse 2s ease-in-out infinite alternate;
}

.float-element {
  position: absolute;
  border-radius: 50%;
  animation: float 3s ease-in-out infinite;
}

.element-1 {
  width: 6rem;
  height: 6rem;
  background-color: #f56565;
  top: 10%;
  left: 20%;
  animation-delay: 0s;
}

.element-2 {
  width: 4rem;
  height: 4rem;
  background-color: #4fd1c7;
  top: 20%;
  right: 15%;
  animation-delay: 1s;
}

.element-3 {
  width: 5rem;
  height: 5rem;
  background-color: #9f7aea;
  bottom: 15%;
  left: 15%;
  animation-delay: 2s;
}

/* 动画 */
@keyframes pulse {
  0% {
    transform: translate(-50%, -50%) scale(1);
  }
  100% {
    transform: translate(-50%, -50%) scale(1.05);
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-1rem);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .not-found-container {
    padding: 1rem;
  }
  
  .not-found-card {
    grid-template-columns: 1fr;
    gap: 2rem;
    padding: 3rem 2rem;
  }
  
  .error-title {
    font-size: 6rem;
  }
  
  .error-subtitle {
    font-size: 2rem;
  }
  
  .error-description {
    font-size: 1.4rem;
  }
  
  .error-actions {
    flex-direction: column;
    gap: 1rem;
  }
  
  .error-illustration {
    height: 25rem;
  }
  
  .illustration-content {
    width: 20rem;
    height: 20rem;
  }
  
  .main-shape {
    width: 15rem;
    height: 15rem;
  }
}

@media (max-width: 480px) {
  .not-found-card {
    padding: 2rem 1.5rem;
  }
  
  .error-title {
    font-size: 4.5rem;
  }
  
  .error-subtitle {
    font-size: 1.8rem;
  }
  
  .suggestions {
    text-align: center;
  }
  
  .element-1, .element-2, .element-3 {
    width: 3rem;
    height: 3rem;
  }
}
</style>
