import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'MoYuHome',
    component: () => import('@/views/MoYuHome.vue'),
    meta: { title: '摸鱼 - 首页' }
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('@/views/Dashboard.vue'),
    meta: { title: '宠物乐园 - 仪表板' }
  },
  {
    path: '/test',
    name: 'TestPage',
    component: () => import('@/views/TestPage.vue'),
    meta: { title: '基础架构测试' }
  },
  {
    path: '/pets',
    name: 'PetManagement',
    component: () => import('@/views/pets/PetManagement.vue'),
    meta: { title: '宠物管理' }
  },
  {
    path: '/pets/:id',
    name: 'PetDetail',
    component: () => import('@/views/pets/PetDetail.vue'),
    meta: { title: '宠物详情' }
  },
  {
    path: '/interaction',
    name: 'PetInteraction',
    component: () => import('@/views/interaction/PetInteraction.vue'),
    meta: { title: '宠物交互' }
  },
  {
    path: '/games',
    name: 'PetGame',
    component: () => import('@/views/games/PetGame.vue'),
    meta: { title: '小游戏' }
  },
  {
    path: '/achievements',
    name: 'PetAchievement',
    component: () => import('@/views/achievements/PetAchievement.vue'),
    meta: { title: '成就系统' }
  },
  {
    path: '/inventory',
    name: 'PetInventory',
    component: () => import('@/views/inventory/PetInventory.vue'),
    meta: { title: '背包系统' }
  },
  {
    path: '/stats',
    name: 'PetStats',
    component: () => import('@/views/stats/PetStats.vue'),
    meta: { title: '统计信息' }
  },
  {
    path: '/system',
    name: 'PetSystem',
    component: () => import('@/views/system/PetSystem.vue'),
    meta: { title: '系统功能' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫 - 设置页面标题
router.beforeEach((to, from, next) => {
  if (to.meta.title) {
    document.title = to.meta.title as string
  }
  next()
})

export default router
