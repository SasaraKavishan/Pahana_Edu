package com.pahanaedu.bookshop.servlet;

import com.pahanaedu.bookshop.model.Item;
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

@WebServlet("/item")
public class ItemServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("add".equals(action)) {
            addItem(request, response);
        } else if ("edit".equals(action)) {
            editItem(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("view".equals(action)) {
            viewItems(request, response);
        } else if ("edit".equals(action)) {
            loadItemForEdit(request, response);
        }
    }

    private void addItem(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Item item = new Item(
                0, // itemId is auto-incremented
                request.getParameter("name"),
                Double.parseDouble(request.getParameter("price"))
        );

        try (Connection conn = DatabaseUtil.getConnection()) {
            String sql = "INSERT INTO items (name, price) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, item.getName());
            stmt.setDouble(2, item.getPrice());
            stmt.executeUpdate();
            response.sendRedirect("item?action=view&success=Item added successfully");
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Failed to add item: " + e.getMessage());
            request.getRequestDispatcher("addItem.jsp").forward(request, response);
        }
    }

    private void editItem(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Item item = new Item(
                Integer.parseInt(request.getParameter("itemId")),
                request.getParameter("name"),
                Double.parseDouble(request.getParameter("price"))
        );

        try (Connection conn = DatabaseUtil.getConnection()) {
            String sql = "UPDATE items SET name = ?, price = ? WHERE item_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, item.getName());
            stmt.setDouble(2, item.getPrice());
            stmt.setInt(3, item.getItemId());
            stmt.executeUpdate();
            response.sendRedirect("item?action=view&success=Item updated successfully");
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Failed to update item: " + e.getMessage());
            request.getRequestDispatcher("editItem.jsp").forward(request, response);
        }
    }

    private void viewItems(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Item> items = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection()) {
            String sql = "SELECT * FROM items";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Item item = new Item(
                        rs.getInt("item_id"),
                        rs.getString("name"),
                        rs.getDouble("price")
                );
                items.add(item);
            }
            System.out.println("Retrieved " + items.size() + " items"); // Debug log
            request.setAttribute("items", items);
            // Transfer success message from query parameter to request attribute
            String success = request.getParameter("success");
            if (success != null) {
                request.setAttribute("success", success);
            }
            request.getRequestDispatcher("manageItems.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Failed to retrieve items: " + e.getMessage());
            request.setAttribute("items", items); // Set empty list
            request.getRequestDispatcher("manageItems.jsp").forward(request, response);
        }
    }

    private void loadItemForEdit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int itemId = Integer.parseInt(request.getParameter("itemId"));
        try (Connection conn = DatabaseUtil.getConnection()) {
            String sql = "SELECT * FROM items WHERE item_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, itemId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Item item = new Item(
                        rs.getInt("item_id"),
                        rs.getString("name"),
                        rs.getDouble("price")
                );
                request.setAttribute("item", item);
                request.getRequestDispatcher("editItem.jsp").forward(request, response);
            } else {
                request.setAttribute("error", "Item not found");
                request.getRequestDispatcher("manageItems.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Failed to load item: " + e.getMessage());
            request.getRequestDispatcher("manageItems.jsp").forward(request, response);
        }
    }

}