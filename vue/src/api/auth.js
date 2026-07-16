import request from './request'

export function loginApi(username, password) {
  return request.post('/api/auth/login', { username, password })
}

export function registerApi(username, password, nickname) {
  return request.post('/api/auth/register', { username, password, nickname })
}
