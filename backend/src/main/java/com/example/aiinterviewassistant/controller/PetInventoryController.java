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
 * 宠物背包控制器
 * 负责背包和物品管理功能，如获取玩家背包等
 * 
 * ✅ 已实现功能：
 * - 获取玩家背包
 */
@RestController
@RequestMapping("/api/pets")
@CrossOrigin(origins = "*")
public class PetInventoryController {

    @Autowired
    private PetService petService;

    /**
     * ✅ 已实现：获取玩家背包
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

}
