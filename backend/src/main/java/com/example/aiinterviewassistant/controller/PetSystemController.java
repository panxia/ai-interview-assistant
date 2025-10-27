package com.example.aiinterviewassistant.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 宠物系统控制器
 * 负责系统级功能，如健康检查等
 * 
 * ✅ 已实现功能：
 * - 健康检查接口
 */
@RestController
@RequestMapping("/api/pets")
@CrossOrigin(origins = "*")
public class PetSystemController {

    /**
     * ✅ 已实现：健康检查接口
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "healthy");
        response.put("service", "pet-service");
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return ResponseEntity.ok(response);
    }
}
