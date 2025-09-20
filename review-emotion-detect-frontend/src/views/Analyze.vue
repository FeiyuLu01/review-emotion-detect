<template>
  <!-- Full-screen Hero -->
  <HeroAnalyze />

  <!-- Main grid: left flow rail + right stacked cards -->
  <div class="analyze-main page-grid">
    <!-- Left: flow rail (auto highlights via activeKey; sections 4–5 highlight on scroll) -->
    <AnalyzeFlowRail :activeKey="flowKey" />

    <!-- Right: five stage cards stacked vertically -->
    <div class="flow-content">

      <div id="section-input-anchor"></div>
      <!-- S1: Input (visible before Analyze; auto hidden when loading or results exist) -->
      <section id="section-input" class="stage-card" data-stage="input" v-show="!loading || hasScores">
        <div class="prethink-tip" role="note" aria-live="polite">
          <a-alert type="warning" show-icon banner :message="prethinkMsg" />
        </div>

        <a-card class="card input-card" :bordered="false">
          <a-typography-title :level="3" class="hero-gradient-title" style="margin:0 0 6px 0;">
            Analyze a Review
          </a-typography-title>
          <a-typography-text type="secondary">
            Paste any review or short text below. We will analyze its emotions using an AI model.
          </a-typography-text>

          <a-textarea
            v-model:value="text"
            :rows="10"
            placeholder="Paste your review here..."
            class="mt16"
          />

          <div class="actions">
            <a-button type="primary" size="large" :loading="loading" :disabled="!text.trim()" @click="analyze">
              Analyze
            </a-button>
            <a-button size="large" :disabled="loading" @click="reset">Clear</a-button>
          </div>
        </a-card>
      </section>

      <!-- S2: Loading (auto after clicking Analyze until API returns) -->
      <section id="section-loading" class="stage-card" data-stage="loading" v-show="loading">
        <a-card class="card result-card" :bordered="false">
          <a-typography-title :level="4" style="margin:0 0 12px 0;">Analyzing…</a-typography-title>
          <a-skeleton active :paragraph="{ rows: 4 }" />
          <a-spin class="mt16" tip="Please wait" />
        </a-card>
      </section>

      <div id="section-results-anchor"></div>

      <!-- S3: Results (auto when API data is available) -->
      <section id="section-results" class="stage-card" data-stage="results" v-show="hasScores && !loading">
        <a-card class="card result-card" :bordered="false">
          <template v-if="hasScores">
            <a-typography-title :level="4" style="margin:0 0 12px 0;">Emotion Data Insights</a-typography-title>

            <!-- Overall tone (normalized shares; always <= 100%) -->
            <a-typography-paragraph v-if="overallTone" class="overall-tone">
              {{ overallTone }}
            </a-typography-paragraph>

            <!-- Top-3 (raw probabilities) -->
            <a-typography-paragraph v-if="top3Text" class="muted">
              Dominant emotions: {{ top3Text }}
            </a-typography-paragraph>

            <!-- Richer explanation -->
            <a-typography-paragraph v-if="extraSummary" class="extra-summary">
              {{ extraSummary }}
            </a-typography-paragraph>

            <!-- CTA to open modal -->
            <div class="more-details">
              <a-button
                class="details-cta"
                size="large"
                @click="openDetails"
                aria-label="See more emotion details"
              >
                Want to see more details about emotions?
              </a-button>
            </div>
          </template>

          <template v-else>
            <a-empty description="Results will appear here." />
          </template>
        </a-card>
      </section>

      

      <!-- S4: Scientific Evidence (reveals when scrolled into view; user-controlled) -->
      <section id="section-evidence" class="stage-card" data-stage="evidence">
        <a-card class="card insights-card" :bordered="false">
          <a-typography-title :level="4" style="margin:0 0 12px 0;">
            How does the review emotion affect you?
          </a-typography-title>

          <a-spin :spinning="insightsLoading">
            <template v-if="insightsError">
              <a-alert
                type="warning"
                show-icon
                message="Failed to load deeper analysis"
                description="The emotion summary is ready, but the server analysis could not be fetched. You can try Analyze again."
              />
            </template>

            <template v-else>
              <template v-if="insights.length">
                <div v-for="(item, idx) in insights" :key="idx" class="insight-item">
                  <a-tag :color="colorForTag(item.type)" class="insight-chip">{{ item.type }}</a-tag>

                  <!-- Analysis body -->
                  <p class="insight-text">{{ item.analysis }}</p>

                  <!-- Reference block -->
                  <div v-if="item.reference" class="ref-block">
                    <span class="ref-label">Reference: </span>
                    <a
                      v-if="item.reference.url"
                      :href="item.reference.url"
                      target="_blank"
                      rel="noopener noreferrer"
                      class="ref-link"
                    >
                      {{ item.reference.title || item.reference.url }}
                    </a>
                    <span v-else class="ref-title">{{ item.reference.title }}</span>

                    <div v-if="item.reference.citation" class="ref-citation">
                      {{ item.reference.citation }}
                    </div>
                  </div>
                </div>
              </template>
            </template>
          </a-spin>
        </a-card>
      </section>

      <!-- S5: Rewrite (reveals when scrolled into view; user-controlled) -->
      <section id="section-rewrite" class="stage-card" data-stage="rewrite">
        <a-card class="card inside-card" :bordered="false">
          <a-typography-title :level="4" style="margin:0 0 12px 0;">Rewrite your review</a-typography-title>
          <a-typography-text type="secondary">
            Adjust tone & clarity based on analysis.
          </a-typography-text>

          <div class="rewrite-actions mt16">
            <a-button type="primary" @click="openRewrite">Rewrite my review</a-button>
            <a-button type="text" @click="dismissRewrite">No, thanks</a-button>
          </div>
        </a-card>
      </section>

      <!-- Charts modal (kept as-is) -->
      <a-modal
        v-model:open="detailsOpen"
        :title="modalTitle"
        width="980px"
        :footer="null"
        destroyOnClose
        @afterOpenChange="onModalOpenChange"
        @after-visible-change="onModalOpenChange"
      >
        <!-- 1) Summary bar -->
        <a-card :bordered="false" class="inside-card">
          <a-typography-text type="secondary">Percentage of Each Emotion in Review</a-typography-text>
          <div ref="modalBarRef" class="chart chart--tall"></div>
        </a-card>

        <!-- 2) Basic-7 rose pie -->
        <a-card :bordered="false" class="inside-card mt16">
          <a-typography-text type="secondary">Basic Emotions Distribution</a-typography-text>
          <div ref="pieRef" class="chart chart--tall"></div>
        </a-card>
      </a-modal>
    </div>
  </div>

  <!-- Rewrite prompt + modal (kept as-is) -->
  <div
    v-if="insights.length && !insightsLoading && !insightsError && showRewritePrompt"
    class="rewrite-suggestion"
  >
    <!-- <a-alert
      type="info"
      show-icon
      message="This reflects how the emotions in your review may influence readers. Would you like to rewrite your review?"
      :banner="false"
    /> -->
    <!-- <div class="rewrite-actions">
      <a-button type="primary" @click="openRewrite">Rewrite my review</a-button>
      <a-button type="text" @click="dismissRewrite">No, thanks</a-button>
    </div> -->
  </div>

  <a-modal
    v-model:open="rewriteOpen"
    title="Rewrite your review"
    :footer="null"
    width="720px"
    destroyOnClose
  >
    <a-alert class="rewrite-warning" type="warning" show-icon>
      <template #message>Heads up</template>
      <template #description>
        Suggestions prioritize removing profanity and overly strong wording.
        Because of this, the result can be very similar to your original, or it may slightly change the nuance.
        Please review carefully before using it as-is.
      </template>
    </a-alert>

    <div class="rewrite-form">
      <div class="rewrite-row">
        <span class="label">Tone</span>
        <a-select v-model:value="rewriteTone" style="width: 220px"
          :options="[
            { label: 'Neutral (balanced)', value: 'neutral' },
            { label: 'Positive (constructive)', value: 'positive' },
            { label: 'Polite (softer)', value: 'polite' },
          ]"
        />
      </div>

      <div class="rewrite-row">
        <span class="label">Original</span>
        <a-textarea v-model:value="rewriteInput" :rows="5" disabled/>
      </div>

      <div class="rewrite-row">
        <span class="label">Suggestion</span>
        <a-textarea v-model:value="rewriteOutput" :rows="6" placeholder="Click Rewrite to generate" />
      </div>

      <div class="rewrite-footer">
        <a-button @click="doRewrite" type="primary" :loading="rewriteLoading">Rewrite</a-button>
        <a-button :disabled="!rewriteOutput.trim()" @click="applyRewrite">Replace original</a-button>
        <a-button type="text" @click="rewriteOpen = false">Close</a-button>
      </div>
    </div>
  </a-modal>
</template>

<script setup>
import { ref, computed, watch, nextTick, onMounted, onBeforeUnmount } from 'vue'
import { message } from 'ant-design-vue'
import * as echarts from 'echarts'
import HeroAnalyze from '@/components/HeroAnalyze.vue'
import AnalyzeFlowRail from '@/components/AnalyzeFlowRail.vue'
import { gsap } from 'gsap'
import { ScrollTrigger } from 'gsap/ScrollTrigger'
import { useRoute } from 'vue-router'
import { useRouter } from 'vue-router'
import { API_BASE, ANALYSIS_BASE, REWRITE_BASE } from '@/utils/apiBase'
const router = useRouter()
gsap.registerPlugin(ScrollTrigger)
const route = useRoute()


const prethinkMsg = 'Encourages all users to critically reflect before analyzing emotions on this site.'
/* ---------------------- External endpoints ---------------------- */
// const HF_ENDPOINT = 'https://api-inference.huggingface.co/models/SamLowe/roberta-base-go_emotions'
// const BACKEND_ANALYSIS_ENDPOINT = `${import.meta.env.VITE_ANALYSIS_API}/emotion_analysis`
const BACKEND_ANALYSIS_ENDPOINT = `${ANALYSIS_BASE}/emotion_analysis`
// const API_BASE = (import.meta.env.VITE_API_BASE || 'https://api.luosong.wang').replace(/\/+$/, '')
const CLASSIFY_URL = `${API_BASE}/classify`
// const REWRITE_API_BASE = (import.meta.env.VITE_REWRITE_API_URL || API_BASE).replace(/\/+$/, '')

/*  8000 */
const REWRITE_API_BASE = (import.meta.env.VITE_REWRITE_API_URL || 'https://api.luosong.wang').replace(/\/+$/, '')



/* ---------------------- State ---------------------- */
const text = ref('')
const loading = ref(false)

const rawResult = ref(null)
const scoresMap = ref({})
const hasScores = computed(() => Object.keys(scoresMap.value).length > 0)
const HEADER_OFFSET = 64

function scrollSectionIntoView(sectionId) {
  nextTick().then(() => {
    const el = document.getElementById(sectionId)
    if (!el) return
    const rect = el.getBoundingClientRect()
    const scrollY = window.pageYOffset || document.documentElement.scrollTop
    const target = rect.top + scrollY - HEADER_OFFSET
    window.scrollTo({ top: target, behavior: 'smooth' })
  })
}

// —— watch 修改：在分析完成后滚到结果卡片 —— 
// watch([hasScores, loading], ([newHas, newLoading], [oldHas, oldLoading]) => {
//   if (newHas && !newLoading) {
//     scrollSectionIntoView('section-results-anchor')
//   }
// })

/* Insights */
const insights = ref([])           // [{ type, analysis, reference }]
const insightsLoading = ref(false)
const insightsError = ref(false)

/* ---------- Tone buckets ---------- */
const POSITIVE = [
  'admiration','amusement','approval','caring','curiosity','excitement',
  'gratitude','joy','love','optimism','pride','realization','relief'
]
const NEGATIVE = [
  'anger','annoyance','disappointment','disapproval','disgust',
  'embarrassment','fear','grief','nervousness','remorse','sadness'
]
const NEUTRAL = ['neutral']

/* ---------- Mapping 28 -> basic-7 ---------- */
const BASIC7 = {
  Neutral: ['neutral'],
  Happiness: ['joy','excitement','love','admiration','amusement','approval','gratitude','pride','relief','optimism','caring','curiosity'],
  Surprise: ['surprise','realization'],
  Sadness: ['sadness','disappointment','grief','remorse','embarrassment'],
  Fear: ['fear','nervousness'],
  Anger: ['anger','annoyance','disapproval'],
  Disgust: ['disgust'],
}

/* ---------- All labels ---------- */
const ALL_LABELS = [
  'admiration','amusement','anger','annoyance','approval','caring',
  'confusion','curiosity','desire','disappointment','disapproval','disgust',
  'embarrassment','excitement','fear','gratitude','grief','joy','love',
  'nervousness','optimism','pride','realization','relief','remorse','sadness',
  'surprise','neutral'
]

/* ---------- Data shaping ---------- */
const chartData = computed(() => {
  const arr = ALL_LABELS.map(l => ({ label: l, score: +(scoresMap.value[l] ?? 0) }))
  return arr.sort((a, b) => b.score - a.score)
})

const chartTopN = computed(() => {
  const all = ALL_LABELS.map(l => ({ label: l, score: +(scoresMap.value[l] ?? 0) }))
  const positives = all.filter(d => d.score > 0).sort((a, b) => b.score - a.score)
  const zeros = all.filter(d => d.score === 0)
  if (positives.length === 0) return zeros.slice(0, 8)
  if (positives.length <= 8) return [...positives, ...zeros.slice(0, 8 - positives.length)]
  return positives
})

/* ---------- Tone summary ---------- */
function computeToneSummary(map) {
  const sum = (labels) => labels.reduce((s, l) => s + (Number(map[l] || 0)), 0)
  const pos = sum(POSITIVE), neg = sum(NEGATIVE), neu = sum(NEUTRAL)
  const denom = pos + neg + neu || 1e-12
  const posShare = pos / denom, negShare = neg / denom, neuShare = neu / denom
  let tone = 'neutral', top = neuShare
  if (posShare > negShare && posShare > neuShare) { tone = 'positive'; top = posShare }
  else if (negShare > posShare && negShare > neuShare) { tone = 'negative'; top = negShare }
  return { tone, posPct: +(posShare * 100).toFixed(1), negPct: +(negShare * 100).toFixed(1), neuPct: +(neuShare * 100).toFixed(1), topPct: +(top * 100).toFixed(1) }
}
const overallTone = computed(() => {
  if (!hasScores.value) return ''
  const { tone, topPct } = computeToneSummary(scoresMap.value)
  if (tone === 'positive') return `Overall the text conveys a positive tone (${topPct}%).`
  if (tone === 'negative') return `Overall the text leans negative (${topPct}%).`
  return `Overall the text is mostly neutral (${topPct}%).`
})

/* ---------- Extra summary ---------- */
const extraSummary = computed(() => {
  if (!hasScores.value) return ''
  const { tone } = computeToneSummary(scoresMap.value)
  if (tone === 'positive') {
    return `Suggesting enthusiasm or approval. This indicates the writer is likely sharing a favorable perspective, with language that conveys excitement or satisfaction. Overall, the review feels encouraging and reflects a supportive attitude toward the subject.`
  }
  if (tone === 'negative') {
    return `Showing strong dissatisfaction or criticism. The writer may be expressing frustration, disappointment, or even hostility, which points to an unfavorable experience. Overall, the tone suggests complaints or negative sentiment that could affect the reader’s perception.`
  }
  return `Indicating a more balanced or objective statement. The text contains limited emotional intensity, focusing more on description or factual content rather than strong opinions. Overall, this makes the review come across as calm, informative, and relatively impartial.`
})

/* ---------- Top-3 raw text ---------- */
const top3Text = computed(() => {
  if (!chartData.value.length) return ''
  const top = chartData.value.filter(x => x.score > 0).slice(0, 3)
  if (!top.length) return ''
  return top.map(x => `${x.label} (${(x.score*100).toFixed(1)}%)`).join(', ')
})

const flowKey = computed(() => {
  if (loading.value) return 'loading'
  if (hasScores.value) return 'results'
  return 'input'
})

/* ---------- Backend insights ---------- */
async function fetchDeeperAnalysis(labelArray) {
  insightsLoading.value = true
  insightsError.value = false
  insights.value = []

  try {
    const res = await fetch(BACKEND_ANALYSIS_ENDPOINT, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(labelArray),
    })
    if (!res.ok) throw new Error('Backend analysis API error: ' + res.status)

    const json = await res.json()
    const list =
      Array.isArray(json?.data?.data) ? json.data.data :
      Array.isArray(json?.data)       ? json.data :
      Array.isArray(json)             ? json :
      []

    insights.value = list
      .filter(it => it && typeof it.analysis === 'string')
      .map(it => ({
        type: String(it.type || '').trim() || '—',
        analysis: it.analysis,
        reference: it.reference ? {
          title: it.reference.title || '',
          url: it.reference.url || '',
          citation: it.reference.citation || ''
        } : null
      }))
  } catch (e) {
    console.error(e)
    insightsError.value = true
  } finally {
    insightsLoading.value = false
  }
}

/* ---------- Analyze ---------- */
async function analyze(options = {}) {
  const { skipLoadingScroll = false } = options
  if (!text.value.trim()) return
  loading.value = true
  router.replace({ hash: '#section-loading' })
  insights.value = []
  insightsError.value = false
  await nextTick()
  // if (!skipLoadingScroll) {
  //   scrollSectionIntoView('section-loading')
  // }

  try {
    const resp = await fetch(CLASSIFY_URL, {
     method: 'POST',
     headers: { 'Content-Type': 'application/json' },
     body: JSON.stringify({ text: text.value }),
   })
    if (!resp.ok) throw new Error('HF API error: ' + resp.status)
    // const data = await resp.json()
    const data = await resp.json()
    // const list = Array.isArray(data?.[0]) ? data[0] : Array.isArray(data) ? data : []
    const list = Array.isArray(data?.results) ? data.results : []
    rawResult.value = list

    const map = {}
    for (const { label, score } of list) {
      const key = String(label).trim().toLowerCase()
      map[key] = (map[key] ?? 0) + Number(score || 0)
    }
    ALL_LABELS.forEach(l => { if (!(l in map)) map[l] = 0 })
    scoresMap.value = map

    const top3Basic7 = pickTop3Basic7ForBackend()
    if (top3Basic7.length) {
      await fetchDeeperAnalysis(top3Basic7)
    }
  } catch (err) {
    console.error(err)
    message.error('Failed to analyze. Please try again.')
  } finally {
    loading.value = false
    router.replace({ hash: '#section-results-anchor' })
    // scrollSectionIntoView('section-results-anchor')
  }
}

function reset() {
  text.value = ''
  rawResult.value = null
  scoresMap.value = {}
  insights.value = []
  insightsError.value = false
}

/* ---------- Charts ---------- */
const detailsOpen = ref(false)
const modalTitle = computed(() => 'Emotion details')

const modalBarRef = ref(null)
const pieRef = ref(null)

let modalBarChart = null
let pieChart = null

function openDetails() { detailsOpen.value = true }

const BAR_GRADIENTS = [
  ['#22d3ee', '#7c3aed'],
  ['#34d399', '#06b6d4'],
  ['#f59e0b', '#ef4444'],
  ['#60a5fa', '#a78bfa'],
  ['#fb7185', '#f472b6'],
  ['#f97316', '#f43f5e'],
  ['#10b981', '#14b8a6'],
  ['#38bdf8', '#6366f1'],
]
function barGradientAt(idx) {
  const [c1, c2] = BAR_GRADIENTS[idx % BAR_GRADIENTS.length]
  return new echarts.graphic.LinearGradient(0, 0, 1, 0, [
    { offset: 0, color: c1 },
    { offset: 1, color: c2 },
  ])
}
function buildBarOption(labels, values) {
  return {
    grid: { left: 120, right: 18, top: 10, bottom: 40 },
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' }, valueFormatter: v => `${Number(v).toFixed(2)}%` },
    xAxis: { type: 'value', max: (val) => Math.max(100, Math.ceil((val.max + 5) / 10) * 10), axisLabel: { formatter: '{value}%' } },
    yAxis: { type: 'category', data: labels },
    series: [{
      name: 'probability',
      type: 'bar',
      data: values,
      barWidth: 16,
      itemStyle: {
        borderRadius: 6,
        color: (p) => barGradientAt(p.dataIndex),
        shadowColor: 'rgba(2,6,23,.10)',
        shadowBlur: 6,
        shadowOffsetY: 2,
      }
    }]
  }
}

function buildBasic7Data(map) {
  const order = ['Neutral','Happiness','Surprise','Sadness','Fear','Anger','Disgust']
  const raw = {}
  let total = 0
  for (const key of order) {
    const arr = BASIC7[key]
    const sum = arr.reduce((s, lbl) => s + (Number(map[lbl] || 0)), 0)
    raw[key] = sum
    total += sum
  }
  if (total < 1e-12) return order.map(name => ({ name, value: 0 }))
  return order.map(name => ({ name, value: +(raw[name] / total * 100).toFixed(3) }))
}
const BASIC7_RENAME_FOR_BACKEND = { Happiness: 'Joy' }
function pickTop3Basic7ForBackend() {
  const data7 = buildBasic7Data(scoresMap.value)
  return data7
    .filter(d => d.value > 0)
    .sort((a, b) => b.value - a.value)
    .slice(0, 3)
    .map(d => BASIC7_RENAME_FOR_BACKEND[d.name] || d.name)
}

function waitForSize(el, timeout = 800) {
  return new Promise((resolve) => {
    const t0 = performance.now()
    function check() {
      const r = el.getBoundingClientRect()
      if (r.width > 10 && r.height > 10) resolve(true)
      else if (performance.now() - t0 > timeout) resolve(false)
      else requestAnimationFrame(check)
    }
    check()
  })
}

function renderBarModal() {
  if (!modalBarRef.value) return
  modalBarChart?.dispose()
  modalBarChart = echarts.init(modalBarRef.value)
  const labels = chartTopN.value.map(d => d.label)
  const values = chartTopN.value.map(d => +(d.score * 100).toFixed(2))
  modalBarChart.setOption(buildBarOption(labels, values))
}

function renderPie() {
  if (!pieRef.value) return
  pieChart?.dispose()
  pieChart = echarts.init(pieRef.value)

  const data7 = buildBasic7Data(scoresMap.value)
  const maxItem = data7.reduce((m, d) => (d.value > m.value ? d : m), { value: -1 })
  const useRose = maxItem.value < 60

  const COLORS = {
    Neutral:  '#94a3b8',
    Happiness:'#22c55e',
    Joy:      '#22c55e',
    Surprise: '#f59e0b',
    Sadness:  '#60a5fa',
    Fear:     '#6366f1',
    Anger:    '#ef4444',
    Disgust:  '#a78bfa',
  }
  const color = data7.map(d => COLORS[d.name] || '#9ca3af')

  const HIDE_THRESHOLD = 2
  const seriesData = data7.map(d => {
    const tooSmall = d.value < HIDE_THRESHOLD && d.name !== 'Neutral'
    return {
      name: d.name,
      value: d.value,
      label: tooSmall ? { show: false } : undefined,
      labelLine: tooSmall ? { show: false } : undefined,
    }
  })

  pieChart.setOption({
    color,
    animation: true,
    animationDuration: 700,
    animationEasing: 'cubicOut',
    legend: {
      type: 'scroll',
      orient: 'horizontal',
      bottom: 6,
      icon: 'roundRect',
      itemHeight: 10,
      itemWidth: 14,
      textStyle: { color: '#334155' }
    },
    tooltip: { trigger: 'item', valueFormatter: v => `${Number(v).toFixed(2)}%` },
    series: [{
      name: 'Basic Emotions',
      type: 'pie',
      center: ['50%','52%'],
      radius: useRose ? ['34%','72%'] : ['40%','66%'],
      roseType: useRose ? 'radius' : false,
      startAngle: 90,
      minAngle: useRose ? 2 : 4,
      minShowLabelAngle: 5,
      itemStyle: { borderRadius: 8, borderColor: '#fff', borderWidth: 1 },
      label: {
        formatter: (p) => p.value < HIDE_THRESHOLD && p.name !== 'Neutral'
          ? ''
          : `{b|${p.name}}\n{v|${p.percent.toFixed(2)}%}`,
        rich: {
          b: { fontWeight: 600, color: '#0f172a' },
          v: { color: '#334155' }
        }
      },
      labelLine: {
        length: 14, length2: 10, smooth: 0.2,
        lineStyle: { color: '#94a3b8' }
      },
      labelLayout: { hideOverlap: true, moveOverlap: 'shiftY' },
      emphasis: {
        scale: true, scaleSize: 8,
        itemStyle: { shadowBlur: 15, shadowColor: 'rgba(2,6,23,.18)' },
        label: { show: true }
      },
      data: seriesData
    }],
    graphic: [{
      type: 'text',
      left: '46.5%',
      top: '47%',
      style: {
        text: 'Overall\nEmotion',
        textAlign: 'center', fontSize: 16, fontWeight: 700, lineHeight: 20, fill: '#0f172a'
      }
    }]
  })
}

/* ---------- Tag colors ---------- */
const TAG_COLORS = {
  Neutral:  '#94a3b8',
  Joy:      '#22c55e',
  Happiness:'#22c55e',
  Surprise: '#f59e0b',
  Sadness:  '#60a5fa',
  Fear:     '#6366f1',
  Anger:    '#ef4444',
  Disgust:  '#a78bfa',
}
function colorForTag(name) {
  return TAG_COLORS[name] || '#9ca3af'
}

/* ---------- Modal lifecycle ---------- */
async function onModalOpenChange(opened) {
  if (!opened) {
    modalBarChart?.dispose(); modalBarChart = null
    pieChart?.dispose();      pieChart = null
    return
  }
  await nextTick()
  await Promise.all([
    modalBarRef.value ? waitForSize(modalBarRef.value) : Promise.resolve(false),
    pieRef.value ? waitForSize(pieRef.value) : Promise.resolve(false),
  ])
  renderBarModal()
  renderPie()
  requestAnimationFrame(() => {
    modalBarChart?.resize()
    pieChart?.resize()
  })
}

watch(detailsOpen, async (opened) => {
  if (!opened) return
  await nextTick()
  await Promise.all([
    modalBarRef.value ? waitForSize(modalBarRef.value) : Promise.resolve(false),
    pieRef.value ? waitForSize(pieRef.value) : Promise.resolve(false),
  ])
  renderBarModal()
  renderPie()
  requestAnimationFrame(() => {
    modalBarChart?.resize()
    pieChart?.resize()
  })
})





onMounted(() => {
  window.addEventListener('resize', () => {
    modalBarChart?.resize()
    pieChart?.resize()
  })
})

 // ========== Auto-run when navigated with ?q=... ==========
 onMounted(async () => {
   const q = (route.query.q ?? '').toString().trim()
   if (q) {
    if (route.hash) {
      const noHash = route.fullPath.split('#')[0]
      window.history.replaceState(null, '', noHash)
    }
     // input content that from home page
     text.value = q
     await nextTick()
     analyze({ skipLoadingScroll: true })
   }
   if (route.hash === '#section-results-anchor') {
     await nextTick()
     const el = document.querySelector('#section-results-anchor')
     if (el) el.scrollIntoView({ behavior: 'smooth', block: 'start' })
   }

   
 })


onBeforeUnmount(() => {
  modalBarChart?.dispose(); modalBarChart = null
  pieChart?.dispose();      pieChart = null
})

/* ---------- Rewrite prompt state ---------- */
const showRewritePrompt = ref(true)
function openRewrite() {
  rewriteInput.value = text.value
  rewriteOutput.value = ''
  rewriteOpen.value = true
}
function dismissRewrite() { showRewritePrompt.value = false }

/* ---------- Rewrite modal state ---------- */
const rewriteOpen = ref(false)
const rewriteTone = ref('neutral')
const rewriteInput = ref('')
const rewriteOutput = ref('')
const rewriteLoading = ref(false)

function normalizeSpaces(str) {
  return str.replace(/\s+/g, ' ').replace(/\s([?.!,;:])/g, '$1').trim()
}

/* ✅ 使用本地后端重写（不再走 HF 在线模型） */
// async function doRewrite() {
//   const src = (rewriteInput.value || '').trim()
//   if (!src) return
//   rewriteLoading.value = true
//   try {
//     const resp = await fetch(`${REWRITE_API_BASE}/rewrite`, {
//       method: 'POST',
//       headers: { 'Content-Type': 'application/json' },
//       body: JSON.stringify({ text: src, tone: rewriteTone.value })
//     })
//     if (!resp.ok) {
//       const txt = await resp.text().catch(() => '')
//       throw new Error(`Rewrite API ${resp.status}: ${txt || resp.statusText}`)
//     }
//     const data = await resp.json()
//     const out = (data && data.text ? data.text : '').trim()
//     rewriteOutput.value = normalizeSpaces(out || src)
//   } catch (e) {
//     console.error(e)
//     message.error('Rewrite failed. Please make sure the local rewrite server is running.')
//   } finally {
//     rewriteLoading.value = false
//   }
// }


async function doRewrite() {
  const src = (rewriteInput.value || '').trim()
  if (!src) return
  rewriteLoading.value = true
  try {
    const resp = await fetch(`${REWRITE_BASE}/rewrite`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ text: src, tone: rewriteTone.value })
    })
    if (!resp.ok) {
      const txt = await resp.text().catch(() => '')
      throw new Error(`Rewrite API ${resp.status}: ${txt || resp.statusText}`)
    }
    const data = await resp.json()
    const out = (data && data.text ? data.text : '').trim()
    rewriteOutput.value = normalizeSpaces(out || src)
  } catch (e) {
    console.error(e)
    message.error('Rewrite failed. Please make sure the local rewrite server is running.')
  } finally {
    rewriteLoading.value = false
  }
}

function applyRewrite() {
  if (!rewriteOutput.value.trim()) return
  text.value = rewriteOutput.value
  rewriteOpen.value = false
  message.success('Replaced the original text.')
}
</script>

<style scoped>
.page-grid {
  position: relative; z-index: 1; background: #0a0d1a;
  display: grid; grid-template-columns: 240px 1fr; gap: 18px;
  padding: 16px clamp(12px, 3vw, 24px) 24px;
}
.flow-content { display: grid; gap: 16px; }
.stage-card { scroll-margin-top: 88px; } /* 便于锚点滚动停靠 */

@media (max-width: 992px){
  .page-grid { grid-template-columns: 1fr; }
}

.analyze { padding-top: 0; }
/* .analyze { padding-top: 6px; } */
.mt16 { margin-top: 16px; }
.analyze-main { position: relative; z-index: 1; background: #0a0d1a; }
.hero-gradient-title {
  background: linear-gradient(90deg,#ffb3c1,#9b5de5,#00bbf9,#ffd6a5,#ff006e);
  background-size: 200% 100%;
  -webkit-background-clip: text; background-clip: text; color: transparent;
  animation: hueShift 12s linear infinite;
}
@keyframes hueShift { 0%{background-position:0% 50%} 100%{background-position:200% 50%} }

.input-card, .result-card { border-radius: var(--ml-radius); }
.actions { display:flex; gap:12px; margin-top: 12px; }

.muted { color: #475569; }

/* charts used in modal */
.chart { width: 100%; height: 280px; }
.chart--tall { height: 320px; width: 100%; }

/* CTA button */
.more-details { display:flex; justify-content:center; margin: 10px 0 4px 0; }
.details-cta {
  padding: 10px 18px; border-radius: 999px; font-weight: 600; letter-spacing: .1px;
  border: 1px solid transparent;
  background: linear-gradient(#fff,#fff) padding-box,
              linear-gradient(135deg,#06B6D4,#7C3AED) border-box;
  color: #0891b2;
  box-shadow: 0 6px 18px rgba(2,6,23,.06);
  transition: transform .15s ease, box-shadow .15s ease, background .15s ease;
}
.details-cta:hover {
  background: linear-gradient(#f8fafc,#f8fafc) padding-box,
              linear-gradient(135deg,#06B6D4,#7C3AED) border-box;
  transform: translateY(-1px);
  box-shadow: 0 10px 22px rgba(2,6,23,.10);
}
.details-cta:focus-visible { outline: 3px solid rgba(6,182,212,.45); outline-offset: 2px; }

/* inner cards inside modal */
.inside-card { border-radius: 12px; }

/* insights section (bottom, full width) */
.insights-wrap { margin-top: 16px; margin-bottom: 24px; }
.insights-card { border-radius: var(--ml-radius); }
.insight-item { margin-bottom: 14px; }
.insight-chip { margin-bottom: 6px; }
.insight-text { margin: 0; color: #0f172a; line-height: 1.6; font-size: 16px; }
.insight-text strong { font-weight: 600; }

/* Overall tone line should be black */
.overall-tone { color: #000; font-weight: 500; }

.rewrite-suggestion { margin-top: 16px; padding-top: 4px; }
.rewrite-actions { display: flex; gap: 8px; margin-top: 10px; }

.rewrite-form .rewrite-row {
  display: grid;
  grid-template-columns: 110px 1fr;
  gap: 12px;
  align-items: start;
  margin-bottom: 14px;
}
.rewrite-form .label { color: #334155; font-weight: 600; line-height: 32px; }
.rewrite-footer { display: flex; gap: 10px; justify-content: flex-end; margin-top: 6px; }
.rewrite-warning {
  margin-bottom: 12px;
  border-radius: 8px;
}

.prethink-tip {
  display: flex;
  justify-content: center;
  margin: 8px 0 16px; 
}

.prethink-tip :deep(.ant-alert) {
  max-width: 980px;     
  width: 100%;
  border-radius: 10px;
}

.prethink-tip :deep(.ant-alert-message) {
  width: 100%;
  text-align: center;
  font-weight: 600;
}

#section-input-anchor { scroll-margin-top: 64px; }
#section-results-anchor { scroll-margin-top: 64px; }
</style>