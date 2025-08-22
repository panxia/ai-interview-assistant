<template>
  <div class="dashboard">
    <!-- 欢迎区域 -->
    <div class="welcome-section mb-8">
      <div class="text-center">
        <h1 class="text-4xl font-bold bg-gradient-to-r from-purple-600 to-pink-600 bg-clip-text text-transparent mb-4">
          🎉 欢迎来到宠物乐园 🎉
        </h1>
        <p class="text-lg text-gray-600 max-w-2xl mx-auto">
          在这里，你可以创造、养育和陪伴你的专属虚拟宠物。每一个宠物都有独特的性格和外观，等待着你的关爱！
        </p>
      </div>
    </div>

    <!-- 快速操作区域 -->
    <div class="quick-actions mb-8">
      <h2 class="text-2xl font-semibold mb-6 text-gray-800">🚀 快速开始</h2>
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <div 
          @click="navigateTo('/pets')"
          class="quick-action-card bg-gradient-to-br from-blue-500 to-purple-600 text-white p-6 rounded-2xl cursor-pointer hover:scale-105 transition-all duration-300 shadow-lg"
        >
          <div class="text-4xl mb-3">🐾</div>
          <h3 class="text-xl font-semibold mb-2">创建宠物</h3>
          <p class="text-blue-100 text-sm">开始你的宠物养成之旅</p>
        </div>

        <div 
          @click="navigateTo('/interaction')"
          class="quick-action-card bg-gradient-to-br from-green-500 to-teal-600 text-white p-6 rounded-2xl cursor-pointer hover:scale-105 transition-all duration-300 shadow-lg"
        >
          <div class="text-4xl mb-3">🎮</div>
          <h3 class="text-xl font-semibold mb-2">宠物交互</h3>
          <p class="text-green-100 text-sm">与宠物互动，培养感情</p>
        </div>

        <div 
          @click="navigateTo('/games')"
          class="quick-action-card bg-gradient-to-br from-orange-500 to-red-600 text-white p-6 rounded-2xl cursor-pointer hover:scale-105 transition-all duration-300 shadow-lg"
        >
          <div class="text-4xl mb-3">🎯</div>
          <h3 class="text-xl font-semibold mb-2">小游戏</h3>
          <p class="text-orange-100 text-sm">挑战小游戏，获得奖励</p>
        </div>

        <div 
          @click="navigateTo('/achievements')"
          class="quick-action-card bg-gradient-to-br from-yellow-500 to-orange-600 text-white p-6 rounded-2xl cursor-pointer hover:scale-105 transition-all duration-300 shadow-lg"
        >
          <div class="text-4xl mb-3">🏆</div>
          <h3 class="text-xl font-semibold mb-2">成就系统</h3>
          <p class="text-yellow-100 text-sm">解锁成就，展示实力</p>
        </div>
      </div>
    </div>

    <!-- 当前状态区域 -->
    <div v-if="petStore.currentPet" class="current-status mb-8">
      <h2 class="text-2xl font-semibold mb-6 text-gray-800">🐱 当前宠物状态</h2>
      <div class="bg-white/80 backdrop-blur-sm rounded-2xl p-6 shadow-lg">
        <div class="flex items-center space-x-6">
          <!-- 宠物头像 -->
          <div class="flex-shrink-0">
            <div class="w-24 h-24 bg-gradient-to-br from-purple-400 to-pink-400 rounded-full flex items-center justify-center text-4xl">
              🐱
            </div>
          </div>
          
          <!-- 宠物信息 -->
          <div class="flex-1">
            <h3 class="text-xl font-semibold text-gray-800 mb-2">
              {{ petStore.currentPet.name || petStore.currentPet.petName }}
            </h3>
            <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
              <div class="text-center">
                <div class="text-sm text-gray-600">等级</div>
                <div class="text-lg font-semibold text-purple-600">{{ petStore.currentPet.stats.level }}</div>
              </div>
              <div class="text-center">
                <div class="text-sm text-gray-600">快乐度</div>
                <div class="text-lg font-semibold text-green-600">{{ petStore.currentPet.stats.happiness }}%</div>
              </div>
              <div class="text-center">
                <div class="text-sm text-gray-600">健康</div>
                <div class="text-lg font-semibold text-blue-600">{{ petStore.currentPet.stats.health }}%</div>
              </div>
              <div class="text-center">
                <div class="text-sm text-gray-600">能量</div>
                <div class="text-lg font-semibold text-orange-600">{{ petStore.currentPet.stats.energy }}%</div>
              </div>
            </div>
          </div>
          
          <!-- 快速操作 -->
          <div class="flex-shrink-0 space-y-2">
            <button 
              @click="navigateTo('/interaction')"
              class="w-full px-4 py-2 bg-purple-500 text-white rounded-lg hover:bg-purple-600 transition-colors"
            >
              互动
            </button>
            <button 
              @click="navigateTo('/pets')"
              class="w-full px-4 py-2 bg-gray-500 text-white rounded-lg hover:bg-gray-600 transition-colors"
            >
              管理
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 游戏统计区域 -->
    <div v-if="petStore.playerStats" class="game-stats mb-8">
      <h2 class="text-2xl font-semibold mb-6 text-gray-800">📊 游戏统计</h2>
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <div class="bg-white/80 backdrop-blur-sm rounded-2xl p-6 shadow-lg text-center">
          <div class="text-3xl mb-2">🐾</div>
          <div class="text-2xl font-bold text-purple-600">{{ petStore.playerStats.totalPets }}</div>
          <div class="text-sm text-gray-600">拥有宠物</div>
        </div>
        
        <div class="bg-white/80 backdrop-blur-sm rounded-2xl p-6 shadow-lg text-center">
          <div class="text-3xl mb-2">🏆</div>
          <div class="text-2xl font-bold text-yellow-600">{{ petStore.playerStats.totalAchievements }}</div>
          <div class="text-sm text-gray-600">获得成就</div>
        </div>
        
        <div class="bg-white/80 backdrop-blur-sm rounded-2xl p-6 shadow-lg text-center">
          <div class="text-3xl mb-2">⭐</div>
          <div class="text-2xl font-bold text-blue-600">{{ petStore.playerStats.totalAchievementPoints }}</div>
          <div class="text-sm text-gray-600">成就积分</div>
        </div>
        
        <div class="bg-white/80 backdrop-blur-sm rounded-2xl p-6 shadow-lg text-center">
          <div class="text-3xl mb-2">📈</div>
          <div class="text-2xl font-bold text-green-600">{{ petStore.playerStats.highestLevel }}</div>
          <div class="text-sm text-gray-600">最高等级</div>
        </div>
      </div>
    </div>

    <!-- 功能导航区域 -->
    <div class="feature-navigation">
      <h2 class="text-2xl font-semibold mb-6 text-gray-800">🔧 功能导航</h2>
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <div 
          @click="navigateTo('/inventory')"
          class="feature-card bg-white/80 backdrop-blur-sm rounded-2xl p-6 shadow-lg cursor-pointer hover:shadow-xl transition-all duration-300"
        >
          <div class="text-3xl mb-3">🎒</div>
          <h3 class="text-lg font-semibold text-gray-800 mb-2">背包系统</h3>
          <p class="text-gray-600 text-sm">管理你的物品和装备</p>
        </div>
        
        <div 
          @click="navigateTo('/stats')"
          class="feature-card bg-white/80 backdrop-blur-sm rounded-2xl p-6 shadow-lg cursor-pointer hover:shadow-xl transition-all duration-300"
        >
          <div class="text-3xl mb-3">📊</div>
          <h3 class="text-lg font-semibold text-gray-800 mb-2">统计信息</h3>
          <p class="text-gray-600 text-sm">查看详细的游戏数据</p>
        </div>
        
        <div 
          @click="navigateTo('/system')"
          class="feature-card bg-white/80 backdrop-blur-sm rounded-2xl p-6 shadow-lg cursor-pointer hover:shadow-xl transition-all duration-300"
        >
          <div class="text-3xl mb-3">⚙️</div>
          <h3 class="text-lg font-semibold text-gray-800 mb-2">系统功能</h3>
          <p class="text-gray-600 text-sm">系统设置和状态监控</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { usePetStore } from '../store/index'

const router = useRouter()
const petStore = usePetStore()

const navigateTo = (path: string) => {
  router.push(path)
}

onMounted(async () => {
  // 加载初始数据
  try {
    if (!petStore.petTypes.length) {
      await petStore.getPetTypes()
    }
    if (!petStore.personalities.length) {
      await petStore.getPersonalities()
    }
    
    // 如果有当前宠物，加载相关数据
    if (petStore.currentPet) {
      await petStore.getPlayerStats(petStore.currentPet.playerId)
      await petStore.getPlayerAchievements(petStore.currentPet.playerId)
      await petStore.getPlayerInventory(petStore.currentPet.playerId)
    }
  } catch (error) {
    console.error('加载初始数据失败:', error)
  }
})
</script>

<style scoped>
.dashboard {
  max-width: 1200px;
  margin: 0 auto;
}

.quick-action-card:hover {
  transform: translateY(-5px);
}

.feature-card:hover {
  transform: translateY(-2px);
}
</style>
