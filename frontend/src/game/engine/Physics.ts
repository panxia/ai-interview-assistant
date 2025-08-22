import * as Matter from 'matter-js';
import { Point, Fish, PhysicsConfig } from '../../types/game';

export class Physics {
  private world: Matter.World;
  private config: PhysicsConfig;
  private fishBodies: Map<string, Matter.Body> = new Map();
  private boundaries: Matter.Body[] = [];

  constructor(world: Matter.World, config: PhysicsConfig) {
    this.world = world;
    this.config = config;
    this.setupBoundaries();
  }

  private setupBoundaries(): void {
    const { x, y, width, height } = this.config.worldBounds;
    
    // 创建边界墙
    const walls = [
      // 上边界
      Matter.Bodies.rectangle(x + width / 2, y, width, 60, { 
        isStatic: true,
        label: 'boundary-top'
      }),
      // 下边界
      Matter.Bodies.rectangle(x + width / 2, y + height, width, 60, { 
        isStatic: true,
        label: 'boundary-bottom'
      }),
      // 左边界
      Matter.Bodies.rectangle(x, y + height / 2, 60, height, { 
        isStatic: true,
        label: 'boundary-left'
      }),
      // 右边界
      Matter.Bodies.rectangle(x + width, y + height / 2, 60, height, { 
        isStatic: true,
        label: 'boundary-right'
      })
    ];

    walls.forEach(wall => {
      Matter.World.add(this.world, wall);
      this.boundaries.push(wall);
    });
  }

  public createFishBody(fish: Fish): Matter.Body {
    // 根据鱼的路径创建物理体
    const vertices = this.createFishVertices(fish.path);
    
    const body = Matter.Bodies.fromVertices(
      fish.position.x,
      fish.position.y,
      [vertices],
      {
        label: `fish-${fish.fishId}`,
        density: 0.001,
        friction: 0.1,
        restitution: 0.6,
        frictionAir: 0.01
      }
    );

    // 存储鱼的身体引用
    this.fishBodies.set(fish.fishId, body);
    
    // 添加到物理世界
    Matter.World.add(this.world, body);
    
    return body;
  }

  private createFishVertices(path: Point[]): Point[] {
    if (path.length < 3) {
      // 如果路径点太少，创建一个简单的圆形
      return this.createCircleVertices(20, 15);
    }

    // 简化路径点，避免过于复杂的物理体
    const simplifiedPath = this.simplifyPath(path, 10);
    
    // 确保路径是凸多边形
    return this.makeConvex(simplifiedPath);
  }

  private createCircleVertices(segments: number, radius: number): Point[] {
    const vertices: Point[] = [];
    for (let i = 0; i < segments; i++) {
      const angle = (i / segments) * Math.PI * 2;
      vertices.push({
        x: Math.cos(angle) * radius,
        y: Math.sin(angle) * radius
      });
    }
    return vertices;
  }

  private simplifyPath(path: Point[], tolerance: number): Point[] {
    if (path.length <= 2) return path;

    const simplified: Point[] = [path[0]];
    
    for (let i = 1; i < path.length - 1; i++) {
      const prev = path[i - 1];
      const curr = path[i];
      const next = path[i + 1];
      
      // 计算点到线段的距离
      const distance = this.pointToLineDistance(curr, prev, next);
      
      if (distance > tolerance) {
        simplified.push(curr);
      }
    }
    
    simplified.push(path[path.length - 1]);
    return simplified;
  }

  private pointToLineDistance(point: Point, lineStart: Point, lineEnd: Point): number {
    const A = point.x - lineStart.x;
    const B = point.y - lineStart.y;
    const C = lineEnd.x - lineStart.x;
    const D = lineEnd.y - lineStart.y;

    const dot = A * C + B * D;
    const lenSq = C * C + D * D;
    
    if (lenSq === 0) return Math.sqrt(A * A + B * B);
    
    const param = dot / lenSq;
    
    let xx, yy;
    if (param < 0) {
      xx = lineStart.x;
      yy = lineStart.y;
    } else if (param > 1) {
      xx = lineEnd.x;
      yy = lineEnd.y;
    } else {
      xx = lineStart.x + param * C;
      yy = lineStart.y + param * D;
    }

    const dx = point.x - xx;
    const dy = point.y - yy;
    return Math.sqrt(dx * dx + dy * dy);
  }

  private makeConvex(vertices: Point[]): Point[] {
    if (vertices.length < 3) return vertices;

    // 使用Graham扫描算法创建凸包
    const sorted = [...vertices].sort((a, b) => {
      if (a.y !== b.y) return a.y - b.y;
      return a.x - b.x;
    });

    const cross = (o: Point, a: Point, b: Point) => {
      return (a.x - o.x) * (b.y - o.y) - (a.y - o.y) * (b.x - o.x);
    };

    const hull: Point[] = [];
    
    // 构建下半部分
    for (let i = 0; i < sorted.length; i++) {
      while (hull.length >= 2 && cross(hull[hull.length - 2], hull[hull.length - 1], sorted[i]) <= 0) {
        hull.pop();
      }
      hull.push(sorted[i]);
    }

    // 构建上半部分
    const t = hull.length + 1;
    for (let i = sorted.length - 2; i >= 0; i--) {
      while (hull.length >= t && cross(hull[hull.length - 2], hull[hull.length - 1], sorted[i]) <= 0) {
        hull.pop();
      }
      hull.push(sorted[i]);
    }

    return hull.slice(0, -1);
  }

  public updateFishPhysics(fish: Fish, deltaTime: number): void {
    const body = this.fishBodies.get(fish.fishId);
    if (!body) return;

    // 应用游动力
    const force = this.calculateSwimForce(fish, deltaTime);
    Matter.Body.applyForce(body, body.position, force);

    // 更新鱼的位置和方向
    fish.position = { x: body.position.x, y: body.position.y };
    fish.direction = body.angle;

    // 边界检查
    this.checkBoundaries(fish, body);
  }

  private calculateSwimForce(fish: Fish, deltaTime: number): Matter.Vector {
    // 计算游动力
    const speed = fish.speed;
    const direction = fish.direction;
    
    // 基础游动力
    const baseForce = {
      x: Math.cos(direction) * speed * 0.001,
      y: Math.sin(direction) * speed * 0.001
    };

    // 添加摆动效果
    const wiggleForce = {
      x: Math.sin(Date.now() * 0.01) * fish.wiggle * 0.0005,
      y: Math.cos(Date.now() * 0.01) * fish.wiggle * 0.0005
    };

    return {
      x: baseForce.x + wiggleForce.x,
      y: baseForce.y + wiggleForce.y
    };
  }

  private checkBoundaries(fish: Fish, body: Matter.Body): void {
    const { x, y, width, height } = this.config.worldBounds;
    const margin = 50;

    let needsUpdate = false;
    let newX = body.position.x;
    let newY = body.position.y;

    // 检查边界
    if (body.position.x < x + margin) {
      newX = x + margin;
      needsUpdate = true;
    } else if (body.position.x > x + width - margin) {
      newX = x + width - margin;
      needsUpdate = true;
    }

    if (body.position.y < y + margin) {
      newY = y + margin;
      needsUpdate = true;
    } else if (body.position.y > y + height - margin) {
      newY = y + height - margin;
      needsUpdate = true;
    }

    if (needsUpdate) {
      Matter.Body.setPosition(body, { x: newX, y: newY });
      
      // 改变游动方向
      fish.direction += (Math.random() - 0.5) * Math.PI;
    }
  }

  public removeFishBody(fishId: string): void {
    const body = this.fishBodies.get(fishId);
    if (body) {
      Matter.World.remove(this.world, body);
      this.fishBodies.delete(fishId);
    }
  }

  public getFishBody(fishId: string): Matter.Body | undefined {
    return this.fishBodies.get(fishId);
  }

  public applyImpulse(fishId: string, impulse: Matter.Vector): void {
    const body = this.fishBodies.get(fishId);
    if (body) {
      Matter.Body.applyForce(body, body.position, impulse);
    }
  }

  public setFishVelocity(fishId: string, velocity: Matter.Vector): void {
    const body = this.fishBodies.get(fishId);
    if (body) {
      Matter.Body.setVelocity(body, velocity);
    }
  }

  public getWorldBounds(): { x: number; y: number; width: number; height: number } {
    return { ...this.config.worldBounds };
  }

  public updateGravity(gravity: { x: number; y: number }): void {
    this.world.gravity.x = gravity.x;
    this.world.gravity.y = gravity.y;
  }

  public clear(): void {
    // 清理所有鱼的身体
    this.fishBodies.forEach(body => {
      Matter.World.remove(this.world, body);
    });
    this.fishBodies.clear();
  }
}
