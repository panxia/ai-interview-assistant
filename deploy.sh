#!/bin/bash

echo "🚀 AI Interview Assistant 快速部署脚本"
echo "====================================="

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 检查必要环境
check_environment() {
    echo -e "${BLUE}检查环境...${NC}"
    
    # 检查Java
    if ! command -v java &> /dev/null; then
        echo -e "${RED}❌ Java未安装${NC}"
        exit 1
    fi
    
    # 检查Maven
    if ! command -v mvn &> /dev/null; then
        echo -e "${RED}❌ Maven未安装${NC}"
        exit 1
    fi
    
    # 检查Node.js
    if ! command -v node &> /dev/null; then
        echo -e "${RED}❌ Node.js未安装${NC}"
        exit 1
    fi
    
    # 检查nginx
    if ! command -v nginx &> /dev/null; then
        echo -e "${RED}❌ nginx未安装${NC}"
        exit 1
    fi
    
    echo -e "${GREEN}✅ 环境检查通过${NC}"
}

# 编译前端
build_frontend() {
    echo -e "${BLUE}编译前端...${NC}"
    cd frontend
    
    # 安装依赖
    if [ ! -d "node_modules" ]; then
        npm install
    fi
    
    # 编译
    npm run build
    
    if [ $? -ne 0 ]; then
        echo -e "${RED}❌ 前端编译失败${NC}"
        exit 1
    fi
    
    cd ..
    echo -e "${GREEN}✅ 前端编译完成${NC}"
}

# 配置nginx
setup_nginx() {
    echo -e "${BLUE}配置nginx...${NC}"
    
    # 复制前端文件
    sudo rm -rf /usr/share/nginx/html/*
    sudo cp -r frontend/dist/* /usr/share/nginx/html/
    
    # 创建nginx配置
    sudo tee /etc/nginx/sites-available/ai-interview-assistant > /dev/null << 'EOF'
server {
    listen 80;
    server_name _;
    
    root /usr/share/nginx/html;
    index index.html;
    
    location / {
        try_files $uri $uri/ /index.html;
    }
    
    location /api/ {
        proxy_pass http://localhost:8080/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
EOF

    # 启用配置
    sudo ln -sf /etc/nginx/sites-available/ai-interview-assistant /etc/nginx/sites-enabled/
    
    # 测试并重启nginx
    sudo nginx -t && sudo systemctl restart nginx
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ nginx配置完成${NC}"
    else
        echo -e "${RED}❌ nginx配置失败${NC}"
        exit 1
    fi
}

# 启动后端
start_backend() {
    echo -e "${BLUE}启动后端...${NC}"
    cd backend
    
    # 编译
    mvn clean compile -q
    
    # 启动
    mvn spring-boot:run &
    BACKEND_PID=$!
    
    # 等待启动
    sleep 15
    
    # 检查健康状态
    if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
        echo -e "${GREEN}✅ 后端启动成功${NC}"
    else
        echo -e "${YELLOW}⚠️  后端可能还在启动中${NC}"
    fi
    
    cd ..
}

# 清理函数
cleanup() {
    echo -e "${YELLOW}停止后端服务...${NC}"
    if [ ! -z "$BACKEND_PID" ]; then
        kill $BACKEND_PID 2>/dev/null
    fi
    echo -e "${GREEN}✅ 部署完成${NC}"
    exit 0
}

# 主函数
main() {
    trap cleanup SIGINT SIGTERM
    
    check_environment
    build_frontend
    setup_nginx
    start_backend
    
    echo ""
    echo -e "${GREEN}🎉 部署完成！${NC}"
    echo "=================================="
    echo -e "${BLUE}访问地址:${NC} http://localhost:80"
    echo -e "${BLUE}API地址:${NC} http://localhost:8080"
    echo ""
    echo -e "${YELLOW}按 Ctrl+C 停止后端服务${NC}"
    echo -e "${YELLOW}nginx服务会继续运行${NC}"
    
    wait
}

main
