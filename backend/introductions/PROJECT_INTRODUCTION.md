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
- `src/main/java/org/frostedstar/mbtisystem/`：核心 Java 代码，包含以下常见包：
  - `controller/`：控制器层，负责接收和响应前端请求，进行参数校验、权限校验，并调用 Service 层处理业务逻辑。
    - `AuthController.java`：处理用户认证相关接口（如登录、注册）。
    - `UserController.java`：用户管理相关接口。
    - `QuestionController.java`：MBTI 测试题相关接口。
    - `QuestionnaireController.java`：问卷管理相关接口。
    - `TestController.java`：测试记录与结果相关接口。
    - `BaseController.java`：控制器通用父类，封装通用响应、异常处理等。
    - `AuthUtils.java`：认证辅助工具类。
  - `service/`：服务层，承载系统核心业务逻辑，负责业务流程编排、事务控制、复杂数据处理等。
    - `UserService.java`：用户相关业务接口，定义用户注册、登录、信息管理等操作。
    - `TestService.java`：测试记录与结果相关业务接口，定义测试流程、结果统计等操作。
    - `QuestionService.java`：题目相关业务接口，定义题目增删改查、题库管理等操作。
    - `QuestionnaireService.java`：问卷相关业务接口，定义问卷创建、发布、查询等操作。
    - `ServiceFactory.java`：服务工厂，统一管理和获取 Service 实例。
    - `BaseService.java`：服务层通用父接口，定义通用业务方法。
    - `impl/`：具体实现类包。
      - `UserServiceImpl.java`：用户业务接口实现。
      - `TestServiceImpl.java`：测试业务接口实现。
      - `QuestionServiceImpl.java`：题目业务接口实现。
      - `QuestionnaireServiceImpl.java`：问卷业务接口实现。
  - `dao/`：数据访问层，专注于与数据库的交互，负责数据的增删改查操作，屏蔽底层数据库细节。
    - `UserDAO.java`：用户数据访问接口，定义用户表的增删改查操作。
    - `TestDAO.java`：测试记录数据访问接口，定义测试记录表的操作。
    - `QuestionDAO.java`：题目数据访问接口，定义题目表的操作。
    - `QuestionnaireDAO.java`：问卷数据访问接口，定义问卷表的操作。
    - `OptionDAO.java`：选项数据访问接口，定义选项表的操作。
    - `AnswerDAO.java`：答卷数据访问接口，定义答卷表的操作。
    - `AnswerDetailDAO.java`：答题详情数据访问接口，定义答题明细表的操作。
    - `DaoFactory.java`：DAO 工厂，统一管理和获取 DAO 实例。
    - `BaseDAO.java`：DAO 层通用父接口，定义通用数据访问方法。
    - `impl/`：具体实现类包。
      - `UserDAOImpl.java`：用户数据访问接口实现。
      - `TestDAOImpl.java`：测试记录数据访问接口实现。
      - `QuestionDAOImpl.java`：题目数据访问接口实现。
      - `QuestionnaireDAOImpl.java`：问卷数据访问接口实现。
      - `OptionDAOImpl.java`：选项数据访问接口实现。
      - `AnswerDAOImpl.java`：答卷数据访问接口实现。
      - `AnswerDetailDAOImpl.java`：答题详情数据访问接口实现。
  - `entity/`：实体类包，用于映射数据库表结构，反映数据库中的数据模型。
    - `User.java`：用户实体，映射用户表，包含用户的基本信息（如用户名、密码、邮箱、注册时间等）。
    - `Questionnaire.java`：问卷实体，映射问卷表，包含问卷的基本信息、创建者、创建时间等。
    - `Question.java`：题目实体，映射题目表，包含题目内容、所属问卷、题目类型等。
    - `Option.java`：选项实体，映射选项表，包含选项内容、分值、所属题目等。
    - `Answer.java`：答卷实体，映射用户提交的答卷，包含答卷所属用户、问卷、提交时间等。
    - `AnswerDetail.java`：答题详情实体，映射答题明细表，记录每道题的作答情况。
  - `dto/`：数据传输对象包，用于在各层之间传递结构化数据，封装请求参数和响应结果。
    - `ApiResponse.java`：通用响应 DTO，封装统一的 API 响应结构。
    - `ErrorResponse.java`：错误响应 DTO，封装错误信息。
    - `PageResponse.java`：分页响应 DTO，封装分页数据结构。
    - `authdto/`：认证相关 DTO 子包。
    - `optiondto/`：选项相关 DTO 子包。
    - `questiondto/`：题目相关 DTO 子包。
    - `questionnairedto/`：问卷相关 DTO 子包。
    - `testdto/`：测试相关 DTO 子包。
    - `userdto/`：用户相关 DTO 子包。
  - `filter/`：过滤器包，用于对进入系统的请求进行预处理，如权限校验、登录验证、日志记录、跨域处理等。
    - `CorsFilter.java`：跨域请求处理过滤器，统一处理跨域访问。
    - `CharacterEncodingFilter.java`：字符编码统一过滤器，保证请求和响应的编码一致。
  - `util/`：工具类包，封装常用的通用方法和工具函数，如字符串处理、加密解密、时间日期处理等。
    - `DatabaseUtil.java`：数据库连接与操作工具类，简化数据库操作流程。
    - `PasswordUtil.java`：密码加密与校验工具类，提供安全的密码处理方法。
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
