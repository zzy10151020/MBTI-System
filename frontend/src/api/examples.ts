/**
 * API使用示例
 * 这个文件展示了如何在Vue组件中使用各个API模块
 */

import { ref, onMounted } from 'vue'
import { 
  authApi, 
  userApi, 
  questionnaireApi, 
  testApi,
  type User,
  type Questionnaire,
  type TestResult 
} from '@/api'

// 示例1: 在登录组件中使用认证API
export function useLogin() {
  const loading = ref(false)
  const errorMessage = ref('')

  const login = async (username: string, password: string) => {
    try {
      loading.value = true
      errorMessage.value = ''
      
      const result = await authApi.login({ username, password })
      
      // 保存token
      authApi.setToken(result.token)
      
      console.log('登录成功:', result)
      return result
    } catch (error: any) {
      errorMessage.value = error.message || '登录失败'
      throw error
    } finally {
      loading.value = false
    }
  }

  const logout = () => {
    authApi.logout()
    // 可以添加页面跳转逻辑
  }

  return {
    loading,
    errorMessage,
    login,
    logout,
    isLoggedIn: authApi.isLoggedIn()
  }
}

// 示例2: 在用户资料页面使用用户API
export function useUserProfile() {
  const user = ref<User | null>(null)
  const loading = ref(false)

  const fetchProfile = async () => {
    try {
      loading.value = true
      user.value = await userApi.getProfile()
    } catch (error) {
      console.error('获取用户信息失败:', error)
    } finally {
      loading.value = false
    }
  }

  const updateProfile = async (email: string) => {
    try {
      loading.value = true
      user.value = await userApi.updateProfile({ email })
      console.log('更新成功')
    } catch (error) {
      console.error('更新失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 组件挂载时自动获取用户信息
  onMounted(() => {
    if (authApi.isLoggedIn()) {
      fetchProfile()
    }
  })

  return {
    user,
    loading,
    fetchProfile,
    updateProfile
  }
}

// 示例3: 在问卷列表页面使用问卷API
export function useQuestionnaires() {
  const questionnaires = ref<Questionnaire[]>([])
  const loading = ref(false)

  const fetchActiveQuestionnaires = async () => {
    try {
      loading.value = true
      questionnaires.value = await questionnaireApi.getActiveQuestionnaires()
    } catch (error) {
      console.error('获取问卷列表失败:', error)
    } finally {
      loading.value = false
    }
  }

  const fetchQuestionnaireDetail = async (id: number) => {
    try {
      return await questionnaireApi.getQuestionnaireDetail(id)
    } catch (error) {
      console.error('获取问卷详情失败:', error)
      throw error
    }
  }

  return {
    questionnaires,
    loading,
    fetchActiveQuestionnaires,
    fetchQuestionnaireDetail
  }
}

// 示例4: 在测试页面使用测试API
export function useTest() {
  const loading = ref(false)
  const testResults = ref<TestResult[]>([])

  const submitTest = async (questionnaireId: number, answers: Record<string, number>) => {
    try {
      loading.value = true
      const result = await testApi.submitAnswers({
        questionnaireId,
        questionAnswers: answers
      })
      console.log('测试结果:', result)
      return result
    } catch (error) {
      console.error('提交测试失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const fetchTestResults = async () => {
    try {
      loading.value = true
      const response = await testApi.getTestResults()
      testResults.value = response.results
    } catch (error) {
      console.error('获取测试结果失败:', error)
    } finally {
      loading.value = false
    }
  }

  const getMbtiReport = async (answerId: number) => {
    try {
      return await testApi.getMbtiReport(answerId)
    } catch (error) {
      console.error('获取MBTI报告失败:', error)
      throw error
    }
  }

  return {
    loading,
    testResults,
    submitTest,
    fetchTestResults,
    getMbtiReport
  }
}

// 示例5: 错误处理封装
export function useApiErrorHandler() {
  const handleApiError = (error: any) => {
    if (error.response) {
      // HTTP错误
      switch (error.response.status) {
        case 401:
          console.error('未授权，请重新登录')
          authApi.logout()
          // 跳转到登录页
          break
        case 403:
          console.error('权限不足')
          break
        case 404:
          console.error('请求的资源不存在')
          break
        case 500:
          console.error('服务器错误，请稍后重试')
          break
        default:
          console.error(`请求失败，状态码：${error.response.status}`)
      }
    } else if (error.message) {
      // 业务错误
      console.error('业务错误:', error.message)
    } else {
      console.error('未知错误:', error)
    }
  }

  return {
    handleApiError
  }
}

// 示例6: 在Vue组件中的完整使用示例
/*
<template>
  <div>
    <div v-if="loading">加载中...</div>
    <div v-else>
      <h1>欢迎, {{ user?.username }}</h1>
      <div v-for="questionnaire in questionnaires" :key="questionnaire.questionnaireId">
        <h3>{{ questionnaire.title }}</h3>
        <p>{{ questionnaire.description }}</p>
        <button @click="startTest(questionnaire.questionnaireId)">
          开始测试
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useUserProfile, useQuestionnaires, useTest } from '@/api/examples'

const { user, loading: userLoading } = useUserProfile()
const { questionnaires, loading: questionnaireLoading, fetchActiveQuestionnaires } = useQuestionnaires()
const { submitTest } = useTest()

const loading = computed(() => userLoading.value || questionnaireLoading.value)

const startTest = async (questionnaireId: number) => {
  try {
    // 这里可以跳转到测试页面
    console.log('开始测试:', questionnaireId)
  } catch (error) {
    console.error('开始测试失败:', error)
  }
}

onMounted(() => {
  fetchActiveQuestionnaires()
})
</script>
*/
