<template>
    <section id="feedback" class="tips-wrap" ref="wrapRef">
      <!-- Header（跨两列） -->
      <header class="tips-head" ref="headRef">
        <h2 class="title">
          <span class="grad">Personalized Tips</span>
          <small>Based on your recent practice</small>
        </h2>
      </header>
  
      <!-- 左侧轨迹 + 小球 -->
      <aside class="rail">
        <svg class="rail-svg" viewBox="0 0 80 560" preserveAspectRatio="xMidYMid meet" aria-hidden="true">
          <path
            id="progressPath"
            d="M40,10 C40,110 12,150 40,210 C68,270 40,320 40,390 C40,460 60,510 40,550"
            fill="none" stroke="rgba(30,41,59,.25)" stroke-width="4" stroke-linecap="round"
          />
        </svg>
        <div class="rail-dot" ref="railDot"></div>
      </aside>
  
      <!-- 顶部“等级 + 插画”卡片 -->
      <div class="card">
        <div class="level-row">
          <div class="level-left two-lines">
            <div class="level-chip">
              <span class="chip-label">{{ levelInfo.level || '—' }}</span>
            </div>
            <p class="level-sentence">
              You’re {{ article }} <b>{{ levelInfo.level || '—' }}</b> emotion detector.
            </p>
          </div>
  
          <div class="level-illustration" aria-hidden="true">
            <img
              v-if="levelImages[levelKey]"
              class="illu"
              :src="levelImages[levelKey]"
              :alt="`${levelInfo.level} level illustration`"
            />
            <div v-else class="illu-placeholder">Illustration</div>
          </div>
        </div>
      </div>
  
      <!-- 三块独立卡片 -->
      <section class="card-section" data-st="1" :class="{ 'stage-locked': revealIndex < 1 }">
        <h4 class="st-title">OVERALL FEEDBACK</h4>
        <p class="overall st-copy">{{ data.feedback || '—' }}</p>
      </section>
  
      <section v-if="chips.length" class="card-section" data-st="2" :class="{ 'stage-locked': revealIndex < 2 }">
        <h4 class="st-title">QUICK TIPS</h4>
        <div class="chips st-copy">
          <span v-for="c in chips" :key="c" class="chip">{{ c }}</span>
        </div>
      </section>
  
      <section v-if="levelInfo.advice" class="card-section" data-st="3" :class="{ 'stage-locked': revealIndex < 3 }">
        <h4 class="st-title">GROWTH PLAN</h4>
        <p class="overall st-copy">{{ levelInfo.advice }}</p>
      </section>
  
      <!-- Resources 放在独立卡片 -->
      <section class="card-section" data-st="4">
        <section class="row grid-1 resources-card" ref="resourcesRef">
          <div>
            <h4 class="res-title">Resources to help you</h4>
            <ul class="list res-list">
              <li v-for="r in refs" :key="r" class="res-item">{{ r }}</li>
            </ul>
          </div>
        </section>
      </section>
  
      <!-- ✅ 抽离出来的按钮：位于资源卡片下方，保持居中与间距 -->
      <div class="actions actions-out">
        <button class="btn ghost" @click="handleRetry">Try again</button>
        <button class="btn" @click="emit('backTop')">Back to top</button>
      </div>
    </section>
  </template>

<script setup>
import { computed, onMounted, ref, onBeforeUnmount, nextTick } from 'vue'
import { gsap } from 'gsap'
import { ScrollTrigger } from 'gsap/ScrollTrigger'
import { MotionPathPlugin } from 'gsap/MotionPathPlugin'
gsap.registerPlugin(ScrollTrigger, MotionPathPlugin)

/* 事件定义 */
const emit = defineEmits(['retry','backTop'])

const props = defineProps({
  data: { type: Object, default: () => ({}) }
})

/** Parse "Intermediate: 1–2× per week" -> { level, advice } */
const levelInfo = computed(() => {
  const raw = (props.data.growthTips || '').trim()
  const m = raw.match(/^([^:：]+)\s*[:：]\s*(.+)$/)
  if (!m) return { level: raw || '', advice: '' }
  return { level: m[1].trim(), advice: m[2].trim() }
})

/** stable key for image mapping */
const levelKey = computed(() => (levelInfo.value.level || '').toLowerCase())

/** Per-level illustrations */
const levelImages = {
  beginner: new URL('@/assets/beginner.png', import.meta.url).href,
  intermediate: new URL('@/assets/intermediate.png', import.meta.url).href,
  advanced: new URL('@/assets/advanced.png', import.meta.url).href,
  expert: new URL('@/assets/expert.png', import.meta.url).href,
}

/** Quick tips as chips */
const chips = computed(() => {
  const t = props.data.tips
  if (Array.isArray(t)) return t.filter(Boolean)
  if (typeof t === 'string') {
    return t.split(/[;；,，]/).map(s => s.trim()).filter(Boolean)
  }
  return []
})

/** Normalize refs */
const refs = computed(() => toArray(props.data.refs))
const growthRefs = computed(() => toArray(props.data.growthRefs))
function toArray(v) {
  if (!v) return []
  if (Array.isArray(v)) return v
  return String(v).split(/\r?\n|[;；]/).map(s => s.trim()).filter(Boolean)
}

/** auto a/an */
const article = computed(() =>
  /^[aeiou]/i.test(levelInfo.value.level || '') ? 'an' : 'a'
)

/** 分段显示的门槛（1=OVERALL, 2=QUICK, 3=GROWTH） */
const revealIndex = ref(1)

/** refs for sizing the rail */
const wrapRef = ref(null)
const headRef = ref(null)
const railDot = ref(null)
const resourcesRef = ref(null)

function updateRailSize() {
  const wrap = wrapRef.value
  const head = headRef.value
  if (!wrap || !head) return
  const headH = head.offsetHeight
  const totalH = wrap.scrollHeight
  wrap.style.setProperty('--railTop', headH + 'px')
  wrap.style.setProperty('--railHeight', Math.max(totalH - headH, 0) + 'px')
  ScrollTrigger.refresh()
}

/* GSAP 初始化（保持你上个版本动画，并不改按钮） */
function initGsap() {
  // 顶部大卡内部：chip -> 句子 -> 插画
  const banner = document.querySelector('.card')
  if (banner) {
    const tl = gsap.timeline({ scrollTrigger: { trigger: banner, start: 'top 75%' } })
    tl.from(banner.querySelector('.level-chip'), { y: 16, opacity: 0, duration: .45, ease: 'power2.out' })
      .from(banner.querySelector('.level-sentence'), { y: 14, opacity: 0, duration: .45, ease: 'power2.out' }, '-=0.2')
      .from(banner.querySelector('.level-illustration'), { x: 18, opacity: 0, duration: .5, ease: 'power2.out' }, '-=0.2')
  }

  // 标题词级弹入
  document.querySelectorAll('.card-section .st-title').forEach(title => {
    const words = title.textContent.trim().split(/\s+/)
    title.innerHTML = words.map(w => `<span class="w">${w}&nbsp;</span>`).join('')
    gsap.set(title.querySelectorAll('.w'), { display: 'inline-block', yPercent: 120, opacity: 0, rotateX: -80, transformOrigin: '0% 100%' })
    gsap.to(title.querySelectorAll('.w'), {
      yPercent: 0, opacity: 1, rotateX: 0,
      ease: 'back.out(1.7)',
      duration: 0.6,
      stagger: 0.045,
      scrollTrigger: { trigger: title, start: 'top 80%' }
    })
  })

  // 标题渐变下划线（.st-title & .res-title）
  document.querySelectorAll('.st-title, .res-title').forEach(el => {
    gsap.fromTo(el, { '--title-underline': 0 }, {
      '--title-underline': 1,
      duration: .8,
      ease: 'power3.out',
      scrollTrigger: { trigger: el, start: 'top 85%', toggleActions: 'play none none reverse' }
    })
  })

  // 小卡正文淡入
  gsap.utils.toArray('.card-section .st-copy').forEach(el => {
    gsap.from(el, {
      y: 24, opacity: 0,
      duration: 0.6, ease: 'power2.out',
      scrollTrigger: { trigger: el, start: 'top 85%' }
    })
  })

  // QUICK TIPS 的 chip 逐条进入
  const chipsWrap = document.querySelector('.card-section[data-st="2"] .chips')
  if (chipsWrap) {
    const chipEls = chipsWrap.querySelectorAll('.chip')
    gsap.from(chipEls, {
      y: 10, opacity: 0, duration: .35, ease: 'power2.out', stagger: .06,
      scrollTrigger: { trigger: chipsWrap, start: 'top 85%' }
    })
  }

  // Resources 卡片整体 + 列表逐条
  if (resourcesRef.value) {
    gsap.from(resourcesRef.value, {
      opacity: 0, y: 24, duration: 0.7, ease: 'power2.out',
      scrollTrigger: { trigger: resourcesRef.value, start: 'top 80%', toggleActions: 'play none none reverse' }
    })
    const items = resourcesRef.value.querySelectorAll('.res-item')
    gsap.from(items, {
      x: -12, opacity: 0, duration: 0.45, ease: 'power2.out', stagger: 0.08,
      scrollTrigger: { trigger: resourcesRef.value, start: 'top 80%', toggleActions: 'play none none reverse' }
    })
  }

  // 卡片 3D 倾斜
  gsap.utils.toArray('.card-section').forEach(card => {
    gsap.fromTo(card,
      { rotateX: 6, y: 20, boxShadow: '0 8px 24px rgba(2,6,23,.10)' },
      {
        rotateX: 0, y: 0, boxShadow: '0 22px 48px rgba(15,23,42,.14)',
        ease: 'none',
        scrollTrigger: { trigger: card, start: 'top 80%', end: 'bottom 50%', scrub: true }
      }
    )
  })

  // 分段解锁
  const sections = gsap.utils.toArray('.card-section')
  sections.forEach((el, i) => {
    ScrollTrigger.create({
      trigger: el,
      start: 'top 70%',
      onEnter: () => { if (revealIndex.value < i + 1) revealIndex.value = i + 1 }
    })
  })

  // 轨迹小球沿路径运动
  if (railDot.value && sections.length) {
    const first = sections[0]
    const last  = sections[sections.length - 1]
    gsap.to(railDot.value, {
      motionPath: { path: '#progressPath', align: '#progressPath', alignOrigin: [0.5, 0.5] },
      ease: 'none',
      scrollTrigger: { trigger: first, start: 'top center', endTrigger: last, end: 'bottom center', scrub: true }
    })
  }

  // 右侧插画微视差
  const illu = document.querySelector('.level-illustration .illu')
  if (illu) {
    gsap.fromTo(illu, { y: -6 }, {
      y: -10, ease: 'none',
      scrollTrigger: { trigger: '.card', start: 'top 80%', end: 'bottom top', scrub: true }
    })
  }
}

/* Try again：本地复位 + 透传 */
async function handleRetry() {
  revealIndex.value = 1
  ScrollTrigger.getAll().forEach(st => st.kill())
  if (railDot.value) gsap.set(railDot.value, { clearProps: 'all' })
  await nextTick()
  updateRailSize()
  initGsap()
  ScrollTrigger.refresh()
  emit('retry')
}

let resizeObs

onMounted(async () => {
  await nextTick()
  updateRailSize()
  initGsap()

  // 监听尺寸变化
  resizeObs = new ResizeObserver(() => updateRailSize())
  resizeObs.observe(wrapRef.value)
  window.addEventListener('resize', updateRailSize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', updateRailSize)
  if (resizeObs && wrapRef.value) resizeObs.unobserve(wrapRef.value)
})
</script>


<style scoped>
.tips-wrap {
  --railTop: 0px;
  --railHeight: 0px;

  width: min(1120px, 92vw);
  margin: 0 auto 96px;
  position: relative;
  display: grid;
  grid-template-columns: 100px 1fr; /* 左列轨迹，右列内容 */
  gap: 24px;
  align-items: start;
}

/* Header 跨两列 */
.tips-head { grid-column: 1 / -1; }

.title{ margin:0; text-align: center; }
.title .grad{
  display:block;
  font-size: clamp(24px, 3.4vw, 36px);
  font-weight: 900;
  background: linear-gradient(90deg,#7C3AED 0%, #06B6D4 100%);
  -webkit-background-clip: text; background-clip: text; color: transparent;
}
.title small{ display:block; color:#64748b; margin-top:6px; font-size: 14px; }

/* 左侧轨迹 */
.rail{
  position: absolute;
  left: 0;
  top: var(--railTop);
  height: var(--railHeight);
  width: 100px;
  display: flex;
  justify-content: center;
  align-items: center;
  pointer-events: none;
  z-index: 0;
}
.rail-svg{ width: 80px; height: 100%; overflow: visible; }
.rail-dot{
  position: absolute;
  width: 14px; height: 14px; border-radius: 999px;
  background: radial-gradient(circle at 30% 30%, #fff 0%, #7C3AED 40%, #06B6D4 100%);
  box-shadow: 0 6px 18px rgba(2,6,23,.25), 0 0 0 2px rgba(124,58,237,.25);
  transform: translate(0,0);
}

/* 右侧列 */
.card, .card-section { grid-column: 2; }

.card{
  background:#fff;
  border-radius:18px;
  box-shadow: 0 30px 80px rgba(2,6,23,.12);
  padding: 24px clamp(18px, 3vw, 34px) 22px;
}

/* Level banner */
.level-row{
  display:grid;
  grid-template-columns: 1fr auto;
  gap: 16px;
  align-items: center;
  padding: 6px 0 14px;
  border-bottom: none;
  margin-bottom: 10px;
}
.level-left{ display:flex; align-items:center; gap: 10px; min-width:0; }
.level-left.two-lines{ display: grid; gap: 8px; align-content: start; justify-items: start; }

/* chip 自适应 */
.level-chip{
  position: relative;
  display: inline-flex;
  justify-self: start;
  width: fit-content;
  padding: 6px 12px;
  border-radius: 999px;
  isolation: isolate;
  background: #fff;
  box-shadow: 0 2px 10px rgba(2,6,23,.06);
}
.level-chip::before{
  content: "";
  position: absolute;
  inset: 0;
  border-radius: inherit;
  padding: 2px;
  background: linear-gradient(90deg,#06B6D4,#3B82F6,#A78BFA,#F472B6,#22C55E,#06B6D4);
  background-size: 300% 100%;
  animation: chip-flow 6s linear infinite;
  -webkit-mask: linear-gradient(#000 0 0) content-box, linear-gradient(#000 0 0);
  -webkit-mask-composite: xor; mask-composite: exclude;
}
@keyframes chip-flow { 0%{background-position:0% 0} 100%{background-position:200% 0} }
.chip-label{ position: relative; font-weight: 900; letter-spacing: .3px; color: #4338ca; }

.level-sentence{ margin: 0; font-size: 18px; line-height: 1.6; color: #0f172a; }
.level-sentence b{
  font-weight: 900;
  background: linear-gradient(90deg,#7C3AED,#06B6D4,#22C55E,#F59E0B,#EF4444,#7C3AED);
  background-size: 250% 100%;
  -webkit-background-clip: text; background-clip: text;
  color: transparent;
  animation: hue-slide 7s ease-in-out infinite;
}
@keyframes hue-slide{ 0%{background-position:0% 0} 50%{background-position:100% 0} 100%{background-position:0% 0} }

/* Illustration */
.level-illustration{
  width: 112px;
  height: 88px;
  padding: 6px;           
  display: grid;
  place-items: center;
  align-self: start;
  overflow: visible;      
}
.illu{
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
  filter: drop-shadow(0 6px 18px rgba(2,6,23,.12));
}
.illu-placeholder{
  width: 100%; height: 100%;
  border-radius: 12px;
  background: linear-gradient(135deg, #f1f5f9, #e2e8f0);
  color:#94a3b8; font-size: 12px; display:grid; place-items:center;
}

/* 资源卡片 */
.row{ margin-top: 0; }
.overall{ margin: 0; color:#0f172a; font-size: 18px; line-height: 1.65; }

.chips{ display:flex; flex-wrap:wrap; gap:10px; }
.chip{
  display:inline-flex; align-items:center; gap:8px;
  padding:10px 14px;
  border-radius:999px;
  background:#f8fafc; border:1px solid #e2e8f0; color:#0f172a;
  box-shadow: 0 1px 0 rgba(2,6,23,.03);
  font-size: 15px;
}

.grid-1 { display: grid; grid-template-columns: 1fr; gap: 18px; }

/* 标题“渐变下划线”（变量 --title-underline 控制） */
.st-title, .res-title { position: relative; display: inline-block; margin-bottom: 10px; }
.st-title::after, .res-title::after{
  content:""; position:absolute; left:0; right:0; bottom:-6px; height:3px; border-radius:999px;
  background: linear-gradient(90deg,#7C3AED 0%, #3B82F6 50%, #06B6D4 100%);
  transform: scaleX(var(--title-underline, 0)); transform-origin:left center;
}

.res-list { list-style: disc; padding-left: 22px; }
.res-item { will-change: opacity, transform; }
.list{ margin:0; padding-left: 18px; color:#0f172a; font-size: 16px; }

/* ✅ 居中按钮 + 与卡片的间距 + 放到右列 */
.actions{ display:flex; gap:10px; justify-content:center; }
.actions-out{ grid-column: 2; margin-top: 22px; }  /* 与上方资源卡片拉开距离 */

.btn{
  border:none; border-radius:12px; padding:10px 16px; font-weight:800; cursor:pointer;
  color:#fff; background: linear-gradient(90deg,#7C3AED,#3B82F6);
  box-shadow: 0 8px 20px rgba(2,6,23,.28), inset 0 0 0 1px rgba(255,255,255,.22);
}
.btn.ghost{ background:#fff; color:#0f172a; border:1px solid #e2e8f0; box-shadow:none; }

/* 卡片样式 */
.card-section{
  background:#fff;
  border-radius:16px;
  box-shadow: 0 22px 48px rgba(15,23,42,.10);
  padding: 22px clamp(18px, 3vw, 28px);
  margin: 18px 0;
  transform-style: preserve-3d;
  will-change: transform, box-shadow, opacity;
}

/* 锁定阶段：仍占位，但不可见 */
.stage-locked{ visibility: hidden; opacity: 0; pointer-events: none; }

.card-section h4{ margin: 0 0 10px; font-size: 16px; font-weight: 800; color:#1e293b; }

/* 响应式 */
@media (max-width: 760px){
  .tips-wrap { grid-template-columns: 1fr; }
  .rail{ display:none; }
  .grid-1{ grid-template-columns: 1fr; }
  .level-row{ grid-template-columns: 1fr; }
  .level-illustration{ justify-self: center; }
  .actions-out{ grid-column: 1; }
}
</style>