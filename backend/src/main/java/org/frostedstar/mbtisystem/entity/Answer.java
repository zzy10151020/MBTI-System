package org.frostedstar.mbtisystem.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 回答实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Answer {
    
    private Integer answerId;
    private Integer userId;
    private Integer questionnaireId;
    private LocalDateTime answeredAt;
    
    // 关联的回答详情列表
    private List<AnswerDetail> details;
    
    // 关联的用户信息
    private User user;
    
    // 关联的问卷信息
    private Questionnaire questionnaire;
}
