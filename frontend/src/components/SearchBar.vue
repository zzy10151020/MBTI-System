<template>
  <el-input
    v-model="searchText"
    :placeholder="searchRecommendation"
    class="search-input"
    size="large"
    @keyup.enter="onSearch"
    @blur="onBlur"
  >
    <template #suffix>
      <el-icon class="el-input__icon" @click="onSearch"><Search/></el-icon>
    </template>
  </el-input>
</template>

<script setup lang="ts">
import { Search } from '@element-plus/icons-vue'
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const searchText = ref('')
const searchRecommendation = ref('')
const router = useRouter()
const route = useRoute()

const onSearch = () => {
  const keyword = searchText.value.trim()
  if (!keyword) return
  // 跳转到搜索结果页，保留uid参数
  const uid = route.params.uid
  router.push({
    name: 'questionnaire-search',
    params: uid ? { uid } : {},
    query: { keyword }
  })
}

const onBlur = () => {
  // 可选：失焦时自动搜索
}
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