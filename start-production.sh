#!/bin/bash

echo "🚀 AI Interview Assistant 生产环境启动脚本"
echo "=========================================="

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 检查Java
check_java() {
    echo -e "${BLUE}检查Java环境...${NC}"
    if ! command -v java &> /dev/null; then
        echo -e "${RED}❌ Java未安装，请先安装Java 17${NC}"
        exit 1
    fi
    
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
    if [ "$JAVA_VERSION" -lt "17" ]; then
        echo -e "${RED}❌ Java版本过低，需要Java 17或更高版本${NC}"
        exit 1
    fi
    
    echo -e "${GREEN}✅ Java版本: $(java -version 2>&1 | head -n 1)${NC}"
}

# 检查Maven
check_maven() {
    echo -e "${BLUE}检查Maven环境...${NC}"
    if ! command -v mvn &> /dev/null; then
        echo -e "${RED}❌ Maven未安装，请先安装Maven${NC}"
        exit 1
    fi
    
    echo -e "${GREEN}✅ Maven版本: $(mvn --version | head -n 1)${NC}"
}

# 检查Node.js
check_node() {
    echo -e "${BLUE}检查Node.js环境...${NC}"
    if ! command -v node &> /dev/null; then
        echo -e "${RED}❌ Node.js未安装，请先安装Node.js 20+${NC}"
        exit 1
    fi
    
    NODE_VERSION=$(node --version | cut -d'v' -f2 | cut -d'.' -f1)
    if [ "$NODE_VERSION" -lt "20" ]; then
        echo -e "${RED}❌ Node.js版本过低，需要Node.js 20或更高版本${NC}"
        exit 1
    fi
    
    echo -e "${GREEN}✅ Node.js版本: $(node --version)${NC}"
}

# 检查npm
check_npm() {
    echo -e "${BLUE}检查npm环境...${NC}"
    if ! command -v npm &> /dev/null; then
        echo -e "${RED}❌ npm未安装${NC}"
        exit 1
    fi
    
    echo -e "${GREEN}✅ npm版本: $(npm --version)${NC}"
}

# 检查nginx
check_nginx() {
    echo -e "${BLUE}检查nginx环境...${NC}"
    if ! command -v nginx &> /dev/null; then
        echo -e "${RED}❌ nginx未安装，请先安装nginx${NC}"
        echo "安装命令:"
        echo "  Ubuntu/Debian: sudo apt-get install nginx"
        echo "  CentOS/RHEL: sudo yum install nginx"
        echo "  macOS: brew install nginx"
        exit 1
    fi
    
    echo -e "${GREEN}✅ nginx版本: $(nginx -v 2>&1)${NC}"
}

# 设置环境变量
setup_env() {
    echo -e "${BLUE}设置环境变量...${NC}"
    
    if [ -z "$DOUBAO_API_KEY" ]; then
        echo -e "${YELLOW}⚠️  警告: DOUBAO_API_KEY 环境变量未设置${NC}"
        echo "请设置您的豆包API密钥:"
        echo "export DOUBAO_API_KEY='your_api_key_here'"
        echo ""
    fi
    
    if [ -z "$DOUBAO_MODEL" ]; then
        echo -e "${YELLOW}⚠️  警告: DOUBAO_MODEL 环境变量未设置${NC}"
        echo "请设置您的模型端点ID:"
        echo "export DOUBAO_MODEL='ep-xxxxxxxx'"
        echo ""
    fi
    
    # 设置默认值
    export DOUBAO_API_BASE=${DOUBAO_API_BASE:-"https://ark.cn-beijing.volces.com/api/v3"}
    export ALLOWED_ORIGINS=${ALLOWED_ORIGINS:-"http://localhost:80"}
    export SYSTEM_PROMPT=${SYSTEM_PROMPT:-"你是一个专业的面试官，擅长结构化提问与基于STAR法的追问，回答请简洁、条理清晰，并在必要时给出改进建议。"}
    
    echo -e "${GREEN}✅ 环境变量设置完成${NC}"
}

# 编译前端
build_frontend() {
    echo -e "${BLUE}编译前端项目...${NC}"
    cd frontend
    
    # 检查node_modules是否存在
    if [ ! -d "node_modules" ]; then
        echo "安装前端依赖..."
        npm install
    fi
    
    # 清理之前的构建
    echo "清理之前的构建..."
    rm -rf dist
    
    # 编译前端
    echo "编译前端项目..."
    npm run build
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ 前端编译成功${NC}"
    else
        echo -e "${RED}❌ 前端编译失败${NC}"
        exit 1
    fi
    
    cd ..
}

# 配置nginx
setup_nginx() {
    echo -e "${BLUE}配置nginx...${NC}"
    
    # 创建nginx配置目录
    sudo mkdir -p /etc/nginx/sites-available
    sudo mkdir -p /etc/nginx/sites-enabled
    
    # 复制前端构建文件到nginx目录
    echo "复制前端文件到nginx目录..."
    sudo rm -rf /usr/share/nginx/html/*
    sudo cp -r frontend/dist/* /usr/share/nginx/html/
    
    # 创建nginx配置文件
    cat > /tmp/ai-interview-assistant.conf << 'EOF'
server {
    listen       80;
    server_name  _;

    gzip on;
    gzip_types text/plain application/javascript text/css application/json image/svg+xml;

    root /usr/share/nginx/html;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /api/ {
        proxy_pass http://localhost:8080/api/;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}
EOF

    # 复制配置文件到nginx
    sudo cp /tmp/ai-interview-assistant.conf /etc/nginx/sites-available/
    sudo ln -sf /etc/nginx/sites-available/ai-interview-assistant.conf /etc/nginx/sites-enabled/
    
    # 测试nginx配置
    echo "测试nginx配置..."
    sudo nginx -t
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ nginx配置测试通过${NC}"
    else
        echo -e "${RED}❌ nginx配置测试失败${NC}"
        exit 1
    fi
    
    # 重启nginx
    echo "重启nginx服务..."
    sudo systemctl restart nginx
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ nginx服务启动成功${NC}"
    else
        echo -e "${RED}❌ nginx服务启动失败${NC}"
        exit 1
    fi
}

# 启动后端
start_backend() {
    echo -e "${BLUE}启动后端服务...${NC}"
    cd backend
    
    # 清理并编译
    echo "清理并编译项目..."
    mvn clean compile -q
    
    # 启动Spring Boot
    echo "启动Spring Boot应用..."
    mvn spring-boot:run &
    BACKEND_PID=$!
    
    # 等待后端启动
    echo "等待后端服务启动..."
    sleep 10
    
    # 检查后端是否启动成功
    if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
        echo -e "${GREEN}✅ 后端服务启动成功${NC}"
    else
        echo -e "${YELLOW}⚠️  后端服务可能还在启动中，请稍等...${NC}"
    fi
    
    cd ..
}

# 显示服务信息
show_info() {
    echo ""
    echo -e "${GREEN}🎉 生产环境服务启动完成！${NC}"
    echo "=================================="
    echo -e "${BLUE}前端地址:${NC} http://localhost:80"
    echo -e "${BLUE}后端API:${NC} http://localhost:8080"
    echo ""
    echo -e "${YELLOW}使用说明:${NC}"
    echo "1. 在浏览器中访问 http://localhost:80"
    echo "2. 开始您的AI面试体验"
    echo "3. 按 Ctrl+C 停止后端服务"
    echo "4. nginx服务会继续运行，需要手动停止"
    echo ""
    echo -e "${YELLOW}服务管理:${NC}"
    echo "停止nginx: sudo systemctl stop nginx"
    echo "重启nginx: sudo systemctl restart nginx"
    echo "查看nginx状态: sudo systemctl status nginx"
    echo ""
}

# 清理函数
cleanup() {
    echo ""
    echo -e "${YELLOW}正在停止服务...${NC}"
    
    if [ ! -z "$BACKEND_PID" ]; then
        kill $BACKEND_PID 2>/dev/null
        echo -e "${GREEN}✅ 后端服务已停止${NC}"
    fi
    
    echo -e "${YELLOW}注意: nginx服务仍在运行${NC}"
    echo "如需停止nginx，请运行: sudo systemctl stop nginx"
    echo -e "${GREEN}🎉 后端服务已停止${NC}"
    exit 0
}

# 主函数
main() {
    # 设置信号处理
    trap cleanup SIGINT SIGTERM
    
    echo -e "${GREEN}开始启动AI面试助手生产环境...${NC}"
    echo ""
    
    # 环境检查
    check_java
    check_maven
    check_node
    check_npm
    check_nginx
    setup_env
    
    echo ""
    echo -e "${BLUE}选择启动模式:${NC}"
    echo "1) 完整启动 (后端 + 前端编译 + nginx)"
    echo "2) 仅启动后端"
    echo "3) 仅编译前端并配置nginx"
    
    read -p "请选择 (1-3): " choice
    
    case $choice in
        1)
            echo -e "${GREEN}启动完整生产环境...${NC}"
            build_frontend
            setup_nginx
            start_backend
            show_info
            ;;
        2)
            echo -e "${GREEN}仅启动后端...${NC}"
            start_backend
            echo -e "${GREEN}✅ 后端服务已启动: http://localhost:8080${NC}"
            ;;
        3)
            echo -e "${GREEN}仅编译前端并配置nginx...${NC}"
            build_frontend
            setup_nginx
            echo -e "${GREEN}✅ 前端已编译并配置nginx: http://localhost:80${NC}"
            ;;
        *)
            echo -e "${RED}❌ 无效选择${NC}"
            exit 1
            ;;
    esac
    
    # 等待用户中断
    echo ""
    echo -e "${YELLOW}服务运行中，按 Ctrl+C 停止后端服务...${NC}"
    wait
}

# 运行主函数
main
