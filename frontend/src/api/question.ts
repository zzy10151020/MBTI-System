import service from './axios'
import type { 
  ApiResponse,
  Question,
  CreateQuestionRequest,
  UpdateQuestionRequest
} from './types'

/**
 * 问题管理相关API
 */
export const questionApi = {
  /**
   * 获取问卷的所有问题
   * @param questionnaireId 问卷ID
   */
  async getQuestionsByQuestionnaireId(questionnaireId: number): Promise<Question[]> {
    const response = await service.get<any, ApiResponse<Question[]>>(`/api/question/questionnaire/${questionnaireId}`)
    return response.data
  },

  /**
   * 创建新问题（管理员权限）
   * @param data 创建问题数据
   */
  async createQuestion(data: CreateQuestionRequest): Promise<Question> {
    const requestData = {
      ...data,
      operationType: 'CREATE'
    }
    const response = await service.post<any, ApiResponse<Question>>('/api/question/create', requestData)
    return response.data
  },

  /**
   * 更新问题（管理员权限）
   * @param data 更新问题数据
   */
  async updateQuestion(data: UpdateQuestionRequest): Promise<Question> {
    const requestData = {
      ...data,
      operationType: 'UPDATE'
    }
    const response = await service.post<any, ApiResponse<Question>>('/api/question/update', requestData)
    return response.data
  },

  /**
   * 删除问题（管理员权限）
   * @param questionId 问题ID
   */
  async deleteQuestion(questionId: number): Promise<void> {
    const requestData = {
      questionId,
      operationType: 'DELETE'
    }
    await service.post<any, ApiResponse<void>>('/api/question/delete', requestData)
  }
}

export default questionApi