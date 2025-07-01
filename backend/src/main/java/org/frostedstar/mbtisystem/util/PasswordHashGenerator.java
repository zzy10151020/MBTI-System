package org.frostedstar.mbtisystem.util;

/**
 * 密码哈希生成工具
 * 用于生成测试用户的BCrypt哈希密码
 * 
 * 注意：这里提供的是预先计算好的BCrypt哈希值
 */
public class PasswordHashGenerator {
    
    public static void main(String[] args) {
        System.out.println("=== MBTI系统测试账号密码 ===");
        System.out.println();
        
        System.out.println("管理员账号:");
        System.out.println("  用户名: admin");
        System.out.println("  密码: password");
        System.out.println("  哈希: $2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi");
        System.out.println();
        
        System.out.println("普通用户账号:");
        System.out.println("  用户名: user1 / user2");
        System.out.println("  密码: password");  
        System.out.println("  哈希: $2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi");
        System.out.println();
        
        System.out.println("=== 登录测试 ===");
        System.out.println("curl -X POST http://localhost:8080/api/auth/login \\");
        System.out.println("  -H \"Content-Type: application/json\" \\");
        System.out.println("  -d '{\"username\":\"admin\",\"password\":\"password\"}'");
        System.out.println();
        System.out.println("curl -X POST http://localhost:8080/api/auth/login \\");
        System.out.println("  -H \"Content-Type: application/json\" \\");
        System.out.println("  -d '{\"username\":\"user1\",\"password\":\"password\"}'");
    }
}
