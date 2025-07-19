package org.frostedstar.mbtisystem.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.frostedstar.mbtisystem.dao.OptionDAO;
import org.frostedstar.mbtisystem.entity.Option;
import org.frostedstar.mbtisystem.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 选项 DAO 实现
 */
@Slf4j
public class OptionDAOImpl implements OptionDAO {
    
    private static final String INSERT_SQL = 
        "INSERT INTO `option` (question_id, content, score) VALUES (?, ?, ?)";
    
    private static final String SELECT_BY_ID_SQL = 
        "SELECT option_id, question_id, content, score FROM `option` WHERE option_id = ?";
    
    private static final String SELECT_ALL_SQL = 
        "SELECT option_id, question_id, content, score FROM `option` ORDER BY question_id";
    
    private static final String UPDATE_SQL = 
        "UPDATE `option` SET content = ?, score = ? WHERE option_id = ?";
    
    private static final String DELETE_SQL = 
        "DELETE FROM `option` WHERE option_id = ?";
    
    private static final String COUNT_SQL = 
        "SELECT COUNT(*) FROM `option`";
    
    private static final String SELECT_BY_QUESTION_ID_SQL = 
        "SELECT option_id, question_id, content, score FROM `option` WHERE question_id = ?";
    
    private static final String DELETE_BY_QUESTION_ID_SQL = 
        "DELETE FROM `option` WHERE question_id = ?";
    
    @Override
    public Option save(Option option) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setInt(1, option.getQuestionId());
            stmt.setString(2, option.getContent());
            stmt.setByte(3, option.getScore());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    option.setOptionId(rs.getInt(1));
                }
            }
            
            return option;
            
        } catch (SQLException e) {
            log.error("保存选项失败", e);
            throw new RuntimeException("保存选项失败", e);
        } finally {
            DatabaseUtil.closeQuietly(rs, stmt, conn);
        }
    }
    
    @Override
    public Optional<Option> findById(Integer id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(SELECT_BY_ID_SQL);
            stmt.setInt(1, id);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToOption(rs));
            }
            
            return Optional.empty();
            
        } catch (SQLException e) {
            log.error("根据ID查找选项失败", e);
            throw new RuntimeException("根据ID查找选项失败", e);
        } finally {
            DatabaseUtil.closeQuietly(rs, stmt, conn);
        }
    }
    
    @Override
    public List<Option> findAll() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Option> options = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(SELECT_ALL_SQL);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                options.add(mapResultSetToOption(rs));
            }
            
            return options;
            
        } catch (SQLException e) {
            log.error("查找所有选项失败", e);
            throw new RuntimeException("查找所有选项失败", e);
        } finally {
            DatabaseUtil.closeQuietly(rs, stmt, conn);
        }
    }
    
    @Override
    public boolean update(Option option) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(UPDATE_SQL);
            
            stmt.setString(1, option.getContent());
            stmt.setByte(2, option.getScore());
            stmt.setInt(3, option.getOptionId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            log.error("更新选项失败", e);
            throw new RuntimeException("更新选项失败", e);
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
            log.error("删除选项失败", e);
            throw new RuntimeException("删除选项失败", e);
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
            log.error("统计选项数量失败", e);
            throw new RuntimeException("统计选项数量失败", e);
        } finally {
            DatabaseUtil.closeQuietly(rs, stmt, conn);
        }
    }
    
    @Override
    public List<Option> findByQuestionId(Integer questionId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Option> options = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(SELECT_BY_QUESTION_ID_SQL);
            stmt.setInt(1, questionId);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                options.add(mapResultSetToOption(rs));
            }
            
            return options;
            
        } catch (SQLException e) {
            log.error("根据问题ID查找选项失败", e);
            throw new RuntimeException("根据问题ID查找选项失败", e);
        } finally {
            DatabaseUtil.closeQuietly(rs, stmt, conn);
        }
    }
    
    @Override
    public boolean deleteByQuestionId(Integer questionId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(DELETE_BY_QUESTION_ID_SQL);
            stmt.setInt(1, questionId);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            log.error("根据问题ID删除选项失败", e);
            throw new RuntimeException("根据问题ID删除选项失败", e);
        } finally {
            DatabaseUtil.closeQuietly(stmt, conn);
        }
    }
    
    @Override
    public List<Option> saveBatch(List<Option> options) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);
            
            stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
            
            for (Option option : options) {
                stmt.setInt(1, option.getQuestionId());
                stmt.setString(2, option.getContent());
                stmt.setByte(3, option.getScore());
                stmt.addBatch();
            }
            
            stmt.executeBatch();
            
            // 获取生成的键
            rs = stmt.getGeneratedKeys();
            int index = 0;
            while (rs.next() && index < options.size()) {
                options.get(index).setOptionId(rs.getInt(1));
                index++;
            }
            
            conn.commit();
            return options;
            
        } catch (SQLException e) {
            log.error("批量保存选项失败", e);
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                log.error("回滚事务失败", ex);
            }
            throw new RuntimeException("批量保存选项失败", e);
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException e) {
                log.error("重置自动提交失败", e);
            }
            DatabaseUtil.closeQuietly(rs, stmt, conn);
        }
    }
    
    /**
     * 将 ResultSet 映射到 Option 对象
     */
    private Option mapResultSetToOption(ResultSet rs) throws SQLException {
        return Option.builder()
                .optionId(rs.getInt("option_id"))
                .questionId(rs.getInt("question_id"))
                .content(rs.getString("content"))
                .score(rs.getByte("score"))
                .build();
    }
}
