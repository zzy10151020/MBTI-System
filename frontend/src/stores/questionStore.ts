import { defineStore } from 'pinia'
import { ref } from 'vue'
import { questionApi } from '@/api'
import type { Question, CreateQuestionRequest, UpdateQuestionRequest } from '@/api/types'

export const useQuestionStore = defineStore('question', () => {
  // 问题列表
  const questions = ref<Question[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)

  // 获取问题列表
  const fetchQuestions = async (questionnaireId: number) => {
    loading.value = true
    error.value = null
    try {
      const result = await questionApi.getQuestionsByQuestionnaire({ questionnaireId })
      questions.value = result
    } catch (err: any) {
      error.value = err.message || '获取问题失败'
    } finally {
      loading.value = false
    }
  }

  // 创建问题
  const createQuestion = async (data: CreateQuestionRequest) => {
    await questionApi.createQuestion(data)
    await fetchQuestions(data.questionnaireId)
  }

  // 更新问题
  const updateQuestion = async (data: UpdateQuestionRequest & { questionnaireId: number }) => {
    await questionApi.updateQuestion(data)
    await fetchQuestions(data.questionnaireId)
  }

  // 删除问题
  const deleteQuestion = async (questionId: number, questionnaireId: number) => {
    await questionApi.deleteQuestion({ questionId })
    await fetchQuestions(questionnaireId)
  }

  return {
    questions,
    loading,
    error,
    fetchQuestions,
    createQuestion,
    updateQuestion,
    deleteQuestion
  }
})
