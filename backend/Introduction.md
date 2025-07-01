**后端结构说明**
```
src/main/java/com/example/mbti/
├── config/              # 配置类
│   ├── SecurityConfig.java
│   ├── JwtConfig.java
│   └── WebConfig.java
├── controller/          # 控制器
│   ├── AuthController.java
│   ├── UserController.java
│   ├── QuestionnaireController.java
│   └── TestController.java
├── model/               # 实体类
│   ├── User.java
│   ├── Questionnaire.java
│   ├── Question.java
│   ├── Option.java
│   ├── Answer.java
│   └── AnswerDetail.java
├── repository/          # 数据访问层
│   ├── UserRepository.java
│   ├── QuestionnaireRepository.java
│   ├── QuestionRepository.java
│   ├── OptionRepository.java
│   ├── AnswerRepository.java
│   └── AnswerDetailRepository.java
├── service/             # 服务层
│   ├── UserService.java
│   ├── AuthService.java
│   ├── QuestionnaireService.java
│   └── TestService.java
├── dto/                 # 数据传输对象
│   ├── UserDTO.java
│   ├── LoginDTO.java
│   ├── QuestionnaireDTO.java
│   └── AnswerSubmitDTO.java
├── security/            # 安全模块
│   ├── JwtUtil.java
│   ├── JwtAuthFilter.java
│   └── UserDetailsImpl.java
└── MbtiApplication.java # 启动类
```