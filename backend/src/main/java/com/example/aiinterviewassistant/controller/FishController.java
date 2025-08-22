package com.example.aiinterviewassistant.controller;

import com.example.aiinterviewassistant.model.Fish;
import com.example.aiinterviewassistant.service.FishService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fishes")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class FishController {

    private final FishService fishService;

    /**
     * 获取所有鱼
     */
    @GetMapping
    public ResponseEntity<List<Fish>> getAllFishes() {
        log.info("获取所有鱼");
        List<Fish> fishes = fishService.getAllFishes();
        return ResponseEntity.ok(fishes);
    }

    /**
     * 根据作者获取鱼
     */
    @GetMapping("/author/{author}")
    public ResponseEntity<List<Fish>> getFishesByAuthor(@PathVariable String author) {
        log.info("根据作者获取鱼: {}", author);
        List<Fish> fishes = fishService.getFishesByAuthor(author);
        return ResponseEntity.ok(fishes);
    }

    /**
     * 保存鱼
     */
    @PostMapping
    public ResponseEntity<Fish> saveFish(@RequestBody Fish fish) {
        log.info("保存鱼: {}", fish.getName());
        Fish savedFish = fishService.saveFish(fish);
        return ResponseEntity.ok(savedFish);
    }

    /**
     * 点赞鱼
     */
    @PostMapping("/{fishId}/like")
    public ResponseEntity<String> likeFish(@PathVariable String fishId) {
        log.info("点赞鱼: {}", fishId);
        boolean success = fishService.likeFish(fishId);
        if (success) {
            return ResponseEntity.ok("点赞成功");
        } else {
            return ResponseEntity.badRequest().body("点赞失败");
        }
    }

    /**
     * 取消点赞鱼
     */
    @PostMapping("/{fishId}/unlike")
    public ResponseEntity<String> unlikeFish(@PathVariable String fishId) {
        log.info("取消点赞鱼: {}", fishId);
        boolean success = fishService.unlikeFish(fishId);
        if (success) {
            return ResponseEntity.ok("取消点赞成功");
        } else {
            return ResponseEntity.badRequest().body("取消点赞失败");
        }
    }

    /**
     * 删除鱼
     */
    @DeleteMapping("/{fishId}")
    public ResponseEntity<String> deleteFish(@PathVariable String fishId) {
        log.info("删除鱼: {}", fishId);
        boolean success = fishService.deleteFish(fishId);
        if (success) {
            return ResponseEntity.ok("删除成功");
        } else {
            return ResponseEntity.badRequest().body("删除失败");
        }
    }

    /**
     * 更新鱼的位置
     */
    @PutMapping("/{fishId}/position")
    public ResponseEntity<String> updateFishPosition(
            @PathVariable String fishId,
            @RequestParam Double positionX,
            @RequestParam Double positionY,
            @RequestParam Double direction) {
        log.info("更新鱼位置: {}, x: {}, y: {}, direction: {}", fishId, positionX, positionY, direction);
        boolean success = fishService.updateFishPosition(fishId, positionX, positionY, direction);
        if (success) {
            return ResponseEntity.ok("位置更新成功");
        } else {
            return ResponseEntity.badRequest().body("位置更新失败");
        }
    }

    /**
     * 初始化示例鱼群
     */
    @PostMapping("/init")
    public ResponseEntity<String> initializeSampleFishes() {
        log.info("初始化示例鱼群");
        fishService.initializeSampleFishes();
        return ResponseEntity.ok("示例鱼群初始化成功");
    }
}
