# 🏞️ 宠物公园 - 全新升级版

一个基于Spring Boot和Vue.js的虚拟宠物养成游戏，在这个温馨的宠物公园里，体验与可爱宠物们的美好时光。

![游戏截图](https://via.placeholder.com/800x400/FF69B4/FFFFFF?text=🏞️+宠物公园+🏞️)

## ✨ 全新游戏特色

### 🎨 深度外观自定义系统
- **完全可定制的外观** - 打造独一无二的专属宠物
- **身体部位自定义** - 头部形状、耳朵类型、眼睛样式、嘴巴表情
- **丰富的颜色系统** - RGB色轮选择器、预设调色板、支持异瞳
- **多样化图案系统** - 条纹、斑点、渐变、心形、鳞片等图案
- **装饰品系统** - 帽子、项圈、眼镜等可爱装饰
- **身体比例调节** - 胖瘦、高矮、头部大小、眼睛大小等比例调整

### 🎭 丰富的动画和表情系统
- **多种动画状态** - 闲置、行走、玩耍、睡觉、进食等动画
- **情绪表达系统** - 开心、困倦、生气、好奇等不同情绪
- **动态特效** - 爱心飘散、睡眠Z字、蒸汽等特殊效果
- **呼吸动画** - 自然的呼吸起伏效果
- **尾巴摇摆** - 根据心情调整摇摆频率

### 🧠 智能性格系统
- **8种性格特征** - 活泼度、亲人度、独立性、好奇心、食欲、社交性、智力、精力
- **行为影响** - 性格特征影响宠物的自主行为和游戏表现
- **个性化推荐** - 基于性格推荐最适合的活动和游戏
- **性格描述** - 动态生成个性化的宠物性格描述
- **成长演化** - 通过互动和经验逐渐调整性格

### 🐱 7种可爱宠物
- **小猫咪** 🐱 - 独立好奇，智力很高，喜欢探索
- **小狗狗** 🐶 - 忠诚活泼，亲人度极高，需要更多关注
- **小兔子** 🐰 - 温顺安静，喜欢平和的环境
- **小仓鼠** 🐹 - 活泼好动，好奇心旺盛，喜欢储存食物
- **小龙** 🐲 - 神秘独立，智力超群，成长潜力巨大
- **小熊猫** 🐼 - 憨憨可爱，食欲很强，喜欢吃竹子
- **小企鹅** 🐧 - 社交能力强，喜欢凉爽的环境

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
- **时尚达人** - 自定义宠物外观成就
- **造型大师** - 多次自定义外观的高级成就

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
- **Java 17** - LTS版本，支持最新特性
- **Maven** - 项目管理和构建工具
- **Spring Web** - RESTful API支持
- **Spring Boot Actuator** - 应用监控

### 前端
- **Vue 3** - 渐进式JavaScript框架
- **TypeScript** - 类型安全的JavaScript超集
- **Vite** - 快速的前端构建工具
- **Tailwind CSS** - 实用性优先的CSS框架
- **@vueuse/core** - Vue组合式API工具集

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

> **作者：panpan** 🧑‍💻 感谢您对宠物公园项目的关注！

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
打开浏览器访问 [http://localhost:5173/](http://localhost:5173/)

## 🎯 游戏玩法

### 创建宠物
1. 首次进入游戏时选择心仪的宠物类型
2. 为你的宠物起一个可爱的名字
3. 开始你的养成之旅！

### 🎨 外观自定义
1. 点击"宠物造型师"进入自定义界面
2. 选择身体部位进行个性化设计
3. 调整颜色、图案、装饰品
4. 实时预览效果并保存

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
pet-park/
├── backend/                 # Spring Boot后端
│   ├── src/main/java/      # Java源代码
│   │   └── com/example/aiinterviewassistant/
│   │       ├── controller/  # REST控制器
│   │       ├── model/      # 数据模型
│   │       │   ├── Pet.java # 宠物实体（新增外观和性格系统）
│   │       │   ├── PetAppearance.java # 外观配置
│   │       │   ├── PetPersonality.java # 性格系统
│   │       │   └── ...     # 其他模型
│   │       └── service/    # 业务逻辑
│   ├── src/main/resources/ # 配置文件
│   └── pom.xml            # Maven配置
├── frontend/               # Vue.js前端
│   ├── src/               # 源代码
│   │   ├── components/    # Vue组件
│   │   │   ├── PetRenderer.vue # 全新SVG宠物渲染器
│   │   │   ├── PetCustomizer.vue # 外观自定义组件
│   │   │   └── PetGame.vue # 主游戏界面
│   │   └── styles.css     # 样式文件
│   ├── public/            # 静态资源
│   └── package.json       # npm配置
├── deploy/                # 部署配置
│   ├── nginx.conf        # Nginx配置
│   └── mime.types        # MIME类型配置
├── GAME_DESIGN_PLAYABILITY.md # 游戏设计文档
└── README.md             # 项目说明
```

## 🔧 API文档

### 宠物管理
- `GET /api/pet/types` - 获取所有宠物类型
- `POST /api/pet/create` - 创建新宠物
- `GET /api/pet/{playerId}` - 获取玩家宠物信息
- `POST /api/pet/{playerId}/action/{actionName}` - 执行宠物动作

### 🎨 外观自定义 (新增)
- `POST /api/pet/{playerId}/appearance` - 更新宠物外观
- `GET /api/pet/appearance/options` - 获取外观配置选项
- `POST /api/pet/{playerId}/personality` - 更新宠物性格
- `GET /api/pet/{playerId}/recommendations` - 获取个性化推荐

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
    name: pet-park

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

## 🆕 最新更新 (v2.0)

### ✨ 新增功能
1. **完整的外观自定义系统**
   - 身体部位、颜色、图案、装饰品全面自定义
   - 实时预览和动画效果
   - 支持异瞳等特殊特征

2. **智能性格系统**
   - 8种性格特征影响宠物行为
   - 个性化活动推荐
   - 性格演化机制

3. **丰富的动画系统**
   - SVG矢量动画，流畅自然
   - 多种动画状态和情绪表达
   - 动态特效和呼吸动画

4. **增强的成就系统**
   - 新增外观自定义相关成就
   - 更丰富的奖励机制

### 🔧 技术改进
- 升级为SVG渲染系统，支持矢量动画
- 增强的后端模型支持复杂的宠物属性
- 优化的前端组件架构
- 更好的TypeScript类型支持

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

### v2.0.0 (2025-01-XX) 🎉 重大更新
- 🎨 **全新外观自定义系统**：支持身体部位、颜色、图案、装饰品的完全自定义
- 🧠 **智能性格系统**：8种性格特征影响宠物行为和推荐
- 🎭 **丰富动画系统**：SVG矢量动画，多种动画状态和情绪表达
- 🏆 **扩展成就系统**：新增外观自定义相关成就
- 📱 **移动端优化**：更好的触控体验和响应式设计
- 🔧 **技术升级**：全面的后端模型重构和前端组件优化

### v1.0.0 (2025-01-XX)
- 🎉 宠物公园正式开园！
- ✨ 支持7种可爱宠物入园
- 🎮 4种趣味小游戏
- 🏆 完整的成就系统
- 🛍️ 丰富的宠物用品商店
- 👨‍💻 由panpan精心打造

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 🙏 致谢

感谢所有为宠物公园项目做出贡献的开发者和玩家们！
特别感谢每一位愿意体验这个温馨小游戏的用户。

## 📞 联系方式

- 项目主页: [GitHub Repository](https://github.com/panpan/pet-park)
- 问题反馈: [Issue Tracker](https://github.com/panpan/pet-park/issues)
- 作者: panpan
- 邮箱: panpan@example.com

---

🏞️ **欢迎来到全新升级的宠物公园，开始您的个性化养成之旅！** 🏞️