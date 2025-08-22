<template>
  <div class="distinctive-pet-renderer" :style="containerStyle">
    <div class="pet-container">
      <!-- 猫咪 -->
      <div v-if="petData.petType === 'CAT'" class="cat-pet" :style="petStyle">
        <!-- 头部 -->
        <div class="cat-head" :style="catHeadStyle">
          <!-- 猫耳朵 -->
          <div class="cat-ears">
            <div class="cat-ear left" :style="catEarStyle"></div>
            <div class="cat-ear right" :style="catEarStyle"></div>
          </div>
          <!-- 猫眼睛 -->
          <div class="cat-eyes">
            <div class="cat-eye left" :style="catEyeStyle">
              <div class="cat-pupil" :style="catPupilStyle"></div>
            </div>
            <div class="cat-eye right" :style="catEyeStyle">
              <div class="cat-pupil" :style="catPupilStyle"></div>
            </div>
          </div>
          <!-- 猫鼻子 -->
          <div class="cat-nose" :style="catNoseStyle"></div>
          <!-- 猫嘴巴 -->
          <div class="cat-mouth" :style="catMouthStyle"></div>
          <!-- 猫胡须 -->
          <div class="cat-whiskers">
            <div class="whisker w1"></div>
            <div class="whisker w2"></div>
            <div class="whisker w3"></div>
            <div class="whisker w4"></div>
            <div class="whisker w5"></div>
            <div class="whisker w6"></div>
          </div>
        </div>
        <!-- 猫身体 -->
        <div class="cat-body" :style="catBodyStyle">
          <div class="cat-belly" :style="catBellyStyle"></div>
        </div>
        <!-- 猫尾巴 -->
        <div class="cat-tail" :style="catTailStyle"></div>
      </div>

      <!-- 狗狗 -->
      <div v-else-if="petData.petType === 'DOG'" class="dog-pet" :style="petStyle">
        <!-- 头部 -->
        <div class="dog-head" :style="dogHeadStyle">
          <!-- 狗耳朵 -->
          <div class="dog-ears">
            <div class="dog-ear left" :style="dogEarStyle"></div>
            <div class="dog-ear right" :style="dogEarStyle"></div>
          </div>
          <!-- 狗眼睛 -->
          <div class="dog-eyes">
            <div class="dog-eye left" :style="dogEyeStyle">
              <div class="dog-pupil" :style="dogPupilStyle"></div>
            </div>
            <div class="dog-eye right" :style="dogEyeStyle">
              <div class="dog-pupil" :style="dogPupilStyle"></div>
            </div>
          </div>
          <!-- 狗鼻子 -->
          <div class="dog-nose" :style="dogNoseStyle"></div>
          <!-- 狗嘴巴 -->
          <div class="dog-mouth" :style="dogMouthStyle"></div>
          <!-- 狗舌头 -->
          <div v-if="showTongue" class="dog-tongue" :style="dogTongueStyle"></div>
        </div>
        <!-- 狗身体 -->
        <div class="dog-body" :style="dogBodyStyle">
          <div class="dog-belly" :style="dogBellyStyle"></div>
        </div>
        <!-- 狗尾巴 -->
        <div class="dog-tail" :style="dogTailStyle"></div>
      </div>

      <!-- 兔子 -->
      <div v-else-if="petData.petType === 'RABBIT'" class="rabbit-pet" :style="petStyle">
        <!-- 头部 -->
        <div class="rabbit-head" :style="rabbitHeadStyle">
          <!-- 兔耳朵 -->
          <div class="rabbit-ears">
            <div class="rabbit-ear left" :style="rabbitEarStyle">
              <div class="rabbit-ear-inner" :style="rabbitEarInnerStyle"></div>
            </div>
            <div class="rabbit-ear right" :style="rabbitEarStyle">
              <div class="rabbit-ear-inner" :style="rabbitEarInnerStyle"></div>
            </div>
          </div>
          <!-- 兔眼睛 -->
          <div class="rabbit-eyes">
            <div class="rabbit-eye left" :style="rabbitEyeStyle">
              <div class="rabbit-pupil" :style="rabbitPupilStyle"></div>
            </div>
            <div class="rabbit-eye right" :style="rabbitEyeStyle">
              <div class="rabbit-pupil" :style="rabbitPupilStyle"></div>
            </div>
          </div>
          <!-- 兔鼻子 -->
          <div class="rabbit-nose" :style="rabbitNoseStyle"></div>
          <!-- 兔嘴巴 -->
          <div class="rabbit-mouth" :style="rabbitMouthStyle"></div>
        </div>
        <!-- 兔身体 -->
        <div class="rabbit-body" :style="rabbitBodyStyle">
          <div class="rabbit-belly" :style="rabbitBellyStyle"></div>
        </div>
        <!-- 兔尾巴 -->
        <div class="rabbit-tail" :style="rabbitTailStyle"></div>
      </div>

      <!-- 仓鼠 -->
      <div v-else-if="petData.petType === 'HAMSTER'" class="hamster-pet" :style="petStyle">
        <!-- 头部 -->
        <div class="hamster-head" :style="hamsterHeadStyle">
          <!-- 仓鼠耳朵 -->
          <div class="hamster-ears">
            <div class="hamster-ear left" :style="hamsterEarStyle"></div>
            <div class="hamster-ear right" :style="hamsterEarStyle"></div>
          </div>
          <!-- 仓鼠眼睛 -->
          <div class="hamster-eyes">
            <div class="hamster-eye left" :style="hamsterEyeStyle">
              <div class="hamster-pupil" :style="hamsterPupilStyle"></div>
            </div>
            <div class="hamster-eye right" :style="hamsterEyeStyle">
              <div class="hamster-pupil" :style="hamsterPupilStyle"></div>
            </div>
          </div>
          <!-- 仓鼠鼻子 -->
          <div class="hamster-nose" :style="hamsterNoseStyle"></div>
          <!-- 仓鼠嘴巴 -->
          <div class="hamster-mouth" :style="hamsterMouthStyle"></div>
          <!-- 仓鼠脸颊 -->
          <div class="hamster-cheeks">
            <div class="hamster-cheek left" :style="hamsterCheekStyle"></div>
            <div class="hamster-cheek right" :style="hamsterCheekStyle"></div>
          </div>
        </div>
        <!-- 仓鼠身体 -->
        <div class="hamster-body" :style="hamsterBodyStyle">
          <div class="hamster-belly" :style="hamsterBellyStyle"></div>
        </div>
      </div>

      <!-- 熊猫 -->
      <div v-else-if="petData.petType === 'PANDA'" class="panda-pet" :style="petStyle">
        <!-- 头部 -->
        <div class="panda-head" :style="pandaHeadStyle">
          <!-- 熊猫耳朵 -->
          <div class="panda-ears">
            <div class="panda-ear left" :style="pandaEarStyle"></div>
            <div class="panda-ear right" :style="pandaEarStyle"></div>
          </div>
          <!-- 熊猫眼圈 -->
          <div class="panda-eye-patches">
            <div class="panda-eye-patch left" :style="pandaEyePatchStyle"></div>
            <div class="panda-eye-patch right" :style="pandaEyePatchStyle"></div>
          </div>
          <!-- 熊猫眼睛 -->
          <div class="panda-eyes">
            <div class="panda-eye left" :style="pandaEyeStyle">
              <div class="panda-pupil" :style="pandaPupilStyle"></div>
            </div>
            <div class="panda-eye right" :style="pandaEyeStyle">
              <div class="panda-pupil" :style="pandaPupilStyle"></div>
            </div>
          </div>
          <!-- 熊猫鼻子 -->
          <div class="panda-nose" :style="pandaNoseStyle"></div>
          <!-- 熊猫嘴巴 -->
          <div class="panda-mouth" :style="pandaMouthStyle"></div>
        </div>
        <!-- 熊猫身体 -->
        <div class="panda-body" :style="pandaBodyStyle">
          <div class="panda-belly" :style="pandaBellyStyle"></div>
        </div>
      </div>

      <!-- 动作指示器 -->
      <div v-if="currentAction && currentAction !== 'idle'" class="action-indicator">
        <div class="action-bubble">{{ getActionEmoji() }}</div>
      </div>

      <!-- 特效层 -->
      <div v-if="petData.hasSparkles" class="sparkles-effect">
        <div v-for="sparkle in sparkles" :key="sparkle.id" 
             class="sparkle" :style="getSparkleStyle(sparkle)">✨</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts" name="DistinctivePetRenderer">
import { ref, computed, onMounted } from 'vue'

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

const sparkles = ref(generateSparkles())

// 基础样式
const containerStyle = computed(() => ({
  width: `${props.size}px`,
  height: `${props.size}px`,
  position: 'relative' as const,
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center'
}))

const petStyle = computed(() => ({
  position: 'relative' as const,
  transform: `scale(${props.petData.size || 1}) rotate(${props.petData.rotation || 0}deg)`,
  transformOrigin: 'center center',
  transition: 'all 0.3s ease'
}))

// 显示逻辑
const showTongue = computed(() => {
  return props.petData.expression === 'happy' && props.currentAction === 'play'
})

// === 猫咪样式 ===
const catHeadStyle = computed(() => ({
  position: 'relative' as const,
  width: '60px',
  height: '55px',
  background: `radial-gradient(circle at 30% 25%, ${lightenColor(props.petData.primaryColor, 40)}, ${props.petData.primaryColor} 70%, ${darkenColor(props.petData.primaryColor, 15)})`,
  borderRadius: '50% 50% 45% 45%',
  boxShadow: '0 4px 12px rgba(0,0,0,0.2)',
  zIndex: 10
}))

const catEarStyle = computed(() => ({
  position: 'absolute' as const,
  width: '22px',
  height: '28px',
  background: `linear-gradient(to bottom, ${props.petData.primaryColor}, ${darkenColor(props.petData.primaryColor, 15)})`,
  borderRadius: '50% 50% 0 0',
  border: `2px solid ${darkenColor(props.petData.primaryColor, 20)}`,
  boxShadow: '0 2px 4px rgba(0,0,0,0.2)'
}))

const catEyeStyle = computed(() => ({
  position: 'absolute' as const,
  width: '16px',
  height: '20px',
  background: 'white',
  borderRadius: '60% 40% 40% 60%',
  border: '1px solid rgba(0,0,0,0.1)',
  boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
}))

const catPupilStyle = computed(() => ({
  position: 'absolute' as const,
  top: '3px',
  left: '3px',
  width: '6px',
  height: '14px',
  background: props.petData.eyeColorLeft || '#2F2F2F',
  borderRadius: '50%'
}))

const catNoseStyle = computed(() => ({
  position: 'absolute' as const,
  top: '32px',
  left: '50%',
  transform: 'translateX(-50%)',
  width: '8px',
  height: '6px',
  background: props.petData.noseColor || '#FFB6C1',
  borderRadius: '50% 50% 50% 50%',
  boxShadow: '0 1px 2px rgba(0,0,0,0.2)'
}))

const catMouthStyle = computed(() => ({
  position: 'absolute' as const,
  top: '40px',
  left: '50%',
  transform: 'translateX(-50%)',
  width: '12px',
  height: '6px',
  border: '2px solid #8B4513',
  borderTop: 'none',
  borderRadius: '0 0 50% 50%'
}))

const catBodyStyle = computed(() => ({
  position: 'absolute' as const,
  top: '30px',
  left: '50%',
  transform: 'translateX(-50%)',
  width: '85px',
  height: '60px',
  background: `radial-gradient(circle at 30% 20%, ${lightenColor(props.petData.primaryColor, 30)}, ${props.petData.primaryColor} 70%, ${darkenColor(props.petData.primaryColor, 10)})`,
  borderRadius: '45px',
  boxShadow: '0 3px 8px rgba(0,0,0,0.15)',
  zIndex: 5
}))

const catBellyStyle = computed(() => ({
  position: 'absolute' as const,
  top: '15px',
  left: '50%',
  transform: 'translateX(-50%)',
  width: '50px',
  height: '35px',
  background: `radial-gradient(circle at 30% 20%, ${lightenColor(props.petData.secondaryColor, 20)}, ${props.petData.secondaryColor})`,
  borderRadius: '50%'
}))

const catTailStyle = computed(() => ({
  position: 'absolute' as const,
  bottom: '8px',
  right: '-6px',
  width: '8px',
  height: '40px',
  background: `linear-gradient(to bottom, ${props.petData.primaryColor}, ${darkenColor(props.petData.primaryColor, 10)})`,
  borderRadius: '50%',
  transformOrigin: 'bottom center',
  zIndex: 3
}))

// === 狗狗样式 ===
const dogHeadStyle = computed(() => ({
  position: 'relative' as const,
  width: '65px',
  height: '60px',
  background: `linear-gradient(135deg, ${lightenColor(props.petData.primaryColor, 30)}, ${props.petData.primaryColor} 50%, ${darkenColor(props.petData.primaryColor, 10)})`,
  borderRadius: '45% 45% 50% 50%',
  boxShadow: '0 4px 12px rgba(0,0,0,0.2)',
  zIndex: 10
}))

const dogEarStyle = computed(() => ({
  position: 'absolute' as const,
  width: '30px',
  height: '40px',
  background: `linear-gradient(to bottom, ${darkenColor(props.petData.primaryColor, 10)}, ${darkenColor(props.petData.primaryColor, 20)})`,
  borderRadius: '50% 50% 20% 80%',
  border: `2px solid ${darkenColor(props.petData.primaryColor, 25)}`,
  boxShadow: '0 2px 4px rgba(0,0,0,0.2)'
}))

const dogEyeStyle = computed(() => ({
  position: 'absolute' as const,
  width: '18px',
  height: '18px',
  background: 'white',
  borderRadius: '50%',
  border: '1px solid rgba(0,0,0,0.1)',
  boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
}))

const dogPupilStyle = computed(() => ({
  position: 'absolute' as const,
  top: '3px',
  left: '3px',
  width: '12px',
  height: '12px',
  background: props.petData.eyeColorLeft || '#2F2F2F',
  borderRadius: '50%'
}))

const dogNoseStyle = computed(() => ({
  position: 'absolute' as const,
  top: '34px',
  left: '50%',
  transform: 'translateX(-50%)',
  width: '12px',
  height: '8px',
  background: props.petData.noseColor || '#1F1F1F',
  borderRadius: '50%',
  boxShadow: '0 1px 2px rgba(0,0,0,0.2)'
}))

const dogMouthStyle = computed(() => ({
  position: 'absolute' as const,
  top: '45px',
  left: '50%',
  transform: 'translateX(-50%)',
  width: '16px',
  height: '8px',
  border: '2px solid #8B4513',
  borderTop: 'none',
  borderRadius: '0 0 50% 50%'
}))

const dogTongueStyle = computed(() => ({
  position: 'absolute' as const,
  top: '48px',
  left: '50%',
  transform: 'translateX(-50%)',
  width: '8px',
  height: '12px',
  background: '#FFB6C1',
  borderRadius: '0 0 50% 50%',
  zIndex: 15
}))

const dogBodyStyle = computed(() => ({
  position: 'absolute' as const,
  top: '30px',
  left: '50%',
  transform: 'translateX(-50%)',
  width: '90px',
  height: '65px',
  background: `linear-gradient(135deg, ${lightenColor(props.petData.primaryColor, 25)}, ${props.petData.primaryColor} 60%, ${darkenColor(props.petData.primaryColor, 12)})`,
  borderRadius: '40px 40px 35% 35%',
  boxShadow: '0 3px 8px rgba(0,0,0,0.15)',
  zIndex: 5
}))

const dogBellyStyle = computed(() => ({
  position: 'absolute' as const,
  top: '15px',
  left: '50%',
  transform: 'translateX(-50%)',
  width: '60px',
  height: '40px',
  background: `radial-gradient(circle at 30% 20%, ${lightenColor(props.petData.secondaryColor, 20)}, ${props.petData.secondaryColor})`,
  borderRadius: '50%'
}))

const dogTailStyle = computed(() => ({
  position: 'absolute' as const,
  bottom: '15px',
  right: '-8px',
  width: '10px',
  height: '30px',
  background: `linear-gradient(to bottom, ${darkenColor(props.petData.primaryColor, 5)}, ${darkenColor(props.petData.primaryColor, 15)})`,
  borderRadius: '50%',
  transformOrigin: 'bottom center',
  zIndex: 3
}))

// === 兔子样式 ===
const rabbitHeadStyle = computed(() => ({
  position: 'relative' as const,
  width: '58px',
  height: '52px',
  background: `radial-gradient(circle at 40% 30%, ${lightenColor(props.petData.primaryColor, 35)}, ${props.petData.primaryColor} 80%)`,
  borderRadius: '60% 60% 40% 40%',
  boxShadow: '0 4px 12px rgba(0,0,0,0.2)',
  zIndex: 10
}))

const rabbitEarStyle = computed(() => ({
  position: 'absolute' as const,
  width: '18px',
  height: '55px',
  background: `linear-gradient(to bottom, ${props.petData.primaryColor}, ${lightenColor(props.petData.primaryColor, 20)} 30%, ${props.petData.primaryColor})`,
  borderRadius: '50% 50% 30% 30%',
  border: `2px solid ${darkenColor(props.petData.primaryColor, 12)}`,
  boxShadow: '0 2px 4px rgba(0,0,0,0.2)'
}))

const rabbitEarInnerStyle = computed(() => ({
  position: 'absolute' as const,
  top: '8px',
  left: '50%',
  transform: 'translateX(-50%)',
  width: '8px',
  height: '30px',
  background: lightenColor(props.petData.secondaryColor || props.petData.primaryColor, 40),
  borderRadius: '50%'
}))

const rabbitEyeStyle = computed(() => ({
  position: 'absolute' as const,
  width: '20px',
  height: '24px',
  background: 'white',
  borderRadius: '50%',
  border: '1px solid rgba(0,0,0,0.1)',
  boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
}))

const rabbitPupilStyle = computed(() => ({
  position: 'absolute' as const,
  top: '3px',
  left: '3px',
  width: '14px',
  height: '18px',
  background: props.petData.eyeColorLeft || '#2F2F2F',
  borderRadius: '50%'
}))

const rabbitNoseStyle = computed(() => ({
  position: 'absolute' as const,
  top: '30px',
  left: '50%',
  transform: 'translateX(-50%)',
  width: '6px',
  height: '4px',
  background: props.petData.noseColor || '#FFB6C1',
  borderRadius: '50%',
  boxShadow: '0 1px 2px rgba(0,0,0,0.2)'
}))

const rabbitMouthStyle = computed(() => ({
  position: 'absolute' as const,
  top: '36px',
  left: '50%',
  transform: 'translateX(-50%)',
  width: '8px',
  height: '4px',
  border: '1px solid #8B4513',
  borderTop: 'none',
  borderRadius: '0 0 50% 50%'
}))

const rabbitBodyStyle = computed(() => ({
  position: 'absolute' as const,
  top: '25px',
  left: '50%',
  transform: 'translateX(-50%)',
  width: '75px',
  height: '55px',
  background: `radial-gradient(circle at 35% 25%, ${lightenColor(props.petData.primaryColor, 35)}, ${props.petData.primaryColor} 75%, ${darkenColor(props.petData.primaryColor, 8)})`,
  borderRadius: '50% 50% 40% 40%',
  boxShadow: '0 3px 8px rgba(0,0,0,0.15)',
  zIndex: 5
}))

const rabbitBellyStyle = computed(() => ({
  position: 'absolute' as const,
  top: '15px',
  left: '50%',
  transform: 'translateX(-50%)',
  width: '45px',
  height: '30px',
  background: `radial-gradient(circle at 30% 20%, ${lightenColor(props.petData.secondaryColor, 20)}, ${props.petData.secondaryColor})`,
  borderRadius: '50%'
}))

const rabbitTailStyle = computed(() => ({
  position: 'absolute' as const,
  bottom: '8px',
  right: '5px',
  width: '12px',
  height: '12px',
  background: 'white',
  borderRadius: '50%',
  border: `2px solid ${lightenColor(props.petData.primaryColor, 20)}`,
  boxShadow: '0 2px 4px rgba(0,0,0,0.2)',
  zIndex: 3
}))

// === 仓鼠样式 ===
const hamsterHeadStyle = computed(() => ({
  position: 'relative' as const,
  width: '70px',
  height: '65px',
  background: `radial-gradient(circle at 35% 25%, ${lightenColor(props.petData.primaryColor, 45)}, ${props.petData.primaryColor} 60%, ${darkenColor(props.petData.primaryColor, 8)})`,
  borderRadius: '50%',
  boxShadow: '0 4px 12px rgba(0,0,0,0.2)',
  zIndex: 10
}))

const hamsterEarStyle = computed(() => ({
  position: 'absolute' as const,
  width: '20px',
  height: '20px',
  background: `linear-gradient(to bottom, ${darkenColor(props.petData.primaryColor, 5)}, ${darkenColor(props.petData.primaryColor, 15)})`,
  borderRadius: '50%',
  border: `2px solid ${darkenColor(props.petData.primaryColor, 15)}`,
  boxShadow: '0 2px 4px rgba(0,0,0,0.2)'
}))

const hamsterEyeStyle = computed(() => ({
  position: 'absolute' as const,
  width: '14px',
  height: '14px',
  background: 'white',
  borderRadius: '50%',
  border: '1px solid rgba(0,0,0,0.1)',
  boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
}))

const hamsterPupilStyle = computed(() => ({
  position: 'absolute' as const,
  top: '3px',
  left: '3px',
  width: '8px',
  height: '8px',
  background: props.petData.eyeColorLeft || '#2F2F2F',
  borderRadius: '50%'
}))

const hamsterNoseStyle = computed(() => ({
  position: 'absolute' as const,
  top: '35px',
  left: '50%',
  transform: 'translateX(-50%)',
  width: '5px',
  height: '4px',
  background: props.petData.noseColor || '#FFB6C1',
  borderRadius: '50%',
  boxShadow: '0 1px 2px rgba(0,0,0,0.2)'
}))

const hamsterMouthStyle = computed(() => ({
  position: 'absolute' as const,
  top: '42px',
  left: '50%',
  transform: 'translateX(-50%)',
  width: '6px',
  height: '3px',
  background: '#FFB6C1',
  borderRadius: '50%'
}))

const hamsterCheekStyle = computed(() => ({
  position: 'absolute' as const,
  width: '18px',
  height: '15px',
  background: lightenColor(props.petData.primaryColor, 30),
  borderRadius: '50%',
  top: '25px'
}))

const hamsterBodyStyle = computed(() => ({
  position: 'absolute' as const,
  top: '35px',
  left: '50%',
  transform: 'translateX(-50%)',
  width: '95px',
  height: '70px',
  background: `radial-gradient(circle at 30% 20%, ${lightenColor(props.petData.primaryColor, 40)}, ${props.petData.primaryColor} 60%, ${darkenColor(props.petData.primaryColor, 8)})`,
  borderRadius: '50%',
  boxShadow: '0 3px 8px rgba(0,0,0,0.15)',
  zIndex: 5
}))

const hamsterBellyStyle = computed(() => ({
  position: 'absolute' as const,
  top: '20px',
  left: '50%',
  transform: 'translateX(-50%)',
  width: '60px',
  height: '40px',
  background: `radial-gradient(circle at 30% 20%, ${lightenColor(props.petData.secondaryColor, 20)}, ${props.petData.secondaryColor})`,
  borderRadius: '50%'
}))

// === 熊猫样式 ===
const pandaHeadStyle = computed(() => ({
  position: 'relative' as const,
  width: '62px',
  height: '58px',
  background: 'radial-gradient(circle at 30% 25%, #FFFFFF, #F5F5F5 70%, #E0E0E0)',
  borderRadius: '50%',
  boxShadow: '0 4px 12px rgba(0,0,0,0.2)',
  zIndex: 10
}))

const pandaEarStyle = computed(() => ({
  position: 'absolute' as const,
  width: '25px',
  height: '25px',
  background: '#1F1F1F',
  borderRadius: '50%',
  border: '2px solid #000000',
  boxShadow: '0 2px 4px rgba(0,0,0,0.2)'
}))

const pandaEyePatchStyle = computed(() => ({
  position: 'absolute' as const,
  width: '20px',
  height: '25px',
  background: '#1F1F1F',
  borderRadius: '50%',
  top: '15px',
  zIndex: 8
}))

const pandaEyeStyle = computed(() => ({
  position: 'absolute' as const,
  width: '16px',
  height: '20px',
  background: 'white',
  borderRadius: '50%',
  border: '1px solid rgba(0,0,0,0.1)',
  boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
  zIndex: 9
}))

const pandaPupilStyle = computed(() => ({
  position: 'absolute' as const,
  top: '3px',
  left: '3px',
  width: '10px',
  height: '14px',
  background: '#2F2F2F',
  borderRadius: '50%'
}))

const pandaNoseStyle = computed(() => ({
  position: 'absolute' as const,
  top: '32px',
  left: '50%',
  transform: 'translateX(-50%)',
  width: '8px',
  height: '6px',
  background: '#1F1F1F',
  borderRadius: '50%',
  boxShadow: '0 1px 2px rgba(0,0,0,0.2)'
}))

const pandaMouthStyle = computed(() => ({
  position: 'absolute' as const,
  top: '40px',
  left: '50%',
  transform: 'translateX(-50%)',
  width: '12px',
  height: '6px',
  border: '2px solid #1F1F1F',
  borderTop: 'none',
  borderRadius: '0 0 50% 50%'
}))

const pandaBodyStyle = computed(() => ({
  position: 'absolute' as const,
  top: '30px',
  left: '50%',
  transform: 'translateX(-50%)',
  width: '85px',
  height: '65px',
  background: 'radial-gradient(circle at 30% 20%, #FFFFFF, #F8F8F8 70%, #E8E8E8)',
  borderRadius: '45px',
  boxShadow: '0 3px 8px rgba(0,0,0,0.15)',
  zIndex: 5
}))

const pandaBellyStyle = computed(() => ({
  position: 'absolute' as const,
  top: '15px',
  left: '50%',
  transform: 'translateX(-50%)',
  width: '55px',
  height: '40px',
  background: 'radial-gradient(circle at 30% 20%, #FFFFFF, #F5F5F5)',
  borderRadius: '50%'
}))

// 工具函数
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

function getSparkleStyle(sparkle: any) {
  return {
    position: 'absolute' as const,
    left: `${sparkle.x}%`,
    top: `${sparkle.y}%`,
    animationDelay: `${sparkle.delay}s`,
    fontSize: '12px'
  }
}

function getActionEmoji(): string {
  const emojis = { 'happy': '😊', 'sleep': '💤', 'eat': '🍎', 'play': '🎾', 'walk': '👣', 'jump': '⬆️' }
  return emojis[props.currentAction as keyof typeof emojis] || ''
}

onMounted(() => {
  if (props.petData.hasSparkles) {
    setInterval(() => {
      sparkles.value = generateSparkles()
    }, 3000)
  }
})
</script>

<style scoped>
.distinctive-pet-renderer {
  display: flex;
  align-items: center;
  justify-content: center;
}

.pet-container {
  position: relative;
  width: 100%;
  height: 100%;
}

/* 猫咪样式 */
.cat-ears {
  position: absolute;
  top: -15px;
  left: 50%;
  transform: translateX(-50%);
  width: 50px;
  display: flex;
  justify-content: space-between;
  z-index: 12;
}

.cat-ear.left {
  transform: rotate(-15deg);
  position: absolute;
  left: 8px;
}

.cat-ear.right {
  transform: rotate(15deg);
  position: absolute;
  right: 8px;
}

.cat-eyes {
  position: absolute;
  top: 18px;
  left: 50%;
  transform: translateX(-50%);
  width: 36px;
  display: flex;
  justify-content: space-between;
}

.cat-eyes .cat-eye.left {
  position: relative;
  left: 0;
}

.cat-eyes .cat-eye.right {
  position: relative;
  right: 0;
}

.cat-whiskers {
  position: absolute;
  top: 25px;
  left: 0;
  right: 0;
  z-index: 15;
}

.whisker {
  position: absolute;
  width: 12px;
  height: 1px;
  background: #8B4513;
  border-radius: 50%;
}

.whisker.w1 { top: 5px; left: 5px; transform: rotate(-15deg); }
.whisker.w2 { top: 10px; left: 3px; transform: rotate(-5deg); }
.whisker.w3 { top: 15px; left: 5px; transform: rotate(10deg); }
.whisker.w4 { top: 5px; right: 5px; transform: rotate(15deg); }
.whisker.w5 { top: 10px; right: 3px; transform: rotate(5deg); }
.whisker.w6 { top: 15px; right: 5px; transform: rotate(-10deg); }

/* 狗狗样式 */
.dog-ears {
  position: absolute;
  top: -10px;
  left: 50%;
  transform: translateX(-50%);
  width: 65px;
  display: flex;
  justify-content: space-between;
  z-index: 12;
}

.dog-ear.left {
  transform: rotate(-35deg);
  left: -10px;
}

.dog-ear.right {
  transform: rotate(35deg);
  right: -10px;
}

.dog-eyes {
  position: absolute;
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
  width: 40px;
  display: flex;
  justify-content: space-between;
}

.dog-eyes .dog-eye.left {
  position: relative;
  left: 0;
}

.dog-eyes .dog-eye.right {
  position: relative;
  right: 0;
}

/* 兔子样式 */
.rabbit-ears {
  position: absolute;
  top: -35px;
  left: 50%;
  transform: translateX(-50%);
  width: 40px;
  display: flex;
  justify-content: space-between;
  z-index: 12;
}

.rabbit-ear.left {
  left: -5px;
}

.rabbit-ear.right {
  right: -5px;
}

.rabbit-eyes {
  position: absolute;
  top: 16px;
  left: 50%;
  transform: translateX(-50%);
  width: 44px;
  display: flex;
  justify-content: space-between;
}

.rabbit-eyes .rabbit-eye.left {
  position: relative;
  left: 0;
}

.rabbit-eyes .rabbit-eye.right {
  position: relative;
  right: 0;
}

/* 仓鼠样式 */
.hamster-ears {
  position: absolute;
  top: -8px;
  left: 50%;
  transform: translateX(-50%);
  width: 50px;
  display: flex;
  justify-content: space-between;
  z-index: 12;
}

.hamster-eyes {
  position: absolute;
  top: 22px;
  left: 50%;
  transform: translateX(-50%);
  width: 34px;
  display: flex;
  justify-content: space-between;
}

.hamster-eyes .hamster-eye.left {
  position: relative;
  left: 0;
}

.hamster-eyes .hamster-eye.right {
  position: relative;
  right: 0;
}

.hamster-cheeks {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  z-index: 11;
}

.hamster-cheek.left {
  left: -8px;
}

.hamster-cheek.right {
  right: -8px;
}

/* 熊猫样式 */
.panda-ears {
  position: absolute;
  top: -10px;
  left: 50%;
  transform: translateX(-50%);
  width: 55px;
  display: flex;
  justify-content: space-between;
  z-index: 12;
}

.panda-ear.left {
  left: -5px;
}

.panda-ear.right {
  right: -5px;
}

.panda-eye-patches {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  z-index: 8;
}

.panda-eye-patch.left {
  left: 8px;
}

.panda-eye-patch.right {
  right: 8px;
}

.panda-eyes {
  position: absolute;
  top: 18px;
  left: 50%;
  transform: translateX(-50%);
  width: 36px;
  display: flex;
  justify-content: space-between;
  z-index: 9;
}

.panda-eyes .panda-eye.left {
  position: relative;
  left: 0;
}

.panda-eyes .panda-eye.right {
  position: relative;
  right: 0;
}

/* 通用特效 */
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

.sparkles-effect {
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

@keyframes bubblePop {
  0% { transform: translateX(-50%) scale(0); opacity: 0; }
  100% { transform: translateX(-50%) scale(1); opacity: 1; }
}

@keyframes sparkleFloat {
  0%, 100% { transform: translateY(0) rotate(0deg); opacity: 1; }
  50% { transform: translateY(-10px) rotate(180deg); opacity: 0.7; }
}
</style>
