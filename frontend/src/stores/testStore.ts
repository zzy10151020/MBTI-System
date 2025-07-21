import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { testApi } from '@/api'
import type { 
  Test,
  TestStatistics,
  TestStatisticses,
  SubmitTestRequest,
} from '@/api/types'

export const useTestStore = defineStore('test', () => {
  // 状态
  const testResults = ref<Test[]>([])
  const currentTestDetail = ref<Test | null>(null)
  const testStatisticses = ref<TestStatisticses | null>(null)
  const currentTestStatistics = ref<TestStatistics | null>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)

  // 计算属性
  const hasTestResults = computed(() => testResults.value.length > 0)
  const latestTestResult = computed(() => {
    return testResults.value.length > 0 ? testResults.value[0] : null
  })

  // 获取测试结果列表
  const fetchTestResults = async () => {
    try {
      loading.value = true
      error.value = null
      
      const results = await testApi.getAllTests()
      testResults.value = results || []
      
    } catch (err: any) {
      console.error('获取测试结果失败:', err)
      error.value = err.message || '获取测试结果失败'
      ElMessage.error(error.value || '获取测试结果失败')
      
      testResults.value = []
    } finally {
      loading.value = false
    }
  }

  // 获取测试详情
  const fetchTestDetail = async (answerId: number) => {
    try {
      loading.value = true
      error.value = null
      
      const result = await testApi.getTestById(answerId)
      currentTestDetail.value = result

      return currentTestDetail.value
    } catch (err: any) {
      console.error('获取测试详情失败:', err)
      error.value = err.message || '获取测试详情失败'
      ElMessage.error(error.value || '获取测试详情失败')
      
      return null
    } finally {
      loading.value = false
    }
  }

  // 提交答案并保存结果
  const submitTestAnswers = async (data: SubmitTestRequest) => {
    try {
      loading.value = true
      error.value = null
      
      const submitData: SubmitTestRequest = {
        questionnaireId: data.questionnaireId,
        answerDetails: data.answerDetails.map(answer => ({
          questionId: answer.questionId,
          optionId: answer.optionId
        }))
      }
      
      // 调用API提交答案
      const result = await testApi.submitTest(submitData)
      
      // 刷新测试结果列表
      await fetchTestResults()

      ElMessage.success('答案提交成功')
      
      return result
    } catch (error: any) {
      console.error('提交答案失败:', error)
      ElMessage.error(error.message || '提交答案失败，请重试')
      throw error
    } finally {
      loading.value = false
    }
  }

  // 根据MBTI类型获取详细描述
  const getMbtiDescription = (mbtiType: string) => {
    const descriptions: Record<string, any> = {
      'ENFP': {
        name: '活跃的鼓舞者',
        description: '热情、富有想象力和创造力的人，认为生活充满可能性。',
        strengths: ['创造力', '同理心', '适应性', '热情'],
        weaknesses: ['过度理想化', '压力管理', '决策困难'],
        careers: ['心理咨询师', '教师', '作家', '艺术家']
      },
      'INFP': {
        name: '调停者',
        description: '诗意的、善良的利他主义者，总是热心帮助正义的事业。',
        strengths: ['理想主义', '创造力', '同理心', '适应性'],
        weaknesses: ['过于理想化', '容易受伤', '难以做决定'],
        careers: ['写作', '心理治疗', '艺术创作', '社会工作']
      },
      'ENFJ': {
        name: '主角',
        description: '有魅力的、鼓舞人心的领导者，能够使听众着迷。',
        strengths: ['领导力', '沟通能力', '同理心', '组织能力'],
        weaknesses: ['过度关注他人', '完美主义', '容易疲惫'],
        careers: ['教育', '咨询', '管理', '公关']
      },
      'INFJ': {
        name: '倡导者',
        description: '安静而神秘，同时鼓舞人心且不知疲倦的理想主义者。',
        strengths: ['洞察力', '理想主义', '决心', '创造力'],
        weaknesses: ['完美主义', '过度敏感', '倦怠'],
        careers: ['咨询', '写作', '研究', '非营利组织']
      },
      'ENTP': {
        name: '辩论家',
        description: '聪明好奇的思想家，无法抗拒智力上的挑战。',
        strengths: ['创新思维', '适应性', '热情', '多才多艺'],
        weaknesses: ['缺乏专注', '不喜欢常规', '争论倾向'],
        careers: ['企业家', '发明家', '营销', '咨询']
      },
      'INTP': {
        name: '逻辑学家',
        description: '创新的发明家，对知识有着止不住的渴望。',
        strengths: ['逻辑思维', '独立性', '创造力', '灵活性'],
        weaknesses: ['社交困难', '缺乏动力', '过度批判'],
        careers: ['研究', '工程', '计算机科学', '哲学']
      },
      'ENTJ': {
        name: '指挥官',
        description: '大胆、富有想象力、意志强烈的领导者。',
        strengths: ['领导力', '自信', '战略思维', '效率'],
        weaknesses: ['不耐烦', '傲慢', '缺乏同理心'],
        careers: ['管理', '企业家', '律师', '投资']
      },
      'INTJ': {
        name: '建筑师',
        description: '富有想象力和战略性的思想家，一切皆在计划之中。',
        strengths: ['独立性', '决心', '洞察力', '多才多艺'],
        weaknesses: ['过度自信', '缺乏耐心', '完美主义'],
        careers: ['科学研究', '工程', '法律', '管理']
      },
      'ESFP': {
        name: '娱乐家',
        description: '自发的、精力充沛的热情人士，生活在他们周围从不无聊。',
        strengths: ['热情', '友好', '适应性', '实用性'],
        weaknesses: ['缺乏专注', '冲动', '压力敏感'],
        careers: ['销售', '娱乐', '教育', '社会工作']
      },
      'ISFP': {
        name: '探险家',
        description: '灵活、迷人的艺术家，总是准备探索新的可能性。',
        strengths: ['艺术性', '灵活性', '热情', '实用性'],
        weaknesses: ['过度敏感', '缺乏长期规划', '竞争力弱'],
        careers: ['艺术', '设计', '医疗', '教育']
      },
      'ESFJ': {
        name: '执政官',
        description: '非常关心他人的人，总是乐于帮助。',
        strengths: ['合作性', '实用性', '支持性', '组织能力'],
        weaknesses: ['过度关注他人', '缺乏创新', '冲突回避'],
        careers: ['教育', '医疗', '社会工作', '管理']
      },
      'ISFJ': {
        name: '守护者',
        description: '非常专注、温暖的守护者，总是准备保护亲人。',
        strengths: ['支持性', '可靠性', '耐心', '实用性'],
        weaknesses: ['过度谦逊', '抗拒变化', '过度利他'],
        careers: ['医疗', '教育', '行政', '社会工作']
      },
      'ESTP': {
        name: '企业家',
        description: '聪明、精力充沛的感知者，真正享受生活在边缘。',
        strengths: ['适应性', '实用性', '感知力', '社交能力'],
        weaknesses: ['冲动', '缺乏专注', '风险偏好'],
        careers: ['销售', '市场营销', '娱乐', '体育']
      },
      'ISTP': {
        name: '鉴赏家',
        description: '大胆而实际的实验家，掌握各种工具。',
        strengths: ['实用性', '灵活性', '危机应对', '独立性'],
        weaknesses: ['固执', '缺乏耐心', '风险偏好'],
        careers: ['工程', '机械', '计算机', '执法']
      },
      'ESTJ': {
        name: '总经理',
        description: '出色的管理者，在管理事物或人员方面无与伦比。',
        strengths: ['组织能力', '实用性', '可靠性', '领导力'],
        weaknesses: ['不灵活', '过度专注工作', '缺乏耐心'],
        careers: ['管理', '行政', '法律', '军事']
      },
      'ISTJ': {
        name: '物流师',
        description: '实际和注重事实的可靠性，可靠性无可挑剔。',
        strengths: ['可靠性', '实用性', '负责任', '冷静'],
        weaknesses: ['抗拒变化', '过度批判', '缺乏灵活性'],
        careers: ['会计', '行政', '法律', '工程']
      }
    }
    
    return descriptions[mbtiType] || {
      name: '未知类型',
      description: '暂无描述信息',
      strengths: [],
      weaknesses: [],
      careers: []
    }
  }

  // 重置store
  const resetStore = () => {
    testResults.value = []
    currentTestDetail.value = null
    loading.value = false
    error.value = null
  }

  // 获取测试统计信息
  const fetchTestStatistics = async (questionnaireId?: number) => {
    try {
      loading.value = true
      error.value = null
      
      // 如果没有提供问卷ID，使用默认值或获取全局统计
      if (!questionnaireId) {
        ElMessage.error('请提供问卷ID以获取统计信息')
        return
      }
      const requestData = { questionnaireId: questionnaireId }
      const stats = await testApi.getTestStatistics(requestData)
      currentTestStatistics.value = {
        ...stats,
        totalParticipants: stats.statistics.totalParticipants || 0,
        mbtiDistribution: stats.statistics.mbtiDistribution || {},
        latestTestTime: stats.statistics.latestTestTime || ''
      }
      
    } catch (err: any) {
      console.error('获取测试统计失败:', err)
      error.value = err.message || '获取测试统计失败'
      // 不显示错误消息，因为这个功能是可选的
      currentTestStatistics.value = null
    } finally {
      loading.value = false
    }
  }

  // 获取全部测试统计信息
  const fetchAllTestStatistics = async () => {
    try {
      loading.value = true
      error.value = null
      
      const stats = await testApi.getAllTestStatistics()
      testStatisticses.value = {
        ...stats,
        totalParticipants: stats.totalParticipants || 0,
        mbtiDistribution: stats.mbtiDistribution || {},
        latestTestTime: stats.latestTestTime || '',
        details: stats.details || []
      }
      
    } catch (err: any) {
      console.error('获取所有测试统计失败:', err)
      error.value = err.message || '获取所有测试统计失败'
      ElMessage.error(error.value || '获取所有测试统计失败')

      testStatisticses.value = null
    } finally {
      loading.value = false
    }
  }

  // 清除当前测试详情
  const clearCurrentTestDetail = () => {
    currentTestDetail.value = null
  }

  // 获取MBTI报告
  const fetchMbtiReport = async (answerId: number) => {
    // 首先尝试从当前的测试结果中获取数据
    const testResult = testResults.value.find(r => r.answerId === answerId)
    if (!testResult) {
      // 如果本地没有找到，尝试从API获取
      try {
        const result = await fetchTestDetail(answerId)
        if (!result) throw new Error('测试结果不存在')
        
        // 使用API返回的数据构建报告
        return buildMbtiReportFromTestResult(result)
      } catch (error) {
        throw new Error('无法获取测试结果')
      }
    }
    
    return buildMbtiReportFromTestResult(testResult)
  }

  // 从测试结果构建MBTI报告
  const buildMbtiReportFromTestResult = (testResult: Test) => {
    const mbtiType = testResult.mbtiType || ''
    const mbtiDescription = getMbtiDescription(mbtiType)

    // 处理后端返回的数据
    let dimensions: Record<string, string> = {}
    let statistics: Record<string, number> = {}
    let personalityProbabilities: Record<string, number> = {}

    if (testResult.dimensions) {
      // 后端返回的是维度对比格式，需要转换为单个字母的概率值
      dimensions = {
        'E_I': testResult.dimensions.E_I || '',
        'S_N': testResult.dimensions.S_N || '',
        'T_F': testResult.dimensions.T_F || '',
        'J_P': testResult.dimensions.J_P || ''
      }
    }
    
    if (testResult.statistics) {
      // 后端返回的是单个字母的概率值，需要转换为维度对比格式
      const probs = testResult.statistics
      statistics = {
        'E': (probs.E_percentage || 0) * 100,  // 转换为百分比
        'I': (probs.I_percentage || 0) * 100,
        'S': (probs.S_percentage || 0) * 100,
        'N': (probs.N_percentage || 0) * 100,
        'T': (probs.T_percentage || 0) * 100,
        'F': (probs.F_percentage || 0) * 100,
        'J': (probs.J_percentage || 0) * 100,
        'P': (probs.P_percentage || 0) * 100
      }
    }

    if (testResult.personalityProbabilities) {
      for (const key in testResult.personalityProbabilities) {
        if (Object.prototype.hasOwnProperty.call(testResult.personalityProbabilities, key)) {
          personalityProbabilities[key] = testResult.personalityProbabilities[key] || 0
        }
      }
    }
    return {
      mbtiType,
      dimensions,
      statistics,
      personalityProbabilities,
      description: mbtiDescription.description,
      traits: mbtiDescription.strengths || [],
      strengths: mbtiDescription.strengths || [],
      weaknesses: mbtiDescription.weaknesses || [],
      careers: mbtiDescription.careers || []
    }
  }

  // 重新测试 (模拟)
  const retakeTest = async (testId: number) => {
    // 模拟重新测试逻辑
    ElMessage.info('重新测试功能开发中...')
    return true
  }

  // 删除测试结果 (模拟)
  const deleteTestResult = async (answerId: number) => {
    testResults.value = testResults.value.filter(r => r.answerId !== answerId)
    ElMessage.success('测试结果已删除')
  }

  // 检查用户是否已完成测试
  const checkTestCompleted = async (questionnaireId: number) => {
    try {
      const result = await testApi.checkTestCompleted({ questionnaireId })
      return result
    } catch (error: any) {
      console.error('检查测试完成状态失败:', error)
      return { completed: false }
    }
  }

  const getAnswerCount = async () => {
    try {
      const result = await testApi.getTestAnswerCount()
      console.log('获取测试答案数量:', result.count)
      return result.count || 0
    } catch(error) {
      ElMessage.error("获取数量失败")
      return 0
    }
  }

  return {
    // 状态
    testResults,
    currentTestDetail,
    testStatisticses,
    currentTestStatistics,
    loading,
    error,
    
    // 计算属性
    hasTestResults,
    latestTestResult,
    
    // 方法
    fetchTestResults,
    fetchTestDetail,
    fetchTestStatistics,
    fetchAllTestStatistics,
    fetchMbtiReport,
    buildMbtiReportFromTestResult,
    checkTestCompleted,
    retakeTest,
    deleteTestResult,
    getAnswerCount,
    submitTestAnswers,
    getMbtiDescription,
    resetStore,
    clearCurrentTestDetail,
  }
})