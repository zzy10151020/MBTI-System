package org.frostedstar.mbtisystem.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.frostedstar.mbtisystem.dao.UserDAO;
import org.frostedstar.mbtisystem.entity.User;
import org.frostedstar.mbtisystem.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 用户 DAO 实现
 */
@Slf4j
public class UserDAOImpl implements UserDAO {
    
    private static final String INSERT_SQL = 
        "INSERT INTO user (username, password_hash, email, role, created_at) VALUES (?, ?, ?, ?, ?)";
    
    private static final String SELECT_BY_ID_SQL = 
        "SELECT user_id, username, password_hash, email, role, created_at FROM user WHERE user_id = ?";
    
    private static final String SELECT_ALL_SQL = 
        "SELECT user_id, username, password_hash, email, role, created_at FROM user ORDER BY created_at DESC";
    
    private static final String UPDATE_SQL = 
        "UPDATE user SET username = ?, password_hash = ?, email = ?, role = ? WHERE user_id = ?";
    
    private static final String DELETE_SQL = 
        "DELETE FROM user WHERE user_id = ?";
    
    private static final String COUNT_SQL = 
        "SELECT COUNT(*) FROM user";
    
    private static final String SELECT_BY_USERNAME_SQL = 
        "SELECT user_id, username, password_hash, email, role, created_at FROM user WHERE username = ?";
    
    private static final String SELECT_BY_EMAIL_SQL = 
        "SELECT user_id, username, password_hash, email, role, created_at FROM user WHERE email = ?";
    
    private static final String EXISTS_BY_USERNAME_SQL = 
        "SELECT 1 FROM user WHERE username = ?";
    
    private static final String EXISTS_BY_EMAIL_SQL = 
        "SELECT 1 FROM user WHERE email = ?";
    
    @Override
    public User save(User user) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getRole().name().toLowerCase());
            stmt.setTimestamp(5, Timestamp.valueOf(user.getCreatedAt() != null ? 
                user.getCreatedAt() : LocalDateTime.now()));
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    user.setUserId(rs.getInt(1));
                }
            }
            
            return user;
            
        } catch (SQLException e) {
            log.error("保存用户失败", e);
            throw new RuntimeException("保存用户失败", e);
        } finally {
            DatabaseUtil.closeQuietly(rs, stmt, conn);
        }
    }
    
    @Override
    public Optional<User> findById(Integer id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(SELECT_BY_ID_SQL);
            stmt.setInt(1, id);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToUser(rs));
            }
            
            return Optional.empty();
            
        } catch (SQLException e) {
            log.error("根据ID查找用户失败", e);
            throw new RuntimeException("根据ID查找用户失败", e);
        } finally {
            DatabaseUtil.closeQuietly(rs, stmt, conn);
        }
    }
    
    @Override
    public List<User> findAll() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<User> users = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(SELECT_ALL_SQL);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
            
            return users;
            
        } catch (SQLException e) {
            log.error("查找所有用户失败", e);
            throw new RuntimeException("查找所有用户失败", e);
        } finally {
            DatabaseUtil.closeQuietly(rs, stmt, conn);
        }
    }
    
    @Override
    public boolean update(User user) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(UPDATE_SQL);
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getRole().name().toLowerCase());
            stmt.setInt(5, user.getUserId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            log.error("更新用户失败", e);
            throw new RuntimeException("更新用户失败", e);
        } finally {
            DatabaseUtil.closeQuietly(stmt, conn);
        }
    }
    
    @Override
    public boolean deleteById(Integer id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(DELETE_SQL);
            stmt.setInt(1, id);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            log.error("删除用户失败", e);
            throw new RuntimeException("删除用户失败", e);
        } finally {
            DatabaseUtil.closeQuietly(stmt, conn);
        }
    }
    
    @Override
    public long count() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(COUNT_SQL);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getLong(1);
            }
            
            return 0;
            
        } catch (SQLException e) {
            log.error("统计用户数量失败", e);
            throw new RuntimeException("统计用户数量失败", e);
        } finally {
            DatabaseUtil.closeQuietly(rs, stmt, conn);
        }
    }
    
    @Override
    public Optional<User> findByUsername(String username) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(SELECT_BY_USERNAME_SQL);
            stmt.setString(1, username);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToUser(rs));
            }
            
            return Optional.empty();
            
        } catch (SQLException e) {
            log.error("根据用户名查找用户失败", e);
            throw new RuntimeException("根据用户名查找用户失败", e);
        } finally {
            DatabaseUtil.closeQuietly(rs, stmt, conn);
        }
    }
    
    @Override
    public Optional<User> findByEmail(String email) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(SELECT_BY_EMAIL_SQL);
            stmt.setString(1, email);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToUser(rs));
            }
            
            return Optional.empty();
            
        } catch (SQLException e) {
            log.error("根据邮箱查找用户失败", e);
            throw new RuntimeException("根据邮箱查找用户失败", e);
        } finally {
            DatabaseUtil.closeQuietly(rs, stmt, conn);
        }
    }
    
    @Override
    public boolean existsByUsername(String username) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(EXISTS_BY_USERNAME_SQL);
            stmt.setString(1, username);
            
            rs = stmt.executeQuery();
            return rs.next();
            
        } catch (SQLException e) {
            log.error("检查用户名是否存在失败", e);
            throw new RuntimeException("检查用户名是否存在失败", e);
        } finally {
            DatabaseUtil.closeQuietly(rs, stmt, conn);
        }
    }
    
    @Override
    public boolean existsByEmail(String email) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(EXISTS_BY_EMAIL_SQL);
            stmt.setString(1, email);
            
            rs = stmt.executeQuery();
            return rs.next();
            
        } catch (SQLException e) {
            log.error("检查邮箱是否存在失败", e);
            throw new RuntimeException("检查邮箱是否存在失败", e);
        } finally {
            DatabaseUtil.closeQuietly(rs, stmt, conn);
        }
    }
    
    /**
     * 将 ResultSet 映射到 User 对象
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        return User.builder()
                .userId(rs.getInt("user_id"))
                .username(rs.getString("username"))
                .passwordHash(rs.getString("password_hash"))
                .email(rs.getString("email"))
                .role(User.Role.valueOf(rs.getString("role").toUpperCase()))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .build();
    }
}
