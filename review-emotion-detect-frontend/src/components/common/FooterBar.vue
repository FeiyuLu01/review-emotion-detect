<template>
    <div class="footer container">
      <div class="footer__left">
        <span>© {{ year }} MoodLens</span>
      </div>
      <div class="footer__right">
        <a-typography-text type="secondary">
          Designed for healthy online discourse
        </a-typography-text>
      </div>
    </div>
  </template>
  
  <script setup>
  // Simple footer with brand statement
  const year = new Date().getFullYear()
  </script>
  
  <style scoped>
  .footer {
    display: flex;
    align-items: center;
    justify-content: space-between;
    color: #334155;
    border-top: 1px dashed rgba(15,23,42,.08);
    padding-top: 12px;
  }
  </style>