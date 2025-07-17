<template>
  <div class="questionnaire-container">
    <div class="questionnaire-card">
      <div class="page-header">
        <h1 class="page-title">
          选择你想要的<span class="text-teal">MBTI</span>问卷进行测试
        </h1>
        <p class="page-subtitle">探索你的性格类型，发现真实的自己</p>
      </div>

      <div class="questionnaire-grid" v-if="questionnaireStore.questionnaires.length > 0">
        <!-- 大图片区域 - 占据第一列前两行 -->
        <div class="featured-image">
          <div class="image-content">
            <img src="/MBTI_LETTER/I.png" alt="MBTI测试" class="feature-img" />
            <div class="image-overlay">
              <h3>MBTI性格测试</h3>
              <p>科学专业的性格分析工具</p>
            </div>
          </div>
        </div>

        <!-- 问卷卡片 -->
        <div 
          v-for="(questionnaire, index) in currentPageQuestionnaires" 
          :key="questionnaire.questionnaireId"
          class="questionnaire-item"
          :class="{ 'featured': index === 0 }"
          @click="selectQuestionnaire(questionnaire)"
        >
          <!-- 暂时移除已完成标记，因为后端DTO中没有hasAnswered字段 -->
          <!-- <div class="item-badge" v-if="questionnaire.hasAnswered">
            <span>已完成</span>
          </div> -->
          
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
                <el-icon><User /></el-icon>
                <!-- 暂时显示固定数值，因为后端DTO中没有answerCount字段 -->
                <span>0人已测试</span>
              </div>
              <div class="stat-item">
                <el-icon><Clock /></el-icon>
                <span>约15分钟</span>
              </div>
            </div>
          </div>

          <div class="item-footer">
            <el-button 
              type="primary" 
              size="default" 
              @click.stop="startTest(questionnaire)"
            >
              开始测试
            </el-button>
          </div>
        </div>

        <!-- 填充空白卡片 -->
        <div 
          v-for="n in emptySlots" 
          :key="'empty-' + n"
          class="questionnaire-item empty-slot"
        >
          <div class="empty-content">
            <el-icon><Plus /></el-icon>
            <p>更多问卷即将推出</p>
          </div>
        </div>
      </div>

      <!-- 分页组件 -->
      <div v-if="totalPages >= 1" class="pagination-section">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="questionnaireStore.questionnaires.length"
          layout="total, sizes, prev, pager, next, jumper"
          :background="true"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>

      <!-- 加载状态 -->
      <div v-else-if="questionnaireStore.loading" class="loading-state">
        <el-icon class="loading-icon"><Loading /></el-icon>
        <p>正在加载问卷...</p>
      </div>

      <!-- 空状态 -->
      <div v-else class="empty-state">
        <el-icon><DocumentRemove /></el-icon>
        <h3>暂无可用问卷</h3>
        <p>管理员还未发布任何问卷，请稍后再来查看</p>
      </div>
    </div>

    <!-- 回到顶部按钮 -->
    <el-backtop 
      :right="40" 
      :bottom="40"
      :visibility-height="200"
    >
      <div class="back-to-top">
        <el-icon><CaretTop /></el-icon>
      </div>
    </el-backtop>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  DocumentChecked, 
  DocumentRemove, 
  User, 
  Clock, 
  Plus, 
  Loading,
  CaretTop
} from '@element-plus/icons-vue'
import { useQuestionnaireStore } from '@/stores/questionnaireStore'
import { useUserStore } from '@/stores/userStore'
import type { Questionnaire } from '@/api'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const questionnaireStore = useQuestionnaireStore()

// 响应式数据
const currentPage = ref(1)
const pageSize = ref(10) // 每页10个问卷（2列6行-1个大图片位置）

// 计算属性
const totalPages = computed(() => {
  return Math.ceil(questionnaireStore.questionnaires.length / pageSize.value)
})

const currentPageQuestionnaires = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return questionnaireStore.questionnaires.slice(start, end)
})

const emptySlots = computed(() => {
  // 计算当前页需要填充的空白位置数量
  const filledSlots = currentPageQuestionnaires.value.length
  return Math.max(0, pageSize.value - filledSlots)
})

// 方法
const fetchQuestionnaires = async () => {
  await questionnaireStore.fetchQuestionnaires()
}

const selectQuestionnaire = (questionnaire: Questionnaire) => {
  console.log('选择问卷:', questionnaire.title)
  // 这里可以显示问卷详情或直接开始测试
}

const startTest = async (questionnaire: Questionnaire) => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录后再进行测试')
    return
  }

  // 暂时移除已完成检查，因为后端DTO中没有hasAnswered字段
  // if (questionnaire.hasAnswered) {
  //   ElMessage.info('您已完成过该问卷')
  //   return
  // }

  try {
    // 跳转到测试页面，保持uid参数并传递问卷ID
    const uid = route.params.uid || userStore.user?.userId?.toString()
    router.push({
      name: 'test',
      params: uid ? { uid } : {},
      query: { questionnaireId: questionnaire.questionnaireId.toString() }
    })
  } catch (error: any) {
    console.error('开始测试失败:', error)
    ElMessage.error('开始测试失败，请稍后再试')
  }
}

// 分页处理
const handleSizeChange = (val: number) => {
  pageSize.value = val
  currentPage.value = 1
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
}

// 组件挂载时获取数据
onMounted(() => {
  fetchQuestionnaires()
  
  // 检查是否有完成的测试结果需要显示
  checkCompletedTest()
})

// 检查完成的测试
const checkCompletedTest = () => {
  const completed = route.query.completed
  const mbtiType = route.query.mbtiType as string
  const answerId = route.query.answerId as string
  
  if (completed === 'true' && mbtiType) {
    // 显示测试完成的祝贺信息
    setTimeout(() => {
      ElMessageBox.alert(
        `恭喜您完成了MBTI测试！\n您的性格类型是：${mbtiType}\n\n您可以在个人中心查看详细的性格分析报告。`,
        '测试完成',
        {
          confirmButtonText: '知道了',
          type: 'success',
          dangerouslyUseHTMLString: false
        }
      ).then(() => {
        // 清除URL中的查询参数
        const uid = route.params.uid
        router.replace({
          name: 'questionnaires',
          params: uid ? { uid } : {}
        })
      })
    }, 500) // 延迟显示，确保页面加载完成
  }
}
</script>

<style scoped>
.questionnaire-container {
  width: 100%;
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding: 2rem 8rem; /* 增加顶部和底部边距 */
  background-color: var(--color-background-soft);
}

.questionnaire-card {
  max-width: 100rem;
  width: 100%;
  background-color: var(--color-background);
  border-radius: 2rem;
  box-shadow: 0 1.2rem 4rem rgba(0, 0, 0, 0.08);
  padding: 5rem 4rem;
  border: 1px solid var(--color-border);
}

/* 页面头部 */
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

/* Grid布局 - 2列6行 */
.questionnaire-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  grid-template-rows: repeat(6, 1fr);
  gap: 1.2rem;
  min-height: 72rem;
  margin: 0 auto;
}

/* 特色图片区域 - 占据第一列前两行 */
.featured-image {
  grid-column: 1;
  grid-row: 1 / 3; /* 占据第1-2行 */
  position: relative;
  border-radius: 1.6rem;
  overflow: hidden;
  background: linear-gradient(135deg, var(--primary-teal-light), var(--primary-teal));
  display: flex;
  align-items: center;
  justify-content: center;
}

.image-content {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.feature-img {
  width: 80%;
  height: 80%;
  object-fit: contain;
  opacity: 0.8;
}

.image-overlay {
  position: absolute;
  bottom: 2rem;
  left: 2rem;
  right: 2rem;
  color: white;
  text-align: center;
}

.image-overlay h3 {
  font-size: 2rem;
  margin: 0 0 0.5rem 0;
  font-weight: 600;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.image-overlay p {
  font-size: 1.4rem;
  margin: 0;
  opacity: 0.9;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
}

/* 问卷卡片 */
.questionnaire-item {
  position: relative; /* 为绝对定位的badge提供相对定位参考 */
  background-color: var(--color-background);
  border: 1px solid var(--color-border);
  border-radius: 1.6rem;
  padding: 0.5rem 2rem;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  aspect-ratio: 16 / 9; /* 设置16:9比例 */
  min-height: 14rem; /* 增加最小高度 */
  max-height: 18rem; /* 限制最大高度 */
  overflow: hidden; /* 防止内容溢出 */
}

.questionnaire-item:hover {
  transform: translateY(-0.4rem);
  box-shadow: 0 0.8rem 2.4rem rgba(32, 178, 170, 0.15);
  border-color: var(--primary-teal-light);
}

.questionnaire-item.featured {
  border-color: var(--primary-teal);
  background: linear-gradient(135deg, 
    var(--color-background) 0%, 
    var(--primary-teal-light) 100%);
}

/* 卡片头部 */
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
  background-color: var(--primary-teal);
  color: white;
  padding: 0.4rem 0.8rem;
  border-radius: 1rem;
  font-size: 1rem;
  font-weight: 500;
  z-index: 2;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

/* 卡片内容 */
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
  line-clamp: 2; /* 标准属性 */
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

/* 卡片底部 */
.item-footer {
  display: flex;
  justify-content: flex-end;
  font-size: 1.2rem;
}

/* 空白卡片 */
.empty-slot {
  background-color: var(--color-background-soft);
  border: 2px dashed var(--color-border);
  cursor: default;
}

.empty-slot:hover {
  transform: none;
  box-shadow: none;
  border-color: var(--color-border);
}

.empty-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: var(--color-text-soft);
}

.empty-content .el-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.empty-content p {
  font-size: 1.2rem;
  margin: 0;
}

/* 加载和空状态 */
.loading-state,
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 50rem; /* 增加最小高度 */
  padding: 4rem 2rem; /* 增加内边距 */
  background-color: var(--color-background-soft);
  border-radius: 1.6rem;
  border: 1px solid var(--color-border);
  margin: 2rem 0; /* 增加外边距 */
  color: var(--color-text-soft);
}

.loading-icon {
  font-size: 4rem;
  margin-bottom: 2rem;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
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

/* 按钮样式覆盖 */
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

:deep(.el-button--primary:disabled) {
  background-color: var(--color-background-soft) !important;
  border-color: var(--color-border) !important;
  color: var(--color-text-soft) !important;
}

/* 分页组件样式 */
.pagination-section {
  display: flex;
  justify-content: center;
  margin-top: 5rem; /* 增加顶部间距 */
  margin-bottom: 2rem; /* 增加底部间距 */
  padding: 3rem 2rem; /* 增加内边距 */
  background-color: var(--color-background-soft);
  border-radius: 1.2rem;
  border: 1px solid var(--color-border);
}

/* 回到顶部按钮样式 */
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

/* Element Plus 分页组件样式覆盖 */
:deep(.el-pagination) {
  --el-pagination-bg-color: var(--color-background);
  --el-pagination-text-color: var(--color-text-primary);
  --el-pagination-border-radius: 0.6rem;
}

:deep(.el-pagination .btn-next),
:deep(.el-pagination .btn-prev) {
  background-color: var(--color-background-soft);
  border: 1px solid var(--color-border);
  color: var(--color-text-primary);
}

:deep(.el-pagination .btn-next:hover),
:deep(.el-pagination .btn-prev:hover) {
  background-color: var(--primary-teal-light);
  border-color: var(--primary-teal);
  color: var(--primary-teal-darker);
}

:deep(.el-pagination .el-pager li) {
  background-color: var(--color-background-soft);
  border: 1px solid var(--color-border);
  color: var(--color-text-primary);
  margin: 0 0.2rem;
  border-radius: 0.4rem;
}

:deep(.el-pagination .el-pager li:hover) {
  background-color: var(--primary-teal-light);
  border-color: var(--primary-teal);
  color: var(--primary-teal-darker);
}

:deep(.el-pagination .el-pager li.is-active) {
  background-color: var(--primary-teal) !important;
  border-color: var(--primary-teal) !important;
  color: white !important;
}

:deep(.el-pagination .el-select .el-select__wrapper) {
  background-color: var(--color-background-soft);
  border-color: var(--color-border);
}

/* Element Plus Backtop组件样式覆盖 */
:deep(.el-backtop) {
  background-color: transparent !important;
  border: none !important;
  box-shadow: none !important;
}

/* 响应式设计 */
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
    grid-template-columns: 1fr; /* 移动端改为单列 */
    grid-template-rows: repeat(12, 1fr); /* 增加行数适应单列布局 */
    gap: 2rem; /* 增加间距 */
    margin: 0 1rem; /* 调整左右边距 */
  }
  
  .featured-image {
    grid-column: 1;
    grid-row: 1 / 3;
    min-height: 16rem; /* 移动端最小高度 */
  }
  
  .questionnaire-item {
    padding: 2rem; /* 增加内边距 */
    min-height: 16rem; /* 增加最小高度 */
    aspect-ratio: 16 / 10; /* 移动端调整比例 */
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
    padding: 1.5rem 1rem; /* 增加顶部和底部边距 */
  }
  
  .questionnaire-card {
    padding: 2rem 1.5rem; /* 调整内边距 */
  }
  
  .page-title {
    font-size: 1.6rem;
  }
  
  .questionnaire-grid {
    gap: 1.5rem; /* 增加间距 */
    margin: 0 0.5rem; /* 调整左右边距 */
  }
  
  .questionnaire-item {
    padding: 1.8rem; /* 增加内边距 */
    min-height: 14rem; /* 调整最小高度 */
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
