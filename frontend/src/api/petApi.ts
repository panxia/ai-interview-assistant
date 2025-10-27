import axios from 'axios'
import type { 
  Pet, 
  PetTypeInfo, 
  PersonalityTypeInfo, 
  Achievement, 
  GameItem, 
  PlayerStats,
  CreatePetRequest,
  FeedRequest,
  PlayRequest,
  StartGameRequest,
  CompleteGameRequest,
  GameData,
  ApiResponse
} from '../types/index'

// 创建axios实例
const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
api.interceptors.request.use(
  (config) => {
    console.log('API Request:', config.method?.toUpperCase(), config.url)
    return config
  },
  (error) => {
    console.error('API Request Error:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  (response) => {
    console.log('API Response:', response.status, response.config.url)
    return response.data
  },
  (error) => {
    console.error('API Response Error:', error.response?.data || error.message)
    return Promise.reject(error.response?.data || error)
  }
)

// 宠物管理API
export const petApi = {
  // 创建新宠物
  async createPet(request: CreatePetRequest): Promise<Pet> {
    const response: ApiResponse<Pet> = await api.post('/pets/create', request)
    if (!response.success) {
      throw new Error(response.message)
    }
    return response.data
  },

  // 获取单个宠物信息
  async getPet(petId: string): Promise<Pet> {
    const response: ApiResponse<Pet> = await api.get(`/pets/${petId}`)
    if (!response.success) {
      throw new Error(response.message)
    }
    return response.data
  },

  // 获取玩家的所有宠物
  async getPlayerPets(playerId: string): Promise<Pet[]> {
    const response: ApiResponse<Pet[]> = await api.get(`/pets/player/${playerId}`)
    if (!response.success) {
      throw new Error(response.message)
    }
    return response.data
  },

  // 获取宠物类型列表
  async getPetTypes(): Promise<PetTypeInfo[]> {
    const response: ApiResponse<PetTypeInfo[]> = await api.get('/pets/types')
    if (!response.success) {
      throw new Error(response.message)
    }
    return response.data
  },

  // 获取性格类型列表
  async getPersonalities(): Promise<PersonalityTypeInfo[]> {
    const response: ApiResponse<PersonalityTypeInfo[]> = await api.get('/pets/personalities')
    if (!response.success) {
      throw new Error(response.message)
    }
    return response.data
  },

  // 喂食宠物
  async feedPet(petId: string, itemId: string): Promise<Pet> {
    const response: ApiResponse<Pet> = await api.post(`/pets/${petId}/feed`, { itemId })
    if (!response.success) {
      throw new Error(response.message)
    }
    return response.data
  },

  // 与宠物玩耍
  async playWithPet(petId: string, activityType: string): Promise<Pet> {
    const response: ApiResponse<Pet> = await api.post(`/pets/${petId}/play`, { activityType })
    if (!response.success) {
      throw new Error(response.message)
    }
    return response.data
  },

  // 清洁宠物
  async cleanPet(petId: string): Promise<Pet> {
    const response: ApiResponse<Pet> = await api.post(`/pets/${petId}/clean`)
    if (!response.success) {
      throw new Error(response.message)
    }
    return response.data
  },

  // 宠物休息
  async restPet(petId: string): Promise<Pet> {
    const response: ApiResponse<Pet> = await api.post(`/pets/${petId}/rest`)
    if (!response.success) {
      throw new Error(response.message)
    }
    return response.data
  },

  // 开始小游戏
  async startMiniGame(petId: string, gameType: string): Promise<GameData> {
    const response: ApiResponse<GameData> = await api.post(`/pets/${petId}/minigames/start`, { gameType })
    if (!response.success) {
      throw new Error(response.message)
    }
    return response.data
  },

  // 完成小游戏
  async completeMiniGame(petId: string, gameType: string, score: number): Promise<Pet> {
    const response: ApiResponse<Pet> = await api.post(`/pets/${petId}/minigames/complete`, { gameType, score })
    if (!response.success) {
      throw new Error(response.message)
    }
    return response.data
  },

  // 获取玩家成就
  async getPlayerAchievements(playerId: string): Promise<Achievement[]> {
    const response: ApiResponse<Achievement[]> = await api.get(`/pets/player/${playerId}/achievements`)
    if (!response.success) {
      throw new Error(response.message)
    }
    return response.data
  },

  // 获取玩家背包
  async getPlayerInventory(playerId: string): Promise<GameItem[]> {
    const response: ApiResponse<GameItem[]> = await api.get(`/pets/player/${playerId}/inventory`)
    if (!response.success) {
      throw new Error(response.message)
    }
    return response.data
  },

  // 获取玩家统计信息
  async getPlayerStats(playerId: string): Promise<PlayerStats> {
    const response: ApiResponse<PlayerStats> = await api.get(`/pets/player/${playerId}/stats`)
    if (!response.success) {
      throw new Error(response.message)
    }
    return response.data
  },

  // 健康检查
  async healthCheck(): Promise<any> {
    return await api.get('/pets/health')
  }
}

// 导出API实例
export default petApi
