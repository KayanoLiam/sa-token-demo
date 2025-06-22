package com.viper.demo.Pojo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ResultTest {

    @Test
    void testDefaultConstructor() {
        // When
        Result<String> result = new Result<>();

        // Then
        assertNotNull(result);
        assertNull(result.getCode());
        assertNull(result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void testAllArgsConstructor() {
        // When
        Result<String> result = new Result<>(200, "success", "test data");

        // Then
        assertEquals(200, result.getCode());
        assertEquals("success", result.getMessage());
        assertEquals("test data", result.getData());
    }

    @Test
    void testSettersAndGetters() {
        // Given
        Result<String> result = new Result<>();

        // When
        result.setCode(200);
        result.setMessage("success");
        result.setData("test data");

        // Then
        assertEquals(200, result.getCode());
        assertEquals("success", result.getMessage());
        assertEquals("test data", result.getData());
    }

    @Test
    void testSuccessMethod() {
        // When
        Result<String> result = Result.success("test data");

        // Then
        assertEquals(200, result.getCode());
        assertEquals("success", result.getMessage());
        assertEquals("test data", result.getData());
    }

    @Test
    void testSuccessMethodWithNullData() {
        // When
        Result<String> result = Result.success(null);

        // Then
        assertEquals(200, result.getCode());
        assertEquals("success", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void testSuccessMethodWithDifferentDataTypes() {
        // Test with Integer
        Result<Integer> intResult = Result.success(123);
        assertEquals(200, intResult.getCode());
        assertEquals("success", intResult.getMessage());
        assertEquals(123, intResult.getData());

        // Test with Boolean
        Result<Boolean> boolResult = Result.success(true);
        assertEquals(200, boolResult.getCode());
        assertEquals("success", boolResult.getMessage());
        assertTrue(boolResult.getData());

        // Test with Object
        User user = new User();
        user.setUsername("test");
        Result<User> userResult = Result.success(user);
        assertEquals(200, userResult.getCode());
        assertEquals("success", userResult.getMessage());
        assertEquals("test", userResult.getData().getUsername());
    }

    @Test
    void testErrorMethod() {
        // When
        Result<String> result = Result.error(400, "Bad Request");

        // Then
        assertEquals(400, result.getCode());
        assertEquals("Bad Request", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void testErrorMethodWithDifferentCodes() {
        // Test 401
        Result<String> result401 = Result.error(401, "Unauthorized");
        assertEquals(401, result401.getCode());
        assertEquals("Unauthorized", result401.getMessage());

        // Test 404
        Result<String> result404 = Result.error(404, "Not Found");
        assertEquals(404, result404.getCode());
        assertEquals("Not Found", result404.getMessage());

        // Test 500
        Result<String> result500 = Result.error(500, "Internal Server Error");
        assertEquals(500, result500.getCode());
        assertEquals("Internal Server Error", result500.getMessage());
    }

    @Test
    void testErrorMethodWithNullMessage() {
        // When
        Result<String> result = Result.error(400, null);

        // Then
        assertEquals(400, result.getCode());
        assertNull(result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void testErrorMethodWithEmptyMessage() {
        // When
        Result<String> result = Result.error(400, "");

        // Then
        assertEquals(400, result.getCode());
        assertEquals("", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        Result<String> result1 = new Result<>(200, "success", "data");
        Result<String> result2 = new Result<>(200, "success", "data");
        Result<String> result3 = new Result<>(400, "error", "data");

        // Then
        assertEquals(result1, result2);
        assertNotEquals(result1, result3);
        assertEquals(result1.hashCode(), result2.hashCode());
        assertNotEquals(result1.hashCode(), result3.hashCode());
    }

    @Test
    void testToString() {
        // Given
        Result<String> result = new Result<>(200, "success", "test data");

        // When
        String toString = result.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("200"));
        assertTrue(toString.contains("success"));
        assertTrue(toString.contains("test data"));
    }

    @Test
    void testGenericTypes() {
        // Test with different generic types
        Result<Integer> intResult = new Result<>(200, "success", 42);
        assertEquals(Integer.class, intResult.getData().getClass());
        assertEquals(42, intResult.getData());

        Result<Boolean> boolResult = new Result<>(200, "success", false);
        assertEquals(Boolean.class, boolResult.getData().getClass());
        assertFalse(boolResult.getData());

        Result<Double> doubleResult = new Result<>(200, "success", 3.14);
        assertEquals(Double.class, doubleResult.getData().getClass());
        assertEquals(3.14, doubleResult.getData(), 0.001);
    }

    @Test
    void testNullValues() {
        // Given
        Result<String> result = new Result<>();

        // When
        result.setCode(null);
        result.setMessage(null);
        result.setData(null);

        // Then
        assertNull(result.getCode());
        assertNull(result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void testStaticMethodsReturnNewInstances() {
        // When
        Result<String> success1 = Result.success("data1");
        Result<String> success2 = Result.success("data2");
        Result<String> error1 = Result.error(400, "error1");
        Result<String> error2 = Result.error(500, "error2");

        // Then
        assertNotSame(success1, success2);
        assertNotSame(error1, error2);
        assertNotSame(success1, error1);
    }

    @Test
    void testComplexDataTypes() {
        // Given
        User user = new User();
        user.setId(1);
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        // When
        Result<User> result = Result.success(user);

        // Then
        assertEquals(200, result.getCode());
        assertEquals("success", result.getMessage());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().getId());
        assertEquals("testuser", result.getData().getUsername());
        assertEquals("test@example.com", result.getData().getEmail());
    }
}
