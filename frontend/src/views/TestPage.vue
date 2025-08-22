<template>
  <div class="test-page p-8">
    <h1 class="text-3xl font-bold mb-6">🧪 基础架构测试页面</h1>
    
    <!-- 状态显示 -->
    <div class="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
      <div class="bg-white/80 backdrop-blur-sm rounded-2xl p-6 shadow-lg">
        <h3 class="text-lg font-semibold mb-4">📊 Store状态</h3>
        <div class="space-y-2 text-sm">
          <div>加载状态: <span :class="petStore.loading ? 'text-red-500' : 'text-green-500'">{{ petStore.loading ? '加载中' : '空闲' }}</span></div>
          <div>错误信息: <span class="text-red-500">{{ petStore.error || '无' }}</span></div>
          <div>宠物数量: <span class="text-blue-500">{{ petStore.pets.length }}</span></div>
          <div>宠物类型: <span class="text-purple-500">{{ petStore.petTypes.length }}</span></div>
          <div>性格类型: <span class="text-green-500">{{ petStore.personalities.length }}</span></div>
        </div>
      </div>
      
      <div class="bg-white/80 backdrop-blur-sm rounded-2xl p-6 shadow-lg">
        <h3 class="text-lg font-semibold mb-4">🔗 路由状态</h3>
        <div class="space-y-2 text-sm">
          <div>当前路径: <span class="text-blue-500">{{ $route.path }}</span></div>
          <div>路由名称: <span class="text-purple-500">{{ $route.name }}</span></div>
          <div>路由参数: <span class="text-green-500">{{ JSON.stringify($route.params) }}</span></div>
        </div>
      </div>
    </div>
    
    <!-- 测试按钮 -->
    <div class="space-y-4 mb-8">
      <h3 class="text-lg font-semibold">🧪 功能测试</h3>
      
      <div class="flex flex-wrap gap-4">
        <button 
          @click="testGetPetTypes"
          :disabled="petStore.loading"
          class="px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 disabled:opacity-50"
        >
          获取宠物类型
        </button>
        
        <button 
          @click="testGetPersonalities"
          :disabled="petStore.loading"
          class="px-4 py-2 bg-green-500 text-white rounded-lg hover:bg-green-600 disabled:opacity-50"
        >
          获取性格类型
        </button>
        
        <button 
          @click="testHealthCheck"
          :disabled="petStore.loading"
          class="px-4 py-2 bg-purple-500 text-white rounded-lg hover:bg-purple-600 disabled:opacity-50"
        >
          健康检查
        </button>
        
        <button 
          @click="testCreatePet"
          :disabled="petStore.loading"
          class="px-4 py-2 bg-orange-500 text-white rounded-lg hover:bg-orange-600 disabled:opacity-50"
        >
          测试创建宠物
        </button>
        
        <button 
          @click="clearError"
          class="px-4 py-2 bg-gray-500 text-white rounded-lg hover:bg-gray-600"
        >
          清除错误
        </button>
      </div>
    </div>
    
    <!-- 结果显示 -->
    <div v-if="testResults.length > 0" class="bg-white/80 backdrop-blur-sm rounded-2xl p-6 shadow-lg">
      <h3 class="text-lg font-semibold mb-4">📋 测试结果</h3>
      <div class="space-y-2">
        <div 
          v-for="(result, index) in testResults" 
          :key="index"
          class="p-3 rounded-lg text-sm"
          :class="result.success ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'"
        >
          <div class="font-medium">{{ result.title }}</div>
          <div class="text-xs mt-1">{{ result.message }}</div>
          <div v-if="result.data" class="text-xs mt-1 font-mono bg-gray-100 p-2 rounded">
            {{ JSON.stringify(result.data, null, 2) }}
          </div>
        </div>
      </div>
    </div>
    
    <!-- 导航测试 -->
    <div class="mt-8 bg-white/80 backdrop-blur-sm rounded-2xl p-6 shadow-lg">
      <h3 class="text-lg font-semibold mb-4">🧭 导航测试</h3>
      <div class="flex flex-wrap gap-4">
        <button 
          @click="$router.push('/')"
          class="px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600"
        >
          首页
        </button>
        <button 
          @click="$router.push('/pets')"
          class="px-4 py-2 bg-green-500 text-white rounded-lg hover:bg-green-600"
        >
          宠物管理
        </button>
        <button 
          @click="$router.push('/interaction')"
          class="px-4 py-2 bg-purple-500 text-white rounded-lg hover:bg-purple-600"
        >
          宠物交互
        </button>
        <button 
          @click="$router.push('/games')"
          class="px-4 py-2 bg-orange-500 text-white rounded-lg hover:bg-orange-600"
        >
          小游戏
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { usePetStore } from '../store/index'
import { petApi } from '../api/petApi'

const petStore = usePetStore()
const testResults = ref<Array<{
  title: string
  message: string
  success: boolean
  data?: any
}>>([])

// 测试获取宠物类型
const testGetPetTypes = async () => {
  try {
    const result = await petStore.getPetTypes()
    testResults.value.unshift({
      title: '✅ 获取宠物类型成功',
      message: `成功获取 ${result.length} 个宠物类型`,
      success: true,
      data: result
    })
  } catch (error: any) {
    testResults.value.unshift({
      title: '❌ 获取宠物类型失败',
      message: error.message || '未知错误',
      success: false,
      data: error
    })
  }
}

// 测试获取性格类型
const testGetPersonalities = async () => {
  try {
    const result = await petStore.getPersonalities()
    testResults.value.unshift({
      title: '✅ 获取性格类型成功',
      message: `成功获取 ${result.length} 个性格类型`,
      success: true,
      data: result
    })
  } catch (error: any) {
    testResults.value.unshift({
      title: '❌ 获取性格类型失败',
      message: error.message || '未知错误',
      success: false,
      data: error
    })
  }
}

// 测试健康检查
const testHealthCheck = async () => {
  try {
    const result = await petApi.healthCheck()
    testResults.value.unshift({
      title: '✅ 健康检查成功',
      message: '后端服务正常运行',
      success: true,
      data: result
    })
  } catch (error: any) {
    testResults.value.unshift({
      title: '❌ 健康检查失败',
      message: error.message || '后端服务异常',
      success: false,
      data: error
    })
  }
}

// 测试创建宠物
const testCreatePet = async () => {
  try {
    const petData = {
      playerId: 'test',
      petName: '测试宠物',
      petType: 'CAT',
      customization: {
        personalityType: 'PLAYFUL',
        headShape: 'round',
        earStyle: 'pointed',
        eyeType: 'normal',
        primaryColor: '#FFA500',
        secondaryColor: '#FFFFFF'
      }
    }
    
    const result = await petStore.createPet(petData)
    testResults.value.unshift({
      title: '✅ 创建宠物成功',
      message: `成功创建宠物: ${result.name || result.petName}`,
      success: true,
      data: result
    })
  } catch (error: any) {
    testResults.value.unshift({
      title: '❌ 创建宠物失败',
      message: error.message || '未知错误',
      success: false,
      data: error
    })
  }
}

// 清除错误
const clearError = () => {
  petStore.clearError()
  testResults.value = []
}

// 限制测试结果数量
if (testResults.value.length > 10) {
  testResults.value = testResults.value.slice(0, 10)
}
</script>

<style scoped>
.test-page {
  max-width: 1200px;
  margin: 0 auto;
}
</style>
