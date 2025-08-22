<template>
  <div class="pet-preview">
    <div class="preview-container relative">
      <!-- 宠物主体 -->
      <div class="pet-body" :style="petBodyStyle">
        <!-- 头部 -->
        <div class="pet-head" :style="petHeadStyle">
          <!-- 耳朵 -->
          <div class="ears">
            <div class="ear left" :style="earStyle"></div>
            <div class="ear right" :style="earStyle"></div>
          </div>
          
          <!-- 眼睛 -->
          <div class="eyes">
            <div class="eye left" :style="leftEyeStyle"></div>
            <div class="eye right" :style="rightEyeStyle"></div>
          </div>
          
          <!-- 鼻子 -->
          <div class="nose" :style="noseStyle"></div>
          
          <!-- 嘴巴 -->
          <div class="mouth" :style="mouthStyle"></div>
        </div>
        
        <!-- 身体 -->
        <div class="pet-torso" :style="torsoStyle">
          <!-- 图案装饰 -->
          <div v-if="petData.pattern && petData.pattern !== 'none'" 
               class="pattern" :style="patternStyle">
          </div>
        </div>
        
        <!-- 四肢 -->
        <div class="limbs">
          <div class="limb front-left" :style="limbStyle"></div>
          <div class="limb front-right" :style="limbStyle"></div>
          <div class="limb back-left" :style="limbStyle"></div>
          <div class="limb back-right" :style="limbStyle"></div>
        </div>
        
        <!-- 尾巴 -->
        <div class="tail" :style="tailStyle"></div>
      </div>
      
      <!-- 装饰品 -->
      <div v-if="petData.hat && petData.hat !== 'none'" class="accessory hat">
        {{ getAccessoryEmoji('hat', petData.hat) }}
      </div>
      <div v-if="petData.collar && petData.collar !== 'none'" class="accessory collar">
        {{ getAccessoryEmoji('collar', petData.collar) }}
      </div>
      <div v-if="petData.glasses && petData.glasses !== 'none'" class="accessory glasses">
        {{ getAccessoryEmoji('glasses', petData.glasses) }}
      </div>
      
      <!-- 特效 -->
      <div v-if="petData.hasGlow" class="glow-effect" :style="glowStyle"></div>
      <div v-if="petData.hasSparkles" class="sparkles">
        <div class="sparkle" v-for="i in 6" :key="i"></div>
      </div>
    </div>
    
    <!-- 宠物信息 -->
    <div class="pet-info mt-4 text-center">
      <div class="text-2xl mb-2">{{ getPetTypeEmoji(petData.petType) }}</div>
      <div class="text-lg font-semibold text-gray-800">
        {{ petData.petName || '新宠物' }}
      </div>
      <div class="text-sm text-gray-600">
        {{ getPetTypeName(petData.petType) }}
      </div>
      <div class="text-sm text-gray-500 mt-1">
        {{ getPersonalityName(petData.personalityType) }}
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface PetPreviewData {
  petName?: string
  petType?: string
  personalityType?: string
  headShape?: string
  earStyle?: string
  eyeType?: string
  mouthExpression?: string
  primaryColor?: string
  secondaryColor?: string
  eyeColorLeft?: string
  eyeColorRight?: string
  noseColor?: string
  pattern?: string
  patternColor?: string
  hat?: string
  collar?: string
  glasses?: string
  hasGlow?: boolean
  glowColor?: string
  hasSparkles?: boolean
  bodyProportion?: {
    fatness?: number
    height?: number
    headSize?: number
    limbLength?: number
    tailLength?: number
  }
}

const props = withDefaults(defineProps<{
  petData: PetPreviewData
  size?: 'small' | 'medium' | 'large'
}>(), {
  size: 'medium'
})

// 尺寸配置
const sizeConfig = computed(() => {
  const configs = {
    small: { scale: 0.7, container: '120px' },
    medium: { scale: 1, container: '200px' },
    large: { scale: 1.5, container: '300px' }
  }
  return configs[props.size]
})

// 宠物身体样式
const petBodyStyle = computed(() => ({
  transform: `scale(${sizeConfig.value.scale})`,
  backgroundColor: props.petData.primaryColor || '#FFA500',
  borderRadius: getShapeRadius(props.petData.headShape),
  height: `${(props.petData.bodyProportion?.height || 1) * 100}px`,
  width: `${(props.petData.bodyProportion?.fatness || 1) * 80}px`
}))

// 头部样式
const petHeadStyle = computed(() => ({
  backgroundColor: props.petData.primaryColor || '#FFA500',
  borderRadius: getShapeRadius(props.petData.headShape),
  width: `${(props.petData.bodyProportion?.headSize || 1) * 60}px`,
  height: `${(props.petData.bodyProportion?.headSize || 1) * 60}px`
}))

// 耳朵样式
const earStyle = computed(() => ({
  backgroundColor: props.petData.primaryColor || '#FFA500',
  borderRadius: getEarShape(props.petData.earStyle)
}))

// 眼睛样式
const leftEyeStyle = computed(() => ({
  backgroundColor: props.petData.eyeColorLeft || '#4169E1',
  width: getEyeSize(props.petData.eyeType),
  height: getEyeSize(props.petData.eyeType)
}))

const rightEyeStyle = computed(() => ({
  backgroundColor: props.petData.eyeColorRight || '#4169E1',
  width: getEyeSize(props.petData.eyeType),
  height: getEyeSize(props.petData.eyeType)
}))

// 鼻子样式
const noseStyle = computed(() => ({
  backgroundColor: props.petData.noseColor || '#FFB6C1'
}))

// 嘴巴样式
const mouthStyle = computed(() => ({
  borderColor: '#333'
}))

// 身体样式
const torsoStyle = computed(() => ({
  backgroundColor: props.petData.secondaryColor || '#FFFFFF',
  borderColor: props.petData.primaryColor || '#FFA500'
}))

// 四肢样式
const limbStyle = computed(() => ({
  backgroundColor: props.petData.primaryColor || '#FFA500',
  height: `${(props.petData.bodyProportion?.limbLength || 1) * 30}px`
}))

// 尾巴样式
const tailStyle = computed(() => ({
  backgroundColor: props.petData.primaryColor || '#FFA500',
  width: `${(props.petData.bodyProportion?.tailLength || 1) * 40}px`
}))

// 图案样式
const patternStyle = computed(() => ({
  backgroundColor: props.petData.patternColor || '#000000',
  opacity: 0.3
}))

// 发光效果
const glowStyle = computed(() => ({
  boxShadow: `0 0 20px ${props.petData.glowColor || '#FFFF00'}`
}))

// 辅助函数
function getShapeRadius(shape?: string) {
  const shapes = {
    round: '50%',
    oval: '50% 50% 50% 50% / 60% 60% 40% 40%',
    square: '10%',
    heart: '50% 50% 50% 50% / 60% 60% 40% 40%'
  }
  return shapes[shape as keyof typeof shapes] || shapes.round
}

function getEarShape(style?: string) {
  const styles = {
    pointed: '50% 50% 50% 0',
    droopy: '50%',
    round: '50%'
  }
  return styles[style as keyof typeof styles] || styles.pointed
}

function getEyeSize(type?: string) {
  const sizes = {
    normal: '8px',
    big: '12px',
    small: '6px'
  }
  return sizes[type as keyof typeof sizes] || sizes.normal
}

function getPetTypeEmoji(type?: string) {
  const emojis = {
    CAT: '🐱',
    DOG: '🐶',
    RABBIT: '🐰',
    BIRD: '🐦',
    FISH: '🐠',
    HAMSTER: '🐹',
    TURTLE: '🐢',
    DRAGON: '🐲',
    PANDA: '🐼'
  }
  return emojis[type as keyof typeof emojis] || '🐾'
}

function getPetTypeName(type?: string) {
  const names = {
    CAT: '小猫咪',
    DOG: '小狗狗',
    RABBIT: '小兔子',
    BIRD: '小鸟儿',
    FISH: '小鱼儿',
    HAMSTER: '小仓鼠',
    TURTLE: '小乌龟',
    DRAGON: '小龙',
    PANDA: '小熊猫'
  }
  return names[type as keyof typeof names] || '神秘宠物'
}

function getPersonalityName(type?: string) {
  const names = {
    PLAYFUL: '活泼型',
    LAZY: '慵懒型',
    CURIOUS: '好奇型',
    AFFECTIONATE: '亲人型',
    INDEPENDENT: '独立型',
    INTELLIGENT: '聪明型',
    TIMID: '胆小型',
    BRAVE: '勇敢型',
    FOODIE: '吃货型',
    BALANCED: '均衡型'
  }
  return names[type as keyof typeof names] || '待定性格'
}

function getAccessoryEmoji(type: string, accessory: string) {
  const accessories = {
    hat: {
      cap: '🧢',
      bow: '🎀',
      crown: '👑'
    },
    collar: {
      basic: '🔗',
      bow: '🎀',
      bell: '🔔'
    },
    glasses: {
      normal: '👓',
      sun: '🕶️',
      cute: '🤓'
    }
  }
  return accessories[type as keyof typeof accessories]?.[accessory as keyof any] || ''
}
</script>

<style scoped>
.pet-preview {
  @apply flex flex-col items-center;
}

.preview-container {
  @apply relative flex items-center justify-center;
  width: v-bind('sizeConfig.container');
  height: v-bind('sizeConfig.container');
  background: linear-gradient(45deg, #f0f9ff, #e0f2fe);
  border-radius: 20px;
  border: 3px solid #e5e7eb;
  overflow: hidden;
}

.pet-body {
  @apply relative;
  transition: all 0.3s ease;
}

.pet-head {
  @apply relative mx-auto mb-2;
  border: 2px solid rgba(255, 255, 255, 0.3);
}

.ears {
  @apply absolute -top-4 left-1/2 transform -translate-x-1/2 flex space-x-4;
}

.ear {
  width: 15px;
  height: 20px;
  border: 1px solid rgba(255, 255, 255, 0.5);
}

.eyes {
  @apply absolute top-1/3 left-1/2 transform -translate-x-1/2 flex space-x-3;
}

.eye {
  @apply rounded-full border border-white;
  position: relative;
}

.eye::after {
  content: '';
  position: absolute;
  top: 2px;
  left: 2px;
  width: 3px;
  height: 3px;
  background: white;
  border-radius: 50%;
}

.nose {
  @apply absolute top-1/2 left-1/2 transform -translate-x-1/2;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  border: 1px solid rgba(255, 255, 255, 0.5);
}

.mouth {
  @apply absolute bottom-1/4 left-1/2 transform -translate-x-1/2;
  width: 12px;
  height: 6px;
  border-bottom: 2px solid;
  border-radius: 0 0 12px 12px;
}

.pet-torso {
  @apply mx-auto rounded-xl border-2;
  width: 50px;
  height: 40px;
  position: relative;
}

.pattern {
  @apply absolute inset-2 rounded-lg;
}

.limbs {
  @apply absolute bottom-0 left-1/2 transform -translate-x-1/2 flex space-x-2;
}

.limb {
  @apply rounded-full;
  width: 8px;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.tail {
  @apply absolute right-0 top-1/2 transform translate-x-1/2 -translate-y-1/2 rounded-full;
  height: 8px;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.accessory {
  @apply absolute text-xl;
}

.accessory.hat {
  top: -10px;
  left: 50%;
  transform: translateX(-50%);
}

.accessory.collar {
  top: 60%;
  left: 50%;
  transform: translateX(-50%);
}

.accessory.glasses {
  top: 35%;
  left: 50%;
  transform: translateX(-50%);
}

.glow-effect {
  @apply absolute inset-0 rounded-full pointer-events-none;
  animation: glow 2s ease-in-out infinite alternate;
}

.sparkles {
  @apply absolute inset-0 pointer-events-none;
}

.sparkle {
  @apply absolute w-1 h-1 bg-yellow-300 rounded-full;
  animation: sparkle 1.5s ease-in-out infinite;
}

.sparkle:nth-child(1) { top: 10%; left: 20%; animation-delay: 0s; }
.sparkle:nth-child(2) { top: 20%; right: 15%; animation-delay: 0.3s; }
.sparkle:nth-child(3) { bottom: 30%; left: 15%; animation-delay: 0.6s; }
.sparkle:nth-child(4) { bottom: 20%; right: 20%; animation-delay: 0.9s; }
.sparkle:nth-child(5) { top: 50%; left: 10%; animation-delay: 1.2s; }
.sparkle:nth-child(6) { top: 70%; right: 10%; animation-delay: 1.5s; }

@keyframes glow {
  from { opacity: 0.5; }
  to { opacity: 1; }
}

@keyframes sparkle {
  0%, 100% { opacity: 0; transform: scale(0); }
  50% { opacity: 1; transform: scale(1); }
}

.pet-body:hover {
  transform: scale(1.05);
  animation: bounce 0.6s ease-in-out;
}

@keyframes bounce {
  0%, 20%, 60%, 100% { transform: translateY(0); }
  40% { transform: translateY(-10px); }
  80% { transform: translateY(-5px); }
}

.preview-container:hover .eye {
  animation: blink 3s infinite;
}

@keyframes blink {
  0%, 90%, 100% { transform: scaleY(1); }
  95% { transform: scaleY(0.1); }
}

.preview-container:hover .tail {
  animation: wag 0.5s ease-in-out infinite alternate;
}

@keyframes wag {
  from { transform: rotate(-10deg); }
  to { transform: rotate(10deg); }
}
</style>
