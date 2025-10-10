<template>
  <section class="share-page">
    <!-- ===== Top Toggle Buttons (New) ===== -->
    <div class="view-toggle">
      <a-button-group>
        <a-button
          :type="activeTab === 'reflection' ? 'primary' : 'default'"
          class="toggle-btn"
          @click="switchTab('reflection')"
        >
          🪞 Reflection Wall
        </a-button>
        <a-button
          :type="activeTab === 'mood' ? 'primary' : 'default'"
          class="toggle-btn"
          @click="switchTab('mood')"
        >
          📊 Community Mood
        </a-button>
      </a-button-group>
    </div>

    <!-- ===== Hero Section (Now inside transition for hiding) ===== -->
    <transition name="fade" mode="out-in">
      <div
        v-if="activeTab === 'reflection'"
        key="hero"
        class="share-hero"
        ref="heroRef"
      >
        <h1 class="title">
          <span class="gradient-text">Anonymous Reflection Wall</span>
        </h1>
        <p class="subtitle">
          Express your thoughts freely, color your emotions 🌈
        </p>
      </div>
    </transition>

    <!-- ===== Content Switcher ===== -->
    <transition name="fade" mode="out-in">
      <div v-if="activeTab === 'reflection'" key="reflection">
        <!-- ===== Input Section ===== -->
        <div class="share-container" ref="formRef">
          <a-card class="share-card" :bordered="false">
            <a-textarea
              v-model:value="thought"
              placeholder="Write your thoughts about your online experience..."
              :rows="6"
            />

            <!-- ===== Color Picker Section ===== -->
            <div class="color-picker">
              <span class="label">Choose your emotion color:</span>
              <div class="color-options">
                <div
                  v-for="(color, i) in emotionColors"
                  :key="i"
                  :style="{ background: color.hex }"
                  class="color-dot"
                  :class="{ selected: selectedColor === color.hex }"
                  @click="selectedColor = color.hex"
                ></div>

                <!-- 🎨 Custom Color Button -->
                <div
                  class="color-dot custom-picker"
                  :class="{ selected: isCustomOpen }"
                  @click="toggleColorPicker"
                >
                  🎨
                </div>
              </div>

              <!-- Floating Color Picker Panel -->
              <transition name="fade">
                <div
                  v-if="isCustomOpen"
                  class="color-panel filter-picker-center draggable"
                  ref="customPickerRef"
                >
                  <div class="picker-header">
                    🎨 Pick a color
                    <span class="close-btn" @click="isCustomOpen = false">✖</span>
                  </div>
                  <ColorPicker
                    :color="customColor"
                    @changeColor="onColorChange"
                    theme="light"
                  />
                </div>
              </transition>
            </div>

            <!-- Submit Button -->
            <a-button
              type="primary"
              class="submit-btn"
              size="large"
              :disabled="isDisabled"
              @click="submitThought"
            >
              Share it ✨
            </a-button>
          </a-card>
        </div>

        <!-- ===== Reflection Wall ===== -->
        <div class="wall-section" ref="wallRef">
          <h2 class="wall-title">Reflection Wall 🪞</h2>

          <!-- 🔍 Filter Bar -->
          <div class="filter-bar">
            <a-input-search
              v-model:value="searchQuery"
              placeholder="Search reflections..."
              class="search-box"
              allow-clear
            />
            <a-select
              v-model:value="selectedFilter"
              class="color-filter"
              style="width: 160px"
            >
              <a-select-option value="all">All colors</a-select-option>
              <a-select-option
                v-for="c in emotionColors"
                :key="c.hex"
                :value="c.hex"
              >
                <span :style="{ color: c.hex }">●</span> {{ c.name }}
              </a-select-option>
              <a-select-option value="custom">🎨 Custom color...</a-select-option>
            </a-select>
          </div>

          <!-- Floating custom filter color picker -->
          <transition name="fade">
            <div
              v-if="isFilterColorPickerOpen"
              class="color-panel filter-picker-center draggable"
              ref="filterPickerRef"
            >
              <div class="picker-header">
                🎨 Choose custom filter color
                <span
                  class="close-btn"
                  @click="isFilterColorPickerOpen = false"
                  >✖</span
                >
              </div>
              <ColorPicker
                :color="customFilterColor"
                @changeColor="onFilterColorChange"
                theme="light"
              />
            </div>
          </transition>

          <div v-for="(posts, group) in displayedGroups" :key="group">
            <h3 v-if="posts.length" class="group-title">
              {{ getGroupTitle(group) }}
            </h3>

            <transition-group name="fade" tag="div" class="wall-grid">
              <div
                v-for="(post, index) in posts"
                :key="index"
                class="wall-item"
                :style="{ background: post.color }"
              >
                <p class="post-content">{{ post.text }}</p>
                <p class="post-author">
                  {{ post.anonymous ? 'Anonymous' : 'You' }}
                </p>
                <p class="post-date">🕓 {{ formatDate(post.createdAt) }}</p>
              </div>
            </transition-group>
          </div>

          <p v-if="filteredReflections.length === 0" class="empty-state">
            No reflections found. Try another keyword or color 🎨
          </p>

          <div v-if="!allGroupsVisible" class="load-more">
            <a-button @click="loadMoreGroups" class="load-more-btn">
              Load more reflections
            </a-button>
          </div>
        </div>
      </div>

      <!-- ===== Epic 6 Dashboard ===== -->
      <CommunityMood v-else key="mood" />
    </transition>
  </section>
</template>

<script setup>
import { ref, onMounted, nextTick, watch, computed } from 'vue'
import { gsap } from 'gsap'
import { sharehttp, dashboardhttp } from '@/api/http'
import { message } from 'ant-design-vue'
import { ColorPicker } from 'vue-color-kit'
import 'vue-color-kit/dist/vue-color-kit.css'
import CommunityMood from './CommunityMood.vue'
import { API_BASE } from '@/utils/apiBase'
import axios from 'axios'

const CLASSIFY_URL = `${API_BASE}/gemini-classify`

// ===== Tab Switch Logic =====
const activeTab = ref('reflection') // 'reflection' | 'mood'
function switchTab(tab) {
  if (tab === activeTab.value) return
  gsap.to('.share-hero', { opacity: 0, y: -30, duration: 0.3 })
  setTimeout(() => {
    activeTab.value = tab
    gsap.fromTo(
      '.share-hero',
      { opacity: 0, y: 30 },
      { opacity: 1, y: 0, duration: 0.5, ease: 'power3.out' }
    )
  }, 250)
}


const thought = ref('')
const isAnonymous = ref(true)
const selectedColor = ref('')
const reflections = ref([])  // This now will store both new and fetched posts

// ===== Search and Filter =====

// User search input
const searchQuery = ref('')

// User selected color filter
const selectedFilter = ref('all')

// ===== Custom Color Filter =====

// User-selected custom filter color
const customFilterColor = ref('#aabbcc')

// Watch if user selects "custom" option, auto open picker
watch(selectedFilter, val => {
  if (val === 'custom') isFilterColorPickerOpen.value = true
  else isFilterColorPickerOpen.value = false
})

// Show/hide color picker for custom filtering
const isFilterColorPickerOpen = ref(false)

function onFilterColorChange(color) {
  customFilterColor.value = color.hex
}

// Computed list after applying search + color filter
// ===== Filtered Reflections (with text + color + custom) =====
const filteredReflections = computed(() => {
  return reflections.value.filter(post => {
    // Match text (case-insensitive)
    const matchText = post.text.toLowerCase().includes(searchQuery.value.toLowerCase())

    // Match color: either "all", selected preset color, or custom color range
    let matchColor = true
    if (selectedFilter.value !== 'all') {
      if (selectedFilter.value === 'custom') {
        // When user selects "Custom color", match within similar tone range
        const diff = colorDistance(post.color, customFilterColor.value)
        matchColor = diff < 60 // tolerance (smaller = stricter)
      } else {
        matchColor = post.color === selectedFilter.value
      }
    }

    return matchText && matchColor
  })
})

// Use the filtered reflections when grouping by time
// const groupedReflections = computed(() => groupReflectionsByDate(filteredReflections.value))

const emotionColors = [
  { hex: '#a5b4fc', name: 'Lavender' },
  { hex: '#f9a8d4', name: 'Pink' },
  { hex: '#fde68a', name: 'Yellow' },
  { hex: '#6ee7b7', name: 'Mint' },
  { hex: '#fca5a5', name: 'Coral' },
  { hex: '#93c5fd', name: 'Sky Blue' },
]

const customColor = ref('#aabbcc')
const isCustomOpen = ref(false)

function toggleColorPicker() {
  isCustomOpen.value = !isCustomOpen.value

  nextTick(() => {
    const el = customPickerRef.value
    if (el) {
      // Force it to appear centered at start
      el.style.position = 'fixed'
      el.style.left = '50%'
      el.style.top = '50%'
      el.style.transform = 'translate(-50%, -50%)'
    }
  })
}
function onColorChange(color) {
  selectedColor.value = color.hex
  customColor.value = color.hex
}

const heroRef = ref(null)
const formRef = ref(null)
const wallRef = ref(null)

const isDisabled = computed(() => {
  return !thought.value.trim() || !selectedColor.value
})


const submitThought = async () => {
  if (!thought.value.trim() || !selectedColor.value) {
    message.warning('Please write something and choose a color!')
    return
  }

  try {
    // 🧠 Step 1: Content moderation before posting
    const moderationRes = await fetch('/api/moderate', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ content: thought.value })
    })
    const moderationData = await moderationRes.json()
    console.log('moderationData: ', moderationData);

    if (!moderationData.allowed) {
      message.error('Your post was blocked: ' + moderationData.reason)
      return
    }

    // ✅ Step 2: Add post to backend
    const res = await sharehttp.post('/posts/add-post', {
      content: thought.value,
      bgColor: selectedColor.value,
    })
    console.log('API response:', res)
    const postData = res

    // ✅ Step 3: Emotion classification using Gemini API
    try {
      const classifyResp = await axios.post(CLASSIFY_URL, { text: thought.value })
      console.log('classifyResp: ', classifyResp);
      
      const results = classifyResp.data?.results || classifyResp.data?.data?.results || []
      if (Array.isArray(results) && results.length > 0) {
        // find the label with highest score
        const top = results.reduce((a, b) => (a.score > b.score ? a : b))
        const emotion = top.label
        console.log('Detected top emotion:', emotion)

        // ✅ Step 4: Send emotion to dashboard processor
        await dashboardhttp.post('/dashboard/process-emotion', { emotion })
        console.log('Sent to /dashboard/process-emotion successfully')
      }
    } catch (err) {
      console.error('Emotion classification or process-emotion failed:', err)
    }

    // ✅ Step 5: Update UI (unchanged)
    reflections.value.unshift({
      text: postData.content,
      anonymous: isAnonymous.value,
      color: postData.bgColor,
      createdAt: postData.createdAt,
    })

    await nextTick()
    const newest = wallRef.value.querySelector('.wall-item:first-child')
    gsap.fromTo(
      newest,
      {
        opacity: 0,
        y: 60,
        scale: gsap.utils.random(0.7, 0.9),
        rotate: gsap.utils.random(-8, 8),
      },
      {
        opacity: 1,
        y: 0,
        scale: 1,
        rotate: 0,
        duration: 1.1,
        ease: 'elastic.out(1, 0.65)',
      }
    )

    message.success('Your post has been shared!')
    thought.value = ''
    selectedColor.value = ''
    isAnonymous.value = true

  } catch (err) {
    message.error('Failed to share post')
    console.error('submitThought error:', err)
  }
}

/**
 * Load all reflections from backend
 */
const loadAllReflections = async () => {
  try {
    const res = await sharehttp.get('/posts/get-all')
    console.log('loadAllReflections response:', res)

    if (Array.isArray(res)) {
      reflections.value = res
        .map(post => {
          const safeColor =
            post.bgColor && typeof post.bgColor === 'string'
              ? post.bgColor
              : '#E5E7EB' // fallback light gray

          return {
            text: post.content || '(No content)',
            anonymous: true,
            color: safeColor,
            createdAt: post.createdAt,
          }
        })
        .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
    } else {
      console.warn('Unexpected get-all response format', res)
    }

    // Animate all cards with fromTo to ensure opacity correct
    await nextTick()
    const items = wallRef.value.querySelectorAll('.wall-item')
    items.forEach((el, idx) => {
      gsap.fromTo(
        el,
        {
            opacity: 0,
            x: gsap.utils.random(-60, 60),
            y: gsap.utils.random(-60, 60),
            rotate: gsap.utils.random(-8, 8),
            scale: gsap.utils.random(0.85, 1),
        },
        {
            opacity: 1,
            x: 0,
            y: 0,
            rotate: 0,
            scale: 1,
            delay: 0.03 * idx,
            duration: 0.8,
            ease: 'power3.out',
        }
        )
        // Add subtle floating animation (loop)
        gsap.to(el, {
            y: `+=${gsap.utils.random(4, 7)}`,
            duration: gsap.utils.random(3.5, 5.5),
            yoyo: true,
            repeat: -1,
            delay: (idx % 4) * 0.4 + gsap.utils.random(0, 0.6), // stagger by column + randomness
            ease: 'sine.inOut',
            })
    })
  } catch (err) {
    message.error('Failed to load reflection wall')
    console.error('loadAllReflections error:', err)
  }
}

onMounted(() => {
  // entrance animations
  gsap.from(heroRef.value, {
    opacity: 0,
    y: -40,
    duration: 1.2,
    ease: 'power3.out',
  })
  gsap.from(formRef.value, {
    opacity: 0,
    y: 40,
    duration: 1.2,
    delay: 0.4,
    ease: 'power3.out',
  })

  // load existing posts
  loadAllReflections()
})

function formatDate(dateString) {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
  })
}

function groupReflectionsByDate(reflections) {
  const today = new Date()
  const oneDay = 24 * 60 * 60 * 1000
  const startOfToday = new Date(today.setHours(0, 0, 0, 0))
  const startOfYesterday = new Date(startOfToday - oneDay)
  const startOfWeek = new Date(startOfToday - 7 * oneDay)
  const startOfLastWeek = new Date(startOfToday - 14 * oneDay)

  const groups = {
    today: [],
    yesterday: [],
    thisWeek: [],
    lastWeek: [],
    older: [],
  }

  reflections.forEach(post => {
    const created = new Date(post.createdAt)
    if (created >= startOfToday) groups.today.push(post)
    else if (created >= startOfYesterday) groups.yesterday.push(post)
    else if (created >= startOfWeek) groups.thisWeek.push(post)
    else if (created >= startOfLastWeek) groups.lastWeek.push(post)
    else groups.older.push(post)
  })

  return groups
}

// const groupedReflections = computed(() => groupReflectionsByDate(reflections.value))
const groupedReflections = computed(() => groupReflectionsByDate(filteredReflections.value))

function getGroupTitle(groupKey) {
  switch (groupKey) {
    case 'today':
      return '🗓 Today'
    case 'yesterday':
      return '🕓 Yesterday'
    case 'thisWeek':
      return '📅 Earlier this week'
    case 'lastWeek':
      return '🧭 Last week'
    default:
      return '🗃 Older'
  }
}

// ===== Lazy Load by Time Groups =====

// Define which groups are visible initially
const visibleGroups = ref(['today', 'yesterday'])

// Define the order of all time groups
const allGroupsOrder = ['today', 'yesterday', 'thisWeek', 'lastWeek', 'older']

// Compute which groups should be displayed
const displayedGroups = computed(() => {
  const subset = {}
  visibleGroups.value.forEach(group => {
    subset[group] = groupedReflections.value[group]
  })
  return subset
})

// Check if all groups are already visible
const allGroupsVisible = computed(() => visibleGroups.value.length >= allGroupsOrder.length)

// Function to load more groups when user clicks the button
function loadMoreGroups() {
  // Find next hidden group and make it visible
  const remaining = allGroupsOrder.filter(g => !visibleGroups.value.includes(g))
  const nextGroup = remaining.shift()
  if (nextGroup) visibleGroups.value.push(nextGroup)

  // Animate the new section when it appears
  nextTick(() => {
    const newSection = wallRef.value.querySelector('.group-title:last-of-type')
    if (newSection) {
      gsap.fromTo(
        newSection,
        { x: -40, opacity: 0 },
        { x: 0, opacity: 1, duration: 0.6, ease: 'power3.out' }
      )
    }
  })
}

// ===== Helper: Calculate color distance =====
function colorDistance(c1, c2) {
  // Convert hex to RGB
  const rgb1 = hexToRgb(c1)
  const rgb2 = hexToRgb(c2)
  if (!rgb1 || !rgb2) return 999
  // Euclidean distance
  const diff = Math.sqrt(
    Math.pow(rgb1.r - rgb2.r, 2) +
    Math.pow(rgb1.g - rgb2.g, 2) +
    Math.pow(rgb1.b - rgb2.b, 2)
  )
  return diff
}

function hexToRgb(hex) {
  const clean = hex.replace('#', '')
  const bigint = parseInt(clean, 16)
  if (clean.length !== 6 || isNaN(bigint)) return null
  return {
    r: (bigint >> 16) & 255,
    g: (bigint >> 8) & 255,
    b: bigint & 255
  }
}
</script>

<style scoped>
.share-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  padding-top: 40px;
  background: radial-gradient(circle at 30% 20%, #a78bfa, #6366f1, #1e3a8a);
  color: #fff;
  overflow-x: hidden;
}

/* Hero Section */
.title {
  font-size: 3rem;
  text-align: center;
  margin-bottom: 8px;
  margin-top: 120px;
}

.gradient-text {
  background: linear-gradient(90deg, #ff7ee5, #84fab0);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.subtitle {
  text-align: center;
  font-size: 1.2rem;
  opacity: 0.9;
}

/* Form Section */
.share-container {
  margin: 60px auto 0 auto;
  width: 90%;
  max-width: 640px;
}

.share-card {
  backdrop-filter: blur(12px);
  background-color: rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}

.options,
.color-picker {
  display: flex;
  align-items: center;
  margin-top: 12px;
  gap: 8px;
  flex-wrap: wrap;
}

.label {
  font-size: 1rem;
}

.color-options {
  display: flex;
  gap: 10px;
  margin-top: 8px;
}

.color-dot {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  border: 2px solid transparent;
}

.color-dot:hover {
  transform: scale(1.15);
  box-shadow: 0 0 10px rgba(255, 255, 255, 0.3);
}

.color-dot.selected {
  border: 2px solid white;
  box-shadow: 0 0 12px rgba(255, 255, 255, 0.5);
}

.submit-btn {
  width: 100%;
  margin-top: 20px;
  background: linear-gradient(90deg, #8b5cf6, #ec4899);
  border: none;
  color: white;
  font-weight: bold;
  letter-spacing: 1px;
  transition: transform 0.2s;
}

.submit-btn:hover {
  transform: scale(1.05);
}

/* Reflection Wall */
.wall-section {
  margin-top: 80px;
  width: 100%;
  max-width: 800px;
}

.wall-title {
  text-align: center;
  font-size: 2rem;
  margin-bottom: 24px;
  position: relative;
  z-index: 20;
}

/* .wall-grid {
  column-count: 4; 
  column-gap: 1.8rem; 
  padding-top: 20px; 
  transition: column-gap 0.3s ease;
} */

/* experimental method */
/* .wall-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  grid-template-rows: masonry;
  gap: 1.8rem;
} */
 .wall-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 1.8rem;
  position: relative;
  z-index: 2;
  margin-top: 12px;
  clear: both; /* 👈 prevent overlapping with previous floating elements */
}

@media (max-width: 1024px) {
  .wall-grid {
    column-count: 2;
    column-gap: 1.4rem;
  }
}

@media (max-width: 640px) {
  .wall-grid {
    column-count: 1;
    column-gap: 0;
  }
}

/* Important for masonry layout: make each item inline-block */
.wall-item {
  display: inline-block;
  width: 100%;
  margin-bottom: 1.2rem;
  break-inside: avoid;
  -webkit-column-break-inside: avoid;
  -moz-column-break-inside: avoid;
  padding: 16px;
  border-radius: 12px;
  color: #1f2937;
  backdrop-filter: blur(6px);
  font-size: 1rem;
  word-break: break-word;
  box-shadow: 0 4px 14px rgba(0, 0, 0, 0.15);
  filter: brightness(0.98);
  transition: transform 0.4s ease, box-shadow 0.4s ease, filter 0.4s ease;
  position: relative;
  z-index: 1; 
  transform-origin: center center;
  will-change: transform;
}

.wall-item:nth-child(odd) {
  transform: translateY(5px);
}

.wall-item:nth-child(even) {
  transform: translateY(-5px);
}


.wall-item:hover {
    transform: translateY(-6px) rotateZ(1.5deg) scale(1.04);
    box-shadow: 0 10px 25px rgba(255, 255, 255, 0.35),
                0 0 20px rgba(255, 255, 255, 0.2);
    transition: transform 0.3s ease, box-shadow 0.3s ease;
    z-index: 10;
    filter: brightness(1.05);
}

.post-author {
  text-align: right;
  font-size: 0.9rem;
  margin-top: 8px;
  opacity: 0.7;
}

.post-date {
  text-align: right;
  font-size: 0.8rem;
  opacity: 0.6;
  margin-top: 4px;
  font-style: italic;
}

/* Transition Animation */
.fade-enter-active,
.fade-leave-active {
  transition: all 0.6s ease;
}
.fade-enter-from {
  opacity: 0;
  transform: translateY(20px);
}
.fade-leave-to {
  opacity: 0;
  transform: translateY(-20px);
}

.wall-item {
  position: relative;
  border: 1px solid rgba(255, 255, 255, 0.25);
  min-height: 100px;
}

.custom-picker {
  background: linear-gradient(45deg, #8b5cf6, #ec4899);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  color: white;
  border-radius: 50%;
  width: 28px;
  height: 28px;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}
.custom-picker:hover {
  transform: scale(1.1);
  box-shadow: 0 0 10px rgba(255, 255, 255, 0.4);
}
.color-panel {
  position: absolute;
  margin-top: 12px;
  z-index: 100;
}
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.25s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.group-title {
  font-size: 1.4rem;
  font-weight: 600;
  margin: 60px 0 20px;
  color: #fdfdfd;
  text-shadow: 0 2px 6px rgba(0, 0, 0, 0.3);
  position: relative;
  z-index: 10;
  background: rgba(30, 41, 59, 0.7); 
  backdrop-filter: blur(4px);
  padding: 6px 10px;
  border-radius: 6px;
}


/* Each group section should form an isolated stacking context */
.wall-section > div {
  position: relative;
  z-index: 1;
  margin-bottom: 60px;
}

.load-more {
  display: flex;
  justify-content: center;
  margin-top: 24px;
  margin-bottom: 24px;
}

.load-more-btn {
  background: linear-gradient(90deg, #8b5cf6, #ec4899);
  border: none;
  color: white;
  font-weight: 600;
  border-radius: 8px;
  padding: 8px 20px;
  transition: all 0.3s ease;
}

.load-more-btn:hover {
  transform: scale(1.05);
  box-shadow: 0 0 12px rgba(255, 255, 255, 0.4);
}

.filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
}

.search-box {
  flex: 1;
}

.color-filter {
  flex-shrink: 0;
}

/* Centered floating color picker for filter */
.filter-picker-center {
  position: fixed; /* make it float over entire page */
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: white;
  padding: 16px;
  border-radius: 12px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.25);
  z-index: 500;
}

.empty-state {
  text-align: center;
  margin-top: 40px;
  font-size: 1.1rem;
  opacity: 0.85;
  color: #f1f5f9;
  font-style: italic;
  animation: fadeIn 0.6s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

/* ===== Color Picker Draggable Header ===== */
.picker-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(90deg, #8b5cf6, #ec4899);
  color: white;
  padding: 6px 10px;
  border-radius: 8px 8px 0 0;
  cursor: move;
  font-size: 0.9rem;
  user-select: none;
}

.close-btn {
  cursor: pointer;
  font-weight: bold;
  margin-left: 8px;
  transition: transform 0.2s ease;
}

.close-btn:hover {
  transform: scale(1.2);
}

/* ===== Top Toggle Buttons ===== */
.view-toggle {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 12px;
  margin-top: 20px;
  margin-bottom: 12px;
}

.toggle-btn {
  font-weight: 600;
  border-radius: 20px;
  transition: all 0.3s ease;
}

.toggle-btn:hover {
  transform: scale(1.05);
  box-shadow: 0 0 10px rgba(255, 255, 255, 0.3);
}
</style>