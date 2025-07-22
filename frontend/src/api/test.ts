import service from './axios'
import type {
	SubmitTestRequest,
	CheckTestCompletedRequest,
	GetTestStatisticsRequest,
} from './types'

export const testApi = {
	// 根据测试ID获取测试记录
	getTestById: async (testId: number): Promise<any> => {
		const response = await service.get(`/api/test/${testId}`)
	return response!
	},

	// 获取所有测试记录（管理员）
	getAllTests: async (): Promise<any> => {
		const response = await service.get('/api/test/all')
	return response!
	},

	// 提交测试答案
	submitTest: async (data: SubmitTestRequest): Promise<any> => {
		const response = await service.post('/api/test', data)
	return response!
	},

	// 检查用户是否已完成测试
	checkTestCompleted: async (data: CheckTestCompletedRequest): Promise<any> => {
		const response = await service.post('/api/test/completed', data)
	return response!
	},

	// 获取测试统计信息
	getTestStatistics: async (data: GetTestStatisticsRequest): Promise<any> => {
		const response = await service.post('/api/test/statistics', data)
	return response!
	},

  // 获取全部测试统计信息
  getAllTestStatistics: async (): Promise<any> => {
	const response = await service.get('/api/test/all-statistics')
	return response!
  },

	// 获取测试答案数量（管理员）
	getTestAnswerCount: async (): Promise<any> => {
		const response = await service.get('/api/test/answer-count')
	return response!
	}
}

export default testApi