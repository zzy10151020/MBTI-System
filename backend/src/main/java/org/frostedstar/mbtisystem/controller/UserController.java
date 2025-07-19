package org.frostedstar.mbtisystem.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.frostedstar.mbtisystem.entity.User;
import org.frostedstar.mbtisystem.service.ServiceFactory;
import org.frostedstar.mbtisystem.service.UserService;
import org.frostedstar.mbtisystem.dto.ApiResponse;
import org.frostedstar.mbtisystem.dto.userdto.UserRequestDTO;
import org.frostedstar.mbtisystem.dto.userdto.UserResponseDTO;
import org.frostedstar.mbtisystem.servlet.Route;

import java.io.IOException;
import java.util.Optional;

/**
 * 用户控制器
 */
@Slf4j
public class UserController extends BaseController {
    
    private final UserService userService;
    
    public UserController() {
        this.userService = ServiceFactory.getUserService();
    }
    
    /**
     * 获取当前用户信息
     */
    @Route(value = "", method = "GET")
    public void getCurrentUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (!AuthUtils.checkHttpMethod(request, response, this, "GET")) return;
            
            // 检查用户是否已登录
            User currentUser = AuthUtils.checkLogin(request, response, this);
            if (currentUser == null) return;
            
            // 获取用户信息
            UserResponseDTO userDTO = UserResponseDTO.fromEntity(currentUser);
            ApiResponse<UserResponseDTO> apiResponse = ApiResponse.success("成功获取用户信息", userDTO);
            
            sendApiResponse(response, apiResponse);
            
        } catch (Exception e) {
            log.error("获取用户信息失败", e);
            sendErrorResponse(response, 500, "获取用户信息失败: " + e.getMessage(), "/api/user");
        }
    }
    
    /**
     * 更新用户信息（统一方法）
     */
    @Route(value = "", method = "POST")
    public void updateUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (!AuthUtils.checkHttpMethod(request, response, this, "POST")) return;
            
            // 检查用户是否已登录
            User currentUser = AuthUtils.checkLogin(request, response, this);
            if (currentUser == null) return;
            
            // 解析请求体到DTO
            UserRequestDTO updateRequest = parseRequestBody(request, UserRequestDTO.class);
            
            // 调用统一的更新方法，传递当前用户对象和request
            updateUserInfo(updateRequest, response, currentUser, request);
            
        } catch (Exception e) {
            log.error("更新用户信息失败", e);
            sendErrorResponse(response, 500, "更新用户信息失败: " + e.getMessage(), "/api/user");
        }
    }

    /**
     * 统一的用户信息更新方法
     */
    private void updateUserInfo(UserRequestDTO updateRequest, HttpServletResponse response, User currentUser, HttpServletRequest request) throws IOException {
        Integer userId = currentUser.getUserId();
        boolean updated = false;
        boolean sessionNeedsUpdate = false;
        String successMessage = "更新成功";

        System.out.println(updateRequest.toString());
        
        // 处理密码修改
        if (updateRequest.getCurrentPassword() != null && updateRequest.getNewPassword() != null) {
            // 验证密码修改参数
            if (updateRequest.getCurrentPassword().trim().isEmpty()) {
                ApiResponse<Object> apiResponse = ApiResponse.error("原密码不能为空");
                sendApiResponse(response, apiResponse);
                return;
            }
            
            if (updateRequest.getNewPassword().trim().isEmpty()) {
                ApiResponse<Object> apiResponse = ApiResponse.error("新密码不能为空");
                sendApiResponse(response, apiResponse);
                return;
            }
            
            if (updateRequest.getNewPassword().length() < 6) {
                ApiResponse<Object> apiResponse = ApiResponse.error("新密码长度不能少于6位");
                sendApiResponse(response, apiResponse);
                return;
            }
            
            // 修改密码
            boolean passwordChanged = userService.changePassword(userId, updateRequest.getCurrentPassword(), updateRequest.getNewPassword());

            if (!passwordChanged) {
                ApiResponse<Object> apiResponse = ApiResponse.error("原密码错误");
                sendApiResponse(response, apiResponse);
                return;
            }
            
            updated = true;
            System.out.println("密码修改成功");
            successMessage = "密码修改成功";
            log.info("用户密码修改成功: {}", currentUser.getUsername());
        }
        
        // 处理邮箱更新
        if (updateRequest.getEmail() != null && !updateRequest.getEmail().trim().isEmpty()) {
            String newEmail = updateRequest.getEmail();
            
            // 检查邮箱是否已存在（排除当前用户）
            Optional<User> existingUser = userService.findByEmail(newEmail);
            if (existingUser.isPresent() && !existingUser.get().getUserId().equals(userId)) {
                ApiResponse<Object> apiResponse = ApiResponse.error("邮箱已存在");
                sendApiResponse(response, apiResponse);
                return;
            }
            
            currentUser.setEmail(newEmail);
            updated = true;
            successMessage = "用户信息更新成功";
        }
        
        // 处理用户名更新
        if (updateRequest.getUsername() != null && !updateRequest.getUsername().trim().isEmpty()) {
            String newUsername = updateRequest.getUsername();
            
            // 检查用户名是否已存在（排除当前用户）
            Optional<User> existingUser = userService.findByUsername(newUsername);
            if (existingUser.isPresent() && !existingUser.get().getUserId().equals(userId)) {
                ApiResponse<Object> apiResponse = ApiResponse.error("用户名已存在");
                sendApiResponse(response, apiResponse);
                return;
            }
            
            currentUser.setUsername(newUsername);
            updated = true;
            sessionNeedsUpdate = true; // 用户名变更需要更新Session
            successMessage = "用户信息更新成功";
        }
        
        // 处理角色更新（仅管理员可操作）
        if (updateRequest.getRole() != null) {
            // 检查当前用户是否为管理员
            if (!User.Role.ADMIN.equals(currentUser.getRole())) {
                ApiResponse<Object> apiResponse = ApiResponse.error("权限不足，只有管理员可以修改用户角色");
                sendApiResponse(response, apiResponse);
                return;
            }
            
            currentUser.setRole(updateRequest.getRole());
            updated = true;
            successMessage = "用户信息更新成功";
        }
        
        // 如果有非密码字段更新，保存到数据库
        if (updated && (updateRequest.getEmail() != null || updateRequest.getUsername() != null || updateRequest.getRole() != null)) {
            boolean success = userService.update(currentUser);
            if (!success) {
                ApiResponse<Object> apiResponse = ApiResponse.error("更新用户信息失败");
                sendApiResponse(response, apiResponse);
                return;
            }
            log.info("用户信息更新成功: {}", currentUser.getUsername());
            
            // 更新Session中的用户信息（如果用户名发生变化）
            if (sessionNeedsUpdate) {
                HttpSession session = request.getSession(false);
                if (session != null) {
                    session.setAttribute("username", currentUser.getUsername());
                }
            }
        }
        
        if (updated) {
            // 重新从数据库获取最新的用户信息以确保数据一致性
            Optional<User> latestUserOpt = userService.findById(userId);
            User latestUser = latestUserOpt.orElse(currentUser);
            
            // 根据更新类型返回相应的响应
            if (updateRequest.getCurrentPassword() != null && 
                updateRequest.getEmail() == null && 
                updateRequest.getUsername() == null && 
                updateRequest.getRole() == null) {
                // 仅密码修改成功，返回简单消息
                ApiResponse<String> apiResponse = ApiResponse.success(successMessage, successMessage);
                sendApiResponse(response, apiResponse);
            } else {
                // 用户信息更新成功，返回更新后的用户信息
                UserResponseDTO userDTO = UserResponseDTO.fromEntity(latestUser);
                ApiResponse<UserResponseDTO> apiResponse = ApiResponse.success(successMessage, userDTO);
                sendApiResponse(response, apiResponse);
            }
        } else {
            ApiResponse<Object> apiResponse = ApiResponse.error("没有可更新的内容");
            sendApiResponse(response, apiResponse);
        }
    }
    
    /**
     * 获取用户详情（根据用户ID）
     */
    @Route(value = "/profile", method = "POST")
    public void getUserProfile(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (!AuthUtils.checkHttpMethod(request, response, this, "POST")) return;

            // 首先检查是否已登录
            User currentUser = AuthUtils.checkLogin(request, response, this);
            if (currentUser == null) return;
            
            // 解析请求体获取用户ID
            UserRequestDTO profileRequest = parseRequestBody(request, UserRequestDTO.class);
            
            // 验证请求参数
            if (profileRequest == null || !profileRequest.isValidForGetUserProfileId()) {
                ApiResponse<Object> apiResponse = ApiResponse.error("缺少或无效的用户ID参数");
                sendApiResponse(response, apiResponse);
                return;
            }
            
            Integer targetUserId = profileRequest.getGetUserProfileId();
            
            // 检查访问权限
            AuthUtils.AccessLevel accessLevel = AuthUtils.checkAccessLevel(request, targetUserId);
            
            // 根据权限级别决定是否允许访问
            if (accessLevel == AuthUtils.AccessLevel.DENIED) {
                ApiResponse<Object> apiResponse = ApiResponse.error("权限不足，无法查看用户详情");
                sendApiResponse(response, apiResponse);
                return;
            }
            
            // 获取目标用户信息
            Optional<User> targetUserOptional = userService.findById(targetUserId);
            if (targetUserOptional.isEmpty()) {
                ApiResponse<Object> apiResponse = ApiResponse.error("用户不存在");
                sendApiResponse(response, apiResponse);
                return;
            }
            
            User targetUser = targetUserOptional.get();
            UserResponseDTO userDTO;
            
            // 根据访问权限级别返回不同详细程度的数据
            switch (accessLevel) {
                case ADMIN:
                case SELF:
                    // 管理员或本人可以看到完整信息（包括邮箱）
                    userDTO = UserResponseDTO.fromEntity(targetUser);
                    break;
                case PUBLIC:
                    // 其他用户只能看到公开信息（不包括邮箱等敏感信息）
                    userDTO = UserResponseDTO.fromEntitySecure(targetUser);
                    break;
                default:
                    ApiResponse<Object> apiResponse = ApiResponse.error("权限不足");
                    sendApiResponse(response, apiResponse);
                    return;
            }
            
            String message = accessLevel == AuthUtils.AccessLevel.SELF ? 
                "获取个人详情成功" : "获取用户详情成功";
            
            ApiResponse<UserResponseDTO> apiResponse = ApiResponse.success(message, userDTO);
            sendApiResponse(response, apiResponse);
            
            // 记录访问日志
            if (accessLevel == AuthUtils.AccessLevel.ADMIN) {
                log.info("管理员 {} 查看用户详情: {} (ID: {})", 
                    currentUser.getUsername(), targetUser.getUsername(), targetUserId);
            } else if (accessLevel == AuthUtils.AccessLevel.PUBLIC) {
                log.info("用户 {} 查看其他用户公开信息: {} (ID: {})", 
                    currentUser.getUsername(), targetUser.getUsername(), targetUserId);
            }
            
        } catch (Exception e) {
            log.error("获取用户详情失败", e);
            sendErrorResponse(response, 500, "获取用户详情失败: " + e.getMessage(), "/api/user/profile");
        }
    }

    /**
     * 获取用户列表（仅管理员可用）
     */
    @Route(value = "/list", method = "GET")
    public void getUserList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (!AuthUtils.checkHttpMethod(request, response, this, "GET")) return;
            
            // 检查是否为管理员
            User adminUser = AuthUtils.checkAdmin(request, response, this);
            if (adminUser == null) return;
            
            // 获取用户列表
            var userList = userService.findAll();
            ApiResponse<?> apiResponse = ApiResponse.success("成功获取用户列表", userList);

            sendApiResponse(response, apiResponse);
            
        } catch (Exception e) {
            log.error("获取用户列表失败", e);
            sendErrorResponse(response, 500, "获取用户列表失败: " + e.getMessage(), "/api/user/list");
        }
    }

    /**
     * 删除用户（仅管理员可用）
     */
    @Route(value = "", method = "DELETE")
    public void deleteUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (!AuthUtils.checkHttpMethod(request, response, this, "DELETE")) return;
            
            // 检查是否为管理员
            User adminUser = AuthUtils.checkAdmin(request, response, this);
            if (adminUser == null) return;

            // 解析请求体获取删除请求
            UserRequestDTO deleteRequest = parseRequestBody(request, UserRequestDTO.class);
            
            // 验证删除请求
            if (!deleteRequest.isValidForDeleteUser()) {
                ApiResponse<Object> apiResponse = ApiResponse.error("删除请求无效：要删除的用户ID不能为空");
                sendApiResponse(response, apiResponse);
                return;
            }
            
            Integer deleteUserId = deleteRequest.getDeleteUserId();
            
            // 防止管理员删除自己
            if (adminUser.getUserId().equals(deleteUserId)) {
                ApiResponse<Object> apiResponse = ApiResponse.error("不能删除自己的账户");
                sendApiResponse(response, apiResponse);
                return;
            }

            // 检查要删除的用户是否存在
            Optional<User> userOptional = userService.findById(deleteUserId);
            if (userOptional.isEmpty()) {
                ApiResponse<Object> apiResponse = ApiResponse.error("要删除的用户不存在");
                sendApiResponse(response, apiResponse);
                return;
            }
            
            User targetUser = userOptional.get();
            String targetUsername = targetUser.getUsername();
            
            // 删除用户
            boolean deleted = userService.deleteById(deleteUserId);
            if (deleted) {
                ApiResponse<String> apiResponse = ApiResponse.success("用户删除成功", "用户 " + targetUsername + " 已被删除");
                sendApiResponse(response, apiResponse);
                log.info("管理员 {} 删除用户成功: {} (ID: {})", adminUser.getUsername(), targetUsername, deleteUserId);
            } else {
                ApiResponse<Object> apiResponse = ApiResponse.error("用户删除失败，可能用户不存在或无法删除");
                sendApiResponse(response, apiResponse);
                log.warn("管理员 {} 删除用户失败: {} (ID: {})", adminUser.getUsername(), targetUsername, deleteUserId);
            }
            
        } catch (Exception e) {
            log.error("删除用户失败", e);
            sendErrorResponse(response, 500, "删除用户失败: " + e.getMessage(), "/api/user");
        }
    }
}
