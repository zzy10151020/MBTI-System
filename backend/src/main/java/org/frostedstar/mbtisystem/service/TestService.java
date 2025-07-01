package org.frostedstar.mbtisystem.service;

import org.frostedstar.mbtisystem.dto.AnswerSubmitDTO;
import org.frostedstar.mbtisystem.model.*;
import org.frostedstar.mbtisystem.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 测试服务层（答题和结果分析）
 */
@Service
@Transactional
public class TestService {

    private final QuestionnaireRepository questionnaireRepository;
    private final QuestionRepository questionRepository;
    private final OptionRepository optionRepository;
    private final AnswerRepository answerRepository;
    private final AnswerDetailRepository answerDetailRepository;
    private final AuthService authService;

    public TestService(QuestionnaireRepository questionnaireRepository,
                      QuestionRepository questionRepository,
                      OptionRepository optionRepository,
                      AnswerRepository answerRepository,
                      AnswerDetailRepository answerDetailRepository,
                      AuthService authService) {
        this.questionnaireRepository = questionnaireRepository;
        this.questionRepository = questionRepository;
        this.optionRepository = optionRepository;
        this.answerDetailRepository = answerDetailRepository;
        this.answerRepository = answerRepository;
        this.authService = authService;
    }

    /**
     * 开始答题（检查问卷是否可答）
     */
    @Transactional(readOnly = true)
    public QuestionnaireTestInfo startTest(Long questionnaireId) {
        Long currentUserId = authService.getCurrentUserId();
        if (currentUserId == null) {
            throw new RuntimeException("用户未登录");
        }

        // 检查问卷是否存在且已发布
        Questionnaire questionnaire = questionnaireRepository.findById(questionnaireId)
                .orElseThrow(() -> new RuntimeException("问卷不存在"));

        if (!questionnaire.getIsPublished()) {
            throw new RuntimeException("问卷未发布，无法答题");
        }

        // 检查用户是否已经回答过
        if (answerRepository.existsByUserIdAndQuestionnaireId(currentUserId, questionnaireId)) {
            throw new RuntimeException("您已经回答过这个问卷了");
        }

        // 获取问题和选项
        List<Question> questions = questionRepository.findQuestionsWithOptionsByQuestionnaireId(questionnaireId);
        if (questions.isEmpty()) {
            throw new RuntimeException("问卷中没有问题");
        }

        return new QuestionnaireTestInfo(questionnaire, questions);
    }

    /**
     * 提交答案
     */
    public TestResult submitAnswer(Long questionnaireId, Map<Long, Long> questionAnswers) {
        Long currentUserId = authService.getCurrentUserId();
        if (currentUserId == null) {
            throw new RuntimeException("用户未登录");
        }

        // 验证问卷
        Questionnaire questionnaire = questionnaireRepository.findById(questionnaireId)
                .orElseThrow(() -> new RuntimeException("问卷不存在"));

        if (!questionnaire.getIsPublished()) {
            throw new RuntimeException("问卷未发布，无法提交答案");
        }

        // 检查用户是否已经回答过
        if (answerRepository.existsByUserIdAndQuestionnaireId(currentUserId, questionnaireId)) {
            throw new RuntimeException("您已经回答过这个问卷了");
        }

        // 验证所有问题都已回答
        List<Question> questions = questionRepository.findByQuestionnaireIdOrderByQuestionOrder(questionnaireId);
        Set<Long> questionIds = questions.stream().map(Question::getQuestionId).collect(Collectors.toSet());
        
        if (!questionAnswers.keySet().equals(questionIds)) {
            throw new RuntimeException("请回答所有问题");
        }

        // 验证选项是否属于对应问题
        for (Map.Entry<Long, Long> entry : questionAnswers.entrySet()) {
            Long questionId = entry.getKey();
            Long optionId = entry.getValue();
            
            Option option = optionRepository.findById(optionId)
                    .orElseThrow(() -> new RuntimeException("选项不存在"));
            
            if (!option.getQuestionId().equals(questionId)) {
                throw new RuntimeException("选项与问题不匹配");
            }
        }

        // 创建答案记录
        Answer answer = new Answer();
        answer.setUserId(currentUserId);
        answer.setQuestionnaireId(questionnaireId);
        answer = answerRepository.save(answer);

        // 创建答案详情
        for (Map.Entry<Long, Long> entry : questionAnswers.entrySet()) {
            AnswerDetail detail = new AnswerDetail();
            detail.setAnswerId(answer.getAnswerId());
            detail.setQuestionId(entry.getKey());
            detail.setOptionId(entry.getValue());
            answerDetailRepository.save(detail);
        }

        // 计算结果
        return calculateTestResult(answer.getAnswerId());
    }

    /**
     * 计算测试结果
     */
    @Transactional(readOnly = true)
    public TestResult calculateTestResult(Long answerId) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new RuntimeException("答案记录不存在"));

        // 计算各维度分数
        Map<MbtiDimension, Integer> scores = new HashMap<>();
        for (MbtiDimension dimension : MbtiDimension.values()) {
            Integer score = answerDetailRepository.calculateScoreByDimension(answerId, dimension);
            scores.put(dimension, score != null ? score : 0);
        }

        // 计算MBTI类型
        String mbtiType = calculateMbtiType(scores);

        // 获取详细答案信息
        List<AnswerDetail> answerDetails = answerDetailRepository.findAnswerDetailsWithQuestionAndOption(answerId);

        return new TestResult(answer, scores, mbtiType, answerDetails);
    }

    /**
     * 获取用户的答题历史
     */
    @Transactional(readOnly = true)
    public List<TestResult> getUserTestHistory() {
        Long currentUserId = authService.getCurrentUserId();
        if (currentUserId == null) {
            throw new RuntimeException("用户未登录");
        }

        List<Answer> answers = answerRepository.findUserAnswerHistoryWithQuestionnaire(currentUserId);
        return answers.stream()
                .map(answer -> calculateTestResult(answer.getAnswerId()))
                .collect(Collectors.toList());
    }

    /**
     * 获取用户特定问卷的答题结果
     */
    @Transactional(readOnly = true)
    public Optional<TestResult> getUserQuestionnaireResult(Long questionnaireId) {
        Long currentUserId = authService.getCurrentUserId();
        if (currentUserId == null) {
            return Optional.empty();
        }

        Optional<Answer> answer = answerRepository.findByUserIdAndQuestionnaireId(currentUserId, questionnaireId);
        return answer.map(a -> calculateTestResult(a.getAnswerId()));
    }

    /**
     * 获取问卷的答题统计（仅管理员）
     */
    @Transactional(readOnly = true)
    public QuestionnaireStatistics getQuestionnaireStatistics(Long questionnaireId) {
        if (!authService.isCurrentUserAdmin()) {
            throw new RuntimeException("无权限查看统计信息");
        }

        Questionnaire questionnaire = questionnaireRepository.findById(questionnaireId)
                .orElseThrow(() -> new RuntimeException("问卷不存在"));

        long totalAnswers = answerRepository.countByQuestionnaireId(questionnaireId);
        
        // 获取各维度分数统计
        List<Object[]> dimensionStats = answerDetailRepository.getQuestionnaireStatistics(questionnaireId);
        Map<String, Map<String, Long>> statistics = new HashMap<>();
        
        for (Object[] stat : dimensionStats) {
            String dimension = ((MbtiDimension) stat[0]).getValue();
            Byte score = (Byte) stat[1];
            Long count = (Long) stat[2];
            
            statistics.computeIfAbsent(dimension, k -> new HashMap<>())
                     .put(score > 0 ? "positive" : "negative", count);
        }

        return new QuestionnaireStatistics(questionnaire, totalAnswers, statistics);
    }

    /**
     * 根据分数计算MBTI类型
     */
    private String calculateMbtiType(Map<MbtiDimension, Integer> scores) {
        StringBuilder mbtiType = new StringBuilder();
        
        // E/I
        mbtiType.append(scores.get(MbtiDimension.EI) >= 0 ? "E" : "I");
        // S/N
        mbtiType.append(scores.get(MbtiDimension.SN) >= 0 ? "N" : "S");
        // T/F
        mbtiType.append(scores.get(MbtiDimension.TF) >= 0 ? "F" : "T");
        // J/P
        mbtiType.append(scores.get(MbtiDimension.JP) >= 0 ? "P" : "J");
        
        return mbtiType.toString();
    }

    /**
     * 提交答案（DTO版本）
     */
    public Map<String, Object> submitAnswers(AnswerSubmitDTO answerSubmitDTO) {
        TestResult result = submitAnswer(answerSubmitDTO.getQuestionnaireId(), answerSubmitDTO.getQuestionAnswers());
        
        Map<String, Object> response = new HashMap<>();
        response.put("answerId", result.getAnswer().getAnswerId());
        response.put("mbtiType", result.getMbtiType());
        response.put("dimensionScores", result.getDimensionScores());
        response.put("submittedAt", result.getAnswer().getAnsweredAt());
        
        return response;
    }

    /**
     * 获取用户测试结果（分页）
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getUserTestResults(int page, int size) {
        Long currentUserId = authService.getCurrentUserId();
        if (currentUserId == null) {
            throw new RuntimeException("用户未登录");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Answer> answerPage = answerRepository.findByUserIdOrderByAnsweredAtDesc(currentUserId, pageable);
        
        List<Map<String, Object>> results = answerPage.getContent().stream()
                .map(answer -> {
                    TestResult testResult = calculateTestResult(answer.getAnswerId());
                    Map<String, Object> resultMap = new HashMap<>();
                    resultMap.put("answerId", answer.getAnswerId());
                    resultMap.put("questionnaireId", answer.getQuestionnaireId());
                    resultMap.put("mbtiType", testResult.getMbtiType());
                    resultMap.put("submittedAt", answer.getAnsweredAt());
                    
                    // 获取问卷标题
                    questionnaireRepository.findById(answer.getQuestionnaireId())
                            .ifPresent(q -> resultMap.put("questionnaireTitle", q.getTitle()));
                    
                    return resultMap;
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("results", results);
        response.put("totalElements", answerPage.getTotalElements());
        response.put("totalPages", answerPage.getTotalPages());
        response.put("currentPage", page);
        response.put("pageSize", size);
        
        return response;
    }

    /**
     * 获取指定问卷的测试结果
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getQuestionnaireResult(Long questionnaireId) {
        Optional<TestResult> resultOpt = getUserQuestionnaireResult(questionnaireId);
        if (resultOpt.isEmpty()) {
            throw new RuntimeException("未找到该问卷的测试结果");
        }
        
        TestResult result = resultOpt.get();
        Map<String, Object> response = new HashMap<>();
        response.put("answerId", result.getAnswer().getAnswerId());
        response.put("questionnaireId", questionnaireId);
        response.put("mbtiType", result.getMbtiType());
        response.put("dimensionScores", result.getDimensionScores());
        response.put("submittedAt", result.getAnswer().getAnsweredAt());
        
        // 获取问卷信息
        questionnaireRepository.findById(questionnaireId).ifPresent(q -> {
            response.put("questionnaireTitle", q.getTitle());
            response.put("questionnaireDescription", q.getDescription());
        });
        
        return response;
    }

    /**
     * 生成MBTI报告
     */
    @Transactional(readOnly = true)
    public Map<String, Object> generateMbtiReport(Long answerId) {
        TestResult result = calculateTestResult(answerId);
        
        Map<String, Object> report = new HashMap<>();
        report.put("mbtiType", result.getMbtiType());
        report.put("dimensionScores", result.getDimensionScores());
        report.put("description", getMbtiTypeDescription(result.getMbtiType()));
        report.put("strengths", getMbtiTypeStrengths(result.getMbtiType()));
        report.put("challenges", getMbtiTypeChallenges(result.getMbtiType()));
        report.put("careers", getMbtiTypeCareers(result.getMbtiType()));
        report.put("generatedAt", new Date());
        
        return report;
    }

    /**
     * 重新计算结果
     */
    public Map<String, Object> recalculateResult(Long answerId) {
        TestResult result = calculateTestResult(answerId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("answerId", answerId);
        response.put("mbtiType", result.getMbtiType());
        response.put("dimensionScores", result.getDimensionScores());
        response.put("recalculatedAt", new Date());
        
        return response;
    }

    /**
     * 获取测试统计
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getTestStatistics() {
        if (!authService.isCurrentUserAdmin()) {
            throw new RuntimeException("无权限查看统计信息");
        }
        
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalAnswers", answerRepository.count());
        statistics.put("totalQuestionnaires", questionnaireRepository.count());
        statistics.put("publishedQuestionnaires", questionnaireRepository.countByIsPublishedTrue());
        
        // MBTI类型分布统计
        Map<String, Long> mbtiDistribution = new HashMap<>();
        List<Answer> allAnswers = answerRepository.findAll();
        for (Answer answer : allAnswers) {
            TestResult result = calculateTestResult(answer.getAnswerId());
            String mbtiType = result.getMbtiType();
            mbtiDistribution.put(mbtiType, mbtiDistribution.getOrDefault(mbtiType, 0L) + 1);
        }
        statistics.put("mbtiDistribution", mbtiDistribution);
        
        return statistics;
    }

    /**
     * 获取问卷答题统计
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getQuestionnaireTestStatistics(Long questionnaireId) {
        QuestionnaireStatistics stats = getQuestionnaireStatistics(questionnaireId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("questionnaireId", questionnaireId);
        response.put("questionnaireTitle", stats.getQuestionnaire().getTitle());
        response.put("totalAnswers", stats.getTotalAnswers());
        response.put("dimensionStatistics", stats.getDimensionStatistics());
        
        return response;
    }

    /**
     * 删除测试记录
     */
    public void deleteTestRecord(Long answerId) {
        if (!authService.isCurrentUserAdmin()) {
            throw new RuntimeException("无权限删除测试记录");
        }
        
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new RuntimeException("测试记录不存在"));
        
        // 删除答案详情
        answerDetailRepository.deleteByAnswerId(answerId);
        // 删除答案记录
        answerRepository.delete(answer);
    }

    /**
     * 获取用户答题历史（分页）
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getUserTestHistory(Long userId, int page, int size) {
        if (!authService.isCurrentUserAdmin()) {
            throw new RuntimeException("无权限查看用户答题历史");
        }
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Answer> answerPage = answerRepository.findByUserIdOrderByAnsweredAtDesc(userId, pageable);
        
        List<Map<String, Object>> history = answerPage.getContent().stream()
                .map(answer -> {
                    TestResult testResult = calculateTestResult(answer.getAnswerId());
                    Map<String, Object> historyItem = new HashMap<>();
                    historyItem.put("answerId", answer.getAnswerId());
                    historyItem.put("questionnaireId", answer.getQuestionnaireId());
                    historyItem.put("mbtiType", testResult.getMbtiType());
                    historyItem.put("submittedAt", answer.getAnsweredAt());
                    
                    // 获取问卷标题
                    questionnaireRepository.findById(answer.getQuestionnaireId())
                            .ifPresent(q -> historyItem.put("questionnaireTitle", q.getTitle()));
                    
                    return historyItem;
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("history", history);
        response.put("totalElements", answerPage.getTotalElements());
        response.put("totalPages", answerPage.getTotalPages());
        response.put("currentPage", page);
        response.put("pageSize", size);
        
        return response;
    }

    /**
     * 获取MBTI类型描述
     */
    private String getMbtiTypeDescription(String mbtiType) {
        // 这里应该有完整的MBTI类型描述，简化处理
        Map<String, String> descriptions = Map.of(
            "ENFP", "热情、富有想象力和创造力的人，认为生活充满可能性。",
            "INFP", "理想主义的人，忠于自己的价值观和重要的人。",
            "ENFJ", "热情、负责的领导者，对他人的需求十分敏感。",
            "INFJ", "富有创意和洞察力，坚定而有原则。"
            // ... 其他类型
        );
        return descriptions.getOrDefault(mbtiType, "MBTI类型: " + mbtiType);
    }

    /**
     * 获取MBTI类型优势
     */
    private List<String> getMbtiTypeStrengths(String mbtiType) {
        // 简化处理，实际应该有完整的优势描述
        return Arrays.asList("创造力", "同理心", "适应性", "热情");
    }

    /**
     * 获取MBTI类型挑战
     */
    private List<String> getMbtiTypeChallenges(String mbtiType) {
        // 简化处理，实际应该有完整的挑战描述
        return Arrays.asList("过度理想化", "压力管理", "决策困难");
    }

    /**
     * 获取MBTI类型推荐职业
     */
    private List<String> getMbtiTypeCareers(String mbtiType) {
        // 简化处理，实际应该有完整的职业推荐
        return Arrays.asList("心理咨询师", "教师", "作家", "艺术家");
    }

    /**
     * 问卷测试信息内部类
     */
    public static class QuestionnaireTestInfo {
        private final Questionnaire questionnaire;
        private final List<Question> questions;

        public QuestionnaireTestInfo(Questionnaire questionnaire, List<Question> questions) {
            this.questionnaire = questionnaire;
            this.questions = questions;
        }

        public Questionnaire getQuestionnaire() { return questionnaire; }
        public List<Question> getQuestions() { return questions; }
    }

    /**
     * 测试结果内部类
     */
    public static class TestResult {
        private final Answer answer;
        private final Map<MbtiDimension, Integer> dimensionScores;
        private final String mbtiType;
        private final List<AnswerDetail> answerDetails;

        public TestResult(Answer answer, Map<MbtiDimension, Integer> dimensionScores, 
                         String mbtiType, List<AnswerDetail> answerDetails) {
            this.answer = answer;
            this.dimensionScores = dimensionScores;
            this.mbtiType = mbtiType;
            this.answerDetails = answerDetails;
        }

        public Answer getAnswer() { return answer; }
        public Map<MbtiDimension, Integer> getDimensionScores() { return dimensionScores; }
        public String getMbtiType() { return mbtiType; }
        public List<AnswerDetail> getAnswerDetails() { return answerDetails; }
    }

    /**
     * 问卷统计信息内部类
     */
    public static class QuestionnaireStatistics {
        private final Questionnaire questionnaire;
        private final long totalAnswers;
        private final Map<String, Map<String, Long>> dimensionStatistics;

        public QuestionnaireStatistics(Questionnaire questionnaire, long totalAnswers, 
                                     Map<String, Map<String, Long>> dimensionStatistics) {
            this.questionnaire = questionnaire;
            this.totalAnswers = totalAnswers;
            this.dimensionStatistics = dimensionStatistics;
        }

        public Questionnaire getQuestionnaire() { return questionnaire; }
        public long getTotalAnswers() { return totalAnswers; }
        public Map<String, Map<String, Long>> getDimensionStatistics() { return dimensionStatistics; }
    }
}
