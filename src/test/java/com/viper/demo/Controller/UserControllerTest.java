package com.viper.demo.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viper.demo.Pojo.User;
import com.viper.demo.Service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private User adminUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1);
        testUser.setUsername("testuser");
        testUser.setPassword("password123");
        testUser.setEmail("test@example.com");
        testUser.setPhone("13800138000");
        testUser.setIsDelete(0);
        testUser.setCreateTime(new Date());
        testUser.setUpdateTime(new Date());

        adminUser = new User();
        adminUser.setId(2);
        adminUser.setUsername("admin");
        adminUser.setPassword("admin123");
        adminUser.setEmail("admin@example.com");
        adminUser.setPhone("13800138001");
        adminUser.setIsDelete(0);
        adminUser.setCreateTime(new Date());
        adminUser.setUpdateTime(new Date());
    }

    @Test
    void testGetUserProfile_Success() throws Exception {
        // Given
        when(userService.findById(1)).thenReturn(testUser);

        // When & Then
        // 注意：由于SA-Token的注解需要实际的登录状态，这里的测试可能需要模拟登录
        // 在实际测试中，你可能需要使用@MockBean来模拟StpUtil的行为
        mockMvc.perform(get("/user/profile")
                .header("satoken", "mock-token"))
                .andExpect(status().isOk());
                // 由于SA-Token的复杂性，这里简化测试
                // 在实际项目中，建议使用集成测试来测试完整的认证流程
    }

    @Test
    void testGetUserList_Success() throws Exception {
        // Given
        List<User> users = Arrays.asList(testUser, adminUser);
        when(userService.findAll()).thenReturn(users);

        // When & Then
        mockMvc.perform(get("/user/list")
                .header("satoken", "admin-token"))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateUserProfile_Success() throws Exception {
        // Given
        User updateUser = new User();
        updateUser.setEmail("updated@example.com");
        updateUser.setPhone("13800138999");

        when(userService.findById(1)).thenReturn(testUser);
        when(userService.update(any(User.class))).thenReturn(testUser);

        // When & Then
        mockMvc.perform(put("/user/profile")
                .header("satoken", "mock-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateUser)))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteUser_Success() throws Exception {
        // Given
        when(userService.deleteById(2)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/user/2")
                .header("satoken", "admin-token"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetUserPermissions() throws Exception {
        // When & Then
        mockMvc.perform(get("/user/permissions")
                .header("satoken", "mock-token"))
                .andExpect(status().isOk());
    }

    @Test
    void testAdminDashboard() throws Exception {
        // Given
        List<User> users = Arrays.asList(testUser, adminUser);
        when(userService.findAll()).thenReturn(users);

        // When & Then
        mockMvc.perform(get("/user/admin/dashboard")
                .header("satoken", "admin-token"))
                .andExpect(status().isOk());
    }
}
