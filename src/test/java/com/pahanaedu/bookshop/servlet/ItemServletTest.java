package com.pahanaedu.bookshop.servlet;

import com.pahanaedu.bookshop.model.Item;
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

class ItemServletTest {

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
    private ItemServlet itemServlet;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        when(DatabaseUtil.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
    }

    @Test
    void testDoPostAddItemSuccess() throws ServletException, IOException, SQLException {

        when(request.getParameter("action")).thenReturn("add");
        when(request.getParameter("name")).thenReturn("Book");
        when(request.getParameter("price")).thenReturn("29.99");
        when(request.getParameter("category")).thenReturn("Fiction");
        when(request.getParameter("stock")).thenReturn("100");


        itemServlet.doPost(request, response);


        verify(preparedStatement).setString(1, "Book");
        verify(preparedStatement).setDouble(2, 29.99);
        verify(preparedStatement).setString(3, "Fiction");
        verify(preparedStatement).setInt(4, 100);
        verify(preparedStatement).executeUpdate();
        verify(response).sendRedirect("item?action=view&success=Item added successfully");
    }

    @Test
    void testDoPostAddItemDatabaseError() throws ServletException, IOException, SQLException {

        when(request.getParameter("action")).thenReturn("add");
        when(request.getParameter("name")).thenReturn("Book");
        when(request.getParameter("price")).thenReturn("29.99");
        when(request.getParameter("category")).thenReturn("Fiction");
        when(request.getParameter("stock")).thenReturn("100");
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Database error"));


        itemServlet.doPost(request, response);


        verify(request).setAttribute("error", "Failed to add item: Database error");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    void testDoPostEditItemSuccess() throws ServletException, IOException, SQLException {

        when(request.getParameter("action")).thenReturn("edit");
        when(request.getParameter("itemId")).thenReturn("1");
        when(request.getParameter("name")).thenReturn("Updated Book");
        when(request.getParameter("price")).thenReturn("39.99");
        when(request.getParameter("category")).thenReturn("Non-Fiction");
        when(request.getParameter("stock")).thenReturn("50");


        itemServlet.doPost(request, response);


        verify(preparedStatement).setString(1, "Updated Book");
        verify(preparedStatement).setDouble(2, 39.99);
        verify(preparedStatement).setString(3, "Non-Fiction");
        verify(preparedStatement).setInt(4, 50);
        verify(preparedStatement).setInt(5, 1);
        verify(preparedStatement).executeUpdate();
        verify(response).sendRedirect("item?action=view&success=Item updated successfully");
    }

    @Test
    void testDoPostEditItemDatabaseError() throws ServletException, IOException, SQLException {

        when(request.getParameter("action")).thenReturn("edit");
        when(request.getParameter("itemId")).thenReturn("1");
        when(request.getParameter("name")).thenReturn("Updated Book");
        when(request.getParameter("price")).thenReturn("39.99");
        when(request.getParameter("category")).thenReturn("Non-Fiction");
        when(request.getParameter("stock")).thenReturn("50");
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Database error"));


        itemServlet.doPost(request, response);


        verify(request).setAttribute("error", "Failed to update item: Database error");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    void testDoGetViewItemsSuccess() throws ServletException, IOException, SQLException {

        when(request.getParameter("action")).thenReturn("view");
        when(request.getParameter("success")).thenReturn("Operation successful");
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getInt("item_id")).thenReturn(1);
        when(resultSet.getString("name")).thenReturn("Book");
        when(resultSet.getDouble("price")).thenReturn(29.99);
        when(resultSet.getString("category")).thenReturn("Fiction");
        when(resultSet.getInt("stock")).thenReturn(100);


        itemServlet.doGet(request, response);


        verify(preparedStatement).executeQuery();
        verify(request).setAttribute(eq("items"), any(List.class));
        verify(request).setAttribute("success", "Operation successful");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    void testDoGetViewItemsDatabaseError() throws ServletException, IOException, SQLException {

        when(request.getParameter("action")).thenReturn("view");
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Database error"));


        itemServlet.doGet(request, response);


        verify(request).setAttribute("error", "Failed to retrieve items: Database error");
        verify(request).setAttribute(eq("items"), any(List.class));
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    void testDoGetLoadItemForEditSuccess() throws ServletException, IOException, SQLException {

        when(request.getParameter("action")).thenReturn("edit");
        when(request.getParameter("itemId")).thenReturn("1");
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("item_id")).thenReturn(1);
        when(resultSet.getString("name")).thenReturn("Book");
        when(resultSet.getDouble("price")).thenReturn(29.99);
        when(resultSet.getString("category")).thenReturn("Fiction");
        when(resultSet.getInt("stock")).thenReturn(100);


        itemServlet.doGet(request, response);


        verify(preparedStatement).setInt(1, 1);
        verify(request).setAttribute(eq("item"), any(Item.class));
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    void testDoGetLoadItemForEditNotFound() throws ServletException, IOException, SQLException {

        when(request.getParameter("action")).thenReturn("edit");
        when(request.getParameter("itemId")).thenReturn("1");
        when(resultSet.next()).thenReturn(false);


        itemServlet.doGet(request, response);


        verify(request).setAttribute("error", "Item not found");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    void testDoGetLoadItemForEditDatabaseError() throws ServletException, IOException, SQLException {

        when(request.getParameter("action")).thenReturn("edit");
        when(request.getParameter("itemId")).thenReturn("1");
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Database error"));


        itemServlet.doGet(request, response);


        verify(request).setAttribute("error", "Failed to load item: Database error");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    void testDoGetDeleteItemSuccess() throws ServletException, IOException, SQLException {

        when(request.getParameter("action")).thenReturn("delete");
        when(request.getParameter("itemId")).thenReturn("1");
        when(preparedStatement.executeUpdate()).thenReturn(1);


        itemServlet.doGet(request, response);


        verify(preparedStatement).setInt(1, 1);
        verify(response).sendRedirect("item?action=view&success=Item deleted successfully");
    }

    @Test
    void testDoGetDeleteItemNotFound() throws ServletException, IOException, SQLException {

        when(request.getParameter("action")).thenReturn("delete");
        when(request.getParameter("itemId")).thenReturn("1");
        when(preparedStatement.executeUpdate()).thenReturn(0);


        itemServlet.doGet(request, response);


        verify(request).setAttribute("error", "Item not found");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    void testDoGetDeleteItemDatabaseError() throws ServletException, IOException, SQLException {

        when(request.getParameter("action")).thenReturn("delete");
        when(request.getParameter("itemId")).thenReturn("1");
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Database error"));


        itemServlet.doGet(request, response);


        verify(request).setAttribute("error", "Failed to delete item: Database error");
        verify(requestDispatcher).forward(request, response);
    }
}