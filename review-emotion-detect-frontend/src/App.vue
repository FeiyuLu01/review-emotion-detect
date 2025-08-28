<template>
  <!-- Wrap everything in Ant Design ConfigProvider for theme tokens -->
  <ConfigProvider :theme="{ token: themeTokens }">
    <DefaultLayout>
      <router-view />
    </DefaultLayout>
  </ConfigProvider>
</template>

<script setup>
// Root keeps minimal: provide layout and router-view
import { inject } from 'vue'
import DefaultLayout from '@/layouts/DefaultLayout.vue'

const themeTokens = inject('themeTokens')
</script>

<style scoped>
/* Keep root styles minimal, global styles in styles/index.css */
</style>