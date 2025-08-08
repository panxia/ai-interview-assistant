<template>
  <div class="min-h-screen bg-gray-50 flex flex-col">
    <header class="p-4 border-b bg-white">
      <h1 class="text-lg font-semibold">AI 面试助手</h1>
      <p class="text-sm text-gray-500">基于豆包大模型 · 支持 PC/手机</p>
    </header>
    <main class="flex-1">
      <Chat />
    </main>
    <footer class="p-4 text-center text-xs text-gray-400">© 2025</footer>
  </div>
</template>

<script setup lang="ts">
import Chat from './components/Chat.vue'
</script>
