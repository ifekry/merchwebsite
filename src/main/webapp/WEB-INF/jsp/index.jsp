<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html lang="en">
<head>

	<!-- Access the bootstrap Css like this, 
		Spring boot will handle the resource mapping automcatically -->
	<link rel="stylesheet" type="text/css" href="webjars/bootstrap/3.3.7/css/bootstrap.min.css" />

	<!-- 
	<spring:url value="/css/main.css" var="springCss" />
	<link href="${springCss}" rel="stylesheet" />
	 -->
	<c:url value="/css/main.css" var="jstlCss" />
	<link href="${jstlCss}" rel="stylesheet" />

</head>
<body>

	<nav class="navbar navbar-inverse">
		<div class="container">
			<div class="navbar-header">
				<a class="navbar-brand" href="#">Merchant Website</a>
			</div>
		</div>
	</nav>

	<div class="container">

		<div class="starter-template">
			<p>Please enter your card details</p>
			<div class="col-md-6">
				<form action="payment" method="post">
				<table class="table">
					<tr><td>Card Number:<td><input type="text" name="cardnumber" class="form-control">
					<tr><td>Card-Holder Name:<td><input type="text" name="cardholdername" class="form-control">
					<tr><td>Card Expiry Date (YYMM):<td><input type="text" name="expirydate" class="form-control">
					<tr><td>CVV2/CVC2:<td><input type="text" name="cvv" class="form-control">
					<tr><td>Amount:<td>85.00 USD<input type="hidden" name="amount" value="85.00">
					<tr><td><td style="text-align: right;"><input type="submit" class="btn btn-info" value="Pay Now">
				</table>
				</form>
			</div>
		</div>

	</div>
	
	<script type="text/javascript" src="webjars/bootstrap/3.3.7/js/bootstrap.min.js"></script>

</body>

</html>