package org.frostedstar.mbtisystem.controller;

import lombok.extern.slf4j.Slf4j;
import org.frostedstar.mbtisystem.dto.testdto.TestRequestDTO;
import org.frostedstar.mbtisystem.dto.testdto.TestResponseDTO;
import org.frostedstar.mbtisystem.entity.Answer;
import org.frostedstar.mbtisystem.entity.AnswerDetail;
import org.frostedstar.mbtisystem.entity.User;
import org.frostedstar.mbtisystem.service.TestService;
import org.frostedstar.mbtisystem.service.ServiceFactory;
import org.frostedstar.mbtisystem.dto.ApiResponse;
import org.frostedstar.mbtisystem.servlet.Route;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 测试控制器
 */
@Slf4j
public class TestController extends BaseController {
    
    private final TestService testService;
    
    public TestController() {
        this.testService = ServiceFactory.getTestService();
    }

    /**
     * 获取测试结果
     */
    @Route(value = "", method = "GET")
    public void getTestResults(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (!AuthUtils.checkHttpMethod(request, response, this, "GET")) return;
            
            // 检查用户是否已登录
            User user = AuthUtils.checkLogin(request, response, this);
            if (user == null) return;
            
            // 从完整的URI中提取路径信息
            String requestURI = request.getRequestURI();
            String contextPath = request.getContextPath();
            String servletPath = request.getServletPath();
            
            // 计算实际的路径信息: requestURI - contextPath - servletPath - "/test"
            String fullPath = requestURI.substring(contextPath.length() + servletPath.length());
            String pathInfo = fullPath.substring("/test".length());
            
            log.debug("TestController.getTestResults - requestURI: {}, contextPath: {}, servletPath: {}, fullPath: {}, pathInfo: {}", 
                requestURI, contextPath, servletPath, fullPath, pathInfo);
            
            if (pathInfo == null || pathInfo.equals("/") || pathInfo.isEmpty()) {
                ApiResponse<Object> apiResponse = ApiResponse.error("请提供问卷ID，或使用 /api/test/all 等端点");
                sendApiResponse(response, apiResponse);
            } else {
                // 尝试解析问卷ID - pathInfo 应该是 "/1", "/2" 这样的格式
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length >= 2 && !pathParts[1].isEmpty()) {
                    try {
                        Integer questionnaireId = Integer.parseInt(pathParts[1]); // pathParts[0] 是空字符串
                        Optional<Answer> answer = testService.getUserTestResult(user.getUserId(), questionnaireId);
                        if (answer.isPresent()) {
                            Answer testAnswer = answer.get();
                            
                            // 计算MBTI结果
                            String mbtiResult = null;
                            if (testAnswer.getDetails() != null && !testAnswer.getDetails().isEmpty()) {
                                mbtiResult = testService.calculateMBTIResult(testAnswer.getDetails());
                            }
                            
                            // 在Controller层使用Service计算结果
                            Map<String, String> dimensions = testService.calculateDimensions(mbtiResult);
                            Map<String, Object> statistics = testService.calculateDimensionStatistics(testAnswer.getDetails());
                            Map<String, Double> personalityProbabilities = testService.calculatePersonalityProbabilities(testAnswer.getDetails());
                            
                            // 创建增强的TestResponseDTO，包含所有必需字段
                            TestResponseDTO testDTO = TestResponseDTO.fromEntityWithDetails(testAnswer, mbtiResult, 
                                dimensions, statistics, personalityProbabilities);
                            ApiResponse<TestResponseDTO> apiResponse = ApiResponse.success("获取测试详情成功", testDTO);
                            sendApiResponse(response, apiResponse);
                        } else {
                            ApiResponse<Object> apiResponse = ApiResponse.error("测试结果不存在");
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
            log.error("获取测试结果失败", e);
            sendErrorResponse(response, 500, "获取测试结果失败: " + e.getMessage(), "/api/test");
        }
    }

    /**
     * 获取用户的所有测试结果
     */
    @Route(value = "/all", method = "GET")
    public void getUserTestResults(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (!AuthUtils.checkHttpMethod(request, response, this, "GET")) return;

            // 检查用户是否已登录
            User user = AuthUtils.checkLogin(request, response, this);
            if (user == null) return;

            List<Answer> answers = testService.getUserAllTestResults(user.getUserId());
            List<TestResponseDTO> testDTOs = answers.stream()
                    .map(answer -> {
                        // 计算每个答案的MBTI结果
                        String mbtiResult = null;
                        if (answer.getDetails() != null && !answer.getDetails().isEmpty()) {
                            mbtiResult = testService.calculateMBTIResult(answer.getDetails());
                        }
                        
                        // 在Controller层使用Service计算结果
                        Map<String, String> dimensions = testService.calculateDimensions(mbtiResult);
                        Map<String, Object> statistics = testService.calculateDimensionStatistics(answer.getDetails());
                        Map<String, Double> personalityProbabilities = testService.calculatePersonalityProbabilities(answer.getDetails());
                        
                        return TestResponseDTO.fromEntityWithDetails(answer, mbtiResult, 
                            dimensions, statistics, personalityProbabilities);
                    })
                    .collect(Collectors.toList());

            ApiResponse<List<TestResponseDTO>> apiResponse = ApiResponse.success("获取测试结果成功", testDTOs);
            sendApiResponse(response, apiResponse);
        } catch (Exception e) {
            log.error("获取用户测试结果失败", e);
            sendErrorResponse(response, 500, "获取用户测试结果失败: " + e.getMessage(), "/api/test/all");
        }
    }

    /**
     * 提交测试结果
     */
    @Route(value = "", method = "POST")
    public void submitTest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (!AuthUtils.checkHttpMethod(request, response, this, "POST")) return;
            
            // 检查用户是否已登录
            User user = AuthUtils.checkLogin(request, response, this);
            if (user == null) return;
            
            // 解析请求体
            TestRequestDTO testRequest = parseRequestBody(request, TestRequestDTO.class);
            
            // 验证请求数据
            if (!testRequest.isValidForSubmitTest()) {
                ApiResponse<Object> apiResponse = ApiResponse.error("请求数据不完整");
                sendApiResponse(response, apiResponse);
                return;
            }
            
            // 转换为AnswerDetail对象
            List<AnswerDetail> answerDetails = new ArrayList<>();
            for (TestRequestDTO.AnswerDetailRequestDTO answerDetailDTO : testRequest.getAnswerDetails()) {
                AnswerDetail detail = AnswerDetail.builder()
                    .questionId(answerDetailDTO.getQuestionId())
                    .optionId(answerDetailDTO.getOptionId())
                    .build();
                answerDetails.add(detail);
            }
            
            // 提交测试结果
            Answer answer = testService.submitTest(user.getUserId(), testRequest.getQuestionnaireId(), answerDetails);
            
            // 计算MBTI结果
            String mbtiResult = testService.calculateMBTIResult(answerDetails);
            
            // 在Controller层使用Service计算结果
            Map<String, String> dimensions = testService.calculateDimensions(mbtiResult);
            Map<String, Object> statistics = testService.calculateDimensionStatistics(answerDetails);
            Map<String, Double> personalityProbabilities = testService.calculatePersonalityProbabilities(answerDetails);
            
            TestResponseDTO testDTO = TestResponseDTO.fromEntityWithDetails(answer, mbtiResult, 
                dimensions, statistics, personalityProbabilities);
            ApiResponse<TestResponseDTO> apiResponse = ApiResponse.success("测试提交成功", testDTO);
            sendApiResponse(response, apiResponse);
        } catch (Exception e) {
            log.error("提交测试结果失败", e);
            sendErrorResponse(response, 500, "提交测试结果失败: " + e.getMessage(), "/api/test");
        }
    }

    /**
     * 检查用户是否已完成测试
     */
    @Route(value = "/completed", method = "POST")
    public void checkUserCompletedTest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (!AuthUtils.checkHttpMethod(request, response, this, "POST")) return;

            // 检查用户是否已登录
            User user = AuthUtils.checkLogin(request, response, this);
            if (user == null) return;

            // 解析请求体
            TestRequestDTO testRequest = parseRequestBody(request, TestRequestDTO.class);
            // 验证请求数据
            if (!testRequest.isValidForCheckUserCompleted()) {
                ApiResponse<Object> apiResponse = ApiResponse.error("请求数据不完整");
                sendApiResponse(response, apiResponse);
                return;
            }
            // 检查用户是否已完成测试
            boolean completed = testService.hasUserCompletedTest(user.getUserId(), testRequest.getQuestionnaireId());
            ApiResponse<Map<String, Boolean>> apiResponse = ApiResponse.success("检查用户完成状态成功", Map.of("completed", completed));
            sendApiResponse(response, apiResponse);

        } catch (Exception e) {
            log.error("检查用户完成状态失败", e);
            sendErrorResponse(response, 500, "检查用户完成状态失败: " + e.getMessage(), "/api/test/completed");
        }
    }

    /**
     * 获取问卷统计数据
     */
    @Route(value = "/statistics", method = "POST")
    public void getQuestionnaireStatistics(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (!AuthUtils.checkHttpMethod(request, response, this, "POST")) return;

            // 检查是否为管理员（统计数据只有管理员能查看）
            User user = AuthUtils.checkAdmin(request, response, this);
            if (user == null) return;

            // 解析请求体
            TestRequestDTO testRequest = parseRequestBody(request, TestRequestDTO.class);

            // 验证请求数据
            if (!testRequest.isValidForGetQuestionnaireStatistics()) {
                ApiResponse<Object> apiResponse = ApiResponse.error("请求数据不完整");
                sendApiResponse(response, apiResponse);
                return;
            }
            // 获取问卷统计数据
            Map<String, Object> statistics = testService.getQuestionnaireStatistics(testRequest.getQuestionnaireId());
            if (statistics == null || statistics.isEmpty()) {
                ApiResponse<Object> apiResponse = ApiResponse.error("没有统计数据");
                sendApiResponse(response, apiResponse);
                return;
            }
            ApiResponse<Map<String, Object>> apiResponse = ApiResponse.success("获取问卷统计数据成功", statistics);
            sendApiResponse(response, apiResponse);
        } catch (Exception e) {
            log.error("获取问卷统计数据失败", e);
            sendErrorResponse(response, 500, "获取问卷统计数据失败: " + e.getMessage(), "/api/test/statistics");
        }
    }
}
