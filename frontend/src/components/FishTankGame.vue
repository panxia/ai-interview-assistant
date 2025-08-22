<template>
  <div class="fish-tank-game">
    <div class="game-controls">
      <button @click="clearCanvas" class="control-btn">清空画布</button>
      <button @click="addRandomFish" class="control-btn">添加随机鱼</button>
      <button @click="toggleAnimation" class="control-btn">
        {{ isAnimating ? '停止动画' : '开始动画' }}
      </button>
      <div class="color-picker">
        <label>鱼的颜色:</label>
        <input type="color" v-model="fishColor" />
      </div>
      <div class="size-slider">
        <label>鱼的大小: {{ fishSize }}</label>
        <input type="range" v-model="fishSize" min="10" max="50" />
      </div>
    </div>
    
    <div class="fish-tank-container">
      <canvas
        ref="canvas"
        @mousedown="startDrawing"
        @mousemove="draw"
        @mouseup="stopDrawing"
        @mouseleave="stopDrawing"
        class="fish-tank-canvas"
      ></canvas>
      
      <div class="fish-tank-decoration">
        <div class="bubble bubble-1"></div>
        <div class="bubble bubble-2"></div>
        <div class="bubble bubble-3"></div>
        <div class="bubble bubble-4"></div>
        <div class="bubble bubble-5"></div>
      </div>
    </div>
    
    <div class="instructions">
      <h3>游戏说明:</h3>
      <p>1. 在画布上按住鼠标左键画鱼</p>
      <p>2. 画完的鱼会自动在鱼缸里游泳</p>
      <p>3. 可以调整鱼的颜色和大小</p>
      <p>4. 点击"添加随机鱼"生成预设鱼</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'

// 画布相关
const canvas = ref<HTMLCanvasElement>()
const ctx = ref<CanvasRenderingContext2D>()
const isDrawing = ref(false)
const lastX = ref(0)
const lastY = ref(0)

// 游戏状态
const isAnimating = ref(true)
const fishColor = ref('#FF6B6B')
const fishSize = ref(20)

// 鱼的数据结构
interface Fish {
  id: number
  x: number
  y: number
  vx: number
  vy: number
  size: number
  color: string
  points: { x: number; y: number }[]
  angle: number
  tailAngle: number
  tailDirection: number
}

const fishes = ref<Fish[]>([])
let animationId: number
let fishIdCounter = 0

// 初始化画布
onMounted(() => {
  if (canvas.value) {
    ctx.value = canvas.value.getContext('2d')!
    canvas.value.width = 800
    canvas.value.height = 600
    drawFishTank()
    startAnimation()
  }
})

onUnmounted(() => {
  if (animationId) {
    cancelAnimationFrame(animationId)
  }
})

// 绘制鱼缸背景
const drawFishTank = () => {
  if (!ctx.value) return
  
  // 清空画布
  ctx.value.clearRect(0, 0, 800, 600)
  
  // 绘制鱼缸边框
  ctx.value.strokeStyle = '#4A90E2'
  ctx.value.lineWidth = 3
  ctx.value.strokeRect(50, 50, 700, 500)
  
  // 绘制鱼缸背景渐变
  const gradient = ctx.value.createLinearGradient(0, 0, 0, 600)
  gradient.addColorStop(0, '#E3F2FD')
  gradient.addColorStop(1, '#BBDEFB')
  ctx.value.fillStyle = gradient
  ctx.value.fillRect(50, 50, 700, 500)
  
  // 绘制水草
  drawSeaweed()
}

// 绘制水草
const drawSeaweed = () => {
  if (!ctx.value) return
  
  ctx.value.strokeStyle = '#4CAF50'
  ctx.value.lineWidth = 2
  
  for (let i = 0; i < 5; i++) {
    const x = 100 + i * 150
    ctx.value.beginPath()
    ctx.value.moveTo(x, 550)
    
    for (let j = 0; j < 8; j++) {
      const curveX = x + Math.sin(j * 0.5) * 20
      const curveY = 550 - j * 60
      ctx.value.quadraticCurveTo(curveX, curveY, curveX, curveY)
    }
    
    ctx.value.stroke()
  }
}

// 绘画相关方法
const startDrawing = (e: MouseEvent) => {
  isDrawing.value = true
  const rect = canvas.value!.getBoundingClientRect()
  lastX.value = e.clientX - rect.left
  lastY.value = e.clientY - rect.top
}

const draw = (e: MouseEvent) => {
  if (!isDrawing.value || !ctx.value) return
  
  const rect = canvas.value!.getBoundingClientRect()
  const currentX = e.clientX - rect.left
  const currentY = e.clientY - rect.top
  
  ctx.value.beginPath()
  ctx.value.moveTo(lastX.value, lastY.value)
  ctx.value.lineTo(currentX, currentY)
  ctx.value.strokeStyle = fishColor.value
  ctx.value.lineWidth = fishSize.value / 4
  ctx.value.lineCap = 'round'
  ctx.value.stroke()
  
  lastX.value = currentX
  lastY.value = currentY
}

const stopDrawing = () => {
  if (isDrawing.value) {
    isDrawing.value = false
    // 将画的内容转换为鱼
    convertDrawingToFish()
  }
}

// 将绘画内容转换为鱼
const convertDrawingToFish = () => {
  if (!canvas.value) return
  
  // 创建临时画布来分析绘画内容
  const tempCanvas = document.createElement('canvas')
  const tempCtx = tempCanvas.getContext('2d')!
  tempCanvas.width = canvas.value.width
  tempCanvas.height = canvas.value.height
  
  // 复制当前画布内容
  tempCtx.drawImage(canvas.value, 0, 0)
  
  // 分析绘画区域
  const imageData = tempCtx.getImageData(0, 0, tempCanvas.width, tempCanvas.height)
  const data = imageData.data
  
  let minX = tempCanvas.width
  let minY = tempCanvas.height
  let maxX = 0
  let maxY = 0
  
  // 找到绘画边界
  for (let y = 0; y < tempCanvas.height; y++) {
    for (let x = 0; x < tempCanvas.width; x++) {
      const index = (y * tempCanvas.width + x) * 4
      if (data[index + 3] > 0) { // 有像素
        minX = Math.min(minX, x)
        minY = Math.min(minY, y)
        maxX = Math.max(maxX, x)
        maxY = Math.max(maxY, y)
      }
    }
  }
  
  if (maxX > minX && maxY > minY) {
    // 创建鱼
    const fish: Fish = {
      id: fishIdCounter++,
      x: (minX + maxX) / 2,
      y: (minY + maxY) / 2,
      vx: (Math.random() - 0.5) * 2,
      vy: (Math.random() - 0.5) * 2,
      size: Math.max(maxX - minX, maxY - minY) / 2,
      color: fishColor.value,
      points: [],
      angle: Math.random() * Math.PI * 2,
      tailAngle: 0,
      tailDirection: 1
    }
    
    fishes.value.push(fish)
    
    // 清空画布，重新绘制鱼缸
    drawFishTank()
  }
}

// 添加随机鱼
const addRandomFish = () => {
  const fish: Fish = {
    id: fishIdCounter++,
    x: Math.random() * 600 + 100,
    y: Math.random() * 400 + 100,
    vx: (Math.random() - 0.5) * 3,
    vy: (Math.random() - 0.5) * 3,
    size: Math.random() * 30 + 15,
    color: `hsl(${Math.random() * 360}, 70%, 60%)`,
    points: [],
    angle: Math.random() * Math.PI * 2,
    tailAngle: 0,
    tailDirection: 1
  }
  
  fishes.value.push(fish)
}

// 清空画布
const clearCanvas = () => {
  fishes.value = []
  drawFishTank()
}

// 切换动画
const toggleAnimation = () => {
  isAnimating.value = !isAnimating.value
  if (isAnimating.value) {
    startAnimation()
  } else {
    cancelAnimationFrame(animationId)
  }
}

// 动画循环
const startAnimation = () => {
  const animate = () => {
    if (!isAnimating.value) return
    
    updateFishes()
    drawFishTank()
    drawFishes()
    
    animationId = requestAnimationFrame(animate)
  }
  
  animate()
}

// 更新鱼的位置
const updateFishes = () => {
  fishes.value.forEach(fish => {
    // 更新位置
    fish.x += fish.vx
    fish.y += fish.vy
    
    // 边界检测
    if (fish.x < 100 || fish.x > 700) {
      fish.vx *= -1
      fish.x = Math.max(100, Math.min(700, fish.x))
    }
    
    if (fish.y < 100 || fish.y > 500) {
      fish.vy *= -1
      fish.y = Math.max(100, Math.min(500, fish.y))
    }
    
    // 更新角度
    fish.angle = Math.atan2(fish.vy, fish.vx)
    
    // 更新尾巴摆动
    fish.tailAngle += 0.2 * fish.tailDirection
    if (fish.tailAngle > 0.5 || fish.tailAngle < -0.5) {
      fish.tailDirection *= -1
    }
    
    // 添加一些随机性
    if (Math.random() < 0.02) {
      fish.vx += (Math.random() - 0.5) * 0.5
      fish.vy += (Math.random() - 0.5) * 0.5
      
      // 限制速度
      fish.vx = Math.max(-3, Math.min(3, fish.vx))
      fish.vy = Math.max(-3, Math.min(3, fish.vy))
    }
  })
}

// 绘制所有鱼
const drawFishes = () => {
  if (!ctx.value) return
  
  fishes.value.forEach(fish => {
    drawFish(fish)
  })
}

// 绘制单条鱼
const drawFish = (fish: Fish) => {
  if (!ctx.value) return
  
  ctx.value.save()
  ctx.value.translate(fish.x, fish.y)
  ctx.value.rotate(fish.angle)
  
  // 鱼身
  ctx.value.fillStyle = fish.color
  ctx.value.beginPath()
  ctx.value.ellipse(0, 0, fish.size, fish.size * 0.6, 0, 0, Math.PI * 2)
  ctx.value.fill()
  
  // 鱼尾
  ctx.value.fillStyle = fish.color
  ctx.value.beginPath()
  ctx.value.moveTo(-fish.size, 0)
  ctx.value.lineTo(-fish.size - fish.size * 0.8, -fish.size * 0.4)
  ctx.value.lineTo(-fish.size - fish.size * 0.8, fish.size * 0.4)
  ctx.value.closePath()
  ctx.value.fill()
  
  // 鱼眼
  ctx.value.fillStyle = '#000'
  ctx.value.beginPath()
  ctx.value.arc(fish.size * 0.3, -fish.size * 0.2, fish.size * 0.1, 0, Math.PI * 2)
  ctx.value.fill()
  
  // 鱼鳍
  ctx.value.fillStyle = fish.color
  ctx.value.beginPath()
  ctx.value.ellipse(0, -fish.size * 0.4, fish.size * 0.3, fish.size * 0.2, 0, 0, Math.PI * 2)
  ctx.value.fill()
  
  ctx.value.restore()
}
</script>

<style scoped>
.fish-tank-game {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
  font-family: 'Arial', sans-serif;
}

.game-controls {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
  flex-wrap: wrap;
  align-items: center;
  background: #f5f5f5;
  padding: 15px;
  border-radius: 8px;
}

.control-btn {
  background: #4A90E2;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
  font-size: 14px;
  transition: background 0.3s;
}

.control-btn:hover {
  background: #357ABD;
}

.color-picker, .size-slider {
  display: flex;
  align-items: center;
  gap: 10px;
}

.color-picker input {
  width: 40px;
  height: 30px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.size-slider input {
  width: 100px;
}

.fish-tank-container {
  position: relative;
  margin-bottom: 20px;
}

.fish-tank-canvas {
  border: 2px solid #ddd;
  border-radius: 8px;
  cursor: crosshair;
  background: white;
}

.fish-tank-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.bubble {
  position: absolute;
  background: rgba(255, 255, 255, 0.6);
  border-radius: 50%;
  animation: float 3s infinite ease-in-out;
}

.bubble-1 {
  width: 8px;
  height: 8px;
  left: 20%;
  animation-delay: 0s;
}

.bubble-2 {
  width: 12px;
  height: 12px;
  left: 40%;
  animation-delay: 0.5s;
}

.bubble-3 {
  width: 6px;
  height: 6px;
  left: 60%;
  animation-delay: 1s;
}

.bubble-4 {
  width: 10px;
  height: 10px;
  left: 80%;
  animation-delay: 1.5s;
}

.bubble-5 {
  width: 14px;
  height: 14px;
  left: 90%;
  animation-delay: 2s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px);
    opacity: 0.6;
  }
  50% {
    transform: translateY(-20px);
    opacity: 1;
  }
}

.instructions {
  background: #E3F2FD;
  padding: 20px;
  border-radius: 8px;
  border-left: 4px solid #4A90E2;
}

.instructions h3 {
  margin-top: 0;
  color: #1976D2;
}

.instructions p {
  margin: 8px 0;
  color: #424242;
}
</style>
