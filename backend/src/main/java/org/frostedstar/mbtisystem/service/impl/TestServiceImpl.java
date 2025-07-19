package org.frostedstar.mbtisystem.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.frostedstar.mbtisystem.dao.AnswerDAO;
import org.frostedstar.mbtisystem.dao.AnswerDetailDAO;
import org.frostedstar.mbtisystem.dao.OptionDAO;
import org.frostedstar.mbtisystem.dao.QuestionDAO;
import org.frostedstar.mbtisystem.dao.impl.AnswerDAOImpl;
import org.frostedstar.mbtisystem.dao.impl.AnswerDetailDAOImpl;
import org.frostedstar.mbtisystem.dao.impl.OptionDAOImpl;
import org.frostedstar.mbtisystem.dao.impl.QuestionDAOImpl;
import org.frostedstar.mbtisystem.entity.*;
import org.frostedstar.mbtisystem.service.TestService;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 测试 Service 实现
 */
@Slf4j
public class TestServiceImpl implements TestService {
    
    private final QuestionDAO questionDAO;
    private final OptionDAO optionDAO;
    private final AnswerDAO answerDAO;
    private final AnswerDetailDAO answerDetailDAO;
    
    public TestServiceImpl() {
        this.questionDAO = new QuestionDAOImpl();
        this.optionDAO = new OptionDAOImpl();
        this.answerDAO = new AnswerDAOImpl();
        this.answerDetailDAO = new AnswerDetailDAOImpl();
    }
    
    @Override
    public Answer submitTest(Integer userId, Integer questionnaireId, List<AnswerDetail> answerDetails) {
        // 检查用户是否已经完成了这个问卷
        if (hasUserCompletedTest(userId, questionnaireId)) {
            throw new RuntimeException("用户已经完成了这个问卷");
        }
        
        // 创建回答记录
        Answer answer = Answer.builder()
                .userId(userId)
                .questionnaireId(questionnaireId)
                .answeredAt(LocalDateTime.now())
                .build();
        
        Answer savedAnswer = answerDAO.save(answer);
        
        // 设置回答详情的 answerId
        for (AnswerDetail detail : answerDetails) {
            detail.setAnswerId(savedAnswer.getAnswerId());
        }
        
        // 批量保存回答详情
        answerDetailDAO.saveBatch(answerDetails);
        
        log.info("用户 {} 完成了问卷 {} 的测试", userId, questionnaireId);
        return savedAnswer;
    }
    
    @Override
    public Optional<Answer> getUserTestResult(Integer userId, Integer questionnaireId) {
        Optional<Answer> answerOptional = answerDAO.findByUserIdAndQuestionnaireId(userId, questionnaireId);
        
        if (answerOptional.isPresent()) {
            Answer answer = answerOptional.get();
            // 加载回答详情
            List<AnswerDetail> details = answerDetailDAO.findByAnswerId(answer.getAnswerId());
            answer.setDetails(details);
            return Optional.of(answer);
        }
        
        return Optional.empty();
    }
    
    @Override
    public List<Answer> getUserAllTestResults(Integer userId) {
        List<Answer> answers = answerDAO.findByUserId(userId);
        
        // 为每个答案加载详情
        for (Answer answer : answers) {
            List<AnswerDetail> details = answerDetailDAO.findByAnswerId(answer.getAnswerId());
            answer.setDetails(details);
        }
        
        return answers;
    }
    
    @Override
    public String calculateMBTIResult(List<AnswerDetail> answerDetails) {
        Map<Question.Dimension, Integer> scores = new HashMap<>();
        
        // 初始化分数
        for (Question.Dimension dimension : Question.Dimension.values()) {
            scores.put(dimension, 0);
        }
        
        // 计算每个维度的分数
        for (AnswerDetail detail : answerDetails) {
            // 获取问题信息
            Optional<Question> questionOptional = questionDAO.findById(detail.getQuestionId());
            if (questionOptional.isPresent()) {
                Question question = questionOptional.get();
                
                // 获取选项信息
                Optional<Option> optionOptional = optionDAO.findById(detail.getOptionId());
                if (optionOptional.isPresent()) {
                    Option option = optionOptional.get();
                    
                    // 累加分数
                    int currentScore = scores.get(question.getDimension());
                    scores.put(question.getDimension(), currentScore + option.getScore());
                }
            }
        }
        
        // 根据分数确定 MBTI 类型
        StringBuilder mbtiResult = new StringBuilder();
        
        // E/I 维度
        mbtiResult.append(scores.get(Question.Dimension.EI) > 0 ? "E" : "I");
        
        // S/N 维度
        mbtiResult.append(scores.get(Question.Dimension.SN) > 0 ? "S" : "N");
        
        // T/F 维度
        mbtiResult.append(scores.get(Question.Dimension.TF) > 0 ? "T" : "F");
        
        // J/P 维度
        mbtiResult.append(scores.get(Question.Dimension.JP) > 0 ? "J" : "P");
        
        return mbtiResult.toString();
    }
    
    @Override
    public Optional<Answer> getTestResultDetail(Integer answerId) {
        Optional<Answer> answerOptional = answerDAO.findById(answerId);
        
        if (answerOptional.isPresent()) {
            Answer answer = answerOptional.get();
            
            // 加载回答详情
            List<AnswerDetail> details = answerDetailDAO.findByAnswerId(answerId);
            
            // 为每个详情加载问题和选项信息
            for (AnswerDetail detail : details) {
                Optional<Question> questionOptional = questionDAO.findById(detail.getQuestionId());
                questionOptional.ifPresent(detail::setQuestion);
                
                Optional<Option> optionOptional = optionDAO.findById(detail.getOptionId());
                optionOptional.ifPresent(detail::setOption);
            }
            
            answer.setDetails(details);
            return Optional.of(answer);
        }
        
        return Optional.empty();
    }
    
    @Override
    public boolean hasUserCompletedTest(Integer userId, Integer questionnaireId) {
        return answerDAO.existsByUserIdAndQuestionnaireId(userId, questionnaireId);
    }
    
    @Override
    public Map<String, Object> getQuestionnaireStatistics(Integer questionnaireId) {
        Map<String, Object> statistics = new HashMap<>();
        
        // 统计参与测试的人数
        List<Answer> answers = answerDAO.findByQuestionnaireId(questionnaireId);
        statistics.put("totalParticipants", answers.size());
        
        // 统计 MBTI 类型分布
        Map<String, Integer> mbtiDistribution = new HashMap<>();
        
        for (Answer answer : answers) {
            List<AnswerDetail> details = answerDetailDAO.findByAnswerId(answer.getAnswerId());
            String mbtiType = calculateMBTIResult(details);
            mbtiDistribution.put(mbtiType, mbtiDistribution.getOrDefault(mbtiType, 0) + 1);
        }
        
        statistics.put("mbtiDistribution", mbtiDistribution);
        
        // 统计最近完成测试的时间
        if (!answers.isEmpty()) {
            LocalDateTime latestTest = answers.stream()
                    .map(Answer::getAnsweredAt)
                    .max(LocalDateTime::compareTo)
                    .orElse(null);
            statistics.put("latestTestTime", latestTest);
        }
        
        return statistics;
    }
}
