<template>
  <div id="app">
    <header class="app-header">
      <Header />
    </header>
    
    <main class="main-content">
      <RouterView />
    </main>
  </div>
  <div class="mini-mask" :style="{ pointerEvents: uiState.loginning || uiState.registering ? 'auto' : 'none' }">
    <LoginMiniMask v-if="uiState.loginning"/>
    <RegisterMiniMask v-if="uiState.registering"/>
  </div>
</template>

<script setup lang="ts">
import { RouterView } from 'vue-router'
import Header from '@/components/Header.vue'
import LoginMiniMask from '@/components/LoginMiniMask.vue'
import RegisterMiniMask from '@/components/RegisterMiniMask.vue'
import { useUiStateStore } from '@/stores/uiStateStore'

const uiState = useUiStateStore()
</script>

<style scoped>
#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.app-header {
  width: 100vw;
  min-height: 8rem;
  background-color: var(--color-background);
  border-bottom: 1px solid var(--color-border);
  display: flex;
  align-items: center;
  padding: 0 2rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  margin: 0;
}

.main-content {
  flex: 1;
  width: 100vw;
  overflow-x: hidden;
  overflow-y: auto;
  margin: 0;
  padding: 0;
}

.mini-mask {
  position: fixed;
  width: 100%;
  height: 100vh;
  background-color: rgba(255, 255, 255, 0);
  z-index: 9998;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .app-header {
    min-height: 50px;
    padding: 0 1rem;
  }
}

@media (min-width: 1024px) {
  .app-header {
    min-height: 70px;
    padding: 0 3rem;
  }
}
</style>
