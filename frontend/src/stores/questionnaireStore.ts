import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { questionnaireApi, type Questionnaire } from '@/api'
import type {
  CreateQuestionnaireRequest,
  UpdateQuestionnaireRequest,
} from '@/api/types'

export const useQuestionnaireStore = defineStore('questionnaire', () => {
  // 状态
  const questionnaires_all = ref<Questionnaire[]>([])
  const questionnaires_published = ref<Questionnaire[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)

  // 计算属性
  const getQuestionnaireById = computed(() => {
    return (id: number) => questionnaires_all.value.find(q => q.questionnaireId === id)
  })

  // 操作方法
  const fetchPublishedQuestionnaires = async (): Promise<boolean> => {
    try {
      loading.value = true
      error.value = null
      const response = await questionnaireApi.getPublishedQuestionnaires()
      questionnaires_published.value = response.data || []
      return true
    } catch (err: any) {
      error.value = err.message || '获取已发布问卷列表失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  // 获取所有问卷
  const fetchAllQuestionnaires = async (): Promise<boolean> => {
    try {
      loading.value = true
      error.value = null
      const response = await questionnaireApi.getAllQuestionnaires()
      questionnaires_all.value = response.data || []
      return true
    } catch (err: any) {
      error.value = err.message || '获取所有问卷失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  // 获取问卷详情
  const fetchQuestionnaireDetail = async (id: number) => {
    try {
      loading.value = true
      error.value = null
      const response = await questionnaireApi.getQuestionnaireDetail({ questionnaireId: id })
      return response.data
    } catch (err: any) {
      console.warn('API获取问卷详情失败:', err)
      error.value = err.message || '获取问卷详情失败'
      return null
    } finally {
      loading.value = false
    }
  }

  // 获取问卷问题
  const fetchQuestionnaireQuestions = async (id: number) => {
    try {
      loading.value = true
      error.value = null
      
      // 使用questionApi获取问题列表
      const response = await questionnaireApi.getQuestionnaireDetail({ questionnaireId: id })
      return response.data
    } catch (err: any) {
      console.error('questionnaireStore: questionApi获取问题失败:', err)
      error.value = null
      return null
    } finally {
      loading.value = false
    }
  }

  // 创建问卷
  const createQuestionnaire = async (data: CreateQuestionnaireRequest): Promise<boolean> => {
    try {
      loading.value = true
      error.value = null
      const response = await questionnaireApi.createQuestionnaire(data)
      if (!response.data) {
        throw new Error('创建问卷失败，未返回有效数据')
      }
      return true
    } catch (err: any) {
      console.error('创建问卷失败:', err)
      throw err
    } finally {
      loading.value = false
    }
  }

  // 更新问卷
  const updateQuestionnaire = async (data: UpdateQuestionnaireRequest): Promise<boolean> => {
    try {
      loading.value = true
      error.value = null
      
      // 调用API更新问卷
      const response = await questionnaireApi.updateQuestionnaire(data)
      const updatedQuestionnaire = response.data
      // 更新本地状态
      const index = questionnaires_all.value.findIndex(q => q.questionnaireId === data.questionnaireId)
      if (index !== -1) {
        questionnaires_all.value[index] = updatedQuestionnaire
      } else {
        console.warn(`问卷 ${data.questionnaireId} 未找到，无法更新`)
        throw new Error(`问卷 ${data.questionnaireId} 未找到，无法更新`)
      }
      return true
    } catch (err: any) {
      console.error('更新问卷失败:', err)
      throw err
    } finally {
      loading.value = false
    }
  }

  // 删除问卷
  const deleteQuestionnaire = async (questionnaireId: number) => {
    try {
      loading.value = true
      error.value = null
      await questionnaireApi.deleteQuestionnaire({ questionnaireId })
      questionnaires_all.value = questionnaires_all.value.filter(q => q.questionnaireId !== questionnaireId)
      return true
    } catch (err: any) {
      console.error('删除问卷失败:', err)
      throw err
    } finally {
      loading.value = false
    }
  }

  // 发布问卷
  const publishQuestionnaire = async (questionnaireId: number): Promise<boolean> => {
    try {
      loading.value = true
      error.value = null
      
      // 调用API发布问卷
      const response = await questionnaireApi.publishQuestionnaire({ questionnaireId })
      const updatedQuestionnaire = response.data
      // 更新本地状态
      const index = questionnaires_all.value.findIndex(q => q.questionnaireId === questionnaireId)
      if (index !== -1) {
        questionnaires_all.value[index].isPublished = updatedQuestionnaire.isPublished
      } else {
        console.warn(`问卷 ${questionnaireId} 未找到，无法更新状态`)
        return false
      }
      return true
    } catch (err: any) {
      console.error('发布问卷失败:', err)
      throw err
    } finally {
      loading.value = false
    }
  }

  // 撤销发布问卷
  const unpublishQuestionnaire = async (questionnaireId: number): Promise<boolean> => {
    try {
      loading.value = true
      error.value = null
      
      // 调用API撤销发布问卷
      const response = await questionnaireApi.unpublishQuestionnaire({ questionnaireId })
      const updatedQuestionnaire = response.data
      // 更新本地状态
      const index = questionnaires_all.value.findIndex(q => q.questionnaireId === questionnaireId)
      if (index !== -1) {
        questionnaires_all.value[index].isPublished = updatedQuestionnaire.isPublished === false ? false : questionnaires_all.value[index].isPublished
      } else {
        console.warn(`问卷 ${questionnaireId} 未找到，无法更新状态`)
        return false
      }
      return true
    } catch (err: any) {
      console.error('撤销发布问卷失败:', err)
      throw err
    } finally {
      loading.value = false
    }
  }

  // 清空状态
  const reset = () => {
    questionnaires_all.value = []
    questionnaires_published.value = []
    loading.value = false
    error.value = null
  }

  return {
    // 状态
    questionnaires_all,
    questionnaires_published,
    loading,
    error,
    
    // 计算属性
    getQuestionnaireById,
    
    // 方法
    fetchPublishedQuestionnaires,
    fetchAllQuestionnaires,
    fetchQuestionnaireDetail,
    fetchQuestionnaireQuestions,
    createQuestionnaire,
    updateQuestionnaire,
    deleteQuestionnaire,
    publishQuestionnaire,
    unpublishQuestionnaire,
    reset
  }
})
