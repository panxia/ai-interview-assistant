#!/bin/bash

echo "🏞️ 宠物公园 域名版启动脚本"
echo "================================="

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# 默认配置
DOMAIN=${DOMAIN:-"pet.bwmiao.com"}
BACKEND_PORT=${BACKEND_PORT:-"8080"}
FRONTEND_PORT=${FRONTEND_PORT:-"80"}
USE_HTTPS=${USE_HTTPS:-"false"}

# 显示配置信息
show_config() {
    echo -e "${CYAN}📋 当前配置:${NC}"
    echo -e "  🌐 域名: ${GREEN}$DOMAIN${NC}"
    echo -e "  🔧 后端端口: ${GREEN}$BACKEND_PORT${NC}"
    echo -e "  🎨 前端端口: ${GREEN}$FRONTEND_PORT${NC}"
    echo -e "  🔒 HTTPS: ${GREEN}$USE_HTTPS${NC}"
    echo ""
}

# 检查Java
check_java() {
    echo -e "${BLUE}🔍 检查Java环境...${NC}"
    if ! command -v java &> /dev/null; then
        echo -e "${RED}❌ Java未安装，请先安装Java 17或更高版本${NC}"
        exit 1
    fi
    
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
    if [ "$JAVA_VERSION" -lt "17" ]; then
        echo -e "${RED}❌ Java版本过低，需要Java 17或更高版本${NC}"
        exit 1
    fi
    
    echo -e "${GREEN}✅ Java环境正常${NC}"
}

# 检查Maven
check_maven() {
    echo -e "${BLUE}🔍 检查Maven环境...${NC}"
    if ! command -v mvn &> /dev/null; then
        echo -e "${RED}❌ Maven未安装，请先安装Maven${NC}"
        exit 1
    fi
    
    echo -e "${GREEN}✅ Maven环境正常${NC}"
}

# 检查Node.js
check_node() {
    echo -e "${BLUE}🔍 检查Node.js环境...${NC}"
    if ! command -v node &> /dev/null; then
        echo -e "${RED}❌ Node.js未安装，请先安装Node.js 18+${NC}"
        exit 1
    fi
    
    echo -e "${GREEN}✅ Node.js环境正常${NC}"
}

# 检查nginx
check_nginx() {
    echo -e "${BLUE}🔍 检查nginx环境...${NC}"
    if ! command -v nginx &> /dev/null; then
        echo -e "${RED}❌ nginx未安装，请先安装nginx${NC}"
        return 1
    fi
    
    echo -e "${GREEN}✅ nginx环境正常${NC}"
    return 0
}

# 设置环境变量
setup_env() {
    echo -e "${PURPLE}🔧 设置环境变量...${NC}"
    
    # 设置允许的来源，包含域名
    if [ "$USE_HTTPS" = "true" ]; then
        export ALLOWED_ORIGINS=${ALLOWED_ORIGINS:-"https://$DOMAIN,http://$DOMAIN"}
        export VITE_API_BASE=${VITE_API_BASE:-"https://$DOMAIN"}
    else
        export ALLOWED_ORIGINS=${ALLOWED_ORIGINS:-"http://$DOMAIN,http://localhost:$FRONTEND_PORT"}
        export VITE_API_BASE=${VITE_API_BASE:-"http://$DOMAIN"}
    fi
    
    export SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE:-"production"}
    
    echo -e "${GREEN}✅ 环境变量设置完成${NC}"
    echo -e "${CYAN}   - 允许的来源: $ALLOWED_ORIGINS${NC}"
    echo -e "${CYAN}   - API基础URL: $VITE_API_BASE${NC}"
    echo -e "${CYAN}   - Spring配置: $SPRING_PROFILES_ACTIVE${NC}"
}

# 启动后端
start_backend() {
    echo -e "${PURPLE}🚀 启动宠物游戏后端服务...${NC}"
    cd backend
    
    # 清理并编译
    echo -e "${BLUE}📦 清理并编译项目...${NC}"
    mvn clean compile -q
    
    if [ $? -ne 0 ]; then
        echo -e "${RED}❌ 后端编译失败${NC}"
        cd ..
        exit 1
    fi
    
    # 启动Spring Boot
    echo -e "${BLUE}🎮 启动Spring Boot宠物游戏应用...${NC}"
    mvn spring-boot:run > ../backend.log 2>&1 &
    BACKEND_PID=$!
    
    # 等待后端启动
    echo -e "${YELLOW}⏳ 等待后端服务启动...${NC}"
    
    for i in {1..30}; do
        if curl -s http://localhost:$BACKEND_PORT/api/pet/types > /dev/null 2>&1; then
            echo -e "${GREEN}✅ 宠物游戏后端服务启动成功！${NC}"
            break
        fi
        
        if [ $i -eq 30 ]; then
            echo -e "${RED}❌ 后端服务启动超时${NC}"
            kill $BACKEND_PID 2>/dev/null
            cd ..
            exit 1
        fi
        
        echo -n "."
        sleep 2
    done
    
    cd ..
}

# 构建前端
build_frontend() {
    echo -e "${PURPLE}🎨 构建宠物游戏前端...${NC}"
    cd frontend
    
    # 安装依赖
    if [ ! -d "node_modules" ]; then
        echo -e "${BLUE}📦 安装前端依赖...${NC}"
        npm install
    fi
    
    # 设置环境变量
    if [ "$USE_HTTPS" = "true" ]; then
        echo -e "${BLUE}🔒 使用HTTPS配置...${NC}"
        cp env.production .env
        echo "VITE_API_BASE=https://$DOMAIN" >> .env
    else
        echo -e "${BLUE}🌐 使用HTTP配置...${NC}"
        cp env.production .env
        echo "VITE_API_BASE=http://$DOMAIN" >> .env
    fi
    
    # 构建前端
    echo -e "${BLUE}🏗️ 构建Vue宠物游戏界面...${NC}"
    npm run build
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ 前端构建成功！${NC}"
    else
        echo -e "${RED}❌ 前端构建失败${NC}"
        cd ..
        exit 1
    fi
    
    cd ..
}

# 配置nginx
setup_nginx() {
    echo -e "${PURPLE}🌐 配置nginx服务...${NC}"
    
    # 选择nginx配置文件
    if [ "$USE_HTTPS" = "true" ]; then
        echo -e "${BLUE}🔒 使用HTTPS配置...${NC}"
        NGINX_CONFIG="deploy/nginx-https.conf"
    else
        echo -e "${BLUE}🌐 使用HTTP配置...${NC}"
        NGINX_CONFIG="deploy/nginx-main.conf"
    fi
    
    # 检查配置文件是否存在
    if [ ! -f "$NGINX_CONFIG" ]; then
        echo -e "${RED}❌ nginx配置文件不存在: $NGINX_CONFIG${NC}"
        exit 1
    fi
    
    # 复制配置文件到系统nginx目录
    echo -e "${BLUE}📋 复制nginx配置...${NC}"
    sudo cp "$NGINX_CONFIG" /etc/nginx/nginx.conf
    
    # 复制MIME类型文件
    sudo cp deploy/mime.types /etc/nginx/
    
    # 复制前端构建文件
    echo -e "${BLUE}📂 复制前端文件...${NC}"
    sudo cp -r frontend/dist/* /usr/share/nginx/html/
    
    echo -e "${GREEN}✅ nginx配置文件设置完成${NC}"
}

# 启动nginx
start_nginx() {
    echo -e "${PURPLE}🚀 启动nginx服务...${NC}"
    
    # 测试nginx配置
    echo -e "${BLUE}🔧 测试nginx配置...${NC}"
    sudo nginx -t
    
    if [ $? -ne 0 ]; then
        echo -e "${RED}❌ nginx配置测试失败${NC}"
        exit 1
    fi
    
    # 启动nginx
    echo -e "${BLUE}🌐 启动nginx服务器...${NC}"
    sudo systemctl restart nginx
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ nginx服务启动成功！${NC}"
        
        # 等待nginx启动
        sleep 3
        
        # 检查nginx是否启动成功
        if curl -s http://localhost:$FRONTEND_PORT > /dev/null 2>&1; then
            echo -e "${GREEN}✅ 宠物游戏前端界面可访问！${NC}"
        else
            echo -e "${YELLOW}⚠️  前端界面可能还在启动中...${NC}"
        fi
    else
        echo -e "${RED}❌ nginx服务启动失败${NC}"
        exit 1
    fi
}

# 显示服务信息
show_info() {
    echo ""
    echo -e "${GREEN}🎉 宠物公园域名版启动完成！${NC}"
    echo "======================================"
    
    if [ "$USE_HTTPS" = "true" ]; then
        echo -e "${CYAN}🌐 游戏界面:${NC} https://$DOMAIN"
        echo -e "${CYAN}🔧 后端API:${NC} http://localhost:$BACKEND_PORT"
    else
        echo -e "${CYAN}🌐 游戏界面:${NC} http://$DOMAIN"
        echo -e "${CYAN}🔧 后端API:${NC} http://localhost:$BACKEND_PORT"
    fi
    
    echo ""
    echo -e "${PURPLE}🐾 游戏特色:${NC}"
    echo "  🐱 7种可爱宠物: 猫咪、小狗、兔子、仓鼠、小龙、熊猫、企鹅"
    echo "  💖 5项状态管理: 饥饿、清洁、快乐、能量、健康"
    echo "  🎮 4种小游戏: 记忆、反应、猜谜、拍拍游戏"
    echo "  🏆 18个成就系统: 完成任务解锁奖励"
    echo "  🛍️ 物品收集: 食物、玩具、药品、装饰品"
    echo ""
    echo -e "${YELLOW}🎯 使用说明:${NC}"
    echo "  1. 在浏览器中访问您的域名"
    echo "  2. 创建您的专属宠物并开始养成之旅"
    echo "  3. 通过互动动作照顾宠物的各项需求"
    echo "  4. 玩小游戏获得金币和经验值"
    echo "  5. 解锁成就获得特殊奖励"
    echo "  6. 按 Ctrl+C 停止所有服务"
    echo ""
    echo -e "${BLUE}📊 日志查看:${NC}"
    echo "  后端日志: tail -f backend.log"
    echo "  nginx访问日志: sudo tail -f /var/log/nginx/access.log"
    echo "  nginx错误日志: sudo tail -f /var/log/nginx/error.log"
    echo ""
    echo -e "${PURPLE}🔧 nginx管理:${NC}"
    echo "  重载配置: sudo nginx -s reload"
    echo "  停止服务: sudo systemctl stop nginx"
    echo "  启动服务: sudo systemctl start nginx"
    echo ""
}

# 清理函数
cleanup() {
    echo ""
    echo -e "${YELLOW}🛑 正在停止宠物公园服务...${NC}"
    
    # 停止后端服务
    if [ ! -z "$BACKEND_PID" ]; then
        kill $BACKEND_PID 2>/dev/null
        echo -e "${GREEN}✅ 后端服务已停止${NC}"
    fi
    
    echo -e "${PURPLE}👋 感谢游玩宠物公园！${NC}"
    echo -e "${GREEN}🎉 所有服务已停止${NC}"
    exit 0
}

# 显示欢迎信息
show_welcome() {
    echo -e "${PURPLE}"
    cat << 'EOF'
    🏞️ 欢迎来到宠物公园！ 🏞️
    
       🐱    🐶    🐰    🐹
         \    |    /    /
          \   |   /    /
           \  |  /    /
            \ | /    /
             \|/    /
              🏠   🎮
    
    在这里，您可以：
    ✨ 养育可爱的虚拟宠物
    🎯 完成有趣的互动任务  
    🏆 解锁丰富的成就奖励
    💎 收集各种游戏物品
EOF
    echo -e "${NC}"
    echo ""
}

# 配置域名
configure_domain() {
    echo -e "${PURPLE}🌐 配置域名访问...${NC}"
    
    read -p "请输入域名 (默认: $DOMAIN): " input_domain
    if [ ! -z "$input_domain" ]; then
        DOMAIN=$input_domain
    fi
    
    read -p "是否使用HTTPS? (y/N): " use_https
    if [[ $use_https =~ ^[Yy]$ ]]; then
        USE_HTTPS="true"
        echo -e "${GREEN}✅ 已启用HTTPS${NC}"
    else
        USE_HTTPS="false"
        echo -e "${BLUE}✅ 使用HTTP${NC}"
    fi
    
    echo ""
}

# 主函数
main() {
    # 设置信号处理
    trap cleanup SIGINT SIGTERM
    
    show_welcome
    
    # 配置域名
    configure_domain
    
    show_config
    
    echo -e "${BLUE}🔧 开始环境检查...${NC}"
    echo ""
    
    # 环境检查
    check_java
    check_maven
    check_node
    
    # 检查nginx
    NGINX_AVAILABLE=false
    if check_nginx; then
        NGINX_AVAILABLE=true
    else
        echo -e "${RED}❌ nginx未安装，无法启动域名版本${NC}"
        echo -e "${CYAN}请先安装nginx: sudo apt-get install nginx${NC}"
        exit 1
    fi
    
    setup_env
    
    echo ""
    echo -e "${PURPLE}🎮 启动宠物公园域名版...${NC}"
    echo ""
    
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
    
    # 等待用户中断
    echo ""
    echo -e "${YELLOW}🎮 宠物公园运行中，按 Ctrl+C 停止所有服务...${NC}"
    echo -e "${CYAN}💡 提示: 您可以通过域名 $DOMAIN 访问游戏${NC}"
    wait
}

# 运行主函数
main "$@"
