# 部署说明

## 快速部署

### 1. 环境准备

确保已安装以下软件：
- Java 17+
- Maven 3.6+
- Node.js 20+
- nginx

### 2. 设置环境变量

```bash
export DOUBAO_API_KEY='your_api_key_here'
export DOUBAO_MODEL='ep-xxxxxxxx'
```

### 3. 一键部署

```bash
# 给脚本执行权限
chmod +x deploy.sh

# 执行部署
./deploy.sh
```

## 脚本说明

### deploy.sh (推荐)
- 快速生产环境部署
- 自动编译前端
- 配置nginx
- 启动后端服务
- 适合快速部署

### start-production.sh
- 完整生产环境部署
- 详细环境检查
- 多种启动模式
- 完整错误处理
- 适合复杂部署

### stop.sh
- 停止所有服务
- 清理端口
- 检查服务状态

## 部署流程

1. **编译前端**
   - 安装依赖
   - 构建生产版本
   - 生成静态文件

2. **配置nginx**
   - 复制前端文件到nginx目录
   - 创建nginx配置文件
   - 重启nginx服务

3. **启动后端**
   - 编译Java项目
   - 启动Spring Boot应用
   - 检查健康状态

## 访问地址

- **前端**: http://localhost:80
- **后端API**: http://localhost:8080

## 故障排除

### 端口被占用
```bash
./stop.sh
```

### nginx配置错误
```bash
sudo nginx -t
sudo systemctl restart nginx
```

### 前端编译失败
```bash
cd frontend
rm -rf node_modules
npm install
npm run build
```

### 后端启动失败
```bash
cd backend
mvn clean compile
mvn spring-boot:run
```

## 服务管理

### 查看服务状态
```bash
./stop.sh
# 选择选项5查看状态
```

### 重启服务
```bash
./stop.sh
./deploy.sh
```

### 仅重启nginx
```bash
sudo systemctl restart nginx
```

### 仅重启后端
```bash
# 停止后端
./stop.sh
# 选择选项2

# 重新启动
cd backend
mvn spring-boot:run
```
