package com.example.aiinterviewassistant.controller;

import com.example.aiinterviewassistant.model.*;
import com.example.aiinterviewassistant.service.PetService;
import com.example.aiinterviewassistant.controller.common.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 宠物小游戏控制器
 * 负责小游戏的开始、完成等游戏相关操作
 * 
 * ✅ 已实现功能：
 * - 开始小游戏
 * - 完成小游戏
 */
@RestController
@RequestMapping("/api/pets")
@CrossOrigin(origins = "*")
public class PetGameController {

    @Autowired
    private PetService petService;

    /**
     * ✅ 已实现：开始小游戏
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
     * ✅ 已实现：完成小游戏
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
}
