package org.frostedstar.mbtisystem.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.frostedstar.mbtisystem.entity.User;
import org.frostedstar.mbtisystem.service.ServiceFactory;
import org.frostedstar.mbtisystem.service.UserService;
import org.frostedstar.mbtisystem.dto.ApiResponse;
import org.frostedstar.mbtisystem.dto.UserDTO;
import org.frostedstar.mbtisystem.dto.OperationType;
import org.frostedstar.mbtisystem.dto.ErrorResponse;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

/**
 * 用户控制器
 */
@Slf4j
public class UserController extends BaseController {
    
    private final UserService userService;
    
    public UserController() {
        this.userService = ServiceFactory.getUserService();
    }
    
    /**
     * 获取当前用户信息
     */
    public void get(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (!"GET".equals(request.getMethod())) {
                ApiResponse<Object> apiResponse = ApiResponse.error("Method Not Allowed");
                sendApiResponse(response, apiResponse);
                return;
            }
            
            // 检查用户是否已登录
            HttpSession session = request.getSession(false);
            if (session == null) {
                ApiResponse<Object> apiResponse = ApiResponse.error("用户未登录");
                sendApiResponse(response, apiResponse);
                return;
            }
            
            Integer userId = (Integer) session.getAttribute("userId");
            if (userId == null) {
                ApiResponse<Object> apiResponse = ApiResponse.error("用户未登录");
                sendApiResponse(response, apiResponse);
                return;
            }
            
            // 获取用户信息
            Optional<User> userOptional = userService.findById(userId);
            
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                
                // 使用DTO转换
                UserDTO userDTO = UserDTO.fromEntity(user);
                ApiResponse<UserDTO> apiResponse = ApiResponse.success(userDTO);
                
                sendApiResponse(response, apiResponse);
            } else {
                ApiResponse<Object> apiResponse = ApiResponse.error("用户不存在");
                sendApiResponse(response, apiResponse);
            }
            
        } catch (Exception e) {
            log.error("获取用户信息失败", e);
            ErrorResponse errorResponse = ErrorResponse.create(
                e.getClass().getSimpleName(),
                "获取用户信息失败: " + e.getMessage(),
                500,
                "/api/user"
            );
            ApiResponse<ErrorResponse> apiResponse = ApiResponse.systemError(errorResponse);
            sendApiResponse(response, apiResponse);
        }
    }
    
    /**
     * 更新用户信息
     */
    public void post(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (!"POST".equals(request.getMethod())) {
                ApiResponse<Object> apiResponse = ApiResponse.error("Method Not Allowed");
                sendApiResponse(response, apiResponse);
                return;
            }
            
            // 检查用户是否已登录
            HttpSession session = request.getSession(false);
            if (session == null) {
                ApiResponse<Object> apiResponse = ApiResponse.error("用户未登录");
                sendApiResponse(response, apiResponse);
                return;
            }
            
            Integer userId = (Integer) session.getAttribute("userId");
            if (userId == null) {
                ApiResponse<Object> apiResponse = ApiResponse.error("用户未登录");
                sendApiResponse(response, apiResponse);
                return;
            }
            
            // 解析请求体到DTO
            UserDTO updateRequest = parseRequestBody(request, UserDTO.class);
            updateRequest.setOperationType(OperationType.UPDATE);
            
            // 验证请求数据
            if (!updateRequest.isValid()) {
                ApiResponse<Object> apiResponse = ApiResponse.error("请求数据不完整");
                sendApiResponse(response, apiResponse);
                return;
            }
            
            // 获取当前用户信息
            Optional<User> userOptional = userService.findById(userId);
            
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                boolean updated = false;
                
                // 更新邮箱
                if (updateRequest.getEmail() != null && !updateRequest.getEmail().trim().isEmpty()) {
                    String newEmail = updateRequest.getEmail();
                    
                    // 检查邮箱是否已存在（排除当前用户）
                    Optional<User> existingUser = userService.findByEmail(newEmail);
                    if (existingUser.isPresent() && !existingUser.get().getUserId().equals(userId)) {
                        ApiResponse<Object> apiResponse = ApiResponse.error("邮箱已存在");
                        sendApiResponse(response, apiResponse);
                        return;
                    }
                    
                    user.setEmail(newEmail);
                    updated = true;
                }
                
                if (updated) {
                    boolean success = userService.update(user);
                    if (success) {
                        // 返回更新后的用户信息
                        UserDTO userDTO = UserDTO.fromEntity(user);
                        ApiResponse<UserDTO> apiResponse = ApiResponse.success("用户信息更新成功", userDTO);
                        
                        sendApiResponse(response, apiResponse);
                        log.info("用户信息更新成功: {}", user.getUsername());
                    } else {
                        ApiResponse<Object> apiResponse = ApiResponse.error("更新用户信息失败");
                        sendApiResponse(response, apiResponse);
                    }
                } else {
                    ApiResponse<Object> apiResponse = ApiResponse.error("没有可更新的内容");
                    sendApiResponse(response, apiResponse);
                }
            } else {
                ApiResponse<Object> apiResponse = ApiResponse.error("用户不存在");
                sendApiResponse(response, apiResponse);
            }
            
        } catch (Exception e) {
            log.error("更新用户信息失败", e);
            ErrorResponse errorResponse = ErrorResponse.create(
                e.getClass().getSimpleName(),
                "更新用户信息失败: " + e.getMessage(),
                500,
                "/api/user"
            );
            ApiResponse<ErrorResponse> apiResponse = ApiResponse.systemError(errorResponse);
            sendApiResponse(response, apiResponse);
        }
    }
    
    /**
     * 修改密码
     */
    public void changePassword(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (!"POST".equals(request.getMethod())) {
                ApiResponse<Object> apiResponse = ApiResponse.error("Method Not Allowed");
                sendApiResponse(response, apiResponse);
                return;
            }
            
            // 检查用户是否已登录
            HttpSession session = request.getSession(false);
            if (session == null) {
                ApiResponse<Object> apiResponse = ApiResponse.error("用户未登录");
                sendApiResponse(response, apiResponse);
                return;
            }
            
            Integer userId = (Integer) session.getAttribute("userId");
            if (userId == null) {
                ApiResponse<Object> apiResponse = ApiResponse.error("用户未登录");
                sendApiResponse(response, apiResponse);
                return;
            }
            
            // 解析请求体
            @SuppressWarnings("unchecked")
            Map<String, Object> passwordData = parseRequestBody(request, Map.class);
            
            String oldPassword = (String) passwordData.get("oldPassword");
            String newPassword = (String) passwordData.get("newPassword");
            
            // 验证参数
            if (oldPassword == null || oldPassword.trim().isEmpty()) {
                ApiResponse<Object> apiResponse = ApiResponse.error("原密码不能为空");
                sendApiResponse(response, apiResponse);
                return;
            }
            
            if (newPassword == null || newPassword.trim().isEmpty()) {
                ApiResponse<Object> apiResponse = ApiResponse.error("新密码不能为空");
                sendApiResponse(response, apiResponse);
                return;
            }
            
            if (newPassword.length() < 6) {
                ApiResponse<Object> apiResponse = ApiResponse.error("新密码长度不能少于6位");
                sendApiResponse(response, apiResponse);
                return;
            }
            
            // 修改密码
            boolean success = userService.changePassword(userId, oldPassword, newPassword);
            
            if (success) {
                ApiResponse<String> apiResponse = ApiResponse.success("密码修改成功", "密码修改成功");
                sendApiResponse(response, apiResponse);
                log.info("用户密码修改成功: {}", session.getAttribute("username"));
            } else {
                ApiResponse<Object> apiResponse = ApiResponse.error("原密码错误");
                sendApiResponse(response, apiResponse);
            }
            
        } catch (Exception e) {
            log.error("修改密码失败", e);
            ErrorResponse errorResponse = ErrorResponse.create(
                e.getClass().getSimpleName(),
                "修改密码失败: " + e.getMessage(),
                500,
                "/api/user/changePassword"
            );
            ApiResponse<ErrorResponse> apiResponse = ApiResponse.systemError(errorResponse);
            sendApiResponse(response, apiResponse);
        }
    }
    
    /**
     * 获取用户详情（根据用户ID）
     */
    public void profile(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (!"GET".equals(request.getMethod())) {
                ApiResponse<Object> apiResponse = ApiResponse.error("Method Not Allowed");
                sendApiResponse(response, apiResponse);
                return;
            }
            
            String userIdStr = request.getParameter("userId");
            if (userIdStr == null || userIdStr.trim().isEmpty()) {
                ApiResponse<Object> apiResponse = ApiResponse.error("用户ID不能为空");
                sendApiResponse(response, apiResponse);
                return;
            }
            
            Integer userId;
            try {
                userId = Integer.parseInt(userIdStr);
            } catch (NumberFormatException e) {
                ApiResponse<Object> apiResponse = ApiResponse.error("用户ID格式不正确");
                sendApiResponse(response, apiResponse);
                return;
            }
            
            // 获取用户信息
            Optional<User> userOptional = userService.findById(userId);
            
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                
                // 使用DTO转换（不包含密码和邮箱）
                UserDTO userDTO = UserDTO.fromEntity(user);
                // 移除敏感信息
                userDTO.setPassword(null);
                userDTO.setEmail(null);
                
                ApiResponse<UserDTO> apiResponse = ApiResponse.success(userDTO);
                sendApiResponse(response, apiResponse);
            } else {
                ApiResponse<Object> apiResponse = ApiResponse.error("用户不存在");
                sendApiResponse(response, apiResponse);
            }
            
        } catch (Exception e) {
            log.error("获取用户详情失败", e);
            ErrorResponse errorResponse = ErrorResponse.create(
                e.getClass().getSimpleName(),
                "获取用户详情失败: " + e.getMessage(),
                500,
                "/api/user/profile"
            );
            ApiResponse<ErrorResponse> apiResponse = ApiResponse.systemError(errorResponse);
            sendApiResponse(response, apiResponse);
        }
    }
}
