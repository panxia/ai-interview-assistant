<template>
  <div class="pet-game min-h-screen bg-gradient-to-br from-blue-50 via-purple-50 to-pink-50">
    <!-- 加载状态 -->
    <div v-if="loading" class="flex items-center justify-center min-h-screen">
      <div class="text-center">
        <div class="animate-spin rounded-full h-32 w-32 border-b-2 border-purple-500 mx-auto mb-4"></div>
        <p class="text-lg text-gray-600">加载中...</p>
      </div>
    </div>

    <!-- 主游戏界面 -->
    <div v-else class="container mx-auto px-4 py-6">
      <!-- 顶部导航栏 -->
      <header class="bg-white/80 backdrop-blur-sm rounded-2xl p-4 mb-6 shadow-lg">
        <div class="flex items-center justify-between">
          <div class="flex items-center space-x-4">
            <h1 class="text-2xl font-bold bg-gradient-to-r from-purple-600 to-pink-600 bg-clip-text text-transparent">
              🐾 宠物乐园 🐾
            </h1>
            <div v-if="currentPet" class="text-sm text-gray-600">
              欢迎回来，{{ currentPet.petName }}！
            </div>
          </div>
          
          <div class="flex items-center space-x-4">
            <!-- 玩家统计 -->
            <div v-if="playerStats" class="flex items-center space-x-3 text-sm">
              <div class="bg-yellow-100 px-3 py-1 rounded-full">
                🏆 {{ playerStats.totalAchievements }} 成就
              </div>
              <div class="bg-blue-100 px-3 py-1 rounded-full">
                ⭐ {{ playerStats.totalAchievementPoints }} 积分
              </div>
            </div>
            
            <!-- 设置按钮 -->
            <button 
              @click="showSettings = !showSettings"
              class="p-2 rounded-full bg-gray-100 hover:bg-gray-200 transition-colors"
            >
              ⚙️
            </button>
          </div>
        </div>
      </header>

      <!-- 宠物创建界面 -->
      <div v-if="!currentPet && !showPetList" class="text-center">
        <div class="bg-white/80 backdrop-blur-sm rounded-3xl p-8 max-w-md mx-auto shadow-lg">
          <h2 class="text-3xl font-bold mb-4">🌟 开始你的宠物之旅 🌟</h2>
          <p class="text-gray-600 mb-6">创造你的第一个专属宠物，开启奇妙的陪伴旅程！</p>
          <button 
            @click="showCustomizer = true"
            class="px-8 py-3 bg-gradient-to-r from-purple-500 to-pink-500 text-white rounded-xl hover:from-purple-600 hover:to-pink-600 transition-all duration-300 transform hover:scale-105"
          >
            🎨 创造宠物
          </button>
        </div>
      </div>

      <!-- 宠物列表界面 -->
      <div v-else-if="showPetList" class="space-y-6">
        <div class="flex items-center justify-between">
          <h2 class="text-2xl font-bold">我的宠物</h2>
          <button 
            @click="showCustomizer = true"
            class="px-4 py-2 bg-purple-500 text-white rounded-lg hover:bg-purple-600 transition-colors"
          >
            ➕ 创建新宠物
          </button>
        </div>
        
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <div 
            v-for="pet in pets" 
            :key="pet.petId"
            @click="selectPet(pet)"
            class="bg-white/80 backdrop-blur-sm rounded-2xl p-6 shadow-lg cursor-pointer hover:shadow-xl transition-all duration-300 transform hover:scale-105"
          >
            <div class="text-center">
              <PetRenderer 
                :appearance="pet.appearance"
                :size="150"
                :interactive="false"
              />
              <h3 class="text-lg font-semibold mt-4">{{ pet.petName }}</h3>
              <div class="text-sm text-gray-600 mt-2">
                <p>等级 {{ pet.stats.level }} | 经验 {{ pet.stats.experience }}</p>
                <p>快乐度 {{ pet.stats.happiness }}% | 健康 {{ pet.stats.health }}%</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 主游戏界面 -->
      <div v-else-if="currentPet" class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <!-- 左侧：宠物展示区 -->
        <div class="lg:col-span-2 space-y-6">
          <!-- 宠物显示区域 -->
          <div class="bg-white/80 backdrop-blur-sm rounded-3xl p-6 shadow-lg">
            <div class="flex justify-center mb-6">
              <PetRenderer 
                :appearance="currentPet.appearance"
                :size="300"
                :interactive="true"
                :show-effects="true"
                @click="handlePetClick"
              />
            </div>
            
            <!-- 宠物状态显示 -->
            <div class="bg-gray-50 rounded-2xl p-4">
              <h3 class="text-lg font-semibold mb-3 text-center">{{ currentPet.petName }} 的状态</h3>
              <div class="grid grid-cols-2 gap-4">
                <div class="space-y-2">
                  <div class="flex items-center justify-between">
                    <span class="text-sm">❤️ 健康</span>
                    <span class="text-sm font-semibold">{{ currentPet.stats.health }}%</span>
                  </div>
                  <div class="w-full bg-gray-200 rounded-full h-2">
                    <div 
                      class="bg-red-500 h-2 rounded-full transition-all duration-300"
                      :style="{ width: currentPet.stats.health + '%' }"
                    ></div>
                  </div>
                  
                  <div class="flex items-center justify-between">
                    <span class="text-sm">😊 快乐</span>
                    <span class="text-sm font-semibold">{{ currentPet.stats.happiness }}%</span>
                  </div>
                  <div class="w-full bg-gray-200 rounded-full h-2">
                    <div 
                      class="bg-yellow-500 h-2 rounded-full transition-all duration-300"
                      :style="{ width: currentPet.stats.happiness + '%' }"
                    ></div>
                  </div>
                  
                  <div class="flex items-center justify-between">
                    <span class="text-sm">⚡ 能量</span>
                    <span class="text-sm font-semibold">{{ currentPet.stats.energy }}%</span>
                  </div>
                  <div class="w-full bg-gray-200 rounded-full h-2">
                    <div 
                      class="bg-blue-500 h-2 rounded-full transition-all duration-300"
                      :style="{ width: currentPet.stats.energy + '%' }"
                    ></div>
                  </div>
                </div>
                
                <div class="space-y-2">
                  <div class="flex items-center justify-between">
                    <span class="text-sm">🍎 饥饿</span>
                    <span class="text-sm font-semibold">{{ currentPet.stats.hunger }}%</span>
                  </div>
                  <div class="w-full bg-gray-200 rounded-full h-2">
                    <div 
                      class="bg-orange-500 h-2 rounded-full transition-all duration-300"
                      :style="{ width: currentPet.stats.hunger + '%' }"
                    ></div>
                  </div>
                  
                  <div class="flex items-center justify-between">
                    <span class="text-sm">🧼 清洁</span>
                    <span class="text-sm font-semibold">{{ currentPet.stats.cleanliness }}%</span>
                  </div>
                  <div class="w-full bg-gray-200 rounded-full h-2">
                    <div 
                      class="bg-green-500 h-2 rounded-full transition-all duration-300"
                      :style="{ width: currentPet.stats.cleanliness + '%' }"
                    ></div>
                  </div>
                  
                  <div class="flex items-center justify-between">
                    <span class="text-sm">💖 忠诚</span>
                    <span class="text-sm font-semibold">{{ currentPet.stats.loyalty }}%</span>
                  </div>
                  <div class="w-full bg-gray-200 rounded-full h-2">
                    <div 
                      class="bg-pink-500 h-2 rounded-full transition-all duration-300"
                      :style="{ width: currentPet.stats.loyalty + '%' }"
                    ></div>
                  </div>
                </div>
              </div>
              
              <!-- 等级和经验 -->
              <div class="mt-4 p-3 bg-purple-50 rounded-lg">
                <div class="flex items-center justify-between mb-2">
                  <span class="text-sm font-semibold">等级 {{ currentPet.stats.level }}</span>
                  <span class="text-sm">经验 {{ currentPet.stats.experience }} / {{ currentPet.stats.level * 100 }}</span>
                </div>
                <div class="w-full bg-gray-200 rounded-full h-3">
                  <div 
                    class="bg-purple-500 h-3 rounded-full transition-all duration-300"
                    :style="{ width: (currentPet.stats.experience / (currentPet.stats.level * 100)) * 100 + '%' }"
                  ></div>
                </div>
              </div>
            </div>
          </div>

          <!-- 小游戏区域 -->
          <div v-if="currentGame" class="bg-white/80 backdrop-blur-sm rounded-3xl p-6 shadow-lg">
            <MiniGamePlayer 
              :game-data="currentGame"
              @game-complete="handleGameComplete"
              @game-cancel="currentGame = null"
            />
          </div>
        </div>

        <!-- 右侧：操作面板 -->
        <div class="space-y-6">
          <!-- 快速操作 -->
          <div class="bg-white/80 backdrop-blur-sm rounded-2xl p-4 shadow-lg">
            <h3 class="text-lg font-semibold mb-4">快速操作</h3>
            <div class="grid grid-cols-2 gap-3">
              <button 
                @click="feedPet"
                class="p-3 bg-orange-100 hover:bg-orange-200 rounded-xl transition-colors text-center"
                :disabled="actionLoading"
              >
                <div class="text-2xl mb-1">🍎</div>
                <div class="text-xs">喂食</div>
              </button>
              
              <button 
                @click="cleanPet"
                class="p-3 bg-blue-100 hover:bg-blue-200 rounded-xl transition-colors text-center"
                :disabled="actionLoading"
              >
                <div class="text-2xl mb-1">🧼</div>
                <div class="text-xs">清洁</div>
              </button>
              
              <button 
                @click="restPet"
                class="p-3 bg-green-100 hover:bg-green-200 rounded-xl transition-colors text-center"
                :disabled="actionLoading"
              >
                <div class="text-2xl mb-1">😴</div>
                <div class="text-xs">休息</div>
              </button>
              
              <button 
                @click="playWithPet('CUDDLE')"
                class="p-3 bg-pink-100 hover:bg-pink-200 rounded-xl transition-colors text-center"
                :disabled="actionLoading"
              >
                <div class="text-2xl mb-1">🤗</div>
                <div class="text-xs">拥抱</div>
              </button>
            </div>
          </div>

          <!-- 互动游戏 -->
          <div class="bg-white/80 backdrop-blur-sm rounded-2xl p-4 shadow-lg">
            <h3 class="text-lg font-semibold mb-4">互动游戏</h3>
            <div class="space-y-3">
              <button 
                @click="playWithPet('FETCH')"
                class="w-full p-3 bg-yellow-100 hover:bg-yellow-200 rounded-xl transition-colors flex items-center"
                :disabled="actionLoading"
              >
                <span class="text-xl mr-3">🎾</span>
                <span class="text-sm">抛接游戏</span>
              </button>
              
              <button 
                @click="playWithPet('PUZZLE')"
                class="w-full p-3 bg-purple-100 hover:bg-purple-200 rounded-xl transition-colors flex items-center"
                :disabled="actionLoading"
              >
                <span class="text-xl mr-3">🧩</span>
                <span class="text-sm">益智游戏</span>
              </button>
              
              <button 
                @click="playWithPet('EXERCISE')"
                class="w-full p-3 bg-red-100 hover:bg-red-200 rounded-xl transition-colors flex items-center"
                :disabled="actionLoading"
              >
                <span class="text-xl mr-3">🏃</span>
                <span class="text-sm">运动训练</span>
              </button>
            </div>
          </div>

          <!-- 小游戏 -->
          <div class="bg-white/80 backdrop-blur-sm rounded-2xl p-4 shadow-lg">
            <h3 class="text-lg font-semibold mb-4">小游戏</h3>
            <div class="space-y-3">
              <button 
                @click="startMiniGame('MEMORY_CHALLENGE')"
                class="w-full p-3 bg-indigo-100 hover:bg-indigo-200 rounded-xl transition-colors flex items-center"
                :disabled="actionLoading || currentGame"
              >
                <span class="text-xl mr-3">🧠</span>
                <span class="text-sm">记忆挑战</span>
              </button>
              
              <button 
                @click="startMiniGame('REACTION_TEST')"
                class="w-full p-3 bg-teal-100 hover:bg-teal-200 rounded-xl transition-colors flex items-center"
                :disabled="actionLoading || currentGame"
              >
                <span class="text-xl mr-3">⚡</span>
                <span class="text-sm">反应测试</span>
              </button>
              
              <button 
                @click="startMiniGame('RHYTHM_GAME')"
                class="w-full p-3 bg-rose-100 hover:bg-rose-200 rounded-xl transition-colors flex items-center"
                :disabled="actionLoading || currentGame"
              >
                <span class="text-xl mr-3">🎵</span>
                <span class="text-sm">节奏游戏</span>
              </button>
            </div>
          </div>

          <!-- 背包物品 -->
          <div class="bg-white/80 backdrop-blur-sm rounded-2xl p-4 shadow-lg">
            <h3 class="text-lg font-semibold mb-4">背包物品</h3>
            <div v-if="inventory.length === 0" class="text-center text-gray-500 py-4">
              背包是空的
            </div>
            <div v-else class="space-y-2">
              <div 
                v-for="item in inventory" 
                :key="item.itemId"
                class="flex items-center justify-between p-2 bg-gray-50 rounded-lg"
              >
                <div class="flex items-center">
                  <span class="text-lg mr-2">📦</span>
                  <div>
                    <div class="text-sm font-medium">{{ item.itemName }}</div>
                    <div class="text-xs text-gray-500">数量: {{ item.quantity }}</div>
                  </div>
                </div>
                <button 
                  v-if="item.itemType === 'CONSUMABLE'"
                  @click="useItem(item.itemId)"
                  class="px-2 py-1 bg-green-500 text-white text-xs rounded hover:bg-green-600"
                  :disabled="actionLoading"
                >
                  使用
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 宠物自定义器模态框 -->
    <div v-if="showCustomizer" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
      <div class="bg-white rounded-3xl max-w-6xl w-full max-h-[90vh] overflow-auto">
        <PetCustomizer @pet-created="handlePetCreated" />
        <div class="p-4 border-t">
          <button 
            @click="showCustomizer = false"
            class="px-4 py-2 bg-gray-300 text-gray-700 rounded-lg hover:bg-gray-400 transition-colors"
          >
            取消
          </button>
        </div>
      </div>
    </div>

    <!-- 成就通知 -->
    <div 
      v-if="showAchievementNotification"
      class="fixed top-4 right-4 bg-yellow-400 text-yellow-900 p-4 rounded-lg shadow-lg z-50 transform transition-all duration-300"
      :class="showAchievementNotification ? 'translate-x-0' : 'translate-x-full'"
    >
      <div class="flex items-center">
        <span class="text-2xl mr-3">🏆</span>
        <div>
          <div class="font-semibold">新成就解锁！</div>
          <div class="text-sm">{{ latestAchievement?.achievementName }}</div>
        </div>
      </div>
    </div>

    <!-- 底部切换栏 -->
    <nav v-if="pets.length > 0" class="fixed bottom-4 left-4 right-4 z-40">
      <div class="bg-white/90 backdrop-blur-sm rounded-2xl p-2 shadow-lg max-w-md mx-auto">
        <div class="flex justify-around">
          <button 
            @click="showPetList = false"
            :class="['p-3 rounded-xl transition-colors', !showPetList ? 'bg-purple-100 text-purple-600' : 'text-gray-600 hover:bg-gray-100']"
          >
            <div class="text-xl">🏠</div>
            <div class="text-xs">主页</div>
          </button>
          
          <button 
            @click="showPetList = true"
            :class="['p-3 rounded-xl transition-colors', showPetList ? 'bg-purple-100 text-purple-600' : 'text-gray-600 hover:bg-gray-100']"
          >
            <div class="text-xl">🐾</div>
            <div class="text-xs">宠物</div>
          </button>
          
          <button 
            @click="showAchievements = true"
            class="p-3 rounded-xl text-gray-600 hover:bg-gray-100 transition-colors"
          >
            <div class="text-xl">🏆</div>
            <div class="text-xs">成就</div>
          </button>
        </div>
      </div>
    </nav>

    <!-- 成就面板 -->
    <div v-if="showAchievements" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
      <div class="bg-white rounded-3xl max-w-2xl w-full max-h-[80vh] overflow-auto">
        <div class="p-6">
          <div class="flex items-center justify-between mb-6">
            <h2 class="text-2xl font-bold">🏆 成就系统</h2>
            <button 
              @click="showAchievements = false"
              class="p-2 rounded-full bg-gray-100 hover:bg-gray-200 transition-colors"
            >
              ✕
            </button>
          </div>
          
          <div v-if="achievements.length === 0" class="text-center text-gray-500 py-8">
            暂无成就，去和宠物互动吧！
          </div>
          
          <div v-else class="space-y-4">
            <div 
              v-for="achievement in achievements" 
              :key="achievement.achievementId"
              class="p-4 bg-yellow-50 border border-yellow-200 rounded-lg"
            >
              <div class="flex items-start justify-between">
                <div class="flex-1">
                  <h3 class="font-semibold text-lg">{{ achievement.achievementName }}</h3>
                  <p class="text-gray-600 mt-1">{{ achievement.description }}</p>
                  <div class="flex items-center mt-2 text-sm text-gray-500">
                    <span>🏆 {{ achievement.points }} 积分</span>
                    <span class="mx-2">•</span>
                    <span>{{ formatDate(achievement.unlockedAt) }}</span>
                  </div>
                </div>
                <div class="text-3xl">🏆</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 通知消息 -->
    <div 
      v-if="notification.show"
      class="fixed top-4 right-4 z-50 transform transition-all duration-300"
      :class="[
        notification.show ? 'translate-x-0 opacity-100' : 'translate-x-full opacity-0',
        notification.type === 'success' ? 'bg-green-500' : 
        notification.type === 'error' ? 'bg-red-500' : 'bg-blue-500'
      ]"
    >
      <div class="text-white p-4 rounded-lg shadow-lg max-w-sm">
        <div class="flex items-center">
          <span class="text-xl mr-3">
            {{ notification.type === 'success' ? '✅' : 
               notification.type === 'error' ? '❌' : 'ℹ️' }}
          </span>
          <div>
            <div v-if="notification.title" class="font-semibold">{{ notification.title }}</div>
            <div>{{ notification.message }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import PetRenderer from './PetRenderer.vue'
import PetCustomizer from './PetCustomizer.vue'
import MiniGamePlayer from './MiniGamePlayer.vue'

// 响应式数据
const loading = ref(true)
const actionLoading = ref(false)
const pets = ref<any[]>([])
const currentPet = ref<any>(null)
const inventory = ref<any[]>([])
const achievements = ref<any[]>([])
const playerStats = ref<any>(null)
const currentGame = ref<any>(null)

// UI状态
const showCustomizer = ref(false)
const showPetList = ref(false)
const showAchievements = ref(false)
const showSettings = ref(false)
const showAchievementNotification = ref(false)
const latestAchievement = ref<any>(null)

// 通知系统
const notification = reactive({
  show: false,
  type: 'info' as 'success' | 'error' | 'info',
  title: '',
  message: ''
})

const playerId = 'player1' // 简化版，实际项目中应该从用户系统获取

// API基础URL
const API_BASE = '/api/pets'

// 工具函数
const showNotification = (type: 'success' | 'error' | 'info', message: string, title?: string) => {
  notification.type = type
  notification.message = message
  notification.title = title || ''
  notification.show = true
  
  setTimeout(() => {
    notification.show = false
  }, 3000)
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString('zh-CN')
}

// API调用函数
const apiCall = async (url: string, options: RequestInit = {}) => {
  try {
    const response = await fetch(url, {
      headers: {
        'Content-Type': 'application/json',
        ...options.headers
      },
      ...options
    })
    
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`)
    }
    
    const data = await response.json()
    return data
  } catch (error) {
    console.error('API call failed:', error)
    throw error
  }
}

// 加载数据
const loadPlayerPets = async () => {
  try {
    const response = await apiCall(`${API_BASE}/player/${playerId}`)
    pets.value = response.data || []
    
    if (pets.value.length > 0 && !currentPet.value) {
      currentPet.value = pets.value[0]
    }
  } catch (error) {
    console.error('加载宠物失败:', error)
  }
}

const loadPlayerInventory = async () => {
  try {
    const response = await apiCall(`${API_BASE}/player/${playerId}/inventory`)
    inventory.value = response.data || []
  } catch (error) {
    console.error('加载背包失败:', error)
  }
}

const loadPlayerAchievements = async () => {
  try {
    const response = await apiCall(`${API_BASE}/player/${playerId}/achievements`)
    achievements.value = response.data || []
  } catch (error) {
    console.error('加载成就失败:', error)
  }
}

const loadPlayerStats = async () => {
  try {
    const response = await apiCall(`${API_BASE}/player/${playerId}/stats`)
    playerStats.value = response.data
  } catch (error) {
    console.error('加载统计失败:', error)
  }
}

const updateCurrentPet = async () => {
  if (!currentPet.value) return
  
  try {
    const response = await apiCall(`${API_BASE}/${currentPet.value.petId}`)
    if (response.success) {
      currentPet.value = response.data
      
      // 更新pets数组中的对应宠物
      const index = pets.value.findIndex(p => p.petId === currentPet.value.petId)
      if (index !== -1) {
        pets.value[index] = currentPet.value
      }
    }
  } catch (error) {
    console.error('更新宠物状态失败:', error)
  }
}

// 宠物操作
const feedPet = async () => {
  if (!currentPet.value || actionLoading.value) return
  
  // 查找食物
  const food = inventory.value.find(item => 
    item.itemId === 'BASIC_FOOD' && item.quantity > 0
  )
  
  if (!food) {
    showNotification('error', '没有食物了！')
    return
  }
  
  actionLoading.value = true
  try {
    const response = await apiCall(`${API_BASE}/${currentPet.value.petId}/feed`, {
      method: 'POST',
      body: JSON.stringify({ itemId: 'BASIC_FOOD' })
    })
    
    if (response.success) {
      currentPet.value = response.data
      showNotification('success', '喂食成功！宠物很开心~')
      await loadPlayerInventory()
    } else {
      showNotification('error', response.message)
    }
  } catch (error) {
    showNotification('error', '喂食失败')
  } finally {
    actionLoading.value = false
  }
}

const cleanPet = async () => {
  if (!currentPet.value || actionLoading.value) return
  
  actionLoading.value = true
  try {
    const response = await apiCall(`${API_BASE}/${currentPet.value.petId}/clean`, {
      method: 'POST'
    })
    
    if (response.success) {
      currentPet.value = response.data
      showNotification('success', '清洁完成！宠物变得干净了~')
    } else {
      showNotification('error', response.message)
    }
  } catch (error) {
    showNotification('error', '清洁失败')
  } finally {
    actionLoading.value = false
  }
}

const restPet = async () => {
  if (!currentPet.value || actionLoading.value) return
  
  actionLoading.value = true
  try {
    const response = await apiCall(`${API_BASE}/${currentPet.value.petId}/rest`, {
      method: 'POST'
    })
    
    if (response.success) {
      currentPet.value = response.data
      showNotification('success', '休息完成！宠物恢复了体力~')
    } else {
      showNotification('error', response.message)
    }
  } catch (error) {
    showNotification('error', '休息失败')
  } finally {
    actionLoading.value = false
  }
}

const playWithPet = async (activityType: string) => {
  if (!currentPet.value || actionLoading.value) return
  
  actionLoading.value = true
  try {
    const response = await apiCall(`${API_BASE}/${currentPet.value.petId}/play`, {
      method: 'POST',
      body: JSON.stringify({ activityType })
    })
    
    if (response.success) {
      currentPet.value = response.data
      const activityNames: Record<string, string> = {
        'FETCH': '抛接游戏',
        'PUZZLE': '益智游戏',
        'CUDDLE': '拥抱',
        'EXERCISE': '运动训练'
      }
      showNotification('success', `${activityNames[activityType]}完成！宠物很开心~`)
      
      // 检查是否有新成就
      await checkNewAchievements()
    } else {
      showNotification('error', response.message)
    }
  } catch (error) {
    showNotification('error', '玩耍失败')
  } finally {
    actionLoading.value = false
  }
}

const useItem = async (itemId: string) => {
  if (itemId === 'BASIC_FOOD') {
    await feedPet()
  } else {
    showNotification('info', '该物品暂无特殊用途')
  }
}

// 小游戏
const startMiniGame = async (gameType: string) => {
  if (!currentPet.value || actionLoading.value || currentGame.value) return
  
  actionLoading.value = true
  try {
    const response = await apiCall(`${API_BASE}/${currentPet.value.petId}/minigames/start`, {
      method: 'POST',
      body: JSON.stringify({ gameType })
    })
    
    if (response.success) {
      currentGame.value = response.data
      showNotification('success', '游戏开始！')
    } else {
      showNotification('error', response.message)
    }
  } catch (error) {
    showNotification('error', '启动游戏失败')
  } finally {
    actionLoading.value = false
  }
}

const handleGameComplete = async (gameType: string, score: number) => {
  if (!currentPet.value) return
  
  try {
    const response = await apiCall(`${API_BASE}/${currentPet.value.petId}/minigames/complete`, {
      method: 'POST',
      body: JSON.stringify({ gameType, score })
    })
    
    if (response.success) {
      currentPet.value = response.data
      currentGame.value = null
      showNotification('success', `游戏完成！得分：${score}`)
      
      // 检查是否有新成就
      await checkNewAchievements()
    } else {
      showNotification('error', response.message)
    }
  } catch (error) {
    showNotification('error', '完成游戏失败')
  }
}

// 宠物管理
const selectPet = (pet: any) => {
  currentPet.value = pet
  showPetList.value = false
}

const handlePetCreated = async (petData: any) => {
  try {
    const response = await apiCall(`${API_BASE}/create`, {
      method: 'POST',
      body: JSON.stringify(petData)
    })
    
    if (response.success) {
      showCustomizer.value = false
      showNotification('success', '宠物创建成功！', '欢迎新伙伴!')
      await loadPlayerPets()
      await loadPlayerInventory()
      await loadPlayerStats()
    } else {
      showNotification('error', response.message)
    }
  } catch (error) {
    showNotification('error', '创建宠物失败')
  }
}

const handlePetClick = () => {
  if (currentPet.value.stats.energy > 10) {
    playWithPet('CUDDLE')
  } else {
    showNotification('info', '宠物太累了，让它休息一下吧~')
  }
}

// 成就检查
const checkNewAchievements = async () => {
  const oldAchievements = [...achievements.value]
  await loadPlayerAchievements()
  
  // 查找新解锁的成就
  const newAchievements = achievements.value.filter(newAch => 
    !oldAchievements.some(oldAch => oldAch.achievementId === newAch.achievementId)
  )
  
  if (newAchievements.length > 0) {
    latestAchievement.value = newAchievements[0]
    showAchievementNotification.value = true
    
    setTimeout(() => {
      showAchievementNotification.value = false
    }, 5000)
    
    await loadPlayerStats()
  }
}

// 初始化
onMounted(async () => {
  try {
    await Promise.all([
      loadPlayerPets(),
      loadPlayerInventory(),
      loadPlayerAchievements(),
      loadPlayerStats()
    ])
  } catch (error) {
    console.error('初始化失败:', error)
  } finally {
    loading.value = false
  }
  
  // 定期更新宠物状态
  setInterval(updateCurrentPet, 30000) // 每30秒更新一次
})
</script>

<style scoped>
/* 自定义滚动条样式 */
::-webkit-scrollbar {
  width: 6px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* 动画效果 */
@keyframes bounce {
  0%, 20%, 53%, 80%, 100% {
    transform: translate3d(0,0,0);
  }
  40%, 43% {
    transform: translate3d(0,-15px,0);
  }
  70% {
    transform: translate3d(0,-7px,0);
  }
  90% {
    transform: translate3d(0,-2px,0);
  }
}

.bounce {
  animation: bounce 1s ease-in-out;
}

/* 渐变背景动画 */
@keyframes gradient {
  0% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0% 50%;
  }
}

.bg-gradient-animated {
  background: linear-gradient(-45deg, #ee7752, #e73c7e, #23a6d5, #23d5ab);
  background-size: 400% 400%;
  animation: gradient 15s ease infinite;
}

/* 移动端适配 */
@media (max-width: 768px) {
  .grid-cols-1.lg\\:grid-cols-3 {
    grid-template-columns: 1fr;
  }
  
  .grid-cols-2 {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .fixed.bottom-4 {
    bottom: 1rem;
    left: 1rem;
    right: 1rem;
  }
}
</style>
