package org.frostedstar.mbtisystem.dto.questiondto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.frostedstar.mbtisystem.dto.optiondto.OptionResponseDTO;
import org.frostedstar.mbtisystem.entity.Question;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 问题响应DTO
 * 用于返回问题数据给客户端
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResponseDTO {
    
    // 基本信息
    private Integer questionId;
    private Integer questionnaireId;
    private String content;
    private String dimension; // EI, SN, TF, JP
    private Short questionOrder;
    private LocalDateTime createdAt;
    
    // 关联信息（仅查询时使用）
    private List<OptionResponseDTO> options;
    
    /**
     * 从Question实体转换为QuestionResponseDTO（完整版）
     */
    public static QuestionResponseDTO fromEntity(Question question) {
        if (question == null) {
            return null;
        }
        
        return QuestionResponseDTO.builder()
                .questionId(question.getQuestionId())
                .questionnaireId(question.getQuestionnaireId())
                .content(question.getContent())
                .dimension(question.getDimension() != null ? question.getDimension().name() : null)
                .questionOrder(question.getQuestionOrder())
                .options(question.getOptions() != null ? 
                    question.getOptions().stream()
                        .map(OptionResponseDTO::fromEntity)
                        .collect(Collectors.toList()) : null)
                .build();
    }
    
    /**
     * 从Question实体转换为QuestionResponseDTO（无选项）
     */
    public static QuestionResponseDTO fromEntitySimple(Question question) {
        if (question == null) {
            return null;
        }
        
        return QuestionResponseDTO.builder()
                .questionId(question.getQuestionId())
                .questionnaireId(question.getQuestionnaireId())
                .content(question.getContent())
                .dimension(question.getDimension() != null ? question.getDimension().name() : null)
                .questionOrder(question.getQuestionOrder())
                .build();
    }
}
