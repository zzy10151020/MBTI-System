package org.frostedstar.mbtisystem.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.frostedstar.mbtisystem.dto.AuthDTO;
import org.frostedstar.mbtisystem.dto.OperationType;
import org.frostedstar.mbtisystem.dto.ApiResponse;
import org.frostedstar.mbtisystem.dto.UserDTO;
import org.frostedstar.mbtisystem.dto.ErrorResponse;
import org.frostedstar.mbtisystem.entity.User;
import org.frostedstar.mbtisystem.service.ServiceFactory;
import org.frostedstar.mbtisystem.service.UserService;

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
    public void login(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (!AuthUtils.checkHttpMethod(request, response, this, "POST")) return;
            
            AuthDTO loginRequest = parseRequestBody(request, AuthDTO.class);
            loginRequest.setOperationType(OperationType.QUERY);
            
            // 验证参数
            if (!loginRequest.isValid()) {
                ApiResponse<Object> apiResponse = ApiResponse.error("登录参数不完整或格式错误");
                sendApiResponse(response, apiResponse);
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
                ApiResponse<Object> apiResponse = ApiResponse.error("用户名或密码错误");
                sendApiResponse(response, apiResponse);
            }
            
        } catch (Exception e) {
            log.error("用户登录失败", e);
            ErrorResponse errorResponse = ErrorResponse.create(
                e.getClass().getSimpleName(),
                "登录失败: " + e.getMessage(),
                500,
                "/api/auth/login"
            );
            ApiResponse<ErrorResponse> apiResponse = ApiResponse.systemError(errorResponse);
            sendApiResponse(response, apiResponse);
        }
    }
    
    /**
     * 用户注册
     */
    public void register(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (!AuthUtils.checkHttpMethod(request, response, this, "POST")) return;
            
            AuthDTO registerRequest = parseRequestBody(request, AuthDTO.class);
            registerRequest.setOperationType(OperationType.CREATE);
            
            // 验证参数
            if (!registerRequest.isValid()) {
                ApiResponse<Object> apiResponse = ApiResponse.error("注册参数不完整或格式错误");
                sendApiResponse(response, apiResponse);
                return;
            }
            
            // 验证用户名长度
            if (registerRequest.getUsername().length() < 3 || registerRequest.getUsername().length() > 20) {
                ApiResponse<Object> apiResponse = ApiResponse.error("用户名长度必须在3-20个字符之间");
                sendApiResponse(response, apiResponse);
                return;
            }
            
            // 验证密码长度
            if (registerRequest.getPassword().length() < 6) {
                ApiResponse<Object> apiResponse = ApiResponse.error("密码长度不能少于6位");
                sendApiResponse(response, apiResponse);
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
            ApiResponse<Object> apiResponse = ApiResponse.error(e.getMessage());
            sendApiResponse(response, apiResponse);
        } catch (Exception e) {
            log.error("用户注册失败", e);
            ErrorResponse errorResponse = ErrorResponse.create(
                e.getClass().getSimpleName(),
                "注册失败: " + e.getMessage(),
                500,
                "/api/auth/register"
            );
            ApiResponse<ErrorResponse> apiResponse = ApiResponse.systemError(errorResponse);
            sendApiResponse(response, apiResponse);
        }
    }
    
    /**
     * 用户注销
     */
    public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
            ErrorResponse errorResponse = ErrorResponse.create(
                e.getClass().getSimpleName(),
                "注销失败: " + e.getMessage(),
                500,
                "/api/auth/logout"
            );
            ApiResponse<ErrorResponse> apiResponse = ApiResponse.systemError(errorResponse);
            sendApiResponse(response, apiResponse);
        }
    }
    
    /**
     * 检查用户名是否存在
     */
    public void checkUsername(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (!AuthUtils.checkHttpMethod(request, response, this, "GET")) return;
            
            String username = request.getParameter("username");
            if (username == null || username.trim().isEmpty()) {
                ApiResponse<Object> apiResponse = ApiResponse.error("用户名不能为空");
                sendApiResponse(response, apiResponse);
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
            ErrorResponse errorResponse = ErrorResponse.create(
                e.getClass().getSimpleName(),
                "检查用户名失败: " + e.getMessage(),
                500,
                "/api/auth/checkUsername"
            );
            ApiResponse<ErrorResponse> apiResponse = ApiResponse.systemError(errorResponse);
            sendApiResponse(response, apiResponse);
        }
    }
    
    /**
     * 检查邮箱是否存在
     */
    public void checkEmail(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (!AuthUtils.checkHttpMethod(request, response, this, "GET")) return;
            
            String email = request.getParameter("email");
            if (email == null || email.trim().isEmpty()) {
                ApiResponse<Object> apiResponse = ApiResponse.error("邮箱不能为空");
                sendApiResponse(response, apiResponse);
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
            ErrorResponse errorResponse = ErrorResponse.create(
                e.getClass().getSimpleName(),
                "检查邮箱失败: " + e.getMessage(),
                500,
                "/api/auth/checkEmail"
            );
            ApiResponse<ErrorResponse> apiResponse = ApiResponse.systemError(errorResponse);
            sendApiResponse(response, apiResponse);
        }
    }
}
