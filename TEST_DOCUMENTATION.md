# 单元测试文档

## 测试概述

本项目为SA-Token登录认证系统编写了全面的单元测试，覆盖了所有主要组件和方法。测试使用JUnit 5、Mockito和Spring Boot Test框架。

## 测试覆盖范围

### 1. Pojo类测试
- **UserTest.java** - 测试User实体类的所有方法
- **ResultTest.java** - 测试Result响应类的所有方法
- **LoginRequestTest.java** - 测试LoginRequest DTO类
- **RegisterRequestTest.java** - 测试RegisterRequest DTO类

### 2. 工具类测试
- **PasswordUtilTest.java** - 测试密码加密工具类的所有方法
  - 盐值生成测试
  - 密码加密测试
  - 密码验证测试
  - MD5加密测试

### 3. Repository层测试
- **UserRepositoryTest.java** - 测试用户数据访问层
  - 使用@DataJpaTest进行数据库层测试
  - 使用H2内存数据库
  - 测试所有自定义查询方法

### 4. Service层测试
- **UserServiceTest.java** - 测试用户服务层的所有方法
  - 使用Mockito模拟Repository依赖
  - 测试业务逻辑和异常处理
  - 覆盖所有边界条件

### 5. 配置类测试
- **StpInterfaceImplTest.java** - 测试SA-Token权限接口实现
  - 测试权限获取逻辑
  - 测试角色获取逻辑
  - 测试异常处理

### 6. Controller层测试
- **AuthControllerTest.java** - 测试认证控制器
  - 使用@WebMvcTest进行Web层测试
  - 测试登录、注册、注销等接口
  - 测试参数验证和异常处理

- **UserControllerTest.java** - 测试用户管理控制器
  - 测试用户信息管理接口
  - 测试权限控制

### 7. 集成测试
- **AuthIntegrationTest.java** - 端到端集成测试
  - 使用@SpringBootTest进行完整应用测试
  - 测试完整的认证流程
  - 使用H2内存数据库

### 8. 应用启动测试
- **SaTokenDemoApplicationTests.java** - 应用上下文加载测试

## 测试方法详细说明

### UserServiceTest 测试方法

| 方法名 | 测试内容 | 测试场景 |
|--------|----------|----------|
| testFindByUsername_Success | 根据用户名查找用户 | 正常查找成功 |
| testFindByUsername_NotFound | 根据用户名查找用户 | 用户不存在 |
| testFindByUsername_NullInput | 根据用户名查找用户 | 输入为null |
| testFindByUsername_EmptyInput | 根据用户名查找用户 | 输入为空字符串 |
| testFindById_Success | 根据ID查找用户 | 正常查找成功 |
| testFindById_NotFound | 根据ID查找用户 | 用户不存在 |
| testFindById_DeletedUser | 根据ID查找用户 | 用户已删除 |
| testFindById_NullInput | 根据ID查找用户 | 输入为null |
| testFindByEmail_Success | 根据邮箱查找用户 | 正常查找成功 |
| testFindByEmail_DeletedUser | 根据邮箱查找用户 | 用户已删除 |
| testFindByPhone_Success | 根据手机号查找用户 | 正常查找成功 |
| testSave_NewUser | 保存新用户 | 正常保存 |
| testSave_NullInput | 保存用户 | 输入为null |
| testUpdate_Success | 更新用户 | 正常更新 |
| testUpdate_NullInput | 更新用户 | 输入为null |
| testUpdate_NullId | 更新用户 | ID为null |
| testDeleteById_Success | 删除用户 | 正常删除 |
| testDeleteById_NotFound | 删除用户 | 用户不存在 |
| testDeleteById_NullInput | 删除用户 | 输入为null |
| testExistsByUsername_True | 检查用户名存在 | 用户名存在 |
| testExistsByUsername_False | 检查用户名存在 | 用户名不存在 |
| testExistsByEmail_True | 检查邮箱存在 | 邮箱存在 |
| testFindAll | 查找所有用户 | 正常查找 |
| testValidatePassword_Success | 验证密码 | 密码正确 |
| testValidatePassword_WrongPassword | 验证密码 | 密码错误 |
| testValidatePassword_UserNotFound | 验证密码 | 用户不存在 |
| testValidatePassword_NullInputs | 验证密码 | 输入为null |

### AuthControllerTest 测试方法

| 方法名 | 测试内容 | 测试场景 |
|--------|----------|----------|
| testDoLogin_Success | 用户登录 | 登录成功 |
| testDoLogin_UserNotFound | 用户登录 | 用户不存在 |
| testDoLogin_WrongPassword | 用户登录 | 密码错误 |
| testDoLogin_DeletedUser | 用户登录 | 用户已删除 |
| testDoLogin_EmptyUsername | 用户登录 | 用户名为空 |
| testDoLogin_EmptyPassword | 用户登录 | 密码为空 |
| testDoLogin_NullRequest | 用户登录 | 请求为null |
| testRegister_Success | 用户注册 | 注册成功 |
| testRegister_UsernameExists | 用户注册 | 用户名已存在 |
| testRegister_EmailExists | 用户注册 | 邮箱已存在 |
| testRegister_EmptyUsername | 用户注册 | 用户名为空 |
| testRegister_EmptyPassword | 用户注册 | 密码为空 |
| testRegister_EmptyEmail | 用户注册 | 邮箱为空 |
| testRegister_SaveFailed | 用户注册 | 保存失败 |
| testIsLogin | 检查登录状态 | 正常检查 |
| testLogout | 用户注销 | 正常注销 |

### PasswordUtilTest 测试方法

| 方法名 | 测试内容 | 测试场景 |
|--------|----------|----------|
| testGenerateSalt | 生成盐值 | 正常生成 |
| testEncryptPassword | 加密密码 | 正常加密 |
| testEncryptPassword_DifferentSalts | 加密密码 | 不同盐值 |
| testEncryptPassword_DifferentPasswords | 加密密码 | 不同密码 |
| testVerifyPassword_Success | 验证密码 | 验证成功 |
| testVerifyPassword_WrongPassword | 验证密码 | 密码错误 |
| testVerifyPassword_WrongSalt | 验证密码 | 盐值错误 |
| testMd5Encrypt | MD5加密 | 正常加密 |
| testMd5Encrypt_DifferentPasswords | MD5加密 | 不同密码 |
| testMd5Encrypt_EmptyString | MD5加密 | 空字符串 |
| testCompleteWorkflow | 完整工作流程 | 端到端测试 |

## 运行测试

### 运行所有测试
```bash
./mvnw test
```

### 运行特定测试类
```bash
./mvnw test -Dtest=UserServiceTest
```

### 运行测试套件
```bash
./mvnw test -Dtest=TestSuite
```

### 生成测试报告
```bash
./mvnw test jacoco:report
```

## 测试配置

### 测试环境配置
- 使用H2内存数据库进行测试
- 配置文件：`src/test/resources/application-test.properties`
- 测试时禁用数据初始化

### 测试依赖
- JUnit 5
- Mockito
- Spring Boot Test
- H2 Database
- TestContainers (可选)

## 测试最佳实践

### 1. 测试命名规范
- 测试方法命名：`test{MethodName}_{Scenario}`
- 测试类命名：`{ClassName}Test`

### 2. 测试结构
- **Given** - 准备测试数据
- **When** - 执行被测试方法
- **Then** - 验证结果

### 3. 测试覆盖
- 正常流程测试
- 异常流程测试
- 边界条件测试
- 空值和null测试

### 4. Mock使用
- 使用@Mock注解创建模拟对象
- 使用@InjectMocks注入依赖
- 使用when().thenReturn()设置模拟行为

## 测试数据管理

### 测试数据准备
- 使用@BeforeEach设置测试数据
- 使用@Transactional确保测试隔离
- 使用TestEntityManager管理测试数据

### 测试数据清理
- 使用@Transactional自动回滚
- 使用@DirtiesContext重置上下文
- 手动清理共享资源

## 持续集成

### CI/CD配置
```yaml
# GitHub Actions示例
- name: Run tests
  run: ./mvnw test
  
- name: Generate test report
  run: ./mvnw jacoco:report
  
- name: Upload coverage
  uses: codecov/codecov-action@v1
```

## 测试指标

### 目标覆盖率
- 行覆盖率：> 90%
- 分支覆盖率：> 85%
- 方法覆盖率：> 95%

### 测试质量指标
- 测试通过率：100%
- 测试执行时间：< 30秒
- 测试稳定性：无随机失败

## 故障排除

### 常见问题
1. **测试数据库连接失败**
   - 检查H2依赖是否正确添加
   - 确认测试配置文件路径正确

2. **Mock对象注入失败**
   - 检查@ExtendWith(MockitoExtension.class)注解
   - 确认@Mock和@InjectMocks注解使用正确

3. **SA-Token相关测试失败**
   - 检查SA-Token配置
   - 确认测试环境下的Token配置正确

### 调试技巧
- 使用@Disabled临时禁用测试
- 使用System.out.println()输出调试信息
- 使用IDE断点调试
- 查看测试日志输出
