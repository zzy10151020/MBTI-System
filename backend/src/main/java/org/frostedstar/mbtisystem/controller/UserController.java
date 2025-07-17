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
     * 更新用户信息（统一方法）
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
            updateRequest.setUserId(userId); // 设置用户ID
            
            // 调用统一的更新方法
            updateUserInfo(updateRequest, response, session);
            
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
     * 统一的用户信息更新方法
     */
    private void updateUserInfo(UserDTO updateRequest, HttpServletResponse response, HttpSession session) throws IOException {
        Integer userId = updateRequest.getUserId();
        
        // 获取当前用户信息
        Optional<User> userOptional = userService.findById(userId);
        
        if (userOptional.isEmpty()) {
            ApiResponse<Object> apiResponse = ApiResponse.error("用户不存在");
            sendApiResponse(response, apiResponse);
            return;
        }
        
        User user = userOptional.get();
        boolean updated = false;
        String successMessage = "更新成功";
        
        // 处理密码修改
        if (updateRequest.getCurrentPassword() != null && updateRequest.getNewPassword() != null) {
            // 验证密码修改参数
            if (updateRequest.getCurrentPassword().trim().isEmpty()) {
                ApiResponse<Object> apiResponse = ApiResponse.error("原密码不能为空");
                sendApiResponse(response, apiResponse);
                return;
            }
            
            if (updateRequest.getNewPassword().trim().isEmpty()) {
                ApiResponse<Object> apiResponse = ApiResponse.error("新密码不能为空");
                sendApiResponse(response, apiResponse);
                return;
            }
            
            if (updateRequest.getNewPassword().length() < 6) {
                ApiResponse<Object> apiResponse = ApiResponse.error("新密码长度不能少于6位");
                sendApiResponse(response, apiResponse);
                return;
            }
            
            // 修改密码
            boolean passwordChanged = userService.changePassword(userId, updateRequest.getCurrentPassword(), updateRequest.getNewPassword());
            
            if (!passwordChanged) {
                ApiResponse<Object> apiResponse = ApiResponse.error("原密码错误");
                sendApiResponse(response, apiResponse);
                return;
            }
            
            updated = true;
            successMessage = "密码修改成功";
            log.info("用户密码修改成功: {}", session.getAttribute("username"));
        }
        
        // 处理邮箱更新
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
            successMessage = "用户信息更新成功";
        }
        
        // 处理用户名更新
        if (updateRequest.getUsername() != null && !updateRequest.getUsername().trim().isEmpty()) {
            String newUsername = updateRequest.getUsername();
            
            // 检查用户名是否已存在（排除当前用户）
            Optional<User> existingUser = userService.findByUsername(newUsername);
            if (existingUser.isPresent() && !existingUser.get().getUserId().equals(userId)) {
                ApiResponse<Object> apiResponse = ApiResponse.error("用户名已存在");
                sendApiResponse(response, apiResponse);
                return;
            }
            
            user.setUsername(newUsername);
            updated = true;
            successMessage = "用户信息更新成功";
        }
        
        // 处理角色更新
        if (updateRequest.getRole() != null) {
            user.setRole(updateRequest.getRole());
            updated = true;
            successMessage = "用户信息更新成功";
        }
        
        // 如果有非密码字段更新，保存到数据库
        if (updated && (updateRequest.getEmail() != null || updateRequest.getUsername() != null || updateRequest.getRole() != null)) {
            boolean success = userService.update(user);
            if (!success) {
                ApiResponse<Object> apiResponse = ApiResponse.error("更新用户信息失败");
                sendApiResponse(response, apiResponse);
                return;
            }
            log.info("用户信息更新成功: {}", user.getUsername());
        }
        
        if (updated) {
            // 根据更新类型返回相应的响应
            if (updateRequest.getCurrentPassword() != null) {
                // 密码修改成功，返回简单消息
                ApiResponse<String> apiResponse = ApiResponse.success(successMessage, successMessage);
                sendApiResponse(response, apiResponse);
            } else {
                // 用户信息更新成功，返回更新后的用户信息
                UserDTO userDTO = UserDTO.fromEntity(user);
                ApiResponse<UserDTO> apiResponse = ApiResponse.success(successMessage, userDTO);
                sendApiResponse(response, apiResponse);
            }
        } else {
            ApiResponse<Object> apiResponse = ApiResponse.error("没有可更新的内容");
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
            
            int userId;
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
