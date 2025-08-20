<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title>Dashboard - Pahana Edu Bookshop</title>
  <link rel="stylesheet" href="css/dashBoard.css">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
<h1>Welcome</h1>
<h2>Pahana Edu Bookshop</h2>
<ul>
  <li><a href="dashboard.jsp" class="nav-link"><i class="fas fa-home"></i> Home</a></li>
  <li><a href="addCustomer.jsp" class="nav-link"><i class="fas fa-user-plus"></i> Add Customer</a></li>
  <li><a href="customer?action=view" class="nav-link"><i class="fas fa-users"></i> View Customers</a></li>
  <li><a href="item?action=view" class="nav-link"><i class="fas fa-book"></i> Manage Items</a></li>
  <li><a href="customer?action=view" class="nav-link"><i class="fas fa-file-invoice"></i> Generate Bill</a></li>
  <li><a href="help" class="nav-link"><i class="fas fa-question-circle"></i> Help</a></li>
  <li><a href="login.jsp" class="nav-link"><i class="fas fa-sign-out-alt"></i> Logout</a></li>
</ul>
</body>
</html>