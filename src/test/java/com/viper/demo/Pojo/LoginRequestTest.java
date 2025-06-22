package com.viper.demo.Pojo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LoginRequestTest {

    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
    }

    @Test
    void testDefaultConstructor() {
        // When
        LoginRequest request = new LoginRequest();

        // Then
        assertNotNull(request);
        assertNull(request.getUsername());
        assertNull(request.getPassword());
    }

    @Test
    void testSettersAndGetters() {
        // When
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        // Then
        assertEquals("testuser", loginRequest.getUsername());
        assertEquals("password123", loginRequest.getPassword());
    }

    @Test
    void testNullValues() {
        // When
        loginRequest.setUsername(null);
        loginRequest.setPassword(null);

        // Then
        assertNull(loginRequest.getUsername());
        assertNull(loginRequest.getPassword());
    }

    @Test
    void testEmptyValues() {
        // When
        loginRequest.setUsername("");
        loginRequest.setPassword("");

        // Then
        assertEquals("", loginRequest.getUsername());
        assertEquals("", loginRequest.getPassword());
    }

    @Test
    void testWhitespaceValues() {
        // When
        loginRequest.setUsername("   ");
        loginRequest.setPassword("   ");

        // Then
        assertEquals("   ", loginRequest.getUsername());
        assertEquals("   ", loginRequest.getPassword());
    }

    @Test
    void testSpecialCharacters() {
        // Given
        String specialUsername = "user@domain.com";
        String specialPassword = "P@ssw0rd!#$";

        // When
        loginRequest.setUsername(specialUsername);
        loginRequest.setPassword(specialPassword);

        // Then
        assertEquals(specialUsername, loginRequest.getUsername());
        assertEquals(specialPassword, loginRequest.getPassword());
    }

    @Test
    void testLongValues() {
        // Given
        String longUsername = "a".repeat(1000);
        String longPassword = "b".repeat(1000);

        // When
        loginRequest.setUsername(longUsername);
        loginRequest.setPassword(longPassword);

        // Then
        assertEquals(longUsername, loginRequest.getUsername());
        assertEquals(longPassword, loginRequest.getPassword());
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        LoginRequest request1 = new LoginRequest();
        request1.setUsername("testuser");
        request1.setPassword("password123");

        LoginRequest request2 = new LoginRequest();
        request2.setUsername("testuser");
        request2.setPassword("password123");

        LoginRequest request3 = new LoginRequest();
        request3.setUsername("otheruser");
        request3.setPassword("password123");

        // Then
        assertEquals(request1, request2);
        assertNotEquals(request1, request3);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotEquals(request1.hashCode(), request3.hashCode());
    }

    @Test
    void testToString() {
        // Given
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        // When
        String toString = loginRequest.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("testuser"));
        // 注意：密码可能不应该出现在toString中，但这取决于Lombok的@Data注解行为
    }

    @Test
    void testUnicodeCharacters() {
        // Given
        String unicodeUsername = "用户名";
        String unicodePassword = "密码123";

        // When
        loginRequest.setUsername(unicodeUsername);
        loginRequest.setPassword(unicodePassword);

        // Then
        assertEquals(unicodeUsername, loginRequest.getUsername());
        assertEquals(unicodePassword, loginRequest.getPassword());
    }

    @Test
    void testNumericValues() {
        // When
        loginRequest.setUsername("123456");
        loginRequest.setPassword("789012");

        // Then
        assertEquals("123456", loginRequest.getUsername());
        assertEquals("789012", loginRequest.getPassword());
    }

    @Test
    void testMixedCaseValues() {
        // When
        loginRequest.setUsername("TestUser");
        loginRequest.setPassword("PassWord123");

        // Then
        assertEquals("TestUser", loginRequest.getUsername());
        assertEquals("PassWord123", loginRequest.getPassword());
    }

    @Test
    void testEmailAsUsername() {
        // When
        loginRequest.setUsername("user@example.com");
        loginRequest.setPassword("password123");

        // Then
        assertEquals("user@example.com", loginRequest.getUsername());
        assertEquals("password123", loginRequest.getPassword());
    }

    @Test
    void testPhoneAsUsername() {
        // When
        loginRequest.setUsername("13800138000");
        loginRequest.setPassword("password123");

        // Then
        assertEquals("13800138000", loginRequest.getUsername());
        assertEquals("password123", loginRequest.getPassword());
    }
}
