package org.frostedstar.mbtisystem.dto.questionnairedto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.frostedstar.mbtisystem.dto.questiondto.QuestionResponseDTO;
import org.frostedstar.mbtisystem.entity.Questionnaire;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 问卷响应DTO
 * 用于返回问卷数据给客户端
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionnaireResponseDTO {
    
    // 基本信息
    private Integer questionnaireId;
    private String title;
    private String description;
    private Integer creatorId;
    private String creatorName;
    private LocalDateTime createdAt;
    private Boolean isPublished;
    
    // 关联信息（仅查询时使用）
    private List<QuestionResponseDTO> questions;
    
    /**
     * 从Questionnaire实体转换为QuestionnaireResponseDTO（完整版）
     */
    public static QuestionnaireResponseDTO fromEntity(Questionnaire questionnaire) {
        if (questionnaire == null) {
            return null;
        }
        
        return QuestionnaireResponseDTO.builder()
                .questionnaireId(questionnaire.getQuestionnaireId())
                .title(questionnaire.getTitle())
                .description(questionnaire.getDescription())
                .creatorId(questionnaire.getCreatorId())
                .creatorName(questionnaire.getCreator() != null ? questionnaire.getCreator().getUsername() : null)
                .createdAt(questionnaire.getCreatedAt())
                .isPublished(questionnaire.getIsPublished())
                .questions(questionnaire.getQuestions() != null ? 
                    questionnaire.getQuestions().stream()
                        .map(QuestionResponseDTO::fromEntity)
                        .collect(Collectors.toList()) : null)
                .build();
    }
    
    /**
     * 从Questionnaire实体转换为QuestionnaireResponseDTO（无问题）
     */
    public static QuestionnaireResponseDTO fromEntitySimple(Questionnaire questionnaire) {
        if (questionnaire == null) {
            return null;
        }
        
        return QuestionnaireResponseDTO.builder()
                .questionnaireId(questionnaire.getQuestionnaireId())
                .title(questionnaire.getTitle())
                .description(questionnaire.getDescription())
                .creatorId(questionnaire.getCreatorId())
                .creatorName(questionnaire.getCreator() != null ? questionnaire.getCreator().getUsername() : null)
                .createdAt(questionnaire.getCreatedAt())
                .isPublished(questionnaire.getIsPublished())
                .build();
    }
}
