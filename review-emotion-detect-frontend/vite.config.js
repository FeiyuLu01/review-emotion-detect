import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  base: process.env.VITE_ROUTER_BASE || '/', 
  build: {
    outDir: process.env.VITE_OUT_DIR || 'dist',             // ← 允许用环境变量覆盖
    emptyOutDir: true,                                      // 只清理各自的 outDir
  },
})
