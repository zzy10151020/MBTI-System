package org.frostedstar.mbtisystem.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 问卷实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Questionnaire {
    
    private Integer questionnaireId;
    private String title;
    private String description;
    private Integer creatorId;
    private LocalDateTime createdAt;
    private Boolean isPublished;
    
    // 关联的问题列表
    private List<Question> questions;
    
    // 创建者信息
    private User creator;
}
