package com.pahanaedu.bookshop.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DatabaseUtilTest {

    private MockedStatic<DriverManager> driverManagerMock;
    private MockedStatic<Class> classMock;

    @BeforeEach
    void setUp() {
        driverManagerMock = mockStatic(DriverManager.class);
        classMock = mockStatic(Class.class);
    }

    @Test
    void testStaticInitializerLoadsDriverSuccessfully() {

        classMock.when(() -> Class.forName("com.mysql.cj.jdbc.Driver")).thenAnswer(invocation -> null);


        assertDoesNotThrow(() -> {
            Class.forName("com.pahanaedu.bookshop.util.DatabaseUtil");
        });
        classMock.verify(() -> Class.forName("com.mysql.cj.jdbc.Driver"), times(1));
    }

    @Test
    void testStaticInitializerDriverNotFound() {

        classMock.when(() -> Class.forName("com.mysql.cj.jdbc.Driver"))
                .thenThrow(new ClassNotFoundException("Driver not found"));


        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            Class.forName("com.pahanaedu.bookshop.util.DatabaseUtil");
        });
        assertEquals("Failed to load MySQL JDBC driver", exception.getMessage());
        classMock.verify(() -> Class.forName("com.mysql.cj.jdbc.Driver"), times(1));
    }

    @Test
    void testGetConnectionSuccess() throws SQLException {

        Connection mockConnection = mock(Connection.class);
        driverManagerMock.when(() -> DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/bookshop?useSSL=false&serverTimezone=UTC", "root", ""))
                .thenReturn(mockConnection);


        Connection connection = DatabaseUtil.getConnection();


        assertNotNull(connection);
        assertEquals(mockConnection, connection);
        driverManagerMock.verify(() -> DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/bookshop?useSSL=false&serverTimezone=UTC", "root", ""), times(1));
    }

    @Test
    void testGetConnectionSQLException() throws SQLException {

        driverManagerMock.when(() -> DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/bookshop?useSSL=false&serverTimezone=UTC", "root", ""))
                .thenThrow(new SQLException("Connection failed"));


        SQLException exception = assertThrows(SQLException.class, DatabaseUtil::getConnection);
        assertEquals("Connection failed", exception.getMessage());
        driverManagerMock.verify(() -> DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/bookshop?useSSL=false&serverTimezone=UTC", "root", ""), times(1));
    }

    @Test
    void testGetConnectionMultipleCalls() throws SQLException {

        Connection mockConnection1 = mock(Connection.class);
        Connection mockConnection2 = mock(Connection.class);
        driverManagerMock.when(() -> DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/bookshop?useSSL=false&serverTimezone=UTC", "root", ""))
                .thenReturn(mockConnection1)
                .thenReturn(mockConnection2);


        Connection connection1 = DatabaseUtil.getConnection();
        Connection connection2 = DatabaseUtil.getConnection();


        assertNotNull(connection1);
        assertNotNull(connection2);
        assertEquals(mockConnection1, connection1);
        assertEquals(mockConnection2, connection2);
        driverManagerMock.verify(() -> DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/bookshop?useSSL=false&serverTimezone=UTC", "root", ""), times(2));
    }
}