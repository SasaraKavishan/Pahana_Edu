<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<html>
<head>
  <title>View Bills - Pahana Edu Bookshop</title>
  <link rel="stylesheet" href="css/billCart.css">
</head>
<body>
<h1>All Generated Bills</h1>
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
<% if (request.getAttribute("success") != null) { %>
<p class="success"><%= request.getAttribute("success") %></p>
<% } %>

<h2>Bills</h2>
<table>
  <tr>
    <th>Bill ID</th>
    <th>Account Number</th>
    <th>Customer Name</th>
    <th>Items</th>
    <th>Total</th>
    <th>Created At</th>
    <th>Actions</th>
  </tr>
  <%
    List<Map<String, Object>> bills = (List<Map<String, Object>>) request.getAttribute("bills");
    if (bills != null && !bills.isEmpty()) {
      for (Map<String, Object> bill : bills) {
  %>
  <tr>
    <td><%= bill.get("billId") %></td>
    <td><%= bill.get("accountNumber") %></td>
    <td><%= bill.get("customerName") %></td>
    <td><%= bill.get("itemNames") != null ? bill.get("itemNames") : "-" %></td>
    <td><%= bill.get("total") %></td>
    <td><%= bill.get("createdAt") %></td>
    <td>
      <a href="bills?action=delete&billId=<%= bill.get("billId") %>"
         onclick="return confirm('Are you sure you want to delete this bill?')">
        <button class="addButton">Delete</button>
      </a>
    </td>
  </tr>
  <%
    }
  } else {
  %>
  <tr>
    <td colspan="7">No bills found.</td>
  </tr>
  <% } %>
</table>
</body>
</html>