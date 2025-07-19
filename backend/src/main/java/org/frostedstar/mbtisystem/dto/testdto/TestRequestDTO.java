package org.frostedstar.mbtisystem.dto.testdto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

/**
 * 测试请求DTO - 用于处理各种测试相关的请求参数
 * 支持测试提交、结果查询、删除等操作
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestRequestDTO {
    
    // 查询相关字段
    /**
     * 答案ID
     */
    private Integer answerId;
    
    /**
     * 问卷ID
     */
    private Integer questionnaireId;
    
    // 创建相关字段
    /**
     * 答案详情列表
     */
    private List<AnswerDetailRequestDTO> answerDetails;
    
    // 验证方法
    
    /**
     * 提交测试请求验证（测试提交）
     */
    public boolean isValidForSubmitTest() {
        return questionnaireId != null && questionnaireId > 0 &&
               answerDetails != null && !answerDetails.isEmpty();
    }
    
    /**
     * 查询测试结果请求验证
     */
    public boolean isValidForQueryTestResult() {
        return questionnaireId != null && questionnaireId > 0;
    }
    
    /**
     * 删除测试结果请求验证
     */
    public boolean isValidForDeleteTestResult() {
        return answerId != null && answerId > 0;
    }
    
    /**
     * 答案详情请求DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnswerDetailRequestDTO {
        private Integer questionId;
        private Integer optionId;
        private String selectedOption;
    }
}
