package com.viper.demo.Pojo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    private User user;
    private Date testDate;

    @BeforeEach
    void setUp() {
        user = new User();
        testDate = new Date();
    }

    @Test
    void testDefaultConstructor() {
        // When
        User newUser = new User();

        // Then
        assertNotNull(newUser);
        assertNull(newUser.getId());
        assertNull(newUser.getUsername());
        assertNull(newUser.getPassword());
        assertNull(newUser.getEmail());
        assertNull(newUser.getPhone());
        assertNull(newUser.getIsDelete());
        assertNull(newUser.getCreateTime());
        assertNull(newUser.getUpdateTime());
    }

    @Test
    void testAllArgsConstructor() {
        // When
        User newUser = new User(1, "testuser", "password", "test@example.com", 
                               "13800138000", 0, testDate, testDate);

        // Then
        assertEquals(1, newUser.getId());
        assertEquals("testuser", newUser.getUsername());
        assertEquals("password", newUser.getPassword());
        assertEquals("test@example.com", newUser.getEmail());
        assertEquals("13800138000", newUser.getPhone());
        assertEquals(0, newUser.getIsDelete());
        assertEquals(testDate, newUser.getCreateTime());
        assertEquals(testDate, newUser.getUpdateTime());
    }

    @Test
    void testSettersAndGetters() {
        // When
        user.setId(1);
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setEmail("test@example.com");
        user.setPhone("13800138000");
        user.setIsDelete(0);
        user.setCreateTime(testDate);
        user.setUpdateTime(testDate);

        // Then
        assertEquals(1, user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("password123", user.getPassword());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("13800138000", user.getPhone());
        assertEquals(0, user.getIsDelete());
        assertEquals(testDate, user.getCreateTime());
        assertEquals(testDate, user.getUpdateTime());
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        User user1 = new User(1, "testuser", "password", "test@example.com", 
                             "13800138000", 0, testDate, testDate);
        User user2 = new User(1, "testuser", "password", "test@example.com", 
                             "13800138000", 0, testDate, testDate);
        User user3 = new User(2, "otheruser", "password", "other@example.com", 
                             "13800138001", 0, testDate, testDate);

        // Then
        assertEquals(user1, user2);
        assertNotEquals(user1, user3);
        assertEquals(user1.hashCode(), user2.hashCode());
        assertNotEquals(user1.hashCode(), user3.hashCode());
    }

    @Test
    void testToString() {
        // Given
        user.setId(1);
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        // When
        String toString = user.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("testuser"));
        assertTrue(toString.contains("test@example.com"));
        assertTrue(toString.contains("1"));
    }

    @Test
    void testNullValues() {
        // When
        user.setId(null);
        user.setUsername(null);
        user.setPassword(null);
        user.setEmail(null);
        user.setPhone(null);
        user.setIsDelete(null);
        user.setCreateTime(null);
        user.setUpdateTime(null);

        // Then
        assertNull(user.getId());
        assertNull(user.getUsername());
        assertNull(user.getPassword());
        assertNull(user.getEmail());
        assertNull(user.getPhone());
        assertNull(user.getIsDelete());
        assertNull(user.getCreateTime());
        assertNull(user.getUpdateTime());
    }

    @Test
    void testIsDeleteValues() {
        // Test different isDelete values
        user.setIsDelete(0);
        assertEquals(0, user.getIsDelete());

        user.setIsDelete(1);
        assertEquals(1, user.getIsDelete());

        user.setIsDelete(null);
        assertNull(user.getIsDelete());
    }

    @Test
    void testDateFields() {
        // Given
        Date createTime = new Date();
        Date updateTime = new Date(createTime.getTime() + 1000); // 1 second later

        // When
        user.setCreateTime(createTime);
        user.setUpdateTime(updateTime);

        // Then
        assertEquals(createTime, user.getCreateTime());
        assertEquals(updateTime, user.getUpdateTime());
        assertTrue(user.getUpdateTime().after(user.getCreateTime()));
    }

    @Test
    void testEmptyStringValues() {
        // When
        user.setUsername("");
        user.setPassword("");
        user.setEmail("");
        user.setPhone("");

        // Then
        assertEquals("", user.getUsername());
        assertEquals("", user.getPassword());
        assertEquals("", user.getEmail());
        assertEquals("", user.getPhone());
    }

    @Test
    void testLongStringValues() {
        // Given
        String longString = "a".repeat(1000);

        // When
        user.setUsername(longString);
        user.setPassword(longString);
        user.setEmail(longString);
        user.setPhone(longString);

        // Then
        assertEquals(longString, user.getUsername());
        assertEquals(longString, user.getPassword());
        assertEquals(longString, user.getEmail());
        assertEquals(longString, user.getPhone());
    }

    @Test
    void testSpecialCharacters() {
        // Given
        String specialChars = "!@#$%^&*()_+-=[]{}|;':\",./<>?";

        // When
        user.setUsername(specialChars);
        user.setPassword(specialChars);

        // Then
        assertEquals(specialChars, user.getUsername());
        assertEquals(specialChars, user.getPassword());
    }
}
