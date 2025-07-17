package org.frostedstar.mbtisystem.controller;

import org.frostedstar.mbtisystem.entity.Questionnaire;
import org.frostedstar.mbtisystem.entity.User;
import org.frostedstar.mbtisystem.service.QuestionnaireService;
import org.frostedstar.mbtisystem.service.ServiceFactory;
import org.frostedstar.mbtisystem.service.UserService;
import org.frostedstar.mbtisystem.dto.ApiResponse;
import org.frostedstar.mbtisystem.dto.QuestionnaireDTO;
import org.frostedstar.mbtisystem.dto.OperationType;
import org.frostedstar.mbtisystem.dto.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 问卷控制器
 */
@Slf4j
public class QuestionnaireController extends BaseController {
    
    private final QuestionnaireService questionnaireService;
    private final UserService userService;
    
    public QuestionnaireController() {
        this.questionnaireService = ServiceFactory.getQuestionnaireService();
        this.userService = ServiceFactory.getUserService();
    }
    
    /**
     * 获取问卷列表
     */
    public void get(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (!"GET".equals(request.getMethod())) {
                ApiResponse<Object> apiResponse = ApiResponse.error("Method Not Allowed");
                sendApiResponse(response, apiResponse);
                return;
            }
            
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/questionnaire") || pathInfo.equals("/questionnaire/")) {
                // 获取所有问卷
                fetchAllQuestionnaires(response);
            } else {
                // 尝试解析问卷ID
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length >= 3 && "questionnaire".equals(pathParts[2])) {
                    if (pathParts.length >= 4) {
                        try {
                            Integer id = Integer.parseInt(pathParts[3]);
                            Optional<Questionnaire> questionnaire = questionnaireService.findById(id);
                            if (questionnaire.isPresent()) {
                                QuestionnaireDTO questionnaireDTO = QuestionnaireDTO.fromEntity(questionnaire.get());
                                ApiResponse<QuestionnaireDTO> apiResponse = ApiResponse.success(questionnaireDTO);
                                sendApiResponse(response, apiResponse);
                            } else {
                                ApiResponse<Object> apiResponse = ApiResponse.error("问卷不存在");
                                sendApiResponse(response, apiResponse);
                            }
                        } catch (NumberFormatException e) {
                            ApiResponse<Object> apiResponse = ApiResponse.error("无效的问卷ID");
                            sendApiResponse(response, apiResponse);
                        }
                    } else {
                        // 如果没有提供ID，则返回所有问卷
                        fetchAllQuestionnaires(response);
                    }
                } else {
                    ApiResponse<Object> apiResponse = ApiResponse.error("接口不存在");
                    sendApiResponse(response, apiResponse);
                }
            }
        } catch (Exception e) {
            log.error("获取问卷列表失败", e);
            ErrorResponse errorResponse = ErrorResponse.create(
                e.getClass().getSimpleName(),
                "获取问卷列表失败: " + e.getMessage(),
                500,
                "/api/questionnaire"
            );
            ApiResponse<ErrorResponse> apiResponse = ApiResponse.systemError(errorResponse);
            sendApiResponse(response, apiResponse);
        }
    }

    /**
     * 获取所有问卷
     */
    private void fetchAllQuestionnaires(HttpServletResponse response) throws IOException {
        List<Questionnaire> questionnaires = questionnaireService.findAll();
        List<QuestionnaireDTO> responses = questionnaires.stream()
            .map(QuestionnaireDTO::fromEntitySimple)
            .collect(Collectors.toList());

        ApiResponse<List<QuestionnaireDTO>> apiResponse = ApiResponse.success(responses);
        sendApiResponse(response, apiResponse);
    }

    /**
     * 创建问卷
     */
    public void post(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (!"POST".equals(request.getMethod())) {
                ApiResponse<Object> apiResponse = ApiResponse.error("Method Not Allowed");
                sendApiResponse(response, apiResponse);
                return;
            }
            
            // 检查管理员权限
            User user = checkAdmin(request, response);
            if (user == null) return;
            
            // 解析请求体
            QuestionnaireDTO createRequest = parseRequestBody(request, QuestionnaireDTO.class);
            createRequest.setOperationType(OperationType.CREATE);
            
            // 验证请求数据
            if (!createRequest.isValid()) {
                ApiResponse<Object> apiResponse = ApiResponse.error("请求数据不完整");
                sendApiResponse(response, apiResponse);
                return;
            }
            
            // 创建问卷
            Questionnaire questionnaire = Questionnaire.builder()
                .title(createRequest.getTitle())
                .description(createRequest.getDescription())
                .creatorId(user.getUserId())
                .build();
            
            Questionnaire savedQuestionnaire = questionnaireService.save(questionnaire);
            
            QuestionnaireDTO questionnaireDTO = QuestionnaireDTO.fromEntity(savedQuestionnaire);
            ApiResponse<QuestionnaireDTO> apiResponse = ApiResponse.success("问卷创建成功", questionnaireDTO);
            sendApiResponse(response, apiResponse);
        } catch (Exception e) {
            log.error("创建问卷失败", e);
            ErrorResponse errorResponse = ErrorResponse.create(
                e.getClass().getSimpleName(),
                "创建问卷失败: " + e.getMessage(),
                500,
                "/api/questionnaire"
            );
            ApiResponse<ErrorResponse> apiResponse = ApiResponse.systemError(errorResponse);
            sendApiResponse(response, apiResponse);
        }
    }
    
    /**
     * 更新问卷
     */
    public void put(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (!"PUT".equals(request.getMethod())) {
                ApiResponse<Object> apiResponse = ApiResponse.error("Method Not Allowed");
                sendApiResponse(response, apiResponse);
                return;
            }
            
            // 检查管理员权限
            User user = checkAdmin(request, response);
            if (user == null) return;
            
            // 解析请求体
            QuestionnaireDTO updateRequest = parseRequestBody(request, QuestionnaireDTO.class);
            updateRequest.setOperationType(OperationType.UPDATE);
            
            // 验证请求数据
            if (!updateRequest.isValid()) {
                ApiResponse<Object> apiResponse = ApiResponse.error("请求数据不完整");
                sendApiResponse(response, apiResponse);
                return;
            }
            
            // 获取现有问卷
            Optional<Questionnaire> existingQuestionnaireOpt = questionnaireService.findById(updateRequest.getQuestionnaireId());
            if (existingQuestionnaireOpt.isEmpty()) {
                ApiResponse<Object> apiResponse = ApiResponse.error("问卷不存在");
                sendApiResponse(response, apiResponse);
                return;
            }
            
            Questionnaire existingQuestionnaire = existingQuestionnaireOpt.get();
            
            // 更新问卷信息
            Questionnaire updatedQuestionnaire = Questionnaire.builder()
                .questionnaireId(updateRequest.getQuestionnaireId())
                .title(updateRequest.getTitle())
                .description(updateRequest.getDescription())
                .creatorId(existingQuestionnaire.getCreatorId())
                .createdAt(existingQuestionnaire.getCreatedAt())
                .isPublished(existingQuestionnaire.getIsPublished())
                .build();
            
            questionnaireService.update(updatedQuestionnaire);
            
            QuestionnaireDTO questionnaireDTO = QuestionnaireDTO.fromEntity(updatedQuestionnaire);
            ApiResponse<QuestionnaireDTO> apiResponse = ApiResponse.success("问卷更新成功", questionnaireDTO);
            sendApiResponse(response, apiResponse);
            
        } catch (Exception e) {
            log.error("更新问卷失败", e);
            ErrorResponse errorResponse = ErrorResponse.create(
                e.getClass().getSimpleName(),
                "更新问卷失败: " + e.getMessage(),
                500,
                "/api/questionnaire"
            );
            ApiResponse<ErrorResponse> apiResponse = ApiResponse.systemError(errorResponse);
            sendApiResponse(response, apiResponse);
        }
    }
    
    /**
     * 删除问卷
     */
    public void delete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (!"DELETE".equals(request.getMethod())) {
                ApiResponse<Object> apiResponse = ApiResponse.error("Method Not Allowed");
                sendApiResponse(response, apiResponse);
                return;
            }
            
            // 检查管理员权限
            User user = checkAdmin(request, response);
            if (user == null) return;
            
            // 从URL参数或请求体获取ID
            String idParam = request.getParameter("id");
            Integer id;
            
            if (idParam != null) {
                try {
                    id = Integer.parseInt(idParam);
                } catch (NumberFormatException e) {
                    ApiResponse<Object> apiResponse = ApiResponse.error("无效的问卷ID");
                    sendApiResponse(response, apiResponse);
                    return;
                }
            } else {
                // 从请求体获取删除请求
                QuestionnaireDTO deleteRequest = parseRequestBody(request, QuestionnaireDTO.class);
                deleteRequest.setOperationType(OperationType.DELETE);
                
                if (!deleteRequest.isValid()) {
                    ApiResponse<Object> apiResponse = ApiResponse.error("缺少问卷ID");
                    sendApiResponse(response, apiResponse);
                    return;
                }
                
                id = deleteRequest.getQuestionnaireId();
            }
            
            // 检查问卷是否存在
            Optional<Questionnaire> questionnaireOpt = questionnaireService.findById(id);
            if (questionnaireOpt.isEmpty()) {
                ApiResponse<Object> apiResponse = ApiResponse.error("问卷不存在");
                sendApiResponse(response, apiResponse);
                return;
            }
            
            // 删除问卷
            questionnaireService.deleteById(id);
            
            ApiResponse<String> apiResponse = ApiResponse.success("问卷删除成功", "问卷删除成功");
            sendApiResponse(response, apiResponse);
            
        } catch (Exception e) {
            log.error("删除问卷失败", e);
            ErrorResponse errorResponse = ErrorResponse.create(
                e.getClass().getSimpleName(),
                "删除问卷失败: " + e.getMessage(),
                500,
                "/api/questionnaire"
            );
            ApiResponse<ErrorResponse> apiResponse = ApiResponse.systemError(errorResponse);
            sendApiResponse(response, apiResponse);
        }
    }
    
    /**
     * 检查管理员权限
     */
    private User checkAdmin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 检查用户是否已登录
        HttpSession session = request.getSession(false);
        if (session == null) {
            ApiResponse<Object> apiResponse = ApiResponse.error("用户未登录");
            sendApiResponse(response, apiResponse);
            return null;
        }
        
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            ApiResponse<Object> apiResponse = ApiResponse.error("用户未登录");
            sendApiResponse(response, apiResponse);
            return null;
        }
        
        // 获取用户信息
        Optional<User> userOpt = userService.findById(userId);
        if (userOpt.isEmpty()) {
            ApiResponse<Object> apiResponse = ApiResponse.error("用户不存在");
            sendApiResponse(response, apiResponse);
            return null;
        }
        
        User user = userOpt.get();
        
        // 检查管理员权限
        if (!User.Role.ADMIN.equals(user.getRole())) {
            ApiResponse<Object> apiResponse = ApiResponse.error("权限不足，需要管理员权限");
            sendApiResponse(response, apiResponse);
            return null;
        }
        
        return user;
    }
}
