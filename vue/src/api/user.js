import request from './request'

export function getUserProfile() {
  return request.get('/api/auth/profile')
}
