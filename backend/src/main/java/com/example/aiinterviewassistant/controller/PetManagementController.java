package com.example.aiinterviewassistant.controller;

import com.example.aiinterviewassistant.model.*;
import com.example.aiinterviewassistant.service.PetService;
import com.example.aiinterviewassistant.controller.common.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 宠物基础管理控制器
 * 负责宠物的创建、获取、列表等基础操作
 * 
 * ✅ 已实现功能：
 * - 创建新宠物
 * - 获取单个宠物信息
 * - 获取玩家的所有宠物
 * - 获取宠物类型列表
 * - 获取性格类型列表
 */
@RestController
@RequestMapping("/api/pets")
@CrossOrigin(origins = "*")
public class PetManagementController {

    @Autowired
    private PetService petService;

    /**
     * ✅ 已实现：创建新宠物
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
     * ✅ 已实现：获取单个宠物信息
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
     * ✅ 已实现：获取玩家的所有宠物
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
     * ✅ 已实现：获取宠物类型列表
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
     * ✅ 已实现：获取性格类型列表
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
}
