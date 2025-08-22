#!/bin/bash

echo "🚀 开始构建前端应用..."

# 检查参数
ENV=${1:-production}
echo "📦 构建环境: $ENV"

# 进入前端目录
cd frontend

# 安装依赖
echo "📥 安装依赖..."
npm install

# 根据环境选择配置文件
if [ "$ENV" = "production" ]; then
    echo "🏭 使用生产环境配置..."
    cp env.production .env
elif [ "$ENV" = "local" ]; then
    echo "🏠 使用本地环境配置..."
    cp env.local .env
else
    echo "⚠️  未知环境，使用默认配置..."
fi

# 构建应用
echo "🔨 构建应用..."
npm run build

if [ $? -eq 0 ]; then
    echo "✅ 前端构建成功！"
    echo "📁 构建文件位于: frontend/dist/"
    
    # 显示构建结果
    echo "📊 构建统计:"
    du -sh dist/
    
    echo "🎉 前端构建完成！"
else
    echo "❌ 前端构建失败！"
    exit 1
fi
