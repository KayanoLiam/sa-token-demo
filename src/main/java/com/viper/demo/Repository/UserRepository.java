package com.viper.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.viper.demo.Pojo.User;

import java.util.Optional;

/**
 * 用户数据访问层接口
 *
 * 该接口继承自JpaRepository，提供用户实体的数据库访问功能
 * 基于Spring Data JPA实现，支持自动生成SQL和自定义查询
 *
 * 继承功能：
 * - 基础CRUD操作：save、findById、findAll、deleteById等
 * - 分页和排序：支持Pageable和Sort参数
 * - 批量操作：saveAll、deleteAll等
 * - 条件查询：支持Example和Specification查询
 *
 * 自定义功能：
 * - 多种查询方式：用户名、邮箱、手机号查询
 * - 逻辑删除支持：过滤已删除用户的查询
 * - 存在性检查：用户名、邮箱唯一性验证
 * - 自定义JPQL查询：复杂业务逻辑查询
 *
 * 命名规范：
 * - findBy*：查询方法，返回Optional<User>或List<User>
 * - existsBy*：存在性检查方法，返回boolean
 * - countBy*：计数方法，返回long
 * - deleteBy*：删除方法，返回void或long
 *
 * 性能优化：
 * - 合理使用索引：username、email字段建议添加唯一索引
 * - 查询优化：避免N+1查询问题
 * - 缓存策略：可配置二级缓存提高查询性能
 *
 * 数据安全：
 * - 参数绑定：使用@Param注解防止SQL注入
 * - 逻辑删除：支持软删除机制保护数据
 * - 事务支持：继承JpaRepository的事务特性
 *
 * @author Viper
 * @version 1.0
 * @since 2024
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * 根据用户名查询用户（包含已删除用户）
     *
     * 该方法使用Spring Data JPA的方法命名规范自动生成查询
     * 会查询所有用户，包括已逻辑删除的用户
     *
     * 生成的SQL类似：
     * SELECT * FROM user WHERE username = ?
     *
     * 使用场景：
     * - 管理员查看所有用户（包括已删除）
     * - 系统内部需要查询已删除用户的场景
     * - 用户名唯一性检查（包括已删除用户）
     *
     * 注意事项：
     * - 此方法不会过滤已删除用户
     * - 如需过滤已删除用户，请使用findByUsernameAndNotDeleted方法
     * - 用户名区分大小写，如需不区分大小写可使用findByUsernameIgnoreCase
     *
     * @param username 用户名，精确匹配，区分大小写
     * @return Optional<User> 用户信息的Optional包装，如果用户不存在则为空
     */
    Optional<User> findByUsername(String username);

    /**
     * 根据用户名查询用户（排除已删除的用户）
     *
     * 该方法使用自定义JPQL查询，自动过滤已逻辑删除的用户
     * 这是业务层最常用的用户查询方法
     *
     * JPQL查询逻辑：
     * - 查询username匹配的用户
     * - 同时满足isDelete为NULL或isDelete为0的条件
     * - NULL值处理：isDelete为NULL视为未删除
     *
     * 查询条件说明：
     * - u.isDelete IS NULL：处理历史数据中isDelete字段为NULL的情况
     * - u.isDelete = 0：isDelete为0表示未删除
     * - 两个条件用OR连接，确保兼容性
     *
     * 使用场景：
     * - 用户登录验证
     * - 业务层查询有效用户
     * - 前端用户信息展示
     *
     * 性能优化建议：
     * - 在username字段上创建索引
     * - 在isDelete字段上创建索引
     * - 考虑创建复合索引(username, isDelete)
     *
     * @param username 用户名，精确匹配，区分大小写
     * @return Optional<User> 有效用户信息的Optional包装，已删除用户不会被返回
     */
    @Query("SELECT u FROM User u WHERE u.username = :username AND (u.isDelete IS NULL OR u.isDelete = 0)")
    Optional<User> findByUsernameAndNotDeleted(@Param("username") String username);

    /**
     * 根据邮箱地址查询用户
     *
     * 该方法使用Spring Data JPA的方法命名规范自动生成查询
     * 用于邮箱登录、邮箱验证等场景
     *
     * 生成的SQL类似：
     * SELECT * FROM user WHERE email = ?
     *
     * 业务规则：
     * - 邮箱地址精确匹配
     * - 包含已删除用户（如需过滤可在Service层处理）
     * - 区分大小写（建议在数据库层面设置为不区分大小写）
     *
     * 使用场景：
     * - 邮箱登录验证
     * - 找回密码功能
     * - 邮箱唯一性检查
     * - 用户信息查询
     *
     * 扩展建议：
     * - 可添加findByEmailIgnoreCase方法支持不区分大小写
     * - 可添加findByEmailAndNotDeleted方法过滤已删除用户
     * - 可添加邮箱格式验证
     *
     * 性能优化：
     * - 在email字段上创建唯一索引
     * - 考虑邮箱地址的标准化存储（统一小写）
     *
     * @param email 邮箱地址，精确匹配
     * @return Optional<User> 用户信息的Optional包装，如果用户不存在则为空
     */
    Optional<User> findByEmail(String email);

    /**
     * 根据手机号码查询用户
     *
     * 该方法使用Spring Data JPA的方法命名规范自动生成查询
     * 用于手机号登录、短信验证等场景
     *
     * 生成的SQL类似：
     * SELECT * FROM user WHERE phone = ?
     *
     * 业务规则：
     * - 手机号精确匹配
     * - 包含已删除用户（如需过滤可在Service层处理）
     * - 支持各种手机号格式（建议统一格式存储）
     *
     * 使用场景：
     * - 手机号登录验证
     * - 短信验证码发送
     * - 手机号唯一性检查
     * - 用户信息查询
     *
     * 数据格式建议：
     * - 统一存储格式：如13800138000（不含分隔符）
     * - 支持国际号码：如+8613800138000
     * - 数据验证：确保手机号格式正确
     *
     * 扩展功能：
     * - 可添加手机号格式验证
     * - 可支持模糊查询（如后四位匹配）
     * - 可添加手机号归属地查询
     *
     * @param phone 手机号码，精确匹配
     * @return Optional<User> 用户信息的Optional包装，如果用户不存在则为空
     */
    Optional<User> findByPhone(String phone);

    /**
     * 检查用户名是否已存在
     *
     * 该方法使用Spring Data JPA的exists查询，高效检查用户名唯一性
     * 返回boolean值，避免查询完整用户对象，提高性能
     *
     * 生成的SQL类似：
     * SELECT COUNT(*) > 0 FROM user WHERE username = ?
     *
     * 业务规则：
     * - 包含已删除用户的用户名检查
     * - 用于防止用户名重复使用
     * - 区分大小写匹配
     *
     * 使用场景：
     * - 用户注册时的用户名验证
     * - 前端实时验证用户名可用性
     * - 批量导入用户时的重复检查
     * - 用户名修改时的唯一性验证
     *
     * 性能优势：
     * - 只返回boolean值，不查询完整对象
     * - 数据库层面的COUNT查询，效率高
     * - 减少网络传输和内存占用
     *
     * 注意事项：
     * - 此方法包含已删除用户，确保用户名全局唯一
     * - 如需只检查有效用户，可在Service层添加额外逻辑
     *
     * @param username 要检查的用户名
     * @return boolean true表示用户名已存在，false表示用户名可用
     */
    boolean existsByUsername(String username);

    /**
     * 检查邮箱地址是否已存在
     *
     * 该方法使用Spring Data JPA的exists查询，高效检查邮箱唯一性
     * 返回boolean值，避免查询完整用户对象，提高性能
     *
     * 生成的SQL类似：
     * SELECT COUNT(*) > 0 FROM user WHERE email = ?
     *
     * 业务规则：
     * - 包含已删除用户的邮箱检查
     * - 用于防止邮箱重复使用
     * - 区分大小写匹配（建议数据库设置为不区分大小写）
     *
     * 使用场景：
     * - 用户注册时的邮箱验证
     * - 前端实时验证邮箱可用性
     * - 邮箱绑定时的唯一性检查
     * - 批量导入用户时的重复检查
     *
     * 数据完整性：
     * - 确保邮箱地址全局唯一
     * - 防止多个账户绑定同一邮箱
     * - 支持邮箱找回密码功能
     *
     * 扩展建议：
     * - 可添加邮箱格式验证
     * - 可支持邮箱域名黑名单检查
     * - 可集成邮箱验证服务
     *
     * @param email 要检查的邮箱地址
     * @return boolean true表示邮箱已存在，false表示邮箱可用
     */
    boolean existsByEmail(String email);
}
