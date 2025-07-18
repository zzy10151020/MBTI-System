<template>
  <div class="header-container">
    <header class="header">
      <div class="header-title" @click="goHome">
        <span class="title-text primary">MBTI</span>
        <span class="title-text">System</span>
      </div>

      <div class="header-search">
        <SearchBar />
      </div>

      <HeaderAvatar />
    </header>
    <nav class="header-nav">
      <router-link 
        :to="homeRoute" 
        class="nav-link"
        :class="{ active: $route.name === 'home' }">
        首页
      </router-link>
      <router-link 
        :to="questionnaireRoute" 
        class="nav-link"
        :class="{ active: $route.name === 'questionnaires' }">
        问卷测试
      </router-link>
      <router-link 
        :to="resultsRoute" 
        class="nav-link"
        :class="{ active: $route.name === 'results' }"
        v-if="userStore.isLoggedIn">
        我的结果
      </router-link>
    </nav>
  </div>
</template>

<script setup lang="ts">
import { useRouter, useRoute } from 'vue-router'
import { computed } from 'vue'
import SearchBar from './SearchBar.vue'
import HeaderAvatar from './HeaderAvatar.vue'
import { useUserStore } from '@/stores/userStore'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// 获取当前用户的 uid，优先使用路由参数，其次使用 store 中的用户信息
const currentUid = computed(() => {
  return route.params.uid || (userStore.user?.userId?.toString())
})

// 构建带 uid 的路由对象
const homeRoute = computed(() => {
  return currentUid.value 
    ? { name: 'home', params: { uid: currentUid.value } }
    : { name: 'home' }
})

const questionnaireRoute = computed(() => {
  return currentUid.value 
    ? { name: 'questionnaires', params: { uid: currentUid.value } }
    : { name: 'questionnaires' }
})

const resultsRoute = computed(() => {
  return currentUid.value 
    ? { name: 'results', params: { uid: currentUid.value } }
    : { name: 'results' }
})

const goHome = () => {
  router.push(homeRoute.value)
}
</script>

<style scoped>
.header-container {
  width: 100%;
  height: auto;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  height: 100%;
  padding: 0;
  margin-top: 0.4rem;
  background-color: var(--color-background);
}

/* 左侧标题 */
.header-title {
  display: flex;
  align-items: baseline;
  justify-content: start;
  flex-shrink: 0;
  min-width: 20rem;
  gap: 0.5rem;
}

.title-text {
  font-size: 2rem;
  font-weight: bold;
  color: var(--color-text-primary);
  text-transform: uppercase;
  letter-spacing: 0.1rem;
  cursor: pointer;
  transition: color 0.3s ease;
}

.title-text.primary {
  font-size: 3rem;
  color: var(--primary-teal);
}

.title-text.primary:hover {
  color: var(--primary-teal-dark);
}

/* 导航菜单 */
.header-nav {
  display: flex;
  align-items: center;
  gap: 0.2rem;
}

.nav-link {
  font-size: 1rem;
  font-weight: 500;
  color: var(--color-text-secondary);
  text-decoration: none;
  padding: 0.4rem 0.8rem;
  border-radius: 0.4rem;
  transition: all 0.3s ease;
  position: relative;
}

.nav-link:hover {
  color: var(--primary-teal);
  background-color: var(--primary-teal-light);
}

.nav-link.active {
  color: var(--primary-teal);
  background-color: var(--primary-teal-light);
}

.nav-link.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 2rem;
  height: 0.2rem;
  background-color: var(--primary-teal);
  border-radius: 0.1rem;
}

/* 中间搜索栏 */
.header-search {
  flex: 1;
  max-width: 40rem; /* 400px */
  margin: 0 1rem;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header {
    padding: 0 1rem;
  }
  
  .header-title {
    min-width: 10rem; /* 100px */
  }
  
  .title-text {
    font-size: 1.6rem; /* 16px */
  }
  
  .header-nav {
    margin-left: 1rem;
    gap: 1rem;
  }
  
  .nav-link {
    font-size: 1.2rem;
    padding: 0.6rem 1rem;
  }
  
  .header-search {
    margin: 0 1rem;
    max-width: 20rem; /* 200px */
  }
}

@media (max-width: 480px) {
  .title-text {
    font-size: 1.4rem; /* 14px */
    letter-spacing: 0.05rem;
  }
  
  .header-nav {
    display: none; /* 移动端隐藏导航菜单 */
  }
  
  .header-search {
    max-width: 15rem; /* 150px */
    margin: 0 0.5rem;
  }
}
</style>