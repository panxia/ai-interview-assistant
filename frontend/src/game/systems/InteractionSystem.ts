import * as PIXI from 'pixi.js';
import { Point, Fish, GameState } from '../../types/game';

export class InteractionSystem {
  private stage: PIXI.Container;
  private gameState: GameState;
  private isDragging = false;
  private dragStart: Point = { x: 0, y: 0 };
  private lastDragPosition: Point = { x: 0, y: 0 };
  private dragThreshold = 5; // 拖拽阈值

  // 事件回调
  private onFishClick?: (fish: Fish) => void;
  private onDrawingStart?: (position: Point) => void;
  private onDrawingMove?: (position: Point) => void;
  private onDrawingEnd?: () => void;
  private onCameraMove?: (deltaX: number, deltaY: number) => void;

  constructor(stage: PIXI.Container, gameState: GameState) {
    this.stage = stage;
    this.gameState = gameState;
    this.setupEventListeners();
  }

  private setupEventListeners(): void {
    // 触摸/鼠标事件
    this.stage.eventMode = 'static';
    this.stage.on('pointerdown', this.onPointerDown.bind(this));
    this.stage.on('pointermove', this.onPointerMove.bind(this));
    this.stage.on('pointerup', this.onPointerUp.bind(this));
    this.stage.on('pointerupoutside', this.onPointerUp.bind(this));

    // 触摸事件（移动设备）
    this.stage.on('touchstart', this.onTouchStart.bind(this));
    this.stage.on('touchmove', this.onTouchMove.bind(this));
    this.stage.on('touchend', this.onTouchEnd.bind(this));

    // 键盘事件
    document.addEventListener('keydown', this.onKeyDown.bind(this));
    document.addEventListener('keyup', this.onKeyUp.bind(this));
  }

  private onPointerDown(event: PIXI.FederatedPointerEvent): void {
    const position = event.global;
    this.dragStart = { x: position.x, y: position.y };
    this.lastDragPosition = { x: position.x, y: position.y };
    this.isDragging = false;

    // 检查是否点击到鱼
    const hitFish = this.checkFishCollision(position);
    if (hitFish) {
      this.handleFishClick(hitFish);
      return;
    }

    // 开始绘制或拖拽
    if (this.gameState.isDrawing) {
      this.startDrawing(position);
    } else {
      this.startDragging(position);
    }
  }

  private onPointerMove(event: PIXI.FederatedPointerEvent): void {
    const position = event.global;
    
    if (this.gameState.isDrawing) {
      this.continueDrawing(position);
    } else if (this.isDragging) {
      this.continueDragging(position);
    }
  }

  private onPointerUp(event: PIXI.FederatedPointerEvent): void {
    if (this.gameState.isDrawing) {
      this.endDrawing();
    } else if (this.isDragging) {
      this.endDragging();
    }
  }

  private onTouchStart(event: PIXI.FederatedPointerEvent): void {
    // 触摸开始
    if (event.touches && event.touches.length === 1) {
      const touch = event.touches[0];
      const position = { x: touch.clientX, y: touch.clientY };
      this.onPointerDown({ ...event, global: position } as any);
    }
  }

  private onTouchMove(event: PIXI.FederatedPointerEvent): void {
    // 触摸移动
    if (event.touches && event.touches.length === 1) {
      const touch = event.touches[0];
      const position = { x: touch.clientX, y: touch.clientY };
      this.onPointerMove({ ...event, global: position } as any);
    }
  }

  private onTouchEnd(event: PIXI.FederatedPointerEvent): void {
    // 触摸结束
    this.onPointerUp(event);
  }

  private onKeyDown(event: KeyboardEvent): void {
    switch (event.code) {
      case 'Space':
        event.preventDefault();
        this.handleSpaceKey();
        break;
      case 'Enter':
        event.preventDefault();
        this.handleEnterKey();
        break;
      case 'KeyR':
        if (event.ctrlKey || event.metaKey) {
          event.preventDefault();
          this.handleResetKey();
        }
        break;
      case 'Escape':
        event.preventDefault();
        this.handleEscapeKey();
        break;
    }
  }

  private onKeyUp(event: KeyboardEvent): void {
    // 键盘释放事件处理
  }

  private checkFishCollision(position: PIXI.Point): Fish | null {
    // 检查是否点击到鱼
    // 这里需要遍历鱼群进行碰撞检测
    // 暂时返回null，实际实现中需要完善
    return null;
  }

  private handleFishClick(fish: Fish): void {
    if (this.onFishClick) {
      this.onFishClick(fish);
    }
  }

  private startDrawing(position: Point): void {
    if (this.onDrawingStart) {
      this.onDrawingStart(position);
    }
  }

  private continueDrawing(position: Point): void {
    if (this.onDrawingMove) {
      this.onDrawingMove(position);
    }
  }

  private endDrawing(): void {
    if (this.onDrawingEnd) {
      this.onDrawingEnd();
    }
  }

  private startDragging(position: Point): void {
    this.isDragging = true;
    this.dragStart = { ...position };
    this.lastDragPosition = { ...position };
  }

  private continueDragging(position: Point): void {
    if (!this.isDragging) return;

    const deltaX = position.x - this.lastDragPosition.x;
    const deltaY = position.y - this.lastDragPosition.y;

    // 检查拖拽距离是否超过阈值
    const distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    if (distance > this.dragThreshold) {
      if (this.onCameraMove) {
        this.onCameraMove(deltaX, deltaY);
      }
      this.lastDragPosition = { ...position };
    }
  }

  private endDragging(): void {
    this.isDragging = false;
  }

  private handleSpaceKey(): void {
    // 空格键：清空绘制
    if (this.gameState.isDrawing) {
      this.clearDrawing();
    }
  }

  private handleEnterKey(): void {
    // 回车键：保存鱼
    if (this.gameState.isDrawing) {
      this.saveFish();
    }
  }

  private handleResetKey(): void {
    // Ctrl/Cmd + R：重置视图
    this.resetView();
  }

  private handleEscapeKey(): void {
    // ESC键：退出绘制模式
    if (this.gameState.isDrawing) {
      this.exitDrawingMode();
    }
  }

  private clearDrawing(): void {
    // 清空绘制
    // 这里需要调用绘制系统的清空方法
  }

  private saveFish(): void {
    // 保存鱼
    // 这里需要调用游戏引擎的保存方法
  }

  private resetView(): void {
    // 重置视图
    this.gameState.camera = { x: 0, y: 0, scale: 1 };
  }

  private exitDrawingMode(): void {
    // 退出绘制模式
    this.gameState.isDrawing = false;
  }

  // 公共方法
  public setOnFishClick(callback: (fish: Fish) => void): void {
    this.onFishClick = callback;
  }

  public setOnDrawingStart(callback: (position: Point) => void): void {
    this.onDrawingStart = callback;
  }

  public setOnDrawingMove(callback: (position: Point) => void): void {
    this.onDrawingMove = callback;
  }

  public setOnDrawingEnd(callback: () => void): void {
    this.onDrawingEnd = callback;
  }

  public setOnCameraMove(callback: (deltaX: number, deltaY: number) => void): void {
    this.onCameraMove = callback;
  }

  public updateGameState(newState: Partial<GameState>): void {
    this.gameState = { ...this.gameState, ...newState };
  }

  public isDraggingActive(): boolean {
    return this.isDragging;
  }

  public getDragStart(): Point {
    return { ...this.dragStart };
  }

  public getLastDragPosition(): Point {
    return { ...this.lastDragPosition };
  }

  public setDragThreshold(threshold: number): void {
    this.dragThreshold = threshold;
  }

  public enableDrawingMode(): void {
    this.gameState.isDrawing = true;
  }

  public disableDrawingMode(): void {
    this.gameState.isDrawing = false;
  }

  public destroy(): void {
    // 清理事件监听器
    this.stage.eventMode = 'none';
    this.stage.off('pointerdown');
    this.stage.off('pointermove');
    this.stage.off('pointerup');
    this.stage.off('pointerupoutside');
    this.stage.off('touchstart');
    this.stage.off('touchmove');
    this.stage.off('touchend');

    document.removeEventListener('keydown', this.onKeyDown.bind(this));
    document.removeEventListener('keyup', this.onKeyUp.bind(this));

    // 清理回调
    this.onFishClick = undefined;
    this.onDrawingStart = undefined;
    this.onDrawingMove = undefined;
    this.onDrawingEnd = undefined;
    this.onCameraMove = undefined;
  }
}
