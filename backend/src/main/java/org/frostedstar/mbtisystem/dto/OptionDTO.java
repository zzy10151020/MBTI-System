package org.frostedstar.mbtisystem.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

/**
 * 选项DTO类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OptionDTO {
    
    private Long optionId;
    
    private Long questionId;
    
    @NotBlank(message = "选项内容不能为空")
    @Size(max = 500, message = "选项内容不能超过500字符")
    private String content;
    
    @NotNull(message = "选项分数不能为空")
    @Min(value = -1, message = "选项分数不能小于-1")
    @Max(value = 1, message = "选项分数不能大于1")
    private Byte score; // -1, 1 (符合数据库约束)
}
