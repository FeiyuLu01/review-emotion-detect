<template>
  <div class="mood-dashboard" ref="dashboardRef">
    <div class="mood-header">
      <h2 class="dashboard-title">
        {{ formattedTitle }} 
        <span class="emoji">🌈</span>
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

    <p ref="summaryRef" class="summary-text">
      {{ summaryMessage }}
    </p>

    <!-- ===== Word Cloud Visualization ===== -->
    <div ref="wordCloudRef" class="word-cloud"></div>

    <!-- ===== Line Chart Selector & Preview ===== -->
    <div class="line-chart-section">
        <div class="chart-toggle">
            <a-segmented
            v-model:value="selectedChart"
            :options="['App Data', 'Twitter Data']"
            class="chart-switch"
            />
        </div>

        <!-- Chart container (currently just a placeholder) -->
        <div ref="lineChartRef" class="line-chart-box">
            <p>📈 Line chart data preview logged in console.</p>
        </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, nextTick } from 'vue'
import { gsap } from 'gsap'
import * as d3 from 'd3'
import cloud from 'd3-cloud'
import axios from 'axios'
import { sharehttp, dashboardhttp } from '@/api/http'
import { API_BASE } from '@/utils/apiBase'
import * as echarts from 'echarts'

const CLASSIFY_URL = `${API_BASE}/gemini-classify`

const selectedPeriod = ref('weekly')
const periodOptions = ['weekly', 'monthly', 'yearly']

const moodStats = ref([
  { label: '😄 Positive', value: 0, bg: 'linear-gradient(135deg,#6ee7b7,#3b82f6)' },
  { label: '😐 Neutral', value: 0, bg: 'linear-gradient(135deg,#e5e7eb,#9ca3af)' },
  { label: '😠 Negative', value: 0, bg: 'linear-gradient(135deg,#fca5a5,#ef4444)' }
])

const keywordData = ref({})
const wordCloudRef = ref(null)

const formattedTitle = computed(() => {
  switch (selectedPeriod.value) {
    case 'weekly': return "This Week’s Community Mood"
    case 'monthly': return "This Month’s Community Mood"
    case 'yearly': return "This Year’s Community Mood"
    default: return "Community Mood"
  }
})

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

/* ---------- Fetch all reflections ---------- */
async function fetchAllReflections() {
  try {
    const posts = await sharehttp.get('/posts/get-all')
    return Array.isArray(posts) ? posts : []
  } catch (err) {
    console.error('fetchAllReflections error', err)
    return []
  }
}

/* ---------- Classify one text ---------- */
async function classifyText(text) {
  try {
    const resp = await axios.post(CLASSIFY_URL, { text })
    const results = resp.data?.results || resp.data?.data?.results
    if (!Array.isArray(results) || results.length === 0) return 'neutral'

    // find the label with the highest score
    const top = results.reduce((a, b) => (a.score > b.score ? a : b))
    const label = top.label?.toLowerCase() || 'neutral'

    // map to tone bucket
    if (POSITIVE.includes(label)) return 'positive'
    if (NEGATIVE.includes(label)) return 'negative'
    return 'neutral'
  } catch (err) {
    console.error('classifyText error', err)
    return 'neutral'
  }
}

/* ---------- Batch classify with concurrency ---------- */
async function classifyBatch(texts, batchSize = 5) {
  const results = []
  for (let i = 0; i < texts.length; i += batchSize) {
    const slice = texts.slice(i, i + batchSize)
    const promises = slice.map(t => classifyText(t).catch(() => 'neutral'))
    const res = await Promise.all(promises)
    results.push(...res)
    await new Promise(r => setTimeout(r, 200))
  }
  return results
}

/* ---------- Compute mood stats ---------- */
async function computeMoodStats() {
  console.log("computeMoodStats invoked, selectedPeriod =", selectedPeriod.value)
  const posts = await fetchAllReflections()
  console.log("Fetched posts count:", posts.length)
  if (!posts.length) {
    moodStats.value = moodStats.value.map(i => ({ ...i, value: 0 }))
    nextTick(() => {
      if (wordCloudRef.value) wordCloudRef.value.innerHTML = ''
    })
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
    } else {
      return ts >= now - 365 * 24 * 3600 * 1000
    }
  })

  const texts = filtered
    .map(p => (typeof p.content === 'string' ? p.content : ''))
    .filter(t => t.trim().length > 0)

  // Fetch keyword stats (for word cloud)
  try {
    const resp = await dashboardhttp.get('/dashboard/sort-emotion', {
      params: { timePeriod: selectedPeriod.value }
    })
    keywordData.value = resp.data.keywordStats ?? {}
  } catch (err) {
    console.error('fetchKeywordStats error', err)
    keywordData.value = {}
  }

  // Render word cloud
  await nextTick()
  renderD3Cloud()

  // Classify and aggregate tones
  const sentiments = await classifyBatch(texts, 5)
  const total = sentiments.length || 1
  const pos = sentiments.filter(s => s === 'positive').length
  const neu = sentiments.filter(s => s === 'neutral').length
  const neg = sentiments.filter(s => s === 'negative').length

  moodStats.value = [
    { label: '😄 Positive', value: (pos / total) * 100, bg: 'linear-gradient(135deg,#6ee7b7,#3b82f6)' },
    { label: '😐 Neutral', value: (neu / total) * 100, bg: 'linear-gradient(135deg,#e5e7eb,#9ca3af)' },
    { label: '😠 Negative', value: (neg / total) * 100, bg: 'linear-gradient(135deg,#fca5a5,#ef4444)' }
  ]

  updateSummaryMessage((pos / total) * 100, (neu / total) * 100, (neg / total) * 100)

  gsap.from('.mood-card', {
    opacity: 0,
    y: 20,
    stagger: 0.1,
    duration: 0.5,
    ease: 'power3.out'
  })
}

// summary message state
const summaryMessage = ref("Based on recent reflections, here's how the community is feeling.")
const summaryRef = ref(null)

/* ---------- Update summary message with GSAP animation ---------- */
function updateSummaryMessage(posPct, neuPct, negPct) {
  // Determine dominant tone
  const dominant = Math.max(posPct, neuPct, negPct)
  let newMessage = ''
  let color = '#fff'

  if (dominant === posPct) {
    newMessage = "🌞 The community is shining with positivity and kindness!"
    color = '#6ee7b7'
  } else if (dominant === neuPct) {
    newMessage = "🌤 A calm and balanced atmosphere surrounds the community."
    color = '#e5e7eb'
  } else {
    newMessage = "🌧 Emotions are a bit tense lately. Let’s uplift the mood together."
    color = '#fca5a5'
  }

  summaryMessage.value = newMessage

  // Animate the text glow and entrance
  nextTick(() => {
    if (!summaryRef.value) return
    gsap.fromTo(
      summaryRef.value,
      { opacity: 0, y: 20, scale: 0.95, color },
      {
        opacity: 1,
        y: 0,
        scale: 1,
        duration: 1,
        ease: 'power3.out',
        color,
        boxShadow: `0 0 20px ${color}`,
        onComplete: () => {
          // subtle pulsing glow
          gsap.to(summaryRef.value, {
            boxShadow: `0 0 40px ${color}`,
            repeat: -1,
            yoyo: true,
            duration: 2,
            ease: 'sine.inOut'
          })
        }
      }
    )
  })
}

/* ---------- Word Cloud ---------- */
function renderD3Cloud() {
  const container = wordCloudRef.value
  if (!container) return
  container.innerHTML = ''

  const width = container.clientWidth || 600
  const height = container.clientHeight || 400

  const data = Object.entries(keywordData.value).map(([text, weight]) => ({ text, weight }))

  const layout = cloud()
    .size([width, height])
    .words(data.map(d => ({ text: d.text, size: d.weight * 5 + 20 })))
    .padding(5)
    .rotate(() => (Math.random() > 0.5 ? 0 : 90))
    .font('Impact')
    .fontSize(d => d.size)
    .spiral('archimedean')
    .on('end', draw)
  layout.start()

  function draw(words) {
    const svg = d3.select(container)
      .append('svg')
      .attr('width', width)
      .attr('height', height)
    const g = svg.append('g')
      .attr('transform', `translate(${width / 2},${height / 2})`)
    const texts = g.selectAll('text')
      .data(words)
      .enter().append('text')
      .style('font-family', 'Impact')
      .style('fill', () => {
        const colors = ['#f472b6', '#60a5fa', '#34d399', '#fbbf24', '#a78bfa']
        return colors[Math.floor(Math.random() * colors.length)]
      })
      .attr('text-anchor', 'middle')
      .attr('transform', d => `translate(${d.x},${d.y})rotate(${d.rotate})`)
      .style('font-size', '0px')
      .style('opacity', 0)
      .text(d => d.text)
    texts.transition()
      .delay((d, i) => i * 30)
      .duration(800)
      .style('font-size', d => `${d.size}px`)
      .style('opacity', 1)
      .ease(d3.easeCubicOut)
  }
}

watch(selectedPeriod, () => {
  computeMoodStats()
})

onMounted(async () => {
  await nextTick()
  computeMoodStats()
})

/* ---------- Line Chart Data Logic ---------- */
const selectedChart = ref('App Data')  // current selection
const lineChartRef = ref(null)

/**
 * Fetch chart data depending on the selected option
 */
async function fetchChartData() {
  try {
    const endpoint =
      selectedChart.value === 'App Data'
        ? '/dashboard/line-chart'
        : '/dashboard/twitter-line-chart'

    const resp = await dashboardhttp.get(endpoint)
    console.log(`✅ ${selectedChart.value} data from ${endpoint}:`, resp)

    if (selectedChart.value === 'App Data') {
      renderLineChart(resp.data)
    } else {
      renderTwitterChart(resp.data)
    }
  } catch (err) {
    console.error(`❌ Failed to fetch ${selectedChart.value} data:`, err)
  }
}

/**
 * Render App Data line chart with animation and gradient
 */
function renderLineChart(data) {
  if (!lineChartRef.value) return

  const chart = echarts.init(lineChartRef.value)
  const option = {
    backgroundColor: 'transparent',
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(50,50,50,0.8)',
      borderColor: '#777',
      textStyle: { color: '#fff' },
    },
    legend: {
      data: ['Positive', 'Neutral', 'Negative'],
      textStyle: { color: '#fff', fontSize: 13 },
      top: 10,
    },
    grid: {
      left: '5%',
      right: '5%',
      bottom: '10%',
      top: '15%',
      containLabel: true,
    },
    xAxis: {
      type: 'category',
      data: data.date || [],
      boundaryGap: false,
      axisLine: { lineStyle: { color: '#aaa' } },
      axisLabel: { color: '#ddd' },
    },
    yAxis: {
      type: 'value',
      name: 'Count',
      nameTextStyle: { color: '#ddd' },
      axisLine: { lineStyle: { color: '#aaa' } },
      splitLine: { lineStyle: { color: 'rgba(255,255,255,0.15)' } },
      axisLabel: { color: '#ccc' },
    },
    series: [
      {
        name: 'Positive',
        type: 'line',
        smooth: true,
        data: data.positive || [],
        lineStyle: { color: '#4ade80', width: 3 },
        itemStyle: { color: '#4ade80' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(74,222,128,0.4)' },
            { offset: 1, color: 'rgba(74,222,128,0)' },
          ]),
        },
      },
      {
        name: 'Neutral',
        type: 'line',
        smooth: true,
        data: data.neutral || [],
        lineStyle: { color: '#9ca3af', width: 3 },
        itemStyle: { color: '#9ca3af' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(156,163,175,0.4)' },
            { offset: 1, color: 'rgba(156,163,175,0)' },
          ]),
        },
      },
      {
        name: 'Negative',
        type: 'line',
        smooth: true,
        data: data.negative || [],
        lineStyle: { color: '#f87171', width: 3 },
        itemStyle: { color: '#f87171' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(248,113,113,0.4)' },
            { offset: 1, color: 'rgba(248,113,113,0)' },
          ]),
        },
      },
    ],
    animationDuration: 1200,
    animationEasing: 'cubicOut',
  }

  chart.setOption(option)

  // handle resize
  window.addEventListener('resize', () => {
    chart.resize()
  })
}

/**
 * Render Twitter Data line chart with animation and gradient
 */
function renderTwitterChart(data) {
  if (!lineChartRef.value) return
  const chart = echarts.init(lineChartRef.value)

  const option = {
    backgroundColor: 'transparent',
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(30,30,30,0.85)',
      borderColor: '#444',
      textStyle: { color: '#fff' },
    },
    legend: {
      data: ['Positive', 'Neutral', 'Negative'],
      textStyle: { color: '#fff', fontSize: 13 },
      top: 10,
    },
    grid: {
      left: '5%',
      right: '5%',
      bottom: '10%',
      top: '15%',
      containLabel: true,
    },
    xAxis: {
      type: 'category',
      data: data.date || [],
      boundaryGap: false,
      axisLine: { lineStyle: { color: '#aaa' } },
      axisLabel: { color: '#ddd' },
    },
    yAxis: {
      type: 'value',
      name: 'Count',
      nameTextStyle: { color: '#ddd' },
      axisLine: { lineStyle: { color: '#aaa' } },
      splitLine: { lineStyle: { color: 'rgba(255,255,255,0.15)' } },
      axisLabel: { color: '#ccc' },
    },
    series: [
      {
        name: 'Positive',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        data: data.positive || [],
        lineStyle: { color: '#22d3ee', width: 3 },
        itemStyle: { color: '#22d3ee' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(34,211,238,0.4)' },
            { offset: 1, color: 'rgba(34,211,238,0)' },
          ]),
        },
      },
      {
        name: 'Neutral',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        data: data.neutral || [],
        lineStyle: { color: '#cbd5e1', width: 3 },
        itemStyle: { color: '#cbd5e1' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(203,213,225,0.4)' },
            { offset: 1, color: 'rgba(203,213,225,0)' },
          ]),
        },
      },
      {
        name: 'Negative',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        data: data.negative || [],
        lineStyle: { color: '#f43f5e', width: 3 },
        itemStyle: { color: '#f43f5e' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(244,63,94,0.4)' },
            { offset: 1, color: 'rgba(244,63,94,0)' },
          ]),
        },
      },
    ],
    animationDuration: 1200,
    animationEasing: 'cubicOut',
  }

  chart.setOption(option)
  window.addEventListener('resize', () => chart.resize())
}

// watch selection changes
watch(selectedChart, () => {
  fetchChartData()
})

// initial fetch
onMounted(() => {
  fetchChartData()
})
</script>

<style scoped>
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

.dashboard-title .emoji {
  -webkit-text-fill-color: initial; /* restore emoji native color */
  background: none; /* remove gradient */
  display: inline-block;
  filter: drop-shadow(0 0 6px rgba(255, 255, 255, 0.5)); /* optional glow */
  animation: floatRainbow 3s ease-in-out infinite alternate;
}

@keyframes floatRainbow {
  0% { transform: translateY(0); }
  100% { transform: translateY(-5px); }
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
position: relative;
overflow: hidden;
}

.summary-text {
  font-size: 1.2rem;
  font-weight: 500;
  letter-spacing: 0.3px;
  text-shadow: 0 0 10px rgba(255,255,255,0.3);
  transition: color 0.6s ease;
  margin-top: 40px;
}

.line-chart-section {
  margin-top: 40px;
  text-align: center;
}

.chart-toggle {
  margin-bottom: 16px;
}

.line-chart-box {
  height: 360px;
  width: 100%;
  background: rgba(255, 255, 255, 0.08);
  border-radius: 16px;
  backdrop-filter: blur(8px);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ddd;
  font-size: 1rem;
}
</style>