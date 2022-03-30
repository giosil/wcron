package org.dew.wcron.web;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;

import javax.servlet.annotation.WebServlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.dew.wcron.auth.WAuthorization;
import org.dew.wcron.auth.WPrincipal;

@WebServlet(name = "WebLogin", loadOnStartup = 0, urlPatterns = { "/login" })
public 
class WebLogin extends HttpServlet 
{
  private static final long serialVersionUID = -3616510726961999128L;
  
  @Override
  protected 
  void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException
  {
    HttpSession httpSession = request.getSession();
    if(httpSession == null) {
      sendMessage(request, response, "Session disabled");
      return;
    }
    
    String username = request.getParameter(WAuthorization.PARAM_USER);
    if(username == null || username.length() == 0) {
      sendMessage(request, response, "Invalid username");
      return;
    }
    request.setAttribute("username", username);
    
    String password = request.getParameter(WAuthorization.PARAM_PASS);
    if(password == null || password.length() == 0) {
      sendMessage(request, response, "Invalid password");
      return;
    }
    
    WPrincipal principal = null;
    try {
      principal = WAuthorization.checkAuthorization(username, password);
      
      if(principal != null) {
        
        httpSession.setAttribute(WAuthorization.SESSION_ATTR, principal);
        
      }
      else {
        
        sendMessage(request, response, "Invalid credentials");
        return;
        
      }
    }
    catch(Exception ex) {
      sendMessage(request, response, "Error: " + ex);
      return;
    }
    
    RequestDispatcher requestDispatcher = request.getRequestDispatcher(WAuthorization.HOME_PAGE);
    requestDispatcher.forward(request, response);
  }
  
  protected static
  void sendMessage(HttpServletRequest request, HttpServletResponse response, String message)
      throws ServletException, IOException
  {
    request.setAttribute("message", message);
    
    RequestDispatcher requestDispatcher = request.getRequestDispatcher(WAuthorization.LOGIN_PAGE);
    requestDispatcher.forward(request, response);
  }
}