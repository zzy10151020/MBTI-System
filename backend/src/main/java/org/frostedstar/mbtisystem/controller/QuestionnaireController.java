package org.frostedstar.mbtisystem.controller;

import org.frostedstar.mbtisystem.entity.Questionnaire;
import org.frostedstar.mbtisystem.entity.User;
import org.frostedstar.mbtisystem.service.QuestionnaireService;
import org.frostedstar.mbtisystem.service.ServiceFactory;
import org.frostedstar.mbtisystem.dto.ApiResponse;
import org.frostedstar.mbtisystem.dto.QuestionnaireDTO;
import org.frostedstar.mbtisystem.dto.QuestionDTO;
import org.frostedstar.mbtisystem.dto.OperationType;
import org.frostedstar.mbtisystem.servlet.Route;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    
    public QuestionnaireController() {
        this.questionnaireService = ServiceFactory.getQuestionnaireService();
    }
    
    /**
     * 获取问卷列表
     */
    @Route(value = "", method = "GET")
    public void getQuestionnaires(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (!AuthUtils.checkHttpMethod(request, response, this, "GET")) return;
            
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
            sendErrorResponse(response, 500, "获取问卷列表失败: " + e.getMessage(), "/api/questionnaire");
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
    @Route(value = "", method = "POST")
    public void createQuestionnaire(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (!AuthUtils.checkHttpMethod(request, response, this, "POST")) return;
            
            // 检查管理员权限
            User user = AuthUtils.checkAdmin(request, response, this);
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
                .questions(createRequest.getQuestions() != null ?
                    createRequest.getQuestions().stream()
                        .map(QuestionDTO::toEntity)
                        .collect(Collectors.toList()) : null)
                .build();

            Questionnaire createdQuestionnaire = questionnaireService.createQuestionnaire(questionnaire);
            
            QuestionnaireDTO questionnaireDTO = QuestionnaireDTO.fromEntity(createdQuestionnaire);
            ApiResponse<QuestionnaireDTO> apiResponse = ApiResponse.success("问卷创建成功", questionnaireDTO);
            sendApiResponse(response, apiResponse);
        } catch (Exception e) {
            log.error("创建问卷失败", e);
            sendErrorResponse(response, 500, "创建问卷失败: " + e.getMessage(), "/api/questionnaire");
        }
    }
    
    /**
     * 更新问卷
     */
    @Route(value = "", method = "PUT")
    public void updateQuestionnaire(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (!AuthUtils.checkHttpMethod(request, response, this, "PUT")) return;
            
            // 检查管理员权限
            User user = AuthUtils.checkAdmin(request, response, this);
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
            sendErrorResponse(response, 500, "更新问卷失败: " + e.getMessage(), "/api/questionnaire");
        }
    }
    
    /**
     * 删除问卷
     */
    @Route(value = "", method = "DELETE")
    public void deleteQuestionnaire(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (!AuthUtils.checkHttpMethod(request, response, this, "DELETE")) return;
            
            // 检查管理员权限
            User user = AuthUtils.checkAdmin(request, response, this);
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
            
            // 级联删除问卷
            if (!questionnaireService.deleteQuestionnaireWithCascade(id)) {
                throw new RuntimeException("可能是数据库错误或级联删除失败");
            }
            
            ApiResponse<String> apiResponse = ApiResponse.success("问卷删除成功", "问卷删除成功");
            sendApiResponse(response, apiResponse);
            
        } catch (Exception e) {
            log.error("删除问卷失败", e);
            sendErrorResponse(response, 500, "删除问卷失败: " + e.getMessage(), "/api/questionnaire");
        }
    }
}
