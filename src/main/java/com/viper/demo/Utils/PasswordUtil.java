package com.viper.demo.Utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 密码加密工具类
 *
 * 该工具类提供了安全的密码加密、验证和相关功能
 * 使用SHA-256哈希算法结合随机盐值，确保密码存储的安全性
 *
 * 主要功能：
 * 1. 随机盐值生成 - 为每个密码生成唯一的盐值
 * 2. 密码加密 - 使用SHA-256+盐值加密密码
 * 3. 密码验证 - 验证明文密码与加密密码是否匹配
 * 4. MD5加密 - 提供MD5加密（不推荐生产使用）
 *
 * 安全特性：
 * - 使用SHA-256算法，安全性高
 * - 随机盐值防止彩虹表攻击
 * - Base64编码便于存储和传输
 * - 密码验证时间恒定，防止时序攻击
 *
 * 使用场景：
 * - 用户注册时密码加密
 * - 用户登录时密码验证
 * - 密码修改时的加密处理
 * - 系统安全性要求较高的场景
 *
 * 使用示例：
 * ```java
 * // 注册时加密密码
 * String salt = PasswordUtil.generateSalt();
 * String encryptedPassword = PasswordUtil.encryptPassword("123456", salt);
 *
 * // 登录时验证密码
 * boolean isValid = PasswordUtil.verifyPassword("123456", salt, encryptedPassword);
 * ```
 *
 * 安全建议：
 * - 生产环境必须使用此工具类加密密码
 * - 盐值必须为每个用户单独生成和存储
 * - 不要使用MD5方法进行密码加密
 * - 定期更新加密算法和密钥长度
 *
 * @author Viper
 * @version 1.0
 * @since 2024
 */
public class PasswordUtil {

    /**
     * 哈希算法名称
     * 使用SHA-256算法，提供256位的哈希值
     * SHA-256是目前广泛使用的安全哈希算法
     */
    private static final String ALGORITHM = "SHA-256";

    /**
     * 盐值长度（字节）
     * 16字节（128位）的盐值长度，提供足够的随机性
     * 经过Base64编码后约为22个字符
     */
    private static final int SALT_LENGTH = 16;

    /**
     * 生成随机盐值
     *
     * 该方法使用SecureRandom生成密码学安全的随机盐值
     * 盐值用于防止彩虹表攻击和增强密码安全性
     *
     * 实现原理：
     * 1. 使用SecureRandom生成随机字节数组
     * 2. 将字节数组转换为Base64编码字符串
     * 3. 返回可存储的盐值字符串
     *
     * 安全特性：
     * - 使用密码学安全的随机数生成器
     * - 每次调用都生成不同的盐值
     * - 足够的长度确保唯一性
     * - Base64编码便于数据库存储
     *
     * 使用场景：
     * - 用户注册时生成盐值
     * - 密码重置时生成新盐值
     * - 任何需要随机盐值的场景
     *
     * 存储建议：
     * - 每个用户的盐值必须单独存储
     * - 盐值可以明文存储（不是秘密）
     * - 建议在User实体中添加salt字段
     *
     * @return 经过Base64编码的随机盐值字符串，长度约22个字符
     *
     * @apiExample 使用示例：
     * ```java
     * String salt = PasswordUtil.generateSalt();
     * // 输出类似：Kv2x8fJ9mN7pQ1rS3tU4vW==
     * ```
     */
    public static String generateSalt() {
        // 创建密码学安全的随机数生成器
        SecureRandom random = new SecureRandom();

        // 创建指定长度的字节数组
        byte[] salt = new byte[SALT_LENGTH];

        // 填充随机字节
        random.nextBytes(salt);

        // 转换为Base64编码字符串并返回
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * 加密密码
     *
     * 该方法使用SHA-256算法结合盐值对密码进行加密
     * 生成的哈希值经过Base64编码，便于存储和传输
     *
     * 加密流程：
     * 1. 获取SHA-256消息摘要实例
     * 2. 先更新盐值到摘要中
     * 3. 再计算密码的哈希值
     * 4. 将结果转换为Base64编码
     *
     * 安全原理：
     * - 盐值防止彩虹表攻击
     * - SHA-256提供强加密强度
     * - 相同密码+不同盐值=不同哈希
     * - 单向函数，无法逆向解密
     *
     * 使用场景：
     * - 用户注册时加密密码
     * - 密码修改时加密新密码
     * - 任何需要安全存储密码的场景
     *
     * 注意事项：
     * - 必须使用相同的盐值才能得到相同结果
     * - 盐值和密码都不能为null
     * - 加密结果应该存储到数据库
     *
     * @param password 原始明文密码，不能为null
     * @param salt 盐值，通常由generateSalt()方法生成，不能为null
     * @return 经过SHA-256+盐值加密并Base64编码的密码字符串
     * @throws RuntimeException 当SHA-256算法不可用时抛出
     *
     * @apiExample 使用示例：
     * ```java
     * String salt = PasswordUtil.generateSalt();
     * String encrypted = PasswordUtil.encryptPassword("123456", salt);
     * // 输出类似：Kv2x8fJ9mN7pQ1rS3tU4vWxYz...
     * ```
     */
    public static String encryptPassword(String password, String salt) {
        try {
            // 获取SHA-256消息摘要实例
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);

            // 先将盐值添加到摘要中
            md.update(salt.getBytes());

            // 计算密码的哈希值
            byte[] hashedPassword = md.digest(password.getBytes());

            // 转换为Base64编码字符串
            return Base64.getEncoder().encodeToString(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            // SHA-256算法不可用时抛出运行时异常
            throw new RuntimeException("密码加密失败：SHA-256算法不可用", e);
        }
    }

    /**
     * 验证密码
     *
     * 该方法用于验证用户输入的明文密码是否与存储的加密密码匹配
     * 通过重新加密明文密码并与存储的密码进行比较来实现验证
     *
     * 验证流程：
     * 1. 使用相同的盐值对输入密码进行加密
     * 2. 将加密结果与存储的密码进行比较
     * 3. 返回比较结果（true=匹配，false=不匹配）
     *
     * 安全特性：
     * - 使用相同的加密算法和盐值
     * - 字符串比较时间恒定，防止时序攻击
     * - 不会暴露原始密码信息
     * - 验证过程中不存储明文密码
     *
     * 使用场景：
     * - 用户登录时验证密码
     * - 修改密码前验证旧密码
     * - 敏感操作前的身份确认
     * - 任何需要密码验证的场景
     *
     * 参数要求：
     * - password：用户输入的明文密码
     * - salt：用户注册时生成的盐值
     * - encryptedPassword：数据库中存储的加密密码
     *
     * 错误处理：
     * - 任何参数为null都会导致验证失败
     * - 加密过程异常会传播到调用方
     * - 建议在业务层捕获异常并统一处理
     *
     * 性能考虑：
     * - 每次验证都需要重新计算哈希
     * - 验证时间与密码长度成正比
     * - 建议对频繁验证进行限流控制
     *
     * @param password 用户输入的明文密码，不能为null
     * @param salt 用户的盐值，必须与加密时使用的盐值相同，不能为null
     * @param encryptedPassword 数据库中存储的加密密码，不能为null
     * @return true表示密码匹配，false表示密码不匹配或参数错误
     *
     * @apiExample 使用示例：
     * ```java
     * // 用户登录验证
     * User user = userService.findByUsername("admin");
     * boolean isValid = PasswordUtil.verifyPassword(
     *     "123456",           // 用户输入的密码
     *     user.getSalt(),     // 用户的盐值
     *     user.getPassword()  // 存储的加密密码
     * );
     * if (isValid) {
     *     // 密码正确，允许登录
     * } else {
     *     // 密码错误，拒绝登录
     * }
     * ```
     */
    public static boolean verifyPassword(String password, String salt, String encryptedPassword) {
        // 参数验证：确保所有参数都不为null
        if (password == null || salt == null || encryptedPassword == null) {
            return false;
        }

        // 使用相同的盐值重新加密输入的密码
        String hashedPassword = encryptPassword(password, salt);

        // 比较加密结果与存储的密码
        // 使用equals方法进行字符串比较，时间恒定，防止时序攻击
        return hashedPassword.equals(encryptedPassword);
    }

    /**
     * MD5密码加密方法（不推荐用于生产环境）
     *
     * 该方法提供MD5哈希加密功能，主要用于兼容旧系统或特殊场景
     * 由于MD5算法存在安全漏洞，强烈不推荐在生产环境中使用
     *
     * MD5算法特点：
     * - 输出固定128位（32个十六进制字符）
     * - 计算速度快
     * - 不可逆（理论上）
     * - 存在碰撞攻击风险
     *
     * 安全问题：
     * - 容易受到彩虹表攻击
     * - 存在已知的碰撞攻击方法
     * - 计算速度快，容易暴力破解
     * - 没有使用盐值，相同密码产生相同哈希
     *
     * 使用限制：
     * - 仅用于非安全敏感场景
     * - 兼容旧系统的数据迁移
     * - 学习和测试目的
     * - 文件完整性校验（非密码）
     *
     * 替代方案：
     * - 生产环境请使用encryptPassword方法
     * - 使用SHA-256或更强的算法
     * - 结合盐值增强安全性
     * - 考虑使用bcrypt、scrypt等专用密码哈希算法
     *
     * 输出格式：
     * - 32位十六进制字符串
     * - 小写字母和数字组合
     * - 固定长度，便于存储
     *
     * @param password 要加密的明文密码，不能为null
     * @return MD5哈希值的十六进制字符串表示，固定32个字符
     * @throws RuntimeException 当MD5算法不可用时抛出
     *
     * @deprecated 此方法不推荐用于生产环境，请使用encryptPassword方法
     *
     * @apiExample 使用示例：
     * ```java
     * String md5Hash = PasswordUtil.md5Encrypt("123456");
     * // 输出：e10adc3949ba59abbe56e057f20f883e
     *
     * // 注意：相同输入总是产生相同输出，存在安全风险
     * String hash1 = PasswordUtil.md5Encrypt("password");
     * String hash2 = PasswordUtil.md5Encrypt("password");
     * // hash1.equals(hash2) == true
     * ```
     */
    public static String md5Encrypt(String password) {
        try {
            // 获取MD5消息摘要实例
            MessageDigest md = MessageDigest.getInstance("MD5");

            // 计算密码的MD5哈希值
            byte[] hashedPassword = md.digest(password.getBytes());

            // 将字节数组转换为十六进制字符串
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedPassword) {
                // 将每个字节转换为两位十六进制字符
                sb.append(String.format("%02x", b));
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // MD5算法不可用时抛出运行时异常
            throw new RuntimeException("MD5加密失败：MD5算法不可用", e);
        }
    }
}
