package com.viper.demo.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.viper.demo.Pojo.User;
import com.viper.demo.Service.UserService;

import java.util.Date;

/**
 * 数据初始化器配置类
 *
 * 该类实现了CommandLineRunner接口，会在Spring Boot应用启动完成后自动执行
 * 主要用途：
 * 1. 在应用首次启动时自动创建默认的测试用户数据
 * 2. 确保系统有基础的用户数据可供测试和演示使用
 * 3. 避免每次重新部署后都需要手动创建测试账户
 *
 * 注意：此类仅用于开发和测试环境，生产环境应该移除或禁用
 *
 * @author Viper
 * @version 1.0
 * @since 2024
 */
@Component
public class DataInitializer implements CommandLineRunner {

    /**
     * 用户服务层对象，用于执行用户相关的业务操作
     * 通过@Autowired注解实现依赖注入
     */
    @Autowired
    private UserService userService;

    /**
     * CommandLineRunner接口的实现方法
     * 该方法会在Spring Boot应用启动完成后自动调用
     *
     * @param args 命令行参数（本方法中未使用）
     * @throws Exception 可能抛出的异常
     */
    @Override
    public void run(String... args) throws Exception {
        // 初始化管理员用户
        initAdminUser();

        // 初始化测试用户
        initTestUser();
    }

    /**
     * 初始化管理员用户
     * 检查数据库中是否已存在用户名为"admin"的用户
     * 如果不存在，则创建一个默认的管理员账户
     */
    private void initAdminUser() {
        // 检查是否已存在admin用户，避免重复创建
        if (!userService.existsByUsername("admin")) {
            // 创建新的用户对象
            User admin = new User();

            // 设置用户基本信息
            admin.setUsername("admin");                    // 用户名：admin
            admin.setPassword("123456");                   // 密码：123456（注意：实际项目中应该使用加密密码）
            admin.setEmail("admin@example.com");           // 邮箱地址
            admin.setPhone("13800138000");                 // 手机号码

            // 设置系统字段
            admin.setIsDelete(0);                          // 删除标记：0表示未删除，1表示已删除
            admin.setCreateTime(new Date());               // 创建时间：当前时间
            admin.setUpdateTime(new Date());               // 更新时间：当前时间

            // 保存用户到数据库
            userService.save(admin);

            // 输出创建成功的提示信息
            System.out.println("✅ 数据初始化：创建管理员用户成功 -> 用户名: admin, 密码: 123456");
        } else {
            System.out.println("ℹ️  数据初始化：管理员用户已存在，跳过创建");
        }
    }

    /**
     * 初始化测试用户
     * 检查数据库中是否已存在用户名为"test"的用户
     * 如果不存在，则创建一个默认的测试账户
     */
    private void initTestUser() {
        // 检查是否已存在test用户，避免重复创建
        if (!userService.existsByUsername("test")) {
            // 创建新的用户对象
            User test = new User();

            // 设置用户基本信息
            test.setUsername("test");                      // 用户名：test
            test.setPassword("123456");                    // 密码：123456（注意：实际项目中应该使用加密密码）
            test.setEmail("test@example.com");             // 邮箱地址
            test.setPhone("13800138001");                  // 手机号码

            // 设置系统字段
            test.setIsDelete(0);                           // 删除标记：0表示未删除，1表示已删除
            test.setCreateTime(new Date());                // 创建时间：当前时间
            test.setUpdateTime(new Date());                // 更新时间：当前时间

            // 保存用户到数据库
            userService.save(test);

            // 输出创建成功的提示信息
            System.out.println("✅ 数据初始化：创建测试用户成功 -> 用户名: test, 密码: 123456");
        } else {
            System.out.println("ℹ️  数据初始化：测试用户已存在，跳过创建");
        }
    }
}
