#!/bin/bash

echo "🛑 AI Interview Assistant 停止服务脚本"
echo "====================================="

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 停止后端服务
stop_backend() {
    echo -e "${BLUE}停止后端服务...${NC}"
    
    # 查找Java进程
    BACKEND_PIDS=$(ps aux | grep "spring-boot:run" | grep -v grep | awk '{print $2}')
    
    if [ ! -z "$BACKEND_PIDS" ]; then
        echo "找到后端进程: $BACKEND_PIDS"
        kill $BACKEND_PIDS
        echo -e "${GREEN}✅ 后端服务已停止${NC}"
    else
        echo -e "${YELLOW}⚠️  未找到运行中的后端服务${NC}"
    fi
}

# 停止nginx服务
stop_nginx() {
    echo -e "${BLUE}停止nginx服务...${NC}"
    
    if command -v nginx &> /dev/null; then
        sudo systemctl stop nginx
        if [ $? -eq 0 ]; then
            echo -e "${GREEN}✅ nginx服务已停止${NC}"
        else
            echo -e "${YELLOW}⚠️  nginx服务可能已经停止${NC}"
        fi
    else
        echo -e "${YELLOW}⚠️  nginx未安装${NC}"
    fi
}

# 清理端口
cleanup_ports() {
    echo -e "${BLUE}清理端口...${NC}"
    
    # 检查8080端口
    if lsof -Pi :8080 -sTCP:LISTEN -t >/dev/null ; then
        echo "清理8080端口..."
        sudo lsof -ti:8080 | xargs sudo kill -9
    fi
    
    # 检查80端口
    if lsof -Pi :80 -sTCP:LISTEN -t >/dev/null ; then
        echo "清理80端口..."
        sudo lsof -ti:80 | xargs sudo kill -9
    fi
    
    echo -e "${GREEN}✅ 端口清理完成${NC}"
}

# 显示服务状态
show_status() {
    echo -e "${BLUE}当前服务状态:${NC}"
    echo "--------------------------------"
    
    # 检查后端
    if ps aux | grep "spring-boot:run" | grep -v grep > /dev/null; then
        echo -e "${GREEN}✅ 后端服务: 运行中${NC}"
    else
        echo -e "${RED}❌ 后端服务: 已停止${NC}"
    fi
    
    # 检查nginx
    if systemctl is-active --quiet nginx; then
        echo -e "${GREEN}✅ nginx服务: 运行中${NC}"
    else
        echo -e "${RED}❌ nginx服务: 已停止${NC}"
    fi
    
    # 检查端口
    if lsof -Pi :8080 -sTCP:LISTEN -t >/dev/null ; then
        echo -e "${GREEN}✅ 端口8080: 被占用${NC}"
    else
        echo -e "${RED}❌ 端口8080: 空闲${NC}"
    fi
    
    if lsof -Pi :80 -sTCP:LISTEN -t >/dev/null ; then
        echo -e "${GREEN}✅ 端口80: 被占用${NC}"
    else
        echo -e "${RED}❌ 端口80: 空闲${NC}"
    fi
}

# 主函数
main() {
    echo -e "${GREEN}开始停止服务...${NC}"
    echo ""
    
    echo -e "${BLUE}选择停止模式:${NC}"
    echo "1) 停止所有服务 (后端 + nginx)"
    echo "2) 仅停止后端服务"
    echo "3) 仅停止nginx服务"
    echo "4) 仅清理端口"
    echo "5) 显示服务状态"
    
    read -p "请选择 (1-5): " choice
    
    case $choice in
        1)
            echo -e "${GREEN}停止所有服务...${NC}"
            stop_backend
            stop_nginx
            cleanup_ports
            ;;
        2)
            echo -e "${GREEN}仅停止后端服务...${NC}"
            stop_backend
            ;;
        3)
            echo -e "${GREEN}仅停止nginx服务...${NC}"
            stop_nginx
            ;;
        4)
            echo -e "${GREEN}仅清理端口...${NC}"
            cleanup_ports
            ;;
        5)
            show_status
            exit 0
            ;;
        *)
            echo -e "${RED}❌ 无效选择${NC}"
            exit 1
            ;;
    esac
    
    echo ""
    echo -e "${GREEN}🎉 操作完成！${NC}"
    echo ""
    show_status
}

# 运行主函数
main
