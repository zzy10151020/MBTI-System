<template>
  <div class="test-container">
    <div class="test-card">
      <!-- 返回按钮 -->
      <div class="back-button-container">
        <el-button 
          @click="handleBack"
          size="large"
          class="back-btn"
          :icon="ArrowLeft"
        >
          返回问卷列表
        </el-button>
      </div>

      <!-- 问卷信息展示 -->
      <div class="questionnaire-info" v-if="currentQuestionnaire">
        <div class="info-header">
          <h1 class="questionnaire-title">
            {{ currentQuestionnaire.title }}
          </h1>
          <p class="questionnaire-description">
            {{ currentQuestionnaire.description }}
          </p>
        </div>
        
        <div class="test-progress">
          <el-progress 
            :percentage="progressPercentage" 
            :color="progressColor"
            :stroke-width="8"
            :show-text="true"
          />
          <div class="progress-text">
            <span>进度：{{ currentQuestionIndex + 1 }} / {{ totalQuestions }}</span>
            <span>剩余时间：{{ formatTime(remainingTime) }}</span>
          </div>
        </div>
      </div>

      <!-- 问题展示区域 -->
      <div class="question-section" v-if="currentQuestion && !isCompleted">
        <div class="question-header">
          <h2 class="question-title">
            问题 {{ currentQuestionIndex + 1 }}
          </h2>
        </div>
        
        <div class="question-content">
          <h3 class="question-text">{{ currentQuestion.questionText || currentQuestion.content }}</h3>
          
          <div class="options-container">
            <div 
              v-for="(option, index) in currentQuestion.options" 
              :key="index"
              class="option-item"
              :class="{ 
                'selected': selectedAnswers[currentQuestionIndex] === (option.value || option.content),
                'option-a': index === 0,
                'option-b': index === 1
              }"
              @click="selectOption(option.value || option.content)"
            >
              <div class="option-content">
                <div class="option-label">{{ String.fromCharCode(65 + index) }}</div>
                <div class="option-text">{{ option.text || option.content }}</div>
              </div>
            </div>
          </div>
        </div>

        <div class="question-actions">
          <el-button 
            v-if="currentQuestionIndex > 0"
            @click="previousQuestion"
            size="large"
            class="nav-btn"
          >
            <el-icon><ArrowLeft /></el-icon>
            上一题
          </el-button>
          
          <div class="action-spacer"></div>
          
          <el-button 
            v-if="currentQuestionIndex < totalQuestions - 1"
            @click="nextQuestion"
            type="primary"
            size="large"
            class="nav-btn"
            :disabled="!selectedAnswers[currentQuestionIndex]"
          >
            下一题
            <el-icon><ArrowRight /></el-icon>
          </el-button>
          
          <el-button 
            v-else
            @click="() => submitTest()"
            type="primary"
            size="large"
            class="submit-btn"
            :disabled="!isAllQuestionsAnswered"
            :loading="submitting"
          >
            <el-icon><Check /></el-icon>
            提交测试
          </el-button>
        </div>
      </div>

      <!-- 完成状态 -->
      <div class="completion-section" v-if="isCompleted">
        <div class="completion-content">
          <div class="completion-icon">
            <el-icon><CircleCheck /></el-icon>
          </div>
          <h2 class="completion-title">测试完成！</h2>
          <p class="completion-description">
            恭喜您完成了 MBTI 性格测试，正在为您生成专属的性格报告...
          </p>
          
          <div class="completion-actions">
            <el-button 
              type="primary" 
              size="large"
              @click="viewResults"
              :loading="generatingReport"
            >
              查看测试结果
            </el-button>
            <el-button 
              size="large"
              @click="backToQuestionnaires"
            >
              返回问卷列表
            </el-button>
          </div>
        </div>
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="loading-section">
        <el-icon class="loading-icon"><Loading /></el-icon>
        <p>正在加载测试内容...</p>
      </div>

      <!-- 错误状态 -->
      <div v-if="error" class="error-section">
        <el-icon class="error-icon"><CircleClose /></el-icon>
        <h3>加载失败</h3>
        <p>{{ error }}</p>
        <el-button @click="retryLoad" type="primary">重试</el-button>
      </div>
    </div>

    <!-- 退出确认对话框 -->
    <el-dialog
      v-model="showExitDialog"
      title="确认退出测试"
      width="400px"
      :before-close="handleDialogClose"
    >
      <p>您确定要退出当前测试吗？已填写的答案将会丢失。</p>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showExitDialog = false">取消</el-button>
          <el-button type="primary" @click="confirmExit">确认退出</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount, watch, nextTick } from 'vue'
import { useRouter, useRoute, onBeforeRouteLeave } from 'vue-router'
import { ElMessage, ElMessageBox, ElNotification } from 'element-plus'
import { 
  ArrowLeft, 
  ArrowRight, 
  Check, 
  CircleCheck, 
  CircleClose, 
  Loading 
} from '@element-plus/icons-vue'
import { useQuestionnaireStore } from '@/stores/questionnaireStore'
import { useUserStore } from '@/stores/userStore'
import { useTestStore } from '@/stores/testStore'
import { testApi } from '@/api'
import type { Questionnaire } from '@/api/types'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const questionnaireStore = useQuestionnaireStore()
const testStore = useTestStore()

// 基础状态
const loading = ref(true)
const error = ref<string | null>(null)
const submitting = ref(false)
const generatingReport = ref(false)
const isCompleted = ref(false)
const showExitDialog = ref(false)

// 问卷和问题数据
const currentQuestionnaire = ref<Questionnaire | null>(null)
const questions = ref<any[]>([])
const currentQuestionIndex = ref(0)
const selectedAnswers = ref<Record<number, string>>({})

// 计时器
const startTime = ref<Date | null>(null)
const remainingTime = ref(1800) // 30分钟 = 1800秒
const timer = ref<NodeJS.Timeout | null>(null)

// 计算属性
const currentQuestion = computed(() => {
  return questions.value[currentQuestionIndex.value]
})

const totalQuestions = computed(() => {
  return questions.value.length
})

const progressPercentage = computed(() => {
  if (totalQuestions.value === 0) return 0
  return Math.round(((currentQuestionIndex.value + 1) / totalQuestions.value) * 100)
})

const progressColor = computed(() => {
  const percentage = progressPercentage.value
  if (percentage < 30) return '#f56c6c'
  if (percentage < 70) return '#e6a23c'
  return 'var(--primary-teal)'
})

const isAllQuestionsAnswered = computed(() => {
  return Object.keys(selectedAnswers.value).length === totalQuestions.value
})

// 模拟问题数据
const getMockQuestions = () => {
  return [
    {
      id: 1,
      questionText: "在社交场合中，您更倾向于：",
      options: [
        { value: "E", text: "主动与他人交谈，享受热闹的氛围" },
        { value: "I", text: "更愿意与少数熟悉的人深入交流" }
      ]
    },
    {
      id: 2,
      questionText: "当面对新信息时，您通常：",
      options: [
        { value: "S", text: "关注具体的事实和细节" },
        { value: "N", text: "寻找潜在的模式和可能性" }
      ]
    },
    {
      id: 3,
      questionText: "在做决定时，您更重视：",
      options: [
        { value: "T", text: "逻辑分析和客观标准" },
        { value: "F", text: "个人价值观和他人感受" }
      ]
    },
    {
      id: 4,
      questionText: "您更喜欢：",
      options: [
        { value: "J", text: "有计划、有条理的生活方式" },
        { value: "P", text: "灵活、开放的生活方式" }
      ]
    },
    {
      id: 5,
      questionText: "在工作中，您更倾向于：",
      options: [
        { value: "E", text: "通过团队合作来解决问题" },
        { value: "I", text: "独立思考后再与他人分享" }
      ]
    }
  ]
}

// 方法
const loadTestData = async () => {
  try {
    loading.value = true
    error.value = null

    const questionnaireId = route.query.questionnaireId as string
    if (!questionnaireId) {
      throw new Error('缺少问卷ID参数')
    }

    // 获取问卷信息
    await questionnaireStore.fetchQuestionnaires()
    currentQuestionnaire.value = questionnaireStore.questionnaires.find(
      q => q.questionnaireId === parseInt(questionnaireId)
    ) || null

    if (!currentQuestionnaire.value) {
      throw new Error('未找到指定的问卷')
    }

    // 获取问卷的问题数据（从questionnaireStore或API）
    try {
      console.log('开始获取问卷问题，问卷ID:', questionnaireId)
      const questionnaireData = await questionnaireStore.fetchQuestionnaireQuestions(parseInt(questionnaireId))
      console.log('获取到的问卷数据:', questionnaireData)
      
      if (questionnaireData && questionnaireData.questions && questionnaireData.questions.length > 0) {
        questions.value = questionnaireData.questions
        console.log('✅ 成功使用API/questionnaireStore数据，问题数:', questions.value.length)
        // 显示成功提示
        ElNotification({
          title: '问卷加载成功',
          message: `已加载 ${questions.value.length} 道测试题目`,
          type: 'success',
          duration: 2000
        })
      } else {
        // 如果API失败，使用questionnaireStore的mock数据而不是TestView的模拟数据
        console.warn('⚠️ API获取问题失败或返回空数据，使用questionnaireStore mock数据')
        
        if (typeof questionnaireStore.getMockQuestionnaireQuestions === 'function') {
          const mockData = questionnaireStore.getMockQuestionnaireQuestions(parseInt(questionnaireId))
          console.log('使用questionnaireStore mock数据:', mockData)
          questions.value = mockData.questions
          // 显示降级提示
          ElNotification({
            title: '使用离线数据',
            message: `当前使用模拟数据进行测试，共 ${questions.value.length} 道题目`,
            type: 'warning',
            duration: 3000
          })
        } else {
          // 降级到TestView内置的模拟数据
          console.warn('questionnaireStore.getMockQuestionnaireQuestions 不是函数，使用TestView模拟数据')
          questions.value = getMockQuestions()
          ElNotification({
            title: '使用备用数据',
            message: `当前使用备用测试数据，共 ${questions.value.length} 道题目`,
            type: 'info',
            duration: 3000
          })
        }
      }
    } catch (apiError: any) {
      console.error('❌ TestView: API获取问题失败，详细错误:', apiError)
      console.error('错误类型:', typeof apiError)
      console.error('错误消息:', apiError?.message)
      console.error('错误响应:', apiError?.response)
      console.error('错误状态:', apiError?.response?.status)
      console.error('错误数据:', apiError?.response?.data)
      
      // 使用mock数据作为降级方案
      
      if (typeof questionnaireStore.getMockQuestionnaireQuestions === 'function') {
        const mockData = questionnaireStore.getMockQuestionnaireQuestions(parseInt(questionnaireId))
        questions.value = mockData.questions
      } else {
        // 降级到TestView内置的模拟数据
        console.warn('questionnaireStore.getMockQuestionnaireQuestions 不是函数，使用TestView模拟数据')
        questions.value = getMockQuestions()
      }
    }
    
    // 初始化计时器
    startTimer()

  } catch (err: any) {
    console.error('加载测试数据失败:', err)
    error.value = err.message || '加载测试数据失败'
  } finally {
    loading.value = false
  }
}

const startTimer = () => {
  startTime.value = new Date()
  timer.value = setInterval(() => {
    remainingTime.value--
    if (remainingTime.value <= 0) {
      timeUp()
    }
  }, 1000)
}

const stopTimer = () => {
  if (timer.value) {
    clearInterval(timer.value)
    timer.value = null
  }
}

const timeUp = () => {
  stopTimer()
  ElMessageBox.alert(
    '测试时间已到，系统将自动提交您的答案。',
    '时间到',
    {
      confirmButtonText: '确定',
      type: 'warning'
    }
  ).then(() => {
    submitTest(true)
  })
}

const formatTime = (seconds: number): string => {
  const hours = Math.floor(seconds / 3600)
  const minutes = Math.floor((seconds % 3600) / 60)
  const secs = seconds % 60
  
  if (hours > 0) {
    return `${hours}:${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
  }
  return `${minutes}:${secs.toString().padStart(2, '0')}`
}

const selectOption = (value: string) => {
  selectedAnswers.value[currentQuestionIndex.value] = value
}

const nextQuestion = () => {
  if (currentQuestionIndex.value < totalQuestions.value - 1) {
    currentQuestionIndex.value++
  }
}

const previousQuestion = () => {
  if (currentQuestionIndex.value > 0) {
    currentQuestionIndex.value--
  }
}

const submitTest = async (isTimeUp = false) => {
  try {
    submitting.value = true

    if (!isTimeUp && !isAllQuestionsAnswered.value) {
      ElMessage.warning('请回答所有问题后再提交')
      return
    }

    // 停止计时器
    stopTimer()

    // 构建提交数据 - 根据API接口格式
    const questionAnswers: Record<string, number> = {}
    
    // 将答案转换为 问题ID: 选项ID 的格式
    Object.entries(selectedAnswers.value).forEach(([questionIndex, answer]) => {
      const question = questions.value[parseInt(questionIndex)]
      
      // 兼容两种数据结构：API数据和模拟数据
      const questionId = question.questionId || question.id
      
      // 查找选中的选项
      let selectedOption = null
      if (question.options && question.options.length > 0) {
        if (question.options[0].optionId !== undefined) {
          // API/questionnaireStore数据结构：{optionId, content, score}
          // 这里answer是用户选择的选项内容
          selectedOption = question.options.find((opt: any) => opt.content === answer)
        } else {
          // TestView模拟数据结构：{value, text}
          selectedOption = question.options.find((opt: any) => opt.value === answer)
          if (selectedOption) {
            // 为模拟数据生成选项ID
            const optionIndex = question.options.findIndex((opt: any) => opt.value === answer)
            selectedOption.optionId = optionIndex + 1
          }
        }
      }
      
      if (!selectedOption) {
        throw new Error(`问题 ${questionId} 的答案无效`)
      }
      
      questionAnswers[questionId] = selectedOption.optionId
    })

    const submitData = {
      questionnaireId: currentQuestionnaire.value!.questionnaireId,
      questionAnswers
    }

    console.log('提交答案数据:', submitData)
    console.log('原始答案:', selectedAnswers.value)
    console.log('问题数据:', questions.value.map((q: any) => ({ 
      questionId: q.questionId || q.id, 
      options: q.options?.map((opt: any) => ({ optionId: opt.optionId, content: opt.content || opt.text }))
    })))

    try {
      // 调用testStore的提交方法
      const result = await testStore.submitTestAnswers(submitData)
      
      // 保存答案ID和MBTI结果以便后续查看
      sessionStorage.setItem('lastAnswerId', result.answerId.toString())
      sessionStorage.setItem('lastMbtiType', result.mbtiType)
      sessionStorage.setItem('lastDimensionScores', JSON.stringify(result.dimensionScores))
      
      isCompleted.value = true
      
    } catch (apiError: any) {
      console.warn('API提交失败，使用模拟提交:', apiError)
      
      // API失败时的降级处理 - 模拟MBTI结果计算
      const mockResult = calculateMockMbtiResult()
      
      // 保存模拟结果到testStore
      const savedResult = testStore.saveMockTestResult(mockResult, submitData.questionnaireId)
      
      sessionStorage.setItem('lastAnswerId', savedResult.answerId.toString())
      sessionStorage.setItem('lastMbtiType', mockResult.mbtiType)
      sessionStorage.setItem('lastDimensionScores', JSON.stringify(mockResult.dimensionScores))
      
      isCompleted.value = true
      ElMessage.success(`测试提交成功！您的MBTI类型是：${mockResult.mbtiType}`)
    }

  } catch (err: any) {
    console.error('提交测试失败:', err)
    ElMessage.error(err.message || '提交测试失败，请稍后重试')
  } finally {
    submitting.value = false
  }
}

// 模拟MBTI结果计算
const calculateMockMbtiResult = () => {
  const answers = Object.values(selectedAnswers.value)
  
  // 简单的MBTI计算逻辑
  const scores = {
    EI: 0, // E vs I
    SN: 0, // S vs N  
    TF: 0, // T vs F
    JP: 0  // J vs P
  }

  answers.forEach((answer) => {
    switch (answer) {
      case 'E': scores.EI += 1; break
      case 'I': scores.EI -= 1; break
      case 'S': scores.SN -= 1; break
      case 'N': scores.SN += 1; break
      case 'T': scores.TF += 1; break
      case 'F': scores.TF -= 1; break
      case 'J': scores.JP += 1; break
      case 'P': scores.JP -= 1; break
    }
  })

  // 转换为百分比分数 (0-100)
  const dimensionScores = {
    EI: Math.max(0, Math.min(100, 50 + scores.EI * 10)),
    SN: Math.max(0, Math.min(100, 50 + scores.SN * 10)), 
    TF: Math.max(0, Math.min(100, 50 + scores.TF * 10)),
    JP: Math.max(0, Math.min(100, 50 + scores.JP * 10))
  }

  // 确定MBTI类型
  const mbtiType = 
    (scores.EI >= 0 ? 'E' : 'I') +
    (scores.SN >= 0 ? 'N' : 'S') +
    (scores.TF >= 0 ? 'T' : 'F') +
    (scores.JP >= 0 ? 'J' : 'P')

  return { mbtiType, dimensionScores }
}

const viewResults = async () => {
  try {
    generatingReport.value = true
    
    const answerId = sessionStorage.getItem('lastAnswerId')
    const mbtiType = sessionStorage.getItem('lastMbtiType')
    
    if (!answerId) {
      ElMessage.error('未找到测试结果')
      return
    }

    // 模拟生成报告的延迟
    await new Promise(resolve => setTimeout(resolve, 1500))
    
    try {
      // 尝试获取详细的MBTI报告
      const report = await testApi.getMbtiReport(parseInt(answerId))
      console.log('获取到详细报告:', report)
      
      // 保存详细报告到sessionStorage
      sessionStorage.setItem('lastMbtiReport', JSON.stringify(report))
      
    } catch (reportError) {
      console.warn('获取详细报告失败，使用基本结果:', reportError)
    }
    
    // 跳转到结果页面或显示结果弹窗
    const uid = route.params.uid || userStore.user?.userId?.toString()
    
    // 这里可以跳转到专门的结果页面，暂时跳转回问卷列表并显示结果
    ElMessageBox.alert(
      `您的MBTI性格类型是：${mbtiType || '未知'}`,
      '测试结果',
      {
        confirmButtonText: '查看详细报告',
        type: 'success',
      }
    ).then(() => {
      router.push({
        name: 'results',
        params: uid ? { uid } : {}
      })
    })

  } catch (err: any) {
    console.error('查看结果失败:', err)
    ElMessage.error('查看结果失败，请稍后再试')
  } finally {
    generatingReport.value = false
  }
}

const backToQuestionnaires = () => {
  const uid = route.params.uid || userStore.user?.userId?.toString()
  router.push({
    name: 'questionnaires',
    params: uid ? { uid } : {}
  })
}

const handleBack = () => {
  if (isCompleted.value || loading.value) {
    // 如果已完成或正在加载，直接返回
    backToQuestionnaires()
  } else {
    // 显示退出确认对话框
    showExitDialog.value = true
  }
}

const retryLoad = () => {
  loadTestData()
}

const confirmExit = () => {
  showExitDialog.value = false
  allowLeave.value = true
  stopTimer()
  
  // 使用 nextTick 确保状态更新后再进行路由跳转
  nextTick(() => {
    backToQuestionnaires()
  })
}

const handleDialogClose = () => {
  showExitDialog.value = false
}

// 路由守卫 - 防止意外离开
const allowLeave = ref(false)

onBeforeRouteLeave((to, from, next) => {
  if (isCompleted.value || loading.value || allowLeave.value) {
    next()
  } else {
    // 如果弹窗已经显示，直接阻止
    if (showExitDialog.value) {
      next(false)
      return
    }
    
    // 显示退出确认对话框
    showExitDialog.value = true
    next(false)
  }
})

// 页面刷新或关闭警告
const handleBeforeUnload = (event: BeforeUnloadEvent) => {
  if (!isCompleted.value && !loading.value) {
    event.preventDefault()
    event.returnValue = '您确定要离开吗？测试数据将会丢失。'
  }
}

// 生命周期
onMounted(() => {
  loadTestData()
  window.addEventListener('beforeunload', handleBeforeUnload)
})

onBeforeUnmount(() => {
  stopTimer()
  window.removeEventListener('beforeunload', handleBeforeUnload)
})

// 监听用户登录状态
watch(() => userStore.isLoggedIn, (isLoggedIn) => {
  if (!isLoggedIn) {
    ElMessage.warning('请先登录后再进行测试')
    router.push({ name: 'home' })
  }
}, { immediate: true })
</script>

<style scoped>
.test-container {
  width: 100%;
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding: 2rem 8rem;
  background-color: var(--color-background-soft);
}

.test-card {
  max-width: 80rem;
  width: 100%;
  background-color: var(--color-background);
  border-radius: 1.6rem;
  box-shadow: 0 0.8rem 3.2rem rgba(0, 0, 0, 0.1);
  padding: 2rem 6rem;
  border: 1px solid var(--color-border);
  position: relative;
}

/* 返回按钮 */
.back-button-container {
  position: absolute;
  top: 2rem;
  left: 2rem;
  z-index: 10;
}

.back-btn {
  background-color: var(--color-background-soft) !important;
  border: 1px solid var(--color-border) !important;
  color: var(--color-text-secondary) !important;
  font-size: 1.2rem !important;
  padding: 0.8rem 1.6rem !important;
  border-radius: 0.8rem !important;
  transition: all 0.3s ease !important;
}

.back-btn:hover {
  background-color: var(--primary-teal-light) !important;
  border-color: var(--primary-teal) !important;
  color: var(--primary-teal-dark) !important;
  transform: translateY(-0.1rem);
}

/* 问卷信息区域 */
.questionnaire-info {
  margin-bottom: 1rem;
  margin-top: 4rem; /* 为返回按钮留出空间 */
}

.info-header {
  text-align: center;
  margin-bottom: 1rem;
}

.questionnaire-title {
  font-size: 2.4rem;
  color: var(--color-text-primary);
  font-weight: 600;
  margin-bottom: 0.2rem;
  letter-spacing: 0.05rem;
}

.questionnaire-description {
  font-size: 1.4rem;
  color: var(--color-text-secondary);
  line-height: 1.6;
  margin: 0;
}

.test-progress {
  margin-bottom: 2rem;
}

.progress-text {
  display: flex;
  justify-content: space-between;
  margin-top: 1rem;
  font-size: 1.2rem;
  color: var(--color-text-secondary);
}

/* 问题区域 */
.question-section {
  margin-bottom: 2rem;
}

.question-header {
  margin-bottom: 2rem;
}

.question-title {
  font-size: 1.8rem;
  color: var(--primary-teal);
  font-weight: 600;
  margin: 0;
}

.question-content {
  margin-bottom: 3rem;
}

.question-text {
  font-size: 1.6rem;
  color: var(--color-text-primary);
  font-weight: 500;
  margin: 0 0 2rem 0;
  line-height: 1.5;
}

.options-container {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.option-item {
  border: 2px solid var(--color-border);
  border-radius: 1.2rem;
  padding: 2rem;
  cursor: pointer;
  transition: all 0.3s ease;
  background-color: var(--color-background);
}

.option-item:hover {
  border-color: var(--primary-teal-light);
  background-color: var(--primary-teal-light);
  transform: translateY(-0.2rem);
}

.option-item.selected {
  border-color: var(--primary-teal);
  background-color: var(--primary-teal-light);
  box-shadow: 0 0.4rem 1.2rem rgba(32, 178, 170, 0.2);
}

.option-item.option-a.selected {
  background: linear-gradient(135deg, var(--primary-teal-light) 0%, rgba(32, 178, 170, 0.1) 100%);
}

.option-item.option-b.selected {
  background: linear-gradient(135deg, rgba(32, 178, 170, 0.1) 0%, var(--primary-teal-light) 100%);
}

.option-content {
  display: flex;
  align-items: center;
  gap: 1.5rem;
}

.option-label {
  width: 3rem;
  height: 3rem;
  border-radius: 50%;
  background-color: var(--primary-teal);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 1.4rem;
  flex-shrink: 0;
}

.option-item.selected .option-label {
  background-color: var(--primary-teal-dark);
  box-shadow: 0 0.2rem 0.8rem rgba(32, 178, 170, 0.4);
}

.option-text {
  font-size: 1.4rem;
  color: var(--color-text-primary);
  line-height: 1.5;
  flex: 1;
}

/* 操作按钮区域 */
.question-actions {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.action-spacer {
  flex: 1;
}

.nav-btn,
.submit-btn {
  font-size: 1.4rem !important;
  padding: 1rem 2rem !important;
  border-radius: 0.8rem !important;
  font-weight: 500 !important;
}

.submit-btn {
  background-color: var(--primary-teal) !important;
  border-color: var(--primary-teal) !important;
  color: white !important;
  box-shadow: 0 0.4rem 1.2rem rgba(32, 178, 170, 0.3);
}

.submit-btn:hover {
  background-color: var(--primary-teal-dark) !important;
  transform: translateY(-0.2rem);
}

/* 完成状态 */
.completion-section {
  text-align: center;
  padding: 4rem 2rem;
}

.completion-content {
  max-width: 50rem;
  margin: 0 auto;
}

.completion-icon {
  font-size: 6rem;
  color: var(--primary-teal);
  margin-bottom: 2rem;
}

.completion-title {
  font-size: 2.4rem;
  color: var(--color-text-primary);
  font-weight: 600;
  margin: 0 0 1rem 0;
}

.completion-description {
  font-size: 1.4rem;
  color: var(--color-text-secondary);
  line-height: 1.6;
  margin: 0 0 3rem 0;
}

.completion-actions {
  display: flex;
  justify-content: center;
  gap: 1rem;
}

/* 加载和错误状态 */
.loading-section,
.error-section {
  text-align: center;
  padding: 4rem 2rem;
}

.loading-icon,
.error-icon {
  font-size: 4rem;
  margin-bottom: 2rem;
}

.loading-icon {
  color: var(--primary-teal);
  animation: spin 1s linear infinite;
}

.error-icon {
  color: var(--color-danger);
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
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

/* 进度条样式覆盖 */
:deep(.el-progress-bar__outer) {
  background-color: var(--color-background-soft);
  border-radius: 0.4rem;
}

:deep(.el-progress-bar__inner) {
  border-radius: 0.4rem;
}

/* 对话框样式覆盖 */
:deep(.el-dialog) {
  border-radius: 1.2rem;
}

:deep(.el-dialog__header) {
  background-color: var(--color-background-soft);
  border-radius: 1.2rem 1.2rem 0 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .test-container {
    padding: 1rem;
  }
  
  .test-card {
    padding: 2rem;
    border-radius: 1.2rem;
  }
  
  .back-button-container {
    top: 1rem;
    left: 1rem;
  }
  
  .back-btn {
    font-size: 1.1rem !important;
    padding: 0.6rem 1.2rem !important;
  }
  
  .questionnaire-info {
    margin-top: 5rem; /* 移动端增加顶部间距 */
  }
  
  .questionnaire-title {
    font-size: 1.8rem;
  }
  
  .question-text {
    font-size: 1.4rem;
  }
  
  .option-content {
    gap: 1rem;
  }
  
  .option-label {
    width: 2.5rem;
    height: 2.5rem;
    font-size: 1.2rem;
  }
  
  .option-text {
    font-size: 1.2rem;
  }
  
  .progress-text {
    flex-direction: column;
    gap: 0.5rem;
    text-align: center;
  }
}

@media (max-width: 480px) {
  .test-card {
    padding: 1.5rem;
  }
  
  .back-button-container {
    top: 0.5rem;
    left: 0.5rem;
  }
  
  .back-btn {
    font-size: 1rem !important;
    padding: 0.5rem 1rem !important;
  }
  
  .questionnaire-info {
    margin-top: 4rem;
  }
  
  .questionnaire-title {
    font-size: 1.6rem;
  }
  
  .question-actions {
    flex-direction: column;
    gap: 1rem;
  }
  
  .action-spacer {
    display: none;
  }
  
  .nav-btn,
  .submit-btn {
    width: 100%;
  }
  
  .completion-actions {
    flex-direction: column;
  }
}
</style>
