package com.viper.demo.Service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viper.demo.Pojo.User;
import com.viper.demo.Repository.UserRepository;
import com.viper.demo.Service.UserService;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 用户业务逻辑服务实现类
 *
 * 该类实现了UserService接口，提供用户管理相关的具体业务逻辑
 * 负责处理用户的增删改查、验证、存在性检查等核心功能
 *
 * 实现特点：
 * 1. 完善的参数验证，确保数据安全性
 * 2. 逻辑删除机制，保护数据完整性
 * 3. 自动维护时间字段，简化业务操作
 * 4. 统一的异常处理，提高系统稳定性
 *
 * 业务规则实现：
 * - 所有查询操作自动过滤已删除用户
 * - 保存操作自动设置创建时间和删除标记
 * - 更新操作自动设置修改时间
 * - 删除操作采用逻辑删除方式
 *
 * 数据安全：
 * - 参数空值检查，防止空指针异常
 * - 字符串参数自动去除首尾空格
 * - 异常捕获和处理，避免系统崩溃
 *
 * 性能优化：
 * - 使用Optional处理查询结果
 * - 合理的数据库查询策略
 * - 避免不必要的数据库操作
 *
 * @author Viper
 * @version 1.0
 * @since 2024
 */
@Service
public class UserServiceImpl implements UserService {

    /**
     * 用户数据访问层对象，用于执行数据库操作
     * 通过@Autowired注解实现依赖注入
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * 根据用户名查询用户信息的具体实现
     *
     * 该方法实现了根据用户名查找用户的业务逻辑
     * 包含完善的参数验证和数据过滤机制
     *
     * 实现逻辑：
     * 1. 参数验证：检查用户名是否为空或null
     * 2. 数据清理：自动去除用户名首尾空格
     * 3. 数据库查询：调用Repository层的专用查询方法
     * 4. 结果处理：使用Optional安全处理查询结果
     *
     * 安全措施：
     * - 空值检查，防止空指针异常
     * - 自动过滤已删除用户（通过Repository方法实现）
     * - 字符串清理，提高查询准确性
     *
     * @param username 用户名，会自动去除首尾空格
     * @return 用户信息对象，如果用户不存在、已删除或参数无效则返回null
     */
    @Override
    public User findByUsername(String username) {
        // 参数验证：检查用户名是否为空
        if (username == null || username.trim().isEmpty()) {
            return null;
        }

        // 调用Repository层查询方法，自动过滤已删除用户
        Optional<User> userOpt = userRepository.findByUsernameAndNotDeleted(username.trim());

        // 使用Optional安全处理查询结果
        return userOpt.orElse(null);
    }

    /**
     * 根据用户ID查询用户信息的具体实现
     *
     * 该方法实现了根据用户ID查找用户的业务逻辑
     * 是最常用和性能最优的用户查询方法
     *
     * 实现逻辑：
     * 1. 参数验证：检查用户ID是否为null
     * 2. 数据库查询：根据主键ID查询用户
     * 3. 删除状态检查：手动检查用户是否被逻辑删除
     * 4. 结果返回：返回有效用户或null
     *
     * 注意事项：
     * - 这里使用标准的findById方法，然后手动检查删除状态
     * - 也可以在Repository层定义专门的方法来自动过滤已删除用户
     * - 删除状态检查确保已删除用户不会被返回
     *
     * @param id 用户ID，主键，不能为null
     * @return 用户信息对象，如果用户不存在、已删除或ID无效则返回null
     */
    @Override
    public User findById(Integer id) {
        // 参数验证：检查用户ID是否为null
        if (id == null) {
            return null;
        }

        // 根据主键ID查询用户
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // 手动检查用户是否被逻辑删除
            if (user.getIsDelete() != null && user.getIsDelete() == 1) {
                return null;  // 已删除用户返回null
            }

            return user;
        }

        return null;  // 用户不存在
    }

    /**
     * 根据邮箱地址查询用户信息的具体实现
     *
     * 该方法实现了根据邮箱地址查找用户的业务逻辑
     * 主要用于邮箱登录和邮箱唯一性验证场景
     *
     * 实现逻辑：
     * 1. 参数验证：检查邮箱是否为空或null
     * 2. 数据清理：自动去除邮箱首尾空格
     * 3. 数据库查询：根据邮箱地址查询用户
     * 4. 删除状态检查：手动检查用户是否被逻辑删除
     * 5. 结果返回：返回有效用户或null
     *
     * 业务规则：
     * - 邮箱地址不区分大小写（可在Repository层处理）
     * - 自动过滤已删除用户
     * - 邮箱格式验证可在Controller层或此处添加
     *
     * @param email 邮箱地址，会自动去除首尾空格
     * @return 用户信息对象，如果用户不存在、已删除或邮箱无效则返回null
     */
    @Override
    public User findByEmail(String email) {
        // 参数验证：检查邮箱是否为空
        if (email == null || email.trim().isEmpty()) {
            return null;
        }

        // 根据邮箱地址查询用户
        Optional<User> userOpt = userRepository.findByEmail(email.trim());
        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // 手动检查用户是否被逻辑删除
            if (user.getIsDelete() != null && user.getIsDelete() == 1) {
                return null;  // 已删除用户返回null
            }

            return user;
        }

        return null;  // 用户不存在
    }

    /**
     * 根据手机号码查询用户信息的具体实现
     *
     * 该方法实现了根据手机号码查找用户的业务逻辑
     * 主要用于手机号登录和手机号唯一性验证场景
     *
     * 实现逻辑：
     * 1. 参数验证：检查手机号是否为空或null
     * 2. 数据清理：自动去除手机号首尾空格
     * 3. 数据库查询：根据手机号查询用户
     * 4. 删除状态检查：手动检查用户是否被逻辑删除
     * 5. 结果返回：返回有效用户或null
     *
     * 扩展建议：
     * - 可以添加手机号格式验证
     * - 可以支持多种手机号格式（国际号码等）
     * - 可以在Repository层添加索引优化查询性能
     *
     * @param phone 手机号码，会自动去除首尾空格
     * @return 用户信息对象，如果用户不存在、已删除或手机号无效则返回null
     */
    @Override
    public User findByPhone(String phone) {
        // 参数验证：检查手机号是否为空
        if (phone == null || phone.trim().isEmpty()) {
            return null;
        }

        // 根据手机号查询用户
        Optional<User> userOpt = userRepository.findByPhone(phone.trim());
        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // 手动检查用户是否被逻辑删除
            if (user.getIsDelete() != null && user.getIsDelete() == 1) {
                return null;  // 已删除用户返回null
            }

            return user;
        }

        return null;  // 用户不存在
    }

    /**
     * 保存新用户信息的具体实现
     *
     * 该方法实现了创建新用户的业务逻辑
     * 自动处理系统字段的设置，确保数据完整性
     *
     * 实现逻辑：
     * 1. 参数验证：检查用户对象是否为null
     * 2. 创建时间设置：如果未设置则自动设置为当前时间
     * 3. 更新时间设置：自动设置为当前时间
     * 4. 删除标记设置：如果未设置则自动设置为0（未删除）
     * 5. 数据库保存：调用Repository层保存用户
     *
     * 自动处理的字段：
     * - createTime：创建时间，首次保存时自动设置
     * - updateTime：更新时间，每次保存时自动设置
     * - isDelete：删除标记，默认设置为0（未删除）
     *
     * 注意事项：
     * - 调用此方法前应该验证用户名和邮箱的唯一性
     * - 密码应该在调用前进行加密处理
     * - 其他业务字段的验证应该在Controller层完成
     *
     * @param user 要保存的用户对象，不能为null
     * @return 保存后的用户对象（包含自动生成的ID和时间字段），保存失败返回null
     */
    @Override
    public User save(User user) {
        // 参数验证：检查用户对象是否为null
        if (user == null) {
            return null;
        }

        // 自动设置创建时间（如果未设置）
        if (user.getCreateTime() == null) {
            user.setCreateTime(new Date());
        }

        // 自动设置更新时间
        user.setUpdateTime(new Date());

        // 自动设置删除标记（如果未设置）
        if (user.getIsDelete() == null) {
            user.setIsDelete(0);  // 0表示未删除
        }

        // 调用Repository层保存用户到数据库
        return userRepository.save(user);
    }

    /**
     * 更新用户信息的具体实现
     *
     * 该方法实现了更新已存在用户信息的业务逻辑
     * 自动维护更新时间，确保数据的时效性
     *
     * 实现逻辑：
     * 1. 参数验证：检查用户对象和用户ID是否为null
     * 2. 更新时间设置：自动设置为当前时间
     * 3. 数据库更新：调用Repository层更新用户信息
     *
     * 业务规则：
     * - 用户ID不能为null（必须是已存在的用户）
     * - 自动更新修改时间
     * - 不会修改创建时间
     * - 使用JPA的save方法，会根据ID判断是更新还是插入
     *
     * 安全考虑：
     * - 不允许修改用户名（业务规则，可在Controller层控制）
     * - 密码修改应该通过专门的接口处理
     * - 敏感字段的修改需要额外的权限验证
     *
     * @param user 要更新的用户对象，不能为null，必须包含有效的用户ID
     * @return 更新后的用户对象，更新失败返回null
     */
    @Override
    public User update(User user) {
        // 参数验证：检查用户对象和用户ID
        if (user == null || user.getId() == null) {
            return null;
        }

        // 自动设置更新时间
        user.setUpdateTime(new Date());

        // 调用Repository层更新用户信息
        return userRepository.save(user);
    }

    /**
     * 删除用户的具体实现（逻辑删除）
     *
     * 该方法实现了删除用户的业务逻辑
     * 采用逻辑删除方式，保护数据完整性和可追溯性
     *
     * 实现逻辑：
     * 1. 参数验证：检查用户ID是否为null
     * 2. 用户查询：根据ID查找要删除的用户
     * 3. 逻辑删除：设置删除标记为1
     * 4. 时间更新：设置更新时间为当前时间
     * 5. 数据保存：保存更新后的用户信息
     * 6. 异常处理：捕获并处理可能的异常
     *
     * 逻辑删除的优势：
     * - 保留数据用于审计和分析
     * - 支持数据恢复功能
     * - 维护数据关联关系的完整性
     * - 符合数据保护法规要求
     *
     * 异常处理：
     * - 捕获所有可能的异常
     * - 打印异常堆栈信息用于调试
     * - 返回false表示删除失败
     *
     * @param id 要删除的用户ID，不能为null
     * @return true表示删除成功，false表示用户不存在或删除失败
     */
    @Override
    public boolean deleteById(Integer id) {
        // 参数验证：检查用户ID是否为null
        if (id == null) {
            return false;
        }

        try {
            // 根据ID查找要删除的用户
            Optional<User> userOpt = userRepository.findById(id);
            if (userOpt.isPresent()) {
                User user = userOpt.get();

                // 执行逻辑删除：设置删除标记
                user.setIsDelete(1);  // 1表示已删除

                // 更新修改时间
                user.setUpdateTime(new Date());

                // 保存更新后的用户信息
                userRepository.save(user);

                return true;  // 删除成功
            }

            return false;  // 用户不存在
        } catch (Exception e) {
            // 异常处理：记录错误信息
            e.printStackTrace();
            return false;  // 删除失败
        }
    }

    /**
     * 检查用户名是否存在的具体实现
     *
     * 该方法实现了用户名唯一性检查的业务逻辑
     * 用于用户注册和用户名修改时的验证
     *
     * 实现逻辑：
     * 1. 参数验证：检查用户名是否为空或null
     * 2. 数据清理：自动去除用户名首尾空格
     * 3. 存在性检查：调用Repository层检查用户名是否存在
     *
     * 业务规则：
     * - 包括已删除用户的用户名检查（防止用户名重复使用）
     * - 不区分大小写（可在Repository层处理）
     * - 空用户名返回false（表示可用）
     *
     * 使用场景：
     * - 用户注册时验证用户名可用性
     * - 前端实时验证用户名
     * - 批量导入用户时的重复检查
     *
     * @param username 要检查的用户名，会自动去除首尾空格
     * @return true表示用户名已存在，false表示用户名可用或参数无效
     */
    @Override
    public boolean existsByUsername(String username) {
        // 参数验证：检查用户名是否为空
        if (username == null || username.trim().isEmpty()) {
            return false;  // 空用户名视为可用
        }

        // 调用Repository层检查用户名是否存在
        return userRepository.existsByUsername(username.trim());
    }

    /**
     * 检查邮箱地址是否存在的具体实现
     *
     * 该方法实现了邮箱地址唯一性检查的业务逻辑
     * 用于用户注册和邮箱修改时的验证
     *
     * 实现逻辑：
     * 1. 参数验证：检查邮箱是否为空或null
     * 2. 数据清理：自动去除邮箱首尾空格
     * 3. 存在性检查：调用Repository层检查邮箱是否存在
     *
     * 业务规则：
     * - 包括已删除用户的邮箱检查（防止邮箱重复使用）
     * - 不区分大小写（可在Repository层处理）
     * - 空邮箱返回false（表示可用）
     *
     * 扩展建议：
     * - 可以添加邮箱格式验证
     * - 可以支持邮箱域名黑名单检查
     * - 可以集成邮箱验证服务
     *
     * @param email 要检查的邮箱地址，会自动去除首尾空格
     * @return true表示邮箱已存在，false表示邮箱可用或参数无效
     */
    @Override
    public boolean existsByEmail(String email) {
        // 参数验证：检查邮箱是否为空
        if (email == null || email.trim().isEmpty()) {
            return false;  // 空邮箱视为可用
        }

        // 调用Repository层检查邮箱是否存在
        return userRepository.existsByEmail(email.trim());
    }

    /**
     * 获取所有用户列表的具体实现
     *
     * 该方法实现了获取系统中所有用户信息的业务逻辑
     * 主要供管理员使用，用于用户管理和统计分析
     *
     * 实现逻辑：
     * 1. 直接调用Repository层的findAll方法
     * 2. 返回所有用户，包括已删除用户
     * 3. 不进行任何过滤或排序（可在Repository层扩展）
     *
     * 注意事项：
     * - 返回的列表包含所有用户（包括已删除用户）
     * - 密码字段需要在Controller层进行过滤
     * - 大量用户时可能影响性能，建议添加分页功能
     *
     * 扩展建议：
     * - 添加分页支持：findAll(Pageable pageable)
     * - 添加排序功能：按创建时间、用户名等排序
     * - 添加过滤功能：只返回未删除用户
     * - 添加搜索功能：根据关键词搜索用户
     *
     * 性能考虑：
     * - 用户数量较多时建议使用分页查询
     * - 可以在Repository层添加缓存
     * - 可以只返回必要的字段（投影查询）
     *
     * @return 所有用户信息列表，如果没有用户则返回空列表（不会返回null）
     */
    @Override
    public List<User> findAll() {
        // 直接调用Repository层获取所有用户
        return userRepository.findAll();
    }

    /**
     * 验证用户密码的具体实现
     *
     * 该方法实现了用户密码验证的业务逻辑
     * 用于用户登录、密码修改等场景的身份验证
     *
     * 实现逻辑：
     * 1. 参数验证：检查用户名和密码是否为null
     * 2. 用户查询：根据用户名查找用户信息
     * 3. 用户存在性检查：验证用户是否存在且未删除
     * 4. 密码比较：比较输入密码与存储密码
     *
     * 当前实现：
     * - 使用明文密码比较（仅用于演示）
     * - 自动过滤已删除用户（通过findByUsername实现）
     *
     * 生产环境改进建议：
     * - 使用加密密码存储和验证
     * - 集成PasswordUtil工具类进行密码验证
     * - 添加密码错误次数限制
     * - 添加账户锁定机制
     * - 记录登录尝试日志
     *
     * 安全考虑：
     * - 密码验证失败时不要透露具体原因
     * - 防止暴力破解攻击
     * - 使用安全的密码哈希算法
     *
     * 示例改进代码：
     * ```java
     * // 使用加密密码验证
     * return PasswordUtil.verifyPassword(password, user.getPassword(), user.getSalt());
     * ```
     *
     * @param username 用户名，不能为null
     * @param password 要验证的密码，不能为null
     * @return true表示密码正确，false表示密码错误或用户不存在
     */
    @Override
    public boolean validatePassword(String username, String password) {
        // 参数验证：检查用户名和密码是否为null
        if (username == null || password == null) {
            return false;
        }

        // 根据用户名查找用户（自动过滤已删除用户）
        User user = findByUsername(username);
        if (user == null) {
            return false;  // 用户不存在或已删除
        }

        // 密码比较
        // 注意：这里使用明文比较，仅用于演示
        // 实际项目中应该使用加密后的密码比较：
        // return PasswordUtil.verifyPassword(password, user.getPassword(), user.getSalt());
        return password.equals(user.getPassword());
    }
}
