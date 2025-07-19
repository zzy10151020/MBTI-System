package org.frostedstar.mbtisystem.service;

import org.frostedstar.mbtisystem.entity.Question;

import java.util.List;

/**
 * 问题 Service 接口
 */
public interface QuestionService extends BaseService<Question, Integer> {
    
    /**
     * 根据问卷ID查找问题
     */
    List<Question> findByQuestionnaireId(Integer questionnaireId);
    
    /**
     * 根据维度查找问题
     */
    List<Question> findByDimension(Question.Dimension dimension);
    
    /**
     * 批量创建问题
     */
    List<Question> createQuestions(List<Question> questions);
    
    /**
     * 获取问题详情（包含选项）
     */
    Question getQuestionDetail(Integer questionId);

    /**
     * 删除问题（级联删除选项）
     */
    boolean deleteQuestionWithCascade(Integer questionId);

    /**
     * 根据问卷ID统计问题数量
     */
    long countByQuestionnaireId(Integer questionnaireId);
}
