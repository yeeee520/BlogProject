import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router'
import { useAuthStore } from '@/stores/auth'
import { AUTH_UNAUTHORIZED_EVENT } from '@/api/request'

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)

const authStore = useAuthStore(pinia)
authStore.initializeAuth()

router.beforeEach(async (to) => {
  if (!to.meta.requiresAdmin) return true
  await authStore.initializeAuth()
  if (authStore.isAdmin) return true
  return { path: '/login', query: { redirect: to.fullPath } }
})

window.addEventListener(AUTH_UNAUTHORIZED_EVENT, () => {
  authStore.clearAuth()
  const currentRoute = router.currentRoute.value
  if (currentRoute.meta.requiresAdmin && currentRoute.path !== '/login') {
    router.replace({ path: '/login', query: { redirect: currentRoute.fullPath } })
  }
})

app.use(router)
app.use(ElementPlus)

app.mount('#app')
