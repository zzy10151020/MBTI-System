package org.frostedstar.mbtisystem.dto.authdto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * 认证请求DTO - 用于处理登录、注册等请求参数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequestDTO {
    
    // 基本信息
    private String username;
    private String password;
    private String email;
    
    // 验证方法
    
    /**
     * 登录请求验证
     */
    public boolean isValidForLogin() {
        return username != null && !username.trim().isEmpty() &&
               password != null && !password.trim().isEmpty();
    }
    
    /**
     * 注册请求验证
     */
    public boolean isValidForRegister() {
        return username != null && !username.trim().isEmpty() &&
               password != null && !password.trim().isEmpty() &&
               email != null && !email.trim().isEmpty() &&
               isValidEmail(email);
    }
    
    /**
     * 邮箱格式验证
     */
    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
}
