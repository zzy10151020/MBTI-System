# 后端项目介绍

本项目为 MBTI-System 的后端部分，基于 Java Servlet 技术开发，采用传统 Java Web 架构，负责为前端系统提供 API 支持、业务逻辑处理及数据持久化。

## 技术栈
- Java 17 及以上
- Servlet
- Maven
- MySQL
- Tomcat（嵌入式或外部）

## 主要功能模块
- 用户注册与登录
- MBTI 测试题管理
- 问卷管理与答题记录
- 结果统计与分析
- 管理员后台接口

## 设计模式说明
本项目在后端开发中应用了多种经典设计模式，以提升系统的可维护性、可扩展性和解耦性，主要包括：

- **单例模式（Singleton）**
  - 用于全局唯一对象的创建，如数据库连接池、配置管理等，保证系统资源的高效利用。

- **工厂模式（Factory）**
  - 用于对象的统一创建和管理，降低模块间的耦合度，便于扩展和维护。

- **策略模式（Strategy）**
  - 用于将不同的业务处理逻辑进行封装和切换，如不同类型的问卷评分算法、用户权限校验策略等。

- **模板方法模式（Template Method）**
  - 用于定义业务处理流程的骨架，将具体步骤延迟到子类实现，常见于抽象控制器或服务基类。

- **数据访问对象模式（DAO）**
  - 通过 DAO 层实现数据访问逻辑与业务逻辑的分离，便于数据库切换和单元测试。

这些设计模式的合理应用，有效提升了系统的灵活性和可维护性。

## 项目架构解释
本后端项目采用经典的分层架构设计，主要包括以下几层，并通过清晰的接口进行解耦：

- **Controller 层**
  - 作用：负责接收和响应前端（或其他客户端）的 HTTP 请求，进行参数校验、权限校验，并将请求数据封装为业务对象，调用 Service 层处理业务逻辑，最后将结果封装为标准响应返回。
  - 与 Service 层关系：Controller 层依赖 Service 层，所有业务处理均委托给 Service 层完成。

- **Service 层**
  - 作用：承载系统的核心业务逻辑，负责业务流程的编排、事务控制、复杂数据处理等。
  - 与 DAO 层关系：Service 层依赖 DAO 层，负责调用 DAO 层进行数据的持久化和查询。
  - 与 Controller 层关系：为 Controller 层提供业务服务接口。

- **DAO 层（Data Access Object）**
  - 作用：专注于与数据库的交互，负责数据的增删改查操作。通过 ORM 框架或原生 SQL 实现数据访问。
  - 与 Service 层关系：DAO 层为 Service 层提供数据访问能力，屏蔽底层数据库细节。

- **Entity 层**
  - 作用：Entity（实体类）用于映射数据库表结构，反映数据库中的数据模型。
  - 与各层关系：Entity 主要在 DAO 层与数据库交互时使用，Service 层也会用到 Entity 进行业务处理。

- **DTO 层（Data Transfer Object）**
  - 作用：DTO 用于在各层之间（如 Controller、Service）传递结构化数据，通常用于封装请求参数和响应结果。
  - 与各层关系：DTO 主要在 Controller 和 Service 层之间传递，避免直接暴露数据库实体，提升安全性和灵活性。


## 其他模块说明
- **Filter（过滤器）**
  - 作用：用于对进入系统的请求进行预处理，如权限校验、登录验证、请求日志记录、跨域处理等。通过过滤器可以统一管理和拦截请求，提升系统安全性和可维护性。

- **Util（工具类）**
  - 作用：封装项目中常用的通用方法和工具函数，如字符串处理、加密解密、时间日期处理等，便于代码复用和维护。

## 目录结构
- `src/main/java/org/frostedstar/mbtisystem/`：核心 Java 代码
- `src/main/resources/`：配置文件与 SQL 脚本
- `introductions/`：接口文档与说明
- `target/`：编译输出目录

## 启动方式
1. 配置好 MySQL 数据库，并在 `src/main/resources/application.properties` 中修改数据库连接信息。
2. 使用 Maven 构建项目：
   ```shell
   ./mvnw clean package
   ```
3. 运行生成的 jar 包：
   ```shell
   java -jar target/mbti-system.jar
   ```
4. 后端服务默认监听 8080 端口。

## 相关文档
- [API_INTRODUCTION.md](backend/introductions/API_INTRODUCTION.md)：接口详细说明
