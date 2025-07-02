import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { questionnaireApi, type Questionnaire } from '@/api'
import { questionApi } from '@/api/question'

export const useQuestionnaireStore = defineStore('questionnaire', () => {
  // 状态
  const questionnaires = ref<Questionnaire[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)

  // 计算属性
  const activeQuestionnaires = computed(() => {
    return questionnaires.value.filter(q => q.isPublished)
  })

  const getQuestionnaireById = computed(() => {
    return (id: number) => questionnaires.value.find(q => q.questionnaireId === id)
  })

  // 操作方法
  const fetchQuestionnaires = async (uid?: number) => {
    try {
      loading.value = true
      error.value = null
      questionnaires.value = await questionnaireApi.getActiveQuestionnaires()
    } catch (err: any) {
      console.warn('API获取问卷失败，使用模拟数据:', err)
      
      // 如果API失败，使用模拟数据进行展示
      questionnaires.value = getMockQuestionnaires()
      error.value = null // 不显示错误，因为有降级方案
    } finally {
      loading.value = false
    }
  }

  // 获取问卷详情
  const fetchQuestionnaireDetail = async (id: number) => {
    try {
      loading.value = true
      error.value = null
      return await questionnaireApi.getQuestionnaireDetail(id)
    } catch (err: any) {
      console.warn('API获取问卷详情失败:', err)
      error.value = err.message || '获取问卷详情失败'
      
      // 返回mock数据
      const mockDetail = questionnaires.value.find(q => q.questionnaireId === id)
      if (mockDetail) {
        return mockDetail
      }
      throw err
    } finally {
      loading.value = false
    }
  }

  // 获取问卷问题
  const fetchQuestionnaireQuestions = async (id: number) => {
    try {
      loading.value = true
      error.value = null
      console.log('questionnaireStore: 使用questionApi获取问卷问题，ID:', id)
      
      // 使用questionApi获取问题列表
      const questions = await questionApi.getQuestionsByQuestionnaireId(id)
      console.log('questionnaireStore: questionApi请求成功，返回问题数据:', questions)
      
      // 构造符合前端期望的数据结构
      const result = {
        questionnaireId: id,
        title: '问卷测试', // 这里可以通过其他API获取问卷基本信息，或从mock数据获取
        description: '问卷描述',
        questions: questions
      }
      
      console.log('questionnaireStore: 构造的最终数据:', result)
      return result
    } catch (err: any) {
      console.error('questionnaireStore: questionApi获取问题失败:', err)
      error.value = null
      
      // 返回mock问题数据
      const mockData = getMockQuestionnaireQuestions(id)
      console.log('questionnaireStore: 使用mock数据:', mockData)
      return mockData
    } finally {
      loading.value = false
    }
  }

  // 模拟数据 - 用于开发测试
  const getMockQuestionnaires = (): Questionnaire[] => {
    return [
      {
        questionnaireId: 1,
        title: '经典MBTI性格测试',
        description: '最权威的MBTI性格类型测试，帮你深入了解自己的性格特征和行为模式',
        creatorId: 1,
        creatorUsername: 'admin',
        createdAt: '2025-07-01T08:00:00',
        isPublished: true,
        answerCount: 1520,
        hasAnswered: false
      },
      {
        questionnaireId: 2,
        title: '职场性格分析',
        description: '专为职场人士设计的性格测试，了解你在工作中的优势和发展方向',
        creatorId: 1,
        creatorUsername: 'admin',
        createdAt: '2025-07-01T09:00:00',
        isPublished: true,
        answerCount: 890,
        hasAnswered: true
      },
      {
        questionnaireId: 3,
        title: '情感倾向测试',
        description: '探索你的情感处理方式和人际交往偏好，提升人际关系质量',
        creatorId: 1,
        creatorUsername: 'admin',
        createdAt: '2025-07-01T10:00:00',
        isPublished: true,
        answerCount: 654,
        hasAnswered: false
      },
      {
        questionnaireId: 4,
        title: '学习风格评估',
        description: '了解你的学习偏好和认知方式，优化学习效率和方法',
        creatorId: 1,
        creatorUsername: 'admin',
        createdAt: '2025-07-01T11:00:00',
        isPublished: true,
        answerCount: 423,
        hasAnswered: false
      },
      {
        questionnaireId: 5,
        title: '领导力风格测试',
        description: '评估你的领导潜力和管理风格，助力职业发展',
        creatorId: 1,
        creatorUsername: 'admin',
        createdAt: '2025-07-01T12:00:00',
        isPublished: true,
        answerCount: 312,
        hasAnswered: false
      },
      {
        questionnaireId: 6,
        title: '创造力评估',
        description: '测试你的创新思维和创造力水平，发现你的创意潜能',
        creatorId: 1,
        creatorUsername: 'admin',
        createdAt: '2025-07-01T13:00:00',
        isPublished: true,
        answerCount: 256,
        hasAnswered: false
      },
      {
        questionnaireId: 7,
        title: '沟通风格评估',
        description: '了解你的沟通偏好和风格，提升人际交往能力',
        creatorId: 1,
        creatorUsername: 'admin',
        createdAt: '2025-07-01T14:00:00',
        isPublished: true,
        answerCount: 189,
        hasAnswered: false
      },
      {
        questionnaireId: 8,
        title: '压力应对测试',
        description: '评估你在压力下的反应模式和应对策略',
        creatorId: 1,
        creatorUsername: 'admin',
        createdAt: '2025-07-01T15:00:00',
        isPublished: true,
        answerCount: 145,
        hasAnswered: false
      },
      {
        questionnaireId: 9,
        title: '团队协作能力',
        description: '测试你在团队中的角色定位和协作风格',
        creatorId: 1,
        creatorUsername: 'admin',
        createdAt: '2025-07-01T16:00:00',
        isPublished: true,
        answerCount: 97,
        hasAnswered: false
      },
      {
        questionnaireId: 10,
        title: '决策风格分析',
        description: '了解你的决策过程和思维模式',
        creatorId: 1,
        creatorUsername: 'admin',
        createdAt: '2025-07-01T17:00:00',
        isPublished: true,
        answerCount: 78,
        hasAnswered: false
      },
      {
        questionnaireId: 11,
        title: '时间管理能力',
        description: '评估你的时间管理风格和效率',
        creatorId: 1,
        creatorUsername: 'admin',
        createdAt: '2025-07-01T18:00:00',
        isPublished: true,
        answerCount: 62,
        hasAnswered: false
      },
      {
        questionnaireId: 12,
        title: '情绪智商测试',
        description: '测试你的情绪感知和管理能力',
        creatorId: 1,
        creatorUsername: 'admin',
        createdAt: '2025-07-01T19:00:00',
        isPublished: true,
        answerCount: 134,
        hasAnswered: true
      }
    ]
  }

  // Mock问卷问题数据
  const getMockQuestionnaireQuestions = (questionnaireId: number) => {
    return {
      questionnaireId,
      title: '经典MBTI性格测试',
      description: '最权威的MBTI性格类型测试',
      questions: [
        {
          questionId: 1,
          content: '在社交场合中，你更倾向于：',
          dimension: 'E/I',
          questionOrder: 1,
          options: [
            { optionId: 1, content: '主动与他人交谈', score: 1 },
            { optionId: 2, content: '等待他人主动接近', score: -1 }
          ]
        },
        {
          questionId: 2,
          content: '当面对新信息时，你更关注：',
          dimension: 'S/N',
          questionOrder: 2,
          options: [
            { optionId: 3, content: '具体的事实和细节', score: -1 },
            { optionId: 4, content: '整体的概念和可能性', score: 1 }
          ]
        },
        {
          questionId: 3,
          content: '在做决定时，你更依赖：',
          dimension: 'T/F',
          questionOrder: 3,
          options: [
            { optionId: 5, content: '逻辑分析和客观标准', score: 1 },
            { optionId: 6, content: '个人价值和他人感受', score: -1 }
          ]
        },
        {
          questionId: 4,
          content: '对于计划安排，你更喜欢：',
          dimension: 'J/P',
          questionOrder: 4,
          options: [
            { optionId: 7, content: '制定详细计划并严格执行', score: 1 },
            { optionId: 8, content: '保持灵活性，随时调整', score: -1 }
          ]
        }
      ]
    }
  }

  // 更新问卷回答状态
  const updateAnswerStatus = (questionnaireId: number, hasAnswered: boolean) => {
    const questionnaire = questionnaires.value.find(q => q.questionnaireId === questionnaireId)
    if (questionnaire) {
      questionnaire.hasAnswered = hasAnswered
      if (hasAnswered) {
        questionnaire.answerCount += 1
      }
    }
  }

  // 清空状态
  const reset = () => {
    questionnaires.value = []
    loading.value = false
    error.value = null
  }

  return {
    // 状态
    questionnaires,
    loading,
    error,
    
    // 计算属性
    activeQuestionnaires,
    getQuestionnaireById,
    
    // 方法
    fetchQuestionnaires,
    fetchQuestionnaireDetail,
    fetchQuestionnaireQuestions,
    getMockQuestionnaireQuestions,
    updateAnswerStatus,
    reset
  }
})
