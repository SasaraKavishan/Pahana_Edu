package com.pahanaedu.bookshop.servlet;

import com.pahanaedu.bookshop.model.Customer;
import com.pahanaedu.bookshop.model.Item;
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
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class CartServletTest {

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
    private CartServlet cartServlet;

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
    void testDoGetAddActionSuccess() throws ServletException, IOException, SQLException {

        Map<Integer, Integer> cart = new HashMap<>();
        when(request.getParameter("action")).thenReturn("add");
        when(request.getParameter("itemId")).thenReturn("1");
        when(request.getParameter("qty")).thenReturn("2");
        when(request.getParameter("accountNumber")).thenReturn("C123");
        when(session.getAttribute("cart")).thenReturn(cart);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("stock")).thenReturn(10);


        cartServlet.doGet(request, response);


        verify(preparedStatement).setInt(1, 1);
        verify(session).setAttribute(eq("cart"), any(Map.class));
        verify(response).sendRedirect("billCart.jsp?accountNumber=C123");
        verify(requestDispatcher, never()).forward(any(), any());
    }

    @Test
    void testDoGetAddActionInvalidQuantity() throws ServletException, IOException, SQLException {

        when(request.getParameter("action")).thenReturn("add");
        when(request.getParameter("itemId")).thenReturn("1");
        when(request.getParameter("qty")).thenReturn("0");
        when(request.getParameter("accountNumber")).thenReturn("C123");
        when(session.getAttribute("cart")).thenReturn(new HashMap<>());


        cartServlet.doGet(request, response);


        verify(response).sendRedirect("billCart.jsp?accountNumber=C123&error=Invalid quantity");
        verify(preparedStatement, never()).executeQuery();
    }

    @Test
    void testDoGetAddActionNotEnoughStock() throws ServletException, IOException, SQLException {

        Map<Integer, Integer> cart = new HashMap<>();
        cart.put(1, 8);
        when(request.getParameter("action")).thenReturn("add");
        when(request.getParameter("itemId")).thenReturn("1");
        when(request.getParameter("qty")).thenReturn("3");
        when(request.getParameter("accountNumber")).thenReturn("C123");
        when(session.getAttribute("cart")).thenReturn(cart);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("stock")).thenReturn(10);


        cartServlet.doGet(request, response);


        verify(response).sendRedirect("billCart.jsp?accountNumber=C123&error=Not enough stock for item 1");
    }

    @Test
    void testDoGetAddActionItemNotFound() throws ServletException, IOException, SQLException {

        when(request.getParameter("action")).thenReturn("add");
        when(request.getParameter("itemId")).thenReturn("1");
        when(request.getParameter("qty")).thenReturn("2");
        when(request.getParameter("accountNumber")).thenReturn("C123");
        when(session.getAttribute("cart")).thenReturn(new HashMap<>());
        when(resultSet.next()).thenReturn(false);


        cartServlet.doGet(request, response);


        verify(response).sendRedirect("billCart.jsp?accountNumber=C123&error=Item not found");
    }

    @Test
    void testDoGetAddActionDatabaseError() throws ServletException, IOException, SQLException {

        when(request.getParameter("action")).thenReturn("add");
        when(request.getParameter("itemId")).thenReturn("1");
        when(request.getParameter("qty")).thenReturn("2");
        when(request.getParameter("accountNumber")).thenReturn("C123");
        when(session.getAttribute("cart")).thenReturn(new HashMap<>());
        when(connection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));


        cartServlet.doGet(request, response);


        verify(response).sendRedirect("billCart.jsp?accountNumber=C123&error=Database error");
    }

    @Test
    void testDoGetClearAction() throws ServletException, IOException {

        when(request.getParameter("action")).thenReturn("clear");
        when(request.getParameter("accountNumber")).thenReturn("C123");


        cartServlet.doGet(request, response);


        verify(session).removeAttribute("cart");
        verify(response).sendRedirect("billCart.jsp?accountNumber=C123");
        verify(requestDispatcher, never()).forward(any(), any());
    }

    @Test
    void testDoGetGenerateActionSuccess() throws ServletException, IOException, SQLException {

        Map<Integer, Integer> cart = new HashMap<>();
        cart.put(1, 2);
        when(request.getParameter("action")).thenReturn("generate");
        when(request.getParameter("accountNumber")).thenReturn("C123");
        when(session.getAttribute("cart")).thenReturn(cart);


        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(true);
        when(resultSet.getString("account_number")).thenReturn("C123");
        when(resultSet.getString("name")).thenReturn("John Doe");
        when(resultSet.getString("address")).thenReturn("123 Main St");
        when(resultSet.getString("telephone")).thenReturn("555-0123");


        when(resultSet.getInt("item_id")).thenReturn(1);
        when(resultSet.getString("name")).thenReturn("Book");
        when(resultSet.getDouble("price")).thenReturn(10.0);
        when(resultSet.getString("category")).thenReturn("Fiction");
        when(resultSet.getInt("stock")).thenReturn(5);


        when(preparedStatement.getGeneratedKeys()).thenReturn(resultSet);
        when(resultSet.getInt(1)).thenReturn(100);


        cartServlet.doGet(request, response);


        verify(connection).setAutoCommit(false);
        verify(connection).commit();
        verify(session).removeAttribute("cart");
        verify(request).setAttribute(eq("customer"), any(Customer.class));
        verify(request).setAttribute(eq("cartItems"), anyList());
        verify(request).setAttribute("total", 20.0);
        verify(request).setAttribute("billId", 100);
        verify(request).setAttribute("success", "Bill saved successfully");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    void testDoGetGenerateActionNoAccountNumber() throws ServletException, IOException, SQLException {

        when(request.getParameter("action")).thenReturn("generate");
        when(request.getParameter("accountNumber")).thenReturn(null);
        when(session.getAttribute("cart")).thenReturn(new HashMap<>());


        cartServlet.doGet(request, response);


        verify(request).setAttribute("error", "Account number not provided");
        verify(requestDispatcher).forward(request, response);
        verify(connection).rollback();
    }

    @Test
    void testDoGetGenerateActionCustomerNotFound() throws ServletException, IOException, SQLException {

        when(request.getParameter("action")).thenReturn("generate");
        when(request.getParameter("accountNumber")).thenReturn("C123");
        when(session.getAttribute("cart")).thenReturn(new HashMap<>());
        when(resultSet.next()).thenReturn(false);


        cartServlet.doGet(request, response);


        verify(request).setAttribute("error", "Customer not found");
        verify(requestDispatcher).forward(request, response);
        verify(connection).rollback();
    }

    @Test
    void testDoGetGenerateActionNotEnoughStock() throws ServletException, IOException, SQLException {

        Map<Integer, Integer> cart = new HashMap<>();
        cart.put(1, 10);
        when(request.getParameter("action")).thenReturn("generate");
        when(request.getParameter("accountNumber")).thenReturn("C123");
        when(session.getAttribute("cart")).thenReturn(cart);


        when(resultSet.next()).thenReturn(true).thenReturn(true); // Customer, Item
        when(resultSet.getString("account_number")).thenReturn("C123");
        when(resultSet.getString("name")).thenReturn("John Doe");
        when(resultSet.getString("address")).thenReturn("123 Main St");
        when(resultSet.getString("telephone")).thenReturn("555-0123");


        when(resultSet.getInt("item_id")).thenReturn(1);
        when(resultSet.getString("name")).thenReturn("Book");
        when(resultSet.getDouble("price")).thenReturn(10.0);
        when(resultSet.getString("category")).thenReturn("Fiction");
        when(resultSet.getInt("stock")).thenReturn(5);


        cartServlet.doGet(request, response);


        verify(request).setAttribute("error", "Not enough stock for item Book");
        verify(requestDispatcher).forward(request, response);
        verify(connection).rollback();
    }

    @Test
    void testDoGetGenerateActionDatabaseError() throws ServletException, IOException, SQLException {

        Map<Integer, Integer> cart = new HashMap<>();
        cart.put(1, 2);
        when(request.getParameter("action")).thenReturn("generate");
        when(request.getParameter("accountNumber")).thenReturn("C123");
        when(session.getAttribute("cart")).thenReturn(cart);
        when(resultSet.next()).thenReturn(true); // Customer query
        when(connection.prepareStatement(anyString())).thenThrow(new SQLException("Database error")).thenReturn(preparedStatement);


        cartServlet.doGet(request, response);


        verify(request).setAttribute("error", "Failed to generate bill: Database error");
        verify(requestDispatcher).forward(request, response);
        verify(connection, atLeastOnce()).rollback();
    }
}