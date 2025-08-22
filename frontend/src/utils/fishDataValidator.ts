import { Fish, Point } from '../types/game';

/**
 * 鱼数据验证和修复工具
 */
export class FishDataValidator {
  
  /**
   * 验证鱼数据的完整性
   */
  static validateFish(fish: any): { isValid: boolean; errors: string[] } {
    const errors: string[] = [];
    
    // 检查必需字段
    if (!fish.fishId) {
      errors.push('缺少fishId');
    }
    
    if (!fish.name) {
      errors.push('缺少name');
    }
    
    if (!fish.color) {
      errors.push('缺少color');
    }
    
    // 检查position字段
    if (!fish.position || typeof fish.position.x !== 'number' || typeof fish.position.y !== 'number') {
      errors.push('position字段不完整或缺失');
    }
    
    // 检查path字段
    if (!fish.path || !Array.isArray(fish.path) || fish.path.length === 0) {
      errors.push('path字段不完整或缺失');
    }
    
    // 检查imageData字段
    if (!fish.imageData || fish.imageData.trim() === '') {
      errors.push('imageData字段缺失或为空');
    }
    
    // 检查数值字段
    if (typeof fish.scale !== 'number' || fish.scale <= 0) {
      errors.push('scale字段无效');
    }
    
    if (typeof fish.direction !== 'number') {
      errors.push('direction字段无效');
    }
    
    if (typeof fish.speed !== 'number' || fish.speed < 0) {
      errors.push('speed字段无效');
    }
    
    if (typeof fish.wiggle !== 'number') {
      errors.push('wiggle字段无效');
    }
    
    if (typeof fish.likes !== 'number' || fish.likes < 0) {
      errors.push('likes字段无效');
    }
    
    if (typeof fish.liked !== 'boolean') {
      errors.push('liked字段无效');
    }
    
    return {
      isValid: errors.length === 0,
      errors
    };
  }
  
  /**
   * 尝试修复鱼数据
   */
  static repairFish(fish: any): Fish | null {
    try {
      // 创建修复后的鱼对象
      const repairedFish: Fish = {
        id: fish.id,
        fishId: fish.fishId || `repaired-${Date.now()}`,
        name: fish.name || '未知鱼',
        color: fish.color || '#FF6B6B',
        path: this.repairPath(fish.path),
        position: this.repairPosition(fish.position),
        direction: typeof fish.direction === 'number' ? fish.direction : 0,
        speed: typeof fish.speed === 'number' && fish.speed >= 0 ? fish.speed : 2,
        wiggle: typeof fish.wiggle === 'number' ? fish.wiggle : 0.02,
        createdAt: fish.createdAt ? new Date(fish.createdAt) : new Date(),
        updatedAt: fish.updatedAt ? new Date(fish.updatedAt) : new Date(),
        author: fish.author || 'system',
        imageData: this.repairImageData(fish.imageData, fish.color),
        scale: typeof fish.scale === 'number' && fish.scale > 0 ? fish.scale : 1,
        likes: typeof fish.likes === 'number' && fish.likes >= 0 ? fish.likes : 0,
        liked: typeof fish.liked === 'boolean' ? fish.liked : false
      };
      
      return repairedFish;
    } catch (error) {
      console.error('修复鱼数据失败:', error);
      return null;
    }
  }
  
  /**
   * 修复路径数据
   */
  private static repairPath(path: any): Point[] {
    if (Array.isArray(path) && path.length > 0) {
      // 验证每个点
      return path.filter(point => 
        point && typeof point.x === 'number' && typeof point.y === 'number'
      );
    }
    
    // 如果没有有效路径，创建一个简单的圆形路径
    const centerX = 100 + Math.random() * 200;
    const centerY = 100 + Math.random() * 200;
    const radius = 20 + Math.random() * 30;
    const segments = 8;
    
    const simplePath: Point[] = [];
    for (let i = 0; i < segments; i++) {
      const angle = (i / segments) * Math.PI * 2;
      simplePath.push({
        x: centerX + Math.cos(angle) * radius,
        y: centerY + Math.sin(angle) * radius
      });
    }
    
    return simplePath;
  }
  
  /**
   * 修复位置数据
   */
  private static repairPosition(position: any): Point {
    if (position && typeof position.x === 'number' && typeof position.y === 'number') {
      return position;
    }
    
    // 如果没有有效位置，随机生成一个
    return {
      x: 100 + Math.random() * 400,
      y: 100 + Math.random() * 300
    };
  }
  
  /**
   * 修复图像数据
   */
  private static repairImageData(imageData: string, color: string): string {
    if (imageData && imageData.trim() !== '') {
      return imageData;
    }
    
    // 如果没有图像数据，创建一个简单的SVG图像
    const svg = `
      <svg width="100" height="60" xmlns="http://www.w3.org/2000/svg">
        <ellipse cx="50" cy="30" rx="40" ry="25" fill="${color}" opacity="0.8"/>
        <circle cx="35" cy="25" r="3" fill="white"/>
        <circle cx="65" cy="25" r="3" fill="white"/>
        <path d="M 45 35 Q 50 40 55 35" stroke="white" stroke-width="2" fill="none"/>
        <path d="M 20 30 Q 10 20 10 30 Q 10 40 20 30" fill="${color}" opacity="0.6"/>
        <path d="M 80 30 Q 90 20 90 30 Q 90 40 80 30" fill="${color}" opacity="0.6"/>
      </svg>
    `;
    
    // 转换为Base64
    return 'data:image/svg+xml;base64,' + btoa(svg);
  }
  
  /**
   * 批量验证和修复鱼群数据
   */
  static validateAndRepairFishes(fishes: any[]): { valid: Fish[]; invalid: any[]; repaired: Fish[] } {
    const valid: Fish[] = [];
    const invalid: any[] = [];
    const repaired: Fish[] = [];
    
    fishes.forEach(fish => {
      const validation = this.validateFish(fish);
      
      if (validation.isValid) {
        valid.push(fish as Fish);
      } else {
        // 尝试修复
        const repairedFish = this.repairFish(fish);
        if (repairedFish) {
          repaired.push(repairedFish);
          console.log(`鱼数据已修复: ${fish.name || fish.fishId}`, {
            original: fish,
            repaired: repairedFish,
            errors: validation.errors
          });
        } else {
          invalid.push(fish);
          console.warn(`无法修复的鱼数据: ${fish.name || fish.fishId}`, {
            fish,
            errors: validation.errors
          });
        }
      }
    });
    
    return { valid, invalid, repaired };
  }
  
  /**
   * 生成数据质量报告
   */
  static generateReport(fishes: any[]): string {
    const { valid, invalid, repaired } = this.validateAndRepairFishes(fishes);
    const total = fishes.length;
    
    return `
鱼群数据质量报告:
- 总数: ${total}
- 有效数据: ${valid.length} (${((valid.length / total) * 100).toFixed(1)}%)
- 已修复: ${repaired.length} (${((repaired.length / total) * 100).toFixed(1)}%)
- 无效数据: ${invalid.length} (${((invalid.length / total) * 100).toFixed(1)}%)
- 数据完整性: ${(((valid.length + repaired.length) / total) * 100).toFixed(1)}%
    `.trim();
  }
}
