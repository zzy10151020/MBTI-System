package org.frostedstar.mbtisystem.dto.optiondto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.frostedstar.mbtisystem.entity.Option;

/**
 * 选项响应DTO
 * 用于返回选项数据给客户端
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptionResponseDTO {
    
    // 基本信息
    private Integer optionId;
    private Integer questionId;
    private String content;
    private String value; // A, B, C, D
    
    /**
     * 从Option实体转换为OptionResponseDTO
     */
    public static OptionResponseDTO fromEntity(Option option) {
        if (option == null) {
            return null;
        }
        
        return OptionResponseDTO.builder()
                .optionId(option.getOptionId())
                .questionId(option.getQuestionId())
                .content(option.getContent())
                .value(scoreToValue(option.getScore()))
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
}
