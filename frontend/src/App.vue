<template>
  <div id="app" class="min-h-screen bg-gradient-to-br from-pink-100 via-purple-50 to-indigo-100">
    <!-- 顶部导航栏 -->
    <AppHeader />
    
    <!-- 主内容区域 -->
    <div class="main-container flex">
      <!-- 侧边栏导航 -->
      <AppSidebar />
      
      <!-- 主内容区 -->
      <main class="content-area flex-1 p-6">
        <router-view />
      </main>
    </div>
    
    <!-- 底部信息 -->
    <AppFooter />
  </div>
</template>

<script setup lang="ts">
import AppHeader from '@/components/layout/AppHeader.vue'
import AppSidebar from '@/components/layout/AppSidebar.vue'
import AppFooter from '@/components/layout/AppFooter.vue'
</script>

<style scoped>
.main-container {
  min-height: calc(100vh - 140px); /* 减去header和footer的高度 */
}

.content-area {
  min-height: 100%;
}
</style>
