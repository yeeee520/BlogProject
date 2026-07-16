<template>
  <div class="travels-page">
    <section class="page-hero">
      <span class="page-label">旅行日志</span>
      <h1 class="page-title"><WordsPullUp text="走过山川湖海" wordClass="page-title-word" /></h1>
      <p class="page-subtitle">每一次出发，都是对自我的重新发现。</p>
    </section>

    <div v-if="authStore.isLoggedIn" class="admin-bar">
      <router-link to="/travels/manage" class="admin-link-btn">
        <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 20h9"/><path d="M16.5 3.5a2.121 2.121 0 0 1 3 3L7 19l-4 1 1-4 12.5-12.5z"/></svg>
        管理游记
      </router-link>
    </div>

    <section class="tag-bar" v-if="tags.length">
      <button class="tag-btn" :class="{ active: activeTag === '' }" @click="filterByTag('')">全部</button>
      <button v-for="tag in tags" :key="tag" class="tag-btn" :class="{ active: activeTag === tag }" @click="filterByTag(tag)">{{ tag }}</button>
    </section>

    <section class="travels-content">
      <div v-if="loading" class="loading-hint"><p>加载中...</p></div>
      <div v-else-if="notes.length === 0" class="empty-hint">
        <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1" stroke-linecap="round" stroke-linejoin="round" style="color: var(--color-gray-500)"><rect x="3" y="3" width="18" height="18" rx="2" ry="2"/><circle cx="8.5" cy="8.5" r="1.5"/><polyline points="21 15 16 10 5 21"/></svg>
        <p>暂无游记</p>
      </div>
      <div v-else class="posts-grid">
        <article v-for="note in notes" :key="note.noteId" class="post-card" @click="goNote(note.noteId)">
          <div v-if="note.coverUrl" class="card-cover">
            <img :src="note.coverUrl" :alt="note.title" class="cover-image" loading="lazy" />
          </div>
          <div class="card-body">
            <h3 class="post-title">{{ note.title }}</h3>
            <p class="post-summary">{{ note.summary }}</p>
            <div class="post-meta">
              <span v-if="note.travelDate">{{ note.travelDate }}</span>
              <span v-if="note.readTime">{{ note.readTime }}</span>
              <span v-if="note.viewCount">{{ note.viewCount }} 次阅读</span>
            </div>
          </div>
        </article>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getNotes, getNoteTags } from '@/api/travel'
import { useAuthStore } from '@/stores/auth'
import WordsPullUp from '@/components/WordsPullUp.vue'

const router = useRouter()
const authStore = useAuthStore()
const notes = ref([])
const tags = ref([])
const activeTag = ref('')
const loading = ref(true)

function goNote(id) { router.push('/post/' + id) }

async function loadNotes() {
  loading.value = true
  try {
    const params = { status: 1 }
    if (activeTag.value) params.tag = activeTag.value
    const res = await getNotes(params)
    if (res.code == 200) notes.value = res.data || []
  } catch (e) { console.error(e) }
  finally { loading.value = false }
}

async function loadTags() {
  try {
    const res = await getNoteTags()
    if (res.code == 200) tags.value = res.data || []
  } catch (e) { console.error(e) }
}

function filterByTag(tag) {
  activeTag.value = tag
  loadNotes()
}

onMounted(() => { loadNotes(); loadTags() })
</script>

<style scoped>
.travels-page { padding-top: 70px; min-height: 100vh; background: var(--color-bg-black); }
.page-hero { padding: 60px 16px 40px; text-align: center; }
@media (min-width: 768px) { .page-hero { padding: 100px 48px 60px; } }
.page-label { color: var(--color-primary); font-size: 11px; letter-spacing: 3px; text-transform: uppercase; }
.page-title { margin-top: 16px; font-size: 28px; font-weight: 700; color: var(--color-primary-text); line-height: 1.2; }
@media (min-width: 768px) { .page-title { font-size: 42px; } }
:deep(.page-title-word) { font-weight: inherit; }
.page-subtitle { margin-top: 16px; font-size: 14px; color: var(--color-gray-400); }

.admin-bar { display: flex; justify-content: center; padding: 0 16px 8px; }
.admin-link-btn {
  display: inline-flex; align-items: center; gap: 6px;
  font-size: 12px; color: var(--color-gray-400); text-decoration: none;
  padding: 6px 16px; border-radius: 999px;
  background: rgba(255,255,255,0.04); border: 1px solid rgba(255,255,255,0.06);
  transition: all 0.3s;
}
.admin-link-btn:hover { color: var(--color-primary-text); background: rgba(255,255,255,0.08); }

.tag-bar { display: flex; flex-wrap: wrap; gap: 8px; justify-content: center; padding: 0 16px 24px; max-width: 800px; margin: 0 auto; }
@media (min-width: 768px) { .tag-bar { padding: 0 48px 32px; } }
.tag-btn { background: rgba(255,255,255,0.06); color: var(--color-gray-400); border: 1px solid rgba(255,255,255,0.08); border-radius: 999px; padding: 6px 16px; font-size: 13px; cursor: pointer; transition: all 0.3s; }
.tag-btn:hover { color: var(--color-primary-text); background: rgba(255,255,255,0.1); }
.tag-btn.active { color: #000; background: var(--color-primary); border-color: var(--color-primary); }

.travels-content { padding: 0 16px 80px; max-width: 800px; margin: 0 auto; }
@media (min-width: 768px) { .travels-content { padding: 0 48px 120px; } }

.loading-hint, .empty-hint { text-align: center; padding: 60px 16px; color: var(--color-gray-500); }
.empty-hint { display: flex; flex-direction: column; align-items: center; gap: 12px; }

.posts-grid { display: flex; flex-direction: column; gap: 16px; }
.post-card {
  background: var(--color-bg-dark); border-radius: 12px; overflow: hidden;
  cursor: pointer; transition: transform 0.4s ease, box-shadow 0.4s ease;
  border: 1px solid rgba(222,219,200,0.06);
}
.post-card:hover { transform: translateY(-3px); box-shadow: 0 12px 36px rgba(0,0,0,0.3); }
.card-cover { overflow: hidden; background: var(--color-bg-card); }
.cover-image { width: 100%; height: 200px; object-fit: cover; display: block; transition: transform 0.6s ease; }
.post-card:hover .cover-image { transform: scale(1.03); }
@media (min-width: 768px) { .cover-image { height: 240px; } }
.card-body { padding: 24px 28px; }
.post-title { font-size: 17px; font-weight: 600; color: var(--color-primary-text); margin-bottom: 8px; }
.post-card:hover .post-title { color: var(--color-primary); }
.post-summary { font-size: 14px; color: var(--color-gray-400); line-height: 1.7; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; margin-bottom: 12px; }
.post-meta { display: flex; gap: 16px; font-size: 12px; color: var(--color-gray-500); }
</style>

