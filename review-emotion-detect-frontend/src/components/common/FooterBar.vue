<template>
  <footer class="footer">
    <div class="footer__container">
      <!-- Left: copyright -->
      <div class="footer__cell footer__left">
        © 2025 ToneLens
      </div>

      <!-- Center: info -->
      <div class="footer__cell footer__center">
        <div class="footer__row">
          <svg class="ico" viewBox="0 0 24 24" aria-hidden="true">
            <path d="M7.5 6h2l1-2h3l1 2h2A3.5 3.5 0 0 1 20 9.5v6A3.5 3.5 0 0 1 16.5 19h-9A3.5 3.5 0 0 1 4 15.5v-6A3.5 3.5 0 0 1 7.5 6Zm4.5 3a4.5 4.5 0 1 0 0 9a4.5 4.5 0 0 0 0-9Zm0 2a2.5 2.5 0 1 1 0 5a2.5 2.5 0 0 1 0-5Z" fill="currentColor"/>
          </svg>
          <span>Images from</span>
          <a href="https://unsplash.com" target="_blank" rel="noopener" class="link">Unsplash</a>
          <span>and</span>
          <a href="https://pexels.com" target="_blank" rel="noopener" class="link">Pexels</a>
          <span>— free to use.</span>
        </div>

        <div class="footer__row">
          <svg class="ico" viewBox="0 0 24 24" aria-hidden="true">
            <path d="M11 3h2v2h3a3 3 0 0 1 3 3v9a3 3 0 0 1-3 3H8a3 3 0 0 1-3-3V8a3 3 0 0 1 3-3h3V3Zm-2 8a1.5 1.5 0 1 0 0 3h6a1.5 1.5 0 1 0 0-3H9Z" fill="currentColor"/>
          </svg>
          <span>Models used:</span>
          <a href="https://huggingface.co/SamLowe/roberta-base-go_emotions" target="_blank" rel="noopener" class="link">
            SamLowe/roberta-base-go_emotions
          </a>
          <span>and</span>
          <a href="https://huggingface.co/google/flan-t5-base" target="_blank" rel="noopener" class="link">
            google/flan-t5-base
          </a>.
        </div>
      </div>

      <!-- Right: tagline -->
      <div class="footer__cell footer__right">
        Designed for healthy online discourse
      </div>
    </div>
  </footer>
</template>

<script setup>
// No runtime logic required
</script>

<style scoped>
/* ===== Shell ===== */
.footer {
  border-top: 1px solid rgba(15,23,42,.08);
  background: var(--ml-bg-color);   /* ✅ 使用站点浅蓝背景，保持一致 */
  color: #475569;
  font-size: 13px;
  flex-shrink: 0;                   /* ✅ 保证在 flex 布局下不会被压缩 */
}

/* Constrain width and center content */
.footer__container {
  max-width: 1120px;
  margin: 0 auto;
  padding: 18px 20px;
  display: grid;
  grid-template-columns: 1fr minmax(520px, 2fr) 1fr;
  gap: 16px;
  align-items: start;
}

.footer__cell { min-width: 0; }

.footer__left,
.footer__right {
  white-space: nowrap;
  color: #64748b;
  display: flex;
  align-items: center;
}

.footer__center {
  text-align: center;
  line-height: 1.5;
  color: #334155;
}

.footer__row {
  margin-top: 8px;
  display: inline-flex;
  gap: 6px;
  align-items: center;
  flex-wrap: wrap;
  color: #64748b;
}

.ico {
  width: 14px;
  height: 14px;
  opacity: .9;
  margin-right: 2px;
}

.link {
  color: #2563eb;
  text-decoration: none;
}
.link:hover { text-decoration: underline; }

@media (min-width: 960px) {
  .footer__left { border-right: 1px solid rgba(15,23,42,.08); padding-right: 16px; }
  .footer__right { border-left: 1px solid rgba(15,23,42,.08); padding-left: 16px; }
}

@media (max-width: 959px) {
  .footer__container {
    grid-template-columns: 1fr;
    text-align: center;
    gap: 10px;
  }
  .footer__left,
  .footer__right {
    justify-content: center;
    white-space: normal;
  }
}
</style>