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
function goHome() {
  router.push('/manage')
}
</script>

<style scoped>
.notfound-page { min-height: calc(100vh - 64px); margin-top: 64px; display: flex; align-items: center; justify-content: center; background: #f5f7fa; }
.notfound-content { text-align: center; }
.notfound-code { font-size: 100px; font-weight: 800; background: linear-gradient(135deg,#ff6b6b,#ffa94d); -webkit-background-clip: text; -webkit-text-fill-color: transparent; background-clip: text; line-height: 1; margin-bottom: 16px; }
.notfound-text { font-size: 18px; color: #8e8ea0; margin-bottom: 32px; }
</style>
