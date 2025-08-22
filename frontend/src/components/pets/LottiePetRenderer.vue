<template>
  <div class="lottie-pet-renderer">
    <div class="pet-container" :style="containerStyle">
      <!-- 主体动画层 -->
      <div class="animation-layer body-layer">
        <Vue3Lottie
          :animationData="bodyAnimation"
          :width="size"
          :height="size"
          :loop="true"
          :autoplay="isAnimated"
          ref="bodyLottie"
          class="body-animation"
        />
      </div>
      
      <!-- 表情动画层 -->
      <div class="animation-layer expression-layer">
        <Vue3Lottie
          v-if="expressionAnimation"
          :animationData="expressionAnimation"
          :width="size * 0.8"
          :height="size * 0.8"
          :loop="true"
          :autoplay="isAnimated"
          ref="expressionLottie"
          class="expression-animation"
        />
      </div>
      
      <!-- 装饰层 -->
      <div class="animation-layer accessory-layer">
        <Vue3Lottie
          v-if="accessoryAnimation"
          :animationData="accessoryAnimation"
          :width="size"
          :height="size"
          :loop="true"
          :autoplay="isAnimated"
          ref="accessoryLottie"
          class="accessory-animation"
        />
      </div>
      
      <!-- 特效层 -->
      <div class="animation-layer effect-layer">
        <Vue3Lottie
          v-if="effectAnimation"
          :animationData="effectAnimation"
          :width="size * 1.2"
          :height="size * 1.2"
          :loop="true"
          :autoplay="isAnimated"
          ref="effectLottie"
          class="effect-animation"
        />
      </div>
      
      <!-- 临时使用改进的CSS版本作为后备 -->
      <div v-if="!bodyAnimation" class="fallback-pet" :style="fallbackStyle">
        <!-- 简化的可爱宠物 -->
        <div class="simple-pet" :style="petStyle">
          <!-- 身体 -->
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
                  <div class="pupil"></div>
                  <div class="highlight"></div>
                </div>
                <div class="eye right" :style="eyeStyle">
                  <div class="pupil"></div>
                  <div class="highlight"></div>
                </div>
              </div>
              
              <!-- 鼻子 -->
              <div class="nose" :style="noseStyle"></div>
              
              <!-- 嘴巴 -->
              <div class="mouth" :style="mouthStyle"></div>
              
              <!-- 脸颊 -->
              <div class="cheeks">
                <div class="cheek left"></div>
                <div class="cheek right"></div>
              </div>
            </div>
            
            <!-- 身体主体 -->
            <div class="body-main" :style="bodyMainStyle">
              <div class="belly" :style="bellyStyle"></div>
            </div>
            
            <!-- 四肢 -->
            <div class="limbs">
              <div class="limb front-left" :style="limbStyle"></div>
              <div class="limb front-right" :style="limbStyle"></div>
              <div class="limb back-left" :style="limbStyle"></div>
              <div class="limb back-right" :style="limbStyle"></div>
            </div>
            
            <!-- 尾巴 -->
            <div v-if="showTail" class="tail" :style="tailStyle"></div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 控制面板 -->
    <div v-if="showControls" class="animation-controls">
      <div class="control-group">
        <label>动画状态</label>
        <div class="control-buttons">
          <button @click="playAnimation" class="control-btn">播放</button>
          <button @click="pauseAnimation" class="control-btn">暂停</button>
          <button @click="stopAnimation" class="control-btn">停止</button>
        </div>
      </div>
      
      <div class="control-group">
        <label>动画速度</label>
        <input 
          type="range" 
          v-model="animationSpeed" 
          min="0.5" 
          max="2" 
          step="0.1"
          @input="updateSpeed"
          class="speed-slider"
        />
        <span class="speed-value">{{ animationSpeed }}x</span>
      </div>
      
      <div class="control-group">
        <label>动作切换</label>
        <div class="action-buttons">
          <button 
            v-for="action in actions" 
            :key="action.name"
            @click="changeAction(action.name)"
            class="action-btn"
            :class="{ active: currentAction === action.name }"
          >
            {{ action.label }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts" name="LottiePetRenderer">
import { ref, computed, onMounted, watch } from 'vue'
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
  showControls?: boolean
  currentAction?: string
}

const props = withDefaults(defineProps<Props>(), {
  size: 200,
  animated: true,
  showControls: false,
  currentAction: 'idle'
})

const emit = defineEmits<{
  'action-change': [action: string]
  'animation-complete': [action: string]
}>()

// 引用
const bodyLottie = ref<InstanceType<typeof Vue3Lottie>>()
const expressionLottie = ref<InstanceType<typeof Vue3Lottie>>()
const accessoryLottie = ref<InstanceType<typeof Vue3Lottie>>()
const effectLottie = ref<InstanceType<typeof Vue3Lottie>>()

// 状态
const isAnimated = ref(props.animated)
const animationSpeed = ref(1.0)
const currentAction = ref(props.currentAction)

// 动画数据 (临时为空，后续加载真实Lottie动画)
const bodyAnimation = ref(null)
const expressionAnimation = ref(null)
const accessoryAnimation = ref(null)
const effectAnimation = ref(null)

// 动作定义
const actions = [
  { name: 'idle', label: '待机' },
  { name: 'happy', label: '开心' },
  { name: 'sad', label: '难过' },
  { name: 'sleep', label: '睡觉' },
  { name: 'eat', label: '吃饭' },
  { name: 'play', label: '玩耍' },
  { name: 'walk', label: '走路' },
  { name: 'jump', label: '跳跃' }
]

// 计算属性
const containerStyle = computed(() => ({
  width: `${props.size}px`,
  height: `${props.size}px`,
  transform: `scale(${props.petData.size}) rotate(${props.petData.rotation}deg)`,
  transition: 'all 0.3s ease'
}))

const fallbackStyle = computed(() => ({
  filter: props.petData.hasGlow ? `drop-shadow(0 0 20px ${props.petData.glowColor || '#FFD700'})` : 'none'
}))

const showTail = computed(() => {
  return props.petData.petType !== 'HAMSTER'
})

// 后备CSS样式 (改进版，更可爱)
const petStyle = computed(() => ({
  transform: 'scale(1)',
  transformOrigin: 'center center'
}))

const bodyStyle = computed(() => ({
  position: 'relative',
  width: '100%',
  height: '100%'
}))

const headStyle = computed(() => {
  const headSizes = {
    'CAT': '60px',
    'DOG': '65px', 
    'RABBIT': '58px',
    'HAMSTER': '70px',
    'PANDA': '62px'
  }
  
  return {
    position: 'absolute',
    top: '10px',
    left: '50%',
    transform: 'translateX(-50%)',
    width: headSizes[props.petData.petType as keyof typeof headSizes] || '60px',
    height: headSizes[props.petData.petType as keyof typeof headSizes] || '60px',
    backgroundColor: props.petData.primaryColor,
    borderRadius: props.petData.petType === 'HAMSTER' ? '50%' : '45px',
    background: `radial-gradient(circle at 30% 20%, ${lightenColor(props.petData.primaryColor, 40)}, ${props.petData.primaryColor} 70%, ${darkenColor(props.petData.primaryColor, 15)})`,
    boxShadow: '0 4px 8px rgba(0,0,0,0.2)',
    animation: 'breathe 3s ease-in-out infinite'
  }
})

const earStyle = computed(() => {
  const earStyles = {
    'CAT': {
      width: '20px',
      height: '25px',
      backgroundColor: props.petData.primaryColor,
      borderRadius: '50% 50% 0 0',
      transform: 'rotate(-20deg)'
    },
    'DOG': {
      width: '25px', 
      height: '35px',
      backgroundColor: props.petData.primaryColor,
      borderRadius: '50%',
      transform: 'rotate(-30deg)'
    },
    'RABBIT': {
      width: '15px',
      height: '45px', 
      backgroundColor: props.petData.primaryColor,
      borderRadius: '50%',
      transform: 'rotate(-10deg)'
    },
    'HAMSTER': {
      width: '18px',
      height: '18px',
      backgroundColor: props.petData.primaryColor,
      borderRadius: '50%',
      transform: 'rotate(-15deg)'
    },
    'PANDA': {
      width: '22px',
      height: '22px',
      backgroundColor: '#2F2F2F',
      borderRadius: '50%',
      transform: 'rotate(-10deg)'
    }
  }
  
  return {
    position: 'absolute',
    top: '-5px',
    animation: 'earWiggle 4s ease-in-out infinite',
    ...earStyles[props.petData.petType as keyof typeof earStyles] || earStyles.CAT
  }
})

const eyeStyle = computed(() => ({
  position: 'absolute',
  top: '20px',
  width: '12px',
  height: '16px',
  backgroundColor: 'white',
  borderRadius: '50%',
  animation: 'blink 4s ease-in-out infinite'
}))

const noseStyle = computed(() => ({
  position: 'absolute',
  top: '35px',
  left: '50%',
  transform: 'translateX(-50%)',
  width: '6px',
  height: '4px',
  backgroundColor: props.petData.noseColor || '#FFB6C1',
  borderRadius: '50%'
}))

const mouthStyle = computed(() => ({
  position: 'absolute',
  top: '42px',
  left: '50%',
  transform: 'translateX(-50%)',
  width: '8px',
  height: '4px',
  border: '1px solid #8B4513',
  borderTop: 'none',
  borderRadius: '0 0 50% 50%'
}))

const bodyMainStyle = computed(() => ({
  position: 'absolute',
  top: '55px',
  left: '50%',
  transform: 'translateX(-50%)',
  width: '80px',
  height: '60px',
  backgroundColor: props.petData.primaryColor,
  borderRadius: '40px',
  background: `radial-gradient(circle at 30% 20%, ${lightenColor(props.petData.primaryColor, 30)}, ${props.petData.primaryColor} 70%, ${darkenColor(props.petData.primaryColor, 10)})`,
  boxShadow: '0 3px 6px rgba(0,0,0,0.15)'
}))

const bellyStyle = computed(() => ({
  position: 'absolute',
  top: '15px',
  left: '50%',
  transform: 'translateX(-50%)',
  width: '50px',
  height: '35px',
  backgroundColor: props.petData.secondaryColor,
  borderRadius: '50%',
  background: `radial-gradient(circle at 30% 20%, ${lightenColor(props.petData.secondaryColor, 20)}, ${props.petData.secondaryColor})`
}))

const limbStyle = computed(() => ({
  position: 'absolute',
  width: '16px',
  height: '25px',
  backgroundColor: darkenColor(props.petData.primaryColor, 10),
  borderRadius: '8px',
  animation: 'limbSway 3s ease-in-out infinite'
}))

const tailStyle = computed(() => {
  const tailStyles = {
    'CAT': {
      width: '8px',
      height: '40px',
      borderRadius: '50%',
      transform: 'rotate(45deg)',
      animation: 'tailWag 2s ease-in-out infinite'
    },
    'DOG': {
      width: '10px',
      height: '30px', 
      borderRadius: '50%',
      transform: 'rotate(30deg)',
      animation: 'tailWag 1.5s ease-in-out infinite'
    },
    'RABBIT': {
      width: '12px',
      height: '12px',
      borderRadius: '50%',
      transform: 'none'
    },
    'PANDA': {
      width: '8px',
      height: '20px',
      borderRadius: '50%',
      transform: 'rotate(20deg)'
    }
  }
  
  return {
    position: 'absolute',
    bottom: '5px',
    right: '-5px',
    backgroundColor: props.petData.primaryColor,
    transformOrigin: 'bottom center',
    ...tailStyles[props.petData.petType as keyof typeof tailStyles] || tailStyles.CAT
  }
})

// 方法
const playAnimation = () => {
  isAnimated.value = true
  bodyLottie.value?.play()
  expressionLottie.value?.play()
  accessoryLottie.value?.play()
  effectLottie.value?.play()
}

const pauseAnimation = () => {
  bodyLottie.value?.pause()
  expressionLottie.value?.pause()
  accessoryLottie.value?.pause()
  effectLottie.value?.pause()
}

const stopAnimation = () => {
  isAnimated.value = false
  bodyLottie.value?.stop()
  expressionLottie.value?.stop()
  accessoryLottie.value?.stop()
  effectLottie.value?.stop()
}

const updateSpeed = () => {
  bodyLottie.value?.setSpeed(animationSpeed.value)
  expressionLottie.value?.setSpeed(animationSpeed.value)
  accessoryLottie.value?.setSpeed(animationSpeed.value)
  effectLottie.value?.setSpeed(animationSpeed.value)
}

const changeAction = (action: string) => {
  currentAction.value = action
  emit('action-change', action)
  
  // 这里加载对应动作的动画数据
  loadActionAnimation(action)
}

const loadActionAnimation = async (action: string) => {
  try {
    console.log(`Loading animation for action: ${action}`)
    
    // 使用动画服务加载动画数据
    const animationData = await AnimationService.getPetAnimation(action, props.petData.petType)
    
    if (animationData) {
      bodyAnimation.value = animationData
      console.log(`Successfully loaded animation for ${action}`)
    } else {
      console.warn(`No animation data found for action: ${action}`)
      // 如果没有动画数据，保持使用CSS后备方案
      bodyAnimation.value = null
    }
    
    // 加载表情动画
    if (props.petData.expression) {
      const expressionData = await AnimationService.getExpressionAnimation(props.petData.expression)
      if (expressionData) {
        expressionAnimation.value = expressionData
      }
    }
    
    // 加载特效动画
    if (props.petData.hasSparkles) {
      const effectData = await AnimationService.getEffectAnimation('sparkles')
      if (effectData) {
        effectAnimation.value = effectData
      }
    }
    
  } catch (error) {
    console.error('Failed to load animation:', error)
  }
}

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

// 生命周期
onMounted(() => {
  // 初始化默认动画
  loadActionAnimation(currentAction.value)
})

// 监听属性变化
watch(() => props.petData, () => {
  // 宠物数据变化时重新加载动画
  loadActionAnimation(currentAction.value)
}, { deep: true })

watch(() => props.currentAction, (newAction) => {
  if (newAction && newAction !== currentAction.value) {
    changeAction(newAction)
  }
})
</script>

<style scoped>
.lottie-pet-renderer {
  @apply relative;
}

.pet-container {
  @apply relative;
}

.animation-layer {
  @apply absolute top-0 left-0 pointer-events-none;
}

.body-layer {
  @apply z-10;
}

.expression-layer {
  @apply z-20 top-2 left-2;
}

.accessory-layer {
  @apply z-30;
}

.effect-layer {
  @apply z-40 -top-5 -left-5;
}

/* 后备CSS样式 - 改进版 */
.fallback-pet {
  @apply w-full h-full;
}

.simple-pet {
  @apply relative w-full h-full;
}

.pet-body {
  @apply relative w-full h-full;
}

.ears {
  @apply absolute -top-2 left-1/2 transform -translate-x-1/2 w-16 h-8 flex justify-between;
}

.ear {
  @apply relative;
}

.ear.left {
  transform-origin: bottom right;
  left: -10px;
}

.ear.right {
  transform-origin: bottom left;
  right: -10px;
}

.eyes {
  @apply absolute top-5 left-1/2 transform -translate-x-1/2 w-8 h-4 flex justify-between;
}

.eye {
  @apply relative;
}

.eye.left {
  left: -8px;
}

.eye.right {
  right: -8px;
}

.pupil {
  @apply absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 w-2 h-3 bg-black rounded-full;
}

.highlight {
  @apply absolute top-1 right-1 w-1 h-1 bg-white rounded-full;
}

.cheeks {
  @apply absolute top-6 left-1/2 transform -translate-x-1/2 w-16 h-4 flex justify-between;
}

.cheek {
  @apply w-3 h-2 bg-pink-200 rounded-full opacity-60;
  animation: blush 3s ease-in-out infinite;
}

.limbs {
  @apply absolute top-28 left-1/2 transform -translate-x-1/2 w-20 h-8;
}

.limb {
  @apply absolute;
}

.limb.front-left {
  top: -10px;
  left: 10px;
  animation-delay: 0s;
}

.limb.front-right {
  top: -10px;
  right: 10px;
  animation-delay: 0.5s;
}

.limb.back-left {
  bottom: -10px;
  left: 15px;
  animation-delay: 0.25s;
}

.limb.back-right {
  bottom: -10px;
  right: 15px;
  animation-delay: 0.75s;
}

/* 控制面板 */
.animation-controls {
  @apply absolute top-0 right-0 bg-black/80 backdrop-blur-sm rounded-lg p-4 text-white min-w-64;
}

.control-group {
  @apply mb-4;
}

.control-group label {
  @apply block text-sm font-medium mb-2;
}

.control-buttons {
  @apply flex space-x-2;
}

.control-btn {
  @apply px-3 py-1 bg-blue-600 hover:bg-blue-500 rounded text-sm transition-colors;
}

.speed-slider {
  @apply w-full h-2 bg-gray-600 rounded-lg appearance-none cursor-pointer;
}

.speed-slider::-webkit-slider-thumb {
  @apply appearance-none w-4 h-4 bg-blue-500 rounded-full cursor-pointer;
}

.speed-value {
  @apply text-sm font-mono bg-gray-700 px-2 py-1 rounded ml-2;
}

.action-buttons {
  @apply grid grid-cols-2 gap-2;
}

.action-btn {
  @apply px-2 py-1 bg-gray-600 hover:bg-gray-500 rounded text-xs transition-colors;
}

.action-btn.active {
  @apply bg-green-600 hover:bg-green-500;
}

/* 动画效果 */
@keyframes breathe {
  0%, 100% { transform: translateX(-50%) scale(1); }
  50% { transform: translateX(-50%) scale(1.02); }
}

@keyframes blink {
  0%, 90%, 100% { transform: scaleY(1); }
  95% { transform: scaleY(0.1); }
}

@keyframes earWiggle {
  0%, 100% { transform: rotate(-20deg); }
  50% { transform: rotate(-25deg); }
}

@keyframes limbSway {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-2px); }
}

@keyframes tailWag {
  0%, 100% { transform: rotate(45deg); }
  25% { transform: rotate(55deg); }
  75% { transform: rotate(35deg); }
}

@keyframes blush {
  0%, 100% { opacity: 0.6; }
  50% { opacity: 0.8; }
}
</style>
