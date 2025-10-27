<template>
  <div class="lottie-skeleton-renderer" :style="containerStyle">
    <div ref="lottieContainer" class="lottie-container"></div>
    
    <!-- 控制开关按钮（不遮挡预览） -->
    <button 
      v-if="showControls"
      class="mini-toggle-btn"
      @click="controlsOpen = !controlsOpen"
      :aria-pressed="controlsOpen"
      title="动画控制"
    >🎬</button>

    <!-- 动画控制面板（默认折叠） -->
    <div v-if="showControls && controlsOpen" class="animation-controls">
      <div class="control-header">
        <h4>🎬 动画控制</h4>
      </div>
      
      <!-- 动作选择 -->
      <div class="action-selector">
        <div class="control-group">
          <label>动作类型</label>
          <div class="action-buttons">
            <button 
              v-for="action in availableActions" 
              :key="action.name"
              @click="playAction(action.name)"
              class="action-btn"
              :class="{ active: currentAction === action.name }"
            >
              <span class="action-emoji">{{ action.emoji }}</span>
              <span class="action-name">{{ action.label }}</span>
            </button>
          </div>
        </div>
      </div>
      
      <!-- 播放控制 -->
      <div class="playback-controls">
        <div class="control-group">
          <label>播放控制</label>
          <div class="control-buttons">
            <button @click="play" class="control-btn" :disabled="isPlaying">
              ▶️ 播放
            </button>
            <button @click="pause" class="control-btn" :disabled="!isPlaying">
              ⏸️ 暂停
            </button>
            <button @click="stop" class="control-btn">
              ⏹️ 停止
            </button>
          </div>
        </div>
        
        <div class="control-group">
          <label>播放速度</label>
          <div class="speed-control">
            <input 
              type="range" 
              v-model="playbackSpeed" 
              min="0.1" 
              max="3" 
              step="0.1"
              class="speed-slider"
              @input="updateSpeed"
            />
            <span class="speed-value">{{ playbackSpeed }}x</span>
          </div>
        </div>
      </div>
      
      <!-- 循环设置 -->
      <div class="loop-controls">
        <div class="control-group">
          <label class="checkbox-label">
            <input 
              type="checkbox" 
              v-model="loop"
              @change="updateLoop"
            />
            <span class="checkmark"></span>
            循环播放
          </label>
        </div>
      </div>
    </div>
    
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-overlay">
      <div class="loading-spinner"></div>
      <div class="loading-text">加载动画中...</div>
    </div>
    
    <!-- 错误提示 -->
    <div v-if="error" class="error-overlay">
      <div class="error-icon">⚠️</div>
      <div class="error-text">{{ error }}</div>
      <button @click="retry" class="retry-btn">重试</button>
    </div>
  </div>
</template>

<script setup lang="ts" name="LottieSkeletonRenderer">
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import lottie from 'lottie-web'

interface PetData {
  petType: string
  primaryColor: string
  secondaryColor: string
  expression: string
  size: number
  rotation: number
}

interface Props {
  petData: PetData
  size?: number
  animated?: boolean
  showControls?: boolean
  currentAction?: string
}

const props = withDefaults(defineProps<Props>(), {
  size: 300,
  animated: true,
  showControls: true,
  currentAction: 'idle'
})

const emit = defineEmits<{
  'action-change': [action: string]
  'animation-loaded': []
  'animation-error': [error: string]
}>()

// 响应式数据
const lottieContainer = ref<HTMLElement>()
const lottieInstance = ref<any>(null)
const loading = ref(false)
const error = ref('')
const isPlaying = ref(false)
const currentAction = ref(props.currentAction)
const playbackSpeed = ref(1.0)
const loop = ref(true)
const controlsOpen = ref(false)

// 可用动作
const availableActions = computed(() => {
  const baseActions = [
    { name: 'idle', label: '待机', emoji: '😌' },
    { name: 'happy', label: '开心', emoji: '😊' },
    { name: 'sleep', label: '睡觉', emoji: '😴' },
    { name: 'eat', label: '吃饭', emoji: '🍎' },
    { name: 'play', label: '玩耍', emoji: '🎾' },
    { name: 'walk', label: '散步', icon: '🚶' }
  ]
  
  // 根据宠物类型添加特殊动作
  if (props.petData.petType === 'CAT') {
    baseActions.push({ name: 'stretch', label: '伸懒腰', emoji: '🐱' })
    baseActions.push({ name: 'purr', label: '呼噜', emoji: '💤' })
  } else if (props.petData.petType === 'DOG') {
    baseActions.push({ name: 'wag', label: '摇尾巴', emoji: '🐕' })
    baseActions.push({ name: 'bark', label: '汪汪', emoji: '🔊' })
  } else if (props.petData.petType === 'RABBIT') {
    baseActions.push({ name: 'hop', label: '跳跃', emoji: '🐰' })
    baseActions.push({ name: 'twitch', label: '抖耳朵', emoji: '👂' })
  }
  
  return baseActions
})

// 计算样式
const containerStyle = computed(() => ({
  width: `${props.size}px`,
  height: `${props.size}px`,
  position: 'relative' as const,
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center'
}))

// 路径拼接
const buildPath = (segments: string[]): string => ['','animations',...segments].join('/')

// 解析动画数据：按优先级尝试 多物种 → 通用动作 → 通用idle
const resolveAnimationData = async (petType: string, action: string): Promise<any> => {
  const tryPaths = [
    buildPath([petType.toLowerCase(), `${action}.json`]),
    buildPath([`pet-${action}.json`]),
    buildPath([`pet-idle.json`])
  ]

  for (const p of tryPaths) {
    try {
      const res = await fetch(p, { cache: 'no-store' })
      if (res.ok) {
        return await res.json()
      }
    } catch (_) {
      // ignore and try next
    }
  }
  throw new Error('未找到可用的动画数据')
}

// 加载动画
const loadAnimation = async (action: string) => {
  if (!lottieContainer.value) return
  
  try {
    loading.value = true
    error.value = ''
    
    // 停止当前动画
    if (lottieInstance.value) {
      lottieInstance.value.destroy()
      lottieInstance.value = null
    }
    
    // 解析动画数据，带通用回退
    const animationData = await resolveAnimationData(props.petData.petType, action)

    // 创建Lottie实例（使用已加载的数据）
    lottieInstance.value = lottie.loadAnimation({
      container: lottieContainer.value,
      renderer: 'svg',
      loop: loop.value,
      autoplay: props.animated,
      animationData,
      rendererSettings: {
        preserveAspectRatio: 'xMidYMid meet'
      }
    })
    
    // 设置播放速度
    lottieInstance.value.setSpeed(playbackSpeed.value)
    
    // 事件监听
    lottieInstance.value.addEventListener('DOMLoaded', () => {
      loading.value = false
      emit('animation-loaded')
    })
    // 数据加载失败
    lottieInstance.value.addEventListener('data_failed', () => {
      loading.value = false
      error.value = '动画数据加载失败（可能缺少对应JSON文件）'
      emit('animation-error', error.value)
      loadFallbackAnimation(action)
    })
    
    lottieInstance.value.addEventListener('loopComplete', () => {
      if (!loop.value) {
        pause()
      }
    })
    
  } catch (err: any) {
    loading.value = false
    error.value = `动画初始化失败: ${err.message || '未知错误'}`
    emit('animation-error', error.value)
    
    // 尝试加载备用动画
    loadFallbackAnimation(action)
  }
}

// 加载备用动画（CSS动画）
const loadFallbackAnimation = (action: string) => {
  console.log('Loading fallback CSS animation for:', action)
  // 这里可以触发CSS动画作为备用
}

// 播放控制方法
const play = () => {
  if (lottieInstance.value) {
    lottieInstance.value.play()
    isPlaying.value = true
  }
}

const pause = () => {
  if (lottieInstance.value) {
    lottieInstance.value.pause()
    isPlaying.value = false
  }
}

const stop = () => {
  if (lottieInstance.value) {
    lottieInstance.value.stop()
    isPlaying.value = false
  }
}

const playAction = (action: string) => {
  currentAction.value = action
  emit('action-change', action)
  loadAnimation(action)
}

const updateSpeed = () => {
  if (lottieInstance.value) {
    lottieInstance.value.setSpeed(playbackSpeed.value)
  }
}

const updateLoop = () => {
  if (lottieInstance.value) {
    lottieInstance.value.loop = loop.value
  }
}

const retry = () => {
  loadAnimation(currentAction.value)
}

// 监听属性变化
watch(() => props.currentAction, (newAction) => {
  if (newAction !== currentAction.value) {
    currentAction.value = newAction
    loadAnimation(newAction)
  }
})

watch(() => props.petData.petType, () => {
  loadAnimation(currentAction.value)
})

// 生命周期
onMounted(() => {
  if (props.animated) {
    loadAnimation(currentAction.value)
  }
})

onUnmounted(() => {
  if (lottieInstance.value) {
    lottieInstance.value.destroy()
  }
})
</script>

<style scoped>
.lottie-skeleton-renderer {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.lottie-container {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 动画控制面板 */
.animation-controls {
  position: absolute;
  right: 8px;
  bottom: 8px;
  background: rgba(0, 0, 0, 0.75);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  padding: 12px;
  min-width: 240px;
  z-index: 100;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.mini-toggle-btn {
  position: absolute;
  right: 8px;
  bottom: 8px;
  width: 36px;
  height: 36px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 9999px;
  background: rgba(0,0,0,0.7);
  color: #fff;
  border: 1px solid rgba(255,255,255,0.25);
  cursor: pointer;
  z-index: 101;
}

.mini-toggle-btn:hover {
  background: rgba(0,0,0,0.85);
}

.control-header h4 {
  margin: 0 0 12px 0;
  color: white;
  font-size: 14px;
  font-weight: 600;
  text-align: center;
}

.control-group {
  margin-bottom: 12px;
}

.control-group label {
  display: block;
  color: #ccc;
  font-size: 12px;
  margin-bottom: 6px;
}

/* 动作选择器 */
.action-buttons {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 6px;
}

.action-btn {
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  padding: 8px 6px;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.action-btn:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-1px);
}

.action-btn.active {
  background: rgba(147, 51, 234, 0.6);
  border-color: rgba(147, 51, 234, 0.8);
}

.action-emoji {
  font-size: 16px;
}

.action-name {
  font-size: 10px;
  color: #ccc;
}

/* 播放控制 */
.control-buttons {
  display: flex;
  gap: 6px;
}

.control-btn {
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 6px;
  padding: 6px 10px;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 11px;
  color: white;
}

.control-btn:hover:not(:disabled) {
  background: rgba(255, 255, 255, 0.2);
}

.control-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 速度控制 */
.speed-control {
  display: flex;
  align-items: center;
  gap: 8px;
}

.speed-slider {
  flex: 1;
  height: 4px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 2px;
  outline: none;
  cursor: pointer;
}

.speed-slider::-webkit-slider-thumb {
  appearance: none;
  width: 12px;
  height: 12px;
  background: #9333ea;
  border-radius: 50%;
  cursor: pointer;
}

.speed-value {
  color: #ccc;
  font-size: 11px;
  min-width: 30px;
}

/* 循环控制 */
.checkbox-label {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-size: 12px;
  color: #ccc;
}

.checkbox-label input[type="checkbox"] {
  display: none;
}

.checkmark {
  width: 16px;
  height: 16px;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.checkbox-label input[type="checkbox"]:checked + .checkmark {
  background: #9333ea;
  border-color: #9333ea;
}

.checkbox-label input[type="checkbox"]:checked + .checkmark::after {
  content: '✓';
  color: white;
  font-size: 10px;
}

/* 加载状态 */
.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
}

.loading-spinner {
  width: 32px;
  height: 32px;
  border: 3px solid rgba(255, 255, 255, 0.3);
  border-top: 3px solid #9333ea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

.loading-text {
  color: white;
  font-size: 12px;
  margin-top: 8px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 错误状态 */
.error-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(220, 38, 38, 0.9);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  padding: 16px;
  text-align: center;
}

.error-icon {
  font-size: 24px;
  margin-bottom: 8px;
}

.error-text {
  color: white;
  font-size: 12px;
  margin-bottom: 12px;
}

.retry-btn {
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 6px;
  padding: 6px 12px;
  color: white;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 11px;
}

.retry-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}
</style>
