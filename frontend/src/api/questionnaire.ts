import service from './axios'
import type { 
  ApiResponse,
  Questionnaire,
  QuestionnaireDetail,
  CreateQuestionnaireRequest,
  PageResponse,
  PageParams
} from './types'

/**
 * 问卷管理相关API
 */
export const questionnaireApi = {
  /**
   * 获取问卷列表
   * @param params 分页参数
   */
  async getQuestionnaireList(params: PageParams = {}): Promise<PageResponse<Questionnaire>> {
    const { page = 0, size = 10 } = params
    const response = await service.get<any, ApiResponse<PageResponse<Questionnaire>>>(
      `/api/questionnaires?page=${page}&size=${size}`
    )
    return response.data
  },

  /**
   * 获取活跃问卷列表
   */
  async getActiveQuestionnaires(): Promise<Questionnaire[]> {
    const response = await service.get<any, ApiResponse<Questionnaire[]>>('/api/questionnaires/active')
    return response.data
  },

  /**
   * 获取问卷详情
   * @param id 问卷ID
   */
  async getQuestionnaireDetail(id: number): Promise<Questionnaire> {
    const response = await service.get<any, ApiResponse<Questionnaire>>(`/api/questionnaires/${id}`)
    return response.data
  },

  /**
   * 获取问卷问题
   * @param id 问卷ID
   */
  async getQuestionnaireQuestions(id: number): Promise<QuestionnaireDetail> {
    const response = await service.get<any, ApiResponse<QuestionnaireDetail>>(`/api/questionnaires/${id}/questions`)
    return response.data
  },

  /**
   * 创建问卷（管理员权限）
   * @param data 问卷数据
   */
  async createQuestionnaire(data: CreateQuestionnaireRequest): Promise<Questionnaire> {
    const response = await service.post<any, ApiResponse<Questionnaire>>('/api/questionnaires', data)
    return response.data
  },

  /**
   * 更新问卷（管理员权限）
   * @param id 问卷ID
   * @param data 更新数据
   */
  async updateQuestionnaire(id: number, data: Partial<CreateQuestionnaireRequest>): Promise<Questionnaire> {
    const response = await service.put<any, ApiResponse<Questionnaire>>(`/api/questionnaires/${id}`, data)
    return response.data
  },

  /**
   * 删除问卷（管理员权限）
   * @param id 问卷ID
   */
  async deleteQuestionnaire(id: number): Promise<void> {
    await service.delete<any, ApiResponse<void>>(`/api/questionnaires/${id}`)
  },

  /**
   * 更新问卷状态（发布/取消发布）（管理员权限）
   * @param id 问卷ID
   * @param active 是否激活（发布）
   */
  async updateQuestionnaireStatus(id: number, active: boolean): Promise<Questionnaire> {
    const response = await service.put<any, ApiResponse<Questionnaire>>(`/api/questionnaires/${id}/status?active=${active}`)
    return response.data
  }
}

export default questionnaireApi
