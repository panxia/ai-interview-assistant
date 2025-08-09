package com.example.aiinterviewassistant.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 宠物实体类
 * 表示用户的虚拟宠物，整合外观、性格、动画等系统
 */
public class Pet {
    private String id;
    private String name;
    private PetType type;
    private PetStats stats;
    
    // 新增系统
    private PetAppearance appearance;
    private PetPersonality personality;
    private PetAnimation animation;
    
    // 基础属性
    private String color; // 保留兼容性
    private LocalDateTime birthDate;
    private LocalDateTime lastInteraction;
    private boolean isAsleep;
    private String mood;
    
    // 成长阶段
    private GrowthStage growthStage;
    
    /**
     * 成长阶段枚举
     */
    public enum GrowthStage {
        BABY("幼儿期", 0, 7, "小巧可爱，需要更多照顾"),
        CHILD("少年期", 8, 30, "活泼好动，学习能力强"),
        TEEN("青年期", 31, 90, "独立自主，技能成熟"),
        ADULT("成年期", 91, Integer.MAX_VALUE, "稳重可靠，智慧增长");
        
        private final String displayName;
        private final int minDays;
        private final int maxDays;
        private final String description;
        
        GrowthStage(String displayName, int minDays, int maxDays, String description) {
            this.displayName = displayName;
            this.minDays = minDays;
            this.maxDays = maxDays;
            this.description = description;
        }
        
        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
        
        public static GrowthStage fromAge(long ageInDays) {
            for (GrowthStage stage : values()) {
                if (ageInDays >= stage.minDays && ageInDays <= stage.maxDays) {
                    return stage;
                }
            }
            return ADULT;
        }
    }

    // 构造函数
    public Pet() {
        this.id = UUID.randomUUID().toString();
        this.stats = new PetStats();
        this.appearance = new PetAppearance();
        this.personality = new PetPersonality();
        this.animation = new PetAnimation();
        this.birthDate = LocalDateTime.now();
        this.lastInteraction = LocalDateTime.now();
        this.isAsleep = false;
        this.color = "default";
        this.growthStage = GrowthStage.BABY;
    }

    public Pet(String name, PetType type) {
        this();
        this.name = name;
        this.type = type;
        this.mood = "curious";
        
        // 根据宠物类型设置默认外观
        applyTypeDefaults(type);
    }
    
    /**
     * 根据宠物类型应用默认设置
     */
    private void applyTypeDefaults(PetType type) {
        switch (type) {
            case CAT -> {
                appearance.setEarStyle(PetAppearance.EarStyle.POINTED);
                appearance.setPrimaryColor("#FFA500");
                personality.setType(PetPersonality.PersonalityType.INDEPENDENT);
            }
            case DOG -> {
                appearance.setEarStyle(PetAppearance.EarStyle.DROOPY);
                appearance.setPrimaryColor("#8B4513");
                personality.setType(PetPersonality.PersonalityType.AFFECTIONATE);
            }
            case RABBIT -> {
                appearance.setEarStyle(PetAppearance.EarStyle.BUNNY);
                appearance.setPrimaryColor("#FFFFFF");
                personality.setType(PetPersonality.PersonalityType.TIMID);
            }
            case HAMSTER -> {
                appearance.setEarStyle(PetAppearance.EarStyle.ROUND);
                appearance.setPrimaryColor("#DEB887");
                personality.setType(PetPersonality.PersonalityType.CURIOUS);
            }
            case BIRD -> {
                appearance.setEarStyle(PetAppearance.EarStyle.NONE);
                appearance.setPrimaryColor("#FFFF00");
                personality.setType(PetPersonality.PersonalityType.PLAYFUL);
            }
            case DRAGON -> {
                appearance.setEarStyle(PetAppearance.EarStyle.POINTED);
                appearance.setPrimaryColor("#800080");
                appearance.setHasGlow(true);
                personality.setType(PetPersonality.PersonalityType.BRAVE);
            }
        }
    }

    /**
     * 计算宠物年龄（天数）
     */
    public long getAgeInDays() {
        return java.time.Duration.between(birthDate, LocalDateTime.now()).toDays();
    }
    
    /**
     * 更新成长阶段
     */
    public void updateGrowthStage() {
        long age = getAgeInDays();
        GrowthStage newStage = GrowthStage.fromAge(age);
        
        if (newStage != growthStage) {
            growthStage = newStage;
            
            // 成长阶段变化时的特殊处理
            switch (newStage) {
                case CHILD -> {
                    // 解锁新动画
                    animation.setAnimationFlag("can_jump", true);
                    stats.updateStat("energy", stats.getEnergy() + 10);
                }
                case TEEN -> {
                    // 解锁更多动画
                    animation.setAnimationFlag("can_dance", true);
                    personality.setIntelligence(personality.getIntelligence() + 10);
                }
                case ADULT -> {
                    // 成年特权
                    animation.setAnimationFlag("can_teach", true);
                    stats.updateStat("health", 100);
                }
            }
        }
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
        
        // 互动时调整性格
        if (animation.getCurrentState() == PetAnimation.AnimationState.BEING_PETTED) {
            personality.adjustPersonality("pet", 1);
        }
    }
    
    /**
     * 执行智能行为（AI决策）
     */
    public void performAutonomousBehavior() {
        PetPersonality.BehaviorTendency tendency = personality.calculateBehaviorTendency();
        
        // 根据性格倾向决定行为
        if (Math.random() < tendency.autonomousBehavior) {
            // 自主行为
            if (stats.getHunger() < 30) {
                // 饿了，走向食盆
                moveToObject("food_bowl");
            } else if (stats.getEnergy() < 20) {
                // 累了，走向床
                moveToObject("pet_bed");
            } else if (stats.getHappiness() < 40) {
                // 无聊，寻找玩具
                moveToObject("toy");
            } else if (Math.random() < tendency.movementFrequency) {
                // 随机移动
                randomMove();
            }
        }
    }
    
    /**
     * 移动到特定物品
     */
    private void moveToObject(String objectType) {
        // 这里应该查找环境中的物品位置
        // 暂时使用随机位置模拟
        PetAnimation.Position target = new PetAnimation.Position(
            (float)(Math.random() * 100),
            (float)(Math.random() * 100)
        );
        animation.moveTo(target);
    }
    
    /**
     * 随机移动
     */
    private void randomMove() {
        PetAnimation.Position current = animation.getCurrentPosition();
        PetAnimation.Position target = new PetAnimation.Position(
            current.x + (float)(Math.random() * 40 - 20),
            current.y + (float)(Math.random() * 40 - 20)
        );
        animation.moveTo(target);
    }

    /**
     * 获取宠物当前状态描述
     */
    public String getStatusDescription() {
        if (isAsleep) {
            return name + " 正在睡觉 😴";
        }
        
        // 检查动画状态
        PetAnimation.AnimationState animState = animation.getCurrentState();
        if (animState == PetAnimation.AnimationState.EATING) {
            return name + " 正在吃东西 🍽️";
        } else if (animState == PetAnimation.AnimationState.PLAYING) {
            return name + " 正在玩耍 🎾";
        } else if (animState == PetAnimation.AnimationState.DANCING) {
            return name + " 正在跳舞 💃";
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
     * 获取完整的宠物描述
     */
    public String getFullDescription() {
        StringBuilder desc = new StringBuilder();
        desc.append("【").append(name).append("】\n");
        desc.append("种类：").append(type.getDisplayName()).append("\n");
        desc.append("成长阶段：").append(growthStage.getDisplayName()).append("\n");
        desc.append("外观：").append(appearance.getDescription()).append("\n");
        desc.append("性格：").append(personality.getPersonalityDescription()).append("\n");
        desc.append("当前状态：").append(getStatusDescription());
        return desc.toString();
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
        
        // 根据性格添加特殊动作
        if (personality.getPlayfulness() > 70) {
            actions.add("跳舞");
            actions.add("追逐");
        }
        
        if (personality.getAffection() > 70) {
            actions.add("拥抱");
        }
        
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
    
    public PetAppearance getAppearance() { return appearance; }
    public void setAppearance(PetAppearance appearance) { this.appearance = appearance; }
    
    public PetPersonality getPersonality() { return personality; }
    public void setPersonality(PetPersonality personality) { this.personality = personality; }
    
    public PetAnimation getAnimation() { return animation; }
    public void setAnimation(PetAnimation animation) { this.animation = animation; }

    public String getColor() { return color; }
    public void setColor(String color) { 
        this.color = color;
        // 同步到外观系统
        if (appearance != null) {
            appearance.setPrimaryColor(color);
        }
    }

    public LocalDateTime getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDateTime birthDate) { this.birthDate = birthDate; }

    public LocalDateTime getLastInteraction() { return lastInteraction; }
    public void setLastInteraction(LocalDateTime lastInteraction) { this.lastInteraction = lastInteraction; }

    public boolean isAsleep() { return isAsleep; }
    public void setAsleep(boolean asleep) { 
        isAsleep = asleep;
        // 同步到动画系统
        if (animation != null) {
            animation.changeState(asleep ? 
                PetAnimation.AnimationState.SLEEPING : 
                PetAnimation.AnimationState.YAWNING);
        }
    }

    public String getMood() { return mood; }
    public void setMood(String mood) { this.mood = mood; }
    
    public GrowthStage getGrowthStage() { 
        updateGrowthStage();
        return growthStage; 
    }
}
