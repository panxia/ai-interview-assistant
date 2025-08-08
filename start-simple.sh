#!/bin/bash

echo "🚀 AI Interview Assistant 简化启动脚本"
echo "======================================"

# 颜色定义
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# 设置环境变量
setup_env() {
    echo -e "${BLUE}设置环境变量...${NC}"
    
    if [ -z "$DOUBAO_API_KEY" ]; then
        echo -e "${YELLOW}⚠️  请设置豆包API密钥:${NC}"
        echo "export DOUBAO_API_KEY='your_api_key_here'"
        echo ""
    fi
    
    if [ -z "$DOUBAO_MODEL" ]; then
        echo -e "${YELLOW}⚠️  请设置模型端点ID:${NC}"
        echo "export DOUBAO_MODEL='ep-xxxxxxxx'"
        echo ""
    fi
    
    # 设置默认值
    export DOUBAO_API_BASE=${DOUBAO_API_BASE:-"https://ark.cn-beijing.volces.com/api/v3"}
    export ALLOWED_ORIGINS=${ALLOWED_ORIGINS:-"http://localhost:3000"}
    export SYSTEM_PROMPT=${SYSTEM_PROMPT:-"你是一个专业的面试官，擅长结构化提问与基于STAR法的追问，回答请简洁、条理清晰，并在必要时给出改进建议。"}
}

# 启动后端
start_backend() {
    echo -e "${BLUE}启动后端服务...${NC}"
    cd backend
    
    # 使用java命令直接运行
    echo "编译项目..."
    mvn clean compile -q
    
    echo "打包项目..."
    mvn package -DskipTests -q
    
    echo "启动Spring Boot应用..."
    cd backend
    java -jar target/*.jar &
    BACKEND_PID=$!
    cd ..
    
    echo "等待后端启动..."
    sleep 15
    
    cd ..
    echo -e "${GREEN}✅ 后端服务已启动: http://localhost:8080${NC}"
}

# 启动前端
start_frontend() {
    echo -e "${BLUE}启动前端服务...${NC}"
    cd frontend
    
    if [ ! -d "node_modules" ]; then
        echo "安装前端依赖..."
        npm install
    fi
    
    echo "启动Vue开发服务器..."
    cd frontend
    npm run dev &
    FRONTEND_PID=$!
    cd ..
    
    sleep 5
    cd ..
    echo -e "${GREEN}✅ 前端服务已启动: http://localhost:80${NC}"
}

# 显示信息
show_info() {
    echo ""
    echo -e "${GREEN}🎉 服务启动完成！${NC}"
    echo "=================================="
    echo -e "${BLUE}前端地址:${NC} http://localhost:80"
    echo -e "${BLUE}后端API:${NC} http://localhost:8080"
    echo ""
    echo -e "${YELLOW}使用说明:${NC}"
    echo "1. 在浏览器中访问 http://localhost:3000"
    echo "2. 开始您的AI面试体验"
    echo "3. 按 Ctrl+C 停止所有服务"
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
    trap cleanup SIGINT SIGTERM
    
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
            ;;
        3)
            echo -e "${GREEN}仅启动前端...${NC}"
            start_frontend
            ;;
        *)
            echo -e "${RED}❌ 无效选择${NC}"
            exit 1
            ;;
    esac
    
    echo ""
    echo -e "${YELLOW}服务运行中，按 Ctrl+C 停止...${NC}"
    wait
}

main
