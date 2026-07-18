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
      <div v-if="post.coverUrl" class="detail-cover">
        <img :src="post.coverUrl" :alt="post.title" class="cover-image" />
      </div>
      <div class="detail-header">
        <h1 class="detail-title">{{ post.title }}</h1>
        <div class="detail-meta">
          <span v-if="post.travelDate" class="meta-item">{{ post.travelDate }}</span>
          <span v-if="post.travelDate" class="meta-sep">·</span>
          <span v-if="post.location" class="meta-item">{{ post.location }}</span>
          <span v-if="post.location && post.readTime" class="meta-sep">·</span>
          <span v-if="post.readTime" class="meta-item">{{ post.readTime }} 阅读</span>
          <span v-if="post.readTime && post.viewCount != null" class="meta-sep">·</span>
          <span v-if="post.viewCount != null" class="meta-item">{{ post.viewCount }} 次浏览</span>
        </div>
        <div v-if="post.tags" class="detail-tags">
          <span v-for="t in post.tags.split(',')" :key="t" class="detail-tag">{{ t.trim() }}</span>
        </div>
      </div>
      <div class="detail-divider"></div>
      <div class="detail-content" v-html="renderedContent"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getNote } from '@/api/travel'
import { getPostDetail } from '@/api/post'
import { renderSafeContent } from '@/utils/sanitizeContent'

const route = useRoute()
const router = useRouter()
const post = ref(null)
const loading = ref(true)
const error = ref('')

function goHome() { router.push('/manage') }

const renderedContent = computed(() => renderSafeContent(post.value?.content || ''))

onMounted(async () => {
  const id = route.params.id
  try {
    // Try travel notes API first
    const res = await getNote(id)
    if (res.code == 200) {
      post.value = res.data
      loading.value = false
      return
    }
  } catch (e) { /* fallback to posts */ }

  try {
    const res = await getPostDetail(id)
    if (res.code == 200) { post.value = res.data }
    else { error.value = res.message || '帖子不存在' }
  } catch (e) {
    error.value = '请求失败: ' + (e.message || '未知错误')
  } finally { loading.value = false }
})
</script>

<style scoped>
.detail-page { min-height: 100vh; padding: 90px 16px 40px; background: var(--color-bg-black); }
@media (min-width: 768px) { .detail-page { padding: 100px 48px 60px; } }
.detail-loading, .detail-error { max-width: 800px; margin: 0 auto; padding: 40px 24px; }
.detail-container { max-width: 800px; margin: 0 auto; }

.detail-cover { border-radius: 16px; overflow: hidden; margin-bottom: 20px; background: var(--color-bg-card); }
.cover-image { width: 100%; height: 240px; object-fit: cover; display: block; }
@media (min-width: 768px) { .cover-image { height: 360px; } }

.detail-header { background: var(--color-bg-dark); border-radius: 16px; padding: 40px 32px; border: 1px solid rgba(222,219,200,0.06); }
@media (min-width: 768px) { .detail-header { padding: 48px 48px; } }
.detail-title { font-size: 24px; font-weight: 700; color: var(--color-primary-text); margin-bottom: 20px; line-height: 1.4; }
@media (min-width: 768px) { .detail-title { font-size: 28px; } }
.detail-meta { display: flex; align-items: center; gap: 8px; font-size: 14px; color: var(--color-gray-400); flex-wrap: wrap; }
.meta-sep { color: var(--color-gray-500); }
.meta-item { color: var(--color-gray-400); }
.detail-tags { display: flex; flex-wrap: wrap; gap: 6px; margin-top: 16px; }
.detail-tag { font-size: 12px; color: var(--color-primary); background: rgba(222,219,200,0.08); padding: 3px 12px; border-radius: 999px; }

.detail-divider { height: 1px; background: rgba(222,219,200,0.08); margin: 24px 0; }
.detail-content {
  background: var(--color-bg-dark); border-radius: 16px; padding: 40px 32px;
  border: 1px solid rgba(222,219,200,0.06);
  font-size: 16px; line-height: 1.8; color: var(--color-gray-400);
}
@media (min-width: 768px) { .detail-content { padding: 48px 48px; } }
.detail-content :deep(h1) { font-size: 24px; font-weight: 700; margin: 24px 0 12px; color: var(--color-primary-text); }
.detail-content :deep(h2) { font-size: 20px; font-weight: 700; margin: 20px 0 10px; color: var(--color-primary-text); }
.detail-content :deep(h3) { font-size: 18px; font-weight: 600; margin: 16px 0 8px; color: var(--color-primary-text); }
.detail-content :deep(p) { margin-bottom: 12px; }
.detail-content :deep(.content-image-wrap) { margin: 16px 0; overflow: hidden; border-radius: 10px; background: var(--color-bg-card); }
.detail-content :deep(.content-image) { width: 100%; max-height: 420px; object-fit: cover; display: block; border-radius: 10px; }
</style>
