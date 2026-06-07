import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { loginApi } from '@/api/auth'
import { getUserProfile } from '@/api/user'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || '')
  const user = ref(null)

  const isLoggedIn = computed(() => !!token.value)

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
    if (!token.value) return
    try {
      const res = await getUserProfile()
      if (res.code === 200) {
        user.value = res.data
      }
    } catch {
      // token invalid
    }
  }

  function logout() {
    setToken('')
    user.value = null
  }

  if (token.value) {
    fetchProfile()
  }

  return { token, user, isLoggedIn, login, logout, fetchProfile }
})
