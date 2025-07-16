package org.frostedstar.mbtisystem.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * 统一认证DTO
 * 支持登录、注册等多种认证操作
 * 替代原来的LoginRequest、RegisterRequest
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthDTO {
    
    // 基本信息
    private String username;
    private String password;
    private String email;
    
    // 操作类型标识
    private OperationType operationType;
    
    // 响应相关字段
    private String sessionId;
    private String message;
    private UserDTO user;
    
    /**
     * 登录请求验证
     */
    public boolean isValidForLogin() {
        return operationType == OperationType.QUERY &&
               username != null && !username.trim().isEmpty() &&
               password != null && !password.trim().isEmpty();
    }
    
    /**
     * 注册请求验证
     */
    public boolean isValidForRegister() {
        return operationType == OperationType.CREATE &&
               username != null && !username.trim().isEmpty() &&
               password != null && !password.trim().isEmpty() &&
               email != null && !email.trim().isEmpty() &&
               isValidEmail(email);
    }
    
    /**
     * 响应验证 - 用于返回认证结果
     */
    public boolean isValidForResponse() {
        return operationType == OperationType.QUERY &&
               message != null && !message.trim().isEmpty();
    }
    
    /**
     * 通用验证方法
     */
    public boolean isValid() {
        if (operationType == null) {
            return false;
        }
        
        switch (operationType) {
            case CREATE:
                return isValidForRegister();
            case QUERY:
                return isValidForLogin() || isValidForResponse();
            case UPDATE:
            case DELETE:
                return false; // 认证DTO不支持更新和删除操作
            default:
                return false;
        }
    }
    
    /**
     * 邮箱格式验证
     */
    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
    
    /**
     * 创建用于登录操作的DTO
     */
    public static AuthDTO forLogin(String username, String password) {
        return AuthDTO.builder()
                .username(username)
                .password(password)
                .operationType(OperationType.QUERY)
                .build();
    }
    
    /**
     * 创建用于注册操作的DTO
     */
    public static AuthDTO forRegister(String username, String password, String email) {
        return AuthDTO.builder()
                .username(username)
                .password(password)
                .email(email)
                .operationType(OperationType.CREATE)
                .build();
    }
    
    /**
     * 创建登录成功响应
     */
    public static AuthDTO loginSuccess(UserDTO user, String sessionId) {
        return AuthDTO.builder()
                .user(user)
                .sessionId(sessionId)
                .message("登录成功")
                .operationType(OperationType.QUERY)
                .build();
    }
    
    /**
     * 创建登录失败响应
     */
    public static AuthDTO loginFailure(String message) {
        return AuthDTO.builder()
                .message(message)
                .operationType(OperationType.QUERY)
                .build();
    }
    
    /**
     * 创建注册成功响应
     */
    public static AuthDTO registerSuccess(UserDTO user, String message) {
        return AuthDTO.builder()
                .user(user)
                .message(message != null ? message : "注册成功")
                .operationType(OperationType.CREATE)
                .build();
    }
}
