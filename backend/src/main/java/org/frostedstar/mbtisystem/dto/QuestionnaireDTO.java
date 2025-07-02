package org.frostedstar.mbtisystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 问卷数据传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionnaireDTO {
    
    /**
     * 问卷ID
     */
    private Long questionnaireId;
    
    /**
     * 问卷标题
     */
    @NotBlank(message = "问卷标题不能为空")
    @Size(max = 100, message = "问卷标题不能超过100个字符")
    private String title;
    
    /**
     * 问卷描述
     */
    private String description;
    
    /**
     * 创建者ID
     */
    private Long creatorId;
    
    /**
     * 创建者用户名
     */
    private String creatorUsername;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 是否已发布
     */
    private Boolean isPublished;
    
    /**
     * 问题列表
     */
    private List<QuestionDTO> questions;
    
    /**
     * 回答数量
     */
    private Long answerCount;
    
    /**
     * 用户是否已回答
     */
    private Boolean hasAnswered;
}
