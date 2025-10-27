<template>
  <div class="cartoon-pet-renderer">
    <svg 
      :width="size" 
      :height="size" 
      viewBox="0 0 200 200" 
      class="pet-svg"
      :style="containerStyle"
    >
      <!-- 定义渐变和滤镜 -->
      <defs>
        <!-- 主体渐变 -->
        <radialGradient id="bodyGradient" cx="0.3" cy="0.3" r="0.8">
          <stop offset="0%" :stop-color="lightenColor(petData.primaryColor, 20)" />
          <stop offset="70%" :stop-color="petData.primaryColor" />
          <stop offset="100%" :stop-color="darkenColor(petData.primaryColor, 20)" />
        </radialGradient>
        
        <!-- 次要颜色渐变 -->
        <radialGradient id="secondaryGradient" cx="0.3" cy="0.3" r="0.8">
          <stop offset="0%" :stop-color="lightenColor(petData.secondaryColor, 15)" />
          <stop offset="100%" :stop-color="petData.secondaryColor" />
        </radialGradient>
        
        <!-- 发光效果 -->
        <filter id="glow" x="-50%" y="-50%" width="200%" height="200%">
          <feGaussianBlur stdDeviation="3" result="coloredBlur"/>
          <feMerge> 
            <feMergeNode in="coloredBlur"/>
            <feMergeNode in="SourceGraphic"/>
          </feMerge>
        </filter>
        
        <!-- 阴影效果 -->
        <filter id="shadow" x="-20%" y="-20%" width="140%" height="140%">
          <feDropShadow dx="2" dy="4" stdDeviation="3" flood-color="rgba(0,0,0,0.3)"/>
        </filter>
      </defs>
      
      <!-- 背景光环 -->
      <circle 
        v-if="petData.hasGlow"
        cx="100" 
        cy="100" 
        :r="glowRadius"
        :fill="petData.glowColor || '#FFD700'"
        opacity="0.3"
        class="glow-ring"
      />
      
      <!-- 宠物主体 -->
      <g :transform="mainTransform" class="pet-main">
        
        <!-- 身体 -->
        <ellipse 
          cx="100" 
          cy="130"
          :rx="bodyWidth" 
          :ry="bodyHeight"
          fill="url(#bodyGradient)"
          filter="url(#shadow)"
          class="pet-body"
        />
        
        <!-- 肚子 -->
        <ellipse 
          cx="100" 
          cy="140"
          :rx="bellyWidth" 
          :ry="bellyHeight"
          fill="url(#secondaryGradient)"
          class="pet-belly"
        />
        
        <!-- 头部 -->
        <g class="pet-head">
          <!-- 头部主体 -->
          <ellipse 
            cx="100" 
            cy="80"
            :rx="headWidth" 
            :ry="headHeight"
            fill="url(#bodyGradient)"
            filter="url(#shadow)"
            class="head-main"
          />
          
          <!-- 脸颊 -->
          <ellipse 
            v-if="petData.petType === 'CAT' || petData.petType === 'HAMSTER'"
            cx="75" 
            cy="85"
            rx="8" 
            ry="6"
            :fill="lightenColor(petData.primaryColor, 30)"
            opacity="0.8"
            class="cheek left"
          />
          <ellipse 
            v-if="petData.petType === 'CAT' || petData.petType === 'HAMSTER'"
            cx="125" 
            cy="85"
            rx="8" 
            ry="6"
            :fill="lightenColor(petData.primaryColor, 30)"
            opacity="0.8"
            class="cheek right"
          />
          
          <!-- 耳朵 -->
          <g class="ears">
            <!-- 左耳 -->
            <ellipse 
              :cx="leftEarX" 
              :cy="leftEarY"
              :rx="earWidth" 
              :ry="earHeight"
              :fill="petData.primaryColor"
              :transform="leftEarTransform"
              class="ear left-ear"
            />
            <ellipse 
              :cx="leftEarX" 
              :cy="leftEarY + 3"
              :rx="earWidth - 3" 
              :ry="earHeight - 3"
              :fill="lightenColor(petData.primaryColor, 40)"
              :transform="leftEarTransform"
              class="ear-inner left"
            />
            
            <!-- 右耳 -->
            <ellipse 
              :cx="rightEarX" 
              :cy="rightEarY"
              :rx="earWidth" 
              :ry="earHeight"
              :fill="petData.primaryColor"
              :transform="rightEarTransform"
              class="ear right-ear"
            />
            <ellipse 
              :cx="rightEarX" 
              :cy="rightEarY + 3"
              :rx="earWidth - 3" 
              :ry="earHeight - 3"
              :fill="lightenColor(petData.primaryColor, 40)"
              :transform="rightEarTransform"
              class="ear-inner right"
            />
          </g>
          
          <!-- 眼睛 -->
          <g class="eyes">
            <!-- 左眼 -->
            <ellipse 
              cx="88" 
              cy="75"
              :rx="eyeSize" 
              :ry="eyeSize + 2"
              fill="white"
              class="eye-white left"
            />
            <ellipse 
              cx="88" 
              cy="75"
              :rx="eyeSize - 3" 
              :ry="eyeSize - 1"
              :fill="petData.eyeColorLeft || '#4169E1'"
              class="eye-iris left"
            />
            <ellipse 
              cx="90" 
              cy="73"
              rx="2" 
              ry="3"
              fill="black"
              class="eye-pupil left"
            />
            <ellipse 
              cx="91" 
              cy="72"
              rx="1" 
              ry="1"
              fill="white"
              class="eye-highlight left"
            />
            
            <!-- 右眼 -->
            <ellipse 
              cx="112" 
              cy="75"
              :rx="eyeSize" 
              :ry="eyeSize + 2"
              fill="white"
              class="eye-white right"
            />
            <ellipse 
              cx="112" 
              cy="75"
              :rx="eyeSize - 3" 
              :ry="eyeSize - 1"
              :fill="petData.eyeColorRight || petData.eyeColorLeft || '#4169E1'"
              class="eye-iris right"
            />
            <ellipse 
              cx="110" 
              cy="73"
              rx="2" 
              ry="3"
              fill="black"
              class="eye-pupil right"
            />
            <ellipse 
              cx="109" 
              cy="72"
              rx="1" 
              ry="1"
              fill="white"
              class="eye-highlight right"
            />
          </g>
          
          <!-- 鼻子 -->
          <ellipse 
            cx="100" 
            cy="85"
            rx="3" 
            ry="2"
            :fill="petData.noseColor || '#FFB6C1'"
            class="nose"
          />
          
          <!-- 嘴巴 -->
          <g class="mouth">
            <path 
              :d="mouthPath"
              stroke="black"
              stroke-width="2"
              fill="none"
              stroke-linecap="round"
              class="mouth-line"
            />
            <!-- 舌头 (开心表情时显示) -->
            <ellipse 
              v-if="showTongue"
              cx="100" 
              cy="95"
              rx="4" 
              ry="2"
              fill="#FF69B4"
              class="tongue"
            />
          </g>
        </g>
        
        <!-- 四肢 -->
        <g class="limbs">
          <!-- 前肢 -->
          <ellipse 
            cx="80" 
            cy="150"
            :rx="limbWidth" 
            :ry="limbLength"
            :fill="petData.primaryColor"
            class="limb front-left"
          />
          <ellipse 
            cx="120" 
            cy="150"
            :rx="limbWidth" 
            :ry="limbLength"
            :fill="petData.primaryColor"
            class="limb front-right"
          />
          
          <!-- 后肢 -->
          <ellipse 
            cx="75" 
            cy="160"
            :rx="limbWidth - 2" 
            :ry="limbLength - 5"
            :fill="darkenColor(petData.primaryColor, 10)"
            class="limb back-left"
          />
          <ellipse 
            cx="125" 
            cy="160"
            :rx="limbWidth - 2" 
            :ry="limbLength - 5"
            :fill="darkenColor(petData.primaryColor, 10)"
            class="limb back-right"
          />
        </g>
        
        <!-- 尾巴 -->
        <ellipse 
          v-if="showTail"
          :cx="tailX" 
          :cy="tailY"
          :rx="tailWidth" 
          :ry="tailLength"
          :fill="petData.primaryColor"
          :transform="tailTransform"
          class="tail"
        />
        
        <!-- 图案装饰 -->
        <g v-if="petData.pattern && petData.pattern !== 'none'" class="patterns">
          <!-- 斑点图案 -->
          <g v-if="petData.pattern === 'spots'">
            <circle cx="85" cy="70" r="4" :fill="petData.patternColor || '#000'" opacity="0.6" />
            <circle cx="115" cy="65" r="3" :fill="petData.patternColor || '#000'" opacity="0.6" />
            <circle cx="90" cy="125" r="5" :fill="petData.patternColor || '#000'" opacity="0.6" />
            <circle cx="110" cy="135" r="4" :fill="petData.patternColor || '#000'" opacity="0.6" />
          </g>
          
          <!-- 条纹图案 -->
          <g v-if="petData.pattern === 'stripes'">
            <rect x="70" y="120" width="60" height="3" :fill="petData.patternColor || '#000'" opacity="0.6" rx="1.5" />
            <rect x="75" y="130" width="50" height="3" :fill="petData.patternColor || '#000'" opacity="0.6" rx="1.5" />
            <rect x="80" y="140" width="40" height="3" :fill="petData.patternColor || '#000'" opacity="0.6" rx="1.5" />
          </g>
        </g>
      </g>
      
      <!-- 装饰配件 -->
      <g class="accessories">
        <!-- 帽子 -->
        <g v-if="petData.hat && petData.hat !== 'none'" class="hat">
          <!-- 棒球帽 -->
          <g v-if="petData.hat === 'cap'">
            <ellipse cx="100" cy="55" rx="25" ry="8" fill="#FF6B6B" />
            <ellipse cx="100" cy="50" rx="20" ry="15" fill="#FF6B6B" />
            <ellipse cx="85" cy="55" rx="15" ry="5" fill="#E55555" transform="rotate(-20 85 55)" />
          </g>
          
          <!-- 皇冠 -->
          <g v-if="petData.hat === 'crown'">
            <rect x="80" y="45" width="40" height="15" fill="#FFD700" rx="2" />
            <polygon points="85,45 90,35 95,45" fill="#FFD700" />
            <polygon points="95,45 100,30 105,45" fill="#FFD700" />
            <polygon points="105,45 110,35 115,45" fill="#FFD700" />
            <circle cx="100" cy="35" r="3" fill="#FF69B4" />
          </g>
          
          <!-- 蝴蝶结 -->
          <g v-if="petData.hat === 'bow'">
            <ellipse cx="90" cy="50" rx="8" ry="6" fill="#FF69B4" />
            <ellipse cx="110" cy="50" rx="8" ry="6" fill="#FF69B4" />
            <rect x="96" y="46" width="8" height="8" fill="#E91E63" rx="2" />
          </g>
        </g>
        
        <!-- 眼镜 -->
        <g v-if="petData.glasses && petData.glasses !== 'none'" class="glasses">
          <!-- 普通眼镜 -->
          <g v-if="petData.glasses === 'normal'">
            <circle cx="88" cy="75" r="12" fill="none" stroke="#333" stroke-width="2" />
            <circle cx="112" cy="75" r="12" fill="none" stroke="#333" stroke-width="2" />
            <line x1="100" y1="75" x2="100" y2="75" stroke="#333" stroke-width="2" />
          </g>
          
          <!-- 太阳镜 -->
          <g v-if="petData.glasses === 'sun'">
            <circle cx="88" cy="75" r="12" fill="#333" opacity="0.8" />
            <circle cx="112" cy="75" r="12" fill="#333" opacity="0.8" />
            <line x1="100" y1="75" x2="100" y2="75" stroke="#333" stroke-width="2" />
          </g>
        </g>
        
        <!-- 项圈 -->
        <g v-if="petData.collar && petData.collar !== 'none'" class="collar">
          <ellipse cx="100" cy="110" rx="30" ry="5" fill="#8B4513" />
          <ellipse cx="100" cy="110" rx="28" ry="3" fill="#D2691E" />
          
          <!-- 铃铛 -->
          <g v-if="petData.collar === 'bell'">
            <circle cx="100" cy="118" r="4" fill="#FFD700" />
            <circle cx="100" cy="118" r="2" fill="#FFA500" />
          </g>
        </g>
      </g>
      
      <!-- 特效粒子 -->
      <g v-if="petData.hasSparkles" class="sparkles">
        <g v-for="(sparkle, index) in sparkles" :key="index" class="sparkle" :style="sparkle.style">
          <polygon :points="sparkle.points" :fill="sparkle.color" />
        </g>
      </g>
    </svg>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'

interface PetData {
  petType: string
  primaryColor: string
  secondaryColor: string
  eyeColorLeft?: string
  eyeColorRight?: string
  noseColor?: string
  expression: string
  size: number
  rotation: number
  hat?: string
  glasses?: string
  collar?: string
  pattern?: string
  patternColor?: string
  hasGlow?: boolean
  glowColor?: string
  hasSparkles?: boolean
}

interface Props {
  petData: PetData
  size?: number
  animated?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  size: 200,
  animated: true
})

const sparkles = ref<Array<{
  points: string
  color: string
  style: any
}>>([])

// 计算属性
const containerStyle = computed(() => ({
  filter: props.petData.hasGlow ? 'url(#glow)' : 'none',
  transform: `scale(${props.petData.size}) rotate(${props.petData.rotation}deg)`,
  transition: 'all 0.3s ease'
}))

const mainTransform = computed(() => {
  const scale = props.petData.size || 1
  return `scale(${scale})`
})

// 身体比例计算
const bodyWidth = computed(() => {
  const base = props.petData.petType === 'HAMSTER' ? 35 : 30
  return base * (props.petData.size || 1)
})

const bodyHeight = computed(() => {
  const base = props.petData.petType === 'HAMSTER' ? 25 : 20
  return base * (props.petData.size || 1)
})

const bellyWidth = computed(() => bodyWidth.value * 0.7)
const bellyHeight = computed(() => bodyHeight.value * 0.6)

// 头部比例
const headWidth = computed(() => {
  const base = props.petData.petType === 'HAMSTER' ? 28 : 25
  return base * (props.petData.size || 1)
})

const headHeight = computed(() => {
  const base = props.petData.petType === 'HAMSTER' ? 25 : 22
  return base * (props.petData.size || 1)
})

// 耳朵计算
const earWidth = computed(() => {
  const bases = {
    'CAT': 8,
    'DOG': 12,
    'RABBIT': 6,
    'HAMSTER': 6,
    'PANDA': 10
  }
  return (bases[props.petData.petType as keyof typeof bases] || 8) * (props.petData.size || 1)
})

const earHeight = computed(() => {
  const bases = {
    'CAT': 12,
    'DOG': 15,
    'RABBIT': 20,
    'HAMSTER': 8,
    'PANDA': 12
  }
  return (bases[props.petData.petType as keyof typeof bases] || 12) * (props.petData.size || 1)
})

const leftEarX = computed(() => 85)
const leftEarY = computed(() => {
  const bases = {
    'RABBIT': 45,
    'DOG': 55,
    'CAT': 58,
    'HAMSTER': 62,
    'PANDA': 58
  }
  return bases[props.petData.petType as keyof typeof bases] || 58
})

const rightEarX = computed(() => 115)
const rightEarY = computed(() => leftEarY.value)

const leftEarTransform = computed(() => {
  const angles = {
    'CAT': 'rotate(-20 85 58)',
    'DOG': 'rotate(-30 85 55)',
    'RABBIT': 'rotate(-10 85 45)',
    'HAMSTER': 'rotate(-15 85 62)',
    'PANDA': 'rotate(-10 85 58)'
  }
  return angles[props.petData.petType as keyof typeof angles] || 'rotate(-20 85 58)'
})

const rightEarTransform = computed(() => {
  const angles = {
    'CAT': 'rotate(20 115 58)',
    'DOG': 'rotate(30 115 55)',
    'RABBIT': 'rotate(10 115 45)',
    'HAMSTER': 'rotate(15 115 62)',
    'PANDA': 'rotate(10 115 58)'
  }
  return angles[props.petData.petType as keyof typeof angles] || 'rotate(20 115 58)'
})

// 眼睛大小
const eyeSize = computed(() => {
  const expressions = {
    'happy': 6,
    'excited': 8,
    'cute': 7,
    'sleepy': 3,
    'playful': 6,
    'cool': 5
  }
  return expressions[props.petData.expression as keyof typeof expressions] || 6
})

// 嘴巴路径
const mouthPath = computed(() => {
  const expressions = {
    'happy': 'M 95 90 Q 100 95 105 90',
    'excited': 'M 92 88 Q 100 98 108 88',
    'cute': 'M 97 90 Q 100 93 103 90',
    'sleepy': 'M 98 92 Q 100 91 102 92',
    'playful': 'M 94 88 Q 100 95 106 90',
    'cool': 'M 97 91 L 103 91'
  }
  return expressions[props.petData.expression as keyof typeof expressions] || 'M 95 90 Q 100 95 105 90'
})

const showTongue = computed(() => {
  return props.petData.expression === 'excited' || props.petData.expression === 'playful'
})

// 四肢
const limbWidth = computed(() => 8 * (props.petData.size || 1))
const limbLength = computed(() => 15 * (props.petData.size || 1))

// 尾巴
const showTail = computed(() => {
  return props.petData.petType !== 'HAMSTER'
})

const tailX = computed(() => 130)
const tailY = computed(() => 140)
const tailWidth = computed(() => 6 * (props.petData.size || 1))
const tailLength = computed(() => {
  const bases = {
    'CAT': 25,
    'DOG': 20,
    'RABBIT': 8,
    'PANDA': 12
  }
  return (bases[props.petData.petType as keyof typeof bases] || 20) * (props.petData.size || 1)
})

const tailTransform = computed(() => {
  const angles = {
    'CAT': 'rotate(45 130 140)',
    'DOG': 'rotate(30 130 140)',
    'RABBIT': 'rotate(0 130 140)',
    'PANDA': 'rotate(20 130 140)'
  }
  return angles[props.petData.petType as keyof typeof angles] || 'rotate(30 130 140)'
})

const glowRadius = computed(() => 80 * (props.petData.size || 1))

// 颜色工具函数
const lightenColor = (color: string, percent: number) => {
  const num = parseInt(color.replace("#", ""), 16)
  const amt = Math.round(2.55 * percent)
  const R = (num >> 16) + amt
  const G = (num >> 8 & 0x00FF) + amt
  const B = (num & 0x0000FF) + amt
  return "#" + (0x1000000 + (R < 255 ? R < 1 ? 0 : R : 255) * 0x10000 +
    (G < 255 ? G < 1 ? 0 : G : 255) * 0x100 +
    (B < 255 ? B < 1 ? 0 : B : 255)).toString(16).slice(1)
}

const darkenColor = (color: string, percent: number) => {
  const num = parseInt(color.replace("#", ""), 16)
  const amt = Math.round(2.55 * percent)
  const R = (num >> 16) - amt
  const G = (num >> 8 & 0x00FF) - amt
  const B = (num & 0x0000FF) - amt
  return "#" + (0x1000000 + (R > 255 ? 255 : R < 0 ? 0 : R) * 0x10000 +
    (G > 255 ? 255 : G < 0 ? 0 : G) * 0x100 +
    (B > 255 ? 255 : B < 0 ? 0 : B)).toString(16).slice(1)
}

// 生成闪闪发光粒子
const generateSparkles = () => {
  sparkles.value = []
  for (let i = 0; i < 8; i++) {
    const x = Math.random() * 200
    const y = Math.random() * 200
    const size = Math.random() * 3 + 2
    const color = ['#FFD700', '#FF69B4', '#00BFFF', '#98FB98'][Math.floor(Math.random() * 4)]
    
    sparkles.value.push({
      points: `${x},${y-size} ${x+size},${y} ${x},${y+size} ${x-size},${y}`,
      color,
      style: {
        animation: `sparkle ${Math.random() * 2 + 1}s ease-in-out infinite`,
        animationDelay: `${Math.random() * 2}s`
      }
    })
  }
}

// 监听闪闪发光效果
watch(() => props.petData.hasSparkles, (newVal) => {
  if (newVal) {
    generateSparkles()
    // 定期更新粒子位置
    setInterval(generateSparkles, 3000)
  }
}, { immediate: true })

onMounted(() => {
  if (props.petData.hasSparkles) {
    generateSparkles()
  }
})
</script>

<style scoped>
.cartoon-pet-renderer {
  @apply flex items-center justify-center;
}

.pet-svg {
  @apply drop-shadow-lg;
  max-width: 100%;
  height: auto;
}

.pet-main {
  transform-origin: center;
}

.glow-ring {
  animation: glow-pulse 2s ease-in-out infinite alternate;
}

.pet-body, .head-main {
  transition: all 0.3s ease;
}

.ear {
  transition: transform 0.3s ease;
}

.eye-white, .eye-iris, .eye-pupil {
  transition: all 0.3s ease;
}

.limb {
  animation: gentle-sway 3s ease-in-out infinite;
}

.tail {
  animation: tail-wag 2s ease-in-out infinite;
  transform-origin: center;
}

.sparkle {
  animation: sparkle 1.5s ease-in-out infinite;
}

/* 动画定义 */
@keyframes glow-pulse {
  0% { opacity: 0.2; transform: scale(0.95); }
  100% { opacity: 0.4; transform: scale(1.05); }
}

@keyframes gentle-sway {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-2px); }
}

@keyframes tail-wag {
  0%, 100% { transform: rotate(0deg); }
  25% { transform: rotate(5deg); }
  75% { transform: rotate(-5deg); }
}

@keyframes sparkle {
  0%, 100% { opacity: 0; transform: scale(0) rotate(0deg); }
  50% { opacity: 1; transform: scale(1) rotate(180deg); }
}

/* 悬停效果 */
.pet-svg:hover .pet-main {
  animation: bounce 0.6s ease-in-out;
}

.pet-svg:hover .ear {
  animation: ear-wiggle 0.5s ease-in-out;
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

@keyframes ear-wiggle {
  0%, 100% { transform: rotate(0deg); }
  25% { transform: rotate(-5deg); }
  75% { transform: rotate(5deg); }
}
</style>
