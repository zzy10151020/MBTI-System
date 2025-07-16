package org.frostedstar.mbtisystem.dao;

import org.frostedstar.mbtisystem.entity.User;

import java.util.Optional;

/**
 * 用户 DAO 接口
 */
public interface UserDAO extends BaseDAO<User, Integer> {
    
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
}
