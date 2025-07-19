package org.frostedstar.mbtisystem.dto.questiondto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.frostedstar.mbtisystem.dto.optiondto.*;
import org.frostedstar.mbtisystem.entity.Question;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 问题请求DTO - 用于处理各种问题相关的请求参数
 * 支持查询、创建、更新、删除等操作
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionRequestDTO {
    
    // 查询相关字段
    /**
     * 问题ID
     */
    private Integer questionId;
    
    /**
     * 问卷ID
     */
    private Integer questionnaireId;
    
    /**
     * 维度
     */
    private String dimension;
    
    /**
     * 通用ID字段 - 可用于删除等操作
     */
    private Integer id;
    
    // 创建/更新相关字段
    /**
     * 问题内容
     */
    private String content;
    
    /**
     * 问题顺序
     */
    private Short questionOrder;
    
    /**
     * 选项列表（创建时使用）
     */
    private List<OptionRequestDTO> options;
    
    // 验证方法
    
    /**
     * 验证问卷ID查询请求
     */
    public boolean isValidForQuestionnaireQuery() {
        return questionnaireId != null && questionnaireId > 0;
    }
    
    /**
     * 验证维度查询请求
     */
    public boolean isValidForDimensionQuery() {
        return dimension != null && !dimension.trim().isEmpty() && isValidDimension(dimension);
    }
    
    /**
     * 验证问题详情查询请求
     */
    public boolean isValidForDetailQuery() {
        return (questionId != null && questionId > 0) || (id != null && id > 0);
    }
    
    /**
     * 创建问题请求验证
     */
    public boolean isValidForCreateQuestion() {
        return questionnaireId != null && questionnaireId > 0 &&
               content != null && !content.trim().isEmpty() &&
               dimension != null && !dimension.trim().isEmpty() &&
               isValidDimension(dimension);
    }
    
    /**
     * 更新问题请求验证
     */
    public boolean isValidForUpdateQuestion() {
        return questionId != null && questionId > 0 &&
               content != null && !content.trim().isEmpty() &&
               dimension != null && !dimension.trim().isEmpty() &&
               isValidDimension(dimension);
    }
    
    /**
     * 删除问题请求验证
     */
    public boolean isValidForDeleteQuestion() {
        return (id != null && id > 0) || (questionId != null && questionId > 0);
    }
    
    /**
     * 验证MBTI维度
     */
    private boolean isValidDimension(String dimension) {
        return "EI".equals(dimension) || "SN".equals(dimension) || 
               "TF".equals(dimension) || "JP".equals(dimension);
    }
    
    /**
     * 获取实际的问题ID（优先使用questionId，然后是id）
     */
    public Integer getActualQuestionId() {
        return questionId != null ? questionId : id;
    }
    
    /**
     * 获取实际的删除ID（优先使用id，然后是questionId）
     */
    public Integer getActualId() {
        return id != null ? id : questionId;
    }
    
    /**
     * 从请求DTO转换为Question实体
     */
    public Question toEntity() {
        Question question = new Question();
        question.setQuestionId(this.questionId);
        question.setQuestionnaireId(this.questionnaireId);
        question.setContent(this.content);
        question.setDimension(this.dimension != null ?
                Question.Dimension.valueOf(this.dimension) : null);
        question.setQuestionOrder(this.questionOrder);

        // 选项转换
        if (this.options != null) {
            question.setOptions(this.options.stream()
                    .map(option -> option.toEntity())
                    .collect(Collectors.toList()));
        }

        return question;
    }
    
    /**
     * 静态方法：从请求DTO转换为Question实体
     */
    public static Question toEntity(QuestionRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        return dto.toEntity();
    }
}
