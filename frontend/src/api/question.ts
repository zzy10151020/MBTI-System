import service from './axios'
import type { 
  GetQuestionsByQuestionnaireRequest,
  GetQuestionsByDimensionRequest,
  GetQuestionDetailRequest,
  CreateQuestionRequest,
  BatchCreateQuestionsRequest,
  UpdateQuestionRequest,
  DeleteQuestionRequest,
  CountQuestionsRequest,
} from './types'

export const questionApi = {
  // 获取所有题目（管理员）
  getAllQuestions: async (): Promise<any> => {
    const response = await service.get('/api/question/all')
    return response.data!
  },

  // 根据问卷ID获取题目列表
  getQuestionsByQuestionnaire: async (data: GetQuestionsByQuestionnaireRequest): Promise<any> => {
    const response = await service.post('/api/question/byQuestionnaire', data)
    return response.data!
  },

  // 根据维度获取题目列表
  getQuestionsByDimension: async (data: GetQuestionsByDimensionRequest): Promise<any> => {
    const response = await service.post('/api/question/byDimension', data)
    return response.data!
  },

  // 获取题目详情
  getQuestionDetail: async (data: GetQuestionDetailRequest): Promise<any> => {
    const response = await service.post('/api/question/detail', data)
    return response.data!
  },

  // 创建题目
  createQuestion: async (data: CreateQuestionRequest): Promise<any> => {
    const response = await service.post('/api/question', data)
    return response.data!
  },

  // 批量创建题目
  batchCreateQuestions: async (data: BatchCreateQuestionsRequest): Promise<any> => {
    const response = await service.post('/question/batch', data)
    return response.data!
  },

  // 更新题目
  updateQuestion: async (data: UpdateQuestionRequest): Promise<any> => {
    const response = await service.put('/question', data)
    return response.data!
  },

  // 删除题目
  deleteQuestion: async (data: DeleteQuestionRequest): Promise<any> => {
    const response = await service.delete('/question', { data })
    return response.data!
  },

  // 获取题目数量
  countQuestions: async (data: CountQuestionsRequest): Promise<any> => {
    const response = await service.post('/question/count', data)
    return response.data!
  }
}

export default questionApi