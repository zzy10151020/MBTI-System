package org.frostedstar.mbtisystem.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    private Integer userId;
    private String username;
    private String passwordHash;
    private String email;
    private Role role;
    private LocalDateTime createdAt;
    
    /**
     * 用户角色枚举
     */
    public enum Role {
        USER, ADMIN
    }
}
