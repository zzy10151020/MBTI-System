import { ref } from 'vue'
import { defineStore } from 'pinia'

export const useUiStateStore = defineStore('uiState', () => {
  const loginning = ref(localStorage.getItem('loginning') === 'true')
  const registering = ref(localStorage.getItem('registering') === 'true')

  // 打开登录弹窗
  const openLogin = () => {
    loginning.value = true
    registering.value = false // 关闭注册弹窗
    localStorage.setItem('loginning', 'true')
    localStorage.setItem('registering', 'false')
  }
  
  // 关闭登录弹窗
  const closeLogin = () => {
    loginning.value = false
    localStorage.setItem('loginning', 'false')
  }

  // 打开注册弹窗
  const openRegister = () => {
    registering.value = true
    loginning.value = false // 关闭登录弹窗
    localStorage.setItem('registering', 'true')
    localStorage.setItem('loginning', 'false')
  }
  
  // 关闭注册弹窗
  const closeRegister = () => {
    registering.value = false
    localStorage.setItem('registering', 'false')
  }

  return {
    loginning,
    registering,
    openLogin,
    closeLogin,
    openRegister,
    closeRegister
  }
}, {
  persist: true
})