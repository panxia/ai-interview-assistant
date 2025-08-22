package com.example.aiinterviewassistant.service;

import com.example.aiinterviewassistant.model.*;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 宠物服务 - 增强版
 * 包含完整的宠物管理、成长、交互和游戏功能
 */
@Service
public class PetService {
    
    // 使用内存存储，实际项目中应该使用数据库
    private final Map<String, Pet> pets = new ConcurrentHashMap<>();
    private final Map<String, List<Achievement>> playerAchievements = new ConcurrentHashMap<>();
    private final Map<String, List<GameItem>> playerInventory = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> petLastInteraction = new ConcurrentHashMap<>();
    private final Random random = new Random();
    
    /**
     * 创建新宠物
     */
    public Pet createPet(String playerId, String petName, PetType petType, Map<String, Object> customization) {
        Pet pet = new Pet();
        pet.setPetId(generatePetId());
        pet.setPlayerId(playerId);
        pet.setPetName(petName);
        pet.setPetType(petType);
        pet.setCreatedAt(LocalDateTime.now());
        pet.setLastInteraction(LocalDateTime.now());
        
        // 初始化基础属性
        PetStats stats = new PetStats();
        stats.setLevel(1);
        stats.setExperience(0);
        stats.setHealth(100);
        stats.setHappiness(80);
        stats.setEnergy(100);
        stats.setHunger(20);
        stats.setCleanliness(90);
        stats.setIntelligence(10);
        stats.setStrength(10);
        stats.setAgility(10);
        stats.setLoyalty(50);
        pet.setStats(stats);
        
        // 设置外观
        PetAppearance appearance = createAppearanceFromCustomization(customization);
        pet.setAppearance(appearance);
        
        // 设置性格
        PetPersonality personality = createPersonalityFromType(
            (String) customization.get("personalityType")
        );
        pet.setPersonality(personality);
        
        // 初始化成就和物品
        playerAchievements.putIfAbsent(playerId, new ArrayList<>());
        playerInventory.putIfAbsent(playerId, new ArrayList<>());
        
        // 添加初始物品
        addInitialItems(playerId);
        
        pets.put(pet.getPetId(), pet);
        petLastInteraction.put(pet.getPetId(), LocalDateTime.now());
        
        // 创建首个宠物成就
        if (pets.values().stream().filter(p -> p.getPlayerId().equals(playerId)).count() == 1) {
            unlockAchievement(playerId, "FIRST_PET", "第一个伙伴", "创造了你的第一个宠物", 100);
        }
        
        return pet;
    }
    
    /**
     * 根据自定义配置创建外观
     */
    private PetAppearance createAppearanceFromCustomization(Map<String, Object> customization) {
        PetAppearance appearance = new PetAppearance();
        
        // 基础设置
        appearance.setHeadShape((String) customization.getOrDefault("headShape", "round"));
        appearance.setEarStyle((String) customization.getOrDefault("earStyle", "pointed"));
        appearance.setEyeType((String) customization.getOrDefault("eyeType", "normal"));
        appearance.setMouthExpression((String) customization.getOrDefault("mouthExpression", "smile"));
        
        // 颜色设置
        appearance.setPrimaryColor((String) customization.getOrDefault("primaryColor", "#FFA500"));
        appearance.setSecondaryColor((String) customization.getOrDefault("secondaryColor", "#FFFFFF"));
        appearance.setEyeColorLeft((String) customization.getOrDefault("eyeColorLeft", "#4169E1"));
        appearance.setEyeColorRight((String) customization.getOrDefault("eyeColorRight", "#4169E1"));
        appearance.setNoseColor((String) customization.getOrDefault("noseColor", "#FFB6C1"));
        
        // 图案和装饰
        appearance.setPattern((String) customization.getOrDefault("pattern", "none"));
        appearance.setPatternColor((String) customization.getOrDefault("patternColor", "#000000"));
        appearance.setHat((String) customization.getOrDefault("hat", "none"));
        appearance.setCollar((String) customization.getOrDefault("collar", "none"));
        appearance.setGlasses((String) customization.getOrDefault("glasses", "none"));
        
        // 特殊效果
        appearance.setHasGlow((Boolean) customization.getOrDefault("hasGlow", false));
        appearance.setGlowColor((String) customization.getOrDefault("glowColor", "#FFFF00"));
        appearance.setHasSparkles((Boolean) customization.getOrDefault("hasSparkles", false));
        
        // 身体比例
        @SuppressWarnings("unchecked")
        Map<String, Double> bodyProportion = (Map<String, Double>) customization.get("bodyProportion");
        if (bodyProportion != null) {
            appearance.setBodyProportionFatness(bodyProportion.getOrDefault("fatness", 1.0));
            appearance.setBodyProportionHeight(bodyProportion.getOrDefault("height", 1.0));
            appearance.setBodyProportionHeadSize(bodyProportion.getOrDefault("headSize", 1.0));
            appearance.setBodyProportionLimbLength(bodyProportion.getOrDefault("limbLength", 1.0));
            appearance.setBodyProportionTailLength(bodyProportion.getOrDefault("tailLength", 1.0));
        }
        
        return appearance;
    }
    
    /**
     * 根据性格类型创建性格
     */
    private PetPersonality createPersonalityFromType(String personalityType) {
        PetPersonality personality = new PetPersonality();
        personality.setPersonalityType(personalityType != null ? personalityType : "PLAYFUL");
        
        switch (personality.getPersonalityType()) {
            case PLAYFUL:
                personality.setPlayfulness(90);
                personality.setLaziness(20);
                personality.setCuriosity(80);
                personality.setAffection(70);
                personality.setIndependence(30);
                personality.setIntelligence(60);
                break;
            case LAZY:
                personality.setPlayfulness(20);
                personality.setLaziness(90);
                personality.setCuriosity(40);
                personality.setAffection(60);
                personality.setIndependence(70);
                personality.setIntelligence(50);
                break;
            case CURIOUS:
                personality.setPlayfulness(70);
                personality.setLaziness(30);
                personality.setCuriosity(95);
                personality.setAffection(60);
                personality.setIndependence(60);
                personality.setIntelligence(85);
                break;
            case AFFECTIONATE:
                personality.setPlayfulness(60);
                personality.setLaziness(40);
                personality.setCuriosity(50);
                personality.setAffection(95);
                personality.setIndependence(20);
                personality.setIntelligence(65);
                break;
            case INDEPENDENT:
                personality.setPlayfulness(50);
                personality.setLaziness(60);
                personality.setCuriosity(70);
                personality.setAffection(30);
                personality.setIndependence(90);
                personality.setIntelligence(75);
                break;
            case INTELLIGENT:
                personality.setPlayfulness(60);
                personality.setLaziness(40);
                personality.setCuriosity(85);
                personality.setAffection(60);
                personality.setIndependence(70);
                personality.setIntelligence(95);
                break;
            default:
                personality.setPlayfulness(50);
                personality.setLaziness(50);
                personality.setCuriosity(50);
                personality.setAffection(50);
                personality.setIndependence(50);
                personality.setIntelligence(50);
        }
        
        return personality;
    }
    
    /**
     * 添加初始物品
     */
    private void addInitialItems(String playerId) {
        List<GameItem> inventory = playerInventory.get(playerId);
        
        // 基础食物
        inventory.add(createItem("BASIC_FOOD", "基础食物", "普通的宠物食物", 5));
        inventory.add(createItem("WATER", "清水", "新鲜的饮用水", 3));
        inventory.add(createItem("TOY_BALL", "小球", "简单的玩具球", 1));
        inventory.add(createItem("BRUSH", "梳子", "保持宠物整洁", 1));
    }
    
    /**
     * 创建物品
     */
    private GameItem createItem(String itemId, String name, String description, int quantity) {
        GameItem item = new GameItem();
        item.setItemId(itemId);
        item.setItemName(name);
        item.setDescription(description);
        item.setQuantity(quantity);
        item.setItemType("CONSUMABLE");
        item.setRarity(1); // COMMON对应稀有度1
        return item;
    }
    
    /**
     * 喂食宠物
     */
    public Pet feedPet(String petId, String itemId) {
        Pet pet = pets.get(petId);
        if (pet == null) {
            throw new RuntimeException("宠物不存在");
        }
        
        // 检查玩家是否有该物品
        List<GameItem> inventory = playerInventory.get(pet.getPlayerId());
        GameItem foodItem = inventory.stream()
            .filter(item -> item.getItemId().equals(itemId) && item.getQuantity() > 0)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("没有该食物"));
        
        // 使用物品
        foodItem.setQuantity(foodItem.getQuantity() - 1);
        if (foodItem.getQuantity() <= 0) {
            inventory.remove(foodItem);
        }
        
        // 喂食效果
        PetStats stats = pet.getStats();
        switch (itemId) {
            case "BASIC_FOOD":
                stats.setHunger(Math.max(0, stats.getHunger() - 20));
                stats.setHappiness(Math.min(100, stats.getHappiness() + 5));
                break;
            case "PREMIUM_FOOD":
                stats.setHunger(Math.max(0, stats.getHunger() - 40));
                stats.setHappiness(Math.min(100, stats.getHappiness() + 15));
                stats.setHealth(Math.min(100, stats.getHealth() + 5));
                break;
            case "WATER":
                stats.setHunger(Math.max(0, stats.getHunger() - 5));
                stats.setEnergy(Math.min(100, stats.getEnergy() + 10));
                break;
        }
        
        // 增加经验
        addExperience(pet, 5);
        
        // 记录交互时间
        pet.setLastInteraction(LocalDateTime.now());
        petLastInteraction.put(petId, LocalDateTime.now());
        
        return pet;
    }
    
    /**
     * 与宠物玩耍
     */
    public Pet playWithPet(String petId, String activityType) {
        Pet pet = pets.get(petId);
        if (pet == null) {
            throw new RuntimeException("宠物不存在");
        }
        
        PetStats stats = pet.getStats();
        PetPersonality personality = pet.getPersonality();
        
        // 检查能量
        if (stats.getEnergy() < 20) {
            throw new RuntimeException("宠物太累了，需要休息");
        }
        
        // 玩耍效果
        int happinessGain = 0;
        int energyCost = 0;
        int experienceGain = 0;
        
        switch (activityType) {
            case "FETCH":
                happinessGain = 15 + (personality.getPlayfulness() / 10);
                energyCost = 25;
                experienceGain = 8;
                stats.setAgility(Math.min(100, stats.getAgility() + 1));
                break;
            case "PUZZLE":
                happinessGain = 10 + (personality.getIntelligence() / 10);
                energyCost = 15;
                experienceGain = 12;
                stats.setIntelligence(Math.min(100, stats.getIntelligence() + 2));
                break;
            case "CUDDLE":
                happinessGain = 20 + (personality.getAffection() / 10);
                energyCost = 5;
                experienceGain = 5;
                stats.setLoyalty(Math.min(100, stats.getLoyalty() + 2));
                break;
            case "EXERCISE":
                happinessGain = 12 + (personality.getPlayfulness() / 15);
                energyCost = 30;
                experienceGain = 10;
                stats.setStrength(Math.min(100, stats.getStrength() + 1));
                stats.setAgility(Math.min(100, stats.getAgility() + 1));
                break;
        }
        
        // 应用效果
        stats.setHappiness(Math.min(100, stats.getHappiness() + happinessGain));
        stats.setEnergy(Math.max(0, stats.getEnergy() - energyCost));
        stats.setHunger(Math.min(100, stats.getHunger() + energyCost / 5));
        
        // 增加经验
        addExperience(pet, experienceGain);
        
        // 记录交互时间
        pet.setLastInteraction(LocalDateTime.now());
        petLastInteraction.put(petId, LocalDateTime.now());
        
        // 检查成就
        checkPlayAchievements(pet.getPlayerId(), activityType);
        
        return pet;
    }
    
    /**
     * 清洁宠物
     */
    public Pet cleanPet(String petId) {
        Pet pet = pets.get(petId);
        if (pet == null) {
            throw new RuntimeException("宠物不存在");
        }
        
        // 检查是否有梳子
        List<GameItem> inventory = playerInventory.get(pet.getPlayerId());
        boolean hasBrush = inventory.stream()
            .anyMatch(item -> item.getItemId().equals("BRUSH") && item.getQuantity() > 0);
        
        if (!hasBrush) {
            throw new RuntimeException("需要梳子来清洁宠物");
        }
        
        PetStats stats = pet.getStats();
        stats.setCleanliness(100);
        stats.setHappiness(Math.min(100, stats.getHappiness() + 10));
        
        // 增加经验
        addExperience(pet, 3);
        
        // 记录交互时间
        pet.setLastInteraction(LocalDateTime.now());
        petLastInteraction.put(petId, LocalDateTime.now());
        
        return pet;
    }
    
    /**
     * 宠物休息
     */
    public Pet restPet(String petId) {
        Pet pet = pets.get(petId);
        if (pet == null) {
            throw new RuntimeException("宠物不存在");
        }
        
        PetStats stats = pet.getStats();
        stats.setEnergy(Math.min(100, stats.getEnergy() + 50));
        stats.setHealth(Math.min(100, stats.getHealth() + 5));
        
        // 记录交互时间
        pet.setLastInteraction(LocalDateTime.now());
        petLastInteraction.put(petId, LocalDateTime.now());
        
        return pet;
    }
    
    /**
     * 添加经验值并处理升级
     */
    private void addExperience(Pet pet, int experience) {
        PetStats stats = pet.getStats();
        stats.setExperience(stats.getExperience() + experience);
        
        // 计算需要的经验值 (level * 100)
        int requiredExp = stats.getLevel() * 100;
        
        while (stats.getExperience() >= requiredExp) {
            stats.setExperience(stats.getExperience() - requiredExp);
            stats.setLevel(stats.getLevel() + 1);
            
            // 升级奖励
            stats.setHealth(Math.min(100, stats.getHealth() + 10));
            stats.setIntelligence(Math.min(100, stats.getIntelligence() + 2));
            stats.setStrength(Math.min(100, stats.getStrength() + 2));
            stats.setAgility(Math.min(100, stats.getAgility() + 2));
            
            // 升级成就
            checkLevelAchievements(pet.getPlayerId(), stats.getLevel());
            
            requiredExp = stats.getLevel() * 100;
        }
    }
    
    /**
     * 更新宠物状态（被动变化）
     */
    public Pet updatePetStatus(String petId) {
        Pet pet = pets.get(petId);
        if (pet == null) {
            return null;
        }
        
        LocalDateTime lastInteraction = petLastInteraction.get(petId);
        if (lastInteraction == null) {
            lastInteraction = pet.getLastInteraction();
        }
        
        long minutesPassed = ChronoUnit.MINUTES.between(lastInteraction, LocalDateTime.now());
        
        if (minutesPassed > 0) {
            PetStats stats = pet.getStats();
            
            // 饥饿度增加
            stats.setHunger(Math.min(100, stats.getHunger() + (int)(minutesPassed / 5)));
            
            // 清洁度下降
            stats.setCleanliness(Math.max(0, stats.getCleanliness() - (int)(minutesPassed / 10)));
            
            // 能量自然恢复（如果宠物在休息）
            if (stats.getEnergy() < 50) {
                stats.setEnergy(Math.min(100, stats.getEnergy() + (int)(minutesPassed / 3)));
            }
            
            // 如果饥饿或脏乱，快乐度下降
            if (stats.getHunger() > 70 || stats.getCleanliness() < 30) {
                stats.setHappiness(Math.max(0, stats.getHappiness() - (int)(minutesPassed / 8)));
            }
            
            // 如果太久没互动，快乐度和忠诚度下降
            if (minutesPassed > 60) {
                stats.setHappiness(Math.max(0, stats.getHappiness() - (int)((minutesPassed - 60) / 15)));
                stats.setLoyalty(Math.max(0, stats.getLoyalty() - (int)((minutesPassed - 60) / 30)));
            }
            
            petLastInteraction.put(petId, LocalDateTime.now());
        }
        
        return pet;
    }
    
    /**
     * 开始小游戏
     */
    public Map<String, Object> startMiniGame(String petId, String gameType) {
        Pet pet = pets.get(petId);
        if (pet == null) {
            throw new RuntimeException("宠物不存在");
        }
        
        PetStats stats = pet.getStats();
        if (stats.getEnergy() < 15) {
            throw new RuntimeException("宠物能量不足");
        }
        
        Map<String, Object> gameData = new HashMap<>();
        gameData.put("gameType", gameType);
        gameData.put("petId", petId);
        
        switch (gameType) {
            case "MEMORY_CHALLENGE":
                gameData.put("sequence", generateMemorySequence());
                gameData.put("difficulty", Math.min(5, stats.getLevel()));
                break;
            case "REACTION_TEST":
                gameData.put("targets", generateReactionTargets());
                gameData.put("timeLimit", Math.max(5, 15 - stats.getLevel()));
                break;
            case "RHYTHM_GAME":
                gameData.put("pattern", generateRhythmPattern());
                gameData.put("speed", Math.min(3.0, 1.0 + stats.getLevel() * 0.2));
                break;
        }
        
        return gameData;
    }
    
    /**
     * 完成小游戏
     */
    public Pet completeMiniGame(String petId, String gameType, int score) {
        Pet pet = pets.get(petId);
        if (pet == null) {
            throw new RuntimeException("宠物不存在");
        }
        
        PetStats stats = pet.getStats();
        
        // 能量消耗
        stats.setEnergy(Math.max(0, stats.getEnergy() - 15));
        
        // 根据分数给予奖励
        int experienceGain = score / 10;
        int happinessGain = score / 20;
        
        stats.setHappiness(Math.min(100, stats.getHappiness() + happinessGain));
        addExperience(pet, experienceGain);
        
        // 根据游戏类型增加相应属性
        switch (gameType) {
            case "MEMORY_CHALLENGE":
                stats.setIntelligence(Math.min(100, stats.getIntelligence() + score / 50));
                break;
            case "REACTION_TEST":
                stats.setAgility(Math.min(100, stats.getAgility() + score / 50));
                break;
            case "RHYTHM_GAME":
                stats.setIntelligence(Math.min(100, stats.getIntelligence() + score / 100));
                stats.setAgility(Math.min(100, stats.getAgility() + score / 100));
                break;
        }
        
        // 检查游戏成就
        checkGameAchievements(pet.getPlayerId(), gameType, score);
        
        // 记录交互时间
        pet.setLastInteraction(LocalDateTime.now());
        petLastInteraction.put(petId, LocalDateTime.now());
        
        return pet;
    }
    
    /**
     * 获取宠物信息
     */
    public Pet getPet(String petId) {
        return updatePetStatus(petId);
    }
    
    /**
     * 获取玩家的所有宠物
     */
    public List<Pet> getPlayerPets(String playerId) {
        return pets.values().stream()
            .filter(pet -> pet.getPlayerId().equals(playerId))
            .map(pet -> updatePetStatus(pet.getPetId()))
            .collect(Collectors.toList());
    }
    
    /**
     * 获取玩家成就
     */
    public List<Achievement> getPlayerAchievements(String playerId) {
        return playerAchievements.getOrDefault(playerId, new ArrayList<>());
    }
    
    /**
     * 获取玩家背包
     */
    public List<GameItem> getPlayerInventory(String playerId) {
        return playerInventory.getOrDefault(playerId, new ArrayList<>());
    }
    
    // ============ 私有辅助方法 ============
    
    private String generatePetId() {
        return "pet_" + System.currentTimeMillis() + "_" + random.nextInt(1000);
    }
    
    private List<String> generateMemorySequence() {
        List<String> sequence = new ArrayList<>();
        String[] colors = {"red", "blue", "green", "yellow", "purple"};
        int length = 3 + random.nextInt(5);
        
        for (int i = 0; i < length; i++) {
            sequence.add(colors[random.nextInt(colors.length)]);
        }
        
        return sequence;
    }
    
    private List<Map<String, Object>> generateReactionTargets() {
        List<Map<String, Object>> targets = new ArrayList<>();
        int count = 3 + random.nextInt(7);
        
        for (int i = 0; i < count; i++) {
            Map<String, Object> target = new HashMap<>();
            target.put("x", random.nextInt(400));
            target.put("y", random.nextInt(400));
            target.put("delay", random.nextInt(2000));
            targets.add(target);
        }
        
        return targets;
    }
    
    private List<Double> generateRhythmPattern() {
        List<Double> pattern = new ArrayList<>();
        int length = 5 + random.nextInt(10);
        
        for (int i = 0; i < length; i++) {
            pattern.add(0.5 + random.nextDouble() * 2.0);
        }
        
        return pattern;
    }
    
    private void unlockAchievement(String playerId, String achievementId, String name, String description, int points) {
        List<Achievement> achievements = playerAchievements.get(playerId);
        boolean alreadyUnlocked = achievements.stream()
            .anyMatch(a -> a.getAchievementId().equals(achievementId));
        
        if (!alreadyUnlocked) {
            Achievement achievement = new Achievement();
            achievement.setAchievementId(achievementId);
            achievement.setAchievementName(name);
            achievement.setDescription(description);
            achievement.setPoints(points);
            achievement.setUnlockedAt(LocalDateTime.now());
            achievements.add(achievement);
        }
    }
    
    private void checkLevelAchievements(String playerId, int level) {
        if (level == 5) {
            unlockAchievement(playerId, "LEVEL_5", "初级训练师", "宠物达到5级", 50);
        } else if (level == 10) {
            unlockAchievement(playerId, "LEVEL_10", "中级训练师", "宠物达到10级", 100);
        } else if (level == 20) {
            unlockAchievement(playerId, "LEVEL_20", "高级训练师", "宠物达到20级", 200);
        }
    }
    
    private void checkPlayAchievements(String playerId, String activityType) {
        // 这里可以添加更复杂的成就检查逻辑
        if (activityType.equals("FETCH")) {
            unlockAchievement(playerId, "FIRST_FETCH", "抛接高手", "第一次玩抛接游戏", 25);
        }
    }
    
    private void checkGameAchievements(String playerId, String gameType, int score) {
        if (score >= 100) {
            unlockAchievement(playerId, "GAME_MASTER", "游戏大师", "在小游戏中获得100分", 75);
        }
    }
}
