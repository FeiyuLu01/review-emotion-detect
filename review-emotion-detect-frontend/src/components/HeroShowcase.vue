<template>
    <section class="hero" ref="heroRef">
      <!-- 背景光斑 -->
      <i class="glow g1"></i>
      <i class="glow g2"></i>
      <i class="glow g3"></i>
  
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
  
        <!-- <div class="hero-cta">
          <button class="cta" @click="$emit('cta')">Start practicing</button>
          <button class="ghost" @click="$emit('cta')">How it works</button>
        </div> -->
  
        <!-- 下滑指示 -->
        <button class="scroll-cue" @click="$emit('cta')" aria-label="Scroll">
          <span class="dot"></span>
        </button>
      </div>
    </section>
  </template>
  
  <script setup>
  import { ref, onMounted, nextTick } from 'vue'
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
    // 首屏入场（TextPlugin + 轻微位移）
    gsap.from([h1a.value, h1b.value], {
      y: 28, opacity: 0, duration: .8, ease: 'power3.out', stagger: .08
    })
    gsap.from(sub.value, { y: 16, opacity: 0, duration: .7, ease: 'power2.out', delay: .15 })
  
    // 背景光斑缓动
    gsap.to('.glow.g1', { x: 40, y: -20, duration: 8, repeat: -1, yoyo: true, ease: 'sine.inOut' })
    gsap.to('.glow.g2', { x: -30, y: 30, duration: 10, repeat: -1, yoyo: true, ease: 'sine.inOut' })
    gsap.to('.glow.g3', { x: 24, y: 16, duration: 12, repeat: -1, yoyo: true, ease: 'sine.inOut' })
  })
  </script>
  
  <style scoped>
  .hero{
    position: relative;
    min-height: 100vh;            /* ✅ 铺满首屏 */
    display: grid; place-items: center;
    overflow: visible;             /* 保留发光完整效果 */
    background:
      radial-gradient(1200px 1200px at 10% 20%, rgba(244,114,182,.18), transparent 60%),
      radial-gradient(1400px 1200px at 80% 45%, rgba(59,130,246,.18), transparent 60%),
      linear-gradient(#eef2ff, #eaf7ff);
  }
  
  /* 背景光斑 */
  .glow{
    position: absolute; width: 540px; height: 540px; border-radius: 50%;
    filter: blur(64px); opacity: .55; pointer-events: none;
  }
  .g1{ left: -120px; top: 18vh; background: radial-gradient(circle, #f0abfc, transparent 60%); }
  .g2{ right: -160px; top: 30vh; background: radial-gradient(circle, #93c5fd, transparent 60%); }
  .g3{ left: 40%; bottom: -120px; background: radial-gradient(circle, #6ee7b7, transparent 60%); }
  
  .hero-inner{ width: min(960px, 92vw); text-align: center; }
  .hero-h1{ margin: 0 0 6px; font-size: clamp(36px, 6.2vw, 64px); line-height: 1.06; letter-spacing: .2px; }
  .h1-line{ font-weight: 900; background: linear-gradient(90deg,#7C3AED,#3B82F6); -webkit-background-clip:text; background-clip:text; color:transparent; }
  .h1-line.alt{ color:#0f172a; -webkit-text-fill-color: initial; background:none; }
  
  .hero-sub{
    margin: 8px auto 16px; color:#334155; font-size: clamp(16px,1.6vw,18px); max-width: 820px;
  }
  
  .hero-cta{ display: flex; gap: 10px; justify-content: center; margin-top: 12px; }
  .cta{
    font-weight: 800; color:#fff; border:none; border-radius: 12px; padding: 12px 18px; cursor: pointer;
    background: linear-gradient(90deg,#7C3AED,#3B82F6);
    box-shadow: 0 10px 24px rgba(2,6,23,.25), inset 0 0 0 1px rgba(255,255,255,.22);
  }
  .ghost{
    font-weight: 800; color:#0f172a; background:#fff; border:1px solid #e2e8f0; border-radius:12px; padding:12px 16px; cursor:pointer;
  }
  
  /* 下滑指示 */
  .scroll-cue{
    position: absolute; left: 50%; bottom: 24px; transform: translateX(-50%);
    width: 28px; height: 44px; border-radius: 16px; border: 2px solid rgba(30,41,59,.25); background: transparent;
  }
  .scroll-cue .dot{
    position: absolute; left: 50%; top: 8px; transform: translateX(-50%);
    width: 6px; height: 6px; border-radius: 999px; background:#64748b; opacity:.9;
    animation: drop 1.6s ease-in-out infinite;
  }
  @keyframes drop{
    0%{ transform: translate(-50%,0); opacity:.9 }
    70%{ transform: translate(-50%,14px); opacity:.2 }
    100%{ transform: translate(-50%,0); opacity:.9 }
  }
  </style>