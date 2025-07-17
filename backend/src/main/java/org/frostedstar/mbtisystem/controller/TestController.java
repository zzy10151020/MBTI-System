package org.frostedstar.mbtisystem.controller;

import lombok.extern.slf4j.Slf4j;
import org.frostedstar.mbtisystem.dto.TestDTO;
import org.frostedstar.mbtisystem.dto.OperationType;
import org.frostedstar.mbtisystem.entity.Answer;
import org.frostedstar.mbtisystem.entity.AnswerDetail;
import org.frostedstar.mbtisystem.entity.User;
import org.frostedstar.mbtisystem.service.TestService;
import org.frostedstar.mbtisystem.service.ServiceFactory;
import org.frostedstar.mbtisystem.dto.ApiResponse;
import org.frostedstar.mbtisystem.dto.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
     * 开始测试/获取测试结果
     */
    public void get(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (!AuthUtils.checkHttpMethod(request, response, this, "GET")) return;
            
            // 检查用户是否已登录
            User user = AuthUtils.checkLogin(request, response, this);
            if (user == null) return;
            
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/test") || pathInfo.equals("/test/")) {
                // 获取用户的测试结果列表
                fetchUserTestResultList(response, user);
            } else {
                // 尝试解析测试ID
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length >= 3 && "test".equals(pathParts[2])) {
                    if (pathParts.length >= 4) {
                        try {
                            Integer answerId = Integer.parseInt(pathParts[3]);
                            Optional<Answer> answer = testService.getTestResultDetail(answerId);
                            if (answer.isPresent() && answer.get().getUserId().equals(user.getUserId())) {
                                TestDTO testDTO = TestDTO.fromEntity(answer.get());
                                ApiResponse<TestDTO> apiResponse = ApiResponse.success("获取测试详情成功", testDTO);
                                sendApiResponse(response, apiResponse);
                            } else {
                                ApiResponse<Object> apiResponse = ApiResponse.error("测试结果不存在");
                                sendApiResponse(response, apiResponse);
                            }
                        } catch (NumberFormatException e) {
                            ApiResponse<Object> apiResponse = ApiResponse.error("无效的测试ID");
                            sendApiResponse(response, apiResponse);
                        }
                    } else {
                        // 没有提供测试ID，返回用户的所有测试结果
                        fetchUserTestResultList(response, user);
                    }
                } else {
                    ApiResponse<Object> apiResponse = ApiResponse.error("接口不存在");
                    sendApiResponse(response, apiResponse);
                }
            }
        } catch (Exception e) {
            log.error("获取测试结果失败", e);
            ErrorResponse errorResponse = ErrorResponse.create(
                e.getClass().getSimpleName(),
                "获取测试结果失败: " + e.getMessage(),
                500,
                "/api/test"
            );
            ApiResponse<ErrorResponse> apiResponse = ApiResponse.systemError(errorResponse);
            sendApiResponse(response, apiResponse);
        }
    }

    private void fetchUserTestResultList(HttpServletResponse response, User user) throws IOException {
        List<Answer> answers = testService.getUserAllTestResults(user.getUserId());
        List<TestDTO> testDTOs = answers.stream()
            .map(TestDTO::fromEntity)
            .collect(Collectors.toList());

        ApiResponse<List<TestDTO>> apiResponse = ApiResponse.success("获取测试结果成功", testDTOs);
        sendApiResponse(response, apiResponse);
    }

    /**
     * 提交测试结果
     */
    public void post(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (!AuthUtils.checkHttpMethod(request, response, this, "POST")) return;
            
            // 检查用户是否已登录
            User user = AuthUtils.checkLogin(request, response, this);
            if (user == null) return;
            
            // 解析请求体
            TestDTO testRequest = parseRequestBody(request, TestDTO.class);
            testRequest.setOperationType(OperationType.CREATE);
            testRequest.setUserId(user.getUserId());
            
            // 验证请求数据
            if (!testRequest.isValid()) {
                ApiResponse<Object> apiResponse = ApiResponse.error("请求数据不完整");
                sendApiResponse(response, apiResponse);
                return;
            }
            
            // 转换为AnswerDetail对象
            List<AnswerDetail> answerDetails = new ArrayList<>();
            for (TestDTO.AnswerDetailDTO answerDetailDTO : testRequest.getAnswerDetails()) {
                AnswerDetail detail = AnswerDetail.builder()
                    .questionId(answerDetailDTO.getQuestionId())
                    .optionId(answerDetailDTO.getOptionId())
                    .build();
                answerDetails.add(detail);
            }
            
            // 提交测试结果
            Answer answer = testService.submitTest(user.getUserId(), testRequest.getQuestionnaireId(), answerDetails);
            
            TestDTO testDTO = TestDTO.fromEntity(answer);
            ApiResponse<TestDTO> apiResponse = ApiResponse.success("测试提交成功", testDTO);
            sendApiResponse(response, apiResponse);
        } catch (Exception e) {
            log.error("提交测试结果失败", e);
            ErrorResponse errorResponse = ErrorResponse.create(
                e.getClass().getSimpleName(),
                "提交测试结果失败: " + e.getMessage(),
                500,
                "/api/test"
            );
            ApiResponse<ErrorResponse> apiResponse = ApiResponse.systemError(errorResponse);
            sendApiResponse(response, apiResponse);
        }
    }
}
