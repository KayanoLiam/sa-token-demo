package com.viper.demo.Config;

import cn.dev33.satoken.stp.StpInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.viper.demo.Pojo.User;
import com.viper.demo.Service.UserService;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义权限验证接口扩展
 */
@Component
public class StpInterfaceImpl implements StpInterface {

    @Autowired
    private UserService userService;

    /**
     * 返回一个账号所拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // 本示例中，假设所有用户都有基础权限
        List<String> list = new ArrayList<>();
        
        try {
            Integer userId = Integer.parseInt(loginId.toString());
            User user = userService.findById(userId);
            
            if (user != null) {
                // 基础权限
                list.add("user:info");
                list.add("user:update");
                
                // 根据用户名分配不同权限（示例）
                if ("admin".equals(user.getUsername())) {
                    list.add("user:delete");
                    list.add("user:list");
                    list.add("admin:dashboard");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return list;
    }

    /**
     * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        List<String> list = new ArrayList<>();
        
        try {
            Integer userId = Integer.parseInt(loginId.toString());
            User user = userService.findById(userId);
            
            if (user != null) {
                // 根据用户名分配角色（示例）
                if ("admin".equals(user.getUsername())) {
                    list.add("admin");
                } else {
                    list.add("user");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return list;
    }
}
