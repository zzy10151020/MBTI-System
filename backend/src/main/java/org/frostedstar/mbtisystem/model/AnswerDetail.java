package org.frostedstar.mbtisystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 回答详情实体类
 */
@Entity
@Table(name = "answer_detail", indexes = {
    @Index(name = "idx_answer_question", columnList = "answer_id, question_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerDetail {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detail_id")
    private Long detailId;
    
    @Column(name = "answer_id", nullable = false)
    private Long answerId;
    
    @Column(name = "question_id", nullable = false)
    private Long questionId;
    
    @Column(name = "option_id", nullable = false)
    private Long optionId;
    
    // 关联关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answer_id", insertable = false, updatable = false)
    private Answer answer;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", insertable = false, updatable = false)
    private Question question;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id", insertable = false, updatable = false)
    private Option option;
}
