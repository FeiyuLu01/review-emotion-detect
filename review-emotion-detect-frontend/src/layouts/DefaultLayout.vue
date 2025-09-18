<template>
  <a-layout class="layout">
    <!-- Topbar -->
    <a-layout-header class="layout__header">
      <TopBar />
    </a-layout-header>

    <!-- Main content -->
    <a-layout-content class="layout__content">
      <slot />
    </a-layout-content>

    <!-- Footer -->
    <a-layout-footer ref="footerRef" class="layout__footer">
      <FooterBar />
    </a-layout-footer>

    <!-- Back-to-Top -->
    <a-back-top
      :visibilityHeight="threshold"
      :style="{ right: '28px', bottom: backTopBottom + 'px' }"
    >
      <button class="backtop-btn" aria-label="Back to top">
        <span class="backtop-ring" aria-hidden="true"></span>
        <svg viewBox="0 0 24 24" width="18" height="18" aria-hidden="true">
          <path
            d="M6 14l6-6 6 6"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          />
        </svg>
      </button>
    </a-back-top>
  </a-layout>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount, nextTick } from 'vue'
import TopBar from '@/components/common/TopBar.vue'
import FooterBar from '@/components/common/FooterBar.vue'

/* ===== BackTop 出现阈值：随视口变化 ===== */
const threshold = ref(240)
function updateThreshold() {
  const half = Math.round(window.innerHeight * 0.5)
  threshold.value = Math.max(180, Math.min(half, 360))
}

/* ===== 观察 Footer 是否进入视口，避免 BackTop 与 Footer 重叠 ===== */
const footerRef = ref(null)
const footerInView = ref(false)
let io = null
const backTopBottom = computed(() => (footerInView.value ? 88 : 28))

onMounted(async () => {
  updateThreshold()
  window.addEventListener('resize', updateThreshold)
  await nextTick()

  // 兼容不同渲染形态，尽可能取到真实 DOM
  let el = footerRef.value
  el = el?.$el ?? el
  // ant-design-vue v3+ 这里一般已经是真实 DOM；若仍是组件，取 $el
  if (el && el.nodeType !== 1 && el.$el) el = el.$el

  if (el instanceof Element) {
    io = new IntersectionObserver(
      (entries) => { footerInView.value = !!entries[0]?.isIntersecting },
      {
        root: null,
        threshold: 0,               // 只要有一丁点进入就算
        rootMargin: '0px 0px -10% 0px', // 提前一点点触发，体验更稳
      }
    )
    io.observe(el)
  }
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', updateThreshold)
  try { io?.disconnect?.() } catch {}
  io = null
})
</script>

<style scoped>
/* 可配置的头部高度（默认 64px） */
:root { --header-h: 64px; }

.layout {
  min-height: 100vh;
  position: relative;
  display: flex;
  flex-direction: column;
}

/* sticky TopBar */
.layout__header {
  position: sticky;
  top: 0;
  z-index: 10;
  backdrop-filter: saturate(160%) blur(10px);
  background: rgba(255, 255, 255, .55);
  border-bottom: 1px solid rgba(15, 23, 42, .06);
  height: var(--header-h);
  line-height: var(--header-h);
  min-height: 64px;
  display: flex;
  align-items: center;
}

/* 内容区给 header 腾出空间 */
.layout__content {
  flex: 1 0 auto;
  padding: var(--header-h) 0 0;  /* ✅ 关键，只改一处即可全站生效 */
  background: transparent;
}

/* Footer：不收缩，背景使用站点色 */
.layout__footer  {
  background: var(--ml-bg-color);
  position: relative;
  z-index: 2;
  padding: 12px 0 32px 0;
  flex-shrink: 0;               /* ✅ 杜绝极少数场景被压缩 */
}

/* Back-to-top 固定在右下，带炫酷按钮样式 */
:deep(.ant-back-top) { position: fixed; right: 28px; z-index: 1000; }
.backtop-btn {
  position: relative; width: 48px; height: 48px; border: none; border-radius: 999px;
  display: grid; place-items: center; cursor: pointer; color: #fff;
  background: linear-gradient(135deg, #7C3AED 0%, #06B6D4 100%);
  box-shadow: 0 12px 28px rgba(16,24,40,.24), inset 0 0 0 1px rgba(255,255,255,.22);
  backdrop-filter: blur(6px) saturate(140%);
  transition: transform .18s ease, box-shadow .18s ease, filter .18s ease;
}
.backtop-btn .backtop-ring{
  position: absolute; inset: -2px; border-radius: inherit;
  background: conic-gradient(from 180deg, rgba(124,58,237,.65), rgba(6,182,212,.65), rgba(124,58,237,.65));
  filter: blur(6px); opacity: .35; z-index: -1; transition: opacity .2s ease, filter .2s ease;
}
.backtop-btn:hover { transform: translateY(-2px) scale(1.04); box-shadow: 0 16px 34px rgba(16,24,40,.28); filter: brightness(1.05); }
.backtop-btn:hover .backtop-ring { opacity: .55; filter: blur(8px); }
.backtop-btn:active { transform: translateY(0) scale(.98); }
.backtop-btn:focus-visible { outline: 3px solid rgba(124,58,237,.45); outline-offset: 2px; }

:deep(.ant-back-top .ant-back-top-content) { animation: bt-in .18s ease-out both; }
@keyframes bt-in { from { opacity: 0; transform: scale(.92);} to { opacity: 1; transform: scale(1);} }

/* 让 TopBar 根元素吃满 header 高度并成为 flex 容器 */
.layout__header :deep(> *) {
  height: var(--header-h);       /* 关键：跟 header 一样高 */
  display: flex;                 /* 自身也用 flex */
  align-items: center;           /* 垂直居中 TopBar 内的内容 */
}

.layout__header :deep(.ant-menu-horizontal) {
  height: var(--header-h);
  line-height: var(--header-h);
}
</style>