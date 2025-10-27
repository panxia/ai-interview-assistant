<template>
  <div class="simple-pet-renderer" :style="containerStyle">
    <div class="pet-stage" :style="stageStyle">
      <!-- 宠物主体 -->
      <div class="pet-character" :style="characterStyle">
        <!-- 身体 -->
        <div class="pet-body" :style="bodyStyle">
          <!-- 头部 -->
          <div class="pet-head" :style="headStyle">
            <!-- 耳朵 -->
            <div class="ears">
              <div class="ear left" :style="earStyle"></div>
              <div class="ear right" :style="earStyle"></div>
            </div>
            
            <!-- 脸部特征 -->
            <div class="face">
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
              
              <!-- 脸颊红晕 -->
              <div class="cheeks" v-if="petData.expression === 'cute' || petData.expression === 'happy'">
                <div class="cheek left"></div>
                <div class="cheek right"></div>
              </div>
            </div>
          </div>
          
          <!-- 身体主体 -->
          <div class="body-main" :style="bodyMainStyle">
            <!-- 肚子 -->
            <div class="belly" :style="bellyStyle"></div>
            
            <!-- 花纹 -->
            <div v-if="petData.pattern" class="pattern" :style="patternStyle">
              <div v-for="spot in patternSpots" :key="spot.id" 
                   class="spot" :style="getSpotStyle(spot)"></div>
            </div>
          </div>
          
          <!-- 四肢 -->
          <div class="limbs">
            <div class="limb front-left" :style="limbStyle" :class="limbClasses"></div>
            <div class="limb front-right" :style="limbStyle" :class="limbClasses"></div>
            <div class="limb back-left" :style="limbStyle" :class="limbClasses"></div>
            <div class="limb back-right" :style="limbStyle" :class="limbClasses"></div>
          </div>
          
          <!-- 尾巴 -->
          <div v-if="showTail" class="tail" :style="tailStyle" :class="tailClasses"></div>
        </div>
        
        <!-- 装饰品 -->
        <div v-if="petData.hat" class="accessory hat" :style="hatStyle">{{ getHatEmoji() }}</div>
        <div v-if="petData.collar" class="accessory collar" :style="collarStyle"></div>
        <div v-if="petData.glasses" class="accessory glasses" :style="glassesStyle">👓</div>
        
        <!-- 特效 -->
        <div v-if="petData.hasSparkles" class="effects sparkles">
          <div v-for="sparkle in sparkles" :key="sparkle.id" 
               class="sparkle" :style="getSparkleStyle(sparkle)">✨</div>
        </div>
        
        <div v-if="petData.hasGlow" class="effects glow" :style="glowStyle"></div>
      </div>
      
      <!-- 动作指示器 -->
      <div v-if="currentAction !== 'idle'" class="action-indicator">
        <div class="action-bubble">{{ getActionLabel() }}</div>
      </div>
    </div>
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
const animationState = ref('idle')
const sparkles = ref(generateSparkles())
const patternSpots = ref(generatePatternSpots())

// 计算属性
const containerStyle = computed(() => ({
  width: `${props.size}px`,
  height: `${props.size}px`,
  position: 'relative' as const
}))

const stageStyle = computed(() => ({
  width: '100%',
  height: '100%',
  position: 'relative' as const,
  overflow: 'hidden',
  borderRadius: '20px'
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
  const headSizes = {
    'CAT': { width: '60px', height: '55px', borderRadius: '50% 50% 45% 45%' },
    'DOG': { width: '65px', height: '60px', borderRadius: '50% 50% 40% 40%' },
    'RABBIT': { width: '58px', height: '52px', borderRadius: '50% 50% 45% 45%' },
    'HAMSTER': { width: '70px', height: '65px', borderRadius: '50%' },
    'PANDA': { width: '62px', height: '58px', borderRadius: '50%' }
  }
  
  const config = headSizes[props.petData.petType as keyof typeof headSizes] || headSizes.CAT
  
  return {
    position: 'absolute' as const,
    top: '-20px',
    left: '50%',
    transform: 'translateX(-50%)',
    ...config,
    background: `radial-gradient(circle at 30% 25%, ${lightenColor(props.petData.primaryColor, 40)}, ${props.petData.primaryColor} 70%, ${darkenColor(props.petData.primaryColor, 15)})`,
    boxShadow: '0 4px 12px rgba(0,0,0,0.2)',
    zIndex: 10
  }
})

const earStyle = computed(() => {
  const earConfigs = {
    'CAT': {
      width: '20px', height: '25px',
      borderRadius: '50% 50% 0 0',
      backgroundColor: props.petData.primaryColor
    },
    'DOG': {
      width: '25px', height: '35px',
      borderRadius: '50%',
      backgroundColor: darkenColor(props.petData.primaryColor, 10)
    },
    'RABBIT': {
      width: '15px', height: '45px',
      borderRadius: '50%',
      backgroundColor: props.petData.primaryColor
    },
    'HAMSTER': {
      width: '18px', height: '18px',
      borderRadius: '50%',
      backgroundColor: darkenColor(props.petData.primaryColor, 5)
    },
    'PANDA': {
      width: '22px', height: '22px',
      borderRadius: '50%',
      backgroundColor: '#2F2F2F'
    }
  }
  
  const config = earConfigs[props.petData.petType as keyof typeof earConfigs] || earConfigs.CAT
  
  return {
    position: 'absolute' as const,
    top: '-8px',
    boxShadow: 'inset 0 0 8px rgba(0,0,0,0.1)',
    ...config
  }
})

const eyeStyle = computed(() => ({
  position: 'absolute' as const,
  top: '18px',
  width: '14px',
  height: '18px',
  backgroundColor: 'white',
  borderRadius: '50%',
  boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
}))

const pupilStyle = computed(() => ({
  position: 'absolute' as const,
  top: '3px',
  left: '3px',
  width: '8px',
  height: '12px',
  backgroundColor: props.petData.eyeColorLeft || '#2F2F2F',
  borderRadius: '50%'
}))

const noseStyle = computed(() => ({
  position: 'absolute' as const,
  top: '32px',
  left: '50%',
  transform: 'translateX(-50%)',
  width: '8px',
  height: '6px',
  backgroundColor: props.petData.noseColor || '#FFB6C1',
  borderRadius: '50%',
  boxShadow: '0 1px 2px rgba(0,0,0,0.2)'
}))

const mouthStyle = computed(() => {
  const mouthStyles = {
    'happy': {
      width: '12px',
      height: '6px',
      border: '2px solid #8B4513',
      borderTop: 'none',
      borderRadius: '0 0 50% 50%'
    },
    'sad': {
      width: '12px',
      height: '6px',
      border: '2px solid #8B4513',
      borderBottom: 'none',
      borderRadius: '50% 50% 0 0',
      transform: 'translateX(-50%) rotate(180deg)'
    },
    'surprised': {
      width: '8px',
      height: '8px',
      border: '2px solid #8B4513',
      borderRadius: '50%'
    },
    'cute': {
      width: '6px',
      height: '3px',
      backgroundColor: '#FFB6C1',
      borderRadius: '50%'
    }
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

const bodyMainStyle = computed(() => ({
  position: 'absolute' as const,
  top: '25px',
  left: '50%',
  transform: 'translateX(-50%)',
  width: '80px',
  height: '60px',
  background: `radial-gradient(circle at 30% 20%, ${lightenColor(props.petData.primaryColor, 30)}, ${props.petData.primaryColor} 70%, ${darkenColor(props.petData.primaryColor, 10)})`,
  borderRadius: '40px',
  boxShadow: '0 3px 8px rgba(0,0,0,0.15)',
  zIndex: 5
}))

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

const limbClasses = computed(() => ({
  'animated': props.animated,
  [`action-${props.currentAction}`]: props.animated
}))

const tailStyle = computed(() => {
  const tailConfigs = {
    'CAT': {
      width: '8px', height: '40px',
      borderRadius: '50%',
      backgroundColor: props.petData.primaryColor
    },
    'DOG': {
      width: '10px', height: '30px',
      borderRadius: '50%',
      backgroundColor: darkenColor(props.petData.primaryColor, 5)
    },
    'RABBIT': {
      width: '12px', height: '12px',
      borderRadius: '50%',
      backgroundColor: 'white'
    },
    'PANDA': {
      width: '8px', height: '20px',
      borderRadius: '50%',
      backgroundColor: '#2F2F2F'
    }
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

const tailClasses = computed(() => ({
  'animated': props.animated,
  [`action-${props.currentAction}`]: props.animated,
  [props.petData.petType.toLowerCase()]: true
}))

const patternStyle = computed(() => ({
  position: 'absolute' as const,
  top: '0',
  left: '0',
  width: '100%',
  height: '100%',
  pointerEvents: 'none' as const
}))

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

const glassesStyle = computed(() => ({
  position: 'absolute' as const,
  top: '15px',
  left: '50%',
  transform: 'translateX(-50%)',
  fontSize: '20px',
  zIndex: 15
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

// 计算属性 - 显示逻辑
const showTail = computed(() => {
  return props.petData.petType !== 'HAMSTER'
})

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
  const hats = {
    'cap': '🧢',
    'crown': '👑',
    'bow': '🎀',
    'hat': '🎩'
  }
  return hats[props.petData.hat as keyof typeof hats] || '🎩'
}

function getActionLabel(): string {
  const labels = {
    'happy': '😊',
    'sleep': '💤',
    'eat': '🍎',
    'play': '🎾',
    'walk': '👣',
    'jump': '⬆️'
  }
  return labels[props.currentAction as keyof typeof labels] || ''
}

// 监听动作变化
watch(() => props.currentAction, (newAction) => {
  animationState.value = newAction
})

// 生命周期
onMounted(() => {
  if (props.petData.hasSparkles) {
    setInterval(() => {
      sparkles.value = generateSparkles()
    }, 3000)
  }
})
</script>

<style scoped>
.simple-pet-renderer {
  display: flex;
  align-items: center;
  justify-content: center;
}

.pet-stage {
  background: linear-gradient(135deg, rgba(255,255,255,0.1) 0%, rgba(255,255,255,0.05) 100%);
  border: 1px solid rgba(255,255,255,0.2);
}

.pet-character {
  transition: transform 0.3s ease;
}

.pet-character:hover {
  transform: translate(-50%, -50%) scale(1.05);
}

/* 耳朵位置 */
.ears {
  position: absolute;
  top: -5px;
  left: 50%;
  transform: translateX(-50%);
  width: 50px;
  height: 20px;
  display: flex;
  justify-content: space-between;
}

.ear.left {
  transform-origin: bottom right;
  transform: rotate(-20deg);
}

.ear.right {
  transform-origin: bottom left;
  transform: rotate(20deg);
}

/* 眼睛位置 */
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

.eye.left {
  left: -2px;
}

.eye.right {
  right: -2px;
}

.highlight {
  position: absolute;
  top: 2px;
  right: 2px;
  width: 3px;
  height: 4px;
  background: white;
  border-radius: 50%;
}

/* 脸颊红晕 */
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

/* 四肢位置 */
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

.limb.front-left {
  top: -5px;
  left: 10px;
  animation-delay: 0s;
}

.limb.front-right {
  top: -5px;
  right: 10px;
  animation-delay: 0.5s;
}

.limb.back-left {
  bottom: -5px;
  left: 15px;
  animation-delay: 0.25s;
}

.limb.back-right {
  bottom: -5px;
  right: 15px;
  animation-delay: 0.75s;
}

/* 动画效果 */
.animated .pet-head {
  animation: headBob 3s ease-in-out infinite;
}

.animated .ear {
  animation: earWiggle 4s ease-in-out infinite;
}

.animated .eye {
  animation: blink 4s ease-in-out infinite;
}

.animated .limb {
  animation: limbMove 2s ease-in-out infinite;
}

.animated .tail.cat {
  animation: tailSwish 2s ease-in-out infinite;
}

.animated .tail.dog {
  animation: tailWag 1.5s ease-in-out infinite;
}

/* 动作特定动画 */
.limb.action-happy {
  animation: happyBounce 0.8s ease-in-out infinite;
}

.limb.action-walk {
  animation: walkCycle 1s ease-in-out infinite;
}

.limb.action-jump {
  animation: jumpMove 1.5s ease-in-out infinite;
}

.tail.action-happy {
  animation: happyWag 0.6s ease-in-out infinite !important;
}

.tail.action-sleep {
  animation: none;
}

/* 特效 */
.sparkles {
  position: absolute;
  top: -30px;
  left: -30px;
  right: -30px;
  bottom: -30px;
  pointer-events: none;
  z-index: 25;
}

.sparkle {
  animation: sparkleFloat 2s ease-in-out infinite;
}

/* 动作指示器 */
.action-indicator {
  position: absolute;
  top: -40px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 30;
}

.action-bubble {
  background: rgba(0,0,0,0.8);
  color: white;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  white-space: nowrap;
  animation: bubblePop 0.3s ease-out;
}

/* 关键帧动画 */
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
  0%, 100% { 
    transform: translateY(0) rotate(0deg);
    opacity: 1;
  }
  50% { 
    transform: translateY(-10px) rotate(180deg);
    opacity: 0.7;
  }
}

@keyframes bubblePop {
  0% { 
    transform: translateX(-50%) scale(0);
    opacity: 0;
  }
  100% { 
    transform: translateX(-50%) scale(1);
    opacity: 1;
  }
}

/* 响应式调整 */
@media (max-width: 768px) {
  .pet-character {
    transform: translate(-50%, -50%) scale(0.8);
  }
}
</style>
