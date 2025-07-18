package org.frostedstar.mbtisystem.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.frostedstar.mbtisystem.entity.User;
import org.frostedstar.mbtisystem.util.PasswordUtil;

import java.time.LocalDateTime;

/**
 * 统一用户DTO
 * 支持创建、更新、查询、删除等多种操作
 * 替代原来的UserUpdateRequest、UserCreateRequest等多个DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    
    // 基本信息
    private Integer userId;
    private String username;
    private String email;
    @JsonIgnore
    private String password;
    private User.Role role;
    
    @JsonIgnore
    private LocalDateTime createdAt;
    
    // 删除操作专用字段 - 要删除的用户ID，只允许写入（接收请求），不允许读取（返回响应）
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer deleteUserId;
    
    // 操作类型标识 - 不序列化到JSON响应中
    @JsonIgnore
    private OperationType operationType;
    
    // 密码修改相关字段 - 只允许写入（接收请求），不允许读取（返回响应）
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String currentPassword;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String newPassword;
    
    /**
     * 创建请求验证 - 不序列化到JSON响应中
     */
    @JsonIgnore
    public boolean isValidForCreate() {
        return operationType == OperationType.CREATE &&
               username != null && !username.trim().isEmpty() &&
               email != null && !email.trim().isEmpty() &&
               password != null && !password.trim().isEmpty() &&
               isValidEmail(email);
    }
    
    /**
     * 密码修改请求验证 - 不序列化到JSON响应中
     */
    @JsonIgnore
    public boolean isValidForPasswordChange() {
        return operationType == OperationType.UPDATE &&
               userId != null && userId > 0 &&
               currentPassword != null && !currentPassword.trim().isEmpty() &&
               newPassword != null && !newPassword.trim().isEmpty() &&
               newPassword.length() >= 6;
    }
    
    /**
     * 更新请求验证 - 不序列化到JSON响应中
     */
    @JsonIgnore
    public boolean isValidForUpdate() {
        if (operationType != OperationType.UPDATE || userId == null || userId <= 0) {
            return false;
        }
        
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
     * 删除请求验证 - 不序列化到JSON响应中
     */
    @JsonIgnore
    public boolean isValidForDelete() {
        return operationType == OperationType.DELETE &&
               userId != null && userId > 0 &&        // 管理员ID
               deleteUserId != null && deleteUserId > 0; // 要删除的用户ID
    }
    
    /**
     * 查询请求验证 - 不序列化到JSON响应中
     */
    @JsonIgnore
    public boolean isValidForQuery() {
        return operationType == OperationType.QUERY;
    }
    
    /**
     * 通用验证方法 - 不序列化到JSON响应中
     */
    @JsonIgnore
    public boolean isValid() {
        if (operationType == null) {
            return false;
        }
        
        switch (operationType) {
            case CREATE:
                return isValidForCreate();
            case UPDATE:
                return isValidForUpdate();
            case DELETE:
                return isValidForDelete();
            case QUERY:
                return isValidForQuery();
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
     * 将UserDTO转换为User实体
     */
    public User toEntity(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }

        User user = new User();
        user.setUserId(userDTO.getUserId());
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPasswordHash(PasswordUtil.hashPassword(userDTO.getPassword()));
        user.setRole(userDTO.getRole());
        user.setCreatedAt(userDTO.getCreatedAt());

        return user;

    }

    /**
     * 从User实体转换为UserDTO
     */
    public static UserDTO fromEntity(User user) {
        if (user == null) {
            return null;
        }
        
        return UserDTO.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .operationType(OperationType.QUERY)
                .build();
    }
    
    /**
     * 创建用于创建操作的DTO
     */
    public static UserDTO forCreate(String username, String email, String password) {
        return UserDTO.builder()
                .username(username)
                .email(email)
                .password(password)
                .role(User.Role.USER) // 默认角色
                .operationType(OperationType.CREATE)
                .build();
    }
    
    /**
     * 创建用于更新操作的DTO
     */
    public static UserDTO forUpdate(Integer userId, String username, String email) {
        return UserDTO.builder()
                .userId(userId)
                .username(username)
                .email(email)
                .operationType(OperationType.UPDATE)
                .build();
    }
    
    /**
     * 创建用于删除操作的DTO
     */
    public static UserDTO forDelete(Integer adminUserId, Integer deleteUserId) {
        return UserDTO.builder()
                .userId(adminUserId)           // 管理员ID
                .deleteUserId(deleteUserId)    // 要删除的用户ID
                .operationType(OperationType.DELETE)
                .build();
    }
    
    /**
     * 创建用于查询操作的DTO
     */
    public static UserDTO forQuery() {
        return UserDTO.builder()
                .operationType(OperationType.QUERY)
                .build();
    }
    
    /**
     * 创建用于密码修改操作的DTO
     */
    public static UserDTO forPasswordChange(Integer userId, String currentPassword, String newPassword) {
        return UserDTO.builder()
                .userId(userId)
                .currentPassword(currentPassword)
                .newPassword(newPassword)
                .operationType(OperationType.UPDATE)
                .build();
    }
}
