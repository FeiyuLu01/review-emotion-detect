<template>
    <section ref="heroRef" class="hero-wrap" aria-label="Analyze Hero">
      <!-- 彩色动感标题 -->
      <h1 class="hero-title">
        <span class="line">Feel the Vibes.</span>
        <span class="line">Decode Emotions.</span>
        <span class="line">Rewrite the Tone.</span>
      </h1>
  
      <!-- 副标题：概括功能，吸引 16-21 岁 -->
      <p class="hero-sub">
        Paste a review → AI maps feelings → See insights → Glow up your words.
      </p>
  
      <!-- CTA：滚动到功能区 -->
      <button class="hero-cta" @click="scrollToMain" aria-label="Start Analyzing">
        Start Analyzing
        <svg viewBox="0 0 24 24" class="chev"><path d="M6 9l6 6 6-6"/></svg>
      </button>
  
      <!-- 背景装饰：柔光粒子（轻量，不阻塞） -->
      <div class="hero-bg">
        <div class="dot d1"></div>
        <div class="dot d2"></div>
        <div class="dot d3"></div>
      </div>
    </section>
  </template>
  
  <script setup lang="ts">
  import { onMounted, onBeforeUnmount, ref } from 'vue'
  import { gsap } from 'gsap'
  import { ScrollTrigger } from 'gsap/ScrollTrigger'
  
  gsap.registerPlugin(ScrollTrigger)
  
  const heroRef = ref<HTMLElement | null>(null)
  
  function scrollToMain() {
    const el = document.querySelector('#analyzeMain')
    if (!el) return
    const top = (el as HTMLElement).getBoundingClientRect().top + window.scrollY - 8
    window.scrollTo({ top, behavior: 'smooth' })
  }
  
  let ctx: gsap.Context | null = null
  onMounted(() => {
    ctx = gsap.context(() => {
      // 标题行：入场弹性 + 色带闪烁
      gsap.set('.hero-title .line', { y: 30, opacity: 0 })
      gsap.to('.hero-title .line', {
        y: 0, opacity: 1, duration: 0.8, ease: 'power3.out', stagger: 0.12
      })
      gsap.from('.hero-sub', { y: 20, opacity: 0, duration: 0.7, delay: 0.2, ease: 'power2.out' })
      gsap.from('.hero-cta', { y: 14, opacity: 0, duration: 0.6, delay: 0.35, ease: 'power2.out' })
  
      // 滚动时：Hero 渐隐缩小，内容区上浮（在页面里注册触发点 #analyzeMain）
      ScrollTrigger.create({
        trigger: heroRef.value,
        start: 'top top',
        end: 'bottom top',
        scrub: 0.5,
        onUpdate: (self) => {
          const p = self.progress // 0 -> 1
          gsap.to(heroRef.value, { opacity: 1 - p * 0.9, scale: 1 - p * 0.06, overwrite: true })
        },
      })
  
      // 背景点柔和漂移（循环）
      const tl = gsap.timeline({ repeat: -1, yoyo: true, defaults: { duration: 6, ease: 'sine.inOut' } })
      tl.to('.d1', { xPercent: -10, yPercent: -6 })
        .to('.d2', { xPercent: 12,  yPercent:  8 }, '<')
        .to('.d3', { xPercent: -6,  yPercent: 10 }, '<')
    }, heroRef)
  })
  
  onBeforeUnmount(() => ctx?.revert())
  </script>
  
  <style scoped>
  .hero-wrap {
    position: relative;
    height: 100vh;
    min-height: 560px;
    display: grid;
    place-content: center;
    text-align: center;
    overflow: hidden;
    padding: 0 24px;
    /* 深色基底 + 轻微纹理渐变 */
    background:
      radial-gradient(1200px 800px at 10% 10%, #171a2b 0%, #0f1222 60%, #0a0d1a 100%);
  }
  
  .hero-title {
    margin: 0 0 10px 0;
    line-height: 1.06;
    font-weight: 900;
    letter-spacing: -0.02em;
    font-size: clamp(36px, 7vw, 72px);
    /* 动态彩色渐变文字 */
    background: linear-gradient(90deg,
      #ffb3c1, #9b5de5, #00bbf9, #ffd6a5, #ff006e, #22c55e, #60a5fa);
    background-size: 300% 100%;
    -webkit-background-clip: text;
    background-clip: text;
    color: transparent;
    animation: hueShift 10s linear infinite;
  }
  .hero-title .line { display: block; }
  
  @keyframes hueShift {
    0% { background-position: 0% 50% }
    100% { background-position: 300% 50% }
  }
  
  .hero-sub {
    margin: 6px 0 18px;
    color: #cbd5e1;
    font-size: clamp(14px, 2.4vw, 18px);
  }
  
  .hero-cta {
    margin: 10px auto 0;
    display: inline-flex;
    align-items: center;
    gap: 8px;
    border: 0;
    border-radius: 999px;
    font-weight: 700;
    padding: 12px 18px;
    cursor: pointer;
    transition: transform .18s ease, box-shadow .18s ease;
    background: linear-gradient(#0b1324, #0b1324) padding-box,
                linear-gradient(135deg,#06B6D4,#7C3AED,#ff006e) border-box;
    color: #e2f2ff;
    box-shadow: 0 8px 30px rgba(14, 22, 43, .45);
  }
  .hero-cta:hover { transform: translateY(-2px); }
  .hero-cta .chev { width: 18px; height: 18px; fill: none; stroke: currentColor; stroke-width: 2.4; }
  
  .hero-bg { pointer-events: none; position: absolute; inset: 0; }
  .dot {
    position: absolute; width: 28vmin; height: 28vmin; border-radius: 50%;
    filter: blur(38px); opacity: .5; mix-blend-mode: screen;
  }
  .d1 { left: -6vmin; top: -6vmin; background: #9b5de5; }
  .d2 { right: -10vmin; top: 18vmin; background: #00bbf9; }
  .d3 { left: 20vmin; bottom: -12vmin; background: #ff006e; }
  
  @media (max-width: 768px) {
    .dot { width: 48vmin; height: 48vmin; filter: blur(56px); }
  }
  </style>