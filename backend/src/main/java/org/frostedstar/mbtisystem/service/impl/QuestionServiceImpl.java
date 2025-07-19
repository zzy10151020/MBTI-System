package org.frostedstar.mbtisystem.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.frostedstar.mbtisystem.dao.OptionDAO;
import org.frostedstar.mbtisystem.dao.QuestionDAO;
import org.frostedstar.mbtisystem.dao.QuestionnaireDAO;
import org.frostedstar.mbtisystem.dao.DaoFactory;
import org.frostedstar.mbtisystem.entity.Question;
import org.frostedstar.mbtisystem.entity.Questionnaire;
import org.frostedstar.mbtisystem.entity.Option;
import org.frostedstar.mbtisystem.service.QuestionService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 问题 Service 实现类
 */
@Slf4j
public class QuestionServiceImpl implements QuestionService {
    
    private final QuestionDAO questionDao;
    private final OptionDAO optionDao;
    private final QuestionnaireDAO questionnaireDao;
    
    public QuestionServiceImpl() {
        this.questionDao = DaoFactory.getQuestionDao();
        this.optionDao = DaoFactory.getOptionDao();
        this.questionnaireDao = DaoFactory.getQuestionnaireDao();
    }
    
    @Override
    public Question save(Question question) {
        try {
            // 验证问题必须有选项
            if (question.getOptions() == null || question.getOptions().isEmpty()) {
                log.error("保存问题失败: 问题必须包含选项");
                throw new IllegalArgumentException("问题必须包含选项");
            }
            
            // 检查问题所属问卷的发布状态（如果有问卷ID）
            if (question.getQuestionnaireId() != null) {
                Optional<Questionnaire> questionnaireOpt = questionnaireDao.findById(question.getQuestionnaireId());
                if (questionnaireOpt.isPresent()) {
                    Questionnaire questionnaire = questionnaireOpt.get();
                    if (questionnaire.getIsPublished()) {
                        log.warn("无法在已发布问卷中添加问题: questionnaireId={}, 问卷标题='{}'", 
                            question.getQuestionnaireId(), questionnaire.getTitle());
                        return null;
                    }
                }
            }
            
            // 先保存问题
            Question savedQuestion = questionDao.save(question);
            
            // 为选项设置问题ID并批量保存选项
            for (Option option : question.getOptions()) {
                option.setQuestionId(savedQuestion.getQuestionId());
            }
            
            List<Option> savedOptions = optionDao.saveBatch(question.getOptions());
            savedQuestion.setOptions(savedOptions);
            
            log.info("问题和选项保存成功: 问题ID {}, 选项数量 {}", 
                savedQuestion.getQuestionId(), savedOptions.size());
            
            return savedQuestion;
            
        } catch (Exception e) {
            log.error("保存问题失败", e);
            return null;
        }
    }
    
    @Override
    public Optional<Question> findById(Integer id) {
        return questionDao.findById(id);
    }
    
    @Override
    public List<Question> findAll() {
        return questionDao.findAll();
    }
    
    @Override
    public boolean update(Question question) {
        try {
            // 验证问题必须有选项
            if (question.getOptions() == null || question.getOptions().isEmpty()) {
                log.error("更新问题失败: 问题必须包含选项");
                return false;
            }
            
            // 检查问题是否存在
            Optional<Question> existingQuestionOpt = questionDao.findById(question.getQuestionId());
            if (existingQuestionOpt.isEmpty()) {
                log.warn("问题不存在: questionId={}", question.getQuestionId());
                return false;
            }
            
            Question existingQuestion = existingQuestionOpt.get();
            Integer questionnaireId = existingQuestion.getQuestionnaireId();
            
            // 检查问题所属问卷的发布状态
            Optional<Questionnaire> questionnaireOpt = questionnaireDao.findById(questionnaireId);
            if (questionnaireOpt.isEmpty()) {
                log.warn("问题所属问卷不存在: questionnaireId={}", questionnaireId);
                return false;
            }
            
            Questionnaire questionnaire = questionnaireOpt.get();
            if (questionnaire.getIsPublished()) {
                log.warn("无法更新已发布问卷中的问题: questionId={}, questionnaireId={}, 问卷标题='{}'", 
                    question.getQuestionId(), questionnaireId, questionnaire.getTitle());
                return false;
            }
            
            // 只有未发布的问卷才能更新问题
            // 保持原有的问题ID和问卷ID
            question.setQuestionId(existingQuestion.getQuestionId());
            question.setQuestionnaireId(existingQuestion.getQuestionnaireId());
            
            // 更新问题
            boolean questionUpdated = questionDao.update(question);
            if (!questionUpdated) {
                log.warn("问题更新失败: questionId={}", question.getQuestionId());
                return false;
            }
            
            // 删除原有的选项
            boolean oldOptionsDeleted = optionDao.deleteByQuestionId(question.getQuestionId());
            if (!oldOptionsDeleted) {
                log.warn("删除原有选项失败: questionId={}", question.getQuestionId());
                return false;
            }
            
            // 为新选项设置问题ID并保存
            for (Option option : question.getOptions()) {
                option.setQuestionId(question.getQuestionId());
                option.setOptionId(null); // 确保创建新的选项ID
            }
            
            List<Option> savedOptions = optionDao.saveBatch(question.getOptions());
            if (savedOptions.isEmpty()) {
                log.warn("保存新选项失败: questionId={}", question.getQuestionId());
                return false;
            }
            
            log.info("问题和选项更新成功: questionId={}, questionnaireId={}, 内容='{}', 选项数量={}", 
                question.getQuestionId(), questionnaireId, question.getContent(), savedOptions.size());
            
            return true;
            
        } catch (Exception e) {
            log.error("更新问题时发生异常: questionId={}", question.getQuestionId(), e);
            return false;
        }
    }
    
    @Override
    public boolean deleteById(Integer id) {
        return questionDao.deleteById(id);
    }
    
    @Override
    public long count() {
        return questionDao.count();
    }
    
    @Override
    public List<Question> findByQuestionnaireId(Integer questionnaireId) {
        return questionDao.findByQuestionnaireId(questionnaireId);
    }
    
    @Override
    public List<Question> findByDimension(Question.Dimension dimension) {
        return questionDao.findByDimension(dimension);
    }
    
    @Override
    public List<Question> createQuestions(List<Question> questions) {
        // 批量保存问题（使用save方法，包含选项验证和保存）
        List<Question> savedQuestions = new ArrayList<>();
        for (Question question : questions) {
            Question savedQuestion = this.save(question);
            if (savedQuestion != null) {
                savedQuestions.add(savedQuestion);
            } else {
                log.error("批量保存中的问题保存失败，跳过该问题: {}", 
                    question.getContent() != null ? question.getContent().substring(0, Math.min(50, question.getContent().length())) : "无内容");
            }
        }
        
        log.info("批量保存问题完成: 尝试保存{}个问题, 成功保存{}个问题", questions.size(), savedQuestions.size());
        return savedQuestions;
    }
    
    @Override
    public Question getQuestionDetail(Integer questionId) {
        // 获取问题详情，这里可以扩展为包含选项
        Optional<Question> question = questionDao.findById(questionId);
        return question.orElse(null);
    }

    @Override
    public boolean deleteQuestionWithCascade(Integer questionId) {
        try {
            // 首先获取问题信息
            Optional<Question> questionOpt = questionDao.findById(questionId);
            if (questionOpt.isEmpty()) {
                log.warn("问题不存在: questionId={}", questionId);
                return false;
            }
            
            Question question = questionOpt.get();
            Integer questionnaireId = question.getQuestionnaireId();
            
            // 检查问题所属问卷的发布状态
            Optional<Questionnaire> questionnaireOpt = questionnaireDao.findById(questionnaireId);
            if (questionnaireOpt.isEmpty()) {
                log.warn("问题所属问卷不存在: questionnaireId={}", questionnaireId);
                return false;
            }
            
            Questionnaire questionnaire = questionnaireOpt.get();
            if (questionnaire.getIsPublished()) {
                log.warn("无法删除已发布问卷中的问题: questionId={}, questionnaireId={}, 问卷标题='{}'", 
                    questionId, questionnaireId, questionnaire.getTitle());
                return false;
            }
            
            // 只有未发布的问卷才能删除问题
            // 删除所有选项
            boolean optionsDeleted = optionDao.deleteByQuestionId(questionId);
            if (optionsDeleted) {
                // 删除问题
                boolean questionDeleted = questionDao.deleteById(questionId);
                if (questionDeleted) {
                    log.info("问题和选项删除成功: questionId={}, questionnaireId={}", questionId, questionnaireId);
                } else {
                    log.warn("选项删除成功，但问题删除失败: questionId={}", questionId);
                }
                return questionDeleted;
            } else {
                log.warn("选项删除失败: questionId={}", questionId);
                return false;
            }

        } catch (Exception e) {
            log.error("级联删除问题失败: questionId={}", questionId, e);
            return false;
        }
    }

    @Override
    public long countByQuestionnaireId(Integer questionnaireId) {
        return questionDao.countByQuestionnaireId(questionnaireId);
    }
}
