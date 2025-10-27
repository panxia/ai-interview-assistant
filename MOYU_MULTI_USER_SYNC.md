# 🐟 摸鱼系统 - 多用户数据共享

## ✨ 功能特点

### 🔄 实时数据同步

- **定时同步**: 每 3 秒自动同步一次鱼群数据
- **即时更新**: 用户操作后立即同步，其他用户快速看到变化
- **状态保持**: 同步时保持用户的本地状态（如点赞状态）

### 👥 多用户协作

- **实时共享**: 不同浏览器、不同设备都能看到相同的鱼群
- **即时互动**: 点赞、新增鱼等操作立即同步给所有用户
- **数据一致性**: 所有用户看到的数据保持一致

## 🏗️ 技术实现

### 同步机制

```typescript
// 实时同步鱼群数据
const syncFishData = async () => {
  try {
    const currentTime = Date.now();
    if (currentTime - lastSyncTime.value < SYNC_INTERVAL) {
      return; // 避免频繁同步
    }

    const fishes = await fetchAllFishes();
    lastSyncTime.value = currentTime;

    // 合并新数据，保持用户绘制的鱼的状态
    const currentFishMap = new Map();
    allFishes.value.forEach((fish) => {
      currentFishMap.set(fish.fishId, fish);
    });

    // 更新或添加新鱼
    fishes.forEach((newFish: Fish) => {
      const existingFish = currentFishMap.get(newFish.fishId);
      if (existingFish) {
        // 保持现有鱼的状态（如点赞状态），但更新其他数据
        newFish.liked = existingFish.liked;
        newFish.position = existingFish.position;
        newFish.direction = existingFish.direction;
      }
      currentFishMap.set(newFish.fishId, newFish);
    });

    // 转换为数组并排序
    allFishes.value = Array.from(currentFishMap.values()).sort(
      (a, b) =>
        new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
    );

    // 预加载新鱼的图像
    allFishes.value.forEach((fish) => {
      preloadFishImage(fish);
    });

    console.log(`同步完成，当前鱼群数量: ${allFishes.value.length}`);
  } catch (error) {
    console.error("同步鱼群数据失败:", error);
  }
};
```

### 同步时机

1. **启动时**: 应用启动后立即开始同步
2. **定时同步**: 每 3 秒自动同步一次
3. **操作后**: 保存鱼、点赞等操作后立即同步
4. **页面关闭**: 停止同步，释放资源

### 数据合并策略

- **新增鱼**: 直接添加到鱼群
- **更新鱼**: 保持本地状态，更新其他数据
- **删除鱼**: 从鱼群中移除
- **排序**: 按创建时间倒序排列

## 📱 使用场景

### 多人协作

- **团队活动**: 团队成员一起画鱼，实时看到彼此的创作
- **课堂演示**: 老师学生互动，共同构建鱼群世界
- **在线聚会**: 朋友聚会时一起摸鱼，分享快乐

### 实时互动

- **点赞互动**: 为喜欢的鱼点赞，其他用户立即看到
- **鱼群观察**: 观看鱼群游动，发现新加入的鱼
- **创作分享**: 展示自己的鱼，获得他人认可

## ⚙️ 配置参数

### 同步间隔

```typescript
const SYNC_INTERVAL = 3000; // 3秒同步一次
```

- 可调整同步频率
- 平衡实时性和性能
- 避免过于频繁的 API 调用

### 同步控制

```typescript
// 开始实时同步
const startRealTimeSync = () => {
  syncInterval.value = window.setInterval(syncFishData, SYNC_INTERVAL);
  console.log("开始实时同步鱼群数据...");
};

// 停止实时同步
const stopRealTimeSync = () => {
  if (syncInterval.value) {
    clearInterval(syncInterval.value);
    syncInterval.value = undefined;
    console.log("停止实时同步");
  }
};
```

## 🔍 监控和调试

### 控制台日志

- 同步开始/停止日志
- 同步完成状态
- 错误信息记录
- 鱼群数量变化

### 网络请求

- 定时 API 调用
- 操作后立即同步
- 错误重试机制

## 🚀 性能优化

### 避免重复同步

- 时间间隔控制
- 状态变化检测
- 智能同步策略

### 内存管理

- 图像预加载
- 定时清理
- 资源释放

## 🔮 未来扩展

### 更高级的同步

- WebSocket 实时推送
- 增量数据同步
- 冲突解决机制

### 用户体验

- 同步状态指示
- 离线支持
- 数据缓存

---

**现在多个用户可以在摸鱼系统中实时协作，共同创造鱼群世界！** 🐟✨👥
