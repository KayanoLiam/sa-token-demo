package com.viper.demo.Pojo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RegisterRequestTest {

    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
    }

    @Test
    void testDefaultConstructor() {
        // When
        RegisterRequest request = new RegisterRequest();

        // Then
        assertNotNull(request);
        assertNull(request.getUsername());
        assertNull(request.getPassword());
        assertNull(request.getEmail());
        assertNull(request.getPhone());
    }

    @Test
    void testSettersAndGetters() {
        // When
        registerRequest.setUsername("testuser");
        registerRequest.setPassword("password123");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPhone("13800138000");

        // Then
        assertEquals("testuser", registerRequest.getUsername());
        assertEquals("password123", registerRequest.getPassword());
        assertEquals("test@example.com", registerRequest.getEmail());
        assertEquals("13800138000", registerRequest.getPhone());
    }

    @Test
    void testNullValues() {
        // When
        registerRequest.setUsername(null);
        registerRequest.setPassword(null);
        registerRequest.setEmail(null);
        registerRequest.setPhone(null);

        // Then
        assertNull(registerRequest.getUsername());
        assertNull(registerRequest.getPassword());
        assertNull(registerRequest.getEmail());
        assertNull(registerRequest.getPhone());
    }

    @Test
    void testEmptyValues() {
        // When
        registerRequest.setUsername("");
        registerRequest.setPassword("");
        registerRequest.setEmail("");
        registerRequest.setPhone("");

        // Then
        assertEquals("", registerRequest.getUsername());
        assertEquals("", registerRequest.getPassword());
        assertEquals("", registerRequest.getEmail());
        assertEquals("", registerRequest.getPhone());
    }

    @Test
    void testWhitespaceValues() {
        // When
        registerRequest.setUsername("   ");
        registerRequest.setPassword("   ");
        registerRequest.setEmail("   ");
        registerRequest.setPhone("   ");

        // Then
        assertEquals("   ", registerRequest.getUsername());
        assertEquals("   ", registerRequest.getPassword());
        assertEquals("   ", registerRequest.getEmail());
        assertEquals("   ", registerRequest.getPhone());
    }

    @Test
    void testValidEmailFormats() {
        // Test various valid email formats
        String[] validEmails = {
            "test@example.com",
            "user.name@domain.co.uk",
            "user+tag@example.org",
            "123@example.com",
            "test_email@sub.domain.com"
        };

        for (String email : validEmails) {
            registerRequest.setEmail(email);
            assertEquals(email, registerRequest.getEmail());
        }
    }

    @Test
    void testValidPhoneFormats() {
        // Test various phone formats
        String[] validPhones = {
            "13800138000",
            "+86 138 0013 8000",
            "138-0013-8000",
            "(138) 0013-8000",
            "138 0013 8000"
        };

        for (String phone : validPhones) {
            registerRequest.setPhone(phone);
            assertEquals(phone, registerRequest.getPhone());
        }
    }

    @Test
    void testSpecialCharactersInUsername() {
        // Given
        String[] specialUsernames = {
            "user_name",
            "user-name",
            "user.name",
            "user123",
            "123user"
        };

        for (String username : specialUsernames) {
            registerRequest.setUsername(username);
            assertEquals(username, registerRequest.getUsername());
        }
    }

    @Test
    void testComplexPassword() {
        // Given
        String complexPassword = "P@ssw0rd!#$%^&*()";

        // When
        registerRequest.setPassword(complexPassword);

        // Then
        assertEquals(complexPassword, registerRequest.getPassword());
    }

    @Test
    void testLongValues() {
        // Given
        String longUsername = "a".repeat(100);
        String longPassword = "b".repeat(100);
        String longEmail = "c".repeat(50) + "@example.com";
        String longPhone = "1".repeat(20);

        // When
        registerRequest.setUsername(longUsername);
        registerRequest.setPassword(longPassword);
        registerRequest.setEmail(longEmail);
        registerRequest.setPhone(longPhone);

        // Then
        assertEquals(longUsername, registerRequest.getUsername());
        assertEquals(longPassword, registerRequest.getPassword());
        assertEquals(longEmail, registerRequest.getEmail());
        assertEquals(longPhone, registerRequest.getPhone());
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        RegisterRequest request1 = new RegisterRequest();
        request1.setUsername("testuser");
        request1.setPassword("password123");
        request1.setEmail("test@example.com");
        request1.setPhone("13800138000");

        RegisterRequest request2 = new RegisterRequest();
        request2.setUsername("testuser");
        request2.setPassword("password123");
        request2.setEmail("test@example.com");
        request2.setPhone("13800138000");

        RegisterRequest request3 = new RegisterRequest();
        request3.setUsername("otheruser");
        request3.setPassword("password123");
        request3.setEmail("other@example.com");
        request3.setPhone("13800138001");

        // Then
        assertEquals(request1, request2);
        assertNotEquals(request1, request3);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotEquals(request1.hashCode(), request3.hashCode());
    }

    @Test
    void testToString() {
        // Given
        registerRequest.setUsername("testuser");
        registerRequest.setPassword("password123");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPhone("13800138000");

        // When
        String toString = registerRequest.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("testuser"));
        assertTrue(toString.contains("test@example.com"));
        assertTrue(toString.contains("13800138000"));
        // 注意：密码可能不应该出现在toString中，但这取决于Lombok的@Data注解行为
    }

    @Test
    void testUnicodeCharacters() {
        // Given
        String unicodeUsername = "用户名";
        String unicodePassword = "密码123";

        // When
        registerRequest.setUsername(unicodeUsername);
        registerRequest.setPassword(unicodePassword);

        // Then
        assertEquals(unicodeUsername, registerRequest.getUsername());
        assertEquals(unicodePassword, registerRequest.getPassword());
    }

    @Test
    void testInternationalPhoneNumbers() {
        // Given
        String[] internationalPhones = {
            "+1-555-123-4567",
            "+44 20 7946 0958",
            "+86 138 0013 8000",
            "+33 1 42 86 83 26"
        };

        for (String phone : internationalPhones) {
            registerRequest.setPhone(phone);
            assertEquals(phone, registerRequest.getPhone());
        }
    }

    @Test
    void testInternationalEmailDomains() {
        // Given
        String[] internationalEmails = {
            "test@example.co.uk",
            "user@domain.de",
            "email@site.fr",
            "contact@company.jp"
        };

        for (String email : internationalEmails) {
            registerRequest.setEmail(email);
            assertEquals(email, registerRequest.getEmail());
        }
    }

    @Test
    void testPartialDataSet() {
        // Test setting only some fields
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@example.com");
        // Leave password and phone as null

        assertEquals("testuser", registerRequest.getUsername());
        assertEquals("test@example.com", registerRequest.getEmail());
        assertNull(registerRequest.getPassword());
        assertNull(registerRequest.getPhone());
    }

    @Test
    void testOverwriteValues() {
        // Given
        registerRequest.setUsername("olduser");
        registerRequest.setPassword("oldpassword");
        registerRequest.setEmail("old@example.com");
        registerRequest.setPhone("13800138000");

        // When
        registerRequest.setUsername("newuser");
        registerRequest.setPassword("newpassword");
        registerRequest.setEmail("new@example.com");
        registerRequest.setPhone("13800138001");

        // Then
        assertEquals("newuser", registerRequest.getUsername());
        assertEquals("newpassword", registerRequest.getPassword());
        assertEquals("new@example.com", registerRequest.getEmail());
        assertEquals("13800138001", registerRequest.getPhone());
    }
}
