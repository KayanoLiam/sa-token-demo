package com.viper.demo.Pojo;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

/**
 * 统一响应结果封装类
 *
 * 该类用于封装所有API接口的响应结果，提供统一的响应格式
 * 使用泛型支持不同类型的数据返回，提高代码复用性
 *
 * 响应格式：
 * {
 *   "code": 200,           // 响应状态码
 *   "message": "success",  // 响应消息
 *   "data": {...}          // 响应数据（泛型）
 * }
 *
 * 设计优势：
 * 1. 统一响应格式，便于前端处理
 * 2. 泛型设计，支持任意类型数据
 * 3. 静态工厂方法，简化对象创建
 * 4. 清晰的成功/失败状态区分
 *
 * 状态码规范：
 * - 200：操作成功
 * - 400：请求参数错误
 * - 401：未登录或认证失败
 * - 403：权限不足
 * - 404：资源不存在
 * - 500：服务器内部错误
 *
 * 使用场景：
 * - Controller层方法返回值
 * - API接口响应封装
 * - 前后端数据交互
 * - 错误信息传递
 *
 * 使用示例：
 * ```java
 * // 成功响应
 * return Result.success(userInfo);
 *
 * // 错误响应
 * return Result.error(400, "参数错误");
 * ```
 *
 * @param <T> 响应数据的类型，支持任意类型
 *
 * @author Viper
 * @version 1.0
 * @since 2024
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> {

    /**
     * 响应状态码
     *
     * 表示请求处理的结果状态
     * 遵循HTTP状态码规范，便于理解和处理
     *
     * 常用状态码：
     * - 200：成功
     * - 400：客户端错误（参数错误等）
     * - 401：未认证（未登录）
     * - 403：无权限
     * - 404：资源不存在
     * - 500：服务器错误
     *
     * 业务约定：
     * - 200表示业务处理成功
     * - 4xx表示客户端问题
     * - 5xx表示服务器问题
     */
    private Integer code;

    /**
     * 响应消息
     *
     * 对响应结果的文字描述
     * 成功时通常为"success"或具体的成功信息
     * 失败时为具体的错误描述
     *
     * 消息规范：
     * - 成功：简洁明了的成功提示
     * - 失败：具体的错误原因和解决建议
     * - 多语言：支持国际化（可扩展）
     * - 用户友好：避免技术术语，使用用户易懂的语言
     *
     * 示例：
     * - "操作成功"
     * - "用户名或密码错误"
     * - "权限不足，请联系管理员"
     */
    private String message;

    /**
     * 响应数据
     *
     * 实际的业务数据，使用泛型支持任意类型
     * 成功时包含具体的业务数据
     * 失败时通常为null或错误详情
     *
     * 数据类型：
     * - 单个对象：User、String等
     * - 集合类型：List<User>、Map<String, Object>等
     * - 基本类型：Integer、Boolean等
     * - 复杂对象：自定义DTO、VO等
     *
     * 设计原则：
     * - 成功时必须有意义的数据
     * - 失败时可以为null
     * - 数据结构要清晰明了
     * - 避免循环引用
     */
    private T data;

    /**
     * 创建成功响应的静态工厂方法
     *
     * 该方法用于创建表示操作成功的响应对象
     * 自动设置状态码为200，消息为"success"
     *
     * 使用场景：
     * - 数据查询成功
     * - 业务操作成功
     * - 用户登录成功
     * - 信息更新成功
     *
     * 方法特点：
     * - 静态方法，无需实例化
     * - 泛型方法，支持任意数据类型
     * - 统一的成功响应格式
     * - 简化代码编写
     *
     * @param <T> 响应数据的类型
     * @param data 要返回的业务数据，可以为null
     * @return 封装好的成功响应对象
     *
     * @apiExample 使用示例：
     * ```java
     * // 返回用户信息
     * return Result.success(user);
     *
     * // 返回用户列表
     * return Result.success(userList);
     *
     * // 返回简单消息
     * return Result.success("操作成功");
     *
     * // 无数据返回
     * return Result.success(null);
     * ```
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("success");
        result.setData(data);
        return result;
    }

    /**
     * 创建错误响应的静态工厂方法
     *
     * 该方法用于创建表示操作失败的响应对象
     * 需要指定具体的错误码和错误消息
     *
     * 使用场景：
     * - 参数验证失败
     * - 业务逻辑错误
     * - 权限验证失败
     * - 系统异常处理
     *
     * 错误码建议：
     * - 400：请求参数错误
     * - 401：未登录或登录失效
     * - 403：权限不足
     * - 404：资源不存在
     * - 500：服务器内部错误
     *
     * 消息建议：
     * - 具体明确的错误描述
     * - 用户友好的提示信息
     * - 避免暴露系统内部信息
     * - 提供解决问题的建议
     *
     * @param <T> 响应数据的类型（错误响应通常为null）
     * @param code 错误状态码，建议使用HTTP状态码规范
     * @param message 错误消息，应该清晰描述错误原因
     * @return 封装好的错误响应对象，data字段为null
     *
     * @apiExample 使用示例：
     * ```java
     * // 参数错误
     * return Result.error(400, "用户名不能为空");
     *
     * // 认证失败
     * return Result.error(401, "用户名或密码错误");
     *
     * // 权限不足
     * return Result.error(403, "权限不足，无法访问该资源");
     *
     * // 资源不存在
     * return Result.error(404, "用户不存在");
     *
     * // 系统错误
     * return Result.error(500, "系统繁忙，请稍后重试");
     * ```
     */
    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        // data字段保持为null，表示错误响应无业务数据
        return result;
    }
}
