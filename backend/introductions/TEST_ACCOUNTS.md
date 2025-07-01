# MBTI系统测试账号信息

## 数据库用户账号

更新数据库后，可以使用以下测试账号登录系统：

### 管理员账号
- **用户名**: `admin`
- **密码**: `password`
- **邮箱**: `admin@mbti.com`
- **角色**: `admin`

### 普通用户账号

#### 用户1
- **用户名**: `user1`  
- **密码**: `password`
- **邮箱**: `user1@test.com`
- **角色**: `user`

#### 用户2
- **用户名**: `user2`
- **密码**: `password`
- **邮箱**: `user2@test.com`
- **角色**: `user`

## API测试示例

### 管理员登录
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password"}'
```

### 普通用户登录
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user1","password":"password"}'
```

## 数据库更新

如果需要重新初始化数据库，运行以下SQL脚本：
```sql
-- 位置：src/main/resources/SQLResources/createSQL.sql
-- 包含表结构创建和测试数据插入
```

## 密码说明

- 所有测试账号的密码都是简单的 `password`
- 数据库中存储的是BCrypt哈希值：`$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi`
- 生产环境请务必更改为安全的密码

## 权限说明

- **admin角色**：可以访问所有API，包括用户管理、问卷管理等
- **user角色**：只能访问普通用户功能，如答题、查看个人信息等

## 注意事项

1. 首次使用前请确保数据库已正确初始化
2. 如果登录失败，请检查：
   - 用户名和密码是否正确
   - 数据库连接是否正常
   - 用户角色是否正确存储（小写：user/admin）
