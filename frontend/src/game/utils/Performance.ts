export class Performance {
  private fps: number = 0;
  private frameCount: number = 0;
  private lastTime: number = 0;
  private frameTimes: number[] = [];
  private maxFrameTimes: number = 60;

  // 性能指标
  private metrics = {
    fps: 0,
    frameTime: 0,
    memoryUsage: 0,
    drawCalls: 0,
    triangles: 0,
    points: 0,
    lines: 0
  };

  // 性能警告阈值
  private thresholds = {
    lowFPS: 30,
    highFrameTime: 33, // 33ms = 30fps
    highMemoryUsage: 50 * 1024 * 1024 // 50MB
  };

  // 回调函数
  private onPerformanceWarning?: (metric: string, value: number, threshold: number) => void;
  private onMetricsUpdate?: (metrics: typeof this.metrics) => void;

  constructor() {
    this.lastTime = performance.now();
  }

  /**
   * 开始性能监控
   */
  public start(): void {
    this.frameCount = 0;
    this.lastTime = performance.now();
    this.frameTimes = [];
    this.metrics = {
      fps: 0,
      frameTime: 0,
      memoryUsage: 0,
      drawCalls: 0,
      triangles: 0,
      points: 0,
      lines: 0
    };
  }

  /**
   * 更新性能指标
   */
  public update(): void {
    const currentTime = performance.now();
    const deltaTime = currentTime - this.lastTime;
    
    if (deltaTime > 0) {
      this.frameCount++;
      this.frameTimes.push(deltaTime);
      
      // 保持帧时间数组在指定大小内
      if (this.frameTimes.length > this.maxFrameTimes) {
        this.frameTimes.shift();
      }
      
      // 计算平均帧时间
      const avgFrameTime = this.frameTimes.reduce((sum, time) => sum + time, 0) / this.frameTimes.length;
      
      // 计算FPS
      this.metrics.fps = 1000 / avgFrameTime;
      this.metrics.frameTime = avgFrameTime;
      
      // 检查性能警告
      this.checkPerformanceWarnings();
      
      // 触发指标更新回调
      if (this.onMetricsUpdate) {
        this.onMetricsUpdate({ ...this.metrics });
      }
    }
    
    this.lastTime = currentTime;
  }

  /**
   * 检查性能警告
   */
  private checkPerformanceWarnings(): void {
    if (!this.onPerformanceWarning) return;

    if (this.metrics.fps < this.thresholds.lowFPS) {
      this.onPerformanceWarning('fps', this.metrics.fps, this.thresholds.lowFPS);
    }

    if (this.metrics.frameTime > this.thresholds.highFrameTime) {
      this.onPerformanceWarning('frameTime', this.metrics.frameTime, this.thresholds.highFrameTime);
    }

    if (this.metrics.memoryUsage > this.thresholds.highMemoryUsage) {
      this.onPerformanceWarning('memoryUsage', this.metrics.memoryUsage, this.thresholds.highMemoryUsage);
    }
  }

  /**
   * 更新渲染统计信息
   */
  public updateRenderStats(stats: {
    drawCalls?: number;
    triangles?: number;
    points?: number;
    lines?: number;
  }): void {
    if (stats.drawCalls !== undefined) this.metrics.drawCalls = stats.drawCalls;
    if (stats.triangles !== undefined) this.metrics.triangles = stats.triangles;
    if (stats.points !== undefined) this.metrics.points = stats.points;
    if (stats.lines !== undefined) this.metrics.lines = stats.lines;
  }

  /**
   * 更新内存使用情况
   */
  public updateMemoryUsage(): void {
    if ('memory' in performance) {
      const memory = (performance as any).memory;
      this.metrics.memoryUsage = memory.usedJSHeapSize;
    }
  }

  /**
   * 获取当前性能指标
   */
  public getMetrics(): typeof this.metrics {
    return { ...this.metrics };
  }

  /**
   * 获取FPS
   */
  public getFPS(): number {
    return this.metrics.fps;
  }

  /**
   * 获取平均帧时间
   */
  public getAverageFrameTime(): number {
    return this.metrics.frameTime;
  }

  /**
   * 获取内存使用情况
   */
  public getMemoryUsage(): number {
    return this.metrics.memoryUsage;
  }

  /**
   * 设置性能警告回调
   */
  public setPerformanceWarningCallback(callback: (metric: string, value: number, threshold: number) => void): void {
    this.onPerformanceWarning = callback;
  }

  /**
   * 设置指标更新回调
   */
  public setMetricsUpdateCallback(callback: (metrics: typeof this.metrics) => void): void {
    this.onMetricsUpdate = callback;
  }

  /**
   * 设置性能阈值
   */
  public setThresholds(thresholds: Partial<typeof this.thresholds>): void {
    this.thresholds = { ...this.thresholds, ...thresholds };
  }

  /**
   * 获取性能报告
   */
  public getPerformanceReport(): {
    current: typeof this.metrics;
    average: typeof this.metrics;
    min: typeof this.metrics;
    max: typeof this.metrics;
    warnings: string[];
  } {
    const warnings: string[] = [];
    
    if (this.metrics.fps < this.thresholds.lowFPS) {
      warnings.push(`FPS过低: ${this.metrics.fps.toFixed(1)} < ${this.thresholds.lowFPS}`);
    }
    
    if (this.metrics.frameTime > this.thresholds.highFrameTime) {
      warnings.push(`帧时间过高: ${this.metrics.frameTime.toFixed(2)}ms > ${this.thresholds.highFrameTime}ms`);
    }
    
    if (this.metrics.memoryUsage > this.thresholds.highMemoryUsage) {
      warnings.push(`内存使用过高: ${(this.metrics.memoryUsage / 1024 / 1024).toFixed(2)}MB > ${(this.thresholds.highMemoryUsage / 1024 / 1024).toFixed(2)}MB`);
    }

    return {
      current: { ...this.metrics },
      average: { ...this.metrics }, // 简化版本，实际可以计算历史平均值
      min: { ...this.metrics },     // 简化版本，实际可以记录最小值
      max: { ...this.metrics },     // 简化版本，实际可以记录最大值
      warnings
    };
  }

  /**
   * 重置性能指标
   */
  public reset(): void {
    this.frameCount = 0;
    this.frameTimes = [];
    this.metrics = {
      fps: 0,
      frameTime: 0,
      memoryUsage: 0,
      drawCalls: 0,
      triangles: 0,
      points: 0,
      lines: 0
    };
  }

  /**
   * 停止性能监控
   */
  public stop(): void {
    this.frameCount = 0;
    this.frameTimes = [];
  }

  /**
   * 销毁性能监控器
   */
  public destroy(): void {
    this.stop();
    this.onPerformanceWarning = undefined;
    this.onMetricsUpdate = undefined;
  }

  /**
   * 格式化内存大小
   */
  public static formatMemorySize(bytes: number): string {
    if (bytes === 0) return '0 B';
    
    const k = 1024;
    const sizes = ['B', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  }

  /**
   * 格式化时间
   */
  public static formatTime(ms: number): string {
    if (ms < 1) return `${(ms * 1000).toFixed(2)}μs`;
    if (ms < 1000) return `${ms.toFixed(2)}ms`;
    return `${(ms / 1000).toFixed(2)}s`;
  }
}
