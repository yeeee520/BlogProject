import request from './request'

const mockPlans = [
  { planId: 1, title: '西藏自驾环线', description: '计划用15天自驾穿越西藏，从拉萨出发经过纳木错、珠峰大本营、林芝等地。', coverUrl: '', planDate: '2026年8月', status: 'upcoming', sortOrder: 10 },
  { planId: 2, title: '日本京都秋季行', description: '在京都最美的红叶季节，体验传统茶道、探访古寺庙宇。', coverUrl: '', planDate: '2026年11月', status: 'planning', sortOrder: 5 },
  { planId: 3, title: '新西兰南岛徒步', description: '完成米尔福德步道和路特本步道，拍摄南阿尔卑斯山脉的壮丽风光。', coverUrl: '', planDate: '2027年1月', status: 'dreaming', sortOrder: 3 },
  { planId: 4, title: '冰岛环岛摄影之旅', description: '追寻极光、拍摄冰川与火山地貌，体验冰与火之国的独特魅力。', coverUrl: '', planDate: '2027年3月', status: 'dreaming', sortOrder: 1 },
]

export function getPlans(params = {}) {
  return request.get('/api/travel-plans', { params }).catch(() => {
    return { code: 200, data: mockPlans }
  })
}

export function getPlan(id) {
  return request.get('/api/travel-plans/' + id).catch(() => {
    const plan = mockPlans.find(p => p.planId === Number(id))
    if (plan) return { code: 200, data: plan }
    return { code: 404, message: '计划不存在' }
  })
}

export function createPlan(formData) {
  return request.post('/api/travel-plans', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export function updatePlan(id, formData) {
  return request.put('/api/travel-plans/' + id, formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export function deletePlan(id) {
  return request.delete('/api/travel-plans/' + id)
}

