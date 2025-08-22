import * as PIXI from 'pixi.js';
import * as Matter from 'matter-js';
import { gsap } from 'gsap';
import { Fish as FishType, Point } from '../../types/game';

export class Fish {
  private sprite: PIXI.Sprite;
  private body: Matter.Body;
  private data: FishType;
  private animationTimeline: gsap.core.Timeline;
  private isAnimating = false;

  constructor(
    sprite: PIXI.Sprite,
    body: Matter.Body,
    data: FishType
  ) {
    this.sprite = sprite;
    this.body = body;
    this.data = data;
    this.animationTimeline = gsap.timeline();
    
    this.setupAnimations();
  }

  private setupAnimations(): void {
    // 创建游动动画时间轴
    this.animationTimeline
      .to(this.sprite, {
        rotation: this.data.direction + 0.1,
        duration: 2,
        ease: "power1.inOut",
        yoyo: true,
        repeat: -1
      })
      .to(this.sprite, {
        y: this.sprite.y + 5,
        duration: 1.5,
        ease: "power1.inOut",
        yoyo: true,
        repeat: -1
      }, 0);
  }

  public update(deltaTime: number): void {
    // 更新物理位置
    if (this.body) {
      this.data.position = { x: this.body.position.x, y: this.body.position.y };
      this.data.direction = this.body.angle;
    }

    // 更新精灵位置
    this.sprite.position.set(this.data.position.x, this.data.position.y);
    this.sprite.rotation = this.data.direction;

    // 随机改变方向
    if (Math.random() < 0.01) {
      this.changeDirection();
    }

    // 边界检查
    this.checkBoundaries();
  }

  private changeDirection(): void {
    if (this.isAnimating) return;

    this.isAnimating = true;
    const newDirection = this.data.direction + (Math.random() - 0.5) * Math.PI / 2;

    gsap.to(this.data, {
      direction: newDirection,
      duration: 1,
      ease: "power2.out",
      onComplete: () => {
        this.isAnimating = false;
      }
    });
  }

  private checkBoundaries(): void {
    const worldBounds = { width: 800, height: 600 }; // 临时值，应该从配置获取
    const margin = 50;

    let needsUpdate = false;
    let newX = this.data.position.x;
    let newY = this.data.position.y;

    if (this.data.position.x < margin) {
      newX = margin;
      needsUpdate = true;
    } else if (this.data.position.x > worldBounds.width - margin) {
      newX = worldBounds.width - margin;
      needsUpdate = true;
    }

    if (this.data.position.y < margin) {
      newY = margin;
      needsUpdate = true;
    } else if (this.data.position.y > worldBounds.height - margin) {
      newY = worldBounds.height - margin;
      needsUpdate = true;
    }

    if (needsUpdate) {
      this.data.position = { x: newX, y: newY };
      if (this.body) {
        Matter.Body.setPosition(this.body, { x: newX, y: newY });
      }
      
      // 改变方向远离边界
      const centerX = worldBounds.width / 2;
      const centerY = worldBounds.height / 2;
      const angleToCenter = Math.atan2(centerY - newY, centerX - newX);
      
      gsap.to(this.data, {
        direction: angleToCenter,
        duration: 0.5,
        ease: "power2.out"
      });
    }
  }

  public swim(): void {
    // 应用游动力
    if (this.body) {
      const force = {
        x: Math.cos(this.data.direction) * this.data.speed * 0.001,
        y: Math.sin(this.data.direction) * this.data.speed * 0.001
      };
      
      Matter.Body.applyForce(this.body, this.body.position, force);
    }
  }

  public wiggle(): void {
    // 摆动效果
    if (this.isAnimating) return;

    this.isAnimating = true;
    const originalRotation = this.sprite.rotation;
    const wiggleAmount = this.data.wiggle * 0.1;

    gsap.to(this.sprite, {
      rotation: originalRotation + wiggleAmount,
      duration: 0.2,
      ease: "power2.out",
      yoyo: true,
      repeat: 1,
      onComplete: () => {
        this.sprite.rotation = originalRotation;
        this.isAnimating = false;
      }
    });
  }

  public like(): void {
    this.data.likes++;
    this.data.liked = true;
    
    // 点赞动画效果
    this.showLikeAnimation();
  }

  public unlike(): void {
    this.data.likes--;
    this.data.liked = false;
  }

  private showLikeAnimation(): void {
    // 创建爱心粒子效果
    const heart = new PIXI.Graphics();
    heart.beginFill(0xFF69B4);
    heart.drawPolygon([
      -8, 0,   // 左点
      0, -12,  // 上点
      8, 0,    // 右点
      0, 5     // 下点
    ]);
    heart.endFill();
    
    heart.position.set(this.sprite.width / 2, -30);
    this.sprite.addChild(heart);

    // 动画效果
    gsap.fromTo(heart, 
      { 
        alpha: 0, 
        scale: 0.5,
        y: heart.y
      },
      {
        alpha: 1,
        scale: 1.2,
        y: heart.y - 40,
        duration: 0.8,
        ease: "power2.out",
        onComplete: () => {
          this.sprite.removeChild(heart);
        }
      }
    );
  }

  public getData(): FishType {
    return { ...this.data };
  }

  public getSprite(): PIXI.Sprite {
    return this.sprite;
  }

  public getBody(): Matter.Body {
    return this.body;
  }

  public setPosition(x: number, y: number): void {
    this.data.position = { x, y };
    this.sprite.position.set(x, y);
    
    if (this.body) {
      Matter.Body.setPosition(this.body, { x, y });
    }
  }

  public setDirection(direction: number): void {
    this.data.direction = direction;
    this.sprite.rotation = direction;
    
    if (this.body) {
      Matter.Body.setAngle(this.body, direction);
    }
  }

  public setSpeed(speed: number): void {
    this.data.speed = speed;
  }

  public setWiggle(wiggle: number): void {
    this.data.wiggle = wiggle;
  }

  public destroy(): void {
    // 停止动画
    this.animationTimeline.kill();
    
    // 清理精灵
    if (this.sprite.parent) {
      this.sprite.parent.removeChild(this.sprite);
    }
    
    // 清理物理体
    if (this.body) {
      Matter.World.remove(Matter.World.create(), this.body);
    }
  }

  public isVisible(): boolean {
    return this.sprite.visible;
  }

  public setVisible(visible: boolean): void {
    this.sprite.visible = visible;
  }

  public getBounds(): PIXI.Rectangle {
    return this.sprite.getBounds();
  }

  public hitTest(point: Point): boolean {
    const bounds = this.getBounds();
    return bounds.contains(point.x, point.y);
  }
}
