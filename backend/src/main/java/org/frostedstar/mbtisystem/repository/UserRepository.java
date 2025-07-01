package org.frostedstar.mbtisystem.repository;

import org.frostedstar.mbtisystem.model.User;
import org.frostedstar.mbtisystem.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户数据访问层
 * 提供用户相关的数据库操作接口
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户信息（可选）
     */
    Optional<User> findByUsername(String username);

    /**
     * 根据邮箱查找用户
     * @param email 邮箱
     * @return 用户信息（可选）
     */
    Optional<User> findByEmail(String email);

    /**
     * 检查用户名是否存在
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 检查邮箱是否存在
     * @param email 邮箱
     * @return 是否存在
     */
    boolean existsByEmail(String email);

    /**
     * 根据用户角色查找用户列表
     * @param role 用户角色
     * @return 用户列表
     */
    List<User> findByRole(UserRole role);

    /**
     * 根据创建时间范围查找用户
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 用户列表
     */
    List<User> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 根据用户名模糊查询用户
     * @param username 用户名关键字
     * @return 用户列表
     */
    List<User> findByUsernameContaining(String username);

    /**
     * 根据邮箱模糊查询用户
     * @param email 邮箱关键字
     * @return 用户列表
     */
    List<User> findByEmailContaining(String email);

    /**
     * 查找指定时间之后创建的用户
     * @param dateTime 指定时间
     * @return 用户列表
     */
    List<User> findByCreatedAtAfter(LocalDateTime dateTime);

    /**
     * 统计指定角色的用户数量
     * @param role 用户角色
     * @return 用户数量
     */
    long countByRole(UserRole role);

    /**
     * 自定义查询：根据用户名或邮箱查找用户
     * @param username 用户名
     * @param email 邮箱
     * @return 用户信息（可选）
     */
    @Query("SELECT u FROM User u WHERE u.username = :username OR u.email = :email")
    Optional<User> findByUsernameOrEmail(@Param("username") String username, @Param("email") String email);

    /**
     * 自定义查询：查找最近注册的用户
     * @param limit 限制数量
     * @return 用户列表
     */
    @Query("SELECT u FROM User u ORDER BY u.createdAt DESC LIMIT :limit")
    List<User> findRecentUsers(@Param("limit") int limit);

    /**
     * 自定义查询：根据关键字在用户名和邮箱中搜索
     * @param keyword 关键字
     * @return 用户列表
     */
    @Query("SELECT u FROM User u WHERE u.username LIKE %:keyword% OR u.email LIKE %:keyword%")
    List<User> searchByKeyword(@Param("keyword") String keyword);
}
