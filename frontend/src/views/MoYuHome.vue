<template>
  <div class="moyu-home">
    <!-- 顶部导航栏 -->
    <nav class="navbar">
      <div class="nav-brand">
        <span class="brand-icon">🐟</span>
        <span class="brand-text">摸鱼世界</span>
    </div>
      <div class="nav-actions">
        <button class="nav-btn" @click="toggleDrawingMode">
          <span class="btn-icon">🎨</span>
          {{ isDrawingMode ? '退出绘制' : '开始画鱼' }}
        </button>
        <button class="nav-btn" @click="toggleStats">
          <span class="btn-icon">📊</span>
          性能监控
        </button>
      </div>
    </nav>

    <!-- 主要内容区域 -->
    <main class="main-content">
      <!-- 绘制模式 -->
      <div v-if="isDrawingMode" class="drawing-mode">
        <div class="drawing-header">
          <h2>🎨 创作你的鱼</h2>
          <p>用画笔绘制一条独特的鱼，让它在大海中自由游动</p>
        </div>
        
        <div class="drawing-workspace">
          <!-- 左侧工具栏 -->
          <div class="toolbar">
          <div class="tool-section">
              <h3>🎨 画笔设置</h3>
              <div class="color-picker">
                <label>颜色选择</label>
                <div class="color-grid">
                  <div 
                    v-for="color in brushColors" 
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
          </div>
          
          <div class="tool-section">
              <h3>🛠️ 操作工具</h3>
              <button class="tool-btn" @click="clearCanvas">
                <span class="tool-icon">🗑️</span>
                清空画布
            </button>
              <button class="tool-btn" @click="undoLastStroke">
                <span class="tool-icon">↶</span>
                撤销
            </button>
          </div>
          
          <div class="tool-section">
              <h3>🐠 鱼的信息</h3>
              <div class="fish-info">
                <label>鱼的名字</label>
            <input 
              v-model="fishName" 
              type="text" 
                  placeholder="给你的鱼起个名字..."
                  class="name-input"
              maxlength="20"
            >
                <span class="char-count">{{ fishName.length }}/20</span>
          </div>
          
              <button 
                class="save-btn" 
                @click="saveFish"
                :disabled="!canSaveFish"
              >
                <span class="btn-icon">💾</span>
                保存鱼
          </button>
            </div>
        </div>
        
          <!-- 右侧绘制区域 -->
          <div class="canvas-area">
            <div class="canvas-container">
              <div ref="drawingContainer" class="drawing-canvas"></div>
              <div v-if="!hasStartedDrawing" class="canvas-overlay">
                <div class="canvas-hint">
                  <span class="hint-icon">✏️</span>
                  <span>在画布上绘制你的鱼</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 游戏模式 -->
      <div v-else class="game-mode">
        <div class="game-header">
          <h2>🌊 海底世界</h2>
          <p>观看你的鱼和其他玩家的鱼在海底自由游动</p>
        </div>
        
        <div class="game-workspace">
          <!-- 游戏画布 -->
          <div class="game-canvas-container">
            <div ref="gameContainer" class="game-canvas"></div>
            
            <!-- 游戏控制面板 -->
            <div class="game-controls">
              <button class="control-btn" @click="resetCamera">
                <span class="control-icon">🏠</span>
                重置视角
              </button>
              <button class="control-btn" @click="togglePause">
                <span class="control-icon">{{ isPaused ? '▶️' : '⏸️' }}</span>
                {{ isPaused ? '继续' : '暂停' }}
            </button>
            </div>
          </div>
          
          <!-- 右侧信息面板 -->
          <div class="info-panel">
            <div class="panel-section">
              <h3>🐠 鱼群信息</h3>
              <div class="fish-stats">
                <div class="stat-item">
                  <span class="stat-label">总数量</span>
                  <span class="stat-value">{{ fishCount }}</span>
          </div>
                <div class="stat-item">
                  <span class="stat-label">我的鱼</span>
                  <span class="stat-value">{{ myFishCount }}</span>
        </div>
                <div class="stat-item">
                  <span class="stat-label">总点赞</span>
                  <span class="stat-value">{{ totalLikes }}</span>
      </div>
    </div>
  </div>
            
            <div class="panel-section">
              <h3>⚡ 性能监控</h3>
              <div class="performance-stats">
                <div class="stat-item">
                  <span class="stat-label">FPS</span>
                  <span class="stat-value">{{ performanceMetrics.fps.toFixed(1) }}</span>
                </div>
                <div class="stat-item">
                  <span class="stat-label">帧时间</span>
                  <span class="stat-value">{{ performanceMetrics.frameTime.toFixed(1) }}ms</span>
                </div>
                <div class="stat-item">
                  <span class="stat-label">内存</span>
                  <span class="stat-value">{{ formatMemorySize(performanceMetrics.memoryUsage) }}</span>
                </div>
              </div>
            </div>
            
            <div class="panel-section">
              <h3>🎮 操作说明</h3>
              <div class="controls-help">
                <div class="help-item">
                  <span class="help-key">鼠标拖拽</span>
                  <span class="help-desc">移动视角</span>
                </div>
                <div class="help-item">
                  <span class="help-key">滚轮</span>
                  <span class="help-desc">缩放视角</span>
                </div>
                <div class="help-item">
                  <span class="help-key">点击鱼</span>
                  <span class="help-desc">点赞/取消点赞</span>
                </div>
                <div class="help-item">
                  <span class="help-key">空格键</span>
                  <span class="help-desc">重置视角</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>
    
    <!-- 底部状态栏 -->
    <footer class="status-bar">
      <div class="status-info">
        <span class="status-item">
          <span class="status-icon">🌊</span>
          在线玩家: {{ onlinePlayers }}
        </span>
        <span class="status-item">
          <span class="status-icon">🐠</span>
          总鱼数: {{ totalFishCount }}
        </span>
        <span class="status-item">
          <span class="status-icon">❤️</span>
          总点赞: {{ totalLikes }}
        </span>
      </div>
      <div class="status-actions">
        <button class="status-btn" @click="refreshGame">
          <span class="btn-icon">🔄</span>
          刷新
        </button>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick, reactive, computed, watch } from 'vue'
import { GameEngine } from '../game/engine/GameEngine'
import { Renderer } from '../game/engine/Renderer'
import { Physics } from '../game/engine/Physics'
import { CanvasDrawingSystem } from '../game/systems/CanvasDrawingSystem'
import { AnimationSystem } from '../game/systems/AnimationSystem'
import { InteractionSystem } from '../game/systems/InteractionSystem'
import { Performance } from '../game/utils/Performance'
import { FishDataValidator } from '../utils/fishDataValidator'
import { 
  GameConfig, 
  GameState, 
  GameEvents, 
  PhysicsConfig, 
  AnimationConfig, 
  DrawingConfig,
  Fish,
  Point
} from '../types/game'

// API基础URL
const API_BASE_URL = 'http://localhost:8080/api/fishes'

// 游戏引擎实例
let gameEngine: GameEngine | null = null
let renderer: Renderer | null = null
let physics: Physics | null = null
let drawingSystem: CanvasDrawingSystem | null = null
let animationSystem: AnimationSystem | null = null
let interactionSystem: InteractionSystem | null = null
let performance: Performance | null = null

// 容器引用
const drawingContainer = ref<HTMLDivElement>()
const gameContainer = ref<HTMLDivElement>()

// 游戏配置
const gameConfig: GameConfig = {
  width: 800,
  height: 600,
  backgroundColor: 0x006994,
  antialias: true,
  resolution: 1,
  autoDensity: true
}

const physicsConfig: PhysicsConfig = {
  gravity: { x: 0, y: 0.1, scale: 1 },
  worldBounds: {
    x: 0,
    y: 0,
    width: 800,
    height: 600
  }
}

const animationConfig: AnimationConfig = {
  frameRate: 60,
  wiggleSpeed: 0.02,
  swimSpeed: 2,
  directionChangeInterval: 5000
}

const drawingConfig: DrawingConfig = {
  brushColors: [
    '#FF6B6B', '#4ECDC4', '#45B7D1', '#96CEB4', 
    '#FFEAA7', '#DDA0DD', '#98D8C8', '#F7DC6F',
    '#E17055', '#74B9FF', '#A29BFE', '#FD79A8'
  ],
  brushSizes: [1, 2, 3, 4, 5, 6, 8, 10, 12, 15, 18, 20],
  canvasWidth: 400,
  canvasHeight: 300
}

// 游戏状态
const gameState = reactive<GameState>({
  isDrawing: false,
  isPlaying: false,
  selectedColor: drawingConfig.brushColors[0],
  selectedBrushSize: drawingConfig.brushSizes[2],
  fishes: [],
  decorations: [],
  camera: { x: 0, y: 0, scale: 1 }
})

// UI状态
const isDrawingMode = ref(false)
const isPaused = ref(false)
const selectedColor = ref(drawingConfig.brushColors[0])
const selectedBrushSize = ref(drawingConfig.brushSizes[2])

// 监听画笔大小变化
watch(selectedBrushSize, (newSize) => {
  if (drawingSystem) {
    drawingSystem.setBrushSize(newSize);
  }
  // 同时更新游戏状态
  gameState.selectedBrushSize = newSize;
});
const fishName = ref('')
const onlinePlayers = ref(1)
const totalFishCount = ref(0)
const totalLikes = ref(0)
const hasStartedDrawing = ref(false)

// 性能指标
const performanceMetrics = reactive({
  fps: 0,
  frameTime: 0,
  memoryUsage: 0
})

// 计算属性
const fishCount = computed(() => gameState.fishes.length)
const myFishCount = computed(() => gameState.fishes.filter(f => f.author === 'user').length)
const canSaveFish = computed(() => fishName.value.trim().length > 0 && gameState.isDrawing)

// 画笔颜色和尺寸
const brushColors = drawingConfig.brushColors
const brushSizes = drawingConfig.brushSizes

// 游戏事件处理
const gameEvents: GameEvents = {
  onFishCreated: (fish: Fish) => {
    console.log('鱼被创建:', fish);
    showNotification('🎉 鱼创建成功！', 'success');
  },
  
  onFishLiked: (fishId: string, liked: boolean) => {
    console.log('鱼被点赞:', fishId, liked);
    updateFishLikes(fishId, liked);
  },
  
  onDrawingStarted: () => {
    gameState.isDrawing = true;
  },
  
  onDrawingFinished: () => {
    gameState.isDrawing = false;
  },
  
  onGameStateChanged: (state: Partial<GameState>) => {
    Object.assign(gameState, state);
  }
};

// 初始化游戏引擎
const initGameEngine = async () => {
  if (!gameContainer.value) return;
  
  try {
    // 创建游戏引擎
    gameEngine = new GameEngine(
      gameContainer.value,
      gameConfig,
      physicsConfig,
      animationConfig,
      gameEvents
    );
    
    // 异步初始化PixiJS应用
    await gameEngine.initialize(gameContainer.value);
    
    // 获取引擎组件
    renderer = new Renderer(gameEngine.getApp());
    physics = new Physics(gameEngine.getWorld(), physicsConfig);
    animationSystem = new AnimationSystem(animationConfig);
    
    // 创建绘制系统
    if (drawingContainer.value) {
      console.log('初始化绘制系统...', drawingContainer.value);
      try {
        drawingSystem = new CanvasDrawingSystem(
          drawingContainer.value,
          drawingConfig
        );
        
        // 设置绘制开始回调
        drawingSystem.setOnDrawingStart(() => {
          hasStartedDrawing.value = true;
        });
        
        console.log('绘制系统初始化成功');
  } catch (error) {
        console.error('绘制系统初始化失败:', error);
      }
    } else {
      console.error('绘制容器未找到');
    }
    
    // 创建交互系统
    interactionSystem = new InteractionSystem(
      gameEngine.getApp().stage,
      gameState
    );
    
    // 创建性能监控
    performance = new Performance();
    performance.setMetricsUpdateCallback((metrics) => {
      Object.assign(performanceMetrics, metrics);
    });
    
        // 设置交互系统回调
    if (interactionSystem) {
      interactionSystem.setOnDrawingStart((position) => {
        if (drawingSystem) {
          drawingSystem.startDrawing(position.x, position.y);
        }
      });
      
      interactionSystem.setOnDrawingMove((position) => {
        if (drawingSystem) {
          drawingSystem.continueDrawing(position.x, position.y);
        }
      });
      
      interactionSystem.setOnDrawingEnd(() => {
        if (drawingSystem) {
          drawingSystem.stopDrawing();
        }
      });
      
      interactionSystem.setOnFishClick((fish) => {
        // 处理鱼被点击
  if (fish.liked) {
          fish.liked = false;
          fish.likes--;
  } else {
          fish.liked = true;
          fish.likes++;
        }
        
        // 同步到后端
        updateFishLikes(fish.fishId, fish.liked);
      });
    }
    
    // 启动性能监控
    performance.start();
    
    // 启动游戏循环
    startGameLoop();
    
    // 加载现有鱼群
    await loadFishes();
    
    console.log('游戏引擎初始化完成');
  } catch (error) {
    console.error('游戏引擎初始化失败:', error);
    showNotification('❌ 游戏引擎初始化失败', 'error');
  }
};

// 加载鱼群数据
const loadFishes = async () => {
  try {
    const response = await fetch(API_BASE_URL);
    if (response.ok) {
      const fishes = await response.json();
      
      // 使用数据验证和修复工具
      const { valid, repaired, invalid } = FishDataValidator.validateAndRepairFishes(fishes);
      
      // 合并有效和已修复的鱼数据
      const allValidFishes = [...valid, ...repaired];
      gameState.fishes = allValidFishes;
      
      // 更新统计信息
      totalFishCount.value = allValidFishes.length;
      totalLikes.value = allValidFishes.reduce((sum: number, fish: Fish) => sum + fish.likes, 0);
      
      // 显示数据质量报告
      const report = FishDataValidator.generateReport(fishes);
      console.log(report);
      
      // 在渲染器中添加所有有效的鱼
      allValidFishes.forEach((fish: Fish) => {
        if (renderer) {
          try {
            renderer.addFish(fish);
          } catch (error) {
            console.error(`添加鱼到渲染器失败: ${fish.name || fish.fishId}`, error);
          }
        }
      });
      
      // 显示修复结果通知
      if (repaired.length > 0) {
        showNotification(`🔧 已修复 ${repaired.length} 条鱼的数据`, 'success');
      }
      
      if (invalid.length > 0) {
        showNotification(`⚠️ 有 ${invalid.length} 条鱼数据无法修复`, 'warning');
      }
      
      console.log(`成功加载鱼群: 有效 ${valid.length} 条, 已修复 ${repaired.length} 条, 无效 ${invalid.length} 条`);
    }
  } catch (error) {
    console.error('加载鱼群失败:', error);
    showNotification('❌ 加载鱼群失败', 'error');
  }
};

// 保存鱼
const saveFish = async () => {
  if (!drawingSystem || !fishName.value.trim()) return;
  
  try {
    const imageData = drawingSystem.getDrawingImageData();
    const path = drawingSystem.getCurrentPath();
    
    if (path.length === 0) {
      showNotification('⚠️ 请先绘制一条鱼', 'warning');
      return;
    }
    
    const fish: Partial<Fish> = {
      fishId: `fish_${Date.now()}`,
      name: fishName.value.trim(),
      color: selectedColor.value,
      path: path,
      position: { x: 100, y: 100 },
      direction: 0,
      speed: 2,
      wiggle: 0.1,
      createdAt: new Date(),
      author: 'user',
      imageData: imageData,
      scale: 1,
      likes: 0,
      liked: false
    };
    
    const response = await fetch(API_BASE_URL, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(fish)
    });
    
    if (response.ok) {
      const savedFish = await response.json();
      gameState.fishes.push(savedFish);
      totalFishCount.value++;
      
      // 在渲染器中添加鱼
      if (renderer) {
        renderer.addFish(savedFish);
      }
      
      // 重置绘制状态
      clearCanvas();
      fishName.value = '';
      
      // 切换到游戏模式
      isDrawingMode.value = false;
      
      showNotification('🎉 鱼保存成功！', 'success');
    } else {
      showNotification('❌ 保存失败，请重试', 'error');
    }
  } catch (error) {
    console.error('保存鱼失败:', error);
    showNotification('❌ 保存失败，请重试', 'error');
  }
};

// 更新鱼点赞状态
const updateFishLikes = async (fishId: string, liked: boolean) => {
  try {
    const response = await fetch(`${API_BASE_URL}/${fishId}/like`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ liked })
    });
    
    if (!response.ok) {
      console.error('更新点赞状态失败');
    }
  } catch (error) {
    console.error('更新点赞状态失败:', error);
  }
};

// 清空画布
const clearCanvas = () => {
  if (drawingSystem) {
    drawingSystem.clearDrawing();
  }
};

// 撤销最后一笔
const undoLastStroke = () => {
  if (drawingSystem) {
    drawingSystem.undo();
  }
};

// 选择颜色
const selectColor = (color: string) => {
  selectedColor.value = color;
  if (drawingSystem) {
    drawingSystem.setColor(color);
  }
  // 同时更新游戏状态
  gameState.selectedColor = color;
};

// 切换绘制模式
const toggleDrawingMode = () => {
  isDrawingMode.value = !isDrawingMode.value;
  if (isDrawingMode.value) {
    // 进入绘制模式
    gameState.isDrawing = true;
  } else {
    // 退出绘制模式
    gameState.isDrawing = false;
    clearCanvas();
  }
};

// 切换性能监控
const toggleStats = () => {
  // 这里可以添加性能监控的显示/隐藏逻辑
};

// 重置相机
const resetCamera = () => {
  if (gameEngine) {
    gameEngine.setGameState({
      camera: { x: 0, y: 0, scale: 1 }
    });
  }
};

// 切换暂停状态
const togglePause = () => {
  isPaused.value = !isPaused.value;
  if (gameEngine) {
    gameEngine.setGameState({
      isPlaying: !isPaused.value
    });
  }
};

// 刷新游戏
const refreshGame = async () => {
  await loadFishes();
  showNotification('🔄 游戏已刷新', 'info');
};

// 显示通知
const showNotification = (message: string, type: 'success' | 'error' | 'warning' | 'info') => {
  // 这里可以实现一个简单的通知系统
  console.log(`${type.toUpperCase()}: ${message}`);
};

// 游戏循环
const startGameLoop = () => {
  const gameLoop = () => {
    // 更新游戏状态
    if (gameEngine && !isPaused.value) {
      // 这里可以添加游戏逻辑更新
      // 例如：更新鱼的位置、动画等
    }
    
    // 更新性能监控
    if (performance) {
      performance.update();
    }
    
    // 继续循环
    requestAnimationFrame(gameLoop);
  };
  
  gameLoop();
};

// 格式化内存大小
const formatMemorySize = (bytes: number): string => {
  if (bytes === 0) return '0 B';
  const k = 1024;
  const sizes = ['B', 'KB', 'MB', 'GB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
};

// 生命周期钩子
onMounted(async () => {
  await nextTick();
  await initGameEngine();
});

onUnmounted(() => {
  // 清理资源
  if (gameEngine) {
    gameEngine.destroy();
  }
  if (performance) {
    performance.destroy();
  }
  if (interactionSystem) {
    interactionSystem.destroy();
  }
});
</script>

<style scoped>
.moyu-home {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  flex-direction: column;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
}

/* 导航栏 */
.navbar {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  padding: 1rem 2rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.nav-brand {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  color: white;
}

.brand-icon {
  font-size: 2rem;
}

.brand-text {
  font-size: 1.5rem;
  font-weight: 700;
}

.nav-actions {
  display: flex;
  gap: 1rem;
}

.nav-btn {
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  color: white;
  padding: 0.75rem 1.5rem;
  border-radius: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.nav-btn:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-2px);
}

.btn-icon {
  font-size: 1.2rem;
}

/* 主要内容 */
.main-content {
  flex: 1;
  padding: 2rem;
  max-width: 1400px;
  margin: 0 auto;
  width: 100%;
}

/* 绘制模式 */
.drawing-mode {
  background: white;
  border-radius: 20px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.drawing-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 2rem;
  text-align: center;
}

.drawing-header h2 {
  margin: 0 0 0.5rem 0;
  font-size: 2rem;
  font-weight: 700;
}

.drawing-header p {
  margin: 0;
  opacity: 0.9;
  font-size: 1.1rem;
}

.drawing-workspace {
  display: flex;
  min-height: 600px;
}

.toolbar {
  width: 320px;
  background: #f8fafc;
  padding: 2rem;
  border-right: 1px solid #e2e8f0;
}

.tool-section {
  margin-bottom: 2rem;
}

.tool-section h3 {
  margin: 0 0 1rem 0;
  color: #1e293b;
  font-size: 1.1rem;
  font-weight: 600;
}

.color-picker label,
.brush-size label {
  display: block;
  margin-bottom: 0.5rem;
  color: #64748b;
  font-weight: 500;
}

.color-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 0.5rem;
  margin-bottom: 1rem;
}

.color-item {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  cursor: pointer;
  border: 3px solid transparent;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.color-item:hover {
  transform: scale(1.1);
}

.color-item.active {
  border-color: #1e293b;
  transform: scale(1.2);
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

.tool-btn {
  width: 100%;
  background: #f1f5f9;
  border: 1px solid #e2e8f0;
  color: #475569;
  padding: 0.75rem;
  border-radius: 12px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  margin-bottom: 0.5rem;
}

.tool-btn:hover {
  background: #e2e8f0;
  transform: translateY(-1px);
}

.tool-icon {
  font-size: 1.2rem;
}

.fish-info {
  margin-bottom: 1rem;
}

.name-input {
  width: 100%;
  padding: 0.75rem;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  font-size: 1rem;
  box-sizing: border-box;
  transition: border-color 0.3s ease;
}

.name-input:focus {
  outline: none;
  border-color: #667eea;
}

.char-count {
  display: block;
  text-align: right;
  font-size: 0.875rem;
  color: #64748b;
  margin-top: 0.25rem;
}

.save-btn {
  width: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  color: white;
  padding: 1rem;
  border-radius: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
}

.save-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
}

.save-btn:disabled {
  background: #94a3b8;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.canvas-area {
  flex: 1;
  padding: 2rem;
  display: flex;
  flex-direction: column;
}

.canvas-container {
  flex: 1;
  background: #f8fafc;
  border-radius: 16px;
  border: 2px dashed #cbd5e1;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  overflow: hidden;
}

.drawing-canvas {
  width: 400px;
  height: 300px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
  pointer-events: auto;
  position: relative;
}

.canvas-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  pointer-events: none;
}

.canvas-hint {
  background: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 1rem 2rem;
  border-radius: 12px;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-weight: 500;
}

.hint-icon {
  font-size: 1.2rem;
}

/* 游戏模式 */
.game-mode {
  background: white;
  border-radius: 20px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.game-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 2rem;
  text-align: center;
}

.game-header h2 {
  margin: 0 0 0.5rem 0;
  font-size: 2rem;
  font-weight: 700;
}

.game-header p {
  margin: 0;
  opacity: 0.9;
  font-size: 1.1rem;
}

.game-workspace {
  display: flex;
  min-height: 600px;
}

.game-canvas-container {
  flex: 1;
  padding: 2rem;
  position: relative;
}

.game-canvas {
  width: 100%;
  height: 100%;
  background: #f8fafc;
  border-radius: 16px;
  border: 2px solid #e2e8f0;
}

.game-controls {
  position: absolute;
  bottom: 2rem;
  left: 2rem;
  display: flex;
  gap: 1rem;
}

.control-btn {
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid #e2e8f0;
  color: #475569;
  padding: 0.75rem 1.5rem;
  border-radius: 12px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.control-btn:hover {
  background: white;
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.control-icon {
  font-size: 1.2rem;
}

.info-panel {
  width: 320px;
  background: #f8fafc;
  padding: 2rem;
  border-left: 1px solid #e2e8f0;
}

.panel-section {
  margin-bottom: 2rem;
}

.panel-section h3 {
  margin: 0 0 1rem 0;
  color: #1e293b;
  font-size: 1.1rem;
  font-weight: 600;
}

.fish-stats,
.performance-stats {
  display: flex;
    flex-direction: column;
  gap: 0.75rem;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem;
  background: white;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
}

.stat-label {
  color: #64748b;
  font-weight: 500;
}

.stat-value {
  color: #1e293b;
  font-weight: 600;
  font-size: 1.1rem;
}

.controls-help {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.help-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.5rem 0;
  border-bottom: 1px solid #e2e8f0;
}

.help-key {
  background: #f1f5f9;
  color: #475569;
  padding: 0.25rem 0.5rem;
  border-radius: 6px;
  font-size: 0.875rem;
  font-weight: 500;
  font-family: monospace;
}

.help-desc {
  color: #64748b;
  font-size: 0.875rem;
}

/* 底部状态栏 */
.status-bar {
  background: rgba(0, 0, 0, 0.8);
  color: white;
  padding: 1rem 2rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.status-info {
  display: flex;
  gap: 2rem;
}

.status-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.875rem;
}

.status-icon {
  font-size: 1.1rem;
}

.status-actions {
  display: flex;
  gap: 1rem;
}

.status-btn {
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  color: white;
  padding: 0.5rem 1rem;
  border-radius: 8px;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.status-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .drawing-workspace,
  .game-workspace {
    flex-direction: column;
  }
  
  .toolbar,
  .info-panel {
    width: 100%;
    border-right: none;
    border-left: none;
    border-bottom: 1px solid #e2e8f0;
  }
}

@media (max-width: 768px) {
  .navbar {
    padding: 1rem;
    flex-direction: column;
    gap: 1rem;
  }
  
  .nav-actions {
    width: 100%;
    justify-content: center;
  }
  
  .main-content {
    padding: 1rem;
  }
  
  .drawing-header,
  .game-header {
    padding: 1.5rem;
  }
  
  .drawing-header h2,
  .game-header h2 {
    font-size: 1.5rem;
  }
  
  .toolbar,
  .info-panel {
    padding: 1.5rem;
  }
  
  .canvas-container {
    padding: 1rem;
  }
  
  .drawing-canvas {
    width: 100%;
    max-width: 300px;
    height: 225px;
  }
  
  .status-bar {
    flex-direction: column;
    gap: 1rem;
    text-align: center;
  }
  
  .status-info {
    flex-direction: column;
    gap: 0.5rem;
  }
}
</style>