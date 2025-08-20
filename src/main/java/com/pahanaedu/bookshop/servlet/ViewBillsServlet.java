package com.pahanaedu.bookshop.servlet;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/bills")
public class ViewBillsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("view".equals(action)) {
            viewBills(request, response);
        } else if ("delete".equals(action)) {
            deleteBill(request, response);
        }
    }

    private void viewBills(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Map<String, Object>> bills = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection()) {
            String sql = "SELECT b.bill_id, b.account_number, b.total, b.created_at, c.name AS customer_name " +
                    "FROM bills b JOIN customers c ON b.account_number = c.account_number";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> bill = new HashMap<>();
                bill.put("billId", rs.getInt("bill_id"));
                bill.put("accountNumber", rs.getString("account_number"));
                bill.put("customerName", rs.getString("customer_name"));
                bill.put("total", rs.getDouble("total"));
                bill.put("createdAt", rs.getTimestamp("created_at"));
                bills.add(bill);
            }
            request.setAttribute("bills", bills);
            String success = request.getParameter("success");
            if (success != null) {
                request.setAttribute("success", success);
            }
            request.getRequestDispatcher("viewBills.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Failed to retrieve bills: " + e.getMessage());
            request.getRequestDispatcher("viewBills.jsp").forward(request, response);
        }
    }

    private void deleteBill(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int billId = Integer.parseInt(request.getParameter("billId"));
        try (Connection conn = DatabaseUtil.getConnection()) {
            conn.setAutoCommit(false); // Start transaction
            try {
                // Get bill items to restore stock
                String sql = "SELECT item_id, quantity FROM bill_items WHERE bill_id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, billId);
                ResultSet rs = stmt.executeQuery();
                List<Map.Entry<Integer, Integer>> billItems = new ArrayList<>();
                while (rs.next()) {
                    billItems.add(Map.entry(rs.getInt("item_id"), rs.getInt("quantity")));
                }

                // Delete bill items
                sql = "DELETE FROM bill_items WHERE bill_id = ?";
                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, billId);
                stmt.executeUpdate();

                // Delete bill
                sql = "DELETE FROM bills WHERE bill_id = ?";
                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, billId);
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected == 0) {
                    conn.rollback();
                    request.setAttribute("error", "Bill not found");
                    request.getRequestDispatcher("viewBills.jsp").forward(request, response);
                    return;
                }

                // Restore stock
                sql = "UPDATE items SET stock = stock + ? WHERE item_id = ?";
                stmt = conn.prepareStatement(sql);
                for (Map.Entry<Integer, Integer> entry : billItems) {
                    stmt.setInt(1, entry.getValue());
                    stmt.setInt(2, entry.getKey());
                    stmt.executeUpdate();
                }

                conn.commit(); // Commit transaction
                response.sendRedirect("bills?action=view&success=Bill deleted successfully");
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                request.setAttribute("error", "Failed to delete bill: " + e.getMessage());
                request.getRequestDispatcher("viewBills.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Database connection error: " + e.getMessage());
            request.getRequestDispatcher("viewBills.jsp").forward(request, response);
        }
    }
}