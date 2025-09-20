<template>
    <section class="hero" ref="heroRef">
      <!-- 背景光斑 -->
      <i class="glow g1"></i>
      <i class="glow g2"></i>
      <i class="glow g3"></i>
      
      <!-- 新增：动态粒子背景 -->
      <div class="particles-container">
        <div class="particle" v-for="i in 20" :key="i" :style="{ '--delay': i * 0.1 + 's' }"></div>
      </div>
      
      <!-- 新增：浮动元素 -->
      <div class="floating-elements">
        <div class="floating-icon">🎯</div>
        <div class="floating-icon">⚡</div>
        <div class="floating-icon">🚀</div>
        <div class="floating-icon">💡</div>
      </div>
  
      <div class="hero-inner">
        <h1 class="hero-h1">
          <span ref="h1a" class="h1-line">Train your vibe radar</span>
          <br />
          <span ref="h1b" class="h1-line alt">and own the comments.</span>
        </h1>
  
        <p class="hero-sub" ref="sub">
          Tap into smart practice to spot the mood behind every post — keep playing,
          keep leveling up, get scary good at reading emotions online.
        </p>
  
        <!-- 新增：CTA按钮区域 -->
        <div class="hero-cta">
          <button class="cta" @click="$emit('cta')">
            <span class="btn-text">Start practicing</span>
            <div class="btn-glow"></div>
          </button>
          <button class="ghost" @click="$emit('cta')">
            <span class="btn-text">How it works</span>
          </button>
        </div>
  
        <!-- 下滑指示 -->
        <button class="scroll-cue" @click="$emit('cta')" aria-label="Scroll">
          <span class="dot"></span>
          <span class="scroll-text">Scroll down</span>
        </button>
      </div>
    </section>
  </template>
  
  <script setup>
  import { ref, onMounted, nextTick, computed } from 'vue'
  import { gsap } from 'gsap'
  import { TextPlugin } from 'gsap/TextPlugin'
  import { ScrollTrigger } from 'gsap/ScrollTrigger'
  gsap.registerPlugin(TextPlugin, ScrollTrigger)
  
  const heroRef = ref(null)
  const h1a = ref(null)
  const h1b = ref(null)
  const sub = ref(null)
  
  onMounted(async () => {
    await nextTick()
    
    // 简化的标题动画
    gsap.from([h1a.value, h1b.value], {
      y: 28, 
      opacity: 0, 
      duration: .8, 
      ease: 'power3.out', 
      stagger: .08
    })
    
    // 副标题动画
    gsap.from(sub.value, { 
      y: 16, 
      opacity: 0, 
      duration: .7, 
      ease: 'power2.out', 
      delay: .15 
    })
    
    // CTA按钮动画
    gsap.from('.hero-cta', {
      y: 20, 
      opacity: 0, 
      duration: 0.6, 
      ease: 'power2.out', 
      delay: 0.8
    })
    
    // 滚动指示器动画
    gsap.from('.scroll-cue', {
      y: 20, 
      opacity: 0, 
      duration: 0.6, 
      ease: 'power2.out', 
      delay: 1.0
    })
  
    // 背景光斑缓动
    gsap.to('.glow.g1', { x: 40, y: -20, duration: 8, repeat: -1, yoyo: true, ease: 'sine.inOut' })
    gsap.to('.glow.g2', { x: -30, y: 30, duration: 10, repeat: -1, yoyo: true, ease: 'sine.inOut' })
    gsap.to('.glow.g3', { x: 24, y: 16, duration: 12, repeat: -1, yoyo: true, ease: 'sine.inOut' })
    
    // 粒子动画
    gsap.from('.particle', {
      scale: 0,
      opacity: 0,
      duration: 1,
      ease: 'power2.out',
      stagger: 0.1,
      delay: 0.3
    })
    
    // 浮动图标动画
    gsap.from('.floating-icon', {
      y: 30,
      opacity: 0,
      duration: 0.8,
      ease: 'back.out(1.7)',
      stagger: 0.2,
      delay: 0.5
    })
    
    // 浮动图标持续动画
    gsap.to('.floating-icon', {
      y: '+=15',
      duration: 3,
      ease: 'sine.inOut',
      repeat: -1,
      yoyo: true,
      stagger: 0.5
    })
  })
  </script>
  
  <style scoped>
  .hero{
    position: relative;
    height: 750px !important;            /* ✅ 铺满首屏 */
    display: grid; place-items: center;
    overflow: visible;             /* 保留发光完整效果 */
    background:
      radial-gradient(1200px 1200px at 10% 20%, rgba(244,114,182,.18), transparent 60%),
      radial-gradient(1400px 1200px at 80% 45%, rgba(59,130,246,.18), transparent 60%),
      linear-gradient(#eef2ff, #eaf7ff);
  }
  
  /* 粒子背景 */
  .particles-container {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    pointer-events: none;
    z-index: 1;
  }
  
  .particle {
    position: absolute;
    width: 4px;
    height: 4px;
    background: linear-gradient(45deg, #F472B6, #06B6D4, #3B82F6);
    border-radius: 50%;
    opacity: 0.6;
    animation: particle-float 4s ease-in-out infinite;
    animation-delay: var(--delay);
  }
  
  .particle:nth-child(odd) {
    background: linear-gradient(45deg, #F472B6, #EC4899);
  }
  
  .particle:nth-child(even) {
    background: linear-gradient(45deg, #06B6D4, #3B82F6);
  }
  
  @keyframes particle-float {
    0%, 100% {
      transform: translateY(0) scale(1);
      opacity: 0.6;
    }
    50% {
      transform: translateY(-20px) scale(1.2);
      opacity: 1;
    }
  }
  
  /* 浮动图标 */
  .floating-elements {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    pointer-events: none;
    z-index: 2;
  }
  
  .floating-icon {
    position: absolute;
    font-size: 2rem;
    opacity: 0.7;
    filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.1));
  }
  
  .floating-icon:nth-child(1) {
    top: 20%;
    left: 10%;
    animation-delay: 0s;
  }
  
  .floating-icon:nth-child(2) {
    top: 60%;
    right: 15%;
    animation-delay: 1s;
  }
  
  .floating-icon:nth-child(3) {
    bottom: 30%;
    left: 20%;
    animation-delay: 2s;
  }
  
  .floating-icon:nth-child(4) {
    top: 40%;
    right: 25%;
    animation-delay: 3s;
  }
  
  /* 背景光斑 */
  .glow{
    position: absolute; width: 540px; height: 540px; border-radius: 50%;
    filter: blur(64px); opacity: .55; pointer-events: none;
  }
  .g1{ left: -120px; top: 18vh; background: radial-gradient(circle, #f0abfc, transparent 60%); }
  .g2{ right: -160px; top: 30vh; background: radial-gradient(circle, #93c5fd, transparent 60%); }
  .g3{ left: 40%; bottom: -120px; background: radial-gradient(circle, #6ee7b7, transparent 60%); }
  
  .hero-inner{ 
    width: min(960px, 92vw); 
    text-align: center; 
    position: relative;
    margin-top: 0%;
    z-index: 3;
  }
  
  .hero-h1{ 
    margin: 0 0 6px; 
    font-size: clamp(36px, 6.2vw, 64px); 
    line-height: 1.06; 
    letter-spacing: .2px; 
  }
  
  .h1-line{ 
    font-weight: 900; 
    background: linear-gradient(90deg,#7C3AED,#3B82F6); 
    -webkit-background-clip:text; 
    background-clip:text; 
    color:transparent; 
  }
  
  .h1-line.alt{ 
    color:#0f172a; 
    -webkit-text-fill-color: initial; 
    background:none; 
  }
  
  .hero-sub{
    margin: 8px auto 16px; 
    color:#334155; 
    font-size: clamp(16px,1.6vw,18px); 
    max-width: 820px;
  }
  
  .hero-cta{ 
    display: flex; 
    gap: 16px; 
    justify-content: center; 
    margin-top: 32px; 
  }
  
  .cta{
    position: relative;
    font-weight: 800; 
    color:#fff; 
    border:none; 
    border-radius: 16px; 
    padding: 16px 32px; 
    cursor: pointer;
    background: linear-gradient(135deg,#7C3AED,#3B82F6,#06B6D4);
    background-size: 200% 200%;
    box-shadow: 0 12px 32px rgba(2,6,23,.3), inset 0 0 0 1px rgba(255,255,255,.22);
    transition: all 0.3s cubic-bezier(0.68, -0.55, 0.265, 1.55);
    overflow: hidden;
  }
  
  .cta:hover {
    transform: translateY(-2px) scale(1.05);
    box-shadow: 0 16px 40px rgba(2,6,23,.4), inset 0 0 0 1px rgba(255,255,255,.3);
    background-position: 100% 100%;
  }
  
  .btn-text {
    position: relative;
    z-index: 2;
  }
  
  .btn-glow {
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: linear-gradient(90deg, transparent, rgba(255,255,255,0.3), transparent);
    transition: left 0.6s ease;
  }
  
  .cta:hover .btn-glow {
    left: 100%;
  }
  
  .ghost{
    position: relative;
    font-weight: 800; 
    color:#0f172a; 
    background:#fff; 
    border:2px solid #e2e8f0; 
    border-radius:16px; 
    padding:16px 32px; 
    cursor:pointer;
    transition: all 0.3s cubic-bezier(0.68, -0.55, 0.265, 1.55);
    overflow: hidden;
  }
  
  .ghost:hover {
    transform: translateY(-2px) scale(1.05);
    border-color: #7C3AED;
    box-shadow: 0 12px 32px rgba(124, 58, 237, 0.2);
  }
  
  .ghost .btn-text {
    position: relative;
    z-index: 2;
  }
  
  /* 下滑指示 */
  .scroll-cue{
    position: absolute; 
    left: 50%; 
    bottom: 24px; 
    transform: translateX(-50%);
    width: 40px; 
    height: 60px; 
    border-radius: 20px; 
    border: 2px solid rgba(124, 58, 237, 0.3); 
    background: rgba(255, 255, 255, 0.9);
    backdrop-filter: blur(10px);
    cursor: pointer;
    transition: all 0.3s ease;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 4px;
  }
  
  .scroll-cue:hover {
    border-color: rgba(124, 58, 237, 0.6);
    background: rgba(255, 255, 255, 1);
    transform: translateX(-50%) translateY(-2px);
    box-shadow: 0 8px 24px rgba(124, 58, 237, 0.2);
  }
  
  .scroll-cue .dot{
    position: relative;
    width: 8px; 
    height: 8px; 
    border-radius: 999px; 
    background: linear-gradient(45deg, #7C3AED, #3B82F6);
    opacity: 0.9;
    animation: drop 1.6s ease-in-out infinite;
  }
  
  .scroll-text {
    font-size: 10px;
    color: #64748b;
    font-weight: 600;
    text-transform: uppercase;
    letter-spacing: 0.5px;
  }
  
  @keyframes drop{
    0%{ transform: translateY(0); opacity:.9 }
    70%{ transform: translateY(8px); opacity:.2 }
    100%{ transform: translateY(0); opacity:.9 }
  }
  </style>