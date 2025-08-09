package com.example.aiinterviewassistant.model;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 升级版小游戏系统
 * 提供多种类型的有趣互动小游戏
 */
public class MiniGame {
    
    /**
     * 游戏类型枚举 - 全面升级版
     */
    @com.fasterxml.jackson.annotation.JsonFormat(shape = com.fasterxml.jackson.annotation.JsonFormat.Shape.OBJECT)
    public enum GameType {
        // 智力游戏类
        MEMORY("记忆挑战Plus", "🧠", "多感官记忆挑战，颜色+声音+震动", 3, 25),
        PUZZLE("宠物拼图", "🧩", "拖拽拼图块，还原可爱宠物照片", 4, 30),
        SPOT_DIFFERENCE("找茬游戏", "🔍", "两张图片中找出不同之处", 3, 20),
        
        // 反应游戏类
        REACTION("超级反应", "⚡", "快速点击目标，避开陷阱", 2, 15),
        PRECISION_SHOOTING("精准射击", "🎯", "弹弓射击移动目标，考虑物理效果", 4, 35),
        WHACK_MOLE("打地鼠升级版", "🕳️", "3D效果打地鼠，特殊地鼠和道具", 3, 20),
        
        // 节奏游戏类
        MUSIC_DANCE("音乐跳舞", "🎶", "跟着节拍让宠物跳舞", 3, 25),
        DRUM_GAME("打鼓游戏", "🥁", "虚拟鼓点，多点触控演奏", 2, 18),
        SINGING_CONTEST("唱歌比赛", "🎤", "音调识别评分，宠物和声", 4, 30),
        
        // 创意游戏类
        PET_CARE_CHALLENGE("护理挑战", "💝", "限时照顾宠物各项需求", 3, 22),
        TREASURE_HUNT("寻宝游戏", "🗺️", "根据线索在房间中寻找宝藏", 4, 28),
        
        // 经典游戏升级版
        TAP("拍拍游戏Plus", "👆", "连续点击宠物，连击获得额外分数", 1, 12);

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
        private int comboCount; // 连击计数
        private int bestCombo; // 最佳连击

        public GameSession(String playerId, GameType gameType) {
            this.sessionId = UUID.randomUUID().toString();
            this.playerId = playerId;
            this.gameType = gameType;
            this.state = GameState.PREPARING;
            this.score = 0;
            this.currentRound = 0;
            this.maxRounds = getDefaultRounds(gameType);
            this.startTime = LocalDateTime.now();
            this.gameData = new HashMap<>();
            this.comboCount = 0;
            this.bestCombo = 0;
        }

        private int getDefaultRounds(GameType gameType) {
            return switch (gameType) {
                case MEMORY, SPOT_DIFFERENCE, PUZZLE -> 3;
                case MUSIC_DANCE, DRUM_GAME, SINGING_CONTEST -> 1; // 单轮但时间较长
                case PRECISION_SHOOTING, WHACK_MOLE -> 3;
                case PET_CARE_CHALLENGE, TREASURE_HUNT -> 1;
                default -> 5;
            };
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
        public int getComboCount() { return comboCount; }
        public void setComboCount(int comboCount) { this.comboCount = comboCount; }
        public int getBestCombo() { return bestCombo; }
        public void setBestCombo(int bestCombo) { this.bestCombo = bestCombo; }
        
        public void putGameData(String key, Object value) {
            this.gameData.put(key, value);
        }
        
        public Object getGameData(String key) {
            return this.gameData.get(key);
        }

        public void addCombo() {
            this.comboCount++;
            if (this.comboCount > this.bestCombo) {
                this.bestCombo = this.comboCount;
            }
        }

        public void resetCombo() {
            this.comboCount = 0;
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
        PAUSED("暂停"),
        FAILED("游戏失败");

        private final String displayName;

        GameState(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() { return displayName; }
    }

    /**
     * 游戏结果类 - 增强版
     */
    public static class GameResult {
        private boolean success;
        private String message;
        private int score;
        private int coinsEarned;
        private int experienceEarned;
        private GameSession session;
        private Map<String, Object> statistics; // 游戏统计数据
        private List<String> achievements; // 本次游戏获得的成就

        public GameResult(boolean success, String message, GameSession session) {
            this.success = success;
            this.message = message;
            this.session = session;
            this.score = session != null ? session.getScore() : 0;
            this.statistics = new HashMap<>();
            this.achievements = new ArrayList<>();
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
        public Map<String, Object> getStatistics() { return statistics; }
        public void setStatistics(Map<String, Object> statistics) { this.statistics = statistics; }
        public List<String> getAchievements() { return achievements; }
        public void setAchievements(List<String> achievements) { this.achievements = achievements; }
    }

    /**
     * 记忆游戏Plus数据生成器
     */
    public static class MemoryGameGenerator {
        private static final String[] EMOJIS = {"🍎", "🍌", "🍇", "🍓", "🥝", "🍑", "🍊", "🥭", "🍍", "🥥"};
        private static final String[] COLORS = {"red", "blue", "green", "yellow", "purple", "orange", "pink", "cyan"};
        private static final String[] SOUNDS = {"beep", "boop", "ding", "chime", "click", "pop", "whistle", "bell"};
        
        public static Map<String, Object> generateSequence(int length, int round) {
            Map<String, Object> data = new HashMap<>();
            List<Map<String, Object>> sequence = new ArrayList<>();
            Random random = new Random();
            
            for (int i = 0; i < length; i++) {
                Map<String, Object> item = new HashMap<>();
                item.put("emoji", EMOJIS[random.nextInt(EMOJIS.length)]);
                item.put("color", COLORS[random.nextInt(COLORS.length)]);
                item.put("sound", SOUNDS[random.nextInt(SOUNDS.length)]);
                item.put("delay", 1000 + i * 500); // 递增延迟
                sequence.add(item);
            }
            
            data.put("sequence", sequence);
            data.put("timeLimit", 5000 + length * 1000); // 时间限制
            return data;
        }
    }

    /**
     * 拼图游戏数据生成器
     */
    public static class PuzzleGameGenerator {
        private static final String[] PET_IMAGES = {
            "cat_sleeping", "dog_playing", "rabbit_eating", "hamster_running",
            "dragon_flying", "panda_bamboo", "penguin_swimming"
        };
        
        public static Map<String, Object> generatePuzzle(int difficulty) {
            Map<String, Object> data = new HashMap<>();
            Random random = new Random();
            
            String selectedImage = PET_IMAGES[random.nextInt(PET_IMAGES.length)];
            int gridSize = switch (difficulty) {
                case 1 -> 3; // 3x3 = 9块
                case 2 -> 4; // 4x4 = 16块
                case 3 -> 5; // 5x5 = 25块
                default -> 3;
            };
            
            List<Map<String, Object>> pieces = new ArrayList<>();
            for (int i = 0; i < gridSize * gridSize; i++) {
                Map<String, Object> piece = new HashMap<>();
                piece.put("id", i);
                piece.put("correctX", i % gridSize);
                piece.put("correctY", i / gridSize);
                piece.put("currentX", -1); // 未放置
                piece.put("currentY", -1);
                piece.put("imageSection", String.format("%s_%d_%d", selectedImage, i % gridSize, i / gridSize));
                pieces.add(piece);
            }
            
            // 打乱拼图块
            Collections.shuffle(pieces);
            
            data.put("image", selectedImage);
            data.put("gridSize", gridSize);
            data.put("pieces", pieces);
            data.put("timeLimit", gridSize * gridSize * 10000); // 每块10秒
            
            return data;
        }
    }

    /**
     * 超级反应游戏数据生成器
     */
    public static class SuperReactionGameGenerator {
        private static final String[] GOOD_TARGETS = {"🎯", "⭐", "💎", "🏆", "🎪", "🍭", "🎁", "🌟"};
        private static final String[] BAD_TARGETS = {"💥", "🌪️", "⚡", "🔥", "❌", "💀", "🕷️", "🐍"};
        private static final String[] GOLDEN_TARGETS = {"👑", "💰", "🥇", "💍"};
        
        public static Map<String, Object> generateTargets(int count, int round) {
            Map<String, Object> data = new HashMap<>();
            List<Map<String, Object>> targets = new ArrayList<>();
            Random random = new Random();
            
            for (int i = 0; i < count; i++) {
                Map<String, Object> target = new HashMap<>();
                
                double chance = random.nextDouble();
                String emoji;
                String type;
                int points;
                
                if (chance < 0.1) { // 10% 黄金目标
                    emoji = GOLDEN_TARGETS[random.nextInt(GOLDEN_TARGETS.length)];
                    type = "golden";
                    points = 50;
                } else if (chance < 0.3) { // 20% 陷阱
                    emoji = BAD_TARGETS[random.nextInt(BAD_TARGETS.length)];
                    type = "trap";
                    points = -20;
                } else { // 70% 普通目标
                    emoji = GOOD_TARGETS[random.nextInt(GOOD_TARGETS.length)];
                    type = "normal";
                    points = 10;
                }
                
                target.put("emoji", emoji);
                target.put("type", type);
                target.put("points", points);
                target.put("x", random.nextInt(280) + 10);
                target.put("y", random.nextInt(280) + 10);
                target.put("size", random.nextInt(20) + 30);
                target.put("speed", random.nextDouble() * 2 + 1);
                target.put("direction", random.nextDouble() * 360);
                target.put("lifetime", random.nextInt(2000) + 3000); // 3-5秒生命周期
                
                targets.add(target);
            }
            
            data.put("targets", targets);
            data.put("spawnInterval", Math.max(500, 2000 - round * 200)); // 随关卡加快生成速度
            return data;
        }
    }

    /**
     * 精准射击游戏数据生成器
     */
    public static class PrecisionShootingGenerator {
        public static Map<String, Object> generateLevel(int round) {
            Map<String, Object> data = new HashMap<>();
            List<Map<String, Object>> targets = new ArrayList<>();
            Random random = new Random();
            
            int targetCount = 3 + round; // 随关卡增加目标数量
            
            for (int i = 0; i < targetCount; i++) {
                Map<String, Object> target = new HashMap<>();
                target.put("x", random.nextInt(200) + 50);
                target.put("y", random.nextInt(100) + 30);
                target.put("size", random.nextInt(20) + 30);
                target.put("moveSpeed", random.nextDouble() * 2 + 0.5);
                target.put("movePattern", random.nextBoolean() ? "horizontal" : "circular");
                target.put("points", (int)(100 / ((Integer)target.get("size")))); // 越小的目标分数越高
                targets.add(target);
            }
            
            data.put("targets", targets);
            data.put("wind", random.nextDouble() * 20 - 10); // -10到10的风力
            data.put("gravity", 9.8);
            data.put("ammunition", 10 + round * 2);
            
            return data;
        }
    }

    /**
     * 音乐跳舞游戏数据生成器
     */
    public static class MusicDanceGenerator {
        private static final String[] DANCE_MOVES = {
            "左摆", "右摆", "跳跃", "转圈", "点头", "摇尾巴", "伸懒腰", "蹦蹦跳"
        };
        
        public static Map<String, Object> generateDanceSequence(String songId, int bpm) {
            Map<String, Object> data = new HashMap<>();
            List<Map<String, Object>> beats = new ArrayList<>();
            Random random = new Random();
            
            int totalBeats = 32; // 32拍
            long beatInterval = 60000 / bpm; // 毫秒
            
            for (int i = 0; i < totalBeats; i++) {
                if (random.nextDouble() < 0.7) { // 70%的节拍需要输入
                    Map<String, Object> beat = new HashMap<>();
                    beat.put("time", i * beatInterval);
                    beat.put("action", DANCE_MOVES[random.nextInt(DANCE_MOVES.length)]);
                    beat.put("perfect", beatInterval / 4); // 完美判定窗口
                    beat.put("good", beatInterval / 2); // 良好判定窗口
                    beats.add(beat);
                }
            }
            
            data.put("songId", songId);
            data.put("bpm", bpm);
            data.put("beats", beats);
            data.put("duration", totalBeats * beatInterval);
            
            return data;
        }
    }

    /**
     * 猜谜游戏题目生成器 - 扩展版
     */
    public static class PuzzleQuestionGenerator {
        private static final List<PuzzleQuestion> QUESTIONS = Arrays.asList(
            // 宠物相关
            new PuzzleQuestion("什么动物喜欢吃鱼？", Arrays.asList("猫", "🐱", "cat"), "🐱"),
            new PuzzleQuestion("什么动物喜欢吃骨头？", Arrays.asList("狗", "🐶", "dog"), "🐶"),
            new PuzzleQuestion("什么动物喜欢吃萝卜？", Arrays.asList("兔子", "🐰", "rabbit"), "🐰"),
            new PuzzleQuestion("什么动物会储存食物在脸颊里？", Arrays.asList("仓鼠", "🐹", "hamster"), "🐹"),
            new PuzzleQuestion("什么动物会喷火？", Arrays.asList("龙", "🐲", "dragon"), "🐲"),
            new PuzzleQuestion("什么动物是黑白色的？", Arrays.asList("熊猫", "🐼", "panda"), "🐼"),
            new PuzzleQuestion("什么动物生活在南极？", Arrays.asList("企鹅", "🐧", "penguin"), "🐧"),
            
            // 数学题
            new PuzzleQuestion("1+1等于几？", Arrays.asList("2", "二", "两"), "2"),
            new PuzzleQuestion("一周有几天？", Arrays.asList("7", "七", "7天"), "7"),
            new PuzzleQuestion("彩虹有几种颜色？", Arrays.asList("7", "七", "7种"), "7"),
            new PuzzleQuestion("2×3等于几？", Arrays.asList("6", "六"), "6"),
            new PuzzleQuestion("10-3等于几？", Arrays.asList("7", "七"), "7"),
            
            // 常识题
            new PuzzleQuestion("太阳从哪个方向升起？", Arrays.asList("东", "东边", "东方"), "🌅"),
            new PuzzleQuestion("一年有几个季节？", Arrays.asList("4", "四", "4个"), "🍂"),
            new PuzzleQuestion("最大的海洋动物是什么？", Arrays.asList("鲸鱼", "蓝鲸", "whale"), "🐋"),
            
            // 游戏相关
            new PuzzleQuestion("宠物饿了应该给它什么？", Arrays.asList("食物", "吃的", "food"), "🍎"),
            new PuzzleQuestion("宠物脏了应该做什么？", Arrays.asList("洗澡", "清洁", "bath"), "🛁"),
            new PuzzleQuestion("宠物累了应该让它做什么？", Arrays.asList("睡觉", "休息", "sleep"), "💤")
        );

        public static PuzzleQuestion getRandomQuestion() {
            Random random = new Random();
            return QUESTIONS.get(random.nextInt(QUESTIONS.size()));
        }
        
        public static PuzzleQuestion getQuestionByCategory(String category) {
            List<PuzzleQuestion> categoryQuestions = QUESTIONS.stream()
                .filter(q -> q.getCategory().equals(category))
                .toList();
            if (categoryQuestions.isEmpty()) {
                return getRandomQuestion();
            }
            Random random = new Random();
            return categoryQuestions.get(random.nextInt(categoryQuestions.size()));
        }
    }

    /**
     * 猜谜问题类 - 增强版
     */
    public static class PuzzleQuestion {
        private String question;
        private List<String> correctAnswers;
        private String emoji;
        private String category;
        private int difficulty;

        public PuzzleQuestion(String question, List<String> correctAnswers, String emoji) {
            this.question = question;
            this.correctAnswers = correctAnswers;
            this.emoji = emoji;
            this.category = "general";
            this.difficulty = 1;
        }

        public PuzzleQuestion(String question, List<String> correctAnswers, String emoji, String category, int difficulty) {
            this.question = question;
            this.correctAnswers = correctAnswers;
            this.emoji = emoji;
            this.category = category;
            this.difficulty = difficulty;
        }

        public boolean isCorrect(String answer) {
            return correctAnswers.stream()
                .anyMatch(correct -> correct.equalsIgnoreCase(answer.trim()));
        }

        // Getters
        public String getQuestion() { return question; }
        public List<String> getCorrectAnswers() { return correctAnswers; }
        public String getEmoji() { return emoji; }
        public String getCategory() { return category; }
        public int getDifficulty() { return difficulty; }
    }
}
