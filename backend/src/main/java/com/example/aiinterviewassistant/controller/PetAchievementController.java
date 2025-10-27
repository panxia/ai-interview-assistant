package com.example.aiinterviewassistant.controller;

import com.example.aiinterviewassistant.model.*;
import com.example.aiinterviewassistant.service.PetService;
import com.example.aiinterviewassistant.controller.common.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 宠物成就控制器
 * 负责成就相关的功能，如获取玩家成就等
 * 
 * ✅ 已实现功能：
 * - 获取玩家成就
 */
@RestController
@RequestMapping("/api/pets")
@CrossOrigin(origins = "*")
public class PetAchievementController {

    @Autowired
    private PetService petService;

    /**
     * ✅ 已实现：获取玩家成就
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

}
