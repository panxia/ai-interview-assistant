<template>
  <div class="pet-customizer">
    <!-- 标题 -->
    <div class="text-center mb-6">
      <h2 class="text-3xl font-bold bg-gradient-to-r from-purple-600 to-pink-600 bg-clip-text text-transparent mb-2">
        🎨 生命工坊 🎨
      </h2>
      <p class="text-gray-600">创造你独一无二的专属宠物</p>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-2 gap-8">
      <!-- 左侧：预览区域 -->
      <div class="preview-section">
        <div class="bg-white/80 backdrop-blur-sm rounded-3xl p-6 shadow-lg">
          <h3 class="text-xl font-semibold mb-4 text-center">✨ 预览效果 ✨</h3>
          
          <!-- 宠物预览 -->
          <div class="flex justify-center mb-6">
            <PetRenderer 
              :appearance="petAppearance"
              :size="300"
              :interactive="false"
              :show-effects="true"
            />
          </div>
          
          <!-- 宠物信息 -->
          <div class="bg-gray-50 rounded-2xl p-4 mb-4">
            <h4 class="font-semibold mb-2">🏷️ 基本信息</h4>
            <div class="space-y-1">
              <p><span class="font-medium">名字:</span> {{ petName || '未命名' }}</p>
              <p><span class="font-medium">种类:</span> {{ selectedType?.displayName || '未选择' }}</p>
              <p><span class="font-medium">性格:</span> {{ selectedPersonality?.displayName || '未选择' }}</p>
            </div>
          </div>
          
          <!-- 外观描述 -->
          <div class="bg-blue-50 rounded-2xl p-4">
            <h4 class="font-semibold mb-2">🎨 外观描述</h4>
            <p class="text-sm text-gray-700">{{ getAppearanceDescription() }}</p>
          </div>
        </div>
      </div>

      <!-- 右侧：自定义选项 -->
      <div class="customization-section">
        <!-- 步骤指示器 -->
        <div class="flex justify-center mb-6">
          <div class="flex space-x-4">
            <div 
              v-for="(step, index) in steps" 
              :key="index"
              :class="[
                'w-10 h-10 rounded-full flex items-center justify-center text-sm font-semibold transition-all duration-300',
                currentStep === index 
                  ? 'bg-purple-500 text-white shadow-lg' 
                  : currentStep > index 
                    ? 'bg-green-500 text-white' 
                    : 'bg-gray-200 text-gray-500'
              ]"
            >
              {{ index + 1 }}
            </div>
          </div>
        </div>

        <!-- 步骤内容 -->
        <div class="bg-white/80 backdrop-blur-sm rounded-3xl p-6 shadow-lg">
          <h3 class="text-xl font-semibold mb-4 text-center">{{ steps[currentStep] }}</h3>

          <!-- 步骤 1: 基础设定 -->
          <div v-if="currentStep === 0" class="space-y-6">
            <!-- 宠物名字 -->
            <div>
              <label class="block text-sm font-medium mb-2">🏷️ 宠物名字</label>
              <input 
                v-model="petName"
                type="text" 
                placeholder="给你的宠物起个可爱的名字..."
                class="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:border-purple-400 focus:outline-none"
              />
            </div>

            <!-- 宠物类型 -->
            <div>
              <label class="block text-sm font-medium mb-2">🧬 选择DNA模板</label>
              <div class="grid grid-cols-3 gap-3">
                <div 
                  v-for="type in petTypes" 
                  :key="type.name"
                  @click="selectType(type)"
                  :class="[
                    'cursor-pointer p-4 rounded-xl border-2 transition-all duration-300 text-center',
                    selectedType?.name === type.name 
                      ? 'border-purple-400 bg-purple-50 transform scale-105' 
                      : 'border-gray-200 hover:border-purple-300'
                  ]"
                >
                  <div class="text-3xl mb-2">{{ type.emoji }}</div>
                  <div class="text-sm font-medium">{{ type.displayName }}</div>
                </div>
              </div>
            </div>

            <!-- 性格类型 -->
            <div>
              <label class="block text-sm font-medium mb-2">🧠 性格特征</label>
              <div class="grid grid-cols-2 gap-3">
                <div 
                  v-for="personality in personalityTypes" 
                  :key="personality.type"
                  @click="selectPersonality(personality)"
                  :class="[
                    'cursor-pointer p-3 rounded-xl border-2 transition-all duration-300',
                    selectedPersonality?.type === personality.type 
                      ? 'border-purple-400 bg-purple-50' 
                      : 'border-gray-200 hover:border-purple-300'
                  ]"
                >
                  <div class="font-medium text-sm">{{ personality.displayName }}</div>
                  <div class="text-xs text-gray-600 mt-1">{{ personality.description }}</div>
                </div>
              </div>
            </div>
          </div>

          <!-- 步骤 2: 头部设计 -->
          <div v-else-if="currentStep === 1" class="space-y-6">
            <!-- 头部形状 -->
            <div>
              <label class="block text-sm font-medium mb-2">👤 头部形状</label>
              <div class="grid grid-cols-4 gap-3">
                <div 
                  v-for="shape in headShapes" 
                  :key="shape.value"
                  @click="petAppearance.headShape = shape.value"
                  :class="[
                    'cursor-pointer p-3 rounded-xl border-2 transition-all duration-300 text-center',
                    petAppearance.headShape === shape.value 
                      ? 'border-purple-400 bg-purple-50' 
                      : 'border-gray-200 hover:border-purple-300'
                  ]"
                >
                  <div class="text-lg mb-1">{{ shape.icon }}</div>
                  <div class="text-xs">{{ shape.label }}</div>
                </div>
              </div>
            </div>

            <!-- 耳朵样式 -->
            <div>
              <label class="block text-sm font-medium mb-2">👂 耳朵样式</label>
              <div class="grid grid-cols-3 gap-3">
                <div 
                  v-for="ear in earStyles" 
                  :key="ear.value"
                  @click="petAppearance.earStyle = ear.value"
                  :class="[
                    'cursor-pointer p-3 rounded-xl border-2 transition-all duration-300 text-center',
                    petAppearance.earStyle === ear.value 
                      ? 'border-purple-400 bg-purple-50' 
                      : 'border-gray-200 hover:border-purple-300'
                  ]"
                >
                  <div class="text-lg mb-1">{{ ear.icon }}</div>
                  <div class="text-xs">{{ ear.label }}</div>
                </div>
              </div>
            </div>

            <!-- 眼睛类型 -->
            <div>
              <label class="block text-sm font-medium mb-2">👀 眼睛类型</label>
              <div class="grid grid-cols-3 gap-3">
                <div 
                  v-for="eye in eyeTypes" 
                  :key="eye.value"
                  @click="petAppearance.eyeType = eye.value"
                  :class="[
                    'cursor-pointer p-3 rounded-xl border-2 transition-all duration-300 text-center',
                    petAppearance.eyeType === eye.value 
                      ? 'border-purple-400 bg-purple-50' 
                      : 'border-gray-200 hover:border-purple-300'
                  ]"
                >
                  <div class="text-lg mb-1">{{ eye.icon }}</div>
                  <div class="text-xs">{{ eye.label }}</div>
                </div>
              </div>
            </div>

            <!-- 嘴巴表情 -->
            <div>
              <label class="block text-sm font-medium mb-2">👄 嘴巴表情</label>
              <div class="grid grid-cols-3 gap-3">
                <div 
                  v-for="mouth in mouthExpressions" 
                  :key="mouth.value"
                  @click="petAppearance.mouthExpression = mouth.value"
                  :class="[
                    'cursor-pointer p-3 rounded-xl border-2 transition-all duration-300 text-center',
                    petAppearance.mouthExpression === mouth.value 
                      ? 'border-purple-400 bg-purple-50' 
                      : 'border-gray-200 hover:border-purple-300'
                  ]"
                >
                  <div class="text-lg mb-1">{{ mouth.icon }}</div>
                  <div class="text-xs">{{ mouth.label }}</div>
                </div>
              </div>
            </div>
          </div>

          <!-- 步骤 3: 颜色搭配 -->
          <div v-else-if="currentStep === 2" class="space-y-6">
            <!-- 主色调 -->
            <div>
              <label class="block text-sm font-medium mb-2">🎨 主色调</label>
              <div class="grid grid-cols-6 gap-3 mb-3">
                <div 
                  v-for="color in primaryColors" 
                  :key="color.value"
                  @click="petAppearance.primaryColor = color.value"
                  :class="[
                    'w-12 h-12 rounded-full cursor-pointer border-4 transition-all duration-300',
                    petAppearance.primaryColor === color.value 
                      ? 'border-purple-400 transform scale-110' 
                      : 'border-gray-200 hover:border-purple-300'
                  ]"
                  :style="{ backgroundColor: color.value }"
                  :title="color.name"
                ></div>
              </div>
              <input 
                v-model="petAppearance.primaryColor"
                type="color" 
                class="w-full h-10 rounded-lg border-2 border-gray-200"
              />
            </div>

            <!-- 辅助色 -->
            <div>
              <label class="block text-sm font-medium mb-2">🌈 辅助色</label>
              <div class="grid grid-cols-6 gap-3 mb-3">
                <div 
                  v-for="color in secondaryColors" 
                  :key="color.value"
                  @click="petAppearance.secondaryColor = color.value"
                  :class="[
                    'w-12 h-12 rounded-full cursor-pointer border-4 transition-all duration-300',
                    petAppearance.secondaryColor === color.value 
                      ? 'border-purple-400 transform scale-110' 
                      : 'border-gray-200 hover:border-purple-300'
                  ]"
                  :style="{ backgroundColor: color.value }"
                  :title="color.name"
                ></div>
              </div>
              <input 
                v-model="petAppearance.secondaryColor"
                type="color" 
                class="w-full h-10 rounded-lg border-2 border-gray-200"
              />
            </div>

            <!-- 眼睛颜色 -->
            <div>
              <label class="block text-sm font-medium mb-2">👁️ 眼睛颜色</label>
              <div class="flex space-x-4">
                <div class="flex-1">
                  <label class="block text-xs text-gray-600 mb-1">左眼</label>
                  <input 
                    v-model="petAppearance.eyeColorLeft"
                    type="color" 
                    class="w-full h-10 rounded-lg border-2 border-gray-200"
                  />
                </div>
                <div class="flex-1">
                  <label class="block text-xs text-gray-600 mb-1">右眼</label>
                  <input 
                    v-model="petAppearance.eyeColorRight"
                    type="color" 
                    class="w-full h-10 rounded-lg border-2 border-gray-200"
                  />
                </div>
              </div>
              <label class="flex items-center mt-2">
                <input 
                  type="checkbox" 
                  :checked="petAppearance.eyeColorLeft === petAppearance.eyeColorRight"
                  @change="syncEyeColors"
                  class="mr-2"
                />
                <span class="text-sm">同色眼睛</span>
              </label>
            </div>

            <!-- 鼻子颜色 -->
            <div>
              <label class="block text-sm font-medium mb-2">👃 鼻子颜色</label>
              <input 
                v-model="petAppearance.noseColor"
                type="color" 
                class="w-full h-10 rounded-lg border-2 border-gray-200"
              />
            </div>
          </div>

          <!-- 步骤 4: 图案装饰 -->
          <div v-else-if="currentStep === 3" class="space-y-6">
            <!-- 图案类型 -->
            <div>
              <label class="block text-sm font-medium mb-2">🎭 图案类型</label>
              <div class="grid grid-cols-3 gap-3">
                <div 
                  v-for="pattern in patterns" 
                  :key="pattern.value"
                  @click="petAppearance.pattern = pattern.value"
                  :class="[
                    'cursor-pointer p-3 rounded-xl border-2 transition-all duration-300 text-center',
                    petAppearance.pattern === pattern.value 
                      ? 'border-purple-400 bg-purple-50' 
                      : 'border-gray-200 hover:border-purple-300'
                  ]"
                >
                  <div class="text-lg mb-1">{{ pattern.icon }}</div>
                  <div class="text-xs">{{ pattern.label }}</div>
                </div>
              </div>
            </div>

            <!-- 图案颜色 -->
            <div v-if="petAppearance.pattern !== 'none'">
              <label class="block text-sm font-medium mb-2">🌈 图案颜色</label>
              <input 
                v-model="petAppearance.patternColor"
                type="color" 
                class="w-full h-10 rounded-lg border-2 border-gray-200"
              />
            </div>
          </div>

          <!-- 步骤 5: 装饰配件 -->
          <div v-else-if="currentStep === 4" class="space-y-6">
            <!-- 帽子 -->
            <div>
              <label class="block text-sm font-medium mb-2">🎩 帽子</label>
              <div class="grid grid-cols-3 gap-3">
                <div 
                  v-for="hat in hats" 
                  :key="hat.value"
                  @click="petAppearance.hat = hat.value"
                  :class="[
                    'cursor-pointer p-3 rounded-xl border-2 transition-all duration-300 text-center',
                    petAppearance.hat === hat.value 
                      ? 'border-purple-400 bg-purple-50' 
                      : 'border-gray-200 hover:border-purple-300'
                  ]"
                >
                  <div class="text-lg mb-1">{{ hat.icon }}</div>
                  <div class="text-xs">{{ hat.label }}</div>
                </div>
              </div>
            </div>

            <!-- 项圈 -->
            <div>
              <label class="block text-sm font-medium mb-2">📿 项圈</label>
              <div class="grid grid-cols-3 gap-3">
                <div 
                  v-for="collar in collars" 
                  :key="collar.value"
                  @click="petAppearance.collar = collar.value"
                  :class="[
                    'cursor-pointer p-3 rounded-xl border-2 transition-all duration-300 text-center',
                    petAppearance.collar === collar.value 
                      ? 'border-purple-400 bg-purple-50' 
                      : 'border-gray-200 hover:border-purple-300'
                  ]"
                >
                  <div class="text-lg mb-1">{{ collar.icon }}</div>
                  <div class="text-xs">{{ collar.label }}</div>
                </div>
              </div>
            </div>

            <!-- 眼镜 -->
            <div>
              <label class="block text-sm font-medium mb-2">👓 眼镜</label>
              <div class="grid grid-cols-3 gap-3">
                <div 
                  v-for="glasses in glassesOptions" 
                  :key="glasses.value"
                  @click="petAppearance.glasses = glasses.value"
                  :class="[
                    'cursor-pointer p-3 rounded-xl border-2 transition-all duration-300 text-center',
                    petAppearance.glasses === glasses.value 
                      ? 'border-purple-400 bg-purple-50' 
                      : 'border-gray-200 hover:border-purple-300'
                  ]"
                >
                  <div class="text-lg mb-1">{{ glasses.icon }}</div>
                  <div class="text-xs">{{ glasses.label }}</div>
                </div>
              </div>
            </div>
          </div>

          <!-- 步骤 6: 特殊效果 -->
          <div v-else-if="currentStep === 5" class="space-y-6">
            <!-- 发光效果 -->
            <div>
              <label class="flex items-center">
                <input 
                  v-model="petAppearance.hasGlow"
                  type="checkbox" 
                  class="mr-3"
                />
                <span class="text-sm font-medium">✨ 发光效果</span>
              </label>
              <div v-if="petAppearance.hasGlow" class="mt-3">
                <label class="block text-xs text-gray-600 mb-1">发光颜色</label>
                <input 
                  v-model="petAppearance.glowColor"
                  type="color" 
                  class="w-full h-8 rounded-lg border-2 border-gray-200"
                />
              </div>
            </div>

            <!-- 星星闪烁 -->
            <div>
              <label class="flex items-center">
                <input 
                  v-model="petAppearance.hasSparkles"
                  type="checkbox" 
                  class="mr-3"
                />
                <span class="text-sm font-medium">⭐ 星星闪烁</span>
              </label>
            </div>

            <!-- 身体比例调节 -->
            <div>
              <label class="block text-sm font-medium mb-3">📏 身体比例</label>
              <div class="space-y-3">
                <div>
                  <label class="block text-xs text-gray-600 mb-1">胖瘦度</label>
                  <input 
                    v-model.number="petAppearance.bodyProportion.fatness"
                    type="range" 
                    min="0.5" 
                    max="1.5" 
                    step="0.1"
                    class="w-full"
                  />
                </div>
                <div>
                  <label class="block text-xs text-gray-600 mb-1">高矮度</label>
                  <input 
                    v-model.number="petAppearance.bodyProportion.height"
                    type="range" 
                    min="0.5" 
                    max="1.5" 
                    step="0.1"
                    class="w-full"
                  />
                </div>
                <div>
                  <label class="block text-xs text-gray-600 mb-1">头部大小</label>
                  <input 
                    v-model.number="petAppearance.bodyProportion.headSize"
                    type="range" 
                    min="0.5" 
                    max="1.5" 
                    step="0.1"
                    class="w-full"
                  />
                </div>
              </div>
            </div>
          </div>

          <!-- 导航按钮 -->
          <div class="flex justify-between mt-8">
            <button 
              @click="prevStep"
              :disabled="currentStep === 0"
              class="px-6 py-2 bg-gray-300 text-gray-700 rounded-xl hover:bg-gray-400 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
            >
              ← 上一步
            </button>
            
            <button 
              v-if="currentStep < steps.length - 1"
              @click="nextStep"
              class="px-6 py-2 bg-purple-500 text-white rounded-xl hover:bg-purple-600 transition-colors"
            >
              下一步 →
            </button>
            
            <button 
              v-else
              @click="createPet"
              :disabled="!canCreate"
              class="px-8 py-2 bg-gradient-to-r from-purple-500 to-pink-500 text-white rounded-xl hover:from-purple-600 hover:to-pink-600 disabled:opacity-50 disabled:cursor-not-allowed transition-all duration-300"
            >
              🎉 创造宠物 🎉
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, reactive } from 'vue'
import PetRenderer from './PetRenderer.vue'

// Props 和 Emits
const emits = defineEmits<{
  petCreated: [data: any]
}>()

// 响应式数据
const currentStep = ref(0)
const petName = ref('')
const selectedType = ref<any>(null)
const selectedPersonality = ref<any>(null)

const steps = [
  '🧬 基础设定',
  '👤 头部设计', 
  '🎨 颜色搭配',
  '🎭 图案装饰',
  '👗 装饰配件',
  '✨ 特殊效果'
]

// 宠物外观配置
const petAppearance = reactive({
  headShape: 'round' as 'round' | 'oval' | 'square' | 'heart',
  earStyle: 'pointed' as 'pointed' | 'round' | 'droopy' | 'none' | 'bunny',
  eyeType: 'normal' as 'normal' | 'big' | 'small' | 'sleepy' | 'star' | 'heart',
  mouthExpression: 'smile' as 'smile' | 'pout' | 'open' | 'closed' | 'tongue',
  primaryColor: '#FFA500',
  secondaryColor: '#FFFFFF',
  eyeColorLeft: '#4169E1',
  eyeColorRight: '#4169E1',
  noseColor: '#FFB6C1',
  pattern: 'none' as 'none' | 'stripes' | 'spots' | 'gradient' | 'hearts' | 'stars' | 'flowers',
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
})

// 选项数据
const petTypes = [
  { name: 'CAT', displayName: '小猫咪', emoji: '🐱', description: '可爱独立的小猫' },
  { name: 'DOG', displayName: '小狗狗', emoji: '🐶', description: '忠诚活泼的小狗' },
  { name: 'RABBIT', displayName: '小兔子', emoji: '🐰', description: '温顺可爱的兔子' },
  { name: 'HAMSTER', displayName: '小仓鼠', emoji: '🐹', description: '活泼机灵的仓鼠' },
  { name: 'DRAGON', displayName: '小龙', emoji: '🐲', description: '神秘强大的小龙' },
  { name: 'PANDA', displayName: '小熊猫', emoji: '🐼', description: '憨厚可爱的熊猫' }
]

const personalityTypes = [
  { type: 'PLAYFUL', displayName: '活泼型', description: '喜欢玩耍，精力充沛' },
  { type: 'LAZY', displayName: '慵懒型', description: '喜欢睡觉，享受悠闲' },
  { type: 'CURIOUS', displayName: '好奇型', description: '喜欢探索新事物' },
  { type: 'AFFECTIONATE', displayName: '亲人型', description: '依恋主人，喜欢陪伴' },
  { type: 'INDEPENDENT', displayName: '独立型', description: '自主性强，不需要太多陪伴' },
  { type: 'INTELLIGENT', displayName: '聪明型', description: '学习能力强，游戏表现优秀' }
]

const headShapes = [
  { value: 'round', label: '圆形', icon: '●' },
  { value: 'oval', label: '椭圆', icon: '○' },
  { value: 'square', label: '方形', icon: '■' },
  { value: 'heart', label: '心形', icon: '♥' }
]

const earStyles = [
  { value: 'pointed', label: '尖耳', icon: '▲' },
  { value: 'round', label: '圆耳', icon: '●' },
  { value: 'droopy', label: '垂耳', icon: '⌄' },
  { value: 'bunny', label: '兔耳', icon: '⫸' },
  { value: 'none', label: '无耳', icon: '○' }
]

const eyeTypes = [
  { value: 'normal', label: '普通', icon: '●●' },
  { value: 'big', label: '大眼', icon: '◉◉' },
  { value: 'small', label: '小眼', icon: '••' },
  { value: 'sleepy', label: '眯眯眼', icon: '一一' },
  { value: 'star', label: '星星眼', icon: '★★' },
  { value: 'heart', label: '爱心眼', icon: '♥♥' }
]

const mouthExpressions = [
  { value: 'smile', label: '微笑', icon: '😊' },
  { value: 'pout', label: '噘嘴', icon: '😗' },
  { value: 'open', label: '张嘴', icon: '😮' },
  { value: 'closed', label: '闭嘴', icon: '😑' },
  { value: 'tongue', label: '吐舌', icon: '😛' }
]

const primaryColors = [
  { name: '橙色', value: '#FFA500' },
  { name: '粉色', value: '#FFB6C1' },
  { name: '蓝色', value: '#87CEEB' },
  { name: '绿色', value: '#98FB98' },
  { name: '紫色', value: '#DDA0DD' },
  { name: '黄色', value: '#FFFF99' },
  { name: '红色', value: '#FF6B6B' },
  { name: '棕色', value: '#D2691E' },
  { name: '灰色', value: '#C0C0C0' },
  { name: '黑色', value: '#696969' },
  { name: '白色', value: '#F5F5F5' },
  { name: '金色', value: '#FFD700' }
]

const secondaryColors = [
  { name: '白色', value: '#FFFFFF' },
  { name: '奶黄', value: '#FFFACD' },
  { name: '淡粉', value: '#FFF0F5' },
  { name: '淡蓝', value: '#F0F8FF' },
  { name: '淡绿', value: '#F0FFF0' },
  { name: '淡紫', value: '#F8F8FF' }
]

const patterns = [
  { value: 'none', label: '无图案', icon: '○' },
  { value: 'stripes', label: '条纹', icon: '▦' },
  { value: 'spots', label: '斑点', icon: '●●●' },
  { value: 'hearts', label: '心形', icon: '♥♥♥' },
  { value: 'stars', label: '星星', icon: '★★★' },
  { value: 'flowers', label: '花朵', icon: '❀❀❀' }
]

const hats = [
  { value: 'none', label: '无帽子', icon: '○' },
  { value: 'baseball_cap', label: '棒球帽', icon: '🧢' },
  { value: 'beret', label: '贝雷帽', icon: '🎓' },
  { value: 'crown', label: '王冠', icon: '👑' },
  { value: 'wizard_hat', label: '巫师帽', icon: '🔮' },
  { value: 'santa_hat', label: '圣诞帽', icon: '🎅' }
]

const collars = [
  { value: 'none', label: '无项圈', icon: '○' },
  { value: 'bow_tie', label: '蝴蝶结', icon: '🎀' },
  { value: 'bell_collar', label: '铃铛', icon: '🔔' },
  { value: 'flower_wreath', label: '花环', icon: '🌸' },
  { value: 'scarf', label: '围巾', icon: '🧣' }
]

const glassesOptions = [
  { value: 'none', label: '无眼镜', icon: '○' },
  { value: 'round_glasses', label: '圆框镜', icon: '👓' },
  { value: 'sunglasses', label: '墨镜', icon: '🕶️' },
  { value: 'monocle', label: '单片镜', icon: '🧐' },
  { value: 'star_glasses', label: '星星镜', icon: '⭐' }
]

// 计算属性
const canCreate = computed(() => {
  return petName.value.trim() && selectedType.value && selectedPersonality.value
})

// 方法
const selectType = (type: any) => {
  selectedType.value = type
  // 根据类型设置默认外观
  switch (type.name) {
    case 'CAT':
      petAppearance.primaryColor = '#FFA500'
      petAppearance.earStyle = 'pointed'
      break
    case 'DOG':
      petAppearance.primaryColor = '#8B4513'
      petAppearance.earStyle = 'droopy'
      break
    case 'RABBIT':
      petAppearance.primaryColor = '#FFFFFF'
      petAppearance.earStyle = 'bunny'
      break
    case 'HAMSTER':
      petAppearance.primaryColor = '#DEB887'
      petAppearance.earStyle = 'round'
      break
    case 'DRAGON':
      petAppearance.primaryColor = '#800080'
      petAppearance.hasGlow = true
      break
    case 'PANDA':
      petAppearance.primaryColor = '#000000'
      petAppearance.secondaryColor = '#FFFFFF'
      break
  }
}

const selectPersonality = (personality: any) => {
  selectedPersonality.value = personality
}

const syncEyeColors = () => {
  petAppearance.eyeColorRight = petAppearance.eyeColorLeft
}

const getAppearanceDescription = () => {
  const parts = []
  
  if (selectedType.value) {
    parts.push(`一只${selectedType.value.displayName}`)
  }
  
  const shapeDesc = headShapes.find(s => s.value === petAppearance.headShape)?.label
  if (shapeDesc) {
    parts.push(`${shapeDesc}脸`)
  }
  
  const earDesc = earStyles.find(e => e.value === petAppearance.earStyle)?.label
  if (earDesc && petAppearance.earStyle !== 'none') {
    parts.push(`${earDesc}`)
  }
  
  const eyeDesc = eyeTypes.find(e => e.value === petAppearance.eyeType)?.label
  if (eyeDesc) {
    parts.push(`${eyeDesc}`)
  }
  
  if (petAppearance.pattern !== 'none') {
    const patternDesc = patterns.find(p => p.value === petAppearance.pattern)?.label
    if (patternDesc) {
      parts.push(`身上有${patternDesc}`)
    }
  }
  
  if (petAppearance.hasGlow) {
    parts.push('散发着神秘光芒')
  }
  
  if (petAppearance.hasSparkles) {
    parts.push('闪闪发光')
  }
  
  parts.push('的可爱宠物')
  
  return parts.join('，') || '正在设计中的宠物'
}

const nextStep = () => {
  if (currentStep.value < steps.length - 1) {
    currentStep.value++
  }
}

const prevStep = () => {
  if (currentStep.value > 0) {
    currentStep.value--
  }
}

const createPet = () => {
  const petData = {
    playerId: 'player1', // 简化版
    petName: petName.value.trim(),
    petType: selectedType.value.name,
    customization: {
      headShape: petAppearance.headShape,
      earStyle: petAppearance.earStyle,
      eyeType: petAppearance.eyeType,
      mouthExpression: petAppearance.mouthExpression,
      primaryColor: petAppearance.primaryColor,
      secondaryColor: petAppearance.secondaryColor,
      eyeColorLeft: petAppearance.eyeColorLeft,
      eyeColorRight: petAppearance.eyeColorRight,
      noseColor: petAppearance.noseColor,
      pattern: petAppearance.pattern,
      patternColor: petAppearance.patternColor,
      hat: petAppearance.hat,
      collar: petAppearance.collar,
      glasses: petAppearance.glasses,
      hasGlow: petAppearance.hasGlow,
      glowColor: petAppearance.glowColor,
      hasSparkles: petAppearance.hasSparkles,
      bodyProportion: petAppearance.bodyProportion,
      personalityType: selectedPersonality.value.type
    }
  }
  
  emits('petCreated', petData)
}
</script>

<style scoped>
.pet-customizer {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.preview-section {
  position: sticky;
  top: 20px;
}

/* 自定义滑块样式 */
input[type="range"] {
  appearance: none;
  height: 6px;
  background: #ddd;
  border-radius: 3px;
  outline: none;
}

input[type="range"]::-webkit-slider-thumb {
  appearance: none;
  width: 20px;
  height: 20px;
  background: #8B5CF6;
  border-radius: 50%;
  cursor: pointer;
}

input[type="range"]::-moz-range-thumb {
  width: 20px;
  height: 20px;
  background: #8B5CF6;
  border-radius: 50%;
  cursor: pointer;
  border: none;
}

/* 颜色选择器样式 */
input[type="color"] {
  border: none;
  border-radius: 8px;
  cursor: pointer;
}

/* 移动端适配 */
@media (max-width: 1024px) {
  .pet-customizer {
    grid-template-columns: 1fr;
  }
  
  .preview-section {
    position: static;
  }
}
</style>