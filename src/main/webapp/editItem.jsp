<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.pahanaedu.bookshop.model.Item" %>
<html>
<head>
  <title>Edit Item - Pahana Edu Bookshop</title>
  <link rel="stylesheet" href="css/editItem.css">
</head>
<body>
<h1>Edit Item</h1>
<nav>
  <a href="dashboard.jsp">Home</a>
  <a href="addCustomer.jsp">Add Customer</a>
  <a href="customer?action=view">View Customers</a>
  <a href="item?action=view">Manage Items</a>
  <a href="customer?action=view">Generate Bill</a>
  <a href="help">Help</a>
  <a href="login.jsp">Logout</a>
</nav>
<% if (request.getAttribute("error") != null) { %>
<p class="error"><%= request.getAttribute("error") %></p>
<% } %>
<% Item item = (Item) request.getAttribute("item"); %>
<% if (item != null) { %>
<form action="item" method="post">
  <input type="hidden" name="action" value="edit">
  <input type="hidden" name="itemId" value="<%= item.getItemId() %>">
  <label>Name:</label>
  <input type="text" name="name" value="<%= item.getName() %>" required><br>
  <label>Price:</label>
  <input type="number" name="price" step="0.01" value="<%= item.getPrice() %>" required><br>
  <label>Category:</label>
  <input type="text" name="category" value="<%= item.getCategory() %>" required><br>
  <label>Stock:</label>
  <input type="number" name="stock" value="<%= item.getStock() %>" min="0" required><br>
  <button type="submit">Update Item</button>
</form>
<% } else { %>
<p>No item data available.</p>
<% } %>
<a class="editItem-backButton" href="item?action=view">Back to Items</a>
</body>
</html>