<template>
  <div class="toolbox-page">
    <section class="page-hero">
      <span class="page-label">百宝箱</span>
      <h1 class="page-title"><WordsPullUp text="常用工具与实用资源" wordClass="page-title-word" /></h1>
      <p class="page-subtitle">精选工具推荐，助力效率提升。</p>
    </section>
    <section class="toolbox-content">
      <div class="tools-grid">
        <div v-for="(cat, i) in categories" :key="i" class="tool-category">
          <h2 class="cat-title">{{ cat.title }}</h2>
          <div class="cat-items">
            <div v-for="tool in cat.tools" :key="tool.name" class="tool-item">
              <div class="tool-icon">{{ tool.emoji }}</div>
              <div class="tool-info">
                <h3>{{ tool.name }}</h3>
                <p>{{ tool.desc }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import WordsPullUp from '@/components/WordsPullUp.vue'

const categories = [
  {
    title: '摄影后期',
    tools: [
      { name: 'Lightroom', emoji: '📷', desc: '专业照片后期处理与管理' },
      { name: 'Capture One', emoji: '🎞️', desc: '高端色彩管理与RAW处理' },
      { name: 'Photoshop', emoji: '🎨', desc: '图像合成与精修必备' },
    ],
  },
  {
    title: '旅行规划',
    tools: [
      { name: 'Google Maps', emoji: '🗺️', desc: '路线规划与离线地图' },
      { name: 'Notion', emoji: '📝', desc: '行程管理与旅行笔记' },
      { name: 'TripIt', emoji: '✈️', desc: '自动整理行程确认邮件' },
    ],
  },
  {
    title: '写作与记录',
    tools: [
      { name: 'Typora', emoji: '✍️', desc: '极简Markdown编辑器' },
      { name: 'Obsidian', emoji: '💎', desc: '知识管理与双向链接笔记' },
    ],
  },
]
</script>

<style scoped>
.toolbox-page { padding-top: 70px; min-height: 100vh; background: var(--color-bg-black); }
.page-hero { padding: 60px 16px 40px; text-align: center; }
@media (min-width: 768px) { .page-hero { padding: 100px 48px 60px; } }
.page-label { color: var(--color-primary); font-size: 11px; letter-spacing: 3px; text-transform: uppercase; }
.page-title { margin-top: 16px; font-size: 28px; font-weight: 700; color: var(--color-primary-text); line-height: 1.2; }
@media (min-width: 768px) { .page-title { font-size: 42px; } }
:deep(.page-title-word) { font-weight: inherit; }
.page-subtitle { margin-top: 16px; font-size: 14px; color: var(--color-gray-400); }
.toolbox-content { padding: 0 16px 80px; max-width: 900px; margin: 0 auto; }
@media (min-width: 768px) { .toolbox-content { padding: 0 48px 120px; } }
.tools-grid { display: flex; flex-direction: column; gap: 32px; }
.cat-title { font-size: 18px; font-weight: 700; color: var(--color-primary-text); margin-bottom: 16px; }
.cat-items { display: flex; flex-direction: column; gap: 12px; }
.tool-item { background: var(--color-bg-dark); border-radius: 12px; padding: 20px 24px; display: flex; align-items: center; gap: 16px; border: 1px solid rgba(222,219,200,0.06); transition: transform 0.3s ease; }
.tool-item:hover { transform: translateY(-2px); }
.tool-icon { font-size: 28px; width: 48px; height: 48px; display: flex; align-items: center; justify-content: center; background: var(--color-bg-card); border-radius: 10px; flex-shrink: 0; }
.tool-info h3 { font-size: 15px; font-weight: 600; color: var(--color-primary-text); margin-bottom: 4px; }
.tool-info p { font-size: 13px; color: var(--color-gray-400); }
</style>
