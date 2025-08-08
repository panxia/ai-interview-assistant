# AI Interview Assistant

一个基于Spring Boot和Vue.js的AI面试助手，提供智能面试问答功能。

## 功能特性

- 🤖 基于豆包API的智能面试官
- 💬 实时对话交互
- 🎯 结构化面试问题
- 📊 面试反馈和建议
- 🎨 现代化Web界面
- 🚀 一键部署脚本

## 技术栈

### 后端
- Spring Boot 3.x
- Java 21
- Maven
- Spring Web
- Spring Boot Actuator

### 前端
- Vue 3
- TypeScript
- Vite
- Tailwind CSS
- Axios

### 部署
- Nginx
- Docker (可选)

## 快速开始

### 环境要求

- Java 17+
- Maven 3.6+
- Node.js 20+
- npm
- nginx

### 安装依赖

1. **安装Java**
   ```bash
   # Ubuntu/Debian
   sudo apt-get install openjdk-17-jdk
   
   # CentOS/RHEL
   sudo yum install java-17-openjdk-devel
   
   # macOS
   brew install openjdk@17
   ```

2. **安装Maven**
   ```bash
   # Ubuntu/Debian
   sudo apt-get install maven
   
   # CentOS/RHEL
   sudo yum install maven
   
   # macOS
   brew install maven
   ```

3. **安装Node.js**
   ```bash
   # 使用nvm安装
   curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.0/install.sh | bash
   nvm install 20
   nvm use 20
   ```

4. **安装nginx**
   ```bash
   # Ubuntu/Debian
   sudo apt-get install nginx
   
   # CentOS/RHEL
   sudo yum install nginx
   
   # macOS
   brew install nginx
   ```

### 环境配置

设置必要的环境变量：

```bash
export DOUBAO_API_KEY='your_api_key_here'
export DOUBAO_MODEL='ep-xxxxxxxx'
```

### 启动服务

#### 方式一：开发环境启动

```bash
# 给脚本执行权限
chmod +x start.sh

# 启动服务
./start.sh
```

#### 方式二：生产环境部署

```bash
# 给脚本执行权限
chmod +x deploy.sh
chmod +x start-production.sh
chmod +x stop.sh

# 快速部署（推荐）
./deploy.sh

# 或者使用完整部署脚本
./start-production.sh
```

#### 方式三：停止服务

```bash
# 停止所有服务
./stop.sh
```

## 脚本说明

### start.sh
- 开发环境启动脚本
- 后端使用Maven启动
- 前端使用Vite开发服务器
- 支持选择启动模式

### deploy.sh
- 生产环境快速部署脚本
- 自动编译前端
- 配置nginx
- 启动后端服务

### start-production.sh
- 完整生产环境部署脚本
- 详细的环境检查
- 多种启动模式选择
- 完整的错误处理

### stop.sh
- 服务停止脚本
- 支持选择性停止
- 端口清理
- 服务状态检查

## 访问地址

- **前端**: http://localhost:80
- **后端API**: http://localhost:8080
- **健康检查**: http://localhost:8080/actuator/health

## 项目结构

```
ai-interview-assistant/
├── backend/                 # Spring Boot后端
│   ├── src/main/java/      # Java源代码
│   ├── src/main/resources/ # 配置文件
│   └── pom.xml            # Maven配置
├── frontend/               # Vue.js前端
│   ├── src/               # 源代码
│   ├── public/            # 静态资源
│   └── package.json       # npm配置
├── deploy/                # 部署配置
│   ├── docker/           # Docker配置
│   └── nginx.conf        # Nginx配置
├── start.sh              # 开发环境启动脚本
├── deploy.sh             # 生产环境部署脚本
├── start-production.sh   # 完整部署脚本
├── stop.sh              # 停止服务脚本
└── README.md            # 项目说明
```

## 开发指南

### 后端开发

```bash
cd backend
mvn spring-boot:run
```

### 前端开发

```bash
cd frontend
npm install
npm run dev
```

### 构建前端

```bash
cd frontend
npm run build
```

## 故障排除

### 常见问题

1. **端口被占用**
   ```bash
   # 查看端口占用
   lsof -i :8080
   lsof -i :80
   
   # 清理端口
   ./stop.sh
   ```

2. **nginx配置错误**
   ```bash
   # 测试nginx配置
   sudo nginx -t
   
   # 重启nginx
   sudo systemctl restart nginx
   ```

3. **前端编译失败**
   ```bash
   # 清理并重新安装依赖
   cd frontend
   rm -rf node_modules package-lock.json
   npm install
   npm run build
   ```

4. **后端启动失败**
   ```bash
   # 检查Java版本
   java -version
   
   # 检查Maven
   mvn --version
   
   # 清理并重新编译
   cd backend
   mvn clean compile
   ```

## 贡献指南

1. Fork 项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 联系方式

如有问题或建议，请提交 Issue 或 Pull Request。