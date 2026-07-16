<template>
  <div class="notfound-page">
    <div class="notfound-content">
      <div class="notfound-code">404</div>
      <p class="notfound-text">页面不存在</p>
      <el-button type="primary" size="large" @click="goHome">返回首页</el-button>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
const router = useRouter()
function goHome() { router.push('/manage') }
</script>

<style scoped>
.notfound-page { min-height: 100vh; display: flex; align-items: center; justify-content: center; background: var(--color-bg-black); padding-top: 64px; }
.notfound-content { text-align: center; }
.notfound-code { font-size: 100px; font-weight: 800; color: var(--color-primary); opacity: 0.3; line-height: 1; margin-bottom: 16px; }
.notfound-text { font-size: 18px; color: var(--color-gray-400); margin-bottom: 32px; }
</style>
