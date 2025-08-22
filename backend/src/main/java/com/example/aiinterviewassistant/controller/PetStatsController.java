package com.example.aiinterviewassistant.controller;

import com.example.aiinterviewassistant.model.*;
import com.example.aiinterviewassistant.service.PetService;
import com.example.aiinterviewassistant.controller.common.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 宠物统计控制器
 * 负责统计信息相关功能，如获取玩家统计信息等
 * 
 * ✅ 已实现功能：
 * - 获取玩家统计信息
 */
@RestController
@RequestMapping("/api/pets")
@CrossOrigin(origins = "*")
public class PetStatsController {

    @Autowired
    private PetService petService;

    /**
     * ✅ 已实现：获取游戏统计信息
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
            stats.setTotalItems(inventory.size());
            
            if (!pets.isEmpty()) {
                Pet highestLevelPet = pets.stream()
                    .max(Comparator.comparing(pet -> pet.getStats().getLevel()))
                    .orElse(null);
                if (highestLevelPet != null) {
                    stats.setHighestLevel(highestLevelPet.getStats().getLevel());
                    stats.setHighestLevelPetName(highestLevelPet.getName());
                }
                
                int totalExperience = pets.stream()
                    .mapToInt(pet -> pet.getStats().getExperience())
                    .sum();
                stats.setTotalExperience(totalExperience);
            }
            
            int totalAchievementPoints = achievements.size() * 10; // 假设每个成就10分
            stats.setTotalAchievementPoints(totalAchievementPoints);
            
            return ResponseEntity.ok(new ApiResponse<>(true, "获取统计信息成功", stats));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "获取统计信息失败: " + e.getMessage(), null));
        }
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
