import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', redirect: '/manage' },
    { path: '/manage', component: () => import('@/views/Manager.vue') },
    { path: '/home', component: () => import('@/views/Home.vue') },
    { path: '/about', component: () => import('@/views/About.vue') },
    { path: '/travels', component: () => import('@/views/Travels.vue') },
    { path: '/album', component: () => import('@/views/Album.vue') },
    { path: '/plan', component: () => import('@/views/Plan.vue') },
    { path: '/toolbox', component: () => import('@/views/Toolbox.vue') },
    { path: '/login', component: () => import('@/views/Login.vue') },
    { path: '/post/:id', component: () => import('@/views/PostDetail.vue') },
    { path: '/notfound', component: () => import('@/views/404.vue') },
    { path: '/:pathMatch(.*)', redirect: '/notfound' },
  ],
})

export default router
