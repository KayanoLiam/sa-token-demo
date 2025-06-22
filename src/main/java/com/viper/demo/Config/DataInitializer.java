package com.viper.demo.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.viper.demo.Pojo.User;
import com.viper.demo.Service.UserService;

import java.util.Date;

/**
 * 数据初始化器，用于创建测试用户
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Override
    public void run(String... args) throws Exception {
        // 检查是否已存在admin用户，如果不存在则创建
        if (!userService.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword("123456"); // 实际项目中应该加密
            admin.setEmail("admin@example.com");
            admin.setPhone("13800138000");
            admin.setIsDelete(0);
            admin.setCreateTime(new Date());
            admin.setUpdateTime(new Date());
            
            userService.save(admin);
            System.out.println("创建管理员用户: admin/123456");
        }

        // 检查是否已存在test用户，如果不存在则创建
        if (!userService.existsByUsername("test")) {
            User test = new User();
            test.setUsername("test");
            test.setPassword("123456"); // 实际项目中应该加密
            test.setEmail("test@example.com");
            test.setPhone("13800138001");
            test.setIsDelete(0);
            test.setCreateTime(new Date());
            test.setUpdateTime(new Date());
            
            userService.save(test);
            System.out.println("创建测试用户: test/123456");
        }
    }
}
