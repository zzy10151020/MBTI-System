package org.frostedstar.mbtisystem.dao;

import org.frostedstar.mbtisystem.entity.Questionnaire;

import java.util.List;

/**
 * 问卷 DAO 接口
 */
public interface QuestionnaireDAO extends BaseDAO<Questionnaire, Integer> {
    
    /**
     * 根据创建者ID查找问卷
     */
    List<Questionnaire> findByCreatorId(Integer creatorId);
    
    /**
     * 查找已发布的问卷
     */
    List<Questionnaire> findPublished();
    
    /**
     * 根据标题模糊查找问卷
     */
    List<Questionnaire> findByTitleLike(String title);
}
