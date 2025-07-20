import service from './axios'
import type { 
  ApiResponse,
  Questionnaire,
  QuestionnaireDetail,
  CreateQuestionnaireRequest,
  UpdateQuestionnaireRequest,
  QuestionnaireSearchRequest,
  QuestionnaireByCreatorRequest,
  QuestionnaireDetailRequest,
  PublishQuestionnaireRequest,
  DeleteQuestionnaireRequest
} from './types'

/**
 * 问卷管理相关API
 */
export const questionnaireApi = {
  /**
   * 根据问卷ID获取问卷信息
   * @param questionnaireId 问卷ID
   */
  async getQuestionnaireById(questionnaireId: number): Promise<Questionnaire> {
    const response = await service.get<any, ApiResponse<Questionnaire>>(`/api/questionnaire/${questionnaireId}`)
    return response.data!
  },

  /**
   * 根据创建者ID查找问卷
   * @param data 查找请求数据
   */
  async getQuestionnairesByCreator(data: QuestionnaireByCreatorRequest): Promise<Questionnaire[]> {
    const response = await service.post<any, ApiResponse<Questionnaire[]>>('/api/questionnaire/byCreator', data)
    return response.data!
  },

  /**
   * 获取已发布的问卷列表
   */
  async getPublishedQuestionnaires(): Promise<Questionnaire[]> {
    const response = await service.get<any, ApiResponse<Questionnaire[]>>('/api/questionnaire/published')
    return response.data!
  },

  /**
   * 获取所有问卷列表（管理员功能）
   */
  async getAllQuestionnaires(): Promise<Questionnaire[]> {
    const response = await service.get<any, ApiResponse<Questionnaire[]>>('/api/questionnaire/all')
    return response.data!
  },

  /**
   * 搜索问卷
   * @param data 搜索请求数据
   */
  async searchQuestionnaires(data: QuestionnaireSearchRequest): Promise<Questionnaire[]> {
    const response = await service.post<any, ApiResponse<Questionnaire[]>>('/api/questionnaire/search', data)
    return response.data!
  },

  /**
   * 创建问卷
   * @param data 创建问卷请求数据
   */
  async createQuestionnaire(data: CreateQuestionnaireRequest): Promise<Questionnaire> {
    const response = await service.post<any, ApiResponse<Questionnaire>>('/api/questionnaire', data)
    return response.data!
  },

  /**
   * 更新问卷
   * @param data 更新问卷请求数据
   */
  async updateQuestionnaire(data: UpdateQuestionnaireRequest): Promise<Questionnaire> {
    const response = await service.put<any, ApiResponse<Questionnaire>>('/api/questionnaire', data)
    return response.data!
  },

  /**
   * 删除问卷
   * @param data 删除问卷请求数据
   */
  async deleteQuestionnaire(data: DeleteQuestionnaireRequest): Promise<void> {
    await service.delete('/api/questionnaire', { data })
  },

  /**
   * 发布问卷
   * @param data 发布问卷请求数据
   */
  async publishQuestionnaire(data: PublishQuestionnaireRequest): Promise<void> {
    await service.post('/api/questionnaire/publish', data)
  },

  /**
   * 取消发布问卷
   * @param data 取消发布请求数据
   */
  async unpublishQuestionnaire(data: PublishQuestionnaireRequest): Promise<void> {
    await service.post('/api/questionnaire/unpublish', data)
  },

  /**
   * 获取问卷详情（包含所有题目）
   * @param data 获取详情请求数据
   */
  async getQuestionnaireDetail(data: QuestionnaireDetailRequest): Promise<QuestionnaireDetail> {
    const response = await service.post<any, ApiResponse<QuestionnaireDetail>>('/api/questionnaire/detail', data)
    return response.data!
  }
}

export default questionnaireApi
