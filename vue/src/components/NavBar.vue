<template>
  <header class="navbar" :class="{ scrolled: isScrolled }">
    <div class="navbar-pill">
      <router-link to="/manage" class="logo">
        <span class="logo-text">CTRL Z</span>
      </router-link>
      <nav class="nav-links">
        <router-link
          v-for="item in navItems"
          :key="item.path"
          :to="item.path"
          class="nav-item"
          :class="{ active: isActive(item.path) }"
        >
          {{ item.label }}
        </router-link>
        <template v-if="authStore.isLoggedIn">
          <el-dropdown trigger="click" class="user-dropdown" @command="handleCommand">
            <span class="user-btn">
              <el-avatar :size="28" :src="authStore.user?.avatar" />
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">{{ authStore.user?.nickname || '用户' }}</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <router-link v-else to="/login" class="nav-item" :class="{ active: isActive('/login') }">
          登录
        </router-link>
      </nav>
    </div>
  </header>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const isScrolled = ref(false)

const navItems = [
  { path: '/manage', label: '首页' },
  { path: '/about', label: '我们' },
  { path: '/travels', label: '游记' },
  { path: '/album', label: '相册' },
  { path: '/plan', label: '计划' },
  { path: '/toolbox', label: '百宝箱' },
]

function isActive(path) {
  return route.path === path
}

async function handleCommand(cmd) {
  if (cmd === 'logout') {
    await authStore.logout()
    router.push('/manage')
  }
}

let handler = null
onMounted(() => {
  handler = () => { isScrolled.value = window.scrollY > 10 }
  window.addEventListener('scroll', handler)
})
onUnmounted(() => {
  if (handler) window.removeEventListener('scroll', handler)
})
</script>

<style scoped>
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  display: flex;
  justify-content: center;
  padding: 0 16px;
  pointer-events: none;
}

.navbar-pill {
  pointer-events: auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-top: 0;
  background: rgba(0, 0, 0, 0.85);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: 0 0 16px 16px;
  padding: 10px 24px;
  transition: all 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  width: 100%;
  max-width: 900px;
}

@media (min-width: 768px) {
  .navbar-pill {
    border-radius: 0 0 24px 24px;
    padding: 12px 40px;
    max-width: 1100px;
  }
}

.logo {
  text-decoration: none;
  flex-shrink: 0;
}

.logo-text {
  font-size: 18px;
  font-weight: 800;
  letter-spacing: 2px;
  color: var(--color-primary);
  transition: color 0.3s ease;
}

.logo:hover .logo-text {
  color: var(--color-primary-text);
}

.nav-links {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: nowrap;
  overflow-x: auto;
}

@media (min-width: 640px) {
  .nav-links {
    gap: 16px;
  }
}

@media (min-width: 768px) {
  .nav-links {
    gap: 28px;
  }
}

.nav-item {
  text-decoration: none;
  color: rgba(225, 224, 204, 0.7);
  font-size: 11px;
  font-weight: 400;
  white-space: nowrap;
  transition: color 0.3s ease;
  position: relative;
}

@media (min-width: 640px) {
  .nav-item {
    font-size: 13px;
  }
}

.nav-item:hover,
.nav-item.active {
  color: var(--color-primary-text);
}

.nav-item.active::after {
  content: '';
  position: absolute;
  bottom: -4px;
  left: 0;
  right: 0;
  height: 1px;
  background: var(--color-primary);
  border-radius: 1px;
}

.user-dropdown {
  margin-left: 4px;
}

.user-btn {
  cursor: pointer;
  display: flex;
  align-items: center;
}

.user-btn :deep(.el-avatar) {
  border: 1.5px solid rgba(222, 219, 200, 0.2);
  transition: border-color 0.3s ease;
}

.user-btn:hover :deep(.el-avatar) {
  border-color: var(--color-primary);
}
</style>
