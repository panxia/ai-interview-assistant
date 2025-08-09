package com.example.aiinterviewassistant.model;

import java.util.*;

/**
 * 增强版小游戏系统
 * 包含智力、反应、节奏等多种类型游戏
 */
public class EnhancedMiniGames {
    
    /**
     * 游戏类别枚举
     */
    public enum GameCategory {
        INTELLIGENCE("智力游戏", "brain"),
        REACTION("反应游戏", "speed"),
        RHYTHM("节奏游戏", "music"),
        PUZZLE("益智游戏", "puzzle"),
        ADVENTURE("冒险游戏", "explore");
        
        private final String displayName;
        private final String icon;
        
        GameCategory(String displayName, String icon) {
            this.displayName = displayName;
            this.icon = icon;
        }
        
        public String getDisplayName() { return displayName; }
        public String getIcon() { return icon; }
    }
    
    /**
     * 智力游戏类
     */
    public static class IntelligenceGames {
        
        /**
         * 记忆挑战Plus
         */
        public static class MemoryChallengeGame {
            private List<MemoryElement> sequence = new ArrayList<>();
            private List<MemoryElement> playerInput = new ArrayList<>();
            private int level = 1;
            private boolean useMultiSensory = true; // 多感官模式
            private int comboCount = 0;
            
            public static class MemoryElement {
                public String color;
                public String sound;
                public String vibration;
                public int position;
                
                public MemoryElement(String color, String sound, String vibration, int position) {
                    this.color = color;
                    this.sound = sound;
                    this.vibration = vibration;
                    this.position = position;
                }
            }
            
            public void generateSequence() {
                sequence.clear();
                int length = 2 + level; // 3→5→7→9序列
                String[] colors = {"red", "blue", "green", "yellow", "purple"};
                String[] sounds = {"do", "re", "mi", "fa", "sol"};
                String[] vibrations = {"short", "long", "double", "triple", "pulse"};
                
                for (int i = 0; i < length; i++) {
                    sequence.add(new MemoryElement(
                        colors[(int)(Math.random() * colors.length)],
                        sounds[(int)(Math.random() * sounds.length)],
                        vibrations[(int)(Math.random() * vibrations.length)],
                        i
                    ));
                }
            }
            
            public boolean checkInput(MemoryElement input) {
                playerInput.add(input);
                int index = playerInput.size() - 1;
                
                if (index >= sequence.size()) {
                    return false;
                }
                
                MemoryElement expected = sequence.get(index);
                boolean correct = expected.color.equals(input.color);
                
                if (useMultiSensory) {
                    correct = correct && expected.sound.equals(input.sound);
                }
                
                if (correct && playerInput.size() == sequence.size()) {
                    // 完成一轮
                    comboCount++;
                    level++;
                    return true;
                }
                
                return correct;
            }
            
            public int calculateScore() {
                return level * 100 + comboCount * 50;
            }
        }
        
        /**
         * 拼图游戏
         */
        public static class PuzzleGame {
            private int[][] puzzle;
            private int[][] solution;
            private int gridSize;
            private int moves = 0;
            private long startTime;
            
            public PuzzleGame(int difficulty) {
                // 难度：9块→16块→25块
                this.gridSize = switch (difficulty) {
                    case 1 -> 3;
                    case 2 -> 4;
                    case 3 -> 5;
                    default -> 3;
                };
                initializePuzzle();
            }
            
            private void initializePuzzle() {
                puzzle = new int[gridSize][gridSize];
                solution = new int[gridSize][gridSize];
                
                // 初始化解决方案
                int num = 1;
                for (int i = 0; i < gridSize; i++) {
                    for (int j = 0; j < gridSize; j++) {
                        solution[i][j] = num++;
                        puzzle[i][j] = num - 1;
                    }
                }
                solution[gridSize-1][gridSize-1] = 0; // 空格
                puzzle[gridSize-1][gridSize-1] = 0;
                
                // 打乱拼图
                shufflePuzzle();
                startTime = System.currentTimeMillis();
            }
            
            private void shufflePuzzle() {
                Random rand = new Random();
                for (int i = 0; i < 100; i++) {
                    int dir = rand.nextInt(4);
                    moveEmptySpace(dir);
                }
            }
            
            private void moveEmptySpace(int direction) {
                // 找到空格位置
                int emptyRow = -1, emptyCol = -1;
                for (int i = 0; i < gridSize; i++) {
                    for (int j = 0; j < gridSize; j++) {
                        if (puzzle[i][j] == 0) {
                            emptyRow = i;
                            emptyCol = j;
                            break;
                        }
                    }
                }
                
                // 根据方向移动
                int newRow = emptyRow, newCol = emptyCol;
                switch (direction) {
                    case 0 -> newRow--; // 上
                    case 1 -> newRow++; // 下
                    case 2 -> newCol--; // 左
                    case 3 -> newCol++; // 右
                }
                
                // 检查边界并交换
                if (newRow >= 0 && newRow < gridSize && newCol >= 0 && newCol < gridSize) {
                    puzzle[emptyRow][emptyCol] = puzzle[newRow][newCol];
                    puzzle[newRow][newCol] = 0;
                    moves++;
                }
            }
            
            public boolean isSolved() {
                for (int i = 0; i < gridSize; i++) {
                    for (int j = 0; j < gridSize; j++) {
                        if (puzzle[i][j] != solution[i][j]) {
                            return false;
                        }
                    }
                }
                return true;
            }
            
            public int calculateScore() {
                long timeSpent = (System.currentTimeMillis() - startTime) / 1000;
                int baseScore = 1000;
                int timePenalty = (int)(timeSpent * 2);
                int movePenalty = moves * 5;
                return Math.max(100, baseScore - timePenalty - movePenalty);
            }
        }
        
        /**
         * 找茬游戏
         */
        public static class SpotDifferenceGame {
            private List<Difference> differences = new ArrayList<>();
            private List<Difference> foundDifferences = new ArrayList<>();
            private int hintsUsed = 0;
            private long startTime;
            
            public static class Difference {
                public float x;
                public float y;
                public float radius;
                public String type;
                public boolean found;
                
                public Difference(float x, float y, float radius, String type) {
                    this.x = x;
                    this.y = y;
                    this.radius = radius;
                    this.type = type;
                    this.found = false;
                }
            }
            
            public void generateDifferences(int count) {
                differences.clear();
                String[] types = {"color", "shape", "missing", "extra", "size"};
                
                for (int i = 0; i < count; i++) {
                    differences.add(new Difference(
                        (float)(Math.random() * 100),
                        (float)(Math.random() * 100),
                        10f,
                        types[(int)(Math.random() * types.length)]
                    ));
                }
                startTime = System.currentTimeMillis();
            }
            
            public boolean checkClick(float x, float y) {
                for (Difference diff : differences) {
                    if (!diff.found) {
                        float distance = (float)Math.sqrt(
                            Math.pow(x - diff.x, 2) + Math.pow(y - diff.y, 2)
                        );
                        if (distance <= diff.radius) {
                            diff.found = true;
                            foundDifferences.add(diff);
                            return true;
                        }
                    }
                }
                return false;
            }
            
            public Difference useHint() {
                hintsUsed++;
                for (Difference diff : differences) {
                    if (!diff.found) {
                        return diff;
                    }
                }
                return null;
            }
            
            public boolean isComplete() {
                return foundDifferences.size() == differences.size();
            }
            
            public int calculateScore() {
                long timeSpent = (System.currentTimeMillis() - startTime) / 1000;
                int baseScore = 500 * differences.size();
                int timePenalty = (int)(timeSpent);
                int hintPenalty = hintsUsed * 100;
                return Math.max(100, baseScore - timePenalty - hintPenalty);
            }
        }
    }
    
    /**
     * 反应游戏类
     */
    public static class ReactionGames {
        
        /**
         * 超级反应游戏
         */
        public static class SuperReactionGame {
            private List<Target> targets = new ArrayList<>();
            private int score = 0;
            private int hits = 0;
            private int misses = 0;
            private long reactionTimeTotal = 0;
            
            public static class Target {
                public float x;
                public float y;
                public TargetType type;
                public long appearTime;
                public long lifetime;
                public boolean hit;
                
                public enum TargetType {
                    NORMAL(10, "normal"),
                    GOLDEN(50, "golden"),
                    BOMB(-20, "bomb"),
                    SPEED(20, "speed"),
                    MULTI(30, "multi");
                    
                    private final int points;
                    private final String code;
                    
                    TargetType(int points, String code) {
                        this.points = points;
                        this.code = code;
                    }
                    
                    public int getPoints() { return points; }
                    public String getCode() { return code; }
                }
                
                public Target(float x, float y, TargetType type) {
                    this.x = x;
                    this.y = y;
                    this.type = type;
                    this.appearTime = System.currentTimeMillis();
                    this.lifetime = switch (type) {
                        case GOLDEN -> 1000;
                        case SPEED -> 500;
                        default -> 2000;
                    };
                    this.hit = false;
                }
                
                public boolean isExpired() {
                    return System.currentTimeMillis() - appearTime > lifetime;
                }
            }
            
            public void spawnTarget() {
                // 随机生成目标
                Target.TargetType type;
                double rand = Math.random();
                if (rand < 0.1) {
                    type = Target.TargetType.GOLDEN;
                } else if (rand < 0.2) {
                    type = Target.TargetType.BOMB;
                } else if (rand < 0.3) {
                    type = Target.TargetType.SPEED;
                } else if (rand < 0.4) {
                    type = Target.TargetType.MULTI;
                } else {
                    type = Target.TargetType.NORMAL;
                }
                
                targets.add(new Target(
                    (float)(Math.random() * 100),
                    (float)(Math.random() * 100),
                    type
                ));
            }
            
            public void hitTarget(Target target) {
                if (!target.hit) {
                    target.hit = true;
                    long reactionTime = System.currentTimeMillis() - target.appearTime;
                    reactionTimeTotal += reactionTime;
                    
                    if (target.type == Target.TargetType.BOMB) {
                        misses++;
                    } else {
                        hits++;
                    }
                    
                    score += target.type.getPoints();
                    
                    // 连击奖励
                    if (hits > 0 && hits % 5 == 0) {
                        score += 50; // 连击奖励
                    }
                }
            }
            
            public void updateTargets() {
                targets.removeIf(target -> {
                    if (!target.hit && target.isExpired()) {
                        if (target.type != Target.TargetType.BOMB) {
                            misses++;
                        }
                        return true;
                    }
                    return target.hit;
                });
            }
            
            public float getAccuracy() {
                int total = hits + misses;
                return total > 0 ? (float)hits / total : 0;
            }
            
            public float getAverageReactionTime() {
                return hits > 0 ? (float)reactionTimeTotal / hits : 0;
            }
        }
        
        /**
         * 精准射击游戏
         */
        public static class PrecisionShootingGame {
            private float slingshotPower = 0;
            private float slingshotAngle = 0;
            private List<Projectile> projectiles = new ArrayList<>();
            private List<Target> targets = new ArrayList<>();
            private float windSpeed = 0;
            private float gravity = 9.8f;
            
            public static class Projectile {
                public float x, y;
                public float vx, vy;
                public boolean active;
                
                public Projectile(float x, float y, float vx, float vy) {
                    this.x = x;
                    this.y = y;
                    this.vx = vx;
                    this.vy = vy;
                    this.active = true;
                }
                
                public void update(float deltaTime, float gravity, float wind) {
                    if (active) {
                        vx += wind * deltaTime;
                        vy -= gravity * deltaTime;
                        x += vx * deltaTime;
                        y += vy * deltaTime;
                        
                        // 检查边界
                        if (y < 0 || x < 0 || x > 100) {
                            active = false;
                        }
                    }
                }
            }
            
            public static class Target {
                public float x, y;
                public float width, height;
                public float speed;
                public int points;
                public boolean moving;
                public boolean hit;
                
                public Target(float x, float y, boolean moving) {
                    this.x = x;
                    this.y = y;
                    this.width = 10;
                    this.height = 10;
                    this.moving = moving;
                    this.speed = moving ? (float)(Math.random() * 5 + 2) : 0;
                    this.points = moving ? 20 : 10;
                    this.hit = false;
                }
                
                public void update(float deltaTime) {
                    if (moving && !hit) {
                        x += speed * deltaTime;
                        if (x > 90 || x < 10) {
                            speed = -speed;
                        }
                    }
                }
                
                public boolean checkHit(float px, float py) {
                    return px >= x && px <= x + width && 
                           py >= y && py <= y + height;
                }
            }
            
            public void shoot(float power, float angle) {
                float vx = power * (float)Math.cos(Math.toRadians(angle));
                float vy = power * (float)Math.sin(Math.toRadians(angle));
                projectiles.add(new Projectile(10, 10, vx, vy));
            }
            
            public void updatePhysics(float deltaTime) {
                // 更新风向
                windSpeed = (float)(Math.sin(System.currentTimeMillis() / 1000.0) * 2);
                
                // 更新投射物
                for (Projectile p : projectiles) {
                    p.update(deltaTime, gravity, windSpeed);
                    
                    // 检查碰撞
                    for (Target t : targets) {
                        if (!t.hit && t.checkHit(p.x, p.y)) {
                            t.hit = true;
                            p.active = false;
                        }
                    }
                }
                
                // 更新目标
                for (Target t : targets) {
                    t.update(deltaTime);
                }
                
                // 清理非活动投射物
                projectiles.removeIf(p -> !p.active);
            }
        }
    }
    
    /**
     * 节奏游戏类
     */
    public static class RhythmGames {
        
        /**
         * 音乐跳舞游戏
         */
        public static class MusicDanceGame {
            private List<Beat> beatSequence = new ArrayList<>();
            private int currentBeat = 0;
            private int perfectHits = 0;
            private int goodHits = 0;
            private int missedHits = 0;
            private int combo = 0;
            private int maxCombo = 0;
            
            public static class Beat {
                public long timestamp;
                public BeatType type;
                public boolean hit;
                public HitQuality quality;
                
                public enum BeatType {
                    TAP("点击", 1),
                    HOLD("长按", 2),
                    SWIPE_UP("上滑", 2),
                    SWIPE_DOWN("下滑", 2),
                    SWIPE_LEFT("左滑", 2),
                    SWIPE_RIGHT("右滑", 2),
                    DOUBLE_TAP("双击", 3);
                    
                    private final String displayName;
                    private final int difficulty;
                    
                    BeatType(String displayName, int difficulty) {
                        this.displayName = displayName;
                        this.difficulty = difficulty;
                    }
                    
                    public String getDisplayName() { return displayName; }
                    public int getDifficulty() { return difficulty; }
                }
                
                public enum HitQuality {
                    PERFECT(100, "完美"),
                    GOOD(50, "良好"),
                    MISS(0, "错过");
                    
                    private final int points;
                    private final String displayName;
                    
                    HitQuality(int points, String displayName) {
                        this.points = points;
                        this.displayName = displayName;
                    }
                    
                    public int getPoints() { return points; }
                    public String getDisplayName() { return displayName; }
                }
                
                public Beat(long timestamp, BeatType type) {
                    this.timestamp = timestamp;
                    this.type = type;
                    this.hit = false;
                    this.quality = HitQuality.MISS;
                }
                
                public HitQuality checkTiming(long inputTime) {
                    long diff = Math.abs(inputTime - timestamp);
                    if (diff <= 50) {
                        return HitQuality.PERFECT;
                    } else if (diff <= 150) {
                        return HitQuality.GOOD;
                    } else {
                        return HitQuality.MISS;
                    }
                }
            }
            
            public void generateBeatmap(String songName, int difficulty) {
                // 根据歌曲生成节拍图
                int bpm = 120; // 每分钟节拍数
                long beatInterval = 60000 / bpm; // 毫秒
                
                for (int i = 0; i < 100; i++) {
                    Beat.BeatType type = switch ((int)(Math.random() * difficulty)) {
                        case 0 -> Beat.BeatType.TAP;
                        case 1 -> Beat.BeatType.HOLD;
                        case 2 -> Beat.BeatType.SWIPE_UP;
                        case 3 -> Beat.BeatType.SWIPE_DOWN;
                        default -> Beat.BeatType.TAP;
                    };
                    
                    beatSequence.add(new Beat(i * beatInterval, type));
                }
            }
            
            public void processInput(Beat.BeatType inputType, long inputTime) {
                if (currentBeat < beatSequence.size()) {
                    Beat beat = beatSequence.get(currentBeat);
                    
                    if (beat.type == inputType) {
                        beat.quality = beat.checkTiming(inputTime);
                        beat.hit = true;
                        
                        switch (beat.quality) {
                            case PERFECT -> {
                                perfectHits++;
                                combo++;
                            }
                            case GOOD -> {
                                goodHits++;
                                combo++;
                            }
                            case MISS -> {
                                missedHits++;
                                combo = 0;
                            }
                        }
                        
                        maxCombo = Math.max(maxCombo, combo);
                        currentBeat++;
                    }
                }
            }
            
            public int calculateScore() {
                return perfectHits * 100 + goodHits * 50 + maxCombo * 10;
            }
            
            public float getAccuracy() {
                int total = perfectHits + goodHits + missedHits;
                return total > 0 ? (float)(perfectHits + goodHits) / total : 0;
            }
        }
        
        /**
         * 打鼓游戏
         */
        public static class DrumGame {
            private Map<String, DrumPad> drumPads = new HashMap<>();
            private List<DrumHit> recordedHits = new ArrayList<>();
            private boolean recording = false;
            private boolean playback = false;
            
            public static class DrumPad {
                public String name;
                public String sound;
                public float x, y;
                public float radius;
                
                public DrumPad(String name, String sound, float x, float y) {
                    this.name = name;
                    this.sound = sound;
                    this.x = x;
                    this.y = y;
                    this.radius = 15;
                }
            }
            
            public static class DrumHit {
                public String padName;
                public long timestamp;
                public float velocity;
                
                public DrumHit(String padName, long timestamp, float velocity) {
                    this.padName = padName;
                    this.timestamp = timestamp;
                    this.velocity = velocity;
                }
            }
            
            public DrumGame() {
                // 初始化鼓垫
                drumPads.put("kick", new DrumPad("底鼓", "kick.wav", 50, 70));
                drumPads.put("snare", new DrumPad("军鼓", "snare.wav", 30, 50));
                drumPads.put("hihat", new DrumPad("踩镲", "hihat.wav", 70, 50));
                drumPads.put("crash", new DrumPad("吊镲", "crash.wav", 30, 30));
                drumPads.put("tom1", new DrumPad("嗵鼓1", "tom1.wav", 50, 30));
                drumPads.put("tom2", new DrumPad("嗵鼓2", "tom2.wav", 70, 30));
            }
            
            public void hitDrum(float x, float y, float velocity) {
                for (Map.Entry<String, DrumPad> entry : drumPads.entrySet()) {
                    DrumPad pad = entry.getValue();
                    float distance = (float)Math.sqrt(
                        Math.pow(x - pad.x, 2) + Math.pow(y - pad.y, 2)
                    );
                    
                    if (distance <= pad.radius) {
                        if (recording) {
                            recordedHits.add(new DrumHit(
                                entry.getKey(),
                                System.currentTimeMillis(),
                                velocity
                            ));
                        }
                        // 播放声音
                        playSound(pad.sound, velocity);
                        break;
                    }
                }
            }
            
            private void playSound(String sound, float velocity) {
                // 播放鼓声，音量根据力度调整
            }
        }
    }
}