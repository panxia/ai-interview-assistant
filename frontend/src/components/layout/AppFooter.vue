<template>
  <footer class="bg-white/80 backdrop-blur-sm border-t py-4 mt-auto">
    <div class="container mx-auto px-4">
      <div class="text-center text-sm text-gray-500">
        <p>© 2025 宠物乐园 · 用爱心陪伴每一天 💕</p>
        <p class="mt-1 text-xs">
          版本 v1.0.0 | 
          <span v-if="systemStatus" class="text-green-500">● 系统正常</span>
          <span v-else class="text-red-500">● 系统异常</span>
        </p>
      </div>
    </div>
  </footer>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { petApi } from '../../api/petApi'

const systemStatus = ref(false)

const checkSystemStatus = async () => {
  try {
    await petApi.healthCheck()
    systemStatus.value = true
  } catch (error) {
    systemStatus.value = false
  }
}

onMounted(() => {
  checkSystemStatus()
  // 每30秒检查一次系统状态
  setInterval(checkSystemStatus, 30000)
})
</script>
