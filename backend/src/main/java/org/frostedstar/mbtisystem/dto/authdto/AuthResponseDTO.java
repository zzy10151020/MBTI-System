package org.frostedstar.mbtisystem.dto.authdto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.frostedstar.mbtisystem.dto.userdto.UserResponseDTO;

/**
 * 认证响应DTO - 用于返回认证结果给客户端
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {
    
    // 响应相关字段
    private String sessionId;
    private String message;
    private UserResponseDTO user;
    private boolean success;
    
    /**
     * 创建登录成功响应
     */
    public static AuthResponseDTO loginSuccess(UserResponseDTO user, String sessionId) {
        return AuthResponseDTO.builder()
                .user(user)
                .sessionId(sessionId)
                .message("登录成功")
                .success(true)
                .build();
    }
    
    /**
     * 创建登录失败响应
     */
    public static AuthResponseDTO loginFailure(String message) {
        return AuthResponseDTO.builder()
                .message(message)
                .success(false)
                .build();
    }
    
    /**
     * 创建注册成功响应
     */
    public static AuthResponseDTO registerSuccess(UserResponseDTO user, String message) {
        return AuthResponseDTO.builder()
                .user(user)
                .message(message != null ? message : "注册成功")
                .success(true)
                .build();
    }
    
    /**
     * 创建注册失败响应
     */
    public static AuthResponseDTO registerFailure(String message) {
        return AuthResponseDTO.builder()
                .message(message)
                .success(false)
                .build();
    }
}
