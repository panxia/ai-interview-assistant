#!/bin/bash

echo "🚀 AI Interview Assistant 启动脚本"
echo "=================================="

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
    export ALLOWED_ORIGINS=${ALLOWED_ORIGINS:-"http://localhost:5173"}
    export SYSTEM_PROMPT=${SYSTEM_PROMPT:-"你是一个专业的面试官，擅长结构化提问与基于STAR法的追问，回答请简洁、条理清晰，并在必要时给出改进建议。"}
    
    echo -e "${GREEN}✅ 环境变量设置完成${NC}"
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

# 启动前端
start_frontend() {
    echo -e "${BLUE}启动前端服务...${NC}"
    cd frontend
    
    # 检查node_modules是否存在
    if [ ! -d "node_modules" ]; then
        echo "安装前端依赖..."
        npm install
    fi
    
    # 启动开发服务器
    echo "启动Vue开发服务器..."
    npm run dev &
    FRONTEND_PID=$!
    
    # 等待前端启动
    echo "等待前端服务启动..."
    sleep 5
    
    cd ..
}

# 显示服务信息
show_info() {
    echo ""
    echo -e "${GREEN}🎉 服务启动完成！${NC}"
    echo "=================================="
    echo -e "${BLUE}前端地址:${NC} http://localhost:5173"
    echo -e "${BLUE}后端API:${NC} http://localhost:8080"
    echo ""
    echo -e "${YELLOW}使用说明:${NC}"
    echo "1. 在浏览器中访问 http://localhost:5173"
    echo "2. 开始您的AI面试体验"
    echo "3. 按 Ctrl+C 停止所有服务"
    echo ""
    echo -e "${YELLOW}日志查看:${NC}"
    echo "后端日志: 查看当前终端"
    echo "前端日志: 查看新打开的终端"
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
    
    if [ ! -z "$FRONTEND_PID" ]; then
        kill $FRONTEND_PID 2>/dev/null
        echo -e "${GREEN}✅ 前端服务已停止${NC}"
    fi
    
    echo -e "${GREEN}🎉 所有服务已停止${NC}"
    exit 0
}

# 主函数
main() {
    # 设置信号处理
    trap cleanup SIGINT SIGTERM
    
    echo -e "${GREEN}开始启动AI面试助手...${NC}"
    echo ""
    
    # 环境检查
    check_java
    check_maven
    check_node
    check_npm
    setup_env
    
    echo ""
    echo -e "${BLUE}选择启动模式:${NC}"
    echo "1) 完整启动 (后端 + 前端)"
    echo "2) 仅启动后端"
    echo "3) 仅启动前端"
    
    read -p "请选择 (1-3): " choice
    
    case $choice in
        1)
            echo -e "${GREEN}启动完整服务...${NC}"
            start_backend
            start_frontend
            show_info
            ;;
        2)
            echo -e "${GREEN}仅启动后端...${NC}"
            start_backend
            echo -e "${GREEN}✅ 后端服务已启动: http://localhost:8080${NC}"
            ;;
        3)
            echo -e "${GREEN}仅启动前端...${NC}"
            start_frontend
            echo -e "${GREEN}✅ 前端服务已启动: http://localhost:5173${NC}"
            ;;
        *)
            echo -e "${RED}❌ 无效选择${NC}"
            exit 1
            ;;
    esac
    
    # 等待用户中断
    echo ""
    echo -e "${YELLOW}服务运行中，按 Ctrl+C 停止...${NC}"
    wait
}

# 运行主函数
main
