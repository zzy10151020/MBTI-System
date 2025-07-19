package org.frostedstar.mbtisystem.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.frostedstar.mbtisystem.dto.authdto.*;
import org.frostedstar.mbtisystem.dto.ApiResponse;
import org.frostedstar.mbtisystem.dto.userdto.UserResponseDTO;
import org.frostedstar.mbtisystem.entity.User;
import org.frostedstar.mbtisystem.service.ServiceFactory;
import org.frostedstar.mbtisystem.service.UserService;
import org.frostedstar.mbtisystem.servlet.Route;

import java.io.IOException;
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
            
            AuthRequestDTO loginRequest = parseRequestBody(request, AuthRequestDTO.class);
            
            // 验证参数
            if (!loginRequest.isValidForLogin()) {
                AuthResponseDTO authResponse = AuthResponseDTO.loginFailure("登录参数不完整或格式错误");
                ApiResponse<AuthResponseDTO> apiResponse = ApiResponse.success("登录参数不完整或格式错误", authResponse);
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
                
                // 使用新的ResponseDTO转换
                UserResponseDTO userDTO = UserResponseDTO.fromEntity(user);
                AuthResponseDTO authResponse = AuthResponseDTO.loginSuccess(userDTO, session.getId());
                
                ApiResponse<AuthResponseDTO> apiResponse = ApiResponse.success("登录成功", authResponse);
                sendApiResponse(response, apiResponse);
                log.info("用户登录成功: {}", user.getUsername());
            } else {
                AuthResponseDTO authResponse = AuthResponseDTO.loginFailure("用户名或密码错误");
                ApiResponse<AuthResponseDTO> apiResponse = ApiResponse.success("用户名或密码错误", authResponse);
                sendApiResponse(response, apiResponse);
            }
            
        } catch (Exception e) {
            log.error("用户登录失败", e);
            AuthResponseDTO authResponse = AuthResponseDTO.loginFailure("登录失败: " + e.getMessage());
            ApiResponse<AuthResponseDTO> apiResponse = ApiResponse.success("登录失败: " + e.getMessage(), authResponse);
            sendApiResponse(response, apiResponse);
        }
    }
    
    /**
     * 用户注册
     */
    @Route(value = "/register", method = "POST")
    public void registerUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (!AuthUtils.checkHttpMethod(request, response, this, "POST")) return;
            
            AuthRequestDTO registerRequest = parseRequestBody(request, AuthRequestDTO.class);
            
            // 验证参数
            if (!registerRequest.isValidForRegister()) {
                AuthResponseDTO authResponse = AuthResponseDTO.registerFailure("注册参数不完整或格式错误");
                ApiResponse<AuthResponseDTO> apiResponse = ApiResponse.success("注册参数不完整或格式错误", authResponse);
                sendApiResponse(response, apiResponse);
                return;
            }
            
            // 验证用户名长度
            if (registerRequest.getUsername().length() < 3 || registerRequest.getUsername().length() > 20) {
                AuthResponseDTO authResponse = AuthResponseDTO.registerFailure("用户名长度必须在3-20个字符之间");
                ApiResponse<AuthResponseDTO> apiResponse = ApiResponse.success("用户名长度必须在3-20个字符之间", authResponse);
                sendApiResponse(response, apiResponse);
                return;
            }
            
            // 验证密码长度
            if (registerRequest.getPassword().length() < 6) {
                AuthResponseDTO authResponse = AuthResponseDTO.registerFailure("密码长度不能少于6位");
                ApiResponse<AuthResponseDTO> apiResponse = ApiResponse.success("密码长度不能少于6位", authResponse);
                sendApiResponse(response, apiResponse);
                return;
            }
            
            // 用户注册
            User user = userService.register(
                registerRequest.getUsername(),
                registerRequest.getPassword(),
                registerRequest.getEmail()
            );
            
            // 使用新的ResponseDTO转换
            UserResponseDTO userDTO = UserResponseDTO.fromEntity(user);
            AuthResponseDTO authResponse = AuthResponseDTO.registerSuccess(userDTO, "注册成功");
            
            ApiResponse<AuthResponseDTO> apiResponse = ApiResponse.success("注册成功", authResponse);
            sendApiResponse(response, apiResponse);
            log.info("用户注册成功: {}", user.getUsername());
            
        } catch (RuntimeException e) {
            log.error("用户注册失败", e);
            AuthResponseDTO authResponse = AuthResponseDTO.registerFailure(e.getMessage());
            ApiResponse<AuthResponseDTO> apiResponse = ApiResponse.success(e.getMessage(), authResponse);
            sendApiResponse(response, apiResponse);
        } catch (Exception e) {
            log.error("用户注册失败", e);
            AuthResponseDTO authResponse = AuthResponseDTO.registerFailure("注册失败: " + e.getMessage());
            ApiResponse<AuthResponseDTO> apiResponse = ApiResponse.success("注册失败: " + e.getMessage(), authResponse);
            sendApiResponse(response, apiResponse);
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
            
            ApiResponse<Object> apiResponse = ApiResponse.error("该功能已被禁用，请使用POST方式查询");
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
            
            ApiResponse<Object> apiResponse = ApiResponse.error("该功能已被禁用，请使用POST方式查询");
            sendApiResponse(response, apiResponse);
            
        } catch (Exception e) {
            log.error("检查邮箱失败", e);
            sendErrorResponse(response, 500, "检查邮箱失败: " + e.getMessage(), "/api/auth/checkEmail");
        }
    }
}
