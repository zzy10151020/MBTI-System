import service from './axios'
import type { 
  GetQuestionnairesByCreatorRequest,
  SearchQuestionnaireRequest,
  CreateQuestionnaireRequest,
  UpdateQuestionnaireRequest,
  DeleteQuestionnaireRequest,
  PublishQuestionnaireRequest,
  UnpublishQuestionnaireRequest,
  GetQuestionnaireDetailRequest,
} from './types'

export const questionnaireApi = {
  // 根据问卷ID获取问卷信息
  getQuestionnaireById: async (questionnaireId: number): Promise<any> => {
    const response = await service.get(`/api/questionnaire/${questionnaireId}`)
    return response!
  },

  // 根据创建者ID获取问卷列表
  getQuestionnairesByCreator: async (data: GetQuestionnairesByCreatorRequest): Promise<any> => {
    const response = await service.post('/api/questionnaire/byCreator', data)
    return response!
  },

  // 获取已发布的问卷列表
  getPublishedQuestionnaires: async (): Promise<any> => {
    const response = await service.get('/api/questionnaire/published')
    return response!
  },

  // 获取所有问卷列表（管理员）
  getAllQuestionnaires: async (): Promise<any> => {
    const response = await service.get('/api/questionnaire/all')
    return response!
  },

  // 搜索问卷
  searchQuestionnaire: async (data: SearchQuestionnaireRequest): Promise<any> => {
    const response = await service.post('/api/questionnaire/search', data)
    return response!
  },

  // 创建问卷
  createQuestionnaire: async (data: CreateQuestionnaireRequest): Promise<any> => {
    const response = await service.post('/api/questionnaire', data)
    return response!
  },

  // 更新问卷
  updateQuestionnaire: async (data: UpdateQuestionnaireRequest): Promise<any> => {
    const response = await service.put('/api/questionnaire', data)
    return response!
  },

  // 删除问卷
  deleteQuestionnaire: async (data: DeleteQuestionnaireRequest): Promise<any> => {
    const response = await service.delete('/api/questionnaire', { data })
    return response!
  },

  // 发布问卷
  publishQuestionnaire: async (data: PublishQuestionnaireRequest): Promise<any> => {
    const response = await service.post('/api/questionnaire/publish', data)
    return response!
  },

  // 撤销发布问卷
  unpublishQuestionnaire: async (data: UnpublishQuestionnaireRequest): Promise<any> => {
    const response = await service.post('/api/questionnaire/unpublish', data)
    return response!
  },

  // 获取问卷详情
  getQuestionnaireDetail: async (data: GetQuestionnaireDetailRequest): Promise<any> => {
    const response = await service.post('/api/questionnaire/detail', data)
    return response!
  }
}

export default questionnaireApi