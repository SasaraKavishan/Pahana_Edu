<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.pahanaedu.bookshop.model.Item" %>
<%@ page import="com.pahanaedu.bookshop.model.Customer" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.sql.*" %>
<%@ page import="com.pahanaedu.bookshop.util.DatabaseUtil" %>
<html>
<head>
  <title>Bill Cart - Pahana Edu Bookshop</title>
  <link rel="stylesheet" href="css/billCart.css">
</head>
<body>
<h1>Bill Cart for Account: <%= request.getParameter("accountNumber") %></h1>
<nav>
  <a href="dashboard.jsp">Home</a>
  <a href="addCustomer.jsp">Add Customer</a>
  <a href="customer?action=view">View Customers</a>
  <a href="item?action=view">Manage Items</a>
  <a href="bills?action=view">View Bills</a>
  <a href="help">Help</a>
  <a href="login.jsp">Logout</a>
</nav>
<% if (request.getAttribute("error") != null) { %>
<p class="error"><%= request.getAttribute("error") %></p>
<% } %>

<h2>Available Items</h2>
<table>
  <tr>
    <th>Item ID</th>
    <th>Name</th>
    <th>Price</th>
    <th>Category</th>
    <th>Stock</th>
    <th>Select Quantity</th>
    <th>Action</th>
  </tr>
  <%
    List<Item> items = new ArrayList<>();
    try (Connection conn = DatabaseUtil.getConnection()) {
      String sql = "SELECT * FROM items";
      PreparedStatement stmt = conn.prepareStatement(sql);
      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        Item item = new Item(
                rs.getInt("item_id"),
                rs.getString("name"),
                rs.getDouble("price"),
                rs.getString("category"),
                rs.getInt("stock")
        );
        items.add(item);
      }
    } catch (SQLException e) {
      e.printStackTrace();
      request.setAttribute("error", "Failed to load items: " + e.getMessage());
    }
    for (Item item : items) {
  %>
  <tr>
    <form action="cart" method="get">
      <input type="hidden" name="action" value="add">
      <input type="hidden" name="itemId" value="<%= item.getItemId() %>">
      <input type="hidden" name="accountNumber" value="<%= request.getParameter("accountNumber") %>">
      <td><%= item.getItemId() %></td>
      <td><%= item.getName() %></td>
      <td><%= item.getPrice() %></td>
      <td><%= item.getCategory() %></td>
      <td><%= item.getStock() %></td>
      <td>
        <% if (item.getStock() > 0) { %>
        <input  type="number" name="qty" value="1" min="1" max="<%= item.getStock() %>">
        <% } else { %>
        Out of Stock
        <% } %>
      </td>
      <td>
        <% if (item.getStock() > 0) { %>
        <button class="addButton" type="submit">Add to Cart</button>
        <% } else { %>
        Out of Stock
        <% } %>
      </td>
    </form>
  </tr>
  <% } %>
</table>

<h2>Bill Cart</h2>
<table>
  <tr>
    <th>Item ID</th>
    <th>Name</th>
    <th>Price</th>
    <th>Category</th>
    <th>Quantity</th>
    <th>Subtotal</th>
  </tr>
  <%
    HttpSession cartSession = request.getSession();
    Map<Integer, Integer> cart = (Map<Integer, Integer>) cartSession.getAttribute("cart");
    if (cart == null) cart = new HashMap<>();
    double total = 0.0;
    try (Connection conn = DatabaseUtil.getConnection()) {
      for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {
        int itemId = entry.getKey();
        int quantity = entry.getValue();
        String sql = "SELECT * FROM items WHERE item_id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, itemId);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
          String name = rs.getString("name");
          double price = rs.getDouble("price");
          String category = rs.getString("category");
          double subtotal = price * quantity;
          total += subtotal;
  %>
  <tr>
    <td><%= itemId %></td>
    <td><%= name %></td>
    <td><%= price %></td>
    <td><%= category %></td>
    <td><%= quantity %></td>
    <td><%= subtotal %></td>
  </tr>
  <%
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
      request.setAttribute("error", "Failed to load cart items: " + e.getMessage());
    }
  %>
</table>
<p class="Total"><strong>Cash Status (Total):</strong> <%= total %></p>

<a class="AG" href="cart?action=generate&accountNumber=<%= request.getParameter("accountNumber") %>">Generate Bill</a>
<a class="actionClear" href="cart?action=clear&accountNumber=<%= request.getParameter("accountNumber") %>">Clear Cart</a>
</body>
</html>