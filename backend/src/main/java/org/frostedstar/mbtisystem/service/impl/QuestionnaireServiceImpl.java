package org.frostedstar.mbtisystem.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.frostedstar.mbtisystem.dao.AnswerDAO;
import org.frostedstar.mbtisystem.dao.AnswerDetailDAO;
import org.frostedstar.mbtisystem.dao.OptionDAO;
import org.frostedstar.mbtisystem.dao.QuestionDAO;
import org.frostedstar.mbtisystem.dao.QuestionnaireDAO;
import org.frostedstar.mbtisystem.dao.impl.AnswerDAOImpl;
import org.frostedstar.mbtisystem.dao.impl.AnswerDetailDAOImpl;
import org.frostedstar.mbtisystem.dao.impl.OptionDAOImpl;
import org.frostedstar.mbtisystem.dao.impl.QuestionDAOImpl;
import org.frostedstar.mbtisystem.dao.impl.QuestionnaireDAOImpl;
import org.frostedstar.mbtisystem.entity.Answer;
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
    private final AnswerDAO answerDAO;
    private final AnswerDetailDAO answerDetailDAO;
    
    public QuestionnaireServiceImpl() {
        this.questionnaireDAO = new QuestionnaireDAOImpl();
        this.questionDAO = new QuestionDAOImpl();
        this.optionDAO = new OptionDAOImpl();
        this.answerDAO = new AnswerDAOImpl();
        this.answerDetailDAO = new AnswerDetailDAOImpl();
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
            
            // 如果问卷已经发布，需要先清理所有的答案数据
            if (questionnaire.getIsPublished()) {
                log.info("问卷正在取消发布，清理答案数据: {}", questionnaire.getTitle());
                
                // 获取所有与该问卷相关的答案
                List<Answer> answers = answerDAO.findByQuestionnaireId(questionnaireId);
                
                // 删除所有答案详情
                for (Answer answer : answers) {
                    answerDetailDAO.deleteByAnswerId(answer.getAnswerId());
                    log.debug("删除答案详情: answerId={}", answer.getAnswerId());
                }
                
                // 批量删除所有答案
                answerDAO.deleteByQuestionnaireId(questionnaireId);
                log.info("已清理问卷答案数据: questionnaireId={}, 删除了{}个答案", questionnaireId, answers.size());
            }
            
            // 取消发布状态
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
            // 1. 首先删除与该问卷相关的所有答案详情和答案
            // 获取所有与该问卷相关的答案
            List<Answer> answers = answerDAO.findByQuestionnaireId(questionnaireId);
            for (Answer answer : answers) {
                // 删除每个答案的详情记录
                answerDetailDAO.deleteByAnswerId(answer.getAnswerId());
                log.debug("删除答案详情: answerId={}", answer.getAnswerId());
            }
            
            // 使用新的批量删除方法删除所有答案
            answerDAO.deleteByQuestionnaireId(questionnaireId);
            log.debug("批量删除问卷答案: questionnaireId={}", questionnaireId);
            
            // 2. 获取问卷的所有问题
            List<Question> questions = questionDAO.findByQuestionnaireId(questionnaireId);
            
            // 3. 删除每个问题的选项
            for (Question question : questions) {
                optionDAO.deleteByQuestionId(question.getQuestionId());
                log.debug("删除问题选项: questionId={}", question.getQuestionId());
            }
            
            // 4. 删除问卷的所有问题
            questionDAO.deleteByQuestionnaireId(questionnaireId);
            log.debug("删除问卷问题: questionnaireId={}", questionnaireId);
            
            // 5. 最后删除问卷本身
            boolean deleted = questionnaireDAO.deleteById(questionnaireId);
            
            if (deleted) {
                log.info("问卷级联删除成功: questionnaireId={}, 删除了{}个答案, {}个问题", 
                    questionnaireId, answers.size(), questions.size());
            } else {
                log.error("问卷删除失败: questionnaireId={}", questionnaireId);
            }
            return deleted;
            
        } catch (Exception e) {
            log.error("问卷级联删除失败: questionnaireId={}", questionnaireId, e);
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
        try {
            // 检查问卷是否存在
            Optional<Questionnaire> existingQuestionnaireOpt = questionnaireDAO.findById(questionnaire.getQuestionnaireId());
            if (existingQuestionnaireOpt.isEmpty()) {
                log.warn("问卷不存在: questionnaireId={}", questionnaire.getQuestionnaireId());
                return false;
            }
            
            Questionnaire existingQuestionnaire = existingQuestionnaireOpt.get();
            
            // 检查问卷是否已发布
            if (existingQuestionnaire.getIsPublished()) {
                log.warn("无法更新已发布的问卷: questionnaireId={}, 问卷标题='{}'", 
                    questionnaire.getQuestionnaireId(), existingQuestionnaire.getTitle());
                return false;
            }
            
            // 只有未发布的问卷才能更新
            // 保持原有的发布状态和创建时间
            questionnaire.setIsPublished(existingQuestionnaire.getIsPublished());
            questionnaire.setCreatedAt(existingQuestionnaire.getCreatedAt());
            questionnaire.setCreatorId(existingQuestionnaire.getCreatorId());
            
            boolean updated = questionnaireDAO.update(questionnaire);
            if (updated) {
                log.info("问卷更新成功: questionnaireId={}, 标题='{}'", 
                    questionnaire.getQuestionnaireId(), questionnaire.getTitle());
            } else {
                log.warn("问卷更新失败: questionnaireId={}", questionnaire.getQuestionnaireId());
            }
            return updated;
            
        } catch (Exception e) {
            log.error("更新问卷时发生异常: questionnaireId={}", questionnaire.getQuestionnaireId(), e);
            return false;
        }
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
