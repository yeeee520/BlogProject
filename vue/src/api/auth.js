import request from './request'

export function loginApi(username, password) {
  return request.post('/api/auth/login', { username, password })
}

export function logoutApi() {
  return request.post('/api/auth/logout')
}
