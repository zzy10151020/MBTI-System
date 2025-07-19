package org.frostedstar.mbtisystem.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.frostedstar.mbtisystem.dao.OptionDAO;
import org.frostedstar.mbtisystem.dao.QuestionDAO;
import org.frostedstar.mbtisystem.entity.Question;
import org.frostedstar.mbtisystem.entity.Option;
import org.frostedstar.mbtisystem.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 问题 DAO 实现
 */
@Slf4j
public class QuestionDAOImpl implements QuestionDAO {
    
    private OptionDAO optionDAO;
    
    /**
     * 延迟获取OptionDAO，避免循环依赖
     */
    private OptionDAO getOptionDAO() {
        if (optionDAO == null) {
            optionDAO = new org.frostedstar.mbtisystem.dao.impl.OptionDAOImpl();
        }
        return optionDAO;
    }
    
    private static final String INSERT_SQL = 
        "INSERT INTO question (questionnaire_id, content, dimension, question_order) VALUES (?, ?, ?, ?)";
    
    private static final String SELECT_BY_ID_SQL = 
        "SELECT question_id, questionnaire_id, content, dimension, question_order FROM question WHERE question_id = ?";
    
    private static final String SELECT_ALL_SQL = 
        "SELECT question_id, questionnaire_id, content, dimension, question_order FROM question ORDER BY questionnaire_id, question_order";
    
    private static final String UPDATE_SQL = 
        "UPDATE question SET content = ?, dimension = ?, question_order = ? WHERE question_id = ?";
    
    private static final String DELETE_SQL = 
        "DELETE FROM question WHERE question_id = ?";
    
    private static final String COUNT_SQL = 
        "SELECT COUNT(*) FROM question";
    
    private static final String SELECT_BY_QUESTIONNAIRE_ID_SQL = 
        "SELECT question_id, questionnaire_id, content, dimension, question_order FROM question WHERE questionnaire_id = ?";
    
    private static final String SELECT_BY_QUESTIONNAIRE_ID_ORDER_SQL = 
        "SELECT question_id, questionnaire_id, content, dimension, question_order FROM question WHERE questionnaire_id = ? ORDER BY question_order";
    
    private static final String SELECT_BY_DIMENSION_SQL = 
        "SELECT question_id, questionnaire_id, content, dimension, question_order FROM question WHERE dimension = ? ORDER BY questionnaire_id, question_order";
    
    private static final String DELETE_BY_QUESTIONNAIRE_ID_SQL = 
        "DELETE FROM question WHERE questionnaire_id = ?";
    
    @Override
    public Question save(Question question) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setInt(1, question.getQuestionnaireId());
            stmt.setString(2, question.getContent());
            stmt.setString(3, question.getDimension().getValue());
            stmt.setShort(4, question.getQuestionOrder());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    question.setQuestionId(rs.getInt(1));
                }
            }
            
            return question;
            
        } catch (SQLException e) {
            log.error("保存问题失败", e);
            throw new RuntimeException("保存问题失败", e);
        } finally {
            DatabaseUtil.closeQuietly(rs, stmt, conn);
        }
    }
    
    @Override
    public Optional<Question> findById(Integer id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(SELECT_BY_ID_SQL);
            stmt.setInt(1, id);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToQuestion(rs));
            }
            
            return Optional.empty();
            
        } catch (SQLException e) {
            log.error("根据ID查找问题失败", e);
            throw new RuntimeException("根据ID查找问题失败", e);
        } finally {
            DatabaseUtil.closeQuietly(rs, stmt, conn);
        }
    }
    
    @Override
    public List<Question> findAll() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Question> questions = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(SELECT_ALL_SQL);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                questions.add(mapResultSetToQuestion(rs));
            }
            
            return questions;
            
        } catch (SQLException e) {
            log.error("查找所有问题失败", e);
            throw new RuntimeException("查找所有问题失败", e);
        } finally {
            DatabaseUtil.closeQuietly(rs, stmt, conn);
        }
    }
    
    @Override
    public boolean update(Question question) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(UPDATE_SQL);
            
            stmt.setString(1, question.getContent());
            stmt.setString(2, question.getDimension().getValue());
            stmt.setShort(3, question.getQuestionOrder());
            stmt.setInt(4, question.getQuestionId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            log.error("更新问题失败", e);
            throw new RuntimeException("更新问题失败", e);
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
            log.error("删除问题失败", e);
            throw new RuntimeException("删除问题失败", e);
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
            log.error("统计问题数量失败", e);
            throw new RuntimeException("统计问题数量失败", e);
        } finally {
            DatabaseUtil.closeQuietly(rs, stmt, conn);
        }
    }
    
    @Override
    public List<Question> findByQuestionnaireId(Integer questionnaireId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Question> questions = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(SELECT_BY_QUESTIONNAIRE_ID_SQL);
            stmt.setInt(1, questionnaireId);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                questions.add(mapResultSetToQuestion(rs));
            }
            
            return questions;
            
        } catch (SQLException e) {
            log.error("根据问卷ID查找问题失败", e);
            throw new RuntimeException("根据问卷ID查找问题失败", e);
        } finally {
            DatabaseUtil.closeQuietly(rs, stmt, conn);
        }
    }
    
    @Override
    public List<Question> findByQuestionnaireIdOrderByQuestionOrder(Integer questionnaireId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Question> questions = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(SELECT_BY_QUESTIONNAIRE_ID_ORDER_SQL);
            stmt.setInt(1, questionnaireId);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                questions.add(mapResultSetToQuestion(rs));
            }
            
            return questions;
            
        } catch (SQLException e) {
            log.error("根据问卷ID按顺序查找问题失败", e);
            throw new RuntimeException("根据问卷ID按顺序查找问题失败", e);
        } finally {
            DatabaseUtil.closeQuietly(rs, stmt, conn);
        }
    }
    
    @Override
    public List<Question> findByDimension(Question.Dimension dimension) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Question> questions = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(SELECT_BY_DIMENSION_SQL);
            stmt.setString(1, dimension.getValue());
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                questions.add(mapResultSetToQuestion(rs));
            }
            
            return questions;
            
        } catch (SQLException e) {
            log.error("根据维度查找问题失败", e);
            throw new RuntimeException("根据维度查找问题失败", e);
        } finally {
            DatabaseUtil.closeQuietly(rs, stmt, conn);
        }
    }
    
    @Override
    public boolean deleteByQuestionnaireId(Integer questionnaireId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(DELETE_BY_QUESTIONNAIRE_ID_SQL);
            stmt.setInt(1, questionnaireId);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            log.error("根据问卷ID删除问题失败", e);
            throw new RuntimeException("根据问卷ID删除问题失败", e);
        } finally {
            DatabaseUtil.closeQuietly(stmt, conn);
        }
    }

    @Override
    public long countByQuestionnaireId(Integer questionnaireId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement("SELECT COUNT(*) FROM question WHERE questionnaire_id = ?");
            stmt.setInt(1, questionnaireId);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getLong(1);
            }
            
            return 0;
            
        } catch (SQLException e) {
            log.error("统计问卷问题数量失败", e);
            throw new RuntimeException("统计问卷问题数量失败", e);
        } finally {
            DatabaseUtil.closeQuietly(rs, stmt, conn);
        }
    }
    
    /**
     * 将 ResultSet 映射到 Question 对象
     */
    private Question mapResultSetToQuestion(ResultSet rs) throws SQLException {
        Question question = Question.builder()
                .questionId(rs.getInt("question_id"))
                .questionnaireId(rs.getInt("questionnaire_id"))
                .content(rs.getString("content"))
                .dimension(Question.Dimension.fromValue(rs.getString("dimension")))
                .questionOrder(rs.getShort("question_order"))
                .build();
        
        // 加载该问题的选项数据
        List<Option> options = getOptionDAO().findByQuestionId(question.getQuestionId());
        question.setOptions(options);
        
        return question;
    }
}
