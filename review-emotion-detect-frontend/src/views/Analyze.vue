<template>
  <div class="analyze container">
    <a-row :gutter="[16,16]">
      <!-- Left: input -->
      <a-col :xs="24" :md="14">
        <a-card class="card input-card" :bordered="false">
          <a-typography-title :level="3" style="margin:0 0 6px 0;">Analyze a Review</a-typography-title>
          <a-typography-text type="secondary">
            Paste any review or short text below. We will analyze its emotions using Hugging Face.
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
      </a-col>

      <!-- Right: result (text-only summary + CTA) -->
      <a-col :xs="24" :md="10">
        <a-card class="card result-card" :bordered="false">
          <template v-if="hasScores">
            <a-typography-title :level="4" style="margin:0 0 12px 0;">Summary</a-typography-title>

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

            <!-- Centered CTA button to open modal -->
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
      </a-col>
    </a-row>

    <!-- ===== Modal: stacked charts (bar -> 7-bucket rose pie) ===== -->
    <a-modal
      v-model:open="detailsOpen"
      :title="modalTitle"
      width="980px"
      :footer="null"
      destroyOnClose
      @afterOpenChange="onModalOpenChange"
      @after-visible-change="onModalOpenChange"
    >
      <!-- 1) Summary bar (new instance, same data) -->
      <a-card :bordered="false" class="inside-card">
        <a-typography-text type="secondary">Percentage of Each Emotion in Review</a-typography-text>
        <div ref="modalBarRef" class="chart chart--tall"></div>
      </a-card>

      <!-- 2) Basic-7 rose pie (Happiness, Sadness, Fear, Anger, Surprise, Disgust, Neutral) -->
      <a-card :bordered="false" class="inside-card mt16">
        <a-typography-text type="secondary">Basic Emotions Distribution</a-typography-text>
        <div ref="pieRef" class="chart chart--tall"></div>
      </a-card>
    </a-modal>
  </div>
</template>

<script setup>
// Analyze page with HF call; modal shows 2 stacked charts: bar + rose pie (7 basic buckets)
import { ref, computed, watch, nextTick, onMounted, onBeforeUnmount } from 'vue'
import { message } from 'ant-design-vue'
import * as echarts from 'echarts'

/* ---------------------- Config (HF) ---------------------- */
const HF_ENDPOINT = 'https://api-inference.huggingface.co/models/SamLowe/roberta-base-go_emotions'
// const HF_ENDPOINT = '/api/hf-goemotions' // use this when you add a proxy on deploy

/* ---------------------- State ---------------------- */
const text = ref('')
const loading = ref(false)

const rawResult = ref(null)        // original HF result list
const scoresMap = ref({})          // normalized scores {label: 0..1}
const hasScores = computed(() => Object.keys(scoresMap.value).length > 0)

/* ---------- Tone buckets (for overall tone line) ---------- */
const POSITIVE = [
  'admiration','amusement','approval','caring','curiosity','excitement',
  'gratitude','joy','love','optimism','pride','realization','relief'
]
const NEGATIVE = [
  'anger','annoyance','disappointment','disapproval','disgust',
  'embarrassment','fear','grief','nervousness','remorse','sadness'
]
const NEUTRAL = ['neutral']

/* ---------- Mapping 28 labels -> 7 basic emotions (for rose pie) ----------
 * We normalize across all 7 buckets so that they sum to 100%.
 */
const BASIC7 = {
  Neutral: ['neutral'],                                          // neutral separated & visualized in gray
  Happiness: [
    'joy','excitement','love','admiration','amusement','approval',
    'gratitude','pride','relief','optimism','caring','curiosity'
  ],
  Surprise: ['surprise','realization'],
  Sadness: ['sadness','disappointment','grief','remorse','embarrassment'],
  Fear: ['fear','nervousness'],
  Anger: ['anger','annoyance','disapproval'],
  Disgust: ['disgust'],
}

/* ---------------------- Labels (GoEmotions 27 + neutral) ---------------------- */
const ALL_LABELS = [
  'admiration','amusement','anger','annoyance','approval','caring',
  'confusion','curiosity','desire','disappointment','disapproval','disgust',
  'embarrassment','excitement','fear','gratitude','grief','joy','love',
  'nervousness','optimism','pride','realization','relief','remorse','sadness',
  'surprise','neutral'
]

/* ---------------------- Data shaping ---------------------- */
// Full sorted list for texts & (modal) bar chart
const chartData = computed(() => {
  const arr = ALL_LABELS.map(l => ({ label: l, score: +(scoresMap.value[l] ?? 0) }))
  return arr.sort((a, b) => b.score - a.score)
})

/** Bars to show in modal bar:
 * - If positives > 8: show all positives (no truncation).
 * - If positives <= 8: pad zeros to 8 (fixed order).
 * - If no positive: show first 8 zeros.
 */
const chartTopN = computed(() => {
  const all = ALL_LABELS.map(l => ({ label: l, score: +(scoresMap.value[l] ?? 0) }))
  const positives = all.filter(d => d.score > 0).sort((a, b) => b.score - a.score)
  const zeros = all.filter(d => d.score === 0)
  if (positives.length === 0) return zeros.slice(0, 8)
  if (positives.length <= 8) return [...positives, ...zeros.slice(0, 8 - positives.length)]
  return positives
})

/* ---------------------- Tone summary (normalized) ---------------------- */
function computeToneSummary(map) {
  const sum = (labels) => labels.reduce((s, l) => s + (Number(map[l] || 0)), 0)
  const pos = sum(POSITIVE), neg = sum(NEGATIVE), neu = sum(NEUTRAL)
  const denom = pos + neg + neu || 1e-12
  const posShare = pos / denom, negShare = neg / denom, neuShare = neu / denom
  let tone = 'neutral', top = neuShare
  if (posShare > negShare && posShare > neuShare) { tone = 'positive'; top = posShare }
  else if (negShare > posShare && negShare > neuShare) { tone = 'negative'; top = negShare }
  return {
    tone,
    posPct: +(posShare * 100).toFixed(1),
    negPct: +(negShare * 100).toFixed(1),
    neuPct: +(neuShare * 100).toFixed(1),
    topPct: +(top * 100).toFixed(1)
  }
}
const overallTone = computed(() => {
  if (!hasScores.value) return ''
  const { tone, topPct } = computeToneSummary(scoresMap.value)
  if (tone === 'positive') return `Overall the text conveys a positive tone (${topPct}%).`
  if (tone === 'negative') return `Overall the text leans negative (${topPct}%).`
  return `Overall the text is mostly neutral (${topPct}%).`
})

/* ---------------------- Richer explanation below Summary ---------------------- */
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

/* ---------------------- Top-3 text (raw) ---------------------- */
const top3Text = computed(() => {
  if (!chartData.value.length) return ''
  const top = chartData.value.filter(x => x.score > 0).slice(0, 3)
  if (!top.length) return ''
  return top.map(x => `${x.label} (${(x.score*100).toFixed(1)}%)`).join(', ')
})

/* ---------------------- Analyze (front-end only) ---------------------- */
async function analyze() {
  if (!text.value.trim()) return
  loading.value = true
  try {
    const resp = await fetch(HF_ENDPOINT, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${import.meta.env.VITE_HF_TOKEN}`, // local dev only
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ inputs: text.value }),
    })
    if (!resp.ok) throw new Error('HF API error: ' + resp.status)
    const data = await resp.json()

    // HF may return [[{label,score},...]] or [{label,score},...]
    const list = Array.isArray(data?.[0]) ? data[0] : Array.isArray(data) ? data : []
    rawResult.value = list

    // Normalize into map
    const map = {}
    for (const { label, score } of list) {
      const key = String(label).trim().toLowerCase()
      map[key] = (map[key] ?? 0) + Number(score || 0)
    }
    ALL_LABELS.forEach(l => { if (!(l in map)) map[l] = 0 })
    scoresMap.value = map
  } catch (err) {
    console.error(err)
    message.error('Failed to analyze. Please try again.')
  } finally {
    loading.value = false
  }
}

function reset() {
  text.value = ''
  rawResult.value = null
  scoresMap.value = {}
}

/* ---------------------- Modal charts (bar + rose pie) ---------------------- */
const detailsOpen = ref(false)
const modalTitle = computed(() => 'Emotion details')

const modalBarRef = ref(null)
const pieRef = ref(null)

let modalBarChart = null
let pieChart = null

function openDetails() { detailsOpen.value = true }

/* ---- ECharts helpers ---- */
// Bar gradients (unchanged from your earlier design)
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

/* ---- Build Basic-7 dataset from scoresMap ----
 * Normalize across the 7 buckets so that they sum to 100%.
 */
function buildBasic7Data(map) {
  // keep the displayed order stable & meaningful
  const order = ['Neutral','Happiness','Surprise','Sadness','Fear','Anger','Disgust']
  const raw = {}
  let total = 0
  for (const key of order) {
    const arr = BASIC7[key]
    const sum = arr.reduce((s, lbl) => s + (Number(map[lbl] || 0)), 0)
    raw[key] = sum
    total += sum
  }
  if (total < 1e-12) {
    return order.map(name => ({ name, value: 0 }))
  }
  return order.map(name => ({
    name,
    value: +(raw[name] / total * 100).toFixed(3) // percent share
  }))
}

/* ---- Size guard (ant-modal transitions) ---- */
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

/* ---- Render modal charts ---- */
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
    Surprise: '#f59e0b',
    Sadness:  '#60a5fa',
    Fear:     '#6366f1',
    Anger:    '#ef4444',
    Disgust:  '#a78bfa',
  }
  const color = data7.map(d => COLORS[d.name] || '#9ca3af')

  // threshold: hide label & labelLine if value < 2%
  const HIDE_THRESHOLD = 2

  // build per-slice options so small slices have no label & no guide line
  const seriesData = data7.map(d => {
    const tooSmall = d.value < HIDE_THRESHOLD && d.name !== 'Neutral'
    return {
      name: d.name,
      value: d.value,
      // turn both off when too small
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
        length: 14,
        length2: 10,
        smooth: 0.2,
        lineStyle: { color: '#94a3b8' }
      },
      labelLayout: {
        hideOverlap: true,
        moveOverlap: 'shiftY'
      },
      emphasis: {
        scale: true,
        scaleSize: 8,
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
        textAlign: 'center',
        fontSize: 16,
        fontWeight: 700,
        lineHeight: 20,
        fill: '#0f172a'
      }
    }]
  })
}

/* ---- Modal lifecycle ---- */
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

// Safety: some Ant versions only update v-model
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
onBeforeUnmount(() => {
  modalBarChart?.dispose(); modalBarChart = null
  pieChart?.dispose();      pieChart = null
})
</script>

<style scoped>
.analyze { padding-top: 6px; }
.mt16 { margin-top: 16px; }

.input-card, .result-card { border-radius: var(--ml-radius); }
.actions { display:flex; gap:12px; margin-top: 12px; }

.muted { color: #475569; }

/* charts used in modal */
.chart { width: 100%; height: 280px; }
.chart--tall { height: 320px; width: 100%; }

.mt16 { margin-top: 16px; }

/* Centered CTA with gradient border */
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

/* Overall tone line should be black */
.overall-tone { color: #000; font-weight: 500; }
</style>