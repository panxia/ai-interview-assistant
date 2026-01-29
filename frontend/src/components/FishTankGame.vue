<template>
  <div class="max-w-6xl mx-auto p-4">
    <!-- 游戏标题区域 -->
    <div class="text-center mb-6">
      <h2 class="text-3xl font-bold text-gray-800 mb-2">🐠 手动画鱼鱼缸游戏 🐠</h2>
      <p class="text-gray-600">在画布上画鱼，观看鱼在鱼缸里游泳！</p>
    </div>

    <!-- 游戏控制面板 -->
    <div class="bg-white/80 backdrop-blur-sm rounded-2xl p-6 shadow-lg mb-6">
      <div class="grid grid-cols-1 md:grid-cols-3 gap-4 items-end">
        <!-- 颜色选择器 -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">鱼的颜色</label>
          <div class="flex items-center space-x-2">
            <input 
              v-model="currentColor" 
              type="color" 
              class="w-10 h-10 border-0 rounded cursor-pointer"
            />
            <span class="text-sm">{{ currentColor }}</span>
          </div>
        </div>

        <!-- 大小滑块 -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">
            鱼的大小: {{ currentSize }}
          </label>
          <input 
            v-model="currentSize" 
            type="range" 
            min="10" 
            max="50" 
            class="w-full"
          />
        </div>

        <!-- 控制按钮 -->
        <div class="flex flex-wrap gap-2">
          <button 
            @click="clearCanvas"
            class="px-4 py-2 bg-red-500 text-white rounded-lg hover:bg-red-600 transition-colors"
          >
            清空画布
          </button>
          <button 
            @click="addRandomFish"
            class="px-4 py-2 bg-green-500 text-white rounded-lg hover:bg-green-600 transition-colors"
          >
            添加随机鱼
          </button>
          <button 
            @click="toggleAnimation"
            class="px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition-colors"
          >
            {{ isAnimating ? '停止动画' : '开始动画' }}
          </button>
        </div>
      </div>
    </div>

    <!-- 游戏画布 -->
    <div class="bg-gradient-to-b from-blue-300 to-blue-500 rounded-3xl p-6 shadow-xl">
      <div class="relative bg-blue-100 rounded-2xl overflow-hidden" 
           :style="{ width: canvasWidth + 'px', height: canvasHeight + 'px', margin: '0 auto' }">
        <canvas 
          ref="canvasRef"
          :width="canvasWidth"
          :height="canvasHeight"
          class="absolute inset-0 cursor-crosshair"
          @mousedown="startDrawing"
          @mousemove="draw"
          @mouseup="stopDrawing"
          @mouseleave="stopDrawing"
        ></canvas>
        
        <!-- 装饰元素 -->
        <div class="absolute bottom-0 left-0 w-full h-16 bg-gradient-to-t from-green-700 to-green-500 opacity-30"></div>
        <div class="absolute bottom-4 left-10 w-2 h-16 bg-green-600 rounded-t-full opacity-60"></div>
        <div class="absolute bottom-6 right-20 w-3 h-12 bg-green-600 rounded-t-full opacity-60"></div>
        <div class="absolute top-10 left-20 w-2 h-2 bg-white rounded-full animate-bounce" 
             :style="{ animationDuration: '3s' }"></div>
        <div class="absolute top-20 right-32 w-1.5 h-1.5 bg-white rounded-full animate-bounce" 
             :style="{ animationDuration: '4s', animationDelay: '1s' }"></div>
        <div class="absolute top-32 left-40 w-1 h-1 bg-white rounded-full animate-bounce" 
             :style="{ animationDuration: '5s', animationDelay: '2s' }"></div>
      </div>
    </div>

    <!-- 游戏统计 -->
    <div class="mt-6 text-center text-gray-600">
      <p>当前鱼的数量: {{ fishes.length }} | 
         <span v-if="drawing">正在绘制...</span>
         <span v-else>点击并拖拽绘制鱼</span>
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick } from 'vue';

// 画布相关
const canvasRef = ref<HTMLCanvasElement | null>(null);
const canvasWidth = 800;
const canvasHeight = 600;

// 绘制状态
const isDrawing = ref(false);
const drawing = ref(false);
const lastX = ref(0);
const lastY = ref(0);

// 游戏状态
const fishes = ref<any[]>([]);
const isAnimating = ref(true);
let animationFrameId: number | null = null;

// 鱼的属性
const currentColor = ref('#FF6B6B');
const currentSize = ref(20);

// 鱼类定义
class Fish {
  x: number;
  y: number;
  vx: number;
  vy: number;
  size: number;
  color: string;
  points: {x: number, y: number}[];
  swimTimer: number;

  constructor(x: number, y: number, size: number, color: string, points?: {x: number, y: number}[]) {
    this.x = x;
    this.y = y;
    this.size = size;
    this.color = color;
    
    // 随机游泳速度
    this.vx = (Math.random() - 0.5) * 2;
    this.vy = (Math.random() - 0.5) * 1;
    
    // 如果提供了点，则使用这些点作为鱼的形状
    this.points = points || [
      {x: 0, y: 0}, {x: 10, y: -5}, {x: 20, y: 0}, {x: 10, y: 5}
    ];
    
    // 游泳方向变化计时器
    this.swimTimer = Math.random() * 100;
  }

  update(canvasWidth: number, canvasHeight: number) {
    // 更新位置
    this.x += this.vx;
    this.y += this.vy;
    
    // 边界检测和反弹
    if (this.x <= this.size || this.x >= canvasWidth - this.size) {
      this.vx = -this.vx;
    }
    if (this.y <= this.size || this.y >= canvasHeight - this.size) {
      this.vy = -this.vy;
    }
    
    // 随机改变游泳方向
    this.swimTimer++;
    if (this.swimTimer > 100 && Math.random() < 0.02) {
      this.vx += (Math.random() - 0.5) * 0.5;
      this.vy += (Math.random() - 0.5) * 0.5;
      
      // 限制速度
      this.vx = Math.max(-3, Math.min(3, this.vx));
      this.vy = Math.max(-2, Math.min(2, this.vy));
      
      this.swimTimer = 0;
    }
  }

  draw(ctx: CanvasRenderingContext2D) {
    ctx.save();
    ctx.translate(this.x, this.y);
    
    // 根据速度调整鱼的角度
    const angle = Math.atan2(this.vy, this.vx);
    ctx.rotate(angle);
    
    // 绘制鱼身
    ctx.fillStyle = this.color;
    ctx.beginPath();
    ctx.ellipse(0, 0, this.size, this.size / 2, 0, 0, Math.PI * 2);
    ctx.fill();
    
    // 绘制鱼尾
    ctx.beginPath();
    ctx.moveTo(-this.size * 0.8, 0);
    ctx.lineTo(-this.size * 1.4, -this.size * 0.5);
    ctx.lineTo(-this.size * 1.4, this.size * 0.5);
    ctx.closePath();
    ctx.fillStyle = this.color;
    ctx.fill();
    
    // 绘制鱼鳍
    ctx.beginPath();
    ctx.ellipse(-this.size * 0.3, this.size * 0.4, this.size * 0.3, this.size * 0.2, 0, 0, Math.PI * 2);
    ctx.fillStyle = this.color;
    ctx.fill();
    
    ctx.restore();
  }
}

// 绘制相关方法
const startDrawing = (e: MouseEvent) => {
  if (!canvasRef.value) return;
  
  const canvas = canvasRef.value;
  const rect = canvas.getBoundingClientRect();
  const scaleX = canvas.width / rect.width;
  const scaleY = canvas.height / rect.height;
  
  lastX.value = (e.clientX - rect.left) * scaleX;
  lastY.value = (e.clientY - rect.top) * scaleY;
  drawing.value = true;
};

const draw = (e: MouseEvent) => {
  if (!drawing.value || !canvasRef.value) return;
  
  const canvas = canvasRef.value;
  const ctx = canvas.getContext('2d');
  if (!ctx) return;
  
  const rect = canvas.getBoundingClientRect();
  const scaleX = canvas.width / rect.width;
  const scaleY = canvas.height / rect.height;
  
  const currentX = (e.clientX - rect.left) * scaleX;
  const currentY = (e.clientY - rect.top) * scaleY;
  
  // 绘制线条
  ctx.lineWidth = currentSize.value;
  ctx.lineCap = 'round';
  ctx.lineJoin = 'round';
  ctx.strokeStyle = currentColor.value;
  
  ctx.beginPath();
  ctx.moveTo(lastX.value, lastY.value);
  ctx.lineTo(currentX, currentY);
  ctx.stroke();
  
  lastX.value = currentX;
  lastY.value = currentY;
};

const stopDrawing = () => {
  if (drawing.value) {
    drawing.value = false;
    // 尝试识别绘制的形状并创建鱼
    createFishFromDrawing();
  }
};

// 从绘制的线条创建鱼
const createFishFromDrawing = () => {
  // 这里可以实现形状识别算法，暂时简单地在绘制终点创建一条鱼
  fishes.value.push(
    new Fish(lastX.value, lastY.value, currentSize.value, currentColor.value)
  );
};

// 游戏控制方法
const clearCanvas = () => {
  if (!canvasRef.value) return;
  const ctx = canvasRef.value.getContext('2d');
  if (ctx) {
    ctx.clearRect(0, 0, canvasRef.value.width, canvasRef.value.height);
  }
  fishes.value = [];
};

const addRandomFish = () => {
  const x = Math.random() * (canvasWidth - 40) + 20;
  const y = Math.random() * (canvasHeight - 40) + 20;
  const size = Math.floor(Math.random() * 20) + 15;
  const colors = ['#FF6B6B', '#4ECDC4', '#45B7D1', '#FFBE0B', '#FB5607', '#8338EC'];
  const color = colors[Math.floor(Math.random() * colors.length)];
  
  fishes.value.push(new Fish(x, y, size, color));
};

const toggleAnimation = () => {
  isAnimating.value = !isAnimating.value;
  if (isAnimating.value) {
    startAnimation();
  } else if (animationFrameId) {
    cancelAnimationFrame(animationFrameId);
  }
};

// 动画循环
const animate = () => {
  if (!canvasRef.value) return;
  const ctx = canvasRef.value.getContext('2d');
  if (!ctx) return;
  
  // 清除画布
  ctx.clearRect(0, 0, canvasWidth, canvasHeight);
  
  // 绘制背景
  drawBackground(ctx);
  
  // 更新并绘制鱼
  fishes.value.forEach(fish => {
    fish.update(canvasWidth, canvasHeight);
    fish.draw(ctx);
  });
  
  if (isAnimating.value) {
    animationFrameId = requestAnimationFrame(animate);
  }
};

const drawBackground = (ctx: CanvasRenderingContext2D) => {
  // 绘制水底
  const gradient = ctx.createLinearGradient(0, canvasHeight * 0.7, 0, canvasHeight);
  gradient.addColorStop(0, 'rgba(34, 197, 94, 0.3)');
  gradient.addColorStop(1, 'rgba(21, 94, 76, 0.5)');
  
  ctx.fillStyle = gradient;
  ctx.fillRect(0, canvasHeight * 0.7, canvasWidth, canvasHeight * 0.3);
  
  // 绘制水草
  ctx.fillStyle = 'rgba(72, 187, 120, 0.7)';
  for (let i = 0; i < 5; i++) {
    const x = (canvasWidth / 5) * i + 20;
    ctx.beginPath();
    ctx.ellipse(x, canvasHeight * 0.7, 3, 30, Math.PI / 2, 0, Math.PI * 2);
    ctx.fill();
  }
  
  // 绘制气泡
  ctx.fillStyle = 'rgba(255, 255, 255, 0.6)';
  for (let i = 0; i < 10; i++) {
    const x = Math.random() * canvasWidth;
    const y = canvasHeight - (Math.random() * canvasHeight * 0.5);
    const radius = Math.random() * 3 + 1;
    ctx.beginPath();
    ctx.arc(x, y, radius, 0, Math.PI * 2);
    ctx.fill();
  }
};

const startAnimation = () => {
  animate();
};

// 初始化
onMounted(async () => {
  await nextTick();
  if (canvasRef.value) {
    const ctx = canvasRef.value.getContext('2d');
    if (ctx) {
      drawBackground(ctx);
    }
  }
  startAnimation();
});

// 清理
onUnmounted(() => {
  if (animationFrameId) {
    cancelAnimationFrame(animationFrameId);
  }
});
</script>

<style scoped>
canvas {
  touch-action: none;
}
</style>