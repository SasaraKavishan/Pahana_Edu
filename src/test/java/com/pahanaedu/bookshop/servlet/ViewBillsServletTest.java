package com.pahanaedu.bookshop.servlet;

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
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ViewBillsServletTest {

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
    private ViewBillsServlet viewBillsServlet;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        when(DatabaseUtil.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(request.getRequestDispatcher("viewBills.jsp")).thenReturn(requestDispatcher);
    }

    @Test
    void testDoGetViewBillsSuccess() throws ServletException, IOException, SQLException {

        when(request.getParameter("action")).thenReturn("view");
        when(request.getParameter("success")).thenReturn("Operation successful");


        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getInt("bill_id")).thenReturn(1);
        when(resultSet.getString("account_number")).thenReturn("C123");
        when(resultSet.getString("customer_name")).thenReturn("John Doe");
        when(resultSet.getDouble("total")).thenReturn(50.0);
        when(resultSet.getTimestamp("created_at")).thenReturn(new Timestamp(System.currentTimeMillis()));


        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getString("name")).thenReturn("Book");


        viewBillsServlet.doGet(request, response);


        verify(preparedStatement, times(2)).executeQuery();
        verify(request).setAttribute(eq("bills"), any(List.class));
        verify(request).setAttribute("success", "Operation successful");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    void testDoGetViewBillsDatabaseError() throws ServletException, IOException, SQLException {

        when(request.getParameter("action")).thenReturn("view");
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Database error"));


        viewBillsServlet.doGet(request, response);


        verify(request).setAttribute("error", "Failed to retrieve bills: Database error");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    void testDoGetDeleteBillSuccess() throws ServletException, IOException, SQLException {

        when(request.getParameter("action")).thenReturn("delete");
        when(request.getParameter("billId")).thenReturn("1");


        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getInt("item_id")).thenReturn(101);
        when(resultSet.getInt("quantity")).thenReturn(2);


        when(preparedStatement.executeUpdate()).thenReturn(1);


        viewBillsServlet.doGet(request, response);


        verify(connection).setAutoCommit(false);
        verify(preparedStatement, times(3)).executeUpdate();
        verify(connection).commit();
        verify(response).sendRedirect("bills?action=view&success=Bill deleted successfully");
    }

    @Test
    void testDoGetDeleteBillNotFound() throws ServletException, IOException, SQLException {

        when(request.getParameter("action")).thenReturn("delete");
        when(request.getParameter("billId")).thenReturn("1");
        when(resultSet.next()).thenReturn(false); // No bill items
        when(preparedStatement.executeUpdate()).thenReturn(0);


        viewBillsServlet.doGet(request, response);


        verify(connection).setAutoCommit(false);
        verify(connection).rollback();
        verify(request).setAttribute("error", "Bill not found");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    void testDoGetDeleteBillDatabaseError() throws ServletException, IOException, SQLException {

        when(request.getParameter("action")).thenReturn("delete");
        when(request.getParameter("billId")).thenReturn("1");
        when(resultSet.next()).thenReturn(true).thenReturn(false); // One bill item
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Database error"));


        viewBillsServlet.doGet(request, response);


        verify(connection).setAutoCommit(false);
        verify(connection).rollback();
        verify(request).setAttribute("error", "Failed to delete bill: Database error");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    void testDoGetDeleteBillConnectionError() throws ServletException, IOException, SQLException {

        when(request.getParameter("action")).thenReturn("delete");
        when(request.getParameter("billId")).thenReturn("1");
        when(DatabaseUtil.getConnection()).thenThrow(new SQLException("Connection error"));


        viewBillsServlet.doGet(request, response);


        verify(request).setAttribute("error", "Database connection error: Connection error");
        verify(requestDispatcher).forward(request, response);
    }
}