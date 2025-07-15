package org.frostedstar.mbtisystem.controller;

import lombok.RequiredArgsConstructor;
import org.frostedstar.mbtisystem.dto.LoginRequest;
import org.frostedstar.mbtisystem.dto.JwtResponse;
import org.frostedstar.mbtisystem.dto.RegisterDTO;
import org.frostedstar.mbtisystem.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            JwtResponse response = authService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "登录失败: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterDTO registerRequest) {
        try {
            if (!authService.register(registerRequest.getUsername(), registerRequest.getPassword(), registerRequest.getEmail())) {
                throw new RuntimeException("用户名或邮箱已存在");
            }
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", Map.of("username", registerRequest.getUsername()),
                "message", "注册成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "注册失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 测试接口 - 检查服务器状态（无需认证）
     */
    @GetMapping("/status")
    public ResponseEntity<?> getServerStatus() {
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "服务器运行正常",
            "timestamp", System.currentTimeMillis(),
            "version", "1.0.0"
        ));
    }
}
