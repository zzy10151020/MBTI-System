package org.frostedstar.mbtisystem.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * JWT 配置类
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "app.jwt")
public class JwtConfig {
    
    /**
     * JWT 密钥
     */
    private String secret = "mySecretKey";
    
    /**
     * JWT 过期时间（毫秒）
     */
    private int expirationMs = 86400000; // 24小时
    
    /**
     * JWT 刷新令牌过期时间（毫秒）
     */
    private int refreshExpirationMs = 604800000; // 7天
    
    /**
     * JWT 令牌前缀
     */
    private String tokenPrefix = "Bearer ";
    
    /**
     * JWT 令牌头名称
     */
    private String headerName = "Authorization";
    
    /**
     * JWT 发行者
     */
    private String issuer = "mbti-system";
    
    /**
     * JWT 受众
     */
    private String audience = "mbti-users";
}
