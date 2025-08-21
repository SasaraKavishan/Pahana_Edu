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
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class CustomerServletTest {

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
    private CustomerServlet customerServlet;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        when(DatabaseUtil.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
    }

    @Test
    void testDoPostAddCustomerSuccess() throws ServletException, IOException, SQLException {

        when(request.getParameter("action")).thenReturn("add");
        when(request.getParameter("accountNumber")).thenReturn("C123");
        when(request.getParameter("name")).thenReturn("John Doe");
        when(request.getParameter("address")).thenReturn("123 Main St");
        when(request.getParameter("telephone")).thenReturn("555-0123");


        customerServlet.doPost(request, response);


        verify(preparedStatement).setString(1, "C123");
        verify(preparedStatement).setString(2, "John Doe");
        verify(preparedStatement).setString(3, "123 Main St");
        verify(preparedStatement).setString(4, "555-0123");
        verify(preparedStatement).executeUpdate();
        verify(response).sendRedirect("customer?action=view&success=Customer added successfully");
    }

    @Test
    void testDoPostAddCustomerDatabaseError() throws ServletException, IOException, SQLException {

        when(request.getParameter("action")).thenReturn("add");
        when(request.getParameter("accountNumber")).thenReturn("C123");
        when(request.getParameter("name")).thenReturn("John Doe");
        when(request.getParameter("address")).thenReturn("123 Main St");
        when(request.getParameter("telephone")).thenReturn("555-0123");
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Database error"));


        customerServlet.doPost(request, response);


        verify(request).setAttribute("error", "Failed to add customer: ");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    void testDoPostEditCustomerSuccess() throws ServletException, IOException, SQLException {

        when(request.getParameter("action")).thenReturn("edit");
        when(request.getParameter("accountNumber")).thenReturn("C123");
        when(request.getParameter("name")).thenReturn("Jane Doe");
        when(request.getParameter("address")).thenReturn("456 Oak Ave");
        when(request.getParameter("telephone")).thenReturn("555-9876");


        customerServlet.doPost(request, response);


        verify(preparedStatement).setString(1, "Jane Doe");
        verify(preparedStatement).setString(2, "456 Oak Ave");
        verify(preparedStatement).setString(3, "555-9876");
        verify(preparedStatement).setString(4, "C123");
        verify(preparedStatement).executeUpdate();
        verify(response).sendRedirect("customer?action=view&success=Customer updated successfully");
    }

    @Test
    void testDoPostEditCustomerDatabaseError() throws ServletException, IOException, SQLException {

        when(request.getParameter("action")).thenReturn("edit");
        when(request.getParameter("accountNumber")).thenReturn("C123");
        when(request.getParameter("name")).thenReturn("Jane Doe");
        when(request.getParameter("address")).thenReturn("456 Oak Ave");
        when(request.getParameter("telephone")).thenReturn("555-9876");
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Database error"));


        customerServlet.doPost(request, response);


        verify(request).setAttribute("error", "Failed to update customer: Database error");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    void testDoGetViewCustomersSuccess() throws ServletException, IOException, SQLException {

        when(request.getParameter("action")).thenReturn("view");
        when(request.getParameter("success")).thenReturn("Operation successful");
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getString("account_number")).thenReturn("C123");
        when(resultSet.getString("name")).thenReturn("John Doe");
        when(resultSet.getString("address")).thenReturn("123 Main St");
        when(resultSet.getString("telephone")).thenReturn("555-0123");


        customerServlet.doGet(request, response);


        verify(preparedStatement).executeQuery();
        verify(request).setAttribute(eq("customers"), any(List.class));
        verify(request).setAttribute("success", "Operation successful");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    void testDoGetViewCustomersDatabaseError() throws ServletException, IOException, SQLException {

        when(request.getParameter("action")).thenReturn("view");
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Database error"));


        customerServlet.doGet(request, response);


        verify(request).setAttribute("error", "Failed to retrieve customers: Database error");
        verify(request).setAttribute(eq("customers"), any(List.class));
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    void testDoGetLoadCustomerForEditSuccess() throws ServletException, IOException, SQLException {

        when(request.getParameter("action")).thenReturn("edit");
        when(request.getParameter("accountNumber")).thenReturn("C123");
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("account_number")).thenReturn("C123");
        when(resultSet.getString("name")).thenReturn("John Doe");
        when(resultSet.getString("address")).thenReturn("123 Main St");
        when(resultSet.getString("telephone")).thenReturn("555-0123");


        customerServlet.doGet(request, response);


        verify(preparedStatement).setString(1, "C123");
        verify(request).setAttribute(eq("customer"), any(Customer.class));
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    void testDoGetLoadCustomerForEditNotFound() throws ServletException, IOException, SQLException {

        when(request.getParameter("action")).thenReturn("edit");
        when(request.getParameter("accountNumber")).thenReturn("C123");
        when(resultSet.next()).thenReturn(false);


        customerServlet.doGet(request, response);


        verify(request).setAttribute("error", "Customer not found");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    void testDoGetLoadCustomerForEditDatabaseError() throws ServletException, IOException, SQLException {

        when(request.getParameter("action")).thenReturn("edit");
        when(request.getParameter("accountNumber")).thenReturn("C123");
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Database error"));


        customerServlet.doGet(request, response);


        verify(request).setAttribute("error", "Failed to load customer: Database error");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    void testDoGetDeleteCustomerSuccess() throws ServletException, IOException, SQLException {

        when(request.getParameter("action")).thenReturn("delete");
        when(request.getParameter("accountNumber")).thenReturn("C123");
        when(preparedStatement.executeUpdate()).thenReturn(1);


        customerServlet.doGet(request, response);


        verify(preparedStatement).setString(1, "C123");
        verify(response).sendRedirect("customer?action=view&success=Customer deleted successfully");
    }

    @Test
    void testDoGetDeleteCustomerNotFound() throws ServletException, IOException, SQLException {

        when(request.getParameter("action")).thenReturn("delete");
        when(request.getParameter("accountNumber")).thenReturn("C123");
        when(preparedStatement.executeUpdate()).thenReturn(0);


        customerServlet.doGet(request, response);


        verify(request).setAttribute("error", "Customer not found");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    void testDoGetDeleteCustomerDatabaseError() throws ServletException, IOException, SQLException {

        when(request.getParameter("action")).thenReturn("delete");
        when(request.getParameter("accountNumber")).thenReturn("C123");
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Database error"));


        customerServlet.doGet(request, response);


        verify(request).setAttribute("error", "Failed to delete customer: Database error");
        verify(requestDispatcher).forward(request, response);
    }
}