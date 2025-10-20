import { createApp } from 'vue'
import { createPinia } from 'pinia'
import Antd, { ConfigProvider } from 'ant-design-vue'
import 'ant-design-vue/dist/reset.css'
import router from './router'
import App from './App.vue'
import './styles/index.css'
import themeTokens from './theme'

async function bootstrap() {
  

  const app = createApp(App)
  app.use(createPinia())
  app.use(router)
  app.use(Antd)

  // Provide AntD theme tokens globally
  app.component('ConfigProvider', ConfigProvider)
  app.provide('themeTokens', themeTokens)

  app.mount('#app')
}

bootstrap()