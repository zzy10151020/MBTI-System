package org.frostedstar.mbtisystem.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * 问题DTO类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDTO {
    
    private Long questionId;
    
    private Long questionnaireId;
    
    @NotBlank(message = "问题内容不能为空")
    @Size(max = 1000, message = "问题内容不能超过1000字符")
    private String content;
    
    @NotBlank(message = "MBTI维度不能为空")
    private String dimension; // EI, SN, TF, JP
    
    @NotNull(message = "问题顺序不能为空")
    @Positive(message = "问题顺序必须为正数")
    private Short questionOrder;
    
    private List<OptionDTO> options;
}
