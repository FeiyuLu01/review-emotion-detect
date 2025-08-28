<template>
    <section class="hero container">
      <div class="hero__left">
        <h1 class="hero__title">
          Understand Emotions.
          <br />Encourage Better Conversations.
        </h1>
        <p class="hero__subtitle">
          MoodLens helps people reflect on their online expression.
          Paste a review, reveal its emotional tones,
          and receive a friendly, constructive summary.
        </p>
        <div class="hero__cta">
          <a-button type="primary" size="large" @click="goAnalyze">
            Start Analyzing
          </a-button>
          <a-button size="large" @click="goAbout">Learn More</a-button>
        </div>
  
        <div class="hero__chips">
          <a-tag color="purple">Go Emotions</a-tag>
          <a-tag color="cyan">Healthy Discourse</a-tag>
          <a-tag color="blue">Privacy-first</a-tag>
        </div>
      </div>
  
      <div class="hero__right card">
        <div class="preview">
          <div class="bubble bubble--user">“I loved this product but the delivery was slow.”</div>
          <div class="bubble bubble--ai">
            Preview:
            <ul>
              <li>Joy: 0.62</li>
              <li>Annoyance: 0.28</li>
              <li>Disappointment: 0.10</li>
            </ul>
          </div>
          <div class="pulse"></div>
        </div>
      </div>
    </section>
  </template>
  
  <script setup>
  // Landing page with animated hero; CTA routes to Analyze page
  import { useRouter } from 'vue-router'
  const router = useRouter()
  function goAnalyze() { router.push('/analyze') }
  function goAbout() { router.push('/about') }
  </script>
  
  <style scoped>
  .hero {
    display: grid;
    grid-template-columns: 1.1fr .9fr;
    gap: 28px;
    align-items: center;
  }
  .hero__left { z-index: 1; }
  .hero__title {
    font-size: clamp(28px, 5vw, 48px);
    margin: 0 0 12px 0;
    line-height: 1.12;
    letter-spacing: .2px;
    background: linear-gradient(90deg, #111827, #4338CA, #0EA5E9);
    -webkit-background-clip: text; background-clip: text; color: transparent;
  }
  .hero__subtitle {
    color: #475569;
    font-size: 16px;
    margin: 0 0 20px 0;
  }
  .hero__cta { display: flex; gap: 12px; margin-bottom: 12px; }
  .hero__chips { display: flex; gap: 8px; }
  
  .hero__right {
    padding: 24px;
    position: relative;
    overflow: hidden;
  }
  
  .preview { position: relative; min-height: 240px; }
  .bubble {
    max-width: 85%;
    padding: 12px 16px;
    border-radius: 14px;
    box-shadow: var(--ml-shadow);
    margin: 10px 0;
    animation: floatIn .7s ease both;
  }
  .bubble--user {
    background: #ffffff;
    border: 1px solid rgba(15,23,42,.06);
  }
  .bubble--ai {
    background: linear-gradient(120deg, rgba(124,58,237,.10), rgba(6,182,212,.10));
    border: 1px solid rgba(15,23,42,.05);
    margin-left: auto;
  }
  @keyframes floatIn {
    from { transform: translateY(10px); opacity: 0; }
    to   { transform: translateY(0);    opacity: 1; }
  }
  
  /* Decorative pulse ring */
  .pulse {
    position: absolute;
    right: -60px; bottom: -60px;
    width: 200px; height: 200px;
    border-radius: 50%;
    background: radial-gradient(circle, rgba(124,58,237,.18), transparent 60%);
    animation: pulse 2.8s ease-in-out infinite;
    filter: blur(10px);
  }
  @keyframes pulse {
    0%, 100% { transform: scale(0.9); opacity: .7; }
    50% { transform: scale(1.05); opacity: 1; }
  }
  </style>