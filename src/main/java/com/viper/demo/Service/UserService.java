package com.viper.demo.Service;

import com.viper.demo.Pojo.User;

import java.util.List;

public interface UserService {
    
    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户信息
     */
    User findByUsername(String username);
    
    /**
     * 根据用户ID查询用户
     * @param id 用户ID
     * @return 用户信息
     */
    User findById(Integer id);
    
    /**
     * 根据邮箱查询用户
     * @param email 邮箱
     * @return 用户信息
     */
    User findByEmail(String email);
    
    /**
     * 根据手机号查询用户
     * @param phone 手机号
     * @return 用户信息
     */
    User findByPhone(String phone);
    
    /**
     * 保存用户
     * @param user 用户信息
     * @return 保存后的用户信息
     */
    User save(User user);
    
    /**
     * 更新用户信息
     * @param user 用户信息
     * @return 更新后的用户信息
     */
    User update(User user);
    
    /**
     * 删除用户（逻辑删除）
     * @param id 用户ID
     * @return 是否删除成功
     */
    boolean deleteById(Integer id);
    
    /**
     * 检查用户名是否存在
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查邮箱是否存在
     * @param email 邮箱
     * @return 是否存在
     */
    boolean existsByEmail(String email);
    
    /**
     * 获取所有用户列表
     * @return 用户列表
     */
    List<User> findAll();
    
    /**
     * 验证用户密码
     * @param username 用户名
     * @param password 密码
     * @return 验证结果
     */
    boolean validatePassword(String username, String password);
}
