<template>
  <div class="about container">
    <!-- ===== 彩色渐变 + 浮动blob 背景层（不影响内容布局） ===== -->
    <div class="bg-layer">
      <div class="blob b1"></div>
      <div class="blob b2"></div>
      <div class="blob b3"></div>
      <div class="radial-mask"></div>
    </div>

    <!-- ===== Top: stacked cards (left) + hero image (right) ===== -->
    <a-row :gutter="[16, 16]" class="section">
      <!-- Left: About + Mission (stacked) -->
      <a-col :xs="24" :lg="10">
        <div class="stacked-cards" ref="leftColRef">
          <a-card class="about-card fill reveal-up" :bordered="false" data-anim="card">
            <a-typography-title :level="2" class="split-title" style="margin:0 0 8px 0">
              About <span id="brand-about" class="brand brand-anim">ToneLens</span>
            </a-typography-title>
            <a-typography-paragraph type="secondary" style="margin-bottom:12px">
              <span class="brand brand-anim">ToneLens</span> is your review <strong>vibe-check</strong>.
              Paste a short review or comment and instantly see the <strong>emotion mix</strong> —
              then get kinder, clearer ways to say the same thing. Built for fast, friendly,
              <strong>privacy-first</strong> conversations.
            </a-typography-paragraph>
            <div class="badges">
              <a-tag color="blue">Vibe check</a-tag>
              <a-tag color="green">Helpful rewrites</a-tag>
              <a-tag color="purple">Privacy-first</a-tag>
            </div>
          </a-card>

          <a-card class="about-card fill bottom-card reveal-up" :bordered="false" data-anim="card">
            <a-typography-title :level="5" style="margin:0 0 8px 0">Our mission</a-typography-title>
            <a-typography-title :level="4" class="split-title" style="margin:0 0 12px 0">
              Feel the emotion — <span class="brand brand-anim-alt">don’t let it drive</span>
            </a-typography-title>
            <ul class="bullet">
              <li>Spot emotional trends <strong>fast</strong> (joy, anger, surprise, more).</li>
              <li>Get rewrites that keep your meaning but <strong>soften the tone</strong>.</li>
              <li>Improve communication with <strong>no text stored</strong> — you stay in control.</li>
            </ul>
          </a-card>
        </div>
      </a-col>

      <!-- Right: hero image (height synced to left stack on ≥lg) -->
      <a-col :xs="24" :lg="14" class="image-col">
        <div class="visual parallax-card" ref="heroRef" data-anim="parallax">
          <img :src="digital02" alt="Digital citizenship" class="visual-img" />
          <a
            class="credit-badge"
            href="https://www.pexels.com/photo/close-up-photography-of-smartphone-icons-267350/"
            target="_blank" rel="noopener noreferrer"
          >Photo: Pexels</a>
        </div>
      </a-col>
    </a-row>

    <!-- ===== Middle: What it can do & How it works ===== -->
    <a-row :gutter="[16, 16]" class="section">
      <a-col :xs="24" :md="14">
        <a-card class="about-card reveal-up" :bordered="false" data-anim="card">
          <a-typography-title :level="4" class="split-title" style="margin:0 0 12px 0">
            What can <span id="brand-what" class="brand brand-anim">ToneLens</span> do?
          </a-typography-title>

          <div class="feature reveal-fade">
            <div class="feature-title"><span class="brand">See your vibe</span></div>
            <div class="feature-desc">
              Break down emotions (positive, negative, surprise, etc.) with clear
              proportions and a <strong>dominant tone</strong> so you know what lands first.
            </div>
          </div>
          <div class="feature reveal-fade">
            <div class="feature-title"><span class="brand">Say it better</span></div>
            <div class="feature-desc">
              Get <strong>neutral / positive / polite</strong> rewrites that keep your message,
              reduce friction, and still sound like <em>you</em>.
            </div>
          </div>
          <div class="feature reveal-fade">
            <div class="feature-title"><span class="brand">Practice to recognize emotion</span></div>
            <div class="feature-desc">
              Join in the VibeQuest and pratice regularly to recognize emotions in the reviews better.
            </div>
          </div>
        </a-card>
      </a-col>

      <a-col :xs="24" :md="10">
        <a-card class="about-card reveal-up" :bordered="false" data-anim="card" ref="rightStackRef">
          <a-typography-title :level="4" class="split-title" style="margin:0 0 12px 0">
            How it works
          </a-typography-title>
          <div class="steps">
            <div class="step step-pop">
              <div class="dot">1</div>
              <div class="step-body">
                <div class="step-title">Paste your text</div>
                <div class="step-desc">A review, comment, or any short paragraph.</div>
              </div>
            </div>
            
            <div class="step step-pop">
              <div class="dot">2</div>
              <div class="step-body">
                <div class="step-title">See the results</div>
                <div class="step-desc">A clean chart + a short summary of the emotion distribution.</div>
              </div>
            </div>
            <div class="step step-pop">
              <div class="dot">3</div>
              <div class="step-body">
                <div class="step-title">Get suggestions</div>
                <div class="step-desc">Tone-aware rewrites that don’t over-edit your voice.</div>
              </div>
            </div>

            <div class="step step-pop">
              <div class="dot">4</div>
              <div class="step-body">
                <div class="step-title">Pratice regularly</div>
                <div class="step-desc">Test your level of ability in recognising the emotions behind comments.</div>
              </div>
            </div>
          </div>
        </a-card>
      </a-col>
    </a-row>

    <!-- ===== FAQ ===== -->
    <a-card class="about-card section reveal-up" :bordered="false" data-anim="card">
      <a-typography-title :level="4" class="split-title" style="margin:0 0 12px 0">FAQ</a-typography-title>
      <a-collapse class="faq" accordion ghost expand-icon-position="end" @change="onFaqChange">
        <a-collapse-panel key="1">
          <template #header>
            <div class="faq-header">
              <span class="chip chip-q">Q</span>
              <span class="faq-question">Why do some rewrites look close to my original?</span>
            </div>
          </template>
          <div class="faq-answer">
            <span class="chip chip-a">A</span>
            <div class="faq-answer-body">
              To keep meaning and your voice, ToneLens makes <strong>minimal edits</strong> unless the tone is strong.
              Try the <strong>“polite”</strong> or <strong>“positive”</strong> style for a bigger change.
            </div>
          </div>
        </a-collapse-panel>
        <a-collapse-panel key="2">
          <template #header>
            <div class="faq-header">
              <span class="chip chip-q">Q</span>
              <span class="faq-question">Can I clear my results?</span>
            </div>
          </template>
          <div class="faq-answer">
            <span class="chip chip-a">A</span>
            <div class="faq-answer-body">
              Yes — hit <strong>“Clear”</strong> on the Analyze page. We don’t store your content after the session.
            </div>
          </div>
        </a-collapse-panel>
        <a-collapse-panel key="3">
          <template #header>
            <div class="faq-header">
              <span class="chip chip-q">Q</span>
              <span class="faq-question">Which languages are supported?</span>
            </div>
          </template>
          <div class="faq-answer">
            <span class="chip chip-a">A</span>
            <div class="faq-answer-body">
              English works best. Other languages may work, but accuracy can vary.
            </div>
          </div>
        </a-collapse-panel>
      </a-collapse>
    </a-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { gsap } from 'gsap'
import { ScrollTrigger } from 'gsap/ScrollTrigger'
import { TextPlugin } from 'gsap/TextPlugin' 

gsap.registerPlugin(ScrollTrigger, TextPlugin)

import digital02 from '@/assets/digital02.jpg'

const leftColRef = ref(null)
const heroRef = ref(null)
const rightStackRef = ref(null)

/* ===== 旧有的高度同步逻辑（保留） ===== */
function isDesktop() { return window.innerWidth >= 992 }
function syncHeights() {
  if (isDesktop()) {
    if (leftColRef.value && heroRef.value) {
      heroRef.value.style.height = `${leftColRef.value.offsetHeight}px`;
    }
  } else {
    if (heroRef.value) heroRef.value.style.height = 'auto';
  }
}
function onResize() { window.requestAnimationFrame(syncHeights) }

/* ===== FAQ 展开动画 ===== */
function onFaqChange() {
  // 针对当前已展开的面板内容做轻微 reveal
  const openBodies = document.querySelectorAll('.ant-collapse-item-active .faq-answer')
  openBodies.forEach(el => {
    gsap.fromTo(el, { y: 8, opacity: 0 }, { y: 0, opacity: 1, duration: 0.35, ease: 'power2.out' })
  })
}

/* ===== 初始化动画：字符级标题、卡片入场、特性列表、步骤点、视差卡片、背景 blob ===== */
function initAnimations() {
  // 1) 字符级标题
  // const titles = gsap.utils.toArray('.split-title')
  // titles.forEach(t => {
  //   const st = new SplitType(t, { types: 'chars,words' }) // 把标题拆成字符+单词
  //   gsap.from(st.chars, {
  //     scrollTrigger: { trigger: t, start: 'top 85%' },      // 进入视口时触发
  //     opacity: 0,
  //     y: 18,
  //     stagger: 0.015,
  //     duration: 0.55,
  //     ease: 'power3.out'
  //   })
  // })

  // 2) 卡片容器向上浮现
  gsap.utils.toArray('[data-anim="card"]').forEach((card, i) => {
  gsap.fromTo(card,
    { opacity: 0, y: 28 },
    {
      scrollTrigger: { trigger: card, start: 'top 90%', toggleActions: 'play none none reverse' },
      opacity: 1,
      y: 0,
      duration: 0.6,
      ease: 'power2.out',
      delay: i * 0.05
    }
  )
})

// feature 行
gsap.utils.toArray('.reveal-fade').forEach((row) => {
  gsap.fromTo(row,
    { opacity: 0, y: 12 },
    {
      scrollTrigger: { trigger: row, start: 'top 92%' },
      opacity: 1,
      y: 0,
      duration: 0.5,
      ease: 'power2.out'
    }
  )
})

  // 4) 步骤点 dot 弹跳
  gsap.utils.toArray('.step-pop .dot').forEach((dot, idx) => {
    gsap.from(dot, {
      scrollTrigger: { trigger: dot, start: 'top 95%' },
      opacity: 0,
      scale: 0.6,
      y: 10,
      duration: 0.5,
      ease: 'back.out(1.7)',
      delay: idx * 0.05
    })
  })

  // 5) 右侧图卡轻微视差（随滚动平移）
  const parallax = document.querySelector('[data-anim="parallax"]')
  if (parallax) {
    gsap.to(parallax, {
      yPercent: -6,
      ease: 'none',
      scrollTrigger: {
        trigger: parallax,
        start: 'top bottom',
        end: 'bottom top',
        scrub: 0.6
      }
    })
  }

  // 6) 背景 blob 慢速漂移（常驻）
  gsap.to('.blob.b1', { xPercent: 10, yPercent: -8, rotate: 8, duration: 18, repeat: -1, yoyo: true, ease: 'sine.inOut' })
  gsap.to('.blob.b2', { xPercent: -8, yPercent: 10, rotate: -10, duration: 20, repeat: -1, yoyo: true, ease: 'sine.inOut' })
  gsap.to('.blob.b3', { xPercent: 6, yPercent: 6, rotate: 6, duration: 22, repeat: -1, yoyo: true, ease: 'sine.inOut' })

  // 7) 两处品牌词打字 + 光标闪烁
//   const brandTargets = ['#brand-about', '#brand-what']
//   brandTargets.forEach((sel, i) => {
//   const el = document.querySelector(sel)
//   if (!el) return
//   const finalText = el.textContent.trim() || 'ToneLens'

//   ScrollTrigger.create({
//     trigger: el,
//     start: 'top 95%',
//     onEnter: () => {
      
//       gsap.fromTo(el, { text: '' }, {
//         text: finalText,
//         duration: 1.1,
//         ease: 'power2.out',
//         delay: i * 0.05
//       })
//     }
//   })
// })
}

onMounted(async () => {
  await nextTick()

  document.documentElement.classList.add('gradient-ready')

  syncHeights()
  initAnimations()
  window.addEventListener('resize', onResize)
  ScrollTrigger.refresh()
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', onResize)
  // 单页里滚动路由切换时的善后建议：根据需要清理 ScrollTrigger（参考实践文）。 
  // ScrollTrigger.getAll().forEach(t => t.kill()) // 如果该页面卸载时需要彻底移除
})
</script>

<style scoped>
/* ===== 色板（可按品牌色调整） ===== */
:root {
  --c1: #7c3aed; /* violet-600 */
  --c2: #06b6d4; /* cyan-500  */
  --c3: #22c55e; /* green-500 */
  --ink: #0f172a; /* slate-900 */
}

/* ===== Section spacing ===== */
.section { margin-bottom: 24px; position: relative; z-index: 2; }

/* ===== 背景层：彩色渐变 + 模糊 blob ===== */
.bg-layer {
  position: fixed;
  inset: 0;
  z-index: 0;
  pointer-events: none;
  overflow: hidden;
}
.radial-mask{
  position: absolute; inset: -20% -10%;
  background:
    radial-gradient(55% 40% at 20% 10%, rgba(124,58,237,.18), transparent 60%),
    radial-gradient(45% 35% at 80% 20%, rgba(34,197,94,.18), transparent 60%),
    radial-gradient(60% 45% at 50% 90%, rgba(6,182,212,.18), transparent 60%);
  filter: blur(30px);
}
.blob{
  --size: 420px;
  position: absolute;
  width: var(--size); height: var(--size);
  border-radius: 50%;
  filter: blur(40px);
  opacity: .5;
  mix-blend-mode: screen;
}
.blob.b1{ left:-120px; top: -80px; background: radial-gradient(circle at 30% 30%, #a78bfa, #7c3aed 60%); }
.blob.b2{ right:-160px; top: 120px; background: radial-gradient(circle at 60% 40%, #5eead4, #06b6d4 60%); }
.blob.b3{ left: 20%; bottom: -140px; background: radial-gradient(circle at 30% 30%, #86efac, #22c55e 60%); }

/* ===== Cards ===== */
.about-card {
  margin-bottom: 24px;
  border-radius: 16px;
  box-shadow: 0 10px 28px rgba(2, 6, 23, 0.12);
  background:
    linear-gradient(180deg, rgba(255,255,255,.9), rgba(255,255,255,.86)),
    radial-gradient(120% 120% at 10% 0%, #eef2ff, #f5f3ff 60%, #ffffff 100%);
  backdrop-filter: blur(6px);
  border: 1px solid rgba(255,255,255,.4);
  transform: translateZ(0);
  transition: transform .25s ease, box-shadow .25s ease;
}
.about-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 18px 44px rgba(2,6,23,0.18);
}
.stacked-cards { display: flex; flex-direction: column; gap: 16px; height: 100%; }
.fill { flex: 1; display: flex; flex-direction: column; }

/* ===== Image card ===== */
.image-col { display: flex; }
.visual {
  position: relative;
  flex: 1;
  overflow: hidden;
  min-height: 320px;
  border-radius: 28px;
  box-shadow: 0 22px 48px rgba(2,6,23,0.22);
  background: radial-gradient(120% 120% at 10% 0%, #eef2ff, #f5f3ff 60%, #ffffff 100%);
}
.visual-img { width:100%; height:100%; object-fit: cover; transform: scale(1.025); transition: transform .5s ease; }
.visual:hover .visual-img { transform: scale(1.06); }
.credit-badge {
  position: absolute; right: 14px; bottom: 14px;
  font-size: 12px; font-weight: 600; color: #fff;
  background: rgba(15,23,42,.55);
  padding: 6px 10px; border-radius: 999px; text-decoration: none;
  backdrop-filter: blur(6px); border: 1px solid rgba(255,255,255,.18);
}

/* ===== Typography & chips ===== */
.bullet { margin: 0; padding-left: 18px; color: #334155; }
.bullet li { margin: 6px 0; }
.feature { margin-bottom: 14px; }
.feature-title { font-weight: 700; color: var(--ink); margin-bottom: 4px; letter-spacing: .1px; }
.feature-desc { color: #475569; }
.steps { display: grid; gap: 12px; }
.step { display: grid; grid-template-columns: 28px 1fr; gap: 10px; align-items: start; }
.dot {
  width: 28px; height: 28px; border-radius: 50%;
  display: grid; place-items: center;
  font-weight: 800; font-size: 13px; color: var(--ink);
  background: #eef2ff; border: 1px solid #c7d2fe; box-shadow: 0 4px 10px rgba(37, 99, 235, .12);
}
.step-title { font-weight: 700; color: var(--ink); }
.step-desc { color: #64748b; }
.badges { display: flex; gap: 8px; flex-wrap: wrap; }
/* .brand{
  font-weight: 800;
  display: inline-block;
  color: #7c3aed;
}

@supports (-webkit-background-clip: text) or (background-clip: text) {
  .brand{
    background: linear-gradient(90deg, var(--c1), var(--c2), var(--c3));
    -webkit-background-clip: text;
    background-clip: text;
    -webkit-text-fill-color: transparent;
    color: transparent; 
  }
}

.brand-anim{
  background-size: 300% 100%;
  animation: gradientShift 6s linear infinite;
}
@keyframes gradientShift{
  0% { background-position: 0% 50%; }
  100% { background-position: 100% 50%; }
}

.brand-anim-alt{
  font-weight: 800;
  display: inline-block;
  color: #6366f1; 
}
@supports (-webkit-background-clip: text) or (background-clip: text) {
  .brand-anim-alt{
    background: linear-gradient(90deg, #6366f1, #22c55e);
    -webkit-background-clip: text;
    background-clip: text;
    -webkit-text-fill-color: transparent;
    color: transparent;
    background-size: 200% 100%;
    animation: gradientShift 8s linear infinite;
  }
} */
/* .brand {
  color: #7c3aed; 
  font-weight: 800;
  display: inline-block;
}

.brand-anim, .brand-anim-alt {
  
  background: none;
} */

/* ------- Gradient text: safe-first (先可见后渐变) ------- */
.brand{
  /* 1) 兜底纯色：任何情况下都能看到 */
  color: #7c3aed;
  font-weight: 800;
  display: inline-block;
  line-height: 1.1; /* 避免某些字体裁切 */
}
.brand-anim,
.brand-anim-alt{
  -webkit-text-fill-color: currentColor;
  color: currentColor;
}

/* 页面就绪后再把渐变打开，避免某些浏览器在解析前短暂不可见 */
:root.gradient-ready .brand.brand-anim{
  background-image: linear-gradient(90deg, var(--c1), var(--c2), var(--c3));
  background-size: 300% 100%;
  background-position: 0% 50%;
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
  color: transparent;
  animation: brandGradientShift 6s linear infinite;
}

/* 备用的双色渐变（用于 “don’t let it drive” ） */
:root.gradient-ready .brand.brand-anim-alt{
  background-image: linear-gradient(90deg, #6366f1, #22c55e);
  background-size: 200% 100%;
  background-position: 0% 50%;
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
  color: transparent;
  animation: brandGradientShift 8s linear infinite;
}

/* 统一的渐变位移动画 */
@keyframes brandGradientShift{
  0%   { background-position: 0% 50%; }
  100% { background-position: 100% 50%; }
}

/* 动画减弱：尊重系统“减少动态效果”设置 */
@media (prefers-reduced-motion: reduce){
  :root.gradient-ready .brand,
  :root.gradient-ready .brand-anim-alt{
    animation: none;
    background-position: 50% 50%;
  }
}
/* FAQ visuals */
.faq :deep(.ant-collapse-item) { border-bottom: 1px solid #e5e7eb; padding: 6px 0; }
.faq :deep(.ant-collapse-header) { padding: 8px 0 !important; }
.faq-header { display: flex; align-items: center; gap: 10px; }
.faq-question { font-weight: 800; color: var(--ink); }
.chip {
  display: inline-flex; align-items: center; justify-content: center;
  height: 22px; min-width: 22px; padding: 0 6px; border-radius: 999px;
  font-size: 12px; font-weight: 800;
}
.chip-q { background: #eef2ff; color: #3730a3; border: 1px solid #c7d2fe; }
.chip-a { background: #ecfeff; color: #155e75; border: 1px solid #a5f3fc; }
.faq-answer {
  display: grid; grid-template-columns: 28px 1fr; gap: 10px;
  background: #f8fafc; border: 1px solid #e2e8f0; border-left: 4px solid #22d3ee;
  border-radius: 12px; padding: 12px; color: var(--ink);
}
.faq-answer-body { color: #334155; }

/* Mobile: let images auto-height */
@media (max-width: 991.98px) {
  .visual { height: auto !important; }
}

/* 小的入场初态（增强首次渲染体验） */
.reveal-up { will-change: transform, opacity; }
</style>

<style>
:root{
  --c1: #7c3aed; /* violet-600 */
  --c2: #06b6d4; /* cyan-500 */
  --c3: #22c55e; /* green-500 */
}
/* === 全局：页面 ready 后开启渐变；覆盖自身和内部任何包裹 === */
:root.gradient-ready .brand.brand-anim,
:root.gradient-ready .brand.brand-anim *{
  background-image: linear-gradient(
    90deg,
    var(--c1, #7c3aed),
    var(--c2, #06b6d4),
    var(--c3, #22c55e)
  ) !important;
  background-size: 300% 100%;
  background-position: 0% 50%;
  -webkit-background-clip: text !important;
  background-clip: text !important;
  -webkit-text-fill-color: transparent !important;
  color: transparent !important;
  animation: brandGradientShift 6s linear infinite;
}
:root.gradient-ready .brand.brand-anim-alt,
:root.gradient-ready .brand.brand-anim-alt * {
  -webkit-background-clip: text !important;
  background-clip: text !important;
  -webkit-text-fill-color: transparent !important;
  color: transparent !important;
}

/* 三色渐变（主用） */
:root.gradient-ready .brand.brand-anim,
:root.gradient-ready .brand.brand-anim * {
  background-image: linear-gradient(90deg, var(--c1), var(--c2), var(--c3)) !important;
  background-size: 300% 100%;
  background-position: 0% 50%;
  animation: brandGradientShift 6s linear infinite;
}

/* 双色渐变（don’t let it drive） */
:root.gradient-ready .brand.brand-anim-alt,
:root.gradient-ready .brand.brand-anim-alt * {
  background-image: linear-gradient(90deg, #6366f1, #22c55e) !important;
  background-size: 200% 100%;
  background-position: 0% 50%;
  animation: brandGradientShift 8s linear infinite;
}

.about.container {
  padding-block: 24px; 
}

.has-sticky-topbar .about.container,
.ant-layout-header + .about.container {
  padding-top: calc(24px + 64px);
}

.about.container > .section:last-of-type {
  margin-bottom: 32px; 
}
</style>