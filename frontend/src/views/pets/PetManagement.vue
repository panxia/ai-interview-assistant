<template>
  <div class="pet-management">
    <!-- 页面标题 -->
    <div class="page-header mb-8">
      <h1 class="text-3xl font-bold text-gray-800 mb-2">🐾 宠物管理</h1>
      <p class="text-gray-600">管理你的所有宠物，创建新的伙伴</p>
    </div>

    <!-- 操作栏 -->
    <div class="action-bar mb-6 flex flex-wrap items-center justify-between gap-4">
      <div class="flex items-center space-x-4">
        <button 
          @click="showCreateForm = true"
          class="px-6 py-3 bg-gradient-to-r from-purple-500 to-pink-500 text-white rounded-xl hover:from-purple-600 hover:to-pink-600 transition-all duration-300 transform hover:scale-105 shadow-lg"
        >
          ✨ 创建新宠物
        </button>
        
        <button 
          @click="refreshPets"
          :disabled="petStore.loading"
          class="px-4 py-3 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition-colors disabled:opacity-50"
        >
          🔄 刷新
        </button>
      </div>
      
      <div class="flex items-center space-x-4">
        <div class="text-sm text-gray-600">
          共 {{ petStore.pets.length }} 只宠物
        </div>
      </div>
    </div>

    <!-- 宠物列表 -->
    <div v-if="petStore.pets.length > 0" class="pet-list">
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
        <div 
          v-for="pet in petStore.pets" 
          :key="pet.id || pet.petId"
          class="pet-card bg-white/80 backdrop-blur-sm rounded-2xl p-6 shadow-lg hover:shadow-xl transition-all duration-300 cursor-pointer"
          @click="selectPet(pet)"
        >
          <!-- 宠物头像 -->
          <div class="text-center mb-4">
            <div class="w-20 h-20 bg-gradient-to-br from-purple-400 to-pink-400 rounded-full flex items-center justify-center text-3xl mx-auto mb-3">
              {{ getPetEmoji(pet.type || pet.petType) }}
            </div>
            <h3 class="text-lg font-semibold text-gray-800">
              {{ pet.name || pet.petName }}
            </h3>
            <p class="text-sm text-gray-500">{{ getPetTypeName(pet.type || pet.petType) }}</p>
          </div>
          
          <!-- 宠物状态 -->
          <div class="space-y-2 mb-4">
            <div class="flex justify-between text-sm">
              <span class="text-gray-600">等级</span>
              <span class="font-semibold text-purple-600">{{ pet.stats.level }}</span>
            </div>
            <div class="flex justify-between text-sm">
              <span class="text-gray-600">快乐度</span>
              <span class="font-semibold text-green-600">{{ pet.stats.happiness }}%</span>
            </div>
            <div class="flex justify-between text-sm">
              <span class="text-gray-600">健康</span>
              <span class="font-semibold text-blue-600">{{ pet.stats.health }}%</span>
            </div>
            <div class="flex justify-between text-sm">
              <span class="text-gray-600">能量</span>
              <span class="font-semibold text-orange-600">{{ pet.stats.energy }}%</span>
            </div>
          </div>
          
          <!-- 操作按钮 -->
          <div class="flex space-x-2">
            <button 
              @click.stop="viewPetDetail(pet)"
              class="flex-1 px-3 py-2 bg-purple-100 text-purple-700 rounded-lg hover:bg-purple-200 transition-colors text-sm"
            >
              详情
            </button>
            <button 
              @click.stop="interactWithPet(pet)"
              class="flex-1 px-3 py-2 bg-green-100 text-green-700 rounded-lg hover:bg-green-200 transition-colors text-sm"
            >
              互动
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else-if="!petStore.loading" class="empty-state text-center py-16">
      <div class="text-6xl mb-4">🐾</div>
      <h3 class="text-2xl font-semibold text-gray-700 mb-2">还没有宠物</h3>
      <p class="text-gray-500 mb-6">创建你的第一个宠物，开始奇妙的陪伴之旅！</p>
      <button 
        @click="showCreateForm = true"
        class="px-8 py-4 bg-gradient-to-r from-purple-500 to-pink-500 text-white rounded-xl hover:from-purple-600 hover:to-pink-600 transition-all duration-300 transform hover:scale-105 shadow-lg"
      >
        ✨ 创建第一个宠物
      </button>
    </div>

    <!-- 加载状态 -->
    <div v-if="petStore.loading" class="loading-state text-center py-16">
      <div class="animate-spin rounded-full h-16 w-16 border-b-2 border-purple-500 mx-auto mb-4"></div>
      <p class="text-gray-600">加载中...</p>
    </div>

    <!-- 错误状态 -->
    <div v-if="petStore.error" class="error-state text-center py-16">
      <div class="text-6xl mb-4">❌</div>
      <h3 class="text-2xl font-semibold text-red-700 mb-2">加载失败</h3>
      <p class="text-gray-500 mb-6">{{ petStore.error }}</p>
      <button 
        @click="refreshPets"
        class="px-6 py-3 bg-red-500 text-white rounded-lg hover:bg-red-600 transition-colors"
      >
        重试
      </button>
    </div>

    <!-- 创建宠物表单模态框 -->
    <div v-if="showCreateForm" class="modal-overlay fixed inset-0 bg-black/50 flex items-center justify-center z-50">
      <div class="modal-content bg-white rounded-2xl p-8 max-w-2xl w-full mx-4 max-h-[90vh] overflow-y-auto">
        <div class="flex items-center justify-between mb-6">
          <h2 class="text-2xl font-bold text-gray-800">✨ 创建新宠物</h2>
          <button 
            @click="showCreateForm = false"
            class="text-gray-400 hover:text-gray-600 transition-colors"
          >
            ✕
          </button>
        </div>
        
        <PetCreationForm 
          @pet-created="onPetCreated"
          @cancel="showCreateForm = false"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { usePetStore } from '../../store/index'
import type { Pet } from '../../types/index'
import PetCreationForm from '../../components/pets/PetCreationForm.vue'

const router = useRouter()
const petStore = usePetStore()
const showCreateForm = ref(false)

// 获取宠物表情
const getPetEmoji = (petType: string) => {
  const emojiMap: Record<string, string> = {
    'CAT': '🐱',
    'DOG': '🐶',
    'RABBIT': '🐰',
    'HAMSTER': '🐹',
    'DRAGON': '🐲',
    'PANDA': '🐼'
  }
  return emojiMap[petType] || '🐾'
}

// 获取宠物类型名称
const getPetTypeName = (petType: string) => {
  const nameMap: Record<string, string> = {
    'CAT': '小猫咪',
    'DOG': '小狗狗',
    'RABBIT': '小兔子',
    'HAMSTER': '小仓鼠',
    'DRAGON': '小龙',
    'PANDA': '小熊猫'
  }
  return nameMap[petType] || '未知类型'
}

// 选择宠物
const selectPet = (pet: Pet) => {
  petStore.selectPet(pet)
  router.push(`/pets/${pet.id || pet.petId}`)
}

// 查看宠物详情
const viewPetDetail = (pet: Pet) => {
  router.push(`/pets/${pet.id || pet.petId}`)
}

// 与宠物互动
const interactWithPet = (pet: Pet) => {
  petStore.selectPet(pet)
  router.push('/interaction')
}

// 刷新宠物列表
const refreshPets = async () => {
  try {
    await petStore.getPlayerPets('default') // 使用默认玩家ID
  } catch (error) {
    console.error('刷新宠物列表失败:', error)
  }
}

// 宠物创建成功回调
const onPetCreated = (pet: Pet) => {
  showCreateForm.value = false
  // 可以选择新创建的宠物
  petStore.selectPet(pet)
  // 或者跳转到宠物详情页
  router.push(`/pets/${pet.id || pet.petId}`)
}

onMounted(async () => {
  // 加载宠物列表
  try {
    await refreshPets()
  } catch (error) {
    console.error('加载宠物列表失败:', error)
  }
})
</script>

<style scoped>
.pet-management {
  max-width: 1200px;
  margin: 0 auto;
}

.pet-card:hover {
  transform: translateY(-2px);
}

.modal-overlay {
  backdrop-filter: blur(4px);
}

.modal-content {
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
}
</style>
