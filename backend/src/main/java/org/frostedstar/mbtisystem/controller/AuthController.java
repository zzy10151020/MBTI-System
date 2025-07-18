package org.frostedstar.mbtisystem.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.frostedstar.mbtisystem.dto.AuthDTO;
import org.frostedstar.mbtisystem.dto.OperationType;
import org.frostedstar.mbtisystem.dto.ApiResponse;
import org.frostedstar.mbtisystem.dto.UserDTO;
import org.frostedstar.mbtisystem.entity.User;
import org.frostedstar.mbtisystem.service.ServiceFactory;
import org.frostedstar.mbtisystem.service.UserService;
import org.frostedstar.mbtisystem.servlet.Route;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 认证控制器
 */
@Slf4j
public class AuthController extends BaseController {
    
    private final UserService userService;
    
    public AuthController() {
        this.userService = ServiceFactory.getUserService();
    }
    
    /**
     * 用户登录
     */
    @Route(value = "/login", method = "POST")
    public void loginUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (!AuthUtils.checkHttpMethod(request, response, this, "POST")) return;
            
            AuthDTO loginRequest = parseRequestBody(request, AuthDTO.class);
            loginRequest.setOperationType(OperationType.QUERY);
            
            // 验证参数
            if (!loginRequest.isValid()) {
                sendErrorResponse(response, 400, "登录参数不完整或格式错误", "/api/auth/login");
                return;
            }
            
            // 用户登录
            Optional<User> userOptional = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
            
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                
                // 创建会话
                HttpSession session = request.getSession();
                session.setAttribute("userId", user.getUserId());
                session.setAttribute("username", user.getUsername());
                session.setAttribute("role", user.getRole().name());
                
                // 使用DTO转换
                UserDTO userDTO = UserDTO.fromEntity(user);
                
                Map<String, Object> result = new HashMap<>();
                result.put("user", userDTO);
                result.put("sessionId", session.getId());
                
                ApiResponse<Map<String, Object>> apiResponse = ApiResponse.success("登录成功", result);
                sendApiResponse(response, apiResponse);
                log.info("用户登录成功: {}", user.getUsername());
            } else {
                sendErrorResponse(response, 401, "用户名或密码错误", "/api/auth/login");
            }
            
        } catch (Exception e) {
            log.error("用户登录失败", e);
            sendErrorResponse(response, 500, "登录失败: " + e.getMessage(), "/api/auth/login");
        }
    }
    
    /**
     * 用户注册
     */
    @Route(value = "/register", method = "POST")
    public void registerUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (!AuthUtils.checkHttpMethod(request, response, this, "POST")) return;
            
            AuthDTO registerRequest = parseRequestBody(request, AuthDTO.class);
            registerRequest.setOperationType(OperationType.CREATE);
            
            // 验证参数
            if (!registerRequest.isValid()) {
                sendErrorResponse(response, 400, "注册参数不完整或格式错误", "/api/auth/register");
                return;
            }
            
            // 验证用户名长度
            if (registerRequest.getUsername().length() < 3 || registerRequest.getUsername().length() > 20) {
                sendErrorResponse(response, 400, "用户名长度必须在3-20个字符之间", "/api/auth/register");
                return;
            }
            
            // 验证密码长度
            if (registerRequest.getPassword().length() < 6) {
                sendErrorResponse(response, 400, "密码长度不能少于6位", "/api/auth/register");
                return;
            }
            
            // 用户注册
            User user = userService.register(
                registerRequest.getUsername(),
                registerRequest.getPassword(),
                registerRequest.getEmail()
            );
            
            // 使用DTO转换
            UserDTO userDTO = UserDTO.fromEntity(user);
            
            ApiResponse<UserDTO> apiResponse = ApiResponse.success("注册成功", userDTO);
            sendApiResponse(response, apiResponse);
            log.info("用户注册成功: {}", user.getUsername());
            
        } catch (RuntimeException e) {
            log.error("用户注册失败", e);
            sendErrorResponse(response, 400, e.getMessage(), "/api/auth/register");
        } catch (Exception e) {
            log.error("用户注册失败", e);
            sendErrorResponse(response, 500, "注册失败: " + e.getMessage(), "/api/auth/register");
        }
    }
    
    /**
     * 用户注销
     */
    @Route(value = "/logout", method = "POST")
    public void logoutUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (!AuthUtils.checkHttpMethod(request, response, this, "POST")) return;
            
            HttpSession session = request.getSession(false);
            if (session != null) {
                String username = (String) session.getAttribute("username");
                session.invalidate();
                log.info("用户注销成功: {}", username);
            }
            
            ApiResponse<String> apiResponse = ApiResponse.success("注销成功", "注销成功");
            sendApiResponse(response, apiResponse);
            
        } catch (Exception e) {
            log.error("用户注销失败", e);
            sendErrorResponse(response, 500, "注销失败: " + e.getMessage(), "/api/auth/logout");
        }
    }
    
    /**
     * 检查用户名是否存在
     */
    @Route(value = "/checkUsername", method = "GET")
    public void checkUsername(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (!AuthUtils.checkHttpMethod(request, response, this, "GET")) return;
            
            String username = request.getParameter("username");
            if (username == null || username.trim().isEmpty()) {
                sendErrorResponse(response, 400, "用户名不能为空", "/api/auth/checkUsername");
                return;
            }
            
            boolean exists = userService.existsByUsername(username);
            
            Map<String, Object> result = new HashMap<>();
            result.put("exists", exists);
            result.put("username", username);
            
            ApiResponse<Map<String, Object>> apiResponse = ApiResponse.success(result);
            sendApiResponse(response, apiResponse);
            
        } catch (Exception e) {
            log.error("检查用户名失败", e);
            sendErrorResponse(response, 500, "检查用户名失败: " + e.getMessage(), "/api/auth/checkUsername");
        }
    }
    
    /**
     * 检查邮箱是否存在
     */
    @Route(value = "/checkEmail", method = "GET")
    public void checkEmail(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (!AuthUtils.checkHttpMethod(request, response, this, "GET")) return;
            
            String email = request.getParameter("email");
            if (email == null || email.trim().isEmpty()) {
                sendErrorResponse(response, 400, "邮箱不能为空", "/api/auth/checkEmail");
                return;
            }
            
            boolean exists = userService.existsByEmail(email);
            
            Map<String, Object> result = new HashMap<>();
            result.put("exists", exists);
            result.put("email", email);
            
            ApiResponse<Map<String, Object>> apiResponse = ApiResponse.success(result);
            sendApiResponse(response, apiResponse);
            
        } catch (Exception e) {
            log.error("检查邮箱失败", e);
            sendErrorResponse(response, 500, "检查邮箱失败: " + e.getMessage(), "/api/auth/checkEmail");
        }
    }
}
