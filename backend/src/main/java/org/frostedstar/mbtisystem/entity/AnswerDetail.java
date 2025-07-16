package org.frostedstar.mbtisystem.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * 回答详情实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerDetail {
    
    private Integer detailId;
    private Integer answerId;
    private Integer questionId;
    private Integer optionId;
    
    // 关联的问题信息
    private Question question;
    
    // 关联的选项信息
    private Option option;
}
