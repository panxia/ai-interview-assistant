package com.example.aiinterviewassistant.model;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 成就系统
 * 玩家可以通过完成特定任务获得成就和奖励
 */
public class Achievement {
    private String id;
    private String name;
    private String description;
    private String emoji;
    private AchievementType type;
    private int targetValue; // 目标值
    private int currentProgress; // 当前进度
    private boolean unlocked; // 是否已解锁
    private LocalDateTime unlockedDate;
    private Reward reward; // 奖励

    // 构造函数
    public Achievement() {}

    public Achievement(String id, String name, String description, String emoji, 
                      AchievementType type, int targetValue, Reward reward) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.emoji = emoji;
        this.type = type;
        this.targetValue = targetValue;
        this.currentProgress = 0;
        this.unlocked = false;
        this.reward = reward;
    }

    /**
     * 成就类型枚举
     */
    public enum AchievementType {
        FEED_COUNT("喂食次数"),
        PLAY_COUNT("玩耍次数"),
        LEVEL_REACHED("等级达成"),
        COINS_EARNED("金币获得"),
        DAYS_OWNED("养育天数"),
        ITEMS_BOUGHT("购买物品"),
        GAMES_WON("小游戏胜利"),
        PET_HAPPINESS("宠物快乐度"),
        PERFECT_CARE("完美照顾");

        private final String displayName;

        AchievementType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() { return displayName; }
    }

    /**
     * 奖励类
     */
    public static class Reward {
        private int coins;
        private int experience;
        private String specialItem; // 特殊物品ID
        private String title; // 称号

        public Reward(int coins, int experience) {
            this.coins = coins;
            this.experience = experience;
        }

        public Reward(int coins, int experience, String specialItem, String title) {
            this.coins = coins;
            this.experience = experience;
            this.specialItem = specialItem;
            this.title = title;
        }

        // Getters and Setters
        public int getCoins() { return coins; }
        public void setCoins(int coins) { this.coins = coins; }
        public int getExperience() { return experience; }
        public void setExperience(int experience) { this.experience = experience; }
        public String getSpecialItem() { return specialItem; }
        public void setSpecialItem(String specialItem) { this.specialItem = specialItem; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
    }

    /**
     * 更新成就进度
     */
    public boolean updateProgress(int value) {
        if (unlocked) return false;
        
        this.currentProgress = Math.min(targetValue, this.currentProgress + value);
        
        if (this.currentProgress >= targetValue && !unlocked) {
            this.unlocked = true;
            this.unlockedDate = LocalDateTime.now();
            return true; // 成就解锁
        }
        
        return false;
    }

    /**
     * 设置进度（用于绝对值类型的成就）
     */
    public boolean setProgress(int value) {
        if (unlocked) return false;
        
        this.currentProgress = Math.min(targetValue, value);
        
        if (this.currentProgress >= targetValue && !unlocked) {
            this.unlocked = true;
            this.unlockedDate = LocalDateTime.now();
            return true; // 成就解锁
        }
        
        return false;
    }

    /**
     * 获取进度百分比
     */
    public double getProgressPercentage() {
        return targetValue > 0 ? (double) currentProgress / targetValue * 100 : 0;
    }

    /**
     * 获取进度描述
     */
    public String getProgressDescription() {
        return currentProgress + "/" + targetValue;
    }

    /**
     * 预定义成就列表
     */
    public static List<Achievement> getDefaultAchievements() {
        return Arrays.asList(
            // 喂食相关
            new Achievement("first_feed", "初次喂食", "第一次给宠物喂食", "🍽️",
                AchievementType.FEED_COUNT, 1, new Reward(10, 5)),
            new Achievement("feed_master", "喂食大师", "累计喂食50次", "🥇",
                AchievementType.FEED_COUNT, 50, new Reward(100, 50, "premium_food", "喂食大师")),

            // 玩耍相关
            new Achievement("first_play", "初次玩耍", "第一次和宠物玩耍", "🎾",
                AchievementType.PLAY_COUNT, 1, new Reward(10, 5)),
            new Achievement("play_enthusiast", "玩耍达人", "累计玩耍30次", "🎮",
                AchievementType.PLAY_COUNT, 30, new Reward(80, 40)),

            // 等级相关
            new Achievement("level_5", "初级训练师", "宠物达到5级", "⭐",
                AchievementType.LEVEL_REACHED, 5, new Reward(50, 25)),
            new Achievement("level_10", "中级训练师", "宠物达到10级", "⭐⭐",
                AchievementType.LEVEL_REACHED, 10, new Reward(100, 50)),
            new Achievement("level_20", "高级训练师", "宠物达到20级", "⭐⭐⭐",
                AchievementType.LEVEL_REACHED, 20, new Reward(200, 100, "crown", "高级训练师")),

            // 金币相关
            new Achievement("first_coin", "初次收入", "第一次获得金币", "💰",
                AchievementType.COINS_EARNED, 1, new Reward(20, 10)),
            new Achievement("rich_player", "富翁", "累计获得1000金币", "💎",
                AchievementType.COINS_EARNED, 1000, new Reward(500, 200)),

            // 时间相关
            new Achievement("one_week", "一周陪伴", "养育宠物满7天", "📅",
                AchievementType.DAYS_OWNED, 7, new Reward(100, 50)),
            new Achievement("one_month", "一月陪伴", "养育宠物满30天", "🗓️",
                AchievementType.DAYS_OWNED, 30, new Reward(300, 150, "castle", "忠实伙伴")),

            // 购物相关
            new Achievement("first_purchase", "初次购买", "第一次购买物品", "🛒",
                AchievementType.ITEMS_BOUGHT, 1, new Reward(15, 8)),
            new Achievement("shopaholic", "购物狂", "购买20件物品", "🛍️",
                AchievementType.ITEMS_BOUGHT, 20, new Reward(150, 75)),

            // 小游戏相关
            new Achievement("first_game_win", "初次胜利", "第一次赢得小游戏", "🏆",
                AchievementType.GAMES_WON, 1, new Reward(25, 15)),
            new Achievement("game_master", "游戏大师", "赢得20次小游戏", "🎖️",
                AchievementType.GAMES_WON, 20, new Reward(200, 100)),

            // 宠物状态相关
            new Achievement("happy_pet", "快乐宠物", "宠物快乐度达到100", "😄",
                AchievementType.PET_HAPPINESS, 100, new Reward(50, 25)),

            // 特殊成就
            new Achievement("perfect_day", "完美一天", "一天内所有状态保持在80以上", "✨",
                AchievementType.PERFECT_CARE, 1, new Reward(200, 100, "laser_pointer", "完美照顾者"))
        );
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getEmoji() { return emoji; }
    public void setEmoji(String emoji) { this.emoji = emoji; }

    public AchievementType getType() { return type; }
    public void setType(AchievementType type) { this.type = type; }

    public int getTargetValue() { return targetValue; }
    public void setTargetValue(int targetValue) { this.targetValue = targetValue; }

    public int getCurrentProgress() { return currentProgress; }
    public void setCurrentProgress(int currentProgress) { this.currentProgress = currentProgress; }

    public boolean isUnlocked() { return unlocked; }
    public void setUnlocked(boolean unlocked) { this.unlocked = unlocked; }

    public LocalDateTime getUnlockedDate() { return unlockedDate; }
    public void setUnlockedDate(LocalDateTime unlockedDate) { this.unlockedDate = unlockedDate; }

    public Reward getReward() { return reward; }
    public void setReward(Reward reward) { this.reward = reward; }
}
