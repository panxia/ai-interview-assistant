// 游戏核心类型定义

export interface Point {
  x: number;
  y: number;
}

export interface Fish {
  id?: number;
  fishId: string;
  name: string;
  color: string;
  path: Point[];
  position: Point;
  direction: number;
  speed: number;
  wiggle: number;
  createdAt: Date;
  updatedAt?: Date;
  author: string;
  imageData: string;
  scale: number;
  likes: number;
  liked: boolean;
}

export interface GameConfig {
  width: number;
  height: number;
  backgroundColor: number;
  antialias: boolean;
  resolution: number;
  autoDensity: boolean;
}

export interface PhysicsConfig {
  gravity: { x: number; y: number; scale: number };
  worldBounds: {
    x: number;
    y: number;
    width: number;
    height: number;
  };
}

export interface AnimationConfig {
  frameRate: number;
  wiggleSpeed: number;
  swimSpeed: number;
  directionChangeInterval: number;
}

export interface DrawingConfig {
  brushColors: string[];
  brushSizes: number[];
  canvasWidth: number;
  canvasHeight: number;
}

export interface DecorationElement {
  id: string;
  type: 'bubble' | 'seaweed' | 'coral';
  position: Point;
  scale: number;
  rotation: number;
  alpha: number;
}

export interface GameState {
  isDrawing: boolean;
  isPlaying: boolean;
  selectedColor: string;
  selectedBrushSize: number;
  fishes: Fish[];
  decorations: DecorationElement[];
  camera: {
    x: number;
    y: number;
    scale: number;
  };
}

export interface GameEvents {
  onFishCreated: (fish: Fish) => void;
  onFishLiked: (fishId: string, liked: boolean) => void;
  onDrawingStarted: () => void;
  onDrawingFinished: () => void;
  onGameStateChanged: (state: Partial<GameState>) => void;
}
