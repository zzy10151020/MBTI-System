package org.frostedstar.mbtisystem.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.frostedstar.mbtisystem.entity.Questionnaire;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 统一问卷DTO
 * 支持创建、更新、查询等多种操作
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionnaireDTO {
    
    // 基本信息
    private Integer questionnaireId;
    private String title;
    private String description;
    private Integer creatorId;
    private String creatorName;
    private LocalDateTime createdAt;
    private Boolean isPublished;
    
    // 关联信息（仅查询时使用）
    private List<QuestionDTO> questions;
    
    // 操作类型标识
    private OperationType operationType;
    
    /**
     * 创建请求验证
     */
    public boolean isValidForCreate() {
        return operationType == OperationType.CREATE &&
               title != null && !title.trim().isEmpty() &&
               description != null && !description.trim().isEmpty();
    }
    
    /**
     * 更新请求验证
     */
    public boolean isValidForUpdate() {
        return operationType == OperationType.UPDATE &&
               questionnaireId != null && questionnaireId > 0 &&
               title != null && !title.trim().isEmpty() &&
               description != null && !description.trim().isEmpty();
    }
    
    /**
     * 删除请求验证
     */
    public boolean isValidForDelete() {
        return operationType == OperationType.DELETE &&
               questionnaireId != null && questionnaireId > 0;
    }
    
    /**
     * 通用验证方法
     */
    public boolean isValid() {
        if (operationType == null) {
            return false;
        }
        
        switch (operationType) {
            case CREATE:
                return isValidForCreate();
            case UPDATE:
                return isValidForUpdate();
            case DELETE:
                return isValidForDelete();
            case QUERY:
                return true; // 查询操作不需要特殊验证
            default:
                return false;
        }
    }
    
    /**
     * 从Questionnaire实体转换为QuestionnaireDTO（完整版）
     */
    public static QuestionnaireDTO fromEntity(Questionnaire questionnaire) {
        if (questionnaire == null) {
            return null;
        }
        
        return QuestionnaireDTO.builder()
                .questionnaireId(questionnaire.getQuestionnaireId())
                .title(questionnaire.getTitle())
                .description(questionnaire.getDescription())
                .creatorId(questionnaire.getCreatorId())
                .creatorName(questionnaire.getCreator() != null ? questionnaire.getCreator().getUsername() : null)
                .createdAt(questionnaire.getCreatedAt())
                .isPublished(questionnaire.getIsPublished())
                .questions(questionnaire.getQuestions() != null ? 
                    questionnaire.getQuestions().stream()
                        .map(QuestionDTO::fromEntity)
                        .collect(Collectors.toList()) : null)
                .operationType(OperationType.QUERY)
                .build();
    }
    
    /**
     * 从Questionnaire实体转换为QuestionnaireDTO（简化版）
     */
    public static QuestionnaireDTO fromEntitySimple(Questionnaire questionnaire) {
        if (questionnaire == null) {
            return null;
        }
        
        return QuestionnaireDTO.builder()
                .questionnaireId(questionnaire.getQuestionnaireId())
                .title(questionnaire.getTitle())
                .description(questionnaire.getDescription())
                .creatorId(questionnaire.getCreatorId())
                .creatorName(questionnaire.getCreator() != null ? questionnaire.getCreator().getUsername() : null)
                .createdAt(questionnaire.getCreatedAt())
                .isPublished(questionnaire.getIsPublished())
                .operationType(OperationType.QUERY)
                .build();
    }
    
    /**
     * 创建用于创建操作的DTO
     */
    public static QuestionnaireDTO forCreate(String title, String description) {
        return QuestionnaireDTO.builder()
                .title(title)
                .description(description)
                .operationType(OperationType.CREATE)
                .build();
    }
    
    /**
     * 创建用于更新操作的DTO
     */
    public static QuestionnaireDTO forUpdate(Integer id, String title, String description) {
        return QuestionnaireDTO.builder()
                .questionnaireId(id)
                .title(title)
                .description(description)
                .operationType(OperationType.UPDATE)
                .build();
    }
    
    /**
     * 创建用于删除操作的DTO
     */
    public static QuestionnaireDTO forDelete(Integer id) {
        return QuestionnaireDTO.builder()
                .questionnaireId(id)
                .operationType(OperationType.DELETE)
                .build();
    }
}
