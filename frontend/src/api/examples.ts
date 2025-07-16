// API使用示例
import { authApi, userApi, questionnaireApi, questionApi, testApi } from '@/api'

// 1. 登录示例
export const loginExample = async () => {
  try {
    const response = await authApi.login({
      username: 'testuser',
      password: '123456'
    })
    console.log('登录成功:', response.user)
    console.log('Session ID:', response.sessionId)
  } catch (error) {
    console.error('登录失败:', error instanceof Error ? error.message : String(error))
  }
}

// 2. 获取用户信息示例
export const getUserInfoExample = async () => {
  try {
    const userInfo = await userApi.getProfile()
    console.log('用户信息:', userInfo)
  } catch (error) {
    console.error('获取用户信息失败:', error instanceof Error ? error.message : String(error))
  }
}

// 3. 获取问卷列表示例
export const getQuestionnairesExample = async () => {
  try {
    const questionnaires = await questionnaireApi.getQuestionnaireList()
    console.log('问卷列表:', questionnaires)
  } catch (error) {
    console.error('获取问卷列表失败:', error instanceof Error ? error.message : String(error))
  }
}

// 4. 获取问卷详情和问题示例
export const getQuestionnaireDetailExample = async (questionnaireId: number) => {
  try {
    const detail = await questionnaireApi.getQuestionnaireDetail(questionnaireId)
    console.log('问卷详情:', detail)
    
    const questions = await questionApi.getQuestionsByQuestionnaireId(questionnaireId)
    console.log('问题列表:', questions)
  } catch (error) {
    console.error('获取问卷详情失败:', error instanceof Error ? error.message : String(error))
  }
}

// 5. 提交答案示例
export const submitAnswersExample = async () => {
  try {
    const result = await testApi.submitAnswers({
      questionnaireId: 1,
      answerDetails: [
        { questionId: 1, optionId: 1 },
        { questionId: 2, optionId: 4 },
        { questionId: 3, optionId: 5 },
        { questionId: 4, optionId: 8 }
      ]
    })
    console.log('提交结果:', result)
    console.log('MBTI类型:', result.mbtiType)
    console.log('维度得分:', result.dimensionScores)
  } catch (error) {
    console.error('提交答案失败:', error instanceof Error ? error.message : String(error))
  }
}

// 6. 获取测试结果示例
export const getTestResultsExample = async () => {
  try {
    const results = await testApi.getTestResults()
    console.log('测试结果列表:', results)
    
    if (results.length > 0) {
      const detail = await testApi.getTestDetail(results[0].answerId)
      console.log('测试详情:', detail)
    }
  } catch (error) {
    console.error('获取测试结果失败:', error instanceof Error ? error.message : String(error))
  }
}

// 7. 管理员功能示例
export const adminExample = async () => {
  try {
    // 创建问卷
    const newQuestionnaire = await questionnaireApi.createQuestionnaire({
      title: '新的MBTI测试',
      description: '这是一个新的MBTI性格测试问卷'
    })
    console.log('新问卷:', newQuestionnaire)
    
    // 为问卷添加问题
    const newQuestion = await questionApi.createQuestion({
      questionnaireId: newQuestionnaire.questionnaireId,
      content: '你更喜欢与人交往还是独自思考？',
      dimension: 'EI',
      questionOrder: 1,
      options: [
        { content: '更喜欢与人交往', scoreValue: '1' },
        { content: '更喜欢独自思考', scoreValue: '-1' }
      ]
    })
    console.log('新问题:', newQuestion)
    
    // 发布问卷
    const publishedQuestionnaire = await questionnaireApi.updateQuestionnaire({
      questionnaireId: newQuestionnaire.questionnaireId,
      isPublished: true
    })
    console.log('已发布问卷:', publishedQuestionnaire)
    
  } catch (error) {
    console.error('管理员操作失败:', error instanceof Error ? error.message : String(error))
  }
}

// 8. 完整的测试流程示例
export const fullTestFlowExample = async () => {
  try {
    // 1. 登录
    await loginExample()
    
    // 2. 获取问卷列表
    const questionnaires = await questionnaireApi.getQuestionnaireList()
    if (questionnaires.length === 0) {
      console.log('没有可用的问卷')
      return
    }
    
    const firstQuestionnaire = questionnaires[0]
    console.log('选择问卷:', firstQuestionnaire.title)
    
    // 3. 获取问题
    const questions = await questionApi.getQuestionsByQuestionnaireId(firstQuestionnaire.questionnaireId)
    console.log('问题数量:', questions.length)
    
    // 4. 模拟答题（随机选择第一个选项）
    const answerDetails = questions.map(question => ({
      questionId: question.questionId,
      optionId: question.options[0].optionId
    }))
    
    // 5. 提交答案
    const result = await testApi.submitAnswers({
      questionnaireId: firstQuestionnaire.questionnaireId,
      answerDetails
    })
    
    console.log('测试完成!')
    console.log('你的MBTI类型:', result.mbtiType)
    console.log('维度得分:', result.dimensionScores)
    
    // 6. 获取详细结果
    const detailResult = await testApi.getTestDetail(result.answerId)
    console.log('详细结果:', detailResult)
    
  } catch (error) {
    console.error('完整测试流程失败:', error instanceof Error ? error.message : String(error))
  }
}
