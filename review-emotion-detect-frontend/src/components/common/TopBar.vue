<template>
  <div class="topbar">
    <div class="brand-section" @click="goHome">
      <div class="logo">
        <span class="logo-emoji">😊</span>
      </div>
      <div class="brand">
        <span class="brand__name">ToneLens</span>
        <span class="brand__tag">Find Review Emotion</span>
      </div>
    </div>

    <div class="nav-section">
      <a 
        href="/home" 
        class="nav-link" 
        :class="{ active: selectedKey === '/home' }"
        @click.prevent="onMenuClick('/home')"
      >
        Home
      </a>
      <a 
        href="/analyze" 
        class="nav-link" 
        :class="{ active: selectedKey === '/analyze' }"
        @click.prevent="onMenuClick('/analyze')"
      >
        AuraInterpreter 
      </a>

      <a 
        href="/test" 
        class="nav-link" 
        :class="{ active: selectedKey === '/test' }"
        @click.prevent="onMenuClick('/test')"
      >
        VibeQuest
      </a>

      <a 
        href="/share" 
        class="nav-link" 
        :class="{ active: selectedKey === '/share' }"
        @click.prevent="onMenuClick('/share')"
      >
        EmotionSquare
      </a>

      <a 
        href="/about" 
        class="nav-link" 
        :class="{ active: selectedKey === '/about' }"
        @click.prevent="onMenuClick('/about')"
      >
        About
      </a>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const selectedKey = computed(() => {
  const p = route.path
  if (p.startsWith('/analyze')) return '/analyze'
  if (p.startsWith('/about')) return '/about'
  if (p.startsWith('/test')) return '/test'
  if (p.startsWith('/share')) return '/share' 
  return '/home'
})

function onMenuClick(key) {
  if (key !== route.path) router.push(key)
}

function goHome() { 
  router.push('/home') 
}
</script>

<style scoped>
/* 主容器 - 纯紫色背景 */
.topbar {
  position: relative;
  z-index: 10;
  background: #7c3aed;
  padding: 20px 60px;
  border-bottom: 1px solid white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  box-sizing: border-box;
}

/* 品牌区域 */
.brand-section {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  transition: opacity 0.2s ease;
}

.brand-section:hover {
  opacity: 0.8;
}

.logo {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: white;
  border-radius: 50%;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.logo-emoji {
  font-size: 24px;
  filter: drop-shadow(0 1px 2px rgba(0, 0, 0, 0.2));
}

.brand {
  display: flex;
  flex-direction: column;
  line-height: 1.2;
}

.brand__name {
  font-size: 24px;
  font-weight: 800;
  color: white;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
  letter-spacing: -0.5px;
}

.brand__tag {
  font-size: 14px;
  color: white;
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.4);
  font-weight: 400;
}

/* 导航区域 */
.nav-section {
  display: flex;
  gap: 32px;
  align-items: center;
}

.nav-link {
  color: white;
  text-decoration: none;
  font-size: 16px;
  font-weight: 500;
  padding: 8px 0;
  position: relative;
  transition: all 0.2s ease;
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.4);
}

.nav-link:hover {
  color: white;
  transform: translateY(-1px);
}

.nav-link.active {
  color: white;
  font-weight: 600;
}

.nav-link.active::after {
  content: '';
  position: absolute;
  bottom: -4px;
  left: 50%;
  transform: translateX(-50%);
  width: 30px;
  height: 2px;
  background: white;
  border-radius: 1px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .topbar {
    padding: 20px 40px;
  }
  
  .nav-section {
    gap: 20px;
  }
  
  .nav-link {
    font-size: 14px;
  }
  
  .brand__name {
    font-size: 20px;
  }
  
  .brand__tag {
    font-size: 12px;
  }
}

@media (max-width: 480px) {
  .topbar {
    padding: 20px 20px;
  }
  
  .nav-section {
    gap: 16px;
  }
  
  .nav-link {
    font-size: 13px;
  }
  
  .brand-section {
    gap: 8px;
  }
  
  .logo {
    width: 32px;
    height: 32px;
  }
  
  .logo-emoji {
    font-size: 20px;
  }
}
</style>