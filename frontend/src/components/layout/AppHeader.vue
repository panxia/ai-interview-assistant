<template>
  <header class="bg-white/80 backdrop-blur-sm border-b shadow-sm sticky top-0 z-50">
    <div class="container mx-auto px-4 py-4">
      <div class="flex items-center justify-between">
        <!-- Logo和标题 -->
        <div class="flex items-center space-x-4">
          <h1 class="text-2xl font-bold bg-gradient-to-r from-pink-500 to-purple-600 bg-clip-text text-transparent">
            🐾 宠物乐园 🐾
          </h1>
          <p class="text-sm text-gray-600 hidden md:block">
            养育你的专属虚拟宠物，体验温馨的陪伴时光
          </p>
        </div>
        
        <!-- 右侧功能区 -->
        <div class="flex items-center space-x-4">
          <!-- 玩家统计信息 -->
          <div v-if="petStore.playerStats" class="hidden md:flex items-center space-x-3 text-sm">
            <div class="bg-yellow-100 px-3 py-1 rounded-full">
              🏆 {{ petStore.playerStats.totalAchievements }} 成就
            </div>
            <div class="bg-blue-100 px-3 py-1 rounded-full">
              ⭐ {{ petStore.playerStats.totalAchievementPoints }} 积分
            </div>
            <div class="bg-green-100 px-3 py-1 rounded-full">
              🐾 {{ petStore.playerStats.totalPets }} 宠物
            </div>
          </div>
          
          <!-- 当前宠物信息 -->
          <div v-if="petStore.currentPet" class="flex items-center space-x-2 bg-purple-50 px-3 py-1 rounded-full">
            <span class="text-sm text-purple-700">
              🐱 {{ petStore.currentPet.name || petStore.currentPet.petName }}
            </span>
            <span class="text-xs text-purple-500">
              Lv.{{ petStore.currentPet.stats.level }}
            </span>
          </div>
          
          <!-- 设置按钮 -->
          <button 
            @click="showSettings = !showSettings"
            class="p-2 rounded-full bg-gray-100 hover:bg-gray-200 transition-colors"
            title="设置"
          >
            ⚙️
          </button>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { usePetStore } from '../../store/index'

const petStore = usePetStore()
const showSettings = ref(false)
</script>
