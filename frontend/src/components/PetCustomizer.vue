<template>
  <div class="pet-customizer">
    <!-- 头部标题 -->
    <div class="customizer-header">
      <h2 class="text-2xl font-bold text-gray-800 mb-2">🎨 宠物造型师</h2>
      <p class="text-gray-600 mb-6">打造您独一无二的专属宠物</p>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-2 gap-8">
      <!-- 左侧：宠物预览 -->
      <div class="pet-preview-section">
        <div class="bg-gradient-to-br from-blue-100 to-purple-100 rounded-3xl p-8 shadow-lg">
          <h3 class="text-lg font-semibold text-gray-700 mb-4 text-center">预览效果</h3>
          <div class="flex justify-center items-center min-h-64">
            <PetRenderer 
              :petData="{ appearance: currentAppearance }"
              :size="280"
              :interactive="true"
              :animationState="animationState"
              :mood="currentMood"
            />
          </div>
          
          <!-- 动画控制 -->
          <div class="mt-6">
            <h4 class="text-sm font-medium text-gray-600 mb-3">动画预览</h4>
            <div class="flex flex-wrap gap-2 justify-center">
              <button 
                v-for="animation in animations" 
                :key="animation.id"
                @click="animationState = animation.id"
                :class="[
                  'px-3 py-1 rounded-full text-xs font-medium transition-all',
                  animationState === animation.id 
                    ? 'bg-blue-500 text-white' 
                    : 'bg-white text-gray-600 hover:bg-blue-50'
                ]"
              >
                {{ animation.emoji }} {{ animation.name }}
              </button>
            </div>
          </div>

          <!-- 情绪控制 -->
          <div class="mt-4">
            <h4 class="text-sm font-medium text-gray-600 mb-3">情绪表达</h4>
            <div class="flex flex-wrap gap-2 justify-center">
              <button 
                v-for="mood in moods" 
                :key="mood.id"
                @click="currentMood = mood.id"
                :class="[
                  'px-3 py-1 rounded-full text-xs font-medium transition-all',
                  currentMood === mood.id 
                    ? 'bg-pink-500 text-white' 
                    : 'bg-white text-gray-600 hover:bg-pink-50'
                ]"
              >
                {{ mood.emoji }} {{ mood.name }}
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧：自定义选项 -->
      <div class="customization-options">
        <div class="bg-white rounded-3xl p-6 shadow-lg max-h-96 overflow-y-auto">
          <!-- 标签页导航 -->
          <div class="tabs-nav mb-6">
            <div class="flex flex-wrap border-b border-gray-200">
              <button 
                v-for="tab in tabs" 
                :key="tab.id"
                @click="activeTab = tab.id"
                :class="[
                  'px-4 py-2 text-sm font-medium border-b-2 transition-colors',
                  activeTab === tab.id
                    ? 'border-indigo-500 text-indigo-600'
                    : 'border-transparent text-gray-500 hover:text-gray-700'
                ]"
              >
                {{ tab.icon }} {{ tab.name }}
              </button>
            </div>
          </div>

          <!-- 身体部位 -->
          <div v-if="activeTab === 'body'" class="customization-panel">
            <div class="space-y-6">
              <!-- 头部形状 -->
              <div class="option-group">
                <label class="block text-sm font-medium text-gray-700 mb-3">头部形状</label>
                <div class="grid grid-cols-2 gap-2">
                  <button 
                    v-for="shape in headShapes" 
                    :key="shape.id"
                    @click="currentAppearance.bodyParts.headShape = shape.id"
                    :class="[
                      'p-3 border-2 rounded-lg text-center transition-all',
                      currentAppearance.bodyParts.headShape === shape.id
                        ? 'border-indigo-500 bg-indigo-50'
                        : 'border-gray-200 hover:border-indigo-300'
                    ]"
                  >
                    <div class="text-2xl mb-1">{{ shape.emoji }}</div>
                    <div class="text-xs text-gray-600">{{ shape.name }}</div>
                  </button>
                </div>
              </div>

              <!-- 耳朵类型 -->
              <div class="option-group">
                <label class="block text-sm font-medium text-gray-700 mb-3">耳朵类型</label>
                <div class="grid grid-cols-3 gap-2">
                  <button 
                    v-for="ear in earTypes" 
                    :key="ear.id"
                    @click="currentAppearance.bodyParts.earType = ear.id"
                    :class="[
                      'p-3 border-2 rounded-lg text-center transition-all',
                      currentAppearance.bodyParts.earType === ear.id
                        ? 'border-indigo-500 bg-indigo-50'
                        : 'border-gray-200 hover:border-indigo-300'
                    ]"
                  >
                    <div class="text-2xl mb-1">{{ ear.emoji }}</div>
                    <div class="text-xs text-gray-600">{{ ear.name }}</div>
                  </button>
                </div>
              </div>

              <!-- 眼睛类型 -->
              <div class="option-group">
                <label class="block text-sm font-medium text-gray-700 mb-3">眼睛类型</label>
                <div class="grid grid-cols-3 gap-2">
                  <button 
                    v-for="eye in eyeTypes" 
                    :key="eye.id"
                    @click="currentAppearance.bodyParts.eyeType = eye.id"
                    :class="[
                      'p-3 border-2 rounded-lg text-center transition-all',
                      currentAppearance.bodyParts.eyeType === eye.id
                        ? 'border-indigo-500 bg-indigo-50'
                        : 'border-gray-200 hover:border-indigo-300'
                    ]"
                  >
                    <div class="text-2xl mb-1">{{ eye.emoji }}</div>
                    <div class="text-xs text-gray-600">{{ eye.name }}</div>
                  </button>
                </div>
              </div>

              <!-- 嘴巴类型 -->
              <div class="option-group">
                <label class="block text-sm font-medium text-gray-700 mb-3">嘴巴类型</label>
                <div class="grid grid-cols-3 gap-2">
                  <button 
                    v-for="mouth in mouthTypes" 
                    :key="mouth.id"
                    @click="currentAppearance.bodyParts.mouthType = mouth.id"
                    :class="[
                      'p-3 border-2 rounded-lg text-center transition-all',
                      currentAppearance.bodyParts.mouthType === mouth.id
                        ? 'border-indigo-500 bg-indigo-50'
                        : 'border-gray-200 hover:border-indigo-300'
                    ]"
                  >
                    <div class="text-2xl mb-1">{{ mouth.emoji }}</div>
                    <div class="text-xs text-gray-600">{{ mouth.name }}</div>
                  </button>
                </div>
              </div>
            </div>
          </div>

          <!-- 颜色方案 -->
          <div v-if="activeTab === 'colors'" class="customization-panel">
            <div class="space-y-6">
              <!-- 主要颜色 -->
              <div class="option-group">
                <label class="block text-sm font-medium text-gray-700 mb-3">主要颜色</label>
                <div class="flex items-center space-x-3">
                  <input 
                    type="color" 
                    v-model="currentAppearance.colorScheme.primaryColor"
                    class="w-12 h-12 rounded-lg border-2 border-gray-300 cursor-pointer"
                  />
                  <div class="flex-1">
                    <div class="grid grid-cols-6 gap-2">
                      <button 
                        v-for="color in colorPalette" 
                        :key="color"
                        @click="currentAppearance.colorScheme.primaryColor = color"
                        :style="{ backgroundColor: color }"
                        class="w-8 h-8 rounded-lg border-2 border-gray-300 hover:scale-110 transition-transform"
                      />
                    </div>
                  </div>
                </div>
              </div>

              <!-- 辅助颜色 -->
              <div class="option-group">
                <label class="block text-sm font-medium text-gray-700 mb-3">辅助颜色</label>
                <div class="flex items-center space-x-3">
                  <input 
                    type="color" 
                    v-model="currentAppearance.colorScheme.secondaryColor"
                    class="w-12 h-12 rounded-lg border-2 border-gray-300 cursor-pointer"
                  />
                  <div class="flex-1">
                    <div class="grid grid-cols-6 gap-2">
                      <button 
                        v-for="color in colorPalette" 
                        :key="`secondary-${color}`"
                        @click="currentAppearance.colorScheme.secondaryColor = color"
                        :style="{ backgroundColor: color }"
                        class="w-8 h-8 rounded-lg border-2 border-gray-300 hover:scale-110 transition-transform"
                      />
                    </div>
                  </div>
                </div>
              </div>

              <!-- 眼睛颜色 -->
              <div class="option-group">
                <label class="block text-sm font-medium text-gray-700 mb-3">眼睛颜色</label>
                <div class="space-y-3">
                  <!-- 是否异瞳 -->
                  <div class="flex items-center space-x-3">
                    <input 
                      type="checkbox" 
                      id="heterochromia"
                      v-model="isHeterochromia"
                      @change="handleHeterochromiaChange"
                      class="rounded"
                    />
                    <label for="heterochromia" class="text-sm text-gray-600">异瞳（不同颜色的眼睛）</label>
                  </div>
                  
                  <!-- 左眼颜色 -->
                  <div>
                    <label class="block text-xs text-gray-500 mb-1">
                      {{ isHeterochromia ? '左眼颜色' : '眼睛颜色' }}
                    </label>
                    <div class="flex items-center space-x-3">
                      <input 
                        type="color" 
                        v-model="currentAppearance.colorScheme.leftEyeColor"
                        @input="handleEyeColorChange"
                        class="w-10 h-10 rounded-lg border-2 border-gray-300 cursor-pointer"
                      />
                      <div class="grid grid-cols-6 gap-1">
                        <button 
                          v-for="color in eyeColors" 
                          :key="`left-eye-${color}`"
                          @click="setEyeColor(color, 'left')"
                          :style="{ backgroundColor: color }"
                          class="w-6 h-6 rounded-full border border-gray-300 hover:scale-110 transition-transform"
                        />
                      </div>
                    </div>
                  </div>

                  <!-- 右眼颜色（仅在异瞳时显示） -->
                  <div v-if="isHeterochromia">
                    <label class="block text-xs text-gray-500 mb-1">右眼颜色</label>
                    <div class="flex items-center space-x-3">
                      <input 
                        type="color" 
                        v-model="currentAppearance.colorScheme.rightEyeColor"
                        class="w-10 h-10 rounded-lg border-2 border-gray-300 cursor-pointer"
                      />
                      <div class="grid grid-cols-6 gap-1">
                        <button 
                          v-for="color in eyeColors" 
                          :key="`right-eye-${color}`"
                          @click="setEyeColor(color, 'right')"
                          :style="{ backgroundColor: color }"
                          class="w-6 h-6 rounded-full border border-gray-300 hover:scale-110 transition-transform"
                        />
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 鼻子颜色 -->
              <div class="option-group">
                <label class="block text-sm font-medium text-gray-700 mb-3">鼻子颜色</label>
                <div class="flex items-center space-x-3">
                  <input 
                    type="color" 
                    v-model="currentAppearance.colorScheme.noseColor"
                    class="w-10 h-10 rounded-lg border-2 border-gray-300 cursor-pointer"
                  />
                  <div class="grid grid-cols-4 gap-2">
                    <button 
                      v-for="color in noseColors" 
                      :key="color"
                      @click="currentAppearance.colorScheme.noseColor = color"
                      :style="{ backgroundColor: color }"
                      class="w-8 h-8 rounded-lg border border-gray-300 hover:scale-110 transition-transform"
                    />
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 图案 -->
          <div v-if="activeTab === 'patterns'" class="customization-panel">
            <div class="space-y-6">
              <!-- 图案类型 -->
              <div class="option-group">
                <label class="block text-sm font-medium text-gray-700 mb-3">图案类型</label>
                <div class="grid grid-cols-2 gap-2">
                  <button 
                    v-for="pattern in patternTypes" 
                    :key="pattern.id"
                    @click="currentAppearance.patternConfig.type = pattern.id"
                    :class="[
                      'p-3 border-2 rounded-lg text-center transition-all',
                      currentAppearance.patternConfig.type === pattern.id
                        ? 'border-purple-500 bg-purple-50'
                        : 'border-gray-200 hover:border-purple-300'
                    ]"
                  >
                    <div class="text-2xl mb-1">{{ pattern.emoji }}</div>
                    <div class="text-xs text-gray-600">{{ pattern.name }}</div>
                  </button>
                </div>
              </div>

              <!-- 图案颜色 -->
              <div v-if="currentAppearance.patternConfig.type !== 'none'" class="option-group">
                <label class="block text-sm font-medium text-gray-700 mb-3">图案颜色</label>
                <div class="flex items-center space-x-3">
                  <input 
                    type="color" 
                    v-model="currentAppearance.patternConfig.color"
                    class="w-12 h-12 rounded-lg border-2 border-gray-300 cursor-pointer"
                  />
                  <div class="grid grid-cols-6 gap-2">
                    <button 
                      v-for="color in colorPalette" 
                      :key="`pattern-${color}`"
                      @click="currentAppearance.patternConfig.color = color"
                      :style="{ backgroundColor: color }"
                      class="w-8 h-8 rounded-lg border-2 border-gray-300 hover:scale-110 transition-transform"
                    />
                  </div>
                </div>
              </div>

              <!-- 图案透明度 -->
              <div v-if="currentAppearance.patternConfig.type !== 'none'" class="option-group">
                <label class="block text-sm font-medium text-gray-700 mb-3">
                  图案透明度: {{ Math.round(currentAppearance.patternConfig.opacity * 100) }}%
                </label>
                <input 
                  type="range" 
                  min="0.1" 
                  max="1" 
                  step="0.1"
                  v-model="currentAppearance.patternConfig.opacity"
                  class="w-full h-2 bg-gray-200 rounded-lg appearance-none cursor-pointer"
                />
              </div>
            </div>
          </div>

          <!-- 装饰品 -->
          <div v-if="activeTab === 'accessories'" class="customization-panel">
            <div class="space-y-6">
              <!-- 帽子 -->
              <div class="option-group">
                <label class="block text-sm font-medium text-gray-700 mb-3">帽子</label>
                <div class="grid grid-cols-3 gap-2">
                  <button 
                    v-for="hat in hatTypes" 
                    :key="hat.id"
                    @click="currentAppearance.accessoryConfig.hatType = hat.id"
                    :class="[
                      'p-3 border-2 rounded-lg text-center transition-all',
                      currentAppearance.accessoryConfig.hatType === hat.id
                        ? 'border-green-500 bg-green-50'
                        : 'border-gray-200 hover:border-green-300'
                    ]"
                  >
                    <div class="text-2xl mb-1">{{ hat.emoji }}</div>
                    <div class="text-xs text-gray-600">{{ hat.name }}</div>
                  </button>
                </div>
              </div>

              <!-- 项圈 -->
              <div class="option-group">
                <label class="block text-sm font-medium text-gray-700 mb-3">项圈</label>
                <div class="grid grid-cols-3 gap-2">
                  <button 
                    v-for="collar in collarTypes" 
                    :key="collar.id"
                    @click="currentAppearance.accessoryConfig.collarType = collar.id"
                    :class="[
                      'p-3 border-2 rounded-lg text-center transition-all',
                      currentAppearance.accessoryConfig.collarType === collar.id
                        ? 'border-green-500 bg-green-50'
                        : 'border-gray-200 hover:border-green-300'
                    ]"
                  >
                    <div class="text-2xl mb-1">{{ collar.emoji }}</div>
                    <div class="text-xs text-gray-600">{{ collar.name }}</div>
                  </button>
                </div>
              </div>

              <!-- 眼镜 -->
              <div class="option-group">
                <label class="block text-sm font-medium text-gray-700 mb-3">眼镜</label>
                <div class="grid grid-cols-2 gap-2">
                  <button 
                    v-for="glasses in glassesTypes" 
                    :key="glasses.id"
                    @click="currentAppearance.accessoryConfig.glassesType = glasses.id"
                    :class="[
                      'p-3 border-2 rounded-lg text-center transition-all',
                      currentAppearance.accessoryConfig.glassesType === glasses.id
                        ? 'border-green-500 bg-green-50'
                        : 'border-gray-200 hover:border-green-300'
                    ]"
                  >
                    <div class="text-2xl mb-1">{{ glasses.emoji }}</div>
                    <div class="text-xs text-gray-600">{{ glasses.name }}</div>
                  </button>
                </div>
              </div>

              <!-- 装饰品颜色 -->
              <div class="option-group">
                <label class="block text-sm font-medium text-gray-700 mb-3">装饰品颜色</label>
                <div class="space-y-3">
                  <div v-if="currentAppearance.accessoryConfig.hatType !== 'none'">
                    <label class="block text-xs text-gray-500 mb-1">帽子颜色</label>
                    <div class="grid grid-cols-6 gap-2">
                      <button 
                        v-for="color in accessoryColors" 
                        :key="`hat-${color}`"
                        @click="currentAppearance.accessoryConfig.accessoryColors.hat = color"
                        :style="{ backgroundColor: color }"
                        class="w-8 h-8 rounded-lg border-2 border-gray-300 hover:scale-110 transition-transform"
                      />
                    </div>
                  </div>
                  
                  <div v-if="currentAppearance.accessoryConfig.collarType !== 'none'">
                    <label class="block text-xs text-gray-500 mb-1">项圈颜色</label>
                    <div class="grid grid-cols-6 gap-2">
                      <button 
                        v-for="color in accessoryColors" 
                        :key="`collar-${color}`"
                        @click="currentAppearance.accessoryConfig.accessoryColors.collar = color"
                        :style="{ backgroundColor: color }"
                        class="w-8 h-8 rounded-lg border-2 border-gray-300 hover:scale-110 transition-transform"
                      />
                    </div>
                  </div>
                  
                  <div v-if="currentAppearance.accessoryConfig.glassesType !== 'none'">
                    <label class="block text-xs text-gray-500 mb-1">眼镜颜色</label>
                    <div class="grid grid-cols-6 gap-2">
                      <button 
                        v-for="color in accessoryColors" 
                        :key="`glasses-${color}`"
                        @click="currentAppearance.accessoryConfig.accessoryColors.glasses = color"
                        :style="{ backgroundColor: color }"
                        class="w-8 h-8 rounded-lg border-2 border-gray-300 hover:scale-110 transition-transform"
                      />
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 身体比例 -->
          <div v-if="activeTab === 'proportions'" class="customization-panel">
            <div class="space-y-6">
              <!-- 胖瘦程度 -->
              <div class="option-group">
                <label class="block text-sm font-medium text-gray-700 mb-3">
                  胖瘦程度: {{ getFatnessLabel(currentAppearance.bodyProportions.fatness) }}
                </label>
                <input 
                  type="range" 
                  min="20" 
                  max="80" 
                  v-model="currentAppearance.bodyProportions.fatness"
                  class="w-full h-2 bg-gray-200 rounded-lg appearance-none cursor-pointer"
                />
                <div class="flex justify-between text-xs text-gray-500 mt-1">
                  <span>瘦</span>
                  <span>胖</span>
                </div>
              </div>

              <!-- 高矮程度 -->
              <div class="option-group">
                <label class="block text-sm font-medium text-gray-700 mb-3">
                  高矮程度: {{ getHeightLabel(currentAppearance.bodyProportions.height) }}
                </label>
                <input 
                  type="range" 
                  min="30" 
                  max="70" 
                  v-model="currentAppearance.bodyProportions.height"
                  class="w-full h-2 bg-gray-200 rounded-lg appearance-none cursor-pointer"
                />
                <div class="flex justify-between text-xs text-gray-500 mt-1">
                  <span>矮</span>
                  <span>高</span>
                </div>
              </div>

              <!-- 头部大小 -->
              <div class="option-group">
                <label class="block text-sm font-medium text-gray-700 mb-3">
                  头部大小: {{ getHeadSizeLabel(currentAppearance.bodyProportions.headSize) }}
                </label>
                <input 
                  type="range" 
                  min="30" 
                  max="70" 
                  v-model="currentAppearance.bodyProportions.headSize"
                  class="w-full h-2 bg-gray-200 rounded-lg appearance-none cursor-pointer"
                />
                <div class="flex justify-between text-xs text-gray-500 mt-1">
                  <span>小</span>
                  <span>大</span>
                </div>
              </div>

              <!-- 眼睛大小 -->
              <div class="option-group">
                <label class="block text-sm font-medium text-gray-700 mb-3">
                  眼睛大小: {{ getEyeSizeLabel(currentAppearance.bodyProportions.eyeSize) }}
                </label>
                <input 
                  type="range" 
                  min="30" 
                  max="70" 
                  v-model="currentAppearance.bodyProportions.eyeSize"
                  class="w-full h-2 bg-gray-200 rounded-lg appearance-none cursor-pointer"
                />
                <div class="flex justify-between text-xs text-gray-500 mt-1">
                  <span>小</span>
                  <span>大</span>
                </div>
              </div>

              <!-- 耳朵大小 -->
              <div class="option-group">
                <label class="block text-sm font-medium text-gray-700 mb-3">
                  耳朵大小: {{ getEarSizeLabel(currentAppearance.bodyProportions.earSize) }}
                </label>
                <input 
                  type="range" 
                  min="30" 
                  max="70" 
                  v-model="currentAppearance.bodyProportions.earSize"
                  class="w-full h-2 bg-gray-200 rounded-lg appearance-none cursor-pointer"
                />
                <div class="flex justify-between text-xs text-gray-500 mt-1">
                  <span>小</span>
                  <span>大</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部按钮 -->
    <div class="customizer-footer mt-8">
      <div class="flex justify-center space-x-4">
        <button 
          @click="resetToDefault"
          class="px-6 py-3 bg-gray-500 text-white font-semibold rounded-2xl hover:bg-gray-600 transition-colors"
        >
          🔄 重置默认
        </button>
        <button 
          @click="saveAppearance"
          :disabled="saving"
          class="px-8 py-3 bg-gradient-to-r from-purple-500 to-pink-600 text-white font-semibold rounded-2xl hover:from-purple-600 hover:to-pink-700 disabled:opacity-50 transition-all transform hover:scale-105"
        >
          {{ saving ? '保存中...' : '💾 保存造型' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, watch } from 'vue'
import PetRenderer from './PetRenderer.vue'

// Props and Emits
interface Props {
  petData?: any
  visible?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  visible: false
})

const emits = defineEmits<{
  save: [appearance: any]
  cancel: []
}>()

// Reactive data
const activeTab = ref('body')
const animationState = ref('idle')
const currentMood = ref('neutral')
const saving = ref(false)
const isHeterochromia = ref(false)

// 当前外观配置
const currentAppearance = reactive({
  bodyParts: {
    headShape: 'round',
    earType: 'pointed',
    eyeType: 'large',
    mouthType: 'small',
    tailType: 'normal'
  },
  colorScheme: {
    primaryColor: '#FF8C00',
    secondaryColor: '#FFA500',
    leftEyeColor: '#0000FF',
    rightEyeColor: '#0000FF',
    noseColor: '#FFB6C1'
  },
  patternConfig: {
    type: 'none',
    color: '#000000',
    opacity: 0.7
  },
  accessoryConfig: {
    hatType: 'none',
    collarType: 'none',
    glassesType: 'none',
    accessoryColors: {
      hat: '#FF0000',
      collar: '#00FF00',
      glasses: '#000000'
    }
  },
  bodyProportions: {
    fatness: 50,
    height: 50,
    headSize: 50,
    eyeSize: 50,
    earSize: 50
  }
})

// UI 配置数据
const tabs = [
  { id: 'body', name: '身体部位', icon: '🦴' },
  { id: 'colors', name: '颜色', icon: '🎨' },
  { id: 'patterns', name: '图案', icon: '🌟' },
  { id: 'accessories', name: '装饰品', icon: '👑' },
  { id: 'proportions', name: '比例', icon: '📏' }
]

const animations = [
  { id: 'idle', name: '闲置', emoji: '😴' },
  { id: 'walking', name: '行走', emoji: '🚶' },
  { id: 'playing', name: '玩耍', emoji: '🎾' },
  { id: 'eating', name: '进食', emoji: '🍽️' },
  { id: 'sleeping', name: '睡觉', emoji: '💤' }
]

const moods = [
  { id: 'neutral', name: '平静', emoji: '😐' },
  { id: 'happy', name: '开心', emoji: '😊' },
  { id: 'sleepy', name: '困倦', emoji: '😴' },
  { id: 'angry', name: '生气', emoji: '😠' },
  { id: 'curious', name: '好奇', emoji: '🤔' }
]

const headShapes = [
  { id: 'round', name: '圆形', emoji: '⭕' },
  { id: 'oval', name: '椭圆', emoji: '🥚' },
  { id: 'square', name: '方形', emoji: '⬜' },
  { id: 'triangular', name: '三角', emoji: '🔺' }
]

const earTypes = [
  { id: 'pointed', name: '尖耳', emoji: '🔺' },
  { id: 'round', name: '圆耳', emoji: '⭕' },
  { id: 'floppy', name: '垂耳', emoji: '👂' },
  { id: 'long', name: '长耳', emoji: '🐰' },
  { id: 'horn', name: '角状', emoji: '🦄' },
  { id: 'none', name: '无耳', emoji: '⚫' }
]

const eyeTypes = [
  { id: 'large', name: '大眼', emoji: '👀' },
  { id: 'small', name: '小眼', emoji: '👁️' },
  { id: 'sleepy', name: '眯眯眼', emoji: '😴' },
  { id: 'mysterious', name: '神秘', emoji: '🔮' },
  { id: 'friendly', name: '友善', emoji: '😊' }
]

const mouthTypes = [
  { id: 'small', name: '小嘴', emoji: '😮' },
  { id: 'open', name: '张嘴', emoji: '😲' },
  { id: 'tiny', name: '微张', emoji: '😗' },
  { id: 'wise', name: '睿智', emoji: '🧙' },
  { id: 'content', name: '满足', emoji: '😌' },
  { id: 'beak', name: '鸟嘴', emoji: '🐦' }
]

const patternTypes = [
  { id: 'none', name: '无图案', emoji: '⚫' },
  { id: 'stripes', name: '条纹', emoji: '🦓' },
  { id: 'spots', name: '斑点', emoji: '🐆' },
  { id: 'gradient', name: '渐变', emoji: '🌈' },
  { id: 'heart', name: '爱心', emoji: '💖' },
  { id: 'scales', name: '鳞片', emoji: '🐉' }
]

const hatTypes = [
  { id: 'none', name: '无帽子', emoji: '⚫' },
  { id: 'baseball_cap', name: '棒球帽', emoji: '🧢' },
  { id: 'beret', name: '贝雷帽', emoji: '👨‍🎨' },
  { id: 'crown', name: '王冠', emoji: '👑' },
  { id: 'bow', name: '蝴蝶结', emoji: '🎀' }
]

const collarTypes = [
  { id: 'none', name: '无项圈', emoji: '⚫' },
  { id: 'bow_tie', name: '蝴蝶结', emoji: '🎀' },
  { id: 'bell', name: '铃铛', emoji: '🔔' },
  { id: 'flower_wreath', name: '花环', emoji: '🌸' }
]

const glassesTypes = [
  { id: 'none', name: '无眼镜', emoji: '⚫' },
  { id: 'round_glasses', name: '圆框镜', emoji: '🤓' },
  { id: 'sunglasses', name: '墨镜', emoji: '😎' },
  { id: 'monocle', name: '单片镜', emoji: '🧐' }
]

// 颜色配置
const colorPalette = [
  '#FF8C00', '#8B4513', '#FFFFFF', '#F4A460', '#9932CC', '#000000',
  '#FF0000', '#00FF00', '#0000FF', '#FFFF00', '#FF00FF', '#00FFFF',
  '#FFA500', '#DEB887', '#F0F0F0', '#FFE4B5', '#8A2BE2', '#696969',
  '#FF4500', '#32CD32', '#4169E1', '#FFD700', '#DA70D6', '#40E0D0'
]

const eyeColors = [
  '#0000FF', '#00FF00', '#8B4513', '#9932CC', '#FF1493', '#32CD32',
  '#FF6347', '#4169E1', '#FFD700', '#FF69B4', '#00CED1', '#FFA500'
]

const noseColors = [
  '#FFB6C1', '#000000', '#8B4513', '#FF69B4'
]

const accessoryColors = [
  '#FF0000', '#00FF00', '#0000FF', '#FFFF00', '#FF00FF', '#00FFFF',
  '#FFA500', '#9932CC', '#FFD700', '#FF69B4', '#32CD32', '#FF6347'
]

// 计算属性和方法
const getFatnessLabel = (value: number) => {
  if (value < 35) return '偏瘦'
  if (value < 65) return '适中'
  return '偏胖'
}

const getHeightLabel = (value: number) => {
  if (value < 40) return '较矮'
  if (value < 60) return '适中'
  return '较高'
}

const getHeadSizeLabel = (value: number) => {
  if (value < 40) return '较小'
  if (value < 60) return '适中'
  return '较大'
}

const getEyeSizeLabel = (value: number) => {
  if (value < 40) return '较小'
  if (value < 60) return '适中'
  return '较大'
}

const getEarSizeLabel = (value: number) => {
  if (value < 40) return '较小'
  if (value < 60) return '适中'
  return '较大'
}

const handleHeterochromiaChange = () => {
  if (!isHeterochromia.value) {
    currentAppearance.colorScheme.rightEyeColor = currentAppearance.colorScheme.leftEyeColor
  }
}

const handleEyeColorChange = () => {
  if (!isHeterochromia.value) {
    currentAppearance.colorScheme.rightEyeColor = currentAppearance.colorScheme.leftEyeColor
  }
}

const setEyeColor = (color: string, eye: 'left' | 'right') => {
  if (eye === 'left') {
    currentAppearance.colorScheme.leftEyeColor = color
    if (!isHeterochromia.value) {
      currentAppearance.colorScheme.rightEyeColor = color
    }
  } else {
    currentAppearance.colorScheme.rightEyeColor = color
  }
}

const resetToDefault = () => {
  // 重置为默认外观
  Object.assign(currentAppearance, {
    bodyParts: {
      headShape: 'round',
      earType: 'pointed',
      eyeType: 'large',
      mouthType: 'small',
      tailType: 'normal'
    },
    colorScheme: {
      primaryColor: '#FF8C00',
      secondaryColor: '#FFA500',
      leftEyeColor: '#0000FF',
      rightEyeColor: '#0000FF',
      noseColor: '#FFB6C1'
    },
    patternConfig: {
      type: 'none',
      color: '#000000',
      opacity: 0.7
    },
    accessoryConfig: {
      hatType: 'none',
      collarType: 'none',
      glassesType: 'none',
      accessoryColors: {
        hat: '#FF0000',
        collar: '#00FF00',
        glasses: '#000000'
      }
    },
    bodyProportions: {
      fatness: 50,
      height: 50,
      headSize: 50,
      eyeSize: 50,
      earSize: 50
    }
  })
  isHeterochromia.value = false
}

const saveAppearance = async () => {
  saving.value = true
  try {
    // 发送保存请求
    emits('save', currentAppearance)
    // 模拟API调用延迟
    await new Promise(resolve => setTimeout(resolve, 500))
  } finally {
    saving.value = false
  }
}

// 监听异瞳状态
watch(() => currentAppearance.colorScheme.leftEyeColor, () => {
  if (currentAppearance.colorScheme.leftEyeColor !== currentAppearance.colorScheme.rightEyeColor) {
    isHeterochromia.value = true
  }
})

watch(() => currentAppearance.colorScheme.rightEyeColor, () => {
  if (currentAppearance.colorScheme.leftEyeColor !== currentAppearance.colorScheme.rightEyeColor) {
    isHeterochromia.value = true
  }
})

// 初始化
onMounted(() => {
  if (props.petData?.appearance) {
    Object.assign(currentAppearance, props.petData.appearance)
    // 检查是否为异瞳
    isHeterochromia.value = currentAppearance.colorScheme.leftEyeColor !== currentAppearance.colorScheme.rightEyeColor
  }
})
</script>

<style scoped>
.pet-customizer {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.customizer-header {
  text-align: center;
  margin-bottom: 2rem;
}

.customization-panel {
  min-height: 200px;
}

.option-group {
  margin-bottom: 1.5rem;
}

.option-group:last-child {
  margin-bottom: 0;
}

/* 自定义滑块样式 */
input[type="range"] {
  -webkit-appearance: none;
  appearance: none;
}

input[type="range"]::-webkit-slider-thumb {
  -webkit-appearance: none;
  appearance: none;
  height: 20px;
  width: 20px;
  border-radius: 50%;
  background: #4F46E5;
  cursor: pointer;
  border: 2px solid #ffffff;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.2);
}

input[type="range"]::-moz-range-thumb {
  height: 20px;
  width: 20px;
  border-radius: 50%;
  background: #4F46E5;
  cursor: pointer;
  border: 2px solid #ffffff;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.2);
}

/* 自定义颜色选择器样式 */
input[type="color"] {
  -webkit-appearance: none;
  border: none;
  cursor: pointer;
}

input[type="color"]::-webkit-color-swatch-wrapper {
  padding: 0;
}

input[type="color"]::-webkit-color-swatch {
  border: none;
  border-radius: 8px;
}

/* 滚动条样式 */
.customization-options > div {
  scrollbar-width: thin;
  scrollbar-color: #CBD5E0 #F7FAFC;
}

.customization-options > div::-webkit-scrollbar {
  width: 6px;
}

.customization-options > div::-webkit-scrollbar-track {
  background: #F7FAFC;
  border-radius: 3px;
}

.customization-options > div::-webkit-scrollbar-thumb {
  background: #CBD5E0;
  border-radius: 3px;
}

.customization-options > div::-webkit-scrollbar-thumb:hover {
  background: #A0AEC0;
}

/* 移动端优化 */
@media (max-width: 768px) {
  .pet-customizer {
    padding: 15px;
  }
  
  .grid.lg\\:grid-cols-2 {
    grid-template-columns: 1fr;
    gap: 1.5rem;
  }
  
  .customization-options > div {
    max-height: 60vh;
  }
  
  .tabs-nav .flex {
    overflow-x: auto;
    -webkit-overflow-scrolling: touch;
  }
  
  .tabs-nav button {
    white-space: nowrap;
    flex-shrink: 0;
  }
}
</style>