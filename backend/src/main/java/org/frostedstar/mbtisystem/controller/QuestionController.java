package org.frostedstar.mbtisystem.controller;

import lombok.extern.slf4j.Slf4j;
import org.frostedstar.mbtisystem.dto.questiondto.*;
import org.frostedstar.mbtisystem.dto.ApiResponse;
import org.frostedstar.mbtisystem.entity.Question;
import org.frostedstar.mbtisystem.entity.User;
import org.frostedstar.mbtisystem.service.QuestionService;
import org.frostedstar.mbtisystem.service.ServiceFactory;
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
            
            // 获取所有问题
            fetchAllQuestions(response);
        } catch (Exception e) {
            log.error("获取问题列表失败", e);
            sendErrorResponse(response, 500, "获取问题列表失败: " + e.getMessage(), "/api/question");
        }
    }

    private void fetchAllQuestions(HttpServletResponse response) throws IOException {
        List<Question> questions = questionService.findAll();
        List<QuestionResponseDTO> questionResponseDTOS = questions.stream()
            .map(QuestionResponseDTO::fromEntity)
            .collect(Collectors.toList());

        ApiResponse<List<QuestionResponseDTO>> apiResponse = ApiResponse.success("获取问题列表成功", questionResponseDTOS);
        sendApiResponse(response, apiResponse);
    }

    /**
     * 根据问卷ID获取问题列表
     */
    private void fetchQuestionsByQuestionnaireId(Integer questionnaireId, HttpServletResponse response) throws IOException {
        List<Question> questions = questionService.findByQuestionnaireId(questionnaireId);
        List<QuestionResponseDTO> questionResponseDTOS = questions.stream()
            .map(QuestionResponseDTO::fromEntity)
            .collect(Collectors.toList());

        ApiResponse<List<QuestionResponseDTO>> apiResponse = ApiResponse.success("获取问卷问题列表成功", questionResponseDTOS);
        sendApiResponse(response, apiResponse);
    }

    /**
     * 根据问卷ID获取问题列表
     */
    @Route(value = "/byQuestionnaire", method = "POST")
    public void getQuestionsByQuestionnaireId(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (!AuthUtils.checkHttpMethod(request, response, this, "POST")) return;

            // 解析请求体
            QuestionRequestDTO queryRequest = parseRequestBody(request, QuestionRequestDTO.class);
            
            // 验证请求数据
            if (!queryRequest.isValidForQuestionnaireQuery()) {
                ApiResponse<Object> apiResponse = ApiResponse.error("缺少或无效的问卷ID");
                sendApiResponse(response, apiResponse);
                return;
            }

            fetchQuestionsByQuestionnaireId(queryRequest.getQuestionnaireId(), response);
        } catch (Exception e) {
            log.error("根据问卷ID获取问题列表失败", e);
            sendErrorResponse(response, 500, "根据问卷ID获取问题列表失败: " + e.getMessage(), "/api/question/by-questionnaire");
        }
    }

    /**
     * 根据维度查找问题
     */
    @Route(value = "/by-dimension", method = "POST")
    public void getQuestionsByDimension(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (!AuthUtils.checkHttpMethod(request, response, this, "POST")) return;

            // 解析请求体
            QuestionRequestDTO queryRequest = parseRequestBody(request, QuestionRequestDTO.class);
            
            // 验证请求数据
            if (!queryRequest.isValidForDimensionQuery()) {
                ApiResponse<Object> apiResponse = ApiResponse.error("缺少或无效的维度参数");
                sendApiResponse(response, apiResponse);
                return;
            }

            Question.Dimension dimension;
            try {
                dimension = Question.Dimension.valueOf(queryRequest.getDimension().toUpperCase());
            } catch (IllegalArgumentException e) {
                ApiResponse<Object> apiResponse = ApiResponse.error("无效的维度参数");
                sendApiResponse(response, apiResponse);
                return;
            }

            List<Question> questions = questionService.findByDimension(dimension);
            List<QuestionResponseDTO> questionResponseDTOS = questions.stream()
                .map(QuestionResponseDTO::fromEntity)
                .collect(Collectors.toList());

            ApiResponse<List<QuestionResponseDTO>> apiResponse = ApiResponse.success("获取维度问题列表成功", questionResponseDTOS);
            sendApiResponse(response, apiResponse);
        } catch (Exception e) {
            log.error("根据维度获取问题列表失败", e);
            sendErrorResponse(response, 500, "根据维度获取问题列表失败: " + e.getMessage(), "/api/question/by-dimension");
        }
    }

    /**
     * 获取问题详情
     */
    @Route(value = "/detail", method = "POST")
    public void getQuestionDetail(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (!AuthUtils.checkHttpMethod(request, response, this, "POST")) return;

            // 解析请求体
            QuestionRequestDTO queryRequest = parseRequestBody(request, QuestionRequestDTO.class);
            
            // 验证请求数据
            if (!queryRequest.isValidForDetailQuery()) {
                ApiResponse<Object> apiResponse = ApiResponse.error("缺少或无效的问题ID");
                sendApiResponse(response, apiResponse);
                return;
            }

            Question question = questionService.getQuestionDetail(queryRequest.getActualQuestionId());
            if (question == null) {
                ApiResponse<Object> apiResponse = ApiResponse.error("问题不存在");
                sendApiResponse(response, apiResponse);
                return;
            }

            QuestionResponseDTO questionResponseDTO = QuestionResponseDTO.fromEntity(question);
            ApiResponse<QuestionResponseDTO> apiResponse = ApiResponse.success("获取问题详情成功", questionResponseDTO);
            sendApiResponse(response, apiResponse);
        } catch (Exception e) {
            log.error("获取问题详情失败", e);
            sendErrorResponse(response, 500, "获取问题详情失败: " + e.getMessage(), "/api/question/detail");
        }
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
            QuestionRequestDTO createRequest = parseRequestBody(request, QuestionRequestDTO.class);
            
            // 验证请求数据
            if (!createRequest.isValidForCreateQuestion()) {
                ApiResponse<Object> apiResponse = ApiResponse.error("请求数据不完整");
                sendApiResponse(response, apiResponse);
                return;
            }

            if (createRequest.getOptions() == null) {
                ApiResponse<Object> apiResponse = ApiResponse.error("选项列表不能为空");
                sendApiResponse(response, apiResponse);
                return;
            }

            // 创建问题
            Question question = createRequest.toEntity();
            
            Question savedQuestion = questionService.save(question);
            
            QuestionResponseDTO questionResponseDTO = QuestionResponseDTO.fromEntity(savedQuestion);
            ApiResponse<QuestionResponseDTO> apiResponse = ApiResponse.success("问题创建成功", questionResponseDTO);
            sendApiResponse(response, apiResponse);
        } catch (Exception e) {
            log.error("创建问题失败", e);
            sendErrorResponse(response, 500, "创建问题失败: " + e.getMessage(), "/api/question");
        }
    }

    /**
     * 批量创建问题
     */
    @Route(value = "/batch", method = "POST")
    public void createQuestionsBatch(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (!AuthUtils.checkHttpMethod(request, response, this, "POST")) return;

            // 检查管理员权限
            User user = AuthUtils.checkAdmin(request, response, this);
            if (user == null) return;

            // 使用改进的解析方法
            List<QuestionRequestDTO> createRequests;
            try {
                createRequests = parseRequestBodyList(request, QuestionRequestDTO.class);
            } catch (Exception e) {
                log.error("解析批量创建问题请求失败", e);
                ApiResponse<Object> apiResponse = ApiResponse.error("请求数据格式错误");
                sendApiResponse(response, apiResponse);
                return;
            }

            if (createRequests == null || createRequests.isEmpty()) {
                ApiResponse<Object> apiResponse = ApiResponse.error("请求数据不能为空");
                sendApiResponse(response, apiResponse);
                return;
            }

            // 验证每个请求数据
            for (int i = 0; i < createRequests.size(); i++) {
                QuestionRequestDTO createRequest = createRequests.get(i);
                if (createRequest == null) {
                    ApiResponse<Object> apiResponse = ApiResponse.error("第" + (i + 1) + "个问题数据为空");
                    sendApiResponse(response, apiResponse);
                    return;
                }
                if (!createRequest.isValidForCreateQuestion()) {
                    ApiResponse<Object> apiResponse = ApiResponse.error("第" + (i + 1) + "个问题数据不完整");
                    sendApiResponse(response, apiResponse);
                    return;
                }
                if (createRequest.getOptions() == null || createRequest.getOptions().isEmpty()) {
                    ApiResponse<Object> apiResponse = ApiResponse.error("第" + (i + 1) + "个问题的选项列表不能为空");
                    sendApiResponse(response, apiResponse);
                    return;
                }
            }

            // 批量创建问题
            List<Question> questions = createRequests.stream()
                .map(dto -> dto.toEntity())
                .collect(Collectors.toList());

            List<Question> savedQuestions = questionService.createQuestions(questions);
            List<QuestionResponseDTO> questionResponseDTOS = savedQuestions.stream()
                .map(QuestionResponseDTO::fromEntity)
                .collect(Collectors.toList());

            ApiResponse<List<QuestionResponseDTO>> apiResponse = ApiResponse.success("批量创建问题成功", questionResponseDTOS);
            sendApiResponse(response, apiResponse);
        } catch (Exception e) {
            log.error("批量创建问题失败", e);
            sendErrorResponse(response, 500, "批量创建问题失败: " + e.getMessage(), "/api/question/batch");
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
            QuestionRequestDTO updateRequest = parseRequestBody(request, QuestionRequestDTO.class);
            
            // 验证请求数据
            if (!updateRequest.isValidForUpdateQuestion()) {
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
            
            QuestionResponseDTO questionResponseDTO = QuestionResponseDTO.fromEntity(updatedQuestion);
            ApiResponse<QuestionResponseDTO> apiResponse = ApiResponse.success("问题更新成功", questionResponseDTO);
            sendApiResponse(response, apiResponse);
        } catch (Exception e) {
            log.error("更新问题失败", e);
            sendErrorResponse(response, 500, "更新问题失败: " + e.getMessage(), "/api/question");
        }
    }
    
    /**
     * 删除问题 - 使用请求体传参
     */
    @Route(value = "", method = "DELETE")
    public void deleteQuestion(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (!AuthUtils.checkHttpMethod(request, response, this, "DELETE")) return;
            
            // 检查管理员权限
            User user = AuthUtils.checkAdmin(request, response, this);
            if (user == null) return;
            
            // 从请求体获取删除请求
            QuestionRequestDTO deleteRequest = parseRequestBody(request, QuestionRequestDTO.class);
            
            // 验证请求数据
            if (!deleteRequest.isValidForDeleteQuestion()) {
                ApiResponse<Object> apiResponse = ApiResponse.error("缺少或无效的问题ID");
                sendApiResponse(response, apiResponse);
                return;
            }
            
            Integer id = deleteRequest.getActualId();
            
            // 检查问题是否存在
            Optional<Question> questionOpt = questionService.findById(id);
            if (questionOpt.isEmpty()) {
                ApiResponse<Object> apiResponse = ApiResponse.error("问题不存在");
                sendApiResponse(response, apiResponse);
                return;
            }
            
            // 删除问题
            if (!questionService.deleteQuestionWithCascade(id)) {
                ApiResponse<Object> apiResponse = ApiResponse.error("删除失败，可能存在关联数据或数据库错误");
                sendApiResponse(response, apiResponse);
                return;
            }
            
            ApiResponse<String> apiResponse = ApiResponse.success("问题删除成功", "问题删除成功");
            sendApiResponse(response, apiResponse);
        } catch (Exception e) {
            log.error("删除问题失败", e);
            sendErrorResponse(response, 500, "删除问题失败: " + e.getMessage(), "/api/question");
        }
    }
}
