# MBTI 性格测试系统

一个功能完整的 MBTI 性格测试系统，采用前后端分离架构，提供用户认证、问卷管理、在线测试和结果分析等功能。

## 📁 项目结构

```
MBTI-System/
├── backend/                    # 后端 (Java + Servlet + MySQL)
│   ├── src/main/java/         # Java 源码
│   ├── src/main/resources/    # 配置文件和资源
│   ├── src/main/webapp/       # Web 应用配置
│   ├── target/                # 编译输出 (ignored)
│   └── pom.xml               # Maven 配置
├── frontend/                   # 前端 (Vue 3 + TypeScript + Vite)
│   ├── src/                   # 前端源码
│   ├── public/                # 静态资源
│   ├── dist/                  # 构建输出 (ignored)
│   ├── node_modules/          # 依赖 (ignored)
│   └── package.json           # npm 配置
├── logs/                      # 应用日志 (ignored)
│   └── mbti-system.log       # 主日志文件
├── .gitignore                # Git 忽略配置
├── MBTI-System.iml           # IntelliJ IDEA 项目文件
└── README.md                 # 项目说明文档
```

## 🛠️ 快速启动

### 环境要求

**前端**
- Node.js 16+
- npm 或 yarn

**后端**
- Java 17+
- Maven 3.6+
- MySQL 8.0+

### 数据库配置

1. **创建数据库**
   ```sql
   CREATE DATABASE mbti_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

2. **配置数据库连接**
   编辑 `backend/src/main/resources/application.yaml`：
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/mbti_system
       username: your_username
       password: your_password
   ```

### 后端启动

1. **进入后端目录**
   ```bash
   cd backend
   ```

2. **安装依赖并启动**
   ```bash
   # 使用 Maven
   mvn spring-boot:run
   
   # 或使用 Maven Wrapper
   ./mvnw spring-boot:run
   ```

3. **验证启动**
   - 后端服务运行在：`http://localhost:8080`
   - 数据库表会自动创建并初始化测试数据

### 前端启动

1. **进入前端目录**
   ```bash
   cd frontend
   ```

2. **安装依赖**
   ```bash
   npm install
   ```

3. **启动开发服务器**
   ```bash
   npm run dev
   ```

4. **访问应用**
   - 前端应用运行在：`http://localhost:5173`（或其他可用端口）

## 🚀 项目概述

### 技术架构

**前端技术栈**
- Vue 3.5+ (Composition API)
- TypeScript 5.8+
- Element Plus 2.9+
- Pinia 3.0+ (状态管理)
- Vue Router 4.5+ (路由)
- Axios 1.9+ (HTTP 客户端)
- Vite 6.2+ (构建工具)

**后端技术栈**
- Spring Boot 3.x
- Spring Security + JWT
- MySQL 8.0+
- Spring Data JPA + Hibernate
- Maven (构建工具)
- Java 17+

## 🎯 功能模块

### 用户认证
- 用户注册和登录
- JWT 令牌认证
- 自动令牌刷新
- 权限控制

### 问卷系统
- 问卷浏览和搜索
- 分页显示
- 管理员问卷管理（创建、编辑、发布）
- 问题和选项管理

### 测试功能
- 在线答题
- 实时进度显示
- 答案提交和验证
- MBTI 结果计算

### 结果管理
- 个人测试历史
- 详细 MBTI 报告
- 测试统计分析
- 数据导出功能

### 管理功能
- 用户管理
- 问卷统计
- 系统配置
- 数据监控

## 🔧 开发指南

### API 文档
- 后端 API 文档：`backend/introductions/API_DOCUMENTATION.md`
- 接口基地址：`http://localhost:8080/api`

### 测试账号
系统提供以下测试账号（密码均为 `password`）：

**管理员账号**
- 用户名：`admin`
- 权限：完整管理权限

**普通用户账号**
- 用户名：`user1`, `user2`
- 权限：基础测试功能

### 构建部署

**前端构建**
```bash
cd frontend
npm run build
```

**后端构建**
```bash
cd backend
mvn clean package
```

### 代码规范
- 前端：TypeScript + ESLint + Prettier
- 后端：Java Code Style + Checkstyle
- Git：Conventional Commits

## 🌟 核心特性

### 1. 智能降级机制
当后端 API 不可用时，前端自动切换到本地 Mock 数据，确保用户体验。

### 2. 响应式设计
完美适配桌面端、平板和移动设备，提供一致的用户体验。

### 3. 实时状态同步
用户登录状态、测试进度等关键数据实时同步，支持多标签页使用。

### 4. 安全性保障
- JWT 令牌认证
- 密码 BCrypt 加密
- CORS 跨域配置
- SQL 注入防护

### 5. 性能优化
- 前端路由懒加载
- API 请求缓存
- 数据库连接池
- 静态资源压缩

## 🚨 常见问题

### 后端启动失败
1. 检查 Java 版本是否为 17+
2. 确认 MySQL 服务正在运行
3. 验证数据库连接配置
4. 查看端口 8080 是否被占用

### 前端无法连接后端
1. 确认后端服务已启动（`http://localhost:8080`）
2. 检查浏览器控制台网络错误
3. 验证 CORS 配置是否正确
4. 确认防火墙设置

### 数据库连接问题
1. 确认 MySQL 服务状态
2. 验证数据库用户权限
3. 检查连接字符串格式
4. 确认数据库已创建

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 🤝 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

---

**MBTI 性格测试系统** - 探索真实的自己 🧠✨

如有问题或建议，请创建 Issue 或联系项目维护者。
