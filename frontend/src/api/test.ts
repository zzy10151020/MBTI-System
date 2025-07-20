import service from './axios'
import type { 
  ApiResponse,
  TestResult,
  SubmitTestRequest,
  SubmitTestResponse,
  CheckTestCompletedRequest,
  CheckTestCompletedResponse,
  GetTestStatisticsRequest,
  TestStatistics
} from './types'

/**
 * 测试相关API
 */
export const testApi = {
  /**
   * 根据测试ID获取测试记录
   * @param testId 测试ID
   */
  async getTestById(testId: number): Promise<TestResult> {
    const response = await service.get<any, ApiResponse<TestResult>>(`/api/test/${testId}`)
    return response.data!
  },

  /**
   * 获取所有测试记录（管理员功能）
   */
  async getAllTests(): Promise<TestResult[]> {
    const response = await service.get<any, ApiResponse<TestResult[]>>('/api/test/all')
    return response.data!
  },

  /**
   * 提交测试答案
   * @param data 提交测试请求数据
   */
  async submitTest(data: SubmitTestRequest): Promise<TestResult> {
    const response = await service.post<any, ApiResponse<TestResult>>('/api/test/submit', data)
    return response.data!
  },

  /**
   * 检查用户是否已完成测试
   * @param data 检查完成测试请求数据
   */
  async checkTestCompleted(data: CheckTestCompletedRequest): Promise<CheckTestCompletedResponse> {
    const response = await service.post<any, ApiResponse<CheckTestCompletedResponse>>('/api/test/completed', data)
    return response.data!
  },

  /**
   * 获取测试统计信息
   * @param data 获取统计信息请求数据
   */
  async getTestStatistics(data: GetTestStatisticsRequest): Promise<TestStatistics> {
    const response = await service.post<any, ApiResponse<TestStatistics>>('/api/test/statistics', data)
    return response.data!
  }
}

export default testApi
