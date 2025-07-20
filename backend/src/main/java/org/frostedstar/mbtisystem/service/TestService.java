package org.frostedstar.mbtisystem.service;

import org.frostedstar.mbtisystem.entity.Answer;
import org.frostedstar.mbtisystem.entity.AnswerDetail;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 测试 Service 接口
 */
public interface TestService {
    
    /**
     * 提交测试答案
     */
    Answer submitTest(Integer userId, Integer questionnaireId, List<AnswerDetail> answerDetails);
    
    /**
     * 获取用户的测试结果
     */
    Optional<Answer> getUserTestResult(Integer userId, Integer questionnaireId);
    
    /**
     * 获取用户的所有测试结果
     */
    List<Answer> getUserAllTestResults(Integer userId);
    
    /**
     * 计算 MBTI 结果
     */
    String calculateMBTIResult(List<AnswerDetail> answerDetails);
    
    /**
     * 获取测试结果详情
     */
    Optional<Answer> getTestResultDetail(Integer answerId);
    
    /**
     * 检查用户是否已经完成了某个问卷
     */
    boolean hasUserCompletedTest(Integer userId, Integer questionnaireId);
    
    /**
     * 获取问卷的统计数据
     */
    Map<String, Object> getQuestionnaireStatistics(Integer questionnaireId);
    
    /**
     * 计算维度统计信息（各维度倾向）
     */
    Map<String, String> calculateDimensions(String mbtiResult);
    
    /**
     * 计算维度比例统计
     */
    Map<String, Object> calculateDimensionStatistics(List<AnswerDetail> answerDetails);
    
    /**
     * 计算所有可能的MBTI类型概率
     */
    Map<String, Double> calculatePersonalityProbabilities(List<AnswerDetail> answerDetails);
}
