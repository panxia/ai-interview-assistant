<template>
  <div class="max-w-6xl mx-auto p-4">
    <!-- 顶部导航 -->
    <div class="flex justify-center mb-6">
      <div class="flex bg-gray-100 rounded-lg p-1">
        <button 
          @click="activeTab = 'game'"
          :class="['px-6 py-2 rounded-md transition-colors', activeTab === 'game' ? 'bg-white shadow' : 'hover:bg-gray-200']"
        >
          🐠 鱼缸游戏
        </button>
        <button 
          @click="activeTab = 'pet'"
          :class="['px-6 py-2 rounded-md transition-colors', activeTab === 'pet' ? 'bg-white shadow' : 'hover:bg-gray-200']"
        >
          🐱 宠物中心
        </button>
      </div>
    </div>

    <!-- 鱼缸游戏标签页 -->
    <div v-if="activeTab === 'game'">
      <FishTankGame />
    </div>

    <!-- 宠物中心标签页 -->
    <div v-else-if="activeTab === 'pet'" class="bg-white/80 backdrop-blur-sm rounded-3xl p-8 shadow-lg">
      <h2 class="text-2xl font-bold text-gray-800 mb-6 text-center">🐱 宠物中心</h2>
      
      <!-- 宠物信息卡片 -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-8">
        <div v-for="pet in pets" :key="pet.id" class="bg-gradient-to-br from-blue-50 to-purple-50 rounded-2xl p-6 shadow">
          <div class="flex items-center mb-4">
            <div class="w-16 h-16 rounded-full bg-gradient-to-r" 
                 :style="{ backgroundColor: pet.color }">
            </div>
            <div class="ml-4">
              <h3 class="font-bold text-lg">{{ pet.name }}</h3>
              <p class="text-sm text-gray-600">{{ pet.type }} | {{ Math.floor(pet.ageInDays) }}天大</p>
            </div>
          </div>
          
          <div class="space-y-2">
            <div class="flex justify-between text-sm">
              <span>饱食度</span>
              <span>{{ pet.stats.hunger }}%</span>
            </div>
            <div class="w-full bg-gray-200 rounded-full h-2">
              <div class="bg-yellow-500 h-2 rounded-full" 
                   :style="{ width: pet.stats.hunger + '%' }"></div>
            </div>
            
            <div class="flex justify-between text-sm">
              <span>健康度</span>
              <span>{{ pet.stats.health }}%</span>
            </div>
            <div class="w-full bg-gray-200 rounded-full h-2">
              <div class="bg-green-500 h-2 rounded-full" 
                   :style="{ width: pet.stats.health + '%' }"></div>
            </div>
            
            <div class="flex justify-between text-sm">
              <span>快乐度</span>
              <span>{{ pet.stats.happiness }}%</span>
            </div>
            <div class="w-full bg-gray-200 rounded-full h-2">
              <div class="bg-blue-500 h-2 rounded-full" 
                   :style="{ width: pet.stats.happiness + '%' }"></div>
            </div>
          </div>
          
          <div class="mt-4 text-center">
            <p class="text-sm">{{ pet.statusDescription }}</p>
          </div>
        </div>
      </div>
      
      <!-- 宠物操作 -->
      <div class="bg-gray-50 rounded-2xl p-6">
        <h3 class="font-bold text-lg mb-4">🐾 宠物操作</h3>
        <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
          <button 
            v-for="action in selectedPet?.recommendedActions || []"
            :key="action"
            @click="performAction(action)"
            class="px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition-colors"
          >
            {{ action }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import FishTankGame from './FishTankGame.vue';

interface PetStats {
  hunger: number;
  health: number;
  happiness: number;
  energy: number;
  cleanliness: number;
  moodStatus: string;
}

interface Pet {
  id: string;
  name: string;
  type: string;
  color: string;
  ageInDays: number;
  stats: PetStats;
  statusDescription: string;
  recommendedActions: string[];
}

const activeTab = ref('game');
const pets = ref<Pet[]>([]);
const selectedPet = ref<Pet | null>(null);

// 初始化示例宠物
onMounted(() => {
  pets.value = [
    {
      id: '1',
      name: '小橙',
      type: '猫',
      color: '#FF6B6B',
      ageInDays: 15,
      stats: {
        hunger: 85,
        health: 95,
        happiness: 90,
        energy: 80,
        cleanliness: 75,
        moodStatus: '非常开心'
      },
      statusDescription: '小橙 非常开心 😄',
      recommendedActions: ['抚摸', '玩耍', '喂食']
    },
    {
      id: '2',
      name: '小蓝',
      type: '狗',
      color: '#4ECDC4',
      ageInDays: 22,
      stats: {
        hunger: 60,
        health: 85,
        happiness: 75,
        energy: 70,
        cleanliness: 90,
        moodStatus: '开心'
      },
      statusDescription: '小蓝 开心 😊',
      recommendedActions: ['喂食', '散步', '玩耍']
    },
    {
      id: '3',
      name: '小紫',
      type: '兔子',
      color: '#8338EC',
      ageInDays: 8,
      stats: {
        hunger: 90,
        health: 88,
        happiness: 85,
        energy: 95,
        cleanliness: 80,
        moodStatus: '开心'
      },
      statusDescription: '小紫 开心 😊',
      recommendedActions: ['抚摸', '喂食', '清洁']
    }
  ];
  
  if (pets.value.length > 0) {
    selectedPet.value = pets.value[0];
  }
});

const performAction = (action: string) => {
  if (selectedPet.value) {
    alert(`${selectedPet.value.name} ${action} 成功！`);
    
    // 模拟更新宠物状态
    if (action === '喂食') {
      selectedPet.value.stats.hunger = Math.min(100, selectedPet.value.stats.hunger + 20);
      selectedPet.value.stats.happiness = Math.min(100, selectedPet.value.stats.happiness + 5);
    } else if (action === '玩耍') {
      selectedPet.value.stats.happiness = Math.min(100, selectedPet.value.stats.happiness + 15);
      selectedPet.value.stats.energy = Math.max(0, selectedPet.value.stats.energy - 10);
    } else if (action === '清洁') {
      selectedPet.value.stats.cleanliness = Math.min(100, selectedPet.value.stats.cleanliness + 25);
      selectedPet.value.stats.happiness = Math.min(100, selectedPet.value.stats.happiness + 10);
    }
    
    // 更新状态描述
    selectedPet.value.statusDescription = `${selectedPet.value.name} ${getMoodDescription(selectedPet.value.stats.moodStatus)}`
  }
};

const getMoodDescription = (mood: string) => {
  const moodEmojis: Record<string, string> = {
    '非常开心': '😄',
    '开心': '😊',
    '一般': '😐',
    '不开心': '😔',
    '很难过': '😢'
  };
  return mood + ' ' + (moodEmojis[mood] || '🙂');
};
</script>

<style scoped>
/* 保持原有样式 */
</style>