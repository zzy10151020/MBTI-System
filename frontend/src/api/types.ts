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
  token: string
  userId: number
  username: string
  roles: string[]
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

export interface UpdateProfileRequest {
  email?: string
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
  content: string
  score: number
}

export interface Question {
  questionId: number
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

// 测试相关类型
export interface SubmitAnswersRequest {
  questionnaireId: number
  questionAnswers: Record<string, number> // 问题ID: 选项ID
}

export interface DimensionScores {
  EI: number
  SN: number
  TF: number
  JP: number
}

export interface SubmitAnswersResponse {
  answerId: number
  mbtiType: string
  dimensionScores: DimensionScores
  submittedAt: string
}

export interface TestResult {
  answerId: number
  questionnaireId: number
  questionnaireTitle: string
  mbtiType: string
  submittedAt: string
}

export interface TestResultsResponse {
  results: TestResult[]
  totalElements: number
  totalPages: number
  currentPage: number
  pageSize: number
}

export interface MbtiReport {
  mbtiType: string
  dimensionScores: DimensionScores
  description: string
  strengths: string[]
  challenges: string[]
  careers: string[]
  generatedAt: string
}

export interface TestStatistics {
  totalAnswers: number
  totalQuestionnaires: number
  publishedQuestionnaires: number
  mbtiDistribution: Record<string, number>
}

// 分页查询参数
export interface PageParams {
  page?: number
  size?: number
}
