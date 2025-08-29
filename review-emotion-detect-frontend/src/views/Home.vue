<template>
    <!-- Full-bleed carousel, flush under the sticky topbar -->
    <section class="bleed">
      <a-carousel
        ref="carouselRef"
        :autoplay="false"
        :autoplaySpeed="4000"
        :pauseOnHover="false"
        :pauseOnDotsHover="false"
        effect="fade"
        class="ml-carousel"
        :dots="true"
        @afterChange="onAfterChange"
      >
        <!-- Slide 1 -->
        <div>
          <div
            class="slide"
            :style="bgStyle(digital01)"
            role="img"
            aria-label="See emotions. Spark better talk."
          >
            <div class="gradient"></div>
  
            <!-- Bottom-right caption (alternates every 2s) -->
            <h2 class="caption caption--br">
              <transition name="caption-fade" mode="out-in">
                <span :key="activeCaptionIndex()">
                  {{ slideCaptions[0][activeCaptionIndex()] }}
                </span>
              </transition>
            </h2>
  
            <!-- CTA to About (keep your text) -->
            <button
              type="button"
              class="cta"
              @click="goAbout"
              aria-label="Go to About page"
            >
              Learn more about us
            </button>
  
            <!-- Image credit -->
            <a
              class="credit"
              href="https://www.pexels.com/photo/modern-devices-on-stylish-brown-surface-33632612/"
              target="_blank"
              rel="noopener noreferrer"
              aria-label="Image credit for slide 1 on Pexels (opens in new tab)"
            >
              Photo: Pexels
            </a>
          </div>
        </div>
  
        <!-- Slide 2 -->
        <div>
          <div
            class="slide"
            :style="bgStyle(emotion02)"
            role="img"
            aria-label="Analyze reviews. Instant insights."
          >
            <div class="gradient"></div>
  
            <!-- Bottom-right caption (alternates every 2s) -->
            <h2 class="caption caption--br">
              <transition name="caption-fade" mode="out-in">
                <span :key="activeCaptionIndex()">
                  {{ slideCaptions[1][activeCaptionIndex()] }}
                </span>
              </transition>
            </h2>
  
            <!-- CTA to analyze (keep your text) -->
            <button
              type="button"
              class="cta"
              @click="goAnalyze"
              aria-label="Go to Analyze page"
            >
              What emotions in the review?
            </button>
  
            <!-- Image credit -->
            <a
              class="credit"
              href="https://www.pexels.com/photo/two-yellow-emoji-on-yellow-case-207983/"
              target="_blank"
              rel="noopener noreferrer"
              aria-label="Image credit for slide 2 on Pexels (opens in new tab)"
            >
              Photo: Pexels
            </a>
          </div>
        </div>
      </a-carousel>
  
      <!-- Centered side arrows -->
      <button
        class="nav-btn nav-btn--left"
        aria-label="Previous slide"
        type="button"
        @click="prevSlide"
      >
        <svg viewBox="0 0 24 24" width="22" height="22" aria-hidden="true">
          <path d="M15 5l-7 7 7 7" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </button>
  
      <button
        class="nav-btn nav-btn--right"
        aria-label="Next slide"
        type="button"
        @click="nextSlide"
      >
        <svg viewBox="0 0 24 24" width="22" height="22" aria-hidden="true">
          <path d="M9 5l7 7-7 7" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </button>
    </section>

    <!-- Divider -->
    <hr class="divider" />
  
    <!-- ===================== Site Intro Section ===================== -->
    <section class="intro container">
      <div class="intro__grid2">
        <!-- Left: text + cards -->
        <div class="intro__left">
          <div class="intro__head">
            <h2 class="intro__title">What is MoodLens?</h2>
            <p class="intro__subtitle">
              MoodLens helps you understand the emotions inside any review — turning raw text into actionable, human-centered insights for healthier online discourse.
            </p>
          </div>
  
          <!-- Single-column vertical stack -->
          <div class="intro__grid">
            <div class="card fade-up" style="--d:0ms">
              <div class="card__icon">
                <svg viewBox="0 0 24 24" width="22" height="22" aria-hidden="true">
                  <path d="M13 2L3 14h7l-1 8 10-12h-7l1-8z" fill="currentColor"/>
                </svg>
              </div>
              <div class="card__body">
                <h3 class="card__title">Quick & clear</h3>
                <p class="card__text">Paste a review and get an emotion breakdown in seconds with a clean, readable report.</p>
              </div>
            </div>
  
            <div class="card fade-up" style="--d:80ms">
              <div class="card__icon">
                <svg viewBox="0 0 24 24" width="22" height="22" aria-hidden="true">
                  <path d="M12 3l9 5-9 5-9-5 9-5zm0 7l9 5-9 5-9-5 9-5z" fill="currentColor"/>
                </svg>
              </div>
              <div class="card__body">
                <h3 class="card__title">Rich emotions</h3>
                <p class="card__text">Detect a wide range of emotions and tones to understand sentiment beyond positive or negative.</p>
              </div>
            </div>
  
            <div class="card fade-up" style="--d:160ms">
              <div class="card__icon">
                <svg viewBox="0 0 24 24" width="22" height="22" aria-hidden="true">
                  <path d="M12 2l8 4v6c0 5-3.5 9-8 10-4.5-1-8-5-8-10V6l8-4z" fill="currentColor"/>
                </svg>
              </div>
              <div class="card__body">
                <h3 class="card__title">Respect & safety</h3>
                <p class="card__text">Designed to encourage constructive dialogue and digital citizenship in every interaction.</p>
              </div>
            </div>
          </div>
  
          <div class="intro__cta">
            <a class="intro__btn" @click.prevent="goAbout" href="/analyze">
              Want to learn more about us?
            </a>
          </div>
        </div>
  
        <!-- Right: illustration -->
        <div class="intro__right">
          <img
            src="@/assets/digital03.jpg"
            alt="Person holding smartphone (Unsplash)"
            class="intro__image"
          />
          <a
            class="credit credit--img"
            href="https://unsplash.com/photos/person-holding-smartphone-0VGG7cqTwCo"
            target="_blank"
            rel="noopener noreferrer"
          >
            Photo: Unsplash
          </a>
        </div>
      </div>
    </section>

    <!-- Divider -->
    <hr class="divider" />
  
    <!-- ===================== Core Feature Section ===================== -->
    <section class="feature container">
      <div class="feature__grid">
        <!-- Left: illustration -->
        <div class="feature__image">
          <img src="@/assets/emotion01.jpg" alt="Emotion analysis illustration" />
          <!-- Pexels credit (this is the one you want to shorten) -->
          <a
            class="credit credit--feature"
            href="https://www.pexels.com/photo/yellow-cube-on-brown-pavement-208147/"
            target="_blank"
            rel="noopener noreferrer"
          >
            Photo: Pexels
          </a>
        </div>
  
        <!-- Right: concise content + preview -->
        <div class="feature__content">
            <div class="feature__head feature__head--center">
                <h2 class="feature__title">Analyze a review, clearly</h2>
                <p class="feature__subtitle">
                Type or paste a review. We leverage the Hugging Face model
                <span class="mono">SamLowe/roberta-base-go_emotions</span>
                to estimate emotion probabilities, visualize them with ECharts,
                and generate a readable report.
                </p>
            </div>

            <!-- Steps: now 3 equal columns that fill the row -->
            <ul class="badges badges--equal">
                <li class="badge"><span class="dot">1</span>Input text</li>
                <li class="badge"><span class="dot">2</span>Detect emotions</li>
                <li class="badge"><span class="dot">3</span>Chart & report</li>
            </ul>

            <FeaturePreview />
        </div>
      </div>
    </section>
  </template>
  
  <script setup>
  import { ref, onMounted, onBeforeUnmount } from 'vue'
  import { useRouter } from 'vue-router'
  import digital01 from '@/assets/digital01.jpg'
  import emotion02 from '@/assets/emotion02.jpg'
  import FeaturePreview from '@/components/home/FeaturePreview.vue'
  
  function bgStyle(src) { return { backgroundImage: `url(${src})` } }
  
  const router = useRouter()
  function goAnalyze() { router.push('/analyze') }
  function goAbout()   { router.push('/about') }
  
  const carouselRef = ref(null)
  function nextSlide() { carouselRef.value?.next?.() }
  function prevSlide() { carouselRef.value?.prev?.() }
  
  const captionTick = ref(0)
  const slideCaptions = [
    ['See emotions. Spark better talk.', 'Foster digital citizenship.'],
    ['Analyze reviews. Instant insights.', 'Understand tone, write kindly.']
  ]
  function activeCaptionIndex() { return captionTick.value % 2 }
  function onAfterChange() {}
  
  let slideTimer = null
  let captionTimer = null
  onMounted(() => {
    slideTimer = setInterval(() => nextSlide(), 4000)
    captionTimer = setInterval(() => { captionTick.value = (captionTick.value + 1) % 2 }, 2000)
  })
  onBeforeUnmount(() => {
    if (slideTimer) clearInterval(slideTimer)
    if (captionTimer) clearInterval(captionTimer)
  })
  </script>
  
  <style scoped>
  /* ===================== Full-bleed container ===================== */
  .bleed {
    position: relative;
    width: 100vw;
    margin-left: calc(50% - 50vw);
    margin-right: calc(50% - 50vw);
    margin-top: -32px;
  }
  
  /* ===================== Carousel & slides ===================== */
  .ml-carousel { border-radius: 0; }
  .slide {
    position: relative;
    width: 100%;
    height: calc(100vh - 72px + 32px);
    min-height: 60vh;
    background-size: cover;
    background-position: center;
    background-repeat: no-repeat;
    overflow: hidden;
  }
  .gradient {
    position: absolute;
    inset: 0;
    pointer-events: none;
    background: linear-gradient(180deg, rgba(0,0,0,.18) 0%, rgba(0,0,0,.06) 35%, rgba(0,0,0,.30) 100%);
  }
  
  /* ===================== Caption ===================== */
  .caption {
    position: absolute;
    margin: 0;
    color: #fff;
    font-weight: 900;
    letter-spacing: .2px;
    line-height: 1.1;
    font-size: clamp(20px, 3.6vw, 44px);
    text-shadow: 0 2px 6px rgba(0,0,0,.55);
    animation: rise .8s ease-out both;
  }
  .caption--br {
    right: max(24px, 3vw);
    bottom: calc(max(24px, 4vh) + 150px);
    text-align: right;
  }
  @keyframes rise {
    from { opacity: 0; transform: translateY(18px) scale(.98); }
    to   { opacity: 1; transform: translateY(6px)  scale(1); }
  }
  :deep(.ant-carousel .slick-slide) { transition: opacity .6s ease; }
  
  /* ===================== Side arrow buttons ===================== */
  .nav-btn {
    position: absolute;
    top: 50%;
    transform: translateY(-50%);
    z-index: 5;
    width: 42px; height: 42px;
    border: none; border-radius: 50%;
    display: grid; place-items: center;
    cursor: pointer; color: #fff;
    background: rgba(15,23,42,.28);
    backdrop-filter: blur(6px) saturate(140%);
    box-shadow: 0 6px 18px rgba(2,6,23,.25), inset 0 0 0 1px rgba(255,255,255,.18);
    transition: transform .15s ease, background .15s ease, box-shadow .15s ease, opacity .2s;
    opacity: .92;
  }
  .nav-btn:hover { transform: translateY(-50%) scale(1.06); background: rgba(15,23,42,.38); }
  .nav-btn:active { transform: translateY(-50%) scale(0.98); }
  .nav-btn--left  { left: 24px; }
  .nav-btn--right { right: 24px; }
  .nav-btn:focus-visible { outline: 2px solid #7C3AED; outline-offset: 2px; }
  
  /* ===================== Carousel dots ===================== */
  :deep(.ant-carousel .slick-dots) { bottom: 18px; }
  :deep(.ant-carousel .slick-dots li) { width: 32px; height: 4px; margin: 0 10px; border-radius: 2px; }
  :deep(.ant-carousel .slick-dots li button) {
    width: 100%; height: 100%; border-radius: inherit;
    background: rgba(255,255,255,0.55); opacity: 1;
    box-shadow: 0 0 0 1px rgba(0,0,0,0.18);
    transition: background .25s ease, box-shadow .25s ease, transform .25s ease;
  }
  :deep(.ant-carousel .slick-dots li.slick-active button) {
    background: linear-gradient(90deg, #7C3AED, #06B6D4);
    box-shadow: 0 0 0 1px rgba(0,0,0,0.18), 0 4px 10px rgba(0,0,0,0.25);
  }
  :deep(.ant-carousel .slick-dots li:hover button) { transform: translateY(-1px); }
  
  /* ===================== Caption switch animations ===================== */
  .caption-fade-enter-active,
  .caption-fade-leave-active { transition: opacity .38s ease, transform .38s ease; }
  .caption-fade-enter-from,
  .caption-fade-leave-to { opacity: 0; transform: translateY(6px); }
  
  /* ===================== Credit badge (slides) ===================== */
  .credit {
    position: absolute;
    left: max(16px, 2vw);
    bottom: max(16px, 2vh);
    z-index: 4;
    display: inline-flex;
    align-items: center;
    gap: 6px;
    padding: 6px 10px;
    font-size: 12px;
    line-height: 1;
    color: #e6f0ff;
    text-decoration: none;
    border-radius: 999px;
    background: rgba(15, 23, 42, .38);
    backdrop-filter: blur(6px) saturate(140%);
    box-shadow: 0 4px 12px rgba(2,6,23,.25), inset 0 0 0 1px rgba(255,255,255,.15);
    transition: transform .15s ease, background .15s ease, box-shadow .15s ease;
  }
  .credit:hover { transform: translateY(-1px); background: rgba(15, 23, 42, .48); }
  
  /* ===================== CTA (slides) ===================== */
  .cta {
    position: absolute;
    right: max(24px, 3vw);
    bottom: max(24px, 4vh);
    z-index: 5;
    padding: 10px 16px;
    border: 0; border-radius: 999px; cursor: pointer;
    font-weight: 700; font-size: clamp(14px, 1.4vw, 16px); color: #fff;
    background: linear-gradient(90deg, #7C3AED, #06B6D4);
    box-shadow: 0 8px 22px rgba(16, 24, 40, 0.25);
    transition: transform .15s ease, box-shadow .15s ease, filter .15s ease;
  }
  .cta:hover { transform: translateY(-1px); box-shadow: 0 10px 26px rgba(16,24,40,.3); filter: brightness(1.05); }
  .cta:active { transform: translateY(0); box-shadow: 0 6px 16px rgba(16,24,40,.22); }
  .cta:focus-visible { outline: 3px solid rgba(124,58,237,.45); outline-offset: 2px; }
  
  /* ===================== Intro section ===================== */
  .intro { padding: 56px 0 28px 0; }
  .intro__grid2 {
    display: grid; grid-template-columns: 1fr; gap: 32px; align-items: center;
  }
  @media (min-width: 992px) {
    .intro__grid2 { grid-template-columns: 1fr 1fr; }
  }
  .intro__left { display: flex; flex-direction: column; }
  .intro__head { text-align: center; margin-bottom: 24px; }
  .intro__title { margin: 0 0 8px 0; font-size: clamp(22px, 3.2vw, 32px); font-weight: 900; letter-spacing: .2px; color: #0f172a; }
  .intro__subtitle { margin: 0 auto; max-width: 820px; color: #475569; font-size: clamp(14px, 1.6vw, 16px); }
  
  /* Cards: vertical stack */
  .intro__grid { display: grid; grid-template-columns: 1fr; gap: 18px; margin-top: 16px; }
  .card { display: flex; gap: 12px; padding: 16px 18px; border-radius: 16px; background: rgba(255,255,255,.7); backdrop-filter: blur(8px) saturate(140%); box-shadow: 0 8px 24px rgba(2,6,23,.06), 0 1px 0 rgba(2,6,23,.06) inset; transition: transform .18s ease, box-shadow .18s ease; }
  .card:hover { transform: translateY(-2px); box-shadow: 0 12px 28px rgba(2,6,23,.10); }
  .card__icon { width: 40px; height: 40px; border-radius: 10px; display: grid; place-items: center; color: #6d28d9; background: rgba(124,58,237,.12); }
  .card__title { margin: 0 0 6px 0; font-weight: 800; color: #0f172a; }
  .card__text  { margin: 0; color: #475569; }
  
  .intro__cta { margin-top: 20px; display: flex; justify-content: center; }
  .intro__btn { display: inline-flex; align-items: center; justify-content: center; padding: 10px 16px; border-radius: 999px; font-weight: 700; color: #fff; background: linear-gradient(90deg, #7C3AED, #06B6D4); text-decoration: none; box-shadow: 0 8px 22px rgba(16,24,40,.2); transition: transform .15s ease, box-shadow .15s ease, filter .15s ease; }
  .intro__btn:hover { transform: translateY(-1px); box-shadow: 0 10px 26px rgba(16,24,40,.28); filter: brightness(1.05); }
  
  /* Right image + credit */
  .intro__right { position: relative; display: flex; justify-content: center; }
  .intro__image { width: 100%; max-width: 560px; border-radius: 24px; box-shadow: 0 18px 40px rgba(2,6,23,.18); }
  /* keep it short & right-aligned */
  .credit--img { position: absolute; left: auto; right: 12px; bottom: 12px; font-size: 11px; padding: 4px 8px; background: rgba(0,0,0,0.45); border-radius: 8px; color: #fff; }
  .credit--img:hover { background: rgba(0,0,0,0.6); }
  
  /* Entrance animation for intro cards */
  .fade-up { opacity: 0; transform: translateY(8px); animation: fadeUp .5s ease forwards; animation-delay: var(--d,0ms); }
  @keyframes fadeUp { to { opacity: 1; transform: translateY(0); } }
  
  /* ===================== Core Feature Section ===================== */
  .feature { padding: 44px 0 16px 0; }
  .feature__grid { display: grid; grid-template-columns: 1fr; gap: 26px; align-items: center; }
  @media (min-width: 992px) { .feature__grid { grid-template-columns: 1fr 1.25fr; } }
  .feature__image { position: relative; }
  .feature__image img { width: 100%; border-radius: 20px; box-shadow: 0 12px 32px rgba(2,6,23,.1); object-fit: cover; }
  .feature__content { display: flex; flex-direction: column; gap: 14px; }
  /* .feature__title { margin: 0; font-weight: 900; font-size: clamp(22px, 3.2vw, 32px); color: #0f172a; } */
  .feature__title {
    margin: 0 0 16px 0; /* <-- was 8px */
    font-size: clamp(22px, 3.2vw, 32px);
    font-weight: 900;
    color: #0f172a;
  }
  .feature__subtitle { margin: 0; color: #475569; }
  .mono { font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono", "Courier New", monospace; }
  
  /* badges row */
  .badges { list-style: none; padding: 0; display: flex; gap: 10px; flex-wrap: wrap; margin: 4px 0 6px 0; }
  .badge  { display: inline-flex; align-items: center; gap: 8px; padding: 8px 12px; border-radius: 999px; background: rgba(124,58,237,.08); color: #3b0764; font-weight: 700; }
  .badge .dot { width: 22px; height: 22px; border-radius: 50%; display: grid; place-items: center; color: #fff; background: linear-gradient(90deg,#7C3AED,#06B6D4); font-size: 12px; font-weight: 800; }
  
  /* >>> FIX: make feature credit short (override .credit left) */
  .credit--feature {
    position: absolute;
    left: auto;          /* <-- key line to cancel base .credit left */
    right: 12px;
    bottom: 12px;
    font-size: 11px;
    padding: 4px 8px;
    display: inline-flex;
    width: auto;
    white-space: nowrap;
    background: rgba(0,0,0,0.45);
    border-radius: 8px;
    color: #fff;
    text-decoration: none;
    transition: background .15s ease;
  }
  .credit--feature:hover { background: rgba(0,0,0,0.6); }

  /* Center the heading block inside the right column */
.feature__head--center { 
  text-align: center;               /* center heading */
  margin-left: auto; 
  margin-right: auto; 
  max-width: 900px;
}

/* Make the 3 badges fill the entire row evenly */
.badges--equal {
  display: grid;
  grid-template-columns: repeat(3, 1fr);   /* 3 equal columns */
  gap: 12px;
  width: 100%;
  max-width: 900px;
  margin-left: auto;
  margin-right: auto;
}

.badge {
  width: 100%;
  justify-content: center;                
  text-align: center;
  padding: 10px 14px;                   
}

/* Divider line between sections */
.divider {
  border: none;
  border-top: 1px solid rgba(0, 0, 0, 0.08); /* subtle line */
  margin: 40px auto; /* space above & below */
  max-width: 960px;  /* keep line not too wide */
}
  </style>