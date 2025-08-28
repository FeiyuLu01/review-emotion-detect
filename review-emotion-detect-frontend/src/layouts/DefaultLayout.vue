<template>
    <a-layout class="layout">
      <a-layout-header class="layout__header">
        <TopBar />
      </a-layout-header>
  
      <a-layout-content class="layout__content">
        <div class="container">
          <slot />
        </div>
      </a-layout-content>
  
      <a-layout-footer class="layout__footer">
        <FooterBar />
      </a-layout-footer>
    </a-layout>
  </template>
  
  <script setup>
  // Site chrome: TopBar, content area with container, footer
  import TopBar from '@/components/common/TopBar.vue'
  import FooterBar from '@/components/common/FooterBar.vue'
  </script>
  
  <style scoped>
  .layout {
    min-height: 100vh;
    position: relative;
  }
  .layout__header {
    position: sticky;
    top: 0;
    z-index: 10;
    backdrop-filter: saturate(160%) blur(10px);
    background: rgba(255, 255, 255, .55);
    border-bottom: 1px solid rgba(15, 23, 42, .06);
  }
  .layout__content {
    padding: 32px 0 48px 0;
  }
  .layout__footer {
    background: transparent;
    padding: 12px 0 32px 0;
  }
  </style>