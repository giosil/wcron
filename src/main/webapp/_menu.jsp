<%@page import="java.security.Principal, org.dew.wcron.auth.WAuthorization, org.dew.wcron.rest.RESTApp"%>
<%
  String requestURI  = request.getRequestURI();
  if(requestURI == null || requestURI.length() == 0) {
    requestURI = "/" + WAuthorization.HOME_PAGE;
  }
  
  String[] itemsHRef = {
      WAuthorization.HOME_PAGE,
      "activities.jsp",
      "jobs.jsp",
      WAuthorization.LOGIN_PAGE
  };
  String[] itemsName = {
      "Home",
      "Activities",
      "Jobs",
      "Logout",
  };
%>
<nav class="navbar navbar-inverse navbar-fixed-top" id="pheader">
  <div class="container">
    <div class="navbar-header">
      <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
        <span class="sr-only">Toggle navigation</span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
      </button>
      <a class="navbar-brand" href="<%= WAuthorization.HOME_PAGE %>"><%= RESTApp.NAME %></a>
    </div>
    <div id="navbar" class="collapse navbar-collapse">
      <% 
        for(int i = 0; i < itemsHRef.length; i++) { 
          String itemHRef  = itemsHRef[i];
          String itemName  = itemsName[i];
          String itemClass = requestURI.endsWith("/" + itemHRef) ? " class=\"active\"" : "";
      %>
      <ul class="nav navbar-nav">
        <li <%= itemClass %>><a href="<%= itemHRef %>"><%= itemName %></a></li>
      </ul>
      <% 
        } 
      %>
    </div>
  </div>
</nav>