# MBTI系统 API接口文档

## 目录
- [1. 概述](#1-概述)
- [2. 通用响应格式](#2-通用响应格式)
- [3. 认证模块 `/api/auth`](#3-认证模块-apiauth)
- [4. 用户模块 `/api/user`](#4-用户模块-apiuser)
- [5. 问卷模块 `/api/questionnaire`](#5-问卷模块-apiquestionnaire)
- [6. 问题模块 `/api/question`](#6-问题模块-apiquestion)
- [7. 测试模块 `/api/test`](#7-测试模块-apitest)

---

## 1. 概述

MBTI系统是一个基于Java Servlet的Web应用，提供MBTI性格测试功能。系统采用前后端分离架构，后端提供RESTful API接口。

**Base URL:** `http://localhost:8080/mbti-system`

**认证方式:** Session认证（基于HttpSession）

**内容类型:** `application/json`

---

## 2. 通用响应格式

所有API响应都使用统一的格式：

```json
{
  "success": true,
  "message": "操作成功",
  "data": {...},
  "timestamp": 1642680000000
}
```

**字段说明:**
- `success`: 布尔值，表示请求是否成功
- `message`: 字符串，响应消息
- `data`: 响应数据，类型根据具体接口而定
- `timestamp`: 时间戳

---

## 3. 认证模块 `/api/auth`

### 3.1 用户登录
**接口:** `POST /api/auth/login`

**请求体:**
```json
{
  "username": "string",
  "password": "string"
}
```

**响应示例:**
```json
{
  "success": true,
  "message": "登录成功",
  "data": {
    "success": true,
    "user": {
      "userId": 1,
      "username": "testuser",
      "email": "test@example.com",
      "role": "USER",
      "createdAt": "2024-01-01T10:00:00"
    },
    "sessionId": "JSESSIONID=ABC123"
  },
  "timestamp": 1642680000000
}
```

### 3.2 用户注册
**接口:** `POST /api/auth/register`

**请求体:**
```json
{
  "username": "string",
  "password": "string", 
  "email": "string"
}
```

**验证规则:**
- 用户名：3-20个字符
- 密码：至少6位
- 邮箱：有效邮箱格式

**响应示例:**
```json
{
  "success": true,
  "message": "注册成功",
  "data": {
    "success": true,
    "user": {
      "userId": 2,
      "username": "newuser",
      "email": "new@example.com",
      "role": "USER",
      "createdAt": "2024-01-01T10:00:00"
    },
    "message": "注册成功"
  },
  "timestamp": 1642680000000
}
```

### 3.3 用户注销
**接口:** `POST /api/auth/logout`

**响应示例:**
```json
{
  "success": true,
  "message": "注销成功",
  "data": "注销成功",
  "timestamp": 1642680000000
}
```

### 3.4 检查用户名/邮箱 (已禁用)
**接口:** `GET /api/auth/checkUsername` 和 `GET /api/auth/checkEmail`

**状态:** 这些接口已被禁用，返回错误提示使用POST方式查询。

---

## 4. 用户模块 `/api/user`

### 4.1 获取当前用户信息
**接口:** `GET /api/user`

**权限:** 需要登录

**响应示例:**
```json
{
  "success": true,
  "data": {
    "userId": 1,
    "username": "testuser",
    "email": "test@example.com",
    "role": "USER",
    "createdAt": "2024-01-01T10:00:00"
  },
  "timestamp": 1642680000000
}
```

### 4.2 更新用户信息
**接口:** `POST /api/user`

**权限:** 需要登录

**请求体:**
```json
{
  // 修改密码
  "currentPassword": "oldpassword",
  "newPassword": "newpassword",
  
  // 或者更新基本信息
  "username": "newusername",
  "email": "newemail@example.com",
  "role": "USER"  // 仅管理员可修改
}
```

**注意:**
- 密码修改和信息更新可以同时进行
- 新密码至少6位
- 用户名和邮箱不能与其他用户重复

### 4.3 获取用户详情 (已禁用)
**接口:** `GET /api/user/profile`

**状态:** 已禁用，建议使用POST方式查询

### 4.4 获取用户列表
**接口:** `GET /api/user/list`

**权限:** 仅管理员

**响应示例:**
```json
{
  "success": true,
  "data": [
    {
      "userId": 1,
      "username": "admin",
      "email": "admin@example.com",
      "role": "ADMIN",
      "createdAt": "2024-01-01T09:00:00"
    },
    {
      "userId": 2,
      "username": "user1",
      "email": "user1@example.com",
      "role": "USER",
      "createdAt": "2024-01-01T10:00:00"
    }
  ],
  "timestamp": 1642680000000
}
```

### 4.5 删除用户
**接口:** `DELETE /api/user`

**权限:** 仅管理员

**请求体:**
```json
{
  "deleteUserId": 123
}
```

**限制:**
- 管理员不能删除自己
- 只能删除存在的用户

---

## 5. 问卷模块 `/api/questionnaire`

### 5.1 根据ID获取问卷
**接口:** `GET /api/questionnaire/{id}`

**示例:** `GET /api/questionnaire/1`

**响应示例:**
```json
{
  "success": true,
  "data": {
    "questionnaireId": 1,
    "title": "MBTI人格测试",
    "description": "经典的MBTI人格测试",
    "creatorId": 1,
    "isPublished": true,
    "createdAt": "2024-01-01T09:00:00",
    "questions": [
      {
        "questionId": 1,
        "content": "你更喜欢与人交往还是独处？",
        "dimension": "EI",
        "questionOrder": 1,
        "options": [
          {
            "optionId": 1,
            "content": "与人交往",
            "score": 1,
            "optionOrder": 1
          }
        ]
      }
    ]
  },
  "timestamp": 1642680000000
}
```

### 5.2 获取已发布问卷列表
**接口:** `GET /api/questionnaire/published`

**响应示例:**
```json
{
  "success": true,
  "data": [
    {
      "questionnaireId": 1,
      "title": "MBTI人格测试",
      "description": "经典的MBTI人格测试",
      "creatorId": 1,
      "isPublished": true,
      "createdAt": "2024-01-01T09:00:00"
    }
  ],
  "timestamp": 1642680000000
}
```

### 5.3 获取所有问卷
**接口:** `GET /api/questionnaire/all`

**权限:** 仅管理员

### 5.4 根据创建者查找问卷
**接口:** `POST /api/questionnaire/byCreator`

**请求体:**
```json
{
  "creatorId": 1
}
```

### 5.5 搜索问卷
**接口:** `POST /api/questionnaire/search`

**请求体:**
```json
{
  "title": "MBTI"
}
```

### 5.6 创建问卷
**接口:** `POST /api/questionnaire`

**权限:** 仅管理员

**请求体:**
```json
{
  "title": "新问卷标题",
  "description": "问卷描述",
  "questions": [
    {
      "content": "问题内容",
      "dimension": "EI",
      "questionOrder": 1,
      "options": [
        {
          "content": "选项内容",
          "score": 1,
          "optionOrder": 1
        }
      ]
    }
  ]
}
```

### 5.7 更新问卷
**接口:** `PUT /api/questionnaire`

**权限:** 仅管理员

**请求体:**
```json
{
  "questionnaireId": 1,
  "title": "更新后的标题",
  "description": "更新后的描述"
}
```

### 5.8 删除问卷
**接口:** `DELETE /api/questionnaire`

**权限:** 仅管理员

**请求体:**
```json
{
  "questionnaireId": 1
}
```

### 5.9 发布问卷
**接口:** `POST /api/questionnaire/publish`

**权限:** 仅管理员

**请求体:**
```json
{
  "questionnaireId": 1
}
```

### 5.10 撤销发布
**接口:** `POST /api/questionnaire/unpublish`

**权限:** 仅管理员

**请求体:**
```json
{
  "questionnaireId": 1
}
```

### 5.11 获取问卷详情
**接口:** `POST /api/questionnaire/detail`

**请求体:**
```json
{
  "questionnaireId": 1
}
```

---

## 6. 问题模块 `/api/question`

### 6.1 获取所有问题
**接口:** `GET /api/question`

### 6.2 根据问卷ID获取问题
**接口:** `POST /api/question/by-questionnaire`

**请求体:**
```json
{
  "questionnaireId": 1
}
```

### 6.3 根据维度获取问题
**接口:** `POST /api/question/by-dimension`

**请求体:**
```json
{
  "dimension": "EI"
}
```

**可用维度:**
- `EI`: 外向-内向
- `SN`: 感觉-直觉  
- `TF`: 思维-情感
- `JP`: 判断-知觉

### 6.4 获取问题详情
**接口:** `POST /api/question/detail`

**请求体:**
```json
{
  "questionId": 1
}
```

### 6.5 创建问题
**接口:** `POST /api/question`

**权限:** 仅管理员

**请求体:**
```json
{
  "questionnaireId": 1,
  "content": "问题内容",
  "dimension": "EI",
  "questionOrder": 1,
  "options": [
    {
      "content": "选项内容",
      "score": 1,
      "optionOrder": 1
    }
  ]
}
```

### 6.6 批量创建问题
**接口:** `POST /api/question/batch`

**权限:** 仅管理员

**请求体:**
```json
[
  {
    "questionnaireId": 1,
    "content": "问题1",
    "dimension": "EI",
    "questionOrder": 1,
    "options": [...]
  },
  {
    "questionnaireId": 1,
    "content": "问题2", 
    "dimension": "SN",
    "questionOrder": 2,
    "options": [...]
  }
]
```

### 6.7 更新问题
**接口:** `PUT /api/question`

**权限:** 仅管理员

**请求体:**
```json
{
  "questionId": 1,
  "content": "更新后的问题内容",
  "dimension": "EI",
  "questionOrder": 1
}
```

### 6.8 删除问题
**接口:** `DELETE /api/question`

**权限:** 仅管理员

**请求体:**
```json
{
  "questionId": 1
}
```

---

## 7. 测试模块 `/api/test`

### 7.1 获取测试结果
**接口:** `GET /api/test`

**权限:** 需要登录

**功能:** 获取当前用户的所有测试结果

**响应示例:**
```json
{
  "success": true,
  "message": "获取测试结果成功",
  "data": [
    {
      "answerId": 1,
      "userId": 1,
      "questionnaireId": 1,
      "mbtiResult": "INTJ",
      "createdAt": "2024-01-01T10:30:00",
      "answerDetails": [
        {
          "answerDetailId": 1,
          "questionId": 1,
          "optionId": 1
        }
      ]
    }
  ],
  "timestamp": 1642680000000
}
```

### 7.2 获取特定问卷的测试结果
**接口:** `GET /api/test/{questionnaireId}`

**示例:** `GET /api/test/1`

**权限:** 需要登录

### 7.3 提交测试结果
**接口:** `POST /api/test`

**权限:** 需要登录

**请求体:**
```json
{
  "questionnaireId": 1,
  "answerDetails": [
    {
      "questionId": 1,
      "optionId": 2
    },
    {
      "questionId": 2,
      "optionId": 4
    }
  ]
}
```

**响应示例:**
```json
{
  "success": true,
  "message": "测试提交成功",
  "data": {
    "answerId": 1,
    "userId": 1,
    "questionnaireId": 1,
    "mbtiResult": "INTJ",
    "createdAt": "2024-01-01T10:30:00",
    "answerDetails": [...]
  },
  "timestamp": 1642680000000
}
```

### 7.4 检查用户完成状态 (已禁用)
**接口:** `GET /api/test/completed`

**状态:** 已禁用，建议使用POST方式查询

### 7.5 获取问卷统计数据 (已禁用)
**接口:** `GET /api/test/statistics`

**权限:** 仅管理员

**状态:** 已禁用，建议使用POST方式查询

---

## 8. 错误处理

### 8.1 常见错误响应

**未登录 (401):**
```json
{
  "success": false,
  "message": "未登录或会话过期",
  "timestamp": 1642680000000
}
```

**权限不足 (403):**
```json
{
  "success": false,
  "message": "权限不足，需要管理员权限",
  "timestamp": 1642680000000
}
```

**参数错误 (400):**
```json
{
  "success": false,
  "message": "请求参数不完整或格式错误",
  "timestamp": 1642680000000
}
```

**资源不存在 (404):**
```json
{
  "success": false,
  "message": "请求的资源不存在",
  "timestamp": 1642680000000
}
```

**服务器错误 (500):**
```json
{
  "error": "服务器内部错误",
  "message": "详细错误信息",
  "path": "/api/xxx",
  "timestamp": 1642680000000
}
```

---

## 9. 数据模型

### 9.1 用户角色枚举
- `USER`: 普通用户
- `ADMIN`: 管理员

### 9.2 问题维度枚举
- `EI`: 外向(Extroversion) - 内向(Introversion)
- `SN`: 感觉(Sensing) - 直觉(iNtuition)  
- `TF`: 思维(Thinking) - 情感(Feeling)
- `JP`: 判断(Judging) - 知觉(Perceiving)

### 9.3 MBTI结果类型
16种可能的组合：
- `ISTJ`, `ISFJ`, `INFJ`, `INTJ`
- `ISTP`, `ISFP`, `INFP`, `INTP`  
- `ESTP`, `ESFP`, `ENFP`, `ENTP`
- `ESTJ`, `ESFJ`, `ENFJ`, `ENTJ`

---

## 10. 开发注意事项

1. **Session管理**: 系统使用HttpSession进行用户认证，请确保在请求头中携带正确的Cookie
2. **权限控制**: 部分接口需要管理员权限，请确保当前用户角色正确
3. **数据验证**: 所有输入数据都会进行服务端验证，请按照文档要求传递参数
4. **错误处理**: 建议在前端对各种错误状态进行适当的处理和用户提示
5. **接口版本**: 部分GET接口已被禁用，建议使用对应的POST接口进行查询操作

---

*文档版本: 1.0*  
*最后更新: 2024年1月1日*