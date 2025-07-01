package org.frostedstar.mbtisystem.service;

import org.frostedstar.mbtisystem.dto.QuestionnaireDTO;
import org.frostedstar.mbtisystem.model.*;
import org.frostedstar.mbtisystem.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 问卷服务层
 */
@Service
@Transactional
public class QuestionnaireService {

    private final QuestionnaireRepository questionnaireRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;
    private final AuthService authService;

    public QuestionnaireService(QuestionnaireRepository questionnaireRepository,
                               QuestionRepository questionRepository,
                               AnswerRepository answerRepository,
                               UserRepository userRepository,
                               AuthService authService) {
        this.questionnaireRepository = questionnaireRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.userRepository = userRepository;
        this.authService = authService;
    }

    /**
     * 创建问卷（仅管理员）
     */
    @PreAuthorize("hasRole('ADMIN')")
    public Questionnaire createQuestionnaire(String title, String description) {
        Long currentUserId = authService.getCurrentUserId();
        if (currentUserId == null) {
            throw new RuntimeException("用户未登录");
        }

        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setTitle(title);
        questionnaire.setDescription(description);
        questionnaire.setCreatorId(currentUserId);
        questionnaire.setIsPublished(false); // 默认未发布

        return questionnaireRepository.save(questionnaire);
    }

    /**
     * 更新问卷信息（仅管理员）
     */
    @PreAuthorize("hasRole('ADMIN')")
    public Questionnaire updateQuestionnaire(Long questionnaireId, String title, String description, Boolean isPublished) {
        Questionnaire questionnaire = questionnaireRepository.findById(questionnaireId)
                .orElseThrow(() -> new RuntimeException("问卷不存在"));

        if (title != null) {
            questionnaire.setTitle(title);
        }
        if (description != null) {
            questionnaire.setDescription(description);
        }
        if (isPublished != null) {
            questionnaire.setIsPublished(isPublished);
        }

        return questionnaireRepository.save(questionnaire);
    }

    /**
     * 发布问卷（仅管理员）
     */
    @PreAuthorize("hasRole('ADMIN')")
    public Questionnaire publishQuestionnaire(Long questionnaireId) {
        Questionnaire questionnaire = questionnaireRepository.findById(questionnaireId)
                .orElseThrow(() -> new RuntimeException("问卷不存在"));

        // 检查问卷是否有问题
        long questionCount = questionRepository.countByQuestionnaireId(questionnaireId);
        if (questionCount == 0) {
            throw new RuntimeException("问卷必须包含至少一个问题才能发布");
        }

        questionnaire.setIsPublished(true);
        return questionnaireRepository.save(questionnaire);
    }

    /**
     * 删除问卷（仅管理员）
     */
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteQuestionnaire(Long questionnaireId) {
        Questionnaire questionnaire = questionnaireRepository.findById(questionnaireId)
                .orElseThrow(() -> new RuntimeException("问卷不存在"));

        // 检查是否有用户已经回答过这个问卷
        long answerCount = answerRepository.countByQuestionnaireId(questionnaireId);
        if (answerCount > 0) {
            throw new RuntimeException("已有用户回答过的问卷不能删除");
        }

        questionnaireRepository.delete(questionnaire);
    }

    /**
     * 获取所有已发布的问卷（普通用户可见）
     */
    @Transactional(readOnly = true)
    public List<Questionnaire> getPublishedQuestionnaires() {
        return questionnaireRepository.findByIsPublishedTrue();
    }

    /**
     * 获取所有问卷（仅管理员）
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public List<Questionnaire> getAllQuestionnaires() {
        return questionnaireRepository.findAll();
    }

    /**
     * 根据ID获取问卷详情
     */
    @Transactional(readOnly = true)
    public Optional<Questionnaire> getQuestionnaireById(Long questionnaireId) {
        Optional<Questionnaire> questionnaire = questionnaireRepository.findById(questionnaireId);
        
        // 普通用户只能查看已发布的问卷
        if (questionnaire.isPresent() && !authService.isCurrentUserAdmin()) {
            if (!questionnaire.get().getIsPublished()) {
                return Optional.empty();
            }
        }
        
        return questionnaire;
    }

    /**
     * 获取问卷的完整信息（包含问题和选项）
     */
    @Transactional(readOnly = true)
    public Optional<Questionnaire> getQuestionnaireWithDetails(Long questionnaireId) {
        Optional<Questionnaire> questionnaireOpt = getQuestionnaireById(questionnaireId);
        
        if (questionnaireOpt.isPresent()) {
            Questionnaire questionnaire = questionnaireOpt.get();
            
            // 获取问题和选项
            List<Question> questions = questionRepository.findQuestionsWithOptionsByQuestionnaireId(questionnaireId);
            questionnaire.setQuestions(questions);
            
            return Optional.of(questionnaire);
        }
        
        return Optional.empty();
    }

    /**
     * 搜索问卷
     */
    @Transactional(readOnly = true)
    public List<Questionnaire> searchQuestionnaires(String keyword) {
        if (authService.isCurrentUserAdmin()) {
            return questionnaireRepository.findByTitleContaining(keyword);
        } else {
            return questionnaireRepository.findByTitleContainingAndIsPublishedTrue(keyword);
        }
    }

    /**
     * 获取最受欢迎的问卷
     */
    @Transactional(readOnly = true)
    public List<Questionnaire> getPopularQuestionnaires() {
        return questionnaireRepository.findPopularQuestionnaires();
    }

    /**
     * 获取最新发布的问卷
     */
    @Transactional(readOnly = true)
    public List<Questionnaire> getLatestQuestionnaires(int limit) {
        return questionnaireRepository.findLatestPublishedQuestionnaires(limit);
    }

    /**
     * 获取创建者的问卷（仅管理员）
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public List<Questionnaire> getQuestionnairesByCreator(Long creatorId) {
        return questionnaireRepository.findByCreatorId(creatorId);
    }

    /**
     * 检查用户是否已回答过问卷
     */
    @Transactional(readOnly = true)
    public boolean hasUserAnsweredQuestionnaire(Long questionnaireId) {
        Long currentUserId = authService.getCurrentUserId();
        if (currentUserId == null) {
            return false;
        }
        return questionnaireRepository.hasUserAnsweredQuestionnaire(currentUserId, questionnaireId);
    }

    /**
     * 获取问卷统计信息（仅管理员）
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public QuestionnaireStats getQuestionnaireStats(Long questionnaireId) {
        Questionnaire questionnaire = questionnaireRepository.findById(questionnaireId)
                .orElseThrow(() -> new RuntimeException("问卷不存在"));

        long questionCount = questionRepository.countByQuestionnaireId(questionnaireId);
        long answerCount = answerRepository.countByQuestionnaireId(questionnaireId);

        return new QuestionnaireStats(
                questionnaire,
                questionCount,
                answerCount,
                questionnaire.getCreatedAt()
        );
    }

    /**
     * 获取所有问卷（分页）
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public Page<QuestionnaireDTO> getAllQuestionnaires(Pageable pageable) {
        Page<Questionnaire> questionnairePage = questionnaireRepository.findAll(pageable);
        List<QuestionnaireDTO> questionnaireDTOs = questionnairePage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(questionnaireDTOs, pageable, questionnairePage.getTotalElements());
    }

    /**
     * 获取活跃问卷列表
     */
    @Transactional(readOnly = true)
    public List<QuestionnaireDTO> getActiveQuestionnaires() {
        List<Questionnaire> questionnaires = questionnaireRepository.findByIsPublishedTrue();
        return questionnaires.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 根据ID获取问卷详情（DTO）
     */
    @Transactional(readOnly = true)
    public QuestionnaireDTO getQuestionnaireDTOById(Long questionnaireId) {
        Optional<Questionnaire> questionnaireOpt = getQuestionnaireById(questionnaireId);
        if (questionnaireOpt.isEmpty()) {
            throw new RuntimeException("问卷不存在或无权访问");
        }
        return convertToDTO(questionnaireOpt.get());
    }

    /**
     * 获取问卷的完整信息（包含问题）
     */
    @Transactional(readOnly = true)
    public QuestionnaireDTO getQuestionnaireWithQuestions(Long questionnaireId) {
        Optional<Questionnaire> questionnaireOpt = getQuestionnaireWithDetails(questionnaireId);
        if (questionnaireOpt.isEmpty()) {
            throw new RuntimeException("问卷不存在或无权访问");
        }
        return convertToDTOWithQuestions(questionnaireOpt.get());
    }

    /**
     * 创建问卷（DTO）
     */
    @PreAuthorize("hasRole('ADMIN')")
    public QuestionnaireDTO createQuestionnaire(QuestionnaireDTO questionnaireDTO) {
        Questionnaire questionnaire = createQuestionnaire(questionnaireDTO.getTitle(), questionnaireDTO.getDescription());
        return convertToDTO(questionnaire);
    }

    /**
     * 更新问卷（DTO）
     */
    @PreAuthorize("hasRole('ADMIN')")
    public QuestionnaireDTO updateQuestionnaire(Long id, QuestionnaireDTO questionnaireDTO) {
        Questionnaire questionnaire = updateQuestionnaire(id, questionnaireDTO.getTitle(), 
                questionnaireDTO.getDescription(), questionnaireDTO.getIsPublished());
        return convertToDTO(questionnaire);
    }

    /**
     * 更新问卷状态
     */
    @PreAuthorize("hasRole('ADMIN')")
    public QuestionnaireDTO updateQuestionnaireStatus(Long id, boolean active) {
        Questionnaire questionnaire = updateQuestionnaire(id, null, null, active);
        return convertToDTO(questionnaire);
    }

    /**
     * 获取问卷统计信息
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public Map<String, Object> getQuestionnaireStatistics(Long id) {
        QuestionnaireStats stats = getQuestionnaireStats(id);
        return Map.of(
            "questionnaireId", id,
            "title", stats.getQuestionnaire().getTitle(),
            "questionCount", stats.getQuestionCount(),
            "answerCount", stats.getAnswerCount(),
            "createdAt", stats.getCreatedAt()
        );
    }

    /**
     * 搜索问卷（分页）
     */
    @Transactional(readOnly = true)
    public Page<QuestionnaireDTO> searchQuestionnaires(String keyword, Pageable pageable) {
        List<Questionnaire> questionnaires = searchQuestionnaires(keyword);
        
        // 手动分页
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), questionnaires.size());
        List<Questionnaire> pageContent = questionnaires.subList(start, end);
        
        List<QuestionnaireDTO> questionnaireDTOs = pageContent.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return new PageImpl<>(questionnaireDTOs, pageable, questionnaires.size());
    }

    /**
     * 转换Questionnaire实体为QuestionnaireDTO
     */
    private QuestionnaireDTO convertToDTO(Questionnaire questionnaire) {
        QuestionnaireDTO dto = new QuestionnaireDTO();
        dto.setQuestionnaireId(questionnaire.getQuestionnaireId());
        dto.setTitle(questionnaire.getTitle());
        dto.setDescription(questionnaire.getDescription());
        dto.setCreatorId(questionnaire.getCreatorId());
        dto.setCreatedAt(questionnaire.getCreatedAt());
        dto.setIsPublished(questionnaire.getIsPublished());
        
        // 设置创建者用户名
        userRepository.findById(questionnaire.getCreatorId())
                .ifPresent(user -> dto.setCreatorUsername(user.getUsername()));
        
        // 设置回答数量
        long answerCount = answerRepository.countByQuestionnaireId(questionnaire.getQuestionnaireId());
        dto.setAnswerCount(answerCount);
        
        // 设置用户是否已回答
        dto.setHasAnswered(hasUserAnsweredQuestionnaire(questionnaire.getQuestionnaireId()));
        
        return dto;
    }

    /**
     * 转换Questionnaire实体为包含问题的QuestionnaireDTO
     */
    private QuestionnaireDTO convertToDTOWithQuestions(Questionnaire questionnaire) {
        QuestionnaireDTO dto = convertToDTO(questionnaire);
        
        // 这里需要添加问题和选项的转换逻辑
        // 暂时留空，待后续实现
        
        return dto;
    }

    /**
     * 问卷统计信息内部类
     */
    public static class QuestionnaireStats {
        private final Questionnaire questionnaire;
        private final long questionCount;
        private final long answerCount;
        private final LocalDateTime createdAt;

        public QuestionnaireStats(Questionnaire questionnaire, long questionCount, long answerCount, LocalDateTime createdAt) {
            this.questionnaire = questionnaire;
            this.questionCount = questionCount;
            this.answerCount = answerCount;
            this.createdAt = createdAt;
        }

        // Getters
        public Questionnaire getQuestionnaire() { return questionnaire; }
        public long getQuestionCount() { return questionCount; }
        public long getAnswerCount() { return answerCount; }
        public LocalDateTime getCreatedAt() { return createdAt; }
    }
}
