package com.example.aiinterviewassistant.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Fish {

    private Long id;

    private String fishId; // 前端生成的唯一ID

    private String name; // 鱼的名字

    private String color; // 鱼的颜色

    private String imageData; // 鱼的图像数据（base64）

    private Double positionX; // X坐标

    private Double positionY; // Y坐标

    private Double direction; // 游动方向

    private Double speed; // 游动速度

    private Double wiggle; // 摆动幅度

    private Double scale; // 缩放比例

    private Integer likes; // 点赞数

    private String author; // 作者

    private LocalDateTime createdAt; // 创建时间

    private LocalDateTime updatedAt; // 更新时间

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
    }
}
