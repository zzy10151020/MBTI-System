package org.frostedstar.mbtisystem.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.frostedstar.mbtisystem.controller.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * API 分发器 Servlet
 */
@Slf4j
@WebServlet(name = "ApiDispatcherServlet", urlPatterns = "/api/*")
public class ApiDispatcherServlet extends HttpServlet {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, Object> controllers = new HashMap<>();
    
    @Override
    public void init() throws ServletException {
        super.init();
        // 初始化控制器
        controllers.put("user", new UserController());
        controllers.put("auth", new AuthController());
        controllers.put("questionnaire", new QuestionnaireController());
        controllers.put("question", new QuestionController());
        controllers.put("test", new TestController());
        
        log.info("API 分发器初始化完成，注册控制器：{}", controllers.keySet());
    }
    
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.length() <= 1) {
            sendError(response, 404, "API 路径不存在");
            return;
        }
        
        // 解析路径
        String[] pathParts = pathInfo.substring(1).split("/");
        String controllerName = pathParts[0];
        String action = pathParts.length > 1 ? pathParts[1] : "";
        
        Object controller = controllers.get(controllerName);
        if (controller == null) {
            sendError(response, 404, "控制器不存在：" + controllerName);
            return;
        }
        
        try {
            // 调用控制器方法
            invokeController(controller, action, request, response);
        } catch (Exception e) {
            log.error("处理请求时发生异常：{}", e.getMessage(), e);
            sendError(response, 500, "服务器内部错误");
        }
    }
    
    /**
     * 调用控制器方法
     */
    private void invokeController(Object controller, String action, 
                                 HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        
        String method = request.getMethod();
        String methodName = action.isEmpty() ? method.toLowerCase() : action;
        
        // 使用反射调用方法
        try {
            var controllerMethod = controller.getClass()
                    .getMethod(methodName, HttpServletRequest.class, HttpServletResponse.class);
            controllerMethod.invoke(controller, request, response);
        } catch (NoSuchMethodException e) {
            sendError(response, 404, "方法不存在：" + methodName);
        }
    }
    
    /**
     * 发送错误响应
     */
    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", message);
        errorResponse.put("timestamp", System.currentTimeMillis());
        
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}
