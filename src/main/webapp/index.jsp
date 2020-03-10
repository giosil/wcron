<%@page import="org.dew.wcron.rest.RESTApp"%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8">
		<title><%= RESTApp.NAME %> <%= RESTApp.VER %></title>
	</head>
	<body>
		<h1><%= RESTApp.NAME %> <%= RESTApp.VER %></h1>
		<br/>
		<a href="scheduler/manager/info">scheduler/manager/info</a><br/>
		<a href="scheduler/manager/listActivities">scheduler/manager/listActivities</a><br/>
	</body>
</html>