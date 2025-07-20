import service from './axios'
import type { 
  ApiResponse,
  Question,
  CreateQuestionRequest,
  BatchCreateQuestionsRequest,
  UpdateQuestionRequest,
  DeleteQuestionRequest,
  GetQuestionsByQuestionnaireRequest,
  GetQuestionsByDimensionRequest,
  GetQuestionDetailRequest,
  CountQuestionsRequest
} from './types'

/**
 * 题目管理相关API
 */
export const questionApi = {
  /**
   * 获取所有题目（管理员功能）
   */
  async getAllQuestions(): Promise<Question[]> {
    const response = await service.get<any, ApiResponse<Question[]>>('/api/question/all')
    return response.data!
  },

  /**
   * 根据问卷ID获取题目列表
   * @param data 请求数据
   */
  async getQuestionsByQuestionnaire(data: GetQuestionsByQuestionnaireRequest): Promise<Question[]> {
    const response = await service.post<any, ApiResponse<Question[]>>('/api/question/byQuestionnaire', data)
    return response.data!
  },

  /**
   * 根据维度获取题目
   * @param data 请求数据
   */
  async getQuestionsByDimension(data: GetQuestionsByDimensionRequest): Promise<Question[]> {
    const response = await service.post<any, ApiResponse<Question[]>>('/api/question/byDimension', data)
    return response.data!
  },

  /**
   * 获取题目详情
   * @param data 请求数据
   */
  async getQuestionDetail(data: GetQuestionDetailRequest): Promise<Question> {
    const response = await service.post<any, ApiResponse<Question>>('/api/question/detail', data)
    return response.data!
  },

  /**
   * 创建题目
   * @param data 创建题目请求数据
   */
  async createQuestion(data: CreateQuestionRequest): Promise<Question> {
    const response = await service.post<any, ApiResponse<Question>>('/api/question', data)
    return response.data!
  },

  /**
   * 批量创建题目
   * @param data 批量创建题目请求数据
   */
  async batchCreateQuestions(data: BatchCreateQuestionsRequest): Promise<Question[]> {
    const response = await service.post<any, ApiResponse<Question[]>>('/api/question/batch', data)
    return response.data!
  },

  /**
   * 更新题目
   * @param data 更新题目请求数据
   */
  async updateQuestion(data: UpdateQuestionRequest): Promise<Question> {
    const response = await service.put<any, ApiResponse<Question>>('/api/question', data)
    return response.data!
  },

  /**
   * 删除题目
   * @param data 删除题目请求数据
   */
  async deleteQuestion(data: DeleteQuestionRequest): Promise<void> {
    await service.delete('/api/question', { data })
  },

  /**
   * 统计问卷题目数量
   * @param data 统计请求数据
   */
  async countQuestions(data: CountQuestionsRequest): Promise<{ count: number }> {
    const response = await service.post<any, ApiResponse<{ count: number }>>('/api/question/count', data)
    return response.data!
  }
}

export default questionApi
