package com.example.aiinterviewassistant.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 宠物实体类
 * 表示用户的虚拟宠物
 */
public class Pet {
    private String id;
    private String name;
    private PetType type;
    private PetStats stats;
    private PetAppearance appearance; // 新增：完整的外观系统
    private PetPersonality personality; // 新增：性格特征系统
    private LocalDateTime birthDate;
    private LocalDateTime lastInteraction;
    private boolean isAsleep;
    private String mood; // 当前心情状态
    private String currentAction; // 当前动作状态
    private Position position; // 新增：宠物位置
    private Environment.RoomType currentRoom; // 新增：当前房间

    // 构造函数
    public Pet() {
        this.id = UUID.randomUUID().toString();
        this.stats = new PetStats();
        this.appearance = new PetAppearance();
        this.personality = new PetPersonality();
        this.birthDate = LocalDateTime.now();
        this.lastInteraction = LocalDateTime.now();
        this.isAsleep = false;
        this.currentAction = "idle";
        this.position = new Position(100, 100);
        this.currentRoom = Environment.RoomType.LIVING_ROOM;
    }

    public Pet(String name, PetType type) {
        this();
        this.name = name;
        this.type = type;
        this.mood = "curious"; // 新生宠物都很好奇
        // 根据宠物类型设置默认外观
        this.appearance.setDefaultAppearanceForType(type);
        // 根据宠物类型设置默认性格
        this.personality.setDefaultPersonalityForType(type);
    }

    /**
     * 计算宠物年龄（天数）
     */
    public long getAgeInDays() {
        return java.time.Duration.between(birthDate, LocalDateTime.now()).toDays();
    }

    /**
     * 获取宠物成长阶段
     */
    public String getGrowthStage() {
        long age = getAgeInDays();
        if (age <= 7) return "幼儿期";
        if (age <= 30) return "少年期";
        if (age <= 90) return "青年期";
        return "成年期";
    }

    /**
     * 检查宠物是否需要关注
     */
    public boolean needsAttention() {
        return stats.needsUrgentCare() || 
               java.time.Duration.between(lastInteraction, LocalDateTime.now()).toHours() > 12;
    }

    /**
     * 更新最后互动时间
     */
    public void updateLastInteraction() {
        this.lastInteraction = LocalDateTime.now();
    }

    /**
     * 获取宠物当前状态描述
     */
    public String getStatusDescription() {
        if (isAsleep) {
            return name + " 正在睡觉 😴";
        }
        
        if (stats.needsUrgentCare()) {
            if (stats.getHunger() <= 20) return name + " 非常饿 😢";
            if (stats.getCleanliness() <= 20) return name + " 很脏，需要清洁 🛁";
            if (stats.getHealth() <= 30) return name + " 看起来不太舒服 😰";
        }
        
        String moodEmoji = switch (stats.getMoodStatus()) {
            case "非常开心" -> "😄";
            case "开心" -> "😊";
            case "一般" -> "😐";
            case "不开心" -> "😔";
            case "很难过" -> "😢";
            default -> "🙂";
        };
        
        return name + " " + stats.getMoodStatus() + " " + moodEmoji;
    }

    /**
     * 获取可执行的推荐动作
     */
    public String[] getRecommendedActions() {
        java.util.List<String> actions = new java.util.ArrayList<>();
        
        if (stats.getHunger() <= 70) actions.add("喂食");
        if (stats.getCleanliness() <= 70) actions.add("清洁");
        if (stats.getHappiness() <= 70) actions.add("玩耍");
        if (stats.getEnergy() <= 30) actions.add("休息");
        
        if (actions.isEmpty()) {
            actions.add("抚摸");
            actions.add("聊天");
        }
        
        return actions.toArray(new String[0]);
    }

    /**
     * 根据性格特征计算行为倾向
     */
    public String getNextAction() {
        if (isAsleep) return "sleeping";
        
        // 根据需求状态决定行为
        if (stats.getHunger() < 30) return "seeking_food";
        if (stats.getEnergy() < 20) return "seeking_rest";
        if (stats.getCleanliness() < 30) return "seeking_cleaning";
        
        // 根据性格特征决定随机行为
        if (personality.getCuriosity() > 70 && Math.random() < 0.3) return "exploring";
        if (personality.getPlayfulness() > 70 && Math.random() < 0.4) return "playing";
        if (personality.getAffection() > 70 && Math.random() < 0.2) return "seeking_attention";
        
        return "idle";
    }

    // 新增的Position内部类
    public static class Position {
        private double x;
        private double y;
        
        public Position(double x, double y) {
            this.x = x;
            this.y = y;
        }
        
        // Getters and Setters
        public double getX() { return x; }
        public void setX(double x) { this.x = x; }
        public double getY() { return y; }
        public void setY(double y) { this.y = y; }
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public PetType getType() { return type; }
    public void setType(PetType type) { this.type = type; }

    public PetStats getStats() { return stats; }
    public void setStats(PetStats stats) { this.stats = stats; }

    public PetAppearance getAppearance() { return appearance; }
    public void setAppearance(PetAppearance appearance) { this.appearance = appearance; }

    public PetPersonality getPersonality() { return personality; }
    public void setPersonality(PetPersonality personality) { this.personality = personality; }

    public LocalDateTime getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDateTime birthDate) { this.birthDate = birthDate; }

    public LocalDateTime getLastInteraction() { return lastInteraction; }
    public void setLastInteraction(LocalDateTime lastInteraction) { this.lastInteraction = lastInteraction; }

    public boolean isAsleep() { return isAsleep; }
    public void setAsleep(boolean asleep) { isAsleep = asleep; }

    public String getMood() { return mood; }
    public void setMood(String mood) { this.mood = mood; }

    public String getCurrentAction() { return currentAction; }
    public void setCurrentAction(String currentAction) { this.currentAction = currentAction; }

    public Position getPosition() { return position; }
    public void setPosition(Position position) { this.position = position; }

    public Environment.RoomType getCurrentRoom() { return currentRoom; }
    public void setCurrentRoom(Environment.RoomType currentRoom) { this.currentRoom = currentRoom; }
}
