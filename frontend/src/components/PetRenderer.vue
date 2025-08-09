<template>
  <div class="pet-renderer" :class="{ 'interactive': interactive }">
    <svg 
      :width="size" 
      :height="size" 
      viewBox="0 0 200 200" 
      @click="handleClick"
      @touchstart="handleTouchStart"
      @touchend="handleTouchEnd"
      class="pet-svg"
      :class="animationClass"
    >
      <!-- 阴影 -->
      <defs>
        <filter id="dropshadow">
          <feDropShadow dx="2" dy="4" stdDeviation="3" flood-opacity="0.3"/>
        </filter>
        <filter id="glow">
          <feGaussianBlur stdDeviation="3" result="coloredBlur"/>
          <feMerge> 
            <feMergeNode in="coloredBlur"/>
            <feMergeNode in="SourceGraphic"/>
          </feMerge>
        </filter>
      </defs>

      <!-- 身体（椭圆形） -->
      <ellipse 
        cx="100" 
        cy="130" 
        :rx="bodySize.width" 
        :ry="bodySize.height"
        :fill="appearance.bodyColor.primary"
        :stroke="appearance.bodyColor.secondary"
        stroke-width="2"
        filter="url(#dropshadow)"
        class="pet-body"
      />

      <!-- 身体图案 -->
      <g v-if="appearance.pattern.type !== 'none'">
        <!-- 条纹图案 -->
        <g v-if="appearance.pattern.type === 'stripes'">
          <rect v-for="i in 3" :key="i"
            :x="100 - bodySize.width + i * 15"
            :y="130 - bodySize.height"
            width="8"
            :height="bodySize.height * 2"
            :fill="appearance.pattern.color"
            opacity="0.6"
          />
        </g>
        
        <!-- 斑点图案 -->
        <g v-if="appearance.pattern.type === 'spots'">
          <circle v-for="spot in patternSpots" :key="spot.id"
            :cx="spot.x"
            :cy="spot.y"
            :r="spot.size"
            :fill="appearance.pattern.color"
            opacity="0.7"
          />
        </g>

        <!-- 心形图案 -->
        <g v-if="appearance.pattern.type === 'heart'">
          <path 
            d="M 85,120 C 85,115 90,110 95,110 C 100,110 105,115 105,120 C 105,115 110,110 115,110 C 120,110 125,115 125,120 C 125,130 105,140 105,140 C 105,140 85,130 85,120 Z"
            :fill="appearance.pattern.color"
            opacity="0.8"
          />
        </g>
      </g>

      <!-- 头部 -->
      <circle 
        cx="100" 
        cy="80" 
        :r="headSize"
        :fill="appearance.headColor.primary"
        :stroke="appearance.headColor.secondary"
        stroke-width="2"
        filter="url(#dropshadow)"
        class="pet-head"
      />

      <!-- 耳朵 -->
      <g class="pet-ears">
        <!-- 左耳 -->
        <g v-if="appearance.ears.type === 'pointed'">
          <polygon 
            points="75,65 85,45 95,65"
            :fill="appearance.headColor.primary"
            :stroke="appearance.headColor.secondary"
            stroke-width="1"
          />
          <polygon 
            points="77,63 85,50 93,63"
            :fill="appearance.ears.innerColor"
          />
        </g>
        
        <!-- 右耳 -->
        <g v-if="appearance.ears.type === 'pointed'">
          <polygon 
            points="105,65 115,45 125,65"
            :fill="appearance.headColor.primary"
            :stroke="appearance.headColor.secondary"
            stroke-width="1"
          />
          <polygon 
            points="107,63 115,50 123,63"
            :fill="appearance.ears.innerColor"
          />
        </g>

        <!-- 圆耳朵 -->
        <g v-if="appearance.ears.type === 'round'">
          <circle cx="85" cy="55" r="12" :fill="appearance.headColor.primary" stroke-width="1"/>
          <circle cx="85" cy="55" r="8" :fill="appearance.ears.innerColor"/>
          <circle cx="115" cy="55" r="12" :fill="appearance.headColor.primary" stroke-width="1"/>
          <circle cx="115" cy="55" r="8" :fill="appearance.ears.innerColor"/>
        </g>

        <!-- 垂耳 -->
        <g v-if="appearance.ears.type === 'floppy'">
          <ellipse cx="80" cy="70" rx="8" ry="15" :fill="appearance.headColor.primary"/>
          <ellipse cx="120" cy="70" rx="8" ry="15" :fill="appearance.headColor.primary"/>
        </g>
      </g>

      <!-- 眼睛 -->
      <g class="pet-eyes">
        <!-- 左眼 -->
        <g>
          <ellipse 
            cx="90" 
            cy="75" 
            :rx="appearance.eyes.size" 
            :ry="appearance.eyes.size"
            fill="white"
            stroke="#333"
            stroke-width="1"
          />
          <circle 
            :cx="90 + eyeOffset.x" 
            :cy="75 + eyeOffset.y" 
            :r="appearance.eyes.size - 2"
            :fill="appearance.eyes.color"
          />
          <circle 
            :cx="90 + eyeOffset.x + 1" 
            :cy="75 + eyeOffset.y - 1" 
            r="2"
            fill="white"
            opacity="0.8"
          />
        </g>
        
        <!-- 右眼 -->
        <g>
          <ellipse 
            cx="110" 
            cy="75" 
            :rx="appearance.eyes.size" 
            :ry="appearance.eyes.size"
            fill="white"
            stroke="#333"
            stroke-width="1"
          />
          <circle 
            :cx="110 + eyeOffset.x" 
            :cy="75 + eyeOffset.y" 
            :r="appearance.eyes.size - 2"
            :fill="appearance.eyes.rightColor || appearance.eyes.color"
          />
          <circle 
            :cx="110 + eyeOffset.x + 1" 
            :cy="75 + eyeOffset.y - 1" 
            r="2"
            fill="white"
            opacity="0.8"
          />
        </g>

        <!-- 眼皮（困倦时） -->
        <g v-if="currentMood === 'sleepy'">
          <path d="M 83,72 Q 90,70 97,72" stroke="#333" stroke-width="2" fill="none"/>
          <path d="M 103,72 Q 110,70 117,72" stroke="#333" stroke-width="2" fill="none"/>
        </g>
      </g>

      <!-- 鼻子 -->
      <ellipse 
        cx="100" 
        cy="85" 
        rx="3" 
        ry="2"
        :fill="appearance.nose.color"
        class="pet-nose"
      />

      <!-- 嘴巴 -->
      <g class="pet-mouth">
        <!-- 开心 -->
        <path v-if="currentMood === 'happy'" 
          d="M 92,90 Q 100,96 108,90" 
          stroke="#333" 
          stroke-width="2" 
          fill="none"
        />
        <!-- 普通 -->
        <path v-else-if="currentMood === 'normal'" 
          d="M 96,90 Q 100,92 104,90" 
          stroke="#333" 
          stroke-width="1.5" 
          fill="none"
        />
        <!-- 伤心 -->
        <path v-else-if="currentMood === 'sad'" 
          d="M 92,92 Q 100,88 108,92" 
          stroke="#333" 
          stroke-width="2" 
          fill="none"
        />
        <!-- 生气 -->
        <path v-else-if="currentMood === 'angry'" 
          d="M 95,90 L 105,90" 
          stroke="#333" 
          stroke-width="2"
        />
      </g>

      <!-- 四肢 -->
      <g class="pet-limbs">
        <!-- 前腿 -->
        <ellipse cx="80" cy="155" rx="8" ry="20" :fill="appearance.bodyColor.primary"/>
        <ellipse cx="120" cy="155" rx="8" ry="20" :fill="appearance.bodyColor.primary"/>
        
        <!-- 后腿 -->
        <ellipse cx="85" cy="165" rx="10" ry="18" :fill="appearance.bodyColor.primary"/>
        <ellipse cx="115" cy="165" rx="10" ry="18" :fill="appearance.bodyColor.primary"/>
        
        <!-- 爪子 -->
        <ellipse cx="80" cy="170" rx="6" ry="4" fill="#444"/>
        <ellipse cx="120" cy="170" rx="6" ry="4" fill="#444"/>
        <ellipse cx="85" cy="178" rx="7" ry="5" fill="#444"/>
        <ellipse cx="115" cy="178" rx="7" ry="5" fill="#444"/>
      </g>

      <!-- 尾巴 -->
      <g class="pet-tail" :class="{ 'wagging': isWagging }">
        <path 
          d="M 140,140 Q 155,130 160,115 Q 165,100 155,90"
          :stroke="appearance.bodyColor.primary"
          stroke-width="12"
          fill="none"
          stroke-linecap="round"
        />
      </g>

      <!-- 装饰品 -->
      <g class="pet-accessories">
        <!-- 帽子 -->
        <g v-if="appearance.accessories.hat !== 'none'">
          <!-- 棒球帽 -->
          <g v-if="appearance.accessories.hat === 'cap'">
            <ellipse cx="100" cy="50" rx="25" ry="8" :fill="appearance.accessories.hatColor"/>
            <circle cx="100" cy="45" r="18" :fill="appearance.accessories.hatColor"/>
          </g>
          
          <!-- 贝雷帽 -->
          <g v-if="appearance.accessories.hat === 'beret'">
            <circle cx="100" cy="50" r="20" :fill="appearance.accessories.hatColor"/>
            <circle cx="95" cy="45" r="3" fill="#444"/>
          </g>

          <!-- 王冠 -->
          <g v-if="appearance.accessories.hat === 'crown'">
            <path d="M 75,50 L 80,35 L 90,40 L 100,30 L 110,40 L 120,35 L 125,50 Z" 
                  fill="#FFD700" stroke="#FFA500" stroke-width="1"/>
            <circle cx="85" cy="42" r="2" fill="#FF0000"/>
            <circle cx="100" cy="37" r="3" fill="#00FF00"/>
            <circle cx="115" cy="42" r="2" fill="#0000FF"/>
          </g>
        </g>

        <!-- 项圈 -->
        <g v-if="appearance.accessories.collar !== 'none'">
          <ellipse cx="100" cy="95" rx="18" ry="4" :fill="appearance.accessories.collarColor"/>
          
          <!-- 蝴蝶结 -->
          <g v-if="appearance.accessories.collar === 'bow'">
            <path d="M 95,93 L 90,90 L 90,100 L 95,97 L 105,97 L 110,100 L 110,90 L 105,93 Z" 
                  :fill="appearance.accessories.collarColor"/>
          </g>
          
          <!-- 铃铛 -->
          <g v-if="appearance.accessories.collar === 'bell'">
            <circle cx="100" cy="100" r="4" fill="#FFD700"/>
            <path d="M 98,102 L 102,102" stroke="#333" stroke-width="1"/>
          </g>
        </g>

        <!-- 眼镜 -->
        <g v-if="appearance.accessories.glasses !== 'none'">
          <!-- 圆框眼镜 -->
          <g v-if="appearance.accessories.glasses === 'round'">
            <circle cx="90" cy="75" r="12" fill="none" stroke="#333" stroke-width="2"/>
            <circle cx="110" cy="75" r="12" fill="none" stroke="#333" stroke-width="2"/>
            <path d="M 102,75 L 98,75" stroke="#333" stroke-width="2"/>
          </g>
          
          <!-- 墨镜 -->
          <g v-if="appearance.accessories.glasses === 'sunglasses'">
            <ellipse cx="90" cy="75" rx="12" ry="8" fill="#333"/>
            <ellipse cx="110" cy="75" rx="12" ry="8" fill="#333"/>
            <path d="M 102,75 L 98,75" stroke="#333" stroke-width="2"/>
          </g>
        </g>
      </g>

      <!-- 特效层 -->
      <g class="pet-effects">
        <!-- 爱心特效 -->
        <g v-if="showHearts">
          <g v-for="heart in hearts" :key="heart.id" class="floating-heart">
            <path 
              :d="`M ${heart.x},${heart.y} C ${heart.x},${heart.y-5} ${heart.x+10},${heart.y-5} ${heart.x+10},${heart.y} C ${heart.x+10},${heart.y-5} ${heart.x+20},${heart.y-5} ${heart.x+20},${heart.y} C ${heart.x+20},${heart.y+10} ${heart.x+10},${heart.y+15} ${heart.x+10},${heart.y+15} C ${heart.x+10},${heart.y+15} ${heart.x},${heart.y+10} ${heart.x},${heart.y} Z`"
              fill="#FF69B4"
              :opacity="heart.opacity"
              filter="url(#glow)"
            />
          </g>
        </g>

        <!-- 星星特效 -->
        <g v-if="showSparkles">
          <g v-for="sparkle in sparkles" :key="sparkle.id" class="floating-sparkle">
            <path 
              :d="`M ${sparkle.x},${sparkle.y} L ${sparkle.x+4},${sparkle.y+4} L ${sparkle.x+8},${sparkle.y} L ${sparkle.x+4},${sparkle.y-4} Z`"
              fill="#FFD700"
              :opacity="sparkle.opacity"
            />
          </g>
        </g>

        <!-- 汗滴特效 -->
        <g v-if="showSweat">
          <ellipse cx="120" cy="60" rx="3" ry="6" fill="#87CEEB" opacity="0.8"/>
          <ellipse cx="122" cy="55" rx="2" ry="4" fill="#87CEEB" opacity="0.6"/>
        </g>
      </g>
    </svg>

    <!-- 交互提示 -->
    <div v-if="interactive && showHints" class="interaction-hints">
      <div class="hint-bubble">{{ currentHint }}</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'

// Props定义
interface PetAppearance {
  bodyColor: {
    primary: string
    secondary: string
  }
  headColor: {
    primary: string
    secondary: string
  }
  eyes: {
    size: number
    color: string
    rightColor?: string // 异瞳
  }
  ears: {
    type: 'pointed' | 'round' | 'floppy' | 'none'
    innerColor: string
  }
  nose: {
    color: string
  }
  pattern: {
    type: 'none' | 'stripes' | 'spots' | 'heart'
    color: string
  }
  accessories: {
    hat: 'none' | 'cap' | 'beret' | 'crown'
    hatColor: string
    collar: 'none' | 'bow' | 'bell'
    collarColor: string
    glasses: 'none' | 'round' | 'sunglasses'
  }
}

interface Props {
  appearance: PetAppearance
  size?: number
  interactive?: boolean
  mood?: 'happy' | 'normal' | 'sad' | 'angry' | 'sleepy'
  showEffects?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  size: 200,
  interactive: true,
  mood: 'normal',
  showEffects: true
})

// Emits
const emits = defineEmits<{
  petClicked: [event: MouseEvent]
  petTouched: [area: string]
}>()

// 响应式数据
const animationClass = ref('')
const currentMood = ref(props.mood)
const eyeOffset = ref({ x: 0, y: 0 })
const isWagging = ref(false)
const showHearts = ref(false)
const showSparkles = ref(false)
const showSweat = ref(false)
const showHints = ref(false)
const currentHint = ref('')

// 特效数据
const hearts = ref<Array<{id: number, x: number, y: number, opacity: number}>>([])
const sparkles = ref<Array<{id: number, x: number, y: number, opacity: number}>>([])

// 计算属性
const bodySize = computed(() => {
  const base = { width: 35, height: 25 }
  return base
})

const headSize = computed(() => {
  return 28
})

const patternSpots = computed(() => {
  if (props.appearance.pattern.type !== 'spots') return []
  return [
    { id: 1, x: 90, y: 125, size: 4 },
    { id: 2, x: 110, y: 135, size: 3 },
    { id: 3, x: 95, y: 145, size: 5 },
    { id: 4, x: 105, y: 120, size: 3 }
  ]
})

// 动画控制
let animationTimer: number | null = null
let effectTimer: number | null = null

// 方法
const handleClick = (event: MouseEvent) => {
  emits('petClicked', event)
  triggerHappyAnimation()
}

const handleTouchStart = (event: TouchEvent) => {
  const touch = event.touches[0]
  const rect = (event.target as SVGElement).getBoundingClientRect()
  const x = touch.clientX - rect.left
  const y = touch.clientY - rect.top
  
  // 确定触摸区域
  const area = getTouchArea(x, y)
  emits('petTouched', area)
  
  triggerTouchAnimation(area)
}

const handleTouchEnd = () => {
  // 触摸结束处理
}

const getTouchArea = (x: number, y: number): string => {
  const centerX = props.size / 2
  const centerY = props.size / 2
  
  // 根据坐标判断触摸区域
  if (y < centerY * 0.6) return 'head'
  if (y > centerY * 1.4) return 'paws'
  if (x < centerX * 0.7) return 'left-side'
  if (x > centerX * 1.3) return 'right-side'
  return 'body'
}

const triggerHappyAnimation = () => {
  animationClass.value = 'bounce'
  currentMood.value = 'happy'
  isWagging.value = true
  
  if (props.showEffects) {
    showHearts.value = true
    generateHearts()
  }
  
  setTimeout(() => {
    animationClass.value = ''
    currentMood.value = props.mood
    isWagging.value = false
    showHearts.value = false
  }, 2000)
}

const triggerTouchAnimation = (area: string) => {
  switch (area) {
    case 'head':
      showSparkles.value = true
      generateSparkles()
      currentHint.value = '好舒服~'
      break
    case 'body':
      currentMood.value = 'happy'
      currentHint.value = '我喜欢被摸摸~'
      break
    case 'paws':
      animationClass.value = 'shake'
      currentHint.value = '不要摸我的爪子~'
      setTimeout(() => animationClass.value = '', 500)
      break
  }
  
  showHints.value = true
  setTimeout(() => {
    showHints.value = false
    showSparkles.value = false
  }, 1500)
}

const generateHearts = () => {
  hearts.value = []
  for (let i = 0; i < 5; i++) {
    hearts.value.push({
      id: i,
      x: 80 + Math.random() * 40,
      y: 60 + Math.random() * 20,
      opacity: 0.8
    })
  }
  
  // 心形动画
  const heartInterval = setInterval(() => {
    hearts.value.forEach(heart => {
      heart.y -= 2
      heart.opacity -= 0.05
    })
    
    if (hearts.value.length > 0 && hearts.value[0].opacity <= 0) {
      clearInterval(heartInterval)
      hearts.value = []
    }
  }, 100)
}

const generateSparkles = () => {
  sparkles.value = []
  for (let i = 0; i < 8; i++) {
    sparkles.value.push({
      id: i,
      x: 85 + Math.random() * 30,
      y: 70 + Math.random() * 20,
      opacity: 1
    })
  }
  
  // 星星动画
  const sparkleInterval = setInterval(() => {
    sparkles.value.forEach(sparkle => {
      sparkle.opacity -= 0.1
    })
    
    if (sparkles.value.length > 0 && sparkles.value[0].opacity <= 0) {
      clearInterval(sparkleInterval)
      sparkles.value = []
    }
  }, 100)
}

const startIdleAnimation = () => {
  const idleAnimations = () => {
    // 随机眨眼
    if (Math.random() < 0.3) {
      currentMood.value = 'sleepy'
      setTimeout(() => {
        currentMood.value = props.mood
      }, 200)
    }
    
    // 随机看向不同方向
    if (Math.random() < 0.4) {
      eyeOffset.value = {
        x: (Math.random() - 0.5) * 2,
        y: (Math.random() - 0.5) * 2
      }
      setTimeout(() => {
        eyeOffset.value = { x: 0, y: 0 }
      }, 1000)
    }
    
    // 随机摇尾巴
    if (Math.random() < 0.2) {
      isWagging.value = true
      setTimeout(() => {
        isWagging.value = false
      }, 1500)
    }
  }
  
  animationTimer = setInterval(idleAnimations, 3000)
}

// 监听props变化
watch(() => props.mood, (newMood) => {
  currentMood.value = newMood
})

// 生命周期
onMounted(() => {
  if (props.interactive) {
    startIdleAnimation()
  }
})

onUnmounted(() => {
  if (animationTimer) {
    clearInterval(animationTimer)
  }
  if (effectTimer) {
    clearInterval(effectTimer)
  }
})
</script>

<style scoped>
.pet-renderer {
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
}

.pet-svg {
  transition: transform 0.3s ease;
  cursor: pointer;
}

.pet-svg.interactive:hover {
  transform: scale(1.05);
}

.pet-svg.bounce {
  animation: bounce 0.8s ease-in-out;
}

.pet-svg.shake {
  animation: shake 0.5s ease-in-out;
}

@keyframes bounce {
  0%, 20%, 60%, 100% {
    transform: translateY(0);
  }
  40% {
    transform: translateY(-10px);
  }
  80% {
    transform: translateY(-5px);
  }
}

@keyframes shake {
  0%, 100% { transform: translateX(0); }
  25% { transform: translateX(-5px); }
  75% { transform: translateX(5px); }
}

.pet-tail.wagging {
  animation: tailWag 0.8s ease-in-out infinite;
  transform-origin: 140px 140px;
}

@keyframes tailWag {
  0%, 100% { transform: rotate(0deg); }
  50% { transform: rotate(20deg); }
}

.floating-heart {
  animation: floatUp 2s ease-out forwards;
}

.floating-sparkle {
  animation: sparkle 1.5s ease-out forwards;
}

@keyframes floatUp {
  from {
    transform: translateY(0) scale(1);
    opacity: 0.8;
  }
  to {
    transform: translateY(-30px) scale(0.5);
    opacity: 0;
  }
}

@keyframes sparkle {
  0% {
    transform: scale(0) rotate(0deg);
    opacity: 1;
  }
  50% {
    transform: scale(1) rotate(180deg);
    opacity: 0.8;
  }
  100% {
    transform: scale(0) rotate(360deg);
    opacity: 0;
  }
}

.interaction-hints {
  position: absolute;
  top: -40px;
  left: 50%;
  transform: translateX(-50%);
}

.hint-bubble {
  background: rgba(255, 255, 255, 0.9);
  color: #333;
  padding: 8px 12px;
  border-radius: 15px;
  font-size: 14px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  white-space: nowrap;
  animation: fadeInOut 1.5s ease-in-out;
}

@keyframes fadeInOut {
  0%, 100% { opacity: 0; transform: translateY(10px); }
  20%, 80% { opacity: 1; transform: translateY(0); }
}

/* 移动端优化 */
@media (max-width: 768px) {
  .pet-svg {
    touch-action: none;
  }
  
  .hint-bubble {
    font-size: 12px;
    padding: 6px 10px;
  }
}
</style>
