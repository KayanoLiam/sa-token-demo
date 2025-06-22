package com.viper.demo.Pojo;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户实体类
 *
 * 该类表示系统中的用户信息，是用户管理功能的核心数据模型
 * 使用JPA注解映射到数据库表，支持完整的CRUD操作
 *
 * 数据库映射：
 * - 表名：user
 * - 主键：id（自增）
 * - 索引建议：username（唯一）、email（唯一）
 *
 * 字段说明：
 * - id：用户唯一标识，主键，自动生成
 * - username：用户名，用于登录，必须唯一
 * - password：密码，建议加密存储
 * - email：邮箱地址，用于登录和通知，必须唯一
 * - phone：手机号码，可选，用于短信验证
 * - isDelete：逻辑删除标记，0=未删除，1=已删除
 * - createTime：创建时间，记录用户注册时间
 * - updateTime：更新时间，记录最后修改时间
 *
 * 业务规则：
 * - 用户名和邮箱必须全局唯一
 * - 删除操作采用逻辑删除，不物理删除数据
 * - 密码应该加密存储，不能明文保存
 * - 时间字段由系统自动维护
 *
 * 使用的注解：
 * - @Entity：标记为JPA实体类
 * - @Table：指定数据库表名
 * - @Data：Lombok注解，自动生成getter/setter/toString/equals/hashCode
 * - @AllArgsConstructor：Lombok注解，生成全参构造函数
 * - @NoArgsConstructor：Lombok注解，生成无参构造函数
 *
 * 安全考虑：
 * - 密码字段在返回给前端时应该被清空
 * - 敏感信息不应该出现在日志中
 * - toString方法可能包含敏感信息，使用时需注意
 *
 * 扩展建议：
 * - 可添加用户状态字段（正常、锁定、待激活等）
 * - 可添加用户角色字段或关联角色表
 * - 可添加最后登录时间字段
 * - 可添加用户头像、昵称等个人信息字段
 *
 * @author Viper
 * @version 1.0
 * @since 2024
 */
@Data
@Table(name = "user")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class User {

    /**
     * 用户ID - 主键
     *
     * 用户的唯一标识符，由数据库自动生成
     * 使用自增策略，确保每个用户都有唯一的ID
     *
     * 数据库配置：
     * - 类型：INT
     * - 约束：PRIMARY KEY, AUTO_INCREMENT
     * - 非空：NOT NULL
     *
     * 使用场景：
     * - 用户身份识别
     * - 关联其他表的外键
     * - SA-Token的登录标识
     * - 用户权限验证
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 用户名
     *
     * 用户的登录名称，必须全局唯一
     * 用于用户登录和身份识别
     *
     * 数据库配置：
     * - 类型：VARCHAR(50)
     * - 约束：UNIQUE, NOT NULL
     * - 索引：UNIQUE INDEX
     *
     * 业务规则：
     * - 长度：3-20个字符
     * - 格式：字母、数字、下划线
     * - 唯一性：全局唯一，包括已删除用户
     * - 不可修改：注册后不允许修改
     *
     * 使用场景：
     * - 用户登录验证
     * - 用户信息展示
     * - 系统日志记录
     */
    private String username;

    /**
     * 密码
     *
     * 用户的登录密码，应该加密存储
     * 当前版本使用明文存储（仅用于演示）
     *
     * 数据库配置：
     * - 类型：VARCHAR(255)
     * - 约束：NOT NULL
     *
     * 安全要求：
     * - 生产环境必须加密存储
     * - 建议使用SHA-256 + 盐值
     * - 不能在日志中输出
     * - 返回给前端时必须清空
     *
     * 密码策略建议：
     * - 最小长度：8位
     * - 包含大小写字母、数字、特殊字符
     * - 定期更换密码
     * - 防止弱密码
     *
     * 改进建议：
     * - 使用PasswordUtil工具类加密
     * - 添加盐值字段
     * - 添加密码修改时间字段
     */
    private String password;

    /**
     * 邮箱地址
     *
     * 用户的邮箱地址，用于登录和通知
     * 必须全局唯一，支持邮箱登录
     *
     * 数据库配置：
     * - 类型：VARCHAR(100)
     * - 约束：UNIQUE, NOT NULL
     * - 索引：UNIQUE INDEX
     *
     * 业务规则：
     * - 格式：符合邮箱格式规范
     * - 唯一性：全局唯一
     * - 验证：建议邮箱验证激活
     * - 不区分大小写
     *
     * 使用场景：
     * - 邮箱登录
     * - 找回密码
     * - 系统通知
     * - 用户联系方式
     */
    private String email;

    /**
     * 手机号码
     *
     * 用户的手机号码，可选字段
     * 用于短信验证和联系
     *
     * 数据库配置：
     * - 类型：VARCHAR(20)
     * - 约束：可为NULL
     * - 索引：普通索引（可选）
     *
     * 业务规则：
     * - 格式：11位数字（中国大陆）
     * - 唯一性：建议唯一（业务需求）
     * - 验证：短信验证码验证
     * - 国际化：支持国际号码格式
     *
     * 使用场景：
     * - 手机号登录
     * - 短信验证码
     * - 双因子认证
     * - 紧急联系方式
     */
    private String phone;

    /**
     * 逻辑删除标记
     *
     * 标记用户是否被逻辑删除
     * 采用逻辑删除保护数据完整性
     *
     * 数据库配置：
     * - 类型：TINYINT
     * - 约束：DEFAULT 0
     * - 索引：普通索引
     *
     * 取值说明：
     * - 0 或 NULL：用户正常，未删除
     * - 1：用户已被逻辑删除
     *
     * 业务规则：
     * - 已删除用户无法登录
     * - 已删除用户不出现在查询结果中
     * - 保留数据用于审计和恢复
     * - 用户名和邮箱仍占用，防止重复使用
     *
     * 使用场景：
     * - 用户注销账户
     * - 管理员删除用户
     * - 数据恢复
     * - 审计追踪
     */
    private Integer isDelete;

    /**
     * 创建时间
     *
     * 记录用户账户的创建时间
     * 由系统自动设置，不可修改
     *
     * 数据库配置：
     * - 类型：DATETIME
     * - 约束：NOT NULL
     * - 默认值：CURRENT_TIMESTAMP
     *
     * 业务规则：
     * - 用户注册时自动设置
     * - 创建后不可修改
     * - 用于统计分析
     * - 数据审计追踪
     *
     * 使用场景：
     * - 用户注册统计
     * - 数据分析报表
     * - 审计日志
     * - 用户生命周期管理
     */
    private Date createTime;

    /**
     * 更新时间
     *
     * 记录用户信息的最后修改时间
     * 每次更新用户信息时自动更新
     *
     * 数据库配置：
     * - 类型：DATETIME
     * - 约束：NOT NULL
     * - 默认值：CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
     *
     * 业务规则：
     * - 创建时设置为当前时间
     * - 每次更新时自动更新
     * - 用于数据同步和缓存失效
     * - 乐观锁版本控制
     *
     * 使用场景：
     * - 数据同步
     * - 缓存更新
     * - 变更追踪
     * - 并发控制
     */
    private Date updateTime;
}
