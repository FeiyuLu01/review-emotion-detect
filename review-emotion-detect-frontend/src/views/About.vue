<template>
    <div class="about container">
      <a-row :gutter="[16,16]">
        <a-col :xs="24" :md="14">
          <a-card class="card" :bordered="false">
            <a-typography-title :level="3" style="margin:0;">What is MoodLens?</a-typography-title>
            <a-typography-paragraph type="secondary">
              MoodLens is a lightweight tool to surface emotional tones in online reviews and posts.
              By reflecting on emotions, we aim to gently foster digital citizenship and healthier conversations.
            </a-typography-paragraph>
  
            <a-typography-title :level="4">Core ideas</a-typography-title>
            <ul class="list">
              <li>Respect privacy and minimize data retention.</li>
              <li>Explain results with friendly language.</li>
              <li>Encourage constructive expression rather than judge.</li>
            </ul>
          </a-card>
        </a-col>
  
        <a-col :xs="24" :md="10">
          <a-card class="card" :bordered="false">
            <a-descriptions bordered title="Tech Stack" :column="1">
              <a-descriptions-item label="Frontend">Vue 3, Ant Design Vue, Vite</a-descriptions-item>
              <a-descriptions-item label="State">Pinia</a-descriptions-item>
              <a-descriptions-item label="Routing">Vue Router</a-descriptions-item>
              <a-descriptions-item label="API">Axios</a-descriptions-item>
              <a-descriptions-item label="Mock (dev)">Mock.js</a-descriptions-item>
            </a-descriptions>
          </a-card>
        </a-col>
      </a-row>
    </div>
  </template>
  
  <script setup>
  </script>
  
  <style scoped>
  .list { margin: 0; padding-left: 18px; color: #334155; }
  .list li { margin: 6px 0; }
  </style>