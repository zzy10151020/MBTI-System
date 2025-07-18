package org.frostedstar.mbtisystem.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.frostedstar.mbtisystem.entity.Question;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 统一问题DTO
 * 支持创建、更新、查询等多种操作
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDTO {
    
    // 基本信息
    private Integer questionId;
    private Integer questionnaireId;
    private String content;
    private String dimension; // EI, SN, TF, JP
    private Short questionOrder;
    private LocalDateTime createdAt;
    
    // 关联信息（仅查询时使用）
    private List<OptionDTO> options;
    
    // 操作类型标识 - 不序列化到JSON响应中
    @JsonIgnore
    private OperationType operationType;
    
    /**
     * 创建请求验证
     */
    @JsonIgnore
    public boolean isValidForCreate() {
        return operationType == OperationType.CREATE &&
               questionnaireId != null && questionnaireId > 0 &&
               content != null && !content.trim().isEmpty() &&
               dimension != null && !dimension.trim().isEmpty() &&
               isValidDimension(dimension);
    }
    
    /**
     * 更新请求验证
     */
    @JsonIgnore
    public boolean isValidForUpdate() {
        return operationType == OperationType.UPDATE &&
               questionId != null && questionId > 0 &&
               content != null && !content.trim().isEmpty() &&
               dimension != null && !dimension.trim().isEmpty() &&
               isValidDimension(dimension);
    }
    
    /**
     * 删除请求验证
     */
    @JsonIgnore
    public boolean isValidForDelete() {
        return operationType == OperationType.DELETE &&
               questionId != null && questionId > 0;
    }
    
    /**
     * 通用验证方法
     */
    @JsonIgnore
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
     * 验证MBTI维度
     */
    @JsonIgnore
    private boolean isValidDimension(String dimension) {
        return "EI".equals(dimension) || "SN".equals(dimension) || 
               "TF".equals(dimension) || "JP".equals(dimension);
    }

    /**
     * 从QuestionDTO转换为Question实体
     */
    public static Question toEntity(QuestionDTO dto) {
        if (dto == null) {
            return null;
        }

        Question question = new Question();
        question.setQuestionId(dto.getQuestionId());
        question.setQuestionnaireId(dto.getQuestionnaireId());
        question.setContent(dto.getContent());
        question.setDimension(dto.getDimension() != null ?
                Question.Dimension.valueOf(dto.getDimension()) : null);
        question.setQuestionOrder(dto.getQuestionOrder());

        // 选项转换
        if (dto.getOptions() != null) {
            question.setOptions(dto.getOptions().stream()
                    .map(OptionDTO::toEntity)
                    .collect(Collectors.toList()));
        }

        return question;
    }

    /**
     * 从Question实体转换为QuestionDTO（完整版）
     */
    public static QuestionDTO fromEntity(Question question) {
        if (question == null) {
            return null;
        }
        
        return QuestionDTO.builder()
                .questionId(question.getQuestionId())
                .questionnaireId(question.getQuestionnaireId())
                .content(question.getContent())
                .dimension(question.getDimension() != null ? question.getDimension().name() : null)
                .questionOrder(question.getQuestionOrder())
                .options(question.getOptions() != null ? 
                    question.getOptions().stream()
                        .map(OptionDTO::fromEntity)
                        .collect(Collectors.toList()) : null)
                .operationType(OperationType.QUERY)
                .build();
    }
    
    /**
     * 从Question实体转换为QuestionDTO（无选项）
     */
    public static QuestionDTO fromEntitySimple(Question question) {
        if (question == null) {
            return null;
        }
        
        return QuestionDTO.builder()
                .questionId(question.getQuestionId())
                .questionnaireId(question.getQuestionnaireId())
                .content(question.getContent())
                .dimension(question.getDimension() != null ? question.getDimension().name() : null)
                .questionOrder(question.getQuestionOrder())
                .operationType(OperationType.QUERY)
                .build();
    }
    
    /**
     * 创建用于创建操作的DTO
     */
    public static QuestionDTO forCreate(Integer questionnaireId, String content, String dimension, Short questionOrder) {
        return QuestionDTO.builder()
                .questionnaireId(questionnaireId)
                .content(content)
                .dimension(dimension)
                .questionOrder(questionOrder)
                .operationType(OperationType.CREATE)
                .build();
    }

    /**
     * 创建用于更新操作的DTO
     */
    public static QuestionDTO forUpdate(Integer questionId, String content, String dimension, Short questionOrder) {
        return QuestionDTO.builder()
                .questionId(questionId)
                .content(content)
                .dimension(dimension)
                .questionOrder(questionOrder)
                .operationType(OperationType.UPDATE)
                .build();
    }
    
    /**
     * 创建用于删除操作的DTO
     */
    public static QuestionDTO forDelete(Integer questionId) {
        return QuestionDTO.builder()
                .questionId(questionId)
                .operationType(OperationType.DELETE)
                .build();
    }
}
