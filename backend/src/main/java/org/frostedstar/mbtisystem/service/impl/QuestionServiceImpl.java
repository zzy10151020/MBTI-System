package org.frostedstar.mbtisystem.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.frostedstar.mbtisystem.dao.OptionDAO;
import org.frostedstar.mbtisystem.dao.QuestionDAO;
import org.frostedstar.mbtisystem.dao.DaoFactory;
import org.frostedstar.mbtisystem.entity.Question;
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
    
    public QuestionServiceImpl() {
        this.questionDao = DaoFactory.getQuestionDao();
        this.optionDao = DaoFactory.getOptionDao();
    }
    
    @Override
    public Question save(Question question) {
        return questionDao.save(question);
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
        return questionDao.update(question);
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
        // 批量保存问题
        List<Question> savedQuestions = new ArrayList<>();
        for (Question question : questions) {
            savedQuestions.add(questionDao.save(question));
        }
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
        // 级联删除问题和选项
        try {
            // 删除所有选项
            boolean deleted;
            if (optionDao.deleteByQuestionId(questionId)) {
                // 删除问题
                deleted = questionDao.deleteById(questionId);
                if (deleted) {
                    log.info("选项和问题删除成功: 问题ID {}", questionId);
                } else {
                    log.warn("选项删除成功，但问题删除失败: 问题ID {}", questionId);
                }
                return deleted;
            } else {
                // 删除问题失败
                log.warn("选项删除失败: 问题ID {}", questionId);
                return false;
            }

        } catch (Exception e) {
            log.error("级联删除问题下的所有选项失败", e);
            throw new RuntimeException("级联删除问题下的所有选项失败", e);
        }
    }
}
