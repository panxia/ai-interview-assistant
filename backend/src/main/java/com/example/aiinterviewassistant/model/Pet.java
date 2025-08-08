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
    private String color; // 宠物颜色/皮肤
    private LocalDateTime birthDate;
    private LocalDateTime lastInteraction;
    private boolean isAsleep;
    private String mood; // 当前心情状态

    // 构造函数
    public Pet() {
        this.id = UUID.randomUUID().toString();
        this.stats = new PetStats();
        this.birthDate = LocalDateTime.now();
        this.lastInteraction = LocalDateTime.now();
        this.isAsleep = false;
        this.color = "default";
    }

    public Pet(String name, PetType type) {
        this();
        this.name = name;
        this.type = type;
        this.mood = "curious"; // 新生宠物都很好奇
    }

    /**
     * 计算宠物年龄（天数）
     */
    public long getAgeInDays() {
        return java.time.Duration.between(birthDate, LocalDateTime.now()).toDays();
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

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public PetType getType() { return type; }
    public void setType(PetType type) { this.type = type; }

    public PetStats getStats() { return stats; }
    public void setStats(PetStats stats) { this.stats = stats; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public LocalDateTime getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDateTime birthDate) { this.birthDate = birthDate; }

    public LocalDateTime getLastInteraction() { return lastInteraction; }
    public void setLastInteraction(LocalDateTime lastInteraction) { this.lastInteraction = lastInteraction; }

    public boolean isAsleep() { return isAsleep; }
    public void setAsleep(boolean asleep) { isAsleep = asleep; }

    public String getMood() { return mood; }
    public void setMood(String mood) { this.mood = mood; }
}
