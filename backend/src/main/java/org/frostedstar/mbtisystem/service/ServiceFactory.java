package org.frostedstar.mbtisystem.service;

import org.frostedstar.mbtisystem.service.impl.QuestionnaireServiceImpl;
import org.frostedstar.mbtisystem.service.impl.QuestionServiceImpl;
import org.frostedstar.mbtisystem.service.impl.TestServiceImpl;
import org.frostedstar.mbtisystem.service.impl.UserServiceImpl;

/**
 * Service 工厂类
 */
public class ServiceFactory {
    
    private static final UserService userService = new UserServiceImpl();
    private static final QuestionnaireService questionnaireService = new QuestionnaireServiceImpl();
    private static final QuestionService questionService = new QuestionServiceImpl();
    private static final TestService testService = new TestServiceImpl();
    
    /**
     * 获取用户服务
     */
    public static UserService getUserService() {
        return userService;
    }
    
    /**
     * 获取问卷服务
     */
    public static QuestionnaireService getQuestionnaireService() {
        return questionnaireService;
    }
    
    /**
     * 获取问题服务
     */
    public static QuestionService getQuestionService() {
        return questionService;
    }
    
    /**
     * 获取测试服务
     */
    public static TestService getTestService() {
        return testService;
    }
}
