package org.frostedstar.mbtisystem.service;

import org.frostedstar.mbtisystem.dto.JwtResponse;
import org.frostedstar.mbtisystem.dto.LoginRequest;
import org.frostedstar.mbtisystem.model.User;
import org.frostedstar.mbtisystem.security.JwtUtil;
import org.frostedstar.mbtisystem.security.UserDetailsImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 认证服务层
 */
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    public AuthService(AuthenticationManager authenticationManager, 
                      JwtUtil jwtUtil, 
                      UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    /**
     * 用户登录
     */
    public JwtResponse login(LoginRequest loginRequest) {
        // 认证用户
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 获取用户详情
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        // 生成JWT令牌
        String jwt = jwtUtil.generateJwtToken(authentication);

        // 获取用户角色
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return new JwtResponse(
                jwt,
                userDetails.getUserId(),
                userDetails.getUsername(),
                roles);
    }

    /**
     * 用户注册
     */
    public boolean register(String username, String password, String email) {
        return userService.registerUser(username, password, email) != null;
    }

    /**
     * 刷新令牌
     */
    public JwtResponse refreshToken(String token) {
        if (jwtUtil.validateToken(token)) {
            String username = jwtUtil.getUsernameFromToken(token);
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));

            // 创建新的认证对象
            UserDetailsImpl userDetails = UserDetailsImpl.build(user);
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            // 生成新的JWT令牌
            String newJwt = jwtUtil.generateJwtToken(authentication);

            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            return new JwtResponse(
                    newJwt,
                    userDetails.getUserId(),
                    userDetails.getUsername(),
                    roles);
        } else {
            throw new RuntimeException("无效的令牌");
        }
    }

    /**
     * 注销
     */
    public void logout() {
        SecurityContextHolder.clearContext();
    }

    /**
     * 验证令牌
     */
    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }

    /**
     * 从令牌获取用户名
     */
    public String getUsernameFromToken(String token) {
        return jwtUtil.getUsernameFromToken(token);
    }

    /**
     * 获取当前认证用户
     */
    public UserDetailsImpl getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            return (UserDetailsImpl) authentication.getPrincipal();
        }
        return null;
    }

    /**
     * 检查当前用户是否为管理员
     */
    public boolean isCurrentUserAdmin() {
        UserDetailsImpl currentUser = getCurrentUser();
        if (currentUser != null) {
            return currentUser.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        }
        return false;
    }

    /**
     * 获取当前用户ID
     */
    public Long getCurrentUserId() {
        UserDetailsImpl currentUser = getCurrentUser();
        return currentUser != null ? currentUser.getUserId() : null;
    }

    /**
     * 获取当前用户名
     */
    public String getCurrentUsername() {
        UserDetailsImpl currentUser = getCurrentUser();
        return currentUser != null ? currentUser.getUsername() : null;
    }
}
