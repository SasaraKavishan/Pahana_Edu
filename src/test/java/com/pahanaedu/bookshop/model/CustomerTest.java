package com.pahanaedu.bookshop.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {
    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer("C123", "John Doe", "123 Main St", "555-0123");
    }

    @Test
    void testConstructor() {
        assertEquals("C123", customer.getAccountNumber());
        assertEquals("John Doe", customer.getName());
        assertEquals("123 Main St", customer.getAddress());
        assertEquals("555-0123", customer.getTelephone());
    }

    @Test
    void testSetAndGetAccountNumber() {
        customer.setAccountNumber("C456");
        assertEquals("C456", customer.getAccountNumber());
    }

    @Test
    void testSetAndGetName() {
        customer.setName("Jane Smith");
        assertEquals("Jane Smith", customer.getName());
    }

    @Test
    void testSetAndGetAddress() {
        customer.setAddress("456 Oak Ave");
        assertEquals("456 Oak Ave", customer.getAddress());
    }

    @Test
    void testSetAndGetTelephone() {
        customer.setTelephone("555-9876");
        assertEquals("555-9876", customer.getTelephone());
    }

    @Test
    void testSetNullAccountNumber() {
        customer.setAccountNumber(null);
        assertNull(customer.getAccountNumber());
    }

    @Test
    void testSetNullName() {
        customer.setName(null);
        assertNull(customer.getName());
    }

    @Test
    void testSetNullAddress() {
        customer.setAddress(null);
        assertNull(customer.getAddress());
    }

    @Test
    void testSetNullTelephone() {
        customer.setTelephone(null);
        assertNull(customer.getTelephone());
    }
}