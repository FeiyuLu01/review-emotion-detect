<template>
    <div class="analyze container">
      <a-row :gutter="[16,16]">
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
              <a-button type="primary" size="large" :disabled="!text.trim()" @click="comingSoon">
                Analyze
              </a-button>
              <a-button size="large" @click="reset">Clear</a-button>
            </div>
          </a-card>
        </a-col>
  
        <a-col :xs="24" :md="10">
          <a-card class="card result-card" :bordered="false">
            <a-empty description="Results will appear here." />
          </a-card>
        </a-col>
      </a-row>
    </div>
  </template>
  
  <script setup>
  // Skeleton only: future logic will post to backend / model and render charts/cards
  import { ref } from 'vue'
  import { message } from 'ant-design-vue'
  
  const text = ref('')
  
  function comingSoon() {
    message.info('Analysis logic is not implemented yet. We will wire it next.')
  }
  function reset() { text.value = '' }
  </script>
  
  <style scoped>
  .analyze { padding-top: 6px; }
  .mt16 { margin-top: 16px; }
  .input-card, .result-card { border-radius: var(--ml-radius); }
  .actions { display:flex; gap:12px; margin-top: 12px; }
  </style>