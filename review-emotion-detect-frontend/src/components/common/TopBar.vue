<template>
  <div class="topbar container">
    <div class="left" @click="goHome">
      <img class="logo" src="/favicon.svg" alt="MoodLens" />
      <div class="brand">
        <span class="brand__name">MoodLens</span>
        <span class="brand__tag">Find Review Emotion</span>
      </div>
    </div>

    <a-menu
      mode="horizontal"
      :selectedKeys="[selectedKey]"
      class="menu"
      @click="onMenuClick"
    >
      <a-menu-item key="/home">Home</a-menu-item>
      <a-menu-item key="/analyze">What emotions in the review?</a-menu-item>
      <a-menu-item key="/about">About us</a-menu-item>
    </a-menu>

    <div class="right"></div>
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
  return '/home'
})

function onMenuClick({ key }) {
  if (key !== route.path) router.push(key)
}
function goHome() { router.push('/home') }
</script>

<style scoped>
.topbar {
  height: 64px;
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  align-items: center;
  gap: 12px;
}

.left { display: flex; align-items: center; gap: 12px; cursor: pointer; }
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
.brand__tag { font-size: 12px; color: #475569; }

@keyframes shine {
  from { filter: drop-shadow(0 0 0 rgba(124,58,237,.0)); }
  to   { filter: drop-shadow(0 2px 8px rgba(6,182,212,.25)); }
}

/* Center menu, transparent background, and disable overflow collapse */
.menu { justify-self: center; border-bottom: none; min-width: max-content; }
:deep(.ant-menu),
:deep(.ant-menu-horizontal) { background: transparent !important; border-bottom: none !important; }
:deep(.ant-menu-overflow) { overflow: visible !important; max-width: none !important; flex-wrap: nowrap !important; }
:deep(.ant-menu-overflow .ant-menu-overflow-item) { flex: 0 0 auto !important; }
:deep(.ant-menu-overflow-item-rest) { display: none !important; }
:deep(.ant-menu-horizontal) { line-height: 64px; height: 64px; }

/* Remove default light theme background on items */
:deep(.ant-menu-light .ant-menu-item),
:deep(.ant-menu-light .ant-menu-submenu) { background: transparent !important; }

/* Text color on hover/selected */
:deep(.ant-menu-horizontal:not(.ant-menu-dark) .ant-menu-item:hover) {
  background: transparent !important;
  color: #7C3AED !important;
}
:deep(.ant-menu-light .ant-menu-item-selected) {
  background: transparent !important;
  color: #7C3AED !important;
  font-weight: 700;
}

/* ===== Underline control (fix multi-highlight) ===== */
/* Base: hide underline for all items by default */
:deep(.ant-menu-horizontal > .ant-menu-item::after),
:deep(.ant-menu-horizontal > .ant-menu-submenu::after) {
  border-bottom-color: transparent !important;  /* important: hide when not active */
}

/* Hover: show purple underline */
:deep(.ant-menu-horizontal > .ant-menu-item:hover::after),
:deep(.ant-menu-horizontal > .ant-menu-submenu:hover::after) {
  border-bottom-color: #7C3AED !important;
}

/* Selected: show purple underline */
:deep(.ant-menu-horizontal > .ant-menu-item-selected::after),
:deep(.ant-menu-horizontal > .ant-menu-submenu-selected::after) {
  border-bottom-color: #7C3AED !important;
  transform: scaleX(1) !important; /* ensure full width */
}

.right { justify-self: end; }
</style>