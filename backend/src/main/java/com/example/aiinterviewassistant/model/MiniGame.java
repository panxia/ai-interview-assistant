package com.example.aiinterviewassistant.model;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 小游戏系统
 * 提供各种有趣的互动小游戏
 */
public class MiniGame {
    
    /**
     * 游戏类型枚举
     */
    @com.fasterxml.jackson.annotation.JsonFormat(shape = com.fasterxml.jackson.annotation.JsonFormat.Shape.OBJECT)
    public enum GameType {
        MEMORY("记忆游戏", "🧠", "记住序列并重复", 3, 15),
        REACTION("反应游戏", "⚡", "快速点击出现的目标", 2, 10),
        PUZZLE("猜谜游戏", "🧩", "回答简单的问题", 4, 20),
        TAP("拍拍游戏", "👆", "连续点击宠物获得分数", 1, 8);

        private final String displayName;
        private final String emoji;
        private final String description;
        private final int difficulty; // 1-5难度等级
        private final int maxReward; // 最大奖励金币

        GameType(String displayName, String emoji, String description, int difficulty, int maxReward) {
            this.displayName = displayName;
            this.emoji = emoji;
            this.description = description;
            this.difficulty = difficulty;
            this.maxReward = maxReward;
        }

        public String getDisplayName() { return displayName; }
        public String getEmoji() { return emoji; }
        public String getDescription() { return description; }
        public int getDifficulty() { return difficulty; }
        public int getMaxReward() { return maxReward; }
        
        public String getName() {
            return this.name();
        }
    }

    /**
     * 游戏会话类
     */
    public static class GameSession {
        private String sessionId;
        private String playerId;
        private GameType gameType;
        private GameState state;
        private int score;
        private int currentRound;
        private int maxRounds;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Map<String, Object> gameData; // 存储游戏特定数据

        public GameSession(String playerId, GameType gameType) {
            this.sessionId = UUID.randomUUID().toString();
            this.playerId = playerId;
            this.gameType = gameType;
            this.state = GameState.PREPARING;
            this.score = 0;
            this.currentRound = 0;
            this.maxRounds = 5; // 默认5轮
            this.startTime = LocalDateTime.now();
            this.gameData = new HashMap<>();
        }

        // Getters and Setters
        public String getSessionId() { return sessionId; }
        public String getPlayerId() { return playerId; }
        public GameType getGameType() { return gameType; }
        public GameState getState() { return state; }
        public void setState(GameState state) { this.state = state; }
        public int getScore() { return score; }
        public void setScore(int score) { this.score = score; }
        public void addScore(int points) { this.score += points; }
        public int getCurrentRound() { return currentRound; }
        public void setCurrentRound(int currentRound) { this.currentRound = currentRound; }
        public int getMaxRounds() { return maxRounds; }
        public void setMaxRounds(int maxRounds) { this.maxRounds = maxRounds; }
        public LocalDateTime getStartTime() { return startTime; }
        public LocalDateTime getEndTime() { return endTime; }
        public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
        public Map<String, Object> getGameData() { return gameData; }
        public void setGameData(Map<String, Object> gameData) { this.gameData = gameData; }
        
        public void putGameData(String key, Object value) {
            this.gameData.put(key, value);
        }
        
        public Object getGameData(String key) {
            return this.gameData.get(key);
        }
    }

    /**
     * 游戏状态枚举
     */
    public enum GameState {
        PREPARING("准备中"),
        PLAYING("游戏中"),
        WAITING_INPUT("等待输入"),
        ROUND_COMPLETE("本轮完成"),
        GAME_COMPLETE("游戏完成"),
        FAILED("游戏失败");

        private final String displayName;

        GameState(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() { return displayName; }
    }

    /**
     * 游戏结果类
     */
    public static class GameResult {
        private boolean success;
        private String message;
        private int score;
        private int coinsEarned;
        private int experienceEarned;
        private GameSession session;

        public GameResult(boolean success, String message, GameSession session) {
            this.success = success;
            this.message = message;
            this.session = session;
            this.score = session != null ? session.getScore() : 0;
        }

        // Getters and Setters
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public int getScore() { return score; }
        public int getCoinsEarned() { return coinsEarned; }
        public void setCoinsEarned(int coinsEarned) { this.coinsEarned = coinsEarned; }
        public int getExperienceEarned() { return experienceEarned; }
        public void setExperienceEarned(int experienceEarned) { this.experienceEarned = experienceEarned; }
        public GameSession getSession() { return session; }
    }

    /**
     * 记忆游戏数据生成器
     */
    public static class MemoryGameGenerator {
        private static final String[] EMOJIS = {"🍎", "🍌", "🍇", "🍓", "🥝", "🍑", "🍊", "🥭", "🍍", "🥥"};
        
        public static List<String> generateSequence(int length) {
            List<String> sequence = new ArrayList<>();
            Random random = new Random();
            for (int i = 0; i < length; i++) {
                sequence.add(EMOJIS[random.nextInt(EMOJIS.length)]);
            }
            return sequence;
        }
    }

    /**
     * 反应游戏数据生成器
     */
    public static class ReactionGameGenerator {
        private static final String[] TARGETS = {"🎯", "⭐", "💎", "🏆", "🎪"};
        private static final String[] DISTRACTORS = {"💥", "🌪️", "⚡", "🔥", "❌"};
        
        public static Map<String, Object> generateTargets(int count) {
            Map<String, Object> data = new HashMap<>();
            List<Map<String, Object>> targets = new ArrayList<>();
            Random random = new Random();
            
            for (int i = 0; i < count; i++) {
                Map<String, Object> target = new HashMap<>();
                target.put("emoji", Math.random() < 0.7 ? 
                    TARGETS[random.nextInt(TARGETS.length)] : 
                    DISTRACTORS[random.nextInt(DISTRACTORS.length)]);
                target.put("isTarget", target.get("emoji").toString().startsWith("🎯") || 
                                     target.get("emoji").toString().startsWith("⭐") ||
                                     target.get("emoji").toString().startsWith("💎") ||
                                     target.get("emoji").toString().startsWith("🏆") ||
                                     target.get("emoji").toString().startsWith("🎪"));
                target.put("x", random.nextInt(300));
                target.put("y", random.nextInt(300));
                targets.add(target);
            }
            
            data.put("targets", targets);
            return data;
        }
    }

    /**
     * 猜谜游戏题目生成器
     */
    public static class PuzzleGameGenerator {
        private static final List<PuzzleQuestion> QUESTIONS = Arrays.asList(
            new PuzzleQuestion("什么动物喜欢吃鱼？", Arrays.asList("猫", "🐱", "cat"), "🐱"),
            new PuzzleQuestion("什么动物喜欢吃骨头？", Arrays.asList("狗", "🐶", "dog"), "🐶"),
            new PuzzleQuestion("什么动物喜欢吃萝卜？", Arrays.asList("兔子", "🐰", "rabbit"), "🐰"),
            new PuzzleQuestion("什么动物会储存食物在脸颊里？", Arrays.asList("仓鼠", "🐹", "hamster"), "🐹"),
            new PuzzleQuestion("什么动物会喷火？", Arrays.asList("龙", "🐲", "dragon"), "🐲"),
            new PuzzleQuestion("什么动物是黑白色的？", Arrays.asList("熊猫", "🐼", "panda"), "🐼"),
            new PuzzleQuestion("什么动物生活在南极？", Arrays.asList("企鹅", "🐧", "penguin"), "🐧"),
            new PuzzleQuestion("1+1等于几？", Arrays.asList("2", "二", "两"), "2"),
            new PuzzleQuestion("一周有几天？", Arrays.asList("7", "七", "7天"), "7"),
            new PuzzleQuestion("彩虹有几种颜色？", Arrays.asList("7", "七", "7种"), "7")
        );

        public static PuzzleQuestion getRandomQuestion() {
            Random random = new Random();
            return QUESTIONS.get(random.nextInt(QUESTIONS.size()));
        }
    }

    /**
     * 猜谜问题类
     */
    public static class PuzzleQuestion {
        private String question;
        private List<String> correctAnswers;
        private String emoji;

        public PuzzleQuestion(String question, List<String> correctAnswers, String emoji) {
            this.question = question;
            this.correctAnswers = correctAnswers;
            this.emoji = emoji;
        }

        public boolean isCorrect(String answer) {
            return correctAnswers.stream()
                .anyMatch(correct -> correct.equalsIgnoreCase(answer.trim()));
        }

        // Getters
        public String getQuestion() { return question; }
        public List<String> getCorrectAnswers() { return correctAnswers; }
        public String getEmoji() { return emoji; }
    }
}
