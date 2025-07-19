package org.frostedstar.mbtisystem.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.frostedstar.mbtisystem.dao.UserDAO;
import org.frostedstar.mbtisystem.dao.impl.UserDAOImpl;
import org.frostedstar.mbtisystem.entity.User;
import org.frostedstar.mbtisystem.service.UserService;
import org.frostedstar.mbtisystem.util.PasswordUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户 Service 实现
 */
@Slf4j
public class UserServiceImpl implements UserService {
    
    private final UserDAO userDAO;
    
    public UserServiceImpl() {
        this.userDAO = new UserDAOImpl();
    }
    
    @Override
    public User register(String username, String password, String email) {
        // 检查用户名是否存在
        if (userDAO.existsByUsername(username)) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 检查邮箱是否存在
        if (userDAO.existsByEmail(email)) {
            throw new RuntimeException("邮箱已存在");
        }
        
        // 创建用户
        User user = User.builder()
                .username(username)
                .passwordHash(PasswordUtil.hashPassword(password))
                .email(email)
                .role(User.Role.USER)
                .createdAt(LocalDateTime.now())
                .build();
        
        User savedUser = userDAO.save(user);
        log.info("用户注册成功: {}", username);
        return savedUser;
    }
    
    @Override
    public Optional<User> login(String username, String password) {
        Optional<User> userOptional = userDAO.findByUsername(username);
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (PasswordUtil.verifyPassword(password, user.getPasswordHash())) {
                log.info("用户登录成功: {}", username);
                return Optional.of(user);
            }
        }
        
        log.warn("用户登录失败: {}", username);
        return Optional.empty();
    }
    
    @Override
    public Optional<User> findByUsername(String username) {
        return userDAO.findByUsername(username);
    }
    
    @Override
    public Optional<User> findByEmail(String email) {
        return userDAO.findByEmail(email);
    }
    
    @Override
    public boolean existsByUsername(String username) {
        return userDAO.existsByUsername(username);
    }
    
    @Override
    public boolean existsByEmail(String email) {
        return userDAO.existsByEmail(email);
    }
    
    @Override
    public boolean changePassword(Integer userId, String oldPassword, String newPassword) {
        Optional<User> userOptional = userDAO.findById(userId);
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            
            // 验证旧密码
            if (PasswordUtil.verifyPassword(oldPassword, user.getPasswordHash())) {
                // 更新密码
                user.setPasswordHash(PasswordUtil.hashPassword(newPassword));
                boolean updated = userDAO.update(user);
                
                if (updated) {
                    log.info("用户密码修改成功: {}", user.getUsername());
                }
                return updated;
            } else {
                log.warn("用户密码修改失败，旧密码错误: {}", user.getUsername());
            }
        }
        
        return false;
    }
    
    @Override
    public User save(User user) {
        return userDAO.save(user);
    }
    
    @Override
    public Optional<User> findById(Integer id) {
        return userDAO.findById(id);
    }
    
    @Override
    public List<User> findAll() {
        return userDAO.findAll();
    }
    
    @Override
    public boolean update(User user) {
        return userDAO.update(user);
    }
    
    @Override
    public boolean deleteById(Integer id) {
        return userDAO.deleteById(id);
    }
    
    @Override
    public long count() {
        return userDAO.count();
    }
}
