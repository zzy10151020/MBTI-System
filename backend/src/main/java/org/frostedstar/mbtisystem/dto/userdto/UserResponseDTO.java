package org.frostedstar.mbtisystem.dto.userdto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.frostedstar.mbtisystem.entity.User;

import java.time.LocalDateTime;

/**
 * 用户响应DTO
 * 用于返回用户数据给客户端
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    
    // 基本信息
    private Integer userId;
    private String username;
    private String email;
    private User.Role role;
    private LocalDateTime createdAt;
    
    /**
     * 从User实体转换为UserResponseDTO
     */
    public static UserResponseDTO fromEntity(User user) {
        if (user == null) {
            return null;
        }
        
        return UserResponseDTO.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }

    /**
     * 从 User实体转换为UserResponseDTO（信息安全版本）
     */
    public static UserResponseDTO fromEntitySecure(User user) {
        if (user == null) {
            return null;
        }
        
        return UserResponseDTO.builder()
                    .userId(user.getUserId())
                    .username(user.getUsername())
                    .role(user.getRole())
                    .createdAt(user.getCreatedAt())
                    .build();
    }
}
