package com.pahanaedu.bookshop.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ItemTest {
    private Item item;

    @BeforeEach
    void setUp() {
        item = new Item(1, "Book", 29.99, "Fiction", 100);
    }

    @Test
    void testConstructor() {
        assertEquals(1, item.getItemId());
        assertEquals("Book", item.getName());
        assertEquals(29.99, item.getPrice(), 0.001);
        assertEquals("Fiction", item.getCategory());
        assertEquals(100, item.getStock());
    }

    @Test
    void testSetAndGetItemId() {
        item.setItemId(2);
        assertEquals(2, item.getItemId());
    }

    @Test
    void testSetAndGetName() {
        item.setName("New Book");
        assertEquals("New Book", item.getName());
    }

    @Test
    void testSetAndGetPrice() {
        item.setPrice(39.99);
        assertEquals(39.99, item.getPrice(), 0.001);
    }

    @Test
    void testSetAndGetCategory() {
        item.setCategory("Non-Fiction");
        assertEquals("Non-Fiction", item.getCategory());
    }

    @Test
    void testSetAndGetStock() {
        item.setStock(50);
        assertEquals(50, item.getStock());
    }

    @Test
    void testSetNegativePrice() {
        item.setPrice(-10.0);
        assertEquals(-10.0, item.getPrice(), 0.001); // Assuming negative prices are allowed
    }

    @Test
    void testSetNegativeStock() {
        item.setStock(-5);
        assertEquals(-5, item.getStock()); // Assuming negative stock is allowed
    }

    @Test
    void testSetNullName() {
        item.setName(null);
        assertNull(item.getName());
    }

    @Test
    void testSetNullCategory() {
        item.setCategory(null);
        assertNull(item.getCategory());
    }
}