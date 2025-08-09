package com.example.aiinterviewassistant.model;

/**
 * 宠物性格特征类
 * 影响宠物的行为模式和游戏体验
 */
public class PetPersonality {
    
    // 性格特征值 (0-100)
    private int playfulness = 50;    // 活泼度 - 影响移动频率和游戏参与度
    private int affection = 50;      // 亲人度 - 影响互动反应和亲密行为
    private int independence = 50;   // 独立性 - 影响自主行为和对主人的依赖
    private int curiosity = 50;      // 好奇心 - 影响探索行为和对新事物的反应
    private int appetite = 50;       // 食欲 - 影响饮食习惯和对食物的偏好
    private int sociability = 50;    // 社交性 - 影响与其他宠物的互动
    private int intelligence = 50;   // 智力 - 影响学习能力和游戏表现
    private int energy = 50;         // 精力 - 影响活动量和疲劳速度

    public PetPersonality() {}

    public PetPersonality(int playfulness, int affection, int independence, 
                         int curiosity, int appetite, int sociability, 
                         int intelligence, int energy) {
        this.playfulness = clamp(playfulness);
        this.affection = clamp(affection);
        this.independence = clamp(independence);
        this.curiosity = clamp(curiosity);
        this.appetite = clamp(appetite);
        this.sociability = clamp(sociability);
        this.intelligence = clamp(intelligence);
        this.energy = clamp(energy);
    }

    /**
     * 根据宠物类型设置默认性格
     */
    public void setDefaultPersonalityForType(PetType type) {
        switch (type) {
            case CAT -> {
                playfulness = 60;
                affection = 40;
                independence = 80;
                curiosity = 90;
                appetite = 60;
                sociability = 30;
                intelligence = 75;
                energy = 70;
            }
            case DOG -> {
                playfulness = 85;
                affection = 95;
                independence = 30;
                curiosity = 70;
                appetite = 80;
                sociability = 90;
                intelligence = 70;
                energy = 80;
            }
            case RABBIT -> {
                playfulness = 40;
                affection = 60;
                independence = 60;
                curiosity = 50;
                appetite = 70;
                sociability = 50;
                intelligence = 60;
                energy = 50;
            }
            case HAMSTER -> {
                playfulness = 80;
                affection = 50;
                independence = 70;
                curiosity = 95;
                appetite = 90;
                sociability = 40;
                intelligence = 65;
                energy = 85;
            }
            case DRAGON -> {
                playfulness = 60;
                affection = 50;
                independence = 90;
                curiosity = 85;
                appetite = 40;
                sociability = 60;
                intelligence = 95;
                energy = 75;
            }
            case PANDA -> {
                playfulness = 30;
                affection = 70;
                independence = 40;
                curiosity = 40;
                appetite = 95;
                sociability = 60;
                intelligence = 55;
                energy = 30;
            }
            case PENGUIN -> {
                playfulness = 70;
                affection = 60;
                independence = 50;
                curiosity = 60;
                appetite = 70;
                sociability = 80;
                intelligence = 70;
                energy = 60;
            }
        }
    }

    /**
     * 获取性格描述
     */
    public String getPersonalityDescription() {
        StringBuilder description = new StringBuilder("这是一只");
        
        if (playfulness > 70) description.append("活泼");
        else if (playfulness < 30) description.append("安静");
        else description.append("温和");
        
        if (curiosity > 70) description.append("好奇");
        else if (curiosity < 30) description.append("谨慎");
        
        description.append("的");
        
        if (affection > 70) description.append("亲人的");
        else if (affection < 30) description.append("独立的");
        
        if (independence > 70) description.append("自主性强的");
        else if (independence < 30) description.append("依赖性强的");
        
        description.append("小宠物");
        
        // 添加特殊描述
        if (appetite > 80) description.append("，非常喜欢美食");
        if (sociability > 80) description.append("，很喜欢社交");
        if (intelligence > 80) description.append("，很聪明");
        if (energy > 80) description.append("，精力充沛");
        
        return description.toString();
    }

    /**
     * 根据性格计算行为权重
     */
    public double getBehaviorWeight(String behaviorType) {
        return switch (behaviorType.toLowerCase()) {
            case "play" -> playfulness / 100.0;
            case "explore" -> curiosity / 100.0;
            case "seek_attention" -> (100 - independence) / 100.0;
            case "eat" -> appetite / 100.0;
            case "rest" -> (100 - energy) / 100.0;
            case "socialize" -> sociability / 100.0;
            case "learn" -> intelligence / 100.0;
            default -> 0.5;
        };
    }

    /**
     * 计算性格匹配度（用于推荐活动）
     */
    public double getActivitySuitability(String activityType) {
        return switch (activityType.toLowerCase()) {
            case "puzzle_game" -> intelligence / 100.0;
            case "reaction_game" -> (playfulness + energy) / 200.0;
            case "rhythm_game" -> (playfulness + sociability) / 200.0;
            case "exploration" -> curiosity / 100.0;
            case "grooming" -> affection / 100.0;
            case "feeding" -> appetite / 100.0;
            case "training" -> (intelligence + (100 - independence)) / 200.0;
            default -> 0.5;
        };
    }

    /**
     * 限制数值在0-100范围内
     */
    private int clamp(int value) {
        return Math.max(0, Math.min(100, value));
    }

    /**
     * 随着时间和经验调整性格（轻微变化）
     */
    public void evolvePersonality(String experienceType, int intensity) {
        switch (experienceType.toLowerCase()) {
            case "positive_interaction" -> {
                affection = clamp(affection + intensity / 10);
                sociability = clamp(sociability + intensity / 20);
            }
            case "learning_success" -> {
                intelligence = clamp(intelligence + intensity / 15);
                curiosity = clamp(curiosity + intensity / 20);
            }
            case "play_activity" -> {
                playfulness = clamp(playfulness + intensity / 10);
                energy = clamp(energy + intensity / 20);
            }
            case "independence_training" -> {
                independence = clamp(independence + intensity / 15);
                affection = clamp(affection - intensity / 30); // 轻微降低依赖
            }
        }
    }

    // Getters and Setters
    public int getPlayfulness() { return playfulness; }
    public void setPlayfulness(int playfulness) { this.playfulness = clamp(playfulness); }

    public int getAffection() { return affection; }
    public void setAffection(int affection) { this.affection = clamp(affection); }

    public int getIndependence() { return independence; }
    public void setIndependence(int independence) { this.independence = clamp(independence); }

    public int getCuriosity() { return curiosity; }
    public void setCuriosity(int curiosity) { this.curiosity = clamp(curiosity); }

    public int getAppetite() { return appetite; }
    public void setAppetite(int appetite) { this.appetite = clamp(appetite); }

    public int getSociability() { return sociability; }
    public void setSociability(int sociability) { this.sociability = clamp(sociability); }

    public int getIntelligence() { return intelligence; }
    public void setIntelligence(int intelligence) { this.intelligence = clamp(intelligence); }

    public int getEnergy() { return energy; }
    public void setEnergy(int energy) { this.energy = clamp(energy); }
}