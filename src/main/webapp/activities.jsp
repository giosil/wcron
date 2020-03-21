<%@page import="java.security.Principal, org.dew.wcron.auth.WAuthorization, org.dew.wcron.util.DataUtil, org.dew.wcron.rest.RESTApp"%>
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
      
      <h1>Activities</h1>
      
      <div class="row" style="padding-bottom: 8px">
        <div class="col-md-12 text-right">
          <button type="button" class="btn btn-primary" data-toggle="modal" id="btnAdd">Add Activity</button>
        </div>
      </div>
      
      <div class="row">
        <div class="col-md-12">
          <table class="table" id="tabResult">
            <thead>
              <tr>
                <th scope="col">Name</th>
                <th scope="col">URI</th>
                <th scope="col">Created At</th>
                <th scope="col">Actions</th>
              </tr>
            </thead>
            <tbody>
            </tbody>
          </table>
        </div>
      </div>
      
    </div>
  </div>
  
  <div class="modal fade" id="dlgEdit" tabindex="-1" role="dialog" aria-labelledby="dlgEditLabel" aria-hidden="true">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h3 class="modal-title" id="dlgEditLabel">Activity</h3>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body">
        <form>
          <label for="name">Name</label>
          <input type="text" id="name" name="name" class="form-control" placeholder="Name" required>
          <label for="uri">URI</label>
          <input type="text" id="uri" name="uri" class="form-control" placeholder="URI" required>
          <label for="parameters">Parameters</label>
          <input type="text" id="parameters" name="parameters" class="form-control" placeholder="Parameters" required>
        </form>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
        <button type="button" class="btn btn-primary" id="btnSave">Save</button>
      </div>
    </div>
  </div>
  </div>
  
  <script src="js/jquery-3.4.1.js"></script>
  <script src="js/bootstrap.min.js"></script>
  
  <script src="js/plugins/jquery-ui/jquery-ui.min.js" type="text/javascript"></script>
  <script src="js/plugins/datapicker/bootstrap-datepicker.js" type="text/javascript"></script>
  
  <script src="js/i18n/datapicker/bootstrap-datepicker.it.js" type="text/javascript"></script>
  
  <script src="js/app/activities.js?<%= DataUtil.STARTUP_TIME %>" type="text/javascript"></script>
  
  <script type="text/javascript">
    $(document).ready(function(){
      
      _initPageApp();
      
    });
  </script>
</body>
</html>