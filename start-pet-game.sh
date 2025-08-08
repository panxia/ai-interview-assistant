#!/bin/bash

echo "🐾 可爱宠物养成游戏 启动脚本"
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

# 启动前端
start_frontend() {
    echo -e "${PURPLE}🎨 启动宠物游戏前端服务...${NC}"
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
    
    # 启动开发服务器
    echo -e "${BLUE}🌈 启动Vue宠物游戏界面...${NC}"
    npm run dev > ../frontend.log 2>&1 &
    FRONTEND_PID=$!
    
    # 等待前端启动
    echo -e "${YELLOW}⏳ 等待前端服务启动...${NC}"
    sleep 8
    
    # 检查前端是否启动成功
    if curl -s http://localhost:5173 > /dev/null 2>&1; then
        echo -e "${GREEN}✅ 宠物游戏前端界面启动成功！${NC}"
    else
        echo -e "${YELLOW}⚠️  前端服务可能还在启动中...${NC}"
    fi
    
    cd ..
}

# 显示服务信息
show_info() {
    echo ""
    echo -e "${GREEN}🎉 宠物养成游戏启动完成！${NC}"
    echo "======================================"
    echo -e "${CYAN}🌐 游戏界面:${NC} http://localhost:5173"
    echo -e "${CYAN}🔧 后端API:${NC} http://localhost:8080"
    echo ""
    echo -e "${PURPLE}🐾 游戏特色:${NC}"
    echo "  🐱 7种可爱宠物: 猫咪、小狗、兔子、仓鼠、小龙、熊猫、企鹅"
    echo "  💖 5项状态管理: 饥饿、清洁、快乐、能量、健康"
    echo "  🎮 4种小游戏: 记忆、反应、猜谜、拍拍游戏"
    echo "  🏆 18个成就系统: 完成任务解锁奖励"
    echo "  🛍️ 物品收集: 食物、玩具、药品、装饰品"
    echo ""
    echo -e "${YELLOW}🎯 使用说明:${NC}"
    echo "  1. 在浏览器中访问 http://localhost:5173"
    echo "  2. 创建您的专属宠物并开始养成之旅"
    echo "  3. 通过互动动作照顾宠物的各项需求"
    echo "  4. 玩小游戏获得金币和经验值"
    echo "  5. 解锁成就获得特殊奖励"
    echo "  6. 按 Ctrl+C 停止所有服务"
    echo ""
    echo -e "${BLUE}📊 日志查看:${NC}"
    echo "  后端日志: tail -f backend.log"
    echo "  前端日志: tail -f frontend.log"
    echo ""
}

# 清理函数
cleanup() {
    echo ""
    echo -e "${YELLOW}🛑 正在停止宠物游戏服务...${NC}"
    
    if [ ! -z "$BACKEND_PID" ]; then
        kill $BACKEND_PID 2>/dev/null
        echo -e "${GREEN}✅ 后端服务已停止${NC}"
    fi
    
    if [ ! -z "$FRONTEND_PID" ]; then
        kill $FRONTEND_PID 2>/dev/null
        echo -e "${GREEN}✅ 前端服务已停止${NC}"
    fi
    
    # 清理可能残留的进程
    pkill -f "spring-boot:run" 2>/dev/null
    pkill -f "vite" 2>/dev/null
    
    echo -e "${PURPLE}👋 感谢游玩宠物养成游戏！${NC}"
    echo -e "${GREEN}🎉 所有服务已停止${NC}"
    exit 0
}

# 显示欢迎信息
show_welcome() {
    echo -e "${PURPLE}"
    cat << 'EOF'
    🐾 欢迎来到可爱宠物养成游戏！ 🐾
    
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

# 检查端口占用
check_ports() {
    echo -e "${BLUE}🔍 检查端口占用...${NC}"
    
    # 检查8080端口
    if lsof -Pi :8080 -sTCP:LISTEN -t >/dev/null 2>&1; then
        echo -e "${YELLOW}⚠️  端口8080已被占用${NC}"
        echo -e "${CYAN}可能已有后端服务在运行${NC}"
    fi
    
    # 检查5173端口
    if lsof -Pi :5173 -sTCP:LISTEN -t >/dev/null 2>&1; then
        echo -e "${YELLOW}⚠️  端口5173已被占用${NC}"
        echo -e "${CYAN}可能已有前端服务在运行${NC}"
    fi
}

# 主函数
main() {
    # 设置信号处理
    trap cleanup SIGINT SIGTERM
    
    show_welcome
    
    echo -e "${BLUE}🔧 开始环境检查...${NC}"
    echo ""
    
    # 环境检查
    check_java
    check_maven
    check_node
    check_npm
    check_ports
    setup_env
    
    echo ""
    echo -e "${PURPLE}🎮 选择启动模式:${NC}"
    echo "  1) 🌟 完整启动 (后端 + 前端) - 推荐"
    echo "  2) 🔧 仅启动后端服务"
    echo "  3) 🎨 仅启动前端界面"
    echo "  4) 📊 查看运行状态"
    echo ""
    
    read -p "请选择启动模式 (1-4): " choice
    
    case $choice in
        1)
            echo -e "${GREEN}🌟 启动完整宠物游戏服务...${NC}"
            echo ""
            start_backend
            start_frontend
            show_info
            
            # 自动打开浏览器（macOS）
            if [[ "$OSTYPE" == "darwin"* ]]; then
                echo -e "${CYAN}🌐 正在为您打开游戏界面...${NC}"
                sleep 2
                open http://localhost:5173 2>/dev/null
            fi
            ;;
        2)
            echo -e "${BLUE}🔧 仅启动后端服务...${NC}"
            start_backend
            echo -e "${GREEN}✅ 后端服务已启动: http://localhost:8080${NC}"
            ;;
        3)
            echo -e "${PURPLE}🎨 仅启动前端界面...${NC}"
            start_frontend
            echo -e "${GREEN}✅ 前端界面已启动: http://localhost:5173${NC}"
            ;;
        4)
            echo -e "${CYAN}📊 检查服务状态...${NC}"
            echo ""
            if curl -s http://localhost:8080/api/pet/types > /dev/null 2>&1; then
                echo -e "${GREEN}✅ 后端服务运行正常${NC}"
            else
                echo -e "${RED}❌ 后端服务未运行${NC}"
            fi
            
            if curl -s http://localhost:5173 > /dev/null 2>&1; then
                echo -e "${GREEN}✅ 前端服务运行正常${NC}"
            else
                echo -e "${RED}❌ 前端服务未运行${NC}"
            fi
            
            echo ""
            echo -e "${YELLOW}如需启动服务，请重新运行此脚本${NC}"
            exit 0
            ;;
        *)
            echo -e "${RED}❌ 无效选择，请输入1-4${NC}"
            exit 1
            ;;
    esac
    
    # 等待用户中断
    echo ""
    echo -e "${YELLOW}🎮 宠物游戏运行中，按 Ctrl+C 停止所有服务...${NC}"
    echo -e "${CYAN}💡 提示: 您可以同时打开多个浏览器标签页游玩${NC}"
    wait
}

# 运行主函数
main "$@"
