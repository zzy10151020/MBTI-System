package org.frostedstar.mbtisystem.dao;

import lombok.Getter;
import org.frostedstar.mbtisystem.dao.impl.*;

/**
 * DAO 工厂类
 */
public class DaoFactory {

    /**
     * -- GETTER --
     *  获取用户 DAO
     */
    @Getter
    private static final UserDAO userDao = new UserDAOImpl();
    /**
     * -- GETTER --
     *  获取问卷 DAO
     */
    @Getter
    private static final QuestionnaireDAO questionnaireDao = new QuestionnaireDAOImpl();
    /**
     * -- GETTER --
     *  获取问题 DAO
     */
    @Getter
    private static final QuestionDAO questionDao = new QuestionDAOImpl();
    /**
     * -- GETTER --
     *  获取选项 DAO
     */
    @Getter
    private static final OptionDAO optionDao = new OptionDAOImpl();
    /**
     * -- GETTER --
     *  获取答案 DAO
     */
    @Getter
    private static final AnswerDAO answerDao = new AnswerDAOImpl();
    /**
     * -- GETTER --
     *  获取答案详情 DAO
     */
    @Getter
    private static final AnswerDetailDAO answerDetailDao = new AnswerDetailDAOImpl();

}
