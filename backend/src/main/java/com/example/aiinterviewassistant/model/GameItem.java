package com.example.aiinterviewassistant.model;

/**
 * 游戏物品类
 * 表示游戏中的各种物品，如食物、玩具、装饰品等
 */
public class GameItem {
    private String id;
    private String name;
    private String emoji;
    private String description;
    private ItemType type;
    private int cost; // 购买成本（游戏币）
    private int rarity; // 稀有度 1-5
    private PetAction.StatEffect effect; // 物品效果
    private boolean isUnlocked; // 是否已解锁

    // 构造函数
    public GameItem() {}

    public GameItem(String id, String name, String emoji, String description, 
                    ItemType type, int cost, int rarity) {
        this.id = id;
        this.name = name;
        this.emoji = emoji;
        this.description = description;
        this.type = type;
        this.cost = cost;
        this.rarity = rarity;
        this.isUnlocked = rarity <= 2; // 低稀有度的物品默认解锁
    }

    /**
     * 物品类型枚举
     */
    public enum ItemType {
        FOOD("食物", "填饱宠物的肚子"),
        TOY("玩具", "增加宠物的快乐度"),
        DECORATION("装饰", "美化宠物的环境"),
        MEDICINE("药品", "治疗生病的宠物"),
        ACCESSORY("饰品", "让宠物更加可爱");

        private final String displayName;
        private final String description;

        ItemType(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }

        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
    }

    /**
     * 获取稀有度显示
     */
    public String getRarityDisplay() {
        return switch (rarity) {
            case 1 -> "普通 ⭐";
            case 2 -> "常见 ⭐⭐";
            case 3 -> "稀有 ⭐⭐⭐";
            case 4 -> "史诗 ⭐⭐⭐⭐";
            case 5 -> "传说 ⭐⭐⭐⭐⭐";
            default -> "未知";
        };
    }

    /**
     * 获取稀有度颜色
     */
    public String getRarityColor() {
        return switch (rarity) {
            case 1 -> "#808080"; // 灰色
            case 2 -> "#FFFFFF"; // 白色
            case 3 -> "#00FF00"; // 绿色
            case 4 -> "#800080"; // 紫色
            case 5 -> "#FF8000"; // 橙色
            default -> "#000000";
        };
    }

    /**
     * 检查是否可以使用该物品
     */
    public boolean canUseOn(Pet pet) {
        if (!isUnlocked) return false;
        
        PetStats stats = pet.getStats();
        return switch (type) {
            case FOOD -> stats.getHunger() < 100;
            case MEDICINE -> stats.getHealth() < 80;
            case TOY -> stats.getHappiness() < 100 && stats.getEnergy() > 20;
            default -> true;
        };
    }

    /**
     * 使用物品
     */
    public String useOn(Pet pet) {
        if (!canUseOn(pet)) {
            return pet.getName() + " 现在不需要这个物品。";
        }

        if (effect != null) {
            effect.applyTo(pet.getStats());
        }

        pet.updateLastInteraction();
        
        return switch (type) {
            case FOOD -> pet.getName() + " 享用了美味的 " + name + "！" + emoji;
            case TOY -> pet.getName() + " 玩 " + name + " 玩得很开心！" + emoji;
            case MEDICINE -> pet.getName() + " 使用了 " + name + "，感觉好多了。" + emoji;
            case DECORATION -> "为 " + pet.getName() + " 添加了 " + name + "！" + emoji;
            case ACCESSORY -> pet.getName() + " 戴上了 " + name + "，看起来更可爱了！" + emoji;
        };
    }

    // 预定义的游戏物品
    public static GameItem[] getDefaultItems() {
        return new GameItem[]{
            // 食物类
            createFood("apple", "苹果", "🍎", "新鲜的苹果，增加饱食度", 10, 1,
                new PetAction.StatEffect("hunger", 20)),
            createFood("fish", "小鱼", "🐟", "新鲜的小鱼，猫咪最爱", 15, 2,
                new PetAction.StatEffect("hunger", 25, "happiness", 5)),
            createFood("carrot", "胡萝卜", "🥕", "脆嫩的胡萝卜，兔子的最爱", 12, 1,
                new PetAction.StatEffect("hunger", 22, "health", 3)),
            createFood("premium_food", "高级宠物粮", "🍖", "营养丰富的高级食物", 50, 3,
                new PetAction.StatEffect("hunger", 30, "health", 10, "happiness", 8)),

            // 玩具类
            createToy("ball", "小球", "⚽", "简单的玩具球", 20, 1,
                new PetAction.StatEffect("happiness", 15, "energy", -5)),
            createToy("mouse_toy", "老鼠玩具", "🐭", "会动的小老鼠玩具", 35, 2,
                new PetAction.StatEffect("happiness", 20, "energy", -8)),
            createToy("laser_pointer", "激光笔", "🔴", "猫咪最爱的激光点", 60, 3,
                new PetAction.StatEffect("happiness", 25, "energy", -12)),

            // 药品类
            createMedicine("basic_medicine", "基础药品", "💊", "治疗轻微不适", 30, 2,
                new PetAction.StatEffect("health", 20)),
            createMedicine("super_medicine", "特效药", "💉", "快速恢复健康", 80, 4,
                new PetAction.StatEffect("health", 40, "happiness", -5)),

            // 装饰类
            createDecoration("flower", "小花", "🌸", "美丽的装饰花朵", 25, 1),
            createDecoration("castle", "小城堡", "🏰", "豪华的宠物城堡", 150, 4),

            // 饰品类
            createAccessory("bow", "蝴蝶结", "🎀", "可爱的蝴蝶结", 40, 2,
                new PetAction.StatEffect("happiness", 10)),
            createAccessory("crown", "小皇冠", "👑", "华丽的小皇冠", 200, 5,
                new PetAction.StatEffect("happiness", 20))
        };
    }

    // 辅助创建方法
    private static GameItem createFood(String id, String name, String emoji, String desc, int cost, int rarity, PetAction.StatEffect effect) {
        GameItem item = new GameItem(id, name, emoji, desc, ItemType.FOOD, cost, rarity);
        item.setEffect(effect);
        return item;
    }

    private static GameItem createToy(String id, String name, String emoji, String desc, int cost, int rarity, PetAction.StatEffect effect) {
        GameItem item = new GameItem(id, name, emoji, desc, ItemType.TOY, cost, rarity);
        item.setEffect(effect);
        return item;
    }

    private static GameItem createMedicine(String id, String name, String emoji, String desc, int cost, int rarity, PetAction.StatEffect effect) {
        GameItem item = new GameItem(id, name, emoji, desc, ItemType.MEDICINE, cost, rarity);
        item.setEffect(effect);
        return item;
    }

    private static GameItem createDecoration(String id, String name, String emoji, String desc, int cost, int rarity) {
        return new GameItem(id, name, emoji, desc, ItemType.DECORATION, cost, rarity);
    }

    private static GameItem createAccessory(String id, String name, String emoji, String desc, int cost, int rarity, PetAction.StatEffect effect) {
        GameItem item = new GameItem(id, name, emoji, desc, ItemType.ACCESSORY, cost, rarity);
        item.setEffect(effect);
        return item;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmoji() { return emoji; }
    public void setEmoji(String emoji) { this.emoji = emoji; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public ItemType getType() { return type; }
    public void setType(ItemType type) { this.type = type; }

    public int getCost() { return cost; }
    public void setCost(int cost) { this.cost = cost; }

    public int getRarity() { return rarity; }
    public void setRarity(int rarity) { this.rarity = rarity; }

    public PetAction.StatEffect getEffect() { return effect; }
    public void setEffect(PetAction.StatEffect effect) { this.effect = effect; }

    public boolean isUnlocked() { return isUnlocked; }
    public void setUnlocked(boolean unlocked) { isUnlocked = unlocked; }
}
