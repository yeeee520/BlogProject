<template>
  <NavBar />
  <main class="main-content">
    <RouterView />
  </main>
</template>

<script setup>
import NavBar from '@/components/NavBar.vue'
</script>

<style>
* { margin: 0; padding: 0; box-sizing: border-box; }
html, body { height: 100%; font-family: 'Inter','PingFang SC','Microsoft YaHei',sans-serif; background: #f5f7fa; color: #1a1a2e; }
#app { min-height: 100vh; display: flex; flex-direction: column; }
.main-content { flex: 1; display: flex; flex-direction: column; }
</style>
