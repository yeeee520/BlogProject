<template>
  <div class="landing-page">
    <!-- Hero Section -->
    <section class="hero">
      <div class="hero-inner">
        <div class="hero-bg">
          <div class="hero-gradient"></div>
          <div class="noise-overlay"></div>
        </div>

        <!-- Hero Content -->
        <div class="hero-logo">
          <WordsPullUp text="CTRL Z" wordClass="hero-word" />
        </div>

        <div class="hero-content">
          <div class="hero-bottom">
            <p
              class="hero-desc"
              :style="{
                opacity: heroDescVisible ? 1 : 0,
                transform: heroDescVisible ? 'translateY(0)' : 'translateY(20px)',
              }"
            >
              用脚步丈量世界，用文字记录时光。这里是一段关于旅行、摄影与生活美学的私人日志。
            </p>
            <button
              class="hero-cta"
              @click="scrollToAbout"
              :style="{
                opacity: heroBtnVisible ? 1 : 0,
                transform: heroBtnVisible ? 'translateY(0)' : 'translateY(20px)',
              }"
            >
              <span>探索内容</span>
              <span class="cta-circle">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="5" y1="12" x2="19" y2="12"/><polyline points="12 5 19 12 12 19"/></svg>
              </span>
            </button>
          </div>
        </div>
      </div>
    </section>

    <!-- About Section -->
    <section id="about-section" class="about-section">
      <div class="about-card">
        <span class="about-label">关于我们</span>
        <h2 class="about-heading">
          <WordsPullUp text="一个关于旅行、记录" wordClass="about-word" />
          <br />
          <span class="font-serif-italic"><WordsPullUp text="与生活美学" wordClass="about-word-italic" /></span>
          <br />
          <WordsPullUp text="的私人日志。" wordClass="about-word" />
        </h2>
        <p
          class="about-body"
          ref="aboutBodyRef"
        >
          <span
            v-for="(char, i) in aboutChars"
            :key="i"
            class="animated-char"
            :style="{ opacity: charOpacity[i] }"
          >{{ char }}</span>
        </p>
      </div>
    </section>

    <!-- Features Section -->
    <section class="features-section bg-noise">
      <div class="features-inner">
        <div class="features-header">
          <h2 class="features-title">
            <WordsPullUp text="精选板块，为你而设。" wordClass="features-title-word" />
          </h2>
          <p class="features-subtitle">记录每一次出发，收藏每一份感动。</p>
        </div>
        <div class="features-grid">
          <router-link
            v-for="(card, i) in featureCards"
            :key="card.title"
            :to="card.path"
            class="feature-card"
            :style="{
              transitionDelay: cardsVisible ? (i * 0.15) + 's' : '0s',
              opacity: cardsVisible ? 1 : 0,
              transform: cardsVisible ? 'scale(1)' : 'scale(0.95)',
            }"
          >
            <div class="card-icon">
              <component :is="card.icon" :size="24" />
            </div>
            <span class="card-number">{{ String(i + 1).padStart(2, '0') }}</span>
            <h3 class="card-title">{{ card.title }}</h3>
            <ul class="card-list">
              <li v-for="item in card.items" :key="item">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round" class="check-icon"><polyline points="20 6 9 17 4 12"/></svg>
                <span>{{ item }}</span>
              </li>
            </ul>
            <span class="card-link">
              探索更多
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="transform: rotate(-45deg)"><line x1="5" y1="12" x2="19" y2="12"/><polyline points="12 5 19 12 12 19"/></svg>
            </span>
          </router-link>
        </div>
      </div>
    </section>

    <!-- Posts Preview Section -->
    <section id="posts" class="posts-section">
      <div class="posts-inner">
        <h2 class="posts-heading">最新文章</h2>
        <div class="posts-grid">
          <article
            v-for="post in posts"
            :key="post.id"
            class="post-card"
            @click="goPost(post.id)"
          >
            <h3 class="post-title">{{ post.title }}</h3>
            <p class="post-summary">{{ post.summary }}</p>
            <div class="post-meta">
              <span>{{ post.date }}</span>
              <span>{{ post.readTime }}</span>
            </div>
          </article>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, h } from 'vue'
import { useRouter } from 'vue-router'
import { getPostsList } from '@/api/post'
import WordsPullUp from '@/components/WordsPullUp.vue'

const router = useRouter()
const posts = ref([])
const heroDescVisible = ref(false)
const heroBtnVisible = ref(false)
const cardsVisible = ref(false)
const aboutBodyRef = ref(null)
const charScrollProgress = ref(0)

function goPost(id) {
  router.push('/post/' + id)
}

let animationId = null
let scrollTimeout = null
let isParticleRunning = false
const particles = []

function scrollToAbout() {
  const target = document.getElementById('about-section')
  if (!target) return
  target.scrollIntoView({ behavior: 'smooth' })
}

function spawnParticles(count) {
  const canvas = particleCanvas.value
  if (!canvas) return
  for (let i = 0; i < count; i++) {
    particles.push({
      x: Math.random() * canvas.width,
      y: canvas.height + Math.random() * 40,
      vx: (Math.random() - 0.5) * 0.6,
      vy: -(2 + Math.random() * 3.5),
      size: 1 + Math.random() * 1.5,
      alpha: 0.3 + Math.random() * 0.5,
      trail: 5 + Math.random() * 10,
    })
  }
}

function startParticleLoop() {
  if (isParticleRunning) return
  isParticleRunning = true
  const canvas = particleCanvas.value
  if (!canvas) return
  const ctx = canvas.getContext('2d')
  canvas.width = window.innerWidth
  canvas.height = window.innerHeight
  canvas.style.display = 'block'

  function draw() {
    ctx.clearRect(0, 0, canvas.width, canvas.height)
    for (let i = particles.length - 1; i >= 0; i--) {
      const p = particles[i]
      p.x += p.vx
      p.y += p.vy
      p.alpha *= 0.988
      if (p.y < -30 || p.alpha < 0.015) { particles.splice(i, 1); continue }
      ctx.beginPath()
      ctx.moveTo(p.x, p.y)
      ctx.lineTo(p.x - p.vx * p.trail, p.y - p.vy * p.trail)
      const grad = ctx.createLinearGradient(p.x, p.y, p.x - p.vx * p.trail, p.y - p.vy * p.trail)
      grad.addColorStop(0, 'rgba(222,219,200,' + p.alpha + ')')
      grad.addColorStop(1, 'rgba(222,219,200,0)')
      ctx.strokeStyle = grad
      ctx.lineWidth = p.size
      ctx.lineCap = 'round'
      ctx.stroke()
      ctx.beginPath()
      ctx.arc(p.x, p.y, p.size * 0.7, 0, Math.PI * 2)
      ctx.fillStyle = 'rgba(222,219,200,' + (p.alpha * 0.8) + ')'
      ctx.fill()
    }
    if (particles.length > 0) {
      animationId = requestAnimationFrame(draw)
    } else {
      isParticleRunning = false
      ctx.clearRect(0, 0, canvas.width, canvas.height)
      canvas.style.display = 'none'
    }
  }
  draw()
}

function onScroll() {
  spawnParticles(1 + Math.floor(Math.random() * 2))
  startParticleLoop()
  clearTimeout(scrollTimeout)
  scrollTimeout = setTimeout(() => { /* particles fade naturally */ }, 300)
}

const aboutText = '在过去几年里，我走过了山川湖海，用镜头捕捉每一个值得铭记的瞬间。从深山古村到海边日落，从城市秘密花园到热带雨林，每一次出发都是对自我的重新发现。'
const aboutChars = computed(() => aboutText.split(''))
const charOpacity = computed(() => {
  return aboutChars.value.map((_, i) => {
    const progress = i / aboutChars.value.length
    const start = Math.max(0, progress - 0.1)
    const end = Math.min(1, progress + 0.05)
    if (charScrollProgress.value <= start) return 0.2
    if (charScrollProgress.value >= end) return 1
    return 0.2 + ((charScrollProgress.value - start) / (end - start)) * 0.8
  })
})

/* simple icon components */
const MapIcon = { render() { return h('svg', { width: 24, height: 24, viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', 'stroke-width': 2, 'stroke-linecap': 'round', 'stroke-linejoin': 'round' }, [h('polygon', { points: '1 6 1 22 8 18 16 22 23 18 23 2 16 6 8 2 1 6' }), h('line', { x1: 8, y1: 2, x2: 8, y2: 18 }), h('line', { x1: 16, y1: 6, x2: 16, y2: 22 })]) } }
const ImageIcon = { render() { return h('svg', { width: 24, height: 24, viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', 'stroke-width': 2, 'stroke-linecap': 'round', 'stroke-linejoin': 'round' }, [h('rect', { x: 3, y: 3, width: 18, height: 18, rx: 2, ry: 2 }), h('circle', { cx: 8.5, cy: 8.5, r: 1.5 }), h('polyline', { points: '21 15 16 10 5 21' })]) } }
const CalendarIcon = { render() { return h('svg', { width: 24, height: 24, viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', 'stroke-width': 2, 'stroke-linecap': 'round', 'stroke-linejoin': 'round' }, [h('rect', { x: 3, y: 4, width: 18, height: 18, rx: 2, ry: 2 }), h('line', { x1: 16, y1: 2, x2: 16, y2: 6 }), h('line', { x1: 8, y1: 2, x2: 8, y2: 6 }), h('line', { x1: 3, y1: 10, x2: 21, y2: 10 })]) } }
const ToolIcon = { render() { return h('svg', { width: 24, height: 24, viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', 'stroke-width': 2, 'stroke-linecap': 'round', 'stroke-linejoin': 'round' }, [h('path', { d: 'M14.7 6.3a1 1 0 0 0 0 1.4l1.6 1.6a1 1 0 0 0 1.4 0l3.77-3.77a6 6 0 0 1-7.94 7.94l-6.91 6.91a2.12 2.12 0 0 1-3-3l6.91-6.91a6 6 0 0 1 7.94-7.94l-3.76 3.76z' })]) } }

const featureCards = [
  {
    title: '旅行日志',
    path: '/travels',
    icon: MapIcon,
    items: ['深度游记分享', '路线规划灵感', '旅途摄影记录', '实用攻略指南'],
  },
  {
    title: '影像相册',
    path: '/album',
    icon: ImageIcon,
    items: ['精选摄影作品', '风光与人文', '后期处理技巧'],
  },
  {
    title: '未来计划',
    path: '/plan',
    icon: CalendarIcon,
    items: ['旅行愿望清单', '年度目标追踪', '行程规划安排'],
  },
  {
    title: '百宝箱',
    path: '/toolbox',
    icon: ToolIcon,
    items: ['常用工具推荐', '效率提升技巧', '实用资源整合'],
  },
]

onMounted(async () => {
  /* stagger hero animations */
  setTimeout(() => { heroDescVisible.value = true }, 500)
  setTimeout(() => { heroBtnVisible.value = true }, 700)

  /* feature cards entrance */
  const cardsObserver = new IntersectionObserver(
    ([entry]) => {
      if (entry.isIntersecting) {
        cardsVisible.value = true
        cardsObserver.disconnect()
      }
    },
    { threshold: 0.1, rootMargin: '-100px' }
  )
  const featEl = document.querySelector('.features-grid')
  if (featEl) cardsObserver.observe(featEl)


  /* about scroll-linked chars */


  function onScroll() {
    if (!aboutBodyRef.value) return
    const rect = aboutBodyRef.value.getBoundingClientRect()
    const windowH = window.innerHeight
    const start = windowH * 0.8
    const end = windowH * 0.2
    const progress = (start - rect.top) / (start - end)
    charScrollProgress.value = Math.max(0, Math.min(1, progress))
  }

  /* load posts */
  try {
    const res = await getPostsList()
    if (res.code === 200) {
      posts.value = (res.data.list || []).slice(0, 3)
    }
  } catch (e) {
    console.error(e)
  }
})
</script>

<style scoped>
/* ========= HERO ========= */
.landing-page {
  background: var(--color-bg-black);
  padding-top: 0;
}

.hero {
  position: relative;
  height: 100vh;
  min-height: 600px;
  padding: 12px;
}

@media (min-width: 768px) {
  .hero {
    padding: 16px;
  }
}

.hero-inner {
  position: relative;
  width: 100%;
  height: 100%;
  border-radius: 12px;
  overflow: hidden;
}

@media (min-width: 768px) {
  .hero-inner {
    border-radius: 28px;
  }
}

.hero-bg {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 30%, #0f3460 60%, #1a1a2e 100%);
}

.hero-gradient {
  position: absolute;
  inset: 0;
  background: linear-gradient(to bottom, rgba(0, 0, 0, 0.3) 0%, transparent 40%, rgba(0, 0, 0, 0.6) 100%);
  z-index: 2;
}

.hero-logo {
  position: absolute;
  top: 32px;
  left: 28px;
  z-index: 10;
}

@media (min-width: 768px) {
  .hero-logo {
    top: 48px;
    left: 48px;
  }
}

.hero-content {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 3;
  padding: 40px 28px;
  display: grid;
  grid-template-columns: 1fr;
  gap: 24px;
}

@media (min-width: 768px) {
  .hero-content {
    padding: 60px 48px;
    grid-template-columns: 2fr 1fr;
    align-items: end;
  }
}

.hero-title {
  font-size: 26vw;
  font-weight: 500;
  line-height: 0.85;
  letter-spacing: -0.07em;
  color: var(--color-primary-text);
}

@media (min-width: 640px) { .hero-title { font-size: 24vw; } }
@media (min-width: 768px) { .hero-title { font-size: 22vw; } }
@media (min-width: 1024px) { .hero-title { font-size: 20vw; } }
@media (min-width: 1280px) { .hero-title { font-size: 19vw; } }
@media (min-width: 1536px) { .hero-title { font-size: 20vw; } }

:deep(.hero-word) {
  font-size: 28px;
  font-weight: 700;
  line-height: 1;
  letter-spacing: 8px;
  color: var(--color-primary);
}

@media (min-width: 768px) {
  :deep(.hero-word) {
    font-size: 36px;
    letter-spacing: 10px;
  }
}

.hero-bottom {
  display: flex;
  flex-direction: column;
  gap: 20px;
  align-self: end;
}

.hero-desc {
  color: var(--color-primary);
  opacity: 0.7;
  font-size: 12px;
  line-height: 1.6;
  max-width: 320px;
  transition: opacity 0.8s cubic-bezier(0.16, 1, 0.3, 1), transform 0.8s cubic-bezier(0.16, 1, 0.3, 1);
}

@media (min-width: 640px) { .hero-desc { font-size: 14px; } }
@media (min-width: 768px) { .hero-desc { font-size: 16px; } }

.hero-cta {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: var(--color-primary);
  color: #000;
  border-radius: 999px;
  padding: 10px 20px;
  font-size: 14px;
  font-weight: 500;
  text-decoration: none;
  width: fit-content;
  transition: opacity 0.8s cubic-bezier(0.16, 1, 0.3, 1), transform 0.8s cubic-bezier(0.16, 1, 0.3, 1), gap 0.3s ease;
}

@media (min-width: 640px) {
  .hero-cta {
    font-size: 16px;
    padding: 12px 24px;
  }
}

.hero-cta:hover {
  gap: 12px;
  color: #000;
}

.cta-circle {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  background: #000;
  border-radius: 50%;
  color: var(--color-primary);
  transition: transform 0.3s ease;
}

@media (min-width: 640px) {
  .cta-circle {
    width: 36px;
    height: 36px;
  }
}

.hero-cta:hover .cta-circle {
  transform: scale(1.1);
}

/* ========= ABOUT ========= */
.about-section {
  padding: 80px 16px;
  background: var(--color-bg-black);
}

@media (min-width: 768px) {
  .about-section {
    padding: 120px 48px;
  }
}

.about-card {
  max-width: 960px;
  margin: 0 auto;
  background: var(--color-bg-dark);
  border-radius: 16px;
  padding: 48px 28px;
  text-align: center;
}

@media (min-width: 768px) {
  .about-card {
    padding: 80px 60px;
    border-radius: 24px;
  }
}

.about-label {
  color: var(--color-primary);
  font-size: 10px;
  letter-spacing: 2px;
  text-transform: uppercase;
}

@media (min-width: 640px) {
  .about-label {
    font-size: 12px;
  }
}

.about-heading {
  margin-top: 24px;
  font-size: 28px;
  font-weight: 400;
  line-height: 1.1;
  color: var(--color-primary-text);
}

@media (min-width: 640px) { .about-heading { font-size: 36px; } }
@media (min-width: 768px) { .about-heading { font-size: 48px; } }
@media (min-width: 1024px) { .about-heading { font-size: 60px; } }
@media (min-width: 1280px) { .about-heading { font-size: 72px; } }

:deep(.about-word) {
  font-weight: 400;
  line-height: inherit;
}

:deep(.about-word-italic) {
  font-family: var(--font-serif);
  font-style: italic;
  font-weight: 400;
  line-height: inherit;
}

.about-body {
  margin-top: 32px;
  font-size: 13px;
  line-height: 2;
  color: var(--color-primary);
  max-width: 640px;
  margin-left: auto;
  margin-right: auto;
}

@media (min-width: 640px) { .about-body { font-size: 14px; } }
@media (min-width: 768px) { .about-body { font-size: 16px; } }

.animated-char {
  transition: opacity 0.1s ease;
}

/* ========= FEATURES ========= */
.features-section {
  padding: 80px 12px;
  background: var(--color-bg-black);
  position: relative;
  overflow: hidden;
}

@media (min-width: 768px) {
  .features-section {
    padding: 120px 48px;
  }
}

.features-inner {
  max-width: 1200px;
  margin: 0 auto;
  position: relative;
  z-index: 2;
}

.features-header {
  text-align: center;
  margin-bottom: 48px;
}

.features-title {
  font-size: 20px;
  font-weight: 400;
  color: var(--color-primary-text);
}

@media (min-width: 640px) { .features-title { font-size: 24px; } }
@media (min-width: 768px) { .features-title { font-size: 28px; } }
@media (min-width: 1024px) { .features-title { font-size: 32px; } }

:deep(.features-title-word) {
  font-weight: inherit;
}

.features-subtitle {
  margin-top: 12px;
  font-size: 13px;
  color: var(--color-gray-500);
}

@media (min-width: 768px) {
  .features-subtitle {
    font-size: 16px;
  }
}

.features-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 12px;
}

@media (min-width: 768px) {
  .features-grid {
    grid-template-columns: 1fr 1fr;
  }
}

@media (min-width: 1024px) {
  .features-grid {
    grid-template-columns: repeat(4, 1fr);
    gap: 8px;
    height: auto;
  }
}

.feature-card {
  background: var(--color-bg-card);
  border-radius: 12px;
  padding: 28px 24px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  text-decoration: none;
  color: var(--color-primary-text);
  transition: opacity 0.8s cubic-bezier(0.22, 1, 0.36, 1), transform 0.8s cubic-bezier(0.22, 1, 0.36, 1), background 0.3s ease;
}

.feature-card:hover {
  background: #2a2a2a;
}

.card-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  background: rgba(222, 219, 200, 0.08);
  color: var(--color-primary);
}

.card-number {
  font-size: 11px;
  color: var(--color-gray-500);
  margin-top: 4px;
}

.card-title {
  font-size: 17px;
  font-weight: 600;
  color: var(--color-primary-text);
}

.card-list {
  list-style: none;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 8px;
  flex: 1;
}

.card-list li {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: var(--color-gray-400);
}

.check-icon {
  color: var(--color-primary);
  flex-shrink: 0;
}

.card-link {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: var(--color-primary);
  margin-top: auto;
  padding-top: 8px;
}

/* ========= POSTS ========= */
.posts-section {
  padding: 60px 16px 100px;
  background: var(--color-bg-black);
}

@media (min-width: 768px) {
  .posts-section {
    padding: 80px 48px 120px;
  }
}

.posts-inner {
  max-width: 1000px;
  margin: 0 auto;
}

.posts-heading {
  font-size: 22px;
  font-weight: 700;
  color: var(--color-primary-text);
  margin-bottom: 28px;
}

.posts-grid {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.post-card {
  background: var(--color-bg-dark);
  border-radius: 12px;
  padding: 24px 28px;
  cursor: pointer;
  transition: transform 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94), box-shadow 0.4s ease;
  border: 1px solid rgba(222, 219, 200, 0.06);
}

.post-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 12px 36px rgba(0, 0, 0, 0.3);
}

.post-title {
  font-size: 17px;
  font-weight: 600;
  color: var(--color-primary-text);
  margin-bottom: 8px;
  transition: color 0.3s ease;
}

.post-card:hover .post-title {
  color: var(--color-primary);
}

.post-summary {
  font-size: 14px;
  color: var(--color-gray-400);
  line-height: 1.7;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin-bottom: 12px;
}

.post-meta {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: var(--color-gray-500);
}

</style>
