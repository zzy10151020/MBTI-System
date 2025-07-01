package org.frostedstar.mbtisystem.controller;

import org.frostedstar.mbtisystem.dto.AnswerSubmitDTO;
import org.frostedstar.mbtisystem.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

/**
 * 测试控制器
 * 处理问卷答题和结果相关功能
 */
@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*")
public class TestController {

    @Autowired
    private TestService testService;

    /**
     * 提交问卷答案
     */
    @PostMapping("/submit")
    public ResponseEntity<?> submitAnswers(@Valid @RequestBody AnswerSubmitDTO answerSubmitDTO) {
        try {
            Map<String, Object> result = testService.submitAnswers(answerSubmitDTO);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", result,
                "message", "答案提交成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "答案提交失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 获取用户的测试结果
     */
    @GetMapping("/results")
    public ResponseEntity<?> getUserTestResults(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Map<String, Object> results = testService.getUserTestResults(page, size);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", results,
                "message", "获取测试结果成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "获取测试结果失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 获取指定问卷的测试结果
     */
    @GetMapping("/results/{questionnaireId}")
    public ResponseEntity<?> getQuestionnaireResult(@PathVariable Long questionnaireId) {
        try {
            Map<String, Object> result = testService.getQuestionnaireResult(questionnaireId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", result,
                "message", "获取测试结果成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "获取测试结果失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 获取MBTI测试报告
     */
    @GetMapping("/report/{answerId}")
    public ResponseEntity<?> getMbtiReport(@PathVariable Long answerId) {
        try {
            Map<String, Object> report = testService.generateMbtiReport(answerId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", report,
                "message", "获取MBTI报告成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "获取MBTI报告失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 重新计算测试结果
     */
    @PutMapping("/recalculate/{answerId}")
    public ResponseEntity<?> recalculateResult(@PathVariable Long answerId) {
        try {
            Map<String, Object> result = testService.recalculateResult(answerId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", result,
                "message", "重新计算结果成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "重新计算结果失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 获取测试统计（管理员权限）
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getTestStatistics() {
        try {
            Map<String, Object> statistics = testService.getTestStatistics();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", statistics,
                "message", "获取测试统计成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "获取测试统计失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 获取问卷答题统计（管理员权限）
     */
    @GetMapping("/statistics/{questionnaireId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getQuestionnaireStatistics(@PathVariable Long questionnaireId) {
        try {
            Map<String, Object> statistics = testService.getQuestionnaireTestStatistics(questionnaireId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", statistics,
                "message", "获取问卷统计成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "获取问卷统计失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 删除测试记录（管理员权限）
     */
    @DeleteMapping("/answers/{answerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteTestRecord(@PathVariable Long answerId) {
        try {
            testService.deleteTestRecord(answerId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "删除测试记录成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "删除测试记录失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 获取用户答题历史（管理员权限）
     */
    @GetMapping("/history/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUserTestHistory(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Map<String, Object> history = testService.getUserTestHistory(userId, page, size);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", history,
                "message", "获取用户答题历史成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "获取用户答题历史失败: " + e.getMessage()
            ));
        }
    }
}
