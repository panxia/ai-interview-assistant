package com.example.aiinterviewassistant.repository;

import com.example.aiinterviewassistant.model.Fish;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class FishRepository {

    // 使用内存存储，线程安全
    private final Map<String, Fish> fishMap = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    /**
     * 保存鱼
     */
    public Fish save(Fish fish) {
        if (fish.getId() == null) {
            fish.setId(idGenerator.getAndIncrement());
        }

        // 确保日期字段不为null
        if (fish.getCreatedAt() == null) {
            fish.setCreatedAt(java.time.LocalDateTime.now());
        }
        fish.setUpdatedAt(java.time.LocalDateTime.now());

        fishMap.put(fish.getFishId(), fish);
        return fish;
    }

    /**
     * 根据前端ID查找鱼
     */
    public Optional<Fish> findByFishId(String fishId) {
        return Optional.ofNullable(fishMap.get(fishId));
    }

    /**
     * 获取所有鱼，按创建时间倒序排列
     */
    public List<Fish> findAllByOrderByCreatedAtDesc() {
        return fishMap.values().stream()
                .filter(fish -> fish.getCreatedAt() != null) // 过滤掉没有创建时间的鱼
                .sorted(Comparator.comparing(Fish::getCreatedAt,
                        Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .collect(Collectors.toList());
    }

    /**
     * 根据作者查找鱼
     */
    public List<Fish> findByAuthorOrderByCreatedAtDesc(String author) {
        return fishMap.values().stream()
                .filter(fish -> author.equals(fish.getAuthor()) && fish.getCreatedAt() != null)
                .sorted(Comparator.comparing(Fish::getCreatedAt,
                        Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .collect(Collectors.toList());
    }

    /**
     * 点赞鱼
     */
    public int incrementLikes(String fishId) {
        Fish fish = fishMap.get(fishId);
        if (fish != null) {
            fish.setLikes(fish.getLikes() + 1);
            fish.setUpdatedAt(java.time.LocalDateTime.now());
            return 1;
        }
        return 0;
    }

    /**
     * 取消点赞鱼
     */
    public int decrementLikes(String fishId) {
        Fish fish = fishMap.get(fishId);
        if (fish != null) {
            fish.setLikes(Math.max(fish.getLikes() - 1, 0));
            fish.setUpdatedAt(java.time.LocalDateTime.now());
            return 1;
        }
        return 0;
    }

    /**
     * 删除鱼
     */
    public void delete(Fish fish) {
        fishMap.remove(fish.getFishId());
    }

    /**
     * 获取所有鱼的数量
     */
    public long count() {
        return fishMap.size();
    }

    /**
     * 初始化一些示例鱼
     */
    public void initializeSampleFishes() {
        if (fishMap.isEmpty()) {
            List<Fish> sampleFishes = Arrays.asList(
                    Fish.builder()
                            .fishId("sample-1")
                            .name("小蓝")
                            .color("#4ECDC4")
                            .imageData("")
                            .positionX(150.0)
                            .positionY(150.0)
                            .direction(Math.random() * Math.PI * 2)
                            .speed(1.0 + Math.random() * 2)
                            .wiggle(0.1 + Math.random() * 0.2)
                            .scale(0.3 + Math.random() * 0.4)
                            .likes((int) (Math.random() * 10) + 1)
                            .author("系统")
                            .createdAt(java.time.LocalDateTime.now().minusDays((long) (Math.random() * 7)))
                            .updatedAt(java.time.LocalDateTime.now())
                            .build(),
                    Fish.builder()
                            .fishId("sample-2")
                            .name("小红")
                            .color("#FF6B6B")
                            .imageData("")
                            .positionX(400.0)
                            .positionY(200.0)
                            .direction(Math.random() * Math.PI * 2)
                            .speed(1.0 + Math.random() * 2)
                            .wiggle(0.1 + Math.random() * 0.2)
                            .scale(0.3 + Math.random() * 0.4)
                            .likes((int) (Math.random() * 10) + 1)
                            .author("系统")
                            .createdAt(java.time.LocalDateTime.now().minusDays((long) (Math.random() * 7)))
                            .updatedAt(java.time.LocalDateTime.now())
                            .build(),
                    Fish.builder()
                            .fishId("sample-3")
                            .name("小黄")
                            .color("#FFEAA7")
                            .imageData("")
                            .positionX(600.0)
                            .positionY(300.0)
                            .direction(Math.random() * Math.PI * 2)
                            .speed(1.0 + Math.random() * 2)
                            .wiggle(0.1 + Math.random() * 0.2)
                            .scale(0.3 + Math.random() * 0.4)
                            .likes((int) (Math.random() * 10) + 1)
                            .author("系统")
                            .createdAt(java.time.LocalDateTime.now().minusDays((long) (Math.random() * 7)))
                            .updatedAt(java.time.LocalDateTime.now())
                            .build());

            sampleFishes.forEach(this::save);
        }
    }
}
