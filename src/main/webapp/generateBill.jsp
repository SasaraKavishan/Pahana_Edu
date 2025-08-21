<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.pahanaedu.bookshop.model.Customer" %>
<%@ page import="com.pahanaedu.bookshop.model.Item" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<html>
<head>
  <title>Generate Bill - Pahana Edu Bookshop</title>
  <link rel="stylesheet" href="css/generateBill.css">
</head>
<body>
<h1>Customer Bill</h1>

<% if (request.getAttribute("success") != null) { %>
<p class="success"><%= request.getAttribute("success") %></p>
<% } %>
<% if (request.getAttribute("error") != null) { %>
<p class="error"><%= request.getAttribute("error") %></p>
<% } %>
<% Customer customer = (Customer) request.getAttribute("customer"); %>
<% if (customer != null) { %>
<h2>Bill Details (ID: <%= request.getAttribute("billId") %>)</h2>
<p><strong>Account Number:</strong> <%= customer.getAccountNumber() %></p>
<p><strong>Name:</strong> <%= customer.getName() %></p>
<p><strong>Address:</strong> <%= customer.getAddress() %></p>
<p><strong>Telephone:</strong> <%= customer.getTelephone() %></p>
<% } else { %>
<p class="error">Customer information not available.</p>
<% } %>
<% List<Map.Entry<Item, Integer>> cartItems = (List<Map.Entry<Item, Integer>>) request.getAttribute("cartItems"); %>
<% Double total = (Double) request.getAttribute("total"); %>
<h3>Cart Items</h3>
<table>
  <tr>
    <th>Item Name</th>
    <th>Category</th>
    <th>Price</th>
    <th>Quantity</th>
    <th>Subtotal</th>
  </tr>
  <% if (cartItems != null) { %>
  <% for (Map.Entry<Item, Integer> entry : cartItems) { %>
  <tr>
    <td><%= entry.getKey().getName() %></td>
    <td><%= entry.getKey().getCategory() %></td>
    <td><%= entry.getKey().getPrice() %></td>
    <td><%= entry.getValue() %></td>
    <td><%= entry.getKey().getPrice() * entry.getValue() %></td>
  </tr>
  <% } %>
  <% } else { %>
  <tr>
    <td colspan="5">No items in cart.</td>
  </tr>
  <% } %>
</table>
<p><strong>Bill Total:</strong> LKR <%= String.format("%.2f", total != null ? total : 0.0) %></p>
<button type="button" class="btn btn-sm btn-primary mt-2" onclick="window.print()">Print Bill</button>
<% if (customer != null) { %>
<% } %>

</body>
</html>