import axios from 'axios'

export const AUTH_UNAUTHORIZED_EVENT = 'auth:unauthorized'

const request = axios.create({
  baseURL: '/',
  timeout: 10000,
})

request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

request.interceptors.response.use(
  (response) => response.data,
  async (error) => {
    if (error.response?.status !== 401) {
      return Promise.reject(error)
    }

    localStorage.removeItem('token')
    window.dispatchEvent(new CustomEvent(AUTH_UNAUTHORIZED_EVENT))

    const originalConfig = error.config
    const publicReadPrefixes = [
      '/api/album/',
      '/api/travel-notes',
      '/api/travel-plans',
      '/user/',
    ]
    const isPublicRead = originalConfig?.method?.toLowerCase() === 'get'
      && publicReadPrefixes.some((prefix) => originalConfig?.url?.startsWith(prefix))

    // Token 过期时，公开读取请求自动以游客身份重试一次。
    if (isPublicRead && !originalConfig._retriedWithoutToken) {
      originalConfig._retriedWithoutToken = true
      if (typeof originalConfig.headers?.delete === 'function') {
        originalConfig.headers.delete('Authorization')
      } else if (originalConfig.headers) {
        delete originalConfig.headers.Authorization
      }
      return request(originalConfig)
    }

    error.isAuthExpired = true
    return Promise.reject(error)
  }
)

export default request
