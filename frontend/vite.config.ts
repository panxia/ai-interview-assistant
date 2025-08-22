import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 80,
    proxy: {
      '/api': {
        target: process.env.VITE_API_BASE || 'http://pet.bwmiao.com',
        changeOrigin: true
      }
    }
  },
  build: {
    outDir: 'dist'
  }
})
