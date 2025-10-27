import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Pet, PetType, Achievement, GameItem, PlayerStats } from '../types/index'
import { petApi } from '../api/petApi'

export const usePetStore = defineStore('pet', () => {
  // 状态
  const currentPet = ref<Pet | null>(null)
  const pets = ref<Pet[]>([])
  const petTypes = ref<PetType[]>([])
  const personalities = ref<any[]>([])
  const achievements = ref<Achievement[]>([])
  const inventory = ref<GameItem[]>([])
  const playerStats = ref<PlayerStats | null>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)

  // 计算属性
  const hasPets = computed(() => pets.value.length > 0)
  const currentPetId = computed(() => currentPet.value?.id || null)
  const currentPlayerId = computed(() => currentPet.value?.playerId || 'default')

  // 宠物管理
  const createPet = async (petData: any) => {
    try {
      loading.value = true
      error.value = null
      const newPet = await petApi.createPet(petData)
      pets.value.push(newPet)
      currentPet.value = newPet
      return newPet
    } catch (err: any) {
      error.value = err.message || '创建宠物失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  const getPet = async (petId: string) => {
    try {
      loading.value = true
      error.value = null
      const pet = await petApi.getPet(petId)
      // 更新pets列表中的宠物信息
      const index = pets.value.findIndex(p => p.id === petId)
      if (index !== -1) {
        pets.value[index] = pet
      }
      if (currentPet.value?.id === petId) {
        currentPet.value = pet
      }
      return pet
    } catch (err: any) {
      error.value = err.message || '获取宠物信息失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  const getPlayerPets = async (playerId: string) => {
    try {
      loading.value = true
      error.value = null
      const playerPets = await petApi.getPlayerPets(playerId)
      pets.value = playerPets
      return playerPets
    } catch (err: any) {
      error.value = err.message || '获取宠物列表失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  const getPetTypes = async () => {
    try {
      loading.value = true
      error.value = null
      const types = await petApi.getPetTypes()
      petTypes.value = types
      return types
    } catch (err: any) {
      error.value = err.message || '获取宠物类型失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  const getPersonalities = async () => {
    try {
      loading.value = true
      error.value = null
      const personalityTypes = await petApi.getPersonalities()
      personalities.value = personalityTypes
      return personalityTypes
    } catch (err: any) {
      error.value = err.message || '获取性格类型失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  // 宠物交互
  const feedPet = async (petId: string, itemId: string) => {
    try {
      loading.value = true
      error.value = null
      const updatedPet = await petApi.feedPet(petId, itemId)
      // 更新宠物状态
      await updatePetInStore(updatedPet)
      return updatedPet
    } catch (err: any) {
      error.value = err.message || '喂食失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  const playWithPet = async (petId: string, activityType: string) => {
    try {
      loading.value = true
      error.value = null
      const updatedPet = await petApi.playWithPet(petId, activityType)
      await updatePetInStore(updatedPet)
      return updatedPet
    } catch (err: any) {
      error.value = err.message || '玩耍失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  const cleanPet = async (petId: string) => {
    try {
      loading.value = true
      error.value = null
      const updatedPet = await petApi.cleanPet(petId)
      await updatePetInStore(updatedPet)
      return updatedPet
    } catch (err: any) {
      error.value = err.message || '清洁失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  const restPet = async (petId: string) => {
    try {
      loading.value = true
      error.value = null
      const updatedPet = await petApi.restPet(petId)
      await updatePetInStore(updatedPet)
      return updatedPet
    } catch (err: any) {
      error.value = err.message || '休息失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  // 小游戏
  const startMiniGame = async (petId: string, gameType: string) => {
    try {
      loading.value = true
      error.value = null
      const gameData = await petApi.startMiniGame(petId, gameType)
      return gameData
    } catch (err: any) {
      error.value = err.message || '开始游戏失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  const completeMiniGame = async (petId: string, gameType: string, score: number) => {
    try {
      loading.value = true
      error.value = null
      const updatedPet = await petApi.completeMiniGame(petId, gameType, score)
      await updatePetInStore(updatedPet)
      return updatedPet
    } catch (err: any) {
      error.value = err.message || '完成游戏失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  // 成就系统
  const getPlayerAchievements = async (playerId: string) => {
    try {
      loading.value = true
      error.value = null
      const playerAchievements = await petApi.getPlayerAchievements(playerId)
      achievements.value = playerAchievements
      return playerAchievements
    } catch (err: any) {
      error.value = err.message || '获取成就失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  // 背包系统
  const getPlayerInventory = async (playerId: string) => {
    try {
      loading.value = true
      error.value = null
      const playerInventory = await petApi.getPlayerInventory(playerId)
      inventory.value = playerInventory
      return playerInventory
    } catch (err: any) {
      error.value = err.message || '获取背包失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  // 统计信息
  const getPlayerStats = async (playerId: string) => {
    try {
      loading.value = true
      error.value = null
      const stats = await petApi.getPlayerStats(playerId)
      playerStats.value = stats
      return stats
    } catch (err: any) {
      error.value = err.message || '获取统计信息失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  // 辅助方法
  const updatePetInStore = async (updatedPet: Pet) => {
    // 更新pets列表中的宠物信息
    const index = pets.value.findIndex(p => p.id === updatedPet.id)
    if (index !== -1) {
      pets.value[index] = updatedPet
    }
    // 如果是当前宠物，也更新currentPet
    if (currentPet.value?.id === updatedPet.id) {
      currentPet.value = updatedPet
    }
  }

  const selectPet = (pet: Pet) => {
    currentPet.value = pet
  }

  const clearError = () => {
    error.value = null
  }

  const resetStore = () => {
    currentPet.value = null
    pets.value = []
    petTypes.value = []
    personalities.value = []
    achievements.value = []
    inventory.value = []
    playerStats.value = null
    loading.value = false
    error.value = null
  }

  return {
    // 状态
    currentPet,
    pets,
    petTypes,
    personalities,
    achievements,
    inventory,
    playerStats,
    loading,
    error,
    
    // 计算属性
    hasPets,
    currentPetId,
    currentPlayerId,
    
    // 宠物管理
    createPet,
    getPet,
    getPlayerPets,
    getPetTypes,
    getPersonalities,
    
    // 宠物交互
    feedPet,
    playWithPet,
    cleanPet,
    restPet,
    
    // 小游戏
    startMiniGame,
    completeMiniGame,
    
    // 成就系统
    getPlayerAchievements,
    
    // 背包系统
    getPlayerInventory,
    
    // 统计信息
    getPlayerStats,
    
    // 辅助方法
    selectPet,
    clearError,
    resetStore
  }
})
