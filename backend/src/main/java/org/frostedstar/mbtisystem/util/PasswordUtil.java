package org.frostedstar.mbtisystem.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Scanner;

/**
 * 密码加密工具类
 */
public class PasswordUtil {
    
    private static final String ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 32;
    
    /**
     * 生成随机盐
     */
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
    
    /**
     * 加密密码
     */
    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(Base64.getDecoder().decode(salt));
            byte[] hashedPassword = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("密码加密失败", e);
        }
    }
    
    /**
     * 加密密码（自动生成盐）
     */
    public static String hashPassword(String password) {
        String salt = generateSalt();
        String hashedPassword = hashPassword(password, salt);
        return salt + ":" + hashedPassword;
    }
    
    /**
     * 验证密码
     */
    public static boolean verifyPassword(String password, String hashedPassword) {
        try {
            String[] parts = hashedPassword.split(":");
            if (parts.length != 2) {
                return false;
            }
            
            String salt = parts[0];
            String hash = parts[1];
            
            String newHash = hashPassword(password, salt);
            return newHash.equals(hash);
        } catch (Exception e) {
            return false;
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("--- 密碼雜湊生成工具 ---");
        System.out.print("請輸入要加密的密碼: ");
        String password = scanner.nextLine();

        if (password != null && !password.trim().isEmpty()) {
            String hashedPassword = hashPassword(password.trim());
            System.out.println("\n生成的密碼雜湊值 (可直接用於SQL):");
            System.out.println(hashedPassword);
            System.out.println(verifyPassword(password, hashedPassword));
        } else {
            System.out.println("密碼不能為空!");
        }
        
        scanner.close();
    }
}
