// Router config with dynamic document title per route (history mode)
// If building an archived snapshot under a subpath (e.g. /iteration1/),
// set: VITE_ROUTER_BASE=/iteration1/

import { createRouter, createWebHistory } from 'vue-router'
import { isAuthed } from '@/utils/auth'

const HEADER_OFFSET = 64
 function scrollToHash(hash, offset = HEADER_OFFSET) {
   const el = document.querySelector(hash)
   if (!el) return false
   const y = el.getBoundingClientRect().top + (window.pageYOffset || window.scrollY) - offset
   window.scrollTo({ top: y, behavior: 'smooth' })
   return true
 }

const routes = [
  { path: '/', redirect: '/home' },
  {
    path: '/login',
    name: 'Login',
    meta: { title: 'Login - MoodLens', requiresAuth: false },
    component: () => import('@/views/Login.vue'),
  },
  {
    path: '/home',
    name: 'Home',
    meta: { title: 'Home - MoodLens' },
    component: () => import('@/views/Home.vue'),
  },
  {
    path: '/analyze',
    name: 'Analyze',
    meta: { title: 'Analyze - MoodLens' },
    component: () => import('@/views/Analyze.vue'),
  },
  {
    path: '/about',
    name: 'About',
    meta: { title: 'About - MoodLens' },
    component: () => import('@/views/About.vue'),
  },
  {
    path: '/test',
    name: 'Test',
    meta: { title: 'Test - MoodLens' },
    component: () => import('@/views/Test.vue'),
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    meta: { title: 'Not Found - MoodLens' },
    component: () => import('@/views/NotFound.vue'),
  },
]

// --- history base ：default Vite  BASE_URL，or '/' ---
const routerBase =
  import.meta.env.VITE_ROUTER_BASE ?? import.meta.env.BASE_URL ?? '/'

const router = createRouter({
  history: createWebHistory(routerBase),
  routes,
  scrollBehavior(to, from, savedPosition) {
     // 返回浏览器后退前进的滚动位置
       if (savedPosition) return savedPosition
    
       // 如果带了 hash（如 #section-input），滚到该元素
       if (to.hash) {
         return {
           el: to.hash,
           // 如果有固定头部，留出偏移（px）
           top: HEADER_OFFSET,
           behavior: 'smooth',
         }
       }
    
       // 默认回到顶部
       return { left: 0, top: 0, behavior: 'smooth' }
     },
})

router.beforeEach((to, from, next) => {
  if (to.meta?.title) document.title = to.meta.title

  if (to.path === '/login') return next()

  if (!isAuthed()) {
    return next({ path: '/login', query: { next: to.fullPath } })
  }

  next()
})


export default router