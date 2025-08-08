package com.example.aiinterviewassistant.model;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 宠物类型枚举
 * 定义了可选择的宠物种类，每种都有独特的特性
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum PetType {
    CAT("小猫咪", "🐱", "可爱的小猫，喜欢独立但也需要关爱"),
    DOG("小狗狗", "🐶", "忠诚的伙伴，活泼好动，需要更多的关注"),
    RABBIT("小兔子", "🐰", "温顺的小兔，喜欢安静的环境"),
    HAMSTER("小仓鼠", "🐹", "活泼的小仓鼠，喜欢储存食物"),
    DRAGON("小龙", "🐲", "神秘的小龙，成长潜力巨大"),
    PANDA("小熊猫", "🐼", "憨憨的小熊猫，喜欢吃竹子"),
    PENGUIN("小企鹅", "🐧", "可爱的小企鹅，喜欢凉爽的环境");

    private final String displayName;
    private final String emoji;
    private final String description;

    PetType(String displayName, String emoji, String description) {
        this.displayName = displayName;
        this.emoji = emoji;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmoji() {
        return emoji;
    }

    public String getDescription() {
        return description;
    }
    
    public String getName() {
        return this.name();
    }
}
