<template>
    <div class="topbar container">
      <div class="left" @click="goHome">
        <img class="logo" src="/favicon.svg" alt="MoodLens" />
        <div class="brand">
          <span class="brand__name">MoodLens</span>
          <span class="brand__tag">Foster Digital Citizenship</span>
        </div>
      </div>
  
      <a-menu
        mode="horizontal"
        :selectedKeys="[selectedKey]"
        class="menu"
        @click="onMenuClick"
      >
        <a-menu-item key="/home">Home</a-menu-item>
        <a-menu-item key="/analyze">Analyze</a-menu-item>
        <a-menu-item key="/about">About</a-menu-item>
      </a-menu>
  
      <div class="right">
        <a-button type="primary" @click="goAnalyze">Start Analyzing</a-button>
      </div>
    </div>
  </template>
  
  <script setup>
  // Top navigation with animated brand and active route highlight
  import { computed } from 'vue'
  import { useRoute, useRouter } from 'vue-router'
  
  const route = useRoute()
  const router = useRouter()
  
  const selectedKey = computed(() => {
    const p = route.path
    if (p.startsWith('/analyze')) return '/analyze'
    if (p.startsWith('/about')) return '/about'
    return '/home'
  })
  
  function onMenuClick({ key }) {
    if (key !== route.path) router.push(key)
  }
  function goAnalyze() {
    router.push('/analyze')
  }
  function goHome() {
    router.push('/home')
  }
  </script>
  
  <style scoped>
  .topbar {
    height: 68px;
    display: grid;
    grid-template-columns: 1fr auto 1fr;
    align-items: center;
    gap: 12px;
  }
  
  .left {
    display: flex;
    align-items: center;
    gap: 12px;
    cursor: pointer;
  }
  .logo { width: 28px; height: 28px; }
  
  .brand { display: flex; flex-direction: column; line-height: 1.1; }
  .brand__name {
    font-weight: 800;
    letter-spacing: .2px;
    background: linear-gradient(90deg, #7C3AED, #06B6D4);
    -webkit-background-clip: text;
    background-clip: text;
    color: transparent;
    animation: shine 6s ease-in-out infinite alternate;
  }
  .brand__tag {
    font-size: 12px;
    color: #475569;
  }
  
  @keyframes shine {
    from { filter: drop-shadow(0 0 0 rgba(124,58,237,.0)); }
    to   { filter: drop-shadow(0 2px 8px rgba(6,182,212,.25)); }
  }
  
  .menu { justify-self: center; border-bottom: none; }
  .right { justify-self: end; }
  </style>