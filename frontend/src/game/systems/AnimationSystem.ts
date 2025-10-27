import { gsap } from 'gsap';
import { Point, Fish, AnimationConfig } from '../../types/game';

export class AnimationSystem {
  private config: AnimationConfig;
  private activeAnimations: Map<string, gsap.core.Timeline> = new Map();

  constructor(config: AnimationConfig) {
    this.config = config;
    this.setupGSAP();
  }

  private setupGSAP(): void {
    // 配置GSAP
    gsap.config({
      nullTargetWarn: false,
      trialWarn: false
    });
  }

  public createSwimAnimation(fish: Fish, targetPosition: Point): gsap.core.Timeline {
    const timeline = gsap.timeline();
    
    // 计算游动距离和时间
    const distance = Math.sqrt(
      Math.pow(targetPosition.x - fish.position.x, 2) + 
      Math.pow(targetPosition.y - fish.position.y, 2)
    );
    
    const duration = distance / fish.speed;
    
    // 游动动画
    timeline.to(fish, {
      position: targetPosition,
      duration: duration,
      ease: "power2.out",
      onUpdate: () => {
        // 更新鱼的方向
        const dx = targetPosition.x - fish.position.x;
        const dy = targetPosition.y - fish.position.y;
        if (Math.abs(dx) > 0.1 || Math.abs(dy) > 0.1) {
          fish.direction = Math.atan2(dy, dx);
        }
      }
    });

    // 摆动动画
    timeline.to(fish, {
      wiggle: fish.wiggle * 1.2,
      duration: duration * 0.5,
      ease: "power1.inOut",
      yoyo: true,
      repeat: 1
    }, 0);

    return timeline;
  }

  public createWiggleAnimation(fish: Fish): gsap.core.Timeline {
    const timeline = gsap.timeline();
    
    // 创建自然的摆动效果
    timeline.to(fish, {
      wiggle: fish.wiggle * 1.5,
      duration: 0.3,
      ease: "power2.out",
      yoyo: true,
      repeat: 1
    });

    return timeline;
  }

  public createDirectionChangeAnimation(fish: Fish, newDirection: number): gsap.core.Timeline {
    const timeline = gsap.timeline();
    
    // 平滑的方向变化
    timeline.to(fish, {
      direction: newDirection,
      duration: 1,
      ease: "power2.out"
    });

    return timeline;
  }

  public createLikeAnimation(fish: Fish): gsap.core.Timeline {
    const timeline = gsap.timeline();
    
    // 点赞时的缩放效果
    timeline.to(fish, {
      scale: fish.scale * 1.1,
      duration: 0.2,
      ease: "back.out(1.7)",
      yoyo: true,
      repeat: 1
    });

    return timeline;
  }

  public createBubbleAnimation(bubble: any, startY: number, endY: number): gsap.core.Timeline {
    const timeline = gsap.timeline();
    
    // 气泡上升动画
    timeline.to(bubble, {
      y: endY,
      duration: 3 + Math.random() * 2,
      ease: "power1.out",
      onComplete: () => {
        // 重置气泡位置
        bubble.y = startY;
        bubble.x = Math.random() * 800; // 临时值，应该从配置获取
      }
    });

    // 气泡的透明度变化
    timeline.to(bubble, {
      alpha: 0.8,
      duration: 1,
      ease: "power1.inOut",
      yoyo: true,
      repeat: -1
    }, 0);

    return timeline;
  }

  public createSeaweedAnimation(seaweed: any): gsap.core.Timeline {
    const timeline = gsap.timeline();
    
    // 海草摆动动画
    const swingRange = 0.1;
    const swingSpeed = 0.02 + Math.random() * 0.03;
    
    timeline.to(seaweed, {
      rotation: swingRange,
      duration: 2 / swingSpeed,
      ease: "power1.inOut",
      yoyo: true,
      repeat: -1
    });

    return timeline;
  }

  public createCameraMoveAnimation(
    camera: { x: number; y: number; scale: number },
    targetX: number,
    targetY: number,
    targetScale: number = 1
  ): gsap.core.Timeline {
    const timeline = gsap.timeline();
    
    // 相机移动动画
    timeline.to(camera, {
      x: targetX,
      y: targetY,
      scale: targetScale,
      duration: 1,
      ease: "power2.out"
    });

    return timeline;
  }

  public createFadeInAnimation(element: any, duration: number = 0.5): gsap.core.Timeline {
    const timeline = gsap.timeline();
    
    // 淡入动画
    timeline.fromTo(element, 
      { alpha: 0, scale: 0.8 },
      { 
        alpha: 1, 
        scale: 1, 
        duration: duration,
        ease: "back.out(1.7)"
      }
    );

    return timeline;
  }

  public createFadeOutAnimation(element: any, duration: number = 0.3): gsap.core.Timeline {
    const timeline = gsap.timeline();
    
    // 淡出动画
    timeline.to(element, {
      alpha: 0,
      scale: 0.8,
      duration: duration,
      ease: "power2.in"
    });

    return timeline;
  }

  public createParticleAnimation(
    particle: any,
    startPosition: Point,
    endPosition: Point,
    options: {
      duration?: number;
      ease?: string;
      onComplete?: () => void;
    } = {}
  ): gsap.core.Timeline {
    const timeline = gsap.timeline();
    
    const duration = options.duration || 1;
    const ease = options.ease || "power2.out";
    
    // 粒子移动动画
    timeline.to(particle, {
      x: endPosition.x,
      y: endPosition.y,
      duration: duration,
      ease: ease,
      onComplete: options.onComplete
    });

    // 粒子缩放和透明度变化
    timeline.to(particle, {
      scale: 0,
      alpha: 0,
      duration: duration * 0.5,
      ease: "power2.in"
    }, duration * 0.5);

    return timeline;
  }

  public createHeartbeatAnimation(element: any): gsap.core.Timeline {
    const timeline = gsap.timeline();
    
    // 心跳动画
    timeline.to(element, {
      scale: 1.2,
      duration: 0.1,
      ease: "power2.out",
      yoyo: true,
      repeat: 1
    });

    return timeline;
  }

  public createShakeAnimation(element: any, intensity: number = 5): gsap.core.Timeline {
    const timeline = gsap.timeline();
    
    // 抖动动画
    timeline.to(element, {
      x: element.x + intensity,
      duration: 0.05,
      ease: "power2.out",
      yoyo: true,
      repeat: 5
    });

    return timeline;
  }

  public playAnimation(id: string, timeline: gsap.core.Timeline): void {
    // 停止之前的动画
    this.stopAnimation(id);
    
    // 播放新动画
    this.activeAnimations.set(id, timeline);
    timeline.play();
  }

  public stopAnimation(id: string): void {
    const animation = this.activeAnimations.get(id);
    if (animation) {
      animation.kill();
      this.activeAnimations.delete(id);
    }
  }

  public stopAllAnimations(): void {
    this.activeAnimations.forEach(animation => {
      animation.kill();
    });
    this.activeAnimations.clear();
  }

  public pauseAnimation(id: string): void {
    const animation = this.activeAnimations.get(id);
    if (animation) {
      animation.pause();
    }
  }

  public resumeAnimation(id: string): void {
    const animation = this.activeAnimations.get(id);
    if (animation) {
      animation.resume();
    }
  }

  public getActiveAnimations(): string[] {
    return Array.from(this.activeAnimations.keys());
  }

  public isAnimationPlaying(id: string): boolean {
    const animation = this.activeAnimations.get(id);
    return animation ? animation.isActive() : false;
  }

  public destroy(): void {
    this.stopAllAnimations();
    this.activeAnimations.clear();
  }
}
