package com.example.aiinterviewassistant.controller;

import com.example.aiinterviewassistant.model.*;
import com.example.aiinterviewassistant.service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 宠物控制器 - 增强版
 * 提供完整的宠物管理、交互和游戏API
 */
@RestController
@RequestMapping("/api/pets")
@CrossOrigin(origins = "*")
public class PetController {

    @Autowired
    private PetService petService;

    /**
     * 创建新宠物
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Pet>> createPet(@RequestBody CreatePetRequest request) {
        try {
            if (request.getPetName() == null || request.getPetName().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "宠物名字不能为空", null));
            }
            
            PetType petType;
            try {
                petType = PetType.valueOf(request.getPetType());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "无效的宠物类型", null));
            }
            
            Pet pet = petService.createPet(
                request.getPlayerId(), 
                request.getPetName(), 
                petType, 
                request.getCustomization()
            );
            
            return ResponseEntity.ok(new ApiResponse<>(true, "宠物创建成功", pet));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "创建宠物失败: " + e.getMessage(), null));
        }
    }

    /**
     * 获取单个宠物信息
     */
    @GetMapping("/{petId}")
    public ResponseEntity<ApiResponse<Pet>> getPet(@PathVariable String petId) {
        try {
            Pet pet = petService.getPet(petId);
            if (pet == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(new ApiResponse<>(true, "获取宠物信息成功", pet));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "获取宠物信息失败: " + e.getMessage(), null));
        }
    }

    /**
     * 获取玩家的所有宠物
     */
    @GetMapping("/player/{playerId}")
    public ResponseEntity<ApiResponse<List<Pet>>> getPlayerPets(@PathVariable String playerId) {
        try {
            List<Pet> pets = petService.getPlayerPets(playerId);
            return ResponseEntity.ok(new ApiResponse<>(true, "获取宠物列表成功", pets));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "获取宠物列表失败: " + e.getMessage(), null));
        }
    }

    /**
     * 喂食宠物
     */
    @PostMapping("/{petId}/feed")
    public ResponseEntity<ApiResponse<Pet>> feedPet(
            @PathVariable String petId, 
            @RequestBody FeedRequest request) {
        try {
            Pet pet = petService.feedPet(petId, request.getItemId());
            return ResponseEntity.ok(new ApiResponse<>(true, "喂食成功", pet));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "喂食失败: " + e.getMessage(), null));
        }
    }

    /**
     * 与宠物玩耍
     */
    @PostMapping("/{petId}/play")
    public ResponseEntity<ApiResponse<Pet>> playWithPet(
            @PathVariable String petId, 
            @RequestBody PlayRequest request) {
        try {
            Pet pet = petService.playWithPet(petId, request.getActivityType());
            return ResponseEntity.ok(new ApiResponse<>(true, "玩耍成功", pet));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "玩耍失败: " + e.getMessage(), null));
        }
    }

    /**
     * 清洁宠物
     */
    @PostMapping("/{petId}/clean")
    public ResponseEntity<ApiResponse<Pet>> cleanPet(@PathVariable String petId) {
        try {
            Pet pet = petService.cleanPet(petId);
            return ResponseEntity.ok(new ApiResponse<>(true, "清洁成功", pet));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "清洁失败: " + e.getMessage(), null));
        }
    }

    /**
     * 宠物休息
     */
    @PostMapping("/{petId}/rest")
    public ResponseEntity<ApiResponse<Pet>> restPet(@PathVariable String petId) {
        try {
            Pet pet = petService.restPet(petId);
            return ResponseEntity.ok(new ApiResponse<>(true, "休息成功", pet));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "休息失败: " + e.getMessage(), null));
        }
    }

    /**
     * 开始小游戏
     */
    @PostMapping("/{petId}/minigames/start")
    public ResponseEntity<ApiResponse<Map<String, Object>>> startMiniGame(
            @PathVariable String petId, 
            @RequestBody StartGameRequest request) {
        try {
            Map<String, Object> gameData = petService.startMiniGame(petId, request.getGameType());
            return ResponseEntity.ok(new ApiResponse<>(true, "游戏开始", gameData));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "开始游戏失败: " + e.getMessage(), null));
        }
    }

    /**
     * 完成小游戏
     */
    @PostMapping("/{petId}/minigames/complete")
    public ResponseEntity<ApiResponse<Pet>> completeMiniGame(
            @PathVariable String petId, 
            @RequestBody CompleteGameRequest request) {
        try {
            Pet pet = petService.completeMiniGame(petId, request.getGameType(), request.getScore());
            return ResponseEntity.ok(new ApiResponse<>(true, "游戏完成", pet));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "完成游戏失败: " + e.getMessage(), null));
        }
    }

    /**
     * 获取玩家成就
     */
    @GetMapping("/player/{playerId}/achievements")
    public ResponseEntity<ApiResponse<List<Achievement>>> getPlayerAchievements(@PathVariable String playerId) {
        try {
            List<Achievement> achievements = petService.getPlayerAchievements(playerId);
            return ResponseEntity.ok(new ApiResponse<>(true, "获取成就成功", achievements));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "获取成就失败: " + e.getMessage(), null));
        }
    }

    /**
     * 获取玩家背包
     */
    @GetMapping("/player/{playerId}/inventory")
    public ResponseEntity<ApiResponse<List<GameItem>>> getPlayerInventory(@PathVariable String playerId) {
        try {
            List<GameItem> inventory = petService.getPlayerInventory(playerId);
            return ResponseEntity.ok(new ApiResponse<>(true, "获取背包成功", inventory));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "获取背包失败: " + e.getMessage(), null));
        }
    }

    /**
     * 获取宠物类型列表
     */
    @GetMapping("/types")
    public ResponseEntity<ApiResponse<List<PetTypeInfo>>> getPetTypes() {
        try {
            List<PetTypeInfo> petTypes = Arrays.asList(
                new PetTypeInfo("CAT", "小猫咪", "🐱", "可爱独立的小猫"),
                new PetTypeInfo("DOG", "小狗狗", "🐶", "忠诚活泼的小狗"),
                new PetTypeInfo("RABBIT", "小兔子", "🐰", "温顺可爱的兔子"),
                new PetTypeInfo("HAMSTER", "小仓鼠", "🐹", "活泼机灵的仓鼠"),
                new PetTypeInfo("DRAGON", "小龙", "🐲", "神秘强大的小龙"),
                new PetTypeInfo("PANDA", "小熊猫", "🐼", "憨厚可爱的熊猫")
            );
            return ResponseEntity.ok(new ApiResponse<>(true, "获取宠物类型成功", petTypes));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "获取宠物类型失败: " + e.getMessage(), null));
        }
    }

    /**
     * 获取性格类型列表
     */
    @GetMapping("/personalities")
    public ResponseEntity<ApiResponse<List<PersonalityTypeInfo>>> getPersonalityTypes() {
        try {
            List<PersonalityTypeInfo> personalityTypes = Arrays.asList(
                new PersonalityTypeInfo("PLAYFUL", "活泼型", "喜欢玩耍，精力充沛"),
                new PersonalityTypeInfo("LAZY", "慵懒型", "喜欢睡觉，享受悠闲"),
                new PersonalityTypeInfo("CURIOUS", "好奇型", "喜欢探索新事物"),
                new PersonalityTypeInfo("AFFECTIONATE", "亲人型", "依恋主人，喜欢陪伴"),
                new PersonalityTypeInfo("INDEPENDENT", "独立型", "自主性强，不需要太多陪伴"),
                new PersonalityTypeInfo("INTELLIGENT", "聪明型", "学习能力强，游戏表现优秀")
            );
            return ResponseEntity.ok(new ApiResponse<>(true, "获取性格类型成功", personalityTypes));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "获取性格类型失败: " + e.getMessage(), null));
        }
    }

    /**
     * 获取游戏统计信息
     */
    @GetMapping("/player/{playerId}/stats")
    public ResponseEntity<ApiResponse<PlayerStats>> getPlayerStats(@PathVariable String playerId) {
        try {
            List<Pet> pets = petService.getPlayerPets(playerId);
            List<Achievement> achievements = petService.getPlayerAchievements(playerId);
            List<GameItem> inventory = petService.getPlayerInventory(playerId);
            
            PlayerStats stats = new PlayerStats();
            stats.setTotalPets(pets.size());
            stats.setTotalAchievements(achievements.size());
            stats.setTotalItems(inventory.stream().mapToInt(GameItem::getQuantity).sum());
            
            if (!pets.isEmpty()) {
                Pet highestLevelPet = pets.stream()
                    .max(Comparator.comparing(pet -> pet.getStats().getLevel()))
                    .orElse(null);
                if (highestLevelPet != null) {
                    stats.setHighestLevel(highestLevelPet.getStats().getLevel());
                    stats.setHighestLevelPetName(highestLevelPet.getPetName());
                }
                
                int totalExperience = pets.stream()
                    .mapToInt(pet -> pet.getStats().getExperience())
                    .sum();
                stats.setTotalExperience(totalExperience);
            }
            
            int totalAchievementPoints = achievements.stream()
                .mapToInt(Achievement::getPoints)
                .sum();
            stats.setTotalAchievementPoints(totalAchievementPoints);
            
            return ResponseEntity.ok(new ApiResponse<>(true, "获取统计信息成功", stats));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "获取统计信息失败: " + e.getMessage(), null));
        }
    }

    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "healthy");
        response.put("service", "pet-service");
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return ResponseEntity.ok(response);
    }

    // ============ 内部类定义 ============

    /**
     * 统一API响应格式
     */
    public static class ApiResponse<T> {
        private boolean success;
        private String message;
        private T data;

        public ApiResponse(boolean success, String message, T data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }

        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public T getData() { return data; }
        public void setData(T data) { this.data = data; }
    }

    /**
     * 创建宠物请求
     */
    public static class CreatePetRequest {
        private String playerId;
        private String petName;
        private String petType;
        private Map<String, Object> customization;

        // Getters and Setters
        public String getPlayerId() { return playerId; }
        public void setPlayerId(String playerId) { this.playerId = playerId; }
        public String getPetName() { return petName; }
        public void setPetName(String petName) { this.petName = petName; }
        public String getPetType() { return petType; }
        public void setPetType(String petType) { this.petType = petType; }
        public Map<String, Object> getCustomization() { return customization; }
        public void setCustomization(Map<String, Object> customization) { this.customization = customization; }
    }

    /**
     * 喂食请求
     */
    public static class FeedRequest {
        private String itemId;

        public String getItemId() { return itemId; }
        public void setItemId(String itemId) { this.itemId = itemId; }
    }

    /**
     * 玩耍请求
     */
    public static class PlayRequest {
        private String activityType;

        public String getActivityType() { return activityType; }
        public void setActivityType(String activityType) { this.activityType = activityType; }
    }

    /**
     * 开始游戏请求
     */
    public static class StartGameRequest {
        private String gameType;

        public String getGameType() { return gameType; }
        public void setGameType(String gameType) { this.gameType = gameType; }
    }

    /**
     * 完成游戏请求
     */
    public static class CompleteGameRequest {
        private String gameType;
        private int score;

        public String getGameType() { return gameType; }
        public void setGameType(String gameType) { this.gameType = gameType; }
        public int getScore() { return score; }
        public void setScore(int score) { this.score = score; }
    }

    /**
     * 宠物类型信息
     */
    public static class PetTypeInfo {
        private String value;
        private String displayName;
        private String emoji;
        private String description;

        public PetTypeInfo(String value, String displayName, String emoji, String description) {
            this.value = value;
            this.displayName = displayName;
            this.emoji = emoji;
            this.description = description;
        }

        // Getters
        public String getValue() { return value; }
        public String getDisplayName() { return displayName; }
        public String getEmoji() { return emoji; }
        public String getDescription() { return description; }
    }

    /**
     * 性格类型信息
     */
    public static class PersonalityTypeInfo {
        private String type;
        private String displayName;
        private String description;

        public PersonalityTypeInfo(String type, String displayName, String description) {
            this.type = type;
            this.displayName = displayName;
            this.description = description;
        }

        // Getters
        public String getType() { return type; }
        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
    }

    /**
     * 玩家统计信息
     */
    public static class PlayerStats {
        private int totalPets;
        private int totalAchievements;
        private int totalItems;
        private int totalExperience;
        private int totalAchievementPoints;
        private int highestLevel;
        private String highestLevelPetName;

        // Getters and Setters
        public int getTotalPets() { return totalPets; }
        public void setTotalPets(int totalPets) { this.totalPets = totalPets; }
        public int getTotalAchievements() { return totalAchievements; }
        public void setTotalAchievements(int totalAchievements) { this.totalAchievements = totalAchievements; }
        public int getTotalItems() { return totalItems; }
        public void setTotalItems(int totalItems) { this.totalItems = totalItems; }
        public int getTotalExperience() { return totalExperience; }
        public void setTotalExperience(int totalExperience) { this.totalExperience = totalExperience; }
        public int getTotalAchievementPoints() { return totalAchievementPoints; }
        public void setTotalAchievementPoints(int totalAchievementPoints) { this.totalAchievementPoints = totalAchievementPoints; }
        public int getHighestLevel() { return highestLevel; }
        public void setHighestLevel(int highestLevel) { this.highestLevel = highestLevel; }
        public String getHighestLevelPetName() { return highestLevelPetName; }
        public void setHighestLevelPetName(String highestLevelPetName) { this.highestLevelPetName = highestLevelPetName; }
    }
}
