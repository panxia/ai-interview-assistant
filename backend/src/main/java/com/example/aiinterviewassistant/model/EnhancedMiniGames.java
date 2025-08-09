package com.example.aiinterviewassistant.model;

import java.util.*;
import java.time.LocalDateTime;

/**
 * 增强版小游戏系统
 * 实现多种类型的游戏：智力、反应、节奏
 */
public class EnhancedMiniGames {
    
    /**
     * 智力游戏类
     */
    public static class IntelligenceGames {
        
        /**
         * 记忆挑战Plus游戏
         */
        public static class MemoryChallengeGame {
            private List<MemorySequence> sequences = new ArrayList<>();
            private int currentLevel = 1;
            private int maxLevel = 10;
            private long timeLimit = 30000; // 30秒
            private boolean usesMultiSensory = true; // 多感官记忆
            
            public static class MemorySequence {
                private List<String> colors;
                private List<String> sounds;
                private List<String> haptics; // 震动模式
                private int difficulty;
                
                public MemorySequence(int difficulty) {
                    this.difficulty = difficulty;
                    this.colors = new ArrayList<>();
                    this.sounds = new ArrayList<>();
                    this.haptics = new ArrayList<>();
                }
                
                // Getters and Setters
                public List<String> getColors() { return colors; }
                public void setColors(List<String> colors) { this.colors = colors; }
                public List<String> getSounds() { return sounds; }
                public void setSounds(List<String> sounds) { this.sounds = sounds; }
                public List<String> getHaptics() { return haptics; }
                public void setHaptics(List<String> haptics) { this.haptics = haptics; }
                public int getDifficulty() { return difficulty; }
            }
            
            public void generateSequence() {
                int length = Math.min(3 + currentLevel, 9);
                MemorySequence sequence = new MemorySequence(currentLevel);
                
                String[] colorOptions = {"red", "blue", "green", "yellow", "purple", "orange", "pink", "cyan"};
                String[] soundOptions = {"beep", "ding", "chime", "click", "pop", "whistle", "bell", "buzz"};
                String[] hapticOptions = {"short", "long", "double", "triple", "pulse", "wave", "tap", "vibrate"};
                
                for (int i = 0; i < length; i++) {
                    sequence.colors.add(colorOptions[new Random().nextInt(colorOptions.length)]);
                    sequence.sounds.add(soundOptions[new Random().nextInt(soundOptions.length)]);
                    sequence.haptics.add(hapticOptions[new Random().nextInt(hapticOptions.length)]);
                }
                
                sequences.add(sequence);
            }
            
            public GameResult checkAnswer(List<String> playerColors, List<String> playerSounds, List<String> playerHaptics) {
                if (sequences.isEmpty()) return new GameResult(false, "没有序列可检查");
                
                MemorySequence current = sequences.get(sequences.size() - 1);
                boolean colorCorrect = current.colors.equals(playerColors);
                boolean soundCorrect = !usesMultiSensory || current.sounds.equals(playerSounds);
                boolean hapticCorrect = !usesMultiSensory || current.haptics.equals(playerHaptics);
                
                if (colorCorrect && soundCorrect && hapticCorrect) {
                    currentLevel++;
                    int score = currentLevel * 100;
                    if (currentLevel > maxLevel) {
                        return new GameResult(true, "游戏完成！", score, true);
                    } else {
                        return new GameResult(true, "进入下一级！", score, false);
                    }
                } else {
                    return new GameResult(false, "记忆错误，游戏结束", 0, true);
                }
            }
            
            // Getters
            public int getCurrentLevel() { return currentLevel; }
            public int getMaxLevel() { return maxLevel; }
            public long getTimeLimit() { return timeLimit; }
            public List<MemorySequence> getSequences() { return sequences; }
        }
        
        /**
         * 拼图游戏
         */
        public static class PuzzleGame {
            private PuzzlePiece[][] pieces;
            private int gridSize;
            private String imagePath;
            private boolean isCompleted = false;
            private int moveCount = 0;
            
            public static class PuzzlePiece {
                private int id;
                private int currentX, currentY;
                private int correctX, correctY;
                private String imageSegment;
                private boolean isCorrectPosition;
                
                public PuzzlePiece(int id, int correctX, int correctY) {
                    this.id = id;
                    this.correctX = correctX;
                    this.correctY = correctY;
                    this.currentX = correctX;
                    this.currentY = correctY;
                }
                
                public boolean isInCorrectPosition() {
                    return currentX == correctX && currentY == correctY;
                }
                
                // Getters and Setters
                public int getId() { return id; }
                public int getCurrentX() { return currentX; }
                public void setCurrentX(int currentX) { this.currentX = currentX; }
                public int getCurrentY() { return currentY; }
                public void setCurrentY(int currentY) { this.currentY = currentY; }
                public int getCorrectX() { return correctX; }
                public int getCorrectY() { return correctY; }
                public String getImageSegment() { return imageSegment; }
                public void setImageSegment(String imageSegment) { this.imageSegment = imageSegment; }
            }
            
            public PuzzleGame(int difficulty) {
                this.gridSize = Math.min(3 + difficulty, 6);
                initializePuzzle();
            }
            
            private void initializePuzzle() {
                pieces = new PuzzlePiece[gridSize][gridSize];
                List<Integer> positions = new ArrayList<>();
                
                // 创建拼图片
                for (int x = 0; x < gridSize; x++) {
                    for (int y = 0; y < gridSize; y++) {
                        positions.add(x * gridSize + y);
                    }
                }
                
                // 打乱顺序
                Collections.shuffle(positions);
                
                int index = 0;
                for (int x = 0; x < gridSize; x++) {
                    for (int y = 0; y < gridSize; y++) {
                        int pieceId = positions.get(index++);
                        int correctX = pieceId / gridSize;
                        int correctY = pieceId % gridSize;
                        pieces[x][y] = new PuzzlePiece(pieceId, correctX, correctY);
                        pieces[x][y].setCurrentX(x);
                        pieces[x][y].setCurrentY(y);
                    }
                }
            }
            
            public GameResult movePiece(int fromX, int fromY, int toX, int toY) {
                if (!isValidMove(fromX, fromY, toX, toY)) {
                    return new GameResult(false, "无效移动");
                }
                
                // 交换拼图片
                PuzzlePiece temp = pieces[fromX][fromY];
                pieces[fromX][fromY] = pieces[toX][toY];
                pieces[toX][toY] = temp;
                
                // 更新位置
                if (pieces[fromX][fromY] != null) {
                    pieces[fromX][fromY].setCurrentX(fromX);
                    pieces[fromX][fromY].setCurrentY(fromY);
                }
                if (pieces[toX][toY] != null) {
                    pieces[toX][toY].setCurrentX(toX);
                    pieces[toX][toY].setCurrentY(toY);
                }
                
                moveCount++;
                
                // 检查是否完成
                if (checkCompletion()) {
                    isCompleted = true;
                    int score = Math.max(1000 - moveCount * 10, 100);
                    return new GameResult(true, "拼图完成！", score, true);
                }
                
                return new GameResult(true, "继续拼图", 0, false);
            }
            
            private boolean isValidMove(int fromX, int fromY, int toX, int toY) {
                return fromX >= 0 && fromX < gridSize && fromY >= 0 && fromY < gridSize &&
                       toX >= 0 && toX < gridSize && toY >= 0 && toY < gridSize &&
                       Math.abs(fromX - toX) + Math.abs(fromY - toY) == 1;
            }
            
            private boolean checkCompletion() {
                for (int x = 0; x < gridSize; x++) {
                    for (int y = 0; y < gridSize; y++) {
                        if (pieces[x][y] != null && !pieces[x][y].isInCorrectPosition()) {
                            return false;
                        }
                    }
                }
                return true;
            }
            
            // Getters
            public PuzzlePiece[][] getPieces() { return pieces; }
            public int getGridSize() { return gridSize; }
            public boolean isCompleted() { return isCompleted; }
            public int getMoveCount() { return moveCount; }
        }
        
        /**
         * 找茬游戏
         */
        public static class SpotDifferenceGame {
            private List<Difference> differences = new ArrayList<>();
            private List<Difference> foundDifferences = new ArrayList<>();
            private int totalDifferences;
            private long timeLimit = 60000; // 60秒
            private boolean useHints = true;
            private int hintsUsed = 0;
            
            public static class Difference {
                private int id;
                private float x, y; // 相对位置 (0-1)
                private String type; // "color", "shape", "missing", "extra"
                private String description;
                private boolean isFound = false;
                
                public Difference(int id, float x, float y, String type, String description) {
                    this.id = id;
                    this.x = x;
                    this.y = y;
                    this.type = type;
                    this.description = description;
                }
                
                public boolean isInArea(float clickX, float clickY, float tolerance) {
                    return Math.abs(x - clickX) < tolerance && Math.abs(y - clickY) < tolerance;
                }
                
                // Getters and Setters
                public int getId() { return id; }
                public float getX() { return x; }
                public float getY() { return y; }
                public String getType() { return type; }
                public String getDescription() { return description; }
                public boolean isFound() { return isFound; }
                public void setFound(boolean found) { isFound = found; }
            }
            
            public void generateDifferences(int count) {
                this.totalDifferences = count;
                differences.clear();
                
                Random random = new Random();
                String[] types = {"color", "shape", "missing", "extra"};
                String[] descriptions = {
                    "颜色不同", "形状变化", "物品消失", "多了物品", 
                    "大小改变", "位置偏移", "图案不同", "阴影变化"
                };
                
                for (int i = 0; i < count; i++) {
                    float x = random.nextFloat();
                    float y = random.nextFloat();
                    String type = types[random.nextInt(types.length)];
                    String desc = descriptions[random.nextInt(descriptions.length)];
                    
                    differences.add(new Difference(i, x, y, type, desc));
                }
            }
            
            public GameResult clickAt(float x, float y) {
                float tolerance = 0.1f; // 10%的容错范围
                
                for (Difference diff : differences) {
                    if (!diff.isFound() && diff.isInArea(x, y, tolerance)) {
                        diff.setFound(true);
                        foundDifferences.add(diff);
                        
                        if (foundDifferences.size() == totalDifferences) {
                            int score = 1000 - hintsUsed * 100;
                            return new GameResult(true, "找到所有不同！", score, true);
                        } else {
                            return new GameResult(true, "找到一个不同：" + diff.getDescription(), 
                                                10, false);
                        }
                    }
                }
                
                return new GameResult(false, "这里没有不同", 0, false);
            }
            
            public GameResult useHint() {
                if (hintsUsed >= 3) {
                    return new GameResult(false, "提示次数已用完");
                }
                
                for (Difference diff : differences) {
                    if (!diff.isFound()) {
                        hintsUsed++;
                        return new GameResult(true, "提示：在 " + diff.getDescription() + " 附近查看");
                    }
                }
                
                return new GameResult(false, "所有不同都已找到");
            }
            
            // Getters
            public List<Difference> getDifferences() { return differences; }
            public List<Difference> getFoundDifferences() { return foundDifferences; }
            public int getTotalDifferences() { return totalDifferences; }
            public long getTimeLimit() { return timeLimit; }
            public int getHintsUsed() { return hintsUsed; }
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
            private int totalTargets = 20;
            private int targetsHit = 0;
            private long startTime;
            private boolean isActive = false;
            
            public static class Target {
                private int id;
                private float x, y;
                private TargetType type;
                private long appearTime;
                private long lifeTime; // 存在时间
                private boolean isClicked = false;
                private int scoreValue;
                
                public enum TargetType {
                    NORMAL(10, 2000, "#4CAF50"),
                    GOLDEN(50, 1500, "#FFD700"),
                    BOMB(-20, 3000, "#F44336"),
                    BONUS(25, 1000, "#9C27B0");
                    
                    public final int scoreValue;
                    public final long lifeTime;
                    public final String color;
                    
                    TargetType(int scoreValue, long lifeTime, String color) {
                        this.scoreValue = scoreValue;
                        this.lifeTime = lifeTime;
                        this.color = color;
                    }
                }
                
                public Target(int id, TargetType type) {
                    this.id = id;
                    this.type = type;
                    this.appearTime = System.currentTimeMillis();
                    this.lifeTime = type.lifeTime;
                    this.scoreValue = type.scoreValue;
                    
                    // 随机位置
                    Random random = new Random();
                    this.x = 0.1f + random.nextFloat() * 0.8f;
                    this.y = 0.1f + random.nextFloat() * 0.8f;
                }
                
                public boolean isExpired() {
                    return System.currentTimeMillis() - appearTime > lifeTime;
                }
                
                public boolean isInRange(float clickX, float clickY, float range) {
                    return Math.abs(x - clickX) < range && Math.abs(y - clickY) < range;
                }
                
                // Getters and Setters
                public int getId() { return id; }
                public float getX() { return x; }
                public float getY() { return y; }
                public TargetType getType() { return type; }
                public boolean isClicked() { return isClicked; }
                public void setClicked(boolean clicked) { isClicked = clicked; }
                public int getScoreValue() { return scoreValue; }
            }
            
            public void startGame() {
                isActive = true;
                startTime = System.currentTimeMillis();
                score = 0;
                targetsHit = 0;
                targets.clear();
                generateTarget();
            }
            
            private void generateTarget() {
                if (targetsHit >= totalTargets) return;
                
                Random random = new Random();
                Target.TargetType[] types = Target.TargetType.values();
                
                // 权重分配：普通目标70%，黄金目标15%，炸弹10%，奖励5%
                float rand = random.nextFloat();
                Target.TargetType type;
                if (rand < 0.7f) type = Target.TargetType.NORMAL;
                else if (rand < 0.85f) type = Target.TargetType.GOLDEN;
                else if (rand < 0.95f) type = Target.TargetType.BOMB;
                else type = Target.TargetType.BONUS;
                
                Target target = new Target(targetsHit, type);
                targets.add(target);
            }
            
            public GameResult clickAt(float x, float y) {
                if (!isActive) return new GameResult(false, "游戏未开始");
                
                float range = 0.05f; // 5%的点击范围
                
                for (Target target : targets) {
                    if (!target.isClicked() && !target.isExpired() && target.isInRange(x, y, range)) {
                        target.setClicked(true);
                        score += target.getScoreValue();
                        targetsHit++;
                        
                        // 生成下一个目标
                        if (targetsHit < totalTargets) {
                            generateTarget();
                        }
                        
                        String message = target.getType() == Target.TargetType.BOMB ? 
                                       "炸弹！扣分！" : "命中！+" + target.getScoreValue();
                        
                        if (targetsHit >= totalTargets) {
                            isActive = false;
                            return new GameResult(true, "游戏完成！最终得分：" + score, score, true);
                        }
                        
                        return new GameResult(true, message, score, false);
                    }
                }
                
                return new GameResult(false, "未命中目标", score, false);
            }
            
            public void updateTargets() {
                // 移除过期目标
                targets.removeIf(Target::isExpired);
                
                // 如果没有活跃目标且游戏仍在进行，生成新目标
                if (isActive && targets.isEmpty() && targetsHit < totalTargets) {
                    generateTarget();
                }
            }
            
            // Getters
            public List<Target> getTargets() { return targets; }
            public int getScore() { return score; }
            public int getTargetsHit() { return targetsHit; }
            public int getTotalTargets() { return totalTargets; }
            public boolean isActive() { return isActive; }
        }
        
        /**
         * 精准射击游戏
         */
        public static class PrecisionShootingGame {
            private List<MovingTarget> targets = new ArrayList<>();
            private int ammo = 10;
            private int score = 0;
            private int hits = 0;
            private boolean gameActive = false;
            private PhysicsEngine physics = new PhysicsEngine();
            
            public static class MovingTarget {
                private int id;
                private float x, y;
                private float velocityX, velocityY;
                private float size;
                private int scoreValue;
                private boolean isHit = false;
                private TargetType type;
                
                public enum TargetType {
                    SLOW_BIG(50, 0.08f, 1.0f),
                    FAST_SMALL(100, 0.04f, 2.0f),
                    BOUNCING(75, 0.06f, 1.5f);
                    
                    public final int scoreValue;
                    public final float size;
                    public final float speedMultiplier;
                    
                    TargetType(int scoreValue, float size, float speedMultiplier) {
                        this.scoreValue = scoreValue;
                        this.size = size;
                        this.speedMultiplier = speedMultiplier;
                    }
                }
                
                public MovingTarget(int id, TargetType type) {
                    this.id = id;
                    this.type = type;
                    this.size = type.size;
                    this.scoreValue = type.scoreValue;
                    
                    Random random = new Random();
                    this.x = random.nextFloat();
                    this.y = random.nextFloat();
                    
                    float speed = 0.01f * type.speedMultiplier;
                    this.velocityX = (random.nextFloat() - 0.5f) * speed;
                    this.velocityY = (random.nextFloat() - 0.5f) * speed;
                }
                
                public void update() {
                    x += velocityX;
                    y += velocityY;
                    
                    // 边界反弹
                    if (x <= 0 || x >= 1) velocityX = -velocityX;
                    if (y <= 0 || y >= 1) velocityY = -velocityY;
                    
                    x = Math.max(0, Math.min(1, x));
                    y = Math.max(0, Math.min(1, y));
                }
                
                public boolean isHitBy(float shotX, float shotY) {
                    float distance = (float) Math.sqrt(Math.pow(x - shotX, 2) + Math.pow(y - shotY, 2));
                    return distance <= size;
                }
                
                // Getters and Setters
                public int getId() { return id; }
                public float getX() { return x; }
                public float getY() { return y; }
                public float getSize() { return size; }
                public int getScoreValue() { return scoreValue; }
                public boolean isHit() { return isHit; }
                public void setHit(boolean hit) { isHit = hit; }
                public TargetType getType() { return type; }
            }
            
            public static class PhysicsEngine {
                private float gravity = 0.001f;
                private float windForce = 0.0005f;
                private float windDirection = 0; // -1 to 1
                
                public ProjectileResult calculateTrajectory(float startX, float startY, 
                                                         float targetX, float targetY, float power) {
                    // 简化的弹道计算
                    float distance = (float) Math.sqrt(Math.pow(targetX - startX, 2) + Math.pow(targetY - startY, 2));
                    float angle = (float) Math.atan2(targetY - startY, targetX - startX);
                    
                    // 考虑重力和风力影响
                    float adjustedX = targetX + windDirection * windForce * distance;
                    float adjustedY = targetY + gravity * distance * distance;
                    
                    return new ProjectileResult(adjustedX, adjustedY, distance * 100, true);
                }
                
                public static class ProjectileResult {
                    public final float finalX, finalY;
                    public final float flightTime;
                    public final boolean hit;
                    
                    public ProjectileResult(float finalX, float finalY, float flightTime, boolean hit) {
                        this.finalX = finalX;
                        this.finalY = finalY;
                        this.flightTime = flightTime;
                        this.hit = hit;
                    }
                }
                
                // Getters and Setters
                public float getGravity() { return gravity; }
                public float getWindForce() { return windForce; }
                public float getWindDirection() { return windDirection; }
                public void setWindDirection(float windDirection) { this.windDirection = windDirection; }
            }
            
            public void startGame() {
                gameActive = true;
                ammo = 10;
                score = 0;
                hits = 0;
                targets.clear();
                
                // 生成移动目标
                Random random = new Random();
                MovingTarget.TargetType[] types = MovingTarget.TargetType.values();
                
                for (int i = 0; i < 5; i++) {
                    MovingTarget.TargetType type = types[random.nextInt(types.length)];
                    targets.add(new MovingTarget(i, type));
                }
            }
            
            public GameResult shoot(float targetX, float targetY, float power) {
                if (!gameActive || ammo <= 0) {
                    return new GameResult(false, "游戏结束或弹药不足");
                }
                
                ammo--;
                
                // 计算弹道
                PhysicsEngine.ProjectileResult result = physics.calculateTrajectory(
                    0.5f, 1.0f, targetX, targetY, power);
                
                // 检查是否命中目标
                for (MovingTarget target : targets) {
                    if (!target.isHit() && target.isHitBy(result.finalX, result.finalY)) {
                        target.setHit(true);
                        hits++;
                        score += target.getScoreValue();
                        
                        if (hits == targets.size()) {
                            gameActive = false;
                            return new GameResult(true, "全部命中！游戏完成！", score, true);
                        }
                        
                        return new GameResult(true, "命中！+" + target.getScoreValue(), score, false);
                    }
                }
                
                if (ammo <= 0) {
                    gameActive = false;
                    return new GameResult(false, "弹药用尽！游戏结束", score, true);
                }
                
                return new GameResult(false, "未命中", score, false);
            }
            
            public void updateTargets() {
                for (MovingTarget target : targets) {
                    if (!target.isHit()) {
                        target.update();
                    }
                }
            }
            
            // Getters
            public List<MovingTarget> getTargets() { return targets; }
            public int getAmmo() { return ammo; }
            public int getScore() { return score; }
            public int getHits() { return hits; }
            public boolean isGameActive() { return gameActive; }
            public PhysicsEngine getPhysics() { return physics; }
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
            private List<BeatNote> beatmap = new ArrayList<>();
            private int score = 0;
            private int combo = 0;
            private int maxCombo = 0;
            private boolean gameActive = false;
            private long gameStartTime;
            private String currentSong;
            
            public static class BeatNote {
                private long timing; // 毫秒时间戳
                private NoteType type;
                private float position; // 0-1 的位置
                private boolean isHit = false;
                private HitJudgment judgment = HitJudgment.NONE;
                
                public enum NoteType {
                    TAP(100),
                    HOLD(200),
                    SWIPE_LEFT(150),
                    SWIPE_RIGHT(150),
                    DOUBLE_TAP(300);
                    
                    public final int baseScore;
                    
                    NoteType(int baseScore) {
                        this.baseScore = baseScore;
                    }
                }
                
                public enum HitJudgment {
                    NONE(0, 0),
                    MISS(0, 0),
                    BAD(50, 0),
                    GOOD(75, 1),
                    GREAT(90, 1),
                    PERFECT(100, 1);
                    
                    public final int scoreMultiplier; // 百分比
                    public final int comboValue;
                    
                    HitJudgment(int scoreMultiplier, int comboValue) {
                        this.scoreMultiplier = scoreMultiplier;
                        this.comboValue = comboValue;
                    }
                }
                
                public BeatNote(long timing, NoteType type, float position) {
                    this.timing = timing;
                    this.type = type;
                    this.position = position;
                }
                
                public HitJudgment judgeHit(long currentTime) {
                    long timeDiff = Math.abs(currentTime - timing);
                    
                    if (timeDiff <= 50) return HitJudgment.PERFECT;
                    else if (timeDiff <= 100) return HitJudgment.GREAT;
                    else if (timeDiff <= 150) return HitJudgment.GOOD;
                    else if (timeDiff <= 200) return HitJudgment.BAD;
                    else return HitJudgment.MISS;
                }
                
                // Getters and Setters
                public long getTiming() { return timing; }
                public NoteType getType() { return type; }
                public float getPosition() { return position; }
                public boolean isHit() { return isHit; }
                public void setHit(boolean hit) { isHit = hit; }
                public HitJudgment getJudgment() { return judgment; }
                public void setJudgment(HitJudgment judgment) { this.judgment = judgment; }
            }
            
            public void generateBeatmap(String songName, int difficulty) {
                this.currentSong = songName;
                beatmap.clear();
                
                Random random = new Random();
                long songDuration = 30000; // 30秒歌曲
                int noteCount = 20 + difficulty * 10; // 根据难度增加音符数量
                
                BeatNote.NoteType[] types = BeatNote.NoteType.values();
                
                for (int i = 0; i < noteCount; i++) {
                    long timing = (long) (random.nextFloat() * songDuration);
                    BeatNote.NoteType type = types[random.nextInt(types.length)];
                    float position = random.nextFloat();
                    
                    beatmap.add(new BeatNote(timing, type, position));
                }
                
                // 按时间排序
                beatmap.sort((a, b) -> Long.compare(a.getTiming(), b.getTiming()));
            }
            
            public void startGame() {
                gameActive = true;
                gameStartTime = System.currentTimeMillis();
                score = 0;
                combo = 0;
                maxCombo = 0;
                
                // 重置所有音符状态
                for (BeatNote note : beatmap) {
                    note.setHit(false);
                    note.setJudgment(BeatNote.HitJudgment.NONE);
                }
            }
            
            public GameResult hitNote(float position, long currentTime) {
                if (!gameActive) return new GameResult(false, "游戏未开始");
                
                long gameTime = currentTime - gameStartTime;
                float positionTolerance = 0.1f;
                
                // 查找最近的未命中音符
                BeatNote closestNote = null;
                long minTimeDiff = Long.MAX_VALUE;
                
                for (BeatNote note : beatmap) {
                    if (!note.isHit() && Math.abs(note.getPosition() - position) <= positionTolerance) {
                        long timeDiff = Math.abs(gameTime - note.getTiming());
                        if (timeDiff < minTimeDiff && timeDiff <= 200) { // 200ms 容错
                            minTimeDiff = timeDiff;
                            closestNote = note;
                        }
                    }
                }
                
                if (closestNote != null) {
                    closestNote.setHit(true);
                    BeatNote.HitJudgment judgment = closestNote.judgeHit(gameTime);
                    closestNote.setJudgment(judgment);
                    
                    // 计算分数
                    int noteScore = (closestNote.getType().baseScore * judgment.scoreMultiplier) / 100;
                    int comboBonus = Math.min(combo / 10, 5); // 连击奖励
                    int finalScore = noteScore + (noteScore * comboBonus / 10);
                    
                    score += finalScore;
                    
                    if (judgment.comboValue > 0) {
                        combo++;
                        maxCombo = Math.max(maxCombo, combo);
                    } else {
                        combo = 0;
                    }
                    
                    // 检查游戏是否结束
                    boolean allHit = beatmap.stream().allMatch(BeatNote::isHit);
                    if (allHit) {
                        gameActive = false;
                        return new GameResult(true, "歌曲完成！", score, true);
                    }
                    
                    return new GameResult(true, judgment.name() + "! +" + finalScore, score, false);
                } else {
                    combo = 0;
                    return new GameResult(false, "MISS!", score, false);
                }
            }
            
            // Getters
            public List<BeatNote> getBeatmap() { return beatmap; }
            public int getScore() { return score; }
            public int getCombo() { return combo; }
            public int getMaxCombo() { return maxCombo; }
            public boolean isGameActive() { return gameActive; }
            public String getCurrentSong() { return currentSong; }
        }
        
        /**
         * 打鼓游戏
         */
        public static class DrumGame {
            private Map<DrumPad, Boolean> padStates = new HashMap<>();
            private List<DrumBeat> sequence = new ArrayList<>();
            private boolean isRecording = false;
            private boolean isPlaying = false;
            private int score = 0;
            private long recordStartTime;
            
            public enum DrumPad {
                KICK("踢鼓", "#FF5722"),
                SNARE("军鼓", "#9C27B0"),
                HI_HAT("踩镲", "#FFC107"),
                CRASH("吊镲", "#4CAF50"),
                TOM1("桶鼓1", "#2196F3"),
                TOM2("桶鼓2", "#FF9800");
                
                public final String displayName;
                public final String color;
                
                DrumPad(String displayName, String color) {
                    this.displayName = displayName;
                    this.color = color;
                }
            }
            
            public static class DrumBeat {
                private DrumPad pad;
                private long timing;
                private float velocity; // 0-1 的力度
                
                public DrumBeat(DrumPad pad, long timing, float velocity) {
                    this.pad = pad;
                    this.timing = timing;
                    this.velocity = velocity;
                }
                
                // Getters
                public DrumPad getPad() { return pad; }
                public long getTiming() { return timing; }
                public float getVelocity() { return velocity; }
            }
            
            public GameResult hitPad(DrumPad pad, float velocity) {
                long currentTime = System.currentTimeMillis();
                
                // 记录模式
                if (isRecording) {
                    long relativeTime = currentTime - recordStartTime;
                    sequence.add(new DrumBeat(pad, relativeTime, velocity));
                    return new GameResult(true, "记录：" + pad.displayName);
                }
                
                // 自由演奏模式
                if (!isPlaying) {
                    return new GameResult(true, "演奏：" + pad.displayName);
                }
                
                // 跟谱模式 - 这里可以添加跟谱逻辑
                return new GameResult(true, "跟谱：" + pad.displayName);
            }
            
            public void startRecording() {
                isRecording = true;
                recordStartTime = System.currentTimeMillis();
                sequence.clear();
            }
            
            public void stopRecording() {
                isRecording = false;
            }
            
            public void playSequence() {
                isPlaying = true;
                // 这里应该实现序列播放逻辑
            }
            
            public void stopPlaying() {
                isPlaying = false;
            }
            
            // Getters
            public Map<DrumPad, Boolean> getPadStates() { return padStates; }
            public List<DrumBeat> getSequence() { return sequence; }
            public boolean isRecording() { return isRecording; }
            public boolean isPlaying() { return isPlaying; }
            public int getScore() { return score; }
        }
    }
    
    /**
     * 游戏结果类
     */
    public static class GameResult {
        private boolean success;
        private String message;
        private int score;
        private boolean gameEnded;
        
        public GameResult(boolean success, String message) {
            this(success, message, 0, false);
        }
        
        public GameResult(boolean success, String message, int score) {
            this(success, message, score, false);
        }
        
        public GameResult(boolean success, String message, int score, boolean gameEnded) {
            this.success = success;
            this.message = message;
            this.score = score;
            this.gameEnded = gameEnded;
        }
        
        // Getters
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public int getScore() { return score; }
        public boolean isGameEnded() { return gameEnded; }
    }
}