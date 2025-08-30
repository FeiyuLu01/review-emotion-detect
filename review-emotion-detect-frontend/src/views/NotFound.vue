<template>
    <div class="nf container">
      <a-result status="404" title="404" sub-title="Sorry, the page you visited does not exist.">
        <template #extra>
          <a-button type="primary" @click="$router.push('/home')">Back Home</a-button>
        </template>
      </a-result>
    </div>
  </template>
  
  <script setup>
  </script>
  
  <style scoped>
  .nf { padding: 24px 0; }
  </style>