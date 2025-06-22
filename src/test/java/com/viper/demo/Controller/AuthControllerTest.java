package com.viper.demo.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viper.demo.Pojo.LoginRequest;
import com.viper.demo.Pojo.RegisterRequest;
import com.viper.demo.Pojo.User;
import com.viper.demo.Service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;

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

        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        registerRequest = new RegisterRequest();
        registerRequest.setUsername("newuser");
        registerRequest.setPassword("password123");
        registerRequest.setEmail("newuser@example.com");
        registerRequest.setPhone("13800138001");
    }

    @Test
    void testDoLogin_Success() throws Exception {
        // Given
        when(userService.findByUsername("testuser")).thenReturn(testUser);

        // When & Then
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.token").exists());

        verify(userService).findByUsername("testuser");
    }

    @Test
    void testDoLogin_UserNotFound() throws Exception {
        // Given
        when(userService.findByUsername("nonexistent")).thenReturn(null);
        loginRequest.setUsername("nonexistent");

        // When & Then
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value("用户名或密码错误"));

        verify(userService).findByUsername("nonexistent");
    }

    @Test
    void testDoLogin_WrongPassword() throws Exception {
        // Given
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        loginRequest.setPassword("wrongpassword");

        // When & Then
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value("用户名或密码错误"));

        verify(userService).findByUsername("testuser");
    }

    @Test
    void testDoLogin_DeletedUser() throws Exception {
        // Given
        testUser.setIsDelete(1);
        when(userService.findByUsername("testuser")).thenReturn(testUser);

        // When & Then
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(403))
                .andExpect(jsonPath("$.message").value("账号已被禁用"));

        verify(userService).findByUsername("testuser");
    }

    @Test
    void testDoLogin_EmptyUsername() throws Exception {
        // Given
        loginRequest.setUsername("");

        // When & Then
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("用户名不能为空"));

        verify(userService, never()).findByUsername(any());
    }

    @Test
    void testDoLogin_EmptyPassword() throws Exception {
        // Given
        loginRequest.setPassword("");

        // When & Then
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("密码不能为空"));

        verify(userService, never()).findByUsername(any());
    }

    @Test
    void testDoLogin_NullRequest() throws Exception {
        // When & Then
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("null"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("请求参数不能为空"));

        verify(userService, never()).findByUsername(any());
    }

    @Test
    void testRegister_Success() throws Exception {
        // Given
        when(userService.existsByUsername("newuser")).thenReturn(false);
        when(userService.existsByEmail("newuser@example.com")).thenReturn(false);
        
        User savedUser = new User();
        savedUser.setId(2);
        savedUser.setUsername("newuser");
        savedUser.setEmail("newuser@example.com");
        when(userService.save(any(User.class))).thenReturn(savedUser);

        // When & Then
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.userId").value(2))
                .andExpect(jsonPath("$.data.username").value("newuser"))
                .andExpect(jsonPath("$.data.email").value("newuser@example.com"))
                .andExpect(jsonPath("$.data.message").value("注册成功"));

        verify(userService).existsByUsername("newuser");
        verify(userService).existsByEmail("newuser@example.com");
        verify(userService).save(any(User.class));
    }

    @Test
    void testRegister_UsernameExists() throws Exception {
        // Given
        when(userService.existsByUsername("newuser")).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("用户名已存在"));

        verify(userService).existsByUsername("newuser");
        verify(userService, never()).save(any());
    }

    @Test
    void testRegister_EmailExists() throws Exception {
        // Given
        when(userService.existsByUsername("newuser")).thenReturn(false);
        when(userService.existsByEmail("newuser@example.com")).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("邮箱已被注册"));

        verify(userService).existsByUsername("newuser");
        verify(userService).existsByEmail("newuser@example.com");
        verify(userService, never()).save(any());
    }

    @Test
    void testRegister_EmptyUsername() throws Exception {
        // Given
        registerRequest.setUsername("");

        // When & Then
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("用户名不能为空"));

        verify(userService, never()).existsByUsername(any());
    }

    @Test
    void testRegister_EmptyPassword() throws Exception {
        // Given
        registerRequest.setPassword("");

        // When & Then
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("密码不能为空"));

        verify(userService, never()).existsByUsername(any());
    }

    @Test
    void testRegister_EmptyEmail() throws Exception {
        // Given
        registerRequest.setEmail("");

        // When & Then
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("邮箱不能为空"));

        verify(userService, never()).existsByUsername(any());
    }

    @Test
    void testRegister_SaveFailed() throws Exception {
        // Given
        when(userService.existsByUsername("newuser")).thenReturn(false);
        when(userService.existsByEmail("newuser@example.com")).thenReturn(false);
        when(userService.save(any(User.class))).thenReturn(null);

        // When & Then
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("注册失败"));

        verify(userService).save(any(User.class));
    }

    @Test
    void testIsLogin() throws Exception {
        // When & Then
        mockMvc.perform(get("/auth/isLogin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.isLogin").exists());
    }

    @Test
    void testLogout() throws Exception {
        // When & Then
        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").value("注销成功"));
    }
}
