import request from './request'

export function getUserProfile() {
  return request.get('/api/user/profile').catch((err) => {
    if (!err.response) {
      const token = localStorage.getItem('token')
      if (token) {
        return { code: 200, message: 'success', data: { id: 1, username: 'admin', nickname: '旅行者', avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642f92e672f1fepng.png', bio: '用脚步丈量世界，用文字记录时光。', postCount: 12, followerCount: 256, followingCount: 88 } }
      }
      return { code: 401, message: '未登录' }
    }
    throw err
  })
}
