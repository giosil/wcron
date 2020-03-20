<%@page import="org.dew.wcron.auth.WAuthorization, org.dew.wcron.rest.RESTApp, org.dew.wcron.util.DataUtil"%>
<% 
  WAuthorization.logout(request);
  
  String username = DataUtil.expectString(request.getAttribute("username"), "");
  String message  = DataUtil.expectString(request.getAttribute("message"));
%>
<!DOCTYPE html>
<html lang="it">
<head>
  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1">

  <title><%= RESTApp.NAME %> <%= RESTApp.VER %></title>

  <link href="css/bootstrap.min.css" rel="stylesheet">
  
  <style>
    .form-signin {
      width: 100%;
      max-width: 330px;
      padding: 15px;
      margin-top: 100px;
      margin-left: auto;
      margin-right: auto;
    }
    .form-signin input{
      margin-bottom: 8px;
    }
  </style>
  
  <!--[if lt IE 9]>
   <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
   <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
  <![endif]-->
</head>
<body class="text-center">
  
  <form class="form-signin" method="POST" action="login">
    <h1>WCron</h1>
    
    <label for="j_username" class="sr-only">Username</label>
    <input type="text" id="<%= WAuthorization.PARAM_USER %>" name="<%= WAuthorization.PARAM_USER %>" class="form-control" placeholder="Username" value="<%= username %>" required autofocus>
    
    <label for="j_password" class="sr-only">Password</label>
    <input type="password" id="<%= WAuthorization.PARAM_PASS %>" name="<%= WAuthorization.PARAM_PASS %>" class="form-control" placeholder="Password" required>
    
    <% if(message != null && message.length() > 0) { %>
      <div class="alert alert-warning" role="alert">
        <%= message %>
      </div>
    <% } %>
    
    <button class="btn btn-lg btn-primary btn-block" type="submit">Sign in</button>
  </form>
  
  <script src="js/jquery-3.4.1.js"></script>
  <script src="js/bootstrap.min.js"></script>
</body>
</html>