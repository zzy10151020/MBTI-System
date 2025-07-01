package org.frostedstar.mbtisystem.repository;

import org.frostedstar.mbtisystem.model.MbtiDimension;
import org.frostedstar.mbtisystem.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 问题数据访问层
 */
@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    /**
     * 根据问卷ID查找问题（按顺序排序）
     */
    List<Question> findByQuestionnaireIdOrderByQuestionOrder(Long questionnaireId);

    /**
     * 根据问卷ID和维度查找问题
     */
    List<Question> findByQuestionnaireIdAndDimension(Long questionnaireId, MbtiDimension dimension);

    /**
     * 根据维度查找问题
     */
    List<Question> findByDimension(MbtiDimension dimension);

    /**
     * 根据问卷ID统计问题数量
     */
    long countByQuestionnaireId(Long questionnaireId);

    /**
     * 根据问卷ID和维度统计问题数量
     */
    long countByQuestionnaireIdAndDimension(Long questionnaireId, MbtiDimension dimension);

    /**
     * 查找问卷中的最大问题序号
     */
    @Query("SELECT COALESCE(MAX(q.questionOrder), 0) FROM Question q WHERE q.questionnaireId = :questionnaireId")
    Short findMaxQuestionOrderByQuestionnaireId(@Param("questionnaireId") Long questionnaireId);

    /**
     * 根据内容模糊查询问题
     */
    List<Question> findByContentContaining(String content);

    /**
     * 检查问卷中是否存在指定顺序的问题
     */
    boolean existsByQuestionnaireIdAndQuestionOrder(Long questionnaireId, Short questionOrder);

    /**
     * 自定义查询：获取问卷的完整问题信息（包含选项）
     */
    @Query("SELECT DISTINCT q FROM Question q LEFT JOIN FETCH q.options o WHERE q.questionnaireId = :questionnaireId ORDER BY q.questionOrder")
    List<Question> findQuestionsWithOptionsByQuestionnaireId(@Param("questionnaireId") Long questionnaireId);
}
