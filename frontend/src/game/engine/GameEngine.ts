import * as PIXI from 'pixi.js';
import * as Matter from 'matter-js';
import { gsap } from 'gsap';
import { GameConfig, GameState, GameEvents, PhysicsConfig, AnimationConfig } from '../../types/game';

export class GameEngine {
  private app: PIXI.Application;
  private engine: Matter.Engine;
  private world: Matter.World;
  private gameState: GameState;
  private events: GameEvents;
  private isInitialized = false;

  // 游戏配置
  private config: GameConfig;
  private physicsConfig: PhysicsConfig;
  private animationConfig: AnimationConfig;

  constructor(
    container: HTMLElement,
    config: GameConfig,
    physicsConfig: PhysicsConfig,
    animationConfig: AnimationConfig,
    events: GameEvents
  ) {
    this.config = config;
    this.physicsConfig = physicsConfig;
    this.animationConfig = animationConfig;
    this.events = events;

    // 初始化游戏状态
    this.gameState = {
      isDrawing: false,
      isPlaying: false,
      selectedColor: '#FF6B6B',
      selectedBrushSize: 3,
      fishes: [],
      decorations: [],
      camera: { x: 0, y: 0, scale: 1 }
    };

    // 初始化PixiJS应用 (使用新的v8 API)
    this.app = new PIXI.Application();
    
    // 初始化Matter.js物理引擎
    this.engine = Matter.Engine.create({
      gravity: physicsConfig.gravity
    });
    this.world = this.engine.world;

    // 验证物理引擎初始化
    if (!this.engine || !this.world) {
      throw new Error('Matter.js物理引擎初始化失败');
    }

    // 设置世界边界
    this.setupWorldBounds();

    // 设置事件监听
    this.setupEventListeners();

    // 启动游戏循环
    this.startGameLoop();
  }

  // 异步初始化方法
  public async initialize(container: HTMLElement): Promise<void> {
    try {
      // 等待PixiJS应用初始化
      await this.app.init({
        width: this.config.width,
        height: this.config.height,
        backgroundColor: this.config.backgroundColor,
        antialias: this.config.antialias,
        resolution: this.config.resolution,
        autoDensity: this.config.autoDensity,
      });

      // 验证PixiJS应用初始化
      if (!this.app || !this.app.canvas) {
        throw new Error('PixiJS应用初始化失败');
      }

      // 添加到容器
      container.appendChild(this.app.canvas);

      console.log('PixiJS应用初始化成功');
    } catch (error) {
      console.error('PixiJS应用初始化失败:', error);
      throw error;
    }
  }

  private setupWorldBounds(): void {
    const { x, y, width, height } = this.physicsConfig.worldBounds;
    
    // 创建边界墙
    const walls = [
      Matter.Bodies.rectangle(x + width / 2, y, width, 60, { isStatic: true }), // 上
      Matter.Bodies.rectangle(x + width / 2, y + height, width, 60, { isStatic: true }), // 下
      Matter.Bodies.rectangle(x, y + height / 2, 60, height, { isStatic: true }), // 左
      Matter.Bodies.rectangle(x + width, y + height / 2, 60, height, { isStatic: true }) // 右
    ];

    walls.forEach(wall => {
      Matter.World.add(this.world, wall);
    });
  }

  private setupEventListeners(): void {
    // 触摸/鼠标事件
    this.app.stage.eventMode = 'static';
    this.app.stage.on('pointerdown', this.onPointerDown.bind(this));
    this.app.stage.on('pointermove', this.onPointerMove.bind(this));
    this.app.stage.on('pointerup', this.onPointerUp.bind(this));

    // 键盘事件
    document.addEventListener('keydown', this.onKeyDown.bind(this));
  }

  private onPointerDown(event: PIXI.FederatedPointerEvent): void {
    // 处理点击事件
    const position = event.global;
    this.handleClick(position);
  }

  private onPointerMove(event: PIXI.FederatedPointerEvent): void {
    // 处理拖拽事件
    if (event.buttons === 1) {
      this.handleDrag(event.global);
    }
  }

  private onPointerUp(event: PIXI.FederatedPointerEvent): void {
    // 处理释放事件
    this.handlePointerUp();
  }

  private onKeyDown(event: KeyboardEvent): void {
    switch (event.code) {
      case 'Space':
        event.preventDefault();
        this.clearDrawing();
        break;
      case 'Enter':
        event.preventDefault();
        this.saveFish();
        break;
      case 'KeyR':
        if (event.ctrlKey || event.metaKey) {
          event.preventDefault();
          this.resetView();
        }
        break;
    }
  }

  private handleClick(position: PIXI.Point): void {
    // 检查是否点击到鱼
    const hitFish = this.checkFishCollision(position);
    if (hitFish) {
      this.toggleFishLike(hitFish);
    }
  }

  private handleDrag(position: PIXI.Point): void {
    if (this.gameState.isDrawing) {
      // 绘制模式下的拖拽
      this.addDrawingPoint(position);
    } else {
      // 移动视角
      this.moveCamera(position);
    }
  }

  private handlePointerUp(): void {
    // 处理指针释放
    if (this.gameState.isDrawing) {
      this.finishDrawing();
    }
  }

  private checkFishCollision(position: PIXI.Point): any {
    // 检查鱼群碰撞
    return null; // 待实现
  }

  private toggleFishLike(fish: any): void {
    // 切换鱼的点赞状态
    fish.liked = !fish.liked;
    fish.likes += fish.liked ? 1 : -1;
    
    // 触发事件
    this.events.onFishLiked(fish.fishId, fish.liked);
  }

  private addDrawingPoint(position: PIXI.Point): void {
    // 添加绘制点
    // 待实现
  }

  private moveCamera(position: PIXI.Point): void {
    // 移动相机
    // 待实现
  }

  private finishDrawing(): void {
    // 完成绘制
    this.gameState.isDrawing = false;
    this.events.onDrawingFinished();
  }

  private clearDrawing(): void {
    // 清空绘制
    // 待实现
  }

  private saveFish(): void {
    // 保存鱼
    // 待实现
  }

  private resetView(): void {
    // 重置视图
    this.gameState.camera = { x: 0, y: 0, scale: 1 };
    this.events.onGameStateChanged({ camera: this.gameState.camera });
  }

  private startGameLoop(): void {
    // 启动游戏循环
    const gameLoop = () => {
      // 更新物理世界
      Matter.Engine.update(this.engine, 1000 / 60);
      
      // 更新游戏状态
      this.updateGameState();
      
      // 继续循环
      requestAnimationFrame(gameLoop);
    };
    
    gameLoop();
  }

  private updateGameState(): void {
    // 更新游戏状态
    // 待实现
  }

  // 公共方法
  public getApp(): PIXI.Application {
    return this.app;
  }

  public getWorld(): Matter.World {
    return this.world;
  }

  public getGameState(): GameState {
    return { ...this.gameState };
  }

  public setGameState(newState: Partial<GameState>): void {
    this.gameState = { ...this.gameState, ...newState };
    this.events.onGameStateChanged(newState);
  }

  public destroy(): void {
    // 清理资源
    this.app.destroy(true);
    Matter.World.clear(this.world, false);
  }
}
