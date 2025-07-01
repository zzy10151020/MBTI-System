package org.frostedstar.mbtisystem.controller;

import org.frostedstar.mbtisystem.dto.QuestionnaireDTO;
import org.frostedstar.mbtisystem.service.QuestionnaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 问卷管理控制器
 */
@RestController
@RequestMapping("/api/questionnaires")
@CrossOrigin(origins = "*")
public class QuestionnaireController {

    @Autowired
    private QuestionnaireService questionnaireService;

    /**
     * 获取所有问卷列表
     */
    @GetMapping
    public ResponseEntity<?> getAllQuestionnaires(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<QuestionnaireDTO> questionnaires = questionnaireService.getAllQuestionnaires(pageable);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", questionnaires,
                "message", "获取问卷列表成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "获取问卷列表失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 获取活跃问卷列表
     */
    @GetMapping("/active")
    public ResponseEntity<?> getActiveQuestionnaires() {
        try {
            List<QuestionnaireDTO> questionnaires = questionnaireService.getActiveQuestionnaires();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", questionnaires,
                "message", "获取活跃问卷列表成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "获取活跃问卷列表失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 根据ID获取问卷详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getQuestionnaireById(@PathVariable Long id) {
        try {
            QuestionnaireDTO questionnaire = questionnaireService.getQuestionnaireDTOById(id);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", questionnaire,
                "message", "获取问卷详情成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "获取问卷详情失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 获取问卷的所有问题
     */
    @GetMapping("/{id}/questions")
    public ResponseEntity<?> getQuestionnaireQuestions(@PathVariable Long id) {
        try {
            QuestionnaireDTO questionnaire = questionnaireService.getQuestionnaireWithQuestions(id);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", questionnaire,
                "message", "获取问卷问题成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "获取问卷问题失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 创建新问卷（管理员权限）
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createQuestionnaire(@Valid @RequestBody QuestionnaireDTO questionnaireDTO) {
        try {
            QuestionnaireDTO createdQuestionnaire = questionnaireService.createQuestionnaire(questionnaireDTO);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", createdQuestionnaire,
                "message", "创建问卷成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "创建问卷失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 更新问卷（管理员权限）
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateQuestionnaire(@PathVariable Long id, @Valid @RequestBody QuestionnaireDTO questionnaireDTO) {
        try {
            QuestionnaireDTO updatedQuestionnaire = questionnaireService.updateQuestionnaire(id, questionnaireDTO);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", updatedQuestionnaire,
                "message", "更新问卷成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "更新问卷失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 删除问卷（管理员权限）
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteQuestionnaire(@PathVariable Long id) {
        try {
            questionnaireService.deleteQuestionnaire(id);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "删除问卷成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "删除问卷失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 激活/停用问卷（管理员权限）
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateQuestionnaireStatus(@PathVariable Long id, @RequestParam boolean active) {
        try {
            QuestionnaireDTO updatedQuestionnaire = questionnaireService.updateQuestionnaireStatus(id, active);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", updatedQuestionnaire,
                "message", active ? "激活问卷成功" : "停用问卷成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "更新问卷状态失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 获取问卷统计信息（管理员权限）
     */
    @GetMapping("/{id}/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getQuestionnaireStatistics(@PathVariable Long id) {
        try {
            Map<String, Object> statistics = questionnaireService.getQuestionnaireStatistics(id);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", statistics,
                "message", "获取问卷统计信息成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "获取问卷统计信息失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 搜索问卷（管理员权限）
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> searchQuestionnaires(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<QuestionnaireDTO> questionnaires = questionnaireService.searchQuestionnaires(keyword, pageable);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", questionnaires,
                "message", "搜索问卷成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "搜索问卷失败: " + e.getMessage()
            ));
        }
    }
}
