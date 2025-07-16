package org.frostedstar.mbtisystem.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.frostedstar.mbtisystem.entity.Answer;
import org.frostedstar.mbtisystem.entity.AnswerDetail;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 统一测试DTO
 * 支持测试提交、结果查询等多种操作
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestDTO {
    
    // 基本信息
    private Integer answerId;
    private Integer userId;
    private Integer questionnaireId;
    private String result;
    private String resultDescription;
    private LocalDateTime createdAt;
    
    // 答案详情
    private List<AnswerDetailDTO> answerDetails;
    
    // 操作类型标识
    private OperationType operationType;
    
    // MBTI结果相关字段
    private String mbtiType;
    private String title;
    private String description;
    private Map<String, String> dimensions;
    private Map<String, Object> statistics;
    
    /**
     * 创建请求验证
     */
    public boolean isValidForCreate() {
        return operationType == OperationType.CREATE &&
               userId != null && userId > 0 &&
               questionnaireId != null && questionnaireId > 0 &&
               answerDetails != null && !answerDetails.isEmpty();
    }
    
    /**
     * 查询请求验证
     */
    public boolean isValidForQuery() {
        return operationType == OperationType.QUERY &&
               (userId != null && userId > 0);
    }
    
    /**
     * 删除请求验证
     */
    public boolean isValidForDelete() {
        return operationType == OperationType.DELETE &&
               answerId != null && answerId > 0;
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
            case QUERY:
                return isValidForQuery();
            case DELETE:
                return isValidForDelete();
            case UPDATE:
                return false; // 测试结果不支持更新
            default:
                return false;
        }
    }
    
    /**
     * 从Answer实体转换为TestDTO
     */
    public static TestDTO fromEntity(Answer answer) {
        if (answer == null) {
            return null;
        }
        
        List<AnswerDetailDTO> answerDetailDTOs = null;
        if (answer.getDetails() != null) {
            answerDetailDTOs = answer.getDetails().stream()
                    .map(AnswerDetailDTO::fromEntity)
                    .collect(Collectors.toList());
        }
        
        return TestDTO.builder()
                .answerId(answer.getAnswerId())
                .userId(answer.getUserId())
                .questionnaireId(answer.getQuestionnaireId())
                .createdAt(answer.getAnsweredAt())
                .answerDetails(answerDetailDTOs)
                .operationType(OperationType.QUERY)
                .build();
    }
    
    /**
     * 创建用于创建操作的DTO
     */
    public static TestDTO forCreate(Integer userId, Integer questionnaireId, List<AnswerDetailDTO> answerDetails) {
        return TestDTO.builder()
                .userId(userId)
                .questionnaireId(questionnaireId)
                .answerDetails(answerDetails)
                .operationType(OperationType.CREATE)
                .build();
    }
    
    /**
     * 创建用于查询操作的DTO
     */
    public static TestDTO forQuery(Integer userId) {
        return TestDTO.builder()
                .userId(userId)
                .operationType(OperationType.QUERY)
                .build();
    }
    
    /**
     * 创建用于删除操作的DTO
     */
    public static TestDTO forDelete(Integer answerId) {
        return TestDTO.builder()
                .answerId(answerId)
                .operationType(OperationType.DELETE)
                .build();
    }
    
    /**
     * 创建MBTI结果响应
     */
    public static TestDTO createMBTIResult(String mbtiType, String title, String description, 
                                          Map<String, String> dimensions, Map<String, Object> statistics) {
        return TestDTO.builder()
                .mbtiType(mbtiType)
                .title(title)
                .description(description)
                .dimensions(dimensions)
                .statistics(statistics)
                .operationType(OperationType.QUERY)
                .build();
    }
    
    /**
     * 内部类：答案详情DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnswerDetailDTO {
        private Integer detailId;
        private Integer answerId;
        private Integer questionId;
        private String questionContent;
        private Integer optionId;
        private String optionContent;
        private Byte optionScore;
        private String selectedOption;
        private LocalDateTime createdAt;
        
        /**
         * 从AnswerDetail实体转换
         */
        public static AnswerDetailDTO fromEntity(AnswerDetail detail) {
            if (detail == null) {
                return null;
            }
            
            return AnswerDetailDTO.builder()
                    .detailId(detail.getDetailId())
                    .answerId(detail.getAnswerId())
                    .questionId(detail.getQuestionId())
                    .questionContent(detail.getQuestion() != null ? detail.getQuestion().getContent() : null)
                    .optionId(detail.getOptionId())
                    .optionContent(detail.getOption() != null ? detail.getOption().getContent() : null)
                    .optionScore(detail.getOption() != null ? detail.getOption().getScore() : null)
                    .build();
        }
    }
}
