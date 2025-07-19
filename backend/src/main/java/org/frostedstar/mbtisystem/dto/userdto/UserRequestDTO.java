package org.frostedstar.mbtisystem.dto.userdto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.frostedstar.mbtisystem.entity.User;
import org.frostedstar.mbtisystem.util.PasswordUtil;

/**
 * 用户请求DTO - 用于处理各种用户相关的请求参数
 * 支持查询、创建、更新、删除等操作
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO {
    
    // 创建/更新相关字段
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 密码（仅创建时使用）
     */
    @JsonIgnore
    private String password;
    
    /**
     * 角色
     */
    private User.Role role;
    
    // 密码修改相关字段
    /**
     * 当前密码
     */
    @JsonIgnore
    private String currentPassword;
    
    /**
     * 新密码
     */
    @JsonIgnore
    private String newPassword;
    
    // 删除操作相关字段
    /**
     * 要删除的用户ID
     */
    private Integer deleteUserId;
    
    // 验证方法
    
    /**
     * 创建用户请求验证
     */
    public boolean isValidForCreateUser() {
        return username != null && !username.trim().isEmpty() &&
               email != null && !email.trim().isEmpty() &&
               password != null && !password.trim().isEmpty() &&
               isValidEmail(email);
    }
    
    /**
     * 修改用户密码请求验证
     */
    public boolean isValidForChangePassword() {
        return currentPassword != null && !currentPassword.trim().isEmpty() &&
               newPassword != null && !newPassword.trim().isEmpty() &&
               newPassword.length() >= 6;
    }
    
    /**
     * 更新用户信息请求验证
     */
    public boolean isValidForUpdateUser() {
        // 检查是否有至少一个可更新的字段
        boolean hasUpdateField = false;
        
        // 检查用户名
        if (username != null && !username.trim().isEmpty()) {
            hasUpdateField = true;
        }
        
        // 检查邮箱
        if (email != null && !email.trim().isEmpty()) {
            if (!isValidEmail(email)) {
                return false; // 邮箱格式不正确
            }
            hasUpdateField = true;
        }
        
        // 检查角色
        if (role != null) {
            hasUpdateField = true;
        }
        
        // 检查密码修改
        if (currentPassword != null && newPassword != null) {
            if (currentPassword.trim().isEmpty() || newPassword.trim().isEmpty()) {
                return false;
            }
            if (newPassword.length() < 6) {
                return false;
            }
            hasUpdateField = true;
        }
        
        return hasUpdateField;
    }
    
    /**
     * 删除用户请求验证 - 管理员删除其他用户
     */
    public boolean isValidForDeleteUser() {
        return deleteUserId != null && deleteUserId > 0; // 要删除的用户ID
    }
    
    /**
     * 查询用户请求验证
     */
    public boolean isValidForQueryUser() {
        return true; // 查询通常不需要特殊验证
    }
    
    /**
     * 邮箱格式验证
     */
    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
    
    /**
     * 从请求DTO转换为User实体
     */
    public User toEntity() {
        User user = new User();
        // 注意：userId由服务端设置，不从请求中获取
        user.setUsername(this.username);
        user.setEmail(this.email);
        
        // 只有在密码非空时才进行hash处理（用于创建用户场景）
        if (this.password != null && !this.password.trim().isEmpty()) {
            user.setPasswordHash(PasswordUtil.hashPassword(this.password));
        }
        
        user.setRole(this.role);
        
        return user;
    }
    
    /**
     * 静态方法：从请求DTO转换为User实体
     */
    public static User toEntity(UserRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        return dto.toEntity();
    }
}
