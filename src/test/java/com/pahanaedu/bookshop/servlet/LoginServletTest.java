package com.pahanaedu.bookshop.servlet;

import com.pahanaedu.bookshop.util.DatabaseUtil;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
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

class LoginServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private RequestDispatcher requestDispatcher;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private LoginServlet loginServlet;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        when(request.getSession()).thenReturn(session);
        when(DatabaseUtil.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
    }

    @Test
    void testDoGetForwardsToLoginJsp() throws ServletException, IOException {

        loginServlet.doGet(request, response);


        verify(request).getRequestDispatcher("login.jsp");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    void testDoPostLoginSuccess() throws ServletException, IOException, SQLException {

        when(request.getParameter("username")).thenReturn("testuser");
        when(request.getParameter("password")).thenReturn("password123");
        when(resultSet.next()).thenReturn(true);


        loginServlet.doPost(request, response);


        verify(preparedStatement).setString(1, "testuser");
        verify(preparedStatement).setString(2, "password123");
        verify(session).setAttribute("username", "testuser");
        verify(response).sendRedirect("dashboard.jsp");
        verify(requestDispatcher, never()).forward(any(), any());
    }

    @Test
    void testDoPostLoginFailureInvalidCredentials() throws ServletException, IOException, SQLException {

        when(request.getParameter("username")).thenReturn("testuser");
        when(request.getParameter("password")).thenReturn("wrongpassword");
        when(resultSet.next()).thenReturn(false);


        loginServlet.doPost(request, response);


        verify(preparedStatement).setString(1, "testuser");
        verify(preparedStatement).setString(2, "wrongpassword");
        verify(request).setAttribute("error", "Invalid username or password");
        verify(requestDispatcher).forward(request, response);
        verify(response, never()).sendRedirect(anyString());
    }

    @Test
    void testDoPostDatabaseError() throws ServletException, IOException, SQLException {

        when(request.getParameter("username")).thenReturn("testuser");
        when(request.getParameter("password")).thenReturn("password123");
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Database error"));


        ServletException exception = assertThrows(ServletException.class, () -> {
            loginServlet.doPost(request, response);
        });
        assertEquals("Database error", exception.getMessage());
        verify(requestDispatcher, never()).forward(any(), any());
        verify(response, never()).sendRedirect(anyString());
    }
}