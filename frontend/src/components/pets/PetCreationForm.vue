<template>
  <div class="pet-creation-form">
            <!-- 使用简化版定制器 -->
        <SimplifiedPetCustomizer 
          @pet-created="$emit('pet-created', $event)"
          @cancel="$emit('cancel')"
        />
  </div>
</template>

<script setup lang="ts">
import type { Pet } from '../../types/index'
import SimplifiedPetCustomizer from './SimplifiedPetCustomizer.vue'

const emit = defineEmits<{
  'pet-created': [pet: Pet]
  'cancel': []
}>()
</script>

<style scoped>
.pet-creation-form {
  @apply w-full h-full;
}
</style>