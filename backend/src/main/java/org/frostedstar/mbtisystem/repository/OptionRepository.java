package org.frostedstar.mbtisystem.repository;

import org.frostedstar.mbtisystem.model.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 选项数据访问层
 */
@Repository
public interface OptionRepository extends JpaRepository<Option, Long> {

    /**
     * 根据问题ID查找选项
     */
    List<Option> findByQuestionId(Long questionId);

    /**
     * 根据问题ID和分数查找选项
     */
    List<Option> findByQuestionIdAndScore(Long questionId, Byte score);

    /**
     * 根据分数查找选项
     */
    List<Option> findByScore(Byte score);

    /**
     * 根据问题ID统计选项数量
     */
    long countByQuestionId(Long questionId);

    /**
     * 根据内容模糊查询选项
     */
    List<Option> findByContentContaining(String content);

    /**
     * 自定义查询：获取问题的所有选项
     */
    @Query("SELECT o FROM Option o WHERE o.questionId = :questionId ORDER BY o.optionId")
    List<Option> findOptionsByQuestionIdOrdered(@Param("questionId") Long questionId);

    /**
     * 自定义查询：根据问卷ID获取所有选项
     */
    @Query("SELECT o FROM Option o JOIN Question q ON o.questionId = q.questionId " +
           "WHERE q.questionnaireId = :questionnaireId ORDER BY q.questionOrder, o.optionId")
    List<Option> findOptionsByQuestionnaireId(@Param("questionnaireId") Long questionnaireId);

    /**
     * 根据问题ID删除所有选项
     */
    void deleteByQuestionId(Long questionId);

    /**
     * 检查选项是否被引用（在回答详情中）
     */
    @Query("SELECT CASE WHEN COUNT(ad) > 0 THEN true ELSE false END " +
           "FROM AnswerDetail ad WHERE ad.optionId = :optionId")
    boolean isOptionReferenced(@Param("optionId") Long optionId);
}
