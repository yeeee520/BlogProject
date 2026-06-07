<template>
  <div class="manage-page">
    <section class="pixel-hero">
      <div class="pixel-art-container">
        <svg class="pixel-art" viewBox="0 0 800 320" preserveAspectRatio="xMidYMid slice" xmlns="http://www.w3.org/2000/svg">
          <defs>
            <linearGradient id="skyGrad" x1="0" y1="0" x2="0" y2="1">
              <stop offset="0%" stop-color="#1a1a32"/>
              <stop offset="35%" stop-color="#2d1b4e"/>
              <stop offset="60%" stop-color="#6b3a5a"/>
              <stop offset="80%" stop-color="#c75b39"/>
              <stop offset="100%" stop-color="#e8a04a"/>
            </linearGradient>
          </defs>
          <rect width="800" height="320" fill="url(#skyGrad)"/>
          <rect x="580" y="40" width="40" height="40" rx="4" fill="#ffd43b" opacity="0.9"/>
          <rect x="590" y="30" width="20" height="10" fill="#ffd43b" opacity="0.6"/>
          <rect x="600" y="20" width="10" height="10" fill="#ffd43b" opacity="0.3"/>
          <rect x="50" y="140" width="120" height="120" fill="#2d1b4e" opacity="0.5"/>
          <rect x="130" y="110" width="140" height="150" fill="#3d2b5e" opacity="0.5"/>
          <rect x="270" y="100" width="100" height="160" fill="#2d1b4e" opacity="0.4"/>
          <rect x="350" y="130" width="100" height="130" fill="#3d2b5e" opacity="0.5"/>
          <rect x="440" y="80" width="140" height="180" fill="#2d1b4e" opacity="0.45"/>
          <rect x="560" y="110" width="90" height="150" fill="#3d2b5e" opacity="0.5"/>
          <rect x="650" y="70" width="130" height="190" fill="#2d1b4e" opacity="0.4"/>
          <rect x="0" y="220" width="800" height="100" fill="#1a3a1a"/>
          <rect x="0" y="200" width="800" height="20" fill="#2a5a2a"/>
          <rect x="100" y="190" width="8" height="30" fill="#8B4513"/>
          <rect x="190" y="185" width="8" height="35" fill="#8B4513"/>
          <rect x="320" y="188" width="8" height="32" fill="#8B4513"/>
          <rect x="450" y="182" width="8" height="38" fill="#8B4513"/>
          <rect x="550" y="187" width="8" height="33" fill="#8B4513"/>
          <rect x="700" y="184" width="8" height="36" fill="#8B4513"/>
          <rect x="80" y="158" width="40" height="40" rx="20" fill="#2a6a2a" opacity="0.8"/>
          <rect x="170" y="155" width="45" height="35" rx="18" fill="#3a7a3a" opacity="0.8"/>
          <rect x="300" y="156" width="38" height="36" rx="18" fill="#2a6a2a" opacity="0.8"/>
          <rect x="430" y="150" width="42" height="38" rx="19" fill="#3a7a3a" opacity="0.8"/>
          <rect x="530" y="157" width="44" height="34" rx="17" fill="#2a6a2a" opacity="0.8"/>
          <rect x="680" y="152" width="36" height="38" rx="18" fill="#3a7a3a" opacity="0.8"/>
          <rect x="200" y="215" width="6" height="6" fill="#ffd43b" opacity="0.3"/>
          <rect x="400" y="220" width="6" height="6" fill="#ffd43b" opacity="0.2"/>
          <rect x="600" y="218" width="6" height="6" fill="#ffd43b" opacity="0.25"/>
          <rect x="300" y="230" width="4" height="4" fill="#fff" opacity="0.15"/>
          <rect x="500" y="225" width="4" height="4" fill="#fff" opacity="0.15"/>
        </svg>
        <div class="pixel-mask"></div>
      </div>
    </section>

    <div class="content-wrapper">
      <div v-if="loading" class="content-loading">
        <el-skeleton :rows="8" animated style="max-width:800px;margin:0 auto" />
      </div>
      <div v-else class="content-inner">
        <aside class="profile-sidebar">
          <template v-if="authStore.isLoggedIn && profile">
            <div class="profile-card">
              <el-avatar :size="110" class="profile-avatar">
                <img :src="profile.avatar" alt="avatar"/>
              </el-avatar>
              <h2 class="profile-name">{{ profile.nickname }}</h2>
              <p class="profile-bio">{{ profile.bio }}</p>
              <div class="profile-stats">
                <div class="stat-item">
                  <span class="stat-value">{{ statsReady ? profile.postCount : "-" }}</span>
                  <span class="stat-label">帖子</span>
                </div>
                <div class="stat-item">
                  <span class="stat-value">{{ profile.followerCount ?? "-" }}</span>
                  <span class="stat-label">粉丝</span>
                </div>
                <div class="stat-item">
                  <span class="stat-value">{{ profile.followingCount ?? "-" }}</span>
                  <span class="stat-label">关注</span>
                </div>
              </div>
            </div>
          </template>
          <template v-else>
            <div class="profile-card profile-guest">
              <el-avatar :size="80" class="guest-avatar">?</el-avatar>
              <h2 class="profile-name">未登录</h2>
              <p class="profile-bio">登录后查看个人信息</p>
              <el-button type="primary" round @click="goLogin" style="margin-top:16px">立即登录</el-button>
            </div>
          </template>
        </aside>
        <main class="posts-main">
          <div class="posts-header">
            <h2 class="posts-title">最新文章</h2>
            <el-divider direction="horizontal" class="posts-divider"/>
          </div>
          <div class="posts-list">
            <article
              v-for="post in posts"
              :key="post.id"
              class="post-card"
              @click="goPost(post.id)"
            >
              <div class="post-content">
                <h3 class="post-title">{{ post.title }}</h3>
                <p class="post-summary">{{ post.summary }}</p>
                <div class="post-meta">
                  <span class="post-date">{{ post.date }}</span>
                  <span class="post-read">{{ post.readTime }}</span>
                </div>
              </div>
              <div class="post-arrow">
                <el-icon><ArrowRight /></el-icon>
              </div>
            </article>
          </div>
        </main>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue"
import { useRouter } from "vue-router"
import { ArrowRight } from "@element-plus/icons-vue"
import { useAuthStore } from "@/stores/auth"
import { getUserProfile } from "@/api/user"
import { getPostsList } from "@/api/post"

const router = useRouter()
const authStore = useAuthStore()

const posts = ref([])
const profile = ref(null)
const loading = ref(true)
const statsReady = ref(false)

function goLogin() { router.push('/login') }
function goPost(id) { router.push('/post/' + id) }

onMounted(async () => {
  try {
    const [postsRes, profileRes] = await Promise.all([
      getPostsList(),
      authStore.isLoggedIn ? getUserProfile() : Promise.resolve(null),
    ])
    if (postsRes.code === 200) {
      posts.value = postsRes.data.list
    }
    if (profileRes && profileRes.code === 200) {
      profile.value = profileRes.data
      setTimeout(() => { statsReady.value = true }, 100)
    } else if (authStore.user) {
      profile.value = authStore.user
      setTimeout(() => { statsReady.value = true }, 100)
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.manage-page { padding-top: 64px; }
.pixel-hero { position: relative; width: 100%; height: 320px; overflow: hidden; }
.pixel-art-container { position: relative; width: 100%; height: 100%; }
.pixel-art { display: block; width: 100%; height: 100%; object-fit: cover; }
.pixel-mask {
  position: absolute; top: 0; left: 0; right: 0; bottom: 0;
  pointer-events: none;
  background: linear-gradient(to bottom, rgba(26,26,46,0.95) 0%, rgba(26,26,46,0.45) 10%, transparent 22%);
}
.content-wrapper { max-width: 1400px; margin: 0 auto; padding: 0 48px 60px; position: relative; margin-top: -40px; z-index: 2; }
.content-loading { padding: 60px 24px; }
.content-inner { display: flex; gap: 40px; align-items: flex-start; }
.profile-sidebar { width: 300px; flex-shrink: 0; }
.profile-card {
  background: #fff; border-radius: 16px; padding: 36px 28px;
  text-align: center; box-shadow: 0 4px 24px rgba(0,0,0,0.06);
  transition: all 0.4s cubic-bezier(0.25,0.46,0.45,0.94);
}
.profile-card:hover { transform: translateY(-4px); box-shadow: 0 12px 40px rgba(0,0,0,0.1); }
.profile-avatar { margin-bottom: 20px; border: 3px solid #f0f0f5; transition: transform 0.5s cubic-bezier(0.25,0.46,0.45,0.94); }
.profile-card:hover .profile-avatar { transform: scale(1.05); }
.profile-name { font-size: 22px; font-weight: 700; color: #1a1a2e; margin-bottom: 8px; }
.profile-bio { font-size: 14px; color: #8e8ea0; line-height: 1.6; margin-bottom: 24px; }
.profile-stats { display: flex; justify-content: center; gap: 32px; padding-top: 20px; border-top: 1px solid #f0f0f5; }
.stat-item { display: flex; flex-direction: column; align-items: center; }
.stat-value { font-size: 20px; font-weight: 700; color: #1a1a2e; }
.stat-label { font-size: 12px; color: #a0a4b8; margin-top: 4px; }
.profile-guest { padding: 48px 28px; }
.guest-avatar { background: #e8e8f0; color: #a0a4b8; font-size: 28px; margin-bottom: 16px; }
.posts-main { flex: 1; min-width: 0; }
.posts-header { margin-bottom: 8px; }
.posts-title { font-size: 22px; font-weight: 700; color: #1a1a2e; }
.posts-divider { margin: 12px 0 20px; }
.posts-list { display: flex; flex-direction: column; gap: 16px; }
.post-card {
  background: #fff; border-radius: 14px; padding: 24px 28px;
  display: flex; align-items: center; justify-content: space-between;
  cursor: pointer; box-shadow: 0 2px 12px rgba(0,0,0,0.04);
  transition: all 0.4s cubic-bezier(0.25,0.46,0.45,0.94);
  position: relative; overflow: hidden;
}
.post-card::before {
  content: ""; position: absolute; left: 0; top: 0; bottom: 0;
  width: 4px; background: linear-gradient(180deg,#ff6b6b,#ffa94d);
  opacity: 0; transition: opacity 0.4s ease;
}
.post-card:hover { transform: translateY(-3px) scale(1.01); box-shadow: 0 12px 36px rgba(0,0,0,0.1); }
.post-card:hover::before { opacity: 1; }
.post-content { flex: 1; min-width: 0; }
.post-title { font-size: 17px; font-weight: 600; color: #1a1a2e; margin-bottom: 8px; transition: color 0.3s ease; }
.post-card:hover .post-title { color: #ff6b6b; }
.post-summary {
  font-size: 14px; color: #7c7c8a; line-height: 1.7;
  display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden;
  margin-bottom: 12px;
}
.post-meta { display: flex; gap: 16px; font-size: 12px; color: #b0b4c0; }
.post-arrow { flex-shrink: 0; margin-left: 20px; color: #c0c4d0; font-size: 18px; transition: all 0.35s cubic-bezier(0.25,0.46,0.45,0.94); }
.post-card:hover .post-arrow { color: #ff6b6b; transform: translateX(4px); }
@media (max-width: 960px) {
  .content-wrapper { padding: 0 24px 40px; }
  .content-inner { flex-direction: column; }
  .profile-sidebar { width: 100%; }
  .pixel-hero { height: 220px; }
}
</style>
