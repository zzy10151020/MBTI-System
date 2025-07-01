package org.frostedstar.mbtisystem.repository;

import org.frostedstar.mbtisystem.model.Questionnaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 问卷数据访问层
 */
@Repository
public interface QuestionnaireRepository extends JpaRepository<Questionnaire, Long> {

    /**
     * 查找已发布的问卷
     */
    List<Questionnaire> findByIsPublishedTrue();

    /**
     * 根据创建者查找问卷
     */
    List<Questionnaire> findByCreatorId(Long creatorId);

    /**
     * 根据创建者查找已发布的问卷
     */
    List<Questionnaire> findByCreatorIdAndIsPublishedTrue(Long creatorId);

    /**
     * 根据标题模糊查询问卷
     */
    List<Questionnaire> findByTitleContaining(String title);

    /**
     * 根据标题模糊查询已发布的问卷
     */
    List<Questionnaire> findByTitleContainingAndIsPublishedTrue(String title);

    /**
     * 查找指定时间之后创建的问卷
     */
    List<Questionnaire> findByCreatedAtAfter(LocalDateTime dateTime);

    /**
     * 查找指定时间之后创建的已发布问卷
     */
    List<Questionnaire> findByCreatedAtAfterAndIsPublishedTrue(LocalDateTime dateTime);

    /**
     * 统计已发布问卷数量
     */
    long countByIsPublishedTrue();

    /**
     * 统计指定创建者的问卷数量
     */
    long countByCreatorId(Long creatorId);

    /**
     * 自定义查询：获取最受欢迎的问卷（按回答数量排序）
     */
    @Query("SELECT q FROM Questionnaire q LEFT JOIN q.answers a " +
           "WHERE q.isPublished = true " +
           "GROUP BY q ORDER BY COUNT(a) DESC")
    List<Questionnaire> findPopularQuestionnaires();

    /**
     * 自定义查询：获取最新发布的问卷
     */
    @Query("SELECT q FROM Questionnaire q WHERE q.isPublished = true ORDER BY q.createdAt DESC LIMIT :limit")
    List<Questionnaire> findLatestPublishedQuestionnaires(@Param("limit") int limit);

    /**
     * 检查用户是否已经回答过该问卷
     */
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
           "FROM Answer a WHERE a.userId = :userId AND a.questionnaireId = :questionnaireId")
    boolean hasUserAnsweredQuestionnaire(@Param("userId") Long userId, @Param("questionnaireId") Long questionnaireId);
}
