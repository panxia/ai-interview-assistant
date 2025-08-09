package com.example.aiinterviewassistant.model;

/**
 * 宠物性格特征系统
 * 影响宠物的行为模式和游戏体验
 */
public class PetPersonality {
    
    // 性格特征值 (0-100)
    private int playfulness = 50;    // 活泼度 - 影响移动频率和主动玩耍
    private int affection = 50;      // 亲人度 - 影响互动反应和依恋程度
    private int independence = 50;   // 独立性 - 影响自主行为和独处能力
    private int curiosity = 50;      // 好奇心 - 影响探索行为和学习速度
    private int appetite = 50;       // 食欲 - 影响饮食习惯和饥饿速度
    private int energy = 50;         // 精力值 - 影响活动持续时间
    private int intelligence = 50;   // 智力 - 影响学习能力和游戏表现
    private int bravery = 50;        // 勇敢度 - 影响对新事物的接受度
    
    // 性格类型
    private PersonalityType type;
    
    // 性格描述
    private String customDescription;
    
    public enum PersonalityType {
        PLAYFUL("活泼型", "喜欢玩耍，精力充沛，总是寻找乐趣"),
        LAZY("慵懒型", "喜欢睡觉，动作缓慢，享受悠闲时光"),
        CURIOUS("好奇型", "喜欢探索，对新事物充满兴趣"),
        AFFECTIONATE("亲人型", "非常依恋主人，喜欢被抚摸和陪伴"),
        INDEPENDENT("独立型", "喜欢独处，自主性强，不太需要陪伴"),
        INTELLIGENT("聪明型", "学习能力强，游戏表现优秀"),
        TIMID("胆小型", "容易害怕，需要更多安慰和鼓励"),
        BRAVE("勇敢型", "不畏惧挑战，喜欢冒险"),
        FOODIE("吃货型", "对食物特别感兴趣，容易被美食吸引"),
        BALANCED("均衡型", "各方面都比较平衡，适应性强");
        
        private final String displayName;
        private final String description;
        
        PersonalityType(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }
        
        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
    }
    
    // 构造函数
    public PetPersonality() {
        this.type = PersonalityType.BALANCED;
    }
    
    public PetPersonality(PersonalityType type) {
        this.type = type;
        applyPersonalityType(type);
    }
    
    /**
     * 根据性格类型设置特征值
     */
    private void applyPersonalityType(PersonalityType type) {
        switch (type) {
            case PLAYFUL -> {
                this.playfulness = 80;
                this.energy = 75;
                this.curiosity = 60;
            }
            case LAZY -> {
                this.playfulness = 30;
                this.energy = 30;
                this.independence = 60;
            }
            case CURIOUS -> {
                this.curiosity = 85;
                this.intelligence = 65;
                this.bravery = 60;
            }
            case AFFECTIONATE -> {
                this.affection = 85;
                this.independence = 30;
                this.playfulness = 60;
            }
            case INDEPENDENT -> {
                this.independence = 80;
                this.affection = 40;
                this.bravery = 65;
            }
            case INTELLIGENT -> {
                this.intelligence = 85;
                this.curiosity = 70;
                this.independence = 55;
            }
            case TIMID -> {
                this.bravery = 25;
                this.affection = 70;
                this.independence = 35;
            }
            case BRAVE -> {
                this.bravery = 85;
                this.curiosity = 70;
                this.playfulness = 65;
            }
            case FOODIE -> {
                this.appetite = 85;
                this.affection = 60;
                this.playfulness = 55;
            }
            case BALANCED -> {
                // 所有值保持默认50
            }
        }
    }
    
    /**
     * 获取性格描述文本
     */
    public String getPersonalityDescription() {
        if (customDescription != null && !customDescription.isEmpty()) {
            return customDescription;
        }
        
        StringBuilder desc = new StringBuilder();
        desc.append("这是一只");
        
        // 根据特征值生成描述
        if (playfulness > 70) {
            desc.append("非常活泼");
        } else if (playfulness < 30) {
            desc.append("比较安静");
        }
        
        if (affection > 70) {
            if (desc.length() > 4) desc.append("、");
            desc.append("很亲人");
        } else if (affection < 30) {
            if (desc.length() > 4) desc.append("、");
            desc.append("比较独立");
        }
        
        if (curiosity > 70) {
            if (desc.length() > 4) desc.append("、");
            desc.append("充满好奇心");
        }
        
        if (intelligence > 70) {
            if (desc.length() > 4) desc.append("、");
            desc.append("很聪明");
        }
        
        if (appetite > 70) {
            if (desc.length() > 4) desc.append("、");
            desc.append("爱吃");
        }
        
        desc.append("的宠物。");
        
        // 添加行为特点
        if (playfulness > 70 && energy > 70) {
            desc.append("它喜欢到处跑动和玩耍，");
        }
        
        if (affection > 70) {
            desc.append("非常依恋主人，喜欢被抚摸，");
        }
        
        if (curiosity > 70) {
            desc.append("对新事物充满兴趣，喜欢探索环境，");
        }
        
        if (independence > 70) {
            desc.append("能够独自玩耍，不需要太多陪伴，");
        }
        
        if (desc.toString().endsWith("，")) {
            desc.setLength(desc.length() - 1);
            desc.append("。");
        }
        
        return desc.toString();
    }
    
    /**
     * 计算行为倾向
     */
    public BehaviorTendency calculateBehaviorTendency() {
        BehaviorTendency tendency = new BehaviorTendency();
        
        // 移动频率
        tendency.movementFrequency = (playfulness * 0.5f + energy * 0.3f + curiosity * 0.2f) / 100f;
        
        // 互动响应度
        tendency.interactionResponse = (affection * 0.6f + playfulness * 0.4f) / 100f;
        
        // 自主行为频率
        tendency.autonomousBehavior = (independence * 0.5f + curiosity * 0.3f + intelligence * 0.2f) / 100f;
        
        // 学习速度
        tendency.learningSpeed = (intelligence * 0.6f + curiosity * 0.4f) / 100f;
        
        // 饥饿速度
        tendency.hungerRate = (appetite * 0.7f + energy * 0.3f) / 100f;
        
        // 疲劳速度
        tendency.fatigueRate = (100 - energy) * 0.01f;
        
        // 恐惧反应
        tendency.fearResponse = (100 - bravery) * 0.01f;
        
        return tendency;
    }
    
    /**
     * 行为倾向类
     */
    public static class BehaviorTendency {
        public float movementFrequency;      // 移动频率 0-1
        public float interactionResponse;    // 互动响应度 0-1
        public float autonomousBehavior;     // 自主行为频率 0-1
        public float learningSpeed;          // 学习速度 0-1
        public float hungerRate;            // 饥饿速度 0-1
        public float fatigueRate;           // 疲劳速度 0-1
        public float fearResponse;          // 恐惧反应 0-1
    }
    
    /**
     * 性格成长 - 根据互动调整性格
     */
    public void adjustPersonality(String interaction, int amount) {
        switch (interaction) {
            case "play" -> {
                playfulness = Math.min(100, playfulness + amount);
                energy = Math.min(100, energy + amount / 2);
            }
            case "feed" -> {
                appetite = Math.min(100, appetite + amount / 2);
                affection = Math.min(100, affection + amount / 3);
            }
            case "pet" -> {
                affection = Math.min(100, affection + amount);
                independence = Math.max(0, independence - amount / 3);
            }
            case "train" -> {
                intelligence = Math.min(100, intelligence + amount);
                curiosity = Math.min(100, curiosity + amount / 2);
            }
            case "explore" -> {
                curiosity = Math.min(100, curiosity + amount);
                bravery = Math.min(100, bravery + amount / 2);
            }
            case "alone" -> {
                independence = Math.min(100, independence + amount);
                affection = Math.max(0, affection - amount / 3);
            }
        }
    }
    
    // Getters and Setters
    public int getPlayfulness() { return playfulness; }
    public void setPlayfulness(int playfulness) { 
        this.playfulness = Math.max(0, Math.min(100, playfulness)); 
    }
    
    public int getAffection() { return affection; }
    public void setAffection(int affection) { 
        this.affection = Math.max(0, Math.min(100, affection)); 
    }
    
    public int getIndependence() { return independence; }
    public void setIndependence(int independence) { 
        this.independence = Math.max(0, Math.min(100, independence)); 
    }
    
    public int getCuriosity() { return curiosity; }
    public void setCuriosity(int curiosity) { 
        this.curiosity = Math.max(0, Math.min(100, curiosity)); 
    }
    
    public int getAppetite() { return appetite; }
    public void setAppetite(int appetite) { 
        this.appetite = Math.max(0, Math.min(100, appetite)); 
    }
    
    public int getEnergy() { return energy; }
    public void setEnergy(int energy) { 
        this.energy = Math.max(0, Math.min(100, energy)); 
    }
    
    public int getIntelligence() { return intelligence; }
    public void setIntelligence(int intelligence) { 
        this.intelligence = Math.max(0, Math.min(100, intelligence)); 
    }
    
    public int getBravery() { return bravery; }
    public void setBravery(int bravery) { 
        this.bravery = Math.max(0, Math.min(100, bravery)); 
    }
    
    public PersonalityType getType() { return type; }
    public void setType(PersonalityType type) { 
        this.type = type; 
        applyPersonalityType(type);
    }
    
    public String getCustomDescription() { return customDescription; }
    public void setCustomDescription(String customDescription) { 
        this.customDescription = customDescription; 
    }
}