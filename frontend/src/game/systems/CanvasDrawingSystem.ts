import { Point, DrawingConfig } from '../../types/game';

export class CanvasDrawingSystem {
  private canvas: HTMLCanvasElement;
  private context: CanvasRenderingContext2D;
  private config: DrawingConfig;
  private isDrawing = false;
  private currentPath: Point[] = [];
  private selectedColor: string;
  private selectedBrushSize: number;
  private onDrawingStart?: () => void;

  constructor(container: HTMLElement, config: DrawingConfig) {
    this.config = config;
    this.selectedColor = config.brushColors[0];
    this.selectedBrushSize = config.brushSizes[0];
    
    // 创建Canvas元素
    this.canvas = document.createElement('canvas');
    this.canvas.width = config.canvasWidth;
    this.canvas.height = config.canvasHeight;
    this.canvas.style.border = '2px solid #e2e8f0';
    this.canvas.style.borderRadius = '12px';
    this.canvas.style.cursor = 'crosshair';
    this.canvas.style.display = 'block';
    this.canvas.style.pointerEvents = 'auto';
    this.canvas.style.userSelect = 'none';
    this.canvas.style.touchAction = 'none';
    
    // 获取2D上下文
    const ctx = this.canvas.getContext('2d');
    if (!ctx) {
      throw new Error('无法获取Canvas 2D上下文');
    }
    this.context = ctx;
    
    // 设置Canvas样式
    this.context.fillStyle = '#FFFFFF';
    this.context.fillRect(0, 0, this.canvas.width, this.canvas.height);
    
    // 添加到容器
    container.appendChild(this.canvas);
    
    // 设置事件监听
    this.setupEventListeners();
    
    // 验证Canvas设置
    console.log('Canvas初始化完成:', {
      width: this.canvas.width,
      height: this.canvas.height,
      container: container,
      canvas: this.canvas
    });
  }

  private setupEventListeners(): void {
    console.log('设置Canvas事件监听器...');
    
    // 鼠标事件
    this.canvas.addEventListener('mousedown', this.onMouseDown.bind(this));
    this.canvas.addEventListener('mouseup', this.onMouseUp.bind(this));
    this.canvas.addEventListener('mouseleave', this.onMouseUp.bind(this));
    
    // 触摸事件
    this.canvas.addEventListener('touchstart', this.onTouchStart.bind(this));
    this.canvas.addEventListener('touchmove', this.onTouchMove.bind(this));
    this.canvas.addEventListener('touchend', this.onTouchEnd.bind(this));
    
    // 添加鼠标进入和离开事件用于调试
    this.canvas.addEventListener('mouseenter', (event) => {
      console.log('🖱️ 鼠标进入画布', { 
        canvasRect: this.canvas.getBoundingClientRect(),
        style: this.canvas.style.cssText 
      });
    });
    
    this.canvas.addEventListener('mouseleave', () => {
      console.log('🖱️ 鼠标离开画布');
    });
    
    // 添加一个通用鼠标移动监听器（无论是否在绘制）
    this.canvas.addEventListener('mousemove', (event) => {
      const rect = this.canvas.getBoundingClientRect();
      const x = event.clientX - rect.left;
      const y = event.clientY - rect.top;
      
      // 每隔一段时间输出一次鼠标位置（减少日志量）
      if (Date.now() % 500 < 16) { // 大约每500ms输出一次
        console.log('🖱️ 鼠标移动:', { x, y, isDrawing: this.isDrawing });
      }
      
      // 调用原始的鼠标移动处理逻辑
      this.onMouseMove(event);
    });
    
    console.log('Canvas事件监听器设置完成');
  }

  private onMouseDown(event: MouseEvent): void {
    console.log('鼠标按下事件触发');
    const rect = this.canvas.getBoundingClientRect();
    const x = event.clientX - rect.left;
    const y = event.clientY - rect.top;
    console.log('鼠标按下坐标:', { x, y, clientX: event.clientX, clientY: event.clientY, rect });
    this.startDrawing(x, y);
  }

  private onMouseMove(event: MouseEvent): void {
    // 只有在绘制状态下才继续绘制
    if (!this.isDrawing) return;
    
    const rect = this.canvas.getBoundingClientRect();
    const x = event.clientX - rect.left;
    const y = event.clientY - rect.top;
    
    // 确保坐标在画布范围内
    if (x >= 0 && x <= this.canvas.width && y >= 0 && y <= this.canvas.height) {
      this.continueDrawing(x, y);
    }
  }

  private onMouseUp(event: MouseEvent): void {
    this.stopDrawing();
  }

  private onTouchStart(event: TouchEvent): void {
    event.preventDefault();
    if (event.touches.length === 1) {
      const touch = event.touches[0];
      const rect = this.canvas.getBoundingClientRect();
      const x = touch.clientX - rect.left;
      const y = touch.clientY - rect.top;
      this.startDrawing(x, y);
    }
  }

  private onTouchMove(event: TouchEvent): void {
    event.preventDefault();
    if (!this.isDrawing || event.touches.length !== 1) return;
    
    const touch = event.touches[0];
    const rect = this.canvas.getBoundingClientRect();
    const x = touch.clientX - rect.left;
    const y = touch.clientY - rect.top;
    
    // 确保坐标在画布范围内
    if (x >= 0 && x <= this.canvas.width && y >= 0 && y <= this.canvas.height) {
      this.continueDrawing(x, y);
    }
  }

  private onTouchEnd(event: TouchEvent): void {
    event.preventDefault();
    this.stopDrawing();
  }

  public startDrawing(x: number, y: number): void {
    console.log('开始绘制:', { x, y, color: this.selectedColor, size: this.selectedBrushSize });
    
    this.isDrawing = true;
    this.currentPath = [{ x, y }];
    
    // 触发绘制开始回调
    if (this.onDrawingStart) {
      this.onDrawingStart();
    }
    
    // 设置绘制样式
    this.context.strokeStyle = this.selectedColor;
    this.context.lineWidth = this.selectedBrushSize;
    this.context.lineCap = 'round';
    this.context.lineJoin = 'round';
    
    // 开始路径
    this.context.beginPath();
    this.context.moveTo(x, y);
  }

  public continueDrawing(x: number, y: number): void {
    if (!this.isDrawing) return;
    
    this.currentPath.push({ x, y });
    
    // 绘制线条
    this.context.lineTo(x, y);
    this.context.stroke();
    
    // 调试信息（可选，生产环境可以移除）
    if (this.currentPath.length % 10 === 0) { // 每10个点记录一次
      console.log('继续绘制:', { x, y, pathLength: this.currentPath.length });
    }
  }

  public stopDrawing(): void {
    if (!this.isDrawing) return;
    
    this.isDrawing = false;
    this.context.closePath();
  }

  public clearDrawing(): void {
    // 清空画布
    this.context.fillStyle = '#FFFFFF';
    this.context.fillRect(0, 0, this.canvas.width, this.canvas.height);
    
    // 重置路径
    this.currentPath = [];
    this.context.closePath();
  }

  public setColor(color: string): void {
    this.selectedColor = color;
  }

  public setBrushSize(size: number): number {
    this.selectedBrushSize = size;
    return size;
  }

  public getCurrentPath(): Point[] {
    return [...this.currentPath];
  }

  public getDrawingImageData(): string {
    return this.canvas.toDataURL('image/png');
  }

  public addDrawingPoint(point: Point): void {
    if (this.isDrawing) {
      this.continueDrawing(point.x, point.y);
    }
  }

  public isDrawingActive(): boolean {
    return this.isDrawing;
  }

  public getSelectedColor(): string {
    return this.selectedColor;
  }

  public getSelectedBrushSize(): number {
    return this.selectedBrushSize;
  }

  public getAvailableColors(): string[] {
    return [...this.config.brushColors];
  }

  public getAvailableBrushSizes(): number[] {
    return [...this.config.brushSizes];
  }

  public undo(): void {
    if (this.currentPath.length > 0) {
      this.currentPath.pop();
      this.redraw();
    }
  }

  private redraw(): void {
    // 清空画布
    this.context.fillStyle = '#FFFFFF';
    this.context.fillRect(0, 0, this.canvas.width, this.canvas.height);
    
    // 重新绘制路径
    if (this.currentPath.length > 0) {
      this.context.strokeStyle = this.selectedColor;
      this.context.lineWidth = this.selectedBrushSize;
      this.context.lineCap = 'round';
      this.context.lineJoin = 'round';
      
      this.context.beginPath();
      this.context.moveTo(this.currentPath[0].x, this.currentPath[0].y);
      
      for (let i = 1; i < this.currentPath.length; i++) {
        this.context.lineTo(this.currentPath[i].x, this.currentPath[i].y);
      }
      
      this.context.stroke();
      this.context.closePath();
    }
  }

  public resize(width: number, height: number): void {
    this.config.canvasWidth = width;
    this.config.canvasHeight = height;
    this.canvas.width = width;
    this.canvas.height = height;
    
    // 重新设置背景
    this.context.fillStyle = '#FFFFFF';
    this.context.fillRect(0, 0, width, height);
    
    // 重新绘制路径
    this.redraw();
  }

  public setOnDrawingStart(callback: () => void): void {
    this.onDrawingStart = callback;
  }

  public destroy(): void {
    // 移除事件监听器
    this.canvas.removeEventListener('mousedown', this.onMouseDown.bind(this));
    this.canvas.removeEventListener('mousemove', this.onMouseMove.bind(this));
    this.canvas.removeEventListener('mouseup', this.onMouseUp.bind(this));
    this.canvas.removeEventListener('mouseleave', this.onMouseUp.bind(this));
    this.canvas.removeEventListener('touchstart', this.onTouchStart.bind(this));
    this.canvas.removeEventListener('touchmove', this.onTouchMove.bind(this));
    this.canvas.removeEventListener('touchend', this.onTouchEnd.bind(this));
    
    // 移除Canvas元素
    if (this.canvas.parentNode) {
      this.canvas.parentNode.removeChild(this.canvas);
    }
  }
}
