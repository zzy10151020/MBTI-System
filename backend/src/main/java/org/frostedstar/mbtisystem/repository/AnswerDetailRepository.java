package org.frostedstar.mbtisystem.repository;

import org.frostedstar.mbtisystem.model.AnswerDetail;
import org.frostedstar.mbtisystem.model.MbtiDimension;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 回答详情数据访问层
 */
@Repository
public interface AnswerDetailRepository extends JpaRepository<AnswerDetail, Long> {

    /**
     * 根据回答ID查找回答详情
     */
    List<AnswerDetail> findByAnswerId(Long answerId);

    /**
     * 根据问题ID查找回答详情
     */
    List<AnswerDetail> findByQuestionId(Long questionId);

    /**
     * 根据选项ID查找回答详情
     */
    List<AnswerDetail> findByOptionId(Long optionId);

    /**
     * 根据回答ID和问题ID查找回答详情
     */
    AnswerDetail findByAnswerIdAndQuestionId(Long answerId, Long questionId);

    /**
     * 检查回答详情是否存在
     */
    boolean existsByAnswerIdAndQuestionId(Long answerId, Long questionId);

    /**
     * 统计回答的详情数量
     */
    long countByAnswerId(Long answerId);

    /**
     * 自定义查询：获取回答的完整详情信息
     */
    @Query("SELECT ad FROM AnswerDetail ad " +
           "LEFT JOIN FETCH ad.question q " +
           "LEFT JOIN FETCH ad.option o " +
           "WHERE ad.answerId = :answerId " +
           "ORDER BY q.questionOrder")
    List<AnswerDetail> findAnswerDetailsWithQuestionAndOption(@Param("answerId") Long answerId);

    /**
     * 自定义查询：根据维度计算用户的分数
     */
    @Query("SELECT SUM(o.score) FROM AnswerDetail ad " +
           "JOIN Option o ON ad.optionId = o.optionId " +
           "JOIN Question q ON ad.questionId = q.questionId " +
           "WHERE ad.answerId = :answerId AND q.dimension = :dimension")
    Integer calculateScoreByDimension(@Param("answerId") Long answerId, @Param("dimension") MbtiDimension dimension);

    /**
     * 自定义查询：获取所有维度的分数
     */
    @Query("SELECT q.dimension, SUM(o.score) FROM AnswerDetail ad " +
           "JOIN Option o ON ad.optionId = o.optionId " +
           "JOIN Question q ON ad.questionId = q.questionId " +
           "WHERE ad.answerId = :answerId " +
           "GROUP BY q.dimension")
    List<Object[]> calculateAllDimensionScores(@Param("answerId") Long answerId);

    /**
     * 自定义查询：统计选项被选择的次数
     */
    @Query("SELECT ad.optionId, COUNT(ad) FROM AnswerDetail ad " +
           "JOIN Question q ON ad.questionId = q.questionId " +
           "WHERE q.questionnaireId = :questionnaireId " +
           "GROUP BY ad.optionId")
    List<Object[]> countOptionSelections(@Param("questionnaireId") Long questionnaireId);

    /**
     * 自定义查询：获取问卷的回答统计信息
     */
    @Query("SELECT q.dimension, o.score, COUNT(ad) FROM AnswerDetail ad " +
           "JOIN Question q ON ad.questionId = q.questionId " +
           "JOIN Option o ON ad.optionId = o.optionId " +
           "WHERE q.questionnaireId = :questionnaireId " +
           "GROUP BY q.dimension, o.score")
    List<Object[]> getQuestionnaireStatistics(@Param("questionnaireId") Long questionnaireId);

    /**
     * 根据回答ID删除回答详情
     */
    void deleteByAnswerId(Long answerId);
}
