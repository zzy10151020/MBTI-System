package org.frostedstar.mbtisystem.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.frostedstar.mbtisystem.dao.AnswerDetailDAO;
import org.frostedstar.mbtisystem.entity.AnswerDetail;
import org.frostedstar.mbtisystem.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 回答详情 DAO 实现
 */
@Slf4j
public class AnswerDetailDAOImpl implements AnswerDetailDAO {
    
    private static final String INSERT_SQL = 
        "INSERT INTO answer_detail (answer_id, question_id, option_id) VALUES (?, ?, ?)";
    
    private static final String SELECT_BY_ID_SQL = 
        "SELECT detail_id, answer_id, question_id, option_id FROM answer_detail WHERE detail_id = ?";
    
    private static final String SELECT_ALL_SQL = 
        "SELECT detail_id, answer_id, question_id, option_id FROM answer_detail ORDER BY answer_id";
    
    private static final String UPDATE_SQL = 
        "UPDATE answer_detail SET option_id = ? WHERE detail_id = ?";
    
    private static final String DELETE_SQL = 
        "DELETE FROM answer_detail WHERE detail_id = ?";
    
    private static final String COUNT_SQL = 
        "SELECT COUNT(*) FROM answer_detail";
    
    private static final String SELECT_BY_ANSWER_ID_SQL = 
        "SELECT detail_id, answer_id, question_id, option_id FROM answer_detail WHERE answer_id = ?";
    
    private static final String DELETE_BY_ANSWER_ID_SQL = 
        "DELETE FROM answer_detail WHERE answer_id = ?";
    
    @Override
    public AnswerDetail save(AnswerDetail answerDetail) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setInt(1, answerDetail.getAnswerId());
            stmt.setInt(2, answerDetail.getQuestionId());
            stmt.setInt(3, answerDetail.getOptionId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    answerDetail.setDetailId(rs.getInt(1));
                }
            }
            
            return answerDetail;
            
        } catch (SQLException e) {
            log.error("保存回答详情失败", e);
            throw new RuntimeException("保存回答详情失败", e);
        } finally {
            DatabaseUtil.closeQuietly(rs, stmt, conn);
        }
    }
    
    @Override
    public Optional<AnswerDetail> findById(Integer id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(SELECT_BY_ID_SQL);
            stmt.setInt(1, id);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToAnswerDetail(rs));
            }
            
            return Optional.empty();
            
        } catch (SQLException e) {
            log.error("根据ID查找回答详情失败", e);
            throw new RuntimeException("根据ID查找回答详情失败", e);
        } finally {
            DatabaseUtil.closeQuietly(rs, stmt, conn);
        }
    }
    
    @Override
    public List<AnswerDetail> findAll() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<AnswerDetail> answerDetails = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(SELECT_ALL_SQL);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                answerDetails.add(mapResultSetToAnswerDetail(rs));
            }
            
            return answerDetails;
            
        } catch (SQLException e) {
            log.error("查找所有回答详情失败", e);
            throw new RuntimeException("查找所有回答详情失败", e);
        } finally {
            DatabaseUtil.closeQuietly(rs, stmt, conn);
        }
    }
    
    @Override
    public boolean update(AnswerDetail answerDetail) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(UPDATE_SQL);
            
            stmt.setInt(1, answerDetail.getOptionId());
            stmt.setInt(2, answerDetail.getDetailId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            log.error("更新回答详情失败", e);
            throw new RuntimeException("更新回答详情失败", e);
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
            log.error("删除回答详情失败", e);
            throw new RuntimeException("删除回答详情失败", e);
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
            log.error("统计回答详情数量失败", e);
            throw new RuntimeException("统计回答详情数量失败", e);
        } finally {
            DatabaseUtil.closeQuietly(rs, stmt, conn);
        }
    }
    
    @Override
    public List<AnswerDetail> findByAnswerId(Integer answerId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<AnswerDetail> answerDetails = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(SELECT_BY_ANSWER_ID_SQL);
            stmt.setInt(1, answerId);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                answerDetails.add(mapResultSetToAnswerDetail(rs));
            }
            
            return answerDetails;
            
        } catch (SQLException e) {
            log.error("根据回答ID查找回答详情失败", e);
            throw new RuntimeException("根据回答ID查找回答详情失败", e);
        } finally {
            DatabaseUtil.closeQuietly(rs, stmt, conn);
        }
    }
    
    @Override
    public boolean deleteByAnswerId(Integer answerId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(DELETE_BY_ANSWER_ID_SQL);
            stmt.setInt(1, answerId);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            log.error("根据回答ID删除回答详情失败", e);
            throw new RuntimeException("根据回答ID删除回答详情失败", e);
        } finally {
            DatabaseUtil.closeQuietly(stmt, conn);
        }
    }
    
    @Override
    public List<AnswerDetail> saveBatch(List<AnswerDetail> answerDetails) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);
            
            stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
            
            for (AnswerDetail answerDetail : answerDetails) {
                stmt.setInt(1, answerDetail.getAnswerId());
                stmt.setInt(2, answerDetail.getQuestionId());
                stmt.setInt(3, answerDetail.getOptionId());
                stmt.addBatch();
            }
            
            stmt.executeBatch();
            
            // 获取生成的键
            rs = stmt.getGeneratedKeys();
            int index = 0;
            while (rs.next() && index < answerDetails.size()) {
                answerDetails.get(index).setDetailId(rs.getInt(1));
                index++;
            }
            
            conn.commit();
            return answerDetails;
            
        } catch (SQLException e) {
            log.error("批量保存回答详情失败", e);
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                log.error("回滚事务失败", ex);
            }
            throw new RuntimeException("批量保存回答详情失败", e);
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
     * 将 ResultSet 映射到 AnswerDetail 对象
     */
    private AnswerDetail mapResultSetToAnswerDetail(ResultSet rs) throws SQLException {
        return AnswerDetail.builder()
                .detailId(rs.getInt("detail_id"))
                .answerId(rs.getInt("answer_id"))
                .questionId(rs.getInt("question_id"))
                .optionId(rs.getInt("option_id"))
                .build();
    }
}
