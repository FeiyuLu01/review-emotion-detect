// Pinia store placeholder for global flags and user context
import { createPinia, defineStore } from 'pinia'

export const pinia = createPinia()

export const useAppStore = defineStore('app', {
  state: () => ({
    initialized: false
  }),
  actions: {
    markInitialized() { this.initialized = true }
  }
})

export default pinia