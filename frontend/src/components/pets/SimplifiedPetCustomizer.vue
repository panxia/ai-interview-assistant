<template>
  <div class="simplified-pet-customizer">
    <!-- 左侧预览区域 -->
    <div class="preview-section">
      <div class="preview-container">
        <h2 class="preview-title">🎨 宠物工坊</h2>
        
        <!-- 主预览窗口 -->
        <div class="preview-viewport">
          <div class="preview-stage">
            <!-- 始终显示卡通渲染，保证有动物形象 -->
            <DistinctivePetRenderer 
              v-if="!useLottie || lottieError"
              :pet-data="previewData" 
              :size="280"
              class="main-pet"
            />

            <!-- 当启用骨骼动画时，覆盖显示Lottie -->
            <LottieSkeletonRenderer 
              v-if="useLottie && !lottieError"
              :pet-data="previewData" 
              :size="280"
              :animated="isAnimated"
              :current-action="currentPetAction"
              :show-controls="true"
              class="main-pet"
              @action-change="handleActionChange"
              @animation-loaded="onLottieLoaded"
              @animation-error="onLottieError"
            />

            <!-- 五官覆盖层（保证骨骼动画仍有脸部特征） -->
            <FaceFeaturesOverlay 
              v-if="useLottie && !lottieError"
              :pet-data="previewData"
            />

            <!-- 开关：骨骼动画 -->
            <button class="lottie-toggle" @click="useLottie = !useLottie">
              {{ useLottie ? '关闭骨骼动画' : '开启骨骼动画' }}
            </button>
            
            <!-- 宠物信息卡片 -->
            <div class="info-card">
              <div class="info-row">
                <span class="info-label">名字:</span>
                <span class="info-value">{{ form.petName || '未命名' }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">种类:</span>
                <span class="info-value">{{ getSpeciesName() }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">心情:</span>
                <span class="info-value">{{ getExpressionName() }}</span>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 动作控制 -->
        <div class="action-control">
          <h4 class="control-title">🎭 动作控制</h4>
          <div class="action-buttons">
            <button 
              v-for="action in quickActions" 
              :key="action.name"
              @click="changeAction(action.name)"
              class="action-btn"
              :class="{ active: currentPetAction === action.name }"
            >
              <span class="action-emoji">{{ action.icon }}</span>
              <span class="action-text">{{ action.label }}</span>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧控制面板 -->
    <div class="control-panel">
      <div class="panel-container">
        
        <!-- 宠物种类选择 -->
        <div class="control-section">
          <h3 class="section-title">🐾 选择宠物种类</h3>
          <div class="species-grid">
            <div 
              v-for="type in petTypes" 
              :key="type.value"
              @click="selectSpecies(type.value)"
              class="species-card"
              :class="{ active: form.petType === type.value }"
            >
              <div class="species-preview">
                <div class="species-icon">{{ type.emoji }}</div>
              </div>
              <div class="species-name">{{ type.name }}</div>
            </div>
          </div>
        </div>

        <!-- 基本信息 -->
        <div class="control-section">
          <h3 class="section-title">📝 基本信息</h3>
          <div class="form-group">
            <label for="petName">宠物名字</label>
            <input 
              id="petName"
              v-model="form.petName" 
              type="text" 
              placeholder="给宠物起个可爱的名字"
              class="form-input"
              @input="updatePreview"
            />
          </div>
        </div>

        <!-- 颜色配置 -->
        <div class="control-section">
          <h3 class="section-title">🎨 颜色配置</h3>
          <div class="color-controls">
            <div class="color-group">
              <label>主色调</label>
              <div class="color-section">
                <input 
                  v-model="customization.primaryColor" 
                  type="color" 
                  class="color-picker"
                  @input="updatePreview"
                />
                <div class="color-presets">
                  <button 
                    v-for="color in primaryColors" 
                    :key="color"
                    @click="customization.primaryColor = color; updatePreview()"
                    class="color-preset"
                    :style="{ backgroundColor: color }"
                  ></button>
                </div>
              </div>
            </div>
            
            <div class="color-group">
              <label>次色调</label>
              <div class="color-section">
                <input 
                  v-model="customization.secondaryColor" 
                  type="color" 
                  class="color-picker"
                  @input="updatePreview"
                />
                <div class="color-presets">
                  <button 
                    v-for="color in secondaryColors" 
                    :key="color"
                    @click="customization.secondaryColor = color; updatePreview()"
                    class="color-preset"
                    :style="{ backgroundColor: color }"
                  ></button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 表情选择 -->
        <div class="control-section">
          <h3 class="section-title">😊 表情选择</h3>
          <div class="expression-grid">
            <div 
              v-for="expr in expressions" 
              :key="expr.value"
              @click="selectExpression(expr.value)"
              class="expression-card"
              :class="{ active: customization.expression === expr.value }"
            >
              <div class="expression-emoji">{{ expr.emoji }}</div>
              <div class="expression-name">{{ expr.name }}</div>
            </div>
          </div>
        </div>

        <!-- 特效选择 -->
        <div class="control-section">
          <h3 class="section-title">✨ 特殊效果</h3>
          <div class="effect-controls">
            <label class="effect-checkbox">
              <input 
                type="checkbox" 
                v-model="customization.hasSparkles"
                @change="updatePreview"
              />
              <span class="checkmark"></span>
              闪烁效果
            </label>
          </div>
        </div>

        <!-- 创建按钮 -->
        <div class="create-section">
          <button 
            @click="createPet" 
            :disabled="loading || !form.petName.trim()"
            class="create-btn"
            :class="{ loading }"
          >
            <span v-if="loading">创建中...</span>
            <span v-else>🎉 创建我的宠物</span>
          </button>
          
          <button @click="$emit('cancel')" class="cancel-btn">
            取消
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, defineAsyncComponent } from 'vue'
// 异步加载，规避类型检查误报默认导出
const DistinctivePetRenderer = defineAsyncComponent(() => import('./DistinctivePetRenderer.vue'))
import { usePetStore } from '../../store/index'
import type { Pet } from '../../types/index'
// 按需异步加载，避免类型检查对.vue默认导出报错
const LottieSkeletonRenderer = defineAsyncComponent(() => import('./LottieSkeletonRenderer.vue'))
const FaceFeaturesOverlay = defineAsyncComponent(() => import('./FaceFeaturesOverlay.vue'))

const emit = defineEmits<{
  'pet-created': [pet: Pet]
  'cancel': []
}>()

const petStore = usePetStore()
const loading = ref(false)
const isAnimated = ref(true)
const currentPetAction = ref('idle')
const lottieReady = ref(false)
const lottieError = ref('')
const useLottie = ref(true)

// 表单数据
const form = ref({
  petName: '',
  petType: 'CAT',
  playerId: 'default'
})

// 定制数据
const customization = ref({
  primaryColor: '#D2691E',
  secondaryColor: '#FFF8DC',
  eyeColorLeft: '#2E8B57',
  eyeColorRight: '#2E8B57',
  noseColor: '#FFB6C1',
  expression: 'happy',
  hasSparkles: false
})

// 快速动作
const quickActions = [
  { name: 'idle', label: '待机', icon: '😌' },
  { name: 'happy', label: '开心', icon: '😊' },
  { name: 'sleep', label: '睡觉', icon: '😴' },
  { name: 'eat', label: '吃饭', icon: '🍎' },
  { name: 'play', label: '玩耍', icon: '🎾' },
  { name: 'walk', label: '散步', icon: '🚶' }
]

// 宠物类型
const petTypes = [
  { value: 'CAT', name: '小猫咪', emoji: '🐱' },
  { value: 'DOG', name: '小狗狗', emoji: '🐶' },
  { value: 'RABBIT', name: '小兔子', emoji: '🐰' },
  { value: 'HAMSTER', name: '小仓鼠', emoji: '🐹' },
  { value: 'PANDA', name: '小熊猫', emoji: '🐼' }
]

// 表情选项
const expressions = [
  { value: 'happy', name: '开心', emoji: '😊' },
  { value: 'cute', name: '可爱', emoji: '🥰' },
  { value: 'sad', name: '难过', emoji: '😢' },
  { value: 'surprised', name: '惊讶', emoji: '😲' }
]

// 颜色预设
const primaryColors = ['#D2691E', '#CD853F', '#DEB887', '#F4A460', '#BC8F8F', '#D2B48C', '#A0522D', '#8B4513']
const secondaryColors = ['#FFF8DC', '#FFFACD', '#F5F5DC', '#FAF0E6', '#FDF5E6', '#FFEFD5', '#F0E68C', '#FFEBCD']

// 默认宠物数据
const defaultPetData = {
  petType: 'CAT',
  primaryColor: '#D2691E',
  secondaryColor: '#FFF8DC',
  eyeColorLeft: '#2E8B57',
  eyeColorRight: '#2E8B57',
  noseColor: '#FFB6C1',
  expression: 'happy',
  size: 1,
  rotation: 0,
  hasSparkles: false
}

// 预览数据
const previewData = computed(() => ({
  ...customization.value,
  petType: form.value.petType,
  size: 1,
  rotation: 0
}))

// 方法
const selectSpecies = (type: string) => {
  form.value.petType = type
  updatePreview()
}

const selectExpression = (expression: string) => {
  customization.value.expression = expression
  updatePreview()
}

const changeAction = (action: string) => {
  currentPetAction.value = action
}

const handleActionChange = (action: string) => {
  currentPetAction.value = action
}

const updatePreview = () => {
  // 触发响应式更新
  console.log('Preview updated')
}

const onLottieLoaded = () => {
  lottieReady.value = true
  lottieError.value = ''
}

const onLottieError = (err: string) => {
  lottieReady.value = false
  lottieError.value = err || '动画加载失败'
}

const getSpeciesName = () => {
  const species = petTypes.find(type => type.value === form.value.petType)
  return species?.name || '未知'
}

const getExpressionName = () => {
  const expr = expressions.find(e => e.value === customization.value.expression)
  return expr?.name || '开心'
}

const createPet = async () => {
  if (!form.value.petName.trim()) {
    alert('请输入宠物名字')
    return
  }

  try {
    loading.value = true
    
    const petData = {
      name: form.value.petName,
      type: form.value.petType,
      playerId: form.value.playerId,
      appearance: {
        primaryColor: customization.value.primaryColor,
        secondaryColor: customization.value.secondaryColor,
        eyeColorLeft: customization.value.eyeColorLeft,
        eyeColorRight: customization.value.eyeColorRight,
        noseColor: customization.value.noseColor,
        bodyProportion: {
          headSize: 1.0,
          bodySize: 1.0,
          limbSize: 1.0
        }
      },
      personality: {
        personalityType: 'PLAYFUL',
        traits: {
          energy: 75,
          happiness: 80,
          curiosity: 70
        }
      },
      stats: {
        health: 100,
        hunger: 50,
        happiness: 80,
        energy: 75,
        cleanliness: 90,
        experience: 0,
        level: 1
      }
    }

    console.log('Creating pet with data:', petData)
    
    const response = await fetch('http://localhost:8080/api/pets', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(petData)
    })

    if (!response.ok) {
      const errorData = await response.json()
      throw new Error(errorData.message || '创建宠物失败')
    }

    const result = await response.json()
    
    if (result.success) {
      emit('pet-created', result.data)
    } else {
      throw new Error(result.message || '创建宠物失败')
    }
    
  } catch (error) {
    console.error('Error creating pet:', error)
    alert('创建宠物失败: ' + (error as Error).message)
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
.simplified-pet-customizer {
  @apply flex h-screen bg-gradient-to-br from-purple-900 via-blue-900 to-indigo-900 text-white;
}

/* 左侧预览区域 */
.preview-section {
  @apply w-2/5 p-6 flex flex-col;
}

.preview-container {
  @apply h-full flex flex-col;
}

.preview-title {
  @apply text-2xl font-bold mb-6 text-center;
}

.preview-viewport {
  @apply flex-1 bg-gradient-to-b from-white/5 to-white/10 rounded-3xl p-8 mb-6 backdrop-blur-sm border border-white/20 relative;
}

.preview-stage {
  @apply relative h-full flex items-center justify-center;
}

.main-pet {
  @apply relative z-10 transition-all duration-300;
}

.lottie-toggle {
  @apply absolute bottom-4 left-4 px-3 py-2 bg-white/10 hover:bg-white/20 rounded-lg border border-white/20 text-sm;
}

.info-card {
  @apply absolute top-4 right-4 bg-black/40 backdrop-blur-sm rounded-lg p-3 space-y-2;
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

.action-control {
  @apply bg-black/20 backdrop-blur-sm rounded-xl p-4;
}

.control-title {
  @apply text-lg font-semibold mb-3;
}

.action-buttons {
  @apply grid grid-cols-3 gap-3;
}

.action-btn {
  @apply bg-white/10 hover:bg-white/20 rounded-lg p-3 cursor-pointer transition-all text-center;
}

.action-btn.active {
  @apply bg-purple-500/30 ring-2 ring-purple-400;
}

.action-emoji {
  @apply block text-xl mb-1;
}

.action-text {
  @apply text-xs;
}

/* 右侧控制面板 */
.control-panel {
  @apply w-3/5 bg-black/30 backdrop-blur-sm overflow-y-auto;
}

.panel-container {
  @apply p-6 space-y-6;
}

.control-section {
  @apply bg-white/5 rounded-xl p-4 border border-white/10;
}

.section-title {
  @apply text-lg font-semibold mb-4;
}

.form-group {
  @apply space-y-2;
}

.form-group label {
  @apply block text-sm font-medium;
}

.form-input {
  @apply w-full px-4 py-3 bg-white/10 border border-white/20 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent backdrop-blur-sm;
}

/* 宠物种类网格 */
.species-grid {
  @apply grid grid-cols-3 gap-3;
}

.species-card {
  @apply bg-white/10 hover:bg-white/20 rounded-lg p-3 cursor-pointer transition-all text-center;
}

.species-card.active {
  @apply bg-purple-500/30 ring-2 ring-purple-400;
}

.species-preview {
  @apply flex justify-center mb-2;
}

.species-name {
  @apply text-sm font-medium;
}

/* 颜色控制 */
.color-controls {
  @apply space-y-4;
}

.color-group {
  @apply space-y-2;
}

.color-group label {
  @apply block text-sm font-medium;
}

.color-section {
  @apply flex items-center space-x-3;
}

.color-picker {
  @apply w-12 h-10 rounded-lg border border-white/20 cursor-pointer;
}

.color-presets {
  @apply flex space-x-2 flex-wrap;
}

.color-preset {
  @apply w-8 h-8 rounded-lg border border-white/20 cursor-pointer hover:scale-110 transition-transform;
}

/* 表情网格 */
.expression-grid {
  @apply grid grid-cols-4 gap-3;
}

.expression-card {
  @apply bg-white/10 hover:bg-white/20 rounded-lg p-3 cursor-pointer transition-all text-center;
}

.expression-card.active {
  @apply bg-purple-500/30 ring-2 ring-purple-400;
}

.expression-emoji {
  @apply text-2xl mb-1;
}

.expression-name {
  @apply text-xs;
}

/* 特效控制 */
.effect-controls {
  @apply space-y-3;
}

.effect-checkbox {
  @apply flex items-center space-x-3 cursor-pointer;
}

.effect-checkbox input[type="checkbox"] {
  @apply sr-only;
}

.checkmark {
  @apply w-5 h-5 bg-white/10 border border-white/20 rounded flex items-center justify-center;
}

.effect-checkbox input[type="checkbox"]:checked + .checkmark {
  @apply bg-purple-500 border-purple-400;
}

.effect-checkbox input[type="checkbox"]:checked + .checkmark::after {
  content: '✓';
  @apply text-white text-sm;
}

/* 创建按钮区域 */
.create-section {
  @apply space-y-3 pt-6 border-t border-white/10;
}

.create-btn {
  @apply w-full px-6 py-4 bg-gradient-to-r from-purple-600 to-blue-600 hover:from-purple-500 hover:to-blue-500 rounded-xl font-semibold transition-all text-lg;
}

.create-btn:disabled {
  @apply opacity-50 cursor-not-allowed;
}

.create-btn.loading {
  @apply animate-pulse;
}

.cancel-btn {
  @apply w-full px-6 py-3 bg-white/10 hover:bg-white/20 rounded-lg font-medium transition-all;
}
</style>
