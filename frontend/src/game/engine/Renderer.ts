import * as PIXI from 'pixi.js';
import { Point, Fish, DecorationElement } from '../../types/game';

export class Renderer {
  private app: PIXI.Application;
  private gameContainer!: PIXI.Container;
  private fishContainer!: PIXI.Container;
  private decorationContainer!: PIXI.Container;
  private backgroundContainer!: PIXI.Container;
  private drawingContainer!: PIXI.Container;

  constructor(app: PIXI.Application) {
    this.app = app;
    this.setupContainers();
    this.createBackground();
  }

  private setupContainers(): void {
    // 创建主游戏容器
    this.gameContainer = new PIXI.Container();
    this.app.stage.addChild(this.gameContainer);

    // 创建背景容器
    this.backgroundContainer = new PIXI.Container();
    this.gameContainer.addChild(this.backgroundContainer);

    // 创建装饰容器
    this.decorationContainer = new PIXI.Container();
    this.gameContainer.addChild(this.decorationContainer);

    // 创建鱼群容器
    this.fishContainer = new PIXI.Container();
    this.gameContainer.addChild(this.fishContainer);

    // 创建绘制容器
    this.drawingContainer = new PIXI.Container();
    this.gameContainer.addChild(this.drawingContainer);
  }

  private createBackground(): void {
    // 创建海底背景
    const background = new PIXI.Graphics();
    
    // 渐变背景
    const gradient = new PIXI.Graphics();
    gradient.beginFill(0x006994, 0.8);
    gradient.drawRect(0, 0, this.app.screen.width, this.app.screen.height);
    gradient.endFill();
    
    // 添加一些装饰性元素
    this.createSeaDecorations();
    
    this.backgroundContainer.addChild(gradient);
  }

  private createSeaDecorations(): void {
    // 创建气泡
    for (let i = 0; i < 20; i++) {
      const bubble = this.createBubble();
      this.decorationContainer.addChild(bubble);
    }

    // 创建海草
    for (let i = 0; i < 15; i++) {
      const seaweed = this.createSeaweed();
      this.decorationContainer.addChild(seaweed);
    }

    // 创建珊瑚
    for (let i = 0; i < 8; i++) {
      const coral = this.createCoral();
      this.decorationContainer.addChild(coral);
    }
  }

  private createBubble(): PIXI.Graphics {
    const bubble = new PIXI.Graphics();
    const x = Math.random() * this.app.screen.width;
    const y = this.app.screen.height + Math.random() * 100;
    const size = 5 + Math.random() * 15;
    
    bubble.beginFill(0xFFFFFF, 0.3);
    bubble.drawCircle(0, 0, size);
    bubble.endFill();
    
    bubble.position.set(x, y);
    
    // 添加浮动动画
    this.animateBubble(bubble);
    
    return bubble;
  }

  private createSeaweed(): PIXI.Graphics {
    const seaweed = new PIXI.Graphics();
    const x = Math.random() * this.app.screen.width;
    const height = 30 + Math.random() * 60;
    
    seaweed.lineStyle(3, 0x228B22, 0.8);
    seaweed.moveTo(0, 0);
    seaweed.lineTo(0, -height);
    
    seaweed.position.set(x, this.app.screen.height);
    
    // 添加摆动动画
    this.animateSeaweed(seaweed);
    
    return seaweed;
  }

  private createCoral(): PIXI.Graphics {
    const coral = new PIXI.Graphics();
    const x = Math.random() * this.app.screen.width;
    const height = 40 + Math.random() * 80;
    
    coral.beginFill(0xFF8C00, 0.7);
    coral.drawEllipse(0, 0, 15, height);
    coral.endFill();
    
    coral.position.set(x, this.app.screen.height - height / 2);
    
    return coral;
  }

  private animateBubble(bubble: PIXI.Graphics): void {
    // 气泡上升动画
    const targetY = -50;
    const duration = 3000 + Math.random() * 2000;
    
    const animate = () => {
      bubble.y -= 1;
      if (bubble.y > targetY) {
        requestAnimationFrame(animate);
      } else {
        // 重置气泡位置
        bubble.y = this.app.screen.height + Math.random() * 100;
        bubble.x = Math.random() * this.app.screen.width;
        setTimeout(() => this.animateBubble(bubble), 1000 + Math.random() * 2000);
      }
    };
    
    animate();
  }

  private animateSeaweed(seaweed: PIXI.Graphics): void {
    // 海草摆动动画
    const originalRotation = seaweed.rotation;
    const swingRange = 0.1;
    const swingSpeed = 0.02 + Math.random() * 0.03;
    
    const animate = () => {
      seaweed.rotation = originalRotation + Math.sin(Date.now() * swingSpeed) * swingRange;
      requestAnimationFrame(animate);
    };
    
    animate();
  }

  public createFishSprite(fish: Fish): PIXI.Sprite {
    // 验证鱼的数据完整性
    if (!fish.position || typeof fish.position.x !== 'number' || typeof fish.position.y !== 'number') {
      throw new Error(`鱼数据不完整: fishId=${fish.fishId}, position=${JSON.stringify(fish.position)}`);
    }
    
    if (!fish.imageData) {
      throw new Error(`鱼缺少图像数据: fishId=${fish.fishId}`);
    }
    
    // 从Base64图像数据创建鱼的精灵
    const texture = PIXI.Texture.from(fish.imageData);
    const sprite = new PIXI.Sprite(texture);
    
    // 设置鱼的属性
    sprite.position.set(fish.position.x, fish.position.y);
    sprite.scale.set(fish.scale || 1, fish.scale || 1);
    sprite.rotation = fish.direction || 0;
    
    // 添加点击事件
    sprite.eventMode = 'static';
    sprite.cursor = 'pointer';
    
    // 存储鱼的数据
    (sprite as any).fishData = fish;
    
    return sprite;
  }

  public updateFishSprite(sprite: PIXI.Sprite, fish: Fish): void {
    // 更新鱼的位置和状态
    sprite.position.set(fish.position.x, fish.position.y);
    sprite.rotation = fish.direction;
    sprite.scale.set(fish.scale, fish.scale);
  }

  public addFish(fish: Fish): PIXI.Sprite {
    const sprite = this.createFishSprite(fish);
    this.fishContainer.addChild(sprite);
    return sprite;
  }

  public removeFish(fishId: string): void {
    const fishSprite = this.fishContainer.children.find(
      child => (child as any).fishData?.fishId === fishId
    );
    
    if (fishSprite) {
      this.fishContainer.removeChild(fishSprite);
    }
  }

  public updateFishLikes(fishId: string, likes: number, liked: boolean): void {
    const fishSprite = this.fishContainer.children.find(
      child => (child as any).fishData?.fishId === fishId
    ) as PIXI.Sprite;
    
    if (fishSprite) {
      (fishSprite as any).fishData.likes = likes;
      (fishSprite as any).fishData.liked = liked;
      
      // 添加点赞视觉反馈
      if (liked) {
        this.showLikeEffect(fishSprite);
      }
    }
  }

  private showLikeEffect(sprite: PIXI.Sprite): void {
    // 创建爱心效果
    const heart = new PIXI.Graphics();
    heart.beginFill(0xFF69B4);
    heart.drawPolygon([
      -5, 0,  // 左点
      0, -8,  // 上点
      5, 0,   // 右点
      0, 3    // 下点
    ]);
    heart.endFill();
    
    heart.position.set(sprite.width / 2, -20);
    sprite.addChild(heart);
    
    // 动画效果
    heart.alpha = 0;
    heart.scale.set(0.5);
    
    // 使用GSAP动画（如果可用）
    if (typeof gsap !== 'undefined') {
      gsap.to(heart, {
        alpha: 1,
        scale: 1.2,
        duration: 0.3,
        yoyo: true,
        repeat: 1,
        onComplete: () => {
          sprite.removeChild(heart);
        }
      });
    } else {
      // 备用动画
      setTimeout(() => {
        sprite.removeChild(heart);
      }, 600);
    }
  }

  public clearDrawing(): void {
    this.drawingContainer.removeChildren();
  }

  public addDrawingPoint(point: Point, color: string, size: number): void {
    const graphics = new PIXI.Graphics();
    graphics.beginFill(parseInt(color.replace('#', '0x')));
    graphics.drawCircle(point.x, point.y, size / 2);
    graphics.endFill();
    
    this.drawingContainer.addChild(graphics);
  }

  public setCamera(x: number, y: number, scale: number): void {
    this.gameContainer.position.set(x, y);
    this.gameContainer.scale.set(scale);
  }

  public getDrawingImageData(): string {
    // 将绘制容器转换为图像数据
    const renderTexture = this.app.renderer.generateTexture(this.drawingContainer);
    
    // 检查extract是否可用
    if (!this.app.renderer.extract) {
      console.warn('PixiJS extract不可用，返回空字符串');
      return '';
    }
    
    const canvas = this.app.renderer.extract.canvas(renderTexture);
    return canvas.toDataURL();
  }

  public resize(width: number, height: number): void {
    if (this.app.renderer) {
      this.app.renderer.resize(width, height);
    }
  }
}
