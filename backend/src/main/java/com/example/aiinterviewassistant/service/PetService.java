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
    
    // 环境管理器
    private final Map<String, Environment.EnvironmentManager> playerEnvironments = new ConcurrentHashMap<>();
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
     * 删除玩家的宠物（重新开始游戏）
     */
    public boolean deletePet(String playerId) {
        Pet pet = pets.remove(playerId);
        if (pet != null) {
            // 清理玩家的所有数据
            playerCoins.remove(playerId);
            playerInventory.remove(playerId);
            playerAchievements.remove(playerId);
            playerStats.remove(playerId);
            
            // 清理活跃的游戏会话
            activeSessions.entrySet().removeIf(entry -> 
                entry.getValue().getPlayerId().equals(playerId));
            
            return true;
        }
        return false;
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
            // 智力游戏类
            case MEMORY -> {
                session.setState(MiniGame.GameState.PLAYING);
                Map<String, Object> sequenceData = MiniGame.MemoryGameGenerator.generateSequence(3, session.getCurrentRound());
                session.putGameData("sequenceData", sequenceData);
                session.putGameData("playerSequence", new ArrayList<String>());
                session.putGameData("showPhase", true);
            }
            case PUZZLE -> {
                session.setState(MiniGame.GameState.PLAYING);
                Map<String, Object> puzzleData = MiniGame.PuzzleGameGenerator.generatePuzzle(session.getCurrentRound() + 1);
                session.putGameData("puzzleData", puzzleData);
                session.putGameData("completedPieces", 0);
            }
            case SPOT_DIFFERENCE -> {
                session.setState(MiniGame.GameState.PLAYING);
                session.putGameData("differences", generateSpotDifferences());
                session.putGameData("foundDifferences", new ArrayList<>());
                session.putGameData("timeLimit", 60000); // 60秒
            }
            
            // 反应游戏类
            case REACTION -> {
                session.setState(MiniGame.GameState.PLAYING);
                Map<String, Object> targets = MiniGame.SuperReactionGameGenerator.generateTargets(5, session.getCurrentRound());
                session.putGameData("targets", targets);
                session.putGameData("correctClicks", 0);
                session.putGameData("totalClicks", 0);
                session.putGameData("startTime", System.currentTimeMillis());
            }
            case PRECISION_SHOOTING -> {
                session.setState(MiniGame.GameState.PLAYING);
                Map<String, Object> levelData = MiniGame.PrecisionShootingGenerator.generateLevel(session.getCurrentRound());
                session.putGameData("levelData", levelData);
                session.putGameData("shots", new ArrayList<>());
                session.putGameData("ammunition", levelData.get("ammunition"));
            }
            case WHACK_MOLE -> {
                session.setState(MiniGame.GameState.PLAYING);
                session.putGameData("moles", generateWhackMoles());
                session.putGameData("hits", 0);
                session.putGameData("misses", 0);
                session.putGameData("timeLimit", 30000); // 30秒
            }
            
            // 节奏游戏类
            case MUSIC_DANCE -> {
                session.setState(MiniGame.GameState.PLAYING);
                Map<String, Object> danceData = MiniGame.MusicDanceGenerator.generateDanceSequence("default_song", 120);
                session.putGameData("danceData", danceData);
                session.putGameData("perfectHits", 0);
                session.putGameData("goodHits", 0);
                session.putGameData("missedHits", 0);
            }
            case DRUM_GAME -> {
                session.setState(MiniGame.GameState.PLAYING);
                session.putGameData("drumPattern", generateDrumPattern());
                session.putGameData("playerInputs", new ArrayList<>());
                session.putGameData("accuracy", 0.0);
            }
            case SINGING_CONTEST -> {
                session.setState(MiniGame.GameState.WAITING_INPUT);
                session.putGameData("song", selectRandomSong());
                session.putGameData("pitchAccuracy", new ArrayList<>());
                session.putGameData("rhythmAccuracy", new ArrayList<>());
            }
            
            // 创意游戏类
            case PET_CARE_CHALLENGE -> {
                session.setState(MiniGame.GameState.PLAYING);
                session.putGameData("challenges", generateCareChallenge());
                session.putGameData("completedTasks", 0);
                session.putGameData("timeLimit", 120000); // 2分钟
            }
            case TREASURE_HUNT -> {
                session.setState(MiniGame.GameState.PLAYING);
                session.putGameData("treasureMap", generateTreasureMap());
                session.putGameData("cluesFound", new ArrayList<>());
                session.putGameData("treasuresFound", 0);
            }
            
            // 经典游戏升级版
            case TAP -> {
                session.setState(MiniGame.GameState.PLAYING);
                session.putGameData("taps", 0);
                session.putGameData("comboMultiplier", 1);
                session.putGameData("timeLimit", 15000); // 15秒限时
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
        Map<String, Object> sequenceData = (Map<String, Object>) session.getGameData("sequenceData");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> sequence = (List<Map<String, Object>>) sequenceData.get("sequence");
        @SuppressWarnings("unchecked")
        List<String> playerSequence = (List<String>) session.getGameData("playerSequence");
        
        String playerInput = (String) input.get("selection");
        playerSequence.add(playerInput);

        if (playerSequence.size() <= sequence.size()) {
            // 检查到目前为止是否正确
            for (int i = 0; i < playerSequence.size(); i++) {
                String correctEmoji = (String) sequence.get(i).get("emoji");
                if (!correctEmoji.equals(playerSequence.get(i))) {
                    List<String> correctSequence = sequence.stream()
                        .map(item -> (String) item.get("emoji"))
                        .toList();
                    return finishGame(session, false, "记忆错误！正确答案是：" + String.join(" ", correctSequence));
                }
            }

            if (playerSequence.size() == sequence.size()) {
                // 完成一轮
                session.addScore(10 * sequence.size());
                session.addCombo(); // 增加连击
                session.setCurrentRound(session.getCurrentRound() + 1);
                
                if (session.getCurrentRound() >= session.getMaxRounds()) {
                    return finishGame(session, true, "恭喜！记忆游戏完成！");
                } else {
                    // 下一轮，序列更长
                    Map<String, Object> newSequenceData = MiniGame.MemoryGameGenerator.generateSequence(sequence.size() + 1, session.getCurrentRound());
                    session.putGameData("sequenceData", newSequenceData);
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
                MiniGame.PuzzleQuestion newQuestion = MiniGame.PuzzleQuestionGenerator.getRandomQuestion();
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

    // ================= 宠物外观自定义系统 =================

    /**
     * 更新宠物外观
     */
    public Pet updatePetAppearance(String playerId, PetAppearance appearance) {
        Pet pet = pets.get(playerId);
        if (pet == null) {
            return null;
        }
        
        pet.setAppearance(appearance);
        pet.updateLastInteraction();
        
        // 更新成就
        updateAchievement(playerId, Achievement.AchievementType.CUSTOMIZATION, 1);
        
        return pet;
    }

    /**
     * 更新宠物性格
     */
    public Pet updatePetPersonality(String playerId, PetPersonality personality) {
        Pet pet = pets.get(playerId);
        if (pet == null) {
            return null;
        }
        
        pet.setPersonality(personality);
        pet.updateLastInteraction();
        
        return pet;
    }

    /**
     * 获取外观配置选项
     */
    public AppearanceOptions getAppearanceOptions() {
        return new AppearanceOptions();
    }

    /**
     * 获取活动推荐
     */
    public List<ActivityRecommendation> getActivityRecommendations(String playerId) {
        Pet pet = pets.get(playerId);
        if (pet == null) {
            return new ArrayList<>();
        }

        List<ActivityRecommendation> recommendations = new ArrayList<>();
        PetPersonality personality = pet.getPersonality();

        // 基于性格推荐活动
        recommendations.add(new ActivityRecommendation(
            "puzzle_game", "智力游戏", "挑战智力，获得经验", 
            personality.getActivitySuitability("puzzle_game"), "🧩"
        ));
        
        recommendations.add(new ActivityRecommendation(
            "reaction_game", "反应游戏", "测试反应速度", 
            personality.getActivitySuitability("reaction_game"), "⚡"
        ));
        
        recommendations.add(new ActivityRecommendation(
            "rhythm_game", "节奏游戏", "跟随音乐节拍", 
            personality.getActivitySuitability("rhythm_game"), "🎵"
        ));
        
        recommendations.add(new ActivityRecommendation(
            "exploration", "探索活动", "发现新事物", 
            personality.getActivitySuitability("exploration"), "🔍"
        ));
        
        recommendations.add(new ActivityRecommendation(
            "grooming", "美容护理", "保持清洁美丽", 
            personality.getActivitySuitability("grooming"), "🛁"
        ));

        // 按适合度排序
        recommendations.sort((a, b) -> Double.compare(b.getSuitability(), a.getSuitability()));
        
        return recommendations;
    }

    // 新增的内部类
    public static class AppearanceOptions {
        private List<String> headShapes;
        private List<String> earTypes;
        private List<String> eyeTypes;
        private List<String> mouthTypes;
        private List<String> patternTypes;
        private List<String> accessoryTypes;
        private List<String> colorPalette;

        public AppearanceOptions() {
            this.headShapes = List.of("round", "oval", "square", "triangular");
            this.earTypes = List.of("pointed", "round", "floppy", "long", "horn", "none");
            this.eyeTypes = List.of("large", "small", "sleepy", "mysterious", "friendly");
            this.mouthTypes = List.of("small", "open", "tiny", "wise", "content", "beak");
            this.patternTypes = List.of("none", "stripes", "spots", "gradient", "heart", "scales");
            this.accessoryTypes = List.of("none", "baseball_cap", "beret", "crown", "bow_tie", "bell");
            this.colorPalette = List.of("#FF8C00", "#8B4513", "#FFFFFF", "#F4A460", "#9932CC", "#000000", 
                                      "#FF0000", "#00FF00", "#0000FF", "#FFFF00", "#FF00FF", "#00FFFF");
        }

        // Getters
        public List<String> getHeadShapes() { return headShapes; }
        public List<String> getEarTypes() { return earTypes; }
        public List<String> getEyeTypes() { return eyeTypes; }
        public List<String> getMouthTypes() { return mouthTypes; }
        public List<String> getPatternTypes() { return patternTypes; }
        public List<String> getAccessoryTypes() { return accessoryTypes; }
        public List<String> getColorPalette() { return colorPalette; }
    }

    public static class ActivityRecommendation {
        private String activityType;
        private String displayName;
        private String description;
        private double suitability;
        private String icon;

        public ActivityRecommendation(String activityType, String displayName, String description, 
                                    double suitability, String icon) {
            this.activityType = activityType;
            this.displayName = displayName;
            this.description = description;
            this.suitability = suitability;
            this.icon = icon;
        }

        // Getters
        public String getActivityType() { return activityType; }
        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
        public double getSuitability() { return suitability; }
        public String getIcon() { return icon; }
    }

    // 新游戏类型的辅助方法

    /**
     * 生成找茬游戏的差异点
     */
    private List<Map<String, Object>> generateSpotDifferences() {
        List<Map<String, Object>> differences = new ArrayList<>();
        Random random = new Random();
        
        // 生成5个差异点
        for (int i = 0; i < 5; i++) {
            Map<String, Object> diff = new HashMap<>();
            diff.put("id", i);
            diff.put("x", random.nextInt(250) + 25); // 在250x250区域内
            diff.put("y", random.nextInt(250) + 25);
            diff.put("size", random.nextInt(20) + 10); // 10-30像素大小
            diff.put("type", random.nextBoolean() ? "missing" : "different"); // 缺失或不同
            differences.add(diff);
        }
        
        return differences;
    }

    /**
     * 生成打地鼠游戏的地鼠
     */
    private List<Map<String, Object>> generateWhackMoles() {
        List<Map<String, Object>> moles = new ArrayList<>();
        Random random = new Random();
        
        // 生成9个洞穴位置 (3x3网格)
        for (int i = 0; i < 9; i++) {
            Map<String, Object> mole = new HashMap<>();
            mole.put("id", i);
            mole.put("x", (i % 3) * 100 + 50);
            mole.put("y", (i / 3) * 100 + 50);
            mole.put("isActive", false);
            mole.put("type", "normal"); // normal, golden, trap
            mole.put("timeLeft", 0);
            moles.add(mole);
        }
        
        return moles;
    }

    /**
     * 生成鼓点模式
     */
    private Map<String, Object> generateDrumPattern() {
        Map<String, Object> pattern = new HashMap<>();
        List<Map<String, Object>> beats = new ArrayList<>();
        Random random = new Random();
        
        // 生成16拍的鼓点模式
        for (int i = 0; i < 16; i++) {
            if (random.nextDouble() < 0.6) { // 60%的概率有鼓点
                Map<String, Object> beat = new HashMap<>();
                beat.put("time", i * 500); // 每500ms一拍
                beat.put("drum", random.nextBoolean() ? "kick" : "snare");
                beat.put("intensity", random.nextDouble() * 0.5 + 0.5); // 0.5-1.0强度
                beats.add(beat);
            }
        }
        
        pattern.put("beats", beats);
        pattern.put("bpm", 120);
        pattern.put("duration", 8000); // 8秒
        
        return pattern;
    }

    /**
     * 选择随机歌曲
     */
    private Map<String, Object> selectRandomSong() {
        String[] songs = {
            "happy_song", "relaxing_melody", "energetic_tune", "gentle_lullaby"
        };
        String[] songNames = {
            "快乐小歌", "轻松旋律", "活力曲调", "温柔摇篮曲"
        };
        
        Random random = new Random();
        int index = random.nextInt(songs.length);
        
        Map<String, Object> song = new HashMap<>();
        song.put("id", songs[index]);
        song.put("name", songNames[index]);
        song.put("duration", 30000); // 30秒
        song.put("difficulty", random.nextInt(3) + 1); // 1-3难度
        
        return song;
    }

    /**
     * 生成护理挑战任务
     */
    private List<Map<String, Object>> generateCareChallenge() {
        List<Map<String, Object>> challenges = new ArrayList<>();
        String[] tasks = {
            "喂食", "清洁", "玩耍", "治疗", "梳毛"
        };
        String[] descriptions = {
            "给宠物喂食恢复饥饿度", "帮宠物洗澡提升清洁度", 
            "和宠物玩耍增加快乐度", "使用药品恢复健康值", "梳理毛发让宠物更美丽"
        };
        
        Random random = new Random();
        
        // 生成3-5个随机任务
        int taskCount = random.nextInt(3) + 3;
        for (int i = 0; i < taskCount; i++) {
            int taskIndex = random.nextInt(tasks.length);
            Map<String, Object> challenge = new HashMap<>();
            challenge.put("id", i);
            challenge.put("task", tasks[taskIndex]);
            challenge.put("description", descriptions[taskIndex]);
            challenge.put("targetValue", random.nextInt(20) + 80); // 目标值80-100
            challenge.put("completed", false);
            challenge.put("timeLimit", 30); // 30秒完成
            challenges.add(challenge);
        }
        
        return challenges;
    }

    /**
     * 生成寻宝地图
     */
    private Map<String, Object> generateTreasureMap() {
        Map<String, Object> treasureMap = new HashMap<>();
        List<Map<String, Object>> treasures = new ArrayList<>();
        List<Map<String, Object>> clues = new ArrayList<>();
        Random random = new Random();
        
        // 生成3个宝藏位置
        String[] rooms = {"living_room", "bedroom", "kitchen", "bathroom", "garden"};
        String[] locations = {"沙发下", "床底下", "冰箱后", "浴缸旁", "花丛中"};
        
        for (int i = 0; i < 3; i++) {
            int roomIndex = random.nextInt(rooms.length);
            Map<String, Object> treasure = new HashMap<>();
            treasure.put("id", i);
            treasure.put("room", rooms[roomIndex]);
            treasure.put("location", locations[roomIndex]);
            treasure.put("x", random.nextInt(200) + 50);
            treasure.put("y", random.nextInt(200) + 50);
            treasure.put("value", (i + 1) * 10); // 10, 20, 30金币
            treasures.add(treasure);
            
            // 为每个宝藏生成线索
            Map<String, Object> clue = new HashMap<>();
            clue.put("id", i);
            clue.put("text", "在" + locations[roomIndex] + "寻找闪亮的宝藏");
            clue.put("treasureId", i);
            clue.put("difficulty", random.nextInt(3) + 1);
            clues.add(clue);
        }
        
        treasureMap.put("treasures", treasures);
        treasureMap.put("clues", clues);
        treasureMap.put("totalValue", 60); // 总价值60金币
        
        return treasureMap;
    }

    // 环境系统相关方法

    /**
     * 获取玩家的环境管理器
     */
    private Environment.EnvironmentManager getEnvironmentManager(String playerId) {
        return playerEnvironments.computeIfAbsent(playerId, k -> new Environment.EnvironmentManager());
    }

    /**
     * 获取当前天气状态
     */
    public Environment.WeatherState getCurrentWeather(String playerId) {
        Environment.EnvironmentManager envManager = getEnvironmentManager(playerId);
        envManager.updateWeather(); // 更新天气
        return envManager.getWeatherState();
    }

    /**
     * 获取所有房间配置
     */
    public Map<Environment.RoomType, Environment.RoomConfiguration> getRooms(String playerId) {
        Environment.EnvironmentManager envManager = getEnvironmentManager(playerId);
        return envManager.getRooms();
    }

    /**
     * 获取可用装饰物品
     */
    public List<Environment.DecorationItem> getAvailableDecorations(String playerId) {
        Environment.EnvironmentManager envManager = getEnvironmentManager(playerId);
        return envManager.getAvailableDecorations();
    }

    /**
     * 在房间中放置装饰物品
     */
    public boolean placeDecoration(String playerId, String itemId, Environment.RoomType roomType, double x, double y) {
        Environment.EnvironmentManager envManager = getEnvironmentManager(playerId);
        
        // 查找装饰物品
        Environment.DecorationItem item = envManager.getAvailableDecorations().stream()
            .filter(decoration -> decoration.getId().equals(itemId))
            .findFirst()
            .orElse(null);
            
        if (item == null || !item.isUnlocked()) {
            return false;
        }

        // 检查玩家是否有足够金币
        Pet pet = getPet(playerId);
        if (pet == null || pet.getStats().getCoins() < item.getCost()) {
            return false;
        }

        // 扣除金币
        pet.getStats().setCoins(pet.getStats().getCoins() - item.getCost());

        // 放置物品
        Environment.RoomConfiguration room = envManager.getRooms().get(roomType);
        if (room != null) {
            Environment.PlacedItem placedItem = new Environment.PlacedItem(item, x, y);
            room.getPlacedItems().add(placedItem);
            return true;
        }

        return false;
    }

    /**
     * 移除房间中的装饰物品
     */
    public boolean removeDecoration(String playerId, Environment.RoomType roomType, String placedItemId) {
        Environment.EnvironmentManager envManager = getEnvironmentManager(playerId);
        Environment.RoomConfiguration room = envManager.getRooms().get(roomType);
        
        if (room != null) {
            return room.getPlacedItems().removeIf(item -> item.getItemId().equals(placedItemId));
        }
        
        return false;
    }

    /**
     * 切换宠物到指定房间
     */
    public void switchRoom(String playerId, Environment.RoomType roomType) {
        Pet pet = getPet(playerId);
        if (pet != null) {
            pet.setCurrentRoom(roomType);
            
            // 应用房间环境效果
            Environment.EnvironmentManager envManager = getEnvironmentManager(playerId);
            Map<String, Double> roomEffects = envManager.calculateRoomEffects(roomType);
            applyEnvironmentEffects(pet, roomEffects);
        }
    }

    /**
     * 应用环境效果到宠物
     */
    private void applyEnvironmentEffects(Pet pet, Map<String, Double> effects) {
        PetStats stats = pet.getStats();
        
        // 应用乘数效果
        if (effects.containsKey("activity_multiplier")) {
            // 活动度影响能量消耗
            double multiplier = effects.get("activity_multiplier");
            if (multiplier > 1.0) {
                stats.setEnergy(Math.max(0, stats.getEnergy() - (int)((multiplier - 1.0) * 5)));
            }
        }
        
        if (effects.containsKey("comfort")) {
            // 舒适度提升快乐度
            double comfort = effects.get("comfort");
            stats.setHappiness(Math.min(100, stats.getHappiness() + (int)(comfort * 10)));
        }
        
        if (effects.containsKey("energy_recovery")) {
            // 能量恢复加成
            double recovery = effects.get("energy_recovery");
            if (pet.isAsleep()) {
                stats.setEnergy(Math.min(100, stats.getEnergy() + (int)(recovery * 15)));
            }
        }
        
        // 其他环境效果可以在这里添加
    }

    /**
     * 获取房间的环境效果
     */
    public Map<String, Double> getRoomEffects(String playerId, Environment.RoomType roomType) {
        Environment.EnvironmentManager envManager = getEnvironmentManager(playerId);
        return envManager.calculateRoomEffects(roomType);
    }
}
