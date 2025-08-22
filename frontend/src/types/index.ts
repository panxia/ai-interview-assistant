// 宠物相关类型
export interface Pet {
  id: string
  petId?: string // 兼容后端字段
  playerId: string
  petName?: string // 兼容后端字段
  name?: string
  type: PetType
  petType?: PetType // 兼容后端字段
  createdAt: string
  lastInteraction: string
  stats: PetStats
  appearance: PetAppearance
  personality: PetPersonality
  animation?: PetAnimation
  color?: string
  birthDate?: string
  isAsleep?: boolean
  mood?: string
  growthStage?: string
}

export enum PetType {
  CAT = 'CAT',
  DOG = 'DOG',
  RABBIT = 'RABBIT',
  HAMSTER = 'HAMSTER',
  DRAGON = 'DRAGON',
  PANDA = 'PANDA'
}

export interface PetStats {
  level: number
  experience: number
  health: number
  happiness: number
  energy: number
  hunger: number
  cleanliness: number
  intelligence: number
  strength: number
  agility: number
  loyalty: number
}

export interface PetAppearance {
  headShape: string
  earStyle: string
  eyeType: string
  mouthExpression: string
  primaryColor: string
  secondaryColor: string
  eyeColorLeft: string
  eyeColorRight: string
  noseColor: string
  pattern: string
  patternColor: string
  hat: string
  collar: string
  glasses: string
  hasGlow: boolean
  glowColor: string
  hasSparkles: boolean
  bodyProportionFatness: number
  bodyProportionHeight: number
  bodyProportionHeadSize: number
  bodyProportionLimbLength: number
  bodyProportionTailLength: number
}

export interface PetPersonality {
  personalityType: string
  playfulness: number
  laziness: number
  curiosity: number
  affection: number
  independence: number
  intelligence: number
}

export interface PetAnimation {
  currentAnimation: string
  animationSpeed: number
  isPlaying: boolean
}

// 成就相关类型
export interface Achievement {
  id: string
  achievementId?: string // 兼容后端字段
  name?: string // 兼容后端字段
  achievementName?: string
  description: string
  emoji?: string
  type?: string
  targetValue?: number
  currentProgress?: number
  unlocked: boolean
  unlockedAt?: string
  unlockedDate?: string
  points?: number
  reward?: any
}

// 游戏物品类型
export interface GameItem {
  id: string
  itemId?: string // 兼容后端字段
  name?: string // 兼容后端字段
  itemName?: string
  emoji?: string
  description: string
  type?: string
  itemType?: string
  cost?: number
  rarity?: string
  quantity?: number
  isUnlocked?: boolean
  effect?: any
}

// 玩家统计类型
export interface PlayerStats {
  totalPets: number
  totalAchievements: number
  totalItems: number
  totalExperience: number
  totalAchievementPoints: number
  highestLevel: number
  highestLevelPetName: string
}

// 宠物类型信息
export interface PetTypeInfo {
  value: string
  displayName: string
  emoji: string
  description: string
}

// 性格类型信息
export interface PersonalityTypeInfo {
  type: string
  displayName: string
  description: string
}

// API响应类型
export interface ApiResponse<T> {
  success: boolean
  message: string
  data: T
}

// 创建宠物请求
export interface CreatePetRequest {
  playerId: string
  petName: string
  petType: string
  customization: Record<string, any>
}

// 喂食请求
export interface FeedRequest {
  itemId: string
}

// 玩耍请求
export interface PlayRequest {
  activityType: string
}

// 开始游戏请求
export interface StartGameRequest {
  gameType: string
}

// 完成游戏请求
export interface CompleteGameRequest {
  gameType: string
  score: number
}

// 游戏数据
export interface GameData {
  gameType: string
  petId: string
  sequence?: string[]
  difficulty?: number
  targets?: any[]
  timeLimit?: number
  pattern?: number[]
  speed?: number
}
