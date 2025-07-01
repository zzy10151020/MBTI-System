package org.frostedstar.mbtisystem.config;

import org.frostedstar.mbtisystem.model.User;
import org.frostedstar.mbtisystem.model.UserRole;
import org.frostedstar.mbtisystem.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 数据初始化配置
 */
@Configuration
public class DataInitConfig {

    private static final Logger logger = LoggerFactory.getLogger(DataInitConfig.class);

    /**
     * 初始化管理员用户
     */
    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // 检查是否已存在管理员用户
            if (!userRepository.existsByUsername("admin")) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPasswordHash(passwordEncoder.encode("admin123"));
                admin.setEmail("admin@mbti.com");
                admin.setRole(UserRole.ADMIN);
                
                userRepository.save(admin);
                logger.info("默认管理员用户已创建: username=admin, password=admin123");
            }
            
            // 检查是否已存在测试用户
            if (!userRepository.existsByUsername("testuser")) {
                User testUser = new User();
                testUser.setUsername("testuser");
                testUser.setPasswordHash(passwordEncoder.encode("test123"));
                testUser.setEmail("test@mbti.com");
                testUser.setRole(UserRole.USER);
                
                userRepository.save(testUser);
                logger.info("默认测试用户已创建: username=testuser, password=test123");
            }
        };
    }
}
