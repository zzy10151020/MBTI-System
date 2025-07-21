import { ref } from 'vue'
import { defineStore } from 'pinia'

export const useUiStateStore = defineStore('uiState', () => {
  const loginning = ref(false)
  const registering = ref(false)

  // 打开登录弹窗
  const openLogin = () => {
    loginning.value = true
    registering.value = false // 关闭注册弹窗
  }
  
  // 关闭登录弹窗
  const closeLogin = () => {
    loginning.value = false
  }

  // 打开注册弹窗
  const openRegister = () => {
    registering.value = true
    loginning.value = false // 关闭登录弹窗
  }
  
  // 关闭注册弹窗
  const closeRegister = () => {
    registering.value = false
  }

  return {
    loginning,
    registering,
    openLogin,
    closeLogin,
    openRegister,
    closeRegister
  }
})