package org.frostedstar.mbtisystem.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.frostedstar.mbtisystem.controller.*;
import org.frostedstar.mbtisystem.dto.ApiResponse;
import org.frostedstar.mbtisystem.dto.ErrorResponse;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * API 分发器 Servlet
 */
@Slf4j
@WebServlet(name = "ApiDispatcherServlet", urlPatterns = "/api/*")
public class ApiDispatcherServlet extends HttpServlet {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, Object> controllers = new HashMap<>();
    // 路由表: 方法签名 -> (控制器实例, 方法对象)
    private final Map<String, Map.Entry<Object, Method>> routeMap = new HashMap<>();
    
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
        
        // 构建路由表
        buildRouteMap();
    }
    
    private void buildRouteMap() {
        for (Map.Entry<String, Object> entry : controllers.entrySet()) {
            String controllerName = entry.getKey();
            Object controller = entry.getValue();
            
            for (Method method : controller.getClass().getMethods()) {
                Route route = method.getAnnotation(Route.class);
                if (route != null) {
                    String path = normalizePath(route.value());
                    String httpMethod = route.method().toUpperCase();
                    String routeKey = httpMethod + ":" + controllerName + path;
                    
                    routeMap.put(routeKey, new AbstractMap.SimpleEntry<>(controller, method));
                    log.info("注册路由: {} -> {}.{}", routeKey, 
                            controller.getClass().getSimpleName(), method.getName());
                }
            }
        }
    }
    
    private String normalizePath(String path) {
        if (path == null || path.isEmpty()) return "";
        if (!path.startsWith("/")) return "/" + path;
        return path;
    }
    
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.length() <= 1) {
            sendErrorResponse(response, 404, "API 路径不存在", request.getRequestURI());
            return;
        }
        
        // 解析路径
        String[] pathParts = pathInfo.substring(1).split("/");
        String controllerName = pathParts[0];
        String subPath = pathParts.length > 1 ? "/" + String.join("/", Arrays.copyOfRange(pathParts, 1, pathParts.length)) : "";
        
        String httpMethod = request.getMethod().toUpperCase();
        String routeKey = httpMethod + ":" + controllerName + subPath;
        
        // 精确匹配路由
        Map.Entry<Object, Method> routeEntry = routeMap.get(routeKey);
        
        // 如果精确匹配失败，尝试前缀匹配（支持路径参数）
        if (routeEntry == null) {
            routeEntry = findRouteByPrefix(httpMethod, controllerName, subPath);
        }
        
        if (routeEntry != null) {
            try {
                invokeControllerMethod(routeEntry.getKey(), routeEntry.getValue(), request, response, subPath);
            } catch (Exception e) {
                log.error("处理请求时发生异常：{}", e.getMessage(), e);
                sendErrorResponse(response, 500, "服务器内部错误", request.getRequestURI());
            }
        } else {
            sendErrorResponse(response, 404, "API 端点不存在: " + routeKey, request.getRequestURI());
        }
    }
    
    private Map.Entry<Object, Method> findRouteByPrefix(String httpMethod, String controllerName, String subPath) {
        for (Map.Entry<String, Map.Entry<Object, Method>> entry : routeMap.entrySet()) {
            String routeKey = entry.getKey();
            
            if (routeKey.startsWith(httpMethod + ":" + controllerName)) {
                String routePath = routeKey.substring(httpMethod.length() + controllerName.length() + 1);
                
                // 支持三种匹配模式：
                // 1. 精确匹配: @Route("/detail")
                // 2. 参数化匹配: @Route("/byCreator/")  
                // 3. 根路径参数匹配: @Route("") - 匹配任何以 / 开头的路径
                if (routePath.endsWith("/") && subPath.startsWith(routePath)) {
                    return entry.getValue();
                } else if (routePath.isEmpty() && subPath.startsWith("/")) {
                    // 空路由路径匹配任何以 / 开头的子路径
                    return entry.getValue();
                }
            }
        }
        return null;
    }
    
    private void invokeControllerMethod(Object controller, Method method, 
                                       HttpServletRequest request, HttpServletResponse response,
                                       String subPath) throws Exception {
        // 提取路径参数
        String[] pathParams = extractPathParameters(method, subPath);
        
        // 根据方法参数类型动态调用
        Class<?>[] paramTypes = method.getParameterTypes();
        if (paramTypes.length == 0) {
            method.invoke(controller);
        } else if (paramTypes.length == 2) {
            method.invoke(controller, request, response);
        } else if (paramTypes.length == 3 && paramTypes[0] == String[].class) {
            method.invoke(controller, pathParams, request, response);
        } else {
            throw new ServletException("不支持的控制器方法签名: " + method.getName());
        }
    }
    
    private String[] extractPathParameters(Method method, String subPath) {
        Route route = method.getAnnotation(Route.class);
        if (route == null) return new String[0];
        
        String routePath = normalizePath(route.value());
        if (!routePath.endsWith("/") || subPath == null) {
            return new String[0];
        }
        
        // 提取参数部分: "/byCreator/123" -> "123"
        String paramPart = subPath.substring(routePath.length());
        return paramPart.split("/");
    }
    
    /**
     * 发送错误响应（使用 ApiResponse 格式）
     */
    private void sendErrorResponse(HttpServletResponse response, int status, String message, String path) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        
        ErrorResponse errorResponse = ErrorResponse.create(
            "HTTP_ERROR",
            message,
            status,
            path
        );
        
        ApiResponse<ErrorResponse> apiResponse = ApiResponse.systemError(errorResponse);
        objectMapper.writeValue(response.getWriter(), apiResponse);
    }
}
