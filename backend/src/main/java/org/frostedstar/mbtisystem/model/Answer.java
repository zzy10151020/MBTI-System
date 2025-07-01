package org.frostedstar.mbtisystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 回答实体类
 */
@Entity
@Table(name = "answer", indexes = {
    @Index(name = "idx_user_questionnaire", columnList = "user_id, questionnaire_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Answer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id")
    private Long answerId;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "questionnaire_id", nullable = false)
    private Long questionnaireId;
    
    @CreationTimestamp
    @Column(name = "answered_at", nullable = false)
    private LocalDateTime answeredAt;
    
    // 关联关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "questionnaire_id", insertable = false, updatable = false)
    private Questionnaire questionnaire;
    
    @OneToMany(mappedBy = "answer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnswerDetail> answerDetails = new ArrayList<>();
}
