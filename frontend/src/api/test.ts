import service from './axios'
import type { 
  ApiResponse,
  SubmitAnswersRequest,
  SubmitAnswersResponse,
  TestResult,
  TestResultDetail
} from './types'

/**
 * 测试相关API
 */
export const testApi = {
  /**
   * 提交答案
   * @param data 答案数据
   */
  async submitAnswers(data: { questionnaireId: number; answers: Array<{ questionId: number; optionId: number }> }): Promise<SubmitAnswersResponse> {
    // 获取当前用户ID
    const userInfo = localStorage.getItem('userInfo')
    if (!userInfo) {
      throw new Error('用户未登录，请先登录')
    }
    
    const user = JSON.parse(userInfo)
    const requestData: SubmitAnswersRequest = {
      userId: user.userId,
      questionnaireId: data.questionnaireId,
      answerDetails: data.answers,
      operationType: 'CREATE'
    }
    const response = await service.post<any, ApiResponse<SubmitAnswersResponse>>('/api/test', requestData)
    return response.data
  },

  /**
   * 获取测试结果列表
   */
  async getTestResults(): Promise<TestResult[]> {
    const response = await service.get<any, ApiResponse<TestResult[]>>('/api/test')
    return response.data
  },

  /**
   * 获取测试详情
   * @param answerId 答案ID
   */
  async getTestDetail(answerId: number): Promise<TestResultDetail> {
    const response = await service.get<any, ApiResponse<TestResultDetail>>(`/api/test?id=${answerId}`)
    return response.data
  }
}

export default testApi
