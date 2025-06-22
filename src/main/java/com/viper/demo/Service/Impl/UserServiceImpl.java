package com.viper.demo.Service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viper.demo.Pojo.User;
import com.viper.demo.Repository.UserRepository;
import com.viper.demo.Service.UserService;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }
        Optional<User> userOpt = userRepository.findByUsernameAndNotDeleted(username.trim());
        return userOpt.orElse(null);
    }

    @Override
    public User findById(Integer id) {
        if (id == null) {
            return null;
        }
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // 检查用户是否被删除
            if (user.getIsDelete() != null && user.getIsDelete() == 1) {
                return null;
            }
            return user;
        }
        return null;
    }

    @Override
    public User findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        Optional<User> userOpt = userRepository.findByEmail(email.trim());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // 检查用户是否被删除
            if (user.getIsDelete() != null && user.getIsDelete() == 1) {
                return null;
            }
            return user;
        }
        return null;
    }

    @Override
    public User findByPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return null;
        }
        Optional<User> userOpt = userRepository.findByPhone(phone.trim());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // 检查用户是否被删除
            if (user.getIsDelete() != null && user.getIsDelete() == 1) {
                return null;
            }
            return user;
        }
        return null;
    }

    @Override
    public User save(User user) {
        if (user == null) {
            return null;
        }
        
        // 设置创建时间
        if (user.getCreateTime() == null) {
            user.setCreateTime(new Date());
        }
        user.setUpdateTime(new Date());
        
        // 设置默认删除状态
        if (user.getIsDelete() == null) {
            user.setIsDelete(0);
        }
        
        return userRepository.save(user);
    }

    @Override
    public User update(User user) {
        if (user == null || user.getId() == null) {
            return null;
        }
        
        // 设置更新时间
        user.setUpdateTime(new Date());
        
        return userRepository.save(user);
    }

    @Override
    public boolean deleteById(Integer id) {
        if (id == null) {
            return false;
        }
        
        try {
            Optional<User> userOpt = userRepository.findById(id);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                // 逻辑删除
                user.setIsDelete(1);
                user.setUpdateTime(new Date());
                userRepository.save(user);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean existsByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return userRepository.existsByUsername(username.trim());
    }

    @Override
    public boolean existsByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return userRepository.existsByEmail(email.trim());
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public boolean validatePassword(String username, String password) {
        if (username == null || password == null) {
            return false;
        }
        
        User user = findByUsername(username);
        if (user == null) {
            return false;
        }
        
        // 简单的密码比较，实际项目中应该使用加密后的密码比较
        return password.equals(user.getPassword());
    }
}
