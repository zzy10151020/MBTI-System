package org.frostedstar.mbtisystem.service;

import org.frostedstar.mbtisystem.entity.Questionnaire;

import java.util.List;
import java.util.Optional;

/**
 * 问卷 Service 接口
 */
public interface QuestionnaireService extends BaseService<Questionnaire, Integer> {
    
    /**
     * 创建问卷（包含问题和选项）
     */
    Questionnaire createQuestionnaire(Questionnaire questionnaire);
    
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
    
    /**
     * 发布问卷
     */
    boolean publishQuestionnaire(Integer questionnaireId);
    
    /**
     * 取消发布问卷
     */
    boolean unpublishQuestionnaire(Integer questionnaireId);
    
    /**
     * 获取问卷详情（包含问题和选项）
     */
    Optional<Questionnaire> getQuestionnaireDetail(Integer questionnaireId);
    
    /**
     * 删除问卷（级联删除问题和选项）
     */
    boolean deleteQuestionnaireWithCascade(Integer questionnaireId);
}
