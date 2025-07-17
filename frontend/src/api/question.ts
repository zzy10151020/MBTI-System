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
    const response = await service.get<any, ApiResponse<Question[]>>(`/api/question?questionnaireId=${questionnaireId}`)
    return response.data
  },

  /**
   * 创建新问题（管理员权限）
   * @param data 创建问题数据
   */
  async createQuestion(data: Omit<CreateQuestionRequest, 'operationType'>): Promise<Question> {
    const requestData: CreateQuestionRequest = {
      ...data,
      operationType: 'CREATE'
    }
    const response = await service.post<any, ApiResponse<Question>>('/api/question', requestData)
    return response.data
  },

  /**
   * 更新问题（管理员权限）
   * @param data 更新问题数据
   */
  async updateQuestion(data: Omit<UpdateQuestionRequest, 'operationType'>): Promise<Question> {
    const requestData: UpdateQuestionRequest = {
      ...data,
      operationType: 'UPDATE'
    }
    const response = await service.put<any, ApiResponse<Question>>('/api/question', requestData)
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
    await service.delete<any, ApiResponse<void>>('/api/question', { data: requestData })
  }
}

export default questionApi