package org.frostedstar.mbtisystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.frostedstar.mbtisystem.dto.ApiResponse;
import org.frostedstar.mbtisystem.dto.ErrorResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 基础控制器
 */
@Slf4j
public abstract class BaseController {
    
    protected final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());
    
    /**
     * 发送成功响应
     */
    protected void sendSuccess(HttpServletResponse response, Object data) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", data);
        result.put("timestamp", System.currentTimeMillis());
        
        objectMapper.writeValue(response.getWriter(), result);
    }
    
    /**
     * 发送错误响应
     */
    protected void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("message", message);
        result.put("timestamp", System.currentTimeMillis());
        
        objectMapper.writeValue(response.getWriter(), result);
    }
    
    /**
     * 读取请求体
     */
    protected String readRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = request.getReader().readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }
    
    /**
     * 解析 JSON 请求体
     */
    protected <T> T parseRequestBody(HttpServletRequest request, Class<T> clazz) throws IOException {
        String body = readRequestBody(request);
        return objectMapper.readValue(body, clazz);
    }
    
    /**
     * 发送 API 响应（使用 DTO）
     */
    protected void sendApiResponse(HttpServletResponse response, ApiResponse<?> apiResponse) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        
        objectMapper.writeValue(response.getWriter(), apiResponse);
    }
    
    /**
     * 发送错误响应（使用 DTO）
     */
    protected void sendErrorResponse(HttpServletResponse response, int status, String message, String path) throws IOException {
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
