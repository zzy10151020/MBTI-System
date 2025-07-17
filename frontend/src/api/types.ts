// API 公共响应类型
export interface ApiResponse<T = any> {
  success: boolean
  data: T
  message?: string
  timestamp: number
}

export interface ApiError {
  success: false
  message: string
  error?: string
  timestamp?: number
  path?: string
}

// 分页响应类型
export interface PageResponse<T> {
  content: T[]
  pageNumber: number
  pageSize: number
  totalElements: number
  totalPages: number
  first: boolean
  last: boolean
  hasNext: boolean
  hasPrevious: boolean
}

// 操作类型枚举
export type OperationType = 'CREATE' | 'UPDATE' | 'QUERY' | 'DELETE'

// 用户相关类型
export interface User {
  userId: number
  username: string
  email: string
  role: 'USER' | 'ADMIN'
  createdAt: string
}

export interface LoginRequest {
  username: string
  password: string
  operationType: 'QUERY'
}

export interface LoginResponse {
  user: User
  sessionId: string
}

export interface RegisterRequest {
  username: string
  password: string
  email: string
  operationType: 'CREATE'
}

export interface RegisterResponse {
  userId: number
  username: string
  email: string
  role: string
  createdAt: string
}

export interface UpdateUserRequest {
  userId: number
  email?: string
  operationType: 'UPDATE'
}

export interface ChangePasswordRequest {
  currentPassword: string
  newPassword: string
  operationType: 'UPDATE'
}

// 检查用户名/邮箱响应类型
export interface CheckUsernameResponse {
  exists: boolean
  username: string
}

export interface CheckEmailResponse {
  exists: boolean
  email: string
}

// 问卷相关类型
export interface Questionnaire {
  questionnaireId: number
  title: string
  description: string
  creatorId: number
  creatorName?: string
  createdAt: string
  isPublished: boolean
}

export interface CreateQuestionnaireRequest {
  title: string
  description: string
  operationType: 'CREATE'
}

export interface UpdateQuestionnaireRequest {
  questionnaireId: number
  title?: string
  description?: string
  isPublished?: boolean
  operationType: 'UPDATE'
}

export interface QuestionOption {
  optionId: number
  questionId?: number
  content: string
  value: string  // A, B, C, D
  optionOrder?: number
  createdAt?: string
}

export interface Question {
  questionId: number
  questionnaireId?: number
  content: string
  dimension: string
  questionOrder: number
  createdAt?: string
  options: QuestionOption[]
}

export interface QuestionnaireDetail {
  questionnaireId: number
  title: string
  description: string
  creatorId: number
  creatorName?: string
  createdAt: string
  isPublished: boolean
  questions: Question[]
}

// 问题管理相关类型
export interface CreateQuestionRequest {
  questionnaireId: number
  content: string
  dimension: string
  questionOrder: number
  options: Array<{
    content: string
    value: string
    optionOrder?: number
  }>
  operationType: 'CREATE'
}

export interface UpdateQuestionRequest {
  questionId: number
  content?: string
  dimension?: string
  questionOrder?: number
  options?: Array<{
    optionId?: number
    content: string
    value: string
    optionOrder?: number
  }>
  operationType: 'UPDATE'
}

// 测试相关类型
export interface AnswerDetail {
  detailId?: number
  answerId?: number
  questionId: number
  questionContent?: string
  optionId: number
  optionContent?: string
  optionScore?: number
  selectedOption?: string
  createdAt?: string
}

export interface SubmitAnswersRequest {
  userId: number
  questionnaireId: number
  answerDetails: Array<{
    questionId: number
    optionId: number
  }>
  operationType: 'CREATE'
}

export interface SubmitAnswersResponse {
  answerId: number
  userId: number
  questionnaireId: number
  result: string
  resultDescription?: string
  mbtiType?: string
  title?: string
  description?: string
  dimensions?: Record<string, string>
  statistics?: Record<string, any>
  createdAt: string
  answerDetails?: AnswerDetail[]
}

export interface TestResult {
  answerId: number
  userId: number
  questionnaireId: number
  result: string
  resultDescription?: string
  mbtiType?: string
  createdAt: string
  answerDetails?: AnswerDetail[]
}

export interface TestResultDetail {
  answerId: number
  userId: number
  questionnaireId: number
  result: string
  resultDescription?: string
  mbtiType?: string
  title?: string
  description?: string
  dimensions?: Record<string, string>
  statistics?: Record<string, any>
  createdAt: string
  answerDetails?: AnswerDetail[]
}

// 分页查询参数
export interface PageParams {
  page?: number
  size?: number
}
