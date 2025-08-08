#!/bin/bash

echo "🏞️ 宠物公园 启动脚本"
echo "================================="

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# 检查Java
check_java() {
    echo -e "${BLUE}🔍 检查Java环境...${NC}"
    if ! command -v java &> /dev/null; then
        echo -e "${RED}❌ Java未安装，请先安装Java 17或更高版本${NC}"
        echo -e "${YELLOW}💡 安装建议：${NC}"
        echo "  macOS: brew install openjdk@17"
        echo "  Ubuntu: sudo apt install openjdk-17-jdk"
        echo "  Windows: 下载并安装Oracle JDK 17"
        exit 1
    fi
    
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
    if [ "$JAVA_VERSION" -lt "17" ]; then
        echo -e "${RED}❌ Java版本过低，需要Java 17或更高版本${NC}"
        echo -e "${YELLOW}当前版本: $(java -version 2>&1 | head -n 1)${NC}"
        exit 1
    fi
    
    echo -e "${GREEN}✅ Java环境正常 - $(java -version 2>&1 | head -n 1)${NC}"
}

# 检查Maven
check_maven() {
    echo -e "${BLUE}🔍 检查Maven环境...${NC}"
    if ! command -v mvn &> /dev/null; then
        echo -e "${RED}❌ Maven未安装，请先安装Maven${NC}"
        echo -e "${YELLOW}💡 安装建议：${NC}"
        echo "  macOS: brew install maven"
        echo "  Ubuntu: sudo apt install maven"
        echo "  Windows: 下载并安装Apache Maven"
        exit 1
    fi
    
    echo -e "${GREEN}✅ Maven环境正常 - $(mvn --version | head -n 1)${NC}"
}

# 检查Node.js
check_node() {
    echo -e "${BLUE}🔍 检查Node.js环境...${NC}"
    if ! command -v node &> /dev/null; then
        echo -e "${RED}❌ Node.js未安装，请先安装Node.js 20+${NC}"
        echo -e "${YELLOW}💡 安装建议：${NC}"
        echo "  macOS: brew install node"
        echo "  Ubuntu: curl -fsSL https://deb.nodesource.com/setup_20.x | sudo -E bash - && sudo apt-get install -y nodejs"
        echo "  Windows: 下载并安装Node.js LTS版本"
        exit 1
    fi
    
    NODE_VERSION=$(node --version | cut -d'v' -f2 | cut -d'.' -f1)
    if [ "$NODE_VERSION" -lt "18" ]; then
        echo -e "${RED}❌ Node.js版本过低，需要Node.js 18或更高版本${NC}"
        echo -e "${YELLOW}当前版本: $(node --version)${NC}"
        exit 1
    fi
    
    echo -e "${GREEN}✅ Node.js环境正常 - $(node --version)${NC}"
}

# 检查npm
check_npm() {
    echo -e "${BLUE}🔍 检查npm环境...${NC}"
    if ! command -v npm &> /dev/null; then
        echo -e "${RED}❌ npm未安装${NC}"
        exit 1
    fi
    
    echo -e "${GREEN}✅ npm环境正常 - v$(npm --version)${NC}"
}

# 检查nginx
check_nginx() {
    echo -e "${BLUE}🔍 检查nginx环境...${NC}"
    if ! command -v nginx &> /dev/null; then
        echo -e "${RED}❌ nginx未安装，请先安装nginx${NC}"
        echo -e "${YELLOW}💡 安装建议：${NC}"
        echo "  macOS: brew install nginx"
        echo "  Ubuntu: sudo apt-get install nginx"
        echo "  CentOS: sudo yum install nginx"
        return 1
    fi
    
    echo -e "${GREEN}✅ nginx环境正常 - $(nginx -v 2>&1)${NC}"
    return 0
}

# 设置环境变量
setup_env() {
    echo -e "${PURPLE}🔧 设置环境变量...${NC}"
    
    # 由于是宠物养成游戏，不再需要API密钥
    export ALLOWED_ORIGINS=${ALLOWED_ORIGINS:-"http://localhost:5173,http://localhost:3000,http://localhost:80"}
    export SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE:-"dev"}
    
    echo -e "${GREEN}✅ 环境变量设置完成${NC}"
    echo -e "${CYAN}   - 允许的来源: $ALLOWED_ORIGINS${NC}"
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
    
    # 更智能的健康检查
    for i in {1..30}; do
        if curl -s http://localhost:8080/api/pet/types > /dev/null 2>&1; then
            echo -e "${GREEN}✅ 宠物游戏后端服务启动成功！${NC}"
            break
        fi
        
        if [ $i -eq 30 ]; then
            echo -e "${RED}❌ 后端服务启动超时${NC}"
            echo -e "${YELLOW}请检查 backend.log 文件获取详细错误信息${NC}"
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
    
    # 检查node_modules是否存在
    if [ ! -d "node_modules" ]; then
        echo -e "${BLUE}📦 安装前端依赖...${NC}"
        npm install
        
        if [ $? -ne 0 ]; then
            echo -e "${RED}❌ 前端依赖安装失败${NC}"
            cd ..
            exit 1
        fi
    fi
    
    # 清理之前的构建
    echo -e "${BLUE}🧹 清理之前的构建文件...${NC}"
    rm -rf dist
    
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
    
    # 创建nginx配置目录
    mkdir -p nginx/conf
    mkdir -p nginx/html
    mkdir -p nginx/logs
    
    # 复制前端构建文件
    echo -e "${BLUE}📂 复制前端文件...${NC}"
    cp -r frontend/dist/* nginx/html/
    
    # 创建mime.types文件
    cat > nginx/conf/mime.types << 'EOF'
types {
    text/html                                        html htm shtml;
    text/css                                         css;
    text/xml                                         xml;
    image/gif                                        gif;
    image/jpeg                                       jpeg jpg;
    image/png                                        png;
    image/svg+xml                                    svg svgz;
    image/webp                                       webp;
    image/x-icon                                     ico;
    application/javascript                           js;
    application/json                                 json;
    application/font-woff                            woff;
    application/font-woff2                           woff2;
    font/ttf                                         ttf;
    font/otf                                         otf;
    application/octet-stream                         bin exe dll;
}
EOF

    # 创建nginx配置文件
    cat > nginx/conf/nginx.conf << 'EOF'
worker_processes 1;
error_log logs/error.log;
pid logs/nginx.pid;

events {
    worker_connections 1024;
}

http {
    include       mime.types;
    default_type  application/octet-stream;
    
    log_format main '$remote_addr - $remote_user [$time_local] "$request" '
                    '$status $body_bytes_sent "$http_referer" '
                    '"$http_user_agent" "$http_x_forwarded_for"';
    
    access_log logs/access.log main;
    sendfile on;
    keepalive_timeout 65;
    gzip on;
    gzip_types text/plain application/javascript text/css application/json image/svg+xml;

    server {
        listen       80;
        server_name  localhost;
        
        root html;
        index index.html;
        
        # 静态文件缓存
        location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
            expires 1y;
            add_header Cache-Control "public, immutable";
        }
        
        # Vue.js单页应用路由
        location / {
            try_files $uri $uri/ /index.html;
        }
        
        # API代理到后端
        location /api/ {
            proxy_pass http://127.0.0.1:8080;
            proxy_http_version 1.1;
            proxy_set_header Upgrade $http_upgrade;
            proxy_set_header Connection 'upgrade';
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-Proto $scheme;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_cache_bypass $http_upgrade;
        }
        
        # 健康检查
        location /health {
            access_log off;
            return 200 "healthy\n";
            add_header Content-Type text/plain;
        }
    }
}
EOF
    
    echo -e "${GREEN}✅ nginx配置文件创建完成${NC}"
}

# 启动nginx
start_nginx() {
    echo -e "${PURPLE}🚀 启动nginx服务...${NC}"
    
    # 测试nginx配置
    echo -e "${BLUE}🔧 测试nginx配置...${NC}"
    nginx -t -c "$(pwd)/nginx/conf/nginx.conf" -p "$(pwd)/nginx/"
    
    if [ $? -ne 0 ]; then
        echo -e "${RED}❌ nginx配置测试失败${NC}"
        exit 1
    fi
    
    # 启动nginx
    echo -e "${BLUE}🌐 启动nginx服务器...${NC}"
    nginx -c "$(pwd)/nginx/conf/nginx.conf" -p "$(pwd)/nginx/"
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ nginx服务启动成功！${NC}"
        
        # 等待nginx启动
        sleep 3
        
        # 检查nginx是否启动成功
        if curl -s http://localhost:80/health > /dev/null 2>&1; then
            echo -e "${GREEN}✅ 宠物游戏前端界面可访问！${NC}"
        else
            echo -e "${YELLOW}⚠️  前端界面可能还在启动中...${NC}"
        fi
    else
        echo -e "${RED}❌ nginx服务启动失败${NC}"
        exit 1
    fi
}

# 停止nginx
stop_nginx() {
    echo -e "${YELLOW}🛑 停止nginx服务...${NC}"
    
    # 查找nginx进程并停止
    if [ -f "nginx/logs/nginx.pid" ]; then
        nginx -s quit -c "$(pwd)/nginx/conf/nginx.conf" -p "$(pwd)/nginx/" 2>/dev/null
        rm -f nginx/logs/nginx.pid 2>/dev/null
    fi
    
    # 强制停止nginx进程
    pkill -f "nginx.*$(pwd)" 2>/dev/null
    
    echo -e "${GREEN}✅ nginx服务已停止${NC}"
}

# 显示服务信息
show_info() {
    echo ""
    echo -e "${GREEN}🎉 宠物公园启动完成！${NC}"
    echo "======================================"
    echo -e "${CYAN}🌐 游戏界面:${NC} http://localhost:80"
    echo -e "${CYAN}🔧 后端API:${NC} http://localhost:8080"
    echo -e "${CYAN}🔍 健康检查:${NC} http://localhost:80/health"
    echo ""
    echo -e "${PURPLE}🐾 游戏特色:${NC}"
    echo "  🐱 7种可爱宠物: 猫咪、小狗、兔子、仓鼠、小龙、熊猫、企鹅"
    echo "  💖 5项状态管理: 饥饿、清洁、快乐、能量、健康"
    echo "  🎮 4种小游戏: 记忆、反应、猜谜、拍拍游戏"
    echo "  🏆 18个成就系统: 完成任务解锁奖励"
    echo "  🛍️ 物品收集: 食物、玩具、药品、装饰品"
    echo ""
    echo -e "${YELLOW}🎯 使用说明:${NC}"
    echo "  1. 在浏览器中访问 http://localhost:80"
    echo "  2. 创建您的专属宠物并开始养成之旅"
    echo "  3. 通过互动动作照顾宠物的各项需求"
    echo "  4. 玩小游戏获得金币和经验值"
    echo "  5. 解锁成就获得特殊奖励"
    echo "  6. 按 Ctrl+C 停止所有服务"
    echo ""
    echo -e "${BLUE}📊 日志查看:${NC}"
    echo "  后端日志: tail -f backend.log"
    echo "  nginx访问日志: tail -f nginx/logs/access.log"
    echo "  nginx错误日志: tail -f nginx/logs/error.log"
    echo ""
    echo -e "${PURPLE}🔧 nginx管理:${NC}"
    echo "  重载配置: nginx -s reload -c $(pwd)/nginx/conf/nginx.conf -p $(pwd)/nginx/"
    echo "  停止服务: nginx -s quit -c $(pwd)/nginx/conf/nginx.conf -p $(pwd)/nginx/"
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
    
    # 停止nginx服务
    stop_nginx
    
    # 清理可能残留的进程
    pkill -f "spring-boot:run" 2>/dev/null
    pkill -f "nginx.*$(pwd)" 2>/dev/null
    
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

# 清理之前的服务
cleanup_previous_services() {
    echo -e "${BLUE}🔧 清理之前的服务...${NC}"
    
    # 停止之前的后端服务
    echo -e "${YELLOW}🛑 停止之前的后端服务...${NC}"
    pkill -f "spring-boot:run" 2>/dev/null
    pkill -f "aiinterviewassistant" 2>/dev/null
    pkill -f "pet-park" 2>/dev/null
    
    # 停止之前的nginx服务
    echo -e "${YELLOW}🛑 停止之前的nginx服务...${NC}"
    if [ -f "nginx/logs/nginx.pid" ]; then
        nginx -s quit -c "$(pwd)/nginx/conf/nginx.conf" -p "$(pwd)/nginx/" 2>/dev/null
        rm -f nginx/logs/nginx.pid 2>/dev/null
    fi
    pkill -f "nginx.*$(pwd)" 2>/dev/null
    
    # 等待进程完全停止
    sleep 2
    
    echo -e "${GREEN}✅ 之前的服务已清理${NC}"
}

# 检查端口占用
check_ports() {
    echo -e "${BLUE}🔍 检查端口占用...${NC}"
    
    # 检查8080端口
    if lsof -Pi :8080 -sTCP:LISTEN -t >/dev/null 2>&1; then
        echo -e "${YELLOW}⚠️  端口8080已被占用，将尝试清理...${NC}"
        cleanup_previous_services
    fi
    
    # 检查80端口
    if lsof -Pi :80 -sTCP:LISTEN -t >/dev/null 2>&1; then
        echo -e "${YELLOW}⚠️  端口80已被占用，将尝试清理...${NC}"
        cleanup_previous_services
    fi
    
    # 再次检查端口是否仍被占用
    if lsof -Pi :8080 -sTCP:LISTEN -t >/dev/null 2>&1; then
        echo -e "${RED}❌ 端口8080仍被其他进程占用，可能需要手动停止${NC}"
        echo -e "${CYAN}提示: sudo lsof -i :8080 查看占用进程${NC}"
    fi
    
    if lsof -Pi :80 -sTCP:LISTEN -t >/dev/null 2>&1; then
        echo -e "${RED}❌ 端口80仍被其他进程占用，可能需要手动停止${NC}"
        echo -e "${CYAN}提示: sudo lsof -i :80 查看占用进程${NC}"
    fi
}

# 主函数
main() {
    # 设置信号处理
    trap cleanup SIGINT SIGTERM
    
    show_welcome
    
    echo -e "${BLUE}🔧 开始环境检查和清理...${NC}"
    echo ""
    
    # 首先清理之前的服务
    cleanup_previous_services
    
    # 环境检查
    check_java
    check_maven
    check_node
    check_npm
    
    # 检查nginx（可选）
    NGINX_AVAILABLE=false
    if check_nginx; then
        NGINX_AVAILABLE=true
    else
        echo -e "${YELLOW}⚠️  nginx未安装，将无法使用nginx启动模式${NC}"
        echo -e "${CYAN}建议安装nginx以获得更好的生产环境体验${NC}"
    fi
    
    check_ports
    setup_env
    
    echo ""
    echo -e "${PURPLE}🎮 选择启动模式:${NC}"
    if [ "$NGINX_AVAILABLE" = true ]; then
        echo "  1) 🌟 生产模式 (后端 + nginx前端) - 推荐"
        echo "  2) 🔧 仅启动后端服务"
        echo "  3) 🎨 仅构建前端 + nginx"
        echo "  4) 📊 查看运行状态"
    else
        echo "  1) 🔧 仅启动后端服务"
        echo "  2) 📊 查看运行状态"
        echo ""
        echo -e "${YELLOW}💡 提示: 安装nginx后可使用完整的前端部署功能${NC}"
    fi
    echo ""
    
    if [ "$NGINX_AVAILABLE" = true ]; then
        read -p "请选择启动模式 (1-4): " choice
    else
        read -p "请选择启动模式 (1-2): " choice
    fi
    
    case $choice in
        1)
            if [ "$NGINX_AVAILABLE" = true ]; then
                echo -e "${GREEN}🌟 启动完整宠物公园服务 (生产模式)...${NC}"
                echo ""
                build_frontend
                setup_nginx
                start_nginx
                start_backend
                show_info
                
                # 自动打开浏览器（macOS）
                if [[ "$OSTYPE" == "darwin"* ]]; then
                    echo -e "${CYAN}🌐 正在为您打开游戏界面...${NC}"
                    sleep 2
                    open http://localhost:80 2>/dev/null
                fi
            else
                echo -e "${BLUE}🔧 仅启动后端服务...${NC}"
                start_backend
                echo -e "${GREEN}✅ 后端服务已启动: http://localhost:8080${NC}"
                echo -e "${YELLOW}💡 安装nginx后可启动完整前端服务${NC}"
            fi
            ;;
        2)
            if [ "$NGINX_AVAILABLE" = true ]; then
                echo -e "${BLUE}🔧 仅启动后端服务...${NC}"
                start_backend
                echo -e "${GREEN}✅ 后端服务已启动: http://localhost:8080${NC}"
            else
                echo -e "${CYAN}📊 检查服务状态...${NC}"
                echo ""
                if curl -s http://localhost:8080/api/pet/types > /dev/null 2>&1; then
                    echo -e "${GREEN}✅ 后端服务运行正常${NC}"
                else
                    echo -e "${RED}❌ 后端服务未运行${NC}"
                fi
                
                if curl -s http://localhost:80/health > /dev/null 2>&1; then
                    echo -e "${GREEN}✅ nginx前端服务运行正常${NC}"
                else
                    echo -e "${RED}❌ nginx前端服务未运行${NC}"
                fi
                
                echo ""
                echo -e "${YELLOW}如需启动服务，请重新运行此脚本${NC}"
                exit 0
            fi
            ;;
        3)
            if [ "$NGINX_AVAILABLE" = true ]; then
                echo -e "${PURPLE}🎨 构建前端并启动nginx...${NC}"
                build_frontend
                setup_nginx
                start_nginx
                echo -e "${GREEN}✅ 前端界面已启动: http://localhost:80${NC}"
                echo -e "${YELLOW}💡 记得启动后端服务以获得完整功能${NC}"
            else
                echo -e "${RED}❌ 无效选择${NC}"
                exit 1
            fi
            ;;
        4)
            if [ "$NGINX_AVAILABLE" = true ]; then
                echo -e "${CYAN}📊 检查服务状态...${NC}"
                echo ""
                if curl -s http://localhost:8080/api/pet/types > /dev/null 2>&1; then
                    echo -e "${GREEN}✅ 后端服务运行正常${NC}"
                else
                    echo -e "${RED}❌ 后端服务未运行${NC}"
                fi
                
                if curl -s http://localhost:80/health > /dev/null 2>&1; then
                    echo -e "${GREEN}✅ nginx前端服务运行正常${NC}"
                else
                    echo -e "${RED}❌ nginx前端服务未运行${NC}"
                fi
                
                echo ""
                echo -e "${YELLOW}如需启动服务，请重新运行此脚本${NC}"
                exit 0
            else
                echo -e "${RED}❌ 无效选择${NC}"
                exit 1
            fi
            ;;
        *)
            echo -e "${RED}❌ 无效选择${NC}"
            exit 1
            ;;
    esac
    
    # 等待用户中断
    echo ""
    echo -e "${YELLOW}🎮 宠物公园运行中，按 Ctrl+C 停止所有服务...${NC}"
    echo -e "${CYAN}💡 提示: 您可以同时打开多个浏览器标签页游玩${NC}"
    echo -e "${GREEN}🔄 现在支持重复启动，如果需要重新启动，可以直接再次运行此脚本${NC}"
    wait
}

# 运行主函数
main "$@"
