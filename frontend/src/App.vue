<template>
  <div class="min-h-screen bg-gradient-to-br from-pink-100 via-purple-50 to-indigo-100">
    <header class="p-4 border-b bg-white/80 backdrop-blur-sm shadow-sm">
      <h1 class="text-2xl font-bold text-center bg-gradient-to-r from-pink-500 to-purple-600 bg-clip-text text-transparent">
        🐾 可爱宠物养成游戏 🐾
      </h1>
      <p class="text-sm text-gray-600 text-center mt-1">养育你的专属虚拟宠物，体验温馨的陪伴时光</p>
    </header>
    <main class="flex-1">
      <PetGame />
    </main>
    <footer class="p-4 text-center text-xs text-gray-400">
      <p>© 2025 可爱宠物养成游戏 · 用爱心陪伴每一天 💕</p>
    </footer>
  </div>
</template>

<script setup lang="ts">
import PetGame from './components/PetGame.vue'
</script>
