# MBTI系统后端API接口文档

## 基础配置说明

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

// Fetch配置
fetch(url, {
    method: 'POST',
    credentials: 'include',
    headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + token
    },
    body: JSON.stringify(data)
});
```

## 目录
- [1. 认证接口](#1-认证接口)
- [2. 用户管理接口](#2-用户管理接口)
- [3. 问卷管理接口](#3-问卷管理接口)
- [4. 测试接口](#4-测试接口)
- [5. 公共响应格式](#5-公共响应格式)
- [6. 错误码说明](#6-错误码说明)
- [7. 认证说明](#7-认证说明)
- [8. 示例代码](#8-示例代码)
- [9. 常见问题排查](#9-常见问题排查)
- [10. 测试工具推荐](#10-测试工具推荐)

---

## 1. 认证接口

### 1.1 用户登录

**接口地址：** `POST /api/auth/login`

**接口说明：** 用户登录获取JWT令牌

**请求头：**
```
Content-Type: application/json
```

**请求体：**
```json
{
    "username": "string",   // 用户名，3-50字符，必填
    "password": "string"    // 密码，6-100字符，必填
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
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userId": 1,
    "username": "testuser",
    "roles": ["ROLE_USER"]
}
```

**响应字段说明：**
- `token`: JWT访问令牌
- `userId`: 用户ID
- `username`: 用户名
- `roles`: 用户角色列表

---

### 1.2 用户注册

**接口地址：** `POST /api/auth/register`

**接口说明：** 用户注册新账户

**请求头：**
```
Content-Type: application/json
```

**请求体：**
```json
{
    "username": "string",   // 用户名，3-50字符，必填
    "password": "string",   // 密码，6-100字符，必填
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
    "data": {
        "userId": 2,
        "username": "newuser",
        "email": "newuser@example.com",
        "role": "USER",
        "createdAt": "2025-07-02T10:30:00"
    },
    "message": "注册成功"
}
```

---

## 2. 用户管理接口

### 2.1 获取当前用户信息

**接口地址：** `GET /api/users/profile`

**接口说明：** 获取当前登录用户的详细信息

**请求头：**
```
Authorization: Bearer {jwt_token}
```

**响应体：**
```json
{
    "success": true,
    "data": {
        "userId": 1,
        "username": "testuser",
        "email": "test@example.com",
        "role": "USER",
        "createdAt": "2025-07-01T08:00:00",
        "answerCount": 3
    },
    "message": "获取用户信息成功"
}
```

---

### 2.2 更新当前用户信息

**接口地址：** `PUT /api/users/profile`

**接口说明：** 更新当前登录用户的信息

**请求头：**
```
Authorization: Bearer {jwt_token}
Content-Type: application/json
```

**请求体：**
```json
{
    "email": "newemail@example.com"  // 新邮箱地址，可选
}
```

**响应体：**
```json
{
    "success": true,
    "data": {
        "userId": 1,
        "username": "testuser",
        "email": "newemail@example.com",
        "role": "USER",
        "createdAt": "2025-07-01T08:00:00",
        "answerCount": 3
    },
    "message": "更新用户信息成功"
}
```

---

### 2.3 获取用户列表（管理员）

**接口地址：** `GET /api/users`

**接口说明：** 获取所有用户列表（仅管理员可访问）

**权限要求：** ADMIN

**请求头：**
```
Authorization: Bearer {jwt_token}
```

**请求参数：**
- `page`: 页码，默认0
- `size`: 每页大小，默认10

**请求示例：**
```
GET /api/users?page=0&size=10
```

**响应体：**
```json
{
    "success": true,
    "data": {
        "content": [
            {
                "userId": 1,
                "username": "admin",
                "email": "admin@example.com",
                "role": "ADMIN",
                "createdAt": "2025-07-01T08:00:00",
                "answerCount": 0
            },
            {
                "userId": 2,
                "username": "testuser",
                "email": "test@example.com",
                "role": "USER",
                "createdAt": "2025-07-01T09:00:00",
                "answerCount": 3
            }
        ],
        "totalElements": 2,
        "totalPages": 1,
        "number": 0,
        "size": 10
    },
    "message": "获取用户列表成功"
}
```

---

### 2.4 删除用户（管理员）

**接口地址：** `DELETE /api/users/{id}`

**接口说明：** 删除指定用户（仅管理员可访问）

**权限要求：** ADMIN

**请求头：**
```
Authorization: Bearer {jwt_token}
```

**路径参数：**
- `id`: 用户ID

**响应体：**
```json
{
    "success": true,
    "message": "删除用户成功"
}
```

---

## 3. 问卷管理接口

### 3.1 获取问卷列表

**接口地址：** `GET /api/questionnaires`

**接口说明：** 获取问卷列表（管理员可见全部，普通用户只能看已发布的）

**请求头：**
```
Authorization: Bearer {jwt_token}
```

**请求参数：**
- `page`: 页码，默认0
- `size`: 每页大小，默认10

**响应体：**
```json
{
    "success": true,
    "data": {
        "content": [
            {
                "questionnaireId": 1,
                "title": "MBTI性格测试",
                "description": "经典的MBTI性格类型测试",
                "creatorId": 1,
                "creatorUsername": "admin",
                "createdAt": "2025-07-01T08:00:00",
                "isPublished": true,
                "answerCount": 15,
                "hasAnswered": false
            }
        ],
        "totalElements": 1,
        "totalPages": 1,
        "number": 0,
        "size": 10
    },
    "message": "获取问卷列表成功"
}
```

---

### 3.2 获取活跃问卷

**接口地址：** `GET /api/questionnaires/active`

**接口说明：** 获取所有已发布的活跃问卷

**请求头：**
```
Authorization: Bearer {jwt_token}
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
            "creatorUsername": "admin",
            "createdAt": "2025-07-01T08:00:00",
            "isPublished": true,
            "answerCount": 15,
            "hasAnswered": false
        }
    ],
    "message": "获取活跃问卷列表成功"
}
```

---

### 3.3 获取问卷详情

**接口地址：** `GET /api/questionnaires/{id}`

**接口说明：** 获取指定问卷的详细信息

**请求头：**
```
Authorization: Bearer {jwt_token}
```

**路径参数：**
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
        "creatorUsername": "admin",
        "createdAt": "2025-07-01T08:00:00",
        "isPublished": true,
        "answerCount": 15,
        "hasAnswered": false
    },
    "message": "获取问卷详情成功"
}
```

---

### 3.4 获取问卷问题

**接口地址：** `GET /api/questionnaires/{id}/questions`

**接口说明：** 获取问卷的所有问题和选项

**请求头：**
```
Authorization: Bearer {jwt_token}
```

**路径参数：**
- `id`: 问卷ID

**响应体：**
```json
{
    "success": true,
    "data": {
        "questionnaireId": 1,
        "title": "MBTI性格测试",
        "description": "经典的MBTI性格类型测试",
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
                        "score": 1
                    },
                    {
                        "optionId": 2,
                        "content": "更喜欢独自思考",
                        "score": -1
                    }
                ]
            }
        ]
    },
    "message": "获取问卷问题成功"
}
```

---

### 3.5 创建问卷（管理员）

**接口地址：** `POST /api/questionnaires`

**接口说明：** 创建新问卷（仅管理员可访问）

**权限要求：** ADMIN

**请求头：**
```
Authorization: Bearer {jwt_token}
Content-Type: application/json
```

**请求体：**
```json
{
    "title": "新的MBTI测试",
    "description": "全新设计的MBTI性格测试问卷"
}
```

**响应体：**
```json
{
    "success": true,
    "data": {
        "questionnaireId": 2,
        "title": "新的MBTI测试",
        "description": "全新设计的MBTI性格测试问卷",
        "creatorId": 1,
        "creatorUsername": "admin",
        "createdAt": "2025-07-02T10:30:00",
        "isPublished": false,
        "answerCount": 0,
        "hasAnswered": false
    },
    "message": "创建问卷成功"
}
```

---

### 3.6 问题管理接口

#### 3.6.1 获取问卷的所有问题

**接口地址：** `GET /api/questions/questionnaire/{questionnaireId}`

**接口说明：** 获取指定问卷的所有问题和选项

**请求头：**
```
Authorization: Bearer {jwt_token}
```

**路径参数：**
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
                    "score": 1
                },
                {
                    "optionId": 2,
                    "questionId": 1,
                    "content": "更喜欢独自思考",
                    "score": -1
                }
            ]
        }
    ],
    "message": "获取问题列表成功"
}
```

---

#### 3.6.2 创建新问题（管理员）

**接口地址：** `POST /api/questions/questionnaire/{questionnaireId}`

**接口说明：** 为指定问卷创建新问题（仅管理员可访问）

**权限要求：** ADMIN

**请求头：**
```
Authorization: Bearer {jwt_token}
Content-Type: application/json
```

**路径参数：**
- `questionnaireId`: 问卷ID

**请求体：**
```json
{
    "content": "新的问题内容",
    "dimension": "EI",
    "questionOrder": 5,
    "options": [
        {
            "content": "选项A",
            "score": 1
        },
        {
            "content": "选项B",
            "score": -1
        }
    ]
}
```

**响应体：**
```json
{
    "success": true,
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
                "score": 1
            },
            {
                "optionId": 10,
                "questionId": 5,
                "content": "选项B",
                "score": -1
            }
        ]
    },
    "message": "创建问题成功"
}
```

---

#### 3.6.3 批量创建问题（管理员）

**接口地址：** `POST /api/questions/questionnaire/{questionnaireId}/batch`

**接口说明：** 为指定问卷批量创建问题（仅管理员可访问）

**权限要求：** ADMIN

**请求头：**
```
Authorization: Bearer {jwt_token}
Content-Type: application/json
```

**路径参数：**
- `questionnaireId`: 问卷ID

**请求体：**
```json
[
    {
        "content": "第一个问题",
        "dimension": "EI",
        "options": [
            {
                "content": "选项A",
                "score": 1
            },
            {
                "content": "选项B",
                "score": -1
            }
        ]
    },
    {
        "content": "第二个问题",
        "dimension": "SN",
        "options": [
            {
                "content": "选项A",
                "score": 1
            },
            {
                "content": "选项B",
                "score": -1
            }
        ]
    }
]
```

**响应体：**
```json
{
    "success": true,
    "data": [
        // 创建的问题列表
    ],
    "message": "批量创建问题成功"
}
```

---

#### 3.6.4 更新问题（管理员）

**接口地址：** `PUT /api/questions/{questionId}`

**接口说明：** 更新指定问题（仅管理员可访问）

**权限要求：** ADMIN

**请求头：**
```
Authorization: Bearer {jwt_token}
Content-Type: application/json
```

**路径参数：**
- `questionnaireId`: 问卷ID
- `questionId`: 问题ID

**请求体：**
```json
{
    "content": "更新后的问题内容",
    "dimension": "EI",
    "questionOrder": 3,
    "options": [
        {
            "content": "更新后的选项A",
            "score": 2
        },
        {
            "content": "更新后的选项B",
            "score": -2
        }
    ]
}
```

---

#### 3.6.5 删除问题（管理员）

**接口地址：** `DELETE /api/questions/{questionId}`

**接口说明：** 删除指定问题（仅管理员可访问）

**权限要求：** ADMIN

**请求头：**
```
Authorization: Bearer {jwt_token}
```

**路径参数：**
- `questionnaireId`: 问卷ID
- `questionId`: 问题ID

**响应体：**
```json
{
    "success": true,
    "message": "删除问题成功"
}
```

---

#### 3.6.6 更新问题顺序（管理员）

**接口地址：** `PUT /api/questions/questionnaire/{questionnaireId}/reorder`

**接口说明：** 批量更新问题顺序（仅管理员可访问）

**权限要求：** ADMIN

**请求头：**
```
Authorization: Bearer {jwt_token}
Content-Type: application/json
```

**路径参数：**
- `questionnaireId`: 问卷ID

**请求体：**
```json
{
    "1": 3,
    "2": 1,
    "3": 2
}
```

**说明：** 键为问题ID，值为新的顺序号

---

## 4. 测试接口

### 4.1 提交答案

**接口地址：** `POST /api/test/submit`

**接口说明：** 提交问卷答案

**请求头：**
```
Authorization: Bearer {jwt_token}
Content-Type: application/json
```

**请求体：**
```json
{
    "questionnaireId": 1,
    "questionAnswers": {
        "1": 1,   // 问题ID: 选项ID
        "2": 4,
        "3": 5,
        "4": 8
    }
}
```

**响应体：**
```json
{
    "success": true,
    "data": {
        "answerId": 123,
        "mbtiType": "ENFP",
        "dimensionScores": {
            "EI": 3,
            "SN": 1,
            "TF": -2,
            "JP": 2
        },
        "submittedAt": "2025-07-02T10:30:00"
    },
    "message": "答案提交成功"
}
```

---

### 4.2 获取测试结果

**接口地址：** `GET /api/test/results`

**接口说明：** 获取当前用户的所有测试结果

**请求头：**
```
Authorization: Bearer {jwt_token}
```

**请求参数：**
- `page`: 页码，默认0
- `size`: 每页大小，默认10

**响应体：**
```json
{
    "success": true,
    "data": {
        "results": [
            {
                "answerId": 123,
                "questionnaireId": 1,
                "questionnaireTitle": "MBTI性格测试",
                "mbtiType": "ENFP",
                "submittedAt": "2025-07-02T10:30:00"
            }
        ],
        "totalElements": 1,
        "totalPages": 1,
        "currentPage": 0,
        "pageSize": 10
    },
    "message": "获取测试结果成功"
}
```

---

### 4.3 获取MBTI报告

**接口地址：** `GET /api/test/report/{answerId}`

**接口说明：** 获取详细的MBTI测试报告

**请求头：**
```
Authorization: Bearer {jwt_token}
```

**路径参数：**
- `answerId`: 答案ID

**响应体：**
```json
{
    "success": true,
    "data": {
        "mbtiType": "ENFP",
        "dimensionScores": {
            "EI": 3,
            "SN": 1,
            "TF": -2,
            "JP": 2
        },
        "description": "热情、富有想象力和创造力的人，认为生活充满可能性。",
        "strengths": [
            "创造力",
            "同理心",
            "适应性",
            "热情"
        ],
        "challenges": [
            "过度理想化",
            "压力管理",
            "决策困难"
        ],
        "careers": [
            "心理咨询师",
            "教师",
            "作家",
            "艺术家"
        ],
        "generatedAt": "2025-07-02T10:30:00"
    },
    "message": "获取MBTI报告成功"
}
```

---

### 4.4 获取测试统计（管理员）

**接口地址：** `GET /api/test/statistics`

**接口说明：** 获取全站测试统计信息（仅管理员可访问）

**权限要求：** ADMIN

**请求头：**
```
Authorization: Bearer {jwt_token}
```

**响应体：**
```json
{
    "success": true,
    "data": {
        "totalAnswers": 150,
        "totalQuestionnaires": 3,
        "publishedQuestionnaires": 2,
        "mbtiDistribution": {
            "ENFP": 15,
            "INFP": 12,
            "ENFJ": 8,
            "INFJ": 10,
            "ENTP": 11,
            "INTP": 9,
            "ENTJ": 7,
            "INTJ": 13,
            "ESFP": 6,
            "ISFP": 8,
            "ESFJ": 9,
            "ISFJ": 11,
            "ESTP": 5,
            "ISTP": 7,
            "ESTJ": 10,
            "ISTJ": 9
        }
    },
    "message": "获取测试统计成功"
}
```

---

## 5. 公共响应格式

### 成功响应
```json
{
    "success": true,
    "data": {},     // 响应数据，可能是对象、数组或基本类型
    "message": "操作成功描述"
}
```

### 失败响应
```json
{
    "success": false,
    "message": "错误描述",
    "error": "具体错误信息（可选）",
    "timestamp": "2025-07-02T10:30:00",
    "path": "/api/xxx"
}
```

### 分页响应格式
```json
{
    "success": true,
    "data": {
        "content": [],           // 数据列表
        "totalElements": 100,    // 总记录数
        "totalPages": 10,        // 总页数
        "number": 0,            // 当前页码（从0开始）
        "size": 10,             // 每页大小
        "first": true,          // 是否第一页
        "last": false           // 是否最后一页
    },
    "message": "操作成功"
}
```

---

## 6. 错误码说明

### HTTP状态码
- `200`: 操作成功
- `400`: 请求参数错误
- `401`: 未认证或令牌无效
- `403`: 权限不足
- `404`: 资源不存在
- `500`: 服务器内部错误

### 业务错误信息
- `用户名已存在`: 注册时用户名重复
- `邮箱已存在`: 注册时邮箱重复
- `用户名或密码错误`: 登录失败
- `Token已过期`: JWT令牌过期
- `权限不足`: 访问需要更高权限的接口
- `问卷不存在`: 访问不存在的问卷
- `您已经回答过这个问卷了`: 重复答题
- `问卷未发布，无法答题`: 访问未发布的问卷

---

## 7. 认证说明

### JWT Token使用
1. 登录成功后，服务器返回JWT令牌
2. 后续请求需要在Header中携带令牌：
   ```
   Authorization: Bearer {jwt_token}
   ```
3. 令牌有效期为24小时
4. 令牌过期后需要重新登录

### 权限级别
- **无权限**: 登录、注册接口
- **USER**: 普通用户权限，可以答题、查看结果
- **ADMIN**: 管理员权限，可以管理问卷、查看统计

---

## 8. 示例代码

### JavaScript/Axios示例
```javascript
// 登录
const login = async (username, password) => {
    try {
        const response = await axios.post('/api/auth/login', {
            username,
            password
        });
        
        // 保存token
        localStorage.setItem('token', response.data.token);
        return response.data;
    } catch (error) {
        console.error('登录失败:', error.response.data.message);
    }
};

// 带token的请求
const getUserProfile = async () => {
    try {
        const token = localStorage.getItem('token');
        const response = await axios.get('/api/users/profile', {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        return response.data;
    } catch (error) {
        console.error('获取用户信息失败:', error.response.data.message);
    }
};

// 提交答案
const submitAnswers = async (questionnaireId, answers) => {
    try {
        const token = localStorage.getItem('token');
        const response = await axios.post('/api/test/submit', {
            questionnaireId,
            questionAnswers: answers
        }, {
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });
        return response.data;
    } catch (error) {
        console.error('提交答案失败:', error.response.data.message);
    }
};
```

### cURL示例
```bash
# 登录
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"123456"}'

# 获取用户信息
curl -X GET http://localhost:8080/api/users/profile \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# 提交答案
curl -X POST http://localhost:8080/api/test/submit \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"questionnaireId":1,"questionAnswers":{"1":1,"2":4,"3":5,"4":8}}'
```

---

## 9. 常见问题排查

### 9.1 登录接口错误

**错误信息：** `Required request body is missing`

**原因：** 请求参数放在了URL查询参数中，而不是请求体中

**错误示例：**
```bash
# ❌ 错误 - 参数在URL中
curl -X POST "http://localhost:8080/api/auth/login?username=testuser&password=test123"
```

**正确示例：**
```bash
# ✅ 正确 - 参数在请求体中
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"test123"}'
```

**JavaScript正确示例：**
```javascript
// ✅ 正确
const response = await fetch('/api/auth/login', {
    method: 'POST',
    headers: {
        'Content-Type': 'application/json'
    },
    body: JSON.stringify({
        username: 'testuser',
        password: 'test123'
    })
});

// ❌ 错误 - 不要用URLSearchParams
const response = await fetch('/api/auth/login?username=testuser&password=test123', {
    method: 'POST'
});
```

### 9.2 JWT Token相关错误

**错误信息：** `401 Unauthorized` 或 `Token已过期`

**解决方案：**
1. 检查Token格式是否正确：`Bearer {token}`
2. 确认Token未过期（有效期24小时）
3. 重新登录获取新Token

**正确的Token使用：**
```javascript
const token = localStorage.getItem('token');
const response = await fetch('/api/users/profile', {
    headers: {
        'Authorization': `Bearer ${token}`
    }
});
```

### 9.3 权限不足错误

**错误信息：** `403 Forbidden` 或 `权限不足`

**解决方案：**
1. 确认当前用户角色是否满足接口要求
2. 管理员接口需要ADMIN角色
3. 检查用户是否已正确登录

### 9.4 参数校验错误

**错误信息：** `400 Bad Request` 参数校验失败

**常见问题：**
- 用户名长度不符合要求（3-50字符）
- 密码长度不符合要求（6-100字符）
- 邮箱格式不正确
- 必填字段为空

**解决方案：**
```json
{
    "username": "test",      // ✅ 3-50字符
    "password": "123456",    // ✅ 6-100字符
    "email": "test@example.com"  // ✅ 正确邮箱格式
}
```

### 9.5 CORS 跨域请求错误

**错误信息：** 
- `Access to fetch at '...' has been blocked by CORS policy`
- `No 'Access-Control-Allow-Origin' header is present`
- `CORS error when allowCredentials is true`

**原因分析：**
1. 前端域名未在CORS配置中允许
2. 请求头设置不正确
3. 预检请求失败

**解决方案：**

1. **检查前端配置：**
```javascript
// ✅ 正确配置
axios.defaults.withCredentials = true;
axios.defaults.headers.common['Content-Type'] = 'application/json';

// 或使用fetch
fetch(url, {
    method: 'POST',
    credentials: 'include',  // 重要：允许发送凭据
    headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + token
    },
    body: JSON.stringify(data)
});
```

2. **开发环境测试：**
```bash
# 使用curl测试CORS
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -H "Origin: http://localhost:3000" \
  -d '{"username":"test","password":"123456"}'
```

3. **浏览器调试：**
- 打开浏览器开发者工具
- 查看Network标签页的请求详情
- 检查Response Headers中的CORS头信息
- 确认OPTIONS预检请求是否成功

**CORS配置说明：**
- 系统已配置支持所有域名的跨域请求
- 生产环境建议指定具体域名以增强安全性
- 支持凭据传递 (allowCredentials: true)

### 9.6 Content-Type 错误

**错误信息：** `415 Unsupported Media Type`

**原因：** 请求头中未正确设置`Content-Type`，或请求体格式与`Content-Type`不匹配

**解决方案：**
1. 确保请求头包含`Content-Type: application/json`
2. 确保请求体为有效的JSON格式

**正确示例：**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"123456"}'
```

```javascript
const response = await fetch('/api/auth/login', {
    method: 'POST',
    headers: {
        'Content-Type': 'application/json'
    },
    body: JSON.stringify({
        username: 'testuser',
        password: '123456'
    })
});
```

---

## 10. 测试工具推荐

### 10.1 Postman测试

1. **导入集合**：创建Postman Collection
2. **环境变量**：设置baseUrl和token变量
3. **自动化测试**：编写测试脚本

**Postman环境变量：**
```
baseUrl: http://localhost:8080
token: {{token}} (从登录响应中自动提取)
```

### 10.2 VS Code REST Client

创建 `.http` 文件进行测试：

```http
### 登录
POST {{baseUrl}}/api/auth/login
Content-Type: application/json

{
    "username": "testuser",
    "password": "test123"
}

### 获取用户信息
GET {{baseUrl}}/api/users/profile
Authorization: Bearer {{token}}

### 提交答案
POST {{baseUrl}}/api/test/submit
Authorization: Bearer {{token}}
Content-Type: application/json

{
    "questionnaireId": 1,
    "questionAnswers": {
        "1": 1,
        "2": 4,
        "3": 5
    }
}
```

---

**文档版本**: v1.0  
**最后更新**: 2025年7月2日  
**联系方式**: 开发团队
