package com.viper.demo;

import cn.dev33.satoken.SaManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * SA-Token 演示项目主启动类
 *
 * 这是一个基于Spring Boot和SA-Token框架的用户认证和权限管理演示项目
 *
 * 项目主要功能：
 * 1. 用户注册、登录、注销功能
 * 2. 基于SA-Token的会话管理和权限控制
 * 3. 用户信息管理和状态控制
 * 4. RESTful API接口设计
 * 5. 完整的单元测试和集成测试
 *
 * 技术栈：
 * - Spring Boot 2.7.x：主框架
 * - SA-Token：轻量级权限认证框架
 * - Spring Data JPA：数据访问层
 * - MySQL：数据库
 * - JUnit 5：单元测试框架
 * - Mockito：Mock测试框架
 *
 * 项目结构：
 * - Controller：控制器层，处理HTTP请求
 * - Service：业务逻辑层，处理核心业务
 * - Repository：数据访问层，数据库操作
 * - Pojo：实体类和数据传输对象
 * - Config：配置类，系统配置和初始化
 * - Utils：工具类，通用功能封装
 *
 * 启动说明：
 * 1. 确保MySQL数据库已启动并创建了对应的数据库
 * 2. 修改application.properties中的数据库连接配置
 * 3. 运行此主类启动应用
 * 4. 访问 http://localhost:9191 进行测试
 *
 * 默认测试账户：
 * - 管理员：admin/123456
 * - 普通用户：test/123456
 *
 * @author Viper
 * @version 1.0
 * @since 2024
 */
@SpringBootApplication
public class SaTokenDemoApplication {

	/**
	 * 应用程序主入口方法
	 *
	 * 该方法负责启动Spring Boot应用程序，并在启动完成后输出SA-Token的配置信息
	 *
	 * 启动流程：
	 * 1. 初始化Spring容器
	 * 2. 自动配置各种组件（数据源、JPA、SA-Token等）
	 * 3. 执行CommandLineRunner实现类（如DataInitializer）
	 * 4. 启动内嵌的Tomcat服务器
	 * 5. 输出SA-Token配置信息用于调试
	 *
	 * @param args 命令行参数，可用于传递启动参数
	 *             例如：--server.port=8080 --spring.profiles.active=dev
	 */
	public static void main(String[] args) {
		// 启动Spring Boot应用
		SpringApplication.run(SaTokenDemoApplication.class, args);

		// 输出SA-Token配置信息，便于开发调试
		// 包含token名称、超时时间、并发登录设置等重要配置
		System.out.println("🚀 应用启动成功！");
		System.out.println("📋 SA-Token 配置信息：" + SaManager.getConfig());
		System.out.println("🌐 访问地址：http://localhost:9191");
		System.out.println("📖 API文档："+"http://localhost:9191/swagger-ui/index.html");
		System.out.println("🧪 测试账户：admin/123456 或 test/123456");
	}

}
