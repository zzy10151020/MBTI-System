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
  async submitAnswers(data: SubmitAnswersRequest): Promise<SubmitAnswersResponse> {
    const requestData = {
      ...data,
      operationType: 'CREATE'
    }
    const response = await service.post<any, ApiResponse<SubmitAnswersResponse>>('/api/test/submit', requestData)
    return response.data
  },

  /**
   * 获取测试结果列表
   */
  async getTestResults(): Promise<TestResult[]> {
    const response = await service.get<any, ApiResponse<TestResult[]>>('/api/test/get')
    return response.data
  },

  /**
   * 获取测试详情
   * @param answerId 答案ID
   */
  async getTestDetail(answerId: number): Promise<TestResultDetail> {
    const response = await service.get<any, ApiResponse<TestResultDetail>>(`/api/test/get/${answerId}`)
    return response.data
  }
}

export default testApi
