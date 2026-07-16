<template>
  <div class="plan-page">
    <section class="page-hero">
      <span class="page-label">未来计划</span>
      <h1 class="page-title"><WordsPullUp text="每一次出发都有计划" wordClass="page-title-word" /></h1>
      <p class="page-subtitle">用目标驱动旅程，用计划丈量远方。</p>
    </section>

    <div v-if="authStore.isLoggedIn" class="admin-bar">
      <router-link to="/plan/manage" class="admin-link-btn">
        <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 20h9"/><path d="M16.5 3.5a2.121 2.121 0 0 1 3 3L7 19l-4 1 1-4 12.5-12.5z"/></svg>
        管理计划
      </router-link>
    </div>

    <section class="plan-content">
      <div v-if="loading" class="loading-hint"><p>加载中...</p></div>
      <div v-else-if="plans.length === 0" class="empty-hint">
        <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1" stroke-linecap="round" stroke-linejoin="round" style="color: var(--color-gray-500)"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
        <p>暂无计划</p>
      </div>
      <div v-else class="plan-list">
        <div v-for="item in plans" :key="item.planId" class="plan-card">
          <div v-if="item.coverUrl" class="plan-cover">
            <img :src="item.coverUrl" :alt="item.title" class="plan-cover-image" loading="lazy" />
          </div>
          <div class="plan-body">
            <div class="plan-status" :class="item.status">{{ statusTextMap[item.status] || item.status }}</div>
            <h3 class="plan-title">{{ item.title }}</h3>
            <p class="plan-desc">{{ item.description }}</p>
            <span class="plan-date">{{ item.planDate }}</span>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getPlans } from '@/api/plan'
import { useAuthStore } from '@/stores/auth'
import WordsPullUp from '@/components/WordsPullUp.vue'

const authStore = useAuthStore()
const plans = ref([])
const loading = ref(true)

const statusTextMap = {
  upcoming: '即将到来',
  planning: '规划中',
  dreaming: '愿望清单',
  completed: '已完成'
}

async function loadPlans() {
  loading.value = true
  try {
    const res = await getPlans()
    if (res.code == 200) plans.value = res.data || []
  } catch (e) { console.error(e) }
  finally { loading.value = false }
}

onMounted(() => { loadPlans() })
</script>

<style scoped>
.plan-page { padding-top: 70px; min-height: 100vh; background: var(--color-bg-black); }
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

.plan-content { padding: 0 16px 80px; max-width: 800px; margin: 0 auto; }
@media (min-width: 768px) { .plan-content { padding: 0 48px 120px; } }

.loading-hint, .empty-hint { text-align: center; padding: 60px 16px; color: var(--color-gray-500); }
.empty-hint { display: flex; flex-direction: column; align-items: center; gap: 12px; }

.plan-list { display: flex; flex-direction: column; gap: 16px; }
.plan-card {
  background: var(--color-bg-dark); border-radius: 12px; overflow: hidden;
  border: 1px solid rgba(222,219,200,0.06); transition: transform 0.4s ease;
}
.plan-card:hover { transform: translateY(-2px); }
.plan-cover { overflow: hidden; background: var(--color-bg-card); }
.plan-cover-image { width: 100%; height: 180px; object-fit: cover; display: block; }
@media (min-width: 768px) { .plan-cover-image { height: 220px; } }
.plan-body { padding: 24px 28px; }
.plan-status { display: inline-block; font-size: 11px; padding: 4px 12px; border-radius: 999px; margin-bottom: 12px; }
.plan-status.upcoming { background: rgba(222,219,200,0.15); color: var(--color-primary); }
.plan-status.planning { background: rgba(107,114,128,0.15); color: var(--color-gray-400); }
.plan-status.dreaming { background: rgba(107,114,128,0.1); color: var(--color-gray-500); }
.plan-status.completed { background: rgba(74,222,128,0.15); color: #4ade80; }
.plan-title { font-size: 18px; font-weight: 600; color: var(--color-primary-text); margin-bottom: 8px; }
.plan-desc { font-size: 14px; color: var(--color-gray-400); line-height: 1.7; margin-bottom: 12px; }
.plan-date { font-size: 12px; color: var(--color-gray-500); }
</style>

