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
    private Map<String, Double> personalityProbabilities;
    
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
     * 从Answer实体转换为TestResponseDTO，包含详细信息和MBTI结果
     */
    public static TestResponseDTO fromEntityWithDetails(Answer answer, String mbtiResult, 
            Map<String, String> dimensions, Map<String, Object> statistics, 
            Map<String, Double> personalityProbabilities) {
        if (answer == null) {
            return null;
        }
        
        List<AnswerDetailResponseDTO> answerDetailDTOs = null;
        if (answer.getDetails() != null) {
            answerDetailDTOs = answer.getDetails().stream()
                    .map(AnswerDetailResponseDTO::fromEntityWithDetails)
                    .collect(Collectors.toList());
        }
        
        // 获取问卷信息用于标题和描述
        String title = answer.getQuestionnaire() != null ? answer.getQuestionnaire().getTitle() : null;
        String description = answer.getQuestionnaire() != null ? answer.getQuestionnaire().getDescription() : null;
        
        // 构建MBTI类型描述
        String resultDescription = mbtiResult != null ? getMBTIDescription(mbtiResult) : null;
        
        return TestResponseDTO.builder()
                .answerId(answer.getAnswerId())
                .userId(answer.getUserId())
                .questionnaireId(answer.getQuestionnaireId())
                .result(mbtiResult)
                .resultDescription(resultDescription)
                .createdAt(answer.getAnsweredAt())
                .answerDetails(answerDetailDTOs)
                .mbtiType(mbtiResult)
                .title(title)
                .description(description)
                .dimensions(dimensions)
                .statistics(statistics)
                .personalityProbabilities(personalityProbabilities)
                .build();
    }
    
    /**
     * 获取MBTI类型描述
     */
    private static String getMBTIDescription(String mbtiType) {
        if (mbtiType == null) return null;
        
        Map<String, String> descriptions = Map.ofEntries(
            Map.entry("INTJ", "建筑师 - 富有想象力和战略性的思想家，一切皆在计划中。"),
            Map.entry("INTP", "思想家 - 具有创造性的思想家，对知识有着不懈的渴求。"),
            Map.entry("ENTJ", "指挥官 - 大胆，富有想象力，意志强烈的领导者。"),
            Map.entry("ENTP", "辩论家 - 聪明好奇的思想家，不会放过任何挑战。"),
            Map.entry("INFJ", "提倡者 - 安静而神秘，同时鼓舞人心且不知疲倦的理想主义者。"),
            Map.entry("INFP", "调停者 - 诗意，善良和利他主义，总是热切地帮助好的事业。"),
            Map.entry("ENFJ", "主人公 - 魅力非凡的鼓舞者，能够让听众着迷。"),
            Map.entry("ENFP", "竞选者 - 热情，有创造性和社交能力强的自由精神。"),
            Map.entry("ISTJ", "物流师 - 实用和注重事实，可靠性无人能及。"),
            Map.entry("ISFJ", "守护者 - 非常专注和温暖的守护者，时刻准备保护爱着的人们。"),
            Map.entry("ESTJ", "总经理 - 优秀的管理者，在管理事情或人的时候是无与伦比的。"),
            Map.entry("ESFJ", "执政官 - 极有同情心，善于交际和受人欢迎，总是热切地帮助他人。"),
            Map.entry("ISTP", "鉴赏家 - 大胆而实际的实验家，擅长使用各种工具。"),
            Map.entry("ISFP", "探险家 - 灵活有魅力的艺术家，时刻准备探索新的可能性。"),
            Map.entry("ESTP", "企业家 - 聪明，精力充沛和善于感知的人们，真心喜欢生活在边缘。"),
            Map.entry("ESFP", "娱乐者 - 自发的，精力充沛和热情的人们，生活对他们来说从不无聊。")
        );
        
        return descriptions.getOrDefault(mbtiType, "未知性格类型");
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
        
        /**
         * 从AnswerDetail实体转换，包含详细信息
         */
        public static AnswerDetailResponseDTO fromEntityWithDetails(AnswerDetail detail) {
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
                    .selectedOption(detail.getOption() != null ? detail.getOption().getContent() : null)
                    .build();
        }
    }
}
