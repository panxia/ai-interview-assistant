<template>
  <div class="mini-game-player">
    <div class="flex items-center justify-between mb-4">
      <h3 class="text-xl font-bold">{{ gameTitle }}</h3>
      <button 
        @click="cancelGame"
        class="px-3 py-1 bg-gray-400 text-white rounded-lg hover:bg-gray-500 transition-colors"
      >
        退出游戏
      </button>
    </div>

    <!-- 记忆挑战游戏 -->
    <div v-if="gameData.gameType === 'MEMORY_CHALLENGE'" class="memory-game">
      <div v-if="gameState === 'showing'" class="text-center">
        <p class="text-lg mb-4">记住这个序列：</p>
        <div class="flex justify-center gap-2 mb-4">
          <div 
            v-for="(color, index) in gameData.sequence" 
            :key="index"
            :class="[
              'w-12 h-12 rounded-full border-4 transition-all duration-300',
              getColorClass(color),
              index <= currentShowingIndex ? 'opacity-100 scale-110' : 'opacity-30'
            ]"
          ></div>
        </div>
        <p class="text-sm text-gray-600">正在显示序列...</p>
      </div>

      <div v-else-if="gameState === 'playing'" class="text-center">
        <p class="text-lg mb-4">请按顺序点击：</p>
        <div class="grid grid-cols-5 gap-3 max-w-md mx-auto mb-4">
          <button 
            v-for="color in availableColors" 
            :key="color"
            @click="selectColor(color)"
            :class="[
              'w-12 h-12 rounded-full border-4 transition-all duration-200 hover:scale-110',
              getColorClass(color)
            ]"
          ></button>
        </div>
        <div class="text-sm text-gray-600">
          <p>已选择: {{ playerSequence.length }} / {{ gameData.sequence.length }}</p>
          <div class="flex justify-center gap-1 mt-2">
            <div 
              v-for="(selected, index) in playerSequence"
              :key="index"
              :class="[
                'w-6 h-6 rounded-full border-2',
                getColorClass(selected),
                selected === gameData.sequence[index] ? 'border-green-500' : 'border-red-500'
              ]"
            ></div>
          </div>
        </div>
      </div>

      <div v-else-if="gameState === 'finished'" class="text-center">
        <div class="text-6xl mb-4">{{ gameResult.success ? '🎉' : '😢' }}</div>
        <h3 class="text-xl font-bold mb-2">
          {{ gameResult.success ? '恭喜完成！' : '再试一次吧！' }}
        </h3>
        <p class="text-gray-600 mb-4">得分: {{ score }}</p>
        <button 
          @click="completeGame"
          class="px-6 py-2 bg-purple-500 text-white rounded-lg hover:bg-purple-600 transition-colors"
        >
          确定
        </button>
      </div>
    </div>

    <!-- 反应测试游戏 -->
    <div v-if="gameData.gameType === 'REACTION_TEST'" class="reaction-game">
      <div v-if="gameState === 'waiting'" class="text-center">
        <p class="text-lg mb-4">准备开始反应测试！</p>
        <p class="text-sm text-gray-600 mb-4">点击出现的红色圆圈，避免点击其他颜色</p>
        <button 
          @click="startReactionGame"
          class="px-6 py-3 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition-colors"
        >
          开始游戏
        </button>
      </div>

      <div v-else-if="gameState === 'playing'" class="text-center">
        <div class="mb-4">
          <p class="text-lg">剩余时间: {{ timeRemaining }}s</p>
          <p class="text-sm text-gray-600">正确点击: {{ correctClicks }} / {{ totalClicks }}</p>
        </div>
        
        <div 
          class="relative w-96 h-96 mx-auto bg-gray-100 rounded-lg border-4 border-gray-300 overflow-hidden"
          @click="handleReactionClick"
        >
          <div 
            v-for="target in activeTargets" 
            :key="target.id"
            :class="[
              'absolute w-12 h-12 rounded-full cursor-pointer transition-all duration-200',
              target.isCorrect ? 'bg-red-500 hover:bg-red-600' : 'bg-blue-500 hover:bg-blue-600'
            ]"
            :style="{ 
              left: target.x + 'px', 
              top: target.y + 'px',
              transform: 'translate(-50%, -50%)'
            }"
            @click.stop="clickTarget(target)"
          ></div>
        </div>
      </div>

      <div v-else-if="gameState === 'finished'" class="text-center">
        <div class="text-6xl mb-4">{{ gameResult.success ? '🎯' : '⏰' }}</div>
        <h3 class="text-xl font-bold mb-2">
          {{ gameResult.success ? '反应神速！' : '时间到了！' }}
        </h3>
        <div class="text-gray-600 mb-4">
          <p>总得分: {{ score }}</p>
          <p>准确率: {{ Math.round((correctClicks / Math.max(1, totalClicks)) * 100) }}%</p>
          <p>正确点击: {{ correctClicks }} / {{ totalClicks }}</p>
        </div>
        <button 
          @click="completeGame"
          class="px-6 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition-colors"
        >
          确定
        </button>
      </div>
    </div>

    <!-- 节奏游戏 -->
    <div v-if="gameData.gameType === 'RHYTHM_GAME'" class="rhythm-game">
      <div v-if="gameState === 'waiting'" class="text-center">
        <p class="text-lg mb-4">准备开始节奏游戏！</p>
        <p class="text-sm text-gray-600 mb-4">跟随节拍点击按钮</p>
        <button 
          @click="startRhythmGame"
          class="px-6 py-3 bg-pink-500 text-white rounded-lg hover:bg-pink-600 transition-colors"
        >
          开始游戏
        </button>
      </div>

      <div v-else-if="gameState === 'playing'" class="text-center">
        <div class="mb-4">
          <p class="text-lg">得分: {{ score }}</p>
          <p class="text-sm text-gray-600">连击: {{ combo }}</p>
        </div>
        
        <!-- 节拍轨道 -->
        <div class="relative w-full h-32 bg-gray-100 rounded-lg border-4 border-gray-300 mb-4 overflow-hidden">
          <!-- 判定线 -->
          <div class="absolute left-1/2 top-0 bottom-0 w-1 bg-red-500 transform -translate-x-1/2 z-10"></div>
          
          <!-- 下落的节拍 -->
          <div 
            v-for="beat in activeBeat" 
            :key="beat.id"
            class="absolute w-8 h-8 bg-purple-500 rounded-full border-2 border-purple-700"
            :style="{ 
              left: '50%',
              top: beat.y + 'px',
              transform: 'translateX(-50%)'
            }"
          ></div>
        </div>
        
        <!-- 击打按钮 -->
        <button 
          @click="hitBeat"
          :class="[
            'w-20 h-20 rounded-full text-2xl font-bold transition-all duration-200',
            isHitting ? 'bg-yellow-400 scale-110' : 'bg-pink-500 hover:bg-pink-600',
            'text-white shadow-lg'
          ]"
        >
          HIT
        </button>
        
        <div class="mt-4 text-sm text-gray-600">
          <p>按空格键或点击按钮来击打节拍</p>
        </div>
      </div>

      <div v-else-if="gameState === 'finished'" class="text-center">
        <div class="text-6xl mb-4">{{ gameResult.success ? '🎵' : '🎶' }}</div>
        <h3 class="text-xl font-bold mb-2">
          {{ gameResult.success ? '节奏大师！' : '继续练习！' }}
        </h3>
        <div class="text-gray-600 mb-4">
          <p>最终得分: {{ score }}</p>
          <p>最高连击: {{ maxCombo }}</p>
          <p>命中率: {{ Math.round(hitRate * 100) }}%</p>
        </div>
        <button 
          @click="completeGame"
          class="px-6 py-2 bg-pink-500 text-white rounded-lg hover:bg-pink-600 transition-colors"
        >
          确定
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'

// Props
const props = defineProps<{
  gameData: any
}>()

// Emits
const emits = defineEmits<{
  gameComplete: [gameType: string, score: number]
  gameCancel: []
}>()

// 游戏状态
const gameState = ref<'waiting' | 'showing' | 'playing' | 'finished'>('waiting')
const score = ref(0)
const gameResult = ref<{ success: boolean; message?: string }>({ success: false })

// 记忆游戏相关
const currentShowingIndex = ref(-1)
const playerSequence = ref<string[]>([])
const availableColors = ['red', 'blue', 'green', 'yellow', 'purple']

// 反应游戏相关
const timeRemaining = ref(0)
const correctClicks = ref(0)
const totalClicks = ref(0)
const activeTargets = ref<any[]>([])
const targetIdCounter = ref(0)

// 节奏游戏相关
const combo = ref(0)
const maxCombo = ref(0)
const hitRate = ref(0)
const totalBeats = ref(0)
const hitBeats = ref(0)
const activeBeat = ref<any[]>([])
const beatIdCounter = ref(0)
const isHitting = ref(false)

// 计算属性
const gameTitle = computed(() => {
  const titles: Record<string, string> = {
    'MEMORY_CHALLENGE': '🧠 记忆挑战',
    'REACTION_TEST': '⚡ 反应测试',
    'RHYTHM_GAME': '🎵 节奏游戏'
  }
  return titles[props.gameData.gameType] || '小游戏'
})

// 方法
const getColorClass = (color: string) => {
  const colorClasses: Record<string, string> = {
    'red': 'bg-red-500 border-red-600',
    'blue': 'bg-blue-500 border-blue-600',
    'green': 'bg-green-500 border-green-600',
    'yellow': 'bg-yellow-500 border-yellow-600',
    'purple': 'bg-purple-500 border-purple-600'
  }
  return colorClasses[color] || 'bg-gray-500 border-gray-600'
}

const cancelGame = () => {
  emits('gameCancel')
}

const completeGame = () => {
  emits('gameComplete', props.gameData.gameType, score.value)
}

// 记忆游戏逻辑
const startMemoryGame = async () => {
  if (props.gameData.gameType !== 'MEMORY_CHALLENGE') return
  
  gameState.value = 'showing'
  currentShowingIndex.value = -1
  playerSequence.value = []
  
  // 逐个显示序列
  for (let i = 0; i < props.gameData.sequence.length; i++) {
    currentShowingIndex.value = i
    await new Promise(resolve => setTimeout(resolve, 800))
  }
  
  // 等待一下后开始玩家输入
  await new Promise(resolve => setTimeout(resolve, 500))
  gameState.value = 'playing'
}

const selectColor = (color: string) => {
  if (gameState.value !== 'playing') return
  
  playerSequence.value.push(color)
  
  // 检查是否正确
  const currentIndex = playerSequence.value.length - 1
  const isCorrect = color === props.gameData.sequence[currentIndex]
  
  if (!isCorrect) {
    // 错误，游戏结束
    gameResult.value = { success: false, message: '记忆错误！' }
    score.value = Math.max(0, currentIndex * 10)
    gameState.value = 'finished'
    return
  }
  
  // 检查是否完成
  if (playerSequence.value.length === props.gameData.sequence.length) {
    // 成功完成
    gameResult.value = { success: true, message: '记忆完美！' }
    score.value = props.gameData.sequence.length * 15 + (props.gameData.difficulty || 1) * 5
    gameState.value = 'finished'
  }
}

// 反应游戏逻辑
const startReactionGame = () => {
  if (props.gameData.gameType !== 'REACTION_TEST') return
  
  gameState.value = 'playing'
  timeRemaining.value = props.gameData.timeLimit || 15
  correctClicks.value = 0
  totalClicks.value = 0
  activeTargets.value = []
  
  // 开始计时器
  const timer = setInterval(() => {
    timeRemaining.value--
    if (timeRemaining.value <= 0) {
      clearInterval(timer)
      endReactionGame()
    }
  }, 1000)
  
  // 开始生成目标
  const targetInterval = setInterval(() => {
    if (gameState.value !== 'playing') {
      clearInterval(targetInterval)
      return
    }
    generateTarget()
  }, 1000 + Math.random() * 1000) // 1-2秒间隔
  
  // 清理过期目标
  const cleanupInterval = setInterval(() => {
    if (gameState.value !== 'playing') {
      clearInterval(cleanupInterval)
      return
    }
    activeTargets.value = activeTargets.value.filter(target => 
      Date.now() - target.createdAt < 3000 // 3秒后消失
    )
  }, 500)
}

const generateTarget = () => {
  const target = {
    id: targetIdCounter.value++,
    x: 50 + Math.random() * 290, // 在400px容器内留边距
    y: 50 + Math.random() * 290,
    isCorrect: Math.random() > 0.3, // 70%概率是正确目标
    createdAt: Date.now()
  }
  activeTargets.value.push(target)
}

const clickTarget = (target: any) => {
  totalClicks.value++
  
  if (target.isCorrect) {
    correctClicks.value++
    score.value += 10
  } else {
    score.value = Math.max(0, score.value - 5)
  }
  
  // 移除被点击的目标
  activeTargets.value = activeTargets.value.filter(t => t.id !== target.id)
}

const handleReactionClick = () => {
  // 点击空白区域扣分
  totalClicks.value++
  score.value = Math.max(0, score.value - 2)
}

const endReactionGame = () => {
  gameState.value = 'finished'
  const accuracy = totalClicks.value > 0 ? correctClicks.value / totalClicks.value : 0
  gameResult.value = { 
    success: accuracy >= 0.7 && correctClicks.value >= 5,
    message: accuracy >= 0.7 ? '反应敏捷！' : '需要更多练习！'
  }
}

// 节奏游戏逻辑
const startRhythmGame = () => {
  if (props.gameData.gameType !== 'RHYTHM_GAME') return
  
  gameState.value = 'playing'
  score.value = 0
  combo.value = 0
  maxCombo.value = 0
  totalBeats.value = 0
  hitBeats.value = 0
  activeBeat.value = []
  
  // 开始生成节拍
  const beatInterval = setInterval(() => {
    if (gameState.value !== 'playing') {
      clearInterval(beatInterval)
      return
    }
    generateBeat()
  }, 1500) // 每1.5秒一个节拍
  
  // 更新节拍位置
  const updateInterval = setInterval(() => {
    if (gameState.value !== 'playing') {
      clearInterval(updateInterval)
      return
    }
    updateBeats()
  }, 50)
  
  // 游戏时长限制
  setTimeout(() => {
    if (gameState.value === 'playing') {
      endRhythmGame()
    }
  }, 20000) // 20秒游戏时间
}

const generateBeat = () => {
  const beat = {
    id: beatIdCounter.value++,
    y: -32, // 从顶部开始
    speed: 2,
    createdAt: Date.now()
  }
  activeBeat.value.push(beat)
  totalBeats.value++
}

const updateBeats = () => {
  activeBeat.value = activeBeat.value.map(beat => ({
    ...beat,
    y: beat.y + beat.speed
  })).filter(beat => beat.y < 150) // 移除超出底部的节拍
}

const hitBeat = () => {
  isHitting.value = true
  setTimeout(() => {
    isHitting.value = false
  }, 200)
  
  // 查找在判定区域的节拍（y坐标在50-80之间）
  const hitBeat = activeBeat.value.find(beat => 
    beat.y >= 40 && beat.y <= 90
  )
  
  if (hitBeat) {
    // 成功击中
    hitBeats.value++
    combo.value++
    maxCombo.value = Math.max(maxCombo.value, combo.value)
    
    const baseScore = 10
    const comboBonus = Math.min(combo.value, 10) * 2
    score.value += baseScore + comboBonus
    
    // 移除被击中的节拍
    activeBeat.value = activeBeat.value.filter(beat => beat.id !== hitBeat.id)
  } else {
    // 未击中，重置连击
    combo.value = 0
  }
  
  // 计算命中率
  hitRate.value = totalBeats.value > 0 ? hitBeats.value / totalBeats.value : 0
}

const endRhythmGame = () => {
  gameState.value = 'finished'
  gameResult.value = { 
    success: hitRate.value >= 0.6 && score.value >= 100,
    message: hitRate.value >= 0.6 ? '节奏感很好！' : '继续练习节奏感！'
  }
}

// 键盘事件
const handleKeyPress = (event: KeyboardEvent) => {
  if (event.code === 'Space' && props.gameData.gameType === 'RHYTHM_GAME' && gameState.value === 'playing') {
    event.preventDefault()
    hitBeat()
  }
}

// 生命周期
onMounted(() => {
  document.addEventListener('keydown', handleKeyPress)
  
  // 根据游戏类型自动开始
  if (props.gameData.gameType === 'MEMORY_CHALLENGE') {
    startMemoryGame()
  } else if (props.gameData.gameType === 'REACTION_TEST') {
    gameState.value = 'waiting'
  } else if (props.gameData.gameType === 'RHYTHM_GAME') {
    gameState.value = 'waiting'
  }
})

onUnmounted(() => {
  document.removeEventListener('keydown', handleKeyPress)
})
</script>

<style scoped>
.mini-game-player {
  max-width: 600px;
  margin: 0 auto;
}

/* 动画效果 */
@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.7;
  }
}

.animate-pulse {
  animation: pulse 1s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}

/* 节拍下落动画 */
@keyframes beatFall {
  from {
    transform: translateY(-100px);
  }
  to {
    transform: translateY(150px);
  }
}

.beat-fall {
  animation: beatFall 3s linear;
}

/* 按钮点击效果 */
.hit-button:active {
  transform: scale(0.95);
}

/* 反应游戏目标动画 */
@keyframes targetAppear {
  from {
    opacity: 0;
    transform: scale(0);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.target-appear {
  animation: targetAppear 0.3s ease-out;
}

/* 移动端适配 */
@media (max-width: 768px) {
  .mini-game-player {
    max-width: 100%;
    padding: 0 1rem;
  }
  
  .reaction-game .relative {
    width: 300px;
    height: 300px;
  }
  
  .grid-cols-5 {
    grid-template-columns: repeat(3, 1fr);
  }
}
</style>