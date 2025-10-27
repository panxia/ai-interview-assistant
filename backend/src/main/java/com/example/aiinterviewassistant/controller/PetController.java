package com.example.aiinterviewassistant.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 宠物基础控制器
 * 提供API总览和基础信息
 */
@RestController
@RequestMapping("/api/pets")
@CrossOrigin(origins = "*")
public class PetController {

    /**
     * API 概览和基础信息
     */
    @GetMapping("")
    public ResponseEntity<Map<String, Object>> getApiOverview() {
        Map<String, Object> overview = new HashMap<>();
        overview.put("service", "Pet Park API");
        overview.put("version", "1.0.0");
        overview.put("description", "宠物公园管理系统API");
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("管理功能", "/api/pets/management - 宠物创建、查询、类型信息");
        endpoints.put("交互功能", "/api/pets/interaction - 喂食、玩耍、清洁、休息");
        endpoints.put("游戏功能", "/api/pets/games - 小游戏开始、完成");
        endpoints.put("成就系统", "/api/pets/achievements - 玩家成就查询");
        endpoints.put("背包系统", "/api/pets/inventory - 玩家物品管理");
        endpoints.put("统计数据", "/api/pets/stats - 玩家和宠物统计");
        endpoints.put("系统功能", "/api/pets/system - 健康检查等");
        
        overview.put("modules", endpoints);
        overview.put("timestamp", new Date().toString());
        
        return ResponseEntity.ok(overview);
    }
}