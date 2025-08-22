# 🐟 摸鱼系统 - 内存存储版本

## ✨ 系统特点

### 🚀 无数据库依赖

- **内存存储**: 使用 Java 内存存储，无需配置数据库
- **快速启动**: 应用启动后立即可用
- **简单部署**: 不需要额外的数据库服务

### 🔄 数据持久化

- **应用重启**: 数据会重置为示例鱼群
- **内存共享**: 同一应用实例内的所有用户共享鱼群数据
- **实时同步**: 不同浏览器访问同一后端实例能看到相同的鱼群

## 🏗️ 技术架构

### 后端架构

```
FishController (REST API)
    ↓
FishService (业务逻辑)
    ↓
FishRepository (内存存储)
    ↓
ConcurrentHashMap + 示例数据
```

### 核心组件

1. **Fish**: 鱼的数据模型
2. **FishRepository**: 内存存储实现
3. **FishService**: 业务逻辑服务
4. **FishController**: REST API 接口
5. **FishInitializer**: 启动时初始化示例鱼群

## 📡 API 接口

### 基础接口

- `GET /api/fishes` - 获取所有鱼
- `POST /api/fishes` - 保存新鱼
- `GET /api/fishes/author/{author}` - 根据作者获取鱼

### 互动接口

- `POST /api/fishes/{fishId}/like` - 点赞鱼
- `POST /api/fishes/{fishId}/unlike` - 取消点赞
- `DELETE /api/fishes/{fishId}` - 删除鱼

### 管理接口

- `PUT /api/fishes/{fishId}/position` - 更新鱼的位置
- `POST /api/fishes/init` - 初始化示例鱼群

## 🎯 使用场景

### 开发测试

- 快速原型开发
- 功能测试验证
- 演示展示

### 小型部署

- 个人项目
- 小团队使用
- 临时活动

### 学习用途

- Spring Boot 学习
- 前端 Canvas 学习
- 实时交互学习

## ⚠️ 注意事项

### 数据限制

- 应用重启后数据丢失
- 内存容量限制
- 不支持数据备份

### 扩展性

- 单实例部署
- 不支持集群
- 负载能力有限

## 🚀 部署说明

### 1. 启动后端

```bash
cd backend
mvn spring-boot:run
```

### 2. 启动前端

```bash
cd frontend
npm run dev
```

### 3. 访问应用

- 前端: http://localhost:3000
- 后端 API: http://localhost:8080/api/fishes

## 🔮 未来扩展

如果需要持久化存储，可以考虑：

- Redis 缓存
- 文件存储
- 关系型数据库
- NoSQL 数据库

---

**现在摸鱼系统使用内存存储，启动更快，部署更简单！** 🐟✨
