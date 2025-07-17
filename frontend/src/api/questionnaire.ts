import service from './axios'
import type { 
  ApiResponse,
  Questionnaire,
  QuestionnaireDetail,
  CreateQuestionnaireRequest,
  UpdateQuestionnaireRequest
} from './types'

/**
 * 问卷管理相关API
 */
export const questionnaireApi = {
  /**
   * 获取问卷列表
   */
  async getQuestionnaireList(): Promise<Questionnaire[]> {
    const response = await service.get<any, ApiResponse<Questionnaire[]>>('/api/questionnaire')
    return response.data
  },

  /**
   * 获取问卷详情
   * @param id 问卷ID
   */
  async getQuestionnaireDetail(id: number): Promise<QuestionnaireDetail> {
    const response = await service.get<any, ApiResponse<QuestionnaireDetail>>(`/api/questionnaire?id=${id}`)
    return response.data
  },

  /**
   * 创建问卷（管理员权限）
   * @param data 创建数据
   */
  async createQuestionnaire(data: { title: string; description: string }): Promise<Questionnaire> {
    const requestData: CreateQuestionnaireRequest = {
      ...data,
      operationType: 'CREATE'
    }
    const response = await service.post<any, ApiResponse<Questionnaire>>('/api/questionnaire', requestData)
    return response.data
  },

  /**
   * 更新问卷（管理员权限）
   * @param data 更新数据
   */
  async updateQuestionnaire(data: UpdateQuestionnaireRequest): Promise<Questionnaire> {
    const response = await service.put<any, ApiResponse<Questionnaire>>('/api/questionnaire', data)
    return response.data
  },

  /**
   * 删除问卷（管理员权限）
   * @param questionnaireId 问卷ID
   */
  async deleteQuestionnaire(questionnaireId: number): Promise<void> {
    const requestData = {
      questionnaireId,
      operationType: 'DELETE'
    }
    await service.delete<any, ApiResponse<void>>('/api/questionnaire', { data: requestData })
  }
}

export default questionnaireApi