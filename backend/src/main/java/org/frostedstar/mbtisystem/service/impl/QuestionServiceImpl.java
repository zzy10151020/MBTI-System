package org.frostedstar.mbtisystem.service.impl;

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
public class QuestionServiceImpl implements QuestionService {
    
    private final QuestionDAO questionDao;
    
    public QuestionServiceImpl() {
        this.questionDao = DaoFactory.getQuestionDao();
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
    public boolean deleteByQuestionnaireId(Integer questionnaireId) {
        return questionDao.deleteByQuestionnaireId(questionnaireId);
    }
}
