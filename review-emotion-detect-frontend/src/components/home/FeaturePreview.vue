<template>
    <div class="fp">
      <div class="fp__card">
        <h3 class="fp__title">Preview the result</h3>
  
        <p class="fp__desc">
          Paste a short review and see a quick demo. Full analysis is on the Analyze page.
        </p>
  
        <div class="fp__body">
          <!-- demo text block -->
          <div class="fp__demo">
            “The phone is sleek and fast, but the battery drains too quickly. Customer
            support was kind though.”
          </div>
  
          <!-- CTA -->
          <!-- <a class="fp__btn" @click.prevent="goAnalyze" href="/analyze">Let's see what emotions are in the review</a> -->
          <div class="fp__btnRow">
            <a class="fp__btn" @click.prevent="goAnalyze" href="/analyze">
                Let's see what emotions are in the review
            </a>
          </div>
        </div>
  
        <p class="fp__note">
          * Demo only. The full report adds details & guidance.
        </p>
      </div>
    </div>
  </template>
  
  <script setup>
  import { useRouter } from 'vue-router'
  const router = useRouter()
  function goAnalyze() { router.push('/analyze') }
  </script>
  
  <style scoped>
  /* Wrapper fills parent width */
  .fp {
    width: 100%;
  }
  
  /* Card uses full width of the right column, with comfortable padding */
  .fp__card {
    width: 100%;
    border-radius: 18px;
    background: rgba(255,255,255,.78);
    box-shadow: 0 12px 36px rgba(2,6,23,.08);
    padding: 18px 20px 20px;
  }
  
  /* Title & description */
  .fp__title {
    margin: 0 0 8px 0;
    font-size: clamp(18px, 2.2vw, 24px);
    font-weight: 900;
    color: #0f172a;
  }
  
  .fp__desc {
    margin: 0 0 14px 0;
    color: #475569;
  }
  
  /* Body becomes a simple vertical stack that uses full width */
  .fp__body {
    display: grid;
    grid-template-columns: 1fr;
    gap: 14px;
    width: 100%;
  }
  
  /* Demo bubble should not cap width excessively */
  .fp__demo {
    width: 100%;                   /* fill available width */
    border-radius: 12px;
    padding: 14px 16px;
    background: #fff;
    box-shadow: 0 1px 0 rgba(2,6,23,.06) inset, 0 0 0 1px rgba(2,6,23,.06);
    color: #111827;
  }
  
  /* CTA button aligned to content start and responsive width */
  .fp__btn {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: fit-content;            /* only as wide as text */
    padding: 10px 16px;
    border-radius: 999px;
    font-weight: 800;
    text-decoration: none;
    color: #fff;
    background: linear-gradient(90deg, #7C3AED, #06B6D4);
    box-shadow: 0 8px 22px rgba(16,24,40,.15);
    transition: transform .15s ease, box-shadow .15s ease, filter .15s ease;
  }
  .fp__btn:hover { transform: translateY(-1px); box-shadow: 0 10px 26px rgba(16,24,40,.22); filter: brightness(1.05); }
  
  /* Footnote */
  .fp__note {
    margin: 12px 0 0 0;
    color: #6b7280;
    font-size: 13px;
  }

.fp__btnRow {
  display: flex;
  justify-content: flex-end; 
  width: 100%;
}

.fp__btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 10px 16px;
  border-radius: 999px;
  font-weight: 800;
  text-decoration: none;
  color: #fff;
  background: linear-gradient(90deg, #7C3AED, #06B6D4);
  box-shadow: 0 8px 22px rgba(16,24,40,.15);
  transition: transform .15s ease, box-shadow .15s ease, filter .15s ease;
}
.fp__btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 10px 26px rgba(16,24,40,.22);
  filter: brightness(1.05);
}
  </style>