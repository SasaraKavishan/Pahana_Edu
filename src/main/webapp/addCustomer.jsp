<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title>Add Customer - Pahana Edu Bookshop</title>
  <link rel="stylesheet" href="css/addCustomer.css">
</head>
<body>
<h1>Add New Customer</h1>
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
<form action="customer" method="post">
  <input type="hidden" name="action" value="add">
  <label>Account Number:</label>
  <input type="text" name="accountNumber" required><br>
  <label>Name:</label>
  <input type="text" name="name" required><br>
  <label>Address:</label>
  <input type="text" name="address" required><br>
  <label>Telephone:</label>
  <input type="text" name="telephone" required><br>
  <button type="submit">Add Customer</button>
</form>
<a class="black " href="customer?action=view">Back to Customers</a>
</body>
</html>