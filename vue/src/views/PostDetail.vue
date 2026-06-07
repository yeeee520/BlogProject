<template>
  <div class="detail-page">
    <div v-if="loading" class="detail-loading">
      <el-skeleton :rows="6" animated />
    </div>
    <div v-else-if="error" class="detail-error">
      <el-result icon="error" title="加载失败" :sub-title="error">
        <template #extra>
          <el-button type="primary" @click="goHome">返回首页</el-button>
        </template>
      </el-result>
    </div>
    <div v-else-if="post" class="detail-container">
      <div class="detail-header">
        <h1 class="detail-title">{{ post.title }}</h1>
        <div class="detail-meta">
          <div class="detail-author">
            <el-avatar :size="36" :src="post.authorAvatar" />
            <span class="author-name">{{ post.author }}</span>
          </div>
          <span class="meta-sep">·</span>
          <span class="meta-item">{{ post.date }}</span>
          <span class="meta-sep">·</span>
          <span class="meta-item">{{ post.readTime }} 阅读</span>
        </div>
      </div>
      <el-divider />
      <div class="detail-content" v-html="renderedContent"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getPostDetail } from '@/api/post'

const route = useRoute()
const router = useRouter()
const post = ref(null)
const loading = ref(true)
const error = ref('')

function goHome() {
  router.push('/manage')
}

function renderMarkdown(text) {
  if (!text) return ''
  return text
    .split('\n')
    .map((line) => {
      if (line.startsWith('### ')) return '<h3>' + line.slice(4) + '</h3>'
      if (line.startsWith('## ')) return '<h2>' + line.slice(3) + '</h2>'
      if (line.startsWith('# ')) return '<h1>' + line.slice(2) + '</h1>'
      if (line.trim() === '') return '<br/>'
      return '<p>' + line + '</p>'
    })
    .join('')
}

const renderedContent = computed(() => renderMarkdown(post.value?.content || ''))

onMounted(async () => {
  try {
    const res = await getPostDetail(route.params.id)
    if (res.code === 200) {
      post.value = res.data
    } else {
      error.value = res.message || '帖子不存在'
    }
  } catch (e) {
    error.value = '请求失败: ' + (e.message || '未知错误')
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.detail-page { min-height: calc(100vh - 64px); margin-top: 64px; background: #f5f7fa; padding: 40px 0; }
.detail-loading { max-width: 800px; margin: 0 auto; padding: 40px 24px; }
.detail-error { max-width: 800px; margin: 0 auto; padding: 80px 24px; }
.detail-container { max-width: 800px; margin: 0 auto; padding: 0 24px; }
.detail-header { background: #fff; border-radius: 16px; padding: 40px 48px; box-shadow: 0 2px 12px rgba(0,0,0,0.04); }
.detail-title { font-size: 28px; font-weight: 700; color: #1a1a2e; margin-bottom: 20px; line-height: 1.4; }
.detail-meta { display: flex; align-items: center; gap: 8px; font-size: 14px; color: #8e8ea0; flex-wrap: wrap; }
.detail-author { display: flex; align-items: center; gap: 10px; }
.author-name { font-weight: 500; color: #4a4a5a; }
.meta-sep { color: #d0d4e0; }
.meta-item { color: #a0a4b8; }
.el-divider { margin: 24px 0; }
.detail-content {
  background: #fff; border-radius: 16px; padding: 40px 48px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.04);
  font-size: 16px; line-height: 1.8; color: #333;
}
.detail-content h1 { font-size: 24px; font-weight: 700; margin: 24px 0 12px; color: #1a1a2e; }
.detail-content h2 { font-size: 20px; font-weight: 700; margin: 20px 0 10px; color: #2a2a3e; }
.detail-content h3 { font-size: 18px; font-weight: 600; margin: 16px 0 8px; color: #3a3a4e; }
.detail-content p { margin-bottom: 12px; }
@media (max-width: 640px) {
  .detail-header { padding: 28px 20px; }
  .detail-content { padding: 28px 20px; }
  .detail-title { font-size: 22px; }
}
</style>
