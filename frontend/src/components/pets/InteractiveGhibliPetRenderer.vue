<template>
  <div class="interactive-ghibli-pet-renderer">
    <svg 
      :width="size" 
      :height="size" 
      viewBox="0 0 300 300" 
      class="pet-svg"
      :style="containerStyle"
      @mousedown="handleMouseDown"
      @mousemove="handleMouseMove"
      @mouseup="handleMouseUp"
      @mouseleave="handleMouseUp"
    >
      <!-- 定义渐变和滤镜 -->
      <defs>
        <!-- 身体渐变 - 增强立体感 -->
        <radialGradient id="bodyGradient" cx="0.3" cy="0.2" r="1.2">
          <stop offset="0%" :stop-color="lightenColor(petData.primaryColor, 45)" />
          <stop offset="20%" :stop-color="lightenColor(petData.primaryColor, 25)" />
          <stop offset="50%" :stop-color="lightenColor(petData.primaryColor, 8)" />
          <stop offset="75%" :stop-color="petData.primaryColor" />
          <stop offset="90%" :stop-color="darkenColor(petData.primaryColor, 12)" />
          <stop offset="100%" :stop-color="darkenColor(petData.primaryColor, 25)" />
        </radialGradient>
        
        <!-- 肚子渐变 -->
        <radialGradient id="bellyGradient" cx="0.3" cy="0.2" r="0.8">
          <stop offset="0%" :stop-color="lightenColor(petData.secondaryColor, 25)" />
          <stop offset="70%" :stop-color="petData.secondaryColor" />
          <stop offset="100%" :stop-color="darkenColor(petData.secondaryColor, 10)" />
        </radialGradient>
        
        <!-- 增强立体阴影 -->
        <filter id="softShadow" x="-50%" y="-50%" width="200%" height="200%">
          <feGaussianBlur in="SourceAlpha" stdDeviation="6"/>
          <feOffset dx="3" dy="8" result="offset"/>
          <feFlood flood-color="rgba(0,0,0,0.25)"/>
          <feComposite in2="offset" operator="in"/>
          <feMerge>
            <feMergeNode/>
            <feMergeNode in="SourceGraphic"/>
          </feMerge>
        </filter>
        
        <!-- 内部阴影效果 -->
        <filter id="innerShadow" x="-50%" y="-50%" width="200%" height="200%">
          <feGaussianBlur in="SourceGraphic" stdDeviation="3"/>
          <feOffset dx="1" dy="2" result="offset"/>
          <feFlood flood-color="rgba(0,0,0,0.1)"/>
          <feComposite in2="offset" operator="in"/>
          <feMerge>
            <feMergeNode in="SourceGraphic"/>
            <feMergeNode/>
          </feMerge>
        </filter>
        
        <!-- 选中高亮 -->
        <filter id="highlight" x="-20%" y="-20%" width="140%" height="140%">
          <feGaussianBlur stdDeviation="3" result="coloredBlur"/>
          <feFlood flood-color="#FFD700" result="glowColor"/>
          <feComposite in="glowColor" in2="coloredBlur" operator="in"/>
          <feMerge>
            <feMergeNode in="coloredBlur"/>
            <feMergeNode in="SourceGraphic"/>
          </feMerge>
        </filter>
      </defs>
      
      <!-- 背景光晕 -->
      <circle 
        v-if="petData.hasGlow"
        cx="150" 
        cy="150" 
        :r="glowRadius"
        :fill="petData.glowColor || '#FFF8DC'"
        opacity="0.2"
        class="warm-glow"
      />
      
      <!-- 宠物主体 -->
      <g :transform="mainTransform" class="pet-main">
        
        <!-- 身体 - 可拖拽 -->
        <ellipse 
          cx="150" 
          cy="200"
          :rx="adjustments.bodyWidth" 
          :ry="adjustments.bodyHeight"
          fill="url(#bodyGradient)"
          filter="url(#softShadow)"
          class="pet-body draggable-part"
          :class="{ highlighted: selectedPart === 'body' }"
          data-part="body"
          style="cursor: pointer;"
        />
        
        <!-- 肚子 -->
        <ellipse 
          cx="150" 
          cy="210"
          :rx="adjustments.bellyWidth" 
          :ry="adjustments.bellyHeight"
          fill="url(#bellyGradient)"
          class="pet-belly"
        />
        
        <!-- 头部 - 可拖拽，不同动物形状差异很大 -->
        <g class="pet-head draggable-part" 
           :class="{ highlighted: selectedPart === 'head' }"
           data-part="head"
           style="cursor: pointer;">
          
          <!-- 猫：圆形头 -->
          <circle 
            v-if="petData.petType === 'CAT'"
            cx="150" 
            cy="120"
            :r="adjustments.headSize"
            fill="url(#bodyGradient)"
            filter="url(#softShadow)"
            class="head-main"
          />
          
          <!-- 狗：椭圆形头，更长 -->
          <ellipse 
            v-if="petData.petType === 'DOG'"
            cx="150" 
            cy="120"
            :rx="adjustments.headSize * 0.9" 
            :ry="adjustments.headSize * 1.2"
            fill="url(#bodyGradient)"
            filter="url(#softShadow)"
            class="head-main"
          />
          
          <!-- 兔子：心形头 -->
          <g v-if="petData.petType === 'RABBIT'">
            <circle cx="135" cy="115" :r="adjustments.headSize * 0.7" fill="url(#bodyGradient)" />
            <circle cx="165" cy="115" :r="adjustments.headSize * 0.7" fill="url(#bodyGradient)" />
            <ellipse cx="150" cy="130" :rx="adjustments.headSize * 0.8" :ry="adjustments.headSize * 0.6" fill="url(#bodyGradient)" />
          </g>
          
          <!-- 仓鼠：超圆胖头 -->
          <ellipse 
            v-if="petData.petType === 'HAMSTER'"
            cx="150" 
            cy="120"
            :rx="adjustments.headSize * 1.3" 
            :ry="adjustments.headSize * 1.1"
            fill="url(#bodyGradient)"
            filter="url(#softShadow)"
            class="head-main"
          />
          
          <!-- 熊猫：圆形头 + 黑眼圈 -->
          <g v-if="petData.petType === 'PANDA'">
            <circle cx="150" cy="120" :r="adjustments.headSize" fill="url(#bodyGradient)" filter="url(#softShadow)" />
            <!-- 黑眼圈 -->
            <ellipse cx="135" cy="115" rx="15" ry="18" fill="#2F2F2F" opacity="0.8" />
            <ellipse cx="165" cy="115" rx="15" ry="18" fill="#2F2F2F" opacity="0.8" />
          </g>
          
          <!-- 脸颊腮红 -->
          <ellipse 
            cx="120" 
            cy="130"
            rx="12" 
            ry="8"
            :fill="lightenColor('#FFB6C1', 20)"
            opacity="0.7"
            class="cheek left"
          />
          <ellipse 
            cx="180" 
            cy="130"
            rx="12" 
            ry="8"
            :fill="lightenColor('#FFB6C1', 20)"
            opacity="0.7"
            class="cheek right"
          />
          
          <!-- 头部高光 -->
          <ellipse 
            cx="140" 
            cy="95"
            rx="8" 
            ry="12"
            :fill="lightenColor(petData.primaryColor, 60)"
            opacity="0.4"
            class="head-highlight"
          />
          
          <!-- 身体高光 -->
          <ellipse 
            cx="140" 
            cy="185"
            rx="12" 
            ry="15"
            :fill="lightenColor(petData.primaryColor, 50)"
            opacity="0.3"
            class="body-highlight"
          />
        </g>
        
        <!-- 耳朵 - 可拖拽，每种动物差异巨大 -->
        <g class="ears draggable-part" 
           :class="{ highlighted: selectedPart === 'ears' }"
           data-part="ears"
           style="cursor: pointer;">
          
          <!-- 猫耳：尖三角形 -->
          <g v-if="petData.petType === 'CAT'">
            <polygon :points="catEarPoints.left" :fill="petData.primaryColor" class="ear left-ear" />
            <polygon :points="catEarInnerPoints.left" :fill="lightenColor(petData.primaryColor, 30)" class="ear-inner" />
            <polygon :points="catEarPoints.right" :fill="petData.primaryColor" class="ear right-ear" />
            <polygon :points="catEarInnerPoints.right" :fill="lightenColor(petData.primaryColor, 30)" class="ear-inner" />
          </g>
          
          <!-- 狗耳：垂下的椭圆 -->
          <g v-if="petData.petType === 'DOG'">
            <ellipse cx="120" cy="100" :rx="adjustments.earSize" :ry="adjustments.earSize * 1.8" 
                     :fill="petData.primaryColor" transform="rotate(-30 120 100)" class="ear left-ear" />
            <ellipse cx="180" cy="100" :rx="adjustments.earSize" :ry="adjustments.earSize * 1.8" 
                     :fill="petData.primaryColor" transform="rotate(30 180 100)" class="ear right-ear" />
          </g>
          
          <!-- 兔耳：超长椭圆 -->
          <g v-if="petData.petType === 'RABBIT'">
            <ellipse cx="135" cy="70" :rx="adjustments.earSize * 0.6" :ry="adjustments.earSize * 2.5" 
                     :fill="petData.primaryColor" transform="rotate(-15 135 70)" class="ear left-ear" />
            <ellipse cx="135" cy="75" :rx="adjustments.earSize * 0.4" :ry="adjustments.earSize * 2.2" 
                     :fill="lightenColor(petData.primaryColor, 30)" transform="rotate(-15 135 75)" class="ear-inner" />
            <ellipse cx="165" cy="70" :rx="adjustments.earSize * 0.6" :ry="adjustments.earSize * 2.5" 
                     :fill="petData.primaryColor" transform="rotate(15 165 70)" class="ear right-ear" />
            <ellipse cx="165" cy="75" :rx="adjustments.earSize * 0.4" :ry="adjustments.earSize * 2.2" 
                     :fill="lightenColor(petData.primaryColor, 30)" transform="rotate(15 165 75)" class="ear-inner" />
          </g>
          
          <!-- 仓鼠耳：小圆耳 -->
          <g v-if="petData.petType === 'HAMSTER'">
            <circle cx="125" cy="90" :r="adjustments.earSize * 0.8" :fill="petData.primaryColor" class="ear left-ear" />
            <circle cx="127" cy="92" :r="adjustments.earSize * 0.5" :fill="lightenColor(petData.primaryColor, 40)" class="ear-inner" />
            <circle cx="175" cy="90" :r="adjustments.earSize * 0.8" :fill="petData.primaryColor" class="ear right-ear" />
            <circle cx="173" cy="92" :r="adjustments.earSize * 0.5" :fill="lightenColor(petData.primaryColor, 40)" class="ear-inner" />
          </g>
          
          <!-- 熊猫耳：黑色圆耳 -->
          <g v-if="petData.petType === 'PANDA'">
            <circle cx="125" cy="85" :r="adjustments.earSize" fill="#2F2F2F" class="ear left-ear" />
            <circle cx="175" cy="85" :r="adjustments.earSize" fill="#2F2F2F" class="ear right-ear" />
          </g>
        </g>
        
        <!-- 眼睛 - 可拖拽 -->
        <g class="eyes draggable-part" 
           :class="{ highlighted: selectedPart === 'eyes' }"
           data-part="eyes"
           style="cursor: pointer;">
          
          <!-- 左眼 -->
          <ellipse 
            cx="130" 
            cy="115"
            :rx="adjustments.eyeSize" 
            :ry="adjustments.eyeSize + 3"
            fill="white"
            class="eye-white left"
          />
          <ellipse 
            cx="130" 
            cy="115"
            :rx="adjustments.eyeSize - 4" 
            :ry="adjustments.eyeSize - 1"
            :fill="petData.eyeColorLeft || '#2E8B57'"
            class="eye-iris left"
          />
          <ellipse 
            cx="133" 
            cy="112"
            rx="3" 
            ry="4"
            fill="black"
            class="eye-pupil left"
          />
          <ellipse 
            cx="134" 
            cy="110"
            rx="3" 
            ry="3"
            fill="white"
            class="eye-highlight left"
          />
          <!-- 次要高光 -->
          <ellipse 
            cx="127" 
            cy="116"
            rx="1" 
            ry="1"
            fill="white"
            opacity="0.8"
            class="eye-highlight-small left"
          />
          
          <!-- 右眼 -->
          <ellipse 
            cx="170" 
            cy="115"
            :rx="adjustments.eyeSize" 
            :ry="adjustments.eyeSize + 3"
            fill="white"
            class="eye-white right"
          />
          <ellipse 
            cx="170" 
            cy="115"
            :rx="adjustments.eyeSize - 4" 
            :ry="adjustments.eyeSize - 1"
            :fill="petData.eyeColorRight || petData.eyeColorLeft || '#2E8B57'"
            class="eye-iris right"
          />
          <ellipse 
            cx="167" 
            cy="112"
            rx="3" 
            ry="4"
            fill="black"
            class="eye-pupil right"
          />
          <ellipse 
            cx="166" 
            cy="110"
            rx="3" 
            ry="3"
            fill="white"
            class="eye-highlight right"
          />
          <!-- 次要高光 -->
          <ellipse 
            cx="173" 
            cy="116"
            rx="1" 
            ry="1"
            fill="white"
            opacity="0.8"
            class="eye-highlight-small right"
          />
        </g>
        
        <!-- 鼻子和嘴巴 - 每种动物差异很大 -->
        <g class="face-features">
          <!-- 猫：小三角鼻 + 人字嘴 -->
          <g v-if="petData.petType === 'CAT'">
            <polygon points="150,125 145,132 155,132" :fill="petData.noseColor || '#FFB6C1'" class="nose" />
            <path d="M 145 135 Q 150 140 155 135" stroke="#8B4513" stroke-width="2" fill="none" class="mouth" />
            <line x1="150" y1="132" x2="150" y2="138" stroke="#8B4513" stroke-width="1" />
          </g>
          
          <!-- 狗：黑色大鼻 + 张开嘴 -->
          <g v-if="petData.petType === 'DOG'">
            <ellipse cx="150" cy="130" rx="6" ry="4" fill="#2F2F2F" class="nose" />
            <path d="M 140 138 Q 150 148 160 138" stroke="#8B4513" stroke-width="2" fill="none" class="mouth" />
            <ellipse v-if="showTongue" cx="150" cy="145" rx="8" ry="4" fill="#FF69B4" opacity="0.8" class="tongue" />
          </g>
          
          <!-- 兔子：Y形鼻 + 小嘴 -->
          <g v-if="petData.petType === 'RABBIT'">
            <path d="M 150 125 L 145 130 M 150 125 L 155 130 M 150 125 L 150 132" 
                  stroke="#FFB6C1" stroke-width="2" fill="none" class="nose" />
            <ellipse cx="150" cy="138" rx="3" ry="2" :fill="petData.noseColor || '#FFB6C1'" class="mouth" />
          </g>
          
          <!-- 仓鼠：粉色小鼻 + 小嘴 -->
          <g v-if="petData.petType === 'HAMSTER'">
            <circle cx="150" cy="128" r="3" :fill="petData.noseColor || '#FFB6C1'" class="nose" />
            <path d="M 147 135 Q 150 138 153 135" stroke="#8B4513" stroke-width="1.5" fill="none" class="mouth" />
          </g>
          
          <!-- 熊猫：黑色大鼻 + 微笑 -->
          <g v-if="petData.petType === 'PANDA'">
            <ellipse cx="150" cy="130" rx="5" ry="4" fill="#2F2F2F" class="nose" />
            <path d="M 142 138 Q 150 145 158 138" stroke="#8B4513" stroke-width="2" fill="none" class="mouth" />
          </g>
        </g>
        
        <!-- 四肢 - 分开布局，更自然 -->
        
        <!-- 前左肢 -->
        <g class="front-left-limb draggable-part" 
           :class="{ highlighted: selectedPart === 'front-left-limb' }"
           data-part="front-left-limb"
           style="cursor: pointer;">
          <ellipse 
            :cx="frontLeftLimbX" 
            :cy="frontLeftLimbY"
            :rx="adjustments.limbWidth" 
            :ry="adjustments.limbLength"
            :fill="darkenColor(petData.primaryColor, 5)"
            class="limb front-left"
            style="animation-delay: 0s;"
          />
          <ellipse 
            :cx="frontLeftLimbX" 
            :cy="frontLeftLimbY + adjustments.limbLength + 5" 
            rx="8" ry="5" 
            :fill="lightenColor(petData.primaryColor, 20)" 
            class="paw front-left" 
          />
        </g>
        
        <!-- 前右肢 -->
        <g class="front-right-limb draggable-part" 
           :class="{ highlighted: selectedPart === 'front-right-limb' }"
           data-part="front-right-limb"
           style="cursor: pointer;">
          <ellipse 
            :cx="frontRightLimbX" 
            :cy="frontRightLimbY"
            :rx="adjustments.limbWidth" 
            :ry="adjustments.limbLength"
            :fill="darkenColor(petData.primaryColor, 5)"
            class="limb front-right"
            style="animation-delay: 0.5s;"
          />
          <ellipse 
            :cx="frontRightLimbX" 
            :cy="frontRightLimbY + adjustments.limbLength + 5" 
            rx="8" ry="5" 
            :fill="lightenColor(petData.primaryColor, 20)" 
            class="paw front-right" 
          />
        </g>
        
        <!-- 后左肢 -->
        <g class="back-left-limb draggable-part" 
           :class="{ highlighted: selectedPart === 'back-left-limb' }"
           data-part="back-left-limb"
           style="cursor: pointer;">
          <ellipse 
            :cx="backLeftLimbX" 
            :cy="backLeftLimbY"
            :rx="adjustments.limbWidth - 1" 
            :ry="adjustments.limbLength - 2"
            :fill="darkenColor(petData.primaryColor, 10)"
            class="limb back-left"
            style="animation-delay: 0.25s;"
          />
          <ellipse 
            :cx="backLeftLimbX" 
            :cy="backLeftLimbY + adjustments.limbLength" 
            rx="7" ry="4" 
            :fill="lightenColor(petData.primaryColor, 20)" 
            class="paw back-left" 
          />
        </g>
        
        <!-- 后右肢 -->
        <g class="back-right-limb draggable-part" 
           :class="{ highlighted: selectedPart === 'back-right-limb' }"
           data-part="back-right-limb"
           style="cursor: pointer;">
          <ellipse 
            :cx="backRightLimbX" 
            :cy="backRightLimbY"
            :rx="adjustments.limbWidth - 1" 
            :ry="adjustments.limbLength - 2"
            :fill="darkenColor(petData.primaryColor, 10)"
            class="limb back-right"
            style="animation-delay: 0.75s;"
          />
          <ellipse 
            :cx="backRightLimbX" 
            :cy="backRightLimbY + adjustments.limbLength" 
            rx="7" ry="4" 
            :fill="lightenColor(petData.primaryColor, 20)" 
            class="paw back-right" 
          />
        </g>
        
        <!-- 尾巴 - 可拖拽，每种动物差异巨大 -->
        <g v-if="showTail" class="tail draggable-part" 
           :class="{ highlighted: selectedPart === 'tail' }"
           data-part="tail"
           style="cursor: pointer;">
          
          <!-- 猫：长弯尾巴 -->
          <g v-if="petData.petType === 'CAT'">
            <path :d="catTailPath" :stroke="petData.primaryColor" :stroke-width="adjustments.tailThickness" 
                  fill="none" stroke-linecap="round" class="tail-main" />
          </g>
          
          <!-- 狗：摆动的尾巴 -->
          <g v-if="petData.petType === 'DOG'">
            <ellipse cx="200" cy="210" :rx="adjustments.tailThickness" :ry="adjustments.tailLength" 
                     :fill="petData.primaryColor" transform="rotate(45 200 210)" class="tail-main" />
          </g>
          
          <!-- 兔子：小圆尾巴 -->
          <g v-if="petData.petType === 'RABBIT'">
            <circle cx="190" cy="220" :r="adjustments.tailThickness" :fill="lightenColor(petData.primaryColor, 25)" class="tail-main" />
          </g>
          
          <!-- 熊猫：短小尾巴 -->
          <g v-if="petData.petType === 'PANDA'">
            <ellipse cx="185" cy="225" :rx="adjustments.tailThickness * 0.8" :ry="adjustments.tailLength * 0.6" 
                     :fill="darkenColor(petData.primaryColor, 20)" class="tail-main" />
          </g>
        </g>
        
        <!-- 装饰配件 -->
        <g class="accessories">
          <!-- 帽子等装饰... -->
        </g>
      </g>
      
      <!-- 拖拽指示器 -->
      <g v-if="selectedPart" class="drag-indicator">
        <text x="20" y="30" fill="#FFD700" font-size="14" font-weight="bold">
          正在调整: {{ getPartName(selectedPart) }}
        </text>
        <text x="20" y="50" fill="#FFF" font-size="12">
          拖拽鼠标调整大小
        </text>
      </g>
      
      <!-- 控制点 -->
      <g v-if="selectedPart" class="control-points">
        <circle :cx="controlPoint.x" :cy="controlPoint.y" r="6" fill="#FFD700" stroke="#FFF" stroke-width="2" 
                class="control-point" style="cursor: grab;" />
      </g>
    </svg>
    
    <!-- 调整面板 -->
    <div v-if="selectedPart" class="adjustment-panel">
      <h4>{{ getPartName(selectedPart) }} 调整</h4>
      <div class="adjustment-controls">
        <div v-if="selectedPart === 'head'" class="control-group">
          <label>头部大小</label>
          <input type="range" v-model="adjustments.headSize" min="20" max="50" step="1" />
          <span>{{ adjustments.headSize }}</span>
        </div>
        
        <div v-if="selectedPart === 'body'" class="control-group">
          <label>身体宽度</label>
          <input type="range" v-model="adjustments.bodyWidth" min="25" max="60" step="1" />
          <span>{{ adjustments.bodyWidth }}</span>
        </div>
        
        <div v-if="selectedPart === 'body'" class="control-group">
          <label>身体高度</label>
          <input type="range" v-model="adjustments.bodyHeight" min="15" max="40" step="1" />
          <span>{{ adjustments.bodyHeight }}</span>
        </div>
        
        <div v-if="selectedPart === 'ears'" class="control-group">
          <label>耳朵大小</label>
          <input type="range" v-model="adjustments.earSize" min="8" max="25" step="1" />
          <span>{{ adjustments.earSize }}</span>
        </div>
        
        <div v-if="selectedPart === 'eyes'" class="control-group">
          <label>眼睛大小</label>
          <input type="range" v-model="adjustments.eyeSize" min="6" max="20" step="1" />
          <span>{{ adjustments.eyeSize }}</span>
        </div>
        
        <div v-if="selectedPart?.includes('limb')" class="control-group">
          <label>四肢粗细</label>
          <input type="range" v-model="adjustments.limbWidth" min="8" max="20" step="1" />
          <span>{{ adjustments.limbWidth }}</span>
        </div>
        
        <div v-if="selectedPart?.includes('limb')" class="control-group">
          <label>四肢长度</label>
          <input type="range" v-model="adjustments.limbLength" min="12" max="30" step="1" />
          <span>{{ adjustments.limbLength }}</span>
        </div>
        
        <div v-if="selectedPart === 'tail'" class="control-group">
          <label>尾巴粗细</label>
          <input type="range" v-model="adjustments.tailThickness" min="5" max="20" step="1" />
          <span>{{ adjustments.tailThickness }}</span>
        </div>
        
        <div v-if="selectedPart === 'tail'" class="control-group">
          <label>尾巴长度</label>
          <input type="range" v-model="adjustments.tailLength" min="15" max="40" step="1" />
          <span>{{ adjustments.tailLength }}</span>
        </div>
      </div>
      
      <div class="panel-actions">
        <button @click="resetPart" class="reset-btn">重置</button>
        <button @click="selectedPart = null" class="close-btn">完成</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'

interface PetData {
  petType: string
  primaryColor: string
  secondaryColor: string
  eyeColorLeft?: string
  eyeColorRight?: string
  noseColor?: string
  expression: string
  size: number
  rotation: number
  hasGlow?: boolean
  glowColor?: string
  hasSparkles?: boolean
}

interface Props {
  petData: PetData
  size?: number
  animated?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  size: 300,
  animated: true
})

const emit = defineEmits<{
  'adjustment-change': [adjustments: any]
}>()

// 交互状态
const selectedPart = ref<string | null>(null)
const isDragging = ref(false)
const dragStart = ref({ x: 0, y: 0 })

// 身体部位调整参数 - 优化比例
const adjustments = ref({
  headSize: 28,        // 缩小头部
  bodyWidth: 45,       // 增加身体宽度  
  bodyHeight: 35,      // 增加身体高度，更立体
  bellyWidth: 30,      // 相应调整肚子
  bellyHeight: 25,
  earSize: 14,         // 稍微增大耳朵
  eyeSize: 10,         // 稍微缩小眼睛
  limbWidth: 16,       // 增粗四肢
  limbLength: 22,      // 增长四肢
  tailThickness: 12,   // 增粗尾巴
  tailLength: 28
})

// 计算属性
const containerStyle = computed(() => ({
  transform: `scale(${props.petData.size}) rotate(${props.petData.rotation}deg)`,
  transition: 'all 0.4s cubic-bezier(0.4, 0.0, 0.2, 1)'
}))

const mainTransform = computed(() => {
  const scale = props.petData.size || 1
  return `scale(${scale})`
})

const showTail = computed(() => {
  return props.petData.petType !== 'HAMSTER'
})

const showTongue = computed(() => {
  return props.petData.expression === 'excited' || props.petData.expression === 'playful'
})

const glowRadius = computed(() => 100 * (props.petData.size || 1))

// 四肢位置计算 - 更自然协调的分布
const frontLeftLimbX = computed(() => 135) // 稍微内收
const frontLeftLimbY = computed(() => 190) // 向下移动

const frontRightLimbX = computed(() => 165) // 稍微内收  
const frontRightLimbY = computed(() => 190) // 向下移动

const backLeftLimbX = computed(() => 130) // 后肢稍微外展
const backLeftLimbY = computed(() => 215) // 更靠后

const backRightLimbX = computed(() => 170) // 后肢稍微外展
const backRightLimbY = computed(() => 215) // 更靠后

// 猫耳朵点坐标
const catEarPoints = computed(() => ({
  left: `125,85 135,65 145,85`,
  right: `155,85 165,65 175,85`
}))

const catEarInnerPoints = computed(() => ({
  left: `130,82 135,72 140,82`,
  right: `160,82 165,72 170,82`
}))

// 猫尾巴路径
const catTailPath = computed(() => {
  return `M 190 220 Q 220 200 240 180 Q 250 160 245 140`
})

// 控制点位置
const controlPoint = computed(() => {
  const positions = {
    head: { x: 150, y: 90 },
    body: { x: 150, y: 180 },
    ears: { x: 150, y: 70 },
    eyes: { x: 150, y: 105 },
    'front-left-limb': { x: frontLeftLimbX.value, y: frontLeftLimbY.value - 10 },
    'front-right-limb': { x: frontRightLimbX.value, y: frontRightLimbY.value - 10 },
    'back-left-limb': { x: backLeftLimbX.value, y: backLeftLimbY.value - 10 },
    'back-right-limb': { x: backRightLimbX.value, y: backRightLimbY.value - 10 },
    tail: { x: 200, y: 200 }
  }
  return positions[selectedPart.value as keyof typeof positions] || { x: 0, y: 0 }
})

// 方法
const handleMouseDown = (event: MouseEvent) => {
  const target = event.target as SVGElement
  const part = target.getAttribute('data-part') || target.closest('[data-part]')?.getAttribute('data-part')
  
  if (part) {
    selectedPart.value = part
    isDragging.value = true
    dragStart.value = { x: event.clientX, y: event.clientY }
    event.preventDefault()
  }
}

const handleMouseMove = (event: MouseEvent) => {
  if (!isDragging.value || !selectedPart.value) return
  
  const deltaX = event.clientX - dragStart.value.x
  const deltaY = event.clientY - dragStart.value.y
  const sensitivity = 0.1
  
  // 根据拖拽方向调整对应参数
  switch (selectedPart.value) {
    case 'head':
      adjustments.value.headSize = Math.max(20, Math.min(50, adjustments.value.headSize + deltaX * sensitivity))
      break
    case 'body':
      adjustments.value.bodyWidth = Math.max(25, Math.min(60, adjustments.value.bodyWidth + deltaX * sensitivity))
      adjustments.value.bodyHeight = Math.max(15, Math.min(40, adjustments.value.bodyHeight - deltaY * sensitivity))
      break
    case 'ears':
      adjustments.value.earSize = Math.max(8, Math.min(25, adjustments.value.earSize + deltaX * sensitivity))
      break
    case 'eyes':
      adjustments.value.eyeSize = Math.max(6, Math.min(20, adjustments.value.eyeSize + deltaX * sensitivity))
      break
    case 'front-left-limb':
    case 'front-right-limb':
    case 'back-left-limb':
    case 'back-right-limb':
      adjustments.value.limbWidth = Math.max(8, Math.min(20, adjustments.value.limbWidth + deltaX * sensitivity))
      adjustments.value.limbLength = Math.max(12, Math.min(30, adjustments.value.limbLength - deltaY * sensitivity))
      break
    case 'tail':
      adjustments.value.tailThickness = Math.max(5, Math.min(20, adjustments.value.tailThickness + deltaX * sensitivity))
      adjustments.value.tailLength = Math.max(15, Math.min(40, adjustments.value.tailLength - deltaY * sensitivity))
      break
  }
  
  dragStart.value = { x: event.clientX, y: event.clientY }
}

const handleMouseUp = () => {
  isDragging.value = false
}

const getPartName = (part: string) => {
  const names = {
    head: '头部',
    body: '身体',
    ears: '耳朵',
    eyes: '眼睛',
    'front-left-limb': '左前肢',
    'front-right-limb': '右前肢',
    'back-left-limb': '左后肢',
    'back-right-limb': '右后肢',
    tail: '尾巴'
  }
  return names[part as keyof typeof names] || part
}

const resetPart = () => {
  if (!selectedPart.value) return
  
  const defaults = {
    headSize: 32,
    bodyWidth: 40,
    bodyHeight: 25,
    earSize: 12,
    eyeSize: 12,
    limbWidth: 12,
    limbLength: 18,
    tailThickness: 10,
    tailLength: 25
  }
  
  switch (selectedPart.value) {
    case 'head':
      adjustments.value.headSize = defaults.headSize
      break
    case 'body':
      adjustments.value.bodyWidth = defaults.bodyWidth
      adjustments.value.bodyHeight = defaults.bodyHeight
      break
    case 'ears':
      adjustments.value.earSize = defaults.earSize
      break
    case 'eyes':
      adjustments.value.eyeSize = defaults.eyeSize
      break
    case 'front-left-limb':
    case 'front-right-limb':
    case 'back-left-limb':
    case 'back-right-limb':
      adjustments.value.limbWidth = defaults.limbWidth
      adjustments.value.limbLength = defaults.limbLength
      break
    case 'tail':
      adjustments.value.tailThickness = defaults.tailThickness
      adjustments.value.tailLength = defaults.tailLength
      break
  }
}

// 颜色工具函数
const lightenColor = (color: string, percent: number) => {
  const num = parseInt(color.replace("#", ""), 16)
  const amt = Math.round(2.55 * percent)
  const R = (num >> 16) + amt
  const G = (num >> 8 & 0x00FF) + amt
  const B = (num & 0x0000FF) + amt
  return "#" + (0x1000000 + (R < 255 ? R < 1 ? 0 : R : 255) * 0x10000 +
    (G < 255 ? G < 1 ? 0 : G : 255) * 0x100 +
    (B < 255 ? B < 1 ? 0 : B : 255)).toString(16).slice(1)
}

const darkenColor = (color: string, percent: number) => {
  const num = parseInt(color.replace("#", ""), 16)
  const amt = Math.round(2.55 * percent)
  const R = (num >> 16) - amt
  const G = (num >> 8 & 0x00FF) - amt
  const B = (num & 0x0000FF) - amt
  return "#" + (0x1000000 + (R > 255 ? 255 : R < 0 ? 0 : R) * 0x10000 +
    (G > 255 ? 255 : G < 0 ? 0 : G) * 0x100 +
    (B > 255 ? 255 : B < 0 ? 0 : B)).toString(16).slice(1)
}

// 监听调整变化
watch(adjustments, (newAdjustments) => {
  // 更新肚子大小
  adjustments.value.bellyWidth = newAdjustments.bodyWidth * 0.6
  adjustments.value.bellyHeight = newAdjustments.bodyHeight * 0.7
  
  emit('adjustment-change', newAdjustments)
}, { deep: true })
</script>

<style scoped>
.interactive-ghibli-pet-renderer {
  @apply relative;
}

.pet-svg {
  @apply drop-shadow-lg;
  max-width: 100%;
  height: auto;
  user-select: none;
}

.draggable-part {
  transition: all 0.2s ease;
}

.draggable-part:hover {
  filter: url(#highlight);
}

.draggable-part.highlighted {
  filter: url(#highlight);
  stroke: #FFD700;
  stroke-width: 2;
}

.control-point {
  animation: pulse 2s ease-in-out infinite;
}

.drag-indicator {
  pointer-events: none;
}

.adjustment-panel {
  @apply absolute top-4 right-4 bg-black/80 backdrop-blur-sm rounded-lg p-4 text-white min-w-64;
}

.adjustment-panel h4 {
  @apply text-lg font-semibold mb-3 text-yellow-400;
}

.adjustment-controls {
  @apply space-y-3;
}

.control-group {
  @apply flex items-center space-x-3;
}

.control-group label {
  @apply text-sm font-medium min-w-20;
}

.control-group input[type="range"] {
  @apply flex-1 h-2 bg-gray-600 rounded-lg appearance-none cursor-pointer;
}

.control-group input[type="range"]::-webkit-slider-thumb {
  @apply appearance-none w-4 h-4 bg-yellow-500 rounded-full cursor-pointer;
}

.control-group span {
  @apply text-sm font-mono bg-gray-700 px-2 py-1 rounded min-w-12 text-center;
}

.panel-actions {
  @apply flex space-x-2 mt-4 pt-3 border-t border-gray-600;
}

.reset-btn, .close-btn {
  @apply flex-1 py-2 rounded text-sm font-medium transition-colors;
}

.reset-btn {
  @apply bg-red-600 hover:bg-red-500 text-white;
}

.close-btn {
  @apply bg-green-600 hover:bg-green-500 text-white;
}

@keyframes pulse {
  0%, 100% { opacity: 0.8; transform: scale(1); }
  50% { opacity: 1; transform: scale(1.2); }
}

/* 动画效果 */
.warm-glow {
  animation: warmPulse 3s ease-in-out infinite alternate;
}

@keyframes warmPulse {
  0% { opacity: 0.15; transform: scale(0.95); }
  100% { opacity: 0.25; transform: scale(1.05); }
}

/* 四肢独立动画 */
.front-left-limb .limb, .front-right-limb .limb, 
.back-left-limb .limb, .back-right-limb .limb {
  animation: gentleSway 4s ease-in-out infinite;
}

.front-left-limb .paw, .front-right-limb .paw, 
.back-left-limb .paw, .back-right-limb .paw {
  animation: gentleSway 4s ease-in-out infinite;
}

.front-left-limb, .front-right-limb, .back-left-limb, .back-right-limb {
  transition: all 0.2s ease;
}

.front-left-limb:hover, .front-right-limb:hover, 
.back-left-limb:hover, .back-right-limb:hover {
  filter: url(#highlight);
}

/* 高光效果 */
.head-highlight, .body-highlight {
  animation: shimmer 3s ease-in-out infinite;
}

.eye-highlight, .eye-highlight-small {
  animation: eyeTwinkle 4s ease-in-out infinite;
}

@keyframes shimmer {
  0%, 100% { opacity: 0.3; }
  50% { opacity: 0.6; }
}

@keyframes eyeTwinkle {
  0%, 90%, 100% { opacity: 1; }
  95% { opacity: 0.3; }
}
</style>
