package com.example.aiinterviewassistant.model;

/**
 * 宠物动作枚举
 * 定义玩家可以对宠物执行的各种互动动作
 */
public enum PetAction {
    // 基础照顾动作
    FEED("喂食", "🍽️", "给宠物喂食，增加饱食度", 
         new StatEffect("hunger", 25, "happiness", 5)),
    
    CLEAN("清洁", "🛁", "给宠物洗澡，增加清洁度", 
          new StatEffect("cleanliness", 30, "happiness", 3)),
    
    PLAY("玩耍", "🎾", "和宠物玩耍，增加快乐度但消耗能量", 
         new StatEffect("happiness", 20, "energy", -15)),
    
    SLEEP("休息", "💤", "让宠物休息，恢复能量", 
          new StatEffect("energy", 40, "health", 5)),
    
    // 互动动作
    PET("抚摸", "✋", "轻柔地抚摸宠物，增加快乐度", 
        new StatEffect("happiness", 10, "health", 2)),
    
    TALK("聊天", "💬", "和宠物说话，增加快乐度", 
         new StatEffect("happiness", 8)),
    
    // 特殊动作
    MEDICINE("治疗", "💊", "给生病的宠物治疗", 
             new StatEffect("health", 30, "happiness", -5)),
    
    EXERCISE("运动", "🏃", "带宠物运动，增加健康度", 
             new StatEffect("health", 15, "energy", -10, "happiness", 10)),
    
    // 喂食特殊食物
    FEED_TREAT("给零食", "🍪", "给宠物特殊零食，大幅增加快乐度", 
               new StatEffect("happiness", 25, "hunger", 10)),
    
    FEED_MEDICINE("喂药", "💉", "给宠物喂药，恢复健康", 
                  new StatEffect("health", 25, "happiness", -8));

    private final String displayName;
    private final String emoji;
    private final String description;
    private final StatEffect effect;

    PetAction(String displayName, String emoji, String description, StatEffect effect) {
        this.displayName = displayName;
        this.emoji = emoji;
        this.description = description;
        this.effect = effect;
    }

    /**
     * 检查动作是否可执行
     */
    public boolean canExecute(Pet pet) {
        PetStats stats = pet.getStats();
        
        // 宠物睡觉时不能执行某些动作
        if (pet.isAsleep() && this != SLEEP) {
            return false;
        }
        
        // 特定条件检查
        return switch (this) {
            case SLEEP -> stats.getEnergy() < 90; // 不累的时候不能睡觉
            case MEDICINE, FEED_MEDICINE -> stats.getHealth() < 80; // 健康时不需要治疗
            case FEED -> stats.getHunger() < 90; // 不饿的时候不需要喂食
            case CLEAN -> stats.getCleanliness() < 90; // 不脏的时候不需要清洁
            case PLAY, EXERCISE -> stats.getEnergy() > 20; // 太累不能玩耍运动
            default -> true; // 其他动作都可以执行
        };
    }

    /**
     * 获取动作执行消息
     */
    public String getExecuteMessage(Pet pet) {
        return switch (this) {
            case FEED -> pet.getName() + " 开心地吃着美味的食物！" + emoji;
            case CLEAN -> pet.getName() + " 在洗澡时很配合，现在干净多了！" + emoji;
            case PLAY -> pet.getName() + " 和你玩得很开心！" + emoji;
            case SLEEP -> pet.getName() + " 安静地睡着了..." + emoji;
            case PET -> pet.getName() + " 享受着你的抚摸！" + emoji;
            case TALK -> pet.getName() + " 专注地听你说话！" + emoji;
            case MEDICINE -> pet.getName() + " 虽然不喜欢，但还是吃了药。" + emoji;
            case EXERCISE -> pet.getName() + " 运动后显得更有活力！" + emoji;
            case FEED_TREAT -> pet.getName() + " 超级喜欢这个零食！" + emoji;
            case FEED_MEDICINE -> pet.getName() + " 勇敢地吃下了苦苦的药。" + emoji;
        };
    }

    // Getters
    public String getDisplayName() { return displayName; }
    public String getEmoji() { return emoji; }
    public String getDescription() { return description; }
    public StatEffect getEffect() { return effect; }

    /**
     * 状态效果内部类
     */
    public static class StatEffect {
        private final String[] statNames;
        private final int[] values;

        public StatEffect(String stat1, int value1) {
            this.statNames = new String[]{stat1};
            this.values = new int[]{value1};
        }

        public StatEffect(String stat1, int value1, String stat2, int value2) {
            this.statNames = new String[]{stat1, stat2};
            this.values = new int[]{value1, value2};
        }

        public StatEffect(String stat1, int value1, String stat2, int value2, String stat3, int value3) {
            this.statNames = new String[]{stat1, stat2, stat3};
            this.values = new int[]{value1, value2, value3};
        }

        /**
         * 应用效果到宠物状态
         */
        public void applyTo(PetStats stats) {
            for (int i = 0; i < statNames.length; i++) {
                String statName = statNames[i];
                int currentValue = getCurrentStatValue(stats, statName);
                stats.updateStat(statName, currentValue + values[i]);
            }
        }

        private int getCurrentStatValue(PetStats stats, String statName) {
            return switch (statName.toLowerCase()) {
                case "hunger" -> stats.getHunger();
                case "cleanliness" -> stats.getCleanliness();
                case "happiness" -> stats.getHappiness();
                case "energy" -> stats.getEnergy();
                case "health" -> stats.getHealth();
                default -> 0;
            };
        }

        public String[] getStatNames() { return statNames; }
        public int[] getValues() { return values; }
    }
}
