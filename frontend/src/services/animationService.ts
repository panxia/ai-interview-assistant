import { 
  AnimationConfig, 
  getPetAnimations, 
  getAnimationConfig, 
  getExpressionConfig, 
  getEffectConfig 
} from '../assets/animations/animationConfig'

// 动画缓存
const animationCache = new Map<string, any>()

// 简单的动画数据生成器 (临时替代方案)
function generateSimpleAnimation(animationId: string, petType: string): any {
  // 这里生成简单的动画数据，替代真实的Lottie文件
  // 在实际项目中，这些应该是由设计师制作的Lottie JSON文件
  
  const baseAnimation = {
    v: "5.7.4",
    fr: 24,
    ip: 0,
    op: 120,
    w: 300,
    h: 300,
    nm: `${petType}_${animationId}`,
    ddd: 0,
    assets: [],
    layers: []
  }
  
  // 根据动作类型生成不同的动画数据
  switch (animationId) {
    case 'idle':
      return {
        ...baseAnimation,
        op: 240, // 更长的循环
        layers: [
          {
            ddd: 0,
            ind: 1,
            ty: 4,
            nm: "idle_animation",
            sr: 1,
            ks: {
              o: { a: 0, k: 100 },
              r: { 
                a: 1, 
                k: [
                  { i: { x: [0.833], y: [0.833] }, o: { x: [0.167], y: [0.167] }, t: 0, s: [-2] },
                  { i: { x: [0.833], y: [0.833] }, o: { x: [0.167], y: [0.167] }, t: 120, s: [2] },
                  { t: 240, s: [-2] }
                ]
              },
              p: { a: 0, k: [150, 150, 0] },
              a: { a: 0, k: [0, 0, 0] },
              s: { 
                a: 1, 
                k: [
                  { i: { x: [0.833], y: [0.833] }, o: { x: [0.167], y: [0.167] }, t: 0, s: [98, 98, 100] },
                  { i: { x: [0.833], y: [0.833] }, o: { x: [0.167], y: [0.167] }, t: 120, s: [102, 102, 100] },
                  { t: 240, s: [98, 98, 100] }
                ]
              }
            }
          }
        ]
      }
      
    case 'happy':
      return {
        ...baseAnimation,
        op: 60,
        layers: [
          {
            ddd: 0,
            ind: 1,
            ty: 4,
            nm: "happy_animation",
            sr: 1,
            ks: {
              o: { a: 0, k: 100 },
              r: { 
                a: 1, 
                k: [
                  { i: { x: [0.833], y: [0.833] }, o: { x: [0.167], y: [0.167] }, t: 0, s: [0] },
                  { i: { x: [0.833], y: [0.833] }, o: { x: [0.167], y: [0.167] }, t: 10, s: [-10] },
                  { i: { x: [0.833], y: [0.833] }, o: { x: [0.167], y: [0.167] }, t: 20, s: [10] },
                  { i: { x: [0.833], y: [0.833] }, o: { x: [0.167], y: [0.167] }, t: 30, s: [-5] },
                  { i: { x: [0.833], y: [0.833] }, o: { x: [0.167], y: [0.167] }, t: 40, s: [5] },
                  { t: 60, s: [0] }
                ]
              },
              p: { 
                a: 1, 
                k: [
                  { i: { x: [0.833], y: [0.833] }, o: { x: [0.167], y: [0.167] }, t: 0, s: [150, 150, 0] },
                  { i: { x: [0.833], y: [0.833] }, o: { x: [0.167], y: [0.167] }, t: 15, s: [150, 140, 0] },
                  { i: { x: [0.833], y: [0.833] }, o: { x: [0.167], y: [0.167] }, t: 30, s: [150, 150, 0] },
                  { i: { x: [0.833], y: [0.833] }, o: { x: [0.167], y: [0.167] }, t: 45, s: [150, 140, 0] },
                  { t: 60, s: [150, 150, 0] }
                ]
              },
              a: { a: 0, k: [0, 0, 0] },
              s: { 
                a: 1, 
                k: [
                  { i: { x: [0.833], y: [0.833] }, o: { x: [0.167], y: [0.167] }, t: 0, s: [100, 100, 100] },
                  { i: { x: [0.833], y: [0.833] }, o: { x: [0.167], y: [0.167] }, t: 15, s: [110, 110, 100] },
                  { i: { x: [0.833], y: [0.833] }, o: { x: [0.167], y: [0.167] }, t: 30, s: [100, 100, 100] },
                  { i: { x: [0.833], y: [0.833] }, o: { x: [0.167], y: [0.167] }, t: 45, s: [110, 110, 100] },
                  { t: 60, s: [100, 100, 100] }
                ]
              }
            }
          }
        ]
      }
      
    case 'sleep':
      return {
        ...baseAnimation,
        op: 180,
        layers: [
          {
            ddd: 0,
            ind: 1,
            ty: 4,
            nm: "sleep_animation",
            sr: 1,
            ks: {
              o: { a: 0, k: 100 },
              r: { a: 0, k: 0 },
              p: { a: 0, k: [150, 165, 0] }, // 降低位置
              a: { a: 0, k: [0, 0, 0] },
              s: { 
                a: 1, 
                k: [
                  { i: { x: [0.833], y: [0.833] }, o: { x: [0.167], y: [0.167] }, t: 0, s: [100, 95, 100] },
                  { i: { x: [0.833], y: [0.833] }, o: { x: [0.167], y: [0.167] }, t: 90, s: [100, 105, 100] },
                  { t: 180, s: [100, 95, 100] }
                ]
              }
            }
          }
        ]
      }
      
    case 'walk':
      return {
        ...baseAnimation,
        op: 48,
        layers: [
          {
            ddd: 0,
            ind: 1,
            ty: 4,
            nm: "walk_animation",
            sr: 1,
            ks: {
              o: { a: 0, k: 100 },
              r: { 
                a: 1, 
                k: [
                  { i: { x: [0.833], y: [0.833] }, o: { x: [0.167], y: [0.167] }, t: 0, s: [0] },
                  { i: { x: [0.833], y: [0.833] }, o: { x: [0.167], y: [0.167] }, t: 12, s: [3] },
                  { i: { x: [0.833], y: [0.833] }, o: { x: [0.167], y: [0.167] }, t: 24, s: [0] },
                  { i: { x: [0.833], y: [0.833] }, o: { x: [0.167], y: [0.167] }, t: 36, s: [-3] },
                  { t: 48, s: [0] }
                ]
              },
              p: { 
                a: 1, 
                k: [
                  { i: { x: [0.833], y: [0.833] }, o: { x: [0.167], y: [0.167] }, t: 0, s: [150, 150, 0] },
                  { i: { x: [0.833], y: [0.833] }, o: { x: [0.167], y: [0.167] }, t: 12, s: [150, 145, 0] },
                  { i: { x: [0.833], y: [0.833] }, o: { x: [0.167], y: [0.167] }, t: 24, s: [150, 150, 0] },
                  { i: { x: [0.833], y: [0.833] }, o: { x: [0.167], y: [0.167] }, t: 36, s: [150, 145, 0] },
                  { t: 48, s: [150, 150, 0] }
                ]
              },
              a: { a: 0, k: [0, 0, 0] },
              s: { a: 0, k: [100, 100, 100] }
            }
          }
        ]
      }
      
    default:
      return baseAnimation
  }
}

// 动画服务类
export class AnimationService {
  // 获取宠物动画数据
  static async getPetAnimation(action: string, petType: string): Promise<any | null> {
    const cacheKey = `${petType}_${action}`
    
    // 检查缓存
    if (animationCache.has(cacheKey)) {
      return animationCache.get(cacheKey)
    }
    
    try {
      const config = getAnimationConfig(action, petType)
      if (!config) {
        console.warn(`Animation config not found for action: ${action}, petType: ${petType}`)
        return null
      }
      
      // 尝试加载真实的动画文件
      // 在开发阶段，我们使用生成的简单动画数据
      let animationData
      
      try {
        // 首先尝试加载真实的Lottie动画文件
        if (action === 'idle') {
          const response = await fetch('/animations/pet-idle.json')
          if (response.ok) {
            animationData = await response.json()
            console.log('Loaded real Lottie animation for idle')
          } else {
            throw new Error('Real animation not found')
          }
        } else {
          // 对于其他动作，尝试从配置路径加载
          const response = await fetch(config.path)
          if (response.ok) {
            animationData = await response.json()
          } else {
            throw new Error(`Failed to load animation: ${response.status}`)
          }
        }
      } catch (error) {
        console.log(`Using generated animation for ${action} - ${error}`)
        // 如果加载失败，使用生成的动画数据
        animationData = generateSimpleAnimation(action, petType)
      }
      
      // 缓存动画数据
      animationCache.set(cacheKey, animationData)
      return animationData
      
    } catch (error) {
      console.error('Error loading pet animation:', error)
      return null
    }
  }
  
  // 获取表情动画数据
  static async getExpressionAnimation(expression: string): Promise<any | null> {
    const cacheKey = `expression_${expression}`
    
    if (animationCache.has(cacheKey)) {
      return animationCache.get(cacheKey)
    }
    
    try {
      const config = getExpressionConfig(expression)
      if (!config) {
        return null
      }
      
      // 临时生成简单的表情动画
      const animationData = generateSimpleAnimation(`expression_${expression}`, 'common')
      animationCache.set(cacheKey, animationData)
      return animationData
      
    } catch (error) {
      console.error('Error loading expression animation:', error)
      return null
    }
  }
  
  // 获取特效动画数据
  static async getEffectAnimation(effect: string): Promise<any | null> {
    const cacheKey = `effect_${effect}`
    
    if (animationCache.has(cacheKey)) {
      return animationCache.get(cacheKey)
    }
    
    try {
      const config = getEffectConfig(effect)
      if (!config) {
        return null
      }
      
      // 临时生成简单的特效动画
      const animationData = generateSimpleAnimation(`effect_${effect}`, 'common')
      animationCache.set(cacheKey, animationData)
      return animationData
      
    } catch (error) {
      console.error('Error loading effect animation:', error)
      return null
    }
  }
  
  // 预加载动画
  static async preloadAnimations(petType: string): Promise<void> {
    const animations = getPetAnimations(petType)
    
    const loadPromises = animations.map(config => 
      this.getPetAnimation(config.id, petType)
    )
    
    try {
      await Promise.all(loadPromises)
      console.log(`Preloaded animations for ${petType}`)
    } catch (error) {
      console.error('Error preloading animations:', error)
    }
  }
  
  // 清除缓存
  static clearCache(): void {
    animationCache.clear()
  }
  
  // 获取缓存信息
  static getCacheInfo(): { size: number; keys: string[] } {
    return {
      size: animationCache.size,
      keys: Array.from(animationCache.keys())
    }
  }
}

export default AnimationService
