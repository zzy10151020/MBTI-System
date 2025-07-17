import './assets/main.css'
import 'element-plus/dist/index.css'
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'
import App from './App.vue'
import router from './router'
import { useUserStore } from '@/stores/userStore'
// 导入Cookie调试工具
import '@/utils/cookieHelper'

const app = createApp(App)

// 全局错误处理
app.config.errorHandler = (err, instance, info) => {
  console.error('全局错误:', err)
  console.error('错误信息:', info)
  
  // 在生产环境中，可以将错误发送到监控服务
  if (import.meta.env.PROD) {
    // 发送错误到监控服务（如 Sentry）
    // sendErrorToMonitoring(err, info)
  }
  
  // 跳转到错误页面
  router.push({
    name: 'error',
    query: { 
      message: encodeURIComponent(err instanceof Error ? err.message : '系统遇到未知错误') 
    }
  })
}

// 捕获未处理的Promise异常
window.addEventListener('unhandledrejection', (event) => {
  console.error('未处理的Promise异常:', event.reason)
  
  if (import.meta.env.PROD) {
    // 发送错误到监控服务
    // sendErrorToMonitoring(event.reason, 'unhandledrejection')
  }
  
  event.preventDefault()
  router.push({
    name: 'error',
    query: { 
      message: encodeURIComponent('网络请求失败，请检查网络连接') 
    }
  })
})

const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)
app.use(pinia)
app.use(router)

// 应用挂载后初始化用户状态
app.mount('#app')

// 初始化用户store，检查登录状态
const userStore = useUserStore()
userStore.initialize();

// 注册全局调试函数
(window as any).debugSession = userStore.debugSession
