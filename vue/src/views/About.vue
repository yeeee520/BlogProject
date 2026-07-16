<template>
  <div class="about-page">
    <section class="page-hero">
      <div class="page-hero-inner">
        <span class="page-label">关于我们</span>
        <h1 class="page-title">
          <WordsPullUp text="一个关于旅行、记录与生活美学的故事" wordClass="page-title-word" />
        </h1>
        <p class="page-subtitle">用脚步丈量世界，用文字记录时光。</p>
      </div>
    </section>
    <section class="about-content">
      <div class="about-grid">
        <div class="about-text">
          <h2 class="section-heading">我们的故事</h2>
          <p>
            CTRL Z 诞生于对旅行和记录的热爱。我们相信，每一次出发都是一次重新认识世界和自己的机会。
            从深山古村到海边日落，从城市秘密花园到热带雨林，我们用镜头和文字记录下每一个值得铭记的瞬间。
          </p>
          <p>
            这里不仅是一个博客，更是一个分享旅行灵感、摄影技巧和生活美学的平台。
            我们希望每一位读者都能在这里找到下一次出发的勇气和灵感。
          </p>
        </div>
        <div class="about-stats">
          <div class="stat-card">
            <span class="stat-number">50+</span>
            <span class="stat-label">旅行目的地</span>
          </div>
          <div class="stat-card">
            <span class="stat-number">200+</span>
            <span class="stat-label">摄影作品</span>
          </div>
          <div class="stat-card">
            <span class="stat-number">100+</span>
            <span class="stat-label">原创文章</span>
          </div>
          <div class="stat-card">
            <span class="stat-number">5000+</span>
            <span class="stat-label">忠实读者</span>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import WordsPullUp from '@/components/WordsPullUp.vue'
</script>

<style scoped>
.about-page {
  padding-top: 70px;
  min-height: 100vh;
  background: var(--color-bg-black);
}

.page-hero {
  padding: 60px 16px 40px;
  text-align: center;
}

@media (min-width: 768px) {
  .page-hero {
    padding: 100px 48px 60px;
  }
}

.page-label {
  color: var(--color-primary);
  font-size: 11px;
  letter-spacing: 3px;
  text-transform: uppercase;
}

.page-title {
  margin-top: 16px;
  font-size: 28px;
  font-weight: 700;
  color: var(--color-primary-text);
  line-height: 1.2;
}

@media (min-width: 768px) {
  .page-title {
    font-size: 42px;
  }
}

:deep(.page-title-word) {
  font-weight: inherit;
}

.page-subtitle {
  margin-top: 16px;
  font-size: 14px;
  color: var(--color-gray-400);
}

.about-content {
  padding: 0 16px 80px;
  max-width: 1100px;
  margin: 0 auto;
}

@media (min-width: 768px) {
  .about-content {
    padding: 0 48px 120px;
  }
}

.about-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 40px;
}

@media (min-width: 768px) {
  .about-grid {
    grid-template-columns: 1.5fr 1fr;
    gap: 60px;
  }
}

.about-text h2 {
  font-size: 22px;
  font-weight: 700;
  color: var(--color-primary-text);
  margin-bottom: 20px;
}

.about-text p {
  font-size: 15px;
  line-height: 1.8;
  color: var(--color-gray-400);
  margin-bottom: 16px;
}

.about-stats {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  align-content: start;
}

.stat-card {
  background: var(--color-bg-dark);
  border-radius: 12px;
  padding: 28px 20px;
  text-align: center;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.stat-number {
  font-size: 28px;
  font-weight: 800;
  color: var(--color-primary);
}

.stat-label {
  font-size: 13px;
  color: var(--color-gray-500);
}
</style>
