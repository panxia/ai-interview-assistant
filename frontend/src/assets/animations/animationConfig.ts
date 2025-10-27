// 动画配置文件
export interface AnimationConfig {
  id: string
  name: string
  path: string
  loop: boolean
  autoplay: boolean
  duration?: number
  description?: string
}

// 宠物动作动画配置
export const petAnimations: Record<string, AnimationConfig[]> = {
  // 基础动作 - 所有宠物类型共用
  common: [
    {
      id: 'idle',
      name: '待机',
      path: '/animations/pets/common/idle.json',
      loop: true,
      autoplay: true,
      description: '默认待机状态'
    },
    {
      id: 'happy',
      name: '开心',
      path: '/animations/pets/common/happy.json',
      loop: true,
      autoplay: true,
      duration: 2000,
      description: '开心表情动画'
    },
    {
      id: 'sleep',
      name: '睡觉',
      path: '/animations/pets/common/sleep.json',
      loop: true,
      autoplay: true,
      description: '睡觉动画'
    },
    {
      id: 'eat',
      name: '吃饭',
      path: '/animations/pets/common/eat.json',
      loop: false,
      autoplay: true,
      duration: 3000,
      description: '吃食物动画'
    },
    {
      id: 'play',
      name: '玩耍',
      path: '/animations/pets/common/play.json',
      loop: true,
      autoplay: true,
      description: '玩耍动画'
    },
    {
      id: 'walk',
      name: '走路',
      path: '/animations/pets/common/walk.json',
      loop: true,
      autoplay: true,
      description: '走路动画'
    },
    {
      id: 'jump',
      name: '跳跃',
      path: '/animations/pets/common/jump.json',
      loop: false,
      autoplay: true,
      duration: 1500,
      description: '跳跃动画'
    }
  ],
  
  // 猫咪特定动画
  cat: [
    {
      id: 'stretch',
      name: '伸懒腰',
      path: '/animations/pets/cat/stretch.json',
      loop: false,
      autoplay: true,
      duration: 2500,
      description: '猫咪伸懒腰'
    },
    {
      id: 'groom',
      name: '舔毛',
      path: '/animations/pets/cat/groom.json',
      loop: true,
      autoplay: true,
      description: '猫咪舔毛动画'
    }
  ],
  
  // 狗狗特定动画
  dog: [
    {
      id: 'wag',
      name: '摆尾',
      path: '/animations/pets/dog/wag.json',
      loop: true,
      autoplay: true,
      description: '狗狗摆尾巴'
    },
    {
      id: 'bark',
      name: '叫声',
      path: '/animations/pets/dog/bark.json',
      loop: false,
      autoplay: true,
      duration: 1000,
      description: '狗狗叫声动画'
    }
  ],
  
  // 兔子特定动画
  rabbit: [
    {
      id: 'hop',
      name: '蹦跳',
      path: '/animations/pets/rabbit/hop.json',
      loop: true,
      autoplay: true,
      description: '兔子蹦跳'
    },
    {
      id: 'ears_twitch',
      name: '耳朵抽动',
      path: '/animations/pets/rabbit/ears_twitch.json',
      loop: true,
      autoplay: true,
      description: '兔子耳朵抽动'
    }
  ],
  
  // 仓鼠特定动画
  hamster: [
    {
      id: 'cheek_stuff',
      name: '塞腮帮',
      path: '/animations/pets/hamster/cheek_stuff.json',
      loop: false,
      autoplay: true,
      duration: 2000,
      description: '仓鼠塞腮帮子'
    },
    {
      id: 'wheel_run',
      name: '跑轮',
      path: '/animations/pets/hamster/wheel_run.json',
      loop: true,
      autoplay: true,
      description: '仓鼠跑轮动画'
    }
  ],
  
  // 熊猫特定动画
  panda: [
    {
      id: 'roll',
      name: '翻滚',
      path: '/animations/pets/panda/roll.json',
      loop: false,
      autoplay: true,
      duration: 3000,
      description: '熊猫翻滚'
    },
    {
      id: 'bamboo_eat',
      name: '吃竹子',
      path: '/animations/pets/panda/bamboo_eat.json',
      loop: true,
      autoplay: true,
      description: '熊猫吃竹子'
    }
  ]
}

// 表情动画配置
export const expressionAnimations: AnimationConfig[] = [
  {
    id: 'cute',
    name: '可爱',
    path: '/animations/expressions/cute.json',
    loop: true,
    autoplay: true,
    description: '可爱表情'
  },
  {
    id: 'surprised',
    name: '惊讶',
    path: '/animations/expressions/surprised.json',
    loop: false,
    autoplay: true,
    duration: 1500,
    description: '惊讶表情'
  },
  {
    id: 'angry',
    name: '生气',
    path: '/animations/expressions/angry.json',
    loop: true,
    autoplay: true,
    description: '生气表情'
  },
  {
    id: 'love',
    name: '爱心',
    path: '/animations/expressions/love.json',
    loop: true,
    autoplay: true,
    description: '爱心表情'
  }
]

// 特效动画配置
export const effectAnimations: AnimationConfig[] = [
  {
    id: 'sparkles',
    name: '闪光',
    path: '/animations/effects/sparkles.json',
    loop: true,
    autoplay: true,
    description: '闪光特效'
  },
  {
    id: 'hearts',
    name: '爱心',
    path: '/animations/effects/hearts.json',
    loop: true,
    autoplay: true,
    description: '爱心特效'
  },
  {
    id: 'stars',
    name: '星星',
    path: '/animations/effects/stars.json',
    loop: true,
    autoplay: true,
    description: '星星特效'
  },
  {
    id: 'glow',
    name: '光环',
    path: '/animations/effects/glow.json',
    loop: true,
    autoplay: true,
    description: '光环特效'
  }
]

// 获取指定宠物类型的动画
export function getPetAnimations(petType: string): AnimationConfig[] {
  const typeKey = petType.toLowerCase()
  const commonAnims = petAnimations.common || []
  const specificAnims = petAnimations[typeKey] || []
  return [...commonAnims, ...specificAnims]
}

// 根据动作名称获取动画配置
export function getAnimationConfig(action: string, petType: string): AnimationConfig | null {
  const animations = getPetAnimations(petType)
  return animations.find(anim => anim.id === action) || null
}

// 获取表情动画配置
export function getExpressionConfig(expression: string): AnimationConfig | null {
  return expressionAnimations.find(anim => anim.id === expression) || null
}

// 获取特效动画配置
export function getEffectConfig(effect: string): AnimationConfig | null {
  return effectAnimations.find(anim => anim.id === effect) || null
}
