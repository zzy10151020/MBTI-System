# MBTI 性格测试系统 - 前端

这是一个基于 Vue 3 + TypeScript + Element Plus 的 MBTI 性格测试系统前端项目。

## 🚀 功能特性

### 🔐 用户认证
- **登录/注册系统**：支持用户账号管理
- **JWT 令牌认证**：安全的用户身份验证
- **权限控制**：普通用户和管理员权限分级

### 📋 问卷系统
- **问卷浏览**：查看所有可用的 MBTI 测试问卷
- **分页显示**：优化大量问卷的浏览体验
- **响应式设计**：适配桌面端和移动端
- **问卷卡片**：美观的卡片式展示

### 🧠 测试功能
- **在线答题**：流畅的答题体验
- **进度显示**：实时显示答题进度
- **倒计时功能**：可选的答题时间限制
- **答案提交**：支持 API 和降级方案

### 📊 结果管理
- **测试历史**：查看所有历史测试记录
- **MBTI 报告**：详细的性格分析报告
- **结果统计**：个人测试数据统计
- **数据导出**：支持测试数据导出

### 👤 个人中心
- **用户资料**：查看和编辑个人信息
- **测试统计**：个人测试数据概览
- **快速操作**：便捷的功能入口

### 🛡️ 管理员功能
- **问卷管理**：创建、编辑、发布问卷
- **用户管理**：管理系统用户
- **数据统计**：全站数据统计分析
- **权限控制**：管理员专属功能

## 🏗️ 技术栈

- **前端框架**：Vue 3.5+ (Composition API)
- **开发语言**：TypeScript 5.8+
- **UI 组件库**：Element Plus 2.9+
- **状态管理**：Pinia 3.0+
- **路由管理**：Vue Router 4.5+
- **HTTP 客户端**：Axios 1.9+
- **构建工具**：Vite 6.2+
- **图标库**：Element Plus Icons

## 📁 项目结构

```
frontend/
├── public/                 # 静态资源
│   └── MBTI_LETTER/       # MBTI 字母图片
├── src/
│   ├── api/               # API 接口
│   │   ├── auth.ts        # 认证相关 API
│   │   ├── user.ts        # 用户相关 API
│   │   ├── questionnaire.ts # 问卷相关 API
│   │   ├── test.ts        # 测试相关 API
│   │   ├── types.ts       # TypeScript 类型定义
│   │   └── axios.ts       # Axios 配置
│   ├── components/        # 公共组件
│   │   ├── Header.vue     # 头部导航
│   │   ├── HeaderAvatar.vue # 头像菜单
│   │   ├── LoginMiniMask.vue # 登录弹窗
│   │   └── SearchBar.vue  # 搜索栏
│   ├── stores/            # Pinia 状态管理
│   │   ├── userStore.ts   # 用户状态
│   │   ├── questionnaireStore.ts # 问卷状态
│   │   ├── testStore.ts   # 测试状态
│   │   └── uiStateStore.ts # UI 状态
│   ├── views/             # 页面组件
│   │   ├── HomeView.vue   # 首页
│   │   ├── QuestionnaireView.vue # 问卷选择页
│   │   ├── TestView.vue   # 测试答题页
│   │   ├── ResultsView.vue # 测试结果页
│   │   ├── UserProfileView.vue # 个人中心
│   │   ├── AdminQuestionnairesView.vue # 管理员问卷管理
│   │   └── NotFoundView.vue # 404 页面
│   ├── router/            # 路由配置
│   │   └── index.ts       # 路由定义和守卫
│   ├── assets/            # 静态资源
│   │   └── main.css       # 全局样式
│   ├── App.vue           # 根组件
│   └── main.ts           # 应用入口
├── package.json          # 项目依赖
├── vite.config.ts        # Vite 配置
└── tsconfig.json         # TypeScript 配置
```

## 🎯 页面路由

所有页面路由都支持可选的 `:uid` 后缀，用于用户身份标识：

- `/` 或 `/:uid` - 首页
- `/questionnaires/:uid?` - 问卷选择页
- `/test/:uid?` - 测试答题页
- `/results/:uid?` - 测试结果页
- `/profile/:uid?` - 个人中心页
- `/admin/questionnaires/:uid?` - 管理员问卷管理页（需要管理员权限）

## 🔧 开发指南

### 环境要求

- Node.js 16+
- npm 或 yarn

### 安装依赖

```bash
npm install
```

### 开发环境运行

```bash
npm run dev
```

### 构建生产版本

```bash
npm run build
```

### 类型检查

```bash
npm run type-check
```

## 🎨 设计特点

### 色彩方案
- **主色调**：青色系 (#20B2AA)
- **辅助色**：淡青色、灰色系
- **强调色**：绿色（成功）、红色（警告）、蓝色（信息）

### 响应式设计
- **桌面端**：1200px+ 完整功能展示
- **平板端**：768px-1199px 适配调整
- **移动端**：320px-767px 移动优化

### 动画效果
- **页面切换**：平滑的路由过渡
- **交互反馈**：按钮悬停、点击效果
- **加载状态**：Loading 动画

## 📡 API 集成

### 认证机制
- 使用 JWT 令牌进行身份验证
- 自动令牌刷新机制
- 请求拦截器自动添加认证头

### 错误处理
- 统一的错误处理机制
- API 失败时的降级方案
- 用户友好的错误提示

### 数据缓存
- Pinia 状态持久化
- 本地存储管理
- 智能数据更新

## 🔒 权限控制

### 路由守卫
- 自动检查用户登录状态
- 管理员页面权限验证
- 未授权访问重定向

### 组件权限
- 条件渲染管理员功能
- 用户角色动态显示
- 操作权限检查

## 🧪 开发规范

### 代码风格
- 使用 TypeScript 严格模式
- Vue 3 Composition API
- ESLint + Prettier 代码格式化

### 组件设计
- 单一职责原则
- Props 类型定义
- 事件命名规范

### 状态管理
- Pinia 模块化设计
- 异步操作统一处理
- 错误状态管理

## 🌟 特色功能

### 1. 智能降级
当 API 请求失败时，自动切换到本地 Mock 数据，确保用户体验不受影响。

### 2. 实时同步
用户登录状态、测试进度等关键数据实时同步，支持多标签页使用。

### 3. 移动优化
专门针对移动设备优化的交互体验，支持触摸操作和手势导航。

### 4. 数据导出
支持个人测试数据的 JSON 格式导出，便于数据备份和分析。

### 5. 主题适配
支持浅色/深色主题切换（预留），适应不同用户偏好。

---

**MBTI 性格测试系统** - 探索真实的自己 🧠✨
