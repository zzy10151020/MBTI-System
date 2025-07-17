import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { testApi } from '@/api'
import type { 
  TestResult, 
  TestResultDetail,
  SubmitAnswersResponse
} from '@/api/types'

// API函数期望的参数类型
interface SubmitAnswersParams {
  questionnaireId: number
  answers: Array<{ questionId: number; optionId: number }>
}

export const useTestStore = defineStore('test', () => {
  // 状态
  const testResults = ref<TestResult[]>([])
  const currentTestDetail = ref<TestResultDetail | null>(null)
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
      
      const results = await testApi.getTestResults()
      testResults.value = results
      
    } catch (err: any) {
      console.error('获取测试结果失败:', err)
      error.value = err.message || '获取测试结果失败'
      ElMessage.error(error.value || '获取测试结果失败')
      
      // 降级处理 - 使用空数组
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
      
      const detail = await testApi.getTestDetail(answerId)
      currentTestDetail.value = detail
      
      return detail
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
  const submitTestAnswers = async (data: SubmitAnswersParams) => {
    try {
      loading.value = true
      error.value = null
      
      // 调用API提交答案
      const result = await testApi.submitAnswers(data)
      
      // 刷新测试结果列表
      await fetchTestResults()
      
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

  // 根据MBTI类型获取详细描述
  const getMbtiDescription = (mbtiType: string) => {
    const descriptions: Record<string, any> = {
      'ENFP': {
        name: '活跃的鼓舞者',
        description: '热情、富有想象力和创造力的人，认为生活充满可能性。',
        strengths: ['创造力', '同理心', '适应性', '热情'],
        challenges: ['过度理想化', '压力管理', '决策困难'],
        careers: ['心理咨询师', '教师', '作家', '艺术家']
      },
      'INFP': {
        name: '调停者',
        description: '诗意的、善良的利他主义者，总是热心帮助正义的事业。',
        strengths: ['理想主义', '创造力', '同理心', '适应性'],
        challenges: ['过于理想化', '容易受伤', '难以做决定'],
        careers: ['写作', '心理治疗', '艺术创作', '社会工作']
      },
      'ENFJ': {
        name: '主角',
        description: '有魅力的、鼓舞人心的领导者，能够使听众着迷。',
        strengths: ['领导力', '沟通能力', '同理心', '组织能力'],
        challenges: ['过度关注他人', '完美主义', '容易疲惫'],
        careers: ['教育', '咨询', '管理', '公关']
      },
      'INFJ': {
        name: '倡导者',
        description: '安静而神秘，同时鼓舞人心且不知疲倦的理想主义者。',
        strengths: ['洞察力', '理想主义', '决心', '创造力'],
        challenges: ['完美主义', '过度敏感', '倦怠'],
        careers: ['咨询', '写作', '研究', '非营利组织']
      },
      'ENTP': {
        name: '辩论家',
        description: '聪明好奇的思想家，无法抗拒智力上的挑战。',
        strengths: ['创新思维', '适应性', '热情', '多才多艺'],
        challenges: ['缺乏专注', '不喜欢常规', '争论倾向'],
        careers: ['企业家', '发明家', '营销', '咨询']
      },
      'INTP': {
        name: '逻辑学家',
        description: '创新的发明家，对知识有着止不住的渴望。',
        strengths: ['逻辑思维', '独立性', '创造力', '灵活性'],
        challenges: ['社交困难', '缺乏动力', '过度批判'],
        careers: ['研究', '工程', '计算机科学', '哲学']
      },
      'ENTJ': {
        name: '指挥官',
        description: '大胆、富有想象力、意志强烈的领导者。',
        strengths: ['领导力', '自信', '战略思维', '效率'],
        challenges: ['不耐烦', '傲慢', '缺乏同理心'],
        careers: ['管理', '企业家', '律师', '投资']
      },
      'INTJ': {
        name: '建筑师',
        description: '富有想象力和战略性的思想家，一切皆在计划之中。',
        strengths: ['独立性', '决心', '洞察力', '多才多艺'],
        challenges: ['过度自信', '缺乏耐心', '完美主义'],
        careers: ['科学研究', '工程', '法律', '管理']
      },
      'ESFP': {
        name: '娱乐家',
        description: '自发的、精力充沛的热情人士，生活在他们周围从不无聊。',
        strengths: ['热情', '友好', '适应性', '实用性'],
        challenges: ['缺乏专注', '冲动', '压力敏感'],
        careers: ['销售', '娱乐', '教育', '社会工作']
      },
      'ISFP': {
        name: '探险家',
        description: '灵活、迷人的艺术家，总是准备探索新的可能性。',
        strengths: ['艺术性', '灵活性', '热情', '实用性'],
        challenges: ['过度敏感', '缺乏长期规划', '竞争力弱'],
        careers: ['艺术', '设计', '医疗', '教育']
      },
      'ESFJ': {
        name: '执政官',
        description: '非常关心他人的人，总是乐于帮助。',
        strengths: ['合作性', '实用性', '支持性', '组织能力'],
        challenges: ['过度关注他人', '缺乏创新', '冲突回避'],
        careers: ['教育', '医疗', '社会工作', '管理']
      },
      'ISFJ': {
        name: '守护者',
        description: '非常专注、温暖的守护者，总是准备保护亲人。',
        strengths: ['支持性', '可靠性', '耐心', '实用性'],
        challenges: ['过度谦逊', '抗拒变化', '过度利他'],
        careers: ['医疗', '教育', '行政', '社会工作']
      },
      'ESTP': {
        name: '企业家',
        description: '聪明、精力充沛的感知者，真正享受生活在边缘。',
        strengths: ['适应性', '实用性', '感知力', '社交能力'],
        challenges: ['冲动', '缺乏专注', '风险偏好'],
        careers: ['销售', '市场营销', '娱乐', '体育']
      },
      'ISTP': {
        name: '鉴赏家',
        description: '大胆而实际的实验家，掌握各种工具。',
        strengths: ['实用性', '灵活性', '危机应对', '独立性'],
        challenges: ['固执', '缺乏耐心', '风险偏好'],
        careers: ['工程', '机械', '计算机', '执法']
      },
      'ESTJ': {
        name: '总经理',
        description: '出色的管理者，在管理事物或人员方面无与伦比。',
        strengths: ['组织能力', '实用性', '可靠性', '领导力'],
        challenges: ['不灵活', '过度专注工作', '缺乏耐心'],
        careers: ['管理', '行政', '法律', '军事']
      },
      'ISTJ': {
        name: '物流师',
        description: '实际和注重事实的可靠性，可靠性无可挑剔。',
        strengths: ['可靠性', '实用性', '负责任', '冷静'],
        challenges: ['抗拒变化', '过度批判', '缺乏灵活性'],
        careers: ['会计', '行政', '法律', '工程']
      }
    }
    
    return descriptions[mbtiType] || {
      name: '未知类型',
      description: '暂无描述信息',
      strengths: [],
      challenges: [],
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

  // 清除当前测试详情
  const clearCurrentTestDetail = () => {
    currentTestDetail.value = null
  }

  return {
    // 状态
    testResults,
    currentTestDetail,
    loading,
    error,
    
    // 计算属性
    hasTestResults,
    latestTestResult,
    
    // 方法
    fetchTestResults,
    fetchTestDetail,
    submitTestAnswers,
    getMbtiDescription,
    resetStore,
    clearCurrentTestDetail
  }
})