package org.frostedstar.mbtisystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 选项实体类
 */
@Entity
@Table(name = "option")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Option {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_id")
    private Long optionId;
    
    @Column(name = "question_id", nullable = false)
    private Long questionId;
    
    @Column(nullable = false)
    private String content;
    
    @Column(nullable = false)
    private Byte score; // -1 或 1
    
    // 关联关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", insertable = false, updatable = false)
    private Question question;
    
    @OneToMany(mappedBy = "option")
    private List<AnswerDetail> answerDetails = new ArrayList<>();
}
