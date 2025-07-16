package org.frostedstar.mbtisystem.dao;

import org.frostedstar.mbtisystem.entity.Question;

import java.util.List;

/**
 * 问题 DAO 接口
 */
public interface QuestionDAO extends BaseDAO<Question, Integer> {
    
    /**
     * 根据问卷ID查找问题
     */
    List<Question> findByQuestionnaireId(Integer questionnaireId);
    
    /**
     * 根据问卷ID和排序查找问题
     */
    List<Question> findByQuestionnaireIdOrderByQuestionOrder(Integer questionnaireId);
    
    /**
     * 根据维度查找问题
     */
    List<Question> findByDimension(Question.Dimension dimension);
    
    /**
     * 根据问卷ID删除所有问题
     */
    boolean deleteByQuestionnaireId(Integer questionnaireId);
}
