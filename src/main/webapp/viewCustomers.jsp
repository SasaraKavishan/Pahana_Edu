<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.pahanaedu.bookshop.model.Customer" %>
<%@ page import="java.util.List" %>
<html>
<head>
  <title>View Customers - Pahana Edu Bookshop</title>
  <link rel="stylesheet" href="css/viewCustomer.css">
</head>
<body>
<h1>Customer List</h1>
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
<table>
  <tr>
    <th>Account Number</th>
    <th>Name</th>
    <th>Address</th>
    <th>Telephone</th>
    <th>Actions</th>
  </tr>
  <%
    List<Customer> customers = (List<Customer>) request.getAttribute("customers");
    if (customers != null && !customers.isEmpty()) {
      for (Customer customer : customers) {
  %>
  <tr>
    <td><%= customer.getAccountNumber() %></td>
    <td><%= customer.getName() %></td>
    <td><%= customer.getAddress() %></td>
    <td><%= customer.getTelephone() %></td>
    <td>

      <a href="customer?action=edit&accountNumber=<%= customer.getAccountNumber()%>">
        <button>Edit</button>
      </a>
      <a href="billCart.jsp?accountNumber=<%= customer.getAccountNumber() %>">
        <button>Generate Bill</button></a>
      <a href="customer?action=delete&accountNumber=<%= customer.getAccountNumber() %>"
         onclick="return confirm('Are you sure you want to delete this customer?')">
        <button>Delete</button></a>
    </td>
  </tr>
  <%
    }
  } else {
  %>
  <tr>
    <td colspan="5">No customers found.</td>
  </tr>
  <% } %>
</table>
</body>
</html>