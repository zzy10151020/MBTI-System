# MBTI 系统 API 接口文档

## 目录

### 1. [基础信息](#基础信息)
- 基础URL和配置
- 通用响应格式

### 2. [认证相关接口 (/api/auth)](#认证相关接口-apiauth)
- [用户登录](#1-用户登录)
- [用户注册](#2-用户注册)
- [用户登出](#3-用户登出)
- [检查用户名是否存在](#4-检查用户名是否存在)
- [检查邮箱是否存在](#5-检查邮箱是否存在)

### 3. [用户管理接口 (/api/user)](#用户管理接口-apiuser)
- [根据用户ID获取用户信息](#1-根据用户id获取用户信息)
- [更新用户信息](#2-更新用户信息)
- [获取用户个人资料](#3-获取用户个人资料)
- [获取用户列表](#4-获取用户列表)
- [删除用户](#5-删除用户)

### 4. [问卷管理接口 (/api/questionnaire)](#问卷管理接口-apiquestionnaire)
- [根据问卷ID获取问卷信息](#1-根据问卷id获取问卷信息)
- [根据创建者ID查找问卷](#2-根据创建者id查找问卷)
- [获取已发布的问卷列表](#3-获取已发布的问卷列表)
- [获取所有问卷列表](#4-获取所有问卷列表)
- [搜索问卷](#5-搜索问卷)
- [创建问卷](#6-创建问卷)
- [更新问卷](#7-更新问卷)
- [删除问卷](#8-删除问卷)
- [发布问卷](#9-发布问卷)
- [取消发布问卷](#10-取消发布问卷)
- [获取问卷详情](#11-获取问卷详情)

### 5. [题目管理接口 (/api/question)](#题目管理接口-apiquestion)
- [获取所有题目](#1-获取所有题目)
- [根据问卷ID获取题目列表](#2-根据问卷id获取题目列表)
- [根据维度获取题目](#3-根据维度获取题目)
- [获取题目详情](#4-获取题目详情)
- [创建题目](#5-创建题目)
- [批量创建题目](#6-批量创建题目)
- [更新题目](#7-更新题目)
- [删除题目](#8-删除题目)
- [统计问卷题目数量](#9-统计问卷题目数量)

### 6. [测试相关接口 (/api/test)](#测试相关接口-apitest)
- [根据测试ID获取测试记录](#1-根据测试id获取测试记录)
- [获取所有测试记录](#2-获取所有测试记录)
- [提交测试答案](#3-提交测试答案)
- [检查用户是否已完成测试](#4-检查用户是否已完成测试)
- [获取测试统计信息](#5-获取测试统计信息)

### 7. [错误处理](#错误处理)
- 错误响应格式
- HTTP状态码说明

### 8. [认证说明](#认证说明)
- Session-Cookie认证机制

### 9. [注意事项](#注意事项)
- 请求头设置
- 权限限制
- 数据格式要求

### 10. [附录](#附录)
- [未实现的功能路径](#未实现的功能路径)
- [API响应格式说明](#api响应格式说明)
- [特殊响应说明](#特殊响应说明)

---

## 基础信息

- **基础URL**: `http://localhost:8080/api`
- **内容类型**: `application/json`
- **字符编码**: `UTF-8`

## 通用响应格式

所有API响应都使用以下统一格式：

```json
{
    "success": true,
    "message": "操作成功",
    "data": {
        // 具体的响应数据
    },
    "timestamp": "2023-xx-xx xx:xx:xx"
}
```

## 认证相关接口 (/api/auth)

### 1. 用户登录
- **URL**: `/api/auth/login`
- **方法**: `POST`
- **描述**: 用户登录系统

**请求体**:
```json
{
    "username": "string",    // 用户名，必填
    "password": "string"     // 密码，必填
}
```

**响应示例**:
```json
{
    "success": true,
    "message": "登录成功",
    "data": {
        "sessionId": "string",
        "message": "登录成功",
        "user": {
            "userId": 1,
            "username": "testuser",
            "email": "test@example.com",
            "role": "USER",
            "createdAt": "2023-xx-xx xx:xx:xx"
        },
        "success": true
    }
}
```

### 2. 用户注册
- **URL**: `/api/auth/register`
- **方法**: `POST`
- **描述**: 注册新用户

**请求体**:
```json
{
    "username": "string",    // 用户名，必填，唯一
    "password": "string",    // 密码，必填
    "email": "string"        // 邮箱，必填，唯一，需符合邮箱格式
}
```

**响应示例**:
```json
{
    "success": true,
    "message": "注册成功",
    "data": {
        "message": "注册成功",
        "user": {
            "userId": 1,
            "username": "testuser",
            "email": "test@example.com",
            "role": "USER",
            "createdAt": "2023-xx-xx xx:xx:xx"
        },
        "success": true
    }
}
```

### 3. 用户登出
- **URL**: `/api/auth/logout`
- **方法**: `POST`
- **描述**: 用户登出系统
- **需要认证**: 是

**请求体**: 无

**响应示例**:
```json
{
    "success": true,
    "message": "登出成功"
}
```

### 4. 检查用户名是否存在
- **URL**: `/api/auth/checkUsername`
- **方法**: `POST`
- **描述**: 检查用户名是否已存在

**请求体**:
```json
{
    "username": "string"     // 要检查的用户名
}
```

**响应示例**:
```json
{
    "success": true,
    "message": "成功查到用户名",
    "data": {
        "exists": false      // false表示不存在（可用），true表示已存在（不可用）
    }
}
```

### 5. 检查邮箱是否存在
- **URL**: `/api/auth/checkEmail`
- **方法**: `POST`
- **描述**: 检查邮箱是否已存在

**请求体**:
```json
{
    "email": "string"        // 要检查的邮箱
}
```

**响应示例**:
```json
{
    "success": true,
    "message": "成功查到邮箱",
    "data": {
        "exists": false      // false表示不存在（可用），true表示已存在（不可用）
    }
}
```

## 用户管理接口 (/api/user)

### 1. 根据用户ID获取用户信息
- **URL**: `/api/user/{userId}`
- **方法**: `GET`
- **描述**: 根据用户ID获取用户基本信息

**路径参数**:
- `userId`: 用户ID (整数)

**响应示例**:
```json
{
    "success": true,
    "message": "成功获取用户信息",
    "data": {
        "userId": 1,
        "username": "testuser",
        "email": "test@example.com",
        "role": "USER",
        "createdAt": "2023-xx-xx xx:xx:xx"
    }
}
```

### 2. 更新用户信息
- **URL**: `/api/user`
- **方法**: `POST`
- **描述**: 更新当前登录用户的个人信息（包括邮箱和密码）
- **需要认证**: 是

**请求体**:
```json
{
    "email": "newemail@example.com",     // 新邮箱，可选
    "currentPassword": "currentPass",    // 当前密码，修改密码时必填
    "newPassword": "newPassword"         // 新密码，可选
}
```

**响应示例**:
```json
{
    "success": true,
    "message": "更新成功",
    "data": {
        "userId": 1,
        "username": "testuser",
        "email": "newemail@example.com",
        "role": "USER",
        "createdAt": "2023-xx-xx xx:xx:xx"
    }
}
```

### 3. 获取用户个人资料
- **URL**: `/api/user/profile`
- **方法**: `POST`
- **描述**: 获取当前登录用户的个人资料
- **需要认证**: 是

**请求体**: 无

### 4. 获取用户列表
- **URL**: `/api/user/list`
- **方法**: `GET`
- **描述**: 获取所有用户列表
- **需要认证**: 是 (管理员)

**响应示例**:
```json
{
    "success": true,
    "message": "获取用户列表成功",
    "data": [
        {
            "userId": 1,
            "username": "testuser",
            "email": "test@example.com",
            "role": "USER",
            "createdAt": "2023-xx-xx xx:xx:xx"
        }
    ]
}
```

### 5. 删除用户
- **URL**: `/api/user`
- **方法**: `DELETE`
- **描述**: 删除指定用户（级联删除相关答题记录）
- **需要认证**: 是 (管理员)

**请求体**:
```json
{
    "deleteUserId": 1        // 要删除的用户ID
}
```

## 问卷管理接口 (/api/questionnaire)

### 1. 根据问卷ID获取问卷信息
- **URL**: `/api/questionnaire/{questionnaireId}`
- **方法**: `GET`
- **描述**: 根据问卷ID获取问卷基本信息（不含题目）

**路径参数**:
- `questionnaireId`: 问卷ID (整数)

**响应示例**:
```json
{
    "success": true,
    "message": "成功获取问卷",
    "data": {
        "questionnaireId": 1,
        "title": "MBTI人格测试",
        "description": "经典的16型人格测试",
        "creatorId": 1,
        "creatorName": "admin",
        "createdAt": "2023-xx-xx xx:xx:xx",
        "isPublished": true,
        "questionCount": 60
    }
}
```

### 2. 根据创建者ID查找问卷
- **URL**: `/api/questionnaire/byCreator`
- **方法**: `POST`
- **描述**: 根据创建者ID查找其创建的所有问卷

**请求体**:
```json
{
    "creatorId": 1           // 创建者用户ID
}
```

### 3. 获取已发布的问卷列表
- **URL**: `/api/questionnaire/published`
- **方法**: `GET`
- **描述**: 获取所有已发布的问卷列表

### 4. 获取所有问卷列表
- **URL**: `/api/questionnaire/all`
- **方法**: `GET`
- **描述**: 获取所有问卷列表（包括未发布）
- **需要认证**: 是 (管理员)

### 5. 搜索问卷
- **URL**: `/api/questionnaire/search`
- **方法**: `POST`
- **描述**: 根据标题模糊搜索问卷

**请求体**:
```json
{
    "title": "string"        // 搜索关键词
}
```

### 6. 创建问卷
- **URL**: `/api/questionnaire`
- **方法**: `POST`
- **描述**: 创建新问卷
- **需要认证**: 是

**请求体**:
```json
{
    "title": "string",       // 问卷标题，必填
    "description": "string", // 问卷描述，可选
    "questions": [           // 问题列表，可选
        {
            "questionText": "string",
            "questionType": "SINGLE_CHOICE",
            "options": [
                {
                    "optionText": "string",
                    "optionValue": "string"
                }
            ]
        }
    ]
}
```

### 7. 更新问卷
- **URL**: `/api/questionnaire`
- **方法**: `PUT`
- **描述**: 更新问卷信息
- **需要认证**: 是

**请求体**:
```json
{
    "questionnaireId": 1,    // 问卷ID，必填
    "title": "string",       // 新标题
    "description": "string"  // 新描述
}
```

### 8. 删除问卷
- **URL**: `/api/questionnaire`
- **方法**: `DELETE`
- **描述**: 删除问卷
- **需要认证**: 是

**请求体**:
```json
{
    "questionnaireId": 1     // 要删除的问卷ID
}
```

### 9. 发布问卷
- **URL**: `/api/questionnaire/publish`
- **方法**: `POST`
- **描述**: 发布问卷，使其对公众可见
- **需要认证**: 是

**请求体**:
```json
{
    "questionnaireId": 1     // 要发布的问卷ID
}
```

### 10. 取消发布问卷
- **URL**: `/api/questionnaire/unpublish`
- **方法**: `POST`
- **描述**: 取消发布问卷
- **需要认证**: 是

**请求体**:
```json
{
    "questionnaireId": 1     // 要取消发布的问卷ID
}
```

### 11. 获取问卷详情
- **URL**: `/api/questionnaire/detail`
- **方法**: `POST`
- **描述**: 获取问卷详情（包含所有题目）

**请求体**:
```json
{
    "questionnaireId": 1     // 问卷ID
}
```

**响应示例**:
```json
{
    "success": true,
    "message": "获取问卷详情成功",
    "data": {
        "questionnaireId": 1,
        "title": "MBTI人格测试",
        "description": "经典的16型人格测试",
        "creatorId": 1,
        "creatorName": "admin",
        "createdAt": "2023-xx-xx xx:xx:xx",
        "isPublished": true,
        "questionCount": 60,
        "questions": [
            {
                "questionId": 1,
                "questionText": "你更喜欢",
                "questionType": "SINGLE_CHOICE",
                "options": [
                    {
                        "optionId": 1,
                        "optionText": "独处",
                        "optionValue": "I"
                    },
                    {
                        "optionId": 2,
                        "optionText": "与他人交往",
                        "optionValue": "E"
                    }
                ]
            }
        ]
    }
}
```

## 题目管理接口 (/api/question)

### 1. 获取所有题目
- **URL**: `/api/question/all`
- **方法**: `GET`
- **描述**: 获取所有题目列表
- **需要认证**: 是 (管理员)

### 2. 根据问卷ID获取题目列表
- **URL**: `/api/question/byQuestionnaire`
- **方法**: `POST`
- **描述**: 获取指定问卷的所有题目

**请求体**:
```json
{
    "questionnaireId": 1     // 问卷ID
}
```

### 3. 根据维度获取题目
- **URL**: `/api/question/byDimension`
- **方法**: `POST`
- **描述**: 根据MBTI维度获取题目

**请求体**:
```json
{
    "dimension": "E_I"       // MBTI维度，可选值：E_I, S_N, T_F, J_P
}
```

### 4. 获取题目详情
- **URL**: `/api/question/detail`
- **方法**: `POST`
- **描述**: 获取题目详细信息

**请求体**:
```json
{
    "questionId": 1          // 题目ID
}
```

### 5. 创建题目
- **URL**: `/api/question`
- **方法**: `POST`
- **描述**: 为问卷创建新题目
- **需要认证**: 是

**请求体**:
```json
{
    "questionnaireId": 1,            // 所属问卷ID，必填
    "questionText": "string",        // 题目文本，必填
    "questionType": "SINGLE_CHOICE", // 题目类型，必填，可选值：SINGLE_CHOICE, MULTIPLE_CHOICE
    "options": [                     // 选项列表，必填
        {
            "optionText": "string",  // 选项文本
            "optionValue": "string"  // 选项值（用于计算）
        }
    ]
}
```

### 6. 批量创建题目
- **URL**: `/api/question/batch`
- **方法**: `POST`
- **描述**: 批量创建题目
- **需要认证**: 是

**请求体**:
```json
{
    "questionnaireId": 1,    // 问卷ID，必填
    "questions": [           // 题目列表，必填
        {
            "questionText": "string",
            "questionType": "SINGLE_CHOICE",
            "options": [
                {
                    "optionText": "string",
                    "optionValue": "string"
                }
            ]
        }
    ]
}
```

### 7. 更新题目
- **URL**: `/api/question`
- **方法**: `PUT`
- **描述**: 更新题目信息
- **需要认证**: 是

**请求体**:
```json
{
    "questionId": 1,                 // 题目ID，必填
    "questionText": "string",        // 新题目文本
    "questionType": "SINGLE_CHOICE", // 新题目类型
    "options": [                     // 新选项列表
        {
            "optionText": "string",
            "optionValue": "string"
        }
    ]
}
```

### 8. 删除题目
- **URL**: `/api/question`
- **方法**: `DELETE`
- **描述**: 删除题目
- **需要认证**: 是

**请求体**:
```json
{
    "questionId": 1          // 要删除的题目ID
}
```

### 9. 统计问卷题目数量
- **URL**: `/api/question/count`
- **方法**: `POST`
- **描述**: 统计指定问卷的题目数量

**请求体**:
```json
{
    "questionnaireId": 1     // 问卷ID
}
```

**响应示例**:
```json
{
    "success": true,
    "message": "统计题目数量成功",
    "data": 60
}
```

## 测试相关接口 (/api/test)

### 1. 根据测试ID获取测试记录
- **URL**: `/api/test/{testId}`
- **方法**: `GET`
- **描述**: 根据测试ID获取测试记录

**路径参数**:
- `testId`: 测试ID (整数)

### 2. 获取所有测试记录
- **URL**: `/api/test/all`
- **方法**: `GET`
- **描述**: 获取所有测试记录
- **需要认证**: 是 (管理员)

### 3. 提交测试答案
- **URL**: `/api/test`
- **方法**: `POST`
- **描述**: 提交测试答案并获取结果
- **需要认证**: 是

**请求体**:
```json
{
    "questionnaireId": 1,    // 问卷ID，必填
    "answerDetails": [       // 答案详情列表，必填
        {
            "questionId": 1, // 题目ID
            "optionId": 1,   // 选择的选项ID
            "selectedOption": "string" // 选项值（可选）
        }
    ]
}
```

**响应示例**:
```json
{
    "success": true,
    "message": "测试提交成功",
    "data": {
        "testId": 1,
        "questionnaireId": 1,
        "userId": 1,
        "submittedAt": "2023-xx-xx xx:xx:xx",
        "result": "INTJ",
        "resultDescription": "建筑师型人格",
        "personalityProbabilities": {
            "E": 0.25,
            "I": 0.75,
            "S": 0.30,
            "N": 0.70,
            "T": 0.80,
            "F": 0.20,
            "J": 0.85,
            "P": 0.15
        }
    }
}
```

### 4. 检查用户是否已完成测试
- **URL**: `/api/test/completed`
- **方法**: `POST`
- **描述**: 检查用户是否已完成指定问卷的测试
- **需要认证**: 是

**请求体**:
```json
{
    "questionnaireId": 1     // 问卷ID
}
```

**响应示例**:
```json
{
    "success": true,
    "message": "检查用户完成状态成功",
    "data": {
        "completed": true
    }
}
```

### 5. 获取测试统计信息
- **URL**: `/api/test/statistics`
- **方法**: `POST`
- **描述**: 获取问卷的测试统计信息
- **需要认证**: 是 (管理员)

**请求体**:
```json
{
    "questionnaireId": 1     // 问卷ID
}
```

**响应示例**:
```json
{
    "success": true,
    "message": "获取统计信息成功",
    "data": {
        "questionnaireId": 1,
        "totalTests": 100,
        "resultDistribution": {
            "INTJ": 15,
            "INFJ": 10,
            "ISTJ": 20,
            // ... 其他类型
        }
    }
}
```

## 错误处理

当API调用出现错误时，将返回如下格式的错误响应：

```json
{
    "success": false,
    "message": "错误描述",
    "error": "详细错误信息",
    "timestamp": "2023-xx-xx xx:xx:xx"
}
```

常见HTTP状态码：
- `200` - 请求成功
- `400` - 请求参数错误
- `401` - 未认证或认证失败
- `403` - 权限不足
- `404` - 资源不存在
- `500` - 服务器内部错误

## 认证说明

系统使用Session-Cookie方式进行身份认证：
1. 用户登录后，服务器会创建Session并返回SessionId
2. 后续请求需要携带SessionId Cookie
3. 部分接口需要管理员权限，普通用户访问会返回403错误

## 注意事项

1. 所有POST、PUT、DELETE请求都需要在请求头中设置 `Content-Type: application/json`
2. 管理员不能删除其他管理员账户，除非先撤销其管理员身份
3. 删除用户会级联删除该用户的所有答题记录
4. 问卷发布后才能被普通用户访问和测试
5. 所有时间格式均为：`yyyy-MM-dd HH:mm:ss`

## 附录

### 未实现的功能路径

以下功能在当前实现中可能存在限制或未完全实现：

1. **创建用户接口**: 目前 `POST /api/user` 只用于更新当前登录用户信息，没有管理员创建新用户的独立接口
2. **用户角色管理**: 没有单独的接口来修改用户角色
3. **批量操作**: 大部分接口都是单个操作，没有批量处理功能
4. **分页查询**: 用户列表等查询接口没有实现分页功能

## API响应格式说明

### 成功响应格式
```json
{
    "success": true,
    "message": "操作成功的描述信息",
    "data": {}, // 或 null，具体数据
    "timestamp": "2023-xx-xx xx:xx:xx"
}
```

### 错误响应格式
```json
{
    "success": false,
    "message": "错误描述",
    "error": "详细错误信息",
    "timestamp": "2023-xx-xx xx:xx:xx"
}
```

### 特殊响应说明

1. **checkUsername/checkEmail**: 返回 `exists` 字段，`true` 表示已存在（不可用），`false` 表示不存在（可用）
2. **测试提交**: 返回完整的测试结果，包括MBTI类型和各维度概率
3. **问卷查询**: 根据不同端点返回简化版本或完整版本（含题目）
4. **用户更新**: 支持单独更新邮箱或密码，也可同时更新

---

*文档最后更新时间: 2025年7月20日*
