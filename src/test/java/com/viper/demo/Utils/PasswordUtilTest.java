package com.viper.demo.Utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordUtilTest {

    @Test
    void testGenerateSalt() {
        // When
        String salt1 = PasswordUtil.generateSalt();
        String salt2 = PasswordUtil.generateSalt();

        // Then
        assertNotNull(salt1);
        assertNotNull(salt2);
        assertNotEquals(salt1, salt2); // 每次生成的盐值应该不同
        assertTrue(salt1.length() > 0);
        assertTrue(salt2.length() > 0);
    }

    @Test
    void testEncryptPassword() {
        // Given
        String password = "testPassword123";
        String salt = "testSalt";

        // When
        String encrypted1 = PasswordUtil.encryptPassword(password, salt);
        String encrypted2 = PasswordUtil.encryptPassword(password, salt);

        // Then
        assertNotNull(encrypted1);
        assertNotNull(encrypted2);
        assertEquals(encrypted1, encrypted2); // 相同密码和盐值应该产生相同的加密结果
        assertNotEquals(password, encrypted1); // 加密后的密码应该与原密码不同
    }

    @Test
    void testEncryptPassword_DifferentSalts() {
        // Given
        String password = "testPassword123";
        String salt1 = "salt1";
        String salt2 = "salt2";

        // When
        String encrypted1 = PasswordUtil.encryptPassword(password, salt1);
        String encrypted2 = PasswordUtil.encryptPassword(password, salt2);

        // Then
        assertNotNull(encrypted1);
        assertNotNull(encrypted2);
        assertNotEquals(encrypted1, encrypted2); // 不同盐值应该产生不同的加密结果
    }

    @Test
    void testEncryptPassword_DifferentPasswords() {
        // Given
        String password1 = "password1";
        String password2 = "password2";
        String salt = "testSalt";

        // When
        String encrypted1 = PasswordUtil.encryptPassword(password1, salt);
        String encrypted2 = PasswordUtil.encryptPassword(password2, salt);

        // Then
        assertNotNull(encrypted1);
        assertNotNull(encrypted2);
        assertNotEquals(encrypted1, encrypted2); // 不同密码应该产生不同的加密结果
    }

    @Test
    void testVerifyPassword_Success() {
        // Given
        String password = "testPassword123";
        String salt = PasswordUtil.generateSalt();
        String encryptedPassword = PasswordUtil.encryptPassword(password, salt);

        // When
        boolean result = PasswordUtil.verifyPassword(password, salt, encryptedPassword);

        // Then
        assertTrue(result);
    }

    @Test
    void testVerifyPassword_WrongPassword() {
        // Given
        String password = "testPassword123";
        String wrongPassword = "wrongPassword";
        String salt = PasswordUtil.generateSalt();
        String encryptedPassword = PasswordUtil.encryptPassword(password, salt);

        // When
        boolean result = PasswordUtil.verifyPassword(wrongPassword, salt, encryptedPassword);

        // Then
        assertFalse(result);
    }

    @Test
    void testVerifyPassword_WrongSalt() {
        // Given
        String password = "testPassword123";
        String salt = PasswordUtil.generateSalt();
        String wrongSalt = PasswordUtil.generateSalt();
        String encryptedPassword = PasswordUtil.encryptPassword(password, salt);

        // When
        boolean result = PasswordUtil.verifyPassword(password, wrongSalt, encryptedPassword);

        // Then
        assertFalse(result);
    }

    @Test
    void testMd5Encrypt() {
        // Given
        String password = "testPassword123";

        // When
        String encrypted1 = PasswordUtil.md5Encrypt(password);
        String encrypted2 = PasswordUtil.md5Encrypt(password);

        // Then
        assertNotNull(encrypted1);
        assertNotNull(encrypted2);
        assertEquals(encrypted1, encrypted2); // 相同输入应该产生相同的MD5结果
        assertEquals(32, encrypted1.length()); // MD5结果应该是32位十六进制字符串
        assertNotEquals(password, encrypted1); // 加密后应该与原密码不同
    }

    @Test
    void testMd5Encrypt_DifferentPasswords() {
        // Given
        String password1 = "password1";
        String password2 = "password2";

        // When
        String encrypted1 = PasswordUtil.md5Encrypt(password1);
        String encrypted2 = PasswordUtil.md5Encrypt(password2);

        // Then
        assertNotNull(encrypted1);
        assertNotNull(encrypted2);
        assertNotEquals(encrypted1, encrypted2); // 不同密码应该产生不同的MD5结果
    }

    @Test
    void testMd5Encrypt_EmptyString() {
        // Given
        String password = "";

        // When
        String encrypted = PasswordUtil.md5Encrypt(password);

        // Then
        assertNotNull(encrypted);
        assertEquals(32, encrypted.length()); // 即使是空字符串，MD5结果也应该是32位
    }

    @Test
    void testEncryptPassword_EmptyPassword() {
        // Given
        String password = "";
        String salt = "testSalt";

        // When
        String encrypted = PasswordUtil.encryptPassword(password, salt);

        // Then
        assertNotNull(encrypted);
        assertTrue(encrypted.length() > 0);
    }

    @Test
    void testEncryptPassword_EmptySalt() {
        // Given
        String password = "testPassword";
        String salt = "";

        // When
        String encrypted = PasswordUtil.encryptPassword(password, salt);

        // Then
        assertNotNull(encrypted);
        assertTrue(encrypted.length() > 0);
    }

    @Test
    void testVerifyPassword_EmptyInputs() {
        // Given
        String salt = PasswordUtil.generateSalt();
        String encryptedPassword = PasswordUtil.encryptPassword("test", salt);

        // When & Then
        assertFalse(PasswordUtil.verifyPassword("", salt, encryptedPassword));
        assertFalse(PasswordUtil.verifyPassword("test", "", encryptedPassword));
        assertFalse(PasswordUtil.verifyPassword("test", salt, ""));
    }

    @Test
    void testCompleteWorkflow() {
        // Given
        String originalPassword = "mySecurePassword123!";

        // When
        String salt = PasswordUtil.generateSalt();
        String encryptedPassword = PasswordUtil.encryptPassword(originalPassword, salt);
        boolean isValid = PasswordUtil.verifyPassword(originalPassword, salt, encryptedPassword);
        boolean isInvalid = PasswordUtil.verifyPassword("wrongPassword", salt, encryptedPassword);

        // Then
        assertNotNull(salt);
        assertNotNull(encryptedPassword);
        assertTrue(isValid);
        assertFalse(isInvalid);
        assertNotEquals(originalPassword, encryptedPassword);
    }
}
