<template>
  <section class="share-page">
    <!-- ===== Hero Section ===== -->
    <div class="share-hero" ref="heroRef">
      <h1 class="title">
        <span class="gradient-text">Anonymous Reflection Wall</span>
      </h1>
      <p class="subtitle">Express your thoughts freely, color your emotions 🌈</p>
    </div>

    <!-- ===== Input Section ===== -->
    <div class="share-container" ref="formRef">
      <a-card class="share-card" :bordered="false">
        <a-textarea
          v-model:value="thought"
          placeholder="Write your thoughts about your online experience..."
          :rows="6"
        />

        <!-- Anonymous Switch -->
        <div class="options">
          <a-switch v-model:checked="isAnonymous" />
          <span class="label">Post anonymously</span>
        </div>

        <!-- ===== Color Picker Section ===== -->
        <div class="color-picker">
            <span class="label">Choose your emotion color:</span>
            <div class="color-options">
                <div
                v-for="(color, i) in emotionColors"
                :key="i"
                :style="{ background: color }"
                class="color-dot"
                :class="{ selected: selectedColor === color }"
                @click="selectedColor = color"
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
                <div v-if="isCustomOpen" class="color-panel">
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
                <p class="post-date">
                🕓 {{ formatDate(post.createdAt) }}
                </p>
            </div>
            </transition-group>
        </div>
        <!-- 🔽 Load More Button -->
        <div v-if="!allGroupsVisible" class="load-more">
            <a-button @click="loadMoreGroups" class="load-more-btn">
                Load more reflections
            </a-button>
        </div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted, nextTick, watch, computed } from 'vue'
import { gsap } from 'gsap'
import { sharehttp } from '@/api/http'
import { message } from 'ant-design-vue'
import { ColorPicker } from 'vue-color-kit'
import 'vue-color-kit/dist/vue-color-kit.css'

const thought = ref('')
const isAnonymous = ref(true)
const selectedColor = ref('')
const reflections = ref([])  // This now will store both new and fetched posts

const emotionColors = [
  '#a5b4fc', '#f9a8d4', '#fde68a', '#6ee7b7', '#fca5a5', '#93c5fd'
]

const customColor = ref('#aabbcc')
const isCustomOpen = ref(false)

function toggleColorPicker() {
  isCustomOpen.value = !isCustomOpen.value
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

watch(thought, (val) => {
  console.log('thought changed:', val)
})
watch(selectedColor, (val) => {
  console.log('selectedColor changed:', val)
})
watch(isDisabled, (val) => {
  console.log('isDisabled changed:', val)
})

const submitThought = async () => {
  if (!thought.value.trim() || !selectedColor.value) {
    message.warning('Please write something and choose a color!')
    return
  }
  try {
    const res = await sharehttp.post('/posts/add-post', {
      content: thought.value,
      bgColor: selectedColor.value,
    })
    console.log('API response:', res)
    const postData = res  // because response is the post object

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

const groupedReflections = computed(() => groupReflectionsByDate(reflections.value))

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
</script>

<style scoped>
.share-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  padding-top: 120px;
  background: radial-gradient(circle at 30% 20%, #a78bfa, #6366f1, #1e3a8a);
  color: #fff;
  overflow-x: hidden;
}

/* Hero Section */
.title {
  font-size: 3rem;
  text-align: center;
  margin-bottom: 8px;
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
  margin-top: 60px;
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

.wall-grid {
  column-count: 4; /* 4 columns masonry layout */
  column-gap: 1.8rem; /* slightly larger */
  padding-top: 20px; /* leave breathing space */
  transition: column-gap 0.3s ease;
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
  margin: 40px 0 16px;
  color: #fdfdfd;
  text-shadow: 0 2px 6px rgba(0, 0, 0, 0.3);
  position: relative;
  z-index: 10;
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
</style>