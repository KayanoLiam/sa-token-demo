package com.viper.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 测试套件 - 简单的测试运行器
 *
 * 这是一个简化的测试套件。要运行所有测试，请使用Maven命令。
 * 详细的测试运行说明请参考 TEST_DOCUMENTATION.md 文件。
 */
@SpringBootTest
public class TestSuite {

    @Test
    void testSuiteInfo() {
        System.out.println("=== SA-Token Demo 测试套件 ===");
        System.out.println("本项目包含以下测试类：");
        System.out.println("1. Pojo Tests: UserTest, ResultTest, LoginRequestTest, RegisterRequestTest");
        System.out.println("2. Utils Tests: PasswordUtilTest");
        System.out.println("3. Repository Tests: UserRepositoryTest");
        System.out.println("4. Service Tests: UserServiceTest");
        System.out.println("5. Config Tests: StpInterfaceImplTest");
        System.out.println("6. Controller Tests: AuthControllerTest, UserControllerTest");
        System.out.println("7. Integration Tests: AuthIntegrationTest");
        System.out.println("8. Application Tests: SaTokenDemoApplicationTests");
        System.out.println("使用 './mvnw test' 命令运行所有测试");
        System.out.println("详细说明请查看 TEST_DOCUMENTATION.md 文件");
    }
}
