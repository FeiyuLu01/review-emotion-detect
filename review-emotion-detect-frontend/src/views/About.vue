<template>
  <div class="about container">
    <!-- ===== Top: stacked cards (left) + hero image (right) ===== -->
    <a-row :gutter="[16, 16]" class="section">
      <!-- Left: About + Mission (stacked) -->
      <a-col :xs="24" :lg="10">
        <div class="stacked-cards" ref="leftColRef">
          <a-card class="about-card fill" :bordered="false">
            <a-typography-title :level="2" style="margin:0 0 8px 0">About MoodLens</a-typography-title>
            <a-typography-paragraph type="secondary" style="margin-bottom:12px">
              MoodLens is a lightweight tool that surfaces the emotional tone of short texts.
              By making emotions visible and suggesting constructive alternatives, it helps keep
              online conversations clearer, kinder, and more effective.
            </a-typography-paragraph>
            <div class="badges">
              <a-tag color="blue">Gentle guidance</a-tag>
              <a-tag color="green">Actionable</a-tag>
              <a-tag color="purple">Privacy-first</a-tag>
            </div>
          </a-card>

          <a-card class="about-card fill bottom-card" :bordered="false">
            <a-typography-title :level="5" style="margin:0 0 8px 0">Our mission</a-typography-title>
            <a-typography-title :level="4" style="margin:0 0 12px 0">
              See the emotion — don’t be led by it
            </a-typography-title>
            <ul class="bullet">
              <li>Detect emotional tendencies quickly.</li>
              <li>Offer rewrites that keep meaning while softening tone.</li>
              <li>Improve communication without storing your text.</li>
            </ul>
          </a-card>
        </div>
      </a-col>

      <!-- Right: hero image (height synced to left stack on ≥lg) -->
      <a-col :xs="24" :lg="14" class="image-col">
        <div class="visual" ref="heroRef">
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
        <a-card class="about-card" :bordered="false">
          <a-typography-title :level="4" style="margin:0 0 12px 0">What can MoodLens do?</a-typography-title>
          <div class="feature">
            <div class="feature-title">Identify emotional direction</div>
            <div class="feature-desc">Extract key emotions (e.g., positive, negative, surprise) with proportions and a dominant tone.</div>
          </div>
          <div class="feature">
            <div class="feature-title">Suggest better phrasing</div>
            <div class="feature-desc">Provide neutral / positive / polite rewrites that keep your meaning and reduce friction.</div>
          </div>
          <div class="feature">
            <div class="feature-title">Explain likely impact</div>
            <div class="feature-desc">Plain-language hints about how readers may feel, so you can decide whether to adjust.</div>
          </div>
        </a-card>
      </a-col>

      <a-col :xs="24" :md="10">
        <a-card class="about-card" :bordered="false">
          <a-typography-title :level="4" style="margin:0 0 12px 0">How it works</a-typography-title>
          <div class="steps">
            <div class="step"><div class="dot">1</div><div class="step-body"><div class="step-title">Paste your text</div><div class="step-desc">A review, comment, or short paragraph.</div></div></div>
            <div class="step"><div class="dot">2</div><div class="step-body"><div class="step-title">Local/controlled inference</div><div class="step-desc">Processed in session or on a controlled server. No content retention.</div></div></div>
            <div class="step"><div class="dot">3</div><div class="step-body"><div class="step-title">Visualize results</div><div class="step-desc">Charts and a short summary of emotion distribution.</div></div></div>
            <div class="step"><div class="dot">4</div><div class="step-body"><div class="step-title">Get suggestions</div><div class="step-desc">Tone-aware rewrites that avoid over-editing your voice.</div></div></div>
          </div>
        </a-card>
      </a-col>
    </a-row>

    <!-- ===== New section: Left image + Right stacked cards (Privacy & Scope) ===== -->
    <a-row :gutter="[16, 16]" class="section">
      <!-- Left: photo (height synced to right stack on ≥lg) -->
      <a-col :xs="24" :lg="10" class="image-col">
        <div class="visual" ref="privacyRef">
          <img :src="privacy01" alt="Privacy illustration" class="visual-img" />
          <a
            class="credit-badge"
            href="https://www.pexels.com/photo/person-holding-smartphone-9145346/"
            target="_blank" rel="noopener noreferrer"
          >Photo: Pexels</a>
        </div>
      </a-col>

      <!-- Right: stacked cards (Privacy & Responsible Use + Scope & limitations) -->
      <a-col :xs="24" :lg="14">
        <div class="stacked-cards" ref="rightStackRef">
          <a-card class="about-card fill" :bordered="false">
            <a-typography-title :level="4" style="margin:0 0 12px 0">Privacy & Responsible Use</a-typography-title>
            <a-descriptions bordered :column="1">
              <a-descriptions-item label="Data minimization">
                Your text is used only for the current analysis. We don’t use it for training or long-term storage.
              </a-descriptions-item>
              <a-descriptions-item label="Explainability">
                We describe results in clear language and note uncertainty or limits when relevant.
              </a-descriptions-item>
              <a-descriptions-item label="No over-automation">
                Suggestions are guidance, not decisions. You remain the author — we avoid “speaking on your behalf”.
              </a-descriptions-item>
            </a-descriptions>
          </a-card>

          <a-card class="about-card fill info-card bottom-card" :bordered="false">
            <a-typography-title :level="4" style="margin:0 0 8px 0">Scope & limitations</a-typography-title>
            <a-typography-paragraph style="margin:0">
              Emotion analysis is statistical and can be affected by context, sarcasm, and ambiguity.
              For high-stakes cases (e.g., PR, safety), combine with human review.
            </a-typography-paragraph>
          </a-card>
        </div>
      </a-col>
    </a-row>

    <!-- ===== Team & Contact ===== -->
    <a-row :gutter="[16, 16]" class="section">
      <a-col :xs="24" :md="14">
        <a-card class="about-card" :bordered="false">
          <a-typography-title :level="4" style="margin:0 0 12px 0">Who we are</a-typography-title>
          <a-typography-paragraph type="secondary">
            We’re a small team focused on human-AI collaboration and digital citizenship. Our backgrounds
            span interaction design, NLP, and data visualization. We aim to balance usability and responsibility.
          </a-typography-paragraph>
          <div class="badges"><a-tag>Human-AI Interaction</a-tag><a-tag>Responsible AI</a-tag><a-tag>Data Visualization</a-tag><a-tag>Product Thinking</a-tag></div>
        </a-card>
      </a-col>

      <a-col :xs="24" :md="10">
        <a-card class="about-card" :bordered="false">
          <a-typography-title :level="4" style="margin:0 0 12px 0">Contact & feedback</a-typography-title>
          <a-typography-paragraph type="secondary" style="margin-bottom:12px">
            Got ideas, collaboration requests, or want to use MoodLens in teaching/research?
          </a-typography-paragraph>
          <div class="contact-actions">
            <a-button type="primary" href="mailto:moodlens03@gmail.com" target="_blank">Email us</a-button>
            <a-button href="/#try" type="default">Try MoodLens</a-button>
          </div>
          <a-typography-paragraph type="secondary" style="margin-top:10px">
            We read every message and use feedback to improve — without collecting personal content.
          </a-typography-paragraph>
        </a-card>
      </a-col>
    </a-row>

    <!-- ===== FAQ (unchanged) ===== -->
    <a-card class="about-card section" :bordered="false">
      <a-typography-title :level="4" style="margin:0 0 12px 0">FAQ</a-typography-title>
      <a-collapse class="faq" accordion ghost expand-icon-position="end">
        <a-collapse-panel key="1">
          <template #header>
            <div class="faq-header"><span class="chip chip-q">Q</span><span class="faq-question">Why do some rewrites look very similar to my original?</span></div>
          </template>
          <div class="faq-answer"><span class="chip chip-a">A</span><div class="faq-answer-body">To preserve meaning and voice, the model makes minimal wording changes unless the tone is strong. Try the <strong>“polite”</strong> or <strong>“positive”</strong> tone for a more noticeable rewrite.</div></div>
        </a-collapse-panel>
        <a-collapse-panel key="2">
          <template #header>
            <div class="faq-header"><span class="chip chip-q">Q</span><span class="faq-question">Can I clear my analysis results?</span></div>
          </template>
          <div class="faq-answer"><span class="chip chip-a">A</span><div class="faq-answer-body">Yes. Use <strong>“Clear”</strong> on the Analyze page. Nothing is stored after your session.</div></div>
        </a-collapse-panel>
        <a-collapse-panel key="3">
          <template #header>
            <div class="faq-header"><span class="chip chip-q">Q</span><span class="faq-question">Which languages are supported?</span></div>
          </template>
          <div class="faq-answer"><span class="chip chip-a">A</span><div class="faq-answer-body">English works best. Other languages may work but can be less accurate.</div></div>
        </a-collapse-panel>
      </a-collapse>
    </a-card>
  </div>
</template>

<script setup>
/**
 * Keep hero (top-right) and privacy (middle-left) images equal height
 * with their stacked-card counterparts on ≥ lg screens. On smaller
 * screens heights revert to auto (natural flow).
 */
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'

// Swap the extensions to your real files: .jpg/.png/.webp, etc.
import digital02 from '@/assets/digital02.jpg'
import privacy01 from '@/assets/privacy01.jpg'

const leftColRef = ref(null)     // top-left stacked cards (About+Mission)
const heroRef = ref(null)        // top-right image container

const rightStackRef = ref(null)  // middle-right stacked cards (Privacy+Scope)
const privacyRef = ref(null)     // middle-left image container

function isDesktop() {
  // Ant Design Vue lg breakpoint ≈ 992px
  return window.innerWidth >= 992
}

function syncHeights() {
  if (isDesktop()) {
    if (leftColRef.value && heroRef.value) {
      heroRef.value.style.height = `${leftColRef.value.offsetHeight}px`
    }
    if (rightStackRef.value && privacyRef.value) {
      privacyRef.value.style.height = `${rightStackRef.value.offsetHeight}px`
    }
  } else {
    if (heroRef.value) heroRef.value.style.height = 'auto'
    if (privacyRef.value) privacyRef.value.style.height = 'auto'
  }
}

function onResize() {
  window.requestAnimationFrame(syncHeights)
}

onMounted(async () => {
  await nextTick()
  syncHeights()
  window.addEventListener('resize', onResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', onResize)
})
</script>

<style scoped>
/* ===== Section spacing ===== */
.section { margin-bottom: 24px; }

/* ===== Cards ===== */
.about-card {
  margin-bottom: 24px;
  border-radius: 12px;
  box-shadow: 0 6px 18px rgba(2, 6, 23, 0.06);
}
.about-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 28px rgba(2,6,23,0.12);
  transition: all .25s ease;
}

/* Stack helpers: two cards evenly share available height */
.stacked-cards { display: flex; flex-direction: column; gap: 16px; height: 100%; }
.fill { flex: 1; display: flex; flex-direction: column; }

/* ===== Shared image container (used by both sections) ===== */
.image-col { display: flex; }
.visual {
  position: relative;
  flex: 1;
  overflow: hidden;
  min-height: 280px;                 /* safety for short viewports */
  border-radius: 28px;
  box-shadow: 0 16px 36px rgba(2,6,23,0.18);
  background: radial-gradient(120% 120% at 10% 0%, #eef2ff, #f5f3ff 60%, #ffffff 100%);
}
.visual-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transform: scale(1.02);
  transition: transform .5s ease;
}
.visual:hover .visual-img { transform: scale(1.05); }

/* Credit pill */
.credit-badge {
  position: absolute;
  right: 14px;
  bottom: 14px;
  font-size: 12px;
  font-weight: 600;
  color: #fff;
  background: rgba(15,23,42,.55);
  padding: 6px 10px;
  border-radius: 999px;
  text-decoration: none;
  backdrop-filter: blur(6px);
  border: 1px solid rgba(255,255,255,.18);
}

/* Feature / steps / badges etc. */
.bullet { margin: 0; padding-left: 18px; color: #334155; }
.bullet li { margin: 6px 0; }

.feature { margin-bottom: 14px; }
.feature-title { font-weight: 600; color: #0f172a; margin-bottom: 4px; }
.feature-desc { color: #475569; }

.steps { display: grid; gap: 12px; }
.step { display: grid; grid-template-columns: 28px 1fr; gap: 10px; align-items: start; }
.dot {
  width: 28px; height: 28px; border-radius: 50%;
  display: grid; place-items: center;
  font-weight: 700; font-size: 13px; color: #0f172a;
  background: #eef2ff; border: 1px solid #c7d2fe;
}
.step-title { font-weight: 600; color: #0f172a; }
.step-desc { color: #64748b; }

.badges { display: flex; gap: 8px; flex-wrap: wrap; }
.contact-actions { display: flex; gap: 10px; flex-wrap: wrap; }

/* FAQ visuals */
.faq :deep(.ant-collapse-item) { border-bottom: 1px solid #e5e7eb; padding: 6px 0; }
.faq :deep(.ant-collapse-header) { padding: 8px 0 !important; }
.faq-header { display: flex; align-items: center; gap: 10px; }
.faq-question { font-weight: 700; color: #0f172a; }
.chip {
  display: inline-flex; align-items: center; justify-content: center;
  height: 22px; min-width: 22px; padding: 0 6px; border-radius: 999px;
  font-size: 12px; font-weight: 700;
}
.chip-q { background: #eef2ff; color: #3730a3; border: 1px solid #c7d2fe; }
.chip-a { background: #ecfeff; color: #155e75; border: 1px solid #a5f3fc; }
.faq-answer {
  display: grid; grid-template-columns: 28px 1fr; gap: 10px;
  background: #f8fafc; border: 1px solid #e2e8f0; border-left: 4px solid #22d3ee;
  border-radius: 10px; padding: 12px; color: #0f172a;
}
.faq-answer-body { color: #334155; }

/* Mobile: let images auto-height */
@media (max-width: 991.98px) {
  .visual { height: auto !important; }
}

.bottom-card{margin-bottom: 0;}
</style>