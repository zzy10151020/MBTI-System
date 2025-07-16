package org.frostedstar.mbtisystem.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

/**
 * 问题实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    
    private Integer questionId;
    private Integer questionnaireId;
    private String content;
    private Dimension dimension;
    private Short questionOrder;
    
    // 关联的选项列表
    private List<Option> options;
    
    /**
     * MBTI 维度枚举
     */
    public enum Dimension {
        EI("E/I"),  // 外向-内向
        SN("S/N"),  // 感觉-直觉
        TF("T/F"),  // 思考-情感
        JP("J/P");  // 判断-知觉
        
        private final String value;
        
        Dimension(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static Dimension fromValue(String value) {
            for (Dimension dimension : values()) {
                if (dimension.value.equals(value)) {
                    return dimension;
                }
            }
            throw new IllegalArgumentException("Unknown dimension: " + value);
        }
    }
}
