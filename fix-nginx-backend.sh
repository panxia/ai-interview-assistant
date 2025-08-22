#!/bin/bash

echo "🔧 修复nginx配置中的backend引用问题..."
echo "======================================"

# 检查是否以root权限运行
if [ "$EUID" -ne 0 ]; then
    echo "❌ 请使用sudo权限运行此脚本"
    exit 1
fi

echo "📋 问题分析："
echo "  - nginx配置中使用了 'backend' 作为upstream名称"
echo "  - 但您的环境中后端服务运行在localhost:8080"
echo "  - 需要将 'backend:8080' 改为 'localhost:8080'"
echo ""

echo "🔍 检查当前nginx配置..."
if nginx -t 2>&1 | grep -q "host not found in upstream"; then
    echo "✅ 确认存在backend引用问题"
else
    echo "ℹ️  未检测到backend引用问题"
fi

echo ""
echo "📝 修复配置文件..."

# 修复所有nginx配置文件
echo "  1. 修复 nginx-main.conf..."
sed -i 's/http:\/\/backend:8080/http:\/\/localhost:8080/g' deploy/nginx-main.conf

echo "  2. 修复 nginx-https.conf..."
sed -i 's/http:\/\/backend:8080/http:\/\/localhost:8080/g' deploy/nginx-https.conf

echo "  3. 修复 nginx.conf..."
sed -i 's/http:\/\/backend:8080/http:\/\/localhost:8080/g' deploy/nginx.conf

echo "✅ 配置文件修复完成！"
echo ""

echo "🧪 测试nginx配置..."
if nginx -t; then
    echo "✅ nginx配置测试通过！"
    
    echo ""
    echo "🔄 重启nginx服务..."
    systemctl restart nginx
    
    if [ $? -eq 0 ]; then
        echo "✅ nginx服务重启成功！"
        echo ""
        echo "🎉 问题已解决！现在您可以正常使用nginx了。"
        echo ""
        echo "📱 访问地址："
        echo "  HTTP:  http://pet.bwmiao.com"
        echo "  HTTPS: https://pet.bwmiao.com (如果配置了SSL)"
    else
        echo "❌ nginx服务重启失败"
        exit 1
    fi
else
    echo "❌ nginx配置仍有问题，请检查配置文件"
    exit 1
fi

echo ""
echo "📚 修复说明："
echo "  - 将配置中的 'backend:8080' 改为 'localhost:8080'"
echo "  - 这是因为您的后端服务运行在主机上，不是Docker容器"
echo "  - 如果以后使用Docker部署，可以再改回 'backend:8080'"
