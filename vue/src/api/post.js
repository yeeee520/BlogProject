import request from './request'

const mockPosts = [
  { id: 1, title: '隐匿在深山的古村落', summary: '穿过蜿蜒的山路，一座有着三百年历史的古村落在晨雾中若隐若现。青石板路、斑驳的土墙、袅袅炊烟，时间在这里仿佛静止。', content: '# 隐匿在深山的古村落\n\n穿过蜿蜒的山路，一座有着三百年历史的古村落在晨雾中若隐若现。\n\n青石板路、斑驳的土墙、袅袅炊烟，时间在这里仿佛静止。当地人依旧保持着传统的农耕生活方式，日出而作，日落而息。\n\n## 古村的清晨\n\n清晨时分，薄雾笼罩着整个村落，远处的山峦若隐若现。公鸡的啼叫声打破了山村的宁静，炊烟从各家各户的烟囱中袅袅升起。\n\n走在青石板路上，露水打湿了鞋面，空气中弥漫着泥土和青草的气息。这里的一切都是那么自然、纯粹，让人忘却了城市的喧嚣。', author: '旅行者', authorAvatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642f92e672f1fepng.png', date: '2026-05-28', readTime: '8 min' },
  { id: 2, title: '海边的日落与诗', summary: '当夕阳把整个海面染成金红色，所有的烦恼都变得渺小了。海浪拍打着礁石，远处渔船的灯火渐次亮起，这是属于大海的浪漫。', content: '# 海边的日落与诗\n\n当夕阳把整个海面染成金红色，所有的烦恼都变得渺小了。\n\n海浪拍打着礁石，远处渔船的灯火渐次亮起，这是属于大海的浪漫。', author: '旅行者', authorAvatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642f92e672f1fepng.png', date: '2026-05-20', readTime: '6 min' },
  { id: 3, title: '城市里的秘密花园', summary: '在繁华的都市中，总有一些不为人知的角落。藏在巷子深处的独立书店，天台上的小花园，还有那些充满故事的咖啡馆。', content: '# 城市里的秘密花园\n\n在繁华的都市中，总有一些不为人知的角落。藏在巷子深处的独立书店，天台上的小花园，还有那些充满故事的咖啡馆。', author: '旅行者', authorAvatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642f92e672f1fepng.png', date: '2026-05-15', readTime: '5 min' },
  { id: 4, title: '徒步穿越雨林日记', summary: '三天的热带雨林徒步，遇见了从未见过的植物，听到了最纯粹的自然之声。夜晚的星空格外明亮，银河清晰可见。', content: '# 徒步穿越雨林日记\n\n三天的热带雨林徒步，遇见了从未见过的植物，听到了最纯粹的自然之声。夜晚的星空格外明亮，银河清晰可见。', author: '旅行者', authorAvatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642f92e672f1fepng.png', date: '2026-04-30', readTime: '12 min' },
  { id: 5, title: '古镇的慢生活哲学', summary: '在这里，没有人催促你。茶馆里的老人悠闲地打着麻将，河边的柳树随风摇摆，小船悠悠地划过水面。', content: '# 古镇的慢生活哲学\n\n在这里，没有人催促你。茶馆里的老人悠闲地打着麻将，河边的柳树随风摇摆，小船悠悠地划过水面。', author: '旅行者', authorAvatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642f92e672f1fepng.png', date: '2026-04-23', readTime: '7 min' },
]

export function getPostsList() {
  return request.get('/api/posts').catch((err) => {
    if (!err.response) {
      return { code: 200, message: 'success', data: { total: mockPosts.length, list: mockPosts.map(({ content, ...rest }) => rest) } }
    }
    throw err
  })
}

export function getPostDetail(id) {
  return request.get('/api/posts/' + id).catch((err) => {
    if (!err.response) {
      const post = mockPosts.find((p) => p.id === Number(id))
      if (post) {
        return { code: 200, message: 'success', data: post }
      }
      return { code: 404, message: '帖子不存在' }
    }
    throw err
  })
}
