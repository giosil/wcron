<%@page import="java.io.File, java.util.List, org.dew.wcron.rest.RESTApp, org.dew.wcron.util.DataUtil"%>
<%
  String root    = DataUtil.expectString(request.getAttribute("root"), "/");
  String curr    = DataUtil.expectString(request.getAttribute("curr"), ".");
  String message = DataUtil.expectString(request.getAttribute("message"));
  File[] files   = DataUtil.expectArray(request.getAttribute("files"), File.class);
  List<String> breadCrumb = DataUtil.expectList(request.getAttribute("breadCrumb"), String.class);
%>
<!DOCTYPE html>
<html lang="it">
<head>
  <meta charset="utf-8">
  <title><%= RESTApp.NAME %> <%= RESTApp.VER %></title>
</head>
<body>
  <h1>Directory listing for <%= curr %></h1>
  <br />
  <% 
    if(message != null && message.length() > 0) {
      out.println("<h2>" + message.replace("<", "&lt;").replace(">", "&gt;") + "</h2>");
      out.println("<br />");
    }
  %>
  <%
    int size = breadCrumb != null ? breadCrumb.size() : 0;
    out.println("<a href=\"" + root + "\" title=\"root\">[" + root + "]</a>");
    String itemHRef = root;
    for(int i = 0; i < size; i++) {
      String item = breadCrumb.get(i);
      itemHRef += "/" + item;
      out.print(" | <a href=\"" + itemHRef + "\" title=\"" + item + "\">" + item + "</a>");
    }
  %>
  <br />
  <hr>
  <%
    if(files != null && files.length > 0) {
      for(int i = 0; i < files.length; i++) {
        File file       = files[i];
        String fileName = file.getName();
        String fileHRef = curr + "/" + fileName;
        String fileLink = "<a href=\"" + fileHRef + "\" title=\"" + fileName + "\">" + fileName + "</a>";
        out.println(fileLink + "<br />");
      }
    }
  %>
</body>
</html>