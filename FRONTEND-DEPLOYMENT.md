# 🎨 前端部署指南

本文档介绍如何为域名 `pet.bwmiao.com` 配置和部署前端应用。

## 🚀 快速部署

### 1. 使用自动化脚本（推荐）

```bash
# 构建生产环境前端
./build-frontend.sh production

# 部署到Nginx
sudo cp -r frontend/dist/* /usr/share/nginx/html/
```

### 2. 手动部署步骤

#### 步骤1: 安装依赖
```bash
cd frontend
npm install
```

#### 步骤2: 配置环境变量
```bash
# 生产环境
cp env.production .env

# 本地开发环境
cp env.local .env
```

#### 步骤3: 构建应用
```bash
npm run build
```

#### 步骤4: 部署文件
```bash
# 将构建文件复制到Nginx目录
sudo cp -r dist/* /usr/share/nginx/html/
```

## ⚙️ 环境配置

### 生产环境配置 (env.production)
```
VITE_API_BASE=http://pet.bwmiao.com
```

### 本地开发配置 (env.local)
```
VITE_API_BASE=http://localhost:8080
```

### 自定义配置
您可以通过设置环境变量来覆盖默认配置：
```bash
export VITE_API_BASE=https://your-domain.com
npm run build
```

## 🔧 配置文件说明

### Vite配置 (vite.config.ts)
```typescript
export default defineConfig({
  server: {
    proxy: {
      '/api': {
        target: process.env.VITE_API_BASE || 'http://pet.bwmiao.com',
        changeOrigin: true
      }
    }
  }
})
```

### 前端API配置 (PetGame.vue)
```typescript
const apiBase = (import.meta as any).env.VITE_API_BASE || 'http://pet.bwmiao.com/api'
```

## 📁 构建输出

构建完成后，前端文件将位于 `frontend/dist/` 目录：
```
frontend/dist/
├── index.html          # 主页面
├── assets/             # 静态资源
│   ├── css/           # 样式文件
│   ├── js/            # JavaScript文件
│   └── images/        # 图片资源
└── favicon.ico         # 网站图标
```

## 🚨 注意事项

1. **环境变量**: 确保在构建前正确设置环境变量
2. **API地址**: 生产环境必须使用正确的域名
3. **HTTPS**: 建议配置SSL证书以支持HTTPS访问
4. **缓存**: 部署后可能需要清除浏览器缓存

## 🔍 故障排除

### 常见问题

1. **API请求失败**
   - 检查环境变量配置
   - 确认后端服务运行状态
   - 检查CORS配置

2. **构建失败**
   - 检查Node.js版本（建议使用Node.js 18+）
   - 清除node_modules并重新安装
   - 检查package.json依赖

3. **部署后无法访问**
   - 确认Nginx配置正确
   - 检查文件权限
   - 查看Nginx错误日志

### 调试命令

```bash
# 检查Nginx状态
sudo systemctl status nginx

# 查看Nginx错误日志
sudo tail -f /var/log/nginx/error.log

# 测试Nginx配置
sudo nginx -t

# 检查文件权限
ls -la /usr/share/nginx/html/
```

## 📚 相关文档

- [后端部署指南](DEPLOYMENT.md)
- [域名配置说明](README.md#域名配置)
- [Nginx配置参考](deploy/nginx-main.conf)

---

🎉 **前端部署完成！现在您可以通过 http://pet.bwmiao.com 访问您的宠物游戏了！**
