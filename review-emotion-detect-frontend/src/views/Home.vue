<template>
  <div class="home-hero">
    <div class="bg-gradient" aria-hidden="true"></div>
    <div class="blob blob-1" aria-hidden="true"></div>
    <div class="blob blob-2" aria-hidden="true"></div>
    <div class="blob blob-3" aria-hidden="true"></div>

    <!-- 保持内容定宽居中，但外层 hero 是全宽 -->
    <div class="hero-container hero-content">
      <div class="hero-grid">
        <div class="hero-left">
          <h1 ref="titleRef" class="hero-title gradient-text">
            See the emotion<br />behind a comment
          </h1>

          <p ref="subtitleRef" class="hero-subtitle color-pop">
            A playful, teen-friendly tool to reveal the feelings in any message.
            Paste a comment, tap <strong>Check</strong>, and get instant insights.
          </p>

          <div ref="searchRef" class="hero-search">
            <a-input-search
              v-model:value="demo"
              size="large"
              :enter-button="enterBtn"
              placeholder="Paste your comment here"
              @search="goAnalyze"
              class="hero-searchbar"
            />
            <div class="try color-pop-2">
              Try it! For example: <em>“You don’t know what you’re talking about!”</em>
            </div>
          </div>

          <div class="pill-list">
            <div class="pill s1">Real-time Emotion Check</div>
            <div class="pill s2">Teen-friendly Design</div>
            <div class="pill s3">Private & Safe</div>
          </div>
        </div>

        <div class="hero-right">
          <div class="card emo-card ec1"><span class="emo">😠</span><span class="txt">Anger</span></div>
          <div class="card emo-card ec2"><span class="emo">😊</span><span class="txt">Joy</span></div>
          <div class="card emo-card ec3"><span class="emo">😟</span><span class="txt">Sadness</span></div>
        </div>
      </div>

      <section class="intro" ref="introRef">
        <h2 class="intro-title gradient-text-2">What is MoodLens?</h2>
        <p class="intro-text color-ink">
          MoodLens helps teens (16–21) spot the hidden emotions in online comments—so you can respond thoughtfully,
          avoid misunderstandings, and build healthier conversations. It’s quick, fun, and privacy-aware.
        </p>
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { gsap } from 'gsap'
import { ScrollTrigger } from 'gsap/ScrollTrigger'
gsap.registerPlugin(ScrollTrigger)

const router = useRouter()
const demo = ref('')
const enterBtn = 'Check'
const titleRef = ref(null)
const subtitleRef = ref(null)
const searchRef = ref(null)
const introRef = ref(null)

function goAnalyze() { router.push({ path: '/analyze', query: { q: demo.value } }) }

onMounted(() => {
  const tl = gsap.timeline({ defaults: { ease: 'power3.out' } })
  tl.from(titleRef.value, { y: 48, opacity: 0, duration: 0.8 })
    .from(subtitleRef.value, { y: 28, opacity: 0, duration: 0.6 }, '-=0.4')
    .from(searchRef.value, { y: 16, opacity: 0, duration: 0.6 }, '-=0.35')

  gsap.to('.emo-card.ec1', { y: -12, duration: 2.2, repeat: -1, yoyo: true })
  gsap.to('.emo-card.ec2', { y: -18, duration: 2.6, repeat: -1, yoyo: true, delay: 0.2 })
  gsap.to('.emo-card.ec3', { y: -10, duration: 2.0, repeat: -1, yoyo: true, delay: 0.4 })

  gsap.to('.blob-1', { x: 22, y: -12, duration: 6, repeat: -1, yoyo: true, ease: 'sine.inOut' })
  gsap.to('.blob-2', { x: -26, y: 16, duration: 7, repeat: -1, yoyo: true, ease: 'sine.inOut' })
  gsap.to('.blob-3', { x: 18, y: 24, duration: 8, repeat: -1, yoyo: true, ease: 'sine.inOut' })

  gsap.utils.toArray('.pill').forEach((el, i) => {
    gsap.from(el, {
      scrollTrigger: { trigger: el, start: 'top 85%' },
      y: 20, opacity: 0, duration: 0.5, delay: i * 0.08, ease: 'power2.out'
    })
  })

  gsap.from(introRef.value, {
    scrollTrigger: { trigger: introRef.value, start: 'top 80%' },
    y: 24, opacity: 0, duration: 0.6
  })
})
</script>

<style scoped>
/* ✅ 改成真正全宽：不再用 100vw/left:-50vw 技巧 */
.home-hero{
  position: relative;
  overflow: hidden;
  width: 100%;           /* 填满父级（父级是全宽） */
  margin: 0;
  padding: 80px 0 72px;  /* 顶部避开 Topbar、底部在 Footer 之前收尾 */
}

/* 背景层自动跟随 .home-hero 宽度 */
.bg-gradient{
  position: absolute;
  inset: 0;
  z-index: 0;
  pointer-events: none;
  background:
    radial-gradient(1200px 600px at 10% 10%, rgba(255,255,255,.10), transparent 60%),
    radial-gradient(900px 500px at 90% 20%, rgba(255,170,255,.25), transparent 60%),
    linear-gradient(135deg, #6d28d9 0%, #7c3aed 28%, #3b82f6 60%, #06b6d4 100%);
}
.blob{
  position: absolute; width: 360px; height: 360px; border-radius: 50%;
  filter: blur(60px); opacity: .45; z-index: 1; pointer-events: none;
}
.blob-1{ background:#fb7185; top:8%; left:-80px; }
.blob-2{ background:#22d3ee; bottom:15%; right:-60px; }
.blob-3{ background:#a78bfa; top:40%; right:20%; }

/* 中间内容仍定宽居中 */
.hero-container{ max-width:1120px; margin:0 auto; padding:0 16px; }

/* Grid & Typography（保持不变） */
.hero-grid{ display:grid; grid-template-columns:1.1fr .9fr; gap:24px; align-items:center; }
@media (max-width:960px){ .hero-grid{ grid-template-columns:1fr; } }

.hero-title{ font-size:clamp(38px,6vw,66px); line-height:1.06; margin:8px 0 12px; font-weight:900; }
.gradient-text{
  background: linear-gradient(90deg,#ffedd5 0%,#ff7ab6 35%,#8b5cf6 65%,#22d3ee 100%);
  -webkit-background-clip:text; background-clip:text; -webkit-text-fill-color:transparent;
  text-shadow:0 10px 30px rgba(0,0,0,.22);
}

.hero-subtitle{ margin:6px 0 16px; font-size:clamp(16px,2.2vw,18px); line-height:1.6; }
.color-pop{ color:#f1f5f9; text-shadow:0 1px 2px rgba(0,0,0,.35); }
.color-pop strong{ color:#ffe58f; }

.hero-search{ max-width:560px; }
.hero-searchbar :deep(.ant-input){ background:rgba(255,255,255,.98); color:#0f172a; }
.hero-searchbar :deep(.ant-input::placeholder){ color:#64748b; }
.hero-searchbar :deep(.ant-input-group-addon .ant-btn){
  background: linear-gradient(90deg,#7c3aed,#3b82f6);
  border:none; color:#fff; font-weight:700;
}

.try{ margin-top:10px; font-size:14px; }
.color-pop-2{ color:#e2e8f0; text-shadow:0 1px 2px rgba(0,0,0,.25); }
.try em{ color:#fff; }

.pill-list{ display:flex; gap:10px; flex-wrap:wrap; margin-top:18px; }
.pill{
  backdrop-filter: blur(6px);
  background: rgba(255,255,255,.18);
  color:#fff; border:1px solid rgba(255,255,255,.24);
  border-radius:999px; padding:8px 14px; font-size:13px;
  box-shadow:0 8px 20px rgba(0,0,0,.18);
}

.hero-right{ display:grid; grid-template-columns:repeat(2,minmax(120px,1fr)); gap:14px; align-content:start; justify-items:center; }
.emo-card{
  width:170px; height:126px; border-radius:20px;
  background:rgba(255,255,255,.88); border:1px solid rgba(255,255,255,.9);
  box-shadow:0 18px 40px rgba(0,18,46,.25);
  display:flex; flex-direction:column; align-items:center; justify-content:center; gap:6px; text-align:center;
}
.emo-card .emo{ font-size:42px; }
.emo-card .txt{ font-weight:800; color:#0f172a; }

.intro{ margin-top:56px; max-width:820px; }
.intro-title{ font-size:26px; margin-bottom:8px; font-weight:900; }
.gradient-text-2{
  background: linear-gradient(90deg,#ffe58f 0%,#f472b6 40%,#60a5fa 100%);
  -webkit-background-clip:text; background-clip:text; -webkit-text-fill-color:transparent;
}
.color-ink{ color:#e2e8f0; text-shadow:0 1px 2px rgba(0,0,0,.25); }
</style>