package com.viper.demo.Controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import com.viper.demo.Pojo.Result;
import com.viper.demo.Pojo.User;
import com.viper.demo.Service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户管理控制器
 *
 * 该控制器负责处理用户管理相关的HTTP请求，提供用户信息的增删改查功能
 * 基于SA-Token框架实现细粒度的权限控制和角色验证
 *
 * 主要功能：
 * 1. 用户个人信息管理 - 查看和更新个人资料
 * 2. 用户列表管理 - 管理员查看所有用户
 * 3. 用户删除功能 - 管理员删除用户账户
 * 4. 权限信息查询 - 获取当前用户的权限和角色
 * 5. 管理员控制台 - 管理员专用功能面板
 *
 * 权限控制说明：
 * - @SaCheckLogin：要求用户必须登录
 * - @SaCheckRole("admin")：要求用户具有admin角色
 * - @SaCheckPermission("user:update")：要求用户具有特定权限
 * - 支持多重权限验证，确保操作安全性
 *
 * API设计规范：
 * - 统一使用/user前缀
 * - 遵循RESTful设计原则
 * - 统一返回Result<T>格式的响应
 * - 完善的权限验证和异常处理
 *
 * 安全特性：
 * - 基于注解的权限控制
 * - 敏感信息过滤（密码不返回给前端）
 * - 防止用户删除自己的账户
 * - 自动踢下线被删除的用户
 *
 * 响应状态码：
 * - 200：操作成功
 * - 400：请求参数错误
 * - 401：未登录或登录失效
 * - 403：权限不足
 * - 404：资源不存在
 * - 500：服务器内部错误
 *
 * @author Viper
 * @version 1.0
 * @since 2024
 */
@Tag(name = "用户管理", description = "用户信息管理相关接口，包括个人资料、用户列表、权限信息等功能")
@RestController
@RequestMapping("/user")
public class UserController {

    /**
     * 用户服务层对象，用于处理用户相关的业务逻辑
     * 通过@Autowired注解实现依赖注入
     */
    @Autowired
    private UserService userService;

    /**
     * 获取当前用户个人资料接口
     *
     * 该接口用于获取当前登录用户的个人资料信息
     * 使用@SaCheckLogin注解确保只有登录用户才能访问
     *
     * 功能说明：
     * - 自动获取当前登录用户的ID
     * - 查询并返回用户的详细信息
     * - 自动过滤敏感信息（密码字段）
     * - 只能查看自己的个人资料，无法查看他人信息
     *
     * 权限要求：
     * - 用户必须已登录（@SaCheckLogin）
     * - 无需额外的角色或权限验证
     *
     * 安全措施：
     * - SA-Token自动验证登录状态
     * - 密码字段会被清空，不返回给前端
     * - 基于Token获取用户ID，防止越权访问
     *
     * 使用场景：
     * - 个人中心页面的数据展示
     * - 个人信息编辑页面的数据回显
     * - 用户头像、昵称等信息的获取
     *
     * @return Result<User> 包含用户个人资料的响应对象
     *
     * @apiNote GET /user/profile
     * @apiExample 响应示例：
     * {
     *   "code": 200,
     *   "message": "操作成功",
     *   "data": {
     *     "id": 1,
     *     "username": "testuser",
     *     "email": "test@example.com",
     *     "phone": "13800138000",
     *     "password": null,
     *     "isDelete": 0,
     *     "createTime": "2024-01-01T00:00:00",
     *     "updateTime": "2024-01-01T00:00:00"
     *   }
     * }
     */
    @Operation(
        summary = "获取个人资料",
        description = "获取当前登录用户的个人资料信息",
        security = @SecurityRequirement(name = "SA-Token")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未登录")
    })
    @SaCheckLogin
    @GetMapping("/profile")
    public Result<User> getUserProfile() {
        try {
            // 获取当前登录用户的ID
            Integer userId = StpUtil.getLoginIdAsInt();

            // 根据用户ID查询用户信息
            User user = userService.findById(userId);
            if (user != null) {
                // 清空密码字段，确保敏感信息不返回给前端
                user.setPassword(null);
                return Result.success(user);
            }

            // 理论上不会出现这种情况，除非数据被异常删除
            return Result.error(404, "用户不存在");
        } catch (Exception e) {
            return Result.error(500, "获取用户信息失败：" + e.getMessage());
        }
    }

    /**
     * 更新用户个人资料接口
     *
     * 该接口用于更新当前登录用户的个人资料信息
     * 使用@SaCheckPermission注解确保用户具有更新权限
     *
     * 功能说明：
     * - 允许用户更新自己的个人信息
     * - 只能更新指定的安全字段（邮箱、手机号）
     * - 不允许更新敏感字段（用户名、密码、ID等）
     * - 自动更新修改时间戳
     *
     * 权限要求：
     * - 用户必须已登录
     * - 用户必须具有"user:update"权限
     * - 只能更新自己的信息，无法更新他人信息
     *
     * 可更新字段：
     * - email：邮箱地址
     * - phone：手机号码
     *
     * 不可更新字段：
     * - id：用户ID（系统自动生成）
     * - username：用户名（注册后不可修改）
     * - password：密码（需要通过专门的修改密码接口）
     * - createTime：创建时间（系统自动设置）
     * - isDelete：删除标记（只能通过删除接口修改）
     *
     * 安全措施：
     * - 基于权限的访问控制
     * - 只允许更新安全字段
     * - 自动获取当前用户ID，防止越权修改
     *
     * @param user 包含要更新信息的用户对象
     * @return Result<String> 更新操作的结果
     *
     * @apiNote PUT /user/profile
     * @apiExample 请求示例：
     * {
     *   "email": "newemail@example.com",
     *   "phone": "13800138999"
     * }
     * @apiExample 响应示例：
     * {
     *   "code": 200,
     *   "message": "操作成功",
     *   "data": "更新成功"
     * }
     */
    @SaCheckPermission("user:update")
    @PutMapping("/profile")
    public Result<String> updateUserProfile(@RequestBody User user) {
        try {
            // 获取当前登录用户的ID
            Integer userId = StpUtil.getLoginIdAsInt();

            // 查询当前用户的完整信息
            User existingUser = userService.findById(userId);
            if (existingUser == null) {
                return Result.error(404, "用户不存在");
            }

            // 只允许更新指定的安全字段
            // 注意：这里只更新邮箱和手机号，其他字段保持不变
            existingUser.setEmail(user.getEmail());
            existingUser.setPhone(user.getPhone());

            // 保存更新后的用户信息
            userService.update(existingUser);

            return Result.success("更新成功");
        } catch (Exception e) {
            return Result.error(500, "更新失败：" + e.getMessage());
        }
    }

    /**
     * 获取所有用户列表接口（管理员专用）
     *
     * 该接口用于管理员查看系统中所有用户的列表信息
     * 使用@SaCheckRole注解确保只有admin角色的用户才能访问
     *
     * 功能说明：
     * - 查询并返回系统中所有用户的信息
     * - 自动过滤所有用户的敏感信息（密码字段）
     * - 包含用户的基本信息和状态信息
     * - 支持管理员进行用户管理操作
     *
     * 权限要求：
     * - 用户必须已登录
     * - 用户必须具有"admin"角色
     * - 普通用户无法访问此接口
     *
     * 返回信息包含：
     * - 用户ID、用户名、邮箱、手机号
     * - 用户状态（是否被删除）
     * - 创建时间和更新时间
     * - 密码字段会被清空，确保安全
     *
     * 使用场景：
     * - 管理员后台的用户管理页面
     * - 用户统计和分析功能
     * - 用户状态监控和管理
     *
     * 安全措施：
     * - 基于角色的访问控制
     * - 敏感信息自动过滤
     * - 只有管理员可以访问
     *
     * @return Result<List<User>> 包含所有用户信息的响应对象
     *
     * @apiNote GET /user/list
     * @apiExample 响应示例：
     * {
     *   "code": 200,
     *   "message": "操作成功",
     *   "data": [
     *     {
     *       "id": 1,
     *       "username": "admin",
     *       "email": "admin@example.com",
     *       "phone": "13800138000",
     *       "password": null,
     *       "isDelete": 0,
     *       "createTime": "2024-01-01T00:00:00",
     *       "updateTime": "2024-01-01T00:00:00"
     *     },
     *     {
     *       "id": 2,
     *       "username": "test",
     *       "email": "test@example.com",
     *       "phone": "13800138001",
     *       "password": null,
     *       "isDelete": 0,
     *       "createTime": "2024-01-01T00:00:00",
     *       "updateTime": "2024-01-01T00:00:00"
     *     }
     *   ]
     * }
     */
    @Operation(
        summary = "获取用户列表",
        description = "管理员获取系统中所有用户的列表信息（需要admin角色）",
        security = @SecurityRequirement(name = "SA-Token")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未登录"),
        @ApiResponse(responseCode = "403", description = "权限不足，需要admin角色")
    })
    @SaCheckRole("admin")
    @GetMapping("/list")
    public Result<List<User>> getUserList() {
        try {
            // 查询所有用户信息
            List<User> users = userService.findAll();

            // 清空所有用户的密码字段，确保敏感信息不返回给前端
            users.forEach(user -> user.setPassword(null));

            return Result.success(users);
        } catch (Exception e) {
            return Result.error(500, "获取用户列表失败：" + e.getMessage());
        }
    }

    /**
     * 删除用户接口（管理员专用）
     *
     * 该接口用于管理员删除指定的用户账户
     * 使用双重权限验证：@SaCheckRole("admin") + @SaCheckPermission("user:delete")
     *
     * 功能说明：
     * - 管理员可以删除指定ID的用户
     * - 被删除的用户会被自动踢下线
     * - 防止管理员删除自己的账户
     * - 支持软删除或硬删除（根据Service层实现）
     *
     * 权限要求：
     * - 用户必须已登录
     * - 用户必须具有"admin"角色
     * - 用户必须具有"user:delete"权限
     * - 双重验证确保操作安全性
     *
     * 安全限制：
     * - 管理员不能删除自己的账户
     * - 被删除的用户会立即被踢下线
     * - 删除操作不可逆，需要谨慎操作
     *
     * 删除流程：
     * 1. 验证当前用户权限
     * 2. 检查是否尝试删除自己
     * 3. 执行用户删除操作
     * 4. 踢下线被删除的用户
     * 5. 返回操作结果
     *
     * 使用场景：
     * - 清理违规用户账户
     * - 删除测试或无效账户
     * - 用户注销申请的处理
     *
     * @param userId 要删除的用户ID（通过路径参数传递）
     * @return Result<String> 删除操作的结果
     *
     * @apiNote DELETE /user/{userId}
     * @apiExample 请求示例：
     * DELETE /user/3
     * @apiExample 响应示例：
     * {
     *   "code": 200,
     *   "message": "操作成功",
     *   "data": "删除成功"
     * }
     */
    @SaCheckRole("admin")
    @SaCheckPermission("user:delete")
    @DeleteMapping("/{userId}")
    public Result<String> deleteUser(@PathVariable Integer userId) {
        try {
            // 获取当前登录的管理员用户ID
            Integer currentUserId = StpUtil.getLoginIdAsInt();

            // 安全检查：不能删除自己的账户
            if (currentUserId.equals(userId)) {
                return Result.error(400, "不能删除自己");
            }

            // 执行用户删除操作
            boolean success = userService.deleteById(userId);
            if (success) {
                // 删除成功后，立即踢下线被删除的用户
                // 确保被删除的用户无法继续使用系统
                StpUtil.kickout(userId);

                return Result.success("删除成功");
            } else {
                // 删除失败，通常是因为用户不存在
                return Result.error(404, "用户不存在");
            }
        } catch (Exception e) {
            return Result.error(500, "删除失败：" + e.getMessage());
        }
    }

    /**
     * 获取当前用户权限和角色信息接口
     *
     * 该接口用于获取当前登录用户的权限列表和角色列表
     * 前端可以根据这些信息来控制页面元素的显示和功能的可用性
     *
     * 功能说明：
     * - 获取当前用户的所有权限列表
     * - 获取当前用户的所有角色列表
     * - 返回当前用户的ID信息
     * - 支持前端进行权限控制和UI渲染
     *
     * 权限要求：
     * - 用户必须已登录（@SaCheckLogin）
     * - 无需额外的角色或权限验证
     *
     * 返回信息说明：
     * - permissions：权限列表，格式如["user:info", "user:update"]
     * - roles：角色列表，格式如["admin", "user"]
     * - userId：当前登录用户的ID
     *
     * 使用场景：
     * - 前端页面初始化时获取用户权限
     * - 动态控制菜单和按钮的显示
     * - 前端路由权限验证
     * - 功能模块的权限控制
     *
     * 前端使用示例：
     * - 根据permissions数组判断是否显示"删除"按钮
     * - 根据roles数组判断是否显示管理员菜单
     * - 根据权限信息进行页面跳转控制
     *
     * @return Result<Map<String, Object>> 包含权限和角色信息的响应对象
     *
     * @apiNote GET /user/permissions
     * @apiExample 响应示例（管理员用户）：
     * {
     *   "code": 200,
     *   "message": "操作成功",
     *   "data": {
     *     "permissions": ["user:info", "user:update", "user:delete", "user:list", "admin:dashboard"],
     *     "roles": ["admin"],
     *     "userId": 1
     *   }
     * }
     * @apiExample 响应示例（普通用户）：
     * {
     *   "code": 200,
     *   "message": "操作成功",
     *   "data": {
     *     "permissions": ["user:info", "user:update"],
     *     "roles": ["user"],
     *     "userId": 2
     *   }
     * }
     */
    @SaCheckLogin
    @GetMapping("/permissions")
    public Result<Map<String, Object>> getUserPermissions() {
        try {
            // 构建返回数据
            Map<String, Object> data = new HashMap<>();

            // 获取当前用户的权限列表
            data.put("permissions", StpUtil.getPermissionList());

            // 获取当前用户的角色列表
            data.put("roles", StpUtil.getRoleList());

            // 获取当前用户ID
            data.put("userId", StpUtil.getLoginId());

            return Result.success(data);
        } catch (Exception e) {
            return Result.error(500, "获取权限信息失败：" + e.getMessage());
        }
    }

    /**
     * 管理员控制台接口（管理员专用）
     *
     * 该接口为管理员提供系统概览和基本统计信息
     * 使用@SaCheckRole注解确保只有admin角色的用户才能访问
     *
     * 功能说明：
     * - 提供管理员控制台的基本信息
     * - 显示系统用户总数统计
     * - 显示当前登录的管理员信息
     * - 可扩展更多的系统统计数据
     *
     * 权限要求：
     * - 用户必须已登录
     * - 用户必须具有"admin"角色
     * - 普通用户无法访问此接口
     *
     * 返回信息包含：
     * - message：欢迎信息
     * - totalUsers：系统用户总数
     * - currentAdmin：当前管理员用户ID
     *
     * 扩展可能性：
     * - 添加在线用户统计
     * - 添加系统资源使用情况
     * - 添加操作日志统计
     * - 添加系统健康状态检查
     *
     * 使用场景：
     * - 管理员后台首页数据展示
     * - 系统监控和统计面板
     * - 管理员工作台信息概览
     *
     * @return Result<Map<String, Object>> 包含控制台信息的响应对象
     *
     * @apiNote GET /user/admin/dashboard
     * @apiExample 响应示例：
     * {
     *   "code": 200,
     *   "message": "操作成功",
     *   "data": {
     *     "message": "欢迎进入管理员控制台",
     *     "totalUsers": 5,
     *     "currentAdmin": 1
     *   }
     * }
     */
    @SaCheckRole("admin")
    @GetMapping("/admin/dashboard")
    public Result<Map<String, Object>> adminDashboard() {
        try {
            // 构建控制台数据
            Map<String, Object> data = new HashMap<>();

            // 欢迎信息
            data.put("message", "欢迎进入管理员控制台");

            // 系统用户总数统计
            data.put("totalUsers", userService.findAll().size());

            // 当前管理员用户ID
            data.put("currentAdmin", StpUtil.getLoginId());

            // 可以在这里添加更多的统计信息
            // data.put("onlineUsers", StpUtil.searchTokenValue("", 0, -1, false).size());
            // data.put("systemStatus", "正常");
            // data.put("lastLoginTime", new Date());

            return Result.success(data);
        } catch (Exception e) {
            return Result.error(500, "获取控制台信息失败：" + e.getMessage());
        }
    }
}
