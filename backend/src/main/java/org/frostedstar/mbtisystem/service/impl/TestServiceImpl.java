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
    public List<Question> startTest(Integer questionnaireId) {
        // 获取问卷的所有问题（按顺序）
        List<Question> questions = questionDAO.findByQuestionnaireIdOrderByQuestionOrder(questionnaireId);
        
        // 为每个问题加载选项
        for (Question question : questions) {
            List<Option> options = optionDAO.findByQuestionId(question.getQuestionId());
            question.setOptions(options);
        }
        
        return questions;
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
    
    @Override
    public Map<String, String> getMBTIDescription(String mbtiType) {
        Map<String, String> description = new HashMap<>();
        
        // 这里可以根据 MBTI 类型返回详细描述
        switch (mbtiType) {
            case "INTJ":
                description.put("title", "建筑师");
                description.put("description", "具有想象力和战略性的思想家，一切皆在计划之中。");
                break;
            case "INTP":
                description.put("title", "思想家");
                description.put("description", "具有创造性的思想家，对知识有着不可抑制的渴望。");
                break;
            case "ENTJ":
                description.put("title", "指挥官");
                description.put("description", "大胆，富有想象力，意志强烈的领导者。");
                break;
            case "ENTP":
                description.put("title", "辩论家");
                description.put("description", "聪明好奇的思想家，不会拒绝智力上的挑战。");
                break;
            case "INFJ":
                description.put("title", "提倡者");
                description.put("description", "安静而神秘，同时鼓舞人心且不知疲倦的理想主义者。");
                break;
            case "INFP":
                description.put("title", "调停者");
                description.put("description", "诗意，善良，利他主义，总是热情地为正义而战。");
                break;
            case "ENFJ":
                description.put("title", "主人公");
                description.put("description", "有魅力鼓舞人心的领导者，有着让听众着迷的能力。");
                break;
            case "ENFP":
                description.put("title", "竞选者");
                description.put("description", "热情，有创造力，社交能力强，总是能找到笑容的理由。");
                break;
            case "ISTJ":
                description.put("title", "物流师");
                description.put("description", "实际，注重事实的可靠性，可以信赖他们完成计划。");
                break;
            case "ISFJ":
                description.put("title", "守护者");
                description.put("description", "非常专注，温暖的守护者，时刻准备着保护爱着的人们。");
                break;
            case "ESTJ":
                description.put("title", "总经理");
                description.put("description", "出色的管理者，在管理事情或人的时候非常高效。");
                break;
            case "ESFJ":
                description.put("title", "执政官");
                description.put("description", "非常关心他人，社交能力强，在团体中备受欢迎。");
                break;
            case "ISTP":
                description.put("title", "鉴赏家");
                description.put("description", "大胆而实际的实验家，擅长使用各种工具。");
                break;
            case "ISFP":
                description.put("title", "探险家");
                description.put("description", "灵活有魅力的艺术家，时刻准备着探索新的可能性。");
                break;
            case "ESTP":
                description.put("title", "企业家");
                description.put("description", "聪明，精力充沛，非常善于感知，真正享受生活。");
                break;
            case "ESFP":
                description.put("title", "娱乐家");
                description.put("description", "自发的，精力充沛，热情的演艺人员，生活永远不会无聊。");
                break;
            default:
                description.put("title", "未知类型");
                description.put("description", "无法识别的 MBTI 类型。");
        }
        
        return description;
    }
}
