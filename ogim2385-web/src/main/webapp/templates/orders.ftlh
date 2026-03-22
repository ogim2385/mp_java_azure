<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Orders</title>
    <link rel="stylesheet" type="text/css" href="${request.contextPath}/css/style.css"></head>
<body>
<h1>Orders</h1>

<table>
    <thead>
    <tr>
        <th>ID</th>
        <th>Date</th>
        <th>Price</th>
        <th>Status</th>
        <th>Shipping address</th>
    </tr>
    </thead>
    <tbody>
    <#list orders as order>
        <tr>
            <td>${order.id}</td>
            <td>${order.orderDate}</td>
            <td>${order.price}</td>
            <td>${order.status}</td>
            <td>${order.shippingAddress}</td>
        </tr>
    </#list>
    </tbody>
</table>

<form method="post" action="${request.contextPath}/logout">
    <button type="submit">Logout</button>
</form>
</body>
</html>