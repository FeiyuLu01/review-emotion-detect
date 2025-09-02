// Router config with dynamic document title per route
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
    component: () => import('@/views/Home.vue')
  },
  {
    path: '/analyze',
    name: 'Analyze',
    meta: { title: 'Analyze - MoodLens' },
    component: () => import('@/views/Analyze.vue')
  },
  {
    path: '/about',
    name: 'About',
    meta: { title: 'About - MoodLens' },
    component: () => import('@/views/About.vue')
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    meta: { title: 'Not Found - MoodLens' },
    component: () => import('@/views/NotFound.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    return { top: 0 }
  }
})

router.beforeEach((to, from, next) => {
  if (to.meta?.title) document.title = to.meta.title

  const isLoginRoute = to.path === '/login'
  if (isLoginRoute) return next()

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