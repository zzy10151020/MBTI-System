import service from './axios'
import type { ApiResponse } from './types'

export interface QuestionOption {
  optionId?: number
  questionId?: number
  content: string
  score: number
}

export interface Question {
  questionId?: number
  questionnaireId?: number
  content: string
  dimension: string // EI, SN, TF, JP
  questionOrder?: number
  options: QuestionOption[]
}

export interface CreateQuestionRequest {
  content: string
  dimension: string
  questionOrder?: number
  options: Omit<QuestionOption, 'optionId' | 'questionId'>[]
}

export interface UpdateQuestionRequest extends CreateQuestionRequest {
  questionId?: number
}

export const questionApi = {
  /**
   * 获取问卷的所有问题
   */
  getQuestionsByQuestionnaireId: async (questionnaireId: number): Promise<Question[]> => {
    const response = await service.get<any, ApiResponse<Question[]>>(`/api/questions/questionnaire/${questionnaireId}`)
    return response.data
  },

  /**
   * 根据ID获取问题详情
   */
  getQuestionById: async (questionnaireId: number, questionId: number): Promise<Question> => {
    const response = await service.get<any, ApiResponse<Question>>(`/api/questions/${questionId}`)
    return response.data
  },

  /**
   * 创建新问题
   */
  createQuestion: async (questionnaireId: number, questionData: CreateQuestionRequest): Promise<Question> => {
    const response = await service.post<any, ApiResponse<Question>>(`/api/questions/questionnaire/${questionnaireId}`, questionData)
    return response.data
  },

  /**
   * 批量创建问题
   */
  createQuestionsBatch: async (questionnaireId: number, questionsData: CreateQuestionRequest[]): Promise<Question[]> => {
    const response = await service.post<any, ApiResponse<Question[]>>(`/api/questions/questionnaire/${questionnaireId}/batch`, questionsData)
    return response.data
  },

  /**
   * 更新问题
   */
  updateQuestion: async (questionnaireId: number, questionId: number, questionData: UpdateQuestionRequest): Promise<Question> => {
    const response = await service.put<any, ApiResponse<Question>>(`/api/questions/${questionId}`, questionData)
    return response.data
  },

  /**
   * 删除问题
   */
  deleteQuestion: async (questionnaireId: number, questionId: number): Promise<void> => {
    await service.delete<any, ApiResponse<void>>(`/api/questions/${questionId}`)
  },

  /**
   * 更新问题顺序
   */
  reorderQuestions: async (questionnaireId: number, questionOrders: Record<number, number>): Promise<void> => {
    await service.put<any, ApiResponse<void>>(`/api/questions/questionnaire/${questionnaireId}/reorder`, questionOrders)
  }
}
