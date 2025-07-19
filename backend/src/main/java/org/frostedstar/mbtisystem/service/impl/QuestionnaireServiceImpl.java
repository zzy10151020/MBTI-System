package org.frostedstar.mbtisystem.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.frostedstar.mbtisystem.dao.OptionDAO;
import org.frostedstar.mbtisystem.dao.QuestionDAO;
import org.frostedstar.mbtisystem.dao.QuestionnaireDAO;
import org.frostedstar.mbtisystem.dao.impl.OptionDAOImpl;
import org.frostedstar.mbtisystem.dao.impl.QuestionDAOImpl;
import org.frostedstar.mbtisystem.dao.impl.QuestionnaireDAOImpl;
import org.frostedstar.mbtisystem.entity.Option;
import org.frostedstar.mbtisystem.entity.Question;
import org.frostedstar.mbtisystem.entity.Questionnaire;
import org.frostedstar.mbtisystem.service.QuestionnaireService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 问卷 Service 实现
 */
@Slf4j
public class QuestionnaireServiceImpl implements QuestionnaireService {
    
    private final QuestionnaireDAO questionnaireDAO;
    private final QuestionDAO questionDAO;
    private final OptionDAO optionDAO;
    
    public QuestionnaireServiceImpl() {
        this.questionnaireDAO = new QuestionnaireDAOImpl();
        this.questionDAO = new QuestionDAOImpl();
        this.optionDAO = new OptionDAOImpl();
    }
    
    @Override
    public Questionnaire createQuestionnaire(Questionnaire questionnaire) {
        // 设置创建时间
        questionnaire.setCreatedAt(LocalDateTime.now());
        questionnaire.setIsPublished(false);
        
        // 保存问卷
        Questionnaire savedQuestionnaire = questionnaireDAO.save(questionnaire);
        
        // 保存问题和选项
        if (questionnaire.getQuestions() != null) {
            for (Question question : questionnaire.getQuestions()) {
                question.setQuestionnaireId(savedQuestionnaire.getQuestionnaireId());
                Question savedQuestion = questionDAO.save(question);
                
                // 保存选项
                if (question.getOptions() != null) {
                    for (Option option : question.getOptions()) {
                        option.setQuestionId(savedQuestion.getQuestionId());
                        optionDAO.save(option);
                    }
                }
            }
        }
        
        log.info("问卷创建成功: {}", savedQuestionnaire.getTitle());
        return savedQuestionnaire;
    }
    
    @Override
    public List<Questionnaire> findByCreatorId(Integer creatorId) {
        return questionnaireDAO.findByCreatorId(creatorId);
    }
    
    @Override
    public List<Questionnaire> findPublished() {
        return questionnaireDAO.findPublished();
    }
    
    @Override
    public List<Questionnaire> findByTitleLike(String title) {
        return questionnaireDAO.findByTitleLike(title);
    }
    
    @Override
    public boolean publishQuestionnaire(Integer questionnaireId) {
        Optional<Questionnaire> questionnaireOptional = questionnaireDAO.findById(questionnaireId);
        
        if (questionnaireOptional.isPresent()) {
            Questionnaire questionnaire = questionnaireOptional.get();
            questionnaire.setIsPublished(true);
            boolean updated = questionnaireDAO.update(questionnaire);
            
            if (updated) {
                log.info("问卷发布成功: {}", questionnaire.getTitle());
            }
            return updated;
        }
        
        return false;
    }
    
    @Override
    public boolean unpublishQuestionnaire(Integer questionnaireId) {
        Optional<Questionnaire> questionnaireOptional = questionnaireDAO.findById(questionnaireId);
        
        if (questionnaireOptional.isPresent()) {
            Questionnaire questionnaire = questionnaireOptional.get();
            questionnaire.setIsPublished(false);
            boolean updated = questionnaireDAO.update(questionnaire);
            
            if (updated) {
                log.info("问卷取消发布成功: {}", questionnaire.getTitle());
            }
            return updated;
        }
        
        return false;
    }
    
    @Override
    public Optional<Questionnaire> getQuestionnaireDetail(Integer questionnaireId) {
        Optional<Questionnaire> questionnaireOptional = questionnaireDAO.findById(questionnaireId);
        
        if (questionnaireOptional.isPresent()) {
            Questionnaire questionnaire = questionnaireOptional.get();
            
            // 加载问题
            List<Question> questions = questionDAO.findByQuestionnaireIdOrderByQuestionOrder(questionnaireId);
            
            // 为每个问题加载选项
            for (Question question : questions) {
                List<Option> options = optionDAO.findByQuestionId(question.getQuestionId());
                question.setOptions(options);
            }
            
            questionnaire.setQuestions(questions);
            return Optional.of(questionnaire);
        }
        
        return Optional.empty();
    }
    
    @Override
    public boolean deleteQuestionnaireWithCascade(Integer questionnaireId) {
        try {
            // 获取问卷的所有问题
            List<Question> questions = questionDAO.findByQuestionnaireId(questionnaireId);
            log.info("准备删除问卷 {} 及其 {} 个问题", questionnaireId, questions.size());
            
            // 删除每个问题的选项
            for (Question question : questions) {
                boolean optionDeleted = optionDAO.deleteByQuestionId(question.getQuestionId());
                if (!optionDeleted) {
                    log.error("删除问题 {} 的选项失败", question.getQuestionId());
                    return false;
                }
            }
            
            // 删除问卷的所有问题
            boolean questionsDeleted = questionDAO.deleteByQuestionnaireId(questionnaireId);
            if (!questionsDeleted) {
                log.error("删除问卷 {} 的问题失败", questionnaireId);
                return false;
            }
            
            // 删除问卷
            boolean questionnaireDeleted = questionnaireDAO.deleteById(questionnaireId);
            
            if (questionnaireDeleted) {
                log.info("问卷级联删除成功: {}", questionnaireId);
            } else {
                log.error("问卷删除失败: {}", questionnaireId);
            }
            return questionnaireDeleted;
            
        } catch (Exception e) {
            log.error("问卷级联删除失败: {}", questionnaireId, e);
            return false;
        }
    }
    
    @Override
    public Questionnaire save(Questionnaire questionnaire) {
        return questionnaireDAO.save(questionnaire);
    }
    
    @Override
    public Optional<Questionnaire> findById(Integer id) {
        return questionnaireDAO.findById(id);
    }
    
    @Override
    public List<Questionnaire> findAll() {
        return questionnaireDAO.findAll();
    }
    
    @Override
    public boolean update(Questionnaire questionnaire) {
        return questionnaireDAO.update(questionnaire);
    }
    
    @Override
    public boolean deleteById(Integer id) {
        return questionnaireDAO.deleteById(id);
    }
    
    @Override
    public long count() {
        return questionnaireDAO.count();
    }
}
