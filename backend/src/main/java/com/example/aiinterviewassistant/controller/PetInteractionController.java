package com.example.aiinterviewassistant.controller;

import com.example.aiinterviewassistant.model.*;
import com.example.aiinterviewassistant.service.PetService;
import com.example.aiinterviewassistant.controller.common.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 宠物交互控制器
 * 负责宠物的日常交互操作，如喂食、玩耍、清洁、休息等
 * 
 * ✅ 已实现功能：
 * - 喂食宠物
 * - 与宠物玩耍
 * - 清洁宠物
 * - 宠物休息
 */
@RestController
@RequestMapping("/api/pets")
@CrossOrigin(origins = "*")
public class PetInteractionController {

    @Autowired
    private PetService petService;

    /**
     * ✅ 已实现：喂食宠物
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
     * ✅ 已实现：与宠物玩耍
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
     * ✅ 已实现：清洁宠物
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
     * ✅ 已实现：宠物休息
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
}
