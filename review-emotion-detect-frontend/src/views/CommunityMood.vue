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
import { ref, computed, watch, onMounted, nextTick } from 'vue'
import { gsap } from 'gsap'
import * as d3 from 'd3'
import cloud from 'd3-cloud'
import axios from 'axios'
import { sharehttp, dashboardhttp } from '@/api/http'
import { API_BASE } from '@/utils/apiBase'

// 分类接口 URL
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
    const resp = await axios.post(CLASSIFY_URL, { text })
    console.log('resp from classify:', resp)
    if (resp.data && resp.data.sentiment) return resp.data.sentiment
    if (resp.data && resp.data.data && resp.data.data.sentiment) return resp.data.data.sentiment
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
    await new Promise(r => setTimeout(r, 200))
  }
  return results
}

async function computeMoodStats() {
  console.log("computeMoodStats invoked, selectedPeriod =", selectedPeriod.value)
  const posts = await fetchAllReflections()
  console.log("Fetched posts count:", posts.length)
  if (!posts.length) {
    moodStats.value = moodStats.value.map(i => ({ ...i, value: 0 }))
    // 清空词云
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

  // 获取关键词数据
  try {
    const resp = await dashboardhttp.get('/dashboard/sort-emotion', {
      params: { timePeriod: selectedPeriod.value }
    })
    keywordData.value = resp.data.keywordStats ?? {}
  } catch (err) {
    console.error('fetchKeywordStats error', err)
    keywordData.value = {}
  }

  // 渲染词云
  await nextTick()
  renderD3Cloud()

  // 情绪统计
  const sentiments = await classifyBatch(texts, 5)
  let pos = 0, neu = 0, neg = 0
  sentiments.forEach(s => {
    if (s === 'positive') pos++
    else if (s === 'neutral') neu++
    else if (s === 'negative') neg++
  })
  const total = pos + neu + neg || 1
  moodStats.value = [
    { label: '😄 Positive', value: (pos / total) * 100, bg: 'linear-gradient(135deg,#6ee7b7,#3b82f6)' },
    { label: '😐 Neutral', value: (neu / total) * 100, bg: 'linear-gradient(135deg,#e5e7eb,#9ca3af)' },
    { label: '😠 Negative', value: (neg / total) * 100, bg: 'linear-gradient(135deg,#fca5a5,#ef4444)' }
  ]

  gsap.from('.mood-card', {
    opacity: 0,
    y: 20,
    stagger: 0.1,
    duration: 0.5,
    ease: 'power3.out'
  })
}

function renderD3Cloud() {
  const container = wordCloudRef.value
  if (!container) return
  container.innerHTML = ''

  const width = container.clientWidth || 600
  const height = container.clientHeight || 400

  const data = Object.entries(keywordData.value).map(([text, weight]) => {
    return { text, weight }
  })

  const layout = cloud()
    .size([width, height])
    .words(data.map(d => ({
      text: d.text,
      size: d.weight * 5 + 20  // 这个缩放比可以调整
    })))
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

    // 入场动画：一个接一个地显现、缩放
    texts.transition()
      .delay((d, i) => i * 30)
      .duration(800)
      .style('font-size', d => `${d.size}px`)
      .style('opacity', 1)
      .ease(d3.easeCubicOut)
  }
}

// 监听周期变化
watch(selectedPeriod, () => {
  computeMoodStats()
})

onMounted(async () => {
  await nextTick()
  computeMoodStats()
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

</style>