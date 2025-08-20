<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.pahanaedu.bookshop.model.Customer" %>
<html>
<head>
  <title>Edit Customer - Pahana Edu Bookshop</title>
  <link rel="stylesheet" href="css/editCustomer.css">
</head>
<body>
<h1>Edit Customer</h1>
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
<% Customer customer = (Customer) request.getAttribute("customer"); %>
<% if (customer != null) { %>
<form action="customer" method="post">
  <input type="hidden" name="action" value="edit">
  <input type="hidden" name="accountNumber" value="<%= customer.getAccountNumber() %>">
  <label>Name:</label>
  <input type="text" name="name" value="<%= customer.getName() %>" required><br>
  <label>Address:</label>
  <input type="text" name="address" value="<%= customer.getAddress() %>" required><br>
  <label>Telephone:</label>
  <input type="text" name="telephone" value="<%= customer.getTelephone() %>" required><br>
  <button type="submit">Update Customer</button>
</form>
<% } else { %>
<p>No customer data available.</p>
<% } %>
<a class="backButton" href="customer?action=view">Back to Customers</a>
</body>
</html>