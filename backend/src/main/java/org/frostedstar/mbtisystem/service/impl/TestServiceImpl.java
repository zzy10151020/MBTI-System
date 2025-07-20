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
            
            // 加载问卷信息
            Optional<org.frostedstar.mbtisystem.entity.Questionnaire> questionnaireOpt = 
                org.frostedstar.mbtisystem.dao.DaoFactory.getQuestionnaireDao().findById(questionnaireId);
            questionnaireOpt.ifPresent(answer::setQuestionnaire);
            
            // 加载回答详情
            List<AnswerDetail> details = answerDetailDAO.findByAnswerId(answer.getAnswerId());
            
            // 为每个详情加载关联的问题和选项信息
            for (AnswerDetail detail : details) {
                // 加载问题信息
                Optional<org.frostedstar.mbtisystem.entity.Question> questionOpt = 
                    questionDAO.findById(detail.getQuestionId());
                questionOpt.ifPresent(detail::setQuestion);
                
                // 加载选项信息
                Optional<org.frostedstar.mbtisystem.entity.Option> optionOpt = 
                    optionDAO.findById(detail.getOptionId());
                optionOpt.ifPresent(detail::setOption);
            }
            
            answer.setDetails(details);
            return Optional.of(answer);
        }
        
        return Optional.empty();
    }
    
    @Override
    public List<Answer> getUserAllTestResults(Integer userId) {
        List<Answer> answers = answerDAO.findByUserId(userId);
        
        // 为每个答案加载详情和关联信息
        for (Answer answer : answers) {
            // 加载问卷信息
            Optional<org.frostedstar.mbtisystem.entity.Questionnaire> questionnaireOpt = 
                org.frostedstar.mbtisystem.dao.DaoFactory.getQuestionnaireDao().findById(answer.getQuestionnaireId());
            questionnaireOpt.ifPresent(answer::setQuestionnaire);
            
            // 加载回答详情
            List<AnswerDetail> details = answerDetailDAO.findByAnswerId(answer.getAnswerId());
            
            // 为每个详情加载关联的问题和选项信息
            for (AnswerDetail detail : details) {
                // 加载问题信息
                Optional<org.frostedstar.mbtisystem.entity.Question> questionOpt = 
                    questionDAO.findById(detail.getQuestionId());
                questionOpt.ifPresent(detail::setQuestion);
                
                // 加载选项信息
                Optional<org.frostedstar.mbtisystem.entity.Option> optionOpt = 
                    optionDAO.findById(detail.getOptionId());
                optionOpt.ifPresent(detail::setOption);
            }
            
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
    
    @Override
    public Map<String, String> calculateDimensions(String mbtiResult) {
        if (mbtiResult == null || mbtiResult.length() != 4) {
            return Map.of();
        }
        
        return Map.of(
            "E_I", String.valueOf(mbtiResult.charAt(0)), // E 或 I
            "S_N", String.valueOf(mbtiResult.charAt(1)), // S 或 N
            "T_F", String.valueOf(mbtiResult.charAt(2)), // T 或 F
            "J_P", String.valueOf(mbtiResult.charAt(3))  // J 或 P
        );
    }
    
    @Override
    public Map<String, Object> calculateDimensionStatistics(List<AnswerDetail> answerDetails) {
        if (answerDetails == null || answerDetails.isEmpty()) {
            return Map.of();
        }
        
        // 统计每个维度的得分
        int eScore = 0, iScore = 0;  // E/I 维度
        int sScore = 0, nScore = 0;  // S/N 维度  
        int tScore = 0, fScore = 0;  // T/F 维度
        int jScore = 0, pScore = 0;  // J/P 维度
        
        int eCount = 0, sCount = 0, tCount = 0, jCount = 0;
        
        for (AnswerDetail detail : answerDetails) {
            if (detail.getQuestion() == null || detail.getOption() == null) {
                continue;
            }
            
            String dimension = detail.getQuestion().getDimension().toString();
            byte optionScore = detail.getOption().getScore();
            
            switch (dimension) {
                case "E/I":
                    if (optionScore > 0) {
                        eScore += optionScore;
                    } else {
                        iScore += Math.abs(optionScore);
                    }
                    eCount++;
                    break;
                case "S/N":
                    if (optionScore > 0) {
                        nScore += optionScore;
                    } else {
                        sScore += Math.abs(optionScore);
                    }
                    sCount++;
                    break;
                case "T/F":
                    if (optionScore > 0) {
                        tScore += optionScore;
                    } else {
                        fScore += Math.abs(optionScore);
                    }
                    tCount++;
                    break;
                case "J/P":
                    if (optionScore > 0) {
                        jScore += optionScore;
                    } else {
                        pScore += Math.abs(optionScore);
                    }
                    jCount++;
                    break;
            }
        }
        
        // 计算百分比
        Map<String, Object> statistics = new HashMap<>();
        
        if (eCount > 0) {
            int totalEI = eScore + iScore;
            if (totalEI > 0) {
                statistics.put("E_percentage", Math.round(eScore * 100.0 / totalEI));
                statistics.put("I_percentage", Math.round(iScore * 100.0 / totalEI));
            }
        }
        
        if (sCount > 0) {
            int totalSN = sScore + nScore;
            if (totalSN > 0) {
                statistics.put("S_percentage", Math.round(sScore * 100.0 / totalSN));
                statistics.put("N_percentage", Math.round(nScore * 100.0 / totalSN));
            }
        }
        
        if (tCount > 0) {
            int totalTF = tScore + fScore;
            if (totalTF > 0) {
                statistics.put("T_percentage", Math.round(tScore * 100.0 / totalTF));
                statistics.put("F_percentage", Math.round(fScore * 100.0 / totalTF));
            }
        }
        
        if (jCount > 0) {
            int totalJP = jScore + pScore;
            if (totalJP > 0) {
                statistics.put("J_percentage", Math.round(jScore * 100.0 / totalJP));
                statistics.put("P_percentage", Math.round(pScore * 100.0 / totalJP));
            }
        }
        
        return statistics;
    }
    
    @Override
    public Map<String, Double> calculatePersonalityProbabilities(List<AnswerDetail> answerDetails) {
        if (answerDetails == null || answerDetails.isEmpty()) {
            return Map.of();
        }
        
        // 获取维度统计
        Map<String, Object> dimensionStats = calculateDimensionStatistics(answerDetails);
        
        // 提取各维度的概率
        double eProb = ((Number) dimensionStats.getOrDefault("E_percentage", 50)).doubleValue() / 100.0;
        double iProb = ((Number) dimensionStats.getOrDefault("I_percentage", 50)).doubleValue() / 100.0;
        double sProb = ((Number) dimensionStats.getOrDefault("S_percentage", 50)).doubleValue() / 100.0;
        double nProb = ((Number) dimensionStats.getOrDefault("N_percentage", 50)).doubleValue() / 100.0;
        double tProb = ((Number) dimensionStats.getOrDefault("T_percentage", 50)).doubleValue() / 100.0;
        double fProb = ((Number) dimensionStats.getOrDefault("F_percentage", 50)).doubleValue() / 100.0;
        double jProb = ((Number) dimensionStats.getOrDefault("J_percentage", 50)).doubleValue() / 100.0;
        double pProb = ((Number) dimensionStats.getOrDefault("P_percentage", 50)).doubleValue() / 100.0;
        
        // 计算所有16种MBTI类型的概率
        Map<String, Double> probabilities = new HashMap<>();
        
        String[] types = {"INTJ", "INTP", "ENTJ", "ENTP", "INFJ", "INFP", "ENFJ", "ENFP",
                         "ISTJ", "ISFJ", "ESTJ", "ESFJ", "ISTP", "ISFP", "ESTP", "ESFP"};
        
        for (String type : types) {
            double prob = 1.0;
            
            // E/I 维度
            prob *= (type.charAt(0) == 'E') ? eProb : iProb;
            
            // S/N 维度
            prob *= (type.charAt(1) == 'S') ? sProb : nProb;
            
            // T/F 维度
            prob *= (type.charAt(2) == 'T') ? tProb : fProb;
            
            // J/P 维度
            prob *= (type.charAt(3) == 'J') ? jProb : pProb;
            
            probabilities.put(type, Math.round(prob * 100 * 100.0) / 100.0); // 保留两位小数
        }
        
        // 按概率降序排序，只返回概率大于1%的类型
        return probabilities.entrySet().stream()
                .filter(entry -> entry.getValue() >= 1.0)
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .collect(java.util.LinkedHashMap::new,
                        (map, entry) -> map.put(entry.getKey(), entry.getValue()),
                        java.util.LinkedHashMap::putAll);
    }
}
