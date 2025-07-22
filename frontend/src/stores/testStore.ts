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
      
      const response = await testApi.getAllTests()
      testResults.value = response.data || []
      
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
      
      const response = await testApi.getTestById(answerId)
      currentTestDetail.value = response.data
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
              const response = await testApi.submitTest(submitData)
      
      // 刷新测试结果列表
      await fetchTestResults()

      ElMessage.success('答案提交成功')
      
              return response.data
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
        description: 'ENFP（活跃的鼓舞者）充满热情、富有想象力和创造力，善于发现生活中的各种可能性。他们乐于探索新鲜事物，喜欢与人交流，能够激励和感染身边的人。ENFP 通常具有强烈的同理心，善于理解他人情感，适应力强，面对变化能积极应对。他们追求理想，渴望实现自我价值，但有时也会因过度理想化而感到失望，或在众多选择中难以做出决策。',
        strengths: ['创造力', '同理心', '适应性', '热情'],
        weaknesses: ['过度理想化', '压力管理', '决策困难'],
        careers: ['心理咨询师', '教师', '作家', '艺术家']
      },
      'INFP': {
        name: '调停者',
        description: 'INFP（调停者）是理想主义者，内心温柔善良，富有同情心和创造力。他们重视内在价值观，渴望为正义和美好事业贡献力量。INFP 喜欢独处，善于自省，常常以诗意和艺术的方式表达自我。他们对他人充满理解和包容，但有时会因过于理想化而受伤，或在现实与理想之间挣扎，难以做出决定。',
        strengths: ['理想主义', '创造力', '同理心', '适应性'],
        weaknesses: ['过于理想化', '容易受伤', '难以做决定'],
        careers: ['写作', '心理治疗', '艺术创作', '社会工作']
      },
      'ENFJ': {
        name: '主角',
        description: 'ENFJ（主角）天生具有领导魅力，善于激励和团结他人。他们关心集体和社会，乐于助人，能够敏锐地察觉他人需求。ENFJ 具备出色的沟通能力和组织能力，常常成为团队的核心人物。他们追求和谐，渴望让身边的人变得更好，但有时会因过度关注他人而忽略自身需求，容易感到疲惫。',
        strengths: ['领导力', '沟通能力', '同理心', '组织能力'],
        weaknesses: ['过度关注他人', '完美主义', '容易疲惫'],
        careers: ['教育', '咨询', '管理', '公关']
      },
      'INFJ': {
        name: '倡导者',
        description: 'INFJ（倡导者）安静、神秘且富有理想主义，内心充满激情和创造力。他们善于洞察人心，能够理解复杂的人际关系和深层动机。INFJ 追求意义和真实，愿意为信仰和理想付出努力。他们常常以温和坚定的方式影响他人，但也容易因追求完美和过度敏感而感到压力和倦怠。',
        strengths: ['洞察力', '理想主义', '决心', '创造力'],
        weaknesses: ['完美主义', '过度敏感', '倦怠'],
        careers: ['咨询', '写作', '研究', '非营利组织']
      },
      'ENTP': {
        name: '辩论家',
        description: 'ENTP（辩论家）聪明、好奇，喜欢挑战和创新。他们思维敏捷，善于发现问题的多种可能性，乐于与人辩论和交流观点。ENTP 不拘泥于常规，喜欢尝试新方法，适应力强。虽然他们充满热情和创造力，但有时也会因缺乏专注或过于争论而影响合作。',
        strengths: ['创新思维', '适应性', '热情', '多才多艺'],
        weaknesses: ['缺乏专注', '不喜欢常规', '争论倾向'],
        careers: ['企业家', '发明家', '营销', '咨询']
      },
      'INTP': {
        name: '逻辑学家',
        description: 'INTP（逻辑学家）是天生的思考者，喜欢分析和理解世界的本质。他们逻辑严密，独立自主，善于提出创新观点。INTP 对知识有强烈渴望，喜欢独自钻研复杂问题。虽然他们富有创造力和灵活性，但有时会因社交困难或过度批判而与他人疏远。',
        strengths: ['逻辑思维', '独立性', '创造力', '灵活性'],
        weaknesses: ['社交困难', '缺乏动力', '过度批判'],
        careers: ['研究', '工程', '计算机科学', '哲学']
      },
      'ENTJ': {
        name: '指挥官',
        description: 'ENTJ（指挥官）意志坚定、果断，是天生的领导者。他们善于制定战略，具备卓越的组织和管理能力。ENTJ 喜欢挑战和高效，能够带领团队实现目标。他们自信、理性，追求卓越，但有时也会因不耐烦或缺乏同理心而与他人产生冲突。',
        strengths: ['领导力', '自信', '战略思维', '效率'],
        weaknesses: ['不耐烦', '傲慢', '缺乏同理心'],
        careers: ['管理', '企业家', '律师', '投资']
      },
      'INTJ': {
        name: '建筑师',
        description: 'INTJ（建筑师）富有远见和战略思维，擅长规划和实现复杂目标。他们独立、理性，喜欢深入思考和创新。INTJ 对知识和效率有极高追求，善于发现系统中的不足并加以改进。虽然他们能力出众，但有时会因过度自信或追求完美而显得冷漠或难以接纳他人意见。',
        strengths: ['独立性', '决心', '洞察力', '多才多艺'],
        weaknesses: ['过度自信', '缺乏耐心', '完美主义'],
        careers: ['科学研究', '工程', '法律', '管理']
      },
      'ESFP': {
        name: '娱乐家',
        description: 'ESFP（娱乐家）热情、友好，喜欢与人互动，享受当下的生活。他们乐于参与各种社交活动，善于调动气氛，让身边的人感到愉快。ESFP 适应力强，注重实际，喜欢用行动表达自我。虽然他们充满活力，但有时也会因冲动或缺乏长远规划而遇到困难。',
        strengths: ['热情', '友好', '适应性', '实用性'],
        weaknesses: ['缺乏专注', '冲动', '压力敏感'],
        careers: ['销售', '娱乐', '教育', '社会工作']
      },
      'ISFP': {
        name: '探险家',
        description: 'ISFP（探险家）温和、灵活，富有艺术气质，喜欢探索新鲜事物。他们注重个人体验，善于用感性和创造力表达自我。ISFP 喜欢安静地帮助他人，追求和谐与自由。虽然他们适应性强，但有时会因过度敏感或缺乏规划而错失机会。',
        strengths: ['艺术性', '灵活性', '热情', '实用性'],
        weaknesses: ['过度敏感', '缺乏长期规划', '竞争力弱'],
        careers: ['艺术', '设计', '医疗', '教育']
      },
      'ESFJ': {
        name: '执政官',
        description: 'ESFJ（执政官）关心他人，乐于助人，是团队中的支持者和组织者。他们注重和谐，善于协调人际关系，喜欢为集体贡献力量。ESFJ 责任心强，注重实际，能够高效完成任务。虽然他们合作性强，但有时会因过度关注他人或回避冲突而忽略自身需求。',
        strengths: ['合作性', '实用性', '支持性', '组织能力'],
        weaknesses: ['过度关注他人', '缺乏创新', '冲突回避'],
        careers: ['教育', '医疗', '社会工作', '管理']
      },
      'ISFJ': {
        name: '守护者',
        description: 'ISFJ（守护者）安静、可靠，富有责任感，总是默默守护身边的人。他们注重细节，善于照顾他人，喜欢为家庭和集体付出。ISFJ 具有耐心和同理心，能够在压力下保持冷静。虽然他们无私奉献，但有时会因抗拒变化或过度利他而感到疲惫。',
        strengths: ['支持性', '可靠性', '耐心', '实用性'],
        weaknesses: ['过度谦逊', '抗拒变化', '过度利他'],
        careers: ['医疗', '教育', '行政', '社会工作']
      },
      'ESTP': {
        name: '企业家',
        description: 'ESTP（企业家）精力充沛、适应力强，喜欢冒险和挑战。他们善于观察和把握机会，喜欢用实际行动解决问题。ESTP 反应迅速，擅长社交，能够在危机中保持冷静。虽然他们充满活力，但有时会因冲动或风险偏好而遇到麻烦。',
        strengths: ['适应性', '实用性', '感知力', '社交能力'],
        weaknesses: ['冲动', '缺乏专注', '风险偏好'],
        careers: ['销售', '市场营销', '娱乐', '体育']
      },
      'ISTP': {
        name: '鉴赏家',
        description: 'ISTP（鉴赏家）独立、务实，喜欢动手实践和解决实际问题。他们善于分析和操作各种工具，喜欢探索新技术。ISTP 适应性强，能够在压力下迅速做出反应。虽然他们冷静理性，但有时会因固执或缺乏耐心而影响合作。',
        strengths: ['实用性', '灵活性', '危机应对', '独立性'],
        weaknesses: ['固执', '缺乏耐心', '风险偏好'],
        careers: ['工程', '机械', '计算机', '执法']
      },
      'ESTJ': {
        name: '总经理',
        description: 'ESTJ（总经理）实际、果断，是天生的管理者。他们注重秩序和效率，善于组织和领导团队。ESTJ 责任心强，能够高效完成任务，追求结果导向。虽然他们可靠务实，但有时会因不灵活或过度专注工作而忽略他人感受。',
        strengths: ['组织能力', '实用性', '可靠性', '领导力'],
        weaknesses: ['不灵活', '过度专注工作', '缺乏耐心'],
        careers: ['管理', '行政', '法律', '军事']
      },
      'ISTJ': {
        name: '物流师',
        description: 'ISTJ（物流师）严谨、可靠，注重事实和细节，是团队中的中坚力量。他们喜欢有序和可预测的环境，善于制定和执行计划。ISTJ 责任心强，能够坚持原则，完成承诺。虽然他们稳定可靠，但有时会因抗拒变化或过度批判而显得保守。',
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
      const response = await testApi.getTestStatistics(requestData)
      const stats = response.data
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
      
      const response = await testApi.getAllTestStatistics()
      const stats = response.data
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
        'E': probs.E_percentage || 0,  // 转换为百分比
        'I': probs.I_percentage || 0,
        'S': probs.S_percentage || 0,
        'N': probs.N_percentage || 0,
        'T': probs.T_percentage || 0,
        'F': probs.F_percentage || 0,
        'J': probs.J_percentage || 0,
        'P': probs.P_percentage || 0
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
      description: mbtiDescription.description || '暂无描述信息',
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
  const deleteTestResult = async () => {
    ElMessage.info('暂不支持删除测试结果功能，遵照内心的选择即可(˃ ⌑ ˂ഃ )')
  }

  // 检查用户是否已完成测试
  const checkTestCompleted = async (questionnaireId: number) => {
    try {
      const response = await testApi.checkTestCompleted({ questionnaireId })
      return response.data
    } catch (error: any) {
      console.error('检查测试完成状态失败:', error)
      return { completed: false }
    }
  }

  const getAnswerCount = async () => {
    try {
      const response = await testApi.getTestAnswerCount()
      console.log('获取测试答案数量:', response.data.count)
      return response.data.count || 0
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