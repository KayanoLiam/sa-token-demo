package com.viper.demo.Config;

import cn.dev33.satoken.stp.StpInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.viper.demo.Pojo.User;
import com.viper.demo.Service.UserService;

import java.util.ArrayList;
import java.util.List;

/**
 * SA-Token权限认证接口实现类
 *
 * 该类实现了SA-Token框架的StpInterface接口，用于自定义权限和角色的获取逻辑
 * SA-Token框架会在进行权限验证时调用此类的方法来获取用户的权限和角色信息
 *
 * 主要功能：
 * 1. 根据用户登录ID获取用户的权限列表
 * 2. 根据用户登录ID获取用户的角色列表
 * 3. 支持基于用户名的权限和角色分配策略
 * 4. 提供灵活的权限扩展机制
 *
 * 权限设计说明：
 * - 基础权限：所有登录用户都拥有的基本权限
 * - 管理员权限：只有admin用户才拥有的高级权限
 * - 权限格式：采用"模块:操作"的格式，如"user:info"、"user:delete"
 *
 * 角色设计说明：
 * - admin：管理员角色，拥有所有权限
 * - user：普通用户角色，拥有基础权限
 *
 * 使用场景：
 * - @SaCheckPermission("user:info") 注解会调用getPermissionList方法
 * - @SaCheckRole("admin") 注解会调用getRoleList方法
 * - StpUtil.hasPermission("user:delete") 会调用getPermissionList方法
 *
 * 注意事项：
 * 1. 此类必须标注@Component注解，让Spring管理
 * 2. 权限和角色的获取应该尽量高效，避免复杂的数据库查询
 * 3. 异常处理要完善，避免影响正常的权限验证流程
 * 4. 实际项目中可以结合数据库设计更复杂的权限模型
 *
 * @author Viper
 * @version 1.0
 * @since 2024
 */
@Component
public class StpInterfaceImpl implements StpInterface {

    /**
     * 用户服务层对象，用于查询用户信息
     * 通过@Autowired注解实现依赖注入
     */
    @Autowired
    private UserService userService;

    /**
     * 获取指定账号的权限码集合
     *
     * 该方法会在SA-Token进行权限验证时被调用，用于获取当前登录用户的所有权限
     * 权限验证流程：用户访问需要权限的接口 -> SA-Token调用此方法获取权限列表 -> 检查是否包含所需权限
     *
     * 权限分配策略：
     * 1. 所有用户都拥有基础权限：user:info（查看个人信息）、user:update（更新个人信息）
     * 2. admin用户额外拥有管理权限：user:delete（删除用户）、user:list（用户列表）、admin:dashboard（管理面板）
     *
     * @param loginId 登录用户的ID，通常是用户的主键ID
     * @param loginType 登录类型，用于区分不同的登录方式（如PC端、移动端等），本项目中暂未使用
     * @return 权限码列表，格式为"模块:操作"，如["user:info", "user:update"]
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // 初始化权限列表
        List<String> permissionList = new ArrayList<>();

        try {
            // 将登录ID转换为整数类型的用户ID
            Integer userId = Integer.parseInt(loginId.toString());

            // 根据用户ID查询用户信息
            User user = userService.findById(userId);

            if (user != null && (user.getIsDelete() == null || user.getIsDelete() == 0)) {
                // 为所有正常用户分配基础权限
                permissionList.add("user:info");        // 查看个人信息权限
                permissionList.add("user:update");      // 更新个人信息权限

                // 根据用户名分配特殊权限（实际项目中可以基于角色表或权限表）
                if ("admin".equals(user.getUsername())) {
                    // 管理员用户的额外权限
                    permissionList.add("user:delete");      // 删除用户权限
                    permissionList.add("user:list");        // 查看用户列表权限
                    permissionList.add("admin:dashboard");  // 管理面板访问权限
                    permissionList.add("admin:kickout");    // 踢人下线权限
                }

                // 这里可以扩展更多的权限分配逻辑
                // 例如：根据用户的部门、职位、VIP等级等分配不同权限
            }
        } catch (NumberFormatException e) {
            // 登录ID格式错误
            System.err.println("权限获取失败：登录ID格式错误 - " + loginId);
        } catch (Exception e) {
            // 其他异常，记录日志但不影响系统运行
            System.err.println("权限获取异常：" + e.getMessage());
            e.printStackTrace();
        }

        return permissionList;
    }

    /**
     * 获取指定账号的角色标识集合
     *
     * 该方法会在SA-Token进行角色验证时被调用，用于获取当前登录用户的所有角色
     * 角色验证流程：用户访问需要角色的接口 -> SA-Token调用此方法获取角色列表 -> 检查是否包含所需角色
     *
     * 角色与权限的区别：
     * - 角色：用户的身份标识，如"admin"、"user"、"vip"等
     * - 权限：用户可以执行的具体操作，如"user:delete"、"order:create"等
     * - 一个角色可以包含多个权限，一个用户可以拥有多个角色
     *
     * 角色分配策略：
     * 1. admin用户：拥有"admin"角色
     * 2. 其他用户：拥有"user"角色
     *
     * @param loginId 登录用户的ID，通常是用户的主键ID
     * @param loginType 登录类型，用于区分不同的登录方式（如PC端、移动端等），本项目中暂未使用
     * @return 角色标识列表，如["admin"]、["user"]
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        // 初始化角色列表
        List<String> roleList = new ArrayList<>();

        try {
            // 将登录ID转换为整数类型的用户ID
            Integer userId = Integer.parseInt(loginId.toString());

            // 根据用户ID查询用户信息
            User user = userService.findById(userId);

            if (user != null && (user.getIsDelete() == null || user.getIsDelete() == 0)) {
                // 根据用户名分配角色（实际项目中可以基于用户角色关联表）
                if ("admin".equals(user.getUsername())) {
                    // 管理员角色
                    roleList.add("admin");
                } else {
                    // 普通用户角色
                    roleList.add("user");
                }

                // 这里可以扩展更多的角色分配逻辑
                // 例如：
                // - 根据用户的VIP等级添加"vip"角色
                // - 根据用户的部门添加"finance"、"hr"等角色
                // - 根据用户的认证状态添加"verified"角色
            }
        } catch (NumberFormatException e) {
            // 登录ID格式错误
            System.err.println("角色获取失败：登录ID格式错误 - " + loginId);
        } catch (Exception e) {
            // 其他异常，记录日志但不影响系统运行
            System.err.println("角色获取异常：" + e.getMessage());
            e.printStackTrace();
        }

        return roleList;
    }
}
