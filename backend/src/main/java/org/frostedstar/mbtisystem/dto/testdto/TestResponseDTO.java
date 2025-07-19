package org.frostedstar.mbtisystem.dto.testdto;

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
 * 测试响应DTO
 * 用于返回测试数据给客户端
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestResponseDTO {
    
    // 基本信息
    private Integer answerId;
    private Integer userId;
    private Integer questionnaireId;
    private String result;
    private String resultDescription;
    private LocalDateTime createdAt;
    
    // 答案详情
    private List<AnswerDetailResponseDTO> answerDetails;
    
    // MBTI结果相关字段
    private String mbtiType;
    private String title;
    private String description;
    private Map<String, String> dimensions;
    private Map<String, Object> statistics;
    
    /**
     * 从Answer实体转换为TestResponseDTO
     */
    public static TestResponseDTO fromEntity(Answer answer) {
        if (answer == null) {
            return null;
        }
        
        List<AnswerDetailResponseDTO> answerDetailDTOs = null;
        if (answer.getDetails() != null) {
            answerDetailDTOs = answer.getDetails().stream()
                    .map(AnswerDetailResponseDTO::fromEntity)
                    .collect(Collectors.toList());
        }
        
        return TestResponseDTO.builder()
                .answerId(answer.getAnswerId())
                .userId(answer.getUserId())
                .questionnaireId(answer.getQuestionnaireId())
                .createdAt(answer.getAnsweredAt())
                .answerDetails(answerDetailDTOs)
                .build();
    }
    
    /**
     * 创建MBTI结果响应
     */
    public static TestResponseDTO createMBTIResult(String mbtiType, String title, String description, 
                                          Map<String, String> dimensions, Map<String, Object> statistics) {
        return TestResponseDTO.builder()
                .mbtiType(mbtiType)
                .title(title)
                .description(description)
                .dimensions(dimensions)
                .statistics(statistics)
                .build();
    }
    
    /**
     * 答案详情响应DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnswerDetailResponseDTO {
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
        public static AnswerDetailResponseDTO fromEntity(AnswerDetail detail) {
            if (detail == null) {
                return null;
            }
            
            return AnswerDetailResponseDTO.builder()
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
