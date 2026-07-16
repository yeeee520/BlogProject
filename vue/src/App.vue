<template>
  <NavBar />
  <main class="main-content">
    <RouterView v-slot="{ Component }">
      <Transition name="page" mode="out-in">
        <component :is="Component" />
      </Transition>
    </RouterView>
  </main>
</template>

<script setup>
import NavBar from '@/components/NavBar.vue'
</script>

<style>
@import '@/assets/css/global.css';

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}
</style>