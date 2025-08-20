<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Add Item - Pahana Edu Bookshop</title>
    <link rel="stylesheet" href="css/addItem.css">
</head>
<body>
<h1>Add New Item</h1>
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
<form action="item" method="post">
    <input type="hidden" name="action" value="add">
    <label>Name:</label>
    <input type="text" name="name" required><br>
    <label>Price:</label>
    <input type="number" name="price" step="0.01" required><br>
    <label>Category:</label>
    <input type="text" name="category" required><br>
    <label>Stock:</label>
    <input type="number" name="stock" min="0" required><br>
    <button type="submit">Add Item</button>
</form>
<a class="addItem-backButtom" href="item?action=view">Back to Items</a>
</body>
</html>