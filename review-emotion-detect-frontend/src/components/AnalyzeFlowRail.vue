<template>
  <aside ref="railRef" class="flow-rail" aria-label="Analyze steps">
    <ol class="flow-steps">
      <li
        v-for="(s,i) in steps"
        :key="s.key"
        class="flow-step"
        :data-key="s.key"
        :aria-current="currentKey===s.key ? 'step' : undefined"
      >
        <span class="dot"></span>
        <span class="label" :data-label="s.label">{{ s.label }}</span>
      </li>
    </ol>
    <div class="progress-line" aria-hidden="true">
      <div class="progress-bar" ref="barRef"></div>
    </div>
  </aside>
</template>

<script setup>
/** 路径：src/components/flow/AnalyzeFlowRail.vue */
import { ref, onMounted, onBeforeUnmount, watch, nextTick } from 'vue'
import { gsap } from 'gsap'
import { ScrollTrigger } from 'gsap/ScrollTrigger'
import { TextPlugin } from 'gsap/TextPlugin'
gsap.registerPlugin(ScrollTrigger, TextPlugin)

const props = defineProps({
  // 'input' | 'loading' | 'results' | 'evidence' | 'rewrite'
  activeKey: { type: String, required: true }
})

const steps = [
  { key: 'input',    label: 'Input Review' },
  { key: 'loading',  label: 'Loading' },
  { key: 'results',  label: 'Analysis Results' },
  { key: 'evidence', label: 'Scientific Evidence' },
  { key: 'rewrite',  label: 'Rewrite Review' },
]

const railRef = ref(null)
const barRef  = ref(null)
const currentKey = ref(props.activeKey)     // ← 本地高亮状态
let ctx
let reduceMotion = false

const HEADER_OFFSET = 64

function isVisible(el, thresh = 0.1) {
  if (!el) return false
  const r = el.getBoundingClientRect()
  const vh = window.innerHeight || document.documentElement.clientHeight
  const viewTop = HEADER_OFFSET
  const viewBottom = vh
  const interTop = Math.max(r.top, viewTop)
  const interBottom = Math.min(r.bottom, viewBottom)
  const inter = Math.max(0, interBottom - interTop)
  const denom = Math.min(r.height, Math.max(1, viewBottom - viewTop))
  return inter / denom >= thresh
}

function prefersReducedMotion () {
  if (typeof window === 'undefined' || !window.matchMedia) return false
  return window.matchMedia('(prefers-reduced-motion: reduce)').matches
}

function setActive(key) {
  // 若 key 非法，直接忽略（避免 undefined）
  if (!key || !steps.some(s => s.key === key)) return

  const nodes = railRef.value?.querySelectorAll('.flow-step') || []
  nodes.forEach(n => {
    const isActive = n.getAttribute('data-key') === key
    n.classList.toggle('is-active', isActive)
    if (isActive) animateStep(n)
  })
  currentKey.value = key
}

function animateStep(stepEl) {
  const labelEl = stepEl?.querySelector('.label')
  const dotEl   = stepEl?.querySelector('.dot')
  if (!labelEl) return

  if (!reduceMotion) {
    gsap.fromTo(stepEl, { y: 4, opacity: 0.9 }, { y: 0, opacity: 1, duration: 0.35, ease: 'power2.out' })
  }

  const targetText = labelEl.getAttribute('data-label') || labelEl.textContent || ''
  if (!reduceMotion) {
    const dur = Math.min(0.8, 0.2 + targetText.length * 0.03)
    gsap.killTweensOf(labelEl)
    gsap.set(labelEl, { text: '' })
    gsap.to(labelEl, { duration: dur, text: { value: targetText, delimiter: '' }, ease: 'none' })
  } else {
    labelEl.textContent = targetText
  }

  pulseDot(dotEl)
}

function pulseDot(dotEl) {
  if (!dotEl || reduceMotion) return
  gsap.fromTo(dotEl, { scale: 1 }, { scale: 1.35, duration: 0.22, yoyo: true, repeat: 1, ease: 'power2.out' })
  gsap.fromTo(dotEl, { boxShadow: '0 0 0 0 rgba(124,58,237,0.0)' }, {
    boxShadow: '0 0 14px 6px rgba(124,58,237,0.45)',
    duration: 0.18, yoyo: true, repeat: 1, ease: 'power1.out'
  })
}

function visibleRatio(el) {
  if (!el) return 0
  const r = el.getBoundingClientRect()
  const vh = window.innerHeight || document.documentElement.clientHeight
  const viewTop = HEADER_OFFSET
  const viewBottom = vh
  const interTop = Math.max(r.top, viewTop)
  const interBottom = Math.min(r.bottom, viewBottom)
  const inter = Math.max(0, interBottom - interTop)
  const denom = Math.min(r.height, Math.max(1, viewBottom - viewTop))
  return inter / denom
}

onMounted(() => {
  reduceMotion = prefersReducedMotion()

  ctx = gsap.context(() => {
    // —— S1~S3：进入即高亮（与状态流相符）——
    ;['input','loading','results'].forEach((k) => {
      const el = document.querySelector(`#section-${k}`)
      if (!el) return  // ← 守卫：元素不存在就不建触发器
      ScrollTrigger.create({
        trigger: el,
        start: 'top center',
        end: 'bottom center',
        onEnter:     () => setActive(k),
        onEnterBack: () => setActive(k),
      })
    })

    // —— S4/S5：完全可见才高亮 —— 
    ;['evidence','rewrite'].forEach((k) => {
      const el = document.querySelector(`#section-${k}`)
      if (!el) return
      ScrollTrigger.create({
        trigger: el,
        start: 'top bottom',
        end: 'bottom top',
        onUpdate: (self) => {
          const ratio = visibleRatio(self?.trigger)
          // 若元素很高，可改成 0.90 或把 visibleRatio 的分母改成 r.height
          if (ratio >= 0.98){
            const inputEl = document.querySelector('#section-input')
            if (isVisible(inputEl, 0.1)) return
            setActive(k)
          }
        }
      })
    })

    // 进度条（evidence -> rewrite）
    // const ev = document.querySelector('#section-evidence')
    // const rw = document.querySelector('#section-rewrite')
    // if (ev && rw) {
    //   ScrollTrigger.create({
    //     trigger: ev,
    //     endTrigger: rw,
    //     start: 'top center',
    //     end: 'bottom center',
    //     scrub: true,
    //     onUpdate: (self) => {
    //       if (!barRef.value) return
    //       const h = Math.max(0, Math.min(1, self.progress)) * 100
    //       gsap.to(barRef.value, { height: `${h}%`, duration: 0.1, overwrite: true })
    //     }
    //   })
    // }
    const rs = document.querySelector('#section-results')
    const rw = document.querySelector('#section-rewrite')
    if (rs && rw) {
      ScrollTrigger.create({
        trigger: rs,
        endTrigger: rw,
        start: `top center+=-${HEADER_OFFSET}`,
        end:   `top bottom-=${HEADER_OFFSET}`,
        scrub: true,
        onUpdate: (self) => {
          if (!barRef.value) return
          const h = Math.max(0, Math.min(1, self.progress)) * 100
          gsap.to(barRef.value, { height: `${h}%`, duration: 0.1, overwrite: true })
        },
        onLeaveBack: () => {   // 回到 S3 以上，复位为 0%
          if (!barRef.value) return
          gsap.to(barRef.value, { height: '0%', duration: 0.1, overwrite: true })
        },
        onLeave: () => {       // 超过 S5 顶部，锁到 100%
          if (!barRef.value) return
          gsap.to(barRef.value, { height: '100%', duration: 0.1, overwrite: true })
        }
      })
    }
  }, railRef)

  // 初始高亮（进入页面未滚动）
  nextTick(() => setActive(props.activeKey))
})

watch(() => props.activeKey, async (k) => {
  setActive(k)
  await nextTick()
  ScrollTrigger.refresh()
})

onBeforeUnmount(() => ctx?.revert())
</script>

<style scoped>
.flow-rail {
  position: sticky; top: 96px;
  align-self: start;
  width: 220px; padding: 8px 0 8px 18px;
}
.flow-steps { list-style: none; margin: 0; padding: 0; position: relative; }
.flow-step {
  display: grid;
  grid-template-columns: 16px 1fr;
  align-items: center;
  gap: 10px;
  padding: 10px 0;
  color: #cbd5e1;
  font-weight: 700;
  transition: color .25s ease, filter .25s ease;
}
.flow-step .dot {
  width: 12px; height: 12px; border-radius: 50%;
  background: #475569; box-shadow: 0 0 0 2px rgba(148,163,184,.2) inset;
  transition: transform .2s, background .2s, box-shadow .2s;
}
.flow-step .label { will-change: filter, transform, background-position; }
.flow-step.is-active,
.flow-step[aria-current="step"] { color: transparent; }
.flow-step.is-active .label {
  font-weight: 800;
  background: linear-gradient(90deg, #06B6D4, #7C3AED, #ff006e, #facc15);
  background-size: 200% 100%;
  -webkit-background-clip: text;
  background-clip: text;
  animation: gradientShift 4s linear infinite;
  text-shadow:
    0 0 8px rgba(124,58,237,.5),
    0 0 16px rgba(6,182,212,.45),
    0 0 24px rgba(255,0,110,.35);
}
.progress-line { position: absolute; left: 23px; top: 18px; bottom: 18px; width: 2px; background: rgba(148,163,184,.2); }
.progress-bar  { position: absolute; left: 0; top: 0; width: 100%; height: 0%; background: linear-gradient(180deg,#06B6D4,#7C3AED,#ff006e); }
@media (max-width: 992px){ .flow-rail { display: none; } }
@keyframes gradientShift { 0%{background-position:0% 50%} 100%{background-position:200% 50%} }
.flow-step.is-active .dot {
  background: #7c3aed;
  transform: scale(1.15);
  box-shadow:
    0 0 0 2px rgba(124,58,237,.35) inset,
    0 0 12px 4px rgba(124,58,237,.6),
    0 0 20px 6px rgba(6,182,212,.4);
}
</style>