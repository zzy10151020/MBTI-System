package org.frostedstar.mbtisystem.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * 错误响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    
    private String error;
    private String message;
    private Integer status;
    private String path;
    private Long timestamp;
    
    /**
     * 创建错误响应
     */
    public static ErrorResponse create(String error, String message, Integer status, String path) {
        return ErrorResponse.builder()
                .error(error)
                .message(message)
                .status(status)
                .path(path)
                .timestamp(System.currentTimeMillis())
                .build();
    }
    
    /**
     * 创建简单错误响应
     */
    public static ErrorResponse create(String message, Integer status) {
        return ErrorResponse.builder()
                .message(message)
                .status(status)
                .timestamp(System.currentTimeMillis())
                .build();
    }
}
