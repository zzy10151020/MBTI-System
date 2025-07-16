package org.frostedstar.mbtisystem.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.frostedstar.mbtisystem.entity.Option;

import java.time.LocalDateTime;

/**
 * 统一选项DTO
 * 支持创建、更新、查询等多种操作
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptionDTO {
    
    // 基本信息
    private Integer optionId;
    private Integer questionId;
    private String content;
    private String value; // A, B, C, D
    private Short optionOrder;
    private LocalDateTime createdAt;
    
    // 操作类型标识
    private OperationType operationType;
    
    /**
     * 创建请求验证
     */
    public boolean isValidForCreate() {
        return operationType == OperationType.CREATE &&
               questionId != null && questionId > 0 &&
               content != null && !content.trim().isEmpty() &&
               value != null && !value.trim().isEmpty() &&
               isValidValue(value);
    }
    
    /**
     * 更新请求验证
     */
    public boolean isValidForUpdate() {
        return operationType == OperationType.UPDATE &&
               optionId != null && optionId > 0 &&
               content != null && !content.trim().isEmpty() &&
               value != null && !value.trim().isEmpty() &&
               isValidValue(value);
    }
    
    /**
     * 删除请求验证
     */
    public boolean isValidForDelete() {
        return operationType == OperationType.DELETE &&
               optionId != null && optionId > 0;
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
     * 验证选项值
     */
    private boolean isValidValue(String value) {
        return "A".equals(value) || "B".equals(value) || 
               "C".equals(value) || "D".equals(value);
    }
    
    /**
     * 从Option实体转换为OptionDTO
     */
    public static OptionDTO fromEntity(Option option) {
        if (option == null) {
            return null;
        }
        
        return OptionDTO.builder()
                .optionId(option.getOptionId())
                .questionId(option.getQuestionId())
                .content(option.getContent())
                .value(scoreToValue(option.getScore()))
                .operationType(OperationType.QUERY)
                .build();
    }
    
    /**
     * 将score转换为value
     */
    private static String scoreToValue(Byte score) {
        if (score == null) {
            return null;
        }
        return score > 0 ? "A" : "B";
    }
    
    /**
     * 将value转换为score
     */
    public Byte getScoreFromValue() {
        if (value == null) {
            return null;
        }
        return "A".equals(value) ? (byte) 1 : (byte) -1;
    }
    
    /**
     * 创建用于创建操作的DTO
     */
    public static OptionDTO forCreate(Integer questionId, String content, String value, Short optionOrder) {
        return OptionDTO.builder()
                .questionId(questionId)
                .content(content)
                .value(value)
                .optionOrder(optionOrder)
                .operationType(OperationType.CREATE)
                .build();
    }
    
    /**
     * 创建用于更新操作的DTO
     */
    public static OptionDTO forUpdate(Integer optionId, String content, String value, Short optionOrder) {
        return OptionDTO.builder()
                .optionId(optionId)
                .content(content)
                .value(value)
                .optionOrder(optionOrder)
                .operationType(OperationType.UPDATE)
                .build();
    }
    
    /**
     * 创建用于删除操作的DTO
     */
    public static OptionDTO forDelete(Integer optionId) {
        return OptionDTO.builder()
                .optionId(optionId)
                .operationType(OperationType.DELETE)
                .build();
    }
}
