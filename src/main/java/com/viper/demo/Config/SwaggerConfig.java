package com.viper.demo.Config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger API文档配置类
 * 
 * 该配置类用于设置SpringDoc OpenAPI 3.0的相关配置
 * 提供可视化的API文档界面，便于前端开发和接口测试
 * 
 * 主要功能：
 * 1. 配置API文档基本信息（标题、描述、版本等）
 * 2. 配置安全认证方案（SA-Token）
 * 3. 设置联系人和许可证信息
 * 4. 自定义文档样式和行为
 * 
 * 访问地址：
 * - Swagger UI: http://localhost:9191/swagger-ui/index.html
 * - API文档JSON: http://localhost:9191/v3/api-docs
 * 
 * 使用说明：
 * - 启动应用后访问Swagger UI地址
 * - 可以直接在页面上测试API接口
 * - 支持SA-Token认证，需要先登录获取Token
 * 
 * @author Viper
 * @version 1.0
 * @since 2024
 */
@Configuration
public class SwaggerConfig {

    /**
     * 配置OpenAPI文档信息
     * 
     * 该方法创建并配置OpenAPI对象，定义API文档的基本信息
     * 包括项目描述、版本信息、联系方式、安全认证等
     * 
     * @return OpenAPI配置对象
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // 配置API基本信息
                .info(new Info()
                        .title("SA-Token 演示项目 API文档")
                        .description("""
                                # SA-Token 演示项目 RESTful API
                                
                                这是一个基于Spring Boot和SA-Token框架的用户认证和权限管理演示项目。
                                
                                ## 主要功能
                                - 用户注册、登录、注销
                                - 基于SA-Token的权限认证
                                - 用户信息管理
                                - 角色和权限控制
                                
                                ## 认证说明
                                1. 通过 `/auth/login` 接口登录获取Token
                                2. 在请求头中添加 `satoken: {token}` 或 `Authorization: Bearer {token}`
                                3. 部分接口需要特定权限或角色
                                
                                ## 测试账户
                                - 管理员：admin/123456
                                - 普通用户：test/123456
                                
                                ## 技术栈
                                - Spring Boot 3.4.7
                                - SA-Token 1.44.0
                                - Spring Data JPA
                                - MySQL 8.0
                                - SpringDoc OpenAPI 3
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Viper")
                                .email("viper@example.com")
                                .url("https://github.com/KayanoLiam/sa-token-demo"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                
                // 配置安全认证
                .addSecurityItem(new SecurityRequirement().addList("SA-Token"))
                .components(new Components()
                        .addSecuritySchemes("SA-Token", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("satoken")
                                .description("SA-Token认证，请先通过登录接口获取Token，然后在此处输入Token值")));
    }
}
