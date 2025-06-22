package com.viper.demo.Controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import com.viper.demo.Pojo.Result;
import com.viper.demo.Pojo.User;
import com.viper.demo.Service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取当前用户信息（需要登录）
     */
    @SaCheckLogin
    @GetMapping("/profile")
    public Result<User> getUserProfile() {
        try {
            Integer userId = StpUtil.getLoginIdAsInt();
            User user = userService.findById(userId);
            if (user != null) {
                // 清空密码字段
                user.setPassword(null);
                return Result.success(user);
            }
            return Result.error(404, "用户不存在");
        } catch (Exception e) {
            return Result.error(500, "获取用户信息失败：" + e.getMessage());
        }
    }

    /**
     * 更新用户信息（需要user:update权限）
     */
    @SaCheckPermission("user:update")
    @PutMapping("/profile")
    public Result<String> updateUserProfile(@RequestBody User user) {
        try {
            Integer userId = StpUtil.getLoginIdAsInt();
            User existingUser = userService.findById(userId);
            
            if (existingUser == null) {
                return Result.error(404, "用户不存在");
            }

            // 只允许更新部分字段
            existingUser.setEmail(user.getEmail());
            existingUser.setPhone(user.getPhone());
            
            userService.update(existingUser);
            return Result.success("更新成功");
        } catch (Exception e) {
            return Result.error(500, "更新失败：" + e.getMessage());
        }
    }

    /**
     * 获取所有用户列表（需要admin角色）
     */
    @SaCheckRole("admin")
    @GetMapping("/list")
    public Result<List<User>> getUserList() {
        try {
            List<User> users = userService.findAll();
            // 清空所有用户的密码字段
            users.forEach(user -> user.setPassword(null));
            return Result.success(users);
        } catch (Exception e) {
            return Result.error(500, "获取用户列表失败：" + e.getMessage());
        }
    }

    /**
     * 删除用户（需要admin角色和user:delete权限）
     */
    @SaCheckRole("admin")
    @SaCheckPermission("user:delete")
    @DeleteMapping("/{userId}")
    public Result<String> deleteUser(@PathVariable Integer userId) {
        try {
            Integer currentUserId = StpUtil.getLoginIdAsInt();
            
            // 不能删除自己
            if (currentUserId.equals(userId)) {
                return Result.error(400, "不能删除自己");
            }

            boolean success = userService.deleteById(userId);
            if (success) {
                // 踢下线被删除的用户
                StpUtil.kickout(userId);
                return Result.success("删除成功");
            } else {
                return Result.error(404, "用户不存在");
            }
        } catch (Exception e) {
            return Result.error(500, "删除失败：" + e.getMessage());
        }
    }

    /**
     * 获取当前用户的权限和角色信息
     */
    @SaCheckLogin
    @GetMapping("/permissions")
    public Result<Map<String, Object>> getUserPermissions() {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("permissions", StpUtil.getPermissionList());
            data.put("roles", StpUtil.getRoleList());
            data.put("userId", StpUtil.getLoginId());
            return Result.success(data);
        } catch (Exception e) {
            return Result.error(500, "获取权限信息失败：" + e.getMessage());
        }
    }

    /**
     * 管理员控制台（需要admin角色）
     */
    @SaCheckRole("admin")
    @GetMapping("/admin/dashboard")
    public Result<Map<String, Object>> adminDashboard() {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("message", "欢迎进入管理员控制台");
            data.put("totalUsers", userService.findAll().size());
            data.put("currentAdmin", StpUtil.getLoginId());
            return Result.success(data);
        } catch (Exception e) {
            return Result.error(500, "获取控制台信息失败：" + e.getMessage());
        }
    }
}
