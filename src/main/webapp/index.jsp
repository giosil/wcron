<%@page import="java.security.Principal, org.dew.wcron.auth.WAuthorization, org.dew.wcron.rest.RESTApp"%>
<%
  Principal principal = WAuthorization.getUserPrincipal(request, response);
  if(principal == null) return;
%>
<!DOCTYPE html>
<html lang="it">
<head>
  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  
  <title><%= RESTApp.NAME %> <%= RESTApp.VER %></title>
  
  <link href="css/bootstrap.min.css" rel="stylesheet">
  
  <link href="css/main.css" rel="stylesheet">

  <link href="css/plugins/jquery-ui/jquery-ui-1.10.4.custom.min.css" rel="stylesheet">
  <link href="css/plugins/datapicker/datepicker3.css" rel="stylesheet">
  
  <!--[if lt IE 9]>
   <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
   <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
  <![endif]-->
</head>
<body>
  
  <%@ include file="_menu.jsp" %>
  
  <div class="container">
    <div class="starter-template" id="view-root">
      <h1><%= RESTApp.NAME %> RESTful API</h1>
      <a href="scheduler/manager/info" target="_blank">info</a><br />
      <a href="scheduler/manager/listActivities" target="_blank">listActivities</a><br />
      <a href="scheduler/manager/getActivityNames" target="_blank">getActivityNames</a><br />
      <a href="#">addActivity (POST)</a><br />
      <a href="scheduler/manager/removeActivity/demo" target="_blank">removeActivity/{activityName}</a><br />
      <a href="scheduler/manager/listJobs" target="_blank">listJobs</a><br />
      <a href="scheduler/manager/schedule/demo/1000_5000" target="_blank">schedule/{activityName}/{expression}</a><br />
      <a href="scheduler/manager/removeJob/1" target="_blank">removeJob/{jobId}</a><br />
      <a href="scheduler/manager/getJob/1" target="_blank">getJob/{jobId}</a><br />
      <a href="scheduler/manager/clean" target="_blank">clean</a><br />
      <hr>
      <a href="out">Directory listint output folder</a>
    </div>
  </div>
  
  <script src="js/jquery-3.4.1.js"></script>
  <script src="js/bootstrap.min.js"></script>
  
  <script src="js/plugins/jquery-ui/jquery-ui.min.js" type="text/javascript"></script>
  <script src="js/plugins/datapicker/bootstrap-datepicker.js" type="text/javascript"></script>
  
  <script src="js/i18n/datapicker/bootstrap-datepicker.it.js" type="text/javascript"></script>
</body>
</html>