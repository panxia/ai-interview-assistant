package com.example.aiinterviewassistant.service;

import com.example.aiinterviewassistant.model.Fish;
import com.example.aiinterviewassistant.repository.FishRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FishService {

    private final FishRepository fishRepository;

    /**
     * 保存鱼
     */
    public Fish saveFish(Fish fish) {
        log.info("保存鱼: {}", fish.getName());
        return fishRepository.save(fish);
    }

    /**
     * 根据前端ID查找鱼
     */
    public Optional<Fish> findByFishId(String fishId) {
        return fishRepository.findByFishId(fishId);
    }

    /**
     * 获取所有鱼
     */
    public List<Fish> getAllFishes() {
        return fishRepository.findAllByOrderByCreatedAtDesc();
    }

    /**
     * 根据作者查找鱼
     */
    public List<Fish> getFishesByAuthor(String author) {
        return fishRepository.findByAuthorOrderByCreatedAtDesc(author);
    }

    /**
     * 点赞鱼
     */
    public boolean likeFish(String fishId) {
        log.info("点赞鱼: {}", fishId);
        int updated = fishRepository.incrementLikes(fishId);
        return updated > 0;
    }

    /**
     * 取消点赞鱼
     */
    public boolean unlikeFish(String fishId) {
        log.info("取消点赞鱼: {}", fishId);
        int updated = fishRepository.decrementLikes(fishId);
        return updated > 0;
    }

    /**
     * 删除鱼
     */
    public boolean deleteFish(String fishId) {
        log.info("删除鱼: {}", fishId);
        Optional<Fish> fishOpt = fishRepository.findByFishId(fishId);
        if (fishOpt.isPresent()) {
            fishRepository.delete(fishOpt.get());
            return true;
        }
        return false;
    }

    /**
     * 更新鱼的位置信息
     */
    public boolean updateFishPosition(String fishId, Double positionX, Double positionY, Double direction) {
        log.info("更新鱼位置: {}, x: {}, y: {}, direction: {}", fishId, positionX, positionY, direction);
        Optional<Fish> fishOpt = fishRepository.findByFishId(fishId);
        if (fishOpt.isPresent()) {
            Fish fish = fishOpt.get();
            fish.setPositionX(positionX);
            fish.setPositionY(positionY);
            fish.setDirection(direction);
            fishRepository.save(fish);
            return true;
        }
        return false;
    }

    /**
     * 初始化示例鱼群
     */
    public void initializeSampleFishes() {
        fishRepository.initializeSampleFishes();
    }
}
