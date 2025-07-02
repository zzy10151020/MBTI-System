package org.frostedstar.mbtisystem.controller;

import lombok.RequiredArgsConstructor;
import org.frostedstar.mbtisystem.dto.QuestionDTO;
import org.frostedstar.mbtisystem.service.QuestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 问题管理控制器
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionService questionService;

    /**
     * 获取问卷的所有问题
     */
    @GetMapping("/questionnaire/{questionnaireId}")
    public ResponseEntity<?> getQuestionsByQuestionnaireId(@PathVariable Long questionnaireId) {
        try {
            List<QuestionDTO> questions = questionService.getQuestionsByQuestionnaireId(questionnaireId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", questions,
                "message", "获取问题列表成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "获取问题列表失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 根据ID获取问题详情
     */
    @GetMapping("/{questionId}")
    public ResponseEntity<?> getQuestionById(@PathVariable Long questionId) {
        try {
            QuestionDTO question = questionService.getQuestionById(questionId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", question,
                "message", "获取问题详情成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "获取问题详情失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 创建新问题（管理员权限）
     */
    @PostMapping("/questionnaire/{questionnaireId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createQuestion(@PathVariable Long questionnaireId, @Valid @RequestBody QuestionDTO questionDTO) {
        try {
            questionDTO.setQuestionnaireId(questionnaireId);
            QuestionDTO createdQuestion = questionService.createQuestion(questionDTO);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", createdQuestion,
                "message", "创建问题成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "创建问题失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 更新问题（管理员权限）
     */
    @PutMapping("/{questionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateQuestion(@PathVariable Long questionId, @Valid @RequestBody QuestionDTO questionDTO) {
        try {
            QuestionDTO updatedQuestion = questionService.updateQuestion(questionId, questionDTO);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", updatedQuestion,
                "message", "更新问题成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "更新问题失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 删除问题（管理员权限）
     */
    @DeleteMapping("/{questionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteQuestion(@PathVariable Long questionId) {
        try {
            questionService.deleteQuestion(questionId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "删除问题成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "删除问题失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 批量创建问题（管理员权限）
     */
    @PostMapping("/questionnaire/{questionnaireId}/batch")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createQuestionsBatch(@PathVariable Long questionnaireId, @Valid @RequestBody List<QuestionDTO> questionsDTO) {
        try {
            questionsDTO.forEach(q -> q.setQuestionnaireId(questionnaireId));
            List<QuestionDTO> createdQuestions = questionService.createQuestionsBatch(questionsDTO);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", createdQuestions,
                "message", "批量创建问题成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "批量创建问题失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 更新问题顺序（管理员权限）
     */
    @PutMapping("/questionnaire/{questionnaireId}/reorder")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> reorderQuestions(@PathVariable Long questionnaireId, @RequestBody Map<Long, Short> questionOrders) {
        try {
            questionService.reorderQuestions(questionOrders);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "更新问题顺序成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "更新问题顺序失败: " + e.getMessage()
            ));
        }
    }
}
