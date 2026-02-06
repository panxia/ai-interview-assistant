package com.example.aiinterviewassistant.model;

import java.util.*;

/**
 * 宠物动画和移动系统
 * 管理动画状态机和智能寻路
 */
public class PetAnimation {
    
    // 当前动画状态
    private AnimationState currentState = AnimationState.IDLE;
    private AnimationState previousState = AnimationState.IDLE;
    
    // 当前位置和目标
    private Position currentPosition = new Position(50, 50);
    private Position targetPosition = null;
    private List<Position> pathToTarget = new ArrayList<>();
    
    // 动画参数
    private float animationSpeed = 1.0f;
    private float movementSpeed = 5.0f;
    private boolean isMoving = false;
    private boolean isInterruptible = true;
    
    // 动画计时器
    private long stateStartTime = System.currentTimeMillis();
    private long animationDuration = 0;
    
    // 特殊动画标记
    private Map<String, Boolean> animationFlags = new HashMap<>();
    
    /**
     * 动画状态枚举
     */
    public enum AnimationState {
        // 基础状态
        IDLE("闲置", "idle", true),
        WALKING("走路", "walk", true),
        RUNNING("跑步", "run", true),
        SITTING("坐下", "sit", true),
        SLEEPING("睡觉", "sleep", false),
        
        // 动作状态
        EATING("吃东西", "eat", false),
        DRINKING("喝水", "drink", false),
        PLAYING("玩耍", "play", true),
        JUMPING("跳跃", "jump", true),
        ROLLING("翻滚", "roll", true),
        
        // 情绪状态
        HAPPY("开心", "happy", true),
        SAD("难过", "sad", true),
        ANGRY("生气", "angry", true),
        SCARED("害怕", "scared", true),
        CURIOUS("好奇", "curious", true),
        
        // 特殊动作
        STRETCHING("伸懒腰", "stretch", false),
        YAWNING("打哈欠", "yawn", false),
        SCRATCHING("挠痒", "scratch", false),
        TAIL_WAGGING("摇尾巴", "tail_wag", true),
        PURRING("发出呼噜声", "purr", true),
        
        // 互动动作
        BEING_PETTED("被抚摸", "petted", false),
        LOOKING_AT_USER("看向用户", "look_user", true),
        FOLLOWING("跟随", "follow", true),
        WAVING("挥手", "wave", false),
        DANCING("跳舞", "dance", false);
        
        private final String displayName;
        private final String animationKey;
        private final boolean canMove;
        
        AnimationState(String displayName, String animationKey, boolean canMove) {
            this.displayName = displayName;
            this.animationKey = animationKey;
            this.canMove = canMove;
        }
        
        public String getDisplayName() { return displayName; }
        public String getAnimationKey() { return animationKey; }
        public boolean canMove() { return canMove; }
    }
    
    /**
     * 位置类
     */
    public static class Position {
        public float x;
        public float y;
        public float z; // 用于3D场景
        
        public Position(float x, float y) {
            this(x, y, 0);
        }
        
        public Position(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
        
        public float distanceTo(Position other) {
            float dx = x - other.x;
            float dy = y - other.y;
            float dz = z - other.z;
            return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        }
        
        @Override
        public String toString() {
            return String.format("(%.1f, %.1f, %.1f)", x, y, z);
        }
    }
    
    /**
     * 动画过渡规则
     */
    public static class TransitionRule {
        public AnimationState from;
        public AnimationState to;
        public float duration; // 过渡时间（秒）
        public String condition; // 过渡条件
        
        public TransitionRule(AnimationState from, AnimationState to, float duration) {
            this.from = from;
            this.to = to;
            this.duration = duration;
        }
    }
    
    // 预定义的动画过渡规则
    private static final List<TransitionRule> TRANSITION_RULES = Arrays.asList(
        new TransitionRule(AnimationState.IDLE, AnimationState.WALKING, 0.3f),
        new TransitionRule(AnimationState.WALKING, AnimationState.RUNNING, 0.2f),
        new TransitionRule(AnimationState.RUNNING, AnimationState.IDLE, 0.5f),
        new TransitionRule(AnimationState.IDLE, AnimationState.SITTING, 0.5f),
        new TransitionRule(AnimationState.SITTING, AnimationState.SLEEPING, 1.0f),
        new TransitionRule(AnimationState.SLEEPING, AnimationState.YAWNING, 0.5f),
        new TransitionRule(AnimationState.YAWNING, AnimationState.IDLE, 0.3f)
    );
    
    /**
     * 切换动画状态
     */
    public boolean changeState(AnimationState newState) {
        if (currentState == newState) {
            return false;
        }
        
        // 检查是否可以中断当前动画
        if (!isInterruptible && !isAnimationComplete()) {
            return false;
        }
        
        // 检查过渡规则
        if (!canTransition(currentState, newState)) {
            return false;
        }
        
        previousState = currentState;
        currentState = newState;
        stateStartTime = System.currentTimeMillis();
        
        // 设置动画持续时间
        animationDuration = getAnimationDuration(newState);
        
        // 更新中断标记
        isInterruptible = newState != AnimationState.EATING 
                      && newState != AnimationState.DRINKING 
                      && newState != AnimationState.SLEEPING;
        
        return true;
    }
    
    /**
     * 检查是否可以过渡
     */
    private boolean canTransition(AnimationState from, AnimationState to) {
        // 睡觉状态只能通过打哈欠唤醒
        if (from == AnimationState.SLEEPING && to != AnimationState.YAWNING) {
            return false;
        }
        
        // 其他情况都允许过渡
        return true;
    }
    
    /**
     * 获取动画持续时间（毫秒）
     */
    private long getAnimationDuration(AnimationState state) {
        return switch (state) {
            case EATING -> 3000;
            case DRINKING -> 2000;
            case SLEEPING -> 10000;
            case YAWNING -> 1500;
            case STRETCHING -> 2000;
            case JUMPING -> 800;
            case ROLLING -> 1200;
            case WAVING -> 1000;
            case DANCING -> 5000;
            default -> 0; // 持续性动画
        };
    }
    
    /**
     * 检查动画是否完成
     */
    public boolean isAnimationComplete() {
        if (animationDuration == 0) {
            return false; // 持续性动画永不完成
        }
        return System.currentTimeMillis() - stateStartTime >= animationDuration;
    }
    
    /**
     * 移动到目标位置
     */
    public void moveTo(Position target) {
        this.targetPosition = target;
        this.pathToTarget = calculatePath(currentPosition, target);
        this.isMoving = true;
        
        // 根据距离决定移动方式
        float distance = currentPosition.distanceTo(target);
        if (distance > 50) {
            changeState(AnimationState.RUNNING);
        } else {
            changeState(AnimationState.WALKING);
        }
    }
    
    /**
     * 计算路径（简单直线，可扩展为A*算法）
     */
    private List<Position> calculatePath(Position from, Position to) {
        List<Position> path = new ArrayList<>();
        
        // 简单的直线路径，可以扩展为避障算法
        int steps = 10;
        for (int i = 1; i <= steps; i++) {
            float t = (float) i / steps;
            float x = from.x + (to.x - from.x) * t;
            float y = from.y + (to.y - from.y) * t;
            float z = from.z + (to.z - from.z) * t;
            path.add(new Position(x, y, z));
        }
        
        return path;
    }
    
    /**
     * 更新位置（每帧调用）
     */
    public void updatePosition(float deltaTime) {
        if (!isMoving || targetPosition == null) {
            return;
        }
        
        if (!currentState.canMove()) {
            return; // 当前状态不允许移动
        }
        
        // 移动向目标
        float distance = currentPosition.distanceTo(targetPosition);
        if (distance < 1.0f) {
            // 到达目标
            currentPosition = targetPosition;
            targetPosition = null;
            isMoving = false;
            changeState(AnimationState.IDLE);
            return;
        }
        
        // 计算移动向量
        float dx = targetPosition.x - currentPosition.x;
        float dy = targetPosition.y - currentPosition.y;
        float dz = targetPosition.z - currentPosition.z;
        
        // 归一化
        float length = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        dx /= length;
        dy /= length;
        dz /= length;
        
        // 应用移动速度
        float speed = movementSpeed * animationSpeed * deltaTime;
        currentPosition.x += dx * speed;
        currentPosition.y += dy * speed;
        currentPosition.z += dz * speed;
    }
    
    /**
     * 获取当前动画帧信息
     */
    public AnimationFrame getCurrentFrame() {
        long elapsed = System.currentTimeMillis() - stateStartTime;
        float progress = animationDuration > 0 ? 
            Math.min(1.0f, (float) elapsed / animationDuration) : 0;
        
        return new AnimationFrame(
            currentState,
            progress,
            currentPosition,
            animationSpeed
        );
    }
    
    /**
     * 动画帧信息
     */
    public static class AnimationFrame {
        public AnimationState state;
        public float progress; // 0-1
        public Position position;
        public float speed;
        
        public AnimationFrame(AnimationState state, float progress, Position position, float speed) {
            this.state = state;
            this.progress = progress;
            this.position = position;
            this.speed = speed;
        }
    }
    
    /**
     * 执行特殊动画序列
     */
    public void playAnimationSequence(List<AnimationState> sequence) {
        // 实现动画序列播放逻辑
        // 可以用于复杂的动作组合
    }
    
    /**
     * 设置动画标记
     */
    public void setAnimationFlag(String flag, boolean value) {
        animationFlags.put(flag, value);
    }
    
    /**
     * 获取动画标记
     */
    public boolean getAnimationFlag(String flag) {
        return animationFlags.getOrDefault(flag, false);
    }
    
    // Getters and Setters
    public AnimationState getCurrentState() { return currentState; }
    public AnimationState getPreviousState() { return previousState; }
    public Position getCurrentPosition() { return currentPosition; }
    public Position getTargetPosition() { return targetPosition; }
    public boolean isMoving() { return isMoving; }
    public float getAnimationSpeed() { return animationSpeed; }
    public void setAnimationSpeed(float speed) { 
        this.animationSpeed = Math.max(0.1f, Math.min(3.0f, speed)); 
    }
    public float getMovementSpeed() { return movementSpeed; }
    public void setMovementSpeed(float speed) { 
        this.movementSpeed = Math.max(1.0f, Math.min(20.0f, speed)); 
    }
}