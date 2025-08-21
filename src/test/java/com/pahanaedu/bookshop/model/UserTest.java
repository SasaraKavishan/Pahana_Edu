package com.pahanaedu.bookshop.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    private User user;

    @BeforeEach
    void setUp() {
        user = new User("testuser", "password123");
    }

    @Test
    void testDefaultConstructor() {
        User defaultUser = new User();
        assertNull(defaultUser.getUsername());
        assertNull(defaultUser.getPassword());
    }

    @Test
    void testParameterizedConstructor() {
        assertEquals("testuser", user.getUsername());
        assertEquals("password123", user.getPassword());
    }

    @Test
    void testSetAndGetUsername() {
        user.setUsername("newuser");
        assertEquals("newuser", user.getUsername());
    }

    @Test
    void testSetAndGetPassword() {
        user.setPassword("newpassword456");
        assertEquals("newpassword456", user.getPassword());
    }

    @Test
    void testSetNullUsername() {
        user.setUsername(null);
        assertNull(user.getUsername());
    }

    @Test
    void testSetNullPassword() {
        user.setPassword(null);
        assertNull(user.getPassword());
    }
}