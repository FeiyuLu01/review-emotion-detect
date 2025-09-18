<template>
    <aside ref="railRef" class="flow-rail" aria-label="Analyze steps">
      <ol class="flow-steps">
        <li
          v-for="(s,i) in steps"
          :key="s.key"
          class="flow-step"
          :data-key="s.key"
          :aria-current="activeKey===s.key ? 'step' : undefined"
        >
          <span class="dot"></span>
          <!-- 增加 data-label，供 TextPlugin 读取目标文本 -->
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
  import { TextPlugin } from 'gsap/TextPlugin'   // ✅ 新增：TextPlugin
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
  let ctx
  let reduceMotion = false  // ✅ 新增：尊重用户“减少动效”偏好
  
  function prefersReducedMotion () {
    if (typeof window === 'undefined' || !window.matchMedia) return false
    return window.matchMedia('(prefers-reduced-motion: reduce)').matches
  }
  
  function setActive(key) {
    const nodes = railRef.value?.querySelectorAll('.flow-step') || []
    nodes.forEach(n => {
      const isActive = n.getAttribute('data-key') === key
      n.classList.toggle('is-active', isActive)
      if (isActive) animateStep(n) // ✅ 新增：激活动画
    })
  }
  
  // ✅ 新增：激活动画（文字打字 + 轻微上浮 + 圆点脉冲）
  function animateStep(stepEl) {
    const labelEl = stepEl.querySelector('.label')
    const dotEl   = stepEl.querySelector('.dot')
    if (!labelEl) return
  
    // 1) 轻微上浮 & 透明度
    if (!reduceMotion) {
      gsap.fromTo(stepEl, { y: 4, opacity: 0.9 }, { y: 0, opacity: 1, duration: 0.35, ease: 'power2.out' })
    }
  
    // 2) 文本打字效果（TextPlugin）
    const targetText = labelEl.getAttribute('data-label') || labelEl.textContent || ''
    if (!reduceMotion) {
      // 先把文字清空再打字出现；为避免卡顿，限制长度较长时用更快时长
      const dur = Math.min(0.8, 0.2 + targetText.length * 0.03)
      gsap.killTweensOf(labelEl)
      gsap.set(labelEl, { text: '' })
      gsap.to(labelEl, {
        duration: dur,
        text: { value: targetText, delimiter: '' }, // 按字符逐个显现
        ease: 'none'
      })
    } else {
      // 降级：直接设置最终文本
      labelEl.textContent = targetText
    }
  
    // 3) 圆点脉冲高亮
    pulseDot(dotEl)
  }
  
  // ✅ 新增：圆点脉冲（缩放 + 光晕）
  function pulseDot(dotEl) {
    if (!dotEl || reduceMotion) return
    gsap.fromTo(dotEl, { scale: 1 }, { scale: 1.35, duration: 0.22, yoyo: true, repeat: 1, ease: 'power2.out' })
    gsap.fromTo(dotEl, { boxShadow: '0 0 0 0 rgba(124,58,237,0.0)' }, {
      boxShadow: '0 0 14px 6px rgba(124,58,237,0.45)',
      duration: 0.18, yoyo: true, repeat: 1, ease: 'power1.out'
    })
  }
  
  onMounted(() => {
    reduceMotion = prefersReducedMotion()
  
    ctx = gsap.context(() => {
      // 每个 section 的滚动高亮
      steps.forEach((s) => {
        ScrollTrigger.create({
          trigger: `#section-${s.key}`,
          start: 'top center',
          end: 'bottom center',
          onEnter:     () => setActive(s.key),
          onEnterBack: () => setActive(s.key),
        })
      })
  
      // 进度条（evidence -> rewrite）
      ScrollTrigger.create({
        trigger: '#section-evidence',
        endTrigger: '#section-rewrite',
        start: 'top center',
        end: 'bottom center',
        scrub: true,
        onUpdate: (self) => {
          if (!barRef.value) return
          const h = Math.max(0, Math.min(1, self.progress)) * 100
          gsap.to(barRef.value, { height: `${h}%`, duration: 0.1, overwrite: true })
        }
      })
    }, railRef)
  
    // 初始高亮一次（进入页面未滚动时也有激活态 + 动画）
    nextTick(() => setActive(props.activeKey))
  })
  
  // 数据切换时刷新触发点并做一次激活动画
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
  
  .flow-step .label {
  will-change: filter, transform, background-position;
}

  /* ✅ 更显眼的高亮态：白字 + 轻描边 + 霓虹文字光 */
  .flow-step.is-active,
.flow-step[aria-current="step"] {
  color: transparent; /* 避免原文字颜色干扰 */
}

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
  /* .flow-step.is-active .dot {
    background: #7c3aed;
    transform: scale(1.12);
    box-shadow:
      0 0 0 2px rgba(124,58,237,.35) inset,
      0 0 14px 4px rgba(124,58,237,.45);
  } */
  
  /* 纵向进度条 */
  .progress-line { position: absolute; left: 23px; top: 18px; bottom: 18px; width: 2px; background: rgba(148,163,184,.2); }
  .progress-bar  { position: absolute; left: 0; top: 0; width: 100%; height: 0%; background: linear-gradient(180deg,#06B6D4,#7C3AED,#ff006e); }
  
  @media (max-width: 992px){ .flow-rail { display: none; } }

  @keyframes gradientShift {
  0%   { background-position: 0% 50%; }
  100% { background-position: 200% 50%; }
}
.flow-step.is-active .dot {
  background: #7c3aed;
  transform: scale(1.15);
  box-shadow:
    0 0 0 2px rgba(124,58,237,.35) inset,
    0 0 12px 4px rgba(124,58,237,.6),
    0 0 20px 6px rgba(6,182,212,.4);
}
  </style>