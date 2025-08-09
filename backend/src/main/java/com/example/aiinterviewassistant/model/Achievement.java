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
     * 成就类型枚举 - 增强版
     */
    public enum AchievementType {
        // 照顾类成就
        FEED_COUNT("喂食次数"),
        PLAY_COUNT("玩耍次数"),
        CLEAN_COUNT("清洁次数"),
        CONTINUOUS_CARE("连续照顾"),
        PERFECT_CARE("完美照顾"),
        HEALTH_MAINTAINED("健康维护"),
        
        // 游戏类成就
        GAMES_WON("小游戏胜利"),
        PERFECT_GAME("游戏满分"),
        GAME_STREAK("连胜记录"),
        GAME_TIME("游戏时长"),
        COMBO_ACHIEVEMENT("连击成就"),
        
        // 收集类成就
        ITEMS_BOUGHT("购买物品"),
        DECORATIONS_COLLECTED("装饰收集"),
        FURNITURE_COLLECTION("家具收藏"),
        PHOTO_COLLECTION("照片收集"),
        SPECIAL_ITEMS("特殊物品"),
        
        // 成长类成就
        LEVEL_REACHED("等级达成"),
        EXPERIENCE_GAINED("经验获得"),
        SKILL_MASTERED("技能掌握"),
        GROWTH_STAGE("成长阶段"),
        
        // 特殊类成就
        WEATHER_ACTIVITIES("天气活动"),
        INTERACTION_COUNT("互动次数"),
        PET_HAPPINESS("宠物快乐度"),
        CUSTOMIZATION("外观自定义"),
        ROOM_DECORATION("房间装饰"),
        SOCIAL_ACTIVITIES("社交活动"),
        
        // 时间类成就
        DAYS_OWNED("养育天数"),
        LOGIN_STREAK("连续登录"),
        ACTIVITY_TIME("活动时间"),
        
        // 经济类成就
        COINS_EARNED("金币获得"),
        COINS_SPENT("金币消费"),
        TREASURE_FOUND("寻宝成功");

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
     * 预定义成就列表 - 全面增强版
     */
    public static List<Achievement> getDefaultAchievements() {
        return Arrays.asList(
            // 👥 照顾类成就
            new Achievement("first_feed", "初次喂食", "第一次给宠物喂食", "🍽️",
                AchievementType.FEED_COUNT, 1, new Reward(10, 5)),
            new Achievement("feed_master", "喂食大师", "累计喂食50次", "🥇",
                AchievementType.FEED_COUNT, 50, new Reward(100, 50, "premium_food", "喂食大师")),
            new Achievement("feeding_champion", "喂食冠军", "累计喂食200次", "👑",
                AchievementType.FEED_COUNT, 200, new Reward(300, 150, "golden_bowl", "喂食冠军")),

            new Achievement("first_clean", "初次清洁", "第一次给宠物清洁", "🛁",
                AchievementType.CLEAN_COUNT, 1, new Reward(10, 5)),
            new Achievement("cleanliness_expert", "清洁专家", "累计清洁30次", "🧽",
                AchievementType.CLEAN_COUNT, 30, new Reward(80, 40)),

            new Achievement("care_streak_7", "一周守护", "连续照顾7天", "📅",
                AchievementType.CONTINUOUS_CARE, 7, new Reward(100, 50)),
            new Achievement("care_streak_30", "一月守护", "连续照顾30天", "🗓️",
                AchievementType.CONTINUOUS_CARE, 30, new Reward(300, 150, "loyalty_medal", "忠实守护者")),
            new Achievement("care_streak_365", "一年守护", "连续照顾365天", "🎊",
                AchievementType.CONTINUOUS_CARE, 365, new Reward(1000, 500, "eternal_crown", "永恒守护者")),

            new Achievement("perfect_day", "完美一天", "一天内所有状态保持在80以上", "✨",
                AchievementType.PERFECT_CARE, 1, new Reward(200, 100, "laser_pointer", "完美照顾者")),
            new Achievement("perfect_week", "完美一周", "一周内每天都完美照顾", "🌟",
                AchievementType.PERFECT_CARE, 7, new Reward(500, 250, "perfect_care_trophy", "完美护理师")),

            new Achievement("never_sick", "健康卫士", "宠物从未生病", "🏥",
                AchievementType.HEALTH_MAINTAINED, 1, new Reward(400, 200, "health_shield", "健康卫士")),

            // 🎮 游戏类成就
            new Achievement("first_game_win", "初次胜利", "第一次赢得小游戏", "🏆",
                AchievementType.GAMES_WON, 1, new Reward(25, 15)),
            new Achievement("game_master", "游戏大师", "赢得20次小游戏", "🎖️",
                AchievementType.GAMES_WON, 20, new Reward(200, 100)),
            new Achievement("game_legend", "游戏传奇", "赢得100次小游戏", "👑",
                AchievementType.GAMES_WON, 100, new Reward(600, 300, "game_crown", "游戏传奇")),

            new Achievement("perfect_score", "满分高手", "获得单局游戏满分", "💯",
                AchievementType.PERFECT_GAME, 1, new Reward(50, 30)),
            new Achievement("perfection_master", "完美大师", "获得10次满分", "🌟",
                AchievementType.PERFECT_GAME, 10, new Reward(300, 150, "perfect_star", "完美大师")),

            new Achievement("win_streak_5", "五连胜", "连续赢得5场游戏", "🔥",
                AchievementType.GAME_STREAK, 5, new Reward(100, 50)),
            new Achievement("win_streak_10", "十连胜", "连续赢得10场游戏", "⚡",
                AchievementType.GAME_STREAK, 10, new Reward(250, 125, "lightning_badge", "闪电连胜")),

            new Achievement("combo_master", "连击大师", "获得50连击", "💥",
                AchievementType.COMBO_ACHIEVEMENT, 50, new Reward(200, 100)),

            // 🎨 收集类成就
            new Achievement("first_purchase", "初次购买", "第一次购买物品", "🛒",
                AchievementType.ITEMS_BOUGHT, 1, new Reward(15, 8)),
            new Achievement("shopaholic", "购物狂", "购买20件物品", "🛍️",
                AchievementType.ITEMS_BOUGHT, 20, new Reward(150, 75)),
            new Achievement("collector", "收藏家", "购买50件物品", "📦",
                AchievementType.ITEMS_BOUGHT, 50, new Reward(400, 200, "collector_badge", "收藏家")),

            new Achievement("decoration_lover", "装饰爱好者", "收集10种装饰品", "🎀",
                AchievementType.DECORATIONS_COLLECTED, 10, new Reward(100, 50)),
            new Achievement("interior_designer", "室内设计师", "收集所有装饰品", "🏠",
                AchievementType.DECORATIONS_COLLECTED, 25, new Reward(500, 250, "designer_tools", "室内设计师")),

            new Achievement("furniture_fan", "家具迷", "收集10件家具", "🪑",
                AchievementType.FURNITURE_COLLECTION, 10, new Reward(120, 60)),
            new Achievement("home_stylist", "家居造型师", "收集所有家具", "🛋️",
                AchievementType.FURNITURE_COLLECTION, 15, new Reward(300, 150, "golden_sofa", "家居造型师")),

            // 🌱 成长类成就
            new Achievement("level_5", "初级训练师", "宠物达到5级", "⭐",
                AchievementType.LEVEL_REACHED, 5, new Reward(50, 25)),
            new Achievement("level_10", "中级训练师", "宠物达到10级", "⭐⭐",
                AchievementType.LEVEL_REACHED, 10, new Reward(100, 50)),
            new Achievement("level_20", "高级训练师", "宠物达到20级", "⭐⭐⭐",
                AchievementType.LEVEL_REACHED, 20, new Reward(200, 100, "crown", "高级训练师")),
            new Achievement("level_50", "传奇训练师", "宠物达到50级", "👑",
                AchievementType.LEVEL_REACHED, 50, new Reward(1000, 500, "legendary_crown", "传奇训练师")),

            new Achievement("exp_1000", "经验丰富", "获得1000经验值", "📚",
                AchievementType.EXPERIENCE_GAINED, 1000, new Reward(100, 0)),
            new Achievement("exp_master", "经验大师", "获得10000经验值", "🎓",
                AchievementType.EXPERIENCE_GAINED, 10000, new Reward(500, 0, "wisdom_book", "经验大师")),

            new Achievement("youth_stage", "青春期", "宠物进入青年期", "🌱",
                AchievementType.GROWTH_STAGE, 1, new Reward(150, 75)),
            new Achievement("adult_stage", "成年期", "宠物进入成年期", "🌳",
                AchievementType.GROWTH_STAGE, 2, new Reward(300, 150, "maturity_medal", "成熟伙伴")),

            // 💫 特殊类成就
            new Achievement("rainy_day_play", "雨天玩耍", "在雨天和宠物玩耍", "🌧️",
                AchievementType.WEATHER_ACTIVITIES, 1, new Reward(50, 25)),
            new Achievement("weather_explorer", "天气探索者", "在所有天气条件下活动", "🌈",
                AchievementType.WEATHER_ACTIVITIES, 6, new Reward(200, 100, "weather_compass", "天气探索者")),

            new Achievement("pet_whisperer", "宠物密语者", "与宠物互动1000次", "💬",
                AchievementType.INTERACTION_COUNT, 1000, new Reward(300, 150, "communication_crystal", "宠物密语者")),

            new Achievement("happy_pet", "快乐宠物", "宠物快乐度达到100", "😄",
                AchievementType.PET_HAPPINESS, 100, new Reward(50, 25)),

            new Achievement("first_customization", "时尚达人", "第一次自定义宠物外观", "🎨",
                AchievementType.CUSTOMIZATION, 1, new Reward(30, 20)),
            new Achievement("style_master", "造型大师", "自定义宠物外观10次", "👗",
                AchievementType.CUSTOMIZATION, 10, new Reward(150, 75, "bow_tie", "造型大师")),
            new Achievement("fashion_icon", "时尚偶像", "自定义宠物外观50次", "💄",
                AchievementType.CUSTOMIZATION, 50, new Reward(400, 200, "fashion_crown", "时尚偶像")),

            new Achievement("room_decorator", "房间装饰师", "装饰5个房间", "🏠",
                AchievementType.ROOM_DECORATION, 5, new Reward(200, 100)),
            new Achievement("master_decorator", "装饰大师", "完美装饰所有房间", "🎪",
                AchievementType.ROOM_DECORATION, 10, new Reward(500, 250, "decorator_tools", "装饰大师")),

            // ⏰ 时间类成就
            new Achievement("one_week", "一周陪伴", "养育宠物满7天", "📅",
                AchievementType.DAYS_OWNED, 7, new Reward(100, 50)),
            new Achievement("one_month", "一月陪伴", "养育宠物满30天", "🗓️",
                AchievementType.DAYS_OWNED, 30, new Reward(300, 150, "castle", "忠实伙伴")),
            new Achievement("six_months", "半年陪伴", "养育宠物满180天", "🎁",
                AchievementType.DAYS_OWNED, 180, new Reward(800, 400, "anniversary_gift", "半年伙伴")),
            new Achievement("one_year", "一年陪伴", "养育宠物满365天", "🎊",
                AchievementType.DAYS_OWNED, 365, new Reward(1500, 750, "yearly_trophy", "年度伙伴")),

            new Achievement("daily_login_7", "每日访问", "连续登录7天", "📱",
                AchievementType.LOGIN_STREAK, 7, new Reward(70, 35)),
            new Achievement("daily_login_30", "忠实用户", "连续登录30天", "💎",
                AchievementType.LOGIN_STREAK, 30, new Reward(300, 150, "loyalty_gem", "忠实用户")),

            // 💰 经济类成就
            new Achievement("first_coin", "初次收入", "第一次获得金币", "💰",
                AchievementType.COINS_EARNED, 1, new Reward(20, 10)),
            new Achievement("rich_player", "富翁", "累计获得1000金币", "💎",
                AchievementType.COINS_EARNED, 1000, new Reward(500, 200)),
            new Achievement("millionaire", "百万富翁", "累计获得10000金币", "👑",
                AchievementType.COINS_EARNED, 10000, new Reward(2000, 1000, "golden_crown", "百万富翁")),

            new Achievement("big_spender", "大消费者", "累计花费500金币", "🛒",
                AchievementType.COINS_SPENT, 500, new Reward(100, 50)),
            new Achievement("economic_master", "经济大师", "累计花费2000金币", "💳",
                AchievementType.COINS_SPENT, 2000, new Reward(400, 200, "gold_card", "经济大师")),

            new Achievement("treasure_hunter", "寻宝猎人", "找到第一个宝藏", "🗺️",
                AchievementType.TREASURE_FOUND, 1, new Reward(100, 50)),
            new Achievement("treasure_master", "寻宝大师", "找到10个宝藏", "💰",
                AchievementType.TREASURE_FOUND, 10, new Reward(500, 250, "treasure_map", "寻宝大师"))
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
