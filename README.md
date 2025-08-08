# 🐾 可爱宠物养成游戏

一个基于Spring Boot和Vue.js的虚拟宠物养成游戏，让您体验温馨的宠物陪伴时光。

![游戏截图](https://via.placeholder.com/800x400/FF69B4/FFFFFF?text=🐾+可爱宠物养成游戏+🐾)

## ✨ 游戏特色

### 🐱 7种可爱宠物
- **小猫咪** 🐱 - 可爱的小猫，喜欢独立但也需要关爱
- **小狗狗** 🐶 - 忠诚的伙伴，活泼好动，需要更多的关注
- **小兔子** 🐰 - 温顺的小兔，喜欢安静的环境
- **小仓鼠** 🐹 - 活泼的小仓鼠，喜欢储存食物
- **小龙** 🐲 - 神秘的小龙，成长潜力巨大
- **小熊猫** 🐼 - 憨憨的小熊猫，喜欢吃竹子
- **小企鹅** 🐧 - 可爱的小企鹅，喜欢凉爽的环境

### 💖 全方位照顾系统
- **饥饿度** 🍽️ - 定时喂食，保持宠物营养充足
- **清洁度** 🛁 - 给宠物洗澡，保持干净整洁
- **快乐度** 😊 - 通过玩耍和互动提升宠物心情
- **能量值** ⚡ - 让宠物休息，恢复体力
- **健康值** 💗 - 关注宠物健康，必要时用药治疗

### 🎮 有趣的小游戏
- **记忆游戏** 🧠 - 记住序列并重复，锻炼记忆力
- **反应游戏** ⚡ - 快速点击出现的目标，测试反应速度
- **猜谜游戏** 🧩 - 回答简单的问题，获得丰厚奖励
- **拍拍游戏** 👆 - 连续点击宠物获得分数和金币

### 🏆 成就系统
- **初始化成就** - 完成基础操作解锁
- **护理大师** - 保持宠物各项数值在高水平
- **游戏高手** - 在各种小游戏中取得好成绩
- **收集专家** - 收集各种稀有物品和饰品

### 🛍️ 丰富的物品系统
#### 🍎 食物类
- 苹果、小鱼、胡萝卜、高级宠物粮等

#### 🎾 玩具类
- 小球、老鼠玩具、激光笔等互动玩具

#### 💊 药品类
- 基础药品、特效药，保持宠物健康

#### 🏰 装饰类
- 小花、城堡等美化环境的装饰品

#### 🎀 饰品类
- 蝴蝶结、帽子等可爱的宠物装饰

## 🚀 技术栈

### 后端
- **Spring Boot 3.x** - 现代化Java框架
- **Java 21** - 最新LTS版本
- **Maven** - 项目管理和构建工具
- **Spring Web** - RESTful API支持
- **Spring Boot Actuator** - 应用监控

### 前端
- **Vue 3** - 渐进式JavaScript框架
- **TypeScript** - 类型安全的JavaScript超集
- **Vite** - 快速的前端构建工具
- **Tailwind CSS** - 实用性优先的CSS框架
- **Axios** - HTTP客户端

### 部署
- **Nginx** - 静态资源服务

## 📦 快速开始

### 环境要求

- Java 17+
- Maven 3.6+
- Node.js 20+
- npm 或 yarn


### 🔧 本地开发

#### 1. 克隆项目
```bash
git clone https://github.com/your-username/cute-pet-game.git
cd cute-pet-game
```

#### 2. 启动后端
```bash
cd backend
mvn clean compile
mvn spring-boot:run
```

#### 3. 启动前端
```bash
cd frontend
npm install
npm run dev
```

#### 4. 访问游戏
打开浏览器访问 [http://localhost:5173](http://localhost:5173)



## 🎯 游戏玩法

### 创建宠物
1. 首次进入游戏时选择心仪的宠物类型
2. 为你的宠物起一个可爱的名字
3. 开始你的养成之旅！

### 日常照顾
- **🍽️ 喂食**: 当饥饿度降低时，使用不同的食物为宠物补充营养
- **🛁 清洁**: 保持宠物的清洁度，让它们看起来更可爱
- **🎾 玩耍**: 通过各种互动提升宠物的快乐度
- **💤 休息**: 让疲惫的宠物休息恢复能量

### 赚取金币
- 完成小游戏获得金币奖励
- 保持宠物良好状态获得日常奖励
- 解锁成就获得额外金币

### 购买物品
- 在商店购买各种食物、玩具和装饰品
- 稀有物品可以带来更好的效果
- 合理管理金币，为宠物提供最好的照顾

## 🏗️ 项目结构

```
cute-pet-game/
├── backend/                 # Spring Boot后端
│   ├── src/main/java/      # Java源代码
│   │   └── com/example/aiinterviewassistant/
│   │       ├── controller/  # REST控制器
│   │       ├── model/      # 数据模型
│   │       └── service/    # 业务逻辑
│   ├── src/main/resources/ # 配置文件
│   └── pom.xml            # Maven配置
├── frontend/               # Vue.js前端
│   ├── src/               # 源代码
│   │   ├── components/    # Vue组件
│   │   └── styles.css     # 样式文件
│   ├── public/            # 静态资源
│   └── package.json       # npm配置
├── deploy/                # 部署配置
│   ├── nginx.conf        # Nginx配置
│   └── mime.types        # MIME类型配置
└── README.md             # 项目说明
```

## 🔧 API文档

### 宠物管理
- `GET /api/pet/types` - 获取所有宠物类型
- `POST /api/pet/create` - 创建新宠物
- `GET /api/pet/{playerId}` - 获取玩家宠物信息
- `POST /api/pet/{playerId}/action/{actionName}` - 执行宠物动作

### 物品系统
- `GET /api/pet/{playerId}/inventory` - 获取玩家背包
- `GET /api/pet/shop/items` - 获取商店物品
- `POST /api/pet/{playerId}/buy/{itemId}` - 购买物品
- `POST /api/pet/{playerId}/use/{itemId}` - 使用物品

### 小游戏
- `GET /api/pet/games/types` - 获取游戏类型
- `POST /api/pet/{playerId}/games/start/{gameType}` - 开始游戏
- `POST /api/pet/{playerId}/games/action` - 游戏操作
- `POST /api/pet/{playerId}/games/end` - 结束游戏

### 成就系统
- `GET /api/pet/{playerId}/achievements` - 获取玩家成就

## 🎨 自定义配置

### 应用配置 (application.yml)
```yaml
server:
  port: 8080

spring:
  application:
    name: cute-pet-game

app:
  allowedOrigins: ${ALLOWED_ORIGINS:http://localhost:5173}
```

### 环境变量
- `ALLOWED_ORIGINS` - 允许的前端访问源地址
- `SPRING_PROFILES_ACTIVE` - Spring配置文件环境

## 🚀 部署指南

### 生产环境部署

1. **构建前端**
```bash
cd frontend
npm run build
```

2. **配置Nginx**
```bash
# 将前端dist文件夹内容复制到nginx静态文件目录
sudo cp -r frontend/dist/* /usr/share/nginx/html/
```

3. **启动后端**
```bash
cd backend
mvn clean package
java -jar target/*.jar
```

### 性能优化建议

- 使用Redis缓存宠物状态数据
- 实现数据库存储替代内存存储
- 添加CDN加速静态资源
- 使用Nginx配置静态资源缓存

## 🤝 贡献指南

我们欢迎社区贡献！请按照以下步骤：

1. Fork 项目
2. 创建功能分支 (`git checkout -b feature/amazing-feature`)
3. 提交更改 (`git commit -m 'Add amazing feature'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 创建 Pull Request

### 开发规范
- 遵循Java代码规范
- 使用TypeScript进行前端开发
- 编写清晰的提交信息
- 添加必要的测试用例

## 📝 更新日志

### v1.0.0 (2025-01-XX)
- 🎉 初始版本发布
- ✨ 支持7种宠物类型
- 🎮 4种小游戏
- 🏆 完整的成就系统
- 🛍️ 丰富的物品商店

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 🙏 致谢

感谢所有为这个项目做出贡献的开发者和玩家们！

## 📞 联系方式

- 项目主页: [GitHub Repository](https://github.com/your-username/cute-pet-game)
- 问题反馈: [Issue Tracker](https://github.com/your-username/cute-pet-game/issues)
- 邮箱: your-email@example.com

---

🐾 **开始您的宠物养成之旅，体验无尽的欢乐与陪伴！** 🐾