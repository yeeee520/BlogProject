import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { loginApi, logoutApi } from '@/api/auth'
import { getUserProfile } from '@/api/user'

export const useAuthStore = defineStore('auth', () => {
  // 初始状态始终为未登录，持久化 Token 需通过 profile 接口校验后才恢复。
  const token = ref('')
  const user = ref(null)
  const initialized = ref(false)
  let initializationPromise = null

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => isLoggedIn.value && user.value?.role === 'ADMIN')

  function setToken(val) {
    token.value = val
    if (val) {
      localStorage.setItem('token', val)
    } else {
      localStorage.removeItem('token')
    }
  }

  async function login(credentials) {
    const res = await loginApi(credentials.username, credentials.password)
    if (res.code === 200) {
      setToken(res.data.token)
      user.value = res.data.user
      return true
    }
    throw new Error(res.message || '登录失败')
  }

  async function fetchProfile() {
    try {
      const res = await getUserProfile()
      if (Number(res.code) === 200) {
        user.value = res.data
        return true
      }
    } catch {
      return false
    }
    return false
  }

  function clearAuth() {
    setToken('')
    user.value = null
  }

  async function logout() {
    try {
      if (token.value) await logoutApi()
    } finally {
      clearAuth()
    }
  }

  async function initializeAuth() {
    if (initialized.value) return isLoggedIn.value
    if (initializationPromise) return initializationPromise

    initializationPromise = (async () => {
      const savedToken = localStorage.getItem('token') || ''
      if (!savedToken) {
        initialized.value = true
        return false
      }

      const valid = await fetchProfile()
      if (valid) {
        token.value = savedToken
      } else {
        clearAuth()
      }
      initialized.value = true
      return valid
    })()

    return initializationPromise
  }

  return {
    token,
    user,
    initialized,
    isLoggedIn,
    isAdmin,
    login,
    logout,
    clearAuth,
    fetchProfile,
    initializeAuth,
  }
})
