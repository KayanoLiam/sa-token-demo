package com.viper.demo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import cn.dev33.satoken.stp.StpUtil;
import com.viper.demo.Pojo.*;
import com.viper.demo.Service.UserService;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户认证控制器
 *
 * 该控制器负责处理用户认证相关的所有HTTP请求，包括登录、注册、注销等核心功能
 * 基于SA-Token框架实现会话管理和权限控制
 *
 * 主要功能：
 * 1. 用户登录认证 - 验证用户名密码，创建登录会话
 * 2. 用户注册 - 创建新用户账户
 * 3. 用户注销 - 清除登录会话
 * 4. 登录状态查询 - 检查用户是否已登录
 * 5. 用户信息获取 - 获取当前登录用户的详细信息
 * 6. 强制下线 - 管理员踢人功能
 *
 * API设计规范：
 * - 统一使用/auth前缀
 * - 遵循RESTful设计原则
 * - 统一返回Result<T>格式的响应
 * - 完善的参数验证和异常处理
 *
 * 安全特性：
 * - 密码验证（注：当前为明文比较，生产环境需要加密）
 * - 账户状态检查（是否被禁用）
 * - 登录状态验证
 * - 敏感信息过滤（密码不返回给前端）
 *
 * 响应状态码：
 * - 200：操作成功
 * - 400：请求参数错误
 * - 401：认证失败（用户名密码错误或未登录）
 * - 403：权限不足（账户被禁用）
 * - 404：资源不存在（用户不存在）
 * - 500：服务器内部错误
 *
 * @author Viper
 * @version 1.0
 * @since 2024
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    /**
     * 用户服务层对象，用于处理用户相关的业务逻辑
     * 通过@Autowired注解实现依赖注入
     */
    @Autowired
    private UserService userService;

    /**
     * 用户登录接口
     *
     * 该接口用于处理用户登录请求，验证用户身份并创建登录会话
     *
     * 登录流程：
     * 1. 接收并验证登录请求参数（用户名、密码）
     * 2. 根据用户名查询用户信息
     * 3. 验证用户状态（是否被禁用）
     * 4. 验证密码是否正确
     * 5. 调用SA-Token创建登录会话
     * 6. 返回登录成功信息和Token
     *
     * 安全措施：
     * - 参数非空验证
     * - 用户状态检查（防止已禁用用户登录）
     * - 密码验证（注：当前为明文比较，生产环境需要使用加密密码）
     * - 统一的错误提示（防止用户名枚举攻击）
     *
     * 返回数据包含：
     * - token：SA-Token生成的会话令牌
     * - userId：用户ID
     * - username：用户名
     * - email：邮箱地址
     * - phone：手机号码
     *
     * @param loginRequest 登录请求对象，包含用户名和密码
     * @return Result<Map<String, Object>> 登录结果，成功时包含用户信息和token
     *
     * @apiNote POST /auth/login
     * @apiExample 请求示例：
     * {
     *   "username": "admin",
     *   "password": "123456"
     * }
     * @apiExample 响应示例：
     * {
     *   "code": 200,
     *   "message": "操作成功",
     *   "data": {
     *     "token": "uuid-token-value",
     *     "userId": 1,
     *     "username": "admin",
     *     "email": "admin@example.com",
     *     "phone": "13800138000"
     *   }
     * }
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> doLogin(@RequestBody LoginRequest loginRequest) {
        try {
            // 第一步：参数校验
            if (loginRequest == null) {
                return Result.error(400, "请求参数不能为空");
            }
            if (loginRequest.getUsername() == null || loginRequest.getUsername().trim().isEmpty()) {
                return Result.error(400, "用户名不能为空");
            }
            if (loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
                return Result.error(400, "密码不能为空");
            }

            // 获取并清理输入参数
            String username = loginRequest.getUsername().trim();
            String password = loginRequest.getPassword();

            // 第二步：根据用户名查询用户信息
            User user = userService.findByUsername(username);
            if (user == null) {
                // 统一错误提示，防止用户名枚举攻击
                return Result.error(401, "用户名或密码错误");
            }

            // 第三步：检查用户账户状态
            if (user.getIsDelete() != null && user.getIsDelete() == 1) {
                return Result.error(403, "账号已被禁用");
            }

            // 第四步：验证密码
            // 注意：这里使用明文比较，实际项目中应该使用加密后的密码比较
            // 建议使用：PasswordUtil.verifyPassword(password, user.getPassword(), user.getSalt())
            if (!password.equals(user.getPassword())) {
                return Result.error(401, "用户名或密码错误");
            }

            // 第五步：执行登录，使用用户ID作为登录标识
            StpUtil.login(user.getId());

            // 第六步：构建返回数据
            Map<String, Object> data = new HashMap<>();
            data.put("token", StpUtil.getTokenValue());    // SA-Token生成的会话令牌
            data.put("userId", user.getId());              // 用户ID
            data.put("username", user.getUsername());      // 用户名
            data.put("email", user.getEmail());            // 邮箱
            data.put("phone", user.getPhone());            // 手机号

            return Result.success(data);

        } catch (Exception e) {
            // 记录异常信息，便于调试
            e.printStackTrace();
            return Result.error(500, "登录失败：" + e.getMessage());
        }
    }

    /**
     * 查询用户登录状态接口
     *
     * 该接口用于检查当前用户的登录状态，无需登录即可访问
     * 前端可以通过此接口判断用户是否已登录，从而决定页面跳转逻辑
     *
     * 功能说明：
     * - 检查当前请求是否携带有效的登录Token
     * - 如果已登录，返回用户ID和Token信息
     * - 如果未登录，仅返回登录状态为false
     *
     * 使用场景：
     * - 页面初始化时检查登录状态
     * - 路由守卫中验证用户权限
     * - 自动登录功能的状态检查
     *
     * @return Result<Map<String, Object>> 包含登录状态的响应对象
     *
     * @apiNote GET /auth/isLogin
     * @apiExample 响应示例（已登录）：
     * {
     *   "code": 200,
     *   "message": "操作成功",
     *   "data": {
     *     "isLogin": true,
     *     "userId": 1,
     *     "token": "uuid-token-value"
     *   }
     * }
     * @apiExample 响应示例（未登录）：
     * {
     *   "code": 200,
     *   "message": "操作成功",
     *   "data": {
     *     "isLogin": false
     *   }
     * }
     */
    @GetMapping("/isLogin")
    public Result<Map<String, Object>> isLogin() {
        // 构建响应数据
        Map<String, Object> data = new HashMap<>();

        // 检查登录状态
        boolean loginStatus = StpUtil.isLogin();
        data.put("isLogin", loginStatus);

        // 如果已登录，返回额外的用户信息
        if (loginStatus) {
            data.put("userId", StpUtil.getLoginId());        // 当前登录用户ID
            data.put("token", StpUtil.getTokenValue());      // 当前会话Token
        }

        return Result.success(data);
    }

    /**
     * 获取当前登录用户详细信息接口
     *
     * 该接口用于获取当前登录用户的完整个人信息
     * 需要用户已登录才能访问，会自动验证登录状态
     *
     * 功能说明：
     * - 验证用户登录状态
     * - 根据登录用户ID查询用户详细信息
     * - 过滤敏感信息（如密码）后返回
     *
     * 安全措施：
     * - 自动验证登录状态，未登录会抛出异常
     * - 密码字段会被清空，不返回给前端
     * - 只能获取当前登录用户的信息，无法获取其他用户信息
     *
     * 使用场景：
     * - 用户个人中心页面数据加载
     * - 个人信息编辑页面的数据回显
     * - 用户头像、昵称等信息的显示
     *
     * @return Result<User> 包含用户信息的响应对象
     *
     * @apiNote GET /auth/userInfo
     * @apiExample 响应示例：
     * {
     *   "code": 200,
     *   "message": "操作成功",
     *   "data": {
     *     "id": 1,
     *     "username": "admin",
     *     "email": "admin@example.com",
     *     "phone": "13800138000",
     *     "password": null,
     *     "isDelete": 0,
     *     "createTime": "2024-01-01T00:00:00",
     *     "updateTime": "2024-01-01T00:00:00"
     *   }
     * }
     */
    @GetMapping("/userInfo")
    public Result<User> getUserInfo() {
        try {
            // 第一步：检查用户是否已登录
            // 如果未登录，SA-Token会抛出NotLoginException异常
            StpUtil.checkLogin();

            // 第二步：获取当前登录用户的ID
            Integer userId = StpUtil.getLoginIdAsInt();

            // 第三步：根据用户ID查询用户详细信息
            User user = userService.findById(userId);
            if (user == null) {
                // 理论上不会出现这种情况，除非数据被异常删除
                return Result.error(404, "用户不存在");
            }

            // 第四步：清空敏感信息，确保密码不会返回给前端
            user.setPassword(null);

            return Result.success(user);

        } catch (Exception e) {
            // 捕获未登录异常或其他异常
            return Result.error(401, "请先登录");
        }
    }

    /**
     * 用户注销接口
     *
     * 该接口用于处理用户主动注销登录的请求
     * 会清除服务器端的会话信息和客户端的Token
     *
     * 功能说明：
     * - 清除当前用户的登录会话
     * - 使当前Token失效
     * - 清理相关的缓存信息
     *
     * 注销效果：
     * - 当前Token立即失效
     * - 需要重新登录才能访问需要认证的接口
     * - 如果开启了记住我功能，相关信息也会被清除
     *
     * 使用场景：
     * - 用户主动退出登录
     * - 切换账户前的注销操作
     * - 安全退出功能
     *
     * @return Result<String> 注销操作的结果
     *
     * @apiNote POST /auth/logout
     * @apiExample 响应示例：
     * {
     *   "code": 200,
     *   "message": "操作成功",
     *   "data": "注销成功"
     * }
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        try {
            // 执行注销操作
            // SA-Token会自动清除当前用户的登录状态和Token信息
            StpUtil.logout();

            return Result.success("注销成功");
        } catch (Exception e) {
            // 记录异常信息
            e.printStackTrace();
            return Result.error(500, "注销失败：" + e.getMessage());
        }
    }

    /**
     * 用户注册接口
     *
     * 该接口用于处理新用户的注册请求，创建新的用户账户
     * 支持用户名、邮箱、密码、手机号等信息的注册
     *
     * 注册流程：
     * 1. 验证请求参数的完整性和有效性
     * 2. 检查用户名和邮箱的唯一性
     * 3. 创建新用户对象并设置基本信息
     * 4. 保存用户到数据库
     * 5. 返回注册成功信息
     *
     * 验证规则：
     * - 用户名：必填，不能为空，不能重复
     * - 密码：必填，不能为空（注：生产环境需要密码强度验证）
     * - 邮箱：必填，不能为空，不能重复，需要符合邮箱格式
     * - 手机号：可选，可以为空
     *
     * 安全措施：
     * - 用户名唯一性检查，防止重复注册
     * - 邮箱唯一性检查，防止重复绑定
     * - 参数清理和验证，防止恶意输入
     * - 密码存储（注：当前为明文存储，生产环境需要加密）
     *
     * 返回数据包含：
     * - userId：新创建的用户ID
     * - username：用户名
     * - email：邮箱地址
     * - message：注册成功提示信息
     *
     * @param registerRequest 注册请求对象，包含用户注册信息
     * @return Result<Map<String, Object>> 注册结果，成功时包含用户基本信息
     *
     * @apiNote POST /auth/register
     * @apiExample 请求示例：
     * {
     *   "username": "newuser",
     *   "password": "123456",
     *   "email": "newuser@example.com",
     *   "phone": "13800138002"
     * }
     * @apiExample 响应示例：
     * {
     *   "code": 200,
     *   "message": "操作成功",
     *   "data": {
     *     "userId": 3,
     *     "username": "newuser",
     *     "email": "newuser@example.com",
     *     "message": "注册成功"
     *   }
     * }
     */
    @PostMapping("/register")
    public Result<Map<String, Object>> register(@RequestBody RegisterRequest registerRequest) {
        try {
            // 第一步：参数完整性校验
            if (registerRequest == null) {
                return Result.error(400, "请求参数不能为空");
            }
            if (registerRequest.getUsername() == null || registerRequest.getUsername().trim().isEmpty()) {
                return Result.error(400, "用户名不能为空");
            }
            if (registerRequest.getPassword() == null || registerRequest.getPassword().trim().isEmpty()) {
                return Result.error(400, "密码不能为空");
            }
            if (registerRequest.getEmail() == null || registerRequest.getEmail().trim().isEmpty()) {
                return Result.error(400, "邮箱不能为空");
            }

            // 获取并清理输入参数
            String username = registerRequest.getUsername().trim();
            String password = registerRequest.getPassword();
            String email = registerRequest.getEmail().trim();
            String phone = registerRequest.getPhone();

            // 第二步：检查用户名唯一性
            if (userService.existsByUsername(username)) {
                return Result.error(400, "用户名已存在");
            }

            // 第三步：检查邮箱唯一性
            if (userService.existsByEmail(email)) {
                return Result.error(400, "邮箱已被注册");
            }

            // 第四步：创建新用户对象
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(password);  // 注意：实际项目中应该使用PasswordUtil.encryptPassword()加密密码
            newUser.setEmail(email);
            newUser.setPhone(phone);
            // 其他字段（如createTime、updateTime、isDelete）会在Service层自动设置

            // 第五步：保存用户到数据库
            User savedUser = userService.save(newUser);
            if (savedUser == null) {
                return Result.error(500, "注册失败");
            }

            // 第六步：构建返回数据
            Map<String, Object> data = new HashMap<>();
            data.put("userId", savedUser.getId());          // 新用户ID
            data.put("username", savedUser.getUsername());  // 用户名
            data.put("email", savedUser.getEmail());        // 邮箱
            data.put("message", "注册成功");                 // 成功提示

            return Result.success(data);

        } catch (Exception e) {
            // 记录异常信息，便于调试
            e.printStackTrace();
            return Result.error(500, "注册失败：" + e.getMessage());
        }
    }

    /**
     * 强制用户下线接口（管理员功能）
     *
     * 该接口用于管理员强制指定用户下线，是一个管理功能
     * 被踢下线的用户需要重新登录才能继续使用系统
     *
     * 功能说明：
     * - 管理员可以强制任意用户下线
     * - 被踢用户的Token会立即失效
     * - 被踢用户正在进行的操作会被中断
     * - 被踢用户需要重新登录
     *
     * 权限要求：
     * - 当前用户必须已登录
     * - 建议添加管理员角色验证（代码中已预留）
     * - 可以扩展为只允许踢除权限低于自己的用户
     *
     * 使用场景：
     * - 发现异常登录行为时强制下线
     * - 用户违规操作时的惩罚措施
     * - 系统维护时清理在线用户
     * - 账户安全问题时的紧急处理
     *
     * 安全考虑：
     * - 应该记录踢人操作的日志
     * - 可以添加踢人原因参数
     * - 建议通知被踢用户下线原因
     *
     * @param userId 要强制下线的用户ID
     * @return Result<String> 操作结果
     *
     * @apiNote POST /auth/kickout
     * @apiExample 请求示例：
     * POST /auth/kickout?userId=2
     * @apiExample 响应示例：
     * {
     *   "code": 200,
     *   "message": "操作成功",
     *   "data": "操作成功"
     * }
     */
    @PostMapping("/kickout")
    public Result<String> kickout(@RequestParam Integer userId) {
        try {
            // 第一步：检查当前用户是否已登录
            StpUtil.checkLogin();

            // 第二步：权限验证（建议启用）
            // 确保只有管理员可以执行踢人操作
            // StpUtil.checkRole("admin");
            // 或者使用权限验证：
            // StpUtil.checkPermission("admin:kickout");

            // 第三步：参数验证
            if (userId == null || userId <= 0) {
                return Result.error(400, "用户ID无效");
            }

            // 第四步：执行踢人操作
            // SA-Token会自动清除指定用户的登录状态
            StpUtil.kickout(userId);

            // 可以在这里添加操作日志记录
            // logService.recordKickoutOperation(StpUtil.getLoginIdAsInt(), userId);

            return Result.success("操作成功");

        } catch (Exception e) {
            // 记录异常信息
            e.printStackTrace();
            return Result.error(500, "操作失败：" + e.getMessage());
        }
    }
}
