import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { testApi } from '@/api'
import type { 
  TestResult, 
  TestResultsResponse, 
  MbtiReport, 
  TestStatistics,
  PageParams 
} from '@/api/types'

export const useTestStore = defineStore('test', () => {
  // 状态
  const testResults = ref<TestResult[]>([])
  const currentReport = ref<MbtiReport | null>(null)
  const statistics = ref<TestStatistics | null>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)
  
  // 分页状态
  const pagination = ref({
    totalElements: 0,
    totalPages: 0,
    currentPage: 0,
    pageSize: 10
  })

  // 计算属性
  const hasTestResults = computed(() => testResults.value.length > 0)
  const latestTestResult = computed(() => {
    return testResults.value.length > 0 ? testResults.value[0] : null
  })

  // 获取测试结果列表
  const fetchTestResults = async (params: PageParams = {}) => {
    try {
      loading.value = true
      error.value = null
      
      const response = await testApi.getTestResults(params)
      
      testResults.value = response.results
      pagination.value = {
        totalElements: response.totalElements,
        totalPages: response.totalPages,
        currentPage: response.currentPage,
        pageSize: response.pageSize
      }
      
    } catch (err: any) {
      console.error('获取测试结果失败:', err)
      error.value = err.message || '获取测试结果失败'
      ElMessage.error(error.value || '获取测试结果失败')
      
      // 降级处理 - 使用mock数据
      testResults.value = getMockTestResults()
      pagination.value = {
        totalElements: testResults.value.length,
        totalPages: 1,
        currentPage: 0,
        pageSize: 10
      }
    } finally {
      loading.value = false
    }
  }

  // 获取MBTI报告
  const fetchMbtiReport = async (answerId: number) => {
    try {
      loading.value = true
      error.value = null
      
      const report = await testApi.getMbtiReport(answerId)
      currentReport.value = report
      
      return report
    } catch (err: any) {
      console.error('获取MBTI报告失败:', err)
      error.value = err.message || '获取MBTI报告失败'
      
      // 降级处理 - 生成mock报告
      const mockReport = generateMockReport(answerId)
      currentReport.value = mockReport
      
      return mockReport
    } finally {
      loading.value = false
    }
  }

  // 获取测试统计（管理员）
  const fetchTestStatistics = async () => {
    try {
      loading.value = true
      error.value = null
      
      const stats = await testApi.getTestStatistics()
      statistics.value = stats
      
      return stats
    } catch (err: any) {
      console.error('获取测试统计失败:', err)
      error.value = err.message || '获取测试统计失败'
      
      // 降级处理
      const mockStats = getMockStatistics()
      statistics.value = mockStats
      
      return mockStats
    } finally {
      loading.value = false
    }
  }

  // 删除测试结果
  const deleteTestResult = async (answerId: number) => {
    try {
      await testApi.deleteTestResult(answerId)
      
      // 从本地状态中移除
      testResults.value = testResults.value.filter(result => result.answerId !== answerId)
      
      ElMessage.success('删除成功')
      return true
    } catch (err: any) {
      console.error('删除测试结果失败:', err)
      ElMessage.error(err.message || '删除失败')
      return false
    }
  }

  // 重新测试
  const retakeTest = async (questionnaireId: number) => {
    try {
      await testApi.retakeTest(questionnaireId)
      
      // 刷新测试结果列表
      await fetchTestResults()
      
      ElMessage.success('已清除之前的测试结果，可以重新开始测试')
      return true
    } catch (err: any) {
      console.error('重新测试失败:', err)
      ElMessage.error(err.message || '重新测试失败')
      return false
    }
  }

  // 提交答案并保存结果
  const submitTestAnswers = async (data: any) => {
    try {
      loading.value = true
      error.value = null
      
      console.log('提交答案数据:', data)
      
      // 调用API提交答案
      const result = await testApi.submitAnswers(data)
      
      console.log('提交成功，返回结果:', result)
      
      // 将新的测试结果添加到列表的开头
      const newTestResult = {
        answerId: result.answerId,
        questionnaireId: data.questionnaireId,
        questionnaireTitle: '性格测试问卷', // 暂时使用默认标题，后续可以从questionnaireStore获取
        mbtiType: result.mbtiType,
        submittedAt: result.submittedAt || new Date().toISOString(),
        dimensionScores: result.dimensionScores
      }
      
      testResults.value.unshift(newTestResult)
      
      // 更新分页信息
      pagination.value.totalElements += 1
      
      ElMessage.success(`测试提交成功！您的MBTI类型是：${result.mbtiType}`)
      
      return result
    } catch (error: any) {
      console.error('提交答案失败:', error)
      ElMessage.error(error.message || '提交答案失败，请重试')
      throw error
    } finally {
      loading.value = false
    }
  }

  // 保存模拟测试结果（用于API失败时的降级处理）
  const saveMockTestResult = (mockResult: any, questionnaireId: number) => {
    const newTestResult = {
      answerId: Date.now(),
      questionnaireId,
      questionnaireTitle: '性格测试问卷',
      mbtiType: mockResult.mbtiType,
      submittedAt: new Date().toISOString(),
      dimensionScores: mockResult.dimensionScores
    }
    
    // 将新的测试结果添加到列表的开头
    testResults.value.unshift(newTestResult)
    
    // 更新分页信息
    pagination.value.totalElements += 1
    
    return newTestResult
  }

  // Mock数据生成
  const getMockTestResults = (): TestResult[] => {
    return [
      {
        answerId: 1,
        questionnaireId: 1,
        questionnaireTitle: 'MBTI性格测试',
        mbtiType: 'ENFP',
        submittedAt: '2025-07-02T10:30:00'
      },
      {
        answerId: 2,
        questionnaireId: 2,
        questionnaireTitle: '职业兴趣测评',
        mbtiType: 'INFJ',
        submittedAt: '2025-07-01T15:20:00'
      }
    ]
  }

  const generateMockReport = (answerId: number): MbtiReport => {
    const mbtiTypes = ['ENFP', 'INFJ', 'ENTP', 'INTJ', 'ESFP', 'ISFJ', 'ESTP', 'ISTJ']
    const randomType = mbtiTypes[Math.floor(Math.random() * mbtiTypes.length)]
    
    return {
      mbtiType: randomType,
      dimensionScores: {
        EI: Math.floor(Math.random() * 21) - 10, // -10 to 10
        SN: Math.floor(Math.random() * 21) - 10,
        TF: Math.floor(Math.random() * 21) - 10,
        JP: Math.floor(Math.random() * 21) - 10
      },
      description: `${randomType}类型的人通常具有独特的性格特征和行为模式。`,
      strengths: ['创造力', '同理心', '适应性', '热情'],
      challenges: ['过度理想化', '压力管理', '决策困难'],
      careers: ['心理咨询师', '教师', '作家', '艺术家'],
      generatedAt: new Date().toISOString()
    }
  }

  const getMockStatistics = (): TestStatistics => {
    return {
      totalAnswers: 150,
      totalQuestionnaires: 3,
      publishedQuestionnaires: 2,
      mbtiDistribution: {
        'ENFP': 15, 'INFP': 12, 'ENFJ': 8, 'INFJ': 10,
        'ENTP': 11, 'INTP': 9, 'ENTJ': 7, 'INTJ': 13,
        'ESFP': 6, 'ISFP': 8, 'ESFJ': 9, 'ISFJ': 11,
        'ESTP': 5, 'ISTP': 7, 'ESTJ': 10, 'ISTJ': 9
      }
    }
  }

  // 重置store
  const resetStore = () => {
    testResults.value = []
    currentReport.value = null
    statistics.value = null
    loading.value = false
    error.value = null
    pagination.value = {
      totalElements: 0,
      totalPages: 0,
      currentPage: 0,
      pageSize: 10
    }
  }

  return {
    // 状态
    testResults,
    currentReport,
    statistics,
    loading,
    error,
    pagination,
    
    // 计算属性
    hasTestResults,
    latestTestResult,
    
    // 方法
    fetchTestResults,
    fetchMbtiReport,
    fetchTestStatistics,
    deleteTestResult,
    retakeTest,
    submitTestAnswers,
    saveMockTestResult,
    resetStore
  }
})
