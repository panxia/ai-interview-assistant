<template>
  <aside class="w-64 bg-white/80 backdrop-blur-sm border-r shadow-sm min-h-full">
    <nav class="p-4">
      <!-- 导航菜单 -->
      <ul class="space-y-2">
        <li>
          <router-link 
            to="/" 
            class="flex items-center space-x-3 px-4 py-3 rounded-lg hover:bg-purple-50 transition-colors"
            :class="{ 'bg-purple-100 text-purple-700': $route.path === '/' }"
          >
            <span class="text-xl">🐟</span>
            <span>摸鱼</span>
          </router-link>
        </li>
        
        <!-- 隐藏其他功能
        <li>
          <router-link 
            to="/dashboard" 
            class="flex items-center space-x-3 px-4 py-3 rounded-lg hover:bg-purple-50 transition-colors"
            :class="{ 'bg-purple-100 text-purple-700': $route.path === '/dashboard' }"
          >
            <span class="text-xl">🏠</span>
            <span>仪表板</span>
          </router-link>
        </li>
        
        <li>
          <router-link 
            to="/pets" 
            class="flex items-center space-x-3 px-4 py-3 rounded-lg hover:bg-purple-50 transition-colors"
            :class="{ 'bg-purple-100 text-purple-700': $route.path.startsWith('/pets') }"
          >
            <span class="text-xl">🐾</span>
            <span>宠物管理</span>
          </router-link>
        </li>
        
        <li>
          <router-link 
            to="/interaction" 
            class="flex items-center space-x-3 px-4 py-3 rounded-lg hover:bg-purple-50 transition-colors"
            :class="{ 'bg-purple-100 text-purple-700': $route.path === '/interaction' }"
          >
            <span class="text-xl">🎮</span>
            <span>宠物交互</span>
          </router-link>
        </li>
        
        <li>
          <router-link 
            to="/games" 
            class="flex items-center space-x-3 px-4 py-3 rounded-lg hover:bg-purple-50 transition-colors"
            :class="{ 'bg-purple-100 text-purple-700': $route.path === '/games' }"
          >
            <span class="text-xl">🎯</span>
            <span>小游戏</span>
          </router-link>
        </li>
        
        <li>
          <router-link 
            to="/achievements" 
            class="flex items-center space-x-3 px-4 py-3 rounded-lg hover:bg-purple-50 transition-colors"
            :class="{ 'bg-purple-100 text-purple-700': $route.path === '/achievements' }"
          >
            <span class="text-xl">🏆</span>
            <span>成就系统</span>
          </router-link>
        </li>
        
        <li>
          <router-link 
            to="/inventory" 
            class="flex items-center space-x-3 px-4 py-3 rounded-lg hover:bg-purple-50 transition-colors"
            :class="{ 'bg-purple-100 text-purple-700': $route.path === '/inventory' }"
          >
            <span class="text-xl">🎒</span>
            <span>背包系统</span>
          </router-link>
        </li>
        
        <li>
          <router-link 
            to="/stats" 
            class="flex items-center space-x-3 px-4 py-3 rounded-lg hover:bg-purple-50 transition-colors"
            :class="{ 'bg-purple-100 text-purple-700': $route.path === '/stats' }"
          >
            <span class="text-xl">📊</span>
            <span>统计信息</span>
          </router-link>
        </li>
        
        <li>
          <router-link 
            to="/system" 
            class="flex items-center space-x-3 px-4 py-3 rounded-lg hover:bg-purple-50 transition-colors"
            :class="{ 'bg-purple-100 text-purple-700': $route.path === '/system' }"
          >
            <span class="text-xl">⚙️</span>
            <span>系统功能</span>
          </router-link>
        </li>
        -->
      </ul>
      
      <!-- 隐藏快速操作
      <div class="mt-8 pt-6 border-t">
        <h3 class="text-sm font-semibold text-gray-600 mb-3 px-4">快速操作</h3>
        <div class="space-y-2">
          <button 
            @click="createNewPet"
            class="w-full flex items-center space-x-3 px-4 py-2 rounded-lg bg-gradient-to-r from-pink-500 to-purple-500 text-white hover:from-pink-600 hover:to-purple-600 transition-all duration-200"
          >
            <span class="text-lg">✨</span>
            <span>创建宠物</span>
          </button>
          
          <button 
            @click="refreshData"
            class="w-full flex items-center space-x-3 px-4 py-3 rounded-lg bg-gray-100 text-gray-700 hover:bg-gray-200 transition-colors"
          >
            <span class="text-lg">🔄</span>
            <span>刷新数据</span>
          </button>
        </div>
      </div>
      -->
    </nav>
  </aside>
</template>

<script setup lang="ts">
// 简化后的侧边栏，只保留摸鱼功能
</script>
