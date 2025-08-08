package com.example.aiinterviewassistant.controller;

import com.example.aiinterviewassistant.model.*;
import com.example.aiinterviewassistant.service.PetService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 宠物游戏控制器
 * 提供宠物养成游戏的所有API接口
 */
@RestController
@RequestMapping(path = "/api/pet", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "*") // 允许跨域请求
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    /**
     * 创建新宠物
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Pet>> createPet(@RequestBody CreatePetRequest request) {
        try {
            Pet pet = petService.createPet(request.getPlayerId(), request.getPetName(), request.getPetType());
            return ResponseEntity.ok(ApiResponse.success("宠物创建成功！", pet));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("创建宠物失败：" + e.getMessage()));
        }
    }

    /**
     * 获取宠物信息
     */
    @GetMapping("/{playerId}")
    public ResponseEntity<ApiResponse<PetInfo>> getPetInfo(@PathVariable String playerId) {
        Pet pet = petService.getPet(playerId);
        
        // 如果玩家没有宠物，自动创建一个默认宠物
        if (pet == null) {
            try {
                pet = petService.createPet(playerId, "小宠物", PetType.CAT);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(ApiResponse.error("自动创建宠物失败：" + e.getMessage()));
            }
        }

        PetInfo petInfo = new PetInfo(
            pet,
            petService.getCoins(playerId),
            petService.getAvailableActions(playerId),
            petService.getPlayerItems(playerId)
        );

        return ResponseEntity.ok(ApiResponse.success("获取宠物信息成功", petInfo));
    }

    /**
     * 执行宠物动作（支持字符串动作名称）
     */
    @PostMapping("/{playerId}/action")
    public ResponseEntity<ApiResponse<Pet>> executeAction(
            @PathVariable String playerId,
            @RequestBody Map<String, Object> request) {
        
        // 获取动作名称
        Object actionObj = request.get("action");
        if (actionObj == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("动作不能为空"));
        }
        
        // 转换为PetAction枚举
        PetAction action;
        try {
            String actionName = actionObj.toString().toUpperCase();
            action = PetAction.valueOf(actionName);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("无效的动作: " + actionObj));
        }
        
        PetService.ActionResult result = petService.executeAction(playerId, action);
        
        if (result.isSuccess()) {
            return ResponseEntity.ok(ApiResponse.success(result.getMessage(), result.getPet()));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error(result.getMessage()));
        }
    }

    /**
     * 使用物品
     */
    @PostMapping("/{playerId}/use-item")
    public ResponseEntity<ApiResponse<Pet>> useItem(
            @PathVariable String playerId,
            @RequestBody UseItemRequest request) {
        
        PetService.ActionResult result = petService.useItem(playerId, request.getItemId());
        
        if (result.isSuccess()) {
            return ResponseEntity.ok(ApiResponse.success(result.getMessage(), result.getPet()));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error(result.getMessage()));
        }
    }

    /**
     * 购买物品
     */
    @PostMapping("/{playerId}/buy-item")
    public ResponseEntity<ApiResponse<String>> buyItem(
            @PathVariable String playerId,
            @RequestBody BuyItemRequest request) {
        
        PetService.ActionResult result = petService.buyItem(playerId, request.getItemId());
        
        if (result.isSuccess()) {
            return ResponseEntity.ok(ApiResponse.success(result.getMessage()));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error(result.getMessage()));
        }
    }

    /**
     * 获取商店物品
     */
    @GetMapping("/shop")
    public ResponseEntity<ApiResponse<List<GameItem>>> getShopItems() {
        List<GameItem> items = petService.getShopItems();
        return ResponseEntity.ok(ApiResponse.success("获取商店物品成功", items));
    }

    /**
     * 获取宠物类型列表
     */
    @GetMapping("/types")
    public ResponseEntity<ApiResponse<PetType[]>> getPetTypes() {
        return ResponseEntity.ok(ApiResponse.success("获取宠物类型成功", PetType.values()));
    }

    /**
     * 获取玩家统计信息
     */
    @GetMapping("/{playerId}/stats")
    public ResponseEntity<ApiResponse<PlayerStats>> getPlayerStats(@PathVariable String playerId) {
        Pet pet = petService.getPet(playerId);
        if (pet == null) {
            return ResponseEntity.notFound().build();
        }

        PlayerStats stats = new PlayerStats(
            pet.getAgeInDays(),
            pet.getStats().getLevel(),
            pet.getStats().getExperience(),
            petService.getCoins(playerId),
            petService.getPlayerItems(playerId).size()
        );

        return ResponseEntity.ok(ApiResponse.success("获取玩家统计成功", stats));
    }

    // ================= 小游戏API =================

    /**
     * 开始小游戏
     */
    @PostMapping("/{playerId}/minigame/start")
    public ResponseEntity<ApiResponse<MiniGame.GameSession>> startMiniGame(
            @PathVariable String playerId,
            @RequestBody StartGameRequest request) {
        
        // 确保玩家有宠物，如果没有则自动创建
        Pet pet = petService.getPet(playerId);
        if (pet == null) {
            try {
                petService.createPet(playerId, "小宠物", PetType.CAT);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(ApiResponse.error("自动创建宠物失败：" + e.getMessage()));
            }
        }
        
        MiniGame.GameResult result = petService.startMiniGame(playerId, request.getGameType());
        
        if (result.isSuccess()) {
            return ResponseEntity.ok(ApiResponse.success(result.getMessage(), result.getSession()));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error(result.getMessage()));
        }
    }

    /**
     * 处理游戏输入
     */
    @PostMapping("/minigame/{sessionId}/input")
    public ResponseEntity<ApiResponse<MiniGame.GameSession>> processGameInput(
            @PathVariable String sessionId,
            @RequestBody Map<String, Object> input) {
        
        MiniGame.GameResult result = petService.processGameInput(sessionId, input);
        
        if (result.isSuccess()) {
            return ResponseEntity.ok(ApiResponse.success(result.getMessage(), result.getSession()));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error(result.getMessage()));
        }
    }

    /**
     * 获取活跃游戏会话
     */
    @GetMapping("/{playerId}/minigame/active")
    public ResponseEntity<ApiResponse<MiniGame.GameSession>> getActiveGameSession(@PathVariable String playerId) {
        MiniGame.GameSession session = petService.getActiveGameSession(playerId);
        if (session != null) {
            return ResponseEntity.ok(ApiResponse.success("获取活跃游戏会话成功", session));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 获取可用的小游戏类型
     */
    @GetMapping("/minigame/types")
    public ResponseEntity<ApiResponse<MiniGame.GameType[]>> getGameTypes() {
        return ResponseEntity.ok(ApiResponse.success("获取游戏类型成功", MiniGame.GameType.values()));
    }

    // ================= 成就API =================

    /**
     * 获取玩家成就列表
     */
    @GetMapping("/{playerId}/achievements")
    public ResponseEntity<ApiResponse<List<Achievement>>> getPlayerAchievements(@PathVariable String playerId) {
        List<Achievement> achievements = petService.getPlayerAchievements(playerId);
        return ResponseEntity.ok(ApiResponse.success("获取成就列表成功", achievements));
    }

    /**
     * 获取玩家详细统计
     */
    @GetMapping("/{playerId}/detailed-stats")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> getDetailedPlayerStats(@PathVariable String playerId) {
        Map<String, Integer> stats = petService.getPlayerStats(playerId);
        return ResponseEntity.ok(ApiResponse.success("获取详细统计成功", stats));
    }

    // DTO类定义
    public static class CreatePetRequest {
        private String playerId;
        private String petName;
        private PetType petType;

        public String getPlayerId() { return playerId; }
        public void setPlayerId(String playerId) { this.playerId = playerId; }
        public String getPetName() { return petName; }
        public void setPetName(String petName) { this.petName = petName; }
        public PetType getPetType() { return petType; }
        public void setPetType(PetType petType) { this.petType = petType; }
    }

    public static class ActionRequest {
        private PetAction action;

        public PetAction getAction() { return action; }
        public void setAction(PetAction action) { this.action = action; }
        
        // 添加用于调试的方法
        @Override
        public String toString() {
            return "ActionRequest{action=" + action + "}";
        }
    }

    public static class UseItemRequest {
        private String itemId;

        public String getItemId() { return itemId; }
        public void setItemId(String itemId) { this.itemId = itemId; }
    }

    public static class BuyItemRequest {
        private String itemId;

        public String getItemId() { return itemId; }
        public void setItemId(String itemId) { this.itemId = itemId; }
    }

    public static class StartGameRequest {
        private MiniGame.GameType gameType;

        public MiniGame.GameType getGameType() { return gameType; }
        public void setGameType(MiniGame.GameType gameType) { this.gameType = gameType; }
    }

    public static class PetInfo {
        private Pet pet;
        private int coins;
        private List<PetAction> availableActions;
        private List<GameItem> inventory;

        public PetInfo(Pet pet, int coins, List<PetAction> availableActions, List<GameItem> inventory) {
            this.pet = pet;
            this.coins = coins;
            this.availableActions = availableActions;
            this.inventory = inventory;
        }

        public Pet getPet() { return pet; }
        public int getCoins() { return coins; }
        public List<PetAction> getAvailableActions() { return availableActions; }
        public List<GameItem> getInventory() { return inventory; }
    }

    public static class PlayerStats {
        private long petAge;
        private int petLevel;
        private int experience;
        private int coins;
        private int itemCount;

        public PlayerStats(long petAge, int petLevel, int experience, int coins, int itemCount) {
            this.petAge = petAge;
            this.petLevel = petLevel;
            this.experience = experience;
            this.coins = coins;
            this.itemCount = itemCount;
        }

        public long getPetAge() { return petAge; }
        public int getPetLevel() { return petLevel; }
        public int getExperience() { return experience; }
        public int getCoins() { return coins; }
        public int getItemCount() { return itemCount; }
    }

    /**
     * 统一API响应格式
     */
    public static class ApiResponse<T> {
        private boolean success;
        private String message;
        private T data;

        public static <T> ApiResponse<T> success(String message, T data) {
            ApiResponse<T> response = new ApiResponse<>();
            response.success = true;
            response.message = message;
            response.data = data;
            return response;
        }

        public static <T> ApiResponse<T> success(String message) {
            return success(message, null);
        }

        public static <T> ApiResponse<T> error(String message) {
            ApiResponse<T> response = new ApiResponse<>();
            response.success = false;
            response.message = message;
            return response;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public T getData() { return data; }
    }
}
