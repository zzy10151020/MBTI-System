package org.frostedstar.mbtisystem.service;

import org.frostedstar.mbtisystem.entity.User;

import java.util.Optional;

/**
 * 用户 Service 接口
 */
public interface UserService extends BaseService<User, Integer> {
    
    /**
     * 用户注册
     */
    User register(String username, String password, String email);
    
    /**
     * 用户登录
     */
    Optional<User> login(String username, String password);
    
    /**
     * 根据用户名查找用户
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 根据邮箱查找用户
     */
    Optional<User> findByEmail(String email);
    
    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(String email);
    
    /**
     * 修改密码
     */
    boolean changePassword(Integer userId, String oldPassword, String newPassword);

    /**
     * 管理员重置用户密码
     */
    boolean resetPasswordByAdmin(Integer userId, String newPassword);
    
    /**
     * 级联删除用户（同时删除用户的所有答题记录）
     */
    boolean deleteUserWithCascade(Integer userId);
}
