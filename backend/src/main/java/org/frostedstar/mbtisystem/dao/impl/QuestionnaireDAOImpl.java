package org.frostedstar.mbtisystem.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.frostedstar.mbtisystem.dao.QuestionnaireDAO;
import org.frostedstar.mbtisystem.entity.Questionnaire;
import org.frostedstar.mbtisystem.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 问卷 DAO 实现
 */
@Slf4j
public class QuestionnaireDAOImpl implements QuestionnaireDAO {
    
    private static final String INSERT_SQL = 
        "INSERT INTO questionnaire (title, description, creator_id, created_at, is_published) VALUES (?, ?, ?, ?, ?)";
    
    private static final String SELECT_BY_ID_SQL = 
        "SELECT questionnaire_id, title, description, creator_id, created_at, is_published FROM questionnaire WHERE questionnaire_id = ?";
    
    private static final String SELECT_ALL_SQL = 
        "SELECT questionnaire_id, title, description, creator_id, created_at, is_published FROM questionnaire ORDER BY created_at DESC";
    
    private static final String UPDATE_SQL = 
        "UPDATE questionnaire SET title = ?, description = ?, is_published = ? WHERE questionnaire_id = ?";
    
    private static final String DELETE_SQL = 
        "DELETE FROM questionnaire WHERE questionnaire_id = ?";
    
    private static final String COUNT_SQL = 
        "SELECT COUNT(*) FROM questionnaire";
    
    private static final String SELECT_BY_CREATOR_ID_SQL = 
        "SELECT questionnaire_id, title, description, creator_id, created_at, is_published FROM questionnaire WHERE creator_id = ? ORDER BY created_at DESC";
    
    private static final String SELECT_PUBLISHED_SQL = 
        "SELECT questionnaire_id, title, description, creator_id, created_at, is_published FROM questionnaire WHERE is_published = true ORDER BY created_at DESC";
    
    private static final String SELECT_BY_TITLE_LIKE_SQL = 
        "SELECT questionnaire_id, title, description, creator_id, created_at, is_published FROM questionnaire WHERE title LIKE ? ORDER BY created_at DESC";
    
    @Override
    public Questionnaire save(Questionnaire questionnaire) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setString(1, questionnaire.getTitle());
            stmt.setString(2, questionnaire.getDescription());
            stmt.setInt(3, questionnaire.getCreatorId());
            stmt.setTimestamp(4, Timestamp.valueOf(questionnaire.getCreatedAt() != null ? 
                questionnaire.getCreatedAt() : LocalDateTime.now()));
            stmt.setBoolean(5, questionnaire.getIsPublished() != null ? 
                questionnaire.getIsPublished() : true);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    questionnaire.setQuestionnaireId(rs.getInt(1));
                }
            }
            
            return questionnaire;
            
        } catch (SQLException e) {
            log.error("保存问卷失败", e);
            throw new RuntimeException("保存问卷失败", e);
        } finally {
            DatabaseUtil.closeQuietly(rs, stmt, conn);
        }
    }
    
    @Override
    public Optional<Questionnaire> findById(Integer id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(SELECT_BY_ID_SQL);
            stmt.setInt(1, id);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToQuestionnaire(rs));
            }
            
            return Optional.empty();
            
        } catch (SQLException e) {
            log.error("根据ID查找问卷失败", e);
            throw new RuntimeException("根据ID查找问卷失败", e);
        } finally {
            DatabaseUtil.closeQuietly(rs, stmt, conn);
        }
    }
    
    @Override
    public List<Questionnaire> findAll() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Questionnaire> questionnaires = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(SELECT_ALL_SQL);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                questionnaires.add(mapResultSetToQuestionnaire(rs));
            }
            
            return questionnaires;
            
        } catch (SQLException e) {
            log.error("查找所有问卷失败", e);
            throw new RuntimeException("查找所有问卷失败", e);
        } finally {
            DatabaseUtil.closeQuietly(rs, stmt, conn);
        }
    }
    
    @Override
    public boolean update(Questionnaire questionnaire) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(UPDATE_SQL);
            
            stmt.setString(1, questionnaire.getTitle());
            stmt.setString(2, questionnaire.getDescription());
            stmt.setBoolean(3, questionnaire.getIsPublished());
            stmt.setInt(4, questionnaire.getQuestionnaireId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            log.error("更新问卷失败", e);
            throw new RuntimeException("更新问卷失败", e);
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
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            log.error("删除问卷失败，问卷ID: {}", id, e);
            return false; // 返回false而不是抛出异常
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
            log.error("统计问卷数量失败", e);
            throw new RuntimeException("统计问卷数量失败", e);
        } finally {
            DatabaseUtil.closeQuietly(rs, stmt, conn);
        }
    }
    
    @Override
    public List<Questionnaire> findByCreatorId(Integer creatorId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Questionnaire> questionnaires = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(SELECT_BY_CREATOR_ID_SQL);
            stmt.setInt(1, creatorId);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                questionnaires.add(mapResultSetToQuestionnaire(rs));
            }
            
            return questionnaires;
            
        } catch (SQLException e) {
            log.error("根据创建者ID查找问卷失败", e);
            throw new RuntimeException("根据创建者ID查找问卷失败", e);
        } finally {
            DatabaseUtil.closeQuietly(rs, stmt, conn);
        }
    }
    
    @Override
    public List<Questionnaire> findPublished() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Questionnaire> questionnaires = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(SELECT_PUBLISHED_SQL);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                questionnaires.add(mapResultSetToQuestionnaire(rs));
            }
            
            return questionnaires;
            
        } catch (SQLException e) {
            log.error("查找已发布问卷失败", e);
            throw new RuntimeException("查找已发布问卷失败", e);
        } finally {
            DatabaseUtil.closeQuietly(rs, stmt, conn);
        }
    }
    
    @Override
    public List<Questionnaire> findByTitleLike(String title) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Questionnaire> questionnaires = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(SELECT_BY_TITLE_LIKE_SQL);
            stmt.setString(1, "%" + title + "%");
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                questionnaires.add(mapResultSetToQuestionnaire(rs));
            }
            
            return questionnaires;
            
        } catch (SQLException e) {
            log.error("根据标题模糊查找问卷失败", e);
            throw new RuntimeException("根据标题模糊查找问卷失败", e);
        } finally {
            DatabaseUtil.closeQuietly(rs, stmt, conn);
        }
    }
    
    /**
     * 将 ResultSet 映射到 Questionnaire 对象
     */
    private Questionnaire mapResultSetToQuestionnaire(ResultSet rs) throws SQLException {
        return Questionnaire.builder()
                .questionnaireId(rs.getInt("questionnaire_id"))
                .title(rs.getString("title"))
                .description(rs.getString("description"))
                .creatorId(rs.getInt("creator_id"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .isPublished(rs.getBoolean("is_published"))
                .build();
    }
}
