<template>
  <!-- Hero section with CTA to scroll to modes -->
  <HeroShowcase ref="heroRef" @cta="scrollToModes" />

  <section class="test-hero" ref="modesRef">
    <!-- Decorative orbit background -->
    <svg class="orbits" viewBox="0 0 600 600" aria-hidden="true">
      <defs>
        <radialGradient id="g" cx="50%" cy="50%">
          <stop offset="0%" stop-color="#fff" stop-opacity="0.9" />
          <stop offset="100%" stop-color="#fff" stop-opacity="0" />
        </radialGradient>
      </defs>
      <circle cx="300" cy="300" r="140" fill="url(#g)"/>
      <circle cx="300" cy="300" r="220" fill="url(#g)"/>
    </svg>

    <div class="container">
      <!-- Page title with gradient reveal -->
      <h1 class="title">
        <span ref="titlePlainRef" class="title-line title-plain">Select Your</span>
        <span ref="titleGradRef" class="title-line title-gradient" aria-label="Test Mode">Test Mode</span>
      </h1>

      <!-- Tagline words flip-in animation -->
      <p ref="taglineRef" class="tagline">
        Practice spotting emotions like a pro — pick a vibe, smash the quiz, level up.
      </p>

      <!-- Mode cards (click to start) -->
      <ul class="mode-stack">
        <li
          v-for="m in modes"
          :key="m.key"
          class="mode-card"
          :class="`--${m.key.toLowerCase()}`"
          role="button"
          tabindex="0"
          :data-key="m.key"
          @click="startTest(m.key)"
          @mouseenter="handleEnter(m.key)"
          @mouseleave="handleLeave(m.key)"
          @focusin="handleEnter(m.key)"
          @focusout="handleLeave(m.key)"
        >
          <span class="aura" aria-hidden="true"></span>
          <span class="shine-clip" aria-hidden="true"><span class="shine"></span></span>

          <div class="mode-left"><div class="mode-emoji">{{ m.emoji }}</div></div>
          <div class="mode-main">
            <h2 class="mode-title">
              {{ m.title }} <span class="mode-badge">{{ m.badge }}</span>
            </h2>
            <p class="mode-desc">{{ m.desc }}</p>
            <div class="perks"><span v-for="p in m.perks" :key="p" class="perk">{{ p }}</span></div>
          </div>
          <div class="mode-right">
            <button class="start-btn" @mouseenter.stop="handleEnter(m.key)" @mouseleave.stop="handleLeave(m.key)">Start</button>
          </div>
        </li>
      </ul>

      <p ref="helperRef" class="helper">Tip: you can choose modes anytime — no pressure, just progress.</p>
    </div>

    <!-- ===== Quiz Layer (teleported overlay, no route change) ===== -->
    <teleport to="body">
      <div v-if="quiz.playing" class="quiz-layer" ref="layerRef" @click.self="closeQuiz">
        <!-- Quiz card -->
        <div ref="cardRef" class="quiz-card fx-tilt" @mousemove="onTilt" @mouseleave="resetTilt">
          <!-- Top bar: progress ring + meta + close -->
          <div class="card-top">
            <svg class="ring" viewBox="0 0 80 80" aria-hidden="true">
              <defs>
                <linearGradient id="ringGrad" x1="0%" y1="0%" x2="100%" y2="100%">
                  <stop offset="0%"  stop-color="#22d3ee"/>
                  <stop offset="50%" stop-color="#3b82f6"/>
                  <stop offset="100%" stop-color="#a78bfa"/>
                </linearGradient>
              </defs>
              <circle class="ring-bg" cx="40" cy="40" r="34"/>
              <circle class="ring-fg" :style="ringStyle" cx="40" cy="40" r="34" stroke="url(#ringGrad)"/>
            </svg>

            <div class="meta">
              <span class="idx">{{ currentIndex + 1 }} / {{ total }}</span>
              <span class="sep">•</span>
              <span class="mode">{{ curModeLabel }}</span>
              <span class="sep">•</span>
              <span class="time">{{ elapsedText }}</span>
            </div>

            <button class="close-btn" @click="onClose" aria-label="Close">
              <svg viewBox="0 0 24 24">
                <path d="M6 6l12 12M18 6L6 18" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
              </svg>
            </button>
          </div>

          <!-- Question area (collapsible text with scenario) -->
          <div ref="qWrap" class="question-wrap" :class="{ expanded: qExpanded }">
            <!-- Scenario block (animated in) -->
            <div ref="scenarioTextRef" class="scenario-box" v-if="currentQuestion.scenario">
              <h4 class="scenario-label">Scenario</h4>
              <p ref="scenarioTextRef" class="scenario-text">{{ currentQuestion.scenario }}</p>
            </div>

            <!-- Review text -->
            <p ref="qText" class="question-text">
              {{ currentQuestion.question }}
            </p>

            

            <!-- Fade mask only when collapsed and overflowing -->
            <i v-if="qOverflow && !qExpanded" class="fade-mask" aria-hidden="true"></i>

            <!-- Expand/collapse button -->
            <button
              v-if="qOverflow"
              class="more-btn"
              @click="qExpanded ? collapseQ() : expandQ()"
            >
              {{ qExpanded ? 'Show less' : 'Show more' }}
            </button>
          </div>

          <!-- Hint below review -->
          <p ref="hintRef" class="question-hint" aria-live="polite">
            Based on this, <span class="hint-emph">what emotion fits best?</span>
          </p>
          <!-- <p class="question-hint">Based on this, what emotion fits best?</p> -->

          <!-- Options (kept OUTSIDE of question-wrap to avoid being clipped) -->
          <div class="options">
            <button
              v-for="(opt,i) in options"
              :key="i"
              class="opt quiz-opt"
              :disabled="lock"
              @click="onChoose(opt, $event)"
              @mousemove="hoverGlow($event)"
            >
              <span class="ripple-holder"></span>
              <span class="dot"></span>
              <span class="label">{{ opt }}</span>
            </button>
          </div>

          <!-- Confetti container (fired on correct) -->
          <svg ref="confettiRef" class="confetti" viewBox="0 0 400 200" aria-hidden="true"></svg>
        </div>

        <!-- Result card (shown when finished) -->
        <div v-if="quiz.done" class="quiz-result" ref="resultRef">
          <h3>Great job!</h3>
          <p class="score"><b>{{ quiz.correct }}</b> / {{ quiz.items.length }} ({{ accuracy }}%)</p>
          <p class="time">Time: {{ elapsedLabel }}</p>
          <div class="actions">
            <button class="btn ghost" @click="restart">Try again</button>
            <button class="btn" @click="seeTips">See tips</button>
          </div>
        </div>
      </div>
    </teleport>

    <!-- Feedback section (appears below the hero) -->
    <FeedbackPanel
      v-if="showFeedback"
      class="feedback-block"
      :data="feedbackData"
      @retry="restartFromPanel"
      @backTop="scrollToTopSmooth"
    />
  </section>
</template>
  
  <script setup>
  import FeedbackPanel from '@/components/FeedbackPanel.vue'
  import HeroShowcase from '@/components/HeroShowcase.vue'
import { getLevelFeedback } from '@/api/modules/test'
// import { ScrollToPlugin } from 'gsap/ScrollToPlugin'
// try { gsap.registerPlugin(ScrollToPlugin) } catch (_) { /* 环境无此插件也没关系 */ }
  import { ref, reactive, computed, onMounted, nextTick, onBeforeUnmount, watch } from 'vue'
//   import { gsap } from 'gsap'
//   import { TextPlugin } from 'gsap/TextPlugin'
  import { getQuestionnaire } from '@/api/modules/test'
//   gsap.registerPlugin(TextPlugin)

import { gsap } from 'gsap'
import { ScrollTrigger } from 'gsap/ScrollTrigger'
import { ScrollToPlugin } from 'gsap/ScrollToPlugin'
import { TextPlugin } from 'gsap/TextPlugin'

gsap.registerPlugin(ScrollTrigger, ScrollToPlugin, TextPlugin)

  /* ====== add：Hero → Modes Rolling Transition ====== */
const modesStart = ref(null)
function scrollToModesFancy () {
  const el = modesStart.value
  if (!el) return
  const top = el.getBoundingClientRect().top + window.scrollY - 8

  // Create a one-time color sweep overlay
  const overlay = document.createElement('div')
  overlay.className = 'sweep-overlay'
  document.body.appendChild(overlay)

  const tl = gsap.timeline({
    onComplete: () => overlay.remove()
  })

  tl.set(overlay, { opacity: 0 })
    // Sweep to reveal
    .to(overlay, { opacity: 1, duration: .2, ease: 'power2.in' })
    // Rolling while sweeping forward
    .to([overlay], {
      '--x': '110%',
      duration: 1.1,
      ease: 'power3.inOut'
    }, 0)
    .to(window, {
      scrollTo: top,
      duration: 1.1,
      ease: 'power3.inOut'
    }, 0)
    // mode card
    .from('.mode-card', {
      y: 28, opacity: 0, rotateX: -10, transformOrigin: '50% 100%',
      duration: .5, stagger: .08, ease: 'back.out(1.6)'
    }, '-=0.25')
}
  
  /* ---------------- Title/Subtitle Animation ---------------- */
  const titlePlainRef = ref(null)
  const titleGradRef  = ref(null)
  const taglineRef    = ref(null)
  const helperRef     = ref(null)
  const heroRef  = ref(null)
  const modesRef = ref(null)
  const hintRef = ref(null)
  const scenarioTextRef = ref(null)
  
  function scrollToModes () {
  const el = modesRef.value
  if (!el) return
  const top = el.getBoundingClientRect().top + window.scrollY
  gsap.to(window, { duration: 1.0, scrollTo: top, ease: 'power3.inOut' })
}

  function splitWords(el){
    const words = el.textContent.trim().split(/\s+/)
    el.innerHTML = words.map(w => `<span class="word">${w}&nbsp;</span>`).join('')
    return el.querySelectorAll('.word')
  }
  
  onMounted(async () => {
    await nextTick()
    gsap.set(titleGradRef.value, {'--reveal':'0%'})
    const tl = gsap.timeline()
    tl.from(titlePlainRef.value, { y:22, opacity:0, duration:.7, ease:'power3.out' }, 0)
      .to(titleGradRef.value,  { '--reveal':'100%', duration:.85, ease:'power2.out' }, 0)
  
    const words = splitWords(taglineRef.value)
    gsap.from(words, {
      delay: 1.0, y: '1.1em', rotateX: -40, opacity: 0,
      transformOrigin: '0% 100% -20', duration: .55, ease: 'back.out(1.6)', stagger: .03
    })
  
    gsap.to('.orbits', { rotate: 15, transformOrigin: '50% 50%', duration: 18, ease: 'none', repeat: -1, yoyo: true })

    await nextTick()

  const heroEl  = heroRef.value?.$el || heroRef.value
  const modesEl = modesRef.value

  if (modesEl) {
  gsap.set(modesEl, { opacity: 0, y: 60 })
  ScrollTrigger.create({
    trigger: modesEl,
    start: 'top 85%',
    once: true,
    onEnter: () => gsap.to(modesEl, { opacity: 1, y: 0, duration: .8, ease: 'power3.out' })
  })
}

if (heroEl) {
  const tl = gsap.timeline({
    scrollTrigger: { trigger: heroEl, start: 'top top', end: '+=100%', scrub: true, pin: true }
  })
  const h1    = heroEl.querySelector?.('.hero-h1')
  const sub   = heroEl.querySelector?.('.hero-sub')
  const glows = heroEl.querySelectorAll?.('.glow')
  h1    && tl.to(h1,    { y: -40, opacity: .65 }, 0)
  sub   && tl.to(sub,   { y: -20, opacity: .5  }, 0)
  glows?.length && tl.to(glows, { opacity: .25, scale: 1.1 }, 0)
}

ScrollTrigger.refresh()
  })
  
  /* ---------------- mode card + hover animation ---------------- */
  const modes = [
    { key: 'Easy',     emoji: '⚡', title: 'Quick Mode',     badge: 'Warm-up',
      desc: 'Fast, friendly, and zero stress — perfect for a quick try.',
      perks: ['Short & sweet', 'Instant feedback', 'Great for first-timers'] },
    { key: 'Standard', emoji: '📊', title: 'Standard Mode',  badge: 'Balanced',
      desc: 'The classic set — solid accuracy with comfortable pacing.',
      perks: ['Well-rounded', 'Steady difficulty', 'Reliable scoring'] },
    { key: 'Advanced', emoji: '🚀', title: 'Challenge Mode', badge: 'Go Big',
      desc: 'Spicy questions and trickier vibes — push your skill ceiling.',
      perks: ['Harder prompts', 'Deeper nuance', 'Best for improvement'] },
  ]
  
  function elsByKey(key){
    const card = document.querySelector(`.mode-card[data-key="${key}"]`)
    return card ? { card, aura: card.querySelector('.aura'), shine: card.querySelector('.shine') } : {}
  }
  function handleEnter(key){
    const { card, aura, shine } = elsByKey(key)
    if (!card) return
    card.dataset.hover = '1'
    gsap.fromTo(card, { scale: 1 }, { scale: 1.01, duration: .18, ease: 'power2.out', yoyo: true, repeat: 1 })
    if (aura){
      gsap.killTweensOf(aura)
      gsap.set(aura, { opacity: .45, scale: 1.02, filter: 'blur(24px)' })
      gsap.to(aura, { opacity: .6, scale: 1.05, filter: 'blur(26px)', duration: 1.4, ease: 'sine.inOut', yoyo: true, repeat: -1 })
    }
    if (shine){
      gsap.killTweensOf(shine)
      gsap.fromTo(shine, { xPercent:-60, opacity: 0 }, {
        xPercent: 60, opacity: .9, duration: .9, ease: 'power2.out',
        onComplete: () => (card.dataset.hover === '1') ? gsap.set(shine, { xPercent: 60, opacity: .9 }) : gsap.to(shine, { opacity: 0, duration: .25 })
      })
    }
  }
  function handleLeave(key){
    const { card, aura, shine } = elsByKey(key)
    if (!card) return
    card.dataset.hover = '0'
    const fadeAll = () => {
      gsap.killTweensOf(aura)
      gsap.to([aura, shine], { opacity: 0, duration: .35, ease: 'power1.out', onComplete: () => [aura, shine].forEach(el => el && gsap.set(el, { clearProps: 'all' }))})
    }
    const tw = gsap.getTweensOf(shine)?.find(t => t.isActive())
    if (tw){ const prev = tw.vars.onComplete; tw.vars.onComplete = () => { prev && prev.call(tw); if (card.dataset.hover !== '1') fadeAll() } }
    else fadeAll()
  }
  
  /* ---------------- Quiz layer ---------------- */
  const layerRef = ref(null)
  const cardRef  = ref(null)
  const resultRef= ref(null)
  const scenarioRef = ref(null)
  
  const activeMode = ref('Easy')
  const quiz = reactive({
    playing: false,
    done:    false,
    items:   [],     // [{question, answer, options}]
    index:   0,
    correct: 0,
    locked:  false,
    startAt: 0,
    endAt:   0,  
  })
  
  
  const activeQ = computed(() => quiz.items[quiz.index] || null)
  const currentQuestion = computed(() => ({
    question: activeQ.value?.question || '',
    answer:   activeQ.value?.answer   || '',
    scenario: activeQ.value?.scenario || ''
  }))
  const options = computed(() => activeQ.value?.options || [])
  const currentIndex = computed(() => quiz.index)
  const total = computed(() => quiz.items.length)
  const lock = computed(() => quiz.locked)
  const accuracy = computed(() => quiz.items.length ? Math.round((quiz.correct / quiz.items.length) * 100) : 0)
  
  /* timer */
  const elapsedText = ref('0:00')
  const timer = ref(null)

  function stopTimer() {
    clearInterval(timer.value)
    quiz.endAt = Date.now()
    const s = Math.max(0, Math.floor((quiz.endAt - (quiz.startAt || Date.now())) / 1000))
    const m = Math.floor(s / 60), r = s % 60
    elapsedText.value = `${m}:${String(r).padStart(2,'0')}`  
}

watch(() => quiz.playing, (v) => {
  clearInterval(timer.value)
  if (v && !quiz.done) {             
    timer.value = setInterval(() => {
      const s = Math.max(0, Math.floor((Date.now() - (quiz.startAt||Date.now()))/1000))
      const m = Math.floor(s/60), r = s%60
      elapsedText.value = `${m}:${String(r).padStart(2,'0')}`
    }, 500)
  }
})
  onBeforeUnmount(() => clearInterval(timer.value))
  const elapsedLabel = computed(() => elapsedText.value)
  const curModeLabel = computed(() => activeMode.value)
  
  /* process circle（SVG） */
  const R = 34
  const C = 2 * Math.PI * R
  const ringStyle = computed(() => {
    const ratio = total.value ? (currentIndex.value) / total.value : 0  
    return {
      strokeDasharray: `${C}px`,
      strokeDashoffset: `${(1 - ratio) * C}px`,
      transform: 'rotate(-90deg)',
      transformOrigin: '50% 50%',
    }
  })
  
  /** Entry: Click the pattern card to retrieve questions and pop up the layered display. */
  async function startTest(mode){
    activeMode.value = mode
    try{
      const resp = await getQuestionnaire(mode)   // { status, msg, data:{ data:[{type, review}, ...] } }
      const rows = resp?.data?.data || resp?.data || []
      const allTypes = Array.from(new Set(rows.map(r => r.type))).filter(Boolean)
  
      const buildOptions = (answer) => {
        const pool = allTypes.filter(t => t !== answer)
        const picks = shuffle(pool).slice(0, Math.max(0, 3))
        return shuffle([answer, ...picks]).slice(0, 4)
      }
  
      quiz.items = rows.map(r => ({
        question: r.review,
        answer:   r.type,
        scenario: r.scenario ?? r.context ?? '',
        options:  buildOptions(r.type),
      }))
      quiz.index = 0
      quiz.correct = 0
      quiz.locked = false
      quiz.done = false
      quiz.playing = true
      quiz.startAt = Date.now()
      await nextTick()
      openLayerAnim()
      enterCardAnim()
    }catch(e){
      console.error('[startTest] failed:', e)
    }
  }
  
  function shuffle(arr){
    const a = arr.slice()
    for(let i=a.length-1;i>0;i--){
      const j = Math.floor(Math.random()*(i+1)); [a[i],a[j]] = [a[j],a[i]]
    }
    return a
  }
  
  /** click options */
  function onChoose(opt, ev){
    // ripple
    const holder = ev.currentTarget.querySelector('.ripple-holder')
    if (holder){
      const ripple = document.createElement('span')
      const rect = ev.currentTarget.getBoundingClientRect()
      const x = ev.clientX - rect.left, y = ev.clientY - rect.top
      ripple.className = 'ripple'
      ripple.style.left = `${x}px`
      ripple.style.top  = `${y}px`
      holder.appendChild(ripple)
      ripple.addEventListener('animationend', () => ripple.remove(), { once:true })
    }
    choose(opt)
  }
  
  /** select answer：go to next question after animation */
  async function choose(opt){
    if (quiz.locked) return
    quiz.locked = true
  
    const isCorrect = opt === currentQuestion.value.answer
  
    const btn = Array.from(document.querySelectorAll('.quiz-opt')).find(b => b.textContent.trim() === opt)
    if (btn) gsap.fromTo(btn, {scale:1}, {scale:1.04, duration:.12, yoyo:true, repeat:1, ease:'power2.out'})
  
    if (isCorrect) {
      quiz.correct++
      await correctAnim()
    } else {
      await wrongAnim()
    }
    await nextQuestion()
  }
  
  /** next question or end */
  async function nextQuestion(){
    if (quiz.index < quiz.items.length - 1){
      quiz.index++
      await nextTick()
  
      // restore the animation of last question
      if (cardRef.value) gsap.set(cardRef.value, { clearProps: 'all', opacity: 1, x: 0, y: 0, rotate: 0, scale: 1 })
      if (qWrap.value)   gsap.set(qWrap.value,   { clearProps: 'all' })
      gsap.set('.quiz-opt',   { clearProps: 'all' })
  
      enterCardAnim()
      quiz.locked = false
    } else {
      quiz.done = true
      stopTimer()
      await showResultAnim()
    }
  }
  
  /* ---------------- GSAP animation ---------------- */
  function openLayerAnim(){
    if (!layerRef.value || !cardRef.value) return
    const tl = gsap.timeline()
    tl.fromTo(layerRef.value, {opacity:0, backdropFilter:'blur(0px)'}, {opacity:1, backdropFilter:'blur(6px)', duration:.35, ease:'power2.out'})
      .from(cardRef.value, {y:40, opacity:0, rotateX:-8, transformOrigin:'50% 100%', duration:.5, ease:'back.out(1.6)'}, '<+.05')
  }
  function enterCardAnim(){
    if (!cardRef.value) return
    gsap.set(cardRef.value, { opacity: 1, x: 0, y: 0, rotate: 0, scale: 1, boxShadow: '' })
    gsap.from(cardRef.value, { y: 28, opacity: 0, scale: .98, duration: .38, ease: 'power3.out' })
    
    if (scenarioRef.value) {
      gsap.from(scenarioRef.value, {
        y: 14,
        opacity: 0,
        duration: .4,
        ease: 'power2.out',
        delay: .05
      })
    }

    if (scenarioTextRef.value) {
      scenarioTextRef.value.textContent = ''
      gsap.killTweensOf(scenarioTextRef.value) 
      gsap.to(scenarioTextRef.value, {
        text: currentQuestion.value.scenario,  
        duration: 1.0,     
        ease: 'none',
        delay: 0.10   
      })
    }

    if (hintRef.value) {
      gsap.from(hintRef.value, {
        y: 8, opacity: 0, duration: .4, ease: 'power2.out', delay: .12
      })
    }
    
    if (qWrap.value) gsap.from(qWrap.value, { y: 16, opacity: 0, duration: .30, ease: 'power2.out', delay: .05 })
    gsap.from('.quiz-opt', { y: 12, opacity: 0, duration: .28, ease: 'power2.out', stagger: .05, delay: .05 })
    measureQ()
  }
  function correctAnim(){
    return new Promise(resolve => {
      if (!cardRef.value) return resolve()
      const tl = gsap.timeline({ onComplete: resolve })
      tl.to(cardRef.value, { boxShadow:'0 30px 70px rgba(16,185,129,.35)', duration:.15 }, 0)
        .to(cardRef.value, { scale:1.02, duration:.15, yoyo:true, repeat:1 }, 0)
        .add(() => confetti(cardRef.value), 0)
        .to(cardRef.value, { y:-120, opacity:0, duration:.4, ease:'power2.in' }, '+=.1')
    })
  }
  function wrongAnim(){
    return new Promise(resolve => {
      if (!cardRef.value) return resolve()
      const tl = gsap.timeline({ onComplete: resolve })
      tl.to(cardRef.value, { boxShadow:'0 30px 70px rgba(239,68,68,.28)', duration:.15 }, 0)
        .to(cardRef.value, { x:-8, duration:.06, yoyo:true, repeat:5, ease:'sine.inOut' }, 0)
        .to(cardRef.value, { y:0, x:-160, rotate:-6, opacity:0, duration:.45, ease:'power2.in' }, '+=.05')
    })
  }
  function showResultAnim(){
    if (!resultRef.value || !cardRef.value) return
    const tl = gsap.timeline()
    tl.to(cardRef.value, {opacity:0, duration:.2})
      .fromTo(resultRef.value, {scale:.9, y:20, opacity:0}, {scale:1, y:0, opacity:1, duration:.5, ease:'back.out(1.6)'})
  }
  
  /** close/reopen */
  function onClose(){ closeQuiz() }
  function closeQuiz(){
    const target = resultRef.value || cardRef.value
    const tl = gsap.timeline()
    if (target) tl.to(target, {opacity:0, y:10, duration:.25, ease:'power1.out'})
    if (layerRef.value) tl.to(layerRef.value, {opacity:0, backdropFilter:'blur(0px)', duration:.25, ease:'power2.in'}, '<')
    tl.add(() => { quiz.playing=false; quiz.done=false; clearInterval(timer.value) })
  }
  function restart(){
    quiz.index = 0
    quiz.correct = 0
    quiz.done = false
    quiz.locked = false
    quiz.startAt = Date.now()
    quiz.endAt = 0   
    elapsedText.value = '0:00'
    clearInterval(timer.value) 
    timer.value = setInterval(() => {
        const s = Math.max(0, Math.floor((Date.now() - quiz.startAt)/1000))
        const m = Math.floor(s/60), r = s%60
        elapsedText.value = `${m}:${String(r).padStart(2,'0')}`
    }, 500)
    if (resultRef.value) gsap.set(resultRef.value, {opacity:0})
    if (cardRef.value)   gsap.set(cardRef.value,   {opacity:1})
    enterCardAnim()
  }
  
  /** success animation */
  function confetti(host){
    const N = 20
    for (let i=0;i<N;i++){
      const s = document.createElement('span')
      s.className = 'confetti'
      host.appendChild(s)
      const angle = gsap.utils.random(-70, -110, 1)
      const dist  = gsap.utils.random(120, 220, 1)
      const dur   = gsap.utils.random(.7, 1.1)
      gsap.set(s, {
        x: 0, y: 0, opacity: 1,
        background: gsap.utils.random(['#34d399','#60a5fa','#a78bfa','#f472b6','#fbbf24']),
        width: gsap.utils.random(6,10), height: gsap.utils.random(6,10), borderRadius: 2,
        position: 'absolute', left: '50%', top: '30%', zIndex: 5
      })
      gsap.to(s, {
        duration: dur, ease:'power2.out',
        x: Math.cos(angle*Math.PI/180)*dist,
        y: Math.sin(angle*Math.PI/180)*dist,
        rotation: gsap.utils.random(-120,120),
        opacity: 0,
        onComplete(){ s.remove() }
      })
    }
  }
  
  /* ----------- Visual Enhancements: Options: Hover Glow + Card 3D Tilt ----------- */
  function hoverGlow(e){
    const btn = e.currentTarget
    const rect = btn.getBoundingClientRect()
    const x = e.clientX - rect.left
    const y = e.clientY - rect.top
    btn.style.setProperty('--mx', `${x}px`)
    btn.style.setProperty('--my', `${y}px`)
  }
  function onTilt(e){
    if (!cardRef.value) return
    const rect = cardRef.value.getBoundingClientRect()
    const cx = rect.left + rect.width/2
    const cy = rect.top  + rect.height/2
    const dx = (e.clientX - cx) / (rect.width/2)
    const dy = (e.clientY - cy) / (rect.height/2)
    gsap.to(cardRef.value, { rotateY: dx*6, rotateX: -dy*6, transformPerspective: 800, transformOrigin:'50% 50%', duration:.2, ease:'power2.out' })
  }
  function resetTilt(){
    if (!cardRef.value) return
    gsap.to(cardRef.value, { rotateX:0, rotateY:0, duration:.3, ease:'power2.out' })
  }

  // ===== Title Bar Overflow Control =====
const qWrap = ref(null)
const qText = ref(null)
const qExpanded = ref(false)    
const qOverflow = ref(false)    

const COLLAPSED_MAX = 140       

function measureQ() {
  
  requestAnimationFrame(() => {
    if (!qWrap.value || !qText.value) return
    
    qExpanded.value = false
    qWrap.value.style.maxHeight = COLLAPSED_MAX + 'px'
    qWrap.value.style.removeProperty('height')
    
    qOverflow.value = qText.value.scrollHeight > COLLAPSED_MAX + 2
  })
}

function expandQ() {
  if (!qWrap.value || !qText.value) return
  const full = qText.value.scrollHeight
  qExpanded.value = true
  // 动画到完整高度，完了之后把 maxHeight 设为 none 以便自适应
  gsap.fromTo(qWrap.value,
    { maxHeight: qWrap.value.clientHeight },
    {
      maxHeight: full,
      duration: .35,
      ease: 'power2.out',
      onComplete: () => {
        qWrap.value.style.maxHeight = 'none'
      }
    }
  )
}

function collapseQ() {
  if (!qWrap.value) return
  qExpanded.value = false
  gsap.fromTo(qWrap.value,
    { maxHeight: qWrap.value.scrollHeight },
    {
      maxHeight: COLLAPSED_MAX,
      duration: .3,
      ease: 'power2.in',
      onComplete: () => {
        qWrap.value.style.maxHeight = COLLAPSED_MAX + 'px'
      }
    }
  )
}

onMounted(() => {
  window.addEventListener('resize', measureQ, { passive: true })
})
onBeforeUnmount(() => {
  window.removeEventListener('resize', measureQ)
})

/* ======= 反馈区状态 ======= */
const showFeedback = ref(false)
const feedbackData = ref({})

// 正确率 => 等级
function accuracyToLevel (acc){
  if (acc >= 90) return 3
  if (acc >= 75) return 1
  if (acc >= 50) return 4
  return 2
}

// 结算里点击 “See tips”
async function seeTips () {
  // 关弹层
  closeQuiz()

  // 拉反馈数据
  try {
    const level = accuracyToLevel(accuracy.value)
    const res = await getLevelFeedback(level)
    // 后端包裹可能是 {data: {...}} 或者 {data:{data:{...}}}
    const payload = res?.data?.data || res?.data || res || {}
    feedbackData.value = {
      feedback:    payload.feedback,
      tips:        payload.tips,
      growthTips:  payload.growthTips,
      refs:        payload.refs,
      growthRefs:  payload.growthRefs
    }
    showFeedback.value = true
    // 平滑滚到反馈区
    await nextTick()
    scrollToFeedback()
  } catch (e) {
    console.error('[seeTips] failed:', e)
  }
}

/* 平滑滚动到反馈区（优先用 GSAP ScrollToPlugin，降级原生 smooth） */
function scrollToFeedback () {
  const el = document.getElementById('feedback')
  if (!el) return
  const top = el.getBoundingClientRect().top + window.scrollY - 16
  if (gsap.core.globals().ScrollToPlugin) {
    gsap.to(window, { duration: 1.1, scrollTo: top, ease: 'power3.inOut' })
  } else {
    window.scrollTo({ top, behavior: 'smooth' })
  }
}

/* 回到顶部（组件 footer 的 Back to top） */
function scrollToTopSmooth () {
  if (gsap.core.globals().ScrollToPlugin) {
    gsap.to(window, { duration: 0.9, scrollTo: 0, ease: 'power2.inOut' })
  } else {
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }
}

/* 反馈面板里点 Try again：回到顶部并重新开局 */
function restartFromPanel () {
  scrollToTopSmooth()
  // 可选：也可以直接触发 startTest(activeMode.value)
}
  </script>
  
  <style scoped>
  /* ===== 你的原有样式（标题、stack、卡片等）保持不变，只省略未关键注释 ===== */
  .test-hero{ position: relative; min-height: calc(100vh - 64px); padding: 0 0 88px; overflow: hidden; scroll-margin-top: 24px;}
  .container{ width: min(1080px, 92vw); margin: 0 auto; padding: 50px 0; }
  .orbits{ position: absolute; width: 1200px; height: 1200px; left: 50%; top: -520px; transform: translateX(-50%); z-index: -1; opacity: .45; }

  .sweep-overlay{
    position: fixed; inset: 0; z-index: 998;
    pointer-events: none; opacity: 0;
    --x: -10%;
    background:
        radial-gradient(600px 300px at var(--x) 50%, rgba(124,58,237,.35), transparent 60%),
        radial-gradient(500px 260px at calc(var(--x) - 20%) 40%, rgba(59,130,246,.35), transparent 60%),
        radial-gradient(400px 240px at calc(var(--x) - 35%) 60%, rgba(6,182,212,.35), transparent 60%);
    filter: blur(10px) saturate(120%);
}

.feedback-block{ margin-top: 200px; }
@media (max-width: 720px){ .feedback-block{ margin-top: 80px; } }
  
  .title{ display: inline-grid; gap: 2px; margin: 0 0 6px; line-height: 1.02; letter-spacing: .3px; }
  .title-line{ display: block; font-weight: 900; font-size: clamp(34px,5.6vw,56px); }
  .title-plain{ color: #fff; text-shadow: 0 10px 26px rgba(0,0,0,.16); }
  .title-gradient{
    background: linear-gradient(90deg,#F472B6 0%,#818CF8 45%,#34D399 100%);
    -webkit-background-clip: text; background-clip: text;
    -webkit-text-fill-color: transparent; color: transparent;
    --reveal:0%;
    -webkit-mask-image: linear-gradient(#000 0 0); mask-image: linear-gradient(#000 0 0);
    -webkit-mask-size: var(--reveal) 100%; mask-size: var(--reveal) 100%;
    -webkit-mask-repeat: no-repeat; mask-repeat: no-repeat;
  }
  .tagline{ margin: 8px 0 28px; color: #334155; font-size: clamp(16px,1.6vw,18px); font-weight: 500; }
  .word{ display:inline-block; }
  
  .mode-stack{ display:grid; grid-template-columns:1fr; gap:18px; margin:20px 0 22px; padding:0; list-style:none; }
  .mode-card{ position:relative; overflow:visible; display:grid; grid-template-columns:72px 1fr auto; align-items:center; gap:16px;
    padding:18px 18px 18px 16px; border-radius:18px; background:rgba(255,255,255,.96);
    border:1px solid rgba(15,23,42,.06); box-shadow:0 16px 40px rgba(15,23,42,.12); color:#0f172a; backdrop-filter:blur(4px);
    cursor:pointer; transition:transform .25s ease, box-shadow .25s ease, background .25s ease, border-color .25s ease;
  }
  .mode-card:hover{ transform: translateY(-4px); box-shadow: 0 22px 48px rgba(15,23,42,.18); background:#fff; border-color:rgba(15,23,42,.10); }
  .aura{ position:absolute; inset:-24px; border-radius:28px; pointer-events:none; z-index:-1; opacity:0; filter:blur(24px); transform:scale(.9); }
  .mode-card.--easy .aura{ background: radial-gradient(circle, rgba(56,189,248,.55) 0%, rgba(56,189,248,.25) 50%, transparent 80%); }
  .mode-card.--standard .aura{ background: radial-gradient(circle, rgba(99,102,241,.55) 0%, rgba(99,102,241,.25) 50%, transparent 80%); }
  .mode-card.--advanced .aura{ background: radial-gradient(circle, rgba(236,72,153,.55) 0%, rgba(236,72,153,.25) 50%, transparent 80%); }
  .shine-clip{ position:absolute; inset:0; border-radius:inherit; overflow:hidden; z-index:0; }
  .shine{ position:absolute; left:0; top:-10%; width:60%; height:120%; background:linear-gradient(115deg, rgba(124,58,237,0) 0%,
    rgba(124,58,237,.7) 50%, rgba(59,130,246,.7) 70%, rgba(59,130,246,0) 100%); filter:blur(6px); transform:skewX(-18deg); opacity:0; }
  .mode-left,.mode-main,.mode-right{ position:relative; z-index:1; }
  .mode-left{ display:grid; place-items:center; }
  .mode-emoji{ font-size:34px; filter: drop-shadow(0 6px 12px rgba(0,0,0,.15)); }
  .mode-title{ color:#0f172a; font-weight:900; font-size:clamp(18px,2vw,22px); margin:0 0 4px; display:flex; align-items:center; gap:10px; }
  .mode-badge{ font-size:12px; font-weight:800; letter-spacing:.2px; padding:4px 8px; border-radius:999px; color:#1e293b; background:#fde68a; }
  .mode-desc{ margin:0 0 8px; color:#475569; }
  .perks{ display:flex; flex-wrap:wrap; gap:8px; }
  .perk{ font-size:12px; padding:4px 8px; border-radius:999px; background:#f1f5f9; border:1px solid #cbd5e1; color:#334155; }
  .mode-right{ display:grid; place-items:center; }
  .start-btn{ font-weight:800; letter-spacing:.2px; color:#fff; background:linear-gradient(90deg,#7C3AED,#3B82F6);
    border:none; border-radius:12px; padding:10px 16px; box-shadow:0 8px 20px rgba(2,6,23,.28), inset 0 0 0 1px rgba(255,255,255,.22); }
  .start-btn:hover{ filter:brightness(1.05); transform:translateY(-1px); }
  .start-btn:active{ transform:translateY(0); }
  .helper{ margin-top:8px; color:#64748b; font-size:13px; }
  
  /* ===== Quiz Layer ===== */
  .quiz-layer{
    position: fixed; inset: 0; z-index: 999;
    display: grid; place-items: center;
    background: rgba(15,23,42,.24);
    backdrop-filter: blur(6px);
  }
  .quiz-card{
    position: relative;
    width: min(720px, 92vw);
    background: #fff;
    border-radius: 18px;
    box-shadow: 0 30px 80px rgba(2,6,23,.25);
    padding: 22px 22px 18px;
    transition: box-shadow .2s ease;
    max-height: 86vh;      
    overflow: auto;       
  }
  
  /* 顶部区域 */
  .card-top{
    display:flex; align-items:center; gap:12px; margin-bottom: 8px;
  }
  .ring{ width:34px; height:34px; }
  .ring-bg{ fill:none; stroke:#e2e8f0; stroke-width:8; }
  .ring-fg{ fill:none; stroke-width:8; stroke-linecap:round; }
  
  /* meta & 关闭 */
  .meta{ color:#64748b; font-size:12px; display:flex; align-items:center; gap:6px; }
  .sep{ opacity:.6; }
  .close-btn{
    margin-left:auto; display:grid; place-items:center;
    width:34px; height:34px; border-radius:999px; border:none; cursor:pointer;
    background:#f1f5f9; color:#0f172a;
  }
  .close-btn:hover{ filter: brightness(1.05); }
  
  /* 题目与选项 */
  .question{ margin: 8px 0 14px; font-size: clamp(20px, 3.2vw, 28px); line-height: 1.5; color:#0f172a; }
  .wipe-mask{ background: linear-gradient(#000 0 0); -webkit-background-clip: text; color: transparent; }
  
  /* 选项按钮：支持鼠标位置发光 + 点击水波纹 */
  .options{ display:grid; 
            gap:10px;
            margin-top: 10px;
            position: relative; 
            z-index: 1; 
  }
  .opt{
    position: relative;
    display:flex; align-items:center; gap:10px;
    padding: 14px 16px; border-radius: 14px; border:1px solid #e2e8f0;
    background: radial-gradient(200px 200px at var(--mx, 50%) var(--my, 50%),
                rgba(99,102,241,.08), transparent 40%) #fbfdff;
    color:#0f172a; cursor:pointer; overflow:hidden;
    transition: transform .12s ease, border-color .12s ease, background .12s ease;
  }
  .opt:hover{ transform: translateY(-1px); border-color:#cbd5e1; }
  .opt:disabled{ opacity: .7; cursor:not-allowed; }
  .opt .dot{ width:10px; height:10px; border-radius:999px; background:#94a3b8; }
  
  /* 点击水波纹 */
  .ripple-holder{ position:absolute; inset:0; overflow:hidden; pointer-events:none; }
  .ripple{
    position:absolute; width:8px; height:8px; border-radius:999px; background: rgba(59,130,246,.35);
    transform: translate(-50%, -50%); animation: ripple .6s ease-out forwards;
  }
  @keyframes ripple{
    from{ opacity:.6; transform: translate(-50%,-50%) scale(.5); }
    to  { opacity:0; transform: translate(-50%,-50%) scale(18); }
  }
  
  /* 结果卡 */
  .quiz-result{
    position: absolute; inset: 0; margin: auto; width: min(520px, 90vw);
    height: fit-content; background: #fff; border-radius: 18px; padding: 22px;
    box-shadow: 0 30px 80px rgba(2,6,23,.25); display: grid; gap: 8px; place-items: center;
  }
  .quiz-result h3{ font-size: 22px; margin: 0; }
  .quiz-result .score{ font-size: 18px; margin: 4px 0; }
  .quiz-result .time{ color:#64748b; font-size: 14px; margin: 0 0 8px; }
  .actions{ display:flex; gap:10px; }
  .btn{
    border:none; border-radius:12px; padding:10px 16px; font-weight:800; cursor:pointer;
    color:#fff; background: linear-gradient(90deg,#7C3AED,#3B82F6);
    box-shadow: 0 8px 20px rgba(2,6,23,.28), inset 0 0 0 1px rgba(255,255,255,.22);
  }
  .btn.ghost{ background:#fff; color:#0f172a; border:1px solid #e2e8f0; box-shadow:none; }
  
  /* 题干外层：初始折叠，带渐隐遮罩与“展开”按钮 */
.question-wrap{
  position: relative;
  margin: 10px 0 16px;
  max-height: 90px;                 
  /* overflow: hidden; */
  transition: max-height .25s ease;
  margin-bottom: 100px;
}

/* 文本本体：强制换行、长词/无空格也能换开 */
.question-text{
  font-size: clamp(18px, 2.2vw, 22px);
  line-height: 1.55;
  color: #0f172a;
  margin: 0;
  white-space: pre-wrap;             /* 保留换行 */
  overflow-wrap: anywhere;           /* 任何位置都允许换行 */
  word-break: break-word;            /* 过长单词强制断开 */
  hyphens: auto;                     /* 支持连字符断词（浏览器支持时） */
}

/* 渐隐遮罩：仅在折叠且溢出时显示 */
.fade-mask{
  position: absolute;
  left: 0; right: 0; bottom: 0;
  height: 56px;
  background: linear-gradient(180deg, rgba(255,255,255,0), #fff 60%);
  pointer-events: none;
}

/* 展开/收起按钮 */
.more-btn{
  position: absolute;
  right: 0; bottom: 6px;
  border: 1px solid #e2e8f0;
  background: #fff;
  color: #0f172a;
  font-weight: 800;
  font-size: 12px;
  padding: 6px 10px;
  border-radius: 999px;
  box-shadow: 0 2px 8px rgba(2,6,23,.06);
  cursor: pointer;
}
.more-btn:hover{ filter: brightness(1.03); }

/* 展开态：去掉遮罩效果（通过不渲染 fade-mask 实现） */
.question-wrap.expanded{
  max-height: none;
}

  /* 响应式 */
  @media (max-width: 720px){
    .mode-card{ grid-template-columns: 56px 1fr; }
    .mode-right{ display: none; }
  }

  .feedback-block{
  margin-top: 200px;              
}

@media (max-width: 720px){
  .feedback-block{
    margin-top: 80px;
  }
}

/* Scenario */
.scenario-box {
  background: #f8fafc;
  border-left: 4px solid #3b82f6;  /* 蓝色强调 */
  padding: 10px 14px;
  border-radius: 10px;
  margin-bottom: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,.04);
  color: #1c4b9c;
}

.scenario-label {
  font-size: 12px;
  font-weight: 800;
  text-transform: uppercase;
  color: #3b82f6;
  margin: 0 0 4px 0;
  letter-spacing: .5px;
}

.scenario-text {
  font-size: clamp(18px, 2.2vw, 22px);
  font-weight: 600;
  line-height: 1.5;
  color: #1f2937;
  margin: 0;
}

/* question hint */
.question-hint {
  margin: 12px 0 14px;
  font-size: clamp(16px, 1.9vw, 18px);
  font-weight: 700;
  color: #334155;
  letter-spacing: .2px;
  font-style: italic;
  text-shadow: 0 1px 12px rgba(59,130,246,.12);
}

.question-hint .hint-emph{
  /* 渐变文本（随时间流动） */
  background: linear-gradient(90deg, #22d3ee, #3b82f6, #a78bfa, #f472b6, #22d3ee);
  background-size: 300% 100%;
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  font-weight: 900;
  /* 让“what emotion fits best?”更大一点 */
  font-size: 1.05em;
  position: relative;
  /* 平滑动画 */
  animation: hintGradient 6s linear infinite;
}

.question-hint .hint-emph::after{
  content: "";
  position: absolute;
  left: 0; right: 0; bottom: -3px;
  height: 3px;
  border-radius: 999px;
  background: linear-gradient(90deg, rgba(34,211,238,.0), rgba(34,211,238,.6), rgba(59,130,246,.7), rgba(167,139,250,.7), rgba(244,114,182,.6), rgba(34,211,238,.0));
  background-size: 200% 100%;
  filter: blur(0.3px);
  animation: hintUnderline 2.8s ease-in-out infinite;
}

@keyframes hintGradient{
  0%   { background-position:   0% 50%; }
  50%  { background-position: 100% 50%; }
  100% { background-position:   0% 50%; }
}

/* 下划线左右轻微流动 + 呼吸亮度 */
@keyframes hintUnderline{
  0%   { background-position:   0% 50%; opacity: .85; }
  50%  { background-position: 100% 50%; opacity: 1;   }
  100% { background-position:   0% 50%; opacity: .85; }
}

@media (prefers-reduced-motion: reduce){
  .question-hint .hint-emph{ animation: none; }
  .question-hint .hint-emph::after{ animation: none; }
}


  </style>