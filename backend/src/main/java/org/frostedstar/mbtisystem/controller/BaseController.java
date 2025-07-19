package org.frostedstar.mbtisystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.frostedstar.mbtisystem.dto.ApiResponse;
import org.frostedstar.mbtisystem.dto.ErrorResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
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
     * 读取请求体（改进版本，支持更大的请求体和更好的字符编码处理）
     */
    protected String readRequestBody(HttpServletRequest request) throws IOException {
        // 设置字符编码
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }
    
    /**
     * 解析 JSON 请求体（单个对象）
     */
    protected <T> T parseRequestBody(HttpServletRequest request, Class<T> clazz) throws IOException {
        String body = readRequestBody(request);
        if (body == null || body.trim().isEmpty()) {
            return null;
        }
        return objectMapper.readValue(body, clazz);
    }
    
    /**
     * 解析 JSON 请求体（集合类型）
     * 专门处理 List<T> 类型的解析，避免类型擦除问题
     */
    protected <T> List<T> parseRequestBodyList(HttpServletRequest request, Class<T> elementClass) throws IOException {
        String body = readRequestBody(request);
        if (body == null || body.trim().isEmpty()) {
            return null;
        }
        CollectionType listType = objectMapper.getTypeFactory()
                .constructCollectionType(List.class, elementClass);
        return objectMapper.readValue(body, listType);
    }
    
    /**
     * 解析原始 JSON 字符串为对象（单个对象）
     */
    protected <T> T parseJsonString(String jsonString, Class<T> clazz) throws IOException {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return null;
        }
        return objectMapper.readValue(jsonString, clazz);
    }
    
    /**
     * 解析原始 JSON 字符串为集合（集合类型）
     */
    protected <T> List<T> parseJsonStringList(String jsonString, Class<T> elementClass) throws IOException {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return null;
        }
        CollectionType listType = objectMapper.getTypeFactory()
                .constructCollectionType(List.class, elementClass);
        return objectMapper.readValue(jsonString, listType);
    }
    
    /**
     * 发送 API 响应（使用标准 ApiResponse 格式）
     */
    protected void sendApiResponse(HttpServletResponse response, ApiResponse<?> apiResponse) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        
        objectMapper.writeValue(response.getWriter(), apiResponse);
    }
    
    /**
     * 发送错误响应（使用标准 ApiResponse 格式）
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
