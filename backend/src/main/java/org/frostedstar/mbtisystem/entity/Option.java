package org.frostedstar.mbtisystem.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * 选项实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Option {
    
    private Integer optionId;
    private Integer questionId;
    private String content;
    private Byte score;  // -1 或 1
}
