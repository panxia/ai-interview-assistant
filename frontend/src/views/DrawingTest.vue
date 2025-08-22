<template>
  <div class="drawing-test">
    <h1>🎨 绘制测试页面</h1>
    
    <div class="test-controls">
      <div class="color-picker">
        <label>选择颜色:</label>
        <div class="color-grid">
          <div 
            v-for="color in colors" 
            :key="color"
            :class="['color-item', { active: selectedColor === color }]"
            :style="{ backgroundColor: color }"
            @click="selectColor(color)"
          ></div>
        </div>
      </div>
      
      <div class="brush-size">
        <label>画笔粗细: {{ selectedBrushSize }}px</label>
        <input 
          type="range" 
          v-model="selectedBrushSize" 
          :min="1" 
          :max="20" 
          class="size-slider"
        >
      </div>
      
      <div class="actions">
        <button @click="clearCanvas" class="btn btn-danger">清空画布</button>
        <button @click="undoLast" class="btn btn-secondary">撤销</button>
        <button @click="saveImage" class="btn btn-primary">保存图片</button>
      </div>
    </div>
    
    <div class="canvas-container">
      <div ref="drawingContainer" class="drawing-canvas"></div>
    </div>
    
    <div class="debug-info">
      <p>当前颜色: <span :style="{ color: selectedColor }">{{ selectedColor }}</span></p>
      <p>画笔粗细: {{ selectedBrushSize }}px</p>
      <p>路径点数: {{ pathPoints }}</p>
      <p>绘制状态: {{ isDrawing ? '绘制中' : '空闲' }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch } from 'vue'
import { CanvasDrawingSystem } from '../game/systems/CanvasDrawingSystem'
import { DrawingConfig } from '../types/game'

// 绘制系统
let drawingSystem: CanvasDrawingSystem | null = null

// 配置
const drawingConfig: DrawingConfig = {
  brushColors: [
    '#FF6B6B', '#4ECDC4', '#45B7D1', '#96CEB4', 
    '#FFEAA7', '#DDA0DD', '#98D8C8', '#F7DC6F',
    '#E17055', '#74B9FF', '#A29BFE', '#FD79A8'
  ],
  brushSizes: [1, 2, 3, 4, 5, 6, 8, 10, 12, 15, 18, 20],
  canvasWidth: 600,
  canvasHeight: 400
}

// 状态
const selectedColor = ref(drawingConfig.brushColors[0])
const selectedBrushSize = ref(drawingConfig.brushSizes[2])
const isDrawing = ref(false)
const pathPoints = ref(0)

// 容器引用
const drawingContainer = ref<HTMLDivElement>()

// 颜色和画笔大小
const colors = drawingConfig.brushColors

// 选择颜色
const selectColor = (color: string) => {
  selectedColor.value = color
  if (drawingSystem) {
    drawingSystem.setColor(color)
  }
}

// 清空画布
const clearCanvas = () => {
  if (drawingSystem) {
    drawingSystem.clearDrawing()
    updateDebugInfo()
  }
}

// 撤销
const undoLast = () => {
  if (drawingSystem) {
    drawingSystem.undo()
    updateDebugInfo()
  }
}

// 保存图片
const saveImage = () => {
  if (drawingSystem) {
    const imageData = drawingSystem.getDrawingImageData()
    if (imageData) {
      const link = document.createElement('a')
      link.download = 'drawing.png'
      link.href = imageData
      link.click()
    }
  }
}

// 更新调试信息
const updateDebugInfo = () => {
  if (drawingSystem) {
    pathPoints.value = drawingSystem.getCurrentPath().length
    isDrawing.value = drawingSystem.isDrawingActive()
  }
}

// 初始化绘制系统
const initDrawingSystem = () => {
  if (drawingContainer.value) {
    try {
      drawingSystem = new CanvasDrawingSystem(
        drawingContainer.value,
        drawingConfig
      )
      
      // 设置初始颜色和画笔大小
      drawingSystem.setColor(selectedColor.value)
      drawingSystem.setBrushSize(selectedBrushSize.value)
      
      console.log('绘制系统初始化成功')
    } catch (error) {
      console.error('绘制系统初始化失败:', error)
    }
  }
}

// 监听画笔大小变化
const watchBrushSize = () => {
  if (drawingSystem) {
    drawingSystem.setBrushSize(selectedBrushSize.value)
  }
}

// 生命周期
onMounted(() => {
  initDrawingSystem()
  
  // 定期更新调试信息
  const interval = setInterval(updateDebugInfo, 100)
  
  onUnmounted(() => {
    clearInterval(interval)
    if (drawingSystem) {
      drawingSystem.destroy()
    }
  })
})

// 监听画笔大小变化
watch(selectedBrushSize, watchBrushSize)
</script>

<style scoped>
.drawing-test {
  padding: 2rem;
  max-width: 1200px;
  margin: 0 auto;
  font-family: 'Inter', sans-serif;
}

h1 {
  text-align: center;
  color: #1e293b;
  margin-bottom: 2rem;
}

.test-controls {
  background: #f8fafc;
  padding: 1.5rem;
  border-radius: 12px;
  margin-bottom: 2rem;
  border: 1px solid #e2e8f0;
}

.color-picker {
  margin-bottom: 1.5rem;
}

.color-picker label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 600;
  color: #475569;
}

.color-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 0.5rem;
}

.color-item {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  cursor: pointer;
  border: 3px solid transparent;
  transition: all 0.2s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.color-item:hover {
  transform: scale(1.1);
}

.color-item.active {
  border-color: #1e293b;
  transform: scale(1.2);
}

.brush-size {
  margin-bottom: 1.5rem;
}

.brush-size label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 600;
  color: #475569;
}

.size-slider {
  width: 100%;
  height: 6px;
  border-radius: 3px;
  background: #e2e8f0;
  outline: none;
  -webkit-appearance: none;
}

.size-slider::-webkit-slider-thumb {
  -webkit-appearance: none;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: #667eea;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.actions {
  display: flex;
  gap: 1rem;
}

.btn {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-primary {
  background: #667eea;
  color: white;
}

.btn-primary:hover {
  background: #5a67d8;
  transform: translateY(-1px);
}

.btn-secondary {
  background: #6c757d;
  color: white;
}

.btn-secondary:hover {
  background: #5a6268;
  transform: translateY(-1px);
}

.btn-danger {
  background: #dc3545;
  color: white;
}

.btn-danger:hover {
  background: #c82333;
  transform: translateY(-1px);
}

.canvas-container {
  display: flex;
  justify-content: center;
  margin-bottom: 2rem;
}

.drawing-canvas {
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  background: white;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.debug-info {
  background: #f1f5f9;
  padding: 1.5rem;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
}

.debug-info p {
  margin: 0.5rem 0;
  font-family: monospace;
  color: #475569;
}

.debug-info span {
  font-weight: 600;
}
</style>
