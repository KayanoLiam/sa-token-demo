# SA-Token 登录认证系统 - 快速指南

<div align="center">

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.7-brightgreen)
![SA-Token](https://img.shields.io/badge/SA--Token-1.44.0-blue)

一个基于 Spring Boot 3 和 SA-Token 的现代化登录认证系统

</div>

## 🚀 快速开始

### 1. 环境要求
- Java 21+
- Maven 3.6+
- MySQL 8.0+

### 2. 克隆并运行
```bash
git clone <repository-url>
cd sa-token-demo

# 配置数据库
# 修改 src/main/resources/application.properties 中的数据库配置

# 运行项目
./mvnw spring-boot:run
```

### 3. 测试接口
```bash
# 登录
curl -X POST http://localhost:9191/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'

# 检查登录状态
curl http://localhost:9191/auth/isLogin
```

## 📖 主要功能

- ✅ 用户注册/登录/注销
- ✅ JWT Token 认证
- ✅ 基于角色的权限控制
- ✅ 用户信息管理
- ✅ 密码加密存储
- ✅ 完整的单元测试

## 🧪 运行测试

```bash
# 运行所有测试
./mvnw test

# 运行特定测试
./mvnw test -Dtest=UserServiceTest

# 使用测试脚本
chmod +x run-tests.sh
./run-tests.sh
```

## 📚 API接口

### 认证接口
- `POST /auth/login` - 用户登录
- `POST /auth/register` - 用户注册
- `GET /auth/isLogin` - 检查登录状态
- `POST /auth/logout` - 用户注销

### 用户管理
- `GET /user/profile` - 获取用户信息
- `PUT /user/profile` - 更新用户信息
- `GET /user/list` - 获取用户列表（管理员）

## 🔧 配置

### 数据库配置
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/sa_token_demo
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### SA-Token配置
```properties
sa-token.token-name=satoken
sa-token.timeout=2592000
sa-token.is-concurrent=true
```

## 🐳 Docker部署

```bash
# 构建镜像
./mvnw clean package -DskipTests
docker build -t sa-token-demo .

# 运行容器
docker run -d -p 9191:9191 sa-token-demo
```

## 📁 项目结构

```
src/
├── main/java/com/viper/demo/
│   ├── Controller/          # 控制器层
│   ├── Service/             # 业务逻辑层
│   ├── Repository/          # 数据访问层
│   ├── Pojo/                # 实体类和DTO
│   ├── Config/              # 配置类
│   └── Utils/               # 工具类
└── test/                    # 测试代码
```

## 📊 测试统计

- **测试文件**: 12个
- **测试方法**: 173个
- **代码覆盖率**: 90%+

## 🔗 相关文档

- [完整README](README.md) - 详细的项目文档
- [API文档](docs/API_DOCUMENTATION.md) - 接口说明
- [测试文档](docs/TEST_DOCUMENTATION.md) - 测试指南
- [部署指南](docs/DEPLOYMENT.md) - 部署说明

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

## 📄 许可证

MIT License

---

<div align="center">

**如果这个项目对您有帮助，请给我们一个 ⭐️**

</div>
