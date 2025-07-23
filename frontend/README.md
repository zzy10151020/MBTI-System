# MBTI-System 前端说明文档

## 项目简介

本前端为 MBTI 测评系统的用户界面，基于 Vue 3 + TypeScript + Element Plus 实现，支持 MBTI 测试、结果展示、用户管理、16型人格介绍等功能，风格现代、响应式，适配 PC 和移动端。

## 目录结构

```
frontend/
├── public/                # 静态资源（如 MBTI 字母图片）
├── src/
│   ├── api/               # API 封装与类型定义
│   ├── assets/            # 全局样式、图片等
│   ├── components/        # 通用组件（如头部、搜索栏、弹窗等）
│   ├── router/            # 路由配置
│   ├── stores/            # Pinia 状态管理
│   ├── utils/             # 工具函数
│   └── views/             # 主要页面视图
│       ├── HomeView.vue           # 首页，入口引导
│       ├── QuestionnaireView.vue  # 问卷选择与分页
│       ├── TestView.vue           # 测试答题页
│       ├── ResultsView.vue        # 测试结果与报告导出
│       ├── UserProfileView.vue    # 用户个人中心
│       ├── AdminUsersView.vue     # 管理员用户管理
│       ├── AdminQuestionsView.vue # 管理员题库管理
│       ├── AdminQuestionnairesView.vue # 管理员问卷管理
│       ├── MBTIProfilesView.vue   # MBTI 16型人格介绍
│       └── ...
├── package.json           # 依赖与脚本
├── vite.config.ts         # Vite 配置
└── ...
```

## 主要功能说明

- **首页（HomeView.vue）**：
  - 欢迎引导，按钮跳转问卷选择。
  - 特色 MBTI 四维度轮播图，风格统一。

- **问卷选择（QuestionnaireView.vue）**：
  - 展示所有可用问卷，支持分页。
  - 卡片式布局，已完成问卷有标记，按钮禁用。

- **测试答题（TestView.vue）**：
  - 动态加载题目，支持进度、自动保存。

- **结果展示（ResultsView.vue）**：
  - 展示 MBTI 测试结果，支持导出 PDF 报告。

- **用户管理（AdminUsersView.vue）**：
  - 管理员可增删改查用户，支持重置密码、角色切换。

- **16型人格介绍（MBTIProfilesView.vue）**：
  - 以分页形式展示 16 种 MBTI 人格。
  - 顶部标签栏可按四大维度筛选（E/I、S/N、T/F、J/P），默认全部。
  - 每页展示一型，含类型、中文名、简介、维度标签、代表图片。

- **其它**：
  - 全局响应式设计，适配移动端。
  - 统一主题色、按钮、分页、弹窗等风格。

## 技术栈

- Vue 3 + Composition API
- TypeScript
- Element Plus
- Pinia
- Vite

## 运行与开发

1. 安装依赖：
   ```bash
   npm install
   ```
2. 启动开发服务器：
   ```bash
   npm run dev
   ```
3. 打包构建：
   ```bash
   npm run build
   ```

## 其它说明

- 静态图片位于 `public/MBTI_LETTER/`，如 E.png、I.png 等。
- 主题色、基础样式见 `src/assets/base.css`。
- 具体 API 接口见 `src/api/` 及后端文档。

如需二次开发或定制，建议先阅读各视图源码及全局样式。
