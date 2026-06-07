import request from './request'

const mockUsers = [
  { id: 1, username: 'admin', password: '123456', nickname: '旅行者', avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642f92e672f1fepng.png', bio: '用脚步丈量世界，用文字记录时光。', postCount: 12, followerCount: 256, followingCount: 88 },
]

export function loginApi(username, password) {
  return request.post('/api/auth/login', { username, password }).catch((err) => {
    if (!err.response) {
      const user = mockUsers.find((u) => u.username === username && u.password === password)
      if (user) {
        const { password: _, ...safe } = user
        return { code: 200, message: 'success', data: { token: 'mock-token-' + user.id, user: safe } }
      }
      return { code: 401, message: '用户名或密码错误' }
    }
    throw err
  })
}

export function registerApi(username, password, nickname) {
  return request.post('/api/auth/register', { username, password, nickname }).catch((err) => {
    if (!err.response) {
      return { code: 200, message: 'success', data: { id: Date.now(), username, nickname, avatar: '', bio: '' } }
    }
    throw err
  })
}
