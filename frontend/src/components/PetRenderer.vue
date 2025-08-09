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
      <!-- 阴影和特效定义 -->
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
        
        <!-- 动态渐变定义 -->
        <linearGradient id="bodyGradient" x1="0%" y1="0%" x2="100%" y2="100%">
          <stop offset="0%" :stop-color="appearance.colorScheme.primaryColor"/>
          <stop offset="100%" :stop-color="appearance.colorScheme.secondaryColor"/>
        </linearGradient>
        
        <!-- 图案定义 -->
        <pattern v-if="appearance.patternConfig.type === 'stripes'" 
                 id="stripesPattern" patternUnits="userSpaceOnUse" width="10" height="10">
          <rect width="5" height="10" :fill="appearance.patternConfig.color" :opacity="appearance.patternConfig.opacity"/>
        </pattern>
        
        <pattern v-if="appearance.patternConfig.type === 'spots'" 
                 id="spotsPattern" patternUnits="userSpaceOnUse" width="20" height="20">
          <circle cx="10" cy="10" r="4" :fill="appearance.patternConfig.color" :opacity="appearance.patternConfig.opacity"/>
        </pattern>
      </defs>

      <!-- 身体 -->
      <ellipse 
        :cx="position.x" 
        :cy="position.y + 50" 
        :rx="getBodyWidth()" 
        :ry="getBodyHeight()"
        :fill="getBodyFill()"
        :stroke="appearance.colorScheme.secondaryColor"
        stroke-width="2"
        filter="url(#dropshadow)"
        class="pet-body"
        :class="bodyAnimationClass"
      />

      <!-- 身体图案 -->
      <g v-if="appearance.patternConfig.type !== 'none'" class="body-patterns">
        <!-- 条纹图案 -->
        <ellipse v-if="appearance.patternConfig.type === 'stripes'"
          :cx="position.x" 
          :cy="position.y + 50" 
          :rx="getBodyWidth() - 5" 
          :ry="getBodyHeight() - 5"
          fill="url(#stripesPattern)"
          :opacity="appearance.patternConfig.opacity"
        />
        
        <!-- 斑点图案 -->
        <ellipse v-if="appearance.patternConfig.type === 'spots'"
          :cx="position.x" 
          :cy="position.y + 50" 
          :rx="getBodyWidth() - 5" 
          :ry="getBodyHeight() - 5"
          fill="url(#spotsPattern)"
          :opacity="appearance.patternConfig.opacity"
        />
        
        <!-- 心形图案 -->
        <path v-if="appearance.patternConfig.type === 'heart'"
          :d="getHeartPath()"
          :fill="appearance.patternConfig.color"
          :opacity="appearance.patternConfig.opacity"
        />

        <!-- 渐变图案 -->
        <ellipse v-if="appearance.patternConfig.type === 'gradient'"
          :cx="position.x" 
          :cy="position.y + 50" 
          :rx="getBodyWidth() - 5" 
          :ry="getBodyHeight() - 5"
          fill="url(#bodyGradient)"
          :opacity="appearance.patternConfig.opacity"
        />
      </g>

      <!-- 头部 -->
      <g class="pet-head" :class="headAnimationClass">
        <!-- 头部形状 -->
        <path 
          :d="getHeadPath()"
          :fill="appearance.colorScheme.primaryColor"
          :stroke="appearance.colorScheme.secondaryColor"
          stroke-width="2"
          filter="url(#dropshadow)"
        />

        <!-- 耳朵 -->
        <g class="pet-ears" v-if="appearance.bodyParts.earType !== 'none'">
          <!-- 左耳 -->
          <path 
            :d="getLeftEarPath()"
            :fill="appearance.colorScheme.primaryColor"
            :stroke="appearance.colorScheme.secondaryColor"
            stroke-width="1"
          />
          <!-- 右耳 -->
          <path 
            :d="getRightEarPath()"
            :fill="appearance.colorScheme.primaryColor"
            :stroke="appearance.colorScheme.secondaryColor"
            stroke-width="1"
          />
          
          <!-- 耳朵内部 -->
          <path 
            :d="getLeftEarInnerPath()"
            :fill="appearance.colorScheme.secondaryColor"
            opacity="0.7"
          />
          <path 
            :d="getRightEarInnerPath()"
            :fill="appearance.colorScheme.secondaryColor"
            opacity="0.7"
          />
        </g>

        <!-- 眼睛 -->
        <g class="pet-eyes" :class="eyeAnimationClass">
          <!-- 左眼 -->
          <ellipse 
            :cx="position.x - getEyeSpacing()" 
            :cy="position.y - 10" 
            :rx="getEyeSize()" 
            :ry="getEyeSize()"
            fill="white"
          />
          <circle 
            :cx="position.x - getEyeSpacing()" 
            :cy="position.y - 10" 
            :r="getEyeSize() * 0.6"
            :fill="appearance.colorScheme.leftEyeColor"
          />
          <circle 
            :cx="position.x - getEyeSpacing() + 2" 
            :cy="position.y - 12" 
            :r="getEyeSize() * 0.2"
            fill="white"
            class="eye-highlight"
          />
          
          <!-- 右眼 -->
          <ellipse 
            :cx="position.x + getEyeSpacing()" 
            :cy="position.y - 10" 
            :rx="getEyeSize()" 
            :ry="getEyeSize()"
            fill="white"
          />
          <circle 
            :cx="position.x + getEyeSpacing()" 
            :cy="position.y - 10" 
            :r="getEyeSize() * 0.6"
            :fill="appearance.colorScheme.rightEyeColor"
          />
          <circle 
            :cx="position.x + getEyeSpacing() + 2" 
            :cy="position.y - 12" 
            :r="getEyeSize() * 0.2"
            fill="white"
            class="eye-highlight"
          />
        </g>

        <!-- 鼻子 -->
        <ellipse 
          :cx="position.x" 
          :cy="position.y + 5" 
          rx="3" 
          ry="2"
          :fill="appearance.colorScheme.noseColor"
        />

        <!-- 嘴巴 -->
        <path 
          :d="getMouthPath()"
          stroke="#555"
          stroke-width="2"
          fill="none"
          stroke-linecap="round"
        />
      </g>

      <!-- 尾巴 -->
      <g class="pet-tail" v-if="appearance.bodyParts.tailType !== 'none'" :class="tailAnimationClass">
        <path 
          :d="getTailPath()"
          :fill="appearance.colorScheme.primaryColor"
          :stroke="appearance.colorScheme.secondaryColor"
          stroke-width="2"
        />
      </g>

      <!-- 装饰品 -->
      <g class="pet-accessories">
        <!-- 帽子 -->
        <g v-if="appearance.accessoryConfig.hatType !== 'none'">
          <path 
            :d="getHatPath()"
            :fill="appearance.accessoryConfig.accessoryColors.hat"
            stroke="#333"
            stroke-width="1"
          />
        </g>

        <!-- 项圈 -->
        <g v-if="appearance.accessoryConfig.collarType !== 'none'">
          <ellipse 
            :cx="position.x" 
            :cy="position.y + 25" 
            rx="20" 
            ry="5"
            :fill="appearance.accessoryConfig.accessoryColors.collar"
            stroke="#333"
            stroke-width="1"
          />
          
          <!-- 蝴蝶结装饰 -->
          <path v-if="appearance.accessoryConfig.collarType === 'bow_tie'"
            :d="getBowTiePath()"
            :fill="appearance.accessoryConfig.accessoryColors.collar"
            stroke="#333"
            stroke-width="1"
          />
          
          <!-- 铃铛装饰 -->
          <circle v-if="appearance.accessoryConfig.collarType === 'bell'"
            :cx="position.x" 
            :cy="position.y + 30" 
            r="4"
            fill="#FFD700"
            stroke="#333"
            stroke-width="1"
          />
        </g>

        <!-- 眼镜 -->
        <g v-if="appearance.accessoryConfig.glassesType !== 'none'">
          <path 
            :d="getGlassesPath()"
            :fill="appearance.accessoryConfig.accessoryColors.glasses"
            stroke="#333"
            stroke-width="2"
            fill-opacity="0.3"
          />
        </g>
      </g>

      <!-- 特效和情绪表达 -->
      <g class="pet-effects">
        <!-- 快乐的心形特效 -->
        <g v-if="mood === 'happy'" class="happiness-effect">
          <path v-for="heart in happinessHearts" :key="heart.id"
            :d="getSmallHeartPath(heart.x, heart.y)"
            fill="#FF69B4"
            :opacity="heart.opacity"
            class="floating-heart"
          />
        </g>
        
        <!-- 困倦的Z字特效 -->
        <g v-if="mood === 'sleepy'" class="sleepy-effect">
          <text :x="position.x + 30" :y="position.y - 40" 
                font-size="20" fill="#6B73FF" opacity="0.7" class="floating-z">Z</text>
          <text :x="position.x + 40" :y="position.y - 60" 
                font-size="16" fill="#6B73FF" opacity="0.5" class="floating-z">z</text>
          <text :x="position.x + 50" :y="position.y - 80" 
                font-size="12" fill="#6B73FF" opacity="0.3" class="floating-z">z</text>
        </g>
        
        <!-- 生气的蒸汽特效 -->
        <g v-if="mood === 'angry'" class="angry-effect">
          <circle :cx="position.x - 25" :cy="position.y - 50" r="3" fill="#FF4444" opacity="0.8" class="steam"/>
          <circle :cx="position.x + 25" :cy="position.y - 50" r="3" fill="#FF4444" opacity="0.8" class="steam"/>
        </g>
      </g>
    </svg>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'

// Props
interface Props {
  petData?: any
  size?: number
  interactive?: boolean
  animationState?: string
  mood?: string
}

const props = withDefaults(defineProps<Props>(), {
  size: 200,
  interactive: true,
  animationState: 'idle',
  mood: 'neutral'
})

// Reactive data
const position = ref({ x: 100, y: 80 })
const animationFrame = ref(0)
const isBreathing = ref(true)
const happinessHearts = ref<Array<{id: number, x: number, y: number, opacity: number}>>([])

// Computed appearance data
const appearance = computed(() => {
  if (!props.petData?.appearance) {
    // Default appearance
    return {
      bodyParts: {
        headShape: 'round',
        earType: 'pointed',
        eyeType: 'large',
        mouthType: 'small',
        tailType: 'normal'
      },
      colorScheme: {
        primaryColor: '#FF8C00',
        secondaryColor: '#FFA500',
        leftEyeColor: '#0000FF',
        rightEyeColor: '#0000FF',
        noseColor: '#FFB6C1'
      },
      patternConfig: {
        type: 'none',
        color: '#000000',
        opacity: 0.7
      },
      accessoryConfig: {
        hatType: 'none',
        collarType: 'none',
        glassesType: 'none',
        accessoryColors: {
          hat: '#FF0000',
          collar: '#00FF00',
          glasses: '#000000'
        }
      },
      bodyProportions: {
        fatness: 50,
        height: 50,
        headSize: 50,
        eyeSize: 50,
        earSize: 50
      }
    }
  }
  return props.petData.appearance
})

// Animation classes
const animationClass = computed(() => {
  return {
    'idle': props.animationState === 'idle',
    'walking': props.animationState === 'walking',
    'playing': props.animationState === 'playing',
    'sleeping': props.animationState === 'sleeping',
    'eating': props.animationState === 'eating'
  }
})

const bodyAnimationClass = computed(() => {
  if (isBreathing.value) return 'breathing'
  return ''
})

const headAnimationClass = computed(() => {
  if (props.animationState === 'playing') return 'head-bobbing'
  if (props.animationState === 'curious') return 'head-tilting'
  return ''
})

const eyeAnimationClass = computed(() => {
  if (props.mood === 'sleepy') return 'blinking-slow'
  if (props.mood === 'happy') return 'sparkling'
  return 'blinking-normal'
})

const tailAnimationClass = computed(() => {
  if (props.mood === 'happy') return 'wagging-fast'
  if (props.animationState === 'walking') return 'wagging-normal'
  return 'wagging-slow'
})

// Size calculations based on body proportions
const getBodyWidth = () => {
  const base = 30
  const fatnessFactor = appearance.value.bodyProportions.fatness / 50
  return base * fatnessFactor
}

const getBodyHeight = () => {
  const base = 20
  const heightFactor = appearance.value.bodyProportions.height / 50
  const fatnessFactor = appearance.value.bodyProportions.fatness / 100
  return base * heightFactor * (1 + fatnessFactor * 0.3)
}

const getHeadSize = () => {
  const base = 25
  const sizeFactor = appearance.value.bodyProportions.headSize / 50
  return base * sizeFactor
}

const getEyeSize = () => {
  const base = 6
  const sizeFactor = appearance.value.bodyProportions.eyeSize / 50
  return base * sizeFactor
}

const getEyeSpacing = () => {
  return getHeadSize() * 0.4
}

// Dynamic path generation functions
const getBodyFill = () => {
  if (appearance.value.patternConfig.type === 'gradient') {
    return 'url(#bodyGradient)'
  }
  return appearance.value.colorScheme.primaryColor
}

const getHeadPath = () => {
  const size = getHeadSize()
  const x = position.value.x
  const y = position.value.y
  
  switch (appearance.value.bodyParts.headShape) {
    case 'round':
      return `M ${x-size},${y} A ${size},${size} 0 1,1 ${x+size},${y} A ${size},${size} 0 1,1 ${x-size},${y}`
    case 'oval':
      return `M ${x-size},${y} A ${size},${size*1.2} 0 1,1 ${x+size},${y} A ${size},${size*1.2} 0 1,1 ${x-size},${y}`
    case 'square':
      return `M ${x-size},${y-size} L ${x+size},${y-size} L ${x+size},${y+size} L ${x-size},${y+size} Z`
    case 'triangular':
      return `M ${x},${y-size} L ${x+size},${y+size} L ${x-size},${y+size} Z`
    default:
      return `M ${x-size},${y} A ${size},${size} 0 1,1 ${x+size},${y} A ${size},${size} 0 1,1 ${x-size},${y}`
  }
}

const getLeftEarPath = () => {
  const x = position.value.x - 15
  const y = position.value.y - 20
  const earSize = appearance.value.bodyProportions.earSize / 50 * 15
  
  switch (appearance.value.bodyParts.earType) {
    case 'pointed':
      return `M ${x},${y+5} L ${x-5},${y-earSize} L ${x+5},${y-earSize+5} Z`
    case 'round':
      return `M ${x-8},${y} A 8,8 0 1,1 ${x+8},${y} A 8,8 0 1,1 ${x-8},${y}`
    case 'floppy':
      return `M ${x-5},${y-10} Q ${x-15},${y-5} ${x-8},${y+5} Q ${x-3},${y-5} ${x+2},${y-8} Z`
    case 'long':
      return `M ${x-3},${y-5} Q ${x-8},${y-earSize*1.5} ${x-5},${y-earSize*2} Q ${x+2},${y-earSize*1.5} ${x+3},${y-5} Z`
    case 'horn':
      return `M ${x-2},${y+5} L ${x},${y-earSize*1.2} L ${x+2},${y+5} Z`
    default:
      return ''
  }
}

const getRightEarPath = () => {
  const x = position.value.x + 15
  const y = position.value.y - 20
  const earSize = appearance.value.bodyProportions.earSize / 50 * 15
  
  switch (appearance.value.bodyParts.earType) {
    case 'pointed':
      return `M ${x},${y+5} L ${x+5},${y-earSize} L ${x-5},${y-earSize+5} Z`
    case 'round':
      return `M ${x-8},${y} A 8,8 0 1,1 ${x+8},${y} A 8,8 0 1,1 ${x-8},${y}`
    case 'floppy':
      return `M ${x+5},${y-10} Q ${x+15},${y-5} ${x+8},${y+5} Q ${x+3},${y-5} ${x-2},${y-8} Z`
    case 'long':
      return `M ${x+3},${y-5} Q ${x+8},${y-earSize*1.5} ${x+5},${y-earSize*2} Q ${x-2},${y-earSize*1.5} ${x-3},${y-5} Z`
    case 'horn':
      return `M ${x+2},${y+5} L ${x},${y-earSize*1.2} L ${x-2},${y+5} Z`
    default:
      return ''
  }
}

const getLeftEarInnerPath = () => {
  const x = position.value.x - 15
  const y = position.value.y - 20
  
  switch (appearance.value.bodyParts.earType) {
    case 'pointed':
      return `M ${x-1},${y+3} L ${x-3},${y-8} L ${x+3},${y-6} Z`
    case 'round':
      return `M ${x-5},${y} A 5,5 0 1,1 ${x+5},${y} A 5,5 0 1,1 ${x-5},${y}`
    case 'floppy':
      return `M ${x-3},${y-8} Q ${x-10},${y-3} ${x-6},${y+3} Q ${x-1},${y-3} ${x+1},${y-6} Z`
    default:
      return ''
  }
}

const getRightEarInnerPath = () => {
  const x = position.value.x + 15
  const y = position.value.y - 20
  
  switch (appearance.value.bodyParts.earType) {
    case 'pointed':
      return `M ${x+1},${y+3} L ${x+3},${y-8} L ${x-3},${y-6} Z`
    case 'round':
      return `M ${x-5},${y} A 5,5 0 1,1 ${x+5},${y} A 5,5 0 1,1 ${x-5},${y}`
    case 'floppy':
      return `M ${x+3},${y-8} Q ${x+10},${y-3} ${x+6},${y+3} Q ${x+1},${y-3} ${x-1},${y-6} Z`
    default:
      return ''
  }
}

const getMouthPath = () => {
  const x = position.value.x
  const y = position.value.y + 12
  
  switch (appearance.value.bodyParts.mouthType) {
    case 'small':
      return `M ${x-3},${y} Q ${x},${y+3} ${x+3},${y}`
    case 'open':
      return `M ${x-5},${y} Q ${x},${y+5} ${x+5},${y} M ${x-2},${y+2} L ${x+2},${y+2}`
    case 'tiny':
      return `M ${x-1},${y} Q ${x},${y+1} ${x+1},${y}`
    case 'wise':
      return `M ${x-4},${y} Q ${x},${y-2} ${x+4},${y}`
    case 'content':
      return `M ${x-4},${y-1} Q ${x},${y+2} ${x+4},${y-1}`
    case 'beak':
      return `M ${x-3},${y} L ${x},${y+5} L ${x+3},${y} Z`
    default:
      return `M ${x-3},${y} Q ${x},${y+3} ${x+3},${y}`
  }
}

const getTailPath = () => {
  const x = position.value.x + getBodyWidth() + 5
  const y = position.value.y + 40
  
  switch (appearance.value.bodyParts.tailType) {
    case 'normal':
      return `M ${x},${y} Q ${x+15},${y-10} ${x+20},${y+5} Q ${x+15},${y+10} ${x+10},${y+5}`
    case 'fluffy':
      return `M ${x},${y} Q ${x+12},${y-15} ${x+25},${y-5} Q ${x+20},${y+15} ${x+5},${y+10} Q ${x+15},${y+5} ${x},${y}`
    case 'long':
      return `M ${x},${y} Q ${x+20},${y-20} ${x+35},${y-10} Q ${x+40},${y+10} ${x+25},${y+15} Q ${x+10},${y+10} ${x},${y}`
    case 'short':
      return `M ${x},${y} Q ${x+8},${y-5} ${x+10},${y+2} Q ${x+8},${y+5} ${x+5},${y+2}`
    default:
      return `M ${x},${y} Q ${x+15},${y-10} ${x+20},${y+5} Q ${x+15},${y+10} ${x+10},${y+5}`
  }
}

const getHeartPath = () => {
  const x = position.value.x - 10
  const y = position.value.y + 30
  return `M ${x},${y} C ${x},${y-5} ${x-10},${y-5} ${x-10},${y} C ${x-10},${y-5} ${x-20},${y-5} ${x-20},${y} C ${x-20},${y+10} ${x},${y+20} ${x+20},${y+10} C ${x+20},${y} ${x+10},${y-5} ${x+10},${y} C ${x+10},${y-5} ${x},${y-5} ${x},${y} Z`
}

const getSmallHeartPath = (heartX: number, heartY: number) => {
  const size = 4
  return `M ${heartX},${heartY} C ${heartX},${heartY-size/2} ${heartX-size},${heartY-size/2} ${heartX-size},${heartY} C ${heartX-size},${heartY-size/2} ${heartX-size*2},${heartY-size/2} ${heartX-size*2},${heartY} C ${heartX-size*2},${heartY+size} ${heartX},${heartY+size*1.5} ${heartX+size*2},${heartY+size} C ${heartX+size*2},${heartY} ${heartX+size},${heartY-size/2} ${heartX+size},${heartY} C ${heartX+size},${heartY-size/2} ${heartX},${heartY-size/2} ${heartX},${heartY} Z`
}

const getHatPath = () => {
  const x = position.value.x
  const y = position.value.y - getHeadSize() - 10
  
  switch (appearance.value.accessoryConfig.hatType) {
    case 'baseball_cap':
      return `M ${x-20},${y+5} Q ${x},${y-15} ${x+20},${y+5} L ${x+25},${y+10} L ${x-25},${y+10} Z M ${x-30},${y+8} L ${x-15},${y+15} L ${x-10},${y+8}`
    case 'beret':
      return `M ${x-25},${y+10} Q ${x-20},${y-10} ${x},${y-15} Q ${x+20},${y-10} ${x+25},${y+10} Z`
    case 'crown':
      return `M ${x-20},${y+5} L ${x-15},${y-5} L ${x-10},${y+5} L ${x-5},${y-10} L ${x},${y+5} L ${x+5},${y-10} L ${x+10},${y+5} L ${x+15},${y-5} L ${x+20},${y+5} Z`
    case 'bow':
      return `M ${x-15},${y} Q ${x-20},${y-5} ${x-15},${y-10} Q ${x},${y-15} ${x+15},${y-10} Q ${x+20},${y-5} ${x+15},${y} Q ${x},${y+5} ${x-15},${y} M ${x-3},${y-5} L ${x+3},${y-5} L ${x+3},${y-15} L ${x-3},${y-15} Z`
    default:
      return ''
  }
}

const getBowTiePath = () => {
  const x = position.value.x
  const y = position.value.y + 25
  return `M ${x-8},${y-3} Q ${x-12},${y-8} ${x-8},${y-13} Q ${x},${y-18} ${x+8},${y-13} Q ${x+12},${y-8} ${x+8},${y-3} Q ${x},${y+2} ${x-8},${y-3} M ${x-2},${y-8} L ${x+2},${y-8} L ${x+2},${y-18} L ${x-2},${y-18} Z`
}

const getGlassesPath = () => {
  const x = position.value.x
  const y = position.value.y - 5
  const eyeSpacing = getEyeSpacing()
  
  switch (appearance.value.accessoryConfig.glassesType) {
    case 'round_glasses':
      return `M ${x-eyeSpacing-8},${y} A 8,8 0 1,1 ${x-eyeSpacing+8},${y} A 8,8 0 1,1 ${x-eyeSpacing-8},${y} M ${x+eyeSpacing-8},${y} A 8,8 0 1,1 ${x+eyeSpacing+8},${y} A 8,8 0 1,1 ${x+eyeSpacing-8},${y} M ${x-eyeSpacing+8},${y} L ${x+eyeSpacing-8},${y}`
    case 'sunglasses':
      return `M ${x-eyeSpacing-10},${y-5} L ${x-eyeSpacing+10},${y-5} L ${x-eyeSpacing+8},${y+5} L ${x-eyeSpacing-8},${y+5} Z M ${x+eyeSpacing-10},${y-5} L ${x+eyeSpacing+10},${y-5} L ${x+eyeSpacing+8},${y+5} L ${x+eyeSpacing-8},${y+5} Z M ${x-eyeSpacing+10},${y} L ${x+eyeSpacing-10},${y}`
    case 'monocle':
      return `M ${x-eyeSpacing-8},${y} A 8,8 0 1,1 ${x-eyeSpacing+8},${y} A 8,8 0 1,1 ${x-eyeSpacing-8},${y} M ${x-eyeSpacing+8},${y+8} L ${x-eyeSpacing+15},${y+15}`
    default:
      return ''
  }
}

// Event handlers
const handleClick = () => {
  if (props.interactive) {
    // Trigger click animation
    animateClick()
  }
}

const handleTouchStart = (event: TouchEvent) => {
  if (props.interactive) {
    event.preventDefault()
    // Add touch feedback
    document.body.style.setProperty('--vibration', '1')
  }
}

const handleTouchEnd = () => {
  if (props.interactive) {
    // Remove touch feedback
    document.body.style.setProperty('--vibration', '0')
  }
}

const animateClick = () => {
  // Add temporary animation class
  const element = document.querySelector('.pet-svg')
  element?.classList.add('clicked')
  setTimeout(() => {
    element?.classList.remove('clicked')
  }, 300)
}

// Animation loop
let animationInterval: number

const startAnimation = () => {
  animationInterval = setInterval(() => {
    animationFrame.value = (animationFrame.value + 1) % 100
    
    // Update breathing animation
    isBreathing.value = Math.sin(animationFrame.value * 0.1) > 0
    
    // Update happiness hearts
    if (props.mood === 'happy' && Math.random() < 0.1) {
      const heart = {
        id: Date.now(),
        x: position.value.x + (Math.random() - 0.5) * 40,
        y: position.value.y - 30,
        opacity: 1
      }
      happinessHearts.value.push(heart)
      
      // Remove heart after animation
      setTimeout(() => {
        const index = happinessHearts.value.findIndex(h => h.id === heart.id)
        if (index > -1) {
          happinessHearts.value.splice(index, 1)
        }
      }, 2000)
    }
    
    // Update heart opacity
    happinessHearts.value.forEach(heart => {
      heart.opacity -= 0.02
      heart.y -= 1
    })
    
  }, 100)
}

const stopAnimation = () => {
  if (animationInterval) {
    clearInterval(animationInterval)
  }
}

// Lifecycle
onMounted(() => {
  startAnimation()
})

onUnmounted(() => {
  stopAnimation()
})
</script>

<style scoped>
.pet-renderer {
  display: flex;
  justify-content: center;
  align-items: center;
  user-select: none;
}

.pet-svg {
  cursor: pointer;
  transition: transform 0.3s ease;
}

.pet-svg.interactive:hover {
  transform: scale(1.05);
}

.pet-svg.clicked {
  animation: clickBounce 0.3s ease;
}

/* Breathing animation */
.pet-body.breathing {
  animation: breathe 2s ease-in-out infinite;
}

/* Head animations */
.pet-head.head-bobbing {
  animation: headBob 1s ease-in-out infinite;
}

.pet-head.head-tilting {
  animation: headTilt 2s ease-in-out infinite;
}

/* Eye animations */
.pet-eyes.blinking-normal {
  animation: blink 3s ease-in-out infinite;
}

.pet-eyes.blinking-slow {
  animation: slowBlink 4s ease-in-out infinite;
}

.pet-eyes.sparkling .eye-highlight {
  animation: sparkle 1s ease-in-out infinite;
}

/* Tail animations */
.pet-tail.wagging-fast {
  animation: wagFast 0.5s ease-in-out infinite;
}

.pet-tail.wagging-normal {
  animation: wagNormal 1s ease-in-out infinite;
}

.pet-tail.wagging-slow {
  animation: wagSlow 2s ease-in-out infinite;
}

/* Special effects */
.floating-heart {
  animation: floatUp 2s ease-out forwards;
}

.floating-z {
  animation: floatUp 3s ease-out infinite;
}

.steam {
  animation: steamRise 1s ease-out infinite;
}

/* Keyframe animations */
@keyframes clickBounce {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.1); }
}

@keyframes breathe {
  0%, 100% { transform: scaleY(1); }
  50% { transform: scaleY(1.05); }
}

@keyframes headBob {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-3px); }
}

@keyframes headTilt {
  0%, 100% { transform: rotate(0deg); }
  25% { transform: rotate(-5deg); }
  75% { transform: rotate(5deg); }
}

@keyframes blink {
  0%, 90%, 100% { transform: scaleY(1); }
  95% { transform: scaleY(0.1); }
}

@keyframes slowBlink {
  0%, 80%, 100% { transform: scaleY(1); }
  85%, 95% { transform: scaleY(0.1); }
}

@keyframes sparkle {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.5; transform: scale(1.2); }
}

@keyframes wagFast {
  0%, 100% { transform: rotate(-10deg); }
  50% { transform: rotate(10deg); }
}

@keyframes wagNormal {
  0%, 100% { transform: rotate(-5deg); }
  50% { transform: rotate(5deg); }
}

@keyframes wagSlow {
  0%, 100% { transform: rotate(-2deg); }
  50% { transform: rotate(2deg); }
}

@keyframes floatUp {
  0% { 
    opacity: 1; 
    transform: translateY(0) scale(1); 
  }
  100% { 
    opacity: 0; 
    transform: translateY(-50px) scale(0.5); 
  }
}

@keyframes steamRise {
  0% { 
    opacity: 0.8; 
    transform: translateY(0) scale(1); 
  }
  100% { 
    opacity: 0; 
    transform: translateY(-20px) scale(1.5); 
  }
}

/* Mobile touch feedback */
@media (hover: none) and (pointer: coarse) {
  .pet-svg {
    transition: filter 0.2s ease;
  }
  
  .pet-svg:active {
    filter: brightness(1.2);
  }
}
</style>
