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

@WebServlet("/billing")
public class BillingServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
                        rs.getString("telephone"),
                        rs.getInt("units_consumed")
                );
                double unitPrice = 10.0; // Example price per unit (adjust as needed)
                double billAmount = customer.getUnitsConsumed() * unitPrice;
                request.setAttribute("customer", customer);
                request.setAttribute("billAmount", billAmount);
                request.getRequestDispatcher("generateBill.jsp").forward(request, response);
            } else {
                response.sendRedirect("viewCustomers.jsp?error=Customer not found");
            }
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
    }
}