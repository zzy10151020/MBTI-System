<template>
  <el-input v-model="searchText" :placeholder="searchRecommendation"
    class="search-input" size="large">
    <template #suffix>
      <el-icon class="el-input__icon"><Search/></el-icon>
    </template>
  </el-input>
</template>

<script setup lang="ts">
import { Search } from '@element-plus/icons-vue'
import { ref } from 'vue'

const searchText = ref('')
const searchRecommendation = ref('')
</script>

<style scoped>
.search-input {
  width: 100%;
  height: 100%;
}

:deep(.el-input__wrapper) {
  background-color: rgba(240, 240, 240, 0.9);
  border-radius: 0.8rem;
  box-shadow: 0 0 0 1px var(--el-input-border-color, var(--el-border-color)) inset;
  transition: all 0.3s ease;
}

:deep(.el-input__wrapper:hover) {
  background-color: rgba(245, 245, 245, 0.8);
  box-shadow: 0 0 0 1px var(--el-input-border-color, var(--el-border-color)) inset;
}

:deep(.el-input__wrapper.is-focus) {
  background-color: #FFFFFF;
}

:deep(.el-input__icon) {
  color: #000000;
  font-size: 1.4rem;
}
</style>