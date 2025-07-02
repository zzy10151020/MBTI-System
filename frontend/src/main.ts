import './assets/main.css'
import 'element-plus/dist/index.css'
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'
import App from './App.vue'
import router from './router'
import { useUserStore } from '@/stores/userStore'

// 开发环境下加载API调试工具
if (import.meta.env.DEV) {
  import('./api/debug')
}

const app = createApp(App)
const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)
app.use(pinia)
app.use(router)

// 应用挂载后初始化用户状态
app.mount('#app')

// 初始化用户store，检查登录状态
const userStore = useUserStore()
userStore.initialize()
