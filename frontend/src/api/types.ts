// API 公共响应类型
export interface ApiResponse<T = any> {
  success: boolean
  message?: string
  data?: T
  timestamp: number
}

export interface ApiError {
  success: false
  message: string
  error?: string
  timestamp: number
}

// 用户相关类型
export interface User {
  userId: number
  username: string
  email: string
  role: 'USER' | 'ADMIN'
  createdAt: string
  answerCount?: number // 用户完成的测试次数
}

// 认证相关类型
export interface LoginRequest {
  username: string
  password: string
}

export interface LoginResponse {
  sessionId: string
  message: string
  user: User
  success: boolean
}

export interface RegisterRequest {
  username: string
  password: string
  email: string
}

export interface RegisterResponse {
  message: string
  user: User
  success: boolean
}

export interface UpdateUserRequest {
  email?: string
  currentPassword?: string
  newPassword?: string
}

// 别名，用于用户资料更新
export type UpdateProfileRequest = UpdateUserRequest

// 检查用户名/邮箱请求类型
export interface CheckUsernameRequest {
  username: string
}

export interface CheckEmailRequest {
  email: string
}

// 检查用户名/邮箱响应类型
export interface CheckUsernameResponse {
  exists: boolean
}

export interface CheckEmailResponse {
  exists: boolean
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
  questionCount?: number
}

export interface CreateQuestionnaireRequest {
  title: string
  description?: string
  questions?: QuestionRequest[]
}

export interface UpdateQuestionnaireRequest {
  questionnaireId: number
  title?: string
  description?: string
}

export interface QuestionnaireSearchRequest {
  title: string
}

export interface QuestionnaireByCreatorRequest {
  creatorId: number
}

export interface QuestionnaireDetailRequest {
  questionnaireId: number
}

export interface PublishQuestionnaireRequest {
  questionnaireId: number
}

export interface DeleteQuestionnaireRequest {
  questionnaireId: number
}

// 选项相关类型
export interface QuestionOption {
  optionId?: number
  questionId?: number
  optionText: string
  optionValue: string
  score?: number
}

// 题目相关类型
export interface Question {
  questionId: number
  questionnaireId?: number
  questionText: string
  questionType: 'SINGLE_CHOICE' | 'MULTIPLE_CHOICE'
  dimension?: string
  questionOrder?: number
  createdAt?: string
  options: QuestionOption[]
}

export interface QuestionRequest {
  questionText: string
  questionType: 'SINGLE_CHOICE' | 'MULTIPLE_CHOICE'
  options: Array<{
    optionText: string
    optionValue: string
  }>
}

export interface QuestionnaireDetail {
  questionnaireId: number
  title: string
  description: string
  creatorId: number
  creatorName?: string
  createdAt: string
  isPublished: boolean
  questionCount: number
  questions: Question[]
}

// 问题管理相关类型
export interface CreateQuestionRequest {
  questionnaireId: number
  questionText: string
  questionType: 'SINGLE_CHOICE' | 'MULTIPLE_CHOICE'
  options: Array<{
    optionText: string
    optionValue: string
  }>
}

export interface BatchCreateQuestionsRequest {
  questionnaireId: number
  questions: CreateQuestionRequest[]
}

export interface UpdateQuestionRequest {
  questionId: number
  questionText?: string
  questionType?: 'SINGLE_CHOICE' | 'MULTIPLE_CHOICE'
  options?: Array<{
    optionText: string
    optionValue: string
  }>
}

export interface DeleteQuestionRequest {
  questionId: number
}

export interface GetQuestionsByQuestionnaireRequest {
  questionnaireId: number
}

export interface GetQuestionsByDimensionRequest {
  dimension: 'E_I' | 'S_N' | 'T_F' | 'J_P'
}

export interface GetQuestionDetailRequest {
  questionId: number
}

export interface CountQuestionsRequest {
  questionnaireId: number
}

// 测试相关类型
export interface AnswerDetail {
  detailId?: number
  answerId?: number
  questionId: number
  questionContent?: string
  optionId?: number
  optionContent?: string
  optionScore?: number
  selectedOption?: string
  createdAt?: string
}

export interface SubmitTestRequest {
  questionnaireId: number
  answerDetails: Array<{
    questionId: number
    selectedOption?: string
  }>
}

export interface SubmitTestResponse {
  testId: number
}

export interface TestResult {
  answerId: number
  userId: number
  questionnaireId: number
  result?: string
  resultDescription?: string
  mbtiType?: string
  title?: string
  description?: string
  dimensions?: Record<string, string>
  statistics?: Record<string, any>
  personalityProbabilities?: Record<string, number>
  createdAt: string
  submittedAt?: string // 提交时间的别名
  answerDetails?: AnswerDetail[]
}

export interface CheckTestCompletedRequest {
  questionnaireId: number
}

export interface CheckTestCompletedResponse {
  completed: boolean
  testId?: number
}

export interface GetTestStatisticsRequest {
  questionnaireId: number
}

// MBTI报告类型
export interface MbtiReport {
  mbtiType: string
  dimensions: Record<string, number>
  dimensionScores?: Record<string, number> // 兼容性字段
  description: string
  traits: string[]
  strengths: string[]
  weaknesses: string[]
  challenges?: string[] // 可选字段
  careers?: string[] // 可选字段
}

export interface TestStatistics {
  totalTests: number
  totalAnswers?: number // 兼容性字段
  totalQuestionnaires?: number // 问卷总数
  publishedQuestionnaires?: number // 已发布问卷数
  mbtiDistribution: Record<string, number>
  averageScores: Record<string, number>
}

export interface DeleteUserRequest {
  deleteUserId: number
}

// 分页查询参数
export interface PageParams {
  page?: number
  size?: number
}
