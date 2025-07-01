package org.frostedstar.mbtisystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * JWT 响应 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
    
    /**
     * JWT 令牌
     */
    private String token;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 用户角色列表
     */
    private List<String> roles;
}
