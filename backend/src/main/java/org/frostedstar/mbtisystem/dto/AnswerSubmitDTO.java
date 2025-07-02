package org.frostedstar.mbtisystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.frostedstar.mbtisystem.model.MbtiDimension;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 答案提交数据传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerSubmitDTO {
    
    /**
     * 问卷ID
     */
    @NotNull(message = "问卷ID不能为空")
    private Long questionnaireId;
    
    /**
     * 问题答案映射 (问题ID -> 选项ID)
     * 支持字符串键的自动转换
     */
    @NotEmpty(message = "答案不能为空")
    private Map<Long, Long> questionAnswers = new HashMap<>();
    
    /**
     * 设置问题答案，支持字符串键的自动转换
     */
    public void setQuestionAnswers(Map<?, ?> answers) {
        this.questionAnswers = new HashMap<>();
        if (answers != null) {
            for (Map.Entry<?, ?> entry : answers.entrySet()) {
                Long questionId = convertToLong(entry.getKey());
                Long optionId = convertToLong(entry.getValue());
                this.questionAnswers.put(questionId, optionId);
            }
        }
    }
    
    /**
     * 将对象转换为Long类型
     */
    private Long convertToLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Long) {
            return (Long) value;
        }
        if (value instanceof Integer) {
            return ((Integer) value).longValue();
        }
        if (value instanceof String) {
            return Long.parseLong((String) value);
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        throw new IllegalArgumentException("无法将 " + value.getClass().getSimpleName() + " 转换为 Long");
    }
}

/**
 * 测试结果数据传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
class TestResultDTO {
    
    /**
     * 回答ID
     */
    private Long answerId;
    
    /**
     * 问卷信息
     */
    private QuestionnaireDTO questionnaire;
    
    /**
     * 回答时间
     */
    private LocalDateTime answeredAt;
    
    /**
     * MBTI类型结果
     */
    private String mbtiType;
    
    /**
     * 各维度分数
     */
    private Map<MbtiDimension, Integer> dimensionScores;
    
    /**
     * 各维度百分比
     */
    private Map<String, Double> dimensionPercentages;
    
    /**
     * 详细回答信息
     */
    private List<AnswerDetailDTO> answerDetails;
    
    /**
     * MBTI类型描述
     */
    private String mbtiDescription;
}

/**
 * 回答详情数据传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
class AnswerDetailDTO {
    
    /**
     * 详情ID
     */
    private Long detailId;
    
    /**
     * 问题内容
     */
    private String questionContent;
    
    /**
     * 选择的选项内容
     */
    private String selectedOption;
    
    /**
     * 选项分数
     */
    private Byte score;
    
    /**
     * MBTI维度
     */
    private MbtiDimension dimension;
    
    /**
     * 问题顺序
     */
    private Short questionOrder;
}

/**
 * 用户答题历史数据传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
class UserAnswerHistoryDTO {
    
    /**
     * 用户信息
     */
    private UserDTO user;
    
    /**
     * 答题历史列表
     */
    private List<TestResultDTO> testResults;
    
    /**
     * 总答题数量
     */
    private Long totalAnswers;
    
    /**
     * 最近答题时间
     */
    private LocalDateTime lastAnsweredAt;
}

/**
 * 密码修改数据传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
class ChangePasswordDTO {
    
    /**
     * 原密码
     */
    @NotNull(message = "原密码不能为空")
    private String oldPassword;
    
    /**
     * 新密码
     */
    @NotNull(message = "新密码不能为空")
    private String newPassword;
}

/**
 * 用户信息更新数据传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
class UserUpdateDTO {
    
    /**
     * 邮箱
     */
    private String email;
}
