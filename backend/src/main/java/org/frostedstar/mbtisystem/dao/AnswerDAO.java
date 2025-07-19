package org.frostedstar.mbtisystem.dao;

import org.frostedstar.mbtisystem.entity.Answer;

import java.util.List;
import java.util.Optional;

/**
 * 回答 DAO 接口
 */
public interface AnswerDAO extends BaseDAO<Answer, Integer> {
    
    /**
     * 根据用户ID查找回答
     */
    List<Answer> findByUserId(Integer userId);
    
    /**
     * 根据问卷ID查找回答
     */
    List<Answer> findByQuestionnaireId(Integer questionnaireId);
    
    /**
     * 根据用户ID和问卷ID查找回答
     */
    Optional<Answer> findByUserIdAndQuestionnaireId(Integer userId, Integer questionnaireId);
    
    /**
     * 检查用户是否已经回答了某个问卷
     */
    boolean existsByUserIdAndQuestionnaireId(Integer userId, Integer questionnaireId);

    /**
     * 根据用户ID和问卷ID删除用户回答的问卷
     */
    boolean deleteByUserIdAndQuestionnaireId(Integer userId, Integer questionnaireId);

    /**
     * 根据问卷ID删除所有回答
     */
    boolean deleteByQuestionnaireId(Integer questionnaireId);

    /**
     * 根据问卷ID统计回答数量
     */
    long countByQuestionnaireId(Integer questionnaireId);
}
