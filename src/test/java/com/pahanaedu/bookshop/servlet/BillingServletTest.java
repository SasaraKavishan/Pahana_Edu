package com.pahanaedu.bookshop.servlet;

import com.pahanaedu.bookshop.model.Customer;
import com.pahanaedu.bookshop.util.DatabaseUtil;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class BillingServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private RequestDispatcher requestDispatcher;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private BillingServlet billingServlet;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        when(DatabaseUtil.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
    }

    @Test
    void testDoGetCustomerFound() throws ServletException, IOException, SQLException {

        when(request.getParameter("accountNumber")).thenReturn("C123");
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("account_number")).thenReturn("C123");
        when(resultSet.getString("name")).thenReturn("John Doe");
        when(resultSet.getString("address")).thenReturn("123 Main St");
        when(resultSet.getString("telephone")).thenReturn("555-0123");


        billingServlet.doGet(request, response);


        verify(preparedStatement).setString(1, "C123");
        verify(request).setAttribute(eq("customer"), any(Customer.class));
        verify(request).setAttribute("billAmount", 10.0);
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    void testDoGetCustomerNotFound() throws ServletException, IOException, SQLException {

        when(request.getParameter("accountNumber")).thenReturn("C999");
        when(resultSet.next()).thenReturn(false);


        billingServlet.doGet(request, response);


        verify(preparedStatement).setString(1, "C999");
        verify(response).sendRedirect("viewCustomers.jsp?error=Customer not found");
        verify(requestDispatcher, never()).forward(any(), any());
    }

    @Test
    void testDoGetDatabaseError() throws ServletException, IOException, SQLException {

        when(request.getParameter("accountNumber")).thenReturn("C123");
        when(connection.prepareStatement(anyString())).thenThrow(new SQLException("Database failure"));


        ServletException exception = assertThrows(ServletException.class, () -> {
            billingServlet.doGet(request, response);
        });
        assertEquals("Database error", exception.getMessage());
    }
}