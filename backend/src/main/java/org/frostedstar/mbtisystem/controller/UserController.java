package org.frostedstar.mbtisystem.controller;

import org.frostedstar.mbtisystem.dto.UserDTO;
import org.frostedstar.mbtisystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

/**
 * 用户管理控制器
 * 提供用户信息查询、更新、管理等功能
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取当前用户信息
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getCurrentUser() {
        try {
            UserDTO user = userService.getCurrentUser();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", user,
                "message", "获取用户信息成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "获取用户信息失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 更新当前用户信息
     */
    @PutMapping("/profile")
    public ResponseEntity<?> updateCurrentUser(@Valid @RequestBody UserDTO userDTO) {
        try {
            UserDTO updatedUser = userService.updateCurrentUser(userDTO);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", updatedUser,
                "message", "更新用户信息成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "更新用户信息失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 获取所有用户列表（管理员权限）
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<UserDTO> users = userService.getAllUsers(pageable);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", users,
                "message", "获取用户列表成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "获取用户列表失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 根据ID获取用户信息（管理员权限）
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            UserDTO user = userService.getUserById(id);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", user,
                "message", "获取用户信息成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "获取用户信息失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 更新用户信息（管理员权限）
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO) {
        try {
            UserDTO updatedUser = userService.updateUser(id, userDTO);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", updatedUser,
                "message", "更新用户信息成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "更新用户信息失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 删除用户（管理员权限）
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "删除用户成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "删除用户失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 修改用户角色（管理员权限）
     */
    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUserRole(@PathVariable Long id, @RequestParam String role) {
        try {
            UserDTO updatedUser = userService.updateUserRole(id, role);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", updatedUser,
                "message", "修改用户角色成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "修改用户角色失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 搜索用户（管理员权限）
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> searchUsers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<UserDTO> users = userService.searchUsers(keyword, pageable);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", users,
                "message", "搜索用户成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "搜索用户失败: " + e.getMessage()
            ));
        }
    }
}
