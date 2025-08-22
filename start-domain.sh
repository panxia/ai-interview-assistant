#!/bin/bash

echo "🏞️ 宠物公园 域名版启动脚本"
echo "================================="

# 颜色定义
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m'

# 默认配置
DOMAIN=${DOMAIN:-"pet.bwmiao.com"}
USE_HTTPS=${USE_HTTPS:-"false"}

# 显示配置信息
show_config() {
    echo -e "${BLUE}📋 当前配置:${NC}"
    echo -e "  🌐 域名: ${GREEN}$DOMAIN${NC}"
    echo -e "  🔒 HTTPS: ${GREEN}$USE_HTTPS${NC}"
    echo ""
}

# 设置环境变量
setup_env() {
    echo -e "${BLUE}🔧 设置环境变量...${NC}"
    
    if [ "$USE_HTTPS" = "true" ]; then
        export ALLOWED_ORIGINS="https://$DOMAIN,http://$DOMAIN"
        export VITE_API_BASE="https://$DOMAIN"
    else
        export ALLOWED_ORIGINS="http://$DOMAIN,http://localhost:80"
        export VITE_API_BASE="http://$DOMAIN"
    fi
    
    echo -e "${GREEN}✅ 环境变量设置完成${NC}"
}

# 构建前端
build_frontend() {
    echo -e "${BLUE}🎨 构建前端...${NC}"
    cd frontend
    
    # 安装依赖
    npm install
    
    # 设置环境变量
    if [ "$USE_HTTPS" = "true" ]; then
        echo "VITE_API_BASE=https://$DOMAIN" > .env
    else
        echo "VITE_API_BASE=http://$DOMAIN" > .env
    fi
    
    # 构建
    npm run build
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ 前端构建成功！${NC}"
    else
        echo -e "${RED}❌ 前端构建失败${NC}"
        exit 1
    fi
    
    cd ..
}

# 配置nginx
setup_nginx() {
    echo -e "${BLUE}🌐 配置nginx...${NC}"
    
    # 选择配置文件
    if [ "$USE_HTTPS" = "true" ]; then
        sudo cp deploy/nginx-https.conf /etc/nginx/nginx.conf
    else
        sudo cp deploy/nginx-main.conf /etc/nginx/nginx.conf
    fi
    
    # 复制MIME类型文件
    sudo cp deploy/mime.types /etc/nginx/
    
    # 复制前端文件
    sudo cp -r frontend/dist/* /usr/share/nginx/html/
    
    echo -e "${GREEN}✅ nginx配置完成${NC}"
}

# 启动nginx
start_nginx() {
    echo -e "${BLUE}🚀 启动nginx...${NC}"
    
    # 测试配置
    sudo nginx -t
    
    if [ $? -eq 0 ]; then
        sudo systemctl restart nginx
        echo -e "${GREEN}✅ nginx启动成功！${NC}"
    else
        echo -e "${RED}❌ nginx配置错误${NC}"
        exit 1
    fi
}

# 启动后端
start_backend() {
    echo -e "${BLUE}🚀 启动后端...${NC}"
    cd backend
    
    # 编译
    mvn clean compile -q
    
    # 启动
    mvn spring-boot:run > ../backend.log 2>&1 &
    BACKEND_PID=$!
    
    # 等待启动
    echo -e "${YELLOW}⏳ 等待后端启动...${NC}"
    sleep 15
    
    cd ..
    echo -e "${GREEN}✅ 后端启动成功！${NC}"
}

# 显示信息
show_info() {
    echo ""
    echo -e "${GREEN}🎉 宠物公园域名版启动完成！${NC}"
    echo "======================================"
    
    if [ "$USE_HTTPS" = "true" ]; then
        echo -e "${BLUE}🌐 游戏地址:${NC} https://$DOMAIN"
    else
        echo -e "${BLUE}🌐 游戏地址:${NC} http://$DOMAIN"
    fi
    
    echo -e "${BLUE}🔧 后端API:${NC} http://localhost:8080"
    echo ""
    echo -e "${YELLOW}使用说明:${NC}"
    echo "1. 在浏览器中访问您的域名"
    echo "2. 开始您的宠物养成之旅"
    echo "3. 按 Ctrl+C 停止服务"
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
    
    echo -e "${GREEN}🎉 所有服务已停止${NC}"
    exit 0
}

# 主函数
main() {
    trap cleanup SIGINT SIGTERM
    
    # 配置域名
    read -p "请输入域名 (默认: $DOMAIN): " input_domain
    if [ ! -z "$input_domain" ]; then
        DOMAIN=$input_domain
    fi
    
    read -p "是否使用HTTPS? (y/N): " use_https
    if [[ $use_https =~ ^[Yy]$ ]]; then
        USE_HTTPS="true"
    fi
    
    show_config
    setup_env
    
    echo -e "${BLUE}开始启动服务...${NC}"
    
    # 构建前端
    build_frontend
    
    # 配置nginx
    setup_nginx
    
    # 启动nginx
    start_nginx
    
    # 启动后端
    start_backend
    
    # 显示信息
    show_info
    
    # 等待
    echo -e "${YELLOW}服务运行中，按 Ctrl+C 停止...${NC}"
    wait
}

main
