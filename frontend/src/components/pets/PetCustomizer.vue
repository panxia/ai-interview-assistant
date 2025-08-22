<template>
  <div class="pet-customizer">
    <!-- 左侧预览区域 -->
    <div class="preview-section">
      <div class="preview-container">
        <div class="preview-header">
          <h2 class="text-xl font-bold text-white">宠物预览</h2>
          <div class="preview-controls">
            <button @click="resetView" class="control-btn">
              🔄 重置视角
            </button>
            <button @click="randomize" class="control-btn">
              🎲 随机生成
            </button>
          </div>
        </div>
        
        <!-- 3D风格预览区域 -->
        <div class="preview-viewport">
          <PetPreview 
            :pet-data="previewData" 
            size="large"
            class="pet-3d-preview"
          />
          
          <!-- 预览灯光控制 -->
          <div class="lighting-controls">
            <div class="light-control">
              <label>环境光</label>
              <input 
                type="range" 
                v-model="lightSettings.ambient" 
                min="0" 
                max="100" 
                class="light-slider"
              />
            </div>
          </div>
        </div>
        
        <!-- 预设样式 -->
        <div class="preset-section">
          <h3 class="text-lg font-semibold mb-3">快速预设</h3>
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
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧控制面板 -->
    <div class="control-panel">
      <!-- 基本信息 -->
      <div class="panel-section">
        <div class="section-header">
          <h3>🏷️ 基本信息</h3>
        </div>
        <div class="section-content">
          <div class="form-group">
            <label>宠物名称</label>
            <input 
              v-model="form.petName" 
              type="text" 
              placeholder="输入宠物名称"
              class="form-input"
            />
          </div>
          
          <div class="form-group">
            <label>宠物种类</label>
            <div class="species-grid">
              <div 
                v-for="type in petStore.petTypes" 
                :key="type.value"
                @click="selectSpecies(type.value)"
                class="species-card"
                :class="{ active: form.petType === type.value }"
              >
                <div class="species-icon">{{ type.emoji }}</div>
                <div class="species-name">{{ type.displayName }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 面部特征 -->
      <div class="panel-section">
        <div class="section-header">
          <h3>😊 面部特征</h3>
          <button @click="randomizeFace" class="section-btn">随机</button>
        </div>
        <div class="section-content">
          <!-- 头部形状 -->
          <div class="control-group">
            <label>头部形状</label>
            <div class="slider-container">
              <div class="shape-options">
                <button 
                  v-for="shape in headShapes" 
                  :key="shape.value"
                  @click="form.customization.headShape = shape.value"
                  class="shape-btn"
                  :class="{ active: form.customization.headShape === shape.value }"
                >
                  {{ shape.icon }}
                </button>
              </div>
            </div>
          </div>

          <!-- 眼睛 -->
          <div class="control-group">
            <label>眼睛类型</label>
            <select v-model="form.customization.eyeType" class="form-select">
              <option value="normal">👁️ 普通</option>
              <option value="big">👀 大眼睛</option>
              <option value="small">😊 小眼睛</option>
              <option value="sleepy">😴 眯眯眼</option>
            </select>
          </div>

          <div class="control-group">
            <label>眼睛大小</label>
            <div class="slider-container">
              <input 
                type="range" 
                v-model="eyeCustomization.size" 
                min="0.5" 
                max="2.0" 
                step="0.1"
                class="custom-slider"
              />
              <span class="slider-value">{{ eyeCustomization.size }}</span>
            </div>
          </div>

          <div class="control-group">
            <label>眼距</label>
            <div class="slider-container">
              <input 
                type="range" 
                v-model="eyeCustomization.distance" 
                min="0.7" 
                max="1.3" 
                step="0.05"
                class="custom-slider"
              />
              <span class="slider-value">{{ eyeCustomization.distance }}</span>
            </div>
          </div>

          <!-- 耳朵 -->
          <div class="control-group">
            <label>耳朵样式</label>
            <select v-model="form.customization.earStyle" class="form-select">
              <option value="pointed">👂 尖耳朵</option>
              <option value="round">⭕ 圆耳朵</option>
              <option value="droopy">🐶 垂耳朵</option>
            </select>
          </div>

          <!-- 嘴巴 -->
          <div class="control-group">
            <label>嘴巴表情</label>
            <div class="expression-grid">
              <button 
                v-for="expr in expressions"
                :key="expr.value"
                @click="form.customization.mouthExpression = expr.value"
                class="expression-btn"
                :class="{ active: form.customization.mouthExpression === expr.value }"
              >
                {{ expr.icon }}
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- 身体比例 -->
      <div class="panel-section">
        <div class="section-header">
          <h3>📏 身体比例</h3>
          <button @click="resetProportions" class="section-btn">重置</button>
        </div>
        <div class="section-content">
          <div class="control-group">
            <label>身材胖瘦</label>
            <div class="slider-container">
              <span class="slider-label">瘦</span>
              <input 
                type="range" 
                v-model="form.customization.bodyProportion.fatness" 
                min="0.5" 
                max="2.0" 
                step="0.1"
                class="custom-slider"
              />
              <span class="slider-label">胖</span>
              <span class="slider-value">{{ form.customization.bodyProportion.fatness }}</span>
            </div>
          </div>

          <div class="control-group">
            <label>身高</label>
            <div class="slider-container">
              <span class="slider-label">矮</span>
              <input 
                type="range" 
                v-model="form.customization.bodyProportion.height" 
                min="0.6" 
                max="1.5" 
                step="0.05"
                class="custom-slider"
              />
              <span class="slider-label">高</span>
              <span class="slider-value">{{ form.customization.bodyProportion.height }}</span>
            </div>
          </div>

          <div class="control-group">
            <label>头部大小</label>
            <div class="slider-container">
              <span class="slider-label">小</span>
              <input 
                type="range" 
                v-model="form.customization.bodyProportion.headSize" 
                min="0.6" 
                max="1.4" 
                step="0.05"
                class="custom-slider"
              />
              <span class="slider-label">大</span>
              <span class="slider-value">{{ form.customization.bodyProportion.headSize }}</span>
            </div>
          </div>

          <div class="control-group">
            <label>四肢长度</label>
            <div class="slider-container">
              <span class="slider-label">短</span>
              <input 
                type="range" 
                v-model="form.customization.bodyProportion.limbLength" 
                min="0.5" 
                max="1.3" 
                step="0.05"
                class="custom-slider"
              />
              <span class="slider-label">长</span>
              <span class="slider-value">{{ form.customization.bodyProportion.limbLength }}</span>
            </div>
          </div>

          <div class="control-group">
            <label>尾巴长度</label>
            <div class="slider-container">
              <span class="slider-label">短</span>
              <input 
                type="range" 
                v-model="form.customization.bodyProportion.tailLength" 
                min="0.3" 
                max="1.5" 
                step="0.05"
                class="custom-slider"
              />
              <span class="slider-label">长</span>
              <span class="slider-value">{{ form.customization.bodyProportion.tailLength }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 颜色配置 -->
      <div class="panel-section">
        <div class="section-header">
          <h3>🎨 颜色配置</h3>
          <button @click="randomizeColors" class="section-btn">随机</button>
        </div>
        <div class="section-content">
          <div class="color-grid">
            <div class="color-group">
              <label>主要颜色</label>
              <div class="color-input-container">
                <input 
                  type="color" 
                  v-model="form.customization.primaryColor"
                  class="color-input"
                />
                <div class="color-presets">
                  <button 
                    v-for="color in primaryColors" 
                    :key="color"
                    @click="form.customization.primaryColor = color"
                    class="color-preset"
                    :style="{ backgroundColor: color }"
                  ></button>
                </div>
              </div>
            </div>

            <div class="color-group">
              <label>次要颜色</label>
              <div class="color-input-container">
                <input 
                  type="color" 
                  v-model="form.customization.secondaryColor"
                  class="color-input"
                />
                <div class="color-presets">
                  <button 
                    v-for="color in secondaryColors" 
                    :key="color"
                    @click="form.customization.secondaryColor = color"
                    class="color-preset"
                    :style="{ backgroundColor: color }"
                  ></button>
                </div>
              </div>
            </div>

            <div class="color-group">
              <label>左眼颜色</label>
              <input 
                type="color" 
                v-model="form.customization.eyeColorLeft"
                class="color-input"
              />
            </div>

            <div class="color-group">
              <label>右眼颜色</label>
              <input 
                type="color" 
                v-model="form.customization.eyeColorRight"
                class="color-input"
              />
              <button 
                @click="syncEyeColors"
                class="sync-btn"
              >
                🔗 同步
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- 装饰配件 -->
      <div class="panel-section">
        <div class="section-header">
          <h3>👑 装饰配件</h3>
          <button @click="clearAccessories" class="section-btn">清除</button>
        </div>
        <div class="section-content">
          <div class="accessory-category">
            <label>帽子</label>
            <div class="accessory-grid">
              <button 
                v-for="hat in accessories.hats" 
                :key="hat.value"
                @click="form.customization.hat = hat.value"
                class="accessory-btn"
                :class="{ active: form.customization.hat === hat.value }"
              >
                {{ hat.icon }}
              </button>
            </div>
          </div>

          <div class="accessory-category">
            <label>项圈</label>
            <div class="accessory-grid">
              <button 
                v-for="collar in accessories.collars" 
                :key="collar.value"
                @click="form.customization.collar = collar.value"
                class="accessory-btn"
                :class="{ active: form.customization.collar === collar.value }"
              >
                {{ collar.icon }}
              </button>
            </div>
          </div>

          <div class="accessory-category">
            <label>眼镜</label>
            <div class="accessory-grid">
              <button 
                v-for="glasses in accessories.glasses" 
                :key="glasses.value"
                @click="form.customization.glasses = glasses.value"
                class="accessory-btn"
                :class="{ active: form.customization.glasses === glasses.value }"
              >
                {{ glasses.icon }}
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- 特效 -->
      <div class="panel-section">
        <div class="section-header">
          <h3>✨ 特殊效果</h3>
        </div>
        <div class="section-content">
          <div class="effect-group">
            <label class="effect-toggle">
              <input 
                type="checkbox" 
                v-model="form.customization.hasGlow"
              />
              <span class="toggle-slider"></span>
              <span class="toggle-label">发光效果</span>
            </label>
            <input 
              v-if="form.customization.hasGlow"
              type="color" 
              v-model="form.customization.glowColor"
              class="color-input small"
            />
          </div>

          <div class="effect-group">
            <label class="effect-toggle">
              <input 
                type="checkbox" 
                v-model="form.customization.hasSparkles"
              />
              <span class="toggle-slider"></span>
              <span class="toggle-label">闪闪发光</span>
            </label>
          </div>
        </div>
      </div>

      <!-- 性格设置 -->
      <div class="panel-section">
        <div class="section-header">
          <h3>🎭 性格特征</h3>
        </div>
        <div class="section-content">
          <div class="personality-grid">
            <div 
              v-for="personality in petStore.personalities" 
              :key="personality.type"
              @click="form.personalityType = personality.type"
              class="personality-card"
              :class="{ active: form.personalityType === personality.type }"
            >
              <div class="personality-name">{{ personality.displayName }}</div>
              <div class="personality-desc">{{ personality.description }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部操作栏 -->
    <div class="action-bar">
      <div class="action-left">
        <button @click="loadPreset" class="action-btn secondary">
          📁 载入预设
        </button>
        <button @click="savePreset" class="action-btn secondary">
          💾 保存预设
        </button>
      </div>
      
      <div class="action-right">
        <button @click="$emit('cancel')" class="action-btn cancel">
          取消
        </button>
        <button 
          @click="createPet" 
          :disabled="!canSubmit || petStore.loading"
          class="action-btn primary"
        >
          <span v-if="petStore.loading">创建中...</span>
          <span v-else>✨ 创建宠物</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { usePetStore } from '../../store/index'
import type { Pet } from '../../types/index'
import PetPreview from './PetPreview.vue'

const emit = defineEmits<{
  'pet-created': [pet: Pet]
  'cancel': []
}>()

const petStore = usePetStore()
const selectedPreset = ref('')

// 表单数据
const form = ref({
  petName: '',
  playerId: 'default',
  petType: '',
  personalityType: '',
  customization: {
    headShape: 'round',
    earStyle: 'pointed',
    eyeType: 'normal',
    mouthExpression: 'smile',
    primaryColor: '#FFA500',
    secondaryColor: '#FFFFFF',
    eyeColorLeft: '#4169E1',
    eyeColorRight: '#4169E1',
    noseColor: '#FFB6C1',
    pattern: 'none',
    patternColor: '#000000',
    hat: 'none',
    collar: 'none',
    glasses: 'none',
    hasGlow: false,
    glowColor: '#FFFF00',
    hasSparkles: false,
    bodyProportion: {
      fatness: 1.0,
      height: 1.0,
      headSize: 1.0,
      limbLength: 1.0,
      tailLength: 1.0
    }
  }
})

// 眼睛定制
const eyeCustomization = ref({
  size: 1.0,
  distance: 1.0
})

// 灯光设置
const lightSettings = ref({
  ambient: 60
})

// 配置数据
const headShapes = [
  { value: 'round', icon: '🔴' },
  { value: 'oval', icon: '🥚' },
  { value: 'square', icon: '⬜' },
  { value: 'heart', icon: '💖' }
]

const expressions = [
  { value: 'smile', icon: '😊' },
  { value: 'happy', icon: '😄' },
  { value: 'cute', icon: '😚' },
  { value: 'serious', icon: '😐' }
]

const primaryColors = ['#FFA500', '#FF6B6B', '#4ECDC4', '#45B7D1', '#96CEB4', '#FFEAA7']
const secondaryColors = ['#FFFFFF', '#F8F8FF', '#FFF8DC', '#E6E6FA', '#F0F8FF', '#FFFACD']

const accessories = {
  hats: [
    { value: 'none', icon: '🚫' },
    { value: 'cap', icon: '🧢' },
    { value: 'bow', icon: '🎀' },
    { value: 'crown', icon: '👑' }
  ],
  collars: [
    { value: 'none', icon: '🚫' },
    { value: 'basic', icon: '🔗' },
    { value: 'bow', icon: '🎀' },
    { value: 'bell', icon: '🔔' }
  ],
  glasses: [
    { value: 'none', icon: '🚫' },
    { value: 'normal', icon: '👓' },
    { value: 'sun', icon: '🕶️' },
    { value: 'cute', icon: '🤓' }
  ]
}

const presets = [
  { id: 'cute', name: '可爱型', emoji: '🐱', config: { /* preset config */ } },
  { id: 'cool', name: '酷炫型', emoji: '😎', config: { /* preset config */ } },
  { id: 'elegant', name: '优雅型', emoji: '👑', config: { /* preset config */ } },
  { id: 'playful', name: '活泼型', emoji: '🎾', config: { /* preset config */ } }
]

// 计算属性
const canSubmit = computed(() => {
  return form.value.petName.trim() && 
         form.value.petType && 
         form.value.personalityType
})

const previewData = computed(() => ({
  petName: form.value.petName,
  petType: form.value.petType,
  personalityType: form.value.personalityType,
  ...form.value.customization
}))

// 方法
const selectSpecies = (type: string) => {
  form.value.petType = type
  // 根据物种应用默认配置
  applySpeciesDefaults(type)
}

const applySpeciesDefaults = (type: string) => {
  const defaults = {
    'CAT': { earStyle: 'pointed', primaryColor: '#FFA500' },
    'DOG': { earStyle: 'droopy', primaryColor: '#8B4513' },
    'RABBIT': { earStyle: 'droopy', primaryColor: '#FFFFFF' },
    // ... 更多默认配置
  }
  
  if (defaults[type as keyof typeof defaults]) {
    Object.assign(form.value.customization, defaults[type as keyof typeof defaults])
  }
}

const randomizeFace = () => {
  form.value.customization.headShape = headShapes[Math.floor(Math.random() * headShapes.length)].value
  form.value.customization.eyeType = ['normal', 'big', 'small'][Math.floor(Math.random() * 3)]
  form.value.customization.earStyle = ['pointed', 'round', 'droopy'][Math.floor(Math.random() * 3)]
  form.value.customization.mouthExpression = expressions[Math.floor(Math.random() * expressions.length)].value
}

const resetProportions = () => {
  form.value.customization.bodyProportion = {
    fatness: 1.0,
    height: 1.0,
    headSize: 1.0,
    limbLength: 1.0,
    tailLength: 1.0
  }
}

const randomizeColors = () => {
  form.value.customization.primaryColor = primaryColors[Math.floor(Math.random() * primaryColors.length)]
  form.value.customization.secondaryColor = secondaryColors[Math.floor(Math.random() * secondaryColors.length)]
  form.value.customization.eyeColorLeft = `#${Math.floor(Math.random()*16777215).toString(16)}`
  form.value.customization.eyeColorRight = form.value.customization.eyeColorLeft
}

const syncEyeColors = () => {
  form.value.customization.eyeColorRight = form.value.customization.eyeColorLeft
}

const clearAccessories = () => {
  form.value.customization.hat = 'none'
  form.value.customization.collar = 'none'
  form.value.customization.glasses = 'none'
}

const randomize = () => {
  randomizeFace()
  randomizeColors()
  // 随机化身体比例
  const randomProportion = () => Math.random() * 0.8 + 0.6
  form.value.customization.bodyProportion = {
    fatness: randomProportion(),
    height: randomProportion(),
    headSize: randomProportion(),
    limbLength: randomProportion(),
    tailLength: randomProportion()
  }
}

const resetView = () => {
  // 重置预览视角
}

const applyPreset = (preset: any) => {
  selectedPreset.value = preset.id
  // 应用预设配置
}

const loadPreset = () => {
  // 从本地存储载入预设
}

const savePreset = () => {
  // 保存当前配置为预设
}

const createPet = async () => {
  try {
    const petData = {
      playerId: form.value.playerId,
      petName: form.value.petName.trim(),
      petType: form.value.petType,
      customization: {
        ...form.value.customization,
        personalityType: form.value.personalityType
      }
    }
    
    const newPet = await petStore.createPet(petData)
    emit('pet-created', newPet)
  } catch (error) {
    console.error('创建宠物失败:', error)
  }
}

onMounted(async () => {
  // 加载数据
  try {
    if (!petStore.petTypes.length) {
      await petStore.getPetTypes()
    }
    if (!petStore.personalities.length) {
      await petStore.getPersonalities()
    }
  } catch (error) {
    console.error('加载数据失败:', error)
    // 使用默认数据
    if (!petStore.petTypes.length) {
      petStore.petTypes = [
        { value: 'CAT', displayName: '小猫咪', description: '可爱的小猫', emoji: '🐱' },
        { value: 'DOG', displayName: '小狗狗', description: '忠诚的伙伴', emoji: '🐶' },
        { value: 'RABBIT', displayName: '小兔子', description: '温顺的朋友', emoji: '🐰' },
        { value: 'BIRD', displayName: '小鸟儿', description: '自由的精灵', emoji: '🐦' },
        { value: 'FISH', displayName: '小鱼儿', description: '安静的陪伴', emoji: '🐠' },
        { value: 'HAMSTER', displayName: '小仓鼠', description: '活泼的小家伙', emoji: '🐹' },
        { value: 'TURTLE', displayName: '小乌龟', description: '长寿的智者', emoji: '🐢' },
        { value: 'DRAGON', displayName: '小龙', description: '神秘的传说', emoji: '🐲' },
        { value: 'PANDA', displayName: '小熊猫', description: '憨厚的国宝', emoji: '🐼' }
      ]
    }
    if (!petStore.personalities.length) {
      petStore.personalities = [
        { type: 'PLAYFUL', displayName: '活泼型', description: '喜欢玩耍，精力充沛' },
        { type: 'LAZY', displayName: '慵懒型', description: '喜欢睡觉，动作缓慢' },
        { type: 'CURIOUS', displayName: '好奇型', description: '喜欢探索，对新事物充满兴趣' },
        { type: 'AFFECTIONATE', displayName: '亲人型', description: '非常依恋主人，喜欢被抚摸' },
        { type: 'INDEPENDENT', displayName: '独立型', description: '喜欢独处，自主性强' },
        { type: 'INTELLIGENT', displayName: '聪明型', description: '学习能力强，游戏表现优秀' },
        { type: 'TIMID', displayName: '胆小型', description: '容易害怕，需要更多安慰' },
        { type: 'BRAVE', displayName: '勇敢型', description: '不畏惧挑战，喜欢冒险' },
        { type: 'FOODIE', displayName: '吃货型', description: '对食物特别感兴趣' },
        { type: 'BALANCED', displayName: '均衡型', description: '各方面都比较平衡，适应性强' }
      ]
    }
  }
})
</script>

<style scoped>
.pet-customizer {
  @apply flex h-screen bg-gray-900 text-white;
}

/* 左侧预览区域 */
.preview-section {
  @apply w-2/5 bg-gradient-to-br from-gray-800 to-gray-900 p-6;
}

.preview-container {
  @apply h-full flex flex-col;
}

.preview-header {
  @apply flex items-center justify-between mb-4;
}

.control-btn {
  @apply px-3 py-2 bg-gray-700 hover:bg-gray-600 rounded-lg text-sm transition-colors mr-2;
}

.preview-viewport {
  @apply flex-1 bg-gradient-to-b from-blue-900 to-purple-900 rounded-2xl p-8 mb-6 relative overflow-hidden;
  background-image: 
    radial-gradient(circle at 25% 25%, rgba(120, 119, 198, 0.3) 0%, transparent 50%),
    radial-gradient(circle at 75% 75%, rgba(255, 119, 198, 0.3) 0%, transparent 50%);
}

.pet-3d-preview {
  @apply transform transition-all duration-300;
}

.lighting-controls {
  @apply absolute bottom-4 left-4 right-4;
}

.light-control {
  @apply flex items-center space-x-2 text-sm;
}

.light-slider {
  @apply flex-1 h-2 bg-gray-600 rounded-lg appearance-none;
}

.preset-section {
  @apply bg-gray-800 rounded-xl p-4;
}

.preset-grid {
  @apply grid grid-cols-4 gap-3;
}

.preset-card {
  @apply bg-gray-700 hover:bg-gray-600 rounded-lg p-3 cursor-pointer transition-all text-center;
}

.preset-card.active {
  @apply bg-purple-600 ring-2 ring-purple-400;
}

.preset-preview {
  @apply text-2xl mb-1;
}

.preset-name {
  @apply text-xs;
}

/* 右侧控制面板 */
.control-panel {
  @apply w-3/5 bg-gray-800 overflow-y-auto;
}

.panel-section {
  @apply border-b border-gray-700;
}

.section-header {
  @apply flex items-center justify-between p-4 bg-gray-700;
}

.section-header h3 {
  @apply text-lg font-semibold;
}

.section-btn {
  @apply px-3 py-1 bg-gray-600 hover:bg-gray-500 rounded text-sm transition-colors;
}

.section-content {
  @apply p-4;
}

.form-group {
  @apply mb-4;
}

.form-group label {
  @apply block text-sm font-medium mb-2;
}

.form-input {
  @apply w-full px-4 py-2 bg-gray-700 border border-gray-600 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent;
}

.form-select {
  @apply w-full px-4 py-2 bg-gray-700 border border-gray-600 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent;
}

.species-grid {
  @apply grid grid-cols-3 gap-3;
}

.species-card {
  @apply bg-gray-700 hover:bg-gray-600 rounded-lg p-3 cursor-pointer transition-all text-center;
}

.species-card.active {
  @apply bg-purple-600 ring-2 ring-purple-400;
}

.species-icon {
  @apply text-2xl mb-1;
}

.species-name {
  @apply text-sm;
}

.control-group {
  @apply mb-6;
}

.control-group label {
  @apply block text-sm font-medium mb-3;
}

.slider-container {
  @apply flex items-center space-x-3;
}

.custom-slider {
  @apply flex-1 h-2 bg-gray-600 rounded-lg appearance-none cursor-pointer;
}

.custom-slider::-webkit-slider-thumb {
  @apply appearance-none w-4 h-4 bg-purple-500 rounded-full cursor-pointer;
}

.slider-label {
  @apply text-xs text-gray-400 min-w-max;
}

.slider-value {
  @apply text-sm font-mono bg-gray-700 px-2 py-1 rounded min-w-max;
}

.shape-options {
  @apply flex space-x-2;
}

.shape-btn {
  @apply w-12 h-12 bg-gray-700 hover:bg-gray-600 rounded-lg text-2xl transition-all;
}

.shape-btn.active {
  @apply bg-purple-600 ring-2 ring-purple-400;
}

.expression-grid {
  @apply grid grid-cols-4 gap-2;
}

.expression-btn {
  @apply w-12 h-12 bg-gray-700 hover:bg-gray-600 rounded-lg text-2xl transition-all;
}

.expression-btn.active {
  @apply bg-purple-600 ring-2 ring-purple-400;
}

.color-grid {
  @apply grid grid-cols-2 gap-4;
}

.color-group {
  @apply space-y-2;
}

.color-group label {
  @apply text-sm font-medium;
}

.color-input-container {
  @apply space-y-2;
}

.color-input {
  @apply w-full h-12 rounded-lg border-2 border-gray-600 cursor-pointer;
}

.color-input.small {
  @apply w-16 h-8;
}

.color-presets {
  @apply flex space-x-2;
}

.color-preset {
  @apply w-8 h-8 rounded-full border-2 border-gray-600 cursor-pointer hover:scale-110 transition-transform;
}

.sync-btn {
  @apply px-2 py-1 bg-gray-600 hover:bg-gray-500 rounded text-xs transition-colors;
}

.accessory-category {
  @apply mb-4;
}

.accessory-category label {
  @apply block text-sm font-medium mb-2;
}

.accessory-grid {
  @apply grid grid-cols-4 gap-2;
}

.accessory-btn {
  @apply w-12 h-12 bg-gray-700 hover:bg-gray-600 rounded-lg text-2xl transition-all;
}

.accessory-btn.active {
  @apply bg-purple-600 ring-2 ring-purple-400;
}

.effect-group {
  @apply flex items-center justify-between mb-4;
}

.effect-toggle {
  @apply flex items-center cursor-pointer;
}

.effect-toggle input {
  @apply sr-only;
}

.toggle-slider {
  @apply relative w-12 h-6 bg-gray-600 rounded-full transition-colors mr-3;
}

.toggle-slider::before {
  @apply content-[''] absolute top-1 left-1 w-4 h-4 bg-white rounded-full transition-transform;
}

.effect-toggle input:checked + .toggle-slider {
  @apply bg-purple-600;
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
  @apply bg-gray-700 hover:bg-gray-600 rounded-lg p-3 cursor-pointer transition-all;
}

.personality-card.active {
  @apply bg-purple-600 ring-2 ring-purple-400;
}

.personality-name {
  @apply font-semibold mb-1;
}

.personality-desc {
  @apply text-xs text-gray-300;
}

/* 底部操作栏 */
.action-bar {
  @apply fixed bottom-0 left-0 right-0 bg-gray-800 border-t border-gray-700 p-4 flex items-center justify-between;
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
  @apply bg-gray-600 hover:bg-gray-500 text-white;
}

.action-btn.cancel {
  @apply bg-red-600 hover:bg-red-500 text-white;
}

.action-btn.primary {
  @apply bg-gradient-to-r from-purple-600 to-pink-600 hover:from-purple-700 hover:to-pink-700 text-white;
}

.action-btn:disabled {
  @apply opacity-50 cursor-not-allowed;
}

/* 滚动条样式 */
.control-panel::-webkit-scrollbar {
  @apply w-2;
}

.control-panel::-webkit-scrollbar-track {
  @apply bg-gray-800;
}

.control-panel::-webkit-scrollbar-thumb {
  @apply bg-gray-600 rounded-full hover:bg-gray-500;
}
</style>
