<template>
  <div class="max-w-4xl mx-auto p-4">
    <!-- 初始化宠物界面 -->
    <div v-if="!pet" class="text-center">
      <div class="bg-white/80 backdrop-blur-sm rounded-3xl p-8 shadow-lg mb-6">
        <h2 class="text-3xl font-bold text-gray-800 mb-4">🏞️ 欢迎来到宠物公园 🏞️</h2>
        <p class="text-gray-600 mb-6">选择你的专属宠物，开始温馨的养成之旅！</p>
        
        <div class="mb-6">
          <input 
            v-model="newPetName" 
            type="text" 
            placeholder="为你的宠物起个名字..."
            class="w-full max-w-sm mx-auto px-4 py-3 border-2 border-pink-200 rounded-2xl text-center text-lg focus:outline-none focus:border-pink-400 bg-white/90"
          />
        </div>

        <div class="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
          <div 
            v-for="petType in petTypes" 
            :key="petType.name"
            @click="selectedPetType = petType.name"
            :class="[
              'cursor-pointer p-4 rounded-2xl border-2 transition-all duration-300 bg-white/90',
              selectedPetType === petType.name 
                ? 'border-pink-400 bg-pink-50 transform scale-105' 
                : 'border-gray-200 hover:border-pink-300 hover:bg-pink-25'
            ]"
          >
            <div class="text-4xl mb-2">{{ petType.emoji }}</div>
            <div class="font-semibold text-gray-800">{{ petType.displayName }}</div>
            <div class="text-xs text-gray-600 mt-1">{{ petType.description }}</div>
          </div>
        </div>

        <button 
          @click="createPet"
          :disabled="!newPetName.trim() || !selectedPetType || creating"
          class="px-8 py-3 bg-gradient-to-r from-pink-500 to-purple-600 text-white font-semibold rounded-2xl hover:from-pink-600 hover:to-purple-700 disabled:opacity-50 disabled:cursor-not-allowed transition-all duration-300 transform hover:scale-105"
        >
          {{ creating ? '创建中...' : '开始养成 🎉' }}
        </button>
      </div>
    </div>

    <!-- 游戏主界面 -->
    <div v-else class="space-y-6">
      <!-- 宠物信息卡片 -->
      <div class="bg-white/80 backdrop-blur-sm rounded-3xl p-6 shadow-lg">
        <div class="flex flex-col md:flex-row items-center gap-6">
          <!-- 宠物显示区域 -->
          <div class="relative">
            <div class="w-48 h-48 bg-gradient-to-br from-blue-100 to-purple-100 rounded-full flex items-center justify-center shadow-inner">
              <div 
                :class="['text-8xl transition-transform duration-500', petAnimationClass]"
                @click="petPet"
              >
                {{ pet.type.emoji }}
              </div>
            </div>
            <div class="absolute -bottom-2 left-1/2 transform -translate-x-1/2 bg-white px-3 py-1 rounded-full shadow-md">
              <span class="text-sm font-semibold text-gray-700">Lv.{{ pet.stats.level }}</span>
            </div>
          </div>

          <!-- 宠物状态 -->
          <div class="flex-1 space-y-4">
            <div class="text-center md:text-left">
              <h2 class="text-2xl font-bold text-gray-800">{{ pet.name }}</h2>
              <p class="text-gray-600">{{ pet.statusDescription }}</p>
              <p class="text-sm text-gray-500">年龄: {{ petAge }}天 | 经验: {{ pet.stats.experience }}/{{ (pet.stats.level) * 100 }}</p>
            </div>

            <!-- 状态条 -->
            <div class="grid grid-cols-2 gap-3">
              <div v-for="(stat, name) in displayStats" :key="name" class="space-y-1">
                <div class="flex justify-between text-sm">
                  <span class="font-medium">{{ stat.name }} {{ stat.emoji }}</span>
                  <span class="text-gray-600">{{ stat.value }}/100</span>
                </div>
                <div class="w-full bg-gray-200 rounded-full h-2">
                  <div 
                    :class="['h-2 rounded-full transition-all duration-500', stat.color]"
                    :style="{ width: `${Math.max(0, Math.min(100, stat.value))}%` }"
                  ></div>
                </div>
              </div>
            </div>
          </div>

          <!-- 金币显示和重新开始按钮 -->
          <div class="space-y-3">
            <div class="bg-yellow-100 px-4 py-2 rounded-2xl shadow-inner">
              <div class="text-center">
                <div class="text-2xl">💰</div>
                <div class="font-bold text-yellow-700">{{ coins }}</div>
                <div class="text-xs text-yellow-600">金币</div>
              </div>
            </div>
            
            <!-- 重新开始按钮 -->
            <button 
              @click="resetGame"
              class="w-full px-3 py-2 bg-gradient-to-r from-gray-400 to-gray-500 text-white text-sm rounded-xl hover:from-gray-500 hover:to-gray-600 transition-all duration-300"
            >
              🔄 重新开始
            </button>
          </div>
        </div>
      </div>

      <!-- 小游戏界面 -->
      <div v-if="activeGameSession" class="bg-white/90 backdrop-blur-sm rounded-3xl p-6 shadow-lg">
        <h3 class="text-xl font-bold text-gray-800 mb-4 text-center">
          🎮 {{ activeGameSession.gameType.displayName }}
        </h3>
        
        <!-- 记忆游戏 -->
        <div v-if="activeGameSession.gameType.name === 'MEMORY'" class="text-center">
          <div v-if="currentSequence" class="mb-4">
            <p class="text-gray-600 mb-2">记住这个序列：</p>
            <div class="flex justify-center gap-2 mb-4">
              <span v-for="emoji in currentSequence" :key="emoji" class="text-2xl">{{ emoji }}</span>
            </div>
            <p class="text-sm text-gray-500">现在请按顺序点击：</p>
            <div class="grid grid-cols-5 gap-2 mt-2">
              <button 
                v-for="emoji in memoryOptions" 
                :key="emoji"
                @click="selectMemoryItem(emoji)"
                class="p-2 text-2xl bg-blue-100 hover:bg-blue-200 rounded-lg transition-colors"
              >
                {{ emoji }}
              </button>
            </div>
          </div>
        </div>

        <!-- 猜谜游戏 -->
        <div v-if="activeGameSession.gameType.name === 'PUZZLE'" class="text-center">
          <div v-if="currentQuestion" class="mb-4">
            <p class="text-lg font-medium mb-4">{{ currentQuestion.question }}</p>
            <input 
              v-model="puzzleAnswer"
              @keyup.enter="submitPuzzleAnswer"
              type="text" 
              placeholder="请输入答案..."
              class="w-full max-w-sm mx-auto px-4 py-2 border-2 border-blue-200 rounded-xl text-center focus:outline-none focus:border-blue-400"
            />
            <button 
              @click="submitPuzzleAnswer"
              class="mt-2 px-4 py-2 bg-blue-500 text-white rounded-xl hover:bg-blue-600 transition-colors"
            >
              提交答案
            </button>
          </div>
        </div>

        <!-- 拍拍游戏 -->
        <div v-if="activeGameSession.gameType.name === 'TAP'" class="text-center">
          <p class="text-lg font-medium mb-4">快速点击宠物！</p>
          <div 
            @click="tapPet"
            class="w-32 h-32 mx-auto bg-gradient-to-br from-pink-200 to-purple-200 rounded-full flex items-center justify-center cursor-pointer hover:scale-105 transition-transform text-6xl"
          >
            {{ pet.type.emoji }}
          </div>
          <p class="mt-2 text-sm text-gray-600">
            拍击次数: {{ tapCount }} | 剩余时间: {{ timeRemaining }}秒
          </p>
        </div>

        <div class="mt-4 text-center">
          <p class="text-sm text-gray-600">得分: {{ activeGameSession.score }}</p>
          <p class="text-sm text-gray-600">轮次: {{ activeGameSession.currentRound }}/{{ activeGameSession.maxRounds }}</p>
        </div>
      </div>

      <!-- 操作按钮区域 -->
      <div class="grid grid-cols-1 md:grid-cols-4 gap-6">
        <!-- 互动动作 -->
        <div class="bg-white/80 backdrop-blur-sm rounded-2xl p-4 shadow-lg">
          <h3 class="text-lg font-bold text-gray-800 mb-3 text-center">🎮 互动动作</h3>
          <div class="grid grid-cols-2 gap-2">
            <button
              v-for="action in availableActions"
              :key="action.name"
              @click="executeAction(action.name)"
              :disabled="executing"
              class="p-3 bg-gradient-to-r from-blue-400 to-purple-500 text-white rounded-xl hover:from-blue-500 hover:to-purple-600 disabled:opacity-50 disabled:cursor-not-allowed transition-all duration-300 text-sm font-medium"
            >
              {{ action.emoji }} {{ action.displayName }}
            </button>
          </div>
        </div>

        <!-- 物品栏 -->
        <div class="bg-white/80 backdrop-blur-sm rounded-2xl p-4 shadow-lg">
          <h3 class="text-lg font-bold text-gray-800 mb-3 text-center">🎒 我的物品</h3>
          <div class="space-y-2 max-h-48 overflow-y-auto">
            <div 
              v-for="item in inventory" 
              :key="item.id"
              class="flex items-center justify-between p-2 bg-gray-50 rounded-xl hover:bg-gray-100 transition-colors"
            >
              <div class="flex items-center gap-2">
                <span class="text-lg">{{ item.emoji }}</span>
                <span class="text-sm font-medium">{{ item.name }}</span>
              </div>
              <button
                @click="useItem(item.id)"
                :disabled="executing"
                class="px-2 py-1 bg-green-500 text-white text-xs rounded-lg hover:bg-green-600 disabled:opacity-50"
              >
                使用
              </button>
            </div>
            <div v-if="inventory.length === 0" class="text-center text-gray-500 text-sm py-4">
              暂无物品
            </div>
          </div>
        </div>

        <!-- 商店 -->
        <div class="bg-white/80 backdrop-blur-sm rounded-2xl p-4 shadow-lg">
          <h3 class="text-lg font-bold text-gray-800 mb-3 text-center">🏪 宠物商店</h3>
          <div class="space-y-2 max-h-48 overflow-y-auto">
            <div 
              v-for="item in shopItems.slice(0, 6)" 
              :key="item.id"
              class="flex items-center justify-between p-2 bg-gray-50 rounded-xl hover:bg-gray-100 transition-colors"
            >
              <div class="flex items-center gap-2">
                <span class="text-lg">{{ item.emoji }}</span>
                <div>
                  <div class="text-sm font-medium">{{ item.name }}</div>
                  <div class="text-xs text-gray-600">{{ item.cost }}💰</div>
                </div>
              </div>
              <button
                @click="buyItem(item.id)"
                :disabled="executing || coins < item.cost"
                class="px-2 py-1 bg-yellow-500 text-white text-xs rounded-lg hover:bg-yellow-600 disabled:opacity-50"
              >
                购买
              </button>
            </div>
          </div>
        </div>

        <!-- 小游戏 -->
        <div class="bg-white/80 backdrop-blur-sm rounded-2xl p-4 shadow-lg">
          <h3 class="text-lg font-bold text-gray-800 mb-3 text-center">🎮 小游戏</h3>
          <div v-if="!activeGameSession" class="space-y-2">
            <button
              v-for="gameType in gameTypes"
              :key="gameType.name"
              @click="startMiniGame(gameType.name)"
              :disabled="executing || (pet.stats.energy < 20)"
              class="w-full p-2 bg-gradient-to-r from-purple-400 to-pink-500 text-white rounded-xl hover:from-purple-500 hover:to-pink-600 disabled:opacity-50 disabled:cursor-not-allowed transition-all duration-300 text-sm font-medium"
            >
              {{ gameType.emoji }} {{ gameType.displayName }}
            </button>
          </div>
          <div v-else class="text-center">
            <p class="text-sm text-gray-600 mb-2">游戏进行中...</p>
            <p class="text-xs text-gray-500">{{ activeGameSession.gameType.description }}</p>
          </div>
        </div>
      </div>

      <!-- 成就展示区域 -->
      <div class="bg-white/80 backdrop-blur-sm rounded-3xl p-6 shadow-lg">
        <h3 class="text-xl font-bold text-gray-800 mb-4 text-center">🏆 成就系统</h3>
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          <div 
            v-for="achievement in achievements.slice(0, 9)" 
            :key="achievement.id"
            :class="[
              'p-4 rounded-2xl border-2 transition-all duration-300',
              achievement.unlocked 
                ? 'bg-gradient-to-br from-yellow-100 to-orange-100 border-yellow-300' 
                : 'bg-gray-50 border-gray-200'
            ]"
          >
            <div class="text-center">
              <div class="text-3xl mb-2">{{ achievement.emoji }}</div>
              <div class="font-semibold text-gray-800">{{ achievement.name }}</div>
              <div class="text-xs text-gray-600 mb-2">{{ achievement.description }}</div>
              
              <!-- 进度条 -->
              <div class="w-full bg-gray-200 rounded-full h-2 mb-2">
                <div 
                  :class="[
                    'h-2 rounded-full transition-all duration-500',
                    achievement.unlocked ? 'bg-yellow-400' : 'bg-blue-400'
                  ]"
                  :style="{ width: `${Math.min(100, achievement.progressPercentage)}%` }"
                ></div>
              </div>
              
              <div class="text-xs text-gray-600">
                {{ achievement.progressDescription }}
                <span v-if="achievement.unlocked" class="text-green-600 font-semibold ml-1">✓ 已完成</span>
              </div>
              
              <!-- 奖励信息 -->
              <div v-if="achievement.reward" class="text-xs text-gray-500 mt-1">
                奖励: {{ achievement.reward.coins }}💰 {{ achievement.reward.experience }}⭐
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 消息提示 -->
      <div 
        v-if="message" 
        :class="[
          'fixed top-20 left-1/2 transform -translate-x-1/2 px-6 py-3 rounded-2xl shadow-lg z-50 transition-all duration-500',
          messageType === 'success' ? 'bg-green-500' : 'bg-red-500',
          'text-white font-medium'
        ]"
      >
        {{ message }}
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'

// 类型定义
interface PetType {
  name: string
  displayName: string
  emoji: string
  description: string
}

interface PetStats {
  hunger: number
  cleanliness: number
  happiness: number
  energy: number
  health: number
  experience: number
  level: number
}

interface Pet {
  id: string
  name: string
  type: PetType
  stats: PetStats
  color: string
  birthDate: string
  lastInteraction: string
  asleep: boolean
  mood: string
  statusDescription: string
  ageInDays: number
}

interface GameItem {
  id: string
  name: string
  emoji: string
  description: string
  type: string
  cost: number
  rarity: number
}

interface PetAction {
  name: string
  displayName: string
  emoji: string
  description: string
}

// 响应式数据
const apiBase = (import.meta as any).env.VITE_API_BASE || 'http://pet.bwmiao.com/api'
const playerId = ref('player1') // 简化版，实际项目中应该使用用户认证

const pet = ref<Pet | null>(null)
const petTypes = ref<PetType[]>([])
const availableActions = ref<PetAction[]>([])
const inventory = ref<GameItem[]>([])
const shopItems = ref<GameItem[]>([])
const coins = ref(0)

// 小游戏和成就系统
const activeGameSession = ref<any>(null)
const gameTypes = ref<any[]>([])
const achievements = ref<any[]>([])
const currentSequence = ref<string[]>([])
const memoryOptions = ref<string[]>(['🍎', '🍌', '🍇', '🍓', '🥝', '🍑', '🍊', '🥭', '🍍', '🥥'])
const currentQuestion = ref<any>(null)
const puzzleAnswer = ref('')
const tapCount = ref(0)
const timeRemaining = ref(0)

const newPetName = ref('')
const selectedPetType = ref('')
const creating = ref(false)
const executing = ref(false)
const message = ref('')
const messageType = ref<'success' | 'error'>('success')

const petAnimationClass = ref('')

// 计算属性
const petAge = computed(() => pet.value?.ageInDays || 0)

const displayStats = computed(() => {
  if (!pet.value) return {}
  
  const stats = pet.value.stats
  return {
    hunger: {
      name: '饱食度',
      emoji: '🍽️',
      value: stats.hunger,
      color: stats.hunger > 70 ? 'bg-green-400' : stats.hunger > 30 ? 'bg-yellow-400' : 'bg-red-400'
    },
    cleanliness: {
      name: '清洁度',
      emoji: '🛁',
      value: stats.cleanliness,
      color: stats.cleanliness > 70 ? 'bg-blue-400' : stats.cleanliness > 30 ? 'bg-yellow-400' : 'bg-red-400'
    },
    happiness: {
      name: '快乐度',
      emoji: '😊',
      value: stats.happiness,
      color: stats.happiness > 70 ? 'bg-pink-400' : stats.happiness > 30 ? 'bg-yellow-400' : 'bg-gray-400'
    },
    energy: {
      name: '能量值',
      emoji: '⚡',
      value: stats.energy,
      color: stats.energy > 70 ? 'bg-purple-400' : stats.energy > 30 ? 'bg-yellow-400' : 'bg-red-400'
    },
    health: {
      name: '健康值',
      emoji: '❤️',
      value: stats.health,
      color: stats.health > 70 ? 'bg-red-400' : stats.health > 30 ? 'bg-yellow-400' : 'bg-gray-400'
    }
  }
})

// 方法
async function loadPetTypes() {
  try {
    const response = await axios.get(`${apiBase}/pet/types`)
    petTypes.value = response.data.data
  } catch (error) {
    showMessage('加载宠物类型失败', 'error')
  }
}

async function loadPetInfo() {
  try {
    const response = await axios.get(`${apiBase}/pet/${playerId.value}`)
    if (response.data.success) {
      const data = response.data.data
      
      // 处理宠物数据，确保type包含完整信息
      if (data.pet) {
        // 如果type是字符串，转换为完整对象
        if (typeof data.pet.type === 'string') {
          const typeMap: {[key: string]: any} = {
            'CAT': { name: 'CAT', displayName: '小猫咪', emoji: '🐱', description: '可爱的小猫，喜欢独立但也需要关爱' },
            'DOG': { name: 'DOG', displayName: '小狗狗', emoji: '🐶', description: '忠诚的伙伴，活泼好动，需要更多的关注' },
            'RABBIT': { name: 'RABBIT', displayName: '小兔子', emoji: '🐰', description: '温顺的小兔，喜欢安静的环境' },
            'HAMSTER': { name: 'HAMSTER', displayName: '小仓鼠', emoji: '🐹', description: '活泼的小仓鼠，喜欢储存食物' },
            'DRAGON': { name: 'DRAGON', displayName: '小龙', emoji: '🐲', description: '神秘的小龙，成长潜力巨大' },
            'PANDA': { name: 'PANDA', displayName: '小熊猫', emoji: '🐼', description: '憨憨的小熊猫，喜欢吃竹子' },
            'PENGUIN': { name: 'PENGUIN', displayName: '小企鹅', emoji: '🐧', description: '可爱的小企鹅，喜欢凉爽的环境' }
          }
          data.pet.type = typeMap[data.pet.type] || typeMap['CAT']
        }
        pet.value = data.pet
      }
      
      coins.value = data.coins
      
      // 处理动作数据，确保包含displayName和emoji
      if (Array.isArray(data.availableActions)) {
        if (data.availableActions.length > 0 && typeof data.availableActions[0] === 'string') {
          // 如果是字符串数组，转换为完整对象
          const actionMap: {[key: string]: any} = {
            'FEED': { name: 'FEED', displayName: '喂食', emoji: '🍽️', description: '给宠物喂食，增加饱食度' },
            'CLEAN': { name: 'CLEAN', displayName: '清洁', emoji: '🛁', description: '给宠物洗澡，增加清洁度' },
            'PLAY': { name: 'PLAY', displayName: '玩耍', emoji: '🎾', description: '和宠物玩耍，增加快乐度但消耗能量' },
            'SLEEP': { name: 'SLEEP', displayName: '休息', emoji: '💤', description: '让宠物休息，恢复能量' },
            'PET': { name: 'PET', displayName: '抚摸', emoji: '✋', description: '轻柔地抚摸宠物，增加快乐度' },
            'TALK': { name: 'TALK', displayName: '聊天', emoji: '💬', description: '和宠物说话，增加快乐度' },
            'MEDICINE': { name: 'MEDICINE', displayName: '治疗', emoji: '💊', description: '给生病的宠物治疗' },
            'EXERCISE': { name: 'EXERCISE', displayName: '运动', emoji: '🏃', description: '带宠物运动，增加健康度' },
            'FEED_TREAT': { name: 'FEED_TREAT', displayName: '给零食', emoji: '🍪', description: '给宠物特殊零食，大幅增加快乐度' },
            'FEED_MEDICINE': { name: 'FEED_MEDICINE', displayName: '喂药', emoji: '💉', description: '给宠物喂药，恢复健康' }
          }
          availableActions.value = data.availableActions.map((actionName: string) => 
            actionMap[actionName] || { name: actionName, displayName: actionName, emoji: '❓', description: '未知动作' }
          )
        } else {
          // 如果已经是对象数组，直接使用
          availableActions.value = data.availableActions
        }
      }
      
      inventory.value = data.inventory
    }
  } catch (error: any) {
    if (error.response?.status === 404) {
      // 用户没有宠物，保持在创建界面
      pet.value = null
    } else {
      showMessage('加载宠物信息失败', 'error')
    }
  }
}

async function loadShopItems() {
  try {
    const response = await axios.get(`${apiBase}/pet/shop`)
    shopItems.value = response.data.data
  } catch (error) {
    showMessage('加载商店失败', 'error')
  }
}

async function loadGameTypes() {
  try {
    const response = await axios.get(`${apiBase}/pet/minigame/types`)
    const data = response.data.data
    
    // 处理游戏类型数据，确保包含完整信息
    if (Array.isArray(data)) {
      if (data.length > 0 && typeof data[0] === 'string') {
        // 如果是字符串数组，转换为完整对象
        const gameTypeMap: {[key: string]: any} = {
          'MEMORY': { name: 'MEMORY', displayName: '记忆游戏', emoji: '🧠', description: '记住序列并重复', difficulty: 3, maxReward: 15 },
          'REACTION': { name: 'REACTION', displayName: '反应游戏', emoji: '⚡', description: '快速点击出现的目标', difficulty: 2, maxReward: 10 },
          'PUZZLE': { name: 'PUZZLE', displayName: '猜谜游戏', emoji: '🧩', description: '回答简单的问题', difficulty: 4, maxReward: 20 },
          'TAP': { name: 'TAP', displayName: '拍拍游戏', emoji: '👆', description: '连续点击宠物获得分数', difficulty: 1, maxReward: 8 }
        }
        gameTypes.value = data.map((gameTypeName: string) => 
          gameTypeMap[gameTypeName] || { name: gameTypeName, displayName: gameTypeName, emoji: '🎮', description: '未知游戏' }
        )
      } else {
        // 如果已经是对象数组，直接使用
        gameTypes.value = data
      }
    }
  } catch (error) {
    showMessage('加载游戏类型失败', 'error')
  }
}

async function loadAchievements() {
  try {
    const response = await axios.get(`${apiBase}/pet/${playerId.value}/achievements`)
    achievements.value = response.data.data
  } catch (error) {
    showMessage('加载成就失败', 'error')
  }
}

async function checkActiveGameSession() {
  try {
    const response = await axios.get(`${apiBase}/pet/${playerId.value}/minigame/active`)
    if (response.data.success) {
      activeGameSession.value = response.data.data
      processGameSessionData()
    }
  } catch (error: any) {
    if (error.response?.status !== 404) {
      console.log('没有活跃的游戏会话')
    }
  }
}

async function createPet() {
  if (!newPetName.value.trim() || !selectedPetType.value) return
  
  creating.value = true
  try {
    const response = await axios.post(`${apiBase}/pet/create`, {
      playerId: playerId.value,
      petName: newPetName.value.trim(),
      petType: selectedPetType.value
    })
    
    if (response.data.success) {
      showMessage('宠物创建成功！', 'success')
      await loadPetInfo()
    } else {
      showMessage(response.data.message, 'error')
    }
  } catch (error: any) {
    showMessage(error.response?.data?.message || '创建宠物失败', 'error')
  } finally {
    creating.value = false
  }
}

async function executeAction(actionName: string) {
  if (executing.value) return
  
  executing.value = true
  try {
    const response = await axios.post(`${apiBase}/pet/${playerId.value}/action`, {
      action: actionName
    })
    
    if (response.data.success) {
      showMessage(response.data.message, 'success')
      pet.value = response.data.data
      animatePet()
      // 重新加载完整信息
      await loadPetInfo()
    } else {
      showMessage(response.data.message, 'error')
    }
  } catch (error: any) {
    showMessage(error.response?.data?.message || '执行动作失败', 'error')
  } finally {
    executing.value = false
  }
}

async function useItem(itemId: string) {
  if (executing.value) return
  
  executing.value = true
  try {
    const response = await axios.post(`${apiBase}/pet/${playerId.value}/use-item`, {
      itemId: itemId
    })
    
    if (response.data.success) {
      showMessage(response.data.message, 'success')
      pet.value = response.data.data
      animatePet()
      await loadPetInfo()
    } else {
      showMessage(response.data.message, 'error')
    }
  } catch (error: any) {
    showMessage(error.response?.data?.message || '使用物品失败', 'error')
  } finally {
    executing.value = false
  }
}

async function buyItem(itemId: string) {
  if (executing.value) return
  
  executing.value = true
  try {
    const response = await axios.post(`${apiBase}/pet/${playerId.value}/buy-item`, {
      itemId: itemId
    })
    
    if (response.data.success) {
      showMessage(response.data.message, 'success')
      await loadPetInfo()
    } else {
      showMessage(response.data.message, 'error')
    }
  } catch (error: any) {
    showMessage(error.response?.data?.message || '购买失败', 'error')
  } finally {
    executing.value = false
  }
}

function petPet() {
  animatePet()
  executeAction('PET')
}

function animatePet() {
  petAnimationClass.value = 'transform scale-110'
  setTimeout(() => {
    petAnimationClass.value = ''
  }, 300)
}

async function resetGame() {
  if (confirm('确定要重新开始吗？这将删除当前宠物！')) {
    try {
      // 发送删除宠物的请求（如果API支持）
      await axios.delete(`${apiBase}/pet/${playerId.value}`).catch(() => {
        // 忽略删除错误，可能API不支持
      })
      
      // 重置本地状态
      pet.value = null
      coins.value = 0
      availableActions.value = []
      inventory.value = []
      activeGameSession.value = null
      achievements.value = []
      
      showMessage('已重置游戏，请重新选择宠物！', 'success')
    } catch (error) {
      showMessage('重置失败，请刷新页面', 'error')
    }
  }
}

function showMessage(msg: string, type: 'success' | 'error' = 'success') {
  message.value = msg
  messageType.value = type
  setTimeout(() => {
    message.value = ''
  }, 3000)
}

// 小游戏相关方法
async function startMiniGame(gameTypeName: string) {
  if (executing.value) return
  
  executing.value = true
  try {
    const response = await axios.post(`${apiBase}/pet/${playerId.value}/minigame/start`, {
      gameType: gameTypeName
    })
    
    if (response.data.success) {
      activeGameSession.value = response.data.data
      processGameSessionData()
      showMessage(response.data.message, 'success')
    } else {
      showMessage(response.data.message, 'error')
    }
  } catch (error: any) {
    showMessage(error.response?.data?.message || '开始游戏失败', 'error')
  } finally {
    executing.value = false
  }
}

function processGameSessionData() {
  if (!activeGameSession.value) return
  
  const gameData = activeGameSession.value.gameData
  
  switch (activeGameSession.value.gameType.name) {
    case 'MEMORY':
      currentSequence.value = gameData.sequence || []
      break
    case 'PUZZLE':
      currentQuestion.value = gameData.question
      puzzleAnswer.value = ''
      break
    case 'TAP':
      tapCount.value = gameData.taps || 0
      updateTapTimer()
      break
  }
}

async function selectMemoryItem(emoji: string) {
  if (!activeGameSession.value || executing.value) return
  
  executing.value = true
  try {
    const response = await axios.post(`${apiBase}/pet/minigame/${activeGameSession.value.sessionId}/input`, {
      selection: emoji
    })
    
    handleGameResponse(response)
  } catch (error: any) {
    showMessage(error.response?.data?.message || '游戏输入失败', 'error')
  } finally {
    executing.value = false
  }
}

async function submitPuzzleAnswer() {
  if (!activeGameSession.value || !puzzleAnswer.value.trim() || executing.value) return
  
  executing.value = true
  try {
    const response = await axios.post(`${apiBase}/pet/minigame/${activeGameSession.value.sessionId}/input`, {
      answer: puzzleAnswer.value.trim()
    })
    
    handleGameResponse(response)
  } catch (error: any) {
    showMessage(error.response?.data?.message || '提交答案失败', 'error')
  } finally {
    executing.value = false
  }
}

async function tapPet() {
  if (!activeGameSession.value || executing.value) return
  
  executing.value = true
  try {
    const response = await axios.post(`${apiBase}/pet/minigame/${activeGameSession.value.sessionId}/input`, {
      tap: true
    })
    
    tapCount.value++
    handleGameResponse(response)
  } catch (error: any) {
    showMessage(error.response?.data?.message || '拍击失败', 'error')
  } finally {
    executing.value = false
  }
}

function handleGameResponse(response: any) {
  if (response.data.success) {
    activeGameSession.value = response.data.data
    showMessage(response.data.message, 'success')
    
    // 检查游戏是否结束
    if (activeGameSession.value.state === 'GAME_COMPLETE' || activeGameSession.value.state === 'FAILED') {
      setTimeout(async () => {
        activeGameSession.value = null
        await Promise.all([loadPetInfo(), loadAchievements()])
      }, 2000)
    } else {
      processGameSessionData()
    }
  } else {
    showMessage(response.data.message, 'error')
  }
}

function updateTapTimer() {
  if (!activeGameSession.value) return
  
  const gameData = activeGameSession.value.gameData
  const startTime = gameData.startTime
  const timeLimit = gameData.timeLimit
  
  const updateInterval = setInterval(() => {
    const elapsed = (Date.now() - startTime) / 1000
    timeRemaining.value = Math.max(0, timeLimit - Math.floor(elapsed))
    
    if (timeRemaining.value <= 0 || !activeGameSession.value) {
      clearInterval(updateInterval)
    }
  }, 100)
}

// 生命周期
onMounted(async () => {
  await Promise.all([
    loadPetTypes(),
    loadPetInfo(),
    loadShopItems(),
    loadGameTypes(),
    loadAchievements(),
    checkActiveGameSession()
  ])
})
</script>

<style scoped>
/* 添加一些自定义样式 */
.bg-pink-25 {
  background-color: rgb(253, 242, 248);
}
</style>
