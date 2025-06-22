package com.viper.demo.Config;

import com.viper.demo.Pojo.User;
import com.viper.demo.Service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StpInterfaceImplTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private StpInterfaceImpl stpInterface;

    private User adminUser;
    private User normalUser;

    @BeforeEach
    void setUp() {
        adminUser = new User();
        adminUser.setId(1);
        adminUser.setUsername("admin");
        adminUser.setPassword("password123");
        adminUser.setEmail("admin@example.com");
        adminUser.setPhone("13800138000");
        adminUser.setIsDelete(0);
        adminUser.setCreateTime(new Date());
        adminUser.setUpdateTime(new Date());

        normalUser = new User();
        normalUser.setId(2);
        normalUser.setUsername("testuser");
        normalUser.setPassword("password123");
        normalUser.setEmail("test@example.com");
        normalUser.setPhone("13800138001");
        normalUser.setIsDelete(0);
        normalUser.setCreateTime(new Date());
        normalUser.setUpdateTime(new Date());
    }

    @Test
    void testGetPermissionList_AdminUser() {
        // Given
        when(userService.findById(1)).thenReturn(adminUser);

        // When
        List<String> permissions = stpInterface.getPermissionList(1, "login");

        // Then
        assertNotNull(permissions);
        assertTrue(permissions.contains("user:info"));
        assertTrue(permissions.contains("user:update"));
        assertTrue(permissions.contains("user:delete"));
        assertTrue(permissions.contains("user:list"));
        assertTrue(permissions.contains("admin:dashboard"));
        assertEquals(5, permissions.size());

        verify(userService).findById(1);
    }

    @Test
    void testGetPermissionList_NormalUser() {
        // Given
        when(userService.findById(2)).thenReturn(normalUser);

        // When
        List<String> permissions = stpInterface.getPermissionList(2, "login");

        // Then
        assertNotNull(permissions);
        assertTrue(permissions.contains("user:info"));
        assertTrue(permissions.contains("user:update"));
        assertFalse(permissions.contains("user:delete"));
        assertFalse(permissions.contains("user:list"));
        assertFalse(permissions.contains("admin:dashboard"));
        assertEquals(2, permissions.size());

        verify(userService).findById(2);
    }

    @Test
    void testGetPermissionList_UserNotFound() {
        // Given
        when(userService.findById(999)).thenReturn(null);

        // When
        List<String> permissions = stpInterface.getPermissionList(999, "login");

        // Then
        assertNotNull(permissions);
        assertTrue(permissions.isEmpty());

        verify(userService).findById(999);
    }

    @Test
    void testGetPermissionList_InvalidLoginId() {
        // When
        List<String> permissions = stpInterface.getPermissionList("invalid", "login");

        // Then
        assertNotNull(permissions);
        assertTrue(permissions.isEmpty());

        verify(userService, never()).findById(any());
    }

    @Test
    void testGetPermissionList_NullLoginId() {
        // When
        List<String> permissions = stpInterface.getPermissionList(null, "login");

        // Then
        assertNotNull(permissions);
        assertTrue(permissions.isEmpty());

        verify(userService, never()).findById(any());
    }

    @Test
    void testGetRoleList_AdminUser() {
        // Given
        when(userService.findById(1)).thenReturn(adminUser);

        // When
        List<String> roles = stpInterface.getRoleList(1, "login");

        // Then
        assertNotNull(roles);
        assertTrue(roles.contains("admin"));
        assertEquals(1, roles.size());

        verify(userService).findById(1);
    }

    @Test
    void testGetRoleList_NormalUser() {
        // Given
        when(userService.findById(2)).thenReturn(normalUser);

        // When
        List<String> roles = stpInterface.getRoleList(2, "login");

        // Then
        assertNotNull(roles);
        assertTrue(roles.contains("user"));
        assertFalse(roles.contains("admin"));
        assertEquals(1, roles.size());

        verify(userService).findById(2);
    }

    @Test
    void testGetRoleList_UserNotFound() {
        // Given
        when(userService.findById(999)).thenReturn(null);

        // When
        List<String> roles = stpInterface.getRoleList(999, "login");

        // Then
        assertNotNull(roles);
        assertTrue(roles.isEmpty());

        verify(userService).findById(999);
    }

    @Test
    void testGetRoleList_InvalidLoginId() {
        // When
        List<String> roles = stpInterface.getRoleList("invalid", "login");

        // Then
        assertNotNull(roles);
        assertTrue(roles.isEmpty());

        verify(userService, never()).findById(any());
    }

    @Test
    void testGetRoleList_NullLoginId() {
        // When
        List<String> roles = stpInterface.getRoleList(null, "login");

        // Then
        assertNotNull(roles);
        assertTrue(roles.isEmpty());

        verify(userService, never()).findById(any());
    }

    @Test
    void testGetPermissionList_ServiceException() {
        // Given
        when(userService.findById(1)).thenThrow(new RuntimeException("Database error"));

        // When
        List<String> permissions = stpInterface.getPermissionList(1, "login");

        // Then
        assertNotNull(permissions);
        assertTrue(permissions.isEmpty());

        verify(userService).findById(1);
    }

    @Test
    void testGetRoleList_ServiceException() {
        // Given
        when(userService.findById(1)).thenThrow(new RuntimeException("Database error"));

        // When
        List<String> roles = stpInterface.getRoleList(1, "login");

        // Then
        assertNotNull(roles);
        assertTrue(roles.isEmpty());

        verify(userService).findById(1);
    }

    @Test
    void testGetPermissionList_StringLoginId() {
        // Given
        when(userService.findById(1)).thenReturn(adminUser);

        // When
        List<String> permissions = stpInterface.getPermissionList("1", "login");

        // Then
        assertNotNull(permissions);
        assertTrue(permissions.contains("user:info"));
        assertTrue(permissions.contains("admin:dashboard"));

        verify(userService).findById(1);
    }

    @Test
    void testGetRoleList_StringLoginId() {
        // Given
        when(userService.findById(2)).thenReturn(normalUser);

        // When
        List<String> roles = stpInterface.getRoleList("2", "login");

        // Then
        assertNotNull(roles);
        assertTrue(roles.contains("user"));
        assertFalse(roles.contains("admin"));

        verify(userService).findById(2);
    }
}
