import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', redirect: '/manage' },
    { path: '/manage', component: () => import('@/views/Manager.vue') },
    { path: '/home', component: () => import('@/views/Home.vue') },
    { path: '/about', component: () => import('@/views/About.vue') },
    { path: '/travels', component: () => import('@/views/Travels.vue') },
    { path: '/travels/manage', component: () => import('@/views/TravelsAdmin.vue'), meta: { requiresAdmin: true } },
    { path: '/album', component: () => import('@/views/Album.vue') },
    { path: '/album/manage', component: () => import('@/views/AlbumAdmin.vue'), meta: { requiresAdmin: true } },
    { path: '/plan', component: () => import('@/views/Plan.vue') },
    { path: '/plan/manage', component: () => import('@/views/PlanAdmin.vue'), meta: { requiresAdmin: true } },
    { path: '/toolbox', component: () => import('@/views/Toolbox.vue') },
    { path: '/login', component: () => import('@/views/Login.vue') },
    { path: '/post/:id', component: () => import('@/views/PostDetail.vue') },
    { path: '/notfound', component: () => import('@/views/404.vue') },
    { path: '/:pathMatch(.*)', redirect: '/notfound' },
  ],
})

export default router
