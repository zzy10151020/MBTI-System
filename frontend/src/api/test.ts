import service from './axios'
import type { 
  ApiResponse,
  SubmitAnswersRequest,
  SubmitAnswersResponse,
  TestResult,
  TestResultsResponse,
  MbtiReport,
  TestStatistics,
  PageParams
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
    const response = await service.post<any, ApiResponse<SubmitAnswersResponse>>('/api/test/submit', data)
    return response.data
  },

  /**
   * 获取测试结果列表
   * @param params 分页参数
   */
  async getTestResults(params: PageParams = {}): Promise<TestResultsResponse> {
    const { page = 0, size = 10 } = params
    const response = await service.get<any, ApiResponse<TestResultsResponse>>(
      `/api/test/results?page=${page}&size=${size}`
    )
    return response.data
  },

  /**
   * 获取MBTI报告
   * @param answerId 答案ID
   */
  async getMbtiReport(answerId: number): Promise<MbtiReport> {
    const response = await service.get<any, ApiResponse<MbtiReport>>(`/api/test/report/${answerId}`)
    return response.data
  },

  /**
   * 获取测试统计（管理员权限）
   */
  async getTestStatistics(): Promise<TestStatistics> {
    const response = await service.get<any, ApiResponse<TestStatistics>>('/api/test/statistics')
    return response.data
  },

  /**
   * 获取单个测试结果
   * @param answerId 答案ID
   */
  async getTestResult(answerId: number): Promise<TestResult> {
    const response = await service.get<any, ApiResponse<TestResult>>(`/api/test/results/${answerId}`)
    return response.data
  },

  /**
   * 删除测试结果
   * @param answerId 答案ID
   */
  async deleteTestResult(answerId: number): Promise<void> {
    await service.delete<any, ApiResponse<void>>(`/api/test/results/${answerId}`)
  },

  /**
   * 重新测试（删除之前的结果）
   * @param questionnaireId 问卷ID
   */
  async retakeTest(questionnaireId: number): Promise<void> {
    await service.delete<any, ApiResponse<void>>(`/api/test/retake/${questionnaireId}`)
  }
}

export default testApi
