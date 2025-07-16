package org.frostedstar.mbtisystem.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * 通用响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    
    private Boolean success;
    private String message;
    private T data;
    private Long timestamp;
    
    /**
     * 成功响应
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .timestamp(System.currentTimeMillis())
                .build();
    }
    
    /**
     * 成功响应带消息
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(System.currentTimeMillis())
                .build();
    }
    
    /**
     * 失败响应 - 用于业务逻辑错误
     */
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .timestamp(System.currentTimeMillis())
                .build();
    }
    
    /**
     * 系统错误响应 - 用于系统级错误，返回ErrorResponse
     */
    public static ApiResponse<ErrorResponse> systemError(ErrorResponse errorResponse) {
        return ApiResponse.<ErrorResponse>builder()
                .success(false)
                .message("系统错误")
                .data(errorResponse)
                .timestamp(System.currentTimeMillis())
                .build();
    }
}
