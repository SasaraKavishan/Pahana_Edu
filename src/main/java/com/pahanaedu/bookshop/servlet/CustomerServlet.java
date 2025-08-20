package com.pahanaedu.bookshop.servlet;

import com.pahanaedu.bookshop.model.Customer;
import com.pahanaedu.bookshop.util.DatabaseUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/customer")
public class CustomerServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("add".equals(action)) {
            addCustomer(request, response);
        } else if ("edit".equals(action)) {
            editCustomer(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("view".equals(action)) {
            viewCustomers(request, response);
        } else if ("edit".equals(action)) {
            loadCustomerForEdit(request, response);
        } else if ("delete".equals(action)) {
            deleteCustomer(request, response);
        }
    }

    private void addCustomer(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Customer customer = new Customer(
                request.getParameter("accountNumber"),
                request.getParameter("name"),
                request.getParameter("address"),
                request.getParameter("telephone")
        );

        try (Connection conn = DatabaseUtil.getConnection()) {
            String sql = "INSERT INTO customers (account_number, name, address, telephone) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, customer.getAccountNumber());
            stmt.setString(2, customer.getName());
            stmt.setString(3, customer.getAddress());
            stmt.setString(4, customer.getTelephone());
            stmt.executeUpdate();
            response.sendRedirect("customer?action=view&success=Customer added successfully");
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Failed to add customer: " + e.getMessage());
            request.getRequestDispatcher("addCustomer.jsp").forward(request, response);
        }
    }

    private void editCustomer(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Customer customer = new Customer(
                request.getParameter("accountNumber"),
                request.getParameter("name"),
                request.getParameter("address"),
                request.getParameter("telephone")
        );

        try (Connection conn = DatabaseUtil.getConnection()) {
            String sql = "UPDATE customers SET name = ?, address = ?, telephone = ? WHERE account_number = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getAddress());
            stmt.setString(3, customer.getTelephone());
            stmt.setString(4, customer.getAccountNumber());
            stmt.executeUpdate();
            response.sendRedirect("customer?action=view&success=Customer updated successfully");
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Failed to update customer: " + e.getMessage());
            request.getRequestDispatcher("editCustomer.jsp").forward(request, response);
        }
    }

    private void viewCustomers(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Customer> customers = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection()) {
            String sql = "SELECT * FROM customers";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Customer customer = new Customer(
                        rs.getString("account_number"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("telephone")
                );
                customers.add(customer);
            }
            System.out.println("Retrieved " + customers.size() + " customers");
            request.setAttribute("customers", customers);
            String success = request.getParameter("success");
            if (success != null) {
                request.setAttribute("success", success);
            }
            request.getRequestDispatcher("viewCustomers.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Failed to retrieve customers: " + e.getMessage());
            request.setAttribute("customers", customers);
            request.getRequestDispatcher("viewCustomers.jsp").forward(request, response);
        }
    }

    private void loadCustomerForEdit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accountNumber = request.getParameter("accountNumber");
        try (Connection conn = DatabaseUtil.getConnection()) {
            String sql = "SELECT * FROM customers WHERE account_number = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, accountNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Customer customer = new Customer(
                        rs.getString("account_number"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("telephone")
                );
                request.setAttribute("customer", customer);
                request.getRequestDispatcher("editCustomer.jsp").forward(request, response);
            } else {
                request.setAttribute("error", "Customer not found");
                request.getRequestDispatcher("viewCustomers.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Failed to load customer: " + e.getMessage());
            request.getRequestDispatcher("viewCustomers.jsp").forward(request, response);
        }
    }

    private void deleteCustomer(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accountNumber = request.getParameter("accountNumber");
        try (Connection conn = DatabaseUtil.getConnection()) {
            String sql = "DELETE FROM customers WHERE account_number = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, accountNumber);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                response.sendRedirect("customer?action=view&success=Customer deleted successfully");
            } else {
                request.setAttribute("error", "Customer not found");
                request.getRequestDispatcher("viewCustomers.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Failed to delete customer: " + e.getMessage());
            request.getRequestDispatcher("viewCustomers.jsp").forward(request, response);
        }
    }
}