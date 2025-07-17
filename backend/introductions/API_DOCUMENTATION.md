# MBTI系统后端API接口文档

## 项目架构说明

### 技术栈
- **后端框架：** 传统JavaWeb (Servlet + JDBC + Tomcat)
- **数据库：** MySQL 8.0+
- **构建工具：** Maven
- **服务器：** Apache Tomcat 10.1.15
- **Java版本：** Java 21

### 部署信息
- **WAR包名称：** `mbti-system.war`
- **项目上下文路径：** `/mbti-system`
- **API基础路径：** `/mbti-system/api`
- **完整基础URL：** `http://localhost:8080/mbti-system/api`

### CORS (跨域资源共享) 配置
本系统已配置全局CORS策略，支持跨域请求：

- **允许的域名模式：** `*` (开发环境) / 指定域名 (生产环境)
- **允许的HTTP方法：** GET, POST, PUT, DELETE, OPTIONS, HEAD, PATCH
- **允许的请求头：** Authorization, Content-Type, X-Requested-With, Accept, Origin 等
- **支持凭据传递：** 是 (allowCredentials: true)
- **预检请求缓存：** 3600秒

**前端请求配置示例：**
```javascript
// Axios配置
axios.defaults.withCredentials = true;
axios.defaults.headers.common['Content-Type'] = 'application/json';
axios.defaults.baseURL = 'http://localhost:8080/mbti-system/api';

// Fetch配置
fetch('http://localhost:8080/mbti-system/api/auth/login', {
    method: 'POST',
    credentials: 'include',
    headers: {
        'Content-Type': 'application/json'
    },
    body: JSON.stringify(data)
});
```

## 目录
- [1. 认证接口](#1-认证接口)
- [2. 用户管理接口](#2-用户管理接口)
- [3. 问卷管理接口](#3-问卷管理接口)
- [4. 问题管理接口](#4-问题管理接口)
- [5. 测试接口](#5-测试接口)
- [6. 统一响应格式](#6-统一响应格式)
- [7. 错误码说明](#7-错误码说明)
- [8. Session认证说明](#8-session认证说明)
- [9. 示例代码](#9-示例代码)
- [10. 常见问题排查](#10-常见问题排查)

---

## 1. 认证接口

### 1.1 用户登录

**接口地址：** `POST /mbti-system/api/auth/login`

**接口说明：** 用户登录，创建Session会话

**请求头：**
```
Content-Type: application/json
```

**请求体：**
```json
{
    "username": "string",   // 用户名，3-20字符，必填
    "password": "string"    // 密码，6位以上，必填
}
```

**请求示例：**
```json
{
    "username": "testuser",
    "password": "123456"
}
```

**响应体：**
```json
{
    "success": true,
    "message": "登录成功",
    "data": {
        "user": {
            "userId": 1,
            "username": "testuser",
            "email": "test@example.com",
            "role": "USER",
            "createdAt": "2025-07-01T08:00:00"
        },
        "sessionId": "A1B2C3D4E5F6G7H8I9J0"
    },
    "timestamp": 1720762800000
}
```

**响应字段说明：**
- `user`: 用户信息对象
- `sessionId`: 会话ID，用于后续请求验证

---

### 1.2 用户注册

**接口地址：** `POST /mbti-system/api/auth/register`

**接口说明：** 用户注册新账户

**请求头：**
```
Content-Type: application/json
```

**请求体：**
```json
{
    "username": "string",   // 用户名，3-20字符，必填
    "password": "string",   // 密码，6位以上，必填
    "email": "string"       // 邮箱，必填，格式校验
}
```

**请求示例：**
```json
{
    "username": "newuser",
    "password": "123456",
    "email": "newuser@example.com"
}
```

**响应体：**
```json
{
    "success": true,
    "message": "注册成功",
    "data": {
        "userId": 2,
        "username": "newuser",
        "email": "newuser@example.com",
        "role": "USER",
        "createdAt": "2025-07-02T10:30:00"
    },
    "timestamp": 1720762800000
}
```

---

### 1.3 用户注销

**接口地址：** `POST /mbti-system/api/auth/logout`

**接口说明：** 用户注销，销毁Session

**请求头：**
```
Content-Type: application/json
Cookie: JSESSIONID=xxxxx
```

**响应体：**
```json
{
    "success": true,
    "message": "注销成功",
    "data": "注销成功",
    "timestamp": 1720762800000
}
```

---

### 1.4 检查用户名是否存在

**接口地址：** `GET /mbti-system/api/auth/checkUsername?username={username}`

**接口说明：** 检查用户名是否已被注册

**请求参数：**
- `username`: 要检查的用户名

**响应体：**
```json
{
    "success": true,
    "data": {
        "exists": false,
        "username": "testuser"
    },
    "timestamp": 1720762800000
}
```

---

### 1.5 检查邮箱是否存在

**接口地址：** `GET /mbti-system/api/auth/checkEmail?email={email}`

**接口说明：** 检查邮箱是否已被注册

**请求参数：**
- `email`: 要检查的邮箱

**响应体：**
```json
{
    "success": true,
    "data": {
        "exists": false,
        "email": "test@example.com"
    },
    "timestamp": 1720762800000
}
```

---

## 2. 用户管理接口

### 2.1 获取当前用户信息

**接口地址：** `GET /mbti-system/api/user`

**接口说明：** 获取当前登录用户的详细信息

**请求头：**
```
Cookie: JSESSIONID=xxxxx
```

**响应体：**
```json
{
    "success": true,
    "message": "获取用户信息成功",
    "data": {
        "userId": 1,
        "username": "testuser",
        "email": "test@example.com",
        "role": "USER",
        "createdAt": "2025-07-01T08:00:00"
    },
    "timestamp": 1720762800000
}
```

---

### 2.2 更新当前用户信息

**接口地址：** `POST /mbti-system/api/user`

**接口说明：** 更新当前登录用户的信息

**请求头：**
```
Content-Type: application/json
Cookie: JSESSIONID=xxxxx
```

**请求体：**
```json
{
    "userId": "1",      // 必须选项
    "email": "newemail@example.com",  // 新邮箱地址，可选其他属性，除了密码
    "operationType": "UPDATE"
}
```

**响应体：**
```json
{
    "success": true,
    "message": "更新用户信息成功",
    "data": {
        "userId": 1,
        "username": "testuser",
        "email": "newemail@example.com",
        "role": "USER",
        "createdAt": "2025-07-01T08:00:00"
    },
    "timestamp": 1720762800000
}
```

---

### 2.3 修改密码

**接口地址：** `POST /mbti-system/api/user`

**接口说明：** 修改当前用户密码

**请求头：**
```
Content-Type: application/json
Cookie: JSESSIONID=xxxxx
```

**请求体：**
```json
{
    "userId": 1,
    "currentPassword": "123456",      // 旧密码
    "newPassword": "newpass123",      // 新密码
    "operationType": "UPDATE"
}
```

**响应体：**
```json
{
    "success": true,
    "message": "密码修改成功",
    "data": "密码修改成功",
    "timestamp": 1720762800000
}
```

---

### 2.4 根据ID获取用户详情

**接口地址：** `POST /mbti-system/api/user/profile?userId={userId}`

**接口说明：** 获取用户详情信息

**请求头：**
```
Content-Type: application/json
```

**响应体：**
```json
{
    "success": true,
    "message": null,
    "data": {
        "userId": 1,
        "username": "admin",
        "email": null,
        "role": "ADMIN"
    },
    "timestamp": 1752769103745
}
```

---

### 2.5 获取用户列表（管理员）

**接口地址：** `GET /mbti-system/api/user/list`

**接口说明：** 获取所有用户列表（仅管理员可访问）

**权限要求：** ADMIN

**请求头：**
```
Cookie: JSESSIONID=xxxxx
```

**响应体：**
```json
{
    "success": true,
    "message": "获取用户列表成功",
    "data": [
        {
            "userId": 1,
            "username": "admin",
            "email": "admin@example.com",
            "role": "ADMIN",
            "createdAt": "2025-07-01T08:00:00"
        },
        {
            "userId": 2,
            "username": "testuser",
            "email": "test@example.com",
            "role": "USER",
            "createdAt": "2025-07-01T09:00:00"
        }
    ],
    "timestamp": 1720762800000
}
```

---

### 2.5 删除用户（管理员）

**接口地址：** `DELETE /mbti-system/api/user`

**接口说明：** 删除指定用户（仅管理员可访问）

**权限要求：** ADMIN

**请求头：**
```
Content-Type: application/json
Cookie: JSESSIONID=xxxxx
```

**请求体：**
```json
{
    "userId": 5,
    "deleteUserId": 9,
    "operationType": "DELETE"
}
```

**响应体：**
```json
{
    "success": true,
    "message": "用户删除成功",
    "data": "用户 testuser 已被删除",
    "timestamp": 1752771596371
}
```

---

## 3. 问卷管理接口

### 3.1 获取问卷列表

**接口地址：** `GET /mbti-system/api/questionnaire`

**接口说明：** 获取问卷列表

**请求头：**
```
Cookie: JSESSIONID=xxxxx
```

**响应体：**
```json
{
    "success": true,
    "data": [
        {
            "questionnaireId": 1,
            "title": "MBTI性格测试",
            "description": "经典的MBTI性格类型测试",
            "creatorId": 1,
            "createdAt": "2025-07-01T08:00:00",
            "isPublished": true
        }
    ],
    "timestamp": 1720762800000
}
```

---

### 3.2 获取问卷详情

**接口地址：** `GET /mbti-system/api/questionnaire/{id}`

**接口说明：** 获取指定问卷的详细信息

**请求头：**
```
Cookie: JSESSIONID=xxxxx
```

**URL参数：**
- `id`: 问卷ID

**响应体：**
```json
{
    "success": true,
    "data": {
        "questionnaireId": 1,
        "title": "MBTI性格测试",
        "description": "经典的MBTI性格类型测试",
        "creatorId": 1,
        "createdAt": "2025-07-01T08:00:00",
        "isPublished": true,
        "questions": [
            {
                "questionId": 1,
                "content": "你更喜欢与人交往还是独自思考？",
                "dimension": "EI",
                "questionOrder": 1,
                "options": [
                    {
                        "optionId": 1,
                        "content": "更喜欢与人交往",
                        "scoreValue": "1"
                    },
                    {
                        "optionId": 2,
                        "content": "更喜欢独自思考",
                        "scoreValue": "-1"
                    }
                ]
            }
        ]
    },
    "timestamp": 1720762800000
}
```

---

### 3.3 创建问卷（管理员）

**接口地址：** `POST /mbti-system/api/questionnaire`

**接口说明：** 创建新问卷（仅管理员可访问）

**权限要求：** ADMIN

**请求头：**
```
Content-Type: application/json
Cookie: JSESSIONID=xxxxx
```

**请求体：**
```json
{
    "title": "新的MBTI测试",
    "description": "全新设计的MBTI性格测试问卷",
    "operationType": "CREATE"
}
```

**响应体：**
```json
{
    "success": true,
    "message": "创建问卷成功",
    "data": {
        "questionnaireId": 2,
        "title": "新的MBTI测试",
        "description": "全新设计的MBTI性格测试问卷",
        "creatorId": 1,
        "createdAt": "2025-07-02T10:30:00",
        "isPublished": false
    },
    "timestamp": 1720762800000
}
```

---

### 3.4 更新问卷（管理员）

**接口地址：** `PUT /mbti-system/api/questionnaire`

**接口说明：** 更新问卷信息（仅管理员可访问）

**权限要求：** ADMIN

**请求头：**
```
Content-Type: application/json
Cookie: JSESSIONID=xxxxx
```

**请求体：**
```json
{
    "questionnaireId": 1,
    "title": "更新后的MBTI测试",
    "description": "更新后的描述",
    "isPublished": true,
    "operationType": "UPDATE"
}
```

**响应体：**
```json
{
    "success": true,
    "message": "更新问卷成功",
    "data": {
        "questionnaireId": 1,
        "title": "更新后的MBTI测试",
        "description": "更新后的描述",
        "creatorId": 1,
        "createdAt": "2025-07-01T08:00:00",
        "isPublished": true
    },
    "timestamp": 1720762800000
}
```

---

### 3.5 删除问卷（管理员）

**接口地址：** `DELETE /mbti-system/api/questionnaire`

**接口说明：** 删除问卷（仅管理员可访问）

**权限要求：** ADMIN

**请求头：**
```
Content-Type: application/json
Cookie: JSESSIONID=xxxxx
```

**请求体：**
```json
{
    "questionnaireId": 1,
    "operationType": "DELETE"
}
```

**响应体：**
```json
{
    "success": true,
    "message": "删除问卷成功",
    "data": "删除问卷成功",
    "timestamp": 1720762800000
}
```

---

## 4. 问题管理接口

### 4.1 获取问卷的所有问题

**接口地址：** `GET /mbti-system/api/question/{questionnaireId}`

**接口说明：** 获取指定问卷的所有问题和选项

**请求头：**
```
Cookie: JSESSIONID=xxxxx
```

**URL参数：**
- `questionnaireId`: 问卷ID

**响应体：**
```json
{
    "success": true,
    "data": [
        {
            "questionId": 1,
            "questionnaireId": 1,
            "content": "你更喜欢与人交往还是独自思考？",
            "dimension": "EI",
            "questionOrder": 1,
            "options": [
                {
                    "optionId": 1,
                    "questionId": 1,
                    "content": "更喜欢与人交往",
                    "scoreValue": "1"
                },
                {
                    "optionId": 2,
                    "questionId": 1,
                    "content": "更喜欢独自思考",
                    "scoreValue": "-1"
                }
            ]
        }
    ],
    "timestamp": 1720762800000
}
```

---

### 4.2 创建问题（管理员）

**接口地址：** `POST /mbti-system/api/question`

**接口说明：** 为指定问卷创建新问题（仅管理员可访问）

**权限要求：** ADMIN

**请求头：**
```
Content-Type: application/json
Cookie: JSESSIONID=xxxxx
```

**请求体：**
```json
{
    "questionnaireId": 1,
    "content": "新的问题内容",
    "dimension": "EI",
    "questionOrder": 5,
    "options": [
        {
            "content": "选项A",
            "scoreValue": "1"
        },
        {
            "content": "选项B",
            "scoreValue": "-1"
        }
    ],
    "operationType": "CREATE"
}
```

**响应体：**
```json
{
    "success": true,
    "message": "创建问题成功",
    "data": {
        "questionId": 5,
        "questionnaireId": 1,
        "content": "新的问题内容",
        "dimension": "EI",
        "questionOrder": 5,
        "options": [
            {
                "optionId": 9,
                "questionId": 5,
                "content": "选项A",
                "scoreValue": "1"
            },
            {
                "optionId": 10,
                "questionId": 5,
                "content": "选项B",
                "scoreValue": "-1"
            }
        ]
    },
    "timestamp": 1720762800000
}
```

---

### 4.3 更新问题（管理员）

**接口地址：** `PUT /mbti-system/api/question`

**接口说明：** 更新指定问题（仅管理员可访问）

**权限要求：** ADMIN

**请求头：**
```
Content-Type: application/json
Cookie: JSESSIONID=xxxxx
```

**请求体：**
```json
{
    "questionId": 1,
    "content": "更新后的问题内容",
    "dimension": "EI",
    "questionOrder": 3,
    "options": [
        {
            "optionId": 1,
            "content": "更新后的选项A",
            "scoreValue": "2"
        },
        {
            "optionId": 2,
            "content": "更新后的选项B",
            "scoreValue": "-2"
        }
    ],
    "operationType": "UPDATE"
}
```

**响应体：**
```json
{
    "success": true,
    "message": "更新问题成功",
    "data": {
        "questionId": 1,
        "content": "更新后的问题内容",
        "dimension": "EI",
        "questionOrder": 3,
        "options": [
            {
                "optionId": 1,
                "content": "更新后的选项A",
                "scoreValue": "2"
            },
            {
                "optionId": 2,
                "content": "更新后的选项B",
                "scoreValue": "-2"
            }
        ]
    },
    "timestamp": 1720762800000
}
```

---

### 4.4 删除问题（管理员）

**接口地址：** `DELETE /mbti-system/api/question`

**接口说明：** 删除指定问题（仅管理员可访问）

**权限要求：** ADMIN

**请求头：**
```
Content-Type: application/json
Cookie: JSESSIONID=xxxxx
```

**请求体：**
```json
{
    "questionId": 1,
    "operationType": "DELETE"
}
```

**响应体：**
```json
{
    "success": true,
    "message": "删除问题成功",
    "data": "删除问题成功",
    "timestamp": 1720762800000
}
```

---

## 5. 测试接口

### 5.1 提交答案

**接口地址：** `POST /mbti-system/api/test`

**接口说明：** 提交问卷答案

**请求头：**
```
Content-Type: application/json
Cookie: JSESSIONID=xxxxx
```

**请求体：**
```json
{
    "questionnaireId": 1,
    "answerDetails": [
        {
            "questionId": 1,
            "optionId": 1
        },
        {
            "questionId": 2,
            "optionId": 4
        },
        {
            "questionId": 3,
            "optionId": 5
        }
    ],
    "operationType": "CREATE"
}
```

**响应体：**
```json
{
    "success": true,
    "message": "提交答案成功",
    "data": {
        "answerId": 123,
        "questionnaireId": 1,
        "userId": 1,
        "mbtiType": "ENFP",
        "dimensionScores": {
            "EI": 3,
            "SN": 1,
            "TF": -2,
            "JP": 2
        },
        "submittedAt": "2025-07-02T10:30:00"
    },
    "timestamp": 1720762800000
}
```

---

### 5.2 获取测试结果

**接口地址：** `GET /mbti-system/api/test`

**接口说明：** 获取当前用户的所有测试结果

**请求头：**
```
Cookie: JSESSIONID=xxxxx
```

**响应体：**
```json
{
    "success": true,
    "message": "获取测试结果成功",
    "data": [
        {
            "answerId": 123,
            "questionnaireId": 1,
            "userId": 1,
            "mbtiType": "ENFP",
            "submittedAt": "2025-07-02T10:30:00",
            "answerDetails": [
                {
                    "questionId": 1,
                    "optionId": 1,
                    "selectedOption": "更喜欢与人交往",
                    "score": 1
                }
            ]
        }
    ],
    "timestamp": 1720762800000
}
```

---

### 5.3 获取测试详情

**接口地址：** `GET /mbti-system/api/test/{answerId}`

**接口说明：** 获取指定测试的详细结果

**请求头：**
```
Cookie: JSESSIONID=xxxxx
```

**URL参数：**
- `answerId`: 答案ID

**响应体：**
```json
{
    "success": true,
    "message": "获取测试详情成功",
    "data": {
        "answerId": 123,
        "questionnaireId": 1,
        "userId": 1,
        "mbtiType": "ENFP",
        "dimensionScores": {
            "EI": 3,
            "SN": 1,
            "TF": -2,
            "JP": 2
        },
        "submittedAt": "2025-07-02T10:30:00",
        "answerDetails": [
            {
                "questionId": 1,
                "optionId": 1,
                "selectedOption": "更喜欢与人交往",
                "score": 1
            },
            {
                "questionId": 2,
                "optionId": 4,
                "selectedOption": "更注重细节",
                "score": -1
            }
        ]
    },
    "timestamp": 1720762800000
}
```

---

## 6. 统一响应格式

### 成功响应
```json
{
    "success": true,
    "message": "操作成功描述",    // 可选
    "data": {},                 // 响应数据，可能是对象、数组或基本类型
    "timestamp": 1720762800000  // 时间戳
}
```

### 失败响应
```json
{
    "success": false,
    "message": "错误描述",
    "timestamp": 1720762800000
}
```

### 系统错误响应
```json
{
    "success": false,
    "message": "系统错误",
    "data": {
        "errorType": "NullPointerException",
        "message": "详细错误信息",
        "statusCode": 500,
        "path": "/api/xxx"
    },
    "timestamp": 1720762800000
}
```

---

## 7. 错误码说明

### HTTP状态码
- `200`: 操作成功
- `400`: 请求参数错误
- `401`: 未认证或Session过期
- `403`: 权限不足
- `404`: 资源不存在或接口不存在
- `500`: 服务器内部错误

### 业务错误信息
- `用户名已存在`: 注册时用户名重复
- `邮箱已存在`: 注册时邮箱重复
- `用户名或密码错误`: 登录失败
- `用户未登录`: Session不存在或过期
- `权限不足`: 访问需要更高权限的接口
- `问卷不存在`: 访问不存在的问卷
- `您已经回答过这个问卷了`: 重复答题
- `问卷未发布，无法答题`: 访问未发布的问卷
- `Method Not Allowed`: HTTP方法不支持

---

## 8. Session认证说明

### Session会话管理
1. 登录成功后，服务器创建Session并返回sessionId
2. 浏览器自动保存Session Cookie (JSESSIONID)
3. 后续请求会自动携带Cookie进行身份验证
4. Session默认过期时间为1天（24小时）

### 权限级别
- **无权限**: 登录、注册、检查用户名/邮箱接口
- **USER**: 普通用户权限，可以答题、查看结果、管理个人信息
- **ADMIN**: 管理员权限，可以管理问卷、问题、查看所有用户

### Session使用示例
```javascript
// 登录后浏览器会自动保存Session Cookie
const loginResponse = await fetch('/mbti-system/api/auth/login', {
    method: 'POST',
    credentials: 'include',  // 重要：包含Cookie
    headers: {
        'Content-Type': 'application/json'
    },
    body: JSON.stringify({
        username: 'testuser',
        password: '123456'
    })
});

// 后续请求自动携带Session Cookie
const userInfo = await fetch('/mbti-system/api/user', {
    method: 'GET',
    credentials: 'include'  // 重要：包含Cookie
});
```

---

## 9. 示例代码

### JavaScript/Fetch示例
```javascript
// 基础配置
const API_BASE_URL = 'http://localhost:8080/mbti-system/api';

// 登录
const login = async (username, password) => {
    try {
        const response = await fetch(`${API_BASE_URL}/auth/login`, {
            method: 'POST',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                username,
                password
            })
        });
        
        const result = await response.json();
        if (result.success) {
            console.log('登录成功:', result.data);
            return result.data;
        } else {
            console.error('登录失败:', result.message);
            throw new Error(result.message);
        }
    } catch (error) {
        console.error('登录异常:', error);
        throw error;
    }
};

// 获取用户信息
const getUserProfile = async () => {
    try {
        const response = await fetch(`${API_BASE_URL}/user/get`, {
            method: 'GET',
            credentials: 'include'
        });
        
        const result = await response.json();
        if (result.success) {
            return result.data;
        } else {
            throw new Error(result.message);
        }
    } catch (error) {
        console.error('获取用户信息失败:', error);
        throw error;
    }
};

// 提交答案
const submitAnswers = async (questionnaireId, answerDetails) => {
    try {
        const response = await fetch(`${API_BASE_URL}/test/submit`, {
            method: 'POST',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                questionnaireId,
                answerDetails,
                operationType: 'CREATE'
            })
        });
        
        const result = await response.json();
        if (result.success) {
            return result.data;
        } else {
            throw new Error(result.message);
        }
    } catch (error) {
        console.error('提交答案失败:', error);
        throw error;
    }
};

// 获取问卷列表
const getQuestionnaires = async () => {
    try {
        const response = await fetch(`${API_BASE_URL}/questionnaire/get`, {
            method: 'GET',
            credentials: 'include'
        });
        
        const result = await response.json();
        if (result.success) {
            return result.data;
        } else {
            throw new Error(result.message);
        }
    } catch (error) {
        console.error('获取问卷列表失败:', error);
        throw error;
    }
};
```

### Axios示例
```javascript
// Axios全局配置
axios.defaults.baseURL = 'http://localhost:8080/mbti-system/api';
axios.defaults.withCredentials = true;
axios.defaults.headers.common['Content-Type'] = 'application/json';

// 响应拦截器
axios.interceptors.response.use(
    response => {
        const result = response.data;
        if (result.success) {
            return result;
        } else {
            throw new Error(result.message);
        }
    },
    error => {
        if (error.response && error.response.status === 401) {
            // Session过期，跳转到登录页
            window.location.href = '/login';
        }
        return Promise.reject(error);
    }
);

// 使用示例
const api = {
    // 登录
    login: (username, password) => {
        return axios.post('/auth/login', { username, password });
    },
    
    // 获取用户信息
    getUserProfile: () => {
        return axios.get('/user/get');
    },
    
    // 提交答案
    submitAnswers: (questionnaireId, answerDetails) => {
        return axios.post('/test/submit', {
            questionnaireId,
            answerDetails,
            operationType: 'CREATE'
        });
    },
    
    // 获取问卷列表
    getQuestionnaires: () => {
        return axios.get('/questionnaire/get');
    }
};
```

### cURL示例
```bash
# 登录
curl -X POST http://localhost:8080/mbti-system/api/auth/login \
  -H "Content-Type: application/json" \
  -c cookies.txt \
  -d '{"username":"testuser","password":"123456"}'

# 获取用户信息（使用保存的cookies）
curl -X GET http://localhost:8080/mbti-system/api/user \
  -b cookies.txt

# 提交答案
curl -X POST http://localhost:8080/mbti-system/api/test \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d '{
    "questionnaireId": 1,
    "answerDetails": [
      {"questionId": 1, "optionId": 1},
      {"questionId": 2, "optionId": 4}
    ],
    "operationType": "CREATE"
  }'

# 获取问卷列表
curl -X GET http://localhost:8080/mbti-system/api/questionnaire \
  -b cookies.txt
```

---

## 10. 常见问题排查

### 10.1 Session相关错误

**错误信息：** `用户未登录` 或 `401 Unauthorized`

**原因分析：**
1. 未登录或Session过期
2. 请求时未包含credentials
3. Cookie被浏览器阻止

**解决方案：**
```javascript
// ✅ 正确配置 - 必须包含credentials
fetch('/mbti-system/api/user', {
    method: 'GET',
    credentials: 'include'  // 关键配置
});

// ❌ 错误配置 - 缺少credentials
fetch('/mbti-system/api/user', {
    method: 'GET'
});
```

### 10.2 CORS跨域问题

**错误信息：** 
- `Access to fetch at '...' has been blocked by CORS policy`
- `No 'Access-Control-Allow-Origin' header is present`

**解决方案：**
1. 确保前端请求包含`credentials: 'include'`
2. 检查请求URL是否正确（包含完整路径）
3. 开发环境可以使用代理服务器

**正确的跨域请求配置：**
```javascript
// 前端代理配置（Vue.js为例）
// vite.config.js
export default {
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080/mbti-system',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '/api')
      }
    }
  }
}
```

### 10.3 URL路径错误

**错误信息：** `404 Not Found` 或 `控制器不存在`

**常见错误：**
```javascript
// ❌ 错误 - 缺少项目上下文路径
fetch('/api/auth/login', { ... });

// ✅ 正确 - 包含完整路径
fetch('/mbti-system/api/auth/login', { ... });

// 或者使用完整URL
fetch('http://localhost:8080/mbti-system/api/auth/login', { ... });
```

### 10.4 参数格式错误

**错误信息：** `400 Bad Request` 或 `参数不完整或格式错误`

**常见问题：**
1. 请求体不是有效JSON
2. 缺少必填字段
3. 字段类型不匹配

**解决方案：**
```javascript
// ✅ 正确格式
{
    "username": "testuser",      // 字符串
    "password": "123456",        // 字符串
    "questionnaireId": 1,        // 数字
    "operationType": "CREATE"    // 字符串
}

// ❌ 错误格式
{
    "username": null,            // 不能为null
    "password": "",              // 不能为空
    "questionnaireId": "1",      // 应该是数字
    "operationType": "create"    // 应该是大写CREATE
}
```

### 10.5 权限不足错误

**错误信息：** `403 Forbidden` 或 `权限不足`

**解决方案：**
1. 确认当前用户角色是否满足接口要求
2. 管理员接口需要ADMIN角色
3. 重新登录获取正确的权限

**权限检查：**
```javascript
// 检查用户角色
const userInfo = await getUserProfile();
if (userInfo.role !== 'ADMIN') {
    console.error('需要管理员权限');
    return;
}
```

### 10.6 Tomcat部署问题

**错误信息：** `404 Not Found` 或 连接拒绝

**解决方案：**
1. 确认Tomcat服务器已启动
2. 检查WAR包是否正确部署到webapps目录
3. 确认端口号是否正确（默认8080）
4. 检查防火墙设置

**部署检查：**
```bash
# 检查Tomcat进程
ps aux | grep tomcat

# 检查端口占用
netstat -an | grep 8080

# 检查WAR包部署
ls $TOMCAT_HOME/webapps/mbti-system/

# 查看Tomcat日志
tail -f $TOMCAT_HOME/logs/catalina.out
```

---

**文档版本**: v2.0  
**最后更新**: 2025年7月17日  
**适用版本**: 传统JavaWeb架构 + Session认证
