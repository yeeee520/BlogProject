<template>
  <div class="album-page">
    <section class="page-hero">
      <span class="page-label">影像相册</span>
      <h1 class="page-title"><WordsPullUp text="用镜头记录世界" wordClass="page-title-word" /></h1>
      <p class="page-subtitle">每一帧画面，都是一段旅程的见证。</p>
    </section>

    <div v-if="authStore.isAdmin" class="admin-bar">
      <router-link to="/album/manage" class="admin-link-btn">
        <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 20h9"/><path d="M16.5 3.5a2.121 2.121 0 0 1 3 3L7 19l-4 1 1-4 12.5-12.5z"/></svg>
        管理相册
      </router-link>
    </div>

    <section class="tag-bar" v-if="tags.length">
      <button class="tag-btn" :class="{ active: activeTag === '' }" @click="filterByTag('')">全部</button>
      <button v-for="tag in tags" :key="tag" class="tag-btn" :class="{ active: activeTag === tag }" @click="filterByTag(tag)">{{ tag }}</button>
    </section>

    <section class="album-content">
      <div v-if="loading" class="album-loading"><p>加载中...</p></div>
      <div v-else-if="photos.length === 0" class="album-empty">
        <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1" stroke-linecap="round" stroke-linejoin="round" style="color: var(--color-gray-500)"><rect x="3" y="3" width="18" height="18" rx="2" ry="2"/><circle cx="8.5" cy="8.5" r="1.5"/><polyline points="21 15 16 10 5 21"/></svg>
        <p>暂无照片</p>
      </div>
      <div v-else class="album-masonry">
        <div v-for="(photo, i) in photos" :key="photo.photoId" class="masonry-item" :style="{ animationDelay: (i * 0.07) + 's' }">
          <div class="photo-card" @click="openLightbox(i)">
            <div class="photo-image-wrap">
              <img v-if="photo.url" :src="photo.url" :alt="photo.title" class="photo-image" loading="lazy" />
              <div v-else class="photo-placeholder">
                <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1" stroke-linecap="round" stroke-linejoin="round" style="color: var(--color-gray-500)"><rect x="3" y="3" width="18" height="18" rx="2" ry="2"/><circle cx="8.5" cy="8.5" r="1.5"/><polyline points="21 15 16 10 5 21"/></svg>
              </div>
            </div>
            <div class="photo-info">
              <h3>{{ photo.title }}</h3>
              <div class="photo-meta">
                <span v-if="photo.location">{{ photo.location }}</span>
                <span v-if="photo.photoDate" class="photo-date">{{ photo.photoDate }}</span>
              </div>
              <div v-if="photo.tags" class="photo-tags">
                <span v-for="t in photo.tags.split(',')" :key="t" class="photo-tag">{{ t.trim() }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <el-dialog v-model="lightboxVisible" :show-close="true" width="90%" destroy-on-close class="lightbox-dialog" top="4vh">
      <div class="lightbox-wrapper">
        <div class="lightbox-counter" v-if="photos.length">{{ currentIndex + 1 }} / {{ photos.length }}</div>
        <div class="lightbox-main">
          <button v-if="photos.length > 1" class="lightbox-nav lightbox-prev" @click.stop="prevPhoto">
            <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="15 18 9 12 15 6"/></svg>
          </button>
          <img v-if="selectedPhoto?.url" :src="selectedPhoto.url" :alt="selectedPhoto.title" class="lightbox-image" />
          <button v-if="photos.length > 1" class="lightbox-nav lightbox-next" @click.stop="nextPhoto">
            <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="9 18 15 12 9 6"/></svg>
          </button>
        </div>
        <div class="lightbox-info">
          <h3>{{ selectedPhoto?.title }}</h3>
          <p v-if="selectedPhoto?.description">{{ selectedPhoto.description }}</p>
          <div class="lightbox-meta">
            <span v-if="selectedPhoto?.location">{{ selectedPhoto.location }}</span>
            <span v-if="selectedPhoto?.photoDate">{{ selectedPhoto.photoDate }}</span>
          </div>
          <button v-if="selectedPhoto" class="download-btn" @click="downloadPhoto(selectedPhoto)">下载原图</button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import WordsPullUp from '@/components/WordsPullUp.vue'
import { getPhotos, getTags, getPhotoDownloadUrl } from '@/api/album'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const photos = ref([])
const tags = ref([])
const activeTag = ref('')
const loading = ref(true)
const lightboxVisible = ref(false)
const currentIndex = ref(0)

const selectedPhoto = computed(() => photos.value[currentIndex.value] || null)

async function loadPhotos() {
  loading.value = true
  try {
    const params = { status: 1 }
    if (activeTag.value) params.tag = activeTag.value
    const res = await getPhotos(params)
    if (res.code == 200) photos.value = res.data || []
  } catch (e) { console.error(e) }
  finally { loading.value = false }
}

async function loadTags() {
  try {
    const res = await getTags()
    if (res.code == 200) tags.value = res.data || []
  } catch (e) { console.error(e) }
}

function filterByTag(tag) { activeTag.value = tag; loadPhotos() }
function openLightbox(index) { currentIndex.value = index; lightboxVisible.value = true }
function prevPhoto() { currentIndex.value = (currentIndex.value - 1 + photos.value.length) % photos.value.length }
function nextPhoto() { currentIndex.value = (currentIndex.value + 1) % photos.value.length }

async function downloadPhoto(photo) {
  try {
    const res = await getPhotoDownloadUrl(photo.photoId)
    if (res.code == 200 && res.data?.url) {
      const link = document.createElement('a')
      link.href = res.data.url
      link.rel = 'noopener'
      document.body.appendChild(link)
      link.click()
      link.remove()
    }
  } catch (e) {
    ElMessage.error('下载地址获取失败')
  }
}

function handleKeydown(e) {
  if (!lightboxVisible.value) return
  if (e.key === 'ArrowLeft') prevPhoto()
  else if (e.key === 'ArrowRight') nextPhoto()
}

onMounted(() => { loadPhotos(); loadTags(); window.addEventListener('keydown', handleKeydown) })
onUnmounted(() => { window.removeEventListener('keydown', handleKeydown) })
</script>

<style scoped>
.album-page { padding-top: 70px; min-height: 100vh; background: var(--color-bg-black); }
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

.tag-bar { display: flex; flex-wrap: wrap; gap: 8px; justify-content: center; padding: 0 16px 24px; max-width: 1100px; margin: 0 auto; }
@media (min-width: 768px) { .tag-bar { padding: 0 48px 32px; } }
.tag-btn { background: rgba(255,255,255,0.06); color: var(--color-gray-400); border: 1px solid rgba(255,255,255,0.08); border-radius: 999px; padding: 6px 16px; font-size: 13px; cursor: pointer; transition: all 0.3s; }
.tag-btn:hover { color: var(--color-primary-text); background: rgba(255,255,255,0.1); }
.tag-btn.active { color: #000; background: var(--color-primary); border-color: var(--color-primary); }

.album-content { padding: 0 16px 80px; max-width: 1100px; margin: 0 auto; }
@media (min-width: 768px) { .album-content { padding: 0 48px 120px; } }

/* Masonry layout */
.album-masonry { columns: 1; column-gap: 14px; }
@media (min-width: 640px) { .album-masonry { columns: 2; } }
@media (min-width: 1024px) { .album-masonry { columns: 3; } }

.masonry-item { break-inside: avoid; margin-bottom: 14px; animation: fadeSlideUp 0.5s ease forwards; opacity: 0; }

.photo-card { background: var(--color-bg-dark); border-radius: 12px; overflow: hidden; transition: transform 0.4s ease; cursor: pointer; }
.photo-card:hover { transform: translateY(-4px); }
.photo-image-wrap { overflow: hidden; background: var(--color-bg-card); }
.photo-image { width: 100%; height: auto; display: block; transition: transform 0.6s ease; }
.photo-card:hover .photo-image { transform: scale(1.05); }
.photo-placeholder { height: 220px; display: flex; align-items: center; justify-content: center; background: var(--color-bg-card); }
.photo-info { padding: 14px 18px; }
.photo-info h3 { font-size: 15px; font-weight: 600; color: var(--color-primary-text); margin-bottom: 4px; }
.photo-meta { display: flex; gap: 12px; font-size: 12px; color: var(--color-gray-500); margin-bottom: 6px; }
.photo-tags { display: flex; flex-wrap: wrap; gap: 4px; }
.photo-tag { font-size: 11px; color: var(--color-primary); background: rgba(222,219,200,0.08); padding: 2px 8px; border-radius: 999px; }

.album-loading, .album-empty { text-align: center; padding: 60px 16px; color: var(--color-gray-500); }
.album-empty { display: flex; flex-direction: column; align-items: center; gap: 12px; }

/* Lightbox */
.lightbox-wrapper { display: flex; flex-direction: column; gap: 12px; }
.lightbox-counter { text-align: center; font-size: 13px; color: var(--color-gray-400); letter-spacing: 2px; }
.lightbox-main { position: relative; display: flex; align-items: center; justify-content: center; }
.lightbox-image { width: 100%; max-height: 68vh; object-fit: contain; border-radius: 8px; }
.lightbox-nav {
  position: absolute; top: 50%; transform: translateY(-50%);
  background: rgba(0,0,0,0.45); backdrop-filter: blur(8px);
  color: rgba(255,255,255,0.9); border: 1px solid rgba(255,255,255,0.1);
  border-radius: 50%; width: 48px; height: 48px; cursor: pointer;
  display: flex; align-items: center; justify-content: center; z-index: 10;
  transition: all 0.3s;
}
.lightbox-nav:hover { background: rgba(0,0,0,0.7); border-color: rgba(255,255,255,0.3); }
.lightbox-prev { left: 8px; }
.lightbox-next { right: 8px; }
@media (max-width: 639px) { .lightbox-nav { width: 36px; height: 36px; } .lightbox-prev { left: 2px; } .lightbox-next { right: 2px; } }

.lightbox-info h3 { font-size: 18px; font-weight: 600; color: var(--color-primary-text); margin-bottom: 8px; }
.lightbox-info p { font-size: 14px; color: var(--color-gray-400); line-height: 1.6; margin-bottom: 8px; }
.lightbox-meta { display: flex; gap: 16px; font-size: 13px; color: var(--color-gray-500); }
.download-btn { margin-top: 14px; padding: 7px 16px; border-radius: 999px; border: 1px solid rgba(255,255,255,0.12); background: rgba(255,255,255,0.05); color: var(--color-primary-text); cursor: pointer; }
.download-btn:hover { background: rgba(255,255,255,0.1); }
:deep(.lightbox-dialog) { background: var(--color-bg-dark) !important; border-radius: 16px !important; }
:deep(.el-dialog__headerbtn .el-dialog__close) { color: var(--color-gray-400); }

@keyframes fadeSlideUp { from { opacity: 0; transform: translateY(24px); } to { opacity: 1; transform: translateY(0); } }
</style>
