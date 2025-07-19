package org.frostedstar.mbtisystem.service;

import lombok.Getter;
import org.frostedstar.mbtisystem.service.impl.QuestionnaireServiceImpl;
import org.frostedstar.mbtisystem.service.impl.QuestionServiceImpl;
import org.frostedstar.mbtisystem.service.impl.TestServiceImpl;
import org.frostedstar.mbtisystem.service.impl.UserServiceImpl;

/**
 * Service 工厂类
 */
public class ServiceFactory {

    /**
     * -- GETTER --
     *  获取用户服务
     */
    @Getter
    private static final UserService userService = new UserServiceImpl();
    /**
     * -- GETTER --
     *  获取问卷服务
     */
    @Getter
    private static final QuestionnaireService questionnaireService = new QuestionnaireServiceImpl();
    /**
     * -- GETTER --
     *  获取问题服务
     */
    @Getter
    private static final QuestionService questionService = new QuestionServiceImpl();
    /**
     * -- GETTER --
     *  获取测试服务
     */
    @Getter
    private static final TestService testService = new TestServiceImpl();

}
