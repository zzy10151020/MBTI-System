import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
// import vueDevTools from 'vite-plugin-vue-devtools'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    // vueDevTools(),
    AutoImport({
      resolvers: [ElementPlusResolver()],
    }),
    Components({
      resolvers: [ElementPlusResolver({
        importStyle: 'css', // 使用 CSS 样式
      })],
    }),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080/mbti-system/',
        changeOrigin: true,
        secure: false,
        cookieDomainRewrite: {
          '*': 'localhost'
        },
        configure: (proxy, options) => {
          proxy.on('proxyReq', (proxyReq, req, res) => {
            // 确保转发所有Cookie头
            if (req.headers.cookie) {
              proxyReq.setHeader('Cookie', req.headers.cookie);
            }
          });
          
          proxy.on('proxyRes', (proxyRes, req, res) => {
            // 处理响应中的Set-Cookie头
            const setCookieHeader = proxyRes.headers['set-cookie'];
            if (setCookieHeader) {
              // 修改cookie的domain、path和安全标志以适应代理
              const modifiedCookies = setCookieHeader.map(cookie => {
                return cookie
                  .replace(/Domain=[^;]+;?/gi, 'Domain=localhost;')
                  .replace(/Path=\/mbti-system/gi, 'Path=/')
                  .replace(/;\s*HttpOnly/gi, '') // 移除HttpOnly以允许JavaScript访问
                  .replace(/;\s*Secure/gi, ''); // 移除Secure标志（开发环境用HTTP）
              });
              proxyRes.headers['set-cookie'] = modifiedCookies;
            }
          });
        }
      }
    }
  },
})
