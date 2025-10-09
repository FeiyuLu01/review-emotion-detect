<template>
  <div class="mood-dashboard" ref="dashboardRef">
    <div class="mood-header">
      <h2 class="dashboard-title">
        {{ formattedTitle }} 🌈
      </h2>
      <a-segmented
        v-model:value="selectedPeriod"
        :options="periodOptions"
        class="period-switch"
      />
    </div>

    <!-- ===== Mood Statistic Cards ===== -->
    <div class="mood-stats">
      <a-card
        v-for="(item, index) in moodStats"
        :key="index"
        class="mood-card"
        :style="{ background: item.bg }"
      >
        <h3>{{ item.label }}</h3>
        <p class="percent">{{ item.value.toFixed(1) }}%</p>
      </a-card>
    </div>

    <p class="summary-text">
      Based on recent reflections, here's how the community is feeling.
    </p>

    <!-- ===== Word Cloud Visualization ===== -->
    <div ref="wordCloudRef" class="word-cloud"></div>
  </div>
</template>

<script setup>
// Imports
import { ref, computed, onMounted, watch, nextTick } from 'vue'
import { gsap } from 'gsap'
import * as echarts from 'echarts'
import 'echarts-wordcloud'
import axios from 'axios'
import { sharehttp, dashboardhttp } from '@/api/http'
import { API_BASE } from '@/utils/apiBase'

// 分类接口 URL
const CLASSIFY_URL = `${API_BASE}/gemini-classify`

// ----- State & Options -----
const selectedPeriod = ref('weekly')
const periodOptions = ['weekly', 'monthly', 'yearly']

const moodStats = ref([
  { label: '😄 Positive', value: 0, bg: 'linear-gradient(135deg,#6ee7b7,#3b82f6)' },
  { label: '😐 Neutral', value: 0, bg: 'linear-gradient(135deg,#e5e7eb,#9ca3af)' },
  { label: '😠 Negative', value: 0, bg: 'linear-gradient(135deg,#fca5a5,#ef4444)' }
])

const keywordData = ref({})

const wordCloudRef = ref(null)
let chartInstance = null

const formattedTitle = computed(() => {
  switch (selectedPeriod.value) {
    case 'weekly':
      return "This Week’s Community Mood"
    case 'monthly':
      return "This Month’s Community Mood"
    case 'yearly':
      return "This Year’s Community Mood"
    default:
      return "Community Mood"
  }
})

// ----- Functions: classify & aggregate -----

async function fetchAllReflections() {
  try {
    const posts = await sharehttp.get('/posts/get-all')
    return Array.isArray(posts) ? posts : []
  } catch (err) {
    console.error('fetchAllReflections error', err)
    return []
  }
}

async function classifyText(text) {
  try {
    console.log('start classify', text)
    // 使用 axios.post 到 CLASSIFY_URL
    const resp = await axios.post(CLASSIFY_URL, { text })
    console.log('resp from classify:', resp)
    // 根据你后台返回结构提取 sentiment 字段
    // 可能是 resp.data.sentiment 或 resp.data.data.sentiment 等
    if (resp.data && resp.data.sentiment) {
      return resp.data.sentiment
    }
    // fallback: maybe resp.data.data.sentiment
    if (resp.data && resp.data.data && resp.data.data.sentiment) {
      return resp.data.data.sentiment
    }
    return null
  } catch (err) {
    console.error('classifyText error', err)
    return null
  }
}

async function classifyBatch(texts, batchSize = 5) {
  const results = []
  for (let i = 0; i < texts.length; i += batchSize) {
    const slice = texts.slice(i, i + batchSize)
    const promises = slice.map(t => classifyText(t).catch(() => null))
    const res = await Promise.all(promises)
    results.push(...res)
    // 延迟避免压力
    await new Promise(r => setTimeout(r, 200))
  }
  return results
}

async function computeMoodStats() {
  console.log("computeMoodStats invoked, selectedPeriod =", selectedPeriod.value)
  const posts = await fetchAllReflections()
  console.log("Example post:", posts[0])
  console.log("Fetched posts count:", posts.length)
  if (!posts.length) {
    moodStats.value = moodStats.value.map(item => ({ ...item, value: 0 }))
    return
  }

  const now = Date.now()
  const filtered = posts.filter(p => {
    if (!p.createdAt) return false
    const ts = new Date(p.createdAt).getTime()
    if (selectedPeriod.value === 'weekly') {
      return ts >= now - 7 * 24 * 3600 * 1000
    } else if (selectedPeriod.value === 'monthly') {
      return ts >= now - 30 * 24 * 3600 * 1000
    } else if (selectedPeriod.value === 'yearly') {
      return ts >= now - 365 * 24 * 3600 * 1000
    }
    return true
  })
  console.log("Filtered posts count (by period):", filtered.length)

  const texts = filtered
    .map(p => {
      if (typeof p.content === 'string') return p.content
      if (typeof p.text === 'string') return p.text
      return ''
    })
    .filter(t => typeof t === 'string' && t.trim().length > 0)
  console.log("Texts array for classification:", texts.slice(0, 5), "... total", texts.length)
  if (texts.length === 0) {
    moodStats.value = moodStats.value.map(item => ({ ...item, value: 0 }))
    return
  }

  const sentiments = await classifyBatch(texts, 5)
  console.log("Classification results:", sentiments.slice(0, 10), " total", sentiments.length)

  let pos = 0, neu = 0, neg = 0
  sentiments.forEach(s => {
    if (s === 'positive') pos++
    else if (s === 'neutral') neu++
    else if (s === 'negative') neg++
  })
  console.log("pos, neu, neg counts:", pos, neu, neg)

  const total = pos + neu + neg || 1
  const posPct = (pos / total) * 100
  const neuPct = (neu / total) * 100
  const negPct = (neg / total) * 100

  moodStats.value = [
    { label: '😄 Positive', value: posPct, bg: 'linear-gradient(135deg,#6ee7b7,#3b82f6)' },
    { label: '😐 Neutral', value: neuPct, bg: 'linear-gradient(135deg,#e5e7eb,#9ca3af)' },
    { label: '😠 Negative', value: negPct, bg: 'linear-gradient(135deg,#fca5a5,#ef4444)' }
  ]

  gsap.from('.mood-card', {
    opacity: 0,
    y: 20,
    stagger: 0.1,
    duration: 0.5,
    ease: 'power3.out'
  })
}

function renderWordCloud() {
  const container = wordCloudRef.value
  if (!container) return
  if (!chartInstance) {
    chartInstance = echarts.init(container)
    window.addEventListener('resize', () => chartInstance.resize())
  }
  const dataArr = Object.entries(keywordData.value).map(([name, val]) => ({
    name,
    value: val
  }))
  const option = {
    tooltip: {},
    series: [
      {
        type: 'wordCloud',
        gridSize: 8,
        sizeRange: [16, 60],
        rotationRange: [-45, 45],
        shape: 'circle',
        textStyle: {
          color: () => {
            const cols = ['#f472b6', '#60a5fa', '#34d399', '#fbbf24', '#a78bfa']
            return cols[Math.floor(Math.random() * cols.length)]
          }
        },
        data: dataArr
      }
    ]
  }
  chartInstance.setOption(option)
}

async function fetchKeywordStats() {
  try {
    const res = await dashboardhttp.get('/dashboard/sort-emotion', {
      params: { timePeriod: selectedPeriod.value }
    })
    const stats = (res.data && res.data.keywordStats) || {}
    keywordData.value = stats
    renderWordCloud()
  } catch (err) {
    console.error('fetchKeywordStats error', err)
  }
}

// Watch & init
watch(selectedPeriod, () => {
  computeMoodStats()
  fetchKeywordStats()
})

onMounted(async () => {
  await nextTick()
  console.log("onMounted: starting computeMoodStats & fetchKeywordStats")
  computeMoodStats()
  fetchKeywordStats()
})
</script>

<style scoped>
/* 保持你原有样式 */
.mood-dashboard {
  max-width: 800px;
  margin: 60px auto;
  text-align: center;
  color: #fff;
}
.mood-header {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 20px;
  margin-bottom: 20px;
}
.dashboard-title {
  font-size: 2.2rem;
  font-weight: 700;
  margin: 0;
  background: linear-gradient(90deg, #ff7ee5, #84fab0);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}
.period-switch {
  font-size: 0.9rem;
}
.mood-stats {
  display: flex;
  justify-content: center;
  gap: 20px;
  flex-wrap: wrap;
  margin-bottom: 20px;
}
.mood-card {
  width: 180px;
  border: none;
  border-radius: 14px;
  color: #fff;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
  transition: transform 0.3s ease;
}
.mood-card:hover {
  transform: translateY(-6px) scale(1.05);
}
.percent {
  font-size: 2rem;
  font-weight: 700;
  margin-top: 8px;
}
.summary-text {
  font-size: 1.1rem;
  opacity: 0.9;
  margin-bottom: 40px;
}
.word-cloud {
  height: 400px;
  width: 100%;
  background: rgba(255, 255, 255, 0.08);
  border-radius: 16px;
  backdrop-filter: blur(8px);
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.2);
}
</style>