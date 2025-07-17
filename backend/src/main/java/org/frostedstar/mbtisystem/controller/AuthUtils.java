package org.frostedstar.mbtisystem.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.frostedstar.mbtisystem.dto.ApiResponse;
import org.frostedstar.mbtisystem.entity.User;
import org.frostedstar.mbtisystem.service.ServiceFactory;
import org.frostedstar.mbtisystem.service.UserService;

import java.io.IOException;
import java.util.Optional;

/**
 * 权限检查和HTTP工具类
 * 提供包内通用的用户身份验证、权限检查和HTTP方法验证
 */
@Slf4j
class AuthUtils {
    
    private static final UserService userService = ServiceFactory.getUserService();
    
    /**
     * 检验HTTP方法是否匹配
     * 
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     * @param baseController 基础控制器，用于发送响应
     * @param expectedMethod 期望的HTTP方法（GET, POST, PUT, DELETE等）
     * @return 如果方法匹配返回true，否则返回false并发送错误响应
     */
    static boolean checkHttpMethod(HttpServletRequest request, HttpServletResponse response, BaseController baseController, String expectedMethod) throws IOException {
        if (!expectedMethod.equals(request.getMethod())) {
            ApiResponse<Object> apiResponse = ApiResponse.error("Method Not Allowed");
            baseController.sendApiResponse(response, apiResponse);
            return false;
        }
        return true;
    }
    
    /**
     * 检查用户是否已登录
     * 
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     * @param baseController 基础控制器，用于发送响应
     * @return 如果已登录返回用户对象，否则返回null并发送错误响应
     */
    static User checkLogin(HttpServletRequest request, HttpServletResponse response, BaseController baseController) throws IOException {
        // 检查用户是否已登录
        HttpSession session = request.getSession(false);
        if (session == null) {
            ApiResponse<Object> apiResponse = ApiResponse.error("用户未登录");
            baseController.sendApiResponse(response, apiResponse);
            return null;
        }
        
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            ApiResponse<Object> apiResponse = ApiResponse.error("用户未登录");
            baseController.sendApiResponse(response, apiResponse);
            return null;
        }
        
        // 获取用户信息
        Optional<User> userOpt = userService.findById(userId);
        if (userOpt.isEmpty()) {
            ApiResponse<Object> apiResponse = ApiResponse.error("用户不存在");
            baseController.sendApiResponse(response, apiResponse);
            return null;
        }
        
        return userOpt.get();
    }
    
    /**
     * 检查管理员权限
     * 
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     * @param baseController 基础控制器，用于发送响应
     * @return 如果是管理员返回用户对象，否则返回null并发送错误响应
     */
    static User checkAdmin(HttpServletRequest request, HttpServletResponse response, BaseController baseController) throws IOException {
        // 首先检查是否已登录
        User user = checkLogin(request, response, baseController);
        if (user == null) {
            return null;
        }
        
        // 检查管理员权限
        if (!User.Role.ADMIN.equals(user.getRole())) {
            ApiResponse<Object> apiResponse = ApiResponse.error("权限不足，需要管理员权限");
            baseController.sendApiResponse(response, apiResponse);
            return null;
        }
        
        return user;
    }
    
    /**
     * 检查用户权限（包括管理员和普通用户）
     * 
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     * @param baseController 基础控制器，用于发送响应
     * @param requiredUserId 需要匹配的用户ID（null表示不检查用户ID匹配）
     * @return 如果有权限返回用户对象，否则返回null并发送错误响应
     */
    static User checkUserPermission(HttpServletRequest request, HttpServletResponse response, BaseController baseController, Integer requiredUserId) throws IOException {
        // 首先检查是否已登录
        User user = checkLogin(request, response, baseController);
        if (user == null) {
            return null;
        }
        
        // 如果是管理员，直接通过
        if (User.Role.ADMIN.equals(user.getRole())) {
            return user;
        }
        
        // 如果指定了用户ID，检查是否为当前用户
        if (requiredUserId != null && !user.getUserId().equals(requiredUserId)) {
            ApiResponse<Object> apiResponse = ApiResponse.error("权限不足，只能操作自己的数据");
            baseController.sendApiResponse(response, apiResponse);
            return null;
        }
        
        return user;
    }
    
    /**
     * 从会话中获取当前用户ID
     * 
     * @param request HTTP请求对象
     * @return 用户ID，如果未登录返回null
     */
    static Integer getCurrentUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        return (Integer) session.getAttribute("userId");
    }
    
    /**
     * 从会话中获取当前用户名
     * 
     * @param request HTTP请求对象
     * @return 用户名，如果未登录返回null
     */
    static String getCurrentUsername(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        return (String) session.getAttribute("username");
    }
}
