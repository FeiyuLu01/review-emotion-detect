<template>
  <a-layout class="layout">
    <!-- Topbar（不改你的逻辑与样式） -->
    <TopBar />

    <!-- Main content -->
    <a-layout-content class="layout__content">
      <!-- ✅ 移除了多余的 container 包裹，保证 full-bleed 正常 -->
      <slot />
    </a-layout-content>

    <!-- Footer -->
    <!-- <a-layout-footer ref="footerRef" class="layout__footer">
      <FooterBar />
    </a-layout-footer> -->

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

const threshold = ref(240)
function updateThreshold() {
  const half = Math.round(window.innerHeight * 0.5)
  threshold.value = Math.max(180, Math.min(half, 360))
}

const footerRef = ref(null)
const footerInView = ref(false)
let io = null
const backTopBottom = computed(() => (footerInView.value ? 88 : 28))

onMounted(async () => {
  updateThreshold()
  window.addEventListener('resize', updateThreshold)
  await nextTick()

  let footerEl = footerRef.value
  if (footerEl && footerEl.$el) footerEl = footerEl.$el
  if (footerEl instanceof Element) {
    io = new IntersectionObserver(
      entries => { footerInView.value = !!entries[0]?.isIntersecting },
      { root: null, threshold: 0.01 }
    )
    io.observe(footerEl)
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
  display: flex;
  flex-direction: column;
}


/* ✅ 关键：给内容区整体让出 TopBar 高度（默认 64px） */
.layout__content {
  background: transparent;
}

/* ✅ 关键：Footer 始终用站点浅蓝底，且在上层（不显示渐变） */
.layout__footer  {
  background: var(--ml-bg-color);
  position: relative;
  z-index: 2;
  padding: 12px 0 32px 0;
  flex-shrink: 0;
}

/* Back-to-top：保持原样 */
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
</style>