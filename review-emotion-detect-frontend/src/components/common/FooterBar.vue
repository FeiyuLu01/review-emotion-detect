<template>
  <footer class="footer">
    <div class="footer__left">
      © 2025 MoodLens
    </div>

    <div class="footer__center">
      <strong>TP03 Group</strong><br />
      <span class="members">
        Feiyu Lu · Yao Zhang · Luosong Wang · Zhenyu Wang · Shiyu He · Bingcen Qian
      </span>
    </div>

    <div class="footer__right">
      Designed for healthy online discourse
    </div>
  </footer>
</template>

<script setup>
// simple footer, no logic needed
</script>

<style scoped>
.footer {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  padding: 16px 24px;
  font-size: 13px;
  color: #475569;
  border-top: 1px solid rgba(15, 23, 42, .08);
  background: transparent;
}

/* Center column (group info) */
.footer__center {
  text-align: center;
  flex: 1 1 auto;
  font-size: 13px;
  line-height: 1.4;
  color: #334155;
}
.footer__center strong {
  font-weight: 700;
  color: #0f172a;
}
.members {
  font-size: 12px;
  color: #475569;
}

/* Responsive: stack on small screens */
@media (max-width: 640px) {
  .footer {
    flex-direction: column;
    text-align: center;
  }
  .footer__left,
  .footer__right {
    order: 2;
    font-size: 12px;
  }
  .footer__center {
    order: 1;
    margin-bottom: 8px;
  }
}
</style>