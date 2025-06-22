package com.viper.demo.Service;

import com.viper.demo.Pojo.User;
import com.viper.demo.Repository.UserRepository;
import com.viper.demo.Service.Impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

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
    }

    @Test
    void testFindByUsername_Success() {
        // Given
        when(userRepository.findByUsernameAndNotDeleted("testuser"))
                .thenReturn(Optional.of(testUser));

        // When
        User result = userService.findByUsername("testuser");

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository).findByUsernameAndNotDeleted("testuser");
    }

    @Test
    void testFindByUsername_NotFound() {
        // Given
        when(userRepository.findByUsernameAndNotDeleted("nonexistent"))
                .thenReturn(Optional.empty());

        // When
        User result = userService.findByUsername("nonexistent");

        // Then
        assertNull(result);
        verify(userRepository).findByUsernameAndNotDeleted("nonexistent");
    }

    @Test
    void testFindByUsername_NullInput() {
        // When
        User result = userService.findByUsername(null);

        // Then
        assertNull(result);
        verify(userRepository, never()).findByUsernameAndNotDeleted(any());
    }

    @Test
    void testFindByUsername_EmptyInput() {
        // When
        User result = userService.findByUsername("");

        // Then
        assertNull(result);
        verify(userRepository, never()).findByUsernameAndNotDeleted(any());
    }

    @Test
    void testFindById_Success() {
        // Given
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));

        // When
        User result = userService.findById(1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("testuser", result.getUsername());
        verify(userRepository).findById(1);
    }

    @Test
    void testFindById_NotFound() {
        // Given
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        // When
        User result = userService.findById(999);

        // Then
        assertNull(result);
        verify(userRepository).findById(999);
    }

    @Test
    void testFindById_DeletedUser() {
        // Given
        testUser.setIsDelete(1);
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));

        // When
        User result = userService.findById(1);

        // Then
        assertNull(result);
        verify(userRepository).findById(1);
    }

    @Test
    void testFindById_NullInput() {
        // When
        User result = userService.findById(null);

        // Then
        assertNull(result);
        verify(userRepository, never()).findById(any());
    }

    @Test
    void testFindByEmail_Success() {
        // Given
        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(testUser));

        // When
        User result = userService.findByEmail("test@example.com");

        // Then
        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void testFindByEmail_DeletedUser() {
        // Given
        testUser.setIsDelete(1);
        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(testUser));

        // When
        User result = userService.findByEmail("test@example.com");

        // Then
        assertNull(result);
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void testFindByPhone_Success() {
        // Given
        when(userRepository.findByPhone("13800138000"))
                .thenReturn(Optional.of(testUser));

        // When
        User result = userService.findByPhone("13800138000");

        // Then
        assertNotNull(result);
        assertEquals("13800138000", result.getPhone());
        verify(userRepository).findByPhone("13800138000");
    }

    @Test
    void testSave_NewUser() {
        // Given
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("password");
        newUser.setEmail("new@example.com");

        User savedUser = new User();
        savedUser.setId(2);
        savedUser.setUsername("newuser");
        savedUser.setPassword("password");
        savedUser.setEmail("new@example.com");
        savedUser.setIsDelete(0);
        savedUser.setCreateTime(new Date());
        savedUser.setUpdateTime(new Date());

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When
        User result = userService.save(newUser);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getId());
        assertEquals("newuser", result.getUsername());
        assertNotNull(result.getCreateTime());
        assertNotNull(result.getUpdateTime());
        assertEquals(0, result.getIsDelete());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testSave_NullInput() {
        // When
        User result = userService.save(null);

        // Then
        assertNull(result);
        verify(userRepository, never()).save(any());
    }

    @Test
    void testUpdate_Success() {
        // Given
        testUser.setEmail("updated@example.com");
        when(userRepository.save(testUser)).thenReturn(testUser);

        // When
        User result = userService.update(testUser);

        // Then
        assertNotNull(result);
        assertEquals("updated@example.com", result.getEmail());
        assertNotNull(result.getUpdateTime());
        verify(userRepository).save(testUser);
    }

    @Test
    void testUpdate_NullInput() {
        // When
        User result = userService.update(null);

        // Then
        assertNull(result);
        verify(userRepository, never()).save(any());
    }

    @Test
    void testUpdate_NullId() {
        // Given
        testUser.setId(null);

        // When
        User result = userService.update(testUser);

        // Then
        assertNull(result);
        verify(userRepository, never()).save(any());
    }

    @Test
    void testDeleteById_Success() {
        // Given
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        boolean result = userService.deleteById(1);

        // Then
        assertTrue(result);
        verify(userRepository).findById(1);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testDeleteById_NotFound() {
        // Given
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        // When
        boolean result = userService.deleteById(999);

        // Then
        assertFalse(result);
        verify(userRepository).findById(999);
        verify(userRepository, never()).save(any());
    }

    @Test
    void testDeleteById_NullInput() {
        // When
        boolean result = userService.deleteById(null);

        // Then
        assertFalse(result);
        verify(userRepository, never()).findById(any());
    }

    @Test
    void testExistsByUsername_True() {
        // Given
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // When
        boolean result = userService.existsByUsername("testuser");

        // Then
        assertTrue(result);
        verify(userRepository).existsByUsername("testuser");
    }

    @Test
    void testExistsByUsername_False() {
        // Given
        when(userRepository.existsByUsername("nonexistent")).thenReturn(false);

        // When
        boolean result = userService.existsByUsername("nonexistent");

        // Then
        assertFalse(result);
        verify(userRepository).existsByUsername("nonexistent");
    }

    @Test
    void testExistsByEmail_True() {
        // Given
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        // When
        boolean result = userService.existsByEmail("test@example.com");

        // Then
        assertTrue(result);
        verify(userRepository).existsByEmail("test@example.com");
    }

    @Test
    void testFindAll() {
        // Given
        List<User> users = Arrays.asList(testUser, new User());
        when(userRepository.findAll()).thenReturn(users);

        // When
        List<User> result = userService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository).findAll();
    }

    @Test
    void testValidatePassword_Success() {
        // Given
        when(userRepository.findByUsernameAndNotDeleted("testuser"))
                .thenReturn(Optional.of(testUser));

        // When
        boolean result = userService.validatePassword("testuser", "password123");

        // Then
        assertTrue(result);
        verify(userRepository).findByUsernameAndNotDeleted("testuser");
    }

    @Test
    void testValidatePassword_WrongPassword() {
        // Given
        when(userRepository.findByUsernameAndNotDeleted("testuser"))
                .thenReturn(Optional.of(testUser));

        // When
        boolean result = userService.validatePassword("testuser", "wrongpassword");

        // Then
        assertFalse(result);
        verify(userRepository).findByUsernameAndNotDeleted("testuser");
    }

    @Test
    void testValidatePassword_UserNotFound() {
        // Given
        when(userRepository.findByUsernameAndNotDeleted("nonexistent"))
                .thenReturn(Optional.empty());

        // When
        boolean result = userService.validatePassword("nonexistent", "password");

        // Then
        assertFalse(result);
        verify(userRepository).findByUsernameAndNotDeleted("nonexistent");
    }

    @Test
    void testValidatePassword_NullInputs() {
        // When & Then
        assertFalse(userService.validatePassword(null, "password"));
        assertFalse(userService.validatePassword("username", null));
        assertFalse(userService.validatePassword(null, null));
        
        verify(userRepository, never()).findByUsernameAndNotDeleted(any());
    }
}
