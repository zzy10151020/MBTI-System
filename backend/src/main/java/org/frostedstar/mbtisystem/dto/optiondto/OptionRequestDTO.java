package org.frostedstar.mbtisystem.dto.optiondto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.frostedstar.mbtisystem.entity.Option;

/**
 * 选项请求DTO - 用于处理各种选项相关的请求参数
 * 支持创建、更新、删除等操作
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptionRequestDTO {
    
    // 查询相关字段
    /**
     * 选项ID
     */
    private Integer optionId;
    
    /**
     * 问题ID
     */
    private Integer questionId;
    
    // 创建/更新相关字段
    /**
     * 选项内容
     */
    private String content;
    
    /**
     * 选项分数 (-1 或 1)
     */
    private Byte score;

    
    // 验证方法
    
    /**
     * 创建选项请求验证
     */
    public boolean isValidForCreateOption() {
        return questionId != null && questionId > 0 &&
               content != null && !content.trim().isEmpty() &&
               score != null && isValidScore(score);
    }
    
    /**
     * 更新选项请求验证
     */
    public boolean isValidForUpdateOption() {
        return optionId != null && optionId > 0 &&
               content != null && !content.trim().isEmpty() &&
               score != null && isValidScore(score);
    }
    
    /**
     * 删除选项请求验证
     */
    public boolean isValidForDeleteOption() {
        return optionId != null && optionId > 0;
    }
    
    /**
     * 验证分数值 (只能是 -1 或 1)
     */
    private boolean isValidScore(Byte score) {
        return score != null && (score == -1 || score == 1);
    }
    
    /**
     * 从请求DTO转换为Option实体
     */
    public Option toEntity() {
        Option option = new Option();
        option.setOptionId(this.optionId);
        option.setQuestionId(this.questionId);
        option.setContent(this.content);
        option.setScore(this.score);
        return option;
    }
    
    /**
     * 静态方法：从请求DTO转换为Option实体
     */
    public static Option toEntity(OptionRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        return dto.toEntity();
    }
}
