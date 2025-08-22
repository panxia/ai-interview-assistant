<template>
  <div class="enhanced-pet-renderer" :style="containerStyle">
    <!-- Lottie动画层 (如果有动画数据) -->
    <div v-if="hasLottieAnimation" class="lottie-layer">
      <Vue3Lottie
        :animationData="currentAnimation"
        :width="size"
        :height="size"
        :loop="true"
        :autoplay="animated"
        :speed="animationSpeed"
        ref="lottieRef"
        class="lottie-animation"
      />
      
      <!-- Lottie控制层 -->
      <div v-if="showControls" class="lottie-controls">
        <div class="control-group">
          <label>动画控制</label>
          <div class="control-buttons">
            <button @click="playAnimation" class="control-btn">▶️</button>
            <button @click="pauseAnimation" class="control-btn">⏸️</button>
            <button @click="stopAnimation" class="control-btn">⏹️</button>
          </div>
        </div>
        
        <div class="control-group">
          <label>速度: {{ animationSpeed }}x</label>
          <input 
            type="range" 
            v-model="animationSpeed" 
            min="0.5" 
            max="2" 
            step="0.1"
            class="speed-slider"
          />
        </div>
      </div>
    </div>
    
    <!-- 后备CSS渲染层 (总是显示，作为基础) -->
    <div class="css-pet-layer" :class="{ 'background-mode': hasLottieAnimation }">
      <div class="pet-stage" :style="stageStyle">
        <div class="pet-character" :style="characterStyle" :class="`pet-${petData.petType.toLowerCase()}`">
          <!-- 身体主体 -->
          <div class="pet-body" :style="bodyStyle">
            <!-- 头部 -->
            <div class="pet-head" :style="headStyle">
              <!-- 耳朵 -->
              <div class="ears">
                <div class="ear left" :style="earStyle"></div>
                <div class="ear right" :style="earStyle"></div>
              </div>
              
              <!-- 眼睛 -->
              <div class="eyes">
                <div class="eye left" :style="eyeStyle">
                  <div class="pupil" :style="pupilStyle"></div>
                  <div class="highlight"></div>
                </div>
                <div class="eye right" :style="eyeStyle">
                  <div class="pupil" :style="pupilStyle"></div>
                  <div class="highlight"></div>
                </div>
              </div>
              
              <!-- 鼻子 -->
              <div class="nose" :style="noseStyle"></div>
              
              <!-- 嘴巴 -->
              <div class="mouth" :style="mouthStyle"></div>
              
              <!-- 脸颊 -->
              <div v-if="showCheeks" class="cheeks">
                <div class="cheek left"></div>
                <div class="cheek right"></div>
              </div>
            </div>
            
            <!-- 身体 -->
            <div class="body-main" :style="bodyMainStyle">
              <div class="belly" :style="bellyStyle"></div>
              
              <!-- 花纹 -->
              <div v-if="petData.pattern" class="pattern">
                <div v-for="spot in patternSpots" :key="spot.id" 
                     class="spot" :style="getSpotStyle(spot)"></div>
              </div>
            </div>
            
            <!-- 四肢 -->
            <div class="limbs">
              <div class="limb front-left" :style="limbStyle" :class="limbAnimationClass"></div>
              <div class="limb front-right" :style="limbStyle" :class="limbAnimationClass"></div>
              <div class="limb back-left" :style="limbStyle" :class="limbAnimationClass"></div>
              <div class="limb back-right" :style="limbStyle" :class="limbAnimationClass"></div>
            </div>
            
            <!-- 尾巴 -->
            <div v-if="showTail" class="tail" :style="tailStyle" :class="tailAnimationClass"></div>
          </div>
          
          <!-- 装饰品 -->
          <div v-if="petData.hat" class="accessory hat" :style="hatStyle">{{ getHatEmoji() }}</div>
          <div v-if="petData.collar" class="accessory collar" :style="collarStyle"></div>
          <div v-if="petData.glasses" class="accessory glasses">👓</div>
          
          <!-- 特效 -->
          <div v-if="petData.hasSparkles" class="effects sparkles">
            <div v-for="sparkle in sparkles" :key="sparkle.id" 
                 class="sparkle" :style="getSparkleStyle(sparkle)">✨</div>
          </div>
          
          <div v-if="petData.hasGlow" class="effects glow" :style="glowStyle"></div>
        </div>
      </div>
    </div>
    
    <!-- 动作控制面板 -->
    <div v-if="showControls" class="action-controls">
      <div class="quick-actions">
        <button 
          v-for="action in quickActions" 
          :key="action.id"
          @click="changeAction(action.id)"
          class="action-btn"
          :class="{ active: currentAction === action.id }"
        >
          <span class="action-emoji">{{ action.emoji }}</span>
          <span class="action-name">{{ action.name }}</span>
        </button>
      </div>
    </div>
    
    <!-- 动作提示 -->
    <div v-if="currentAction !== 'idle'" class="action-indicator">
      <div class="action-bubble">{{ getActionEmoji() }}</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch, nextTick } from 'vue'
import { Vue3Lottie } from 'vue3-lottie'
import AnimationService from '../../services/animationService'

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
  currentAction?: string
  showControls?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  size: 200,
  animated: true,
  currentAction: 'idle',
  showControls: false
})

const emit = defineEmits<{
  'action-change': [action: string]
}>()

// 响应式数据
const lottieRef = ref()
const currentAnimation = ref(null)
const animationSpeed = ref(1.0)
const currentAction = ref(props.currentAction)
const sparkles = ref(generateSparkles())
const patternSpots = ref(generatePatternSpots())

// 快速动作按钮
const quickActions = [
  { id: 'idle', name: '待机', emoji: '😌' },
  { id: 'happy', name: '开心', emoji: '😊' },
  { id: 'sleep', name: '睡觉', emoji: '😴' },
  { id: 'eat', name: '吃饭', emoji: '🍎' },
  { id: 'play', name: '玩耍', emoji: '🎾' },
  { id: 'walk', name: '散步', emoji: '🚶' }
]

// 计算属性
const hasLottieAnimation = computed(() => {
  return currentAnimation.value !== null
})

const containerStyle = computed(() => ({
  width: `${props.size}px`,
  height: `${props.size}px`,
  position: 'relative' as const
}))

const stageStyle = computed(() => ({
  width: '100%',
  height: '100%',
  borderRadius: '20px',
  overflow: 'hidden',
  background: 'linear-gradient(135deg, rgba(255,255,255,0.1) 0%, rgba(255,255,255,0.05) 100%)',
  border: '1px solid rgba(255,255,255,0.2)'
}))

const characterStyle = computed(() => ({
  position: 'absolute' as const,
  top: '50%',
  left: '50%',
  transform: `translate(-50%, -50%) scale(${props.petData.size || 1}) rotate(${props.petData.rotation || 0}deg)`,
  transformOrigin: 'center center',
  transition: 'all 0.3s ease'
}))

const bodyStyle = computed(() => ({
  position: 'relative' as const,
  width: '120px',
  height: '100px'
}))

const headStyle = computed(() => {
  const headConfigs = {
    'CAT': { 
      width: '60px', 
      height: '55px', 
      borderRadius: '50% 50% 45% 45%',
      background: `radial-gradient(circle at 30% 25%, ${lightenColor(props.petData.primaryColor, 40)}, ${props.petData.primaryColor} 70%, ${darkenColor(props.petData.primaryColor, 15)})`
    },
    'DOG': { 
      width: '65px', 
      height: '60px', 
      borderRadius: '45% 45% 50% 50%',
      background: `linear-gradient(135deg, ${lightenColor(props.petData.primaryColor, 30)}, ${props.petData.primaryColor} 50%, ${darkenColor(props.petData.primaryColor, 10)})`
    },
    'RABBIT': { 
      width: '58px', 
      height: '52px', 
      borderRadius: '60% 60% 40% 40%',
      background: `radial-gradient(circle at 40% 30%, ${lightenColor(props.petData.primaryColor, 35)}, ${props.petData.primaryColor} 80%)`
    },
    'HAMSTER': { 
      width: '70px', 
      height: '65px', 
      borderRadius: '50%',
      background: `radial-gradient(circle at 35% 25%, ${lightenColor(props.petData.primaryColor, 45)}, ${props.petData.primaryColor} 60%, ${darkenColor(props.petData.primaryColor, 8)})`
    },
    'PANDA': { 
      width: '62px', 
      height: '58px', 
      borderRadius: '50%',
      background: `radial-gradient(circle at 30% 25%, #FFFFFF, #F5F5F5 70%, #E0E0E0)`
    }
  }
  
  const config = headConfigs[props.petData.petType as keyof typeof headConfigs] || headConfigs.CAT
  
  return {
    position: 'absolute' as const,
    top: '-20px',
    left: '50%',
    transform: 'translateX(-50%)',
    boxShadow: '0 4px 12px rgba(0,0,0,0.2)',
    zIndex: 10,
    ...config
  }
})

const earStyle = computed(() => {
  const earConfigs = {
    'CAT': { 
      width: '22px', 
      height: '28px', 
      borderRadius: '50% 50% 0 0', 
      backgroundColor: props.petData.primaryColor,
      border: `2px solid ${darkenColor(props.petData.primaryColor, 15)}`
    },
    'DOG': { 
      width: '30px', 
      height: '40px', 
      borderRadius: '50% 50% 20% 80%', 
      backgroundColor: darkenColor(props.petData.primaryColor, 10),
      border: `2px solid ${darkenColor(props.petData.primaryColor, 20)}`
    },
    'RABBIT': { 
      width: '18px', 
      height: '55px', 
      borderRadius: '50% 50% 30% 30%', 
      backgroundColor: props.petData.primaryColor,
      border: `2px solid ${darkenColor(props.petData.primaryColor, 12)}`,
      background: `linear-gradient(to bottom, ${props.petData.primaryColor}, ${lightenColor(props.petData.primaryColor, 20)} 30%, ${props.petData.primaryColor})`
    },
    'HAMSTER': { 
      width: '20px', 
      height: '20px', 
      borderRadius: '50%', 
      backgroundColor: darkenColor(props.petData.primaryColor, 5),
      border: `2px solid ${darkenColor(props.petData.primaryColor, 15)}`
    },
    'PANDA': { 
      width: '25px', 
      height: '25px', 
      borderRadius: '50%', 
      backgroundColor: '#1F1F1F',
      border: '2px solid #000000'
    }
  }
  
  const config = earConfigs[props.petData.petType as keyof typeof earConfigs] || earConfigs.CAT
  
  return {
    position: 'absolute' as const,
    top: '-10px',
    boxShadow: 'inset 0 0 8px rgba(0,0,0,0.1), 0 2px 4px rgba(0,0,0,0.2)',
    ...config
  }
})

const eyeStyle = computed(() => {
  const eyeConfigs = {
    'CAT': { width: '16px', height: '20px', borderRadius: '50% 50% 50% 50%', top: '18px' },
    'DOG': { width: '18px', height: '18px', borderRadius: '50%', top: '20px' },
    'RABBIT': { width: '20px', height: '24px', borderRadius: '50%', top: '16px' },
    'HAMSTER': { width: '14px', height: '14px', borderRadius: '50%', top: '22px' },
    'PANDA': { width: '16px', height: '20px', borderRadius: '50%', top: '18px' }
  }
  
  const config = eyeConfigs[props.petData.petType as keyof typeof eyeConfigs] || eyeConfigs.CAT
  
  return {
    position: 'absolute' as const,
    backgroundColor: 'white',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1), inset 0 0 4px rgba(0,0,0,0.05)',
    border: '1px solid rgba(0,0,0,0.1)',
    ...config
  }
})

const pupilStyle = computed(() => {
  const pupilConfigs = {
    'CAT': { width: '10px', height: '16px', borderRadius: '50% 50% 50% 50%', top: '2px', left: '3px' },
    'DOG': { width: '12px', height: '12px', borderRadius: '50%', top: '3px', left: '3px' },
    'RABBIT': { width: '14px', height: '18px', borderRadius: '50%', top: '3px', left: '3px' },
    'HAMSTER': { width: '8px', height: '8px', borderRadius: '50%', top: '3px', left: '3px' },
    'PANDA': { width: '10px', height: '14px', borderRadius: '50%', top: '3px', left: '3px' }
  }
  
  const config = pupilConfigs[props.petData.petType as keyof typeof pupilConfigs] || pupilConfigs.CAT
  
  return {
    position: 'absolute' as const,
    backgroundColor: props.petData.eyeColorLeft || '#2F2F2F',
    ...config
  }
})

const noseStyle = computed(() => {
  const noseConfigs = {
    'CAT': { 
      width: '8px', 
      height: '6px', 
      borderRadius: '50% 50% 50% 50%', 
      backgroundColor: props.petData.noseColor || '#FFB6C1',
      top: '32px'
    },
    'DOG': { 
      width: '12px', 
      height: '8px', 
      borderRadius: '50%', 
      backgroundColor: props.petData.noseColor || '#1F1F1F',
      top: '34px'
    },
    'RABBIT': { 
      width: '6px', 
      height: '4px', 
      borderRadius: '50%', 
      backgroundColor: props.petData.noseColor || '#FFB6C1',
      top: '30px'
    },
    'HAMSTER': { 
      width: '5px', 
      height: '4px', 
      borderRadius: '50%', 
      backgroundColor: props.petData.noseColor || '#FFB6C1',
      top: '35px'
    },
    'PANDA': { 
      width: '8px', 
      height: '6px', 
      borderRadius: '50%', 
      backgroundColor: '#1F1F1F',
      top: '32px'
    }
  }
  
  const config = noseConfigs[props.petData.petType as keyof typeof noseConfigs] || noseConfigs.CAT
  
  return {
    position: 'absolute' as const,
    left: '50%',
    transform: 'translateX(-50%)',
    boxShadow: '0 1px 2px rgba(0,0,0,0.2)',
    ...config
  }
})

const mouthStyle = computed(() => {
  const mouthStyles = {
    'happy': { width: '12px', height: '6px', border: '2px solid #8B4513', borderTop: 'none', borderRadius: '0 0 50% 50%' },
    'sad': { width: '12px', height: '6px', border: '2px solid #8B4513', borderBottom: 'none', borderRadius: '50% 50% 0 0', transform: 'translateX(-50%) rotate(180deg)' },
    'surprised': { width: '8px', height: '8px', border: '2px solid #8B4513', borderRadius: '50%' },
    'cute': { width: '6px', height: '3px', backgroundColor: '#FFB6C1', borderRadius: '50%' }
  }
  
  const style = mouthStyles[props.petData.expression as keyof typeof mouthStyles] || mouthStyles.happy
  
  return {
    position: 'absolute' as const,
    top: '40px',
    left: '50%',
    transform: 'translateX(-50%)',
    ...style
  }
})

const bodyMainStyle = computed(() => {
  const bodyConfigs = {
    'CAT': {
      width: '85px',
      height: '60px', 
      borderRadius: '45px',
      background: `radial-gradient(circle at 30% 20%, ${lightenColor(props.petData.primaryColor, 30)}, ${props.petData.primaryColor} 70%, ${darkenColor(props.petData.primaryColor, 10)})`
    },
    'DOG': {
      width: '90px',
      height: '65px',
      borderRadius: '40px 40px 35% 35%',
      background: `linear-gradient(135deg, ${lightenColor(props.petData.primaryColor, 25)}, ${props.petData.primaryColor} 60%, ${darkenColor(props.petData.primaryColor, 12)})`
    },
    'RABBIT': {
      width: '75px',
      height: '55px',
      borderRadius: '50% 50% 40% 40%',
      background: `radial-gradient(circle at 35% 25%, ${lightenColor(props.petData.primaryColor, 35)}, ${props.petData.primaryColor} 75%, ${darkenColor(props.petData.primaryColor, 8)})`
    },
    'HAMSTER': {
      width: '95px',
      height: '70px',
      borderRadius: '50%',
      background: `radial-gradient(circle at 30% 20%, ${lightenColor(props.petData.primaryColor, 40)}, ${props.petData.primaryColor} 60%, ${darkenColor(props.petData.primaryColor, 8)})`
    },
    'PANDA': {
      width: '85px',
      height: '65px',
      borderRadius: '45px',
      background: `radial-gradient(circle at 30% 20%, #FFFFFF, #F8F8F8 70%, #E8E8E8)`
    }
  }
  
  const config = bodyConfigs[props.petData.petType as keyof typeof bodyConfigs] || bodyConfigs.CAT
  
  return {
    position: 'absolute' as const,
    top: '25px',
    left: '50%',
    transform: 'translateX(-50%)',
    boxShadow: '0 3px 8px rgba(0,0,0,0.15)',
    zIndex: 5,
    ...config
  }
})

const bellyStyle = computed(() => ({
  position: 'absolute' as const,
  top: '15px',
  left: '50%',
  transform: 'translateX(-50%)',
  width: '50px',
  height: '35px',
  background: `radial-gradient(circle at 30% 20%, ${lightenColor(props.petData.secondaryColor, 20)}, ${props.petData.secondaryColor})`,
  borderRadius: '50%'
}))

const limbStyle = computed(() => ({
  position: 'absolute' as const,
  width: '16px',
  height: '25px',
  backgroundColor: darkenColor(props.petData.primaryColor, 8),
  borderRadius: '8px',
  boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
}))

const tailStyle = computed(() => {
  const tailConfigs = {
    'CAT': { width: '8px', height: '40px', borderRadius: '50%', backgroundColor: props.petData.primaryColor },
    'DOG': { width: '10px', height: '30px', borderRadius: '50%', backgroundColor: darkenColor(props.petData.primaryColor, 5) },
    'RABBIT': { width: '12px', height: '12px', borderRadius: '50%', backgroundColor: 'white' },
    'PANDA': { width: '8px', height: '20px', borderRadius: '50%', backgroundColor: '#2F2F2F' }
  }
  
  const config = tailConfigs[props.petData.petType as keyof typeof tailConfigs] || tailConfigs.CAT
  
  return {
    position: 'absolute' as const,
    bottom: '8px',
    right: '-6px',
    transformOrigin: 'bottom center',
    ...config
  }
})

const hatStyle = computed(() => ({
  position: 'absolute' as const,
  top: '-45px',
  left: '50%',
  transform: 'translateX(-50%)',
  fontSize: '24px',
  zIndex: 20
}))

const collarStyle = computed(() => ({
  position: 'absolute' as const,
  top: '45px',
  left: '50%',
  transform: 'translateX(-50%)',
  width: '70px',
  height: '8px',
  backgroundColor: '#8B4513',
  borderRadius: '4px',
  zIndex: 15,
  boxShadow: '0 1px 3px rgba(0,0,0,0.3)'
}))

const glowStyle = computed(() => ({
  position: 'absolute' as const,
  top: '-20px',
  left: '-20px',
  right: '-20px',
  bottom: '-20px',
  background: `radial-gradient(circle, ${props.petData.glowColor || '#FFD700'}40 0%, transparent 70%)`,
  borderRadius: '50%',
  zIndex: 1
}))

// 动画相关计算属性
const limbAnimationClass = computed(() => {
  const classes = []
  if (props.animated) classes.push('animated')
  classes.push(`action-${currentAction.value}`)
  return classes
})

const tailAnimationClass = computed(() => {
  const classes = []
  if (props.animated) classes.push('animated')
  classes.push(`action-${currentAction.value}`)
  classes.push(props.petData.petType.toLowerCase())
  return classes
})

// 显示逻辑
const showTail = computed(() => props.petData.petType !== 'HAMSTER')
const showCheeks = computed(() => ['cute', 'happy'].includes(props.petData.expression))

// 方法
function lightenColor(color: string, percent: number): string {
  const num = parseInt(color.replace("#", ""), 16)
  const amt = Math.round(2.55 * percent)
  const R = (num >> 16) + amt
  const G = (num >> 8 & 0x00FF) + amt
  const B = (num & 0x0000FF) + amt
  return "#" + (0x1000000 + (R < 255 ? R < 1 ? 0 : R : 255) * 0x10000 +
    (G < 255 ? G < 1 ? 0 : G : 255) * 0x100 +
    (B < 255 ? B < 1 ? 0 : B : 255)).toString(16).slice(1)
}

function darkenColor(color: string, percent: number): string {
  const num = parseInt(color.replace("#", ""), 16)
  const amt = Math.round(2.55 * percent)
  const R = (num >> 16) - amt
  const G = (num >> 8 & 0x00FF) - amt
  const B = (num & 0x0000FF) - amt
  return "#" + (0x1000000 + (R > 255 ? 255 : R < 0 ? 0 : R) * 0x10000 +
    (G > 255 ? 255 : G < 0 ? 0 : G) * 0x100 +
    (B > 255 ? 255 : B < 0 ? 0 : B)).toString(16).slice(1)
}

function generateSparkles() {
  return Array.from({ length: 6 }, (_, i) => ({
    id: i,
    x: Math.random() * 100,
    y: Math.random() * 100,
    delay: Math.random() * 2
  }))
}

function generatePatternSpots() {
  const spots = []
  const count = props.petData.petType === 'CAT' ? 4 : 3
  for (let i = 0; i < count; i++) {
    spots.push({
      id: i,
      x: 20 + Math.random() * 60,
      y: 20 + Math.random() * 40,
      size: 8 + Math.random() * 6
    })
  }
  return spots
}

function getSpotStyle(spot: any) {
  return {
    position: 'absolute' as const,
    left: `${spot.x}%`,
    top: `${spot.y}%`,
    width: `${spot.size}px`,
    height: `${spot.size}px`,
    backgroundColor: props.petData.patternColor || darkenColor(props.petData.primaryColor, 20),
    borderRadius: '50%',
    opacity: 0.7
  }
}

function getSparkleStyle(sparkle: any) {
  return {
    position: 'absolute' as const,
    left: `${sparkle.x}%`,
    top: `${sparkle.y}%`,
    animationDelay: `${sparkle.delay}s`,
    fontSize: '12px'
  }
}

function getHatEmoji(): string {
  const hats = { 'cap': '🧢', 'crown': '👑', 'bow': '🎀', 'hat': '🎩' }
  return hats[props.petData.hat as keyof typeof hats] || '🎩'
}

function getActionEmoji(): string {
  const emojis = { 'happy': '😊', 'sleep': '💤', 'eat': '🍎', 'play': '🎾', 'walk': '👣', 'jump': '⬆️' }
  return emojis[currentAction.value as keyof typeof emojis] || ''
}

// Lottie控制方法
const playAnimation = () => {
  lottieRef.value?.play()
}

const pauseAnimation = () => {
  lottieRef.value?.pause()
}

const stopAnimation = () => {
  lottieRef.value?.stop()
}

// 动作切换
const changeAction = async (action: string) => {
  currentAction.value = action
  emit('action-change', action)
  
  // 尝试加载对应的Lottie动画
  try {
    const animationData = await AnimationService.getPetAnimation(action, props.petData.petType)
    if (animationData) {
      currentAnimation.value = animationData
    }
  } catch (error) {
    console.log('Using CSS fallback for action:', action)
  }
}

// 监听器
watch(() => animationSpeed.value, (newSpeed) => {
  if (lottieRef.value) {
    lottieRef.value.setSpeed(newSpeed)
  }
})

watch(() => props.currentAction, (newAction) => {
  if (newAction !== currentAction.value) {
    changeAction(newAction)
  }
})

// 生命周期
onMounted(() => {
  // 初始化动画
  changeAction(currentAction.value)
  
  // 启动闪烁效果
  if (props.petData.hasSparkles) {
    setInterval(() => {
      sparkles.value = generateSparkles()
    }, 3000)
  }
})
</script>

<style scoped>
.enhanced-pet-renderer {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.lottie-layer {
  position: absolute;
  top: 0;
  left: 0;
  z-index: 20;
}

.lottie-animation {
  border-radius: 20px;
}

.css-pet-layer {
  position: relative;
  z-index: 10;
}

.css-pet-layer.background-mode {
  opacity: 0.3;
  z-index: 5;
}

.lottie-controls {
  position: absolute;
  top: -60px;
  right: -120px;
  background: rgba(0,0,0,0.8);
  border-radius: 8px;
  padding: 12px;
  color: white;
  font-size: 12px;
  min-width: 150px;
}

.control-group {
  margin-bottom: 8px;
}

.control-group label {
  display: block;
  margin-bottom: 4px;
  font-weight: bold;
}

.control-buttons {
  display: flex;
  gap: 4px;
}

.control-btn {
  background: #4A90E2;
  border: none;
  border-radius: 4px;
  color: white;
  padding: 4px 8px;
  cursor: pointer;
  font-size: 12px;
}

.control-btn:hover {
  background: #357ABD;
}

.speed-slider {
  width: 100%;
  height: 4px;
  background: #ddd;
  border-radius: 2px;
  outline: none;
  -webkit-appearance: none;
}

.speed-slider::-webkit-slider-thumb {
  -webkit-appearance: none;
  width: 12px;
  height: 12px;
  background: #4A90E2;
  border-radius: 50%;
  cursor: pointer;
}

.action-controls {
  position: absolute;
  bottom: -80px;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(0,0,0,0.8);
  border-radius: 12px;
  padding: 8px;
  z-index: 30;
}

.quick-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  background: rgba(255,255,255,0.1);
  border: 1px solid rgba(255,255,255,0.2);
  border-radius: 8px;
  color: white;
  padding: 8px 4px;
  cursor: pointer;
  text-align: center;
  transition: all 0.2s ease;
  min-width: 50px;
}

.action-btn:hover {
  background: rgba(255,255,255,0.2);
  transform: translateY(-2px);
}

.action-btn.active {
  background: rgba(74, 144, 226, 0.6);
  border-color: #4A90E2;
  box-shadow: 0 0 8px rgba(74, 144, 226, 0.4);
}

.action-emoji {
  display: block;
  font-size: 16px;
  margin-bottom: 2px;
}

.action-name {
  display: block;
  font-size: 10px;
}

.action-indicator {
  position: absolute;
  top: -40px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 25;
}

.action-bubble {
  background: rgba(0,0,0,0.8);
  color: white;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 16px;
  animation: bubblePop 0.3s ease-out;
}

/* CSS Pet 样式 */
.pet-character:hover {
  transform: translate(-50%, -50%) scale(1.05);
}

.ears {
  position: absolute;
  top: -8px;
  left: 50%;
  transform: translateX(-50%);
  width: 60px;
  height: 30px;
  display: flex;
  justify-content: space-between;
  z-index: 12;
}

.ear.left {
  transform-origin: bottom right;
  transform: rotate(-25deg);
  left: -5px;
}

.ear.right {
  transform-origin: bottom left;
  transform: rotate(25deg);
  right: -5px;
}

.eyes {
  position: absolute;
  top: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 32px;
  height: 20px;
  display: flex;
  justify-content: space-between;
}

.eye.left { left: -2px; }
.eye.right { right: -2px; }

.highlight {
  position: absolute;
  top: 2px;
  right: 2px;
  width: 3px;
  height: 4px;
  background: white;
  border-radius: 50%;
}

.cheeks {
  position: absolute;
  top: 25px;
  left: 50%;
  transform: translateX(-50%);
  width: 50px;
  height: 10px;
  display: flex;
  justify-content: space-between;
}

.cheek {
  width: 8px;
  height: 6px;
  background: rgba(255, 182, 193, 0.6);
  border-radius: 50%;
}

.limbs {
  position: absolute;
  top: 50px;
  left: 50%;
  transform: translateX(-50%);
  width: 70px;
  height: 30px;
}

.limb {
  z-index: 3;
}

.limb.front-left { top: -5px; left: 10px; animation-delay: 0s; }
.limb.front-right { top: -5px; right: 10px; animation-delay: 0.5s; }
.limb.back-left { bottom: -5px; left: 15px; animation-delay: 0.25s; }
.limb.back-right { bottom: -5px; right: 15px; animation-delay: 0.75s; }

.pattern {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.effects {
  position: absolute;
  pointer-events: none;
}

.sparkles {
  top: -30px;
  left: -30px;
  right: -30px;
  bottom: -30px;
  z-index: 25;
}

.sparkle {
  animation: sparkleFloat 2s ease-in-out infinite;
}

/* 动画 */
.animated .pet-head { animation: headBob 3s ease-in-out infinite; }
.animated .ear { animation: earWiggle 4s ease-in-out infinite; }
.animated .eye { animation: blink 4s ease-in-out infinite; }
.animated .limb { animation: limbMove 2s ease-in-out infinite; }
.animated .tail.cat { animation: tailSwish 2s ease-in-out infinite; }
.animated .tail.dog { animation: tailWag 1.5s ease-in-out infinite; }

.limb.action-happy { animation: happyBounce 0.8s ease-in-out infinite; }
.limb.action-walk { animation: walkCycle 1s ease-in-out infinite; }
.limb.action-jump { animation: jumpMove 1.5s ease-in-out infinite; }
.tail.action-happy { animation: happyWag 0.6s ease-in-out infinite !important; }
.tail.action-sleep { animation: none; }

@keyframes headBob {
  0%, 100% { transform: translateX(-50%) translateY(0); }
  50% { transform: translateX(-50%) translateY(-2px); }
}

@keyframes earWiggle {
  0%, 100% { transform: rotate(-20deg); }
  50% { transform: rotate(-25deg); }
}

@keyframes blink {
  0%, 90%, 100% { transform: scaleY(1); }
  95% { transform: scaleY(0.1); }
}

@keyframes limbMove {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-1px); }
}

@keyframes tailSwish {
  0%, 100% { transform: rotate(20deg); }
  25% { transform: rotate(35deg); }
  75% { transform: rotate(5deg); }
}

@keyframes tailWag {
  0%, 100% { transform: rotate(15deg); }
  50% { transform: rotate(-15deg); }
}

@keyframes happyBounce {
  0%, 100% { transform: translateY(0) scale(1); }
  50% { transform: translateY(-3px) scale(1.1); }
}

@keyframes walkCycle {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  25% { transform: translateY(-2px) rotate(5deg); }
  75% { transform: translateY(-1px) rotate(-5deg); }
}

@keyframes jumpMove {
  0%, 100% { transform: translateY(0); }
  30% { transform: translateY(-8px); }
  70% { transform: translateY(-4px); }
}

@keyframes happyWag {
  0%, 100% { transform: rotate(20deg) scale(1); }
  50% { transform: rotate(-20deg) scale(1.2); }
}

@keyframes sparkleFloat {
  0%, 100% { transform: translateY(0) rotate(0deg); opacity: 1; }
  50% { transform: translateY(-10px) rotate(180deg); opacity: 0.7; }
}

@keyframes bubblePop {
  0% { transform: translateX(-50%) scale(0); opacity: 0; }
  100% { transform: translateX(-50%) scale(1); opacity: 1; }
}

/* 动物特定样式 */
.pet-cat .ear.left {
  transform: rotate(-15deg);
}

.pet-cat .ear.right {
  transform: rotate(15deg);
}

.pet-dog .ear.left {
  transform: rotate(-35deg);
  border-radius: 50% 50% 20% 80%;
}

.pet-dog .ear.right {
  transform: rotate(35deg);
  border-radius: 50% 50% 80% 20%;
}

.pet-rabbit .ear.left,
.pet-rabbit .ear.right {
  transform: rotate(0deg);
  position: relative;
  top: -15px;
}

.pet-rabbit .ear.left {
  left: -8px;
}

.pet-rabbit .ear.right {
  right: -8px;
}

.pet-hamster .ears {
  width: 45px;
  top: -5px;
}

.pet-panda .ear.left,
.pet-panda .ear.right {
  transform: rotate(0deg);
  position: relative;
  top: -5px;
}

/* 为熊猫添加眼圈 */
.pet-panda .pet-head::before,
.pet-panda .pet-head::after {
  content: '';
  position: absolute;
  width: 20px;
  height: 25px;
  background: #1F1F1F;
  border-radius: 50%;
  top: 15px;
  z-index: 8;
}

.pet-panda .pet-head::before {
  left: 8px;
}

.pet-panda .pet-head::after {
  right: 8px;
}
</style>
