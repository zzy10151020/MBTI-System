package org.frostedstar.mbtisystem;

import org.frostedstar.mbtisystem.controller.AuthController;
import org.frostedstar.mbtisystem.controller.UserController;
import org.frostedstar.mbtisystem.controller.QuestionnaireController;
import org.frostedstar.mbtisystem.controller.TestController;
import org.frostedstar.mbtisystem.service.AuthService;
import org.frostedstar.mbtisystem.service.UserService;
import org.frostedstar.mbtisystem.service.QuestionnaireService;
import org.frostedstar.mbtisystem.service.TestService;
import org.frostedstar.mbtisystem.model.*;
import org.frostedstar.mbtisystem.dto.*;
import org.frostedstar.mbtisystem.repository.*;
import org.frostedstar.mbtisystem.config.*;
import org.frostedstar.mbtisystem.security.*;

/**
 * 编译测试类 - 验证所有类是否可以正常导入
 */
public class CompilationTest {
    
    // Controllers
    private AuthController authController;
    private UserController userController;
    private QuestionnaireController questionnaireController;
    private TestController testController;
    
    // Services
    private AuthService authService;
    private UserService userService;
    private QuestionnaireService questionnaireService;
    private TestService testService;
    
    // Models
    private User user;
    private Questionnaire questionnaire;
    private Question question;
    private Option option;
    private Answer answer;
    private AnswerDetail answerDetail;
    private UserRole userRole = UserRole.USER;
    private MbtiDimension mbtiDimension = MbtiDimension.EI;
    
    // DTOs
    private UserDTO userDTO;
    private LoginRequest loginRequest;
    private RegisterDTO registerDTO;
    private QuestionnaireDTO questionnaireDTO;
    private AnswerSubmitDTO answerSubmitDTO;
    private JwtResponse jwtResponse;
    
    // Repositories
    private UserRepository userRepository;
    private QuestionnaireRepository questionnaireRepository;
    private QuestionRepository questionRepository;
    private OptionRepository optionRepository;
    private AnswerRepository answerRepository;
    private AnswerDetailRepository answerDetailRepository;
    
    // Config
    private SecurityConfig securityConfig;
    private JwtConfig jwtConfig;
    private WebConfig webConfig;
    private DataInitConfig dataInitConfig;
    private GlobalExceptionHandler globalExceptionHandler;
    
    // Security
    private JwtUtil jwtUtil;
    private JwtAuthFilter jwtAuthFilter;
    private UserDetailsImpl userDetailsImpl;
    
    public void testCompilation() {
        System.out.println("所有类都可以正常导入和编译！");
    }
}
