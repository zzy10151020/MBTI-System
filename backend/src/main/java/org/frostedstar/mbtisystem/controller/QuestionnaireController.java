package org.frostedstar.mbtisystem.controller;

import org.frostedstar.mbtisystem.entity.Questionnaire;
import org.frostedstar.mbtisystem.entity.User;
import org.frostedstar.mbtisystem.service.QuestionnaireService;
import org.frostedstar.mbtisystem.service.ServiceFactory;
import org.frostedstar.mbtisystem.dto.ApiResponse;
import org.frostedstar.mbtisystem.dto.questionnairedto.*;
import org.frostedstar.mbtisystem.dto.questiondto.QuestionRequestDTO;
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
     * 根据问卷ID查找
     */
    @Route(value = "", method = "GET")
    public void getQuestionnaires(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (!AuthUtils.checkHttpMethod(request, response, this, "GET")) return;
            
            // 从完整的URI中提取路径信息
            String requestURI = request.getRequestURI();
            String contextPath = request.getContextPath();
            String servletPath = request.getServletPath();
            
            // 计算实际的路径信息: requestURI - contextPath - servletPath - "/questionnaire"
            String fullPath = requestURI.substring(contextPath.length() + servletPath.length());
            String pathInfo = fullPath.substring("/questionnaire".length());
            
            log.debug("QuestionnaireController.getQuestionnaires - requestURI: {}, contextPath: {}, servletPath: {}, fullPath: {}, pathInfo: {}", 
                requestURI, contextPath, servletPath, fullPath, pathInfo);
            
            if (pathInfo == null || pathInfo.equals("/") || pathInfo.isEmpty()) {
                // 根路径不支持，需要提供具体的问卷ID或使用其他端点
                ApiResponse<Object> apiResponse = ApiResponse.error("请提供问卷ID，或使用 /api/questionnaire/all、/api/questionnaire/published 等端点");
                sendApiResponse(response, apiResponse);
            } else {
                // 尝试解析问卷ID - pathInfo 应该是 "/1", "/2" 这样的格式
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length >= 2 && !pathParts[1].isEmpty()) {
                    try {
                        Integer id = Integer.parseInt(pathParts[1]); // pathParts[0] 是空字符串
                        Optional<Questionnaire> questionnaire = questionnaireService.findById(id);
                        if (questionnaire.isPresent()) {
                            QuestionnaireResponseDTO questionnaireDTO = QuestionnaireResponseDTO.fromEntity(questionnaire.get());
                            ApiResponse<QuestionnaireResponseDTO> apiResponse = ApiResponse.success("成功获取问卷", questionnaireDTO);
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
                    // 如果路径格式不正确
                    ApiResponse<Object> apiResponse = ApiResponse.error("请提供有效的问卷ID");
                    sendApiResponse(response, apiResponse);
                }
            }
        } catch (Exception e) {
            log.error("获取问卷列表失败", e);
            sendErrorResponse(response, 500, "获取问卷列表失败: " + e.getMessage(), "/api/questionnaire");
        }
    }

    /**
     * 根据创建者ID查找问卷
     */
    @Route(value = "/byCreator", method = "POST")
    public void getQuestionnairesByCreator(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (!AuthUtils.checkHttpMethod(request, response, this, "POST")) return;

            // 解析请求体中的创建者ID
            QuestionnaireRequestDTO requestDTO = parseRequestBody(request, QuestionnaireRequestDTO.class);
            if (requestDTO.getCreatorId() == null || requestDTO.getCreatorId() <= 0) {
                ApiResponse<Object> apiResponse = ApiResponse.error("缺少创建者ID");
                sendApiResponse(response, apiResponse);
                return;
            }
            Integer creatorId = requestDTO.getCreatorId();
            // 查找问卷
            List<Questionnaire> questionnaires = questionnaireService.findByCreatorId(creatorId);
            if (questionnaires.isEmpty()) {
                ApiResponse<Object> apiResponse = ApiResponse.error("没有找到该创建者的问卷");
                sendApiResponse(response, apiResponse);
                return;
            }
            List<QuestionnaireResponseDTO> responses = questionnaires.stream()
                    .map(QuestionnaireResponseDTO::fromEntitySimple)
                    .collect(Collectors.toList());
            ApiResponse<List<QuestionnaireResponseDTO>> apiResponse = ApiResponse.success("成功获取问卷", responses);
            sendApiResponse(response, apiResponse);
        } catch (Exception e) {
            log.error("根据创建者ID查找问卷失败", e);
            sendErrorResponse(response, 500, "根据创建者ID查找问卷失败: " + e.getMessage(), "/api/questionnaire/creator");
        }
    }

    /**
     * 查找已发布的问卷
     */
    @Route(value = "/published", method = "GET")
    public void getPublishedQuestionnaires(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (!AuthUtils.checkHttpMethod(request, response, this, "GET")) return;

            List<Questionnaire> publishedQuestionnaires = questionnaireService.findPublished();
            List<QuestionnaireResponseDTO> responses = publishedQuestionnaires.stream()
                    .map(QuestionnaireResponseDTO::fromEntitySimple)
                    .collect(Collectors.toList());

            ApiResponse<List<QuestionnaireResponseDTO>> apiResponse = ApiResponse.success("成功获取已发布问卷", responses);
            sendApiResponse(response, apiResponse);
        } catch (Exception e) {
            log.error("查找已发布的问卷失败", e);
            sendErrorResponse(response, 500, "查找已发布的问卷失败: " + e.getMessage(), "/api/questionnaire/published");
        }
    }

    /**
     * 获取全部问卷（包括未发布，仅管理员）
     */
    @Route(value = "/all", method = "GET")
    public void getAllQuestionnaires(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (!AuthUtils.checkHttpMethod(request, response, this, "GET")) return;

            // 检查管理员权限
            User user = AuthUtils.checkAdmin(request, response, this);
            if (user == null) return;

            // 获取所有问卷
            List<Questionnaire> questionnaires = questionnaireService.findAll();
            List<QuestionnaireResponseDTO> responses = questionnaires.stream()
                    .map(QuestionnaireResponseDTO::fromEntitySimple)
                    .collect(Collectors.toList());

            ApiResponse<List<QuestionnaireResponseDTO>> apiResponse = ApiResponse.success("成功获取所有问卷", responses);
            sendApiResponse(response, apiResponse);
        } catch (Exception e) {
            log.error("获取所有问卷失败", e);
            sendErrorResponse(response, 500, "获取所有问卷失败: " + e.getMessage(), "/api/questionnaire/all");
        }
    }

    /**
     * 根据标题模糊查找问卷
     */
    @Route(value = "/search", method = "POST")
    public void searchQuestionnaires(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (!AuthUtils.checkHttpMethod(request, response, this, "POST")) return;

            // 解析请求体中的标题
            QuestionnaireRequestDTO searchRequest = parseRequestBody(request, QuestionnaireRequestDTO.class);
            if (searchRequest.getTitle() == null || searchRequest.getTitle().trim().isEmpty()) {
                ApiResponse<Object> apiResponse = ApiResponse.error("缺少标题");
                sendApiResponse(response, apiResponse);
                return;
            }
            String title = searchRequest.getTitle().trim();
            // 查找问卷
            List<Questionnaire> questionnaires = questionnaireService.findByTitleLike(title);
            if (questionnaires.isEmpty()) {
                ApiResponse<Object> apiResponse = ApiResponse.error("没有找到匹配的问卷");
                sendApiResponse(response, apiResponse);
                return;
            }
            List<QuestionnaireResponseDTO> responses = questionnaires.stream()
                    .map(QuestionnaireResponseDTO::fromEntitySimple)
                    .collect(Collectors.toList());
            ApiResponse<List<QuestionnaireResponseDTO>> apiResponse = ApiResponse.success("问卷查找成功", responses);
            sendApiResponse(response, apiResponse);
        } catch (Exception e) {
            log.error("根据标题模糊查找问卷失败", e);
            sendErrorResponse(response, 500, "根据标题模糊查找问卷失败: " + e.getMessage(), "/api/questionnaire/search");
        }
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
            QuestionnaireRequestDTO createRequest = parseRequestBody(request, QuestionnaireRequestDTO.class);
            
            // 验证请求数据
            if (!createRequest.isValidForCreateQuestionnaire()) {
                ApiResponse<Object> apiResponse = ApiResponse.error("请求数据不完整");
                sendApiResponse(response, apiResponse);
                return;
            }
            
            // 创建问卷
            Questionnaire questionnaire = Questionnaire.builder()
                .title(createRequest.getTitle())
                .description(createRequest.getDescription())
                .creatorId(user.getUserId())
                .isPublished(false)
                .questions(createRequest.getQuestions() != null ?
                    createRequest.getQuestions().stream()
                        .map((QuestionRequestDTO question) -> question.toEntity())
                        .collect(Collectors.toList()) : null)
                .build();

            Questionnaire createdQuestionnaire = questionnaireService.createQuestionnaire(questionnaire);
            
            QuestionnaireResponseDTO questionnaireDTO = QuestionnaireResponseDTO.fromEntity(createdQuestionnaire);
            ApiResponse<QuestionnaireResponseDTO> apiResponse = ApiResponse.success("问卷创建成功", questionnaireDTO);
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
            QuestionnaireRequestDTO updateRequest = parseRequestBody(request, QuestionnaireRequestDTO.class);
            
            // 验证请求数据
            if (!updateRequest.isValidForUpdateQuestionnaire()) {
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
            
            QuestionnaireResponseDTO questionnaireDTO = QuestionnaireResponseDTO.fromEntity(updatedQuestionnaire);
            ApiResponse<QuestionnaireResponseDTO> apiResponse = ApiResponse.success("问卷更新成功", questionnaireDTO);
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
            
            // 只支持从请求体获取删除请求
            QuestionnaireRequestDTO deleteRequest = parseRequestBody(request, QuestionnaireRequestDTO.class);
            
            if (!deleteRequest.isValidForDeleteQuestionnaire()) {
                ApiResponse<Object> apiResponse = ApiResponse.error("缺少问卷ID");
                sendApiResponse(response, apiResponse);
                return;
            }
            
            Integer id = deleteRequest.getQuestionnaireId();
            
            // 检查问卷是否存在
            Optional<Questionnaire> questionnaireOpt = questionnaireService.findById(id);
            if (questionnaireOpt.isEmpty()) {
                ApiResponse<Object> apiResponse = ApiResponse.error("问卷不存在");
                sendApiResponse(response, apiResponse);
                return;
            }
            
            // 级联删除问卷
            if (!questionnaireService.deleteQuestionnaireWithCascade(id)) {
                ApiResponse<Object> apiResponse = ApiResponse.error("删除失败，可能存在关联数据或数据库错误");
                sendApiResponse(response, apiResponse);
                return;
            }
            
            ApiResponse<String> apiResponse = ApiResponse.success("问卷删除成功", "问卷删除成功");
            sendApiResponse(response, apiResponse);
            
        } catch (Exception e) {
            log.error("删除问卷失败", e);
            sendErrorResponse(response, 500, "删除问卷失败: " + e.getMessage(), "/api/questionnaire");
        }
    }

    /**
     * 发布问卷
     */
    @Route(value = "/publish", method = "POST")
    public void publishQuestionnaire(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int questionnaireId = getQuestionnaireId(request, response);
            if (questionnaireId == -1) return;

            // 发布问卷
            boolean success = questionnaireService.publishQuestionnaire(questionnaireId);
            if (!success) {
                ApiResponse<Object> apiResponse = ApiResponse.error("发布问卷失败");
                sendApiResponse(response, apiResponse);
                return;
            }

            ApiResponse<String> apiResponse = ApiResponse.success("问卷发布成功", "问卷发布成功");
            sendApiResponse(response, apiResponse);
        } catch (Exception e) {
            log.error("发布问卷失败", e);
            sendErrorResponse(response, 500, "发布问卷失败: " + e.getMessage(), "/api/questionnaire/publish");
        }
    }

    /**
     * 撤销发布问卷
     */
    @Route(value = "/unpublish", method = "POST")
    public void unpublishQuestionnaire(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int questionnaireId = getQuestionnaireId(request, response);
            if (questionnaireId == -1) return;

            // 撤销发布问卷
            boolean success = questionnaireService.unpublishQuestionnaire(questionnaireId);
            if (!success) {
                ApiResponse<Object> apiResponse = ApiResponse.error("撤销发布问卷失败");
                sendApiResponse(response, apiResponse);
                return;
            }

            ApiResponse<String> apiResponse = ApiResponse.success("问卷撤销发布成功", "问卷撤销发布成功");
            sendApiResponse(response, apiResponse);
        } catch (Exception e) {
            log.error("撤销发布问卷失败", e);
            sendErrorResponse(response, 500, "撤销发布问卷失败: " + e.getMessage(), "/api/questionnaire/unpublish");
        }
    }

    /**
     * 辅助发法，获取问卷Id并验证管理员权限
     */
    private Integer getQuestionnaireId(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!AuthUtils.checkHttpMethod(request, response, this, "POST")) return -1;

        // 检查管理员权限
        User user = AuthUtils.checkAdmin(request, response, this);
        if (user == null) return -1;

        // 从请求体获取问卷ID
        QuestionnaireRequestDTO unpublishRequest = parseRequestBody(request, QuestionnaireRequestDTO.class);

        if (unpublishRequest.getQuestionnaireId() == null) {
            ApiResponse<Object> apiResponse = ApiResponse.error("缺少问卷ID");
            sendApiResponse(response, apiResponse);
            return -1;
        }
        return unpublishRequest.getQuestionnaireId();
    }

    /**
     * 获取问卷详情
     */
    @Route(value = "/detail", method = "POST")
    public void getQuestionnaireDetail(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (!AuthUtils.checkHttpMethod(request, response, this, "POST")) return;

            // 从请求体获取问卷ID
            QuestionnaireRequestDTO detailRequest = parseRequestBody(request, QuestionnaireRequestDTO.class);
            if (detailRequest.getQuestionnaireId() == null) {
                ApiResponse<Object> apiResponse = ApiResponse.error("缺少问卷ID");
                sendApiResponse(response, apiResponse);
                return;
            }
            Integer questionnaireId = detailRequest.getQuestionnaireId();
            // 获取问卷详情
            Optional<Questionnaire> questionnaireOpt = questionnaireService.getQuestionnaireDetail(questionnaireId);
            if (questionnaireOpt.isEmpty()) {
                ApiResponse<Object> apiResponse = ApiResponse.error("问卷不存在");
                sendApiResponse(response, apiResponse);
                return;
            }
            Questionnaire questionnaire = questionnaireOpt.get();
            QuestionnaireResponseDTO questionnaireDetailDTO = QuestionnaireResponseDTO.fromEntity(questionnaire);
            ApiResponse<QuestionnaireResponseDTO> apiResponse = ApiResponse.success("获取问卷详情成功", questionnaireDetailDTO);
            sendApiResponse(response, apiResponse);
        } catch (Exception e) {
            log.error("获取问卷详情失败", e);
            sendErrorResponse(response, 500, "获取问卷详情失败: " + e.getMessage(), "/api/questionnaire/detail");
        }
    }
}
