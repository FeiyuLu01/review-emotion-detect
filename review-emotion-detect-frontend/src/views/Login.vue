<template>
    <div class="gate-wrap">
      <a-card class="gate-card" :bordered="false">
        <div class="logo">🔒 MoodLens</div>
        <a-typography-title :level="3" style="margin: 8px 0 4px 0">Enter password</a-typography-title>
        <a-typography-text type="secondary">
          This site is password protected for coursework demo.
        </a-typography-text>
  
        <a-form :model="form" @submit.prevent="onSubmit" class="mt16">
          <a-form-item>
            <a-input-password
              v-model:value="form.password"
              size="large"
              placeholder="Password"
              autocomplete="current-password"
            />
          </a-form-item>
  
          <a-form-item>
            <a-button
              type="primary"
              size="large"
              block
              :loading="loading"
              @click="onSubmit"
            >
              Enter
            </a-button>
          </a-form-item>
  
          <a-alert
            v-if="error"
            type="error"
            show-icon
            message="Incorrect password"
            description="Please check with the instructor / teammate."
          />
        </a-form>
  
        <a-typography-paragraph type="secondary" class="hint">
          Tip: Ask anyone in the TP03 team to get the password.
        </a-typography-paragraph>
      </a-card>
    </div>
  </template>
  
  <script setup>
  import { reactive, ref } from 'vue'
  import { useRoute, useRouter } from 'vue-router'
  import { loginWithPassword, isAuthed } from '@/utils/auth'
  
  const route = useRoute()
  const router = useRouter()
  
  const form = reactive({ password: '' })
  const loading = ref(false)
  const error = ref(false)
  
  async function onSubmit() {
    error.value = false
    loading.value = true
    try {
      const ok = loginWithPassword(form.password)
      if (!ok) {
        error.value = true
        return
      }
      // redirect to next (if exists) or home
      const next = (route.query.next && String(route.query.next)) || '/'
      router.replace(next)
    } finally {
      loading.value = false
    }
  }
  
  // If already authed and user hits /login, send them away.
  if (isAuthed()) {
    const next = (route.query.next && String(route.query.next)) || '/'
    router.replace(next)
  }
  </script>
  
  <style scoped>
  .gate-wrap {
    min-height: calc(100dvh - 120px);
    display: grid;
    place-items: center;
    padding: 24px;
  }
  .gate-card {
    width: 100%;
    max-width: 420px;
    border-radius: 16px;
    box-shadow: 0 10px 28px rgba(2,6,23,.10);
  }
  .logo {
    font-weight: 800;
    font-size: 18px;
    color: #0f172a;
  }
  .mt16 { margin-top: 16px; }
  .hint { margin-top: 8px; }
  </style>