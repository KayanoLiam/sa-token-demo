# SA-Token 登录认证 API 文档

## 项目概述

本项目基于 Spring Boot 3.4.7 和 SA-Token 1.44.0 实现了完整的用户认证和权限管理系统。

## 技术栈

- Spring Boot 3.4.7
- SA-Token 1.44.0
- Spring Data JPA
- MySQL
- Lombok

## 默认测试用户

系统启动时会自动创建以下测试用户：

| 用户名 | 密码   | 角色  | 邮箱              |
|--------|--------|-------|-------------------|
| admin  | 123456 | admin | admin@example.com |
| test   | 123456 | user  | test@example.com  |

## API 接口文档

### 1. 认证相关接口

#### 1.1 用户登录

**接口地址：** `POST /auth/login`

**请求参数：**
```json
{
    "username": "admin",
    "password": "123456"
}
```

**响应示例：**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9...",
        "userId": 1,
        "username": "admin",
        "email": "admin@example.com",
        "phone": "13800138000"
    }
}
```

#### 1.2 用户注册

**接口地址：** `POST /auth/register`

**请求参数：**
```json
{
    "username": "newuser",
    "password": "123456",
    "email": "newuser@example.com",
    "phone": "13800138002"
}
```

**响应示例：**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "userId": 3,
        "username": "newuser",
        "email": "newuser@example.com",
        "message": "注册成功"
    }
}
```

#### 1.3 检查登录状态

**接口地址：** `GET /auth/isLogin`

**响应示例：**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "isLogin": true,
        "userId": 1,
        "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9..."
    }
}
```

#### 1.4 获取当前用户信息

**接口地址：** `GET /auth/userInfo`

**请求头：** `satoken: {token}`

**响应示例：**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "id": 1,
        "username": "admin",
        "email": "admin@example.com",
        "phone": "13800138000",
        "isDelete": 0,
        "createTime": "2024-01-01T10:00:00",
        "updateTime": "2024-01-01T10:00:00"
    }
}
```

#### 1.5 用户注销

**接口地址：** `POST /auth/logout`

**请求头：** `satoken: {token}`

**响应示例：**
```json
{
    "code": 200,
    "message": "success",
    "data": "注销成功"
}
```

### 2. 用户管理接口

#### 2.1 获取用户个人信息

**接口地址：** `GET /user/profile`

**权限要求：** 需要登录

**请求头：** `satoken: {token}`

#### 2.2 更新用户信息

**接口地址：** `PUT /user/profile`

**权限要求：** 需要 `user:update` 权限

**请求头：** `satoken: {token}`

**请求参数：**
```json
{
    "email": "newemail@example.com",
    "phone": "13800138999"
}
```

#### 2.3 获取用户列表（管理员）

**接口地址：** `GET /user/list`

**权限要求：** 需要 `admin` 角色

**请求头：** `satoken: {token}`

#### 2.4 删除用户（管理员）

**接口地址：** `DELETE /user/{userId}`

**权限要求：** 需要 `admin` 角色和 `user:delete` 权限

**请求头：** `satoken: {token}`

#### 2.5 获取用户权限信息

**接口地址：** `GET /user/permissions`

**权限要求：** 需要登录

**请求头：** `satoken: {token}`

**响应示例：**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "permissions": ["user:info", "user:update", "user:delete", "user:list", "admin:dashboard"],
        "roles": ["admin"],
        "userId": 1
    }
}
```

#### 2.6 管理员控制台

**接口地址：** `GET /user/admin/dashboard`

**权限要求：** 需要 `admin` 角色

**请求头：** `satoken: {token}`

## 权限说明

### 角色权限

- **admin**: 管理员角色，拥有所有权限
- **user**: 普通用户角色，拥有基础权限

### 权限列表

- `user:info`: 查看用户信息
- `user:update`: 更新用户信息
- `user:delete`: 删除用户
- `user:list`: 查看用户列表
- `admin:dashboard`: 访问管理员控制台

## 使用示例

### 1. 登录获取Token

```bash
curl -X POST http://localhost:9191/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'
```

### 2. 使用Token访问受保护接口

```bash
curl -X GET http://localhost:9191/user/profile \
  -H "satoken: {从登录接口获取的token}"
```

### 3. 管理员功能

```bash
# 获取用户列表
curl -X GET http://localhost:9191/user/list \
  -H "satoken: {admin用户的token}"

# 删除用户
curl -X DELETE http://localhost:9191/user/2 \
  -H "satoken: {admin用户的token}"
```

## 错误码说明

- `200`: 成功
- `400`: 请求参数错误
- `401`: 未登录或登录失败
- `403`: 权限不足
- `404`: 资源不存在
- `500`: 服务器内部错误

## 注意事项

1. 所有需要认证的接口都需要在请求头中携带 `satoken` 字段
2. Token 有效期为 30 天（可在配置文件中修改）
3. 密码目前使用明文存储，生产环境中应该使用加密存储
4. 系统支持同一账号多地登录
5. 管理员不能删除自己的账号
