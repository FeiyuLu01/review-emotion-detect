<template>
  <div class="analyze container">
    <a-row :gutter="[16,16]">
      <!-- Left: input -->
      <a-col :xs="24" :md="14">
        <a-card class="card input-card" :bordered="false">
          <a-typography-title :level="3" style="margin:0 0 6px 0;">Analyze a Review</a-typography-title>
          <a-typography-text type="secondary">
            Paste any review or short text below. We will analyze its emotions soon.
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

          <a-alert
            v-if="error"
            class="mt12"
            type="error"
            show-icon
            :message="error"
          />
        </a-card>
      </a-col>

      <!-- Right: result -->
      <a-col :xs="24" :md="10">
        <a-card class="card result-card" :bordered="false">
          <a-skeleton :loading="loading" active :paragraph="{ rows: 8 }">
            <template v-if="parsed.length">
              <a-typography-title :level="4" style="margin-top:0;">Summary</a-typography-title>
              <p class="summary">{{ summary }}</p>

              <EmotionChart :data="parsed" />

              <a-collapse class="mt12">
                <a-collapse-panel key="raw" header="Raw Result (JSON)">
                  <pre class="json">{{ pretty }}</pre>
                </a-collapse-panel>
              </a-collapse>
            </template>

            <template v-else>
              <a-empty description="Results will appear here." />
            </template>
          </a-skeleton>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script setup>
// Analyze page: call Hugging Face, parse scores, draw chart, and show a short report
import { ref, computed, watch } from 'vue'
import EmotionChart from '@/components/analyze/EmotionChart.vue'
import { message } from 'ant-design-vue'

// Use direct HF API for now (works locally). Later, switch to '/api/analyze' proxy on deploy.
const HF_URL = 'https://api-inference.huggingface.co/models/SamLowe/roberta-base-go_emotions'
const HF_TOKEN = import.meta.env.VITE_HF_TOKEN || '' // set in .env.local for local dev

const text = ref('')
const loading = ref(false)
const error = ref('')
const raw = ref(null) // raw json from hf

// Persist textarea to localStorage (quality-of-life)
const LS_KEY = 'moodlens.analyze.text'
text.value = localStorage.getItem(LS_KEY) || ''
watch(text, v => localStorage.setItem(LS_KEY, v ?? ''))

/** Normalize HF response:
 * HF returns: [ [ {label:'joy', score:0.12}, ... ] ]
 * We return one flat array of {label, score} and ensure all labels known to the model appear (missing => 0).
 */
const ALL_LABELS = [
  'admiration','amusement','anger','annoyance','approval','caring','confusion','curiosity',
  'desire','disappointment','disapproval','disgust','embarrassment','excitement','fear','gratitude',
  'grief','joy','love','nervousness','optimism','pride','realization','relief','remorse','sadness',
  'surprise','neutral'
]

const parsed = computed(() => {
  if (!raw.value) return []
  // HF commonly returns an array with one item: an array of {label,score}
  const arr = Array.isArray(raw.value) ? raw.value[0] : null
  if (!Array.isArray(arr)) return []
  const map = new Map(arr.map(d => [String(d.label), Number(d.score)]))
  // Ensure all labels exist (0 if missing)
  return ALL_LABELS.map(l => ({ label: l, score: map.get(l) ?? 0 }))
})

const pretty = computed(() => raw.value ? JSON.stringify(raw.value, null, 2) : '')

/** Build a short textual summary from top emotions */
const summary = computed(() => {
  if (!parsed.value.length) return 'No result yet.'
  const sorted = [...parsed.value].sort((a, b) => b.score - a.score)
  const top = sorted.slice(0, 3).map(d => ({ ...d, pct: +(d.score * 100).toFixed(1) }))

  const t1 = top[0]
  // A friendly sentence with small heuristics
  if (t1.label === 'neutral' && t1.pct >= 70) {
    return `Tone is largely neutral (${t1.pct}%). Little emotional polarity detected.`
  }
  const names = top.map(t => `${t.label} (${t.pct}%)`).join(', ')
  return `Dominant emotions: ${names}.`
})

async function analyze() {
  if (!text.value.trim()) return
  error.value = ''
  loading.value = true
  raw.value = null

  try {
    // ---- Option 1: direct Hugging Face (current local setup) ----
    const resp = await fetch(HF_URL, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...(HF_TOKEN ? { 'Authorization': `Bearer ${HF_TOKEN}` } : {}),
      },
      body: JSON.stringify({ inputs: text.value }),
    })

    // Hugging Face returning 503 => model loading; 429 => rate limit
    if (!resp.ok) {
      const txt = await resp.text()
      throw new Error(`HF error ${resp.status}: ${txt.slice(0, 200)}`)
    }

    const json = await resp.json()
    console.log('[HF raw]', json)
    raw.value = json
  } catch (e) {
    console.error(e)
    error.value = e?.message || 'Unexpected error'
    message.error('Analyze failed.')
  } finally {
    loading.value = false
  }
}

function reset() {
  text.value = ''
  raw.value = null
  error.value = ''
}
</script>

<style scoped>
.analyze { padding-top: 6px; }
.mt16 { margin-top: 16px; }
.mt12 { margin-top: 12px; }
.input-card, .result-card { border-radius: var(--ml-radius); }
.actions { display:flex; gap:12px; margin-top: 12px; }
.summary { margin: 6px 0 10px 0; color: #334155; }
.json {
  margin: 0;
  white-space: pre-wrap;
  word-break: break-word;
  font-family: ui-monospace, SFMono-Regular, Menlo, Consolas, "Liberation Mono", monospace;
  font-size: 12px;
}
</style>