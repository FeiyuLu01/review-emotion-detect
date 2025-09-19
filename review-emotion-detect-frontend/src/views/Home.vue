<template>
  <div class="home-container">
    <div class="main-content">
      <!-- 左侧内容区 -->
      <div class="left-section">
      <div class="hero-content">
        <h1 ref="titleRef" class="title">
          <span class="title-main">Discover the Emotion</span>
          <span class="title-sub">Behind Every Review</span>
        </h1>
        
        <p ref="subtitleRef" class="subtitle">
          Unlock the hidden emotions in customer feedback with our advanced AI-powered sentiment analysis. 
          Understand not just what customers say, but how they truly feel.
        </p>
      </div>
      
      <div ref="searchRef" class="search-area">
        <div class="input-wrapper">
          <input 
            v-model="demo" 
            type="text" 
            placeholder="Paste your review or comment here..."
            class="input-field"
            @keyup.enter="goAnalyze"
          />
          <button @click="goAnalyze" class="btn-check">
            <span class="btn-text">Analyze Emotion</span>
            <span class="btn-icon">🔍</span>
          </button>
        </div>
        
        <div class="example">
          <span class="example-text">💡 Try it out with examples like:</span>
          <div class="example-quotes">
            <span class="example-quote">"You don't know what you're talking about!"</span>
            <span class="example-quote">"Absolutely love this product! Best purchase ever!"</span>
            <span class="example-quote">"The service was okay, nothing special though."</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧插图区 -->
    <div class="right-section">
      <div class="illustration">
        <!-- 小男孩图片 -->
        <div class="character-image">
          <div class="image-container">
            <img src="/src/assets/Home1.png" alt="little boy" class="boy-image" />
          </div>
        </div>
        
        <!-- 表情气泡 -->
        <div class="emotion-bubble sad-bubble">
          <span class="bubble-emoji">😞</span>
          <span class="bubble-label">Sad</span>
        </div>
        <div class="emotion-bubble angry-bubble">
          <span class="bubble-emoji">👎</span>
          <span class="bubble-label">Angry</span>
        </div>
        
        <!-- 新增正面情绪气泡 -->
        <div class="emotion-bubble happy-bubble">
          <span class="bubble-emoji">😊</span>
          <span class="bubble-label">Happy</span>
        </div>
        <div class="emotion-bubble neutral-bubble">
          <span class="bubble-emoji">😐</span>
          <span class="bubble-label">Neutral</span>
        </div>
        
        <!-- 背景装饰元素 -->
        <div class="bg-decoration circle-1"></div>
        <div class="bg-decoration circle-2"></div>
        <div class="bg-decoration circle-3">        </div>
      </div>
    </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { gsap } from 'gsap'

const router = useRouter()
const demo = ref('')
const titleRef = ref(null)
const subtitleRef = ref(null)
const searchRef = ref(null)

function goAnalyze() { 
  if (demo.value.trim()) {
    router.push({ path: '/analyze', query: { q: demo.value } })
  }
}

function handleExampleClick(quote) {
  demo.value = quote.replace(/"/g, '')
}

onMounted(() => {
  // 增强的进入动画
  const tl = gsap.timeline({ defaults: { ease: 'power2.out' } })
  tl.from(titleRef.value, { y: 40, opacity: 0, duration: 1 })
    .from(subtitleRef.value, { y: 20, opacity: 0, duration: 0.8 }, '-=0.6')
    .from(searchRef.value, { y: 20, opacity: 0, duration: 0.6 }, '-=0.4')

  // 表情气泡的浮动动画
  gsap.to('.sad-bubble', { 
    y: -8, 
    duration: 2, 
    repeat: -1, 
    yoyo: true, 
    ease: 'sine.inOut' 
  })
  
  gsap.to('.angry-bubble', { 
    y: -6, 
    duration: 2.5, 
    repeat: -1, 
    yoyo: true, 
    ease: 'sine.inOut',
    delay: 0.5
  })

  gsap.to('.happy-bubble', { 
    y: -10, 
    duration: 2.2, 
    repeat: -1, 
    yoyo: true, 
    ease: 'sine.inOut',
    delay: 1
  })

  gsap.to('.neutral-bubble', { 
    y: -7, 
    duration: 2.8, 
    repeat: -1, 
    yoyo: true, 
    ease: 'sine.inOut',
    delay: 0.8
  })

  // 为示例添加点击事件
  const exampleQuotes = document.querySelectorAll('.example-quote')
  exampleQuotes.forEach(quote => {
    quote.addEventListener('click', () => {
      handleExampleClick(quote.textContent)
    })
  })

  // 功能卡片的进入动画
  const featureCards = document.querySelectorAll('.feature-card')
  const observer = new IntersectionObserver((entries) => {
    entries.forEach((entry, index) => {
      if (entry.isIntersecting) {
        gsap.fromTo(entry.target, 
          { y: 50, opacity: 0 },
          { y: 0, opacity: 1, duration: 0.6, delay: index * 0.2, ease: 'power2.out' }
        )
        observer.unobserve(entry.target)
      }
    })
  }, { threshold: 0.1 })
  
  featureCards.forEach(card => observer.observe(card))
})
</script>

<style scoped>
/* 主容器 */
.home-container {
  height: 100%;
  background: linear-gradient(135deg, #8b5cf6 0%, #7c3aed 50%, #6d28d9 100%);
  display: flex;
  flex-direction: column;
  width: 100%;
  padding: 0;
  margin: 0;
}

/* 主要内容区域 */
.main-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0;
  align-items: center;
}

/* 左侧内容区 */
.left-section {
  padding: 50px 60px;
  color: white;
}

/* 右侧插图区 */
.right-section {
  padding: 60px;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
}

.hero-content {
  margin-bottom: 50px;
}

.title {
  font-size: clamp(2.8rem, 5.5vw, 4rem);
  font-weight: 900;
  line-height: 1.1;
  margin-bottom: 20px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.title-main {
  display: block;
  color: white;
}

.title-sub {
  display: block;
  color: #fbbf24;
  font-size: 0.85em;
  margin-top: 8px;
}

.subtitle {
  font-size: 18px;
  line-height: 1.6;
  color: #e2e8f0;
  margin-bottom: 0;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
  max-width: 90%;
}

.search-area {
  max-width: 500px;
}

.input-wrapper {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.input-field {
  flex: 1;
  padding: 16px 20px;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  background: white;
  color: #1f2937;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transition: box-shadow 0.2s ease;
}

.input-field:focus {
  outline: none;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.2);
}

.input-field::placeholder {
  color: #9ca3af;
}

.btn-check {
  padding: 16px 24px;
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 700;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.4);
  transition: all 0.3s ease;
  white-space: nowrap;
  display: flex;
  align-items: center;
  gap: 8px;
  position: relative;
  overflow: hidden;
}

.btn-check:hover {
  background: linear-gradient(135deg, #2563eb 0%, #1e40af 100%);
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(59, 130, 246, 0.5);
}

.btn-check:active {
  transform: translateY(0);
}

.btn-text {
  z-index: 1;
}

.btn-icon {
  font-size: 18px;
  z-index: 1;
}

.example {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-top: 24px;
}

.example-text {
  font-size: 16px;
  color: #e2e8f0;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
  font-weight: 500;
}

.example-quotes {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.example-quote {
  font-size: 15px;
  color: white;
  font-style: italic;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
  padding: 8px 12px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  border-left: 3px solid #fbbf24;
  cursor: pointer;
  transition: all 0.2s ease;
}

.example-quote:hover {
  background: rgba(255, 255, 255, 0.15);
  transform: translateX(4px);
}

/* 右侧插图区 */
.right-section {
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
}

.illustration {
  position: relative;
  width: 380px;
  height: 450px;
}

.character-image {
  position: relative;
  width: 280px;
  height: 350px;
  margin: 30px auto;
  display: flex;
  align-items: center;
  justify-content: center;
}

.image-container {
  position: relative;
  width: 100%;
  height: 100%;
  background: 
    radial-gradient(circle at center, rgba(255, 255, 255, 0.08) 0%, transparent 70%),
    linear-gradient(135deg, rgba(139, 92, 246, 0.1) 0%, rgba(124, 58, 237, 0.1) 100%);
  border-radius: 50%;
  padding: 15px;
  display: flex;
  align-items: center;
  justify-content: center;
  /* 添加微妙的边框光晕 */
  box-shadow: 
    inset 0 0 30px rgba(255, 255, 255, 0.1),
    0 0 40px rgba(255, 255, 255, 0.05);
  /* 添加背景混合模式 */
  background-blend-mode: overlay, normal;
}

.boy-image {
  width: 180%;
  height: 180%;
  object-fit: contain;
  /* 尝试使用混合模式 */
  mix-blend-mode: normal;
  margin-top: 80px;
  border-radius: 30px;
}

/* 表情气泡 */
.emotion-bubble {
  position: absolute;
  border-radius: 16px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.25);
  color: white;
  backdrop-filter: blur(10px);
  border: 2px solid rgba(255, 255, 255, 0.2);
  transition: all 0.3s ease;
}

.emotion-bubble:hover {
  transform: scale(1.1);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.35);
}

.bubble-emoji {
  font-size: 24px;
  margin-bottom: 4px;
}

.bubble-label {
  font-size: 12px;
  font-weight: 600;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
}

.sad-bubble {
  top: 20px;
  right: 20px;
  width: 75px;
  height: 85px;
  background: linear-gradient(135deg, #fbbf24 0%, #f59e0b 100%);
}

.angry-bubble {
  top: 60px;
  left: 15px;
  width: 65px;
  height: 75px;
  background: linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%);
}

.happy-bubble {
  top: 160px;
  right: 10px;
  width: 70px;
  height: 80px;
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
}

.neutral-bubble {
  bottom: -10px;
  left: 25px;
  width: 65px;
  height: 75px;
  background: linear-gradient(135deg, #6b7280 0%, #4b5563 100%);
}

/* 背景装饰元素 */
.bg-decoration {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.circle-1 {
  top: -8%;
  left: 0%;
  width: 100px;
  height: 100px;
  animation: float 6s ease-in-out infinite;
}

.circle-2 {
  bottom: 20%;
  right: 15%;
  width: 80px;
  height: 80px;
  animation: float 8s ease-in-out infinite reverse;
}

.circle-3 {
  top: 60%;
  left: 5%;
  width: 60px;
  height: 60px;
  animation: float 10s ease-in-out infinite;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
    opacity: 0.7;
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
    opacity: 0.3;
  }
}

/* 功能亮点区域 */
.features-section {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(20px);
  border-top: 1px solid rgba(255, 255, 255, 0.2);
  padding: 60px 0;
}

.features-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 40px;
}

.features-title {
  text-align: center;
  font-size: 2.5rem;
  font-weight: 800;
  color: white;
  margin-bottom: 50px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 30px;
  align-items: start;
}

.feature-card {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 20px;
  padding: 30px;
  text-align: center;
  transition: all 0.3s ease;
  color: white;
}

.feature-card:hover {
  transform: translateY(-8px);
  background: rgba(255, 255, 255, 0.15);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.2);
}

.feature-icon {
  font-size: 3rem;
  margin-bottom: 20px;
  display: block;
}

.feature-title {
  font-size: 1.5rem;
  font-weight: 700;
  margin-bottom: 15px;
  color: white;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
}

.feature-description {
  font-size: 1rem;
  line-height: 1.6;
  color: #e2e8f0;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .main-content {
    grid-template-columns: 1fr;
    gap: 0;
    text-align: center;
  }
  
  .left-section {
    padding: 60px 40px;
  }
  
  .right-section {
    padding: 40px 40px 60px;
  }
  
  .illustration {
    width: 320px;
    height: 400px;
  }
  
  .character-image {
    width: 240px;
    height: 300px;
  }
  
  .subtitle {
    max-width: 100%;
    text-align: center;
  }
}

@media (max-width: 768px) {
  .left-section {
    padding: 50px 30px;
  }
  
  .right-section {
    padding: 30px 30px 50px;
  }
  
  .illustration {
    width: 280px;
    height: 350px;
  }
  
  .character-image {
    width: 200px;
    height: 250px;
  }
  
  .input-wrapper {
    flex-direction: column;
    gap: 16px;
  }
  
  .btn-check {
    width: 100%;
    justify-content: center;
  }
  
  .example-quotes {
    gap: 6px;
  }
  
  .example-quote {
    font-size: 14px;
    padding: 6px 10px;
  }
  
  /* 调整气泡位置和大小 */
  .sad-bubble, .angry-bubble, .happy-bubble, .neutral-bubble {
    transform: scale(0.8);
  }
}

@media (max-width: 480px) {
  .left-section {
    padding: 30px 20px;
  }
  
  .right-section {
    padding: 20px 20px 30px;
  }
  
  .illustration {
    width: 240px;
    height: 300px;
  }
  
  .character-image {
    width: 170px;
    height: 210px;
  }
  
  .title {
    font-size: clamp(2rem, 8vw, 2.5rem);
  }
  
  .subtitle {
    font-size: 16px;
  }
  
  .input-field {
    font-size: 16px; /* 防止iOS缩放 */
  }
  
  .example-quote {
    font-size: 13px;
    padding: 5px 8px;
  }
  
  /* 进一步缩小气泡 */
  .sad-bubble, .angry-bubble, .happy-bubble, .neutral-bubble {
    transform: scale(0.7);
  }
  
  .bubble-label {
    font-size: 9px;
  }
}

/* 功能亮点区域响应式 */
@media (max-width: 768px) {
  .features-section {
    padding: 40px 0;
  }
  
  .features-container {
    padding: 0 20px;
  }
  
  .features-title {
    font-size: 2rem;
    margin-bottom: 30px;
  }
  
  .features-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .feature-card {
    padding: 25px;
  }
  
  .feature-icon {
    font-size: 2.5rem;
  }
  
  .feature-title {
    font-size: 1.3rem;
  }
  
  .feature-description {
    font-size: 0.9rem;
  }
}
</style>