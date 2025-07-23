<template>
  <div class="questionnaire-container">
    <div class="questionnaire-card">
      <div class="page-header">
        <h1 class="page-title">
          搜索结果<span class="text-teal">问卷</span>
        </h1>
        <p class="page-subtitle">共找到 {{ totalResults }} 个相关问卷</p>
      </div>

      <div class="questionnaire-grid" v-if="searchResults.length > 0">
        <div 
          v-for="(questionnaire, index) in currentPageResults" 
          :key="questionnaire.questionnaireId"
          class="questionnaire-item"
          :class="{ 'featured': index === 0, 'completed': isQuestionnaireCompleted(questionnaire.questionnaireId) }"
        >
          <div class="item-badge" v-if="isQuestionnaireCompleted(questionnaire.questionnaireId)">
            <span>已完成</span>
          </div>
          <div class="item-header">
            <div class="item-icon">
              <el-icon><DocumentChecked /></el-icon>
            </div>
          </div>
          <div class="item-content">
            <h4 class="item-title">{{ questionnaire.title }}</h4>
            <p class="item-description">{{ questionnaire.description }}</p>
            <div class="item-stats">
              <div class="stat-item">
                <el-icon><DocumentChecked /></el-icon>
                <span>{{ questionnaire.questionCount || 0 }}道题目</span>
              </div>
              <div class="stat-item">
                <el-icon><Clock /></el-icon>
                <span>约{{ Math.ceil((questionnaire.questionCount || 60) / 4) }}分钟</span>
              </div>
            </div>
          </div>
          <div class="item-footer">
            <el-button 
              type="primary" 
              size="default" 
              :disabled="isQuestionnaireCompleted(questionnaire.questionnaireId)"
              @click.stop="startTest(questionnaire)"
            >
              <template v-if="isQuestionnaireCompleted(questionnaire.questionnaireId)">已完成</template>
              <template v-else>开始测试</template>
            </el-button>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-else class="empty-state">
        <el-icon><DocumentRemove /></el-icon>
        <h3>未找到相关问卷</h3>
        <p>请尝试修改关键词或稍后再试</p>
      </div>

      <!-- 分页组件 -->
      <div v-if="totalPages >= 1" class="pagination-section">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="searchResults.length"
          layout="total, sizes, prev, pager, next, jumper"
          :background="true"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
    <el-backtop :right="40" :bottom="40" :visibility-height="200">
      <div class="back-to-top">
        <el-icon><CaretTop /></el-icon>
      </div>
    </el-backtop>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { DocumentChecked, DocumentRemove, Clock, CaretTop } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/userStore'
import { useTestStore } from '@/stores/testStore'
import questionnaireApi from '@/api/questionnaire'
import type { Questionnaire } from '@/api'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const testStore = useTestStore()

// 搜索结果
const searchResults = ref<Questionnaire[]>([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const completedQuestionnaires = ref<Set<number>>(new Set())

const totalResults = computed(() => searchResults.value.length)
const totalPages = computed(() => Math.ceil(searchResults.value.length / pageSize.value))
const currentPageResults = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return searchResults.value.slice(start, end)
})

const isQuestionnaireCompleted = (questionnaireId: number): boolean => {
  return completedQuestionnaires.value.has(questionnaireId)
}

const startTest = async (questionnaire: Questionnaire) => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录后再进行测试')
    return
  }
  if (isQuestionnaireCompleted(questionnaire.questionnaireId)) {
    ElMessageBox.confirm(
      '您已完成过该问卷，是否要重新测试？重新测试将覆盖之前的结果。',
      '确认重新测试',
      {
        confirmButtonText: '重新测试',
        cancelButtonText: '取消',
        type: 'warning'
      }
    ).then(() => {
      navigateToTest(questionnaire)
    }).catch(() => {})
    return
  }
  navigateToTest(questionnaire)
}

const navigateToTest = (questionnaire: Questionnaire) => {
  try {
    const uid = route.params.uid || userStore.user?.userId?.toString()
    router.push({
      name: 'test',
      params: uid ? { uid } : {},
      query: { questionnaireId: questionnaire.questionnaireId.toString() }
    })
  } catch (error: any) {
    ElMessage.error('开始测试失败，请稍后再试')
  }
}

const handleSizeChange = (val: number) => {
  pageSize.value = val
  currentPage.value = 1
}
const handleCurrentChange = (val: number) => {
  currentPage.value = val
}

// 检查所有问卷的完成状态
const checkAllQuestionnairesCompletion = async () => {
  if (!userStore.isLoggedIn) return
  const completedSet = new Set<number>()
  for (const questionnaire of searchResults.value) {
    try {
      const result = await testStore.checkTestCompleted(questionnaire.questionnaireId)
      if (result.completed) {
        completedSet.add(questionnaire.questionnaireId)
      }
    } catch (error) {}
  }
  completedQuestionnaires.value = completedSet
}


// 根据关键词搜索问卷
const fetchSearchResults = async (keyword: string) => {
  if (!keyword || !keyword.trim()) {
    searchResults.value = []
    return
  }
  loading.value = true
  try {
    const res = await questionnaireApi.searchQuestionnaire({ title: keyword })
    // 兼容后端返回格式
    searchResults.value = Array.isArray(res?.data) ? res.data : (res?.data?.list || [])
  } catch (e) {
    searchResults.value = []
  } finally {
    loading.value = false
    checkAllQuestionnairesCompletion()
  }
}

// 监听路由变化，自动搜索
watch(
  () => route.query.keyword,
  (keyword) => {
    fetchSearchResults(keyword as string || '')
  },
  { immediate: true }
)
</script>

<style scoped>
.questionnaire-container {
  width: 100%;
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding: 2rem 8rem;
  background-color: var(--color-background-soft);
}

.questionnaire-card {
  max-width: 100rem;
  width: 100%;
  background-color: var(--color-background);
  border-radius: 2rem;
  box-shadow: 0 1.2rem 4rem rgba(0, 0, 0, 0.08);
  padding: 4rem 4rem 2rem 4rem;
  border: 1px solid var(--color-border);
}

.page-header {
  text-align: center;
  margin-bottom: 2rem;
  padding: 0 2rem;
}

.page-title {
  font-size: 2.4rem;
  line-height: 1.5;
  color: var(--color-text-primary);
  font-weight: 600;
  margin: 0 0 0.8rem 0;
  letter-spacing: 0.05rem;
}

.page-title .text-teal {
  color: var(--primary-teal);
  font-weight: bold;
}

.page-subtitle {
  font-size: 1.4rem;
  color: var(--color-text-secondary);
  margin: 0;
  font-weight: 400;
}

.questionnaire-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1.2rem;
  margin: 0 auto;
}

.questionnaire-item {
  position: relative;
  background-color: var(--color-background);
  border: 1px solid var(--color-border);
  border-radius: 1.6rem;
  padding: 0.5rem 2rem;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  aspect-ratio: 16 / 9;
  min-height: 14rem;
  max-height: 18rem;
  overflow: hidden;
}

.questionnaire-item:hover {
  transform: translateY(-0.4rem);
  box-shadow: 0 0.8rem 2.4rem rgba(32, 178, 170, 0.15);
  border-color: var(--primary-teal-light);
}

.questionnaire-item.featured {
  border-color: var(--primary-teal);
  background: linear-gradient(135deg, var(--color-background) 0%, var(--primary-teal-light) 100%);
}

.questionnaire-item.completed {
  background: linear-gradient(135deg, var(--color-background-soft) 0%, #f0f9ff 100%);
  border-color: #22c55e;
}

.questionnaire-item.completed:hover {
  border-color: #16a34a;
  box-shadow: 0 0.8rem 2.4rem rgba(34, 197, 94, 0.15);
}

.item-header {
  display: flex;
  justify-content: flex-start;
  align-items: flex-start;
  margin: 0.5rem 0;
}

.item-icon {
  width: 4rem;
  height: 4rem;
  background-color: var(--primary-teal-light);
  border-radius: 0.8rem;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--primary-teal);
  font-size: 2rem;
}

.item-badge {
  position: absolute;
  top: 1.5rem;
  right: 1.5rem;
  background-color: #22c55e;
  color: white;
  padding: 0.4rem 0.8rem;
  border-radius: 1rem;
  font-size: 1rem;
  font-weight: 500;
  z-index: 2;
  box-shadow: 0 2px 6px rgba(34, 197, 94, 0.2);
}

.item-content {
  flex: 1;
}

.item-title {
  font-size: 1.6rem;
  font-weight: 600;
  color: var(--color-text-primary);
  margin-bottom: 0.2rem;
  line-height: 1.3;
}

.item-description {
  font-size: 1.2rem;
  color: var(--color-text-secondary);
  line-height: 1.4;
  margin-bottom: 0.5rem;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-clamp: 2;
  overflow: hidden;
}

.item-stats {
  display: flex;
  gap: 1.5rem;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 1.1rem;
  color: var(--color-text-soft);
}

.stat-item .el-icon {
  font-size: 1.2rem;
}

.item-footer {
  position: absolute;
  right: 1.5rem;
  bottom: 1.5rem;
  display: flex;
  justify-content: flex-end;
  font-size: 1.2rem;
  width: auto;
  background: none;
  box-shadow: none;
  z-index: 3;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 50rem;
  padding: 4rem 2rem;
  background-color: var(--color-background-soft);
  border-radius: 1.6rem;
  border: 1px solid var(--color-border);
  margin: 2rem 0;
  color: var(--color-text-soft);
}

.empty-state .el-icon {
  font-size: 6rem;
  margin-bottom: 2rem;
  color: var(--color-text-soft);
}

.empty-state h3 {
  font-size: 2rem;
  margin: 0 0 1rem 0;
  color: var(--color-text-secondary);
}

.empty-state p {
  font-size: 1.4rem;
  margin: 0;
  color: var(--color-text-soft);
}

.pagination-section {
  display: flex;
  justify-content: center;
  margin-top: 5rem;
  margin-bottom: 2rem;
  padding: 3rem 2rem;
  background-color: var(--color-background-soft);
  border-radius: 1.2rem;
  border: 1px solid var(--color-border);
}

.back-to-top {
  width: 2.5rem;
  height: 2.5rem;
  background-color: var(--primary-teal);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 1.8rem;
  transition: all 0.3s ease;
  box-shadow: 0 0.4rem 1.2rem rgba(32, 178, 170, 0.3);
}

.back-to-top:hover {
  background-color: var(--primary-teal-dark);
  transform: scale(1.1);
  box-shadow: 0 0.6rem 1.6rem rgba(32, 178, 170, 0.4);
}

@media (max-width: 768px) {
  .questionnaire-container {
    padding: 1rem;
  }
  .questionnaire-card {
    padding: 2rem;
    border-radius: 1.2rem;
  }
  .page-title {
    font-size: 1.8rem;
  }
  .page-subtitle {
    font-size: 1.2rem;
  }
  .questionnaire-grid {
    grid-template-columns: 1fr;
    grid-template-rows: repeat(12, 1fr);
    gap: 2rem;
    margin: 0 1rem;
  }
  .questionnaire-item {
    padding: 2rem;
    min-height: 16rem;
    aspect-ratio: 16 / 10;
  }
  .item-title {
    font-size: 1.4rem;
  }
  .item-description {
    font-size: 1.1rem;
  }
}

@media (max-width: 480px) {
  .questionnaire-container {
    padding: 1.5rem 1rem;
  }
  .questionnaire-card {
    padding: 2rem 1.5rem;
  }
  .page-title {
    font-size: 1.6rem;
  }
  .questionnaire-grid {
    gap: 1.5rem;
    margin: 0 0.5rem;
  }
  .questionnaire-item {
    padding: 1.8rem;
    min-height: 14rem;
  }
  .item-icon {
    width: 3rem;
    height: 3rem;
    font-size: 1.6rem;
  }
  .item-stats {
    flex-direction: column;
    gap: 0.8rem;
  }
}
</style>
