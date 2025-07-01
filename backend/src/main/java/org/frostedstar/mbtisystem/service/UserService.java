package org.frostedstar.mbtisystem.service;

import org.frostedstar.mbtisystem.dto.UserDTO;
import org.frostedstar.mbtisystem.model.User;
import org.frostedstar.mbtisystem.model.UserRole;
import org.frostedstar.mbtisystem.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用户服务层
 */
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 注册新用户
     */
    public User registerUser(String username, String password, String email) {
        // 检查用户名和邮箱是否已存在
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("用户名已存在");
        }
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("邮箱已存在");
        }

        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setRole(UserRole.USER); // 默认为普通用户

        return userRepository.save(user);
    }

    /**
     * 根据用户名查找用户
     */
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * 根据ID查找用户
     */
    @Transactional(readOnly = true)
    public Optional<User> findById(Long userId) {
        return userRepository.findById(userId);
    }

    /**
     * 根据邮箱查找用户
     */
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * 更新用户信息（普通用户只能更新自己的信息）
     */
    public User updateUserInfo(Long userId, String email, String currentUsername) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 检查权限：只能更新自己的信息，除非是管理员
        if (!user.getUsername().equals(currentUsername) && !isCurrentUserAdmin()) {
            throw new RuntimeException("无权限修改其他用户信息");
        }

        // 检查邮箱是否被其他用户使用
        if (email != null && !email.equals(user.getEmail())) {
            if (userRepository.existsByEmail(email)) {
                throw new RuntimeException("邮箱已被使用");
            }
            user.setEmail(email);
        }

        return userRepository.save(user);
    }

    /**
     * 修改密码
     */
    public void changePassword(Long userId, String oldPassword, String newPassword, String currentUsername) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 检查权限：只能修改自己的密码，除非是管理员
        if (!user.getUsername().equals(currentUsername) && !isCurrentUserAdmin()) {
            throw new RuntimeException("无权限修改其他用户密码");
        }

        // 验证旧密码（管理员可以跳过验证）
        if (!isCurrentUserAdmin() && !passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new RuntimeException("原密码错误");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    /**
     * 获取所有用户（仅管理员）
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * 根据角色查找用户（仅管理员）
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public List<User> getUsersByRole(UserRole role) {
        return userRepository.findByRole(role);
    }

    /**
     * 删除用户（仅管理员）
     */
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 防止删除管理员账户
        if (user.getRole() == UserRole.ADMIN) {
            throw new RuntimeException("不能删除管理员账户");
        }

        userRepository.delete(user);
    }

    /**
     * 搜索用户（仅管理员）
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public List<User> searchUsers(String keyword) {
        return userRepository.searchByKeyword(keyword);
    }

    /**
     * 获取最近注册的用户（仅管理员）
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public List<User> getRecentUsers(int limit) {
        return userRepository.findRecentUsers(limit);
    }

    /**
     * 统计用户数量
     */
    @Transactional(readOnly = true)
    public long countUsers() {
        return userRepository.count();
    }

    /**
     * 统计指定角色的用户数量
     */
    @Transactional(readOnly = true)
    public long countUsersByRole(UserRole role) {
        return userRepository.countByRole(role);
    }

    /**
     * 获取指定时间段内注册的用户
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public List<User> getUsersRegisteredBetween(LocalDateTime startTime, LocalDateTime endTime) {
        return userRepository.findByCreatedAtBetween(startTime, endTime);
    }

    /**
     * 获取当前用户
     */
    @Transactional(readOnly = true)
    public UserDTO getCurrentUser() {
        String currentUsername = getCurrentUsername();
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("当前用户不存在"));
        return convertToDTO(user);
    }

    /**
     * 更新当前用户信息
     */
    public UserDTO updateCurrentUser(UserDTO userDTO) {
        String currentUsername = getCurrentUsername();
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("当前用户不存在"));
        
        // 只允许更新邮箱
        if (userDTO.getEmail() != null && !userDTO.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(userDTO.getEmail())) {
                throw new RuntimeException("邮箱已被使用");
            }
            user.setEmail(userDTO.getEmail());
        }
        
        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    /**
     * 根据ID获取用户信息
     */
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return convertToDTO(user);
    }

    /**
     * 更新用户信息（管理员权限）
     */
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 更新邮箱
        if (userDTO.getEmail() != null && !userDTO.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(userDTO.getEmail())) {
                throw new RuntimeException("邮箱已被使用");
            }
            user.setEmail(userDTO.getEmail());
        }
        
        // 更新角色
        if (userDTO.getRole() != null) {
            user.setRole(userDTO.getRole());
        }
        
        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    /**
     * 修改用户角色
     */
    public UserDTO updateUserRole(Long id, String roleName) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        UserRole role;
        try {
            role = UserRole.valueOf(roleName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("无效的角色类型");
        }
        
        user.setRole(role);
        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    /**
     * 获取所有用户（分页）
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        Page<User> userPage = userRepository.findAll(pageable);
        List<UserDTO> userDTOs = userPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(userDTOs, pageable, userPage.getTotalElements());
    }

    /**
     * 搜索用户（分页）
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public Page<UserDTO> searchUsers(String keyword, Pageable pageable) {
        // 这里简化实现，实际可以使用更复杂的搜索逻辑
        List<User> users = userRepository.searchByKeyword(keyword);
        
        // 手动分页
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), users.size());
        List<User> pageContent = users.subList(start, end);
        
        List<UserDTO> userDTOs = pageContent.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return new PageImpl<>(userDTOs, pageable, users.size());
    }

    /**
     * 获取当前用户名
     */
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("用户未认证");
        }
        return authentication.getName();
    }

    /**
     * 检查当前用户是否为管理员
     */
    private boolean isCurrentUserAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
    }

    /**
     * 转换User实体为UserDTO
     */
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setCreatedAt(user.getCreatedAt());
        
        // 获取答题数量（这里简化，实际可能需要关联查询）
        dto.setAnswerCount(0L); // 需要后续实现答题统计
        
        return dto;
    }
}
