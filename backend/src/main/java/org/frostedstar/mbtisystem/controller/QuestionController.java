package org.frostedstar.mbtisystem.controller;

import lombok.extern.slf4j.Slf4j;
import org.frostedstar.mbtisystem.dto.QuestionDTO;
import org.frostedstar.mbtisystem.dto.OperationType;
import org.frostedstar.mbtisystem.entity.Question;
import org.frostedstar.mbtisystem.entity.User;
import org.frostedstar.mbtisystem.service.QuestionService;
import org.frostedstar.mbtisystem.service.ServiceFactory;
import org.frostedstar.mbtisystem.dto.ApiResponse;
import org.frostedstar.mbtisystem.servlet.Route;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 问题控制器
 */
@Slf4j
public class QuestionController extends BaseController {
    
    private final QuestionService questionService;
    
    public QuestionController() {
        this.questionService = ServiceFactory.getQuestionService();
    }
    
    /**
     * 获取问题列表
     */
    @Route(value = "", method = "GET")
    public void getQuestions(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (!AuthUtils.checkHttpMethod(request, response, this, "GET")) return;
            
            // 检查是否有questionnaireId查询参数
            String questionnaireIdParam = request.getParameter("questionnaireId");
            if (questionnaireIdParam != null && !questionnaireIdParam.trim().isEmpty()) {
                try {
                    Integer questionnaireId = Integer.parseInt(questionnaireIdParam);
                    fetchQuestionsByQuestionnaireId(questionnaireId, response);
                } catch (NumberFormatException e) {
                    ApiResponse<Object> apiResponse = ApiResponse.error("无效的问卷ID");
                    sendApiResponse(response, apiResponse);
                }
                return;
            }
            
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/question") || pathInfo.equals("/question/")) {
                // 获取所有问题
                fetchAllQuestions(response);
            } else {
                // 尝试解析问题ID
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length >= 3 && "question".equals(pathParts[2])) {
                    if (pathParts.length >= 4) {
                        try {
                            Integer id = Integer.parseInt(pathParts[3]);
                            Optional<Question> question = questionService.findById(id);
                            if (question.isPresent()) {
                                QuestionDTO questionDTO = QuestionDTO.fromEntity(question.get());
                                ApiResponse<QuestionDTO> apiResponse = ApiResponse.success("获取问题成功", questionDTO);
                                sendApiResponse(response, apiResponse);
                            } else {
                                ApiResponse<Object> apiResponse = ApiResponse.error("问题不存在");
                                sendApiResponse(response, apiResponse);
                            }
                        } catch (NumberFormatException e) {
                            ApiResponse<Object> apiResponse = ApiResponse.error("无效的问题ID");
                            sendApiResponse(response, apiResponse);
                        }
                    } else {
                        // 如果没有提供ID，则返回所有问题
                        fetchAllQuestions(response);
                    }
                } else {
                    ApiResponse<Object> apiResponse = ApiResponse.error("接口不存在");
                    sendApiResponse(response, apiResponse);
                }
            }
        } catch (Exception e) {
            log.error("获取问题列表失败", e);
            sendErrorResponse(response, 500, "获取问题列表失败: " + e.getMessage(), "/api/question");
        }
    }

    private void fetchAllQuestions(HttpServletResponse response) throws IOException {
        List<Question> questions = questionService.findAll();
        List<QuestionDTO> questionDTOs = questions.stream()
            .map(QuestionDTO::fromEntity)
            .collect(Collectors.toList());

        ApiResponse<List<QuestionDTO>> apiResponse = ApiResponse.success("获取问题列表成功", questionDTOs);
        sendApiResponse(response, apiResponse);
    }

    /**
     * 根据问卷ID获取问题列表
     */
    private void fetchQuestionsByQuestionnaireId(Integer questionnaireId, HttpServletResponse response) throws IOException {
        List<Question> questions = questionService.findByQuestionnaireId(questionnaireId);
        List<QuestionDTO> questionDTOs = questions.stream()
            .map(QuestionDTO::fromEntity)
            .collect(Collectors.toList());

        ApiResponse<List<QuestionDTO>> apiResponse = ApiResponse.success("获取问卷问题列表成功", questionDTOs);
        sendApiResponse(response, apiResponse);
    }

    /**
     * 创建问题
     */
    @Route(value = "", method = "POST")
    public void createQuestion(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (!AuthUtils.checkHttpMethod(request, response, this, "POST")) return;
            
            // 检查管理员权限
            User user = AuthUtils.checkAdmin(request, response, this);
            if (user == null) return;
            
            // 解析请求体
            QuestionDTO createRequest = parseRequestBody(request, QuestionDTO.class);
            createRequest.setOperationType(OperationType.CREATE);
            
            // 验证请求数据
            if (!createRequest.isValid()) {
                ApiResponse<Object> apiResponse = ApiResponse.error("请求数据不完整");
                sendApiResponse(response, apiResponse);
                return;
            }
            
            // 创建问题
            Question question = Question.builder()
                .questionnaireId(createRequest.getQuestionnaireId())
                .content(createRequest.getContent())
                .dimension(Question.Dimension.valueOf(createRequest.getDimension()))
                .questionOrder(createRequest.getQuestionOrder())
                .build();
            
            Question savedQuestion = questionService.save(question);
            
            QuestionDTO questionDTO = QuestionDTO.fromEntity(savedQuestion);
            ApiResponse<QuestionDTO> apiResponse = ApiResponse.success("问题创建成功", questionDTO);
            sendApiResponse(response, apiResponse);
        } catch (Exception e) {
            log.error("创建问题失败", e);
            sendErrorResponse(response, 500, "创建问题失败: " + e.getMessage(), "/api/question");
        }
    }
    
    /**
     * 更新问题
     */
    @Route(value = "", method = "PUT")
    public void updateQuestion(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (!AuthUtils.checkHttpMethod(request, response, this, "PUT")) return;
            
            // 检查管理员权限
            User user = AuthUtils.checkAdmin(request, response, this);
            if (user == null) return;
            
            // 解析请求体
            QuestionDTO updateRequest = parseRequestBody(request, QuestionDTO.class);
            updateRequest.setOperationType(OperationType.UPDATE);
            
            // 验证请求数据
            if (!updateRequest.isValid()) {
                ApiResponse<Object> apiResponse = ApiResponse.error("请求数据不完整");
                sendApiResponse(response, apiResponse);
                return;
            }
            
            // 获取现有问题
            Optional<Question> existingQuestionOpt = questionService.findById(updateRequest.getQuestionId());
            if (existingQuestionOpt.isEmpty()) {
                ApiResponse<Object> apiResponse = ApiResponse.error("问题不存在");
                sendApiResponse(response, apiResponse);
                return;
            }
            
            Question existingQuestion = existingQuestionOpt.get();
            
            // 更新问题信息
            Question updatedQuestion = Question.builder()
                .questionId(updateRequest.getQuestionId())
                .questionnaireId(existingQuestion.getQuestionnaireId())
                .content(updateRequest.getContent())
                .dimension(Question.Dimension.valueOf(updateRequest.getDimension()))
                .questionOrder(updateRequest.getQuestionOrder())
                .build();
            
            questionService.update(updatedQuestion);
            
            QuestionDTO questionDTO = QuestionDTO.fromEntity(updatedQuestion);
            ApiResponse<QuestionDTO> apiResponse = ApiResponse.success("问题更新成功", questionDTO);
            sendApiResponse(response, apiResponse);
        } catch (Exception e) {
            log.error("更新问题失败", e);
            sendErrorResponse(response, 500, "更新问题失败: " + e.getMessage(), "/api/question");
        }
    }
    
    /**
     * 删除问题
     */
    @Route(value = "", method = "DELETE")
    public void deleteQuestion(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
                    ApiResponse<Object> apiResponse = ApiResponse.error("无效的问题ID");
                    sendApiResponse(response, apiResponse);
                    return;
                }
            } else {
                // 从请求体获取删除请求
                QuestionDTO deleteRequest = parseRequestBody(request, QuestionDTO.class);
                deleteRequest.setOperationType(OperationType.DELETE);
                
                if (!deleteRequest.isValid()) {
                    ApiResponse<Object> apiResponse = ApiResponse.error("缺少问题ID");
                    sendApiResponse(response, apiResponse);
                    return;
                }
                
                id = deleteRequest.getQuestionId();
            }
            
            // 检查问题是否存在
            Optional<Question> questionOpt = questionService.findById(id);
            if (questionOpt.isEmpty()) {
                ApiResponse<Object> apiResponse = ApiResponse.error("问题不存在");
                sendApiResponse(response, apiResponse);
                return;
            }
            
            // 删除问题
            questionService.deleteById(id);
            
            ApiResponse<String> apiResponse = ApiResponse.success("问题删除成功", "问题删除成功");
            sendApiResponse(response, apiResponse);
        } catch (Exception e) {
            log.error("删除问题失败", e);
            sendErrorResponse(response, 500, "删除问题失败: " + e.getMessage(), "/api/question");
        }
    }
}
