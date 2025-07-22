// 定义API请求和响应的类型
// 这些类型用于描述API请求和响应的格式，便于前端与后端进行数据交互
export interface ApiResponse<T = any> {
  success: boolean        // 请求是否成功
  data?: T                // 成功时返回的数据
  message?: string        // 错误或提示信息
  timestamp?: number      // 响应时间戳
}

// 用户相关API请求和响应类型定义
// 这些类型用于描述用户登录、注册、登出等操作的请求和响应格式
export interface User {
  userId: number          // 用户ID
  username: string        // 用户名
  email: string           // 邮箱地址
  role: "ADMIN" | "USER"  // 用户角色
  createdAt: string       // 创建时间
}

export interface LoginRequest {
  username: string        // 登录用户名
  password: string        // 登录密码
}

export interface RegisterRequest {
  username: string        // 注册用户名
  password: string        // 注册密码
  email: string           // 注册邮箱
}

export interface CheckUsernameRequest {
  username: string        // 要检查的用户名
}

export interface CheckEmailRequest {
  email: string           // 要检查的邮箱
}

export interface UpdateUserRequest {
  updateUserId?: number         // 要更新的用户ID
  username?: string       // 更新的用户名
  email?: string          // 更新的邮箱地址
  currentPassword?: string // 当前密码（用于验证）
  newPassword?: string     // 新密码（如果需要更新）
  role?: "ADMIN" | "USER"  // 更新的用户角色
}

export interface DeleteUserRequest {
  deleteUserId: number    // 要删除的用户ID
}

// 问卷相关API请求和响应类型定义
export interface Questionnaire {
  questionnaireId: number, // 问卷ID
  title: string,           // 问卷标题
  description: string,     // 问卷描述
  creatorId: number,       // 创建者ID
  creatorName: string,     // 创建者用户名
  createdAt: string,       // 创建时间
  isPublished: boolean,    // 是否已发布
  questionCount: number    // 问卷题目数量
  questions?: Question[]   // 问卷题目列表（可选）
}

export interface GetQuestionnairesByCreatorRequest {
  creatorId: number        // 创建者ID
}

export interface SearchQuestionnaireRequest {
  title: string           // 要搜索的问卷标题
}

export interface CreateQuestionnaireRequest {
  title: string,          // 问卷标题
  description: string,    // 问卷描述
  questions?: Question[], // 问卷题目列表
}

export interface UpdateQuestionnaireRequest {
  questionnaireId: number, // 问卷ID
  title?: string,          // 更新的问卷标题
  description?: string,    // 更新的问卷描述
}

export interface DeleteQuestionnaireRequest {
  questionnaireId: number   // 要删除的问卷ID
}

export interface PublishQuestionnaireRequest {
  questionnaireId: number   // 要发布的问卷ID
}

export interface UnpublishQuestionnaireRequest {
  questionnaireId: number   // 要取消发布的问卷ID
}

export interface GetQuestionnaireDetailRequest {
  questionnaireId: number   // 要获取的问卷ID
}

// 题目相关API请求和响应类型定义
export interface Question {
  questionId?: number          // 题目ID
  questionnaireId: number     // 所属问卷ID
  content: string             // 题目内容
  dimension: string           // 题目维度
  questionOrder: number       // 题目顺序
  createdAt?: string           // 创建时间
  options: Option[]           // 题目选项
}

export interface Option {
  optionId?: number           // 选项ID
  content: string             // 选项内容
  score: number               // 选项分数
}

export interface GetQuestionsByQuestionnaireRequest {
  questionnaireId: number     // 问卷ID
}

export interface GetQuestionsByDimensionRequest {
  dimension: string           // 题目维度 "EI", "SN", "TF", "JP"
}

export interface GetQuestionDetailRequest {
  questionId: number          // 题目ID
}

export interface CreateQuestionRequest {
  questionnaireId: number,    // 所属问卷ID
  content: string,            // 题目内容
  dimension: string,          // 题目维度
  questionOrder: number,      // 题目顺序
  options: Option[],          // 题目选项
}

export interface BatchCreateQuestionsRequest {
  questions: Question[]       // 要批量创建的题目列表
}

export interface UpdateQuestionRequest {
  questionId: number,         // 题目ID
  content?: string,           // 更新的题目内容
  dimension?: string,         // 更新的题目维度
  questionOrder?: number,     // 更新的题目顺序
  options?: Option[],         // 更新的题目选项
}

export interface DeleteQuestionRequest {
  questionId: number          // 要删除的题目ID
}

export interface CountQuestionsRequest {
  questionnaireId: number     // 问卷ID
}

// 测试相关API请求和响应类型定义
export interface Test {
  answerId: number,                               // 测试答案ID
  userId: number,                                 // 用户ID
  questionnaireId: number,                        // 问卷ID
  title: string,                                  // 测试标题
  description: string,                            // 测试描述
  answeredAt: string,                             // 答题时间
  answerDetails: AnswerDetail[],                  // 答案详情列表
  mbtiType: string,                               // MBTI类型
  dimensions: Dimensions,                         // 各维度方向
  statistics: Statistics,                         // 各维度占比
  personalityProbabilities: PersonalityProbability // 人格特征概率
}

export interface Dimensions {
  E_I: string,
  S_N: string,
  T_F: string,
  J_P: string,
}
export interface Statistics {
  E_percentage: number,          // 外向百分比
  I_percentage: number,          // 内向百分比
  S_percentage: number,          // 实感百分比
  N_percentage: number,          // 直觉百分比
  T_percentage: number,          // 思考百分比
  F_percentage: number,          // 情感百分比
  J_percentage: number,          // 判断百分比
  P_percentage: number,          // 知觉百分比
}
export interface PersonalityProbability {
  [key: string]: number
}

export interface AnswerDetail {
  detailId?: number,              // 答案详情ID
  answerId?: number,              // 答案ID
  questionId: number,             // 题目ID
  questionContent?: string,       // 题目内容
  optionId: number,               // 选项ID
  optionContent?: string,         // 选项内容
  optionScore?: number,           // 选项分数
}

export interface TestStatistics {
  totalParticipants: number,              // 测试总数
  mbtiDistribution: Record<string, number>,  // 各MBTI类型的测试数量
  latestTestTime: string                  // 最新测试时间
}

export interface TestStatisticses {
  totalParticipants: number,              // 测试总数
  mbtiDistribution: Record<string, number>,  // 各MBTI类型的测试数量
  latestTestTime: string,                // 最新测试时间
  details?: TestStatistics[] // 测试详情列表
}

export interface SubmitTestRequest {
  questionnaireId: number,       // 问卷ID
  answerDetails: AnswerDetail[], // 答案详情列表
}

export interface CheckTestCompletedRequest {
  questionnaireId: number        // 问卷ID
}

export interface GetTestStatisticsRequest {
  questionnaireId: number        // 问卷ID
}