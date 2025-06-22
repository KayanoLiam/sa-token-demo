package com.viper.demo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import cn.dev33.satoken.stp.StpUtil;
import com.viper.demo.Pojo.*;
import com.viper.demo.Service.UserService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录接口
     * @param loginRequest 登录请求参数
     * @return 登录结果
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> doLogin(@RequestBody LoginRequest loginRequest) {
        try {
            // 参数校验
            if (loginRequest == null) {
                return Result.error(400, "请求参数不能为空");
            }
            if (loginRequest.getUsername() == null || loginRequest.getUsername().trim().isEmpty()) {
                return Result.error(400, "用户名不能为空");
            }
            if (loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
                return Result.error(400, "密码不能为空");
            }

            String username = loginRequest.getUsername().trim();
            String password = loginRequest.getPassword();

            // 根据用户名查询用户
            User user = userService.findByUsername(username);
            if (user == null) {
                return Result.error(401, "用户名或密码错误");
            }

            // 检查用户是否被删除
            if (user.getIsDelete() != null && user.getIsDelete() == 1) {
                return Result.error(403, "账号已被禁用");
            }

            // 验证密码（这里简单比较，实际项目中应该使用加密后的密码比较）
            if (!password.equals(user.getPassword())) {
                return Result.error(401, "用户名或密码错误");
            }

            // 执行登录，使用用户ID作为登录标识
            StpUtil.login(user.getId());

            // 构建返回数据
            Map<String, Object> data = new HashMap<>();
            data.put("token", StpUtil.getTokenValue());
            data.put("userId", user.getId());
            data.put("username", user.getUsername());
            data.put("email", user.getEmail());
            data.put("phone", user.getPhone());

            return Result.success(data);

        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(500, "登录失败：" + e.getMessage());
        }
    }

    /**
     * 查询登录状态
     * @return 登录状态
     */
    @GetMapping("/isLogin")
    public Result<Map<String, Object>> isLogin() {
        Map<String, Object> data = new HashMap<>();
        data.put("isLogin", StpUtil.isLogin());

        if (StpUtil.isLogin()) {
            data.put("userId", StpUtil.getLoginId());
            data.put("token", StpUtil.getTokenValue());
        }

        return Result.success(data);
    }

    /**
     * 获取当前登录用户信息
     * @return 用户信息
     */
    @GetMapping("/userInfo")
    public Result<User> getUserInfo() {
        try {
            // 检查是否登录
            StpUtil.checkLogin();

            // 获取当前登录用户ID
            Integer userId = StpUtil.getLoginIdAsInt();

            // 查询用户信息
            User user = userService.findById(userId);
            if (user == null) {
                return Result.error(404, "用户不存在");
            }

            // 清空密码字段，不返回给前端
            user.setPassword(null);

            return Result.success(user);

        } catch (Exception e) {
            return Result.error(401, "请先登录");
        }
    }

    /**
     * 用户注销
     * @return 注销结果
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        try {
            StpUtil.logout();
            return Result.success("注销成功");
        } catch (Exception e) {
            return Result.error(500, "注销失败：" + e.getMessage());
        }
    }

    /**
     * 用户注册接口
     * @param registerRequest 注册请求参数
     * @return 注册结果
     */
    @PostMapping("/register")
    public Result<Map<String, Object>> register(@RequestBody RegisterRequest registerRequest) {
        try {
            // 参数校验
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

            String username = registerRequest.getUsername().trim();
            String password = registerRequest.getPassword();
            String email = registerRequest.getEmail().trim();
            String phone = registerRequest.getPhone();

            // 检查用户名是否已存在
            if (userService.existsByUsername(username)) {
                return Result.error(400, "用户名已存在");
            }

            // 检查邮箱是否已存在
            if (userService.existsByEmail(email)) {
                return Result.error(400, "邮箱已被注册");
            }

            // 创建新用户
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(password); // 实际项目中应该加密密码
            newUser.setEmail(email);
            newUser.setPhone(phone);

            // 保存用户
            User savedUser = userService.save(newUser);
            if (savedUser == null) {
                return Result.error(500, "注册失败");
            }

            // 构建返回数据
            Map<String, Object> data = new HashMap<>();
            data.put("userId", savedUser.getId());
            data.put("username", savedUser.getUsername());
            data.put("email", savedUser.getEmail());
            data.put("message", "注册成功");

            return Result.success(data);

        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(500, "注册失败：" + e.getMessage());
        }
    }

    /**
     * 踢人下线（管理员功能）
     * @param userId 要踢下线的用户ID
     * @return 操作结果
     */
    @PostMapping("/kickout")
    public Result<String> kickout(@RequestParam Integer userId) {
        try {
            // 检查当前用户是否登录
            StpUtil.checkLogin();

            // 这里可以添加权限检查，确保只有管理员可以踢人
            // StpUtil.checkRole("admin");

            // 踢人下线
            StpUtil.kickout(userId);

            return Result.success("操作成功");
        } catch (Exception e) {
            return Result.error(500, "操作失败：" + e.getMessage());
        }
    }
}
