// Router config with dynamic document title per route (history mode)
// If building an archived snapshot under a subpath (e.g. /iteration1/),
// set: VITE_ROUTER_BASE=/iteration1/

import { createRouter, createWebHistory } from 'vue-router'
import { isAuthed } from '@/utils/auth'

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
  scrollBehavior() {
    return { top: 0 }
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

router.afterEach((to) => {
  const base = import.meta.env.VITE_APP_TITLE || 'MoodLens'
  document.title = to.meta?.title || base
})

export default router