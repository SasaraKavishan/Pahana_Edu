package com.pahanaedu.bookshop.servlet;

import com.pahanaedu.bookshop.model.Customer;
import com.pahanaedu.bookshop.model.Item;
import com.pahanaedu.bookshop.util.DatabaseUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/cart")
public class CartServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        HttpSession session = request.getSession();
        Map<Integer, Integer> cart = (Map<Integer, Integer>) session.getAttribute("cart");
        if (cart == null) {
            cart = new HashMap<>();
            session.setAttribute("cart", cart);
        }

        if ("add".equals(action)) {
            int itemId = Integer.parseInt(request.getParameter("itemId"));
            int qty = Integer.parseInt(request.getParameter("qty") != null ? request.getParameter("qty") : "1");
            if (qty <= 0) {
                response.sendRedirect("billCart.jsp?accountNumber=" + request.getParameter("accountNumber") + "&error=Invalid quantity");
                return;
            }

            try (Connection conn = DatabaseUtil.getConnection()) {
                String sql = "SELECT stock FROM items WHERE item_id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, itemId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    int stock = rs.getInt("stock");
                    int currentCartQty = cart.getOrDefault(itemId, 0);
                    if (currentCartQty + qty > stock) {
                        response.sendRedirect("billCart.jsp?accountNumber=" + request.getParameter("accountNumber") + "&error=Not enough stock for item " + itemId);
                        return;
                    }
                    cart.put(itemId, currentCartQty + qty);
                    session.setAttribute("cart", cart);
                    response.sendRedirect("billCart.jsp?accountNumber=" + request.getParameter("accountNumber"));
                } else {
                    response.sendRedirect("billCart.jsp?accountNumber=" + request.getParameter("accountNumber") + "&error=Item not found");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                response.sendRedirect("billCart.jsp?accountNumber=" + request.getParameter("accountNumber") + "&error=Database error");
            }
        } else if ("clear".equals(action)) {
            session.removeAttribute("cart");
            response.sendRedirect("billCart.jsp?accountNumber=" + request.getParameter("accountNumber"));
        } else if ("generate".equals(action)) {

            List<Map.Entry<Item, Integer>> cartItems = new ArrayList<>();
            double total = 0.0;
            int billId = 0;
            try (Connection conn = DatabaseUtil.getConnection()) {
                conn.setAutoCommit(false);

                String accountNumber = request.getParameter("accountNumber");
                if (accountNumber == null) {
                    request.setAttribute("error", "Account number not provided");
                    request.getRequestDispatcher("billCart.jsp").forward(request, response);
                    conn.rollback();
                    return;
                }
                String sql = "SELECT * FROM customers WHERE account_number = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, accountNumber);
                ResultSet rs = stmt.executeQuery();
                Customer customer;
                if (rs.next()) {
                    customer = new Customer(
                            rs.getString("account_number"),
                            rs.getString("name"),
                            rs.getString("address"),
                            rs.getString("telephone")
                    );
                    request.setAttribute("customer", customer);
                } else {
                    request.setAttribute("error", "Customer not found");
                    request.getRequestDispatcher("billCart.jsp").forward(request, response);
                    conn.rollback();
                    return;
                }


                for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {
                    int itemId = entry.getKey();
                    int quantity = entry.getValue();
                    sql = "SELECT * FROM items WHERE item_id = ?";
                    stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, itemId);
                    rs = stmt.executeQuery();
                    if (rs.next()) {
                        Item item = new Item(
                                rs.getInt("item_id"),
                                rs.getString("name"),
                                rs.getDouble("price"),
                                rs.getString("category"),
                                rs.getInt("stock")
                        );
                        if (quantity > item.getStock()) {
                            request.setAttribute("error", "Not enough stock for item " + item.getName());
                            request.getRequestDispatcher("billCart.jsp").forward(request, response);
                            conn.rollback();
                            return;
                        }
                        cartItems.add(Map.entry(item, quantity));
                        total += item.getPrice() * quantity;
                    }
                }

                // Save bill to database
                sql = "INSERT INTO bills (account_number, total) VALUES (?, ?)";
                stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
                stmt.setString(1, accountNumber);
                stmt.setDouble(2, total);
                stmt.executeUpdate();
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    billId = rs.getInt(1);
                }

                // Save bill items
                sql = "INSERT INTO bill_items (bill_id, item_id, quantity, subtotal) VALUES (?, ?, ?, ?)";
                stmt = conn.prepareStatement(sql);
                for (Map.Entry<Item, Integer> entry : cartItems) {
                    stmt.setInt(1, billId);
                    stmt.setInt(2, entry.getKey().getItemId());
                    stmt.setInt(3, entry.getValue());
                    stmt.setDouble(4, entry.getKey().getPrice() * entry.getValue());
                    stmt.executeUpdate();
                }

                // Update stock
                sql = "UPDATE items SET stock = stock - ? WHERE item_id = ?";
                stmt = conn.prepareStatement(sql);
                for (Map.Entry<Item, Integer> entry : cartItems) {
                    stmt.setInt(1, entry.getValue());
                    stmt.setInt(2, entry.getKey().getItemId());
                    stmt.executeUpdate();
                }

                conn.commit(); // Commit transaction
                session.removeAttribute("cart"); // Clear cart after saving bill
            } catch (SQLException e) {
                e.printStackTrace();
                try (Connection conn = DatabaseUtil.getConnection()) {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
                request.setAttribute("error", "Failed to generate bill: " + e.getMessage());
                request.getRequestDispatcher("billCart.jsp").forward(request, response);
                return;
            }
            request.setAttribute("cartItems", cartItems);
            request.setAttribute("total", total);
            request.setAttribute("billId", billId);
            request.setAttribute("success", "Bill saved successfully");
            request.getRequestDispatcher("generateBill.jsp").forward(request, response);
        }
    }
}