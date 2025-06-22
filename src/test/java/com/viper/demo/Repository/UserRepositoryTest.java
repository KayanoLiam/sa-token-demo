package com.viper.demo.Repository;

import com.viper.demo.Pojo.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private User deletedUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password123");
        testUser.setEmail("test@example.com");
        testUser.setPhone("13800138000");
        testUser.setIsDelete(0);
        testUser.setCreateTime(new Date());
        testUser.setUpdateTime(new Date());

        deletedUser = new User();
        deletedUser.setUsername("deleteduser");
        deletedUser.setPassword("password123");
        deletedUser.setEmail("deleted@example.com");
        deletedUser.setPhone("13800138001");
        deletedUser.setIsDelete(1);
        deletedUser.setCreateTime(new Date());
        deletedUser.setUpdateTime(new Date());

        // 保存测试数据
        entityManager.persistAndFlush(testUser);
        entityManager.persistAndFlush(deletedUser);
    }

    @Test
    void testFindByUsername_Success() {
        // When
        Optional<User> result = userRepository.findByUsername("testuser");

        // Then
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
        assertEquals("test@example.com", result.get().getEmail());
    }

    @Test
    void testFindByUsername_NotFound() {
        // When
        Optional<User> result = userRepository.findByUsername("nonexistent");

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testFindByUsername_DeletedUser() {
        // When
        Optional<User> result = userRepository.findByUsername("deleteduser");

        // Then
        assertTrue(result.isPresent()); // findByUsername 应该能找到已删除的用户
        assertEquals(1, result.get().getIsDelete());
    }

    @Test
    void testFindByUsernameAndNotDeleted_Success() {
        // When
        Optional<User> result = userRepository.findByUsernameAndNotDeleted("testuser");

        // Then
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
        assertEquals(0, result.get().getIsDelete());
    }

    @Test
    void testFindByUsernameAndNotDeleted_DeletedUser() {
        // When
        Optional<User> result = userRepository.findByUsernameAndNotDeleted("deleteduser");

        // Then
        assertFalse(result.isPresent()); // 已删除的用户不应该被找到
    }

    @Test
    void testFindByUsernameAndNotDeleted_NotFound() {
        // When
        Optional<User> result = userRepository.findByUsernameAndNotDeleted("nonexistent");

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testFindByEmail_Success() {
        // When
        Optional<User> result = userRepository.findByEmail("test@example.com");

        // Then
        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
        assertEquals("testuser", result.get().getUsername());
    }

    @Test
    void testFindByEmail_NotFound() {
        // When
        Optional<User> result = userRepository.findByEmail("nonexistent@example.com");

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testFindByPhone_Success() {
        // When
        Optional<User> result = userRepository.findByPhone("13800138000");

        // Then
        assertTrue(result.isPresent());
        assertEquals("13800138000", result.get().getPhone());
        assertEquals("testuser", result.get().getUsername());
    }

    @Test
    void testFindByPhone_NotFound() {
        // When
        Optional<User> result = userRepository.findByPhone("99999999999");

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testExistsByUsername_True() {
        // When
        boolean exists = userRepository.existsByUsername("testuser");

        // Then
        assertTrue(exists);
    }

    @Test
    void testExistsByUsername_False() {
        // When
        boolean exists = userRepository.existsByUsername("nonexistent");

        // Then
        assertFalse(exists);
    }

    @Test
    void testExistsByUsername_DeletedUser() {
        // When
        boolean exists = userRepository.existsByUsername("deleteduser");

        // Then
        assertTrue(exists); // existsByUsername 应该能找到已删除的用户
    }

    @Test
    void testExistsByEmail_True() {
        // When
        boolean exists = userRepository.existsByEmail("test@example.com");

        // Then
        assertTrue(exists);
    }

    @Test
    void testExistsByEmail_False() {
        // When
        boolean exists = userRepository.existsByEmail("nonexistent@example.com");

        // Then
        assertFalse(exists);
    }

    @Test
    void testSaveUser() {
        // Given
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("newpassword");
        newUser.setEmail("newuser@example.com");
        newUser.setPhone("13800138002");
        newUser.setIsDelete(0);
        newUser.setCreateTime(new Date());
        newUser.setUpdateTime(new Date());

        // When
        User savedUser = userRepository.save(newUser);

        // Then
        assertNotNull(savedUser.getId());
        assertEquals("newuser", savedUser.getUsername());
        assertEquals("newuser@example.com", savedUser.getEmail());

        // 验证数据库中确实保存了
        Optional<User> found = userRepository.findById(savedUser.getId());
        assertTrue(found.isPresent());
        assertEquals("newuser", found.get().getUsername());
    }

    @Test
    void testUpdateUser() {
        // Given
        testUser.setEmail("updated@example.com");
        testUser.setPhone("13800138999");

        // When
        User updatedUser = userRepository.save(testUser);

        // Then
        assertEquals("updated@example.com", updatedUser.getEmail());
        assertEquals("13800138999", updatedUser.getPhone());

        // 验证数据库中确实更新了
        Optional<User> found = userRepository.findById(testUser.getId());
        assertTrue(found.isPresent());
        assertEquals("updated@example.com", found.get().getEmail());
        assertEquals("13800138999", found.get().getPhone());
    }

    @Test
    void testDeleteUser() {
        // Given
        Integer userId = testUser.getId();

        // When
        userRepository.deleteById(userId);
        entityManager.flush();

        // Then
        Optional<User> found = userRepository.findById(userId);
        assertFalse(found.isPresent());
    }

    @Test
    void testFindAll() {
        // When
        Iterable<User> users = userRepository.findAll();

        // Then
        assertNotNull(users);
        // 转换为List以便计数
        List<User> userList = new ArrayList<>();
        users.forEach(userList::add);
        assertEquals(2, userList.size()); // 应该有两个用户：testUser 和 deletedUser
    }

    @Test
    void testFindByUsernameAndNotDeleted_NullIsDelete() {
        // Given - 创建一个 isDelete 为 null 的用户
        User nullDeleteUser = new User();
        nullDeleteUser.setUsername("nulldeleteuser");
        nullDeleteUser.setPassword("password123");
        nullDeleteUser.setEmail("nulldelete@example.com");
        nullDeleteUser.setPhone("13800138003");
        nullDeleteUser.setIsDelete(null); // 设置为 null
        nullDeleteUser.setCreateTime(new Date());
        nullDeleteUser.setUpdateTime(new Date());
        entityManager.persistAndFlush(nullDeleteUser);

        // When
        Optional<User> result = userRepository.findByUsernameAndNotDeleted("nulldeleteuser");

        // Then
        assertTrue(result.isPresent()); // isDelete 为 null 的用户应该被找到
        assertEquals("nulldeleteuser", result.get().getUsername());
    }
}
