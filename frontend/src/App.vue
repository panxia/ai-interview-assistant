<template>
  <div class="min-h-screen bg-gradient-to-br from-pink-100 via-purple-50 to-indigo-100">
    <header class="p-4 border-b bg-white/80 backdrop-blur-sm shadow-sm">
      <h1 class="text-2xl font-bold text-center bg-gradient-to-r from-pink-500 to-purple-600 bg-clip-text text-transparent">
        🐠 手动画鱼鱼缸游戏 🐠
      </h1>
      <p class="text-sm text-gray-600 text-center mt-1">在画布上画鱼，观看鱼在鱼缸里游泳的奇妙体验</p>
    </header>
    <main class="flex-1">
      <div class="flex gap-4 p-4">
        <div class="flex-1">
          <FishTankGame />
        </div>
        <div class="w-80">
          <PetGame />
        </div>
      </div>
    </main>
    <footer class="p-4 text-center text-xs text-gray-400">
      <p>© 2025 手动画鱼鱼缸游戏 · 用创意绘制美好时光 🎨</p>
    </footer>
  </div>
</template>

<script setup lang="ts">
import PetGame from './components/PetGame.vue'
import FishTankGame from './components/FishTankGame.vue'
</script>
