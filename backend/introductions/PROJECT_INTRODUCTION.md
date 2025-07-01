# MBTI系统后端项目结构说明

## 项目概述
MBTI系统后端基于Spring Boot 3.x框架，提供用户认证、问卷管理和测试结果分析功能。

## 技术栈
- **框架**: Spring Boot 3.x
- **安全**: Spring Security + JWT
- **数据库**: MySQL 8.0+
- **ORM**: Spring Data JPA + Hibernate
- **构建工具**: Maven
- **Java版本**: 17+

## 核心功能模块

### 1. 认证模块 (Authentication)
- **控制器**: `AuthController`
- **服务**: `AuthService`, `UserDetailsServiceImpl`
- **功能**: 用户注册、登录、JWT令牌管理
- **安全**: JWT过滤器、密码BCrypt加密

### 2. 用户管理模块 (User Management)
- **控制器**: `UserController`
- **服务**: `UserService`
- **实体**: `User`, `UserRole`
- **功能**: 用户信息管理、角色权限控制

### 3. 问卷系统模块 (Questionnaire System)
- **控制器**: `QuestionnaireController`
- **服务**: `QuestionnaireService`
- **实体**: `Questionnaire`, `Question`, `Option`
- **功能**: 问卷创建、问题管理、选项配置

### 4. 测试评估模块 (Test Assessment)
- **控制器**: `TestController`
- **服务**: `TestService`
- **实体**: `Answer`, `AnswerDetail`, `MbtiDimension`
- **功能**: 答案收集、MBTI结果分析

## 数据库设计要点

### 实体关系
- `User` ↔ `Answer` (一对多)
- `Questionnaire` ↔ `Question` (一对多)
- `Question` ↔ `Option` (一对多)
- `Answer` ↔ `AnswerDetail` (一对多)

### 枚举转换
- `UserRole`: 使用`UserRoleConverter`映射数据库值
- `MbtiDimension`: 使用`MbtiDimensionConverter`映射数据库值
- 数据库存储小写字符串，Java使用枚举类型

### 关键字处理
- `Option`表使用反引号避免MySQL关键字冲突
- 全局配置`globally_quoted_identifiers: true`

## 配置说明

### 安全配置 (`SecurityConfig`)
- JWT认证配置
- 无状态会话管理
- 路径权限控制
- CORS跨域支持

### 数据库配置 (`application.yaml`)
- MySQL连接配置
- UTF-8字符集支持
- JPA/Hibernate配置
- 连接池设置

### 全局异常处理 (`GlobalExceptionHandler`)
- 统一异常响应格式
- 参数验证异常处理
- 业务异常处理

## API设计规范

### 认证接口 (`/api/auth`)
- `POST /register` - 用户注册
- `POST /login` - 用户登录
- `GET /status` - 健康检查

### 用户接口 (`/api/users`)
- `GET /profile` - 获取用户信息
- `PUT /profile` - 更新用户信息

### 问卷接口 (`/api/questionnaires`)
- `GET /` - 获取问卷列表
- `GET /{id}` - 获取问卷详情
- `POST /` - 创建问卷 (管理员)

### 测试接口 (`/api/tests`)
- `POST /submit` - 提交答案
- `GET /result/{id}` - 获取测试结果

## 开发注意事项

### 依赖注入
- 使用`@RequiredArgsConstructor` + `final`字段
- 避免字段注入和Setter注入
- 确保依赖不为null

### 数据验证
- DTO使用JSR-303验证注解
- Controller层参数验证
- 业务层数据校验

### 异常处理
- 使用自定义业务异常
- 全局异常处理器统一响应
- 日志记录和错误追踪

### 安全考虑
- JWT令牌过期时间控制
- 密码强度验证
- SQL注入防护
- XSS攻击防护

## 测试账号
详见 `TEST_ACCOUNTS.md` 文件，所有测试账号密码均为 `password`。

## 相关文档
- `API_DOCUMENTATION.md` - 完整API文档
- `COMPLETION_REPORT.md` - 项目完成报告
- `TEST_ACCOUNTS.md` - 测试账号说明
- `USER_AUTH_TEST_GUIDE.md` - 认证测试指南

## 目录结构
```
src/main/java/org/frostedstar/mbtisystem/
├── config/                    # 配置类
│   ├── DataInitConfig.java         # 数据初始化配置
│   ├── GlobalExceptionHandler.java # 全局异常处理器
│   ├── JwtConfig.java              # JWT配置
│   ├── SecurityConfig.java         # Spring Security配置
│   └── WebConfig.java              # Web配置（CORS等）
├── controller/                # REST控制器
│   ├── AuthController.java         # 认证相关API
│   ├── QuestionnaireController.java # 问卷相关API
│   ├── TestController.java         # 测试相关API
│   └── UserController.java         # 用户相关API
├── dto/                       # 数据传输对象
│   ├── AnswerSubmitDTO.java        # 答案提交DTO
│   ├── JwtResponse.java            # JWT响应DTO
│   ├── LoginRequest.java           # 登录请求DTO
│   ├── QuestionnaireDTO.java       # 问卷DTO
│   ├── RegisterDTO.java            # 注册DTO
│   └── UserDTO.java                # 用户DTO
├── model/                     # 实体类
│   ├── Answer.java                 # 答案实体
│   ├── AnswerDetail.java           # 答案详情实体
│   ├── MbtiDimension.java          # MBTI维度枚举
│   ├── MbtiDimensionConverter.java # MBTI维度转换器
│   ├── Option.java                 # 选项实体
│   ├── Question.java               # 问题实体
│   ├── Questionnaire.java          # 问卷实体
│   ├── User.java                   # 用户实体
│   ├── UserRole.java               # 用户角色枚举
│   └── UserRoleConverter.java      # 用户角色转换器
├── repository/                # 数据访问层
│   ├── AnswerDetailRepository.java  # 答案详情Repository
│   ├── AnswerRepository.java        # 答案Repository
│   ├── OptionRepository.java        # 选项Repository
│   ├── QuestionnaireRepository.java # 问卷Repository
│   ├── QuestionRepository.java      # 问题Repository
│   └── UserRepository.java          # 用户Repository
├── service/                   # 服务层
│   ├── AuthService.java            # 认证服务
│   ├── QuestionnaireService.java   # 问卷服务
│   ├── TestService.java            # 测试服务
│   ├── UserDetailsServiceImpl.java # Spring Security用户详情服务
│   └── UserService.java            # 用户服务
├── security/                  # 安全模块
│   ├── JwtAuthFilter.java          # JWT认证过滤器
│   ├── JwtUtil.java                # JWT工具类
│   └── UserDetailsImpl.java       # Spring Security用户详情实现
├── util/                      # 工具类
│   └── PasswordHashGenerator.java  # 密码哈希生成器
└── MbtiApplication.java       # 应用启动类
```