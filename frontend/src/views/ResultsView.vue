<template>
  <div class="results-container">
    <div class="results-card">
      <!-- 页面头部 -->
      <div class="page-header">
        <h1 class="page-title">我的测试结果</h1>
        <p class="page-subtitle">查看您的MBTI性格测试历史记录和详细报告</p>
      </div>

      <!-- 统计卡片 -->
      <div class="stats-section" v-if="testStore.hasTestResults">
        <div class="stat-card">
          <div class="stat-icon">
            <el-icon><DocumentChecked /></el-icon>
          </div>
          <div class="stat-content">
            <h3>{{ testStore.testResults.length }}</h3>
            <p>已完成测试</p>
          </div>
        </div>
        
        <div class="stat-card" v-if="testStore.latestTestResult">
          <div class="stat-icon mbti-icon">
            <span>{{ testStore.latestTestResult.mbtiType }}</span>
          </div>
          <div class="stat-content">
            <h3>最新类型</h3>
            <p>{{ formatDate(testStore.latestTestResult.submittedAt) }}</p>
          </div>
        </div>
      </div>

      <!-- 测试结果列表 -->
      <div class="results-section" v-if="testStore.hasTestResults">
        <h2 class="section-title">测试历史</h2>
        
        <div class="results-grid">
          <div 
            v-for="result in testStore.testResults" 
            :key="result.answerId"
            class="result-card"
          >
            <div class="result-header">
              <div class="result-info">
                <h3 class="result-title">{{ result.questionnaireTitle }}</h3>
                <p class="result-date">{{ formatDate(result.submittedAt) }}</p>
              </div>
              <div class="result-actions">
                <el-dropdown @command="handleAction">
                  <el-button text>
                    <el-icon><MoreFilled /></el-icon>
                  </el-button>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item :command="{action: 'view', id: result.answerId}">
                        查看报告
                      </el-dropdown-item>
                      <el-dropdown-item :command="{action: 'retake', id: result.questionnaireId}">
                        重新测试
                      </el-dropdown-item>
                      <el-dropdown-item :command="{action: 'delete', id: result.answerId}" divided>
                        删除记录
                      </el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </div>
            </div>
            
            <div class="result-content">
              <div class="mbti-display">
                <div class="mbti-type">{{ result.mbtiType }}</div>
                <div class="mbti-letters">
                  <span 
                    v-for="(letter, index) in result.mbtiType.split('')" 
                    :key="index"
                    class="mbti-letter"
                  >
                    {{ letter }}
                  </span>
                </div>
              </div>
              
              <div class="result-summary">
                <p>{{ getMbtiDescription(result.mbtiType) }}</p>
              </div>
            </div>
            
            <div class="result-footer">
              <el-button 
                type="primary" 
                size="small"
                @click="viewReport(result.answerId)"
              >
                查看详细报告
              </el-button>
            </div>
          </div>
        </div>
        
        <!-- 分页 -->
        <div class="pagination-section" v-if="testStore.pagination.totalPages > 1">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[5, 10, 20]"
            :total="testStore.pagination.totalElements"
            layout="total, sizes, prev, pager, next, jumper"
            :background="true"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>

      <!-- 空状态 -->
      <div v-else-if="!testStore.loading" class="empty-state">
        <div class="empty-icon">
          <el-icon><DocumentRemove /></el-icon>
        </div>
        <h3>暂无测试记录</h3>
        <p>您还没有完成任何MBTI测试，快去开始您的第一次测试吧！</p>
        <el-button 
          type="primary" 
          size="large"
          @click="goToQuestionnaires"
        >
          开始测试
        </el-button>
      </div>

      <!-- 加载状态 -->
      <div v-if="testStore.loading" class="loading-state">
        <el-icon class="loading-icon"><Loading /></el-icon>
        <p>正在加载测试结果...</p>
      </div>
    </div>

    <!-- 详细报告弹窗 -->
    <el-dialog
      v-model="showReportDialog"
      title="MBTI详细报告"
      width="800px"
      :before-close="handleDialogClose"
    >
      <div v-if="currentReport" class="report-content">
        <div class="report-header">
          <div class="report-mbti">
            <h2>{{ currentReport.mbtiType }}</h2>
            <div class="dimension-scores">
              <div 
                v-for="(score, dimension) in currentReport.dimensionScores" 
                :key="dimension"
                class="dimension-item"
              >
                <span class="dimension-label">{{ dimension }}</span>
                <div class="dimension-bar">
                  <div 
                    class="dimension-fill"
                    :style="{ width: `${getDimensionPercentage(score)}%` }"
                  ></div>
                </div>
                <span class="dimension-value">{{ score }}</span>
              </div>
            </div>
          </div>
        </div>

        <div class="report-section">
          <h3>性格描述</h3>
          <p>{{ currentReport.description }}</p>
        </div>

        <div class="report-section">
          <h3>优势特质</h3>
          <div class="trait-tags">
            <el-tag 
              v-for="strength in currentReport.strengths" 
              :key="strength"
              type="success"
            >
              {{ strength }}
            </el-tag>
          </div>
        </div>

        <div class="report-section">
          <h3>成长挑战</h3>
          <div class="trait-tags">
            <el-tag 
              v-for="challenge in currentReport.challenges" 
              :key="challenge"
              type="warning"
            >
              {{ challenge }}
            </el-tag>
          </div>
        </div>

        <div class="report-section">
          <h3>适合职业</h3>
          <div class="trait-tags">
            <el-tag 
              v-for="career in currentReport.careers" 
              :key="career"
              type="info"
            >
              {{ career }}
            </el-tag>
          </div>
        </div>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showReportDialog = false">关闭</el-button>
          <el-button type="primary" @click="downloadReport">下载报告</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  DocumentChecked, 
  DocumentRemove, 
  Loading, 
  MoreFilled 
} from '@element-plus/icons-vue'
import { useTestStore } from '@/stores/testStore'
import { useUserStore } from '@/stores/userStore'
import type { MbtiReport } from '@/api/types'

const router = useRouter()
const route = useRoute()
const testStore = useTestStore()
const userStore = useUserStore()

// 响应式数据
const currentPage = ref(1)
const pageSize = ref(10)
const showReportDialog = ref(false)
const currentReport = ref<MbtiReport | null>(null)

// 计算属性
const currentUid = computed(() => {
  return route.params.uid || userStore.user?.userId?.toString()
})

// MBTI类型描述
const mbtiDescriptions: Record<string, string> = {
  'ENFP': '热情、富有想象力和创造力的人，认为生活充满可能性。',
  'INFP': '理想主义和忠诚的人，总是寻求与自己的价值观一致的方式。',
  'ENFJ': '热情且负责任的人，对他人的感受非常敏感。',
  'INFJ': '具有创造性、洞察力和责任感的人，寻求意义和真实。',
  'ENTP': '快速、机智、警觉且坦率的人，擅长解决新的挑战性问题。',
  'INTP': '寻求理解世界的逻辑原理的思考者，重视知识和能力。',
  'ENTJ': '坦率、果断，天生的领导者，能够看到不合逻辑和低效的程序和政策。',
  'INTJ': '具有原创思维的独立思考者，对实现自己的想法和目标有很大的动力。',
  'ESFP': '外向、友善、接受性强的人，热爱生活和人群。',
  'ISFP': '安静、友善、敏感、和善的人，不喜欢冲突。',
  'ESFJ': '热心、负责、合作的人，希望周围的环境和谐有序。',
  'ISFJ': '安静、友善、负责、良知的人，致力于满足他人的需要。',
  'ESTP': '灵活、宽容，采取现实主义的方法，擅长处理危机。',
  'ISTP': '宽容、灵活，安静的观察者，能够快速分析问题。',
  'ESTJ': '实际、现实、有事实根据的人，果断、有组织能力。',
  'ISTJ': '安静、严肃、通过专注和可信赖获得成功。'
}

// 方法
const getMbtiDescription = (mbtiType: string): string => {
  return mbtiDescriptions[mbtiType] || '独特的性格类型，具有自己的优势和特点。'
}

const formatDate = (dateString: string): string => {
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const getDimensionPercentage = (score: number): number => {
  // 将-10到10的分数转换为0-100的百分比
  return Math.max(0, Math.min(100, (score + 10) * 5))
}

const viewReport = async (answerId: number) => {
  try {
    const report = await testStore.fetchMbtiReport(answerId)
    currentReport.value = report
    showReportDialog.value = true
  } catch (error) {
    ElMessage.error('获取报告失败')
  }
}

const handleAction = async (command: {action: string, id: number}) => {
  const { action, id } = command
  
  switch (action) {
    case 'view':
      await viewReport(id)
      break
    case 'retake':
      ElMessageBox.confirm(
        '重新测试将删除当前结果，是否继续？',
        '确认重新测试',
        {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: 'warning'
        }
      ).then(async () => {
        const success = await testStore.retakeTest(id)
        if (success) {
          goToTest(id)
        }
      })
      break
    case 'delete':
      ElMessageBox.confirm(
        '确定要删除这条测试记录吗？此操作不可恢复。',
        '确认删除',
        {
          confirmButtonText: '删除',
          cancelButtonText: '取消',
          type: 'warning'
        }
      ).then(async () => {
        await testStore.deleteTestResult(id)
      })
      break
  }
}

const downloadReport = () => {
  if (!currentReport.value) return
  
  // 这里可以实现报告下载功能
  ElMessage.info('报告下载功能开发中...')
}

const goToQuestionnaires = () => {
  if (currentUid.value) {
    router.push({ 
      name: 'questionnaires', 
      params: { uid: currentUid.value }
    })
  } else {
    router.push({ name: 'questionnaires' })
  }
}

const goToTest = (questionnaireId: number) => {
  if (currentUid.value) {
    router.push({ 
      name: 'test', 
      params: { uid: currentUid.value },
      query: { questionnaireId: questionnaireId.toString() }
    })
  } else {
    router.push({ 
      name: 'test',
      query: { questionnaireId: questionnaireId.toString() }
    })
  }
}

const handleSizeChange = (val: number) => {
  pageSize.value = val
  currentPage.value = 1
  fetchResults()
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  fetchResults()
}

const fetchResults = () => {
  testStore.fetchTestResults({
    page: currentPage.value - 1,
    size: pageSize.value
  })
}

const handleDialogClose = () => {
  showReportDialog.value = false
  currentReport.value = null
}

// 生命周期
onMounted(() => {
  fetchResults()
})
</script>

<style scoped>
.results-container {
  width: 100%;
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding: 2rem;
  background-color: var(--color-background-soft);
}

.results-card {
  max-width: 100rem;
  width: 100%;
  background-color: var(--color-background);
  border-radius: 1.6rem;
  box-shadow: 0 0.8rem 3.2rem rgba(0, 0, 0, 0.1);
  padding: 4rem;
  border: 1px solid var(--color-border);
}

/* 页面头部 */
.page-header {
  text-align: center;
  margin-bottom: 4rem;
}

.page-title {
  font-size: 2.4rem;
  color: var(--color-text-primary);
  font-weight: 600;
  margin: 0 0 1rem 0;
  letter-spacing: 0.05rem;
}

.page-subtitle {
  font-size: 1.4rem;
  color: var(--color-text-secondary);
  margin: 0;
}

/* 统计卡片 */
.stats-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
  margin-bottom: 4rem;
}

.stat-card {
  background-color: var(--color-background-soft);
  border-radius: 1.2rem;
  padding: 2rem;
  display: flex;
  align-items: center;
  gap: 1.5rem;
  border: 1px solid var(--color-border);
}

.stat-icon {
  width: 4rem;
  height: 4rem;
  background-color: var(--primary-teal);
  border-radius: 0.8rem;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 2rem;
}

.stat-icon.mbti-icon {
  background: linear-gradient(135deg, var(--primary-teal), var(--primary-teal-dark));
  font-size: 1.2rem;
  font-weight: bold;
}

.stat-content h3 {
  font-size: 2rem;
  color: var(--color-text-primary);
  margin: 0 0 0.5rem 0;
  font-weight: 600;
}

.stat-content p {
  font-size: 1.2rem;
  color: var(--color-text-secondary);
  margin: 0;
}

/* 结果列表 */
.results-section {
  margin-bottom: 2rem;
}

.section-title {
  font-size: 1.8rem;
  color: var(--color-text-primary);
  font-weight: 600;
  margin: 0 0 2rem 0;
}

.results-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 2rem;
  margin-bottom: 3rem;
}

.result-card {
  background-color: var(--color-background-soft);
  border-radius: 1.2rem;
  padding: 2rem;
  border: 1px solid var(--color-border);
  transition: all 0.3s ease;
}

.result-card:hover {
  transform: translateY(-0.2rem);
  box-shadow: 0 0.8rem 2.4rem rgba(0, 0, 0, 0.1);
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 1.5rem;
}

.result-title {
  font-size: 1.4rem;
  color: var(--color-text-primary);
  font-weight: 600;
  margin: 0 0 0.5rem 0;
}

.result-date {
  font-size: 1.1rem;
  color: var(--color-text-secondary);
  margin: 0;
}

.result-content {
  margin-bottom: 2rem;
}

.mbti-display {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 1rem;
}

.mbti-type {
  font-size: 2.4rem;
  font-weight: bold;
  color: var(--primary-teal);
}

.mbti-letters {
  display: flex;
  gap: 0.5rem;
}

.mbti-letter {
  width: 2rem;
  height: 2rem;
  background-color: var(--primary-teal-light);
  color: var(--primary-teal-dark);
  border-radius: 0.4rem;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1rem;
  font-weight: bold;
}

.result-summary p {
  font-size: 1.2rem;
  color: var(--color-text-secondary);
  line-height: 1.5;
  margin: 0;
}

.result-footer {
  display: flex;
  justify-content: flex-end;
}

/* 报告弹窗 */
.report-content {
  max-height: 60vh;
  overflow-y: auto;
}

.report-header {
  text-align: center;
  margin-bottom: 2rem;
  padding-bottom: 2rem;
  border-bottom: 1px solid var(--color-border);
}

.report-mbti h2 {
  font-size: 3rem;
  color: var(--primary-teal);
  margin: 0 0 1rem 0;
  font-weight: bold;
}

.dimension-scores {
  display: grid;
  gap: 1rem;
}

.dimension-item {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.dimension-label {
  width: 2rem;
  font-weight: bold;
  color: var(--color-text-primary);
}

.dimension-bar {
  flex: 1;
  height: 0.8rem;
  background-color: var(--color-background-soft);
  border-radius: 0.4rem;
  overflow: hidden;
}

.dimension-fill {
  height: 100%;
  background: linear-gradient(90deg, var(--primary-teal-light), var(--primary-teal));
  transition: width 0.3s ease;
}

.dimension-value {
  width: 2rem;
  text-align: right;
  font-weight: bold;
  color: var(--color-text-secondary);
}

.report-section {
  margin-bottom: 2rem;
}

.report-section h3 {
  font-size: 1.6rem;
  color: var(--color-text-primary);
  font-weight: 600;
  margin: 0 0 1rem 0;
}

.report-section p {
  font-size: 1.3rem;
  color: var(--color-text-secondary);
  line-height: 1.6;
  margin: 0;
}

.trait-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.8rem;
}

/* 空状态和加载状态 */
.empty-state,
.loading-state {
  text-align: center;
  padding: 4rem 2rem;
}

.empty-icon,
.loading-icon {
  font-size: 6rem;
  color: var(--color-text-soft);
  margin-bottom: 2rem;
}

.loading-icon {
  color: var(--primary-teal);
  animation: spin 1s linear infinite;
}

.empty-state h3 {
  font-size: 2rem;
  color: var(--color-text-secondary);
  margin: 0 0 1rem 0;
}

.empty-state p {
  font-size: 1.4rem;
  color: var(--color-text-soft);
  margin: 0 0 2rem 0;
}

/* 分页 */
.pagination-section {
  display: flex;
  justify-content: center;
  margin-top: 3rem;
  padding: 2rem;
  background-color: var(--color-background-soft);
  border-radius: 1.2rem;
  border: 1px solid var(--color-border);
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
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .results-container {
    padding: 1rem;
  }
  
  .results-card {
    padding: 2rem;
  }
  
  .results-grid {
    grid-template-columns: 1fr;
  }
  
  .stats-section {
    grid-template-columns: 1fr;
  }
}
</style>
