package org.frostedstar.mbtisystem.dao;

import org.frostedstar.mbtisystem.dao.impl.*;

/**
 * DAO 工厂类
 */
public class DaoFactory {
    
    private static final UserDAO userDao = new UserDAOImpl();
    private static final QuestionnaireDAO questionnaireDao = new QuestionnaireDAOImpl();
    private static final QuestionDAO questionDao = new QuestionDAOImpl();
    private static final OptionDAO optionDao = new OptionDAOImpl();
    private static final AnswerDAO answerDao = new AnswerDAOImpl();
    private static final AnswerDetailDAO answerDetailDao = new AnswerDetailDAOImpl();
    
    /**
     * 获取用户 DAO
     */
    public static UserDAO getUserDao() {
        return userDao;
    }
    
    /**
     * 获取问卷 DAO
     */
    public static QuestionnaireDAO getQuestionnaireDao() {
        return questionnaireDao;
    }
    
    /**
     * 获取问题 DAO
     */
    public static QuestionDAO getQuestionDao() {
        return questionDao;
    }
    
    /**
     * 获取选项 DAO
     */
    public static OptionDAO getOptionDao() {
        return optionDao;
    }
    
    /**
     * 获取答案 DAO
     */
    public static AnswerDAO getAnswerDao() {
        return answerDao;
    }
    
    /**
     * 获取答案详情 DAO
     */
    public static AnswerDetailDAO getAnswerDetailDao() {
        return answerDetailDao;
    }
}
