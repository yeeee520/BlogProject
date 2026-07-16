<template>
  <div class="home-page">
    <section class="page-hero">
      <span class="page-label">首页</span>
      <h1 class="page-title"><WordsPullUp text="欢迎来到 CTRL Z" wordClass="page-title-word" /></h1>
      <p class="page-subtitle">探索旅行、摄影与生活美学的精彩内容。</p>
    </section>
    <section class="home-content">
      <div class="home-grid">
        <router-link to="/travels" class="home-card">
          <div class="card-icon-wrap">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polygon points="1 6 1 22 8 18 16 22 23 18 23 2 16 6 8 2 1 6"/><line x1="8" y1="2" x2="8" y2="18"/><line x1="16" y1="6" x2="16" y2="22"/></svg>
          </div>
          <h3>旅行日志</h3>
          <p>探索世界各地的深度游记与旅行灵感。</p>
        </router-link>
        <router-link to="/album" class="home-card">
          <div class="card-icon-wrap">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="3" width="18" height="18" rx="2" ry="2"/><circle cx="8.5" cy="8.5" r="1.5"/><polyline points="21 15 16 10 5 21"/></svg>
          </div>
          <h3>影像相册</h3>
          <p>精选摄影作品，用镜头记录美好瞬间。</p>
        </router-link>
        <router-link to="/plan" class="home-card">
          <div class="card-icon-wrap">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
          </div>
          <h3>未来计划</h3>
          <p>旅行愿望清单与行程规划。</p>
        </router-link>
        <router-link to="/toolbox" class="home-card">
          <div class="card-icon-wrap">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14.7 6.3a1 1 0 0 0 0 1.4l1.6 1.6a1 1 0 0 0 1.4 0l3.77-3.77a6 6 0 0 1-7.94 7.94l-6.91 6.91a2.12 2.12 0 0 1-3-3l6.91-6.91a6 6 0 0 1 7.94-7.94l-3.76 3.76z"/></svg>
          </div>
          <h3>百宝箱</h3>
          <p>常用工具推荐与效率提升技巧。</p>
        </router-link>
      </div>
    </section>
  </div>
</template>

<script setup>
import WordsPullUp from '@/components/WordsPullUp.vue'
</script>

<style scoped>
.home-page { padding-top: 70px; min-height: 100vh; background: var(--color-bg-black); }
.page-hero { padding: 60px 16px 40px; text-align: center; }
@media (min-width: 768px) { .page-hero { padding: 100px 48px 60px; } }
.page-label { color: var(--color-primary); font-size: 11px; letter-spacing: 3px; text-transform: uppercase; }
.page-title { margin-top: 16px; font-size: 28px; font-weight: 700; color: var(--color-primary-text); line-height: 1.2; }
@media (min-width: 768px) { .page-title { font-size: 42px; } }
:deep(.page-title-word) { font-weight: inherit; }
.page-subtitle { margin-top: 16px; font-size: 14px; color: var(--color-gray-400); }
.home-content { padding: 0 16px 80px; max-width: 1000px; margin: 0 auto; }
@media (min-width: 768px) { .home-content { padding: 0 48px 120px; } }
.home-grid { display: grid; grid-template-columns: 1fr; gap: 16px; }
@media (min-width: 640px) { .home-grid { grid-template-columns: 1fr 1fr; } }
.home-card { background: var(--color-bg-dark); border-radius: 12px; padding: 32px 28px; text-decoration: none; color: var(--color-primary-text); transition: transform 0.4s ease, background 0.3s ease; border: 1px solid rgba(222,219,200,0.06); }
.home-card:hover { transform: translateY(-4px); background: var(--color-bg-card); }
.card-icon-wrap { width: 48px; height: 48px; display: flex; align-items: center; justify-content: center; background: rgba(222,219,200,0.08); border-radius: 10px; color: var(--color-primary); margin-bottom: 16px; }
.home-card h3 { font-size: 18px; font-weight: 600; margin-bottom: 8px; }
.home-card p { font-size: 14px; color: var(--color-gray-400); line-height: 1.6; }
</style>
