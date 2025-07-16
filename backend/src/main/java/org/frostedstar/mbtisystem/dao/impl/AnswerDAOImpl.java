package org.frostedstar.mbtisystem.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.frostedstar.mbtisystem.dao.AnswerDAO;
import org.frostedstar.mbtisystem.entity.Answer;
import org.frostedstar.mbtisystem.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 回答 DAO 实现
 */
@Slf4j
public class AnswerDAOImpl implements AnswerDAO {
    
    private static final String INSERT_SQL = 
        "INSERT INTO answer (user_id, questionnaire_id, answered_at) VALUES (?, ?, ?)";
    
    private static final String SELECT_BY_ID_SQL = 
        "SELECT answer_id, user_id, questionnaire_id, answered_at FROM answer WHERE answer_id = ?";
    
    private static final String SELECT_ALL_SQL = 
        "SELECT answer_id, user_id, questionnaire_id, answered_at FROM answer ORDER BY answered_at DESC";
    
    private static final String UPDATE_SQL = 
        "UPDATE answer SET answered_at = ? WHERE answer_id = ?";
    
    private static final String DELETE_SQL = 
        "DELETE FROM answer WHERE answer_id = ?";
    
    private static final String COUNT_SQL = 
        "SELECT COUNT(*) FROM answer";
    
    private static final String SELECT_BY_USER_ID_SQL = 
        "SELECT answer_id, user_id, questionnaire_id, answered_at FROM answer WHERE user_id = ? ORDER BY answered_at DESC";
    
    private static final String SELECT_BY_QUESTIONNAIRE_ID_SQL = 
        "SELECT answer_id, user_id, questionnaire_id, answered_at FROM answer WHERE questionnaire_id = ? ORDER BY answered_at DESC";
    
    private static final String SELECT_BY_USER_ID_AND_QUESTIONNAIRE_ID_SQL = 
        "SELECT answer_id, user_id, questionnaire_id, answered_at FROM answer WHERE user_id = ? AND questionnaire_id = ?";
    
    private static final String EXISTS_BY_USER_ID_AND_QUESTIONNAIRE_ID_SQL = 
        "SELECT 1 FROM answer WHERE user_id = ? AND questionnaire_id = ?";
    
    @Override
    public Answer save(Answer answer) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setInt(1, answer.getUserId());
            stmt.setInt(2, answer.getQuestionnaireId());
            stmt.setTimestamp(3, Timestamp.valueOf(answer.getAnsweredAt() != null ? 
                answer.getAnsweredAt() : LocalDateTime.now()));
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    answer.setAnswerId(rs.getInt(1));
                }
            }
            
            return answer;
            
        } catch (SQLException e) {
            log.error("保存回答失败", e);
            throw new RuntimeException("保存回答失败", e);
        } finally {
            DatabaseUtil.closeQuietly(rs, stmt, conn);
        }
    }
    
    @Override
    public Optional<Answer> findById(Integer id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(SELECT_BY_ID_SQL);
            stmt.setInt(1, id);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToAnswer(rs));
            }
            
            return Optional.empty();
            
        } catch (SQLException e) {
            log.error("根据ID查找回答失败", e);
            throw new RuntimeException("根据ID查找回答失败", e);
        } finally {
            DatabaseUtil.closeQuietly(rs, stmt, conn);
        }
    }
    
    @Override
    public List<Answer> findAll() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Answer> answers = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(SELECT_ALL_SQL);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                answers.add(mapResultSetToAnswer(rs));
            }
            
            return answers;
            
        } catch (SQLException e) {
            log.error("查找所有回答失败", e);
            throw new RuntimeException("查找所有回答失败", e);
        } finally {
            DatabaseUtil.closeQuietly(rs, stmt, conn);
        }
    }
    
    @Override
    public boolean update(Answer answer) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(UPDATE_SQL);
            
            stmt.setTimestamp(1, Timestamp.valueOf(answer.getAnsweredAt()));
            stmt.setInt(2, answer.getAnswerId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            log.error("更新回答失败", e);
            throw new RuntimeException("更新回答失败", e);
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
            log.error("删除回答失败", e);
            throw new RuntimeException("删除回答失败", e);
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
            log.error("统计回答数量失败", e);
            throw new RuntimeException("统计回答数量失败", e);
        } finally {
            DatabaseUtil.closeQuietly(rs, stmt, conn);
        }
    }
    
    @Override
    public List<Answer> findByUserId(Integer userId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Answer> answers = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(SELECT_BY_USER_ID_SQL);
            stmt.setInt(1, userId);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                answers.add(mapResultSetToAnswer(rs));
            }
            
            return answers;
            
        } catch (SQLException e) {
            log.error("根据用户ID查找回答失败", e);
            throw new RuntimeException("根据用户ID查找回答失败", e);
        } finally {
            DatabaseUtil.closeQuietly(rs, stmt, conn);
        }
    }
    
    @Override
    public List<Answer> findByQuestionnaireId(Integer questionnaireId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Answer> answers = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(SELECT_BY_QUESTIONNAIRE_ID_SQL);
            stmt.setInt(1, questionnaireId);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                answers.add(mapResultSetToAnswer(rs));
            }
            
            return answers;
            
        } catch (SQLException e) {
            log.error("根据问卷ID查找回答失败", e);
            throw new RuntimeException("根据问卷ID查找回答失败", e);
        } finally {
            DatabaseUtil.closeQuietly(rs, stmt, conn);
        }
    }
    
    @Override
    public Optional<Answer> findByUserIdAndQuestionnaireId(Integer userId, Integer questionnaireId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(SELECT_BY_USER_ID_AND_QUESTIONNAIRE_ID_SQL);
            stmt.setInt(1, userId);
            stmt.setInt(2, questionnaireId);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToAnswer(rs));
            }
            
            return Optional.empty();
            
        } catch (SQLException e) {
            log.error("根据用户ID和问卷ID查找回答失败", e);
            throw new RuntimeException("根据用户ID和问卷ID查找回答失败", e);
        } finally {
            DatabaseUtil.closeQuietly(rs, stmt, conn);
        }
    }
    
    @Override
    public boolean existsByUserIdAndQuestionnaireId(Integer userId, Integer questionnaireId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(EXISTS_BY_USER_ID_AND_QUESTIONNAIRE_ID_SQL);
            stmt.setInt(1, userId);
            stmt.setInt(2, questionnaireId);
            
            rs = stmt.executeQuery();
            return rs.next();
            
        } catch (SQLException e) {
            log.error("检查用户是否已回答问卷失败", e);
            throw new RuntimeException("检查用户是否已回答问卷失败", e);
        } finally {
            DatabaseUtil.closeQuietly(rs, stmt, conn);
        }
    }
    
    /**
     * 将 ResultSet 映射到 Answer 对象
     */
    private Answer mapResultSetToAnswer(ResultSet rs) throws SQLException {
        return Answer.builder()
                .answerId(rs.getInt("answer_id"))
                .userId(rs.getInt("user_id"))
                .questionnaireId(rs.getInt("questionnaire_id"))
                .answeredAt(rs.getTimestamp("answered_at").toLocalDateTime())
                .build();
    }
}
