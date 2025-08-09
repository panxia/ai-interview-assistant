<template>
  <div class="pet-renderer" :class="{ 'interactive': interactive }">
    <svg 
      :width="size" 
      :height="size" 
      viewBox="0 0 200 200" 
      @click="handleClick"
      @touchstart="handleTouchStart"
      @touchend="handleTouchEnd"
      class="pet-svg"
      :class="animationClass"
    >
      <!-- 滤镜定义 -->
      <defs>
        <filter id="dropshadow">
          <feDropShadow dx="2" dy="4" stdDeviation="3" flood-opacity="0.3"/>
        </filter>
        <filter id="glow" v-if="appearance.hasGlow">
          <feGaussianBlur stdDeviation="3" result="coloredBlur"/>
          <feMerge> 
            <feMergeNode in="coloredBlur"/>
            <feMergeNode in="SourceGraphic"/>
          </feMerge>
        </filter>
        
        <!-- 图案定义 -->
        <pattern id="stripePattern" patternUnits="userSpaceOnUse" width="15" height="15">
          <rect width="8" height="15" :fill="appearance.patternColor" opacity="0.6"/>
        </pattern>
        
        <radialGradient id="spotPattern" cx="50%" cy="50%" r="50%">
          <stop offset="0%" :stop-color="appearance.patternColor" stop-opacity="0.8"/>
          <stop offset="100%" :stop-color="appearance.patternColor" stop-opacity="0"/>
        </radialGradient>
      </defs>

      <!-- 身体 -->
      <g class="pet-body">
        <!-- 根据头部形状绘制身体 -->
        <ellipse 
          v-if="appearance.headShape === 'round' || appearance.headShape === 'oval'"
          cx="100" 
          cy="130" 
          :rx="bodySize.width * bodyProportion.fatness" 
          :ry="bodySize.height * bodyProportion.height"
          :fill="appearance.primaryColor"
          :stroke="appearance.secondaryColor"
          stroke-width="2"
          filter="url(#dropshadow)"
        />
        
        <rect 
          v-else-if="appearance.headShape === 'square'"
          :x="100 - (bodySize.width * bodyProportion.fatness)"
          :y="130 - (bodySize.height * bodyProportion.height)"
          :width="(bodySize.width * bodyProportion.fatness) * 2"
          :height="(bodySize.height * bodyProportion.height) * 2"
          :fill="appearance.primaryColor"
          :stroke="appearance.secondaryColor"
          stroke-width="2"
          rx="8"
          filter="url(#dropshadow)"
        />
        
        <!-- 心形身体 -->
        <path 
          v-else-if="appearance.headShape === 'heart'"
          :d="heartBodyPath"
          :fill="appearance.primaryColor"
          :stroke="appearance.secondaryColor"
          stroke-width="2"
          filter="url(#dropshadow)"
        />
      </g>

      <!-- 身体图案 -->
      <g v-if="appearance.pattern !== 'none'" class="pet-patterns">
        <!-- 条纹图案 -->
        <g v-if="appearance.pattern === 'stripes'">
          <ellipse 
            cx="100" 
            cy="130" 
            :rx="bodySize.width * bodyProportion.fatness" 
            :ry="bodySize.height * bodyProportion.height"
            fill="url(#stripePattern)"
          />
        </g>
        
        <!-- 斑点图案 -->
        <g v-if="appearance.pattern === 'spots'">
          <circle v-for="spot in patternSpots" :key="spot.id"
            :cx="spot.x"
            :cy="spot.y"
            :r="spot.size"
            :fill="appearance.patternColor"
            opacity="0.7"
          />
        </g>

        <!-- 心形图案 -->
        <g v-if="appearance.pattern === 'hearts'">
          <g v-for="heart in patternHearts" :key="heart.id">
            <path 
              :d="getHeartPath(heart.x, heart.y, heart.size)"
              :fill="appearance.patternColor"
              opacity="0.8"
            />
          </g>
        </g>
        
        <!-- 星星图案 -->
        <g v-if="appearance.pattern === 'stars'">
          <g v-for="star in patternStars" :key="star.id">
            <path 
              :d="getStarPath(star.x, star.y, star.size)"
              :fill="appearance.patternColor"
              opacity="0.8"
            />
          </g>
        </g>
      </g>

      <!-- 头部 -->
      <g class="pet-head">
        <!-- 圆形头部 -->
        <circle 
          v-if="appearance.headShape === 'round'"
          cx="100" 
          cy="80" 
          :r="headSize * bodyProportion.headSize"
          :fill="appearance.primaryColor"
          :stroke="appearance.secondaryColor"
          stroke-width="2"
          :filter="appearance.hasGlow ? 'url(#glow)' : 'url(#dropshadow)'"
        />
        
        <!-- 椭圆形头部 -->
        <ellipse 
          v-else-if="appearance.headShape === 'oval'"
          cx="100" 
          cy="80" 
          :rx="headSize * bodyProportion.headSize * 0.8"
          :ry="headSize * bodyProportion.headSize"
          :fill="appearance.primaryColor"
          :stroke="appearance.secondaryColor"
          stroke-width="2"
          :filter="appearance.hasGlow ? 'url(#glow)' : 'url(#dropshadow)'"
        />
        
        <!-- 方形头部 -->
        <rect 
          v-else-if="appearance.headShape === 'square'"
          :x="100 - (headSize * bodyProportion.headSize)"
          :y="80 - (headSize * bodyProportion.headSize)"
          :width="(headSize * bodyProportion.headSize) * 2"
          :height="(headSize * bodyProportion.headSize) * 2"
          :fill="appearance.primaryColor"
          :stroke="appearance.secondaryColor"
          stroke-width="2"
          rx="5"
          :filter="appearance.hasGlow ? 'url(#glow)' : 'url(#dropshadow)'"
        />
        
        <!-- 心形头部 -->
        <path 
          v-else-if="appearance.headShape === 'heart'"
          :d="heartHeadPath"
          :fill="appearance.primaryColor"
          :stroke="appearance.secondaryColor"
          stroke-width="2"
          :filter="appearance.hasGlow ? 'url(#glow)' : 'url(#dropshadow)'"
        />
      </g>

      <!-- 耳朵 -->
      <g class="pet-ears" v-if="appearance.earStyle !== 'none'">
        <!-- 尖耳 -->
        <g v-if="appearance.earStyle === 'pointed'">
          <polygon 
            points="75,65 85,45 95,65"
            :fill="appearance.primaryColor"
            :stroke="appearance.secondaryColor"
            stroke-width="1"
          />
          <polygon 
            points="77,63 85,50 93,63"
            :fill="appearance.secondaryColor"
          />
          <polygon 
            points="105,65 115,45 125,65"
            :fill="appearance.primaryColor"
            :stroke="appearance.secondaryColor"
            stroke-width="1"
          />
          <polygon 
            points="107,63 115,50 123,63"
            :fill="appearance.secondaryColor"
          />
        </g>
        
        <!-- 圆耳 -->
        <g v-if="appearance.earStyle === 'round'">
          <circle cx="85" cy="55" r="12" :fill="appearance.primaryColor" stroke-width="1"/>
          <circle cx="85" cy="55" r="8" :fill="appearance.secondaryColor"/>
          <circle cx="115" cy="55" r="12" :fill="appearance.primaryColor" stroke-width="1"/>
          <circle cx="115" cy="55" r="8" :fill="appearance.secondaryColor"/>
        </g>

        <!-- 垂耳 -->
        <g v-if="appearance.earStyle === 'droopy'">
          <ellipse cx="80" cy="70" rx="8" ry="15" :fill="appearance.primaryColor"/>
          <ellipse cx="120" cy="70" rx="8" ry="15" :fill="appearance.primaryColor"/>
        </g>
        
        <!-- 兔耳 -->
        <g v-if="appearance.earStyle === 'bunny'">
          <ellipse cx="85" cy="50" rx="6" ry="20" :fill="appearance.primaryColor"/>
          <ellipse cx="85" cy="50" rx="3" ry="15" :fill="appearance.secondaryColor"/>
          <ellipse cx="115" cy="50" rx="6" ry="20" :fill="appearance.primaryColor"/>
          <ellipse cx="115" cy="50" rx="3" ry="15" :fill="appearance.secondaryColor"/>
        </g>
      </g>

      <!-- 眼睛 -->
      <g class="pet-eyes">
        <!-- 左眼 -->
        <g>
          <!-- 普通眼 -->
          <ellipse 
            v-if="appearance.eyeType === 'normal'"
            cx="90" 
            cy="75" 
            rx="6" 
            ry="6"
            fill="white"
            stroke="#333"
            stroke-width="1"
          />
          
          <!-- 大眼 -->
          <ellipse 
            v-else-if="appearance.eyeType === 'big'"
            cx="90" 
            cy="75" 
            rx="8" 
            ry="8"
            fill="white"
            stroke="#333"
            stroke-width="1"
          />
          
          <!-- 小眼 -->
          <ellipse 
            v-else-if="appearance.eyeType === 'small'"
            cx="90" 
            cy="75" 
            rx="4" 
            ry="4"
            fill="white"
            stroke="#333"
            stroke-width="1"
          />
          
          <!-- 眯眯眼 -->
          <path 
            v-else-if="appearance.eyeType === 'sleepy'"
            d="M 83,75 Q 90,72 97,75"
            stroke="#333" 
            stroke-width="2" 
            fill="none"
          />
          
          <!-- 星星眼 -->
          <g v-else-if="appearance.eyeType === 'star'">
            <ellipse cx="90" cy="75" rx="6" ry="6" fill="white" stroke="#333" stroke-width="1"/>
            <path :d="getStarPath(90, 75, 4)" fill="#FFD700"/>
          </g>
          
          <!-- 爱心眼 -->
          <g v-else-if="appearance.eyeType === 'heart'">
            <ellipse cx="90" cy="75" rx="6" ry="6" fill="white" stroke="#333" stroke-width="1"/>
            <path :d="getHeartPath(90, 75, 3)" fill="#FF69B4"/>
          </g>
          
          <!-- 瞳孔（非眯眯眼时显示） -->
          <circle 
            v-if="appearance.eyeType !== 'sleepy'"
            :cx="90 + eyeOffset.x" 
            :cy="75 + eyeOffset.y" 
            :r="getEyeSize() - 2"
            :fill="appearance.eyeColorLeft"
          />
          <circle 
            v-if="appearance.eyeType !== 'sleepy'"
            :cx="90 + eyeOffset.x + 1" 
            :cy="75 + eyeOffset.y - 1" 
            r="2"
            fill="white"
            opacity="0.8"
          />
        </g>
        
        <!-- 右眼 -->
        <g>
          <!-- 普通眼 -->
          <ellipse 
            v-if="appearance.eyeType === 'normal'"
            cx="110" 
            cy="75" 
            rx="6" 
            ry="6"
            fill="white"
            stroke="#333"
            stroke-width="1"
          />
          
          <!-- 大眼 -->
          <ellipse 
            v-else-if="appearance.eyeType === 'big'"
            cx="110" 
            cy="75" 
            rx="8" 
            ry="8"
            fill="white"
            stroke="#333"
            stroke-width="1"
          />
          
          <!-- 小眼 -->
          <ellipse 
            v-else-if="appearance.eyeType === 'small'"
            cx="110" 
            cy="75" 
            rx="4" 
            ry="4"
            fill="white"
            stroke="#333"
            stroke-width="1"
          />
          
          <!-- 眯眯眼 -->
          <path 
            v-else-if="appearance.eyeType === 'sleepy'"
            d="M 103,75 Q 110,72 117,75"
            stroke="#333" 
            stroke-width="2" 
            fill="none"
          />
          
          <!-- 星星眼 -->
          <g v-else-if="appearance.eyeType === 'star'">
            <ellipse cx="110" cy="75" rx="6" ry="6" fill="white" stroke="#333" stroke-width="1"/>
            <path :d="getStarPath(110, 75, 4)" fill="#FFD700"/>
          </g>
          
          <!-- 爱心眼 -->
          <g v-else-if="appearance.eyeType === 'heart'">
            <ellipse cx="110" cy="75" rx="6" ry="6" fill="white" stroke="#333" stroke-width="1"/>
            <path :d="getHeartPath(110, 75, 3)" fill="#FF69B4"/>
          </g>
          
          <!-- 瞳孔（非眯眯眼时显示） -->
          <circle 
            v-if="appearance.eyeType !== 'sleepy'"
            :cx="110 + eyeOffset.x" 
            :cy="75 + eyeOffset.y" 
            :r="getEyeSize() - 2"
            :fill="appearance.eyeColorRight || appearance.eyeColorLeft"
          />
          <circle 
            v-if="appearance.eyeType !== 'sleepy'"
            :cx="110 + eyeOffset.x + 1" 
            :cy="75 + eyeOffset.y - 1" 
            r="2"
            fill="white"
            opacity="0.8"
          />
        </g>
      </g>

      <!-- 鼻子 -->
      <ellipse 
        cx="100" 
        cy="85" 
        rx="3" 
        ry="2"
        :fill="appearance.noseColor"
        class="pet-nose"
      />

      <!-- 嘴巴 -->
      <g class="pet-mouth">
        <!-- 微笑 -->
        <path 
          v-if="appearance.mouthExpression === 'smile' || currentMood === 'happy'" 
          d="M 92,90 Q 100,96 108,90" 
          stroke="#333" 
          stroke-width="2" 
          fill="none"
        />
        
        <!-- 噘嘴 -->
        <ellipse 
          v-else-if="appearance.mouthExpression === 'pout'"
          cx="100" 
          cy="90" 
          rx="3" 
          ry="2"
          fill="#FFB6C1"
          stroke="#333"
          stroke-width="1"
        />
        
        <!-- 张嘴 -->
        <ellipse 
          v-else-if="appearance.mouthExpression === 'open'"
          cx="100" 
          cy="90" 
          rx="4" 
          ry="6"
          fill="#333"
        />
        
        <!-- 闭嘴 -->
        <line 
          v-else-if="appearance.mouthExpression === 'closed'"
          x1="96" 
          y1="90" 
          x2="104" 
          y2="90"
          stroke="#333" 
          stroke-width="2"
        />
        
        <!-- 吐舌 -->
        <g v-else-if="appearance.mouthExpression === 'tongue'">
          <path d="M 96,90 Q 100,92 104,90" stroke="#333" stroke-width="1.5" fill="none"/>
          <ellipse cx="100" cy="94" rx="3" ry="2" fill="#FFB6C1"/>
        </g>
        
        <!-- 默认普通嘴巴 -->
        <path 
          v-else
          d="M 96,90 Q 100,92 104,90" 
          stroke="#333" 
          stroke-width="1.5" 
          fill="none"
        />
      </g>

      <!-- 四肢 -->
      <g class="pet-limbs">
        <!-- 前腿 -->
        <ellipse 
          cx="80" 
          cy="155" 
          :rx="8 * bodyProportion.limbLength" 
          :ry="20 * bodyProportion.limbLength" 
          :fill="appearance.primaryColor"
        />
        <ellipse 
          cx="120" 
          cy="155" 
          :rx="8 * bodyProportion.limbLength" 
          :ry="20 * bodyProportion.limbLength" 
          :fill="appearance.primaryColor"
        />
        
        <!-- 后腿 -->
        <ellipse 
          cx="85" 
          cy="165" 
          :rx="10 * bodyProportion.limbLength" 
          :ry="18 * bodyProportion.limbLength" 
          :fill="appearance.primaryColor"
        />
        <ellipse 
          cx="115" 
          cy="165" 
          :rx="10 * bodyProportion.limbLength" 
          :ry="18 * bodyProportion.limbLength" 
          :fill="appearance.primaryColor"
        />
        
        <!-- 爪子 -->
        <ellipse cx="80" cy="170" rx="6" ry="4" fill="#444"/>
        <ellipse cx="120" cy="170" rx="6" ry="4" fill="#444"/>
        <ellipse cx="85" cy="178" rx="7" ry="5" fill="#444"/>
        <ellipse cx="115" cy="178" rx="7" ry="5" fill="#444"/>
      </g>

      <!-- 尾巴 -->
      <g class="pet-tail" :class="{ 'wagging': isWagging }">
        <path 
          :d="getTailPath()"
          :stroke="appearance.primaryColor"
          :stroke-width="12 * bodyProportion.tailLength"
          fill="none"
          stroke-linecap="round"
        />
      </g>

      <!-- 装饰品 -->
      <g class="pet-accessories">
        <!-- 帽子 -->
        <g v-if="appearance.hat && appearance.hat !== 'none'">
          <!-- 棒球帽 -->
          <g v-if="appearance.hat === 'baseball_cap'">
            <ellipse cx="100" cy="50" rx="25" ry="8" fill="#FF4444"/>
            <circle cx="100" cy="45" r="18" fill="#FF4444"/>
          </g>
          
          <!-- 贝雷帽 -->
          <g v-else-if="appearance.hat === 'beret'">
            <circle cx="100" cy="50" r="20" fill="#8B4513"/>
            <circle cx="95" cy="45" r="3" fill="#444"/>
          </g>

          <!-- 王冠 -->
          <g v-else-if="appearance.hat === 'crown'">
            <path d="M 75,50 L 80,35 L 90,40 L 100,30 L 110,40 L 120,35 L 125,50 Z" 
                  fill="#FFD700" stroke="#FFA500" stroke-width="1"/>
            <circle cx="85" cy="42" r="2" fill="#FF0000"/>
            <circle cx="100" cy="37" r="3" fill="#00FF00"/>
            <circle cx="115" cy="42" r="2" fill="#0000FF"/>
          </g>
          
          <!-- 巫师帽 -->
          <g v-else-if="appearance.hat === 'wizard_hat'">
            <path d="M 85,50 L 90,25 L 110,25 L 115,50 Z" fill="#4B0082"/>
            <ellipse cx="100" cy="50" rx="20" ry="6" fill="#4B0082"/>
            <circle cx="100" cy="27" r="2" fill="#FFD700"/>
          </g>
          
          <!-- 圣诞帽 -->
          <g v-else-if="appearance.hat === 'santa_hat'">
            <path d="M 85,50 L 95,25 L 120,35 L 115,50 Z" fill="#DC143C"/>
            <circle cx="118" cy="32" r="4" fill="white"/>
            <ellipse cx="100" cy="50" rx="18" ry="4" fill="white"/>
          </g>
        </g>

        <!-- 项圈 -->
        <g v-if="appearance.collar && appearance.collar !== 'none'">
          <ellipse cx="100" cy="95" rx="18" ry="4" fill="#8B4513"/>
          
          <!-- 蝴蝶结 -->
          <g v-if="appearance.collar === 'bow_tie'">
            <path d="M 95,93 L 90,90 L 90,100 L 95,97 L 105,97 L 110,100 L 110,90 L 105,93 Z" 
                  fill="#FF69B4"/>
          </g>
          
          <!-- 铃铛 -->
          <g v-else-if="appearance.collar === 'bell_collar'">
            <circle cx="100" cy="100" r="4" fill="#FFD700"/>
            <path d="M 98,102 L 102,102" stroke="#333" stroke-width="1"/>
          </g>
          
          <!-- 花环 -->
          <g v-else-if="appearance.collar === 'flower_wreath'">
            <g v-for="flower in collarFlowers" :key="flower.id">
              <circle :cx="flower.x" :cy="flower.y" r="3" :fill="flower.color"/>
              <circle :cx="flower.x" :cy="flower.y" r="1" fill="#FFD700"/>
            </g>
          </g>
          
          <!-- 围巾 -->
          <g v-else-if="appearance.collar === 'scarf'">
            <path d="M 82,95 Q 100,90 118,95 Q 120,100 115,105 Q 100,100 85,105 Q 80,100 82,95" 
                  fill="#FF6347"/>
          </g>
        </g>

        <!-- 眼镜 -->
        <g v-if="appearance.glasses && appearance.glasses !== 'none'">
          <!-- 圆框眼镜 -->
          <g v-if="appearance.glasses === 'round_glasses'">
            <circle cx="90" cy="75" r="12" fill="none" stroke="#333" stroke-width="2"/>
            <circle cx="110" cy="75" r="12" fill="none" stroke="#333" stroke-width="2"/>
            <path d="M 102,75 L 98,75" stroke="#333" stroke-width="2"/>
          </g>
          
          <!-- 墨镜 -->
          <g v-else-if="appearance.glasses === 'sunglasses'">
            <ellipse cx="90" cy="75" rx="12" ry="8" fill="#333"/>
            <ellipse cx="110" cy="75" rx="12" ry="8" fill="#333"/>
            <path d="M 102,75 L 98,75" stroke="#333" stroke-width="2"/>
          </g>
          
          <!-- 单片镜 -->
          <g v-else-if="appearance.glasses === 'monocle'">
            <circle cx="90" cy="75" r="10" fill="none" stroke="#333" stroke-width="2"/>
            <path d="M 80,75 L 70,85" stroke="#333" stroke-width="2"/>
          </g>
          
          <!-- 星星眼镜 -->
          <g v-else-if="appearance.glasses === 'star_glasses'">
            <path :d="getStarPath(90, 75, 10)" fill="none" stroke="#333" stroke-width="2"/>
            <path :d="getStarPath(110, 75, 10)" fill="none" stroke="#333" stroke-width="2"/>
            <path d="M 102,75 L 98,75" stroke="#333" stroke-width="2"/>
          </g>
        </g>
      </g>

      <!-- 特效层 -->
      <g class="pet-effects">
        <!-- 发光效果 -->
        <g v-if="appearance.hasGlow">
          <circle 
            cx="100" 
            cy="100" 
            :r="80"
            :fill="appearance.glowColor || '#FFFF00'"
            opacity="0.1"
            class="glow-effect"
          />
        </g>
        
        <!-- 星星闪烁效果 -->
        <g v-if="appearance.hasSparkles || showSparkles">
          <g v-for="sparkle in sparkles" :key="sparkle.id" class="floating-sparkle">
            <path 
              :d="getStarPath(sparkle.x, sparkle.y, sparkle.size)"
              fill="#FFD700"
              :opacity="sparkle.opacity"
            />
          </g>
        </g>
        
        <!-- 爱心特效 -->
        <g v-if="showHearts">
          <g v-for="heart in hearts" :key="heart.id" class="floating-heart">
            <path 
              :d="getHeartPath(heart.x, heart.y, heart.size)"
              fill="#FF69B4"
              :opacity="heart.opacity"
              filter="url(#glow)"
            />
          </g>
        </g>

        <!-- 汗滴特效 -->
        <g v-if="showSweat">
          <ellipse cx="120" cy="60" rx="3" ry="6" fill="#87CEEB" opacity="0.8"/>
          <ellipse cx="122" cy="55" rx="2" ry="4" fill="#87CEEB" opacity="0.6"/>
        </g>
      </g>
    </svg>

    <!-- 交互提示 -->
    <div v-if="interactive && showHints" class="interaction-hints">
      <div class="hint-bubble">{{ currentHint }}</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'

// Props定义
interface PetAppearance {
  headShape: 'round' | 'oval' | 'square' | 'heart'
  earStyle: 'pointed' | 'round' | 'droopy' | 'none' | 'bunny'
  eyeType: 'normal' | 'big' | 'small' | 'sleepy' | 'star' | 'heart'
  mouthExpression: 'smile' | 'pout' | 'open' | 'closed' | 'tongue'
  primaryColor: string
  secondaryColor: string
  eyeColorLeft: string
  eyeColorRight?: string
  noseColor: string
  pattern: 'none' | 'stripes' | 'spots' | 'gradient' | 'hearts' | 'stars' | 'flowers'
  patternColor: string
  hat?: string
  collar?: string
  glasses?: string
  hasGlow?: boolean
  glowColor?: string
  hasSparkles?: boolean
  bodyProportion?: {
    fatness: number
    height: number
    headSize: number
    limbLength: number
    tailLength: number
  }
}

interface Props {
  appearance: PetAppearance
  size?: number
  interactive?: boolean
  mood?: 'happy' | 'normal' | 'sad' | 'angry' | 'sleepy'
  showEffects?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  size: 200,
  interactive: true,
  mood: 'normal',
  showEffects: true,
  appearance: () => ({
    headShape: 'round',
    earStyle: 'pointed',
    eyeType: 'normal',
    mouthExpression: 'smile',
    primaryColor: '#FFA500',
    secondaryColor: '#FFFFFF',
    eyeColorLeft: '#4169E1',
    noseColor: '#FFB6C1',
    pattern: 'none',
    patternColor: '#000000',
    bodyProportion: {
      fatness: 1.0,
      height: 1.0,
      headSize: 1.0,
      limbLength: 1.0,
      tailLength: 1.0
    }
  })
})

// Emits
const emits = defineEmits<{
  petClicked: [event: MouseEvent]
  petTouched: [area: string]
}>()

// 响应式数据
const animationClass = ref('')
const currentMood = ref(props.mood)
const eyeOffset = ref({ x: 0, y: 0 })
const isWagging = ref(false)
const showHearts = ref(false)
const showSparkles = ref(false)
const showSweat = ref(false)
const showHints = ref(false)
const currentHint = ref('')

// 特效数据
const hearts = ref<Array<{id: number, x: number, y: number, size: number, opacity: number}>>([])
const sparkles = ref<Array<{id: number, x: number, y: number, size: number, opacity: number}>>([])

// 计算属性
const bodySize = computed(() => ({ width: 35, height: 25 }))
const headSize = computed(() => 28)

const bodyProportion = computed(() => {
  return props.appearance.bodyProportion || {
    fatness: 1.0,
    height: 1.0,
    headSize: 1.0,
    limbLength: 1.0,
    tailLength: 1.0
  }
})

const patternSpots = computed(() => {
  if (props.appearance.pattern !== 'spots') return []
  return [
    { id: 1, x: 90, y: 125, size: 4 },
    { id: 2, x: 110, y: 135, size: 3 },
    { id: 3, x: 95, y: 145, size: 5 },
    { id: 4, x: 105, y: 120, size: 3 }
  ]
})

const patternHearts = computed(() => {
  if (props.appearance.pattern !== 'hearts') return []
  return [
    { id: 1, x: 85, y: 120, size: 5 },
    { id: 2, x: 115, y: 140, size: 4 },
    { id: 3, x: 100, y: 135, size: 6 }
  ]
})

const patternStars = computed(() => {
  if (props.appearance.pattern !== 'stars') return []
  return [
    { id: 1, x: 90, y: 125, size: 4 },
    { id: 2, x: 110, y: 135, size: 3 },
    { id: 3, x: 100, y: 145, size: 5 }
  ]
})

const collarFlowers = computed(() => {
  const flowers = []
  for (let i = 0; i < 8; i++) {
    const angle = (i / 8) * Math.PI * 2
    flowers.push({
      id: i,
      x: 100 + Math.cos(angle) * 18,
      y: 95 + Math.sin(angle) * 4,
      color: ['#FF69B4', '#FFD700', '#FF4444', '#44FF44'][i % 4]
    })
  }
  return flowers
})

const heartHeadPath = computed(() => {
  const size = headSize.value * bodyProportion.value.headSize
  const x = 100
  const y = 80
  return `M ${x},${y+size*0.3} C ${x-size*0.7},${y-size*0.2} ${x-size*0.3},${y-size*0.7} ${x},${y-size*0.3} C ${x+size*0.3},${y-size*0.7} ${x+size*0.7},${y-size*0.2} ${x},${y+size*0.3} Z`
})

const heartBodyPath = computed(() => {
  const w = bodySize.value.width * bodyProportion.value.fatness
  const h = bodySize.value.height * bodyProportion.value.height
  const x = 100
  const y = 130
  return `M ${x},${y+h} C ${x-w},${y-h*0.2} ${x-w*0.5},${y-h} ${x},${y-h*0.5} C ${x+w*0.5},${y-h} ${x+w},${y-h*0.2} ${x},${y+h} Z`
})

// 方法
const getEyeSize = () => {
  switch (props.appearance.eyeType) {
    case 'big': return 8
    case 'small': return 4
    default: return 6
  }
}

const getHeartPath = (x: number, y: number, size: number) => {
  return `M ${x},${y+size*0.3} C ${x-size*0.7},${y-size*0.2} ${x-size*0.3},${y-size*0.7} ${x},${y-size*0.3} C ${x+size*0.3},${y-size*0.7} ${x+size*0.7},${y-size*0.2} ${x},${y+size*0.3} Z`
}

const getStarPath = (x: number, y: number, size: number) => {
  const points = []
  for (let i = 0; i < 10; i++) {
    const angle = (i / 10) * Math.PI * 2 - Math.PI / 2
    const radius = i % 2 === 0 ? size : size * 0.4
    points.push(`${x + Math.cos(angle) * radius},${y + Math.sin(angle) * radius}`)
  }
  return `M ${points.join(' L ')} Z`
}

const getTailPath = () => {
  const length = bodyProportion.value.tailLength
  const baseX = 140
  const baseY = 140
  return `M ${baseX},${baseY} Q ${baseX + 15*length},${baseY - 10*length} ${baseX + 20*length},${baseY - 25*length} Q ${baseX + 25*length},${baseY - 40*length} ${baseX + 15*length},${baseY - 50*length}`
}

const handleClick = (event: MouseEvent) => {
  emits('petClicked', event)
  triggerHappyAnimation()
}

const handleTouchStart = (event: TouchEvent) => {
  const touch = event.touches[0]
  const rect = (event.target as SVGElement).getBoundingClientRect()
  const x = ((touch.clientX - rect.left) / rect.width) * 100
  const y = ((touch.clientY - rect.top) / rect.height) * 100
  
  const area = getTouchArea(x, y)
  emits('petTouched', area)
  
  triggerTouchAnimation(area)
}

const handleTouchEnd = () => {
  // 触摸结束处理
}

const getTouchArea = (x: number, y: number): string => {
  if (y < 40) return 'head'
  if (y > 80) return 'paws'
  if (x < 30) return 'left-side'
  if (x > 70) return 'right-side'
  return 'body'
}

const triggerHappyAnimation = () => {
  animationClass.value = 'bounce'
  currentMood.value = 'happy'
  isWagging.value = true
  
  if (props.showEffects) {
    showHearts.value = true
    generateHearts()
  }
  
  setTimeout(() => {
    animationClass.value = ''
    currentMood.value = props.mood
    isWagging.value = false
    showHearts.value = false
  }, 2000)
}

const triggerTouchAnimation = (area: string) => {
  switch (area) {
    case 'head':
      showSparkles.value = true
      generateSparkles()
      currentHint.value = '好舒服~'
      break
    case 'body':
      currentMood.value = 'happy'
      currentHint.value = '我喜欢被摸摸~'
      break
    case 'paws':
      animationClass.value = 'shake'
      currentHint.value = '不要摸我的爪子~'
      setTimeout(() => animationClass.value = '', 500)
      break
  }
  
  showHints.value = true
  setTimeout(() => {
    showHints.value = false
    showSparkles.value = false
  }, 1500)
}

const generateHearts = () => {
  hearts.value = []
  for (let i = 0; i < 5; i++) {
    hearts.value.push({
      id: i,
      x: 80 + Math.random() * 40,
      y: 60 + Math.random() * 20,
      size: 5 + Math.random() * 3,
      opacity: 0.8
    })
  }
  
  const heartInterval = setInterval(() => {
    hearts.value.forEach(heart => {
      heart.y -= 2
      heart.opacity -= 0.05
    })
    
    if (hearts.value.length > 0 && hearts.value[0].opacity <= 0) {
      clearInterval(heartInterval)
      hearts.value = []
    }
  }, 100)
}

const generateSparkles = () => {
  sparkles.value = []
  for (let i = 0; i < 8; i++) {
    sparkles.value.push({
      id: i,
      x: 85 + Math.random() * 30,
      y: 70 + Math.random() * 20,
      size: 3 + Math.random() * 2,
      opacity: 1
    })
  }
  
  const sparkleInterval = setInterval(() => {
    sparkles.value.forEach(sparkle => {
      sparkle.opacity -= 0.1
    })
    
    if (sparkles.value.length > 0 && sparkles.value[0].opacity <= 0) {
      clearInterval(sparkleInterval)
      sparkles.value = []
    }
  }, 100)
}

const startIdleAnimation = () => {
  const idleAnimations = () => {
    // 随机眨眼
    if (Math.random() < 0.3) {
      const originalEyeType = props.appearance.eyeType
      // 临时改变眼睛类型来模拟眨眼（需要父组件配合）
      setTimeout(() => {
        // 恢复原来的眼睛类型
      }, 200)
    }
    
    // 随机看向不同方向
    if (Math.random() < 0.4) {
      eyeOffset.value = {
        x: (Math.random() - 0.5) * 2,
        y: (Math.random() - 0.5) * 2
      }
      setTimeout(() => {
        eyeOffset.value = { x: 0, y: 0 }
      }, 1000)
    }
    
    // 随机摇尾巴
    if (Math.random() < 0.2) {
      isWagging.value = true
      setTimeout(() => {
        isWagging.value = false
      }, 1500)
    }
  }
  
  setInterval(idleAnimations, 3000)
}

// 监听props变化
watch(() => props.mood, (newMood) => {
  currentMood.value = newMood
})

// 生命周期
onMounted(() => {
  if (props.interactive) {
    startIdleAnimation()
  }
})
</script>

<style scoped>
.pet-renderer {
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
}

.pet-svg {
  transition: transform 0.3s ease;
  cursor: pointer;
}

.pet-svg.interactive:hover {
  transform: scale(1.05);
}

.pet-svg.bounce {
  animation: bounce 0.8s ease-in-out;
}

.pet-svg.shake {
  animation: shake 0.5s ease-in-out;
}

@keyframes bounce {
  0%, 20%, 60%, 100% {
    transform: translateY(0);
  }
  40% {
    transform: translateY(-10px);
  }
  80% {
    transform: translateY(-5px);
  }
}

@keyframes shake {
  0%, 100% { transform: translateX(0); }
  25% { transform: translateX(-5px); }
  75% { transform: translateX(5px); }
}

.pet-tail.wagging {
  animation: tailWag 0.8s ease-in-out infinite;
  transform-origin: 140px 140px;
}

@keyframes tailWag {
  0%, 100% { transform: rotate(0deg); }
  50% { transform: rotate(20deg); }
}

.floating-heart {
  animation: floatUp 2s ease-out forwards;
}

.floating-sparkle {
  animation: sparkle 1.5s ease-out forwards;
}

@keyframes floatUp {
  from {
    transform: translateY(0) scale(1);
    opacity: 0.8;
  }
  to {
    transform: translateY(-30px) scale(0.5);
    opacity: 0;
  }
}

@keyframes sparkle {
  0% {
    transform: scale(0) rotate(0deg);
    opacity: 1;
  }
  50% {
    transform: scale(1) rotate(180deg);
    opacity: 0.8;
  }
  100% {
    transform: scale(0) rotate(360deg);
    opacity: 0;
  }
}

.glow-effect {
  animation: glow 2s ease-in-out infinite alternate;
}

@keyframes glow {
  from {
    opacity: 0.1;
  }
  to {
    opacity: 0.3;
  }
}

.interaction-hints {
  position: absolute;
  top: -40px;
  left: 50%;
  transform: translateX(-50%);
}

.hint-bubble {
  background: rgba(255, 255, 255, 0.9);
  color: #333;
  padding: 8px 12px;
  border-radius: 15px;
  font-size: 14px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  white-space: nowrap;
  animation: fadeInOut 1.5s ease-in-out;
}

@keyframes fadeInOut {
  0%, 100% { opacity: 0; transform: translateY(10px); }
  20%, 80% { opacity: 1; transform: translateY(0); }
}

/* 移动端优化 */
@media (max-width: 768px) {
  .pet-svg {
    touch-action: none;
  }
  
  .hint-bubble {
    font-size: 12px;
    padding: 6px 10px;
  }
}
</style>