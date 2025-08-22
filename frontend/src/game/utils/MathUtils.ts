import { Point } from '../../types/game';

export class MathUtils {
  /**
   * 计算两点之间的距离
   */
  static distance(p1: Point, p2: Point): number {
    const dx = p2.x - p1.x;
    const dy = p2.y - p1.y;
    return Math.sqrt(dx * dx + dy * dy);
  }

  /**
   * 计算点到直线的距离
   */
  static pointToLineDistance(point: Point, lineStart: Point, lineEnd: Point): number {
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

  /**
   * 计算两个向量之间的角度
   */
  static angleBetweenVectors(v1: Point, v2: Point): number {
    const dot = v1.x * v2.x + v1.y * v2.y;
    const det = v1.x * v2.y - v1.y * v2.x;
    return Math.atan2(det, dot);
  }

  /**
   * 将角度限制在 -π 到 π 之间
   */
  static normalizeAngle(angle: number): number {
    while (angle > Math.PI) angle -= 2 * Math.PI;
    while (angle < -Math.PI) angle += 2 * Math.PI;
    return angle;
  }

  /**
   * 线性插值
   */
  static lerp(start: number, end: number, t: number): number {
    return start + (end - start) * t;
  }

  /**
   * 点插值
   */
  static lerpPoint(start: Point, end: Point, t: number): Point {
    return {
      x: this.lerp(start.x, end.x, t),
      y: this.lerp(start.y, end.y, t)
    };
  }

  /**
   * 平滑插值（使用缓动函数）
   */
  static smoothLerp(start: number, end: number, t: number): number {
    const smoothT = t * t * (3 - 2 * t); // 平滑步进函数
    return this.lerp(start, end, smoothT);
  }

  /**
   * 将值限制在指定范围内
   */
  static clamp(value: number, min: number, max: number): number {
    return Math.min(Math.max(value, min), max);
  }

  /**
   * 将值映射到新的范围
   */
  static map(value: number, fromMin: number, fromMax: number, toMin: number, toMax: number): number {
    return toMin + (value - fromMin) * (toMax - toMin) / (fromMax - fromMin);
  }

  /**
   * 生成指定范围内的随机数
   */
  static random(min: number, max: number): number {
    return Math.random() * (max - min) + min;
  }

  /**
   * 生成指定范围内的随机整数
   */
  static randomInt(min: number, max: number): number {
    return Math.floor(this.random(min, max + 1));
  }

  /**
   * 生成随机点
   */
  static randomPoint(minX: number, maxX: number, minY: number, maxY: number): Point {
    return {
      x: this.random(minX, maxX),
      y: this.random(minY, maxY)
    };
  }

  /**
   * 生成随机方向（弧度）
   */
  static randomDirection(): number {
    return this.random(0, 2 * Math.PI);
  }

  /**
   * 计算多边形的面积
   */
  static polygonArea(points: Point[]): number {
    if (points.length < 3) return 0;
    
    let area = 0;
    for (let i = 0; i < points.length; i++) {
      const j = (i + 1) % points.length;
      area += points[i].x * points[j].y;
      area -= points[j].x * points[i].y;
    }
    
    return Math.abs(area) / 2;
  }

  /**
   * 计算多边形的重心
   */
  static polygonCentroid(points: Point[]): Point {
    if (points.length === 0) return { x: 0, y: 0 };
    
    let cx = 0, cy = 0;
    const area = this.polygonArea(points);
    
    if (area === 0) {
      // 如果面积为0，返回第一个点
      return { ...points[0] };
    }
    
    for (let i = 0; i < points.length; i++) {
      const j = (i + 1) % points.length;
      const factor = (points[i].x * points[j].y - points[j].x * points[i].y);
      cx += (points[i].x + points[j].x) * factor;
      cy += (points[i].y + points[j].y) * factor;
    }
    
    cx /= (6 * area);
    cy /= (6 * area);
    
    return { x: cx, y: cy };
  }

  /**
   * 检查点是否在多边形内
   */
  static pointInPolygon(point: Point, polygon: Point[]): boolean {
    let inside = false;
    
    for (let i = 0, j = polygon.length - 1; i < polygon.length; j = i++) {
      if (((polygon[i].y > point.y) !== (polygon[j].y > point.y)) &&
          (point.x < (polygon[j].x - polygon[i].x) * (point.y - polygon[i].y) / (polygon[j].y - polygon[i].y) + polygon[i].x)) {
        inside = !inside;
      }
    }
    
    return inside;
  }

  /**
   * 计算贝塞尔曲线上的点
   */
  static bezierPoint(p0: Point, p1: Point, p2: Point, p3: Point, t: number): Point {
    const u = 1 - t;
    const tt = t * t;
    const uu = u * u;
    const uuu = uu * u;
    const ttt = tt * t;
    
    const x = uuu * p0.x + 3 * uu * t * p1.x + 3 * u * tt * p2.x + ttt * p3.x;
    const y = uuu * p0.y + 3 * uu * t * p1.y + 3 * u * tt * p2.y + ttt * p3.y;
    
    return { x, y };
  }

  /**
   * 生成平滑的路径
   */
  static smoothPath(points: Point[], tension: number = 0.5): Point[] {
    if (points.length < 3) return points;
    
    const smoothed: Point[] = [];
    
    for (let i = 0; i < points.length; i++) {
      const prev = points[Math.max(0, i - 1)];
      const curr = points[i];
      const next = points[Math.min(points.length - 1, i + 1)];
      
      if (i === 0 || i === points.length - 1) {
        smoothed.push({ ...curr });
      } else {
        const cp1 = {
          x: curr.x - (next.x - prev.x) * tension * 0.5,
          y: curr.y - (next.y - prev.y) * tension * 0.5
        };
        
        const cp2 = {
          x: curr.x + (next.x - prev.x) * tension * 0.5,
          y: curr.y + (next.y - prev.y) * tension * 0.5
        };
        
        // 添加控制点之间的插值点
        for (let t = 0; t <= 1; t += 0.1) {
          const p = this.bezierPoint(prev, cp1, cp2, next, t);
          smoothed.push(p);
        }
      }
    }
    
    return smoothed;
  }
}
