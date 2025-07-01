package org.frostedstar.mbtisystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.frostedstar.mbtisystem.model.UserRole;

import java.time.LocalDateTime;

/**
 * 用户数据传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 用户角色
     */
    private UserRole role;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 回答问卷数量
     */
    private Long answerCount;
}
