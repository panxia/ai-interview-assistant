#!/bin/bash

echo "🚀 开始配置域名 pet.bwmiao.com 访问..."

# 检查是否以root权限运行
if [ "$EUID" -ne 0 ]; then
    echo "❌ 请使用sudo权限运行此脚本"
    exit 1
fi

# 创建SSL证书目录
echo "📁 创建SSL证书目录..."
mkdir -p /etc/nginx/ssl

# 检查SSL证书是否存在
if [ ! -f "/etc/nginx/ssl/pet.bwmiao.com.crt" ] || [ ! -f "/etc/nginx/ssl/pet.bwmiao.com.key" ]; then
    echo "⚠️  SSL证书文件不存在，将使用HTTP配置"
    echo "📋 复制HTTP配置文件..."
    cp deploy/nginx-main.conf /etc/nginx/nginx.conf
else
    echo "✅ SSL证书文件存在，将使用HTTPS配置"
    echo "📋 复制HTTPS配置文件..."
    cp deploy/nginx-https.conf /etc/nginx/nginx.conf
fi

# 复制MIME类型文件
echo "📋 复制MIME类型文件..."
cp deploy/mime.types /etc/nginx/

# 测试Nginx配置
echo "🧪 测试Nginx配置..."
nginx -t

if [ $? -eq 0 ]; then
    echo "✅ Nginx配置测试通过"
    
    # 重启Nginx服务
    echo "🔄 重启Nginx服务..."
    systemctl restart nginx
    
    if [ $? -eq 0 ]; then
        echo "✅ Nginx服务重启成功"
        echo "🌐 域名配置完成！"
        echo "📱 您现在可以通过以下地址访问："
        echo "   HTTP:  http://pet.bwmiao.com"
        if [ -f "/etc/nginx/ssl/pet.bwmiao.com.crt" ]; then
            echo "   HTTPS: https://pet.bwmiao.com"
        fi
    else
        echo "❌ Nginx服务重启失败"
        exit 1
    fi
else
    echo "❌ Nginx配置测试失败"
    exit 1
fi

echo "🎉 域名配置完成！"
