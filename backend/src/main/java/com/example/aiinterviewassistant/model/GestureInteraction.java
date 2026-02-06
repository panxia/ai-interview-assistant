package com.example.aiinterviewassistant.model;

import java.util.*;

/**
 * 手势交互和触觉反馈系统
 * 管理用户手势输入和触觉反馈响应
 */
public class GestureInteraction {
    
    // 手势识别参数
    private GestureType lastGesture;
    private long lastGestureTime;
    private PetAnimation.Position lastTouchPosition;
    private List<TouchPoint> touchHistory = new ArrayList<>();
    
    // 触觉反馈配置
    private boolean hapticsEnabled = true;
    private float hapticIntensity = 0.7f;
    private Map<String, HapticPattern> hapticPatterns = new HashMap<>();
    
    // 交互统计
    private Map<GestureType, Integer> gestureCount = new HashMap<>();
    private int totalInteractions = 0;
    
    /**
     * 手势类型枚举
     */
    public enum GestureType {
        // 基础手势
        TAP("轻触", "tap", "吸引注意"),
        DOUBLE_TAP("双击", "double_tap", "叫名字"),
        LONG_PRESS("长按", "long_press", "抚摸"),
        SWIPE("滑动", "swipe", "引导移动"),
        WAVE("挥手", "wave", "打招呼"),
        
        // 多点触控
        PINCH("捏合", "pinch", "缩放视角"),
        SPREAD("张开", "spread", "放大视角"),
        ROTATE("旋转", "rotate", "旋转视角"),
        TWO_FINGER_TAP("双指点击", "two_finger_tap", "特殊互动"),
        
        // 特殊手势
        CIRCLE("画圈", "circle", "让宠物转圈"),
        HEART("画心", "heart", "表达爱意"),
        ZIGZAG("Z字形", "zigzag", "让宠物跳跃"),
        SHAKE("摇晃", "shake", "吸引注意"),
        
        // 设备特殊
        DEVICE_SHAKE("设备摇晃", "device_shake", "宠物好奇"),
        TILT("倾斜", "tilt", "控制方向"),
        VOICE_INPUT("语音输入", "voice", "语音互动"),
        CAMERA_GESTURE("相机手势", "camera", "拍照模式");
        
        private final String displayName;
        private final String code;
        private final String action;
        
        GestureType(String displayName, String code, String action) {
            this.displayName = displayName;
            this.code = code;
            this.action = action;
        }
        
        public String getDisplayName() { return displayName; }
        public String getCode() { return code; }
        public String getAction() { return action; }
    }
    
    /**
     * 触摸点类
     */
    public static class TouchPoint {
        public float x;
        public float y;
        public long timestamp;
        public float pressure;
        public TouchZone zone;
        
        public TouchPoint(float x, float y, float pressure) {
            this.x = x;
            this.y = y;
            this.pressure = pressure;
            this.timestamp = System.currentTimeMillis();
            this.zone = detectZone(x, y);
        }
        
        private TouchZone detectZone(float x, float y) {
            // 简单的区域检测逻辑
            if (y < 30) return TouchZone.HEAD;
            if (y > 70) return TouchZone.TAIL;
            if (x < 30) return TouchZone.LEFT_PAW;
            if (x > 70) return TouchZone.RIGHT_PAW;
            if (y > 40 && y < 60) return TouchZone.BELLY;
            return TouchZone.BACK;
        }
    }
    
    /**
     * 触摸区域枚举
     */
    public enum TouchZone {
        HEAD("头部", "轻震动", "眯眼开心"),
        BELLY("肚子", "连续震动", "害羞翻滚"),
        BACK("背部", "温和震动", "舒适伸懒腰"),
        LEFT_PAW("左爪", "短震动", "缩爪子"),
        RIGHT_PAW("右爪", "短震动", "缩爪子"),
        TAIL("尾巴", "特殊震动", "尾巴摇摆"),
        EAR("耳朵", "微震动", "耳朵抖动"),
        NOSE("鼻子", "点震动", "打喷嚏");
        
        private final String displayName;
        private final String hapticType;
        private final String reaction;
        
        TouchZone(String displayName, String hapticType, String reaction) {
            this.displayName = displayName;
            this.hapticType = hapticType;
            this.reaction = reaction;
        }
        
        public String getDisplayName() { return displayName; }
        public String getHapticType() { return hapticType; }
        public String getReaction() { return reaction; }
    }
    
    /**
     * 触觉反馈模式
     */
    public static class HapticPattern {
        public String name;
        public int[] pattern; // 震动模式 [延迟, 震动, 延迟, 震动...]
        public int[] amplitudes; // 震动强度
        public int repeat; // 重复次数
        
        public HapticPattern(String name, int[] pattern, int[] amplitudes) {
            this.name = name;
            this.pattern = pattern;
            this.amplitudes = amplitudes;
            this.repeat = 0;
        }
        
        // 预定义模式
        public static HapticPattern LIGHT_TAP = new HapticPattern(
            "轻触", new int[]{0, 50}, new int[]{0, 100}
        );
        
        public static HapticPattern HEARTBEAT = new HapticPattern(
            "心跳", new int[]{0, 100, 100, 100}, new int[]{0, 150, 0, 150}
        );
        
        public static HapticPattern PURR = new HapticPattern(
            "呼噜", new int[]{0, 50, 50, 50, 50, 50}, new int[]{0, 80, 0, 80, 0, 80}
        );
        
        public static HapticPattern HAPPY = new HapticPattern(
            "开心", new int[]{0, 200, 100, 200}, new int[]{0, 200, 0, 200}
        );
        
        public static HapticPattern SCARED = new HapticPattern(
            "害怕", new int[]{0, 30, 30, 30, 30, 30}, new int[]{0, 255, 0, 255, 0, 255}
        );
    }
    
    /**
     * 手势响应类
     */
    public static class GestureResponse {
        public PetAnimation.AnimationState animation;
        public HapticPattern hapticFeedback;
        public String soundEffect;
        public String message;
        public int affectionChange;
        
        public GestureResponse(PetAnimation.AnimationState animation, 
                             HapticPattern haptic, 
                             String sound, 
                             String message) {
            this.animation = animation;
            this.hapticFeedback = haptic;
            this.soundEffect = sound;
            this.message = message;
            this.affectionChange = 0;
        }
    }
    
    // 构造函数
    public GestureInteraction() {
        initializeHapticPatterns();
    }
    
    /**
     * 初始化触觉模式
     */
    private void initializeHapticPatterns() {
        hapticPatterns.put("light", HapticPattern.LIGHT_TAP);
        hapticPatterns.put("heartbeat", HapticPattern.HEARTBEAT);
        hapticPatterns.put("purr", HapticPattern.PURR);
        hapticPatterns.put("happy", HapticPattern.HAPPY);
        hapticPatterns.put("scared", HapticPattern.SCARED);
    }
    
    /**
     * 处理手势输入
     */
    public GestureResponse handleGesture(GestureType gesture, PetAnimation.Position position, Pet pet) {
        // 记录手势
        lastGesture = gesture;
        lastGestureTime = System.currentTimeMillis();
        lastTouchPosition = position;
        totalInteractions++;
        gestureCount.put(gesture, gestureCount.getOrDefault(gesture, 0) + 1);
        
        // 根据手势类型生成响应
        GestureResponse response = generateResponse(gesture, position, pet);
        
        // 应用响应到宠物
        if (response.animation != null && pet.getAnimation() != null) {
            pet.getAnimation().changeState(response.animation);
        }
        
        if (response.affectionChange != 0 && pet.getPersonality() != null) {
            pet.getPersonality().adjustPersonality("pet", response.affectionChange);
        }
        
        return response;
    }
    
    /**
     * 生成手势响应
     */
    private GestureResponse generateResponse(GestureType gesture, PetAnimation.Position position, Pet pet) {
        switch (gesture) {
            case TAP -> {
                return new GestureResponse(
                    PetAnimation.AnimationState.LOOKING_AT_USER,
                    HapticPattern.LIGHT_TAP,
                    "tap.wav",
                    pet.getName() + " 看向你"
                );
            }
            case DOUBLE_TAP -> {
                return new GestureResponse(
                    PetAnimation.AnimationState.HAPPY,
                    HapticPattern.HAPPY,
                    "meow.wav",
                    pet.getName() + " 回应你的呼唤"
                );
            }
            case LONG_PRESS -> {
                TouchZone zone = new TouchPoint(position.x, position.y, 1.0f).zone;
                return generateZoneResponse(zone, pet);
            }
            case SWIPE -> {
                pet.getAnimation().moveTo(position);
                return new GestureResponse(
                    PetAnimation.AnimationState.WALKING,
                    null,
                    "footstep.wav",
                    pet.getName() + " 跟随你的引导"
                );
            }
            case WAVE -> {
                return new GestureResponse(
                    PetAnimation.AnimationState.WAVING,
                    HapticPattern.HAPPY,
                    "hello.wav",
                    pet.getName() + " 向你挥手"
                );
            }
            case PINCH, SPREAD -> {
                return new GestureResponse(
                    null,
                    null,
                    "zoom.wav",
                    "视角已调整"
                );
            }
            case HEART -> {
                GestureResponse response = new GestureResponse(
                    PetAnimation.AnimationState.HAPPY,
                    HapticPattern.HEARTBEAT,
                    "love.wav",
                    pet.getName() + " 感受到你的爱意 ❤️"
                );
                response.affectionChange = 5;
                return response;
            }
            case DEVICE_SHAKE -> {
                return new GestureResponse(
                    PetAnimation.AnimationState.CURIOUS,
                    HapticPattern.LIGHT_TAP,
                    "curious.wav",
                    pet.getName() + " 好奇地歪着头"
                );
            }
            default -> {
                return new GestureResponse(
                    PetAnimation.AnimationState.IDLE,
                    null,
                    null,
                    ""
                );
            }
        }
    }
    
    /**
     * 生成区域触摸响应
     */
    private GestureResponse generateZoneResponse(TouchZone zone, Pet pet) {
        switch (zone) {
            case HEAD -> {
                GestureResponse response = new GestureResponse(
                    PetAnimation.AnimationState.BEING_PETTED,
                    HapticPattern.PURR,
                    "purr.wav",
                    pet.getName() + " " + zone.getReaction()
                );
                response.affectionChange = 2;
                return response;
            }
            case BELLY -> {
                return new GestureResponse(
                    PetAnimation.AnimationState.ROLLING,
                    HapticPattern.HAPPY,
                    "giggle.wav",
                    pet.getName() + " " + zone.getReaction()
                );
            }
            case BACK -> {
                return new GestureResponse(
                    PetAnimation.AnimationState.STRETCHING,
                    HapticPattern.PURR,
                    "stretch.wav",
                    pet.getName() + " " + zone.getReaction()
                );
            }
            case TAIL -> {
                return new GestureResponse(
                    PetAnimation.AnimationState.TAIL_WAGGING,
                    new HapticPattern("tail", new int[]{0, 100, 100, 100}, new int[]{0, 150, 0, 150}),
                    "swish.wav",
                    pet.getName() + " " + zone.getReaction()
                );
            }
            default -> {
                return new GestureResponse(
                    PetAnimation.AnimationState.HAPPY,
                    HapticPattern.LIGHT_TAP,
                    "touch.wav",
                    pet.getName() + " 享受你的抚摸"
                );
            }
        }
    }
    
    /**
     * 检测手势组合
     */
    public boolean detectGestureCombo(List<GestureType> sequence, long timeWindow) {
        if (touchHistory.size() < sequence.size()) {
            return false;
        }
        
        long now = System.currentTimeMillis();
        List<TouchPoint> recentTouches = touchHistory.stream()
            .filter(t -> now - t.timestamp <= timeWindow)
            .toList();
        
        return recentTouches.size() >= sequence.size();
    }
    
    /**
     * 获取交互统计
     */
    public Map<String, Object> getInteractionStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total_interactions", totalInteractions);
        stats.put("gesture_counts", gestureCount);
        stats.put("favorite_gesture", getMostUsedGesture());
        stats.put("last_interaction", lastGestureTime);
        return stats;
    }
    
    /**
     * 获取最常用的手势
     */
    private GestureType getMostUsedGesture() {
        return gestureCount.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);
    }
    
    // Getters and Setters
    public boolean isHapticsEnabled() { return hapticsEnabled; }
    public void setHapticsEnabled(boolean enabled) { this.hapticsEnabled = enabled; }
    
    public float getHapticIntensity() { return hapticIntensity; }
    public void setHapticIntensity(float intensity) { 
        this.hapticIntensity = Math.max(0, Math.min(1, intensity)); 
    }
    
    public GestureType getLastGesture() { return lastGesture; }
    public long getLastGestureTime() { return lastGestureTime; }
    public PetAnimation.Position getLastTouchPosition() { return lastTouchPosition; }
}