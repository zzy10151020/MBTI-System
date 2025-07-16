// API 公共响应类型
export interface ApiResponse<T = any> {
  success: boolean
  data: T
  message: string
}

export interface ApiError {
  success: false
  message: string
  error?: string
  timestamp?: string
  path?: string
}

// 分页响应类型
export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
  size: number
  first: boolean
  last: boolean
}

// 用户相关类型
export interface User {
  userId: number
  username: string
  email: string
  role: 'USER' | 'ADMIN'
  createdAt: string
  answerCount?: number
}

export interface LoginRequest {
  username: string
  password: string
}

export interface LoginResponse {
  user: User
  sessionId: string
}

export interface RegisterRequest {
  username: string
  password: string
  email: string
}

export interface RegisterResponse {
  userId: number
  username: string
  email: string
  role: string
  createdAt: string
}

export interface UpdateUserRequest {
  email?: string
}

export interface ChangePasswordRequest {
  oldPassword: string
  newPassword: string
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
  creatorUsername: string
  createdAt: string
  isPublished: boolean
  answerCount: number
  hasAnswered: boolean
}

export interface CreateQuestionnaireRequest {
  title: string
  description: string
}

export interface QuestionOption {
  optionId: number
  questionId?: number
  content: string
  scoreValue: string
}

export interface Question {
  questionId: number
  questionnaireId?: number
  content: string
  dimension: string
  questionOrder: number
  options: QuestionOption[]
}

export interface QuestionnaireDetail {
  questionnaireId: number
  title: string
  description: string
  questions: Question[]
}

export interface UpdateQuestionnaireRequest {
  questionnaireId: number
  title?: string
  description?: string
  isPublished?: boolean
}

// 测试相关类型
export interface SubmitAnswersRequest {
  questionnaireId: number
  answerDetails: Array<{
    questionId: number
    optionId: number
  }>
}

export interface DimensionScores {
  EI: number
  SN: number
  TF: number
  JP: number
}

export interface SubmitAnswersResponse {
  answerId: number
  questionnaireId: number
  userId: number
  mbtiType: string
  dimensionScores: DimensionScores
  submittedAt: string
}

export interface TestResult {
  answerId: number
  questionnaireId: number
  userId: number
  mbtiType: string
  submittedAt: string
  answerDetails: Array<{
    questionId: number
    optionId: number
    selectedOption: string
    score: number
  }>
}

export interface TestResultDetail {
  answerId: number
  questionnaireId: number
  userId: number
  mbtiType: string
  dimensionScores: DimensionScores
  submittedAt: string
  answerDetails: Array<{
    questionId: number
    optionId: number
    selectedOption: string
    score: number
  }>
}

// 分页查询参数
export interface PageParams {
  page?: number
  size?: number
}

export interface CreateQuestionRequest {
  questionnaireId: number
  content: string
  dimension: string
  questionOrder: number
  options: Array<{
    content: string
    scoreValue: string
  }>
}

export interface UpdateQuestionRequest {
  questionId: number
  content?: string
  dimension?: string
  questionOrder?: number
  options?: Array<{
    optionId?: number
    content: string
    scoreValue: string
  }>
}
