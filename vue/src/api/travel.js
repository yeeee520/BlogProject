import request from './request'

const mockNotes = [
  { noteId: 1, title: '隐藏在深山的古村落', summary: '穿过蜿蜒的山路，一座有着三百年历史的古村落在晨雾中若隐若现。青石板路、斑驳的土墙、袅袅炊烟，时间在这里仿佢静止。', content: '# 隐藏在深山的古村落\n\n穿过蜿蜒的山路，一座有着三百年历史的古村落在晨雾中若隐若现。\n\n## 古村的清晨\n\n清晨时分，薄雾笼罩着整个村落，远处的山峦若隐若现。', coverUrl: '', location: '云南·大理', travelDate: '2026-05-28', tags: '古村,旅行', readTime: '8 min', status: 1, viewCount: 128, createTime: '2026-05-28T10:00:00' },
  { noteId: 2, title: '海边的日落与诗', summary: '当太阳把整个海面染成金红色，所有的烦恼都变得渺小了。海浪拍打着礁石，远处渔船的灯火渐次亮起，这是属于大海的浪漫。', content: '# 海边的日落与诗\n\n当太阳把整个海面染成金红色，所有的烦恼都变得渺小了。', coverUrl: '', location: '海南·三亚', travelDate: '2026-05-20', tags: '海边,日落', readTime: '6 min', status: 1, viewCount: 256, createTime: '2026-05-20T10:00:00' },
  { noteId: 3, title: '城市里的秘密花园', summary: '在繁华的都市中，总有一些不为人知的角落。藏在巷子深处的独立书店，天台上的小花园，还有那些充满故事的咖啡馆。', content: '# 城市里的秘密花园\n\n在繁华的都市中，总有一些不为人知的角落。', coverUrl: '', location: '上海', travelDate: '2026-05-15', tags: '城市,探索', readTime: '5 min', status: 1, viewCount: 89, createTime: '2026-05-15T10:00:00' },
]

export function getNotes(params = {}) {
  return request.get('/api/travel-notes', { params }).catch(() => {
    let filtered = mockNotes.filter(n => n.status === 1)
    if (params.tag) {
      filtered = filtered.filter(n => n.tags && n.tags.split(',').map(t => t.trim()).includes(params.tag))
    }
    return { code: 200, data: filtered }
  })
}

export function getNote(id) {
  return request.get('/api/travel-notes/' + id).catch(() => {
    const note = mockNotes.find(n => n.noteId === Number(id))
    if (note) return { code: 200, data: note }
    return { code: 404, message: '游记不存在' }
  })
}

export function createNote(formData) {
  return request.post('/api/travel-notes', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export function updateNote(id, formData) {
  return request.put('/api/travel-notes/' + id, formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export function deleteNote(id) {
  return request.delete('/api/travel-notes/' + id)
}

export function uploadNoteImage(id, formData) {
  return request.post('/api/travel-notes/' + id + '/images', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export function getNoteTags() {
  return request.get('/api/travel-notes/tags').catch(() => {
    const tagSet = new Set()
    mockNotes.forEach(n => {
      if (n.tags) n.tags.split(',').map(t => t.trim()).filter(Boolean).forEach(t => tagSet.add(t))
    })
    return { code: 200, data: Array.from(tagSet) }
  })
}

