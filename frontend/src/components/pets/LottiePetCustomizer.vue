<template>
  <div class="lottie-pet-customizer">
    <!-- 左侧预览区域 -->
    <div class="preview-section">
      <div class="preview-container">
        <div class="preview-header">
          <h2 class="text-xl font-bold text-white mb-4">🎨 宠物工坊</h2>
          <div class="preview-controls">
            <button @click="randomizeAll" class="control-btn">
              🎲 随机生成
            </button>
            <button @click="resetAll" class="control-btn">
              🔄 重置
            </button>
          </div>
        </div>
        
        <!-- 3D风格预览窗口 -->
        <div class="preview-viewport">
          <!-- 宠物预览容器 -->
          <div class="pet-container" :class="{ 'animate-bounce': isAnimating }">
            <!-- 背景层 -->
            <div class="pet-background" :style="backgroundStyle"></div>
            
            <!-- 宠物主体 - 使用大型emoji作为基础 -->
            <div class="pet-main" :style="petMainStyle">
              <div class="pet-emoji" :style="petEmojiStyle">
                {{ currentPetEmoji }}
              </div>
              
              <!-- 装饰层 -->
              <div v-if="customization.hat !== 'none'" class="accessory hat" :style="hatStyle">
                {{ getAccessoryEmoji('hat') }}
              </div>
              
              <div v-if="customization.glasses !== 'none'" class="accessory glasses" :style="glassesStyle">
                {{ getAccessoryEmoji('glasses') }}
              </div>
              
              <div v-if="customization.collar !== 'none'" class="accessory collar" :style="collarStyle">
                {{ getAccessoryEmoji('collar') }}
              </div>
              
              <!-- 特效层 -->
              <div v-if="customization.hasGlow" class="effect glow" :style="glowStyle"></div>
              <div v-if="customization.hasSparkles" class="effect sparkles">
                <span v-for="i in 6" :key="i" class="sparkle" :style="getSparkleStyle(i)">✨</span>
              </div>
            </div>
            
            <!-- 情绪指示器 -->
            <div class="mood-indicator">
              <div class="mood-text">{{ currentMood }}</div>
              <div class="mood-emoji">{{ getMoodEmoji() }}</div>
            </div>
          </div>
          
          <!-- 预览信息 -->
          <div class="preview-info">
            <div class="info-item">
              <span class="label">名字:</span>
              <span class="value">{{ form.petName || '未命名' }}</span>
            </div>
            <div class="info-item">
              <span class="label">种类:</span>
              <span class="value">{{ getPetTypeName() }}</span>
            </div>
            <div class="info-item">
              <span class="label">性格:</span>
              <span class="value">{{ getPersonalityName() }}</span>
            </div>
          </div>
        </div>
        
        <!-- 快速预设 -->
        <div class="preset-section">
          <h3 class="text-lg font-semibold mb-3">🎭 风格预设</h3>
          <div class="preset-grid">
            <div 
              v-for="preset in presets" 
              :key="preset.id"
              @click="applyPreset(preset)"
              class="preset-card"
              :class="{ active: selectedPreset === preset.id }"
            >
              <div class="preset-preview">{{ preset.emoji }}</div>
              <div class="preset-name">{{ preset.name }}</div>
              <div class="preset-desc">{{ preset.description }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧控制面板 -->
    <div class="control-panel">
      <!-- 基本信息 -->
      <div class="panel-section active">
        <div class="section-header" @click="toggleSection('basic')">
          <h3>🏷️ 基本信息</h3>
          <span class="toggle-icon">{{ openSections.basic ? '▼' : '▶' }}</span>
        </div>
        <div v-show="openSections.basic" class="section-content">
          <div class="form-group">
            <label>宠物名称</label>
            <input 
              v-model="form.petName" 
              type="text" 
              placeholder="给宠物起个可爱的名字"
              class="form-input"
              @input="updatePreview"
            />
          </div>
          
          <div class="form-group">
            <label>宠物种类</label>
            <div class="species-grid">
              <div 
                v-for="type in petTypes" 
                :key="type.value"
                @click="selectSpecies(type.value)"
                class="species-card"
                :class="{ active: form.petType === type.value }"
              >
                <div class="species-icon">{{ type.emoji }}</div>
                <div class="species-name">{{ type.name }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 外观定制 -->
      <div class="panel-section">
        <div class="section-header" @click="toggleSection('appearance')">
          <h3>🎨 外观定制</h3>
          <span class="toggle-icon">{{ openSections.appearance ? '▼' : '▶' }}</span>
        </div>
        <div v-show="openSections.appearance" class="section-content">
          <!-- 表情选择 -->
          <div class="control-group">
            <label>表情</label>
            <div class="expression-grid">
              <button 
                v-for="expr in expressions"
                :key="expr.value"
                @click="selectExpression(expr.value)"
                class="expression-btn"
                :class="{ active: customization.expression === expr.value }"
                @mouseenter="previewExpression(expr.value)"
                @mouseleave="resetPreview"
              >
                <div class="expr-emoji">{{ expr.emoji }}</div>
                <div class="expr-name">{{ expr.name }}</div>
              </button>
            </div>
          </div>

          <!-- 大小调节 -->
          <div class="control-group">
            <label>大小</label>
            <div class="slider-container">
              <span class="slider-label">小</span>
              <input 
                type="range" 
                v-model="customization.size" 
                min="0.5" 
                max="2.0" 
                step="0.1"
                class="custom-slider"
                @input="updatePreview"
              />
              <span class="slider-label">大</span>
              <span class="slider-value">{{ customization.size }}x</span>
            </div>
          </div>

          <!-- 旋转角度 -->
          <div class="control-group">
            <label>角度</label>
            <div class="slider-container">
              <span class="slider-label">-45°</span>
              <input 
                type="range" 
                v-model="customization.rotation" 
                min="-45" 
                max="45" 
                step="5"
                class="custom-slider"
                @input="updatePreview"
              />
              <span class="slider-label">45°</span>
              <span class="slider-value">{{ customization.rotation }}°</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 颜色配置 -->
      <div class="panel-section">
        <div class="section-header" @click="toggleSection('colors')">
          <h3>🌈 颜色配置</h3>
          <span class="toggle-icon">{{ openSections.colors ? '▼' : '▶' }}</span>
        </div>
        <div v-show="openSections.colors" class="section-content">
          <div class="color-controls">
            <div class="color-group">
              <label>色调</label>
              <div class="slider-container">
                <input 
                  type="range" 
                  v-model="customization.hue" 
                  min="0" 
                  max="360" 
                  step="10"
                  class="custom-slider hue-slider"
                  @input="updatePreview"
                />
                <span class="slider-value">{{ customization.hue }}°</span>
              </div>
            </div>

            <div class="color-group">
              <label>饱和度</label>
              <div class="slider-container">
                <input 
                  type="range" 
                  v-model="customization.saturation" 
                  min="0" 
                  max="200" 
                  step="10"
                  class="custom-slider"
                  @input="updatePreview"
                />
                <span class="slider-value">{{ customization.saturation }}%</span>
              </div>
            </div>

            <div class="color-group">
              <label>亮度</label>
              <div class="slider-container">
                <input 
                  type="range" 
                  v-model="customization.brightness" 
                  min="50" 
                  max="150" 
                  step="5"
                  class="custom-slider"
                  @input="updatePreview"
                />
                <span class="slider-value">{{ customization.brightness }}%</span>
              </div>
            </div>

            <!-- 快速颜色预设 -->
            <div class="color-presets">
              <button 
                v-for="color in colorPresets" 
                :key="color.name"
                @click="applyColorPreset(color)"
                class="color-preset-btn"
                :style="{ background: color.gradient }"
                :title="color.name"
              >
                {{ color.emoji }}
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- 装饰配件 -->
      <div class="panel-section">
        <div class="section-header" @click="toggleSection('accessories')">
          <h3>👑 装饰配件</h3>
          <span class="toggle-icon">{{ openSections.accessories ? '▼' : '▶' }}</span>
        </div>
        <div v-show="openSections.accessories" class="section-content">
          <div class="accessory-category">
            <label>帽子</label>
            <div class="accessory-grid">
              <button 
                v-for="hat in accessories.hats" 
                :key="hat.value"
                @click="selectAccessory('hat', hat.value)"
                class="accessory-btn"
                :class="{ active: customization.hat === hat.value }"
              >
                <div class="acc-emoji">{{ hat.emoji }}</div>
                <div class="acc-name">{{ hat.name }}</div>
              </button>
            </div>
          </div>

          <div class="accessory-category">
            <label>眼镜</label>
            <div class="accessory-grid">
              <button 
                v-for="glasses in accessories.glasses" 
                :key="glasses.value"
                @click="selectAccessory('glasses', glasses.value)"
                class="accessory-btn"
                :class="{ active: customization.glasses === glasses.value }"
              >
                <div class="acc-emoji">{{ glasses.emoji }}</div>
                <div class="acc-name">{{ glasses.name }}</div>
              </button>
            </div>
          </div>

          <div class="accessory-category">
            <label>项圈</label>
            <div class="accessory-grid">
              <button 
                v-for="collar in accessories.collars" 
                :key="collar.value"
                @click="selectAccessory('collar', collar.value)"
                class="accessory-btn"
                :class="{ active: customization.collar === collar.value }"
              >
                <div class="acc-emoji">{{ collar.emoji }}</div>
                <div class="acc-name">{{ collar.name }}</div>
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- 特效 -->
      <div class="panel-section">
        <div class="section-header" @click="toggleSection('effects')">
          <h3>✨ 特殊效果</h3>
          <span class="toggle-icon">{{ openSections.effects ? '▼' : '▶' }}</span>
        </div>
        <div v-show="openSections.effects" class="section-content">
          <div class="effect-group">
            <label class="effect-toggle">
              <input 
                type="checkbox" 
                v-model="customization.hasGlow"
                @change="updatePreview"
              />
              <span class="toggle-slider"></span>
              <span class="toggle-label">✨ 发光效果</span>
            </label>
          </div>

          <div class="effect-group">
            <label class="effect-toggle">
              <input 
                type="checkbox" 
                v-model="customization.hasSparkles"
                @change="updatePreview"
              />
              <span class="toggle-slider"></span>
              <span class="toggle-label">⭐ 闪闪发光</span>
            </label>
          </div>

          <div class="effect-group">
            <label class="effect-toggle">
              <input 
                type="checkbox" 
                v-model="customization.isAnimated"
                @change="updatePreview"
              />
              <span class="toggle-slider"></span>
              <span class="toggle-label">🎭 动画效果</span>
            </label>
          </div>
        </div>
      </div>

      <!-- 性格设置 -->
      <div class="panel-section">
        <div class="section-header" @click="toggleSection('personality')">
          <h3>🎭 性格特征</h3>
          <span class="toggle-icon">{{ openSections.personality ? '▼' : '▶' }}</span>
        </div>
        <div v-show="openSections.personality" class="section-content">
          <div class="personality-grid">
            <div 
              v-for="personality in personalities" 
              :key="personality.type"
              @click="selectPersonality(personality.type)"
              class="personality-card"
              :class="{ active: form.personalityType === personality.type }"
            >
              <div class="personality-emoji">{{ personality.emoji }}</div>
              <div class="personality-name">{{ personality.name }}</div>
              <div class="personality-desc">{{ personality.description }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部操作栏 -->
    <div class="action-bar">
      <div class="action-left">
        <button @click="saveAsPreset" class="action-btn secondary">
          💾 保存预设
        </button>
        <button @click="shareDesign" class="action-btn secondary">
          📤 分享设计
        </button>
      </div>
      
      <div class="action-right">
        <button @click="$emit('cancel')" class="action-btn cancel">
          取消
        </button>
        <button 
          @click="createPet" 
          :disabled="!canSubmit || loading"
          class="action-btn primary"
        >
          <span v-if="loading">创建中...</span>
          <span v-else">✨ 创建宠物</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { usePetStore } from '../../store/index'
import type { Pet } from '../../types/index'

const emit = defineEmits<{
  'pet-created': [pet: Pet]
  'cancel': []
}>()

const petStore = usePetStore()
const loading = ref(false)
const isAnimating = ref(false)
const selectedPreset = ref('')

// 表单数据
const form = ref({
  petName: '',
  playerId: 'default',
  petType: '',
  personalityType: ''
})

// 定制选项
const customization = ref({
  expression: 'happy',
  size: 1.0,
  rotation: 0,
  hue: 0,
  saturation: 100,
  brightness: 100,
  hat: 'none',
  glasses: 'none',
  collar: 'none',
  hasGlow: false,
  hasSparkles: false,
  isAnimated: true
})

// 界面状态
const openSections = ref({
  basic: true,
  appearance: false,
  colors: false,
  accessories: false,
  effects: false,
  personality: false
})

// 宠物类型数据
const petTypes = [
  { value: 'CAT', name: '小猫', emoji: '🐱' },
  { value: 'DOG', name: '小狗', emoji: '🐶' },
  { value: 'RABBIT', name: '兔子', emoji: '🐰' },
  { value: 'PANDA', name: '熊猫', emoji: '🐼' },
  { value: 'HAMSTER', name: '仓鼠', emoji: '🐹' },
  { value: 'BIRD', name: '小鸟', emoji: '🐦' }
]

// 表情数据
const expressions = [
  { value: 'happy', name: '开心', emoji: '😊' },
  { value: 'excited', name: '兴奋', emoji: '🤩' },
  { value: 'cute', name: '可爱', emoji: '🥰' },
  { value: 'sleepy', name: '困倦', emoji: '😴' },
  { value: 'playful', name: '顽皮', emoji: '😜' },
  { value: 'cool', name: '酷酷', emoji: '😎' }
]

// 装饰配件
const accessories = {
  hats: [
    { value: 'none', name: '无', emoji: '🚫' },
    { value: 'cap', name: '帽子', emoji: '🧢' },
    { value: 'crown', name: '皇冠', emoji: '👑' },
    { value: 'bow', name: '蝴蝶结', emoji: '🎀' }
  ],
  glasses: [
    { value: 'none', name: '无', emoji: '🚫' },
    { value: 'normal', name: '眼镜', emoji: '👓' },
    { value: 'sun', name: '墨镜', emoji: '🕶️' },
    { value: 'cute', name: '萌镜', emoji: '🤓' }
  ],
  collars: [
    { value: 'none', name: '无', emoji: '🚫' },
    { value: 'basic', name: '项圈', emoji: '🔗' },
    { value: 'bell', name: '铃铛', emoji: '🔔' },
    { value: 'bow', name: '蝴蝶结', emoji: '🎀' }
  ]
}

// 颜色预设
const colorPresets = [
  { name: '原色', emoji: '🎨', hue: 0, saturation: 100, brightness: 100, gradient: 'linear-gradient(45deg, #ff6b6b, #4ecdc4)' },
  { name: '粉色', emoji: '🌸', hue: 330, saturation: 80, brightness: 120, gradient: 'linear-gradient(45deg, #ff9a9e, #fecfef)' },
  { name: '蓝色', emoji: '💙', hue: 200, saturation: 90, brightness: 110, gradient: 'linear-gradient(45deg, #a8edea, #fed6e3)' },
  { name: '绿色', emoji: '💚', hue: 120, saturation: 70, brightness: 110, gradient: 'linear-gradient(45deg, #d299c2, #fef9d7)' },
  { name: '紫色', emoji: '💜', hue: 280, saturation: 85, brightness: 115, gradient: 'linear-gradient(45deg, #89f7fe, #66a6ff)' },
  { name: '橙色', emoji: '🧡', hue: 30, saturation: 90, brightness: 120, gradient: 'linear-gradient(45deg, #fdbb2d, #22c1c3)' }
]

// 性格数据
const personalities = [
  { type: 'PLAYFUL', name: '活泼', emoji: '🎾', description: '喜欢玩耍' },
  { type: 'LAZY', name: '慵懒', emoji: '😴', description: '爱睡觉' },
  { type: 'CURIOUS', name: '好奇', emoji: '🔍', description: '爱探索' },
  { type: 'AFFECTIONATE', name: '亲人', emoji: '💕', description: '很粘人' },
  { type: 'INDEPENDENT', name: '独立', emoji: '🦅', description: '很自主' },
  { type: 'INTELLIGENT', name: '聪明', emoji: '🧠', description: '很机智' }
]

// 风格预设
const presets = [
  { 
    id: 'cute', 
    name: '可爱风', 
    emoji: '🥰', 
    description: '粉嫩可爱',
    config: { hue: 330, saturation: 80, brightness: 120, hasSparkles: true }
  },
  { 
    id: 'cool', 
    name: '酷炫风', 
    emoji: '😎', 
    description: '帅气十足',
    config: { hue: 200, saturation: 90, brightness: 90, glasses: 'sun' }
  },
  { 
    id: 'elegant', 
    name: '优雅风', 
    emoji: '👑', 
    description: '高贵典雅',
    config: { hue: 280, saturation: 70, brightness: 110, hat: 'crown' }
  },
  { 
    id: 'playful', 
    name: '活泼风', 
    emoji: '🎾', 
    description: '活力四射',
    config: { hue: 120, saturation: 100, brightness: 120, isAnimated: true }
  }
]

// 计算属性
const canSubmit = computed(() => {
  return form.value.petName.trim() && form.value.petType && form.value.personalityType
})

const currentPetEmoji = computed(() => {
  const type = petTypes.find(t => t.value === form.value.petType)
  if (!type) return '🐱'
  
  // 根据表情调整emoji
  const baseEmoji = type.emoji
  const expr = customization.value.expression
  
  // 不同表情的emoji映射
  const emojiMap: Record<string, Record<string, string>> = {
    'CAT': {
      'happy': '😸',
      'excited': '😻',
      'cute': '😽',
      'sleepy': '😴',
      'playful': '😹',
      'cool': '😎'
    },
    'DOG': {
      'happy': '🐶',
      'excited': '🤩',
      'cute': '🥰',
      'sleepy': '😴',
      'playful': '😜',
      'cool': '😎'
    }
  }
  
  return emojiMap[form.value.petType]?.[expr] || baseEmoji
})

const currentMood = computed(() => {
  const expr = expressions.find(e => e.value === customization.value.expression)
  return expr?.name || '开心'
})

const petMainStyle = computed(() => ({
  transform: `scale(${customization.value.size}) rotate(${customization.value.rotation}deg)`,
  filter: `hue-rotate(${customization.value.hue}deg) saturate(${customization.value.saturation}%) brightness(${customization.value.brightness}%)`,
  transition: 'all 0.3s ease'
}))

const petEmojiStyle = computed(() => ({
  fontSize: '120px',
  animation: customization.value.isAnimated ? 'petBounce 2s ease-in-out infinite' : 'none'
}))

const backgroundStyle = computed(() => ({
  background: `radial-gradient(circle, rgba(${Math.sin(customization.value.hue * Math.PI / 180) * 127 + 128}, ${Math.cos(customization.value.hue * Math.PI / 180) * 127 + 128}, 255, 0.1) 0%, transparent 70%)`
}))

const hatStyle = computed(() => ({
  position: 'absolute',
  top: '-20px',
  left: '50%',
  transform: 'translateX(-50%)',
  fontSize: '40px',
  zIndex: 10
}))

const glassesStyle = computed(() => ({
  position: 'absolute',
  top: '30px',
  left: '50%',
  transform: 'translateX(-50%)',
  fontSize: '35px',
  zIndex: 5
}))

const collarStyle = computed(() => ({
  position: 'absolute',
  bottom: '10px',
  left: '50%',
  transform: 'translateX(-50%)',
  fontSize: '25px',
  zIndex: 3
}))

const glowStyle = computed(() => ({
  position: 'absolute',
  top: '50%',
  left: '50%',
  transform: 'translate(-50%, -50%)',
  width: '200px',
  height: '200px',
  background: `radial-gradient(circle, rgba(255, 255, 0, 0.3) 0%, transparent 70%)`,
  borderRadius: '50%',
  animation: 'glow 2s ease-in-out infinite alternate',
  pointerEvents: 'none'
}))

// 方法
const toggleSection = (section: string) => {
  openSections.value[section as keyof typeof openSections.value] = !openSections.value[section as keyof typeof openSections.value]
}

const selectSpecies = (type: string) => {
  form.value.petType = type
  updatePreview()
}

const selectExpression = (expr: string) => {
  customization.value.expression = expr
  updatePreview()
}

const selectAccessory = (type: string, value: string) => {
  (customization.value as any)[type] = value
  updatePreview()
}

const selectPersonality = (type: string) => {
  form.value.personalityType = type
}

const previewExpression = (expr: string) => {
  // 临时预览表情
}

const resetPreview = () => {
  // 重置预览
}

const updatePreview = () => {
  // 触发动画
  isAnimating.value = true
  setTimeout(() => {
    isAnimating.value = false
  }, 600)
}

const applyColorPreset = (color: any) => {
  customization.value.hue = color.hue
  customization.value.saturation = color.saturation
  customization.value.brightness = color.brightness
  updatePreview()
}

const applyPreset = (preset: any) => {
  selectedPreset.value = preset.id
  Object.assign(customization.value, preset.config)
  updatePreview()
}

const randomizeAll = () => {
  // 随机化所有设置
  form.value.petType = petTypes[Math.floor(Math.random() * petTypes.length)].value
  customization.value.expression = expressions[Math.floor(Math.random() * expressions.length)].value
  customization.value.size = Math.random() * 1.5 + 0.5
  customization.value.rotation = Math.random() * 90 - 45
  customization.value.hue = Math.random() * 360
  customization.value.saturation = Math.random() * 100 + 50
  customization.value.brightness = Math.random() * 50 + 75
  
  updatePreview()
}

const resetAll = () => {
  customization.value = {
    expression: 'happy',
    size: 1.0,
    rotation: 0,
    hue: 0,
    saturation: 100,
    brightness: 100,
    hat: 'none',
    glasses: 'none',
    collar: 'none',
    hasGlow: false,
    hasSparkles: false,
    isAnimated: true
  }
  updatePreview()
}

const getAccessoryEmoji = (type: string) => {
  const accessory = (accessories as any)[type + 's'].find((item: any) => item.value === (customization.value as any)[type])
  return accessory?.emoji || ''
}

const getMoodEmoji = () => {
  const expr = expressions.find(e => e.value === customization.value.expression)
  return expr?.emoji || '😊'
}

const getPetTypeName = () => {
  const type = petTypes.find(t => t.value === form.value.petType)
  return type?.name || '未选择'
}

const getPersonalityName = () => {
  const personality = personalities.find(p => p.type === form.value.personalityType)
  return personality?.name || '未选择'
}

const getSparkleStyle = (index: number) => ({
  position: 'absolute',
  top: `${Math.random() * 100}%`,
  left: `${Math.random() * 100}%`,
  fontSize: '20px',
  animation: `sparkle 1.5s ease-in-out infinite`,
  animationDelay: `${index * 0.2}s`
})

const saveAsPreset = () => {
  // 保存为预设
  console.log('保存预设')
}

const shareDesign = () => {
  // 分享设计
  console.log('分享设计')
}

const createPet = async () => {
  try {
    loading.value = true
    const petData = {
      playerId: form.value.playerId,
      petName: form.value.petName.trim(),
      petType: form.value.petType,
      customization: {
        ...customization.value,
        personalityType: form.value.personalityType
      }
    }
    
    const newPet = await petStore.createPet(petData)
    emit('pet-created', newPet)
  } catch (error) {
    console.error('创建宠物失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  // 默认选择第一个宠物类型
  if (petTypes.length > 0) {
    form.value.petType = petTypes[0].value
  }
  if (personalities.length > 0) {
    form.value.personalityType = personalities[0].type
  }
})
</script>

<style scoped>
.lottie-pet-customizer {
  @apply flex h-screen bg-gradient-to-br from-indigo-900 via-purple-900 to-pink-900 text-white;
}

/* 左侧预览区域 */
.preview-section {
  @apply w-2/5 p-6;
}

.preview-container {
  @apply h-full flex flex-col;
}

.preview-header {
  @apply mb-6;
}

.preview-controls {
  @apply flex space-x-3;
}

.control-btn {
  @apply px-4 py-2 bg-white/10 hover:bg-white/20 rounded-lg text-sm transition-all backdrop-blur-sm;
}

.preview-viewport {
  @apply flex-1 bg-gradient-to-b from-blue-500/20 to-purple-500/20 rounded-3xl p-8 mb-6 relative overflow-hidden backdrop-blur-sm border border-white/10;
}

.pet-container {
  @apply relative w-full h-full flex items-center justify-center;
}

.pet-background {
  @apply absolute inset-0 rounded-full;
}

.pet-main {
  @apply relative z-10;
}

.pet-emoji {
  @apply text-center leading-none select-none;
}

.accessory {
  @apply absolute;
}

.effect.glow {
  @apply absolute;
}

.effect.sparkles {
  @apply absolute inset-0;
}

.sparkle {
  @apply absolute;
}

.mood-indicator {
  @apply absolute bottom-4 left-4 bg-black/30 rounded-lg p-3 backdrop-blur-sm;
}

.mood-text {
  @apply text-sm font-medium;
}

.mood-emoji {
  @apply text-xl text-center mt-1;
}

.preview-info {
  @apply absolute top-4 right-4 bg-black/30 rounded-lg p-3 backdrop-blur-sm space-y-1;
}

.info-item {
  @apply flex items-center space-x-2 text-sm;
}

.label {
  @apply text-gray-300;
}

.value {
  @apply font-medium;
}

.preset-section {
  @apply bg-black/20 rounded-xl p-4 backdrop-blur-sm;
}

.preset-grid {
  @apply grid grid-cols-2 gap-3;
}

.preset-card {
  @apply bg-white/10 hover:bg-white/20 rounded-lg p-3 cursor-pointer transition-all text-center;
}

.preset-card.active {
  @apply bg-purple-500/30 ring-2 ring-purple-400;
}

.preset-preview {
  @apply text-2xl mb-1;
}

.preset-name {
  @apply font-semibold text-sm;
}

.preset-desc {
  @apply text-xs text-gray-300;
}

/* 右侧控制面板 */
.control-panel {
  @apply w-3/5 bg-black/30 backdrop-blur-sm overflow-y-auto;
}

.panel-section {
  @apply border-b border-white/10;
}

.section-header {
  @apply flex items-center justify-between p-4 cursor-pointer hover:bg-white/5 transition-colors;
}

.section-header h3 {
  @apply text-lg font-semibold;
}

.toggle-icon {
  @apply text-gray-400;
}

.section-content {
  @apply p-4;
}

.form-group {
  @apply mb-6;
}

.form-group label {
  @apply block text-sm font-medium mb-3;
}

.form-input {
  @apply w-full px-4 py-3 bg-white/10 border border-white/20 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent backdrop-blur-sm;
}

.species-grid {
  @apply grid grid-cols-3 gap-3;
}

.species-card {
  @apply bg-white/10 hover:bg-white/20 rounded-lg p-3 cursor-pointer transition-all text-center;
}

.species-card.active {
  @apply bg-purple-500/30 ring-2 ring-purple-400;
}

.species-icon {
  @apply text-3xl mb-1;
}

.species-name {
  @apply text-sm font-medium;
}

.control-group {
  @apply mb-6;
}

.control-group label {
  @apply block text-sm font-medium mb-3;
}

.expression-grid {
  @apply grid grid-cols-3 gap-2;
}

.expression-btn {
  @apply bg-white/10 hover:bg-white/20 rounded-lg p-3 cursor-pointer transition-all text-center;
}

.expression-btn.active {
  @apply bg-purple-500/30 ring-2 ring-purple-400;
}

.expr-emoji {
  @apply text-2xl mb-1;
}

.expr-name {
  @apply text-xs;
}

.slider-container {
  @apply flex items-center space-x-3;
}

.custom-slider {
  @apply flex-1 h-2 bg-white/20 rounded-lg appearance-none cursor-pointer;
}

.custom-slider::-webkit-slider-thumb {
  @apply appearance-none w-5 h-5 bg-purple-500 rounded-full cursor-pointer shadow-lg;
}

.hue-slider {
  @apply bg-gradient-to-r from-red-500 via-yellow-500 via-green-500 via-blue-500 to-purple-500;
}

.slider-label {
  @apply text-xs text-gray-300 min-w-max;
}

.slider-value {
  @apply text-sm font-mono bg-white/10 px-2 py-1 rounded min-w-max;
}

.color-presets {
  @apply grid grid-cols-6 gap-2 mt-4;
}

.color-preset-btn {
  @apply w-12 h-12 rounded-lg text-xl cursor-pointer hover:scale-110 transition-transform border-2 border-white/20;
}

.accessory-category {
  @apply mb-6;
}

.accessory-category label {
  @apply block text-sm font-medium mb-3;
}

.accessory-grid {
  @apply grid grid-cols-4 gap-2;
}

.accessory-btn {
  @apply bg-white/10 hover:bg-white/20 rounded-lg p-2 cursor-pointer transition-all text-center;
}

.accessory-btn.active {
  @apply bg-purple-500/30 ring-2 ring-purple-400;
}

.acc-emoji {
  @apply text-xl mb-1;
}

.acc-name {
  @apply text-xs;
}

.effect-group {
  @apply mb-4;
}

.effect-toggle {
  @apply flex items-center cursor-pointer;
}

.effect-toggle input {
  @apply sr-only;
}

.toggle-slider {
  @apply relative w-12 h-6 bg-white/20 rounded-full transition-colors mr-3;
}

.toggle-slider::before {
  @apply content-[''] absolute top-1 left-1 w-4 h-4 bg-white rounded-full transition-transform;
}

.effect-toggle input:checked + .toggle-slider {
  @apply bg-purple-500;
}

.effect-toggle input:checked + .toggle-slider::before {
  @apply transform translate-x-6;
}

.toggle-label {
  @apply text-sm;
}

.personality-grid {
  @apply grid grid-cols-2 gap-3;
}

.personality-card {
  @apply bg-white/10 hover:bg-white/20 rounded-lg p-3 cursor-pointer transition-all;
}

.personality-card.active {
  @apply bg-purple-500/30 ring-2 ring-purple-400;
}

.personality-emoji {
  @apply text-2xl mb-2;
}

.personality-name {
  @apply font-semibold mb-1;
}

.personality-desc {
  @apply text-xs text-gray-300;
}

/* 底部操作栏 */
.action-bar {
  @apply fixed bottom-0 left-0 right-0 bg-black/50 backdrop-blur-sm border-t border-white/10 p-4 flex items-center justify-between;
}

.action-left {
  @apply flex space-x-3;
}

.action-right {
  @apply flex space-x-3;
}

.action-btn {
  @apply px-6 py-3 rounded-lg font-semibold transition-all;
}

.action-btn.secondary {
  @apply bg-white/10 hover:bg-white/20 text-white;
}

.action-btn.cancel {
  @apply bg-red-500/80 hover:bg-red-500 text-white;
}

.action-btn.primary {
  @apply bg-gradient-to-r from-purple-500 to-pink-500 hover:from-purple-600 hover:to-pink-600 text-white;
}

.action-btn:disabled {
  @apply opacity-50 cursor-not-allowed;
}

/* 动画 */
@keyframes petBounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

@keyframes glow {
  0% { opacity: 0.3; transform: translate(-50%, -50%) scale(0.8); }
  100% { opacity: 0.6; transform: translate(-50%, -50%) scale(1.2); }
}

@keyframes sparkle {
  0%, 100% { opacity: 0; transform: scale(0); }
  50% { opacity: 1; transform: scale(1); }
}

.animate-bounce {
  animation: petBounce 0.6s ease-in-out;
}

/* 滚动条样式 */
.control-panel::-webkit-scrollbar {
  @apply w-2;
}

.control-panel::-webkit-scrollbar-track {
  @apply bg-transparent;
}

.control-panel::-webkit-scrollbar-thumb {
  @apply bg-white/20 rounded-full hover:bg-white/30;
}
</style>
