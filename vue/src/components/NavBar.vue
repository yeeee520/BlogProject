<template>
  <header class="navbar" :class="{ scrolled: isScrolled }">
    <div class="navbar-inner">
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
          <span>{{ item.label }}</span>
        </router-link>

        <template v-if="authStore.isLoggedIn">
          <el-dropdown trigger="click" class="user-dropdown" @command="handleCommand">
            <span class="user-btn">
              <el-avatar :size="32" :src="authStore.user?.avatar" />
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
          <span>登录</span>
        </router-link>
      </nav>
    </div>
  </header>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from "vue"
import { useRoute, useRouter } from "vue-router"
import { useAuthStore } from "@/stores/auth"

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const isScrolled = ref(false)

const navItems = [
  { path: "/home", label: "首页" },
  { path: "/about", label: "我们" },
  { path: "/travels", label: "游记" },
  { path: "/album", label: "相册" },
  { path: "/plan", label: "计划" },
  { path: "/toolbox", label: "百宝箱" },
]

function isActive(path) {
  return route.path === path
}

function handleCommand(cmd) {
  if (cmd === "logout") {
    authStore.logout()
    router.push("/manage")
  }
}

let handler = null
onMounted(() => {
  handler = () => { isScrolled.value = window.scrollY > 10 }
  window.addEventListener("scroll", handler)
})
onUnmounted(() => {
  if (handler) window.removeEventListener("scroll", handler)
})
</script>

<style scoped>
.navbar {
  position: fixed; top: 0; left: 0; right: 0; z-index: 1000;
  background: rgba(26,26,46,0.85);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border-bottom: 1px solid rgba(255,255,255,0.06);
  transition: all 0.4s cubic-bezier(0.25,0.46,0.45,0.94);
}
.navbar.scrolled {
  background: rgba(26,26,46,0.95);
  box-shadow: 0 4px 30px rgba(0,0,0,0.3);
}
.navbar-inner {
  max-width: 1400px; margin: 0 auto;
  display: flex; align-items: center; justify-content: space-between;
  padding: 0 48px; height: 64px;
}
.logo { text-decoration: none; flex-shrink: 0; }
.logo-text {
  font-size: 22px; font-weight: 700; letter-spacing: 2px;
  background: linear-gradient(135deg,#ff6b6b,#ffa94d,#ffd43b);
  -webkit-background-clip: text; -webkit-text-fill-color: transparent;
  background-clip: text;
}
.nav-links { display: flex; align-items: center; gap: 4px; }
.nav-item {
  display: flex; align-items: center; gap: 6px;
  padding: 8px 18px; border-radius: 10px;
  text-decoration: none; color: rgba(255,255,255,0.6);
  font-size: 14px; font-weight: 500;
  transition: all 0.35s cubic-bezier(0.25,0.46,0.45,0.94);
  position: relative; overflow: hidden;
}
.nav-item::before {
  content: ""; position: absolute; inset: 0; border-radius: 10px;
  background: rgba(255,255,255,0.08); opacity: 0;
  transition: opacity 0.35s ease;
}
.nav-item:hover { color: rgba(255,255,255,0.9); transform: translateY(-1px); }
.nav-item:hover::before { opacity: 1; }
.nav-item.active {
  color: #fff;
  background: rgba(255,107,107,0.2);
  box-shadow: 0 0 20px rgba(255,107,107,0.1);
}
.nav-item.active::after {
  content: ""; position: absolute; bottom: 4px; left: 50%;
  transform: translateX(-50%); width: 20px; height: 2px;
  background: linear-gradient(90deg,#ff6b6b,#ffa94d); border-radius: 2px;
}
.user-dropdown { margin-left: 8px; }
.user-btn { cursor: pointer; display: flex; align-items: center; }
.user-btn .el-avatar {
  transition: transform 0.35s cubic-bezier(0.25,0.46,0.45,0.94);
  border: 2px solid rgba(255,255,255,0.15);
}
.user-btn:hover .el-avatar { transform: scale(1.1); border-color: #ff6b6b; }
</style>
