<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.pahanaedu.bookshop.model.Item" %>
<%@ page import="java.util.List" %>
<html>
<head>
  <title>Manage Items - Pahana Edu Bookshop</title>
  <link rel="stylesheet" href="css/manageItems.css">
</head>
<body>
<h1>Manage Items</h1>
<nav>
  <a href="dashboard.jsp">Home</a>
  <a href="addCustomer.jsp">Add Customer</a>
  <a href="customer?action=view">View Customers</a>
  <a href="item?action=view">Manage Items</a>
  <a href="customer?action=view">Generate Bill</a>
  <a href="help">Help</a>
  <a href="login.jsp">Logout</a>
</nav>
<% if (request.getAttribute("success") != null) { %>
<p class="success"><%= request.getAttribute("success") %></p>
<% } %>
<% if (request.getAttribute("error") != null) { %>
<p class="error"><%= request.getAttribute("error") %></p>
<% } %>
<a href="addItem.jsp">Add New Item</a>
<table>
  <tr>
    <th>Item ID</th>
    <th>Name</th>
    <th>Price</th>
    <th>Category</th>
    <th>Stock</th>
    <th>Actions</th>
  </tr>
  <%
    List<Item> items = (List<Item>) request.getAttribute("items");
    if (items != null && !items.isEmpty()) {
      for (Item item : items) {
  %>
  <tr>
    <td><%= item.getItemId() %></td>
    <td><%= item.getName() %></td>
    <td><%= item.getPrice() %></td>
    <td><%= item.getCategory() %></td>
    <td><%= item.getStock() %></td>
    <td>
      <a href="item?action=edit&itemId=<%= item.getItemId() %>"><button> Edit</button></a>
      <a href="item?action=delete&itemId=<%= item.getItemId() %>"
         onclick="return confirm('Are you sure you want to delete this item?')"><button> Delete</button></a>
    </td>
  </tr>
  <%
    }
  } else {
  %>
  <tr>
    <td colspan="6">No items found.</td>
  </tr>
  <% } %>
</table>
</body>
</html>