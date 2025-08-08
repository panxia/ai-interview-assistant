package com.example.aiinterviewassistant.service;

import com.example.aiinterviewassistant.model.*;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 宠物服务类
 * 负责管理宠物的业务逻辑
 */
@Service
public class PetService {
    
    // 简单的内存存储（实际项目中应该使用数据库）
    private final Map<String, Pet> pets = new ConcurrentHashMap<>();
    private final Map<String, Integer> playerCoins = new ConcurrentHashMap<>();
    private final Map<String, List<String>> playerInventory = new ConcurrentHashMap<>();
    private final Map<String, GameItem> gameItems = new ConcurrentHashMap<>();
    
    // 小游戏和成就系统
    private final Map<String, MiniGame.GameSession> activeSessions = new ConcurrentHashMap<>();
    private final Map<String, List<Achievement>> playerAchievements = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Integer>> playerStats = new ConcurrentHashMap<>();
    
    public PetService() {
        // 初始化游戏物品
        for (GameItem item : GameItem.getDefaultItems()) {
            gameItems.put(item.getId(), item);
        }
    }

    /**
     * 创建新宠物
     */
    public Pet createPet(String playerId, String petName, PetType petType) {
        Pet pet = new Pet(petName, petType);
        pets.put(playerId, pet);
        
        // 给新玩家一些初始资源
        playerCoins.put(playerId, 100);
        playerInventory.put(playerId, new ArrayList<>(Arrays.asList("apple", "ball")));
        
        // 初始化成就系统
        initializePlayerAchievements(playerId);
        
        return pet;
    }

    /**
     * 获取玩家的宠物
     */
    public Pet getPet(String playerId) {
        Pet pet = pets.get(playerId);
        if (pet != null) {
            updatePetStatusByTime(pet);
        }
        return pet;
    }

    /**
     * 执行宠物动作
     */
    public ActionResult executeAction(String playerId, PetAction action) {
        Pet pet = getPet(playerId);
        if (pet == null) {
            return new ActionResult(false, "没有找到宠物", null);
        }
        
        // 验证动作是否为空
        if (action == null) {
            return new ActionResult(false, "动作不能为空", pet);
        }

        if (!action.canExecute(pet)) {
            return new ActionResult(false, "现在无法执行这个动作", pet);
        }

        // 应用动作效果
        action.getEffect().applyTo(pet.getStats());
        pet.updateLastInteraction();
        
        // 获得经验值
        boolean leveledUp = pet.getStats().addExperience(10);
        
        // 有概率获得金币
        int coinsEarned = 0;
        if (Math.random() < 0.3) { // 30%概率
            coinsEarned = 5 + (int)(Math.random() * 10);
            addCoins(playerId, coinsEarned);
        }

        // 更新成就进度
        List<Achievement> newlyUnlocked = new ArrayList<>();
        switch (action) {
            case FEED, FEED_TREAT, FEED_MEDICINE -> {
                newlyUnlocked.addAll(updateAchievement(playerId, Achievement.AchievementType.FEED_COUNT, 1));
                updatePlayerStat(playerId, "feed_count", 1);
            }
            case PLAY -> {
                newlyUnlocked.addAll(updateAchievement(playerId, Achievement.AchievementType.PLAY_COUNT, 1));
                updatePlayerStat(playerId, "play_count", 1);
            }
        }
        
        // 检查等级成就
        newlyUnlocked.addAll(setAchievementProgress(playerId, Achievement.AchievementType.LEVEL_REACHED, pet.getStats().getLevel()));
        
        // 检查快乐度成就
        newlyUnlocked.addAll(setAchievementProgress(playerId, Achievement.AchievementType.PET_HAPPINESS, pet.getStats().getHappiness()));
        
        // 检查金币成就
        if (coinsEarned > 0) {
            newlyUnlocked.addAll(updateAchievement(playerId, Achievement.AchievementType.COINS_EARNED, coinsEarned));
        }

        String message = action.getExecuteMessage(pet);
        if (leveledUp) {
            message += "\n🎉 " + pet.getName() + " 升级了！现在是 " + pet.getStats().getLevel() + " 级！";
        }
        if (coinsEarned > 0) {
            message += "\n💰 获得了 " + coinsEarned + " 金币！";
        }
        
        // 显示新解锁的成就
        for (Achievement achievement : newlyUnlocked) {
            message += "\n🏆 解锁成就：" + achievement.getName() + " " + achievement.getEmoji();
        }

        return new ActionResult(true, message, pet);
    }

    /**
     * 使用物品
     */
    public ActionResult useItem(String playerId, String itemId) {
        Pet pet = getPet(playerId);
        if (pet == null) {
            return new ActionResult(false, "没有找到宠物", null);
        }

        GameItem item = gameItems.get(itemId);
        if (item == null) {
            return new ActionResult(false, "物品不存在", pet);
        }

        if (!hasItem(playerId, itemId)) {
            return new ActionResult(false, "你没有这个物品", pet);
        }

        if (!item.canUseOn(pet)) {
            return new ActionResult(false, item.getName() + " 现在无法使用", pet);
        }

        // 使用物品
        String message = item.useOn(pet);
        removeItem(playerId, itemId);

        return new ActionResult(true, message, pet);
    }

    /**
     * 购买物品
     */
    public ActionResult buyItem(String playerId, String itemId) {
        GameItem item = gameItems.get(itemId);
        if (item == null) {
            return new ActionResult(false, "物品不存在", null);
        }

        if (!item.isUnlocked()) {
            return new ActionResult(false, "物品尚未解锁", null);
        }

        int playerCoins = getCoins(playerId);
        if (playerCoins < item.getCost()) {
            return new ActionResult(false, "金币不足！需要 " + item.getCost() + " 金币", null);
        }

        // 扣除金币并添加物品
        addCoins(playerId, -item.getCost());
        addItem(playerId, itemId);
        
        // 更新购买成就
        List<Achievement> newlyUnlocked = updateAchievement(playerId, Achievement.AchievementType.ITEMS_BOUGHT, 1);
        updatePlayerStat(playerId, "items_bought", 1);

        String message = "成功购买了 " + item.getName() + " " + item.getEmoji();
        for (Achievement achievement : newlyUnlocked) {
            message += "\n🏆 解锁成就：" + achievement.getName() + " " + achievement.getEmoji();
        }

        return new ActionResult(true, message, null);
    }

    /**
     * 获取玩家金币
     */
    public int getCoins(String playerId) {
        return playerCoins.getOrDefault(playerId, 0);
    }

    /**
     * 添加金币
     */
    public void addCoins(String playerId, int amount) {
        int current = getCoins(playerId);
        playerCoins.put(playerId, Math.max(0, current + amount));
    }

    /**
     * 获取玩家物品列表
     */
    public List<GameItem> getPlayerItems(String playerId) {
        List<String> itemIds = playerInventory.getOrDefault(playerId, new ArrayList<>());
        List<GameItem> items = new ArrayList<>();
        for (String itemId : itemIds) {
            GameItem item = gameItems.get(itemId);
            if (item != null) {
                items.add(item);
            }
        }
        return items;
    }

    /**
     * 获取商店物品列表
     */
    public List<GameItem> getShopItems() {
        return gameItems.values().stream()
                .filter(GameItem::isUnlocked)
                .sorted(Comparator.comparing(GameItem::getRarity))
                .toList();
    }

    /**
     * 获取宠物可执行的动作
     */
    public List<PetAction> getAvailableActions(String playerId) {
        Pet pet = getPet(playerId);
        if (pet == null) {
            return Collections.emptyList();
        }

        return Arrays.stream(PetAction.values())
                .filter(action -> action.canExecute(pet))
                .toList();
    }
    
    /**
     * 获取宠物可执行动作的详细信息（用于前端显示）
     */
    public List<ActionInfo> getAvailableActionInfo(String playerId) {
        Pet pet = getPet(playerId);
        if (pet == null) {
            return Collections.emptyList();
        }

        return Arrays.stream(PetAction.values())
                .filter(action -> action.canExecute(pet))
                .map(action -> new ActionInfo(
                    action.name(),
                    action.getDisplayName(),
                    action.getEmoji(),
                    action.getDescription()
                ))
                .toList();
    }
    
    /**
     * 动作信息类
     */
    public static class ActionInfo {
        private String name;
        private String displayName;
        private String emoji;
        private String description;
        
        public ActionInfo(String name, String displayName, String emoji, String description) {
            this.name = name;
            this.displayName = displayName;
            this.emoji = emoji;
            this.description = description;
        }
        
        // Getters
        public String getName() { return name; }
        public String getDisplayName() { return displayName; }
        public String getEmoji() { return emoji; }
        public String getDescription() { return description; }
    }

    /**
     * 随时间更新宠物状态
     */
    private void updatePetStatusByTime(Pet pet) {
        LocalDateTime now = LocalDateTime.now();
        Duration timePassed = Duration.between(pet.getLastInteraction(), now);
        long hoursAway = timePassed.toHours();

        if (hoursAway > 0) {
            PetStats stats = pet.getStats();
            
            // 随时间减少的状态（每小时减少一定数值）
            int hungerDecrease = (int) Math.min(hoursAway * 3, 30);
            int cleanlinessDecrease = (int) Math.min(hoursAway * 2, 20);
            int happinessDecrease = (int) Math.min(hoursAway * 1, 15);
            
            stats.updateStat("hunger", stats.getHunger() - hungerDecrease);
            stats.updateStat("cleanliness", stats.getCleanliness() - cleanlinessDecrease);
            stats.updateStat("happiness", stats.getHappiness() - happinessDecrease);

            // 如果宠物在睡觉，恢复能量
            if (pet.isAsleep()) {
                int energyIncrease = (int) Math.min(hoursAway * 10, 50);
                stats.updateStat("energy", stats.getEnergy() + energyIncrease);
                
                // 睡够了就醒来
                if (stats.getEnergy() >= 80) {
                    pet.setAsleep(false);
                }
            }

            // 更新心情
            updatePetMood(pet);
        }
    }

    /**
     * 更新宠物心情
     */
    private void updatePetMood(Pet pet) {
        PetStats stats = pet.getStats();
        int avgStats = (stats.getHunger() + stats.getCleanliness() + 
                       stats.getHappiness() + stats.getEnergy() + stats.getHealth()) / 5;

        String newMood = switch (avgStats / 20) {
            case 0, 1 -> "sick";
            case 2 -> "sad";
            case 3 -> "okay";
            case 4 -> "happy";
            default -> "excited";
        };

        pet.setMood(newMood);
    }

    // 物品管理辅助方法
    private boolean hasItem(String playerId, String itemId) {
        List<String> items = playerInventory.getOrDefault(playerId, new ArrayList<>());
        return items.contains(itemId);
    }

    private void addItem(String playerId, String itemId) {
        List<String> items = playerInventory.computeIfAbsent(playerId, k -> new ArrayList<>());
        items.add(itemId);
    }

    private void removeItem(String playerId, String itemId) {
        List<String> items = playerInventory.get(playerId);
        if (items != null) {
            items.remove(itemId);
        }
    }

    // ================= 小游戏系统 =================
    
    /**
     * 开始小游戏
     */
    public MiniGame.GameResult startMiniGame(String playerId, MiniGame.GameType gameType) {
        Pet pet = getPet(playerId);
        if (pet == null) {
            return new MiniGame.GameResult(false, "没有找到宠物", null);
        }

        // 检查宠物状态
        if (pet.getStats().getEnergy() < 20) {
            return new MiniGame.GameResult(false, "宠物太累了，无法玩游戏", null);
        }

        // 创建游戏会话
        MiniGame.GameSession session = new MiniGame.GameSession(playerId, gameType);
        activeSessions.put(session.getSessionId(), session);

        // 初始化游戏数据
        switch (gameType) {
            case MEMORY -> {
                session.setState(MiniGame.GameState.PLAYING);
                List<String> sequence = MiniGame.MemoryGameGenerator.generateSequence(3);
                session.putGameData("sequence", sequence);
                session.putGameData("playerSequence", new ArrayList<String>());
            }
            case REACTION -> {
                session.setState(MiniGame.GameState.PLAYING);
                Map<String, Object> targets = MiniGame.ReactionGameGenerator.generateTargets(5);
                session.putGameData("targets", targets);
                session.putGameData("correctClicks", 0);
                session.putGameData("totalClicks", 0);
            }
            case PUZZLE -> {
                session.setState(MiniGame.GameState.WAITING_INPUT);
                MiniGame.PuzzleQuestion question = MiniGame.PuzzleGameGenerator.getRandomQuestion();
                session.putGameData("question", question);
            }
            case TAP -> {
                session.setState(MiniGame.GameState.PLAYING);
                session.putGameData("taps", 0);
                session.putGameData("timeLimit", 10); // 10秒限时
                session.putGameData("startTime", System.currentTimeMillis());
            }
        }

        return new MiniGame.GameResult(true, "游戏开始！", session);
    }

    /**
     * 处理游戏输入
     */
    public MiniGame.GameResult processGameInput(String sessionId, Map<String, Object> input) {
        MiniGame.GameSession session = activeSessions.get(sessionId);
        if (session == null) {
            return new MiniGame.GameResult(false, "游戏会话不存在", null);
        }

        switch (session.getGameType()) {
            case MEMORY -> {
                return processMemoryGameInput(session, input);
            }
            case REACTION -> {
                return processReactionGameInput(session, input);
            }
            case PUZZLE -> {
                return processPuzzleGameInput(session, input);
            }
            case TAP -> {
                return processTapGameInput(session, input);
            }
        }

        return new MiniGame.GameResult(false, "未知游戏类型", session);
    }

    private MiniGame.GameResult processMemoryGameInput(MiniGame.GameSession session, Map<String, Object> input) {
        @SuppressWarnings("unchecked")
        List<String> sequence = (List<String>) session.getGameData("sequence");
        @SuppressWarnings("unchecked")
        List<String> playerSequence = (List<String>) session.getGameData("playerSequence");
        
        String playerInput = (String) input.get("selection");
        playerSequence.add(playerInput);

        if (playerSequence.size() <= sequence.size()) {
            // 检查到目前为止是否正确
            for (int i = 0; i < playerSequence.size(); i++) {
                if (!sequence.get(i).equals(playerSequence.get(i))) {
                    return finishGame(session, false, "记忆错误！正确答案是：" + String.join(" ", sequence));
                }
            }

            if (playerSequence.size() == sequence.size()) {
                // 完成一轮
                session.addScore(10 * sequence.size());
                session.setCurrentRound(session.getCurrentRound() + 1);
                
                if (session.getCurrentRound() >= session.getMaxRounds()) {
                    return finishGame(session, true, "恭喜！记忆游戏完成！");
                } else {
                    // 下一轮，序列更长
                    List<String> newSequence = MiniGame.MemoryGameGenerator.generateSequence(sequence.size() + 1);
                    session.putGameData("sequence", newSequence);
                    session.putGameData("playerSequence", new ArrayList<String>());
                    return new MiniGame.GameResult(true, "进入下一轮！", session);
                }
            } else {
                return new MiniGame.GameResult(true, "继续输入...", session);
            }
        }

        return new MiniGame.GameResult(false, "输入过多", session);
    }

    private MiniGame.GameResult processReactionGameInput(MiniGame.GameSession session, Map<String, Object> input) {
        int correctClicks = (Integer) session.getGameData("correctClicks");
        int totalClicks = (Integer) session.getGameData("totalClicks");
        
        boolean clickedTarget = (Boolean) input.getOrDefault("isTarget", false);
        totalClicks++;
        
        if (clickedTarget) {
            correctClicks++;
            session.addScore(5);
        } else {
            session.addScore(-2); // 错误点击扣分
        }
        
        session.putGameData("correctClicks", correctClicks);
        session.putGameData("totalClicks", totalClicks);
        
        if (totalClicks >= 10) {
            double accuracy = (double) correctClicks / totalClicks;
            if (accuracy >= 0.7) {
                return finishGame(session, true, String.format("反应游戏完成！准确率：%.1f%%", accuracy * 100));
            } else {
                return finishGame(session, false, String.format("反应游戏失败！准确率：%.1f%% (需要70%%以上)", accuracy * 100));
            }
        }
        
        return new MiniGame.GameResult(true, "继续点击目标！", session);
    }

    private MiniGame.GameResult processPuzzleGameInput(MiniGame.GameSession session, Map<String, Object> input) {
        MiniGame.PuzzleQuestion question = (MiniGame.PuzzleQuestion) session.getGameData("question");
        String answer = (String) input.get("answer");
        
        if (question.isCorrect(answer)) {
            session.addScore(15);
            session.setCurrentRound(session.getCurrentRound() + 1);
            
            if (session.getCurrentRound() >= session.getMaxRounds()) {
                return finishGame(session, true, "恭喜！猜谜游戏完成！");
            } else {
                // 下一题
                MiniGame.PuzzleQuestion newQuestion = MiniGame.PuzzleGameGenerator.getRandomQuestion();
                session.putGameData("question", newQuestion);
                return new MiniGame.GameResult(true, "回答正确！下一题...", session);
            }
        } else {
            return finishGame(session, false, "回答错误！正确答案包括：" + String.join(", ", question.getCorrectAnswers()));
        }
    }

    private MiniGame.GameResult processTapGameInput(MiniGame.GameSession session, Map<String, Object> input) {
        int taps = (Integer) session.getGameData("taps");
        long startTime = (Long) session.getGameData("startTime");
        int timeLimit = (Integer) session.getGameData("timeLimit");
        
        taps++;
        session.putGameData("taps", taps);
        session.addScore(1);
        
        long elapsed = (System.currentTimeMillis() - startTime) / 1000;
        if (elapsed >= timeLimit) {
            return finishGame(session, true, String.format("拍拍游戏完成！总共拍了 %d 次！", taps));
        }
        
        return new MiniGame.GameResult(true, String.format("继续拍拍！剩余时间：%d秒", timeLimit - elapsed), session);
    }

    private MiniGame.GameResult finishGame(MiniGame.GameSession session, boolean success, String message) {
        session.setState(success ? MiniGame.GameState.GAME_COMPLETE : MiniGame.GameState.FAILED);
        session.setEndTime(LocalDateTime.now());
        
        MiniGame.GameResult result = new MiniGame.GameResult(success, message, session);
        
        if (success) {
            // 计算奖励
            int baseReward = session.getGameType().getMaxReward();
            int scoreMultiplier = Math.max(1, session.getScore() / 10);
            int coinsEarned = Math.min(baseReward, scoreMultiplier * 2);
            int expEarned = coinsEarned / 2;
            
            result.setCoinsEarned(coinsEarned);
            result.setExperienceEarned(expEarned);
            
            // 给予奖励
            addCoins(session.getPlayerId(), coinsEarned);
            Pet pet = getPet(session.getPlayerId());
            if (pet != null) {
                pet.getStats().addExperience(expEarned);
                pet.updateLastInteraction();
            }
            
            // 更新成就
            updateAchievement(session.getPlayerId(), Achievement.AchievementType.GAMES_WON, 1);
            updatePlayerStat(session.getPlayerId(), "games_won", 1);
        }
        
        // 移除活跃会话
        activeSessions.remove(session.getSessionId());
        return result;
    }

    /**
     * 获取活跃游戏会话
     */
    public MiniGame.GameSession getActiveGameSession(String playerId) {
        return activeSessions.values().stream()
            .filter(session -> session.getPlayerId().equals(playerId))
            .findFirst()
            .orElse(null);
    }

    // ================= 成就系统 =================
    
    /**
     * 初始化玩家成就
     */
    private void initializePlayerAchievements(String playerId) {
        if (!playerAchievements.containsKey(playerId)) {
            List<Achievement> achievements = new ArrayList<>();
            for (Achievement template : Achievement.getDefaultAchievements()) {
                Achievement playerAchievement = new Achievement(
                    template.getId(), template.getName(), template.getDescription(),
                    template.getEmoji(), template.getType(), template.getTargetValue(),
                    template.getReward()
                );
                achievements.add(playerAchievement);
            }
            playerAchievements.put(playerId, achievements);
        }
        
        if (!playerStats.containsKey(playerId)) {
            playerStats.put(playerId, new ConcurrentHashMap<>());
        }
    }

    /**
     * 更新成就进度
     */
    public List<Achievement> updateAchievement(String playerId, Achievement.AchievementType type, int value) {
        initializePlayerAchievements(playerId);
        
        List<Achievement> playerAchievements = this.playerAchievements.get(playerId);
        List<Achievement> newlyUnlocked = new ArrayList<>();
        
        for (Achievement achievement : playerAchievements) {
            if (achievement.getType() == type && !achievement.isUnlocked()) {
                boolean unlocked = achievement.updateProgress(value);
                if (unlocked) {
                    newlyUnlocked.add(achievement);
                    // 给予奖励
                    addCoins(playerId, achievement.getReward().getCoins());
                    Pet pet = getPet(playerId);
                    if (pet != null) {
                        pet.getStats().addExperience(achievement.getReward().getExperience());
                        
                        // 特殊物品奖励
                        if (achievement.getReward().getSpecialItem() != null) {
                            addItem(playerId, achievement.getReward().getSpecialItem());
                        }
                    }
                }
            }
        }
        
        return newlyUnlocked;
    }

    /**
     * 设置成就进度（用于绝对值类型）
     */
    public List<Achievement> setAchievementProgress(String playerId, Achievement.AchievementType type, int value) {
        initializePlayerAchievements(playerId);
        
        List<Achievement> playerAchievements = this.playerAchievements.get(playerId);
        List<Achievement> newlyUnlocked = new ArrayList<>();
        
        for (Achievement achievement : playerAchievements) {
            if (achievement.getType() == type && !achievement.isUnlocked()) {
                boolean unlocked = achievement.setProgress(value);
                if (unlocked) {
                    newlyUnlocked.add(achievement);
                    // 给予奖励
                    addCoins(playerId, achievement.getReward().getCoins());
                    Pet pet = getPet(playerId);
                    if (pet != null) {
                        pet.getStats().addExperience(achievement.getReward().getExperience());
                        
                        // 特殊物品奖励
                        if (achievement.getReward().getSpecialItem() != null) {
                            addItem(playerId, achievement.getReward().getSpecialItem());
                        }
                    }
                }
            }
        }
        
        return newlyUnlocked;
    }

    /**
     * 获取玩家成就列表
     */
    public List<Achievement> getPlayerAchievements(String playerId) {
        initializePlayerAchievements(playerId);
        return new ArrayList<>(playerAchievements.get(playerId));
    }

    /**
     * 更新玩家统计数据
     */
    private void updatePlayerStat(String playerId, String statName, int increment) {
        Map<String, Integer> stats = playerStats.computeIfAbsent(playerId, k -> new ConcurrentHashMap<>());
        stats.put(statName, stats.getOrDefault(statName, 0) + increment);
    }

    /**
     * 获取玩家统计数据
     */
    public Map<String, Integer> getPlayerStats(String playerId) {
        return new HashMap<>(playerStats.getOrDefault(playerId, new HashMap<>()));
    }

    /**
     * 动作执行结果类
     */
    public static class ActionResult {
        private final boolean success;
        private final String message;
        private final Pet pet;

        public ActionResult(boolean success, String message, Pet pet) {
            this.success = success;
            this.message = message;
            this.pet = pet;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public Pet getPet() { return pet; }
    }
}
