<template>
  <div class="cartoon-pet-customizer">
    <!-- 左侧预览区域 -->
    <div class="preview-section">
      <div class="preview-container">
        <div class="preview-header">
          <h2 class="text-2xl font-bold text-white mb-4">🎨 宠物工坊</h2>
          <div class="preview-controls">
            <button @click="randomizeAll" class="control-btn">
              🎲 随机生成
            </button>
            <button @click="resetAll" class="control-btn">
              🔄 重置
            </button>
            <button @click="toggleAnimation" class="control-btn">
              {{ isAnimated ? '⏸️ 暂停' : '▶️ 播放' }}
            </button>
          </div>
        </div>
        
        <!-- 主预览窗口 -->
        <div class="preview-viewport">
          <div class="preview-stage">
            <!-- 背景装饰 -->
            <div class="stage-background" :style="stageBackgroundStyle"></div>
            
            <!-- 宠物渲染器 - 独特特征版 -->
            <DistinctivePetRenderer 
              :pet-data="previewData" 
              :size="300"
              :animated="isAnimated"
              :current-action="currentPetAction"
              class="main-pet"
              @action-change="handleActionChange"
            />
            
            <!-- 预览信息卡片 -->
            <div class="info-card">
              <div class="info-row">
                <span class="info-label">名字:</span>
                <span class="info-value">{{ form.petName || '未命名' }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">种类:</span>
                <span class="info-value">{{ getPetTypeName() }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">心情:</span>
                <span class="info-value">{{ getExpressionName() }}</span>
              </div>
            </div>
            
            <!-- 实时调整提示 -->
            <div v-if="isAdjusting" class="adjustment-hint">
              <div class="hint-text">{{ adjustmentHint }}</div>
            </div>
          </div>
          
          <!-- 动作控制 -->
          <div class="action-control-panel">
            <h4 class="text-sm font-semibold text-white mb-2">🎭 动作控制</h4>
            <div class="action-grid">
              <button 
                v-for="action in quickActions" 
                :key="action.name"
                @click="changeAction(action.name)"
                class="action-quick-btn"
                :class="{ active: currentPetAction === action.name }"
              >
                <span class="action-icon">{{ action.icon }}</span>
                <span class="action-label">{{ action.label }}</span>
              </button>
            </div>
          </div>
          
          <!-- 交互说明 -->
          <div class="interaction-guide">
            <h4 class="text-sm font-semibold text-white mb-2">🎮 新功能说明</h4>
            <div class="guide-items">
              <div class="guide-item">
                <span class="guide-icon">🎭</span>
                <span class="guide-text">点击动作按钮看宠物表演</span>
              </div>
              <div class="guide-item">
                <span class="guide-icon">🎬</span>
                <span class="guide-text">支持多种动作和表情</span>
              </div>
              <div class="guide-item">
                <span class="guide-icon">⚙️</span>
                <span class="guide-text">右上角控制动画播放</span>
              </div>
            </div>
          </div>
          
          <!-- 预览控制 -->
          <div class="preview-controls-panel">
            <div class="control-group">
              <label>预览角度</label>
              <input 
                type="range" 
                v-model="previewRotation" 
                min="-45" 
                max="45" 
                step="5"
                class="angle-slider"
              />
            </div>
            
            <div class="control-group">
              <label>预览大小</label>
              <input 
                type="range" 
                v-model="previewScale" 
                min="0.5" 
                max="1.5" 
                step="0.1"
                class="scale-slider"
              />
            </div>
          </div>
        </div>
        
        <!-- 快速预设 -->
        <div class="preset-section">
          <h3 class="text-lg font-semibold mb-3">🎭 快速预设</h3>
          <div class="preset-grid">
            <div 
              v-for="preset in presets" 
              :key="preset.id"
              @click="applyPreset(preset)"
              class="preset-card"
              :class="{ active: selectedPreset === preset.id }"
            >
              <div class="preset-preview">
                <DistinctivePetRenderer 
                  :pet-data="preset.previewData" 
                  :size="60"
                  :animated="true"
                />
              </div>
              <div class="preset-info">
                <div class="preset-name">{{ preset.name }}</div>
                <div class="preset-desc">{{ preset.description }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧控制面板 -->
    <div class="control-panel">
      <!-- 基本信息 -->
      <div class="panel-section" :class="{ active: activeSection === 'basic' }">
        <div class="section-header" @click="setActiveSection('basic')">
          <h3>🏷️ 基本信息</h3>
          <span class="section-indicator">{{ activeSection === 'basic' ? '▼' : '▶' }}</span>
        </div>
        <div v-show="activeSection === 'basic'" class="section-content">
          <div class="form-group">
            <label>宠物名称</label>
            <input 
              v-model="form.petName" 
              type="text" 
              placeholder="给宠物起个可爱的名字"
              class="form-input"
              @input="updatePreview('名称')"
            />
          </div>
          
          <div class="form-group">
            <label>宠物种类</label>
            <div class="species-selector">
              <div 
                v-for="type in petTypes" 
                :key="type.value"
                @click="selectSpecies(type.value)"
                class="species-option"
                :class="{ active: form.petType === type.value }"
              >
                <div class="species-preview">
                  <DistinctivePetRenderer 
                    :pet-data="{ ...defaultPetData, petType: type.value }" 
                    :size="50"
                    :animated="true"
                  />
                </div>
                <div class="species-name">{{ type.name }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 外观定制 -->
      <div class="panel-section" :class="{ active: activeSection === 'appearance' }">
        <div class="section-header" @click="setActiveSection('appearance')">
          <h3>😊 外观定制</h3>
          <span class="section-indicator">{{ activeSection === 'appearance' ? '▼' : '▶' }}</span>
        </div>
        <div v-show="activeSection === 'appearance'" class="section-content">
          <!-- 表情选择 -->
          <div class="control-group">
            <label>表情</label>
            <div class="expression-selector">
              <button 
                v-for="expr in expressions"
                :key="expr.value"
                @click="selectExpression(expr.value)"
                @mouseenter="previewExpression(expr.value)"
                @mouseleave="resetExpressionPreview"
                class="expression-btn"
                :class="{ active: customization.expression === expr.value }"
              >
                <div class="expr-icon">{{ expr.emoji }}</div>
                <div class="expr-name">{{ expr.name }}</div>
              </button>
            </div>
          </div>

          <!-- 大小调节 -->
          <div class="control-group">
            <label>整体大小</label>
            <div class="slider-control">
              <span class="slider-min">小</span>
              <input 
                type="range" 
                v-model="customization.size" 
                min="0.6" 
                max="1.4" 
                step="0.05"
                class="custom-slider size-slider"
                @input="updatePreview('大小')"
              />
              <span class="slider-max">大</span>
              <span class="slider-value">{{ (customization.size * 100).toFixed(0) }}%</span>
            </div>
          </div>

          <!-- 旋转角度 -->
          <div class="control-group">
            <label>角度调整</label>
            <div class="slider-control">
              <span class="slider-min">←</span>
              <input 
                type="range" 
                v-model="customization.rotation" 
                min="-30" 
                max="30" 
                step="2"
                class="custom-slider rotation-slider"
                @input="updatePreview('角度')"
              />
              <span class="slider-max">→</span>
              <span class="slider-value">{{ customization.rotation }}°</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 颜色配置 -->
      <div class="panel-section" :class="{ active: activeSection === 'colors' }">
        <div class="section-header" @click="setActiveSection('colors')">
          <h3>🎨 颜色配置</h3>
          <span class="section-indicator">{{ activeSection === 'colors' ? '▼' : '▶' }}</span>
        </div>
        <div v-show="activeSection === 'colors'" class="section-content">
          <div class="color-controls">
            <!-- 主要颜色 -->
            <div class="color-group">
              <label>主要颜色</label>
              <div class="color-picker-container">
                <input 
                  type="color" 
                  v-model="customization.primaryColor"
                  class="color-picker"
                  @input="updatePreview('主色')"
                />
                <div class="color-presets">
                  <button 
                    v-for="color in primaryColorPresets" 
                    :key="color"
                    @click="setPrimaryColor(color)"
                    class="color-preset"
                    :style="{ backgroundColor: color }"
                  ></button>
                </div>
              </div>
            </div>

            <!-- 次要颜色 -->
            <div class="color-group">
              <label>次要颜色</label>
              <div class="color-picker-container">
                <input 
                  type="color" 
                  v-model="customization.secondaryColor"
                  class="color-picker"
                  @input="updatePreview('副色')"
                />
                <div class="color-presets">
                  <button 
                    v-for="color in secondaryColorPresets" 
                    :key="color"
                    @click="setSecondaryColor(color)"
                    class="color-preset"
                    :style="{ backgroundColor: color }"
                  ></button>
                </div>
              </div>
            </div>

            <!-- 眼睛颜色 -->
            <div class="color-group">
              <label>眼睛颜色</label>
              <div class="eye-color-controls">
                <div class="eye-color-item">
                  <span>左眼</span>
                  <input 
                    type="color" 
                    v-model="customization.eyeColorLeft"
                    class="color-picker small"
                    @input="updatePreview('眼色')"
                  />
                </div>
                <button @click="syncEyeColors" class="sync-btn">🔗</button>
                <div class="eye-color-item">
                  <span>右眼</span>
                  <input 
                    type="color" 
                    v-model="customization.eyeColorRight"
                    class="color-picker small"
                    @input="updatePreview('眼色')"
                  />
                </div>
              </div>
            </div>

            <!-- 鼻子颜色 -->
            <div class="color-group">
              <label>鼻子颜色</label>
              <input 
                type="color" 
                v-model="customization.noseColor"
                class="color-picker"
                @input="updatePreview('鼻色')"
              />
            </div>
          </div>
        </div>
      </div>

      <!-- 图案装饰 -->
      <div class="panel-section" :class="{ active: activeSection === 'patterns' }">
        <div class="section-header" @click="setActiveSection('patterns')">
          <h3>🎭 图案装饰</h3>
          <span class="section-indicator">{{ activeSection === 'patterns' ? '▼' : '▶' }}</span>
        </div>
        <div v-show="activeSection === 'patterns'" class="section-content">
          <div class="pattern-controls">
            <div class="control-group">
              <label>图案类型</label>
              <div class="pattern-selector">
                <button 
                  v-for="pattern in patterns"
                  :key="pattern.value"
                  @click="selectPattern(pattern.value)"
                  class="pattern-btn"
                  :class="{ active: customization.pattern === pattern.value }"
                >
                  <div class="pattern-icon">{{ pattern.icon }}</div>
                  <div class="pattern-name">{{ pattern.name }}</div>
                </button>
              </div>
            </div>

            <div v-if="customization.pattern !== 'none'" class="control-group">
              <label>图案颜色</label>
              <input 
                type="color" 
                v-model="customization.patternColor"
                class="color-picker"
                @input="updatePreview('图案')"
              />
            </div>
          </div>
        </div>
      </div>

      <!-- 装饰配件 -->
      <div class="panel-section" :class="{ active: activeSection === 'accessories' }">
        <div class="section-header" @click="setActiveSection('accessories')">
          <h3>👑 装饰配件</h3>
          <span class="section-indicator">{{ activeSection === 'accessories' ? '▼' : '▶' }}</span>
        </div>
        <div v-show="activeSection === 'accessories'" class="section-content">
          <!-- 帽子 -->
          <div class="accessory-category">
            <label>帽子</label>
            <div class="accessory-selector">
              <button 
                v-for="hat in accessories.hats" 
                :key="hat.value"
                @click="selectAccessory('hat', hat.value)"
                class="accessory-btn"
                :class="{ active: customization.hat === hat.value }"
              >
                <div class="acc-icon">{{ hat.icon }}</div>
                <div class="acc-name">{{ hat.name }}</div>
              </button>
            </div>
          </div>

          <!-- 眼镜 -->
          <div class="accessory-category">
            <label>眼镜</label>
            <div class="accessory-selector">
              <button 
                v-for="glasses in accessories.glasses" 
                :key="glasses.value"
                @click="selectAccessory('glasses', glasses.value)"
                class="accessory-btn"
                :class="{ active: customization.glasses === glasses.value }"
              >
                <div class="acc-icon">{{ glasses.icon }}</div>
                <div class="acc-name">{{ glasses.name }}</div>
              </button>
            </div>
          </div>

          <!-- 项圈 -->
          <div class="accessory-category">
            <label>项圈</label>
            <div class="accessory-selector">
              <button 
                v-for="collar in accessories.collars" 
                :key="collar.value"
                @click="selectAccessory('collar', collar.value)"
                class="accessory-btn"
                :class="{ active: customization.collar === collar.value }"
              >
                <div class="acc-icon">{{ collar.icon }}</div>
                <div class="acc-name">{{ collar.name }}</div>
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- 特效 -->
      <div class="panel-section" :class="{ active: activeSection === 'effects' }">
        <div class="section-header" @click="setActiveSection('effects')">
          <h3>✨ 特殊效果</h3>
          <span class="section-indicator">{{ activeSection === 'effects' ? '▼' : '▶' }}</span>
        </div>
        <div v-show="activeSection === 'effects'" class="section-content">
          <div class="effects-controls">
            <div class="effect-item">
              <label class="effect-toggle">
                <input 
                  type="checkbox" 
                  v-model="customization.hasGlow"
                  @change="updatePreview('发光')"
                />
                <span class="toggle-switch"></span>
                <span class="toggle-label">✨ 发光效果</span>
              </label>
              <input 
                v-if="customization.hasGlow"
                type="color" 
                v-model="customization.glowColor"
                class="color-picker small"
                @input="updatePreview('发光色')"
              />
            </div>

            <div class="effect-item">
              <label class="effect-toggle">
                <input 
                  type="checkbox" 
                  v-model="customization.hasSparkles"
                  @change="updatePreview('闪光')"
                />
                <span class="toggle-switch"></span>
                <span class="toggle-label">⭐ 闪闪发光</span>
              </label>
            </div>
          </div>
        </div>
      </div>

      <!-- 性格设置 -->
      <div class="panel-section" :class="{ active: activeSection === 'personality' }">
        <div class="section-header" @click="setActiveSection('personality')">
          <h3>🎭 性格特征</h3>
          <span class="section-indicator">{{ activeSection === 'personality' ? '▼' : '▶' }}</span>
        </div>
        <div v-show="activeSection === 'personality'" class="section-content">
          <div class="personality-selector">
            <div 
              v-for="personality in personalities" 
              :key="personality.type"
              @click="selectPersonality(personality.type)"
              class="personality-option"
              :class="{ active: form.personalityType === personality.type }"
            >
              <div class="personality-icon">{{ personality.emoji }}</div>
              <div class="personality-info">
                <div class="personality-name">{{ personality.name }}</div>
                <div class="personality-desc">{{ personality.description }}</div>
              </div>
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
        <button @click="exportDesign" class="action-btn secondary">
          📤 导出设计
        </button>
      </div>
      
      <div class="action-center">
        <div class="progress-indicator">
          <div class="progress-step" :class="{ completed: form.petName }">名称</div>
          <div class="progress-step" :class="{ completed: form.petType }">种类</div>
          <div class="progress-step" :class="{ completed: form.personalityType }">性格</div>
        </div>
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
import DistinctivePetRenderer from './DistinctivePetRenderer.vue'

const emit = defineEmits<{
  'pet-created': [pet: Pet]
  'cancel': []
}>()

const petStore = usePetStore()
const loading = ref(false)
const isAnimated = ref(true)
const selectedPreset = ref('')
const activeSection = ref('species')
const isAdjusting = ref(false)
const adjustmentHint = ref('')
const previewRotation = ref(0)
const previewScale = ref(1.0)
const currentPetAction = ref('idle')

// 表单数据
const form = ref({
  petName: '',
  playerId: 'default',
  petType: '',
  personalityType: ''
})

// 定制选项 - 吉卜力温暖色调
const customization = ref({
  expression: 'happy',
  size: 1.0,
  rotation: 0,
  primaryColor: '#D2691E',  // 温暖的橙棕色
  secondaryColor: '#FFF8DC', // 温暖的米色
  eyeColorLeft: '#2E8B57',   // 森林绿
  eyeColorRight: '#2E8B57',
  noseColor: '#FFB6C1',      // 温柔粉色
  pattern: 'none',
  patternColor: '#8B4513',   // 温暖棕色
  hat: 'none',
  glasses: 'none',
  collar: 'none',
  hasGlow: false,
  glowColor: '#FFF8DC',      // 温暖发光
  hasSparkles: false
})

// 默认宠物数据 - 吉卜力风格
const defaultPetData = {
  petType: 'CAT',
  primaryColor: '#D2691E',   // 温暖橙棕色
  secondaryColor: '#FFF8DC', // 温暖米色
  eyeColorLeft: '#2E8B57',   // 森林绿
  eyeColorRight: '#2E8B57',
  noseColor: '#FFB6C1',
  expression: 'happy',
  size: 1.0,
  rotation: 0,
  pattern: 'none',
  patternColor: '#8B4513',
  hat: 'none',
  glasses: 'none',
  collar: 'none',
  hasGlow: false,
  glowColor: '#FFF8DC',
  hasSparkles: false
}

// 宠物类型
const petTypes = [
  { value: 'CAT', name: '小猫咪' },
  { value: 'DOG', name: '小狗狗' },
  { value: 'RABBIT', name: '小兔子' },
  { value: 'HAMSTER', name: '小仓鼠' },
  { value: 'PANDA', name: '小熊猫' }
]

// 表情
const expressions = [
  { value: 'happy', name: '开心', emoji: '😊' },
  { value: 'excited', name: '兴奋', emoji: '🤩' },
  { value: 'cute', name: '可爱', emoji: '🥰' },
  { value: 'sleepy', name: '困倦', emoji: '😴' },
  { value: 'playful', name: '顽皮', emoji: '😜' },
  { value: 'cool', name: '酷酷', emoji: '😎' }
]

// 图案
const patterns = [
  { value: 'none', name: '无', icon: '🚫' },
  { value: 'spots', name: '斑点', icon: '🔴' },
  { value: 'stripes', name: '条纹', icon: '📏' }
]

// 装饰配件
const accessories = {
  hats: [
    { value: 'none', name: '无', icon: '🚫' },
    { value: 'cap', name: '帽子', icon: '🧢' },
    { value: 'crown', name: '皇冠', icon: '👑' },
    { value: 'bow', name: '蝴蝶结', icon: '🎀' }
  ],
  glasses: [
    { value: 'none', name: '无', icon: '🚫' },
    { value: 'normal', name: '眼镜', icon: '👓' },
    { value: 'sun', name: '墨镜', icon: '🕶️' }
  ],
  collars: [
    { value: 'none', name: '无', icon: '🚫' },
    { value: 'basic', name: '项圈', icon: '🔗' },
    { value: 'bell', name: '铃铛', icon: '🔔' }
  ]
}

// 颜色预设 - 吉卜力温暖色调
const primaryColorPresets = ['#D2691E', '#CD853F', '#DEB887', '#F4A460', '#BC8F8F', '#D2B48C']
const secondaryColorPresets = ['#FFF8DC', '#FFFACD', '#F5F5DC', '#FAF0E6', '#FDF5E6', '#FFEFD5']

// 性格
const personalities = [
  { type: 'PLAYFUL', name: '活泼', emoji: '🎾', description: '喜欢玩耍，精力充沛' },
  { type: 'LAZY', name: '慵懒', emoji: '😴', description: '喜欢睡觉，动作缓慢' },
  { type: 'CURIOUS', name: '好奇', emoji: '🔍', description: '喜欢探索新事物' },
  { type: 'AFFECTIONATE', name: '亲人', emoji: '💕', description: '非常依恋主人' },
  { type: 'INDEPENDENT', name: '独立', emoji: '🦅', description: '喜欢独处，自主性强' },
  { type: 'INTELLIGENT', name: '聪明', emoji: '🧠', description: '学习能力强，很机智' }
]

// 快速动作
const quickActions = [
  { name: 'idle', label: '待机', icon: '😌' },
  { name: 'happy', label: '开心', icon: '😊' },
  { name: 'sleep', label: '睡觉', icon: '😴' },
  { name: 'eat', label: '吃饭', icon: '🍎' },
  { name: 'play', label: '玩耍', icon: '🎾' },
  { name: 'walk', label: '散步', icon: '🚶' }
]

// 预设 - 吉卜力风格
const presets = [
  {
    id: 'totoro',
    name: '龙猫风',
    description: '温暖可爱',
    previewData: { ...defaultPetData, primaryColor: '#A0522D', secondaryColor: '#F5F5DC', expression: 'cute', hasSparkles: true, petType: 'HAMSTER' }
  },
  {
    id: 'forest',
    name: '森林风', 
    description: '自然清新',
    previewData: { ...defaultPetData, primaryColor: '#228B22', secondaryColor: '#F0FFF0', expression: 'happy', eyeColorLeft: '#8FBC8F', pattern: 'spots' }
  },
  {
    id: 'sunset',
    name: '夕阳风',
    description: '温暖治愈', 
    previewData: { ...defaultPetData, primaryColor: '#CD853F', secondaryColor: '#FFEFD5', expression: 'sleepy', hasGlow: true, glowColor: '#FFE4B5' }
  },
  {
    id: 'magical',
    name: '魔法风',
    description: '奇幻梦境',
    previewData: { ...defaultPetData, primaryColor: '#DDA0DD', secondaryColor: '#F8F8FF', expression: 'excited', hat: 'crown', hasSparkles: true }
  }
]

// 计算属性
const canSubmit = computed(() => {
  return form.value.petName.trim() && form.value.petType && form.value.personalityType
})

const previewData = computed(() => ({
  petType: form.value.petType || 'CAT',
  ...customization.value,
  rotation: customization.value.rotation + previewRotation.value,
  size: customization.value.size * previewScale.value
}))

const stageBackgroundStyle = computed(() => ({
  background: `radial-gradient(circle at 30% 30%, ${customization.value.primaryColor}20 0%, transparent 50%),
               radial-gradient(circle at 70% 70%, ${customization.value.secondaryColor}15 0%, transparent 50%)`
}))

// 方法
const setActiveSection = (section: string) => {
  activeSection.value = section
}

const updatePreview = (hint: string) => {
  isAdjusting.value = true
  adjustmentHint.value = `正在调整${hint}...`
  
  setTimeout(() => {
    isAdjusting.value = false
  }, 1000)
}

const selectSpecies = (type: string) => {
  form.value.petType = type
  updatePreview('种类')
}

const selectExpression = (expr: string) => {
  customization.value.expression = expr
  updatePreview('表情')
}

const previewExpression = (expr: string) => {
  // 临时预览表情
}

const resetExpressionPreview = () => {
  // 重置表情预览
}

const selectPattern = (pattern: string) => {
  customization.value.pattern = pattern
  updatePreview('图案')
}

const selectAccessory = (type: string, value: string) => {
  (customization.value as any)[type] = value
  updatePreview('配件')
}

const selectPersonality = (type: string) => {
  form.value.personalityType = type
  updatePreview('性格')
}

const setPrimaryColor = (color: string) => {
  customization.value.primaryColor = color
  updatePreview('主色')
}

const setSecondaryColor = (color: string) => {
  customization.value.secondaryColor = color
  updatePreview('副色')
}

const syncEyeColors = () => {
  customization.value.eyeColorRight = customization.value.eyeColorLeft
  updatePreview('眼色同步')
}

const toggleAnimation = () => {
  isAnimated.value = !isAnimated.value
}

const randomizeAll = () => {
  form.value.petType = petTypes[Math.floor(Math.random() * petTypes.length)].value
  customization.value.expression = expressions[Math.floor(Math.random() * expressions.length)].value
  customization.value.size = Math.random() * 0.8 + 0.6
  customization.value.rotation = Math.random() * 60 - 30
  customization.value.primaryColor = primaryColorPresets[Math.floor(Math.random() * primaryColorPresets.length)]
  customization.value.secondaryColor = secondaryColorPresets[Math.floor(Math.random() * secondaryColorPresets.length)]
  
  updatePreview('全部')
}

const resetAll = () => {
  Object.assign(customization.value, {
    expression: 'happy',
    size: 1.0,
    rotation: 0,
    primaryColor: '#FF6B6B',
    secondaryColor: '#FFFFFF',
    eyeColorLeft: '#4169E1',
    eyeColorRight: '#4169E1',
    noseColor: '#FFB6C1',
    pattern: 'none',
    patternColor: '#000000',
    hat: 'none',
    glasses: 'none',
    collar: 'none',
    hasGlow: false,
    glowColor: '#FFD700',
    hasSparkles: false
  })
  
  previewRotation.value = 0
  previewScale.value = 1.0
  updatePreview('重置')
}

const applyPreset = (preset: any) => {
  selectedPreset.value = preset.id
  Object.assign(customization.value, preset.previewData)
  updatePreview('预设')
}

const saveAsPreset = () => {
  console.log('保存预设')
}

const exportDesign = () => {
  console.log('导出设计')
}

const getPetTypeName = () => {
  const type = petTypes.find(t => t.value === form.value.petType)
  return type?.name || '未选择'
}

const getExpressionName = () => {
  const expr = expressions.find(e => e.value === customization.value.expression)
  return expr?.name || '开心'
}

const handleActionChange = (action: string) => {
  currentPetAction.value = action
  updatePreview(`动作切换: ${action}`)
}

const handleAnimationComplete = (action: string) => {
  console.log(`Animation completed: ${action}`)
}

const changeAction = (action: string) => {
  currentPetAction.value = action
  updatePreview(`切换到${action}动作`)
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
})
</script>

<style scoped>
.cartoon-pet-customizer {
  @apply flex h-screen bg-gradient-to-br from-purple-900 via-blue-900 to-indigo-900 text-white overflow-hidden;
}

/* 左侧预览区域 */
.preview-section {
  @apply w-2/5 p-6 flex flex-col;
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
  @apply px-4 py-2 bg-white/10 hover:bg-white/20 rounded-lg text-sm transition-all backdrop-blur-sm border border-white/20;
}

.preview-viewport {
  @apply flex-1 bg-gradient-to-b from-white/5 to-white/10 rounded-3xl p-6 mb-6 backdrop-blur-sm border border-white/20;
}

.preview-stage {
  @apply relative h-full flex items-center justify-center;
}

.stage-background {
  @apply absolute inset-0 rounded-2xl;
}

.main-pet {
  @apply relative z-10 transition-all duration-300;
}

.info-card {
  @apply absolute top-4 right-4 bg-black/30 backdrop-blur-sm rounded-lg p-3 space-y-2;
}

.info-row {
  @apply flex items-center space-x-2 text-sm;
}

.info-label {
  @apply text-gray-300 font-medium;
}

.info-value {
  @apply text-white;
}

.adjustment-hint {
  @apply absolute bottom-4 left-1/2 transform -translate-x-1/2 bg-purple-500/80 backdrop-blur-sm rounded-lg px-4 py-2;
}

.hint-text {
  @apply text-sm font-medium;
}

.action-control-panel {
  @apply bg-black/30 backdrop-blur-sm rounded-lg p-3 mb-4;
}

.action-grid {
  @apply grid grid-cols-3 gap-2;
}

.action-quick-btn {
  @apply bg-white/10 hover:bg-white/20 rounded-lg p-2 cursor-pointer transition-all text-center;
}

.action-quick-btn.active {
  @apply bg-purple-500/30 ring-2 ring-purple-400;
}

.action-icon {
  @apply block text-lg mb-1;
}

.action-label {
  @apply text-xs;
}

.interaction-guide {
  @apply bg-black/30 backdrop-blur-sm rounded-lg p-3 mb-4;
}

.guide-items {
  @apply space-y-2;
}

.guide-item {
  @apply flex items-center space-x-2 text-sm;
}

.guide-icon {
  @apply text-lg;
}

.guide-text {
  @apply text-gray-300;
}

.preview-controls-panel {
  @apply mt-4 space-y-3;
}

.control-group {
  @apply flex items-center space-x-3;
}

.control-group label {
  @apply text-sm font-medium min-w-max;
}

.angle-slider, .scale-slider {
  @apply flex-1 h-2 bg-white/20 rounded-lg appearance-none cursor-pointer;
}

.preset-section {
  @apply bg-black/20 backdrop-blur-sm rounded-xl p-4;
}

.preset-grid {
  @apply grid grid-cols-2 gap-3;
}

.preset-card {
  @apply bg-white/10 hover:bg-white/20 rounded-lg p-3 cursor-pointer transition-all;
}

.preset-card.active {
  @apply bg-purple-500/30 ring-2 ring-purple-400;
}

.preset-preview {
  @apply flex justify-center mb-2;
}

.preset-info {
  @apply text-center;
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

.panel-section.active {
  @apply bg-white/5;
}

.section-header {
  @apply flex items-center justify-between p-4 cursor-pointer hover:bg-white/5 transition-colors;
}

.section-header h3 {
  @apply text-lg font-semibold;
}

.section-indicator {
  @apply text-gray-400;
}

.section-content {
  @apply p-4 space-y-6;
}

.form-group {
  @apply space-y-3;
}

.form-group label {
  @apply block text-sm font-medium;
}

.form-input {
  @apply w-full px-4 py-3 bg-white/10 border border-white/20 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent backdrop-blur-sm;
}

.species-selector {
  @apply grid grid-cols-3 gap-3;
}

.species-option {
  @apply bg-white/10 hover:bg-white/20 rounded-lg p-3 cursor-pointer transition-all text-center;
}

.species-option.active {
  @apply bg-purple-500/30 ring-2 ring-purple-400;
}

.species-preview {
  @apply flex justify-center mb-2;
}

.species-name {
  @apply text-sm font-medium;
}

.control-group {
  @apply space-y-3;
}

.control-group label {
  @apply block text-sm font-medium;
}

.expression-selector {
  @apply grid grid-cols-3 gap-2;
}

.expression-btn {
  @apply bg-white/10 hover:bg-white/20 rounded-lg p-3 cursor-pointer transition-all text-center;
}

.expression-btn.active {
  @apply bg-purple-500/30 ring-2 ring-purple-400;
}

.expr-icon {
  @apply text-2xl mb-1;
}

.expr-name {
  @apply text-xs;
}

.slider-control {
  @apply flex items-center space-x-3;
}

.custom-slider {
  @apply flex-1 h-2 bg-white/20 rounded-lg appearance-none cursor-pointer;
}

.custom-slider::-webkit-slider-thumb {
  @apply appearance-none w-5 h-5 bg-purple-500 rounded-full cursor-pointer shadow-lg;
}

.slider-min, .slider-max {
  @apply text-xs text-gray-300 min-w-max;
}

.slider-value {
  @apply text-sm font-mono bg-white/10 px-2 py-1 rounded min-w-max;
}

.color-controls {
  @apply space-y-4;
}

.color-group {
  @apply space-y-2;
}

.color-group label {
  @apply text-sm font-medium;
}

.color-picker-container {
  @apply space-y-2;
}

.color-picker {
  @apply w-full h-12 rounded-lg border-2 border-white/20 cursor-pointer;
}

.color-picker.small {
  @apply w-16 h-8;
}

.color-presets {
  @apply flex space-x-2;
}

.color-preset {
  @apply w-8 h-8 rounded-full border-2 border-white/20 cursor-pointer hover:scale-110 transition-transform;
}

.eye-color-controls {
  @apply flex items-center space-x-3;
}

.eye-color-item {
  @apply flex items-center space-x-2;
}

.sync-btn {
  @apply px-2 py-1 bg-white/10 hover:bg-white/20 rounded text-xs transition-colors;
}

.pattern-selector {
  @apply grid grid-cols-3 gap-2;
}

.pattern-btn {
  @apply bg-white/10 hover:bg-white/20 rounded-lg p-3 cursor-pointer transition-all text-center;
}

.pattern-btn.active {
  @apply bg-purple-500/30 ring-2 ring-purple-400;
}

.pattern-icon {
  @apply text-xl mb-1;
}

.pattern-name {
  @apply text-xs;
}

.accessory-category {
  @apply space-y-3;
}

.accessory-category label {
  @apply text-sm font-medium;
}

.accessory-selector {
  @apply grid grid-cols-4 gap-2;
}

.accessory-btn {
  @apply bg-white/10 hover:bg-white/20 rounded-lg p-2 cursor-pointer transition-all text-center;
}

.accessory-btn.active {
  @apply bg-purple-500/30 ring-2 ring-purple-400;
}

.acc-icon {
  @apply text-lg mb-1;
}

.acc-name {
  @apply text-xs;
}

.effects-controls {
  @apply space-y-4;
}

.effect-item {
  @apply flex items-center justify-between;
}

.effect-toggle {
  @apply flex items-center cursor-pointer;
}

.effect-toggle input {
  @apply sr-only;
}

.toggle-switch {
  @apply relative w-12 h-6 bg-white/20 rounded-full transition-colors mr-3;
}

.toggle-switch::before {
  @apply content-[''] absolute top-1 left-1 w-4 h-4 bg-white rounded-full transition-transform;
}

.effect-toggle input:checked + .toggle-switch {
  @apply bg-purple-500;
}

.effect-toggle input:checked + .toggle-switch::before {
  @apply transform translate-x-6;
}

.toggle-label {
  @apply text-sm;
}

.personality-selector {
  @apply grid grid-cols-2 gap-3;
}

.personality-option {
  @apply bg-white/10 hover:bg-white/20 rounded-lg p-3 cursor-pointer transition-all flex items-center space-x-3;
}

.personality-option.active {
  @apply bg-purple-500/30 ring-2 ring-purple-400;
}

.personality-icon {
  @apply text-2xl;
}

.personality-info {
  @apply flex-1;
}

.personality-name {
  @apply font-semibold;
}

.personality-desc {
  @apply text-xs text-gray-300;
}

/* 底部操作栏 */
.action-bar {
  @apply fixed bottom-0 left-0 right-0 bg-black/50 backdrop-blur-sm border-t border-white/10 p-4 flex items-center justify-between;
}

.action-left, .action-right {
  @apply flex space-x-3;
}

.action-center {
  @apply flex-1 flex justify-center;
}

.progress-indicator {
  @apply flex items-center space-x-4;
}

.progress-step {
  @apply px-3 py-1 bg-white/10 rounded-lg text-sm transition-colors;
}

.progress-step.completed {
  @apply bg-green-500/30 text-green-300;
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
