package org.frostedstar.mbtisystem.dto.questionnairedto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.frostedstar.mbtisystem.dto.questiondto.QuestionRequestDTO;
import org.frostedstar.mbtisystem.entity.Questionnaire;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 问卷请求DTO - 用于处理各种问卷相关的请求参数
 * 支持查询、创建、更新、删除等操作
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionnaireRequestDTO {
    
    // 查询相关字段
    /**
     * 问卷ID
     */
    private Integer questionnaireId;
    
    // 创建/更新相关字段
    /**
     * 问卷标题
     */
    private String title;
    
    /**
     * 问卷描述
     */
    private String description;
    
    /**
     * 创建者ID
     */
    private Integer creatorId;
    
    /**
     * 是否发布
     */
    private Boolean isPublished;
    
    /**
     * 问题列表（创建时使用）
     */
    private List<QuestionRequestDTO> questions;
    
    // 验证方法
    
    /**
     * 创建问卷请求验证
     */
    public boolean isValidForCreateQuestionnaire() {
        return title != null && !title.trim().isEmpty() &&
               description != null && !description.trim().isEmpty();
    }
    
    /**
     * 更新问卷请求验证
     */
    public boolean isValidForUpdateQuestionnaire() {
        return questionnaireId != null && questionnaireId > 0 &&
               title != null && !title.trim().isEmpty() &&
               description != null && !description.trim().isEmpty();
    }
    
    /**
     * 删除问卷请求验证
     */
    public boolean isValidForDeleteQuestionnaire() {
        return questionnaireId != null && questionnaireId > 0;
    }
    
    /**
     * 从请求DTO转换为Questionnaire实体
     */
    public Questionnaire toEntity() {
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setQuestionnaireId(this.questionnaireId);
        questionnaire.setTitle(this.title);
        questionnaire.setDescription(this.description);
        questionnaire.setCreatorId(this.creatorId);
        questionnaire.setIsPublished(this.isPublished);

        // 问题转换
        if (this.questions != null) {
            questionnaire.setQuestions(this.questions.stream()
                    .map(question -> question.toEntity())
                    .collect(Collectors.toList()));
        }

        return questionnaire;
    }
    
    /**
     * 静态方法：从请求DTO转换为Questionnaire实体
     */
    public static Questionnaire toEntity(QuestionnaireRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        return dto.toEntity();
    }
}
