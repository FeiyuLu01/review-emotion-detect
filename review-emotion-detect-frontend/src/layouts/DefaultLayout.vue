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

    <!-- 用 ref 获取 Footer 组件实例；稍后用 $el 取真实元素 -->
    <a-layout-footer ref="footerRef" class="layout__footer">
      <FooterBar />
    </a-layout-footer>

    <!-- Back-to-Top：bottom 随 footer 是否可见动态调整 -->
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

/** ===== Visibility threshold (show when scrolled ~half viewport, capped) ===== */
const threshold = ref(240)
function updateThreshold() {
  const half = Math.round(window.innerHeight * 0.5)
  threshold.value = Math.max(180, Math.min(half, 360))
}

/** ===== Footer in-view detection to avoid overlap ===== */
const footerRef = ref(null)          // component instance or element
const footerInView = ref(false)
let io = null

const backTopBottom = computed(() => (footerInView.value ? 88 : 28))

onMounted(async () => {
  updateThreshold()
  window.addEventListener('resize', updateThreshold)

  // 等 DOM 真正挂载后再取元素
  await nextTick()

  // 兼容组件 / 原生元素两种情况
  let footerEl = footerRef.value
  // ant-design-vue 组件实例有 $el
  if (footerEl && footerEl.$el) footerEl = footerEl.$el

  // 只有在拿到原生 Element 时才去 observe，避免报错
  if (footerEl instanceof Element) {
    io = new IntersectionObserver(
      entries => {
        footerInView.value = !!entries[0]?.isIntersecting
      },
      { root: null, threshold: 0.01 }
    )
    io.observe(footerEl)
  } else {
    // 兜底：找 class 选一次（极端情况下）
    const fallback = document.querySelector('.layout__footer')
    if (fallback) {
      io = new IntersectionObserver(
        entries => { footerInView.value = !!entries[0]?.isIntersecting },
        { root: null, threshold: 0.01 }
      )
      io.observe(fallback)
    }
  }
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', updateThreshold)
  try { io?.disconnect?.() } catch {}
  io = null
})
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
.layout__content { padding: 32px 0 48px 0; }
.layout__footer  { background: transparent; padding: 12px 0 32px 0; }

/* BackTop 固定在右下；bottom 由内联 :style 控制 */
:deep(.ant-back-top) {
  position: fixed;
  right: 28px;
  z-index: 1000;
}

/* 漂亮的玻璃渐变按钮 */
.backtop-btn {
  position: relative;
  width: 48px;
  height: 48px;
  border: none;
  border-radius: 999px;
  display: grid;
  place-items: center;
  cursor: pointer;

  color: #fff;
  background: linear-gradient(135deg, #7C3AED 0%, #06B6D4 100%);
  box-shadow:
    0 12px 28px rgba(16,24,40,.24),
    inset 0 0 0 1px rgba(255,255,255,.22);
  backdrop-filter: blur(6px) saturate(140%);
  transition: transform .18s ease, box-shadow .18s ease, filter .18s ease;
}
.backtop-btn .backtop-ring {
  position: absolute;
  inset: -2px;
  border-radius: inherit;
  background: conic-gradient(from 180deg,
    rgba(124,58,237,.65),
    rgba(6,182,212,.65),
    rgba(124,58,237,.65));
  filter: blur(6px);
  opacity: .35;
  z-index: -1;
  transition: opacity .2s ease, filter .2s ease;
}
.backtop-btn:hover {
  transform: translateY(-2px) scale(1.04);
  box-shadow: 0 16px 34px rgba(16,24,40,.28);
  filter: brightness(1.05);
}
.backtop-btn:hover .backtop-ring { opacity: .55; filter: blur(8px); }
.backtop-btn:active { transform: translateY(0) scale(.98); }
.backtop-btn:focus-visible {
  outline: 3px solid rgba(124,58,237,.45);
  outline-offset: 2px;
}

/* BackTop 出现时的轻微入场动画 */
:deep(.ant-back-top .ant-back-top-content) {
  animation: bt-in .18s ease-out both;
}
@keyframes bt-in {
  from { opacity: 0; transform: scale(.92); }
  to   { opacity: 1; transform: scale(1); }
}
</style>