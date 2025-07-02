# MBTI系统前端API模块

本目录包含了MBTI系统前端的所有API接口封装，采用模块化设计，便于维护和使用。

## 文件结构

```
src/api/
├── index.ts          # 统一导出文件
├── types.ts          # TypeScript类型定义
├── axios.ts          # Axios实例配置
├── auth.ts           # 认证相关API
├── user.ts           # 用户管理API
├── questionnaire.ts  # 问卷管理API
├── test.ts           # 测试相关API
├── examples.ts       # 使用示例和组合式函数
└── README.md         # 本文件
```

## 快速开始

### 1. 基础导入

```typescript
import { authApi, userApi, questionnaireApi, testApi } from '@/api'
```

### 2. 认证流程

```typescript
// 登录
const loginResult = await authApi.login({
  username: 'testuser',
  password: '123456'
})

// 保存token
authApi.setToken(loginResult.token)

// 检查登录状态
if (authApi.isLoggedIn()) {
  // 用户已登录
}

// 退出登录
authApi.logout()
```

### 3. 获取问卷并开始测试

```typescript
// 获取活跃问卷
const questionnaires = await questionnaireApi.getActiveQuestionnaires()

// 获取问卷问题
const questionnaireDetail = await questionnaireApi.getQuestionnaireQuestions(1)

// 提交答案
const testResult = await testApi.submitAnswers({
  questionnaireId: 1,
  questionAnswers: {
    '1': 1,  // 问题ID: 选项ID
    '2': 4,
    '3': 5
  }
})

// 获取MBTI报告
const report = await testApi.getMbtiReport(testResult.answerId)
```

## API模块说明

### authApi - 认证模块
- `login(data)` - 用户登录
- `register(data)` - 用户注册  
- `logout()` - 退出登录
- `isLoggedIn()` - 检查登录状态
- `getToken()` - 获取token
- `setToken(token)` - 设置token

### userApi - 用户管理模块
- `getProfile()` - 获取当前用户信息
- `updateProfile(data)` - 更新用户信息
- `getUserList(params)` - 获取用户列表（管理员）
- `deleteUser(id)` - 删除用户（管理员）

### questionnaireApi - 问卷管理模块
- `getQuestionnaireList(params)` - 获取问卷列表
- `getActiveQuestionnaires()` - 获取活跃问卷
- `getQuestionnaireDetail(id)` - 获取问卷详情
- `getQuestionnaireQuestions(id)` - 获取问卷问题
- `createQuestionnaire(data)` - 创建问卷（管理员）
- `updateQuestionnaire(id, data)` - 更新问卷（管理员）
- `deleteQuestionnaire(id)` - 删除问卷（管理员）

### testApi - 测试模块
- `submitAnswers(data)` - 提交答案
- `getTestResults(params)` - 获取测试结果列表
- `getMbtiReport(answerId)` - 获取MBTI报告
- `getTestStatistics()` - 获取测试统计（管理员）
- `deleteTestResult(answerId)` - 删除测试结果

## 使用组合式函数

为了更好地在Vue组件中使用API，我们提供了预封装的组合式函数：

```typescript
import { useLogin, useUserProfile, useQuestionnaires, useTest } from '@/api/examples'

// 在组件中使用
const { login, logout, loading, errorMessage } = useLogin()
const { user, fetchProfile, updateProfile } = useUserProfile()
const { questionnaires, fetchActiveQuestionnaires } = useQuestionnaires()
const { submitTest, fetchTestResults, getMbtiReport } = useTest()
```

## 错误处理

所有API都包含统一的错误处理：

- HTTP 401: 自动清除token并提示重新登录
- HTTP 403: 权限不足提示
- HTTP 404: 资源不存在提示
- HTTP 500: 服务器错误提示
- 业务错误: 显示后端返回的错误信息

## 类型安全

所有API都有完整的TypeScript类型定义，提供：

- 请求参数类型检查
- 响应数据类型提示
- 编辑器智能提示和自动补全

## 配置说明

### 环境配置

API基础URL会根据环境自动切换：
- 开发环境: `http://localhost:8080`
- 生产环境: 需要在 `src/api/index.ts` 中配置

### 请求配置

- 超时时间: 15秒
- 自动添加Authorization头
- 自动处理CORS
- 支持请求和响应拦截

## 注意事项

1. 所有需要认证的接口都会自动添加Authorization头
2. Token过期时会自动清除并提示重新登录
3. 建议使用组合式函数而不是直接调用API
4. 错误处理已统一封装，一般不需要额外处理
5. 分页参数默认值：page=0, size=10

## 更新日志

- v1.0: 初始版本，实现基础CRUD操作
- 完整支持MBTI系统后端API接口文档中的所有接口
