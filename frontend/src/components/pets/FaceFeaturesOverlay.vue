<template>
  <div class="face-features-overlay" :style="wrapperStyle">
    <div v-if="petData.petType === 'CAT'" class="cat-features">
      <!-- 眼睛 -->
      <div class="eyes">
        <div class="eye left" :style="eyeStyle">
          <div class="pupil" :style="leftPupilStyle"></div>
        </div>
        <div class="eye right" :style="eyeStyle">
          <div class="pupil" :style="rightPupilStyle"></div>
        </div>
      </div>
      <!-- 鼻子 -->
      <div class="nose" :style="noseStyle"></div>
      <!-- 嘴巴 -->
      <div class="mouth" :style="mouthStyle"></div>
      <!-- 胡须 -->
      <div class="whiskers">
        <div class="w left w1"></div>
        <div class="w left w2"></div>
        <div class="w left w3"></div>
        <div class="w right w4"></div>
        <div class="w right w5"></div>
        <div class="w right w6"></div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts" name="FaceFeaturesOverlay">
import { computed } from 'vue'

interface PetData {
  petType: string
  primaryColor: string
  secondaryColor: string
  eyeColorLeft?: string
  eyeColorRight?: string
  noseColor?: string
  size?: number
  rotation?: number
}

interface Props {
  petData: PetData
}

const props = defineProps<Props>()

const wrapperStyle = computed(() => ({
  position: 'absolute',
  inset: '0px',
  pointerEvents: 'none',
  zIndex: 20 as const,
}))

const eyeStyle = computed(() => ({
  width: '28px',
  height: '22px',
  background: 'white',
  borderRadius: '50% 50% 45% 45%',
  boxShadow: '0 2px 4px rgba(0,0,0,0.15)'
}))

const leftPupilStyle = computed(() => ({
  width: '10px',
  height: '14px',
  background: props.petData.eyeColorLeft || '#2F2F2F',
  borderRadius: '8px',
  position: 'absolute' as const,
  left: '8px',
  top: '4px'
}))

const rightPupilStyle = computed(() => ({
  width: '10px',
  height: '14px',
  background: props.petData.eyeColorRight || props.petData.eyeColorLeft || '#2F2F2F',
  borderRadius: '8px',
  position: 'absolute' as const,
  left: '8px',
  top: '4px'
}))

const noseStyle = computed(() => ({
  width: '10px',
  height: '8px',
  background: props.petData.noseColor || '#FFB6C1',
  borderRadius: '50%',
  position: 'absolute' as const,
  left: '50%',
  transform: 'translateX(-50%)',
  top: '50%'
}))

const mouthStyle = computed(() => ({
  width: '18px',
  height: '10px',
  borderBottom: '2px solid #8B4513',
  borderLeft: '2px solid transparent',
  borderRight: '2px solid transparent',
  borderTop: '0px solid transparent',
  borderRadius: '0 0 50% 50%',
  position: 'absolute' as const,
  left: '50%',
  transform: 'translateX(-50%)',
  top: '58%'
}))
</script>

<style scoped>
.face-features-overlay {
  position: absolute;
}

.cat-features {
  position: absolute;
  inset: 0;
}

.eyes {
  position: absolute;
  top: 40%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 70px;
  display: flex;
  justify-content: space-between;
}

.eye {
  position: relative;
}

.whiskers {
  position: absolute;
  top: 55%;
  left: 50%;
  width: 110px;
  transform: translateX(-50%);
}

.w {
  position: absolute;
  height: 2px;
  background: #8B4513;
  border-radius: 1px;
  opacity: 0.9;
}

.left.w1 { width: 26px; left: -50px; top: -6px; transform: rotate(-10deg); }
.left.w2 { width: 28px; left: -52px; top: -2px; transform: rotate(0deg); }
.left.w3 { width: 26px; left: -50px; top: 2px; transform: rotate(10deg); }
.right.w4 { width: 26px; right: -50px; top: -6px; transform: rotate(10deg); }
.right.w5 { width: 28px; right: -52px; top: -2px; transform: rotate(0deg); }
.right.w6 { width: 26px; right: -50px; top: 2px; transform: rotate(-10deg); }
</style>
