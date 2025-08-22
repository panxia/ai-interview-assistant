import * as PIXI from 'pixi.js';
import { Point, DrawingConfig } from '../../types/game';

export class DrawingSystem {
  private container: PIXI.Container;
  private config: DrawingConfig;
  private isDrawing = false;
  private currentPath: Point[] = [];
  private currentGraphics: PIXI.Graphics | null = null;
  private selectedColor: string;
  private selectedBrushSize: number;

  constructor(container: PIXI.Container, config: DrawingConfig) {
    this.container = container;
    this.config = config;
    this.selectedColor = config.brushColors[0];
    this.selectedBrushSize = config.brushSizes[0];
  }

  public startDrawing(x: number, y: number): void {
    this.isDrawing = true;
    this.currentPath = [{ x, y }];
    
    // 创建新的图形对象
    this.currentGraphics = new PIXI.Graphics();
    this.currentGraphics.lineStyle(this.selectedBrushSize, this.parseColor(this.selectedColor), 1);
    this.currentGraphics.moveTo(x, y);
    
    this.container.addChild(this.currentGraphics);
  }

  public continueDrawing(x: number, y: number): void {
    if (!this.isDrawing || !this.currentGraphics) return;

    this.currentPath.push({ x, y });
    
    // 绘制线条
    this.currentGraphics.lineTo(x, y);
    
    // 添加一些平滑效果
    if (this.currentPath.length > 2) {
      const lastPoint = this.currentPath[this.currentPath.length - 1];
      const prevPoint = this.currentPath[this.currentPath.length - 2];
      
      // 创建平滑的曲线
      const midX = (prevPoint.x + lastPoint.x) / 2;
      const midY = (prevPoint.y + lastPoint.y) / 2;
      
      this.currentGraphics.quadraticCurveTo(prevPoint.x, prevPoint.y, midX, midY);
    }
  }

  public stopDrawing(): void {
    if (!this.isDrawing) return;
    
    this.isDrawing = false;
    this.currentGraphics = null;
  }

  public clearDrawing(): void {
    this.container.removeChildren();
    this.currentPath = [];
    this.currentGraphics = null;
  }

  public setColor(color: string): void {
    this.selectedColor = color;
  }

  public setBrushSize(size: number): void {
    this.selectedBrushSize = size;
  }

  public getCurrentPath(): Point[] {
    return [...this.currentPath];
  }

  public getDrawingImageData(): string {
    // 将绘制容器转换为图像数据
    if (this.container.children.length === 0) {
      return '';
    }

    // 创建临时画布来获取图像数据
    const tempCanvas = document.createElement('canvas');
    tempCanvas.width = this.config.canvasWidth;
    tempCanvas.height = this.config.canvasHeight;
    
    const tempContext = tempCanvas.getContext('2d');
    if (!tempContext) return '';

    // 设置背景色
    tempContext.fillStyle = '#FFFFFF';
    tempContext.fillRect(0, 0, tempCanvas.width, tempCanvas.height);

    // 将PixiJS图形转换为Canvas路径
    this.container.children.forEach(child => {
      if (child instanceof PIXI.Graphics) {
        this.renderGraphicsToCanvas(child, tempContext);
      }
    });

    return tempCanvas.toDataURL();
  }

  private renderGraphicsToCanvas(graphics: PIXI.Graphics, context: CanvasRenderingContext2D): void {
    // 这是一个简化的实现，实际项目中可能需要更复杂的转换逻辑
    const bounds = graphics.getBounds();
    
    context.save();
    context.translate(bounds.x, bounds.y);
    
    // 设置绘制样式
    context.strokeStyle = this.selectedColor;
    context.lineWidth = this.selectedBrushSize;
    context.lineCap = 'round';
    context.lineJoin = 'round';
    
    // 绘制路径
    if (this.currentPath.length > 1) {
      context.beginPath();
      context.moveTo(this.currentPath[0].x, this.currentPath[0].y);
      
      for (let i = 1; i < this.currentPath.length; i++) {
        context.lineTo(this.currentPath[i].x, this.currentPath[i].y);
      }
      
      context.stroke();
    }
    
    context.restore();
  }

  private parseColor(color: string): number {
    // 将CSS颜色转换为PixiJS颜色
    if (color.startsWith('#')) {
      return parseInt(color.replace('#', '0x'));
    }
    
    // 处理其他颜色格式
    const canvas = document.createElement('canvas');
    const context = canvas.getContext('2d');
    if (context) {
      context.fillStyle = color;
      const computedColor = context.fillStyle;
      if (computedColor.startsWith('#')) {
        return parseInt(computedColor.replace('#', '0x'));
      }
    }
    
    return 0xFF0000; // 默认红色
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
      
      // 重新绘制
      this.redraw();
    }
  }

  private redraw(): void {
    if (this.currentGraphics) {
      this.container.removeChild(this.currentGraphics);
    }
    
    if (this.currentPath.length > 0) {
      this.currentGraphics = new PIXI.Graphics();
      this.currentGraphics.lineStyle(this.selectedBrushSize, this.parseColor(this.selectedColor), 1);
      
      this.currentGraphics.moveTo(this.currentPath[0].x, this.currentPath[0].y);
      
      for (let i = 1; i < this.currentPath.length; i++) {
        this.currentGraphics.lineTo(this.currentPath[i].x, this.currentPath[i].y);
      }
      
      this.container.addChild(this.currentGraphics);
    }
  }

  public resize(width: number, height: number): void {
    this.config.canvasWidth = width;
    this.config.canvasHeight = height;
  }

  public destroy(): void {
    this.clearDrawing();
    this.container = null as any;
  }
}
