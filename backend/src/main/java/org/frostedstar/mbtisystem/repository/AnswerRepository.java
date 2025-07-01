package org.frostedstar.mbtisystem.repository;

import org.frostedstar.mbtisystem.model.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 回答数据访问层
 */
@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {

    /**
     * 根据用户ID查找回答
     */
    List<Answer> findByUserId(Long userId);

    /**
     * 根据问卷ID查找回答
     */
    List<Answer> findByQuestionnaireId(Long questionnaireId);

    /**
     * 根据用户ID和问卷ID查找回答
     */
    Optional<Answer> findByUserIdAndQuestionnaireId(Long userId, Long questionnaireId);

    /**
     * 检查用户是否已回答某问卷
     */
    boolean existsByUserIdAndQuestionnaireId(Long userId, Long questionnaireId);

    /**
     * 根据回答时间范围查找回答
     */
    List<Answer> findByAnsweredAtBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 根据用户ID和时间范围查找回答
     */
    List<Answer> findByUserIdAndAnsweredAtBetween(Long userId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 根据问卷ID和时间范围查找回答
     */
    List<Answer> findByQuestionnaireIdAndAnsweredAtBetween(Long questionnaireId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 统计用户的回答数量
     */
    long countByUserId(Long userId);

    /**
     * 统计问卷的回答数量
     */
    long countByQuestionnaireId(Long questionnaireId);

    /**
     * 查找指定时间之后的回答
     */
    List<Answer> findByAnsweredAtAfter(LocalDateTime dateTime);

    /**
     * 自定义查询：获取用户的回答历史（包含问卷信息）
     */
    @Query("SELECT a FROM Answer a LEFT JOIN FETCH a.questionnaire WHERE a.userId = :userId ORDER BY a.answeredAt DESC")
    List<Answer> findUserAnswerHistoryWithQuestionnaire(@Param("userId") Long userId);

    /**
     * 自定义查询：获取问卷的回答统计（包含用户信息）
     */
    @Query("SELECT a FROM Answer a LEFT JOIN FETCH a.user WHERE a.questionnaireId = :questionnaireId ORDER BY a.answeredAt DESC")
    List<Answer> findQuestionnaireAnswersWithUser(@Param("questionnaireId") Long questionnaireId);

    /**
     * 自定义查询：获取最近的回答记录
     */
    @Query("SELECT a FROM Answer a ORDER BY a.answeredAt DESC LIMIT :limit")
    List<Answer> findRecentAnswers(@Param("limit") int limit);

    /**
     * 自定义查询：获取回答详情完整信息
     */
    @Query("SELECT a FROM Answer a LEFT JOIN FETCH a.answerDetails ad " +
           "LEFT JOIN FETCH ad.question LEFT JOIN FETCH ad.option " +
           "WHERE a.answerId = :answerId")
    Optional<Answer> findAnswerWithFullDetails(@Param("answerId") Long answerId);

    /**
     * 根据用户ID查找回答（分页，按回答时间倒序）
     */
    Page<Answer> findByUserIdOrderByAnsweredAtDesc(Long userId, Pageable pageable);

    /**
     * 根据用户ID查找回答（按回答时间倒序）
     */
    List<Answer> findByUserIdOrderByAnsweredAtDesc(Long userId);
}
