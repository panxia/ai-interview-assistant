<template>
  <div class="ghibli-pet-renderer">
    <svg 
      :width="size" 
      :height="size" 
      viewBox="0 0 240 240" 
      class="pet-svg"
      :style="containerStyle"
    >
      <!-- 定义渐变和滤镜 -->
      <defs>
        <!-- 柔和的身体渐变 -->
        <radialGradient id="bodyGradient" cx="0.35" cy="0.25" r="0.9">
          <stop offset="0%" :stop-color="lightenColor(petData.primaryColor, 35)" />
          <stop offset="40%" :stop-color="lightenColor(petData.primaryColor, 15)" />
          <stop offset="80%" :stop-color="petData.primaryColor" />
          <stop offset="100%" :stop-color="darkenColor(petData.primaryColor, 15)" />
        </radialGradient>
        
        <!-- 肚子渐变 -->
        <radialGradient id="bellyGradient" cx="0.3" cy="0.2" r="0.8">
          <stop offset="0%" :stop-color="lightenColor(petData.secondaryColor, 25)" />
          <stop offset="70%" :stop-color="petData.secondaryColor" />
          <stop offset="100%" :stop-color="darkenColor(petData.secondaryColor, 10)" />
        </radialGradient>
        
        <!-- 柔和阴影 -->
        <filter id="softShadow" x="-50%" y="-50%" width="200%" height="200%">
          <feGaussianBlur in="SourceAlpha" stdDeviation="4"/>
          <feOffset dx="2" dy="6" result="offset"/>
          <feFlood flood-color="rgba(0,0,0,0.15)"/>
          <feComposite in2="offset" operator="in"/>
          <feMerge>
            <feMergeNode/>
            <feMergeNode in="SourceGraphic"/>
          </feMerge>
        </filter>
        
        <!-- 温暖发光 -->
        <filter id="warmGlow" x="-50%" y="-50%" width="200%" height="200%">
          <feGaussianBlur stdDeviation="6" result="coloredBlur"/>
          <feMerge>
            <feMergeNode in="coloredBlur"/>
            <feMergeNode in="SourceGraphic"/>
          </feMerge>
        </filter>
      </defs>
      
      <!-- 背景光晕 -->
      <circle 
        v-if="petData.hasGlow"
        cx="120" 
        cy="120" 
        :r="glowRadius"
        :fill="petData.glowColor || '#FFF8DC'"
        opacity="0.2"
        class="warm-glow"
      />
      
      <!-- 宠物主体 -->
      <g :transform="mainTransform" class="pet-main">
        
        <!-- 身体 -->
        <ellipse 
          cx="120" 
          cy="160"
          :rx="bodyWidth" 
          :ry="bodyHeight"
          fill="url(#bodyGradient)"
          filter="url(#softShadow)"
          class="pet-body"
        />
        
        <!-- 肚子 -->
        <ellipse 
          cx="120" 
          cy="170"
          :rx="bellyWidth" 
          :ry="bellyHeight"
          fill="url(#bellyGradient)"
          class="pet-belly"
        />
        
        <!-- 头部 -->
        <g class="pet-head">
          <!-- 头部主体 - 更圆润 -->
          <circle 
            cx="120" 
            cy="100"
            :r="headRadius"
            fill="url(#bodyGradient)"
            filter="url(#softShadow)"
            class="head-main"
          />
          
          <!-- 脸颊腮红 -->
          <ellipse 
            cx="95" 
            cy="110"
            rx="12" 
            ry="8"
            :fill="lightenColor('#FFB6C1', 20)"
            opacity="0.6"
            class="cheek left"
          />
          <ellipse 
            cx="145" 
            cy="110"
            rx="12" 
            ry="8"
            :fill="lightenColor('#FFB6C1', 20)"
            opacity="0.6"
            class="cheek right"
          />
          
          <!-- 耳朵 - 吉卜力风格圆润 -->
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
              :cx="leftEarX + 2" 
              :cy="leftEarY + 4"
              :rx="earWidth - 5" 
              :ry="earHeight - 5"
              :fill="lightenColor(petData.primaryColor, 30)"
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
              :cx="rightEarX - 2" 
              :cy="rightEarY + 4"
              :rx="earWidth - 5" 
              :ry="earHeight - 5"
              :fill="lightenColor(petData.primaryColor, 30)"
              :transform="rightEarTransform"
              class="ear-inner right"
            />
          </g>
          
          <!-- 眼睛 - 吉卜力大眼睛风格 -->
          <g class="eyes">
            <!-- 左眼 -->
            <ellipse 
              cx="105" 
              cy="95"
              :rx="eyeWidth" 
              :ry="eyeHeight"
              fill="white"
              class="eye-white left"
            />
            <ellipse 
              cx="105" 
              cy="95"
              :rx="eyeWidth - 4" 
              :ry="eyeHeight - 2"
              :fill="petData.eyeColorLeft || '#2E8B57'"
              class="eye-iris left"
            />
            <ellipse 
              cx="108" 
              cy="92"
              rx="3" 
              ry="4"
              fill="black"
              class="eye-pupil left"
            />
            <!-- 高光 - 吉卜力特色 -->
            <ellipse 
              cx="109" 
              cy="90"
              rx="2" 
              ry="2"
              fill="white"
              class="eye-highlight left"
            />
            <ellipse 
              cx="106" 
              cy="94"
              rx="1" 
              ry="1"
              fill="white"
              opacity="0.8"
              class="eye-sparkle left"
            />
            
            <!-- 右眼 -->
            <ellipse 
              cx="135" 
              cy="95"
              :rx="eyeWidth" 
              :ry="eyeHeight"
              fill="white"
              class="eye-white right"
            />
            <ellipse 
              cx="135" 
              cy="95"
              :rx="eyeWidth - 4" 
              :ry="eyeHeight - 2"
              :fill="petData.eyeColorRight || petData.eyeColorLeft || '#2E8B57'"
              class="eye-iris right"
            />
            <ellipse 
              cx="132" 
              cy="92"
              rx="3" 
              ry="4"
              fill="black"
              class="eye-pupil right"
            />
            <!-- 高光 -->
            <ellipse 
              cx="131" 
              cy="90"
              rx="2" 
              ry="2"
              fill="white"
              class="eye-highlight right"
            />
            <ellipse 
              cx="134" 
              cy="94"
              rx="1" 
              ry="1"
              fill="white"
              opacity="0.8"
              class="eye-sparkle right"
            />
          </g>
          
          <!-- 鼻子 - 小巧可爱 -->
          <ellipse 
            cx="120" 
            cy="108"
            rx="4" 
            ry="3"
            :fill="petData.noseColor || '#FFB6C1'"
            class="nose"
          />
          
          <!-- 嘴巴 - 吉卜力风格 -->
          <g class="mouth">
            <path 
              :d="mouthPath"
              stroke="#8B4513"
              stroke-width="2"
              fill="none"
              stroke-linecap="round"
              class="mouth-line"
            />
            <!-- 舌头 -->
            <ellipse 
              v-if="showTongue"
              cx="120" 
              cy="120"
              rx="6" 
              ry="3"
              fill="#FF69B4"
              opacity="0.8"
              class="tongue"
            />
          </g>
        </g>
        
        <!-- 四肢 - 短粗可爱 -->
        <g class="limbs">
          <!-- 前肢 -->
          <ellipse 
            cx="95" 
            cy="180"
            :rx="limbWidth" 
            :ry="limbLength"
            :fill="darkenColor(petData.primaryColor, 5)"
            class="limb front-left"
          />
          <ellipse 
            cx="145" 
            cy="180"
            :rx="limbWidth" 
            :ry="limbLength"
            :fill="darkenColor(petData.primaryColor, 5)"
            class="limb front-right"
          />
          
          <!-- 后肢 -->
          <ellipse 
            cx="90" 
            cy="190"
            :rx="limbWidth - 2" 
            :ry="limbLength - 3"
            :fill="darkenColor(petData.primaryColor, 10)"
            class="limb back-left"
          />
          <ellipse 
            cx="150" 
            cy="190"
            :rx="limbWidth - 2" 
            :ry="limbLength - 3"
            :fill="darkenColor(petData.primaryColor, 10)"
            class="limb back-right"
          />
          
          <!-- 小爪子 -->
          <ellipse cx="95" cy="195" rx="6" ry="4" :fill="lightenColor(petData.primaryColor, 20)" class="paw front-left" />
          <ellipse cx="145" cy="195" rx="6" ry="4" :fill="lightenColor(petData.primaryColor, 20)" class="paw front-right" />
          <ellipse cx="90" cy="205" rx="5" ry="3" :fill="lightenColor(petData.primaryColor, 20)" class="paw back-left" />
          <ellipse cx="150" cy="205" rx="5" ry="3" :fill="lightenColor(petData.primaryColor, 20)" class="paw back-right" />
        </g>
        
        <!-- 尾巴 - 蓬松可爱 -->
        <g v-if="showTail" class="tail">
          <ellipse 
            :cx="tailX" 
            :cy="tailY"
            :rx="tailWidth" 
            :ry="tailLength"
            :fill="petData.primaryColor"
            :transform="tailTransform"
            class="tail-main"
          />
          <!-- 尾巴尖端 -->
          <ellipse 
            :cx="tailTipX" 
            :cy="tailTipY"
            rx="8" 
            ry="8"
            :fill="lightenColor(petData.primaryColor, 25)"
            class="tail-tip"
          />
        </g>
        
        <!-- 图案装饰 - 吉卜力风格 -->
        <g v-if="petData.pattern && petData.pattern !== 'none'" class="patterns">
          <!-- 斑点图案 -->
          <g v-if="petData.pattern === 'spots'">
            <circle cx="100" cy="85" r="6" :fill="petData.patternColor || '#8B4513'" opacity="0.4" />
            <circle cx="140" cy="80" r="4" :fill="petData.patternColor || '#8B4513'" opacity="0.4" />
            <circle cx="110" cy="150" r="8" :fill="petData.patternColor || '#8B4513'" opacity="0.4" />
            <circle cx="130" cy="160" r="5" :fill="petData.patternColor || '#8B4513'" opacity="0.4" />
          </g>
          
          <!-- 条纹图案 -->
          <g v-if="petData.pattern === 'stripes'">
            <ellipse cx="120" cy="140" rx="25" ry="3" :fill="petData.patternColor || '#8B4513'" opacity="0.3" />
            <ellipse cx="120" cy="155" rx="20" ry="3" :fill="petData.patternColor || '#8B4513'" opacity="0.3" />
            <ellipse cx="120" cy="170" rx="15" ry="3" :fill="petData.patternColor || '#8B4513'" opacity="0.3" />
          </g>
        </g>
      </g>
      
      <!-- 装饰配件 - 吉卜力风格 -->
      <g class="accessories">
        <!-- 帽子 -->
        <g v-if="petData.hat && petData.hat !== 'none'" class="hat">
          <!-- 草帽 -->
          <g v-if="petData.hat === 'cap'">
            <ellipse cx="120" cy="65" rx="35" ry="8" fill="#D2691E" />
            <ellipse cx="120" cy="60" rx="25" ry="18" fill="#DEB887" />
            <ellipse cx="120" cy="58" rx="20" ry="15" fill="#F5DEB3" />
          </g>
          
          <!-- 花冠 -->
          <g v-if="petData.hat === 'crown'">
            <ellipse cx="120" cy="65" rx="30" ry="6" fill="#228B22" />
            <circle cx="105" cy="60" r="4" fill="#FF69B4" />
            <circle cx="120" cy="58" r="5" fill="#FFD700" />
            <circle cx="135" cy="60" r="4" fill="#FF6347" />
          </g>
          
          <!-- 蝴蝶结 -->
          <g v-if="petData.hat === 'bow'">
            <ellipse cx="105" cy="65" rx="12" ry="8" fill="#FF69B4" />
            <ellipse cx="135" cy="65" rx="12" ry="8" fill="#FF69B4" />
            <rect x="115" y="60" width="10" height="10" fill="#E91E63" rx="2" />
          </g>
        </g>
        
        <!-- 眼镜 - 圆框 -->
        <g v-if="petData.glasses && petData.glasses !== 'none'" class="glasses">
          <g v-if="petData.glasses === 'normal'">
            <circle cx="105" cy="95" r="15" fill="none" stroke="#8B4513" stroke-width="3" />
            <circle cx="135" cy="95" r="15" fill="none" stroke="#8B4513" stroke-width="3" />
            <line x1="120" y1="95" x2="120" y2="95" stroke="#8B4513" stroke-width="3" />
          </g>
          
          <g v-if="petData.glasses === 'sun'">
            <circle cx="105" cy="95" r="15" fill="#333" opacity="0.7" />
            <circle cx="135" cy="95" r="15" fill="#333" opacity="0.7" />
            <line x1="120" y1="95" x2="120" y2="95" stroke="#333" stroke-width="3" />
          </g>
        </g>
        
        <!-- 项圈 - 温馨风格 -->
        <g v-if="petData.collar && petData.collar !== 'none'" class="collar">
          <ellipse cx="120" cy="135" rx="35" ry="6" fill="#8B4513" />
          <ellipse cx="120" cy="135" rx="33" ry="4" fill="#D2691E" />
          
          <!-- 铃铛 -->
          <g v-if="petData.collar === 'bell'">
            <circle cx="120" cy="145" r="5" fill="#FFD700" />
            <circle cx="120" cy="145" r="3" fill="#FFA500" />
            <line x1="120" y1="142" x2="120" y2="148" stroke="#B8860B" stroke-width="1" />
          </g>
        </g>
      </g>
      
      <!-- 特效粒子 - 温暖风格 -->
      <g v-if="petData.hasSparkles" class="sparkles">
        <g v-for="(sparkle, index) in sparkles" :key="index" class="sparkle" :style="sparkle.style">
          <circle :cx="sparkle.x" :cy="sparkle.y" :r="sparkle.size" :fill="sparkle.color" opacity="0.8" />
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
  size: 240,
  animated: true
})

const sparkles = ref<Array<{
  x: number
  y: number
  size: number
  color: string
  style: any
}>>([])

// 计算属性
const containerStyle = computed(() => ({
  filter: props.petData.hasGlow ? 'url(#warmGlow)' : 'none',
  transform: `scale(${props.petData.size}) rotate(${props.petData.rotation}deg)`,
  transition: 'all 0.4s cubic-bezier(0.4, 0.0, 0.2, 1)'
}))

const mainTransform = computed(() => {
  const scale = props.petData.size || 1
  return `scale(${scale})`
})

// 身体比例 - 吉卜力风格更圆润
const bodyWidth = computed(() => {
  const bases = {
    'CAT': 40,
    'DOG': 38,
    'RABBIT': 35,
    'HAMSTER': 45,
    'PANDA': 42
  }
  return (bases[props.petData.petType as keyof typeof bases] || 40) * (props.petData.size || 1)
})

const bodyHeight = computed(() => {
  const bases = {
    'CAT': 25,
    'DOG': 28,
    'RABBIT': 22,
    'HAMSTER': 30,
    'PANDA': 28
  }
  return (bases[props.petData.petType as keyof typeof bases] || 25) * (props.petData.size || 1)
})

const bellyWidth = computed(() => bodyWidth.value * 0.6)
const bellyHeight = computed(() => bodyHeight.value * 0.7)

// 头部 - 圆形更可爱
const headRadius = computed(() => {
  const bases = {
    'CAT': 32,
    'DOG': 30,
    'RABBIT': 28,
    'HAMSTER': 35,
    'PANDA': 33
  }
  return (bases[props.petData.petType as keyof typeof bases] || 32) * (props.petData.size || 1)
})

// 耳朵 - 吉卜力风格
const earWidth = computed(() => {
  const bases = {
    'CAT': 12,
    'DOG': 15,
    'RABBIT': 8,
    'HAMSTER': 10,
    'PANDA': 14
  }
  return (bases[props.petData.petType as keyof typeof bases] || 12) * (props.petData.size || 1)
})

const earHeight = computed(() => {
  const bases = {
    'CAT': 18,
    'DOG': 20,
    'RABBIT': 25,
    'HAMSTER': 12,
    'PANDA': 16
  }
  return (bases[props.petData.petType as keyof typeof bases] || 18) * (props.petData.size || 1)
})

const leftEarX = computed(() => 95)
const leftEarY = computed(() => {
  const bases = {
    'RABBIT': 55,
    'DOG': 70,
    'CAT': 72,
    'HAMSTER': 75,
    'PANDA': 70
  }
  return bases[props.petData.petType as keyof typeof bases] || 72
})

const rightEarX = computed(() => 145)
const rightEarY = computed(() => leftEarY.value)

const leftEarTransform = computed(() => {
  const angles = {
    'CAT': 'rotate(-25 95 72)',
    'DOG': 'rotate(-35 95 70)',
    'RABBIT': 'rotate(-15 95 55)',
    'HAMSTER': 'rotate(-20 95 75)',
    'PANDA': 'rotate(-15 95 70)'
  }
  return angles[props.petData.petType as keyof typeof angles] || 'rotate(-25 95 72)'
})

const rightEarTransform = computed(() => {
  const angles = {
    'CAT': 'rotate(25 145 72)',
    'DOG': 'rotate(35 145 70)',
    'RABBIT': 'rotate(15 145 55)',
    'HAMSTER': 'rotate(20 145 75)',
    'PANDA': 'rotate(15 145 70)'
  }
  return angles[props.petData.petType as keyof typeof angles] || 'rotate(25 145 72)'
})

// 眼睛 - 吉卜力大眼睛
const eyeWidth = computed(() => {
  const expressions = {
    'happy': 12,
    'excited': 15,
    'cute': 14,
    'sleepy': 8,
    'playful': 13,
    'cool': 10
  }
  return expressions[props.petData.expression as keyof typeof expressions] || 12
})

const eyeHeight = computed(() => {
  const expressions = {
    'happy': 16,
    'excited': 18,
    'cute': 17,
    'sleepy': 6,
    'playful': 16,
    'cool': 12
  }
  return expressions[props.petData.expression as keyof typeof expressions] || 16
})

// 嘴巴 - 吉卜力风格
const mouthPath = computed(() => {
  const expressions = {
    'happy': 'M 110 115 Q 120 125 130 115',
    'excited': 'M 108 112 Q 120 128 132 112',
    'cute': 'M 115 115 Q 120 120 125 115',
    'sleepy': 'M 117 118 Q 120 116 123 118',
    'playful': 'M 105 115 Q 120 125 135 115',
    'cool': 'M 115 118 L 125 118'
  }
  return expressions[props.petData.expression as keyof typeof expressions] || 'M 110 115 Q 120 125 130 115'
})

const showTongue = computed(() => {
  return props.petData.expression === 'excited' || props.petData.expression === 'playful'
})

// 四肢 - 短粗可爱
const limbWidth = computed(() => 12 * (props.petData.size || 1))
const limbLength = computed(() => 18 * (props.petData.size || 1))

// 尾巴
const showTail = computed(() => {
  return props.petData.petType !== 'HAMSTER'
})

const tailX = computed(() => 160)
const tailY = computed(() => 170)
const tailWidth = computed(() => 10 * (props.petData.size || 1))
const tailLength = computed(() => {
  const bases = {
    'CAT': 30,
    'DOG': 25,
    'RABBIT': 12,
    'PANDA': 15
  }
  return (bases[props.petData.petType as keyof typeof bases] || 25) * (props.petData.size || 1)
})

const tailTransform = computed(() => {
  const angles = {
    'CAT': 'rotate(45 160 170)',
    'DOG': 'rotate(35 160 170)',
    'RABBIT': 'rotate(0 160 170)',
    'PANDA': 'rotate(25 160 170)'
  }
  return angles[props.petData.petType as keyof typeof angles] || 'rotate(35 160 170)'
})

const tailTipX = computed(() => {
  const positions = {
    'CAT': 180,
    'DOG': 175,
    'RABBIT': 160,
    'PANDA': 170
  }
  return positions[props.petData.petType as keyof typeof positions] || 175
})

const tailTipY = computed(() => {
  const positions = {
    'CAT': 155,
    'DOG': 160,
    'RABBIT': 170,
    'PANDA': 165
  }
  return positions[props.petData.petType as keyof typeof positions] || 160
})

const glowRadius = computed(() => 90 * (props.petData.size || 1))

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

// 生成温暖的闪光粒子
const generateSparkles = () => {
  sparkles.value = []
  for (let i = 0; i < 6; i++) {
    const x = Math.random() * 200 + 20
    const y = Math.random() * 200 + 20
    const size = Math.random() * 2 + 1
    const colors = ['#FFD700', '#FFA500', '#FF69B4', '#98FB98', '#87CEEB']
    const color = colors[Math.floor(Math.random() * colors.length)]
    
    sparkles.value.push({
      x,
      y,
      size,
      color,
      style: {
        animation: `gentleFloat ${Math.random() * 3 + 2}s ease-in-out infinite`,
        animationDelay: `${Math.random() * 2}s`
      }
    })
  }
}

// 监听闪光效果
watch(() => props.petData.hasSparkles, (newVal) => {
  if (newVal) {
    generateSparkles()
    setInterval(generateSparkles, 4000)
  }
}, { immediate: true })

onMounted(() => {
  if (props.petData.hasSparkles) {
    generateSparkles()
  }
})
</script>

<style scoped>
.ghibli-pet-renderer {
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

.warm-glow {
  animation: warmPulse 3s ease-in-out infinite alternate;
}

.pet-body, .head-main {
  transition: all 0.4s cubic-bezier(0.4, 0.0, 0.2, 1);
}

.ear {
  transition: transform 0.4s cubic-bezier(0.4, 0.0, 0.2, 1);
}

.eye-white, .eye-iris, .eye-pupil {
  transition: all 0.4s ease;
}

.cheek {
  animation: blush 4s ease-in-out infinite;
}

.limb {
  animation: gentleSway 4s ease-in-out infinite;
}

.paw {
  animation: gentleSway 4s ease-in-out infinite;
  animation-delay: 0.2s;
}

.tail-main {
  animation: gentleTailWag 3s ease-in-out infinite;
  transform-origin: center;
}

.tail-tip {
  animation: gentleTailWag 3s ease-in-out infinite;
  animation-delay: 0.1s;
}

.sparkle {
  animation: gentleFloat 2s ease-in-out infinite;
}

/* 吉卜力风格动画 */
@keyframes warmPulse {
  0% { opacity: 0.15; transform: scale(0.95); }
  100% { opacity: 0.25; transform: scale(1.05); }
}

@keyframes blush {
  0%, 100% { opacity: 0.4; }
  50% { opacity: 0.7; }
}

@keyframes gentleSway {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  25% { transform: translateY(-1px) rotate(1deg); }
  75% { transform: translateY(1px) rotate(-1deg); }
}

@keyframes gentleTailWag {
  0%, 100% { transform: rotate(0deg); }
  33% { transform: rotate(8deg); }
  66% { transform: rotate(-5deg); }
}

@keyframes gentleFloat {
  0%, 100% { opacity: 0.6; transform: translateY(0) scale(1); }
  50% { opacity: 1; transform: translateY(-8px) scale(1.1); }
}

/* 悬停效果 - 温和的吉卜力风格 */
.pet-svg:hover .pet-main {
  animation: gentleBounce 1s ease-in-out;
}

.pet-svg:hover .ear {
  animation: earTwitch 0.8s ease-in-out;
}

.pet-svg:hover .eye-iris {
  animation: eyeSparkle 1s ease-in-out;
}

@keyframes gentleBounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-6px); }
}

@keyframes earTwitch {
  0%, 100% { transform: rotate(0deg); }
  25% { transform: rotate(-3deg); }
  75% { transform: rotate(3deg); }
}

@keyframes eyeSparkle {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.1); }
}
</style>
