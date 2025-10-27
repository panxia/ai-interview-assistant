<template>
  <div class="pet-renderer">
    <PetPreview 
      :pet-data="petDisplayData" 
      :size="size"
      class="pet-display"
    />
    
    <!-- 宠物状态栏 -->
    <div v-if="showStats && pet" class="pet-stats mt-4">
      <div class="flex items-center justify-between text-sm">
        <div class="flex items-center space-x-2">
          <span>❤️</span>
          <div class="bg-gray-200 rounded-full h-2 w-16">
            <div 
              class="bg-red-500 h-2 rounded-full transition-all duration-300"
              :style="{ width: `${pet.stats?.health || 0}%` }"
            ></div>
          </div>
          <span class="text-gray-600">{{ pet.stats?.health || 0 }}%</span>
        </div>
        
        <div class="flex items-center space-x-2">
          <span>😊</span>
          <div class="bg-gray-200 rounded-full h-2 w-16">
            <div 
              class="bg-yellow-500 h-2 rounded-full transition-all duration-300"
              :style="{ width: `${pet.stats?.happiness || 0}%` }"
            ></div>
          </div>
          <span class="text-gray-600">{{ pet.stats?.happiness || 0 }}%</span>
        </div>
      </div>
    </div>
    
    <!-- 互动按钮 -->
    <div v-if="showActions && pet" class="pet-actions mt-4 flex space-x-2">
      <button 
        @click="$emit('feed', pet)"
        class="px-3 py-2 bg-green-100 text-green-700 rounded-lg hover:bg-green-200 transition-colors text-sm"
      >
        🍼 喂食
      </button>
      <button 
        @click="$emit('play', pet)"
        class="px-3 py-2 bg-blue-100 text-blue-700 rounded-lg hover:bg-blue-200 transition-colors text-sm"
      >
        🎾 玩耍
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { Pet } from '../types/index'
import PetPreview from './pets/PetPreview.vue'

const props = withDefaults(defineProps<{
  pet?: Pet | null
  size?: 'small' | 'medium' | 'large'
  showStats?: boolean
  showActions?: boolean
}>(), {
  size: 'medium',
  showStats: false,
  showActions: false
})

const emit = defineEmits<{
  feed: [pet: Pet]
  play: [pet: Pet]
}>()

// 转换宠物数据为预览组件需要的格式
const petDisplayData = computed(() => {
  if (!props.pet) {
    return {
      petName: '神秘宠物',
      petType: 'CAT',
      personalityType: 'BALANCED'
    }
  }
  
  return {
    petName: props.pet.name || props.pet.petName,
    petType: props.pet.type || props.pet.petType,
    personalityType: props.pet.personality?.type || 'BALANCED',
    headShape: props.pet.appearance?.headShape || 'round',
    earStyle: props.pet.appearance?.earStyle || 'pointed',
    eyeType: props.pet.appearance?.eyeType || 'normal',
    mouthExpression: props.pet.appearance?.mouthExpression || 'smile',
    primaryColor: props.pet.appearance?.primaryColor || '#FFA500',
    secondaryColor: props.pet.appearance?.secondaryColor || '#FFFFFF',
    eyeColorLeft: props.pet.appearance?.eyeColorLeft || '#4169E1',
    eyeColorRight: props.pet.appearance?.eyeColorRight || '#4169E1',
    noseColor: props.pet.appearance?.noseColor || '#FFB6C1',
    pattern: props.pet.appearance?.pattern || 'none',
    patternColor: props.pet.appearance?.patternColor || '#000000',
    hat: props.pet.appearance?.hat || 'none',
    collar: props.pet.appearance?.collar || 'none',
    glasses: props.pet.appearance?.glasses || 'none',
    hasGlow: props.pet.appearance?.hasGlow || false,
    glowColor: props.pet.appearance?.glowColor || '#FFFF00',
    hasSparkles: props.pet.appearance?.hasSparkles || false,
    bodyProportion: props.pet.appearance?.bodyProportion || {
      fatness: 1.0,
      height: 1.0,
      headSize: 1.0,
      limbLength: 1.0,
      tailLength: 1.0
    }
  }
})
</script>

<style scoped>
.pet-renderer {
  @apply flex flex-col items-center;
}

.pet-display {
  cursor: pointer;
}

.pet-display:hover {
  transform: scale(1.02);
  transition: transform 0.2s ease;
}
</style>
