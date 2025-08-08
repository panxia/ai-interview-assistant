package com.example.aiinterviewassistant.model;

import java.time.LocalDateTime;

/**
 * 宠物状态数据
 * 管理宠物的各项数值状态
 */
public class PetStats {
    // 饥饿值 (0-100，0为最饿，100为最饱)
    private int hunger = 50;
    
    // 清洁度 (0-100，0为最脏，100为最干净)
    private int cleanliness = 80;
    
    // 快乐值 (0-100，0为最不开心，100为最开心)
    private int happiness = 60;
    
    // 能量值 (0-100，0为最累，100为最有活力)
    private int energy = 70;
    
    // 健康值 (0-100，0为生病，100为健康)
    private int health = 90;
    
    // 经验值
    private int experience = 0;
    
    // 等级
    private int level = 1;
    
    // 最后更新时间（用于计算随时间的状态变化）
    private LocalDateTime lastUpdate = LocalDateTime.now();

    // 构造函数
    public PetStats() {}

    public PetStats(int hunger, int cleanliness, int happiness, int energy, int health) {
        this.hunger = Math.max(0, Math.min(100, hunger));
        this.cleanliness = Math.max(0, Math.min(100, cleanliness));
        this.happiness = Math.max(0, Math.min(100, happiness));
        this.energy = Math.max(0, Math.min(100, energy));
        this.health = Math.max(0, Math.min(100, health));
    }

    /**
     * 更新状态值，确保在0-100范围内
     */
    public void updateStat(String statName, int value) {
        value = Math.max(0, Math.min(100, value));
        switch (statName.toLowerCase()) {
            case "hunger" -> this.hunger = value;
            case "cleanliness" -> this.cleanliness = value;
            case "happiness" -> this.happiness = value;
            case "energy" -> this.energy = value;
            case "health" -> this.health = value;
        }
        this.lastUpdate = LocalDateTime.now();
    }

    /**
     * 增加经验值并检查是否升级
     */
    public boolean addExperience(int exp) {
        this.experience += exp;
        int newLevel = Math.min(50, (this.experience / 100) + 1); // 最高50级
        boolean leveledUp = newLevel > this.level;
        this.level = newLevel;
        return leveledUp;
    }

    /**
     * 获取总体情绪状态
     */
    public String getMoodStatus() {
        int average = (hunger + cleanliness + happiness + energy + health) / 5;
        if (average >= 80) return "非常开心";
        if (average >= 60) return "开心";
        if (average >= 40) return "一般";
        if (average >= 20) return "不开心";
        return "很难过";
    }

    /**
     * 检查是否需要紧急照顾
     */
    public boolean needsUrgentCare() {
        return hunger <= 20 || cleanliness <= 20 || health <= 30;
    }

    // Getters and Setters
    public int getHunger() { return hunger; }
    public void setHunger(int hunger) { this.hunger = Math.max(0, Math.min(100, hunger)); }

    public int getCleanliness() { return cleanliness; }
    public void setCleanliness(int cleanliness) { this.cleanliness = Math.max(0, Math.min(100, cleanliness)); }

    public int getHappiness() { return happiness; }
    public void setHappiness(int happiness) { this.happiness = Math.max(0, Math.min(100, happiness)); }

    public int getEnergy() { return energy; }
    public void setEnergy(int energy) { this.energy = Math.max(0, Math.min(100, energy)); }

    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = Math.max(0, Math.min(100, health)); }

    public int getExperience() { return experience; }
    public void setExperience(int experience) { this.experience = experience; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public LocalDateTime getLastUpdate() { return lastUpdate; }
    public void setLastUpdate(LocalDateTime lastUpdate) { this.lastUpdate = lastUpdate; }
}
