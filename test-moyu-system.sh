#!/bin/bash

echo "🐟 摸鱼系统测试脚本"
echo "=================="

# 检查后端服务
echo "1. 检查后端服务状态..."
if curl -s http://localhost:8080/api/fishes > /dev/null 2>&1; then
    echo "✅ 后端服务运行正常 (http://localhost:8080)"
else
    echo "❌ 后端服务未响应"
    exit 1
fi

# 检查前端服务
echo "2. 检查前端服务状态..."
if curl -s http://localhost:3000 > /dev/null 2>&1; then
    echo "✅ 前端服务运行正常 (http://localhost:3000)"
else
    echo "❌ 前端服务未响应"
    exit 1
fi

# 测试API端点
echo "3. 测试API端点..."
echo "   - 获取所有鱼: $(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/fishes)"

# 显示访问信息
echo ""
echo "🎯 系统访问信息:"
echo "   前端页面: http://localhost:3000"
echo "   后端API: http://localhost:8080/api/fishes"
echo "   数据库控制台: http://localhost:8080/h2-console"
echo ""
echo "🚀 现在可以打开浏览器访问 http://localhost:3000 开始摸鱼了！"
echo ""
echo "📝 使用说明:"
echo "   1. 在画布上绘制你的鱼"
echo "   2. 给鱼取个名字"
echo "   3. 点击保存鱼"
echo "   4. 观看你的鱼在海底游动"
echo "   5. 为其他鱼点赞"
echo ""
echo "✨ 不同浏览器访问相同地址都能看到相同的鱼群！"
