package org.dew.wcron.auth;

import java.security.Principal;
import java.util.Base64;

import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.dew.wcron.util.LoggerFactory;

public 
class WAuthorization 
{
  protected static Logger logger = LoggerFactory.getLogger(WAuthorization.class);
  
  public static final String HEADER_AUTH  = "Authorization";
  public static final String AUTH_SCHEME  = "Basic";
  public static final String REALM_NAME   = "WCron";
  public static final String AUTHENTICATE = AUTH_SCHEME + " realm=\"" + REALM_NAME + "\"";
  
  public static final String AUTH_SESSION = "Session";
  public static final String SESSION_ATTR = "User";
  public static final String LOGIN_PAGE   = "login.jsp";
  public static final String HOME_PAGE    = "index.jsp";
  public static final String PARAM_USER   = "j_username";
  public static final String PARAM_PASS   = "j_password";
  
  public static
  void logout(HttpServletRequest request)
  {
    try {
      Principal principal = request.getUserPrincipal();
      if(principal != null) {
        request.logout();
      }
      
      HttpSession httpSession = request.getSession();
      if(httpSession != null) {
        httpSession.invalidate();
      }
    }
    catch(Exception ex) {
      System.err.println("WAuthorization.logout: " + ex);
    }
  }
  
  public static
  WPrincipal getUserPrincipal(HttpServletRequest request)
  {
    Principal principal = request.getUserPrincipal();
    
    if(principal instanceof WPrincipal) {
      return (WPrincipal) principal;
    }
    else if(principal != null) {
      return new WPrincipal(principal.getName());
    }
    
    HttpSession httpSession = request.getSession();
    if(httpSession != null) {
      Object sessionAuth = httpSession.getAttribute(SESSION_ATTR);
      if(sessionAuth instanceof WPrincipal) {
        return (WPrincipal) sessionAuth;
      }
    }
    
    return null;
  }
  
  public static
  WPrincipal getUserPrincipal(HttpServletRequest request, HttpServletResponse response)
  {
    Principal principal = request.getUserPrincipal();
    
    if(principal instanceof WPrincipal) {
      return (WPrincipal) principal;
    }
    else if(principal != null) {
      return new WPrincipal(principal.getName());
    }
    
    HttpSession httpSession = request.getSession();
    if(httpSession != null) {
      Object sessionAuth = httpSession.getAttribute(SESSION_ATTR);
      if(sessionAuth instanceof WPrincipal) {
        return (WPrincipal) sessionAuth;
      }
    }
    
    try {
      RequestDispatcher requestDispatcher = request.getRequestDispatcher(LOGIN_PAGE);
      requestDispatcher.forward(request, response);
    }
    catch(Exception ex) {
      logger.severe("Exception in WAuthorization.getUserPrincipal(request, response): " + ex);
    }
    
    return null;
  }
  
  public static
  WPrincipal checkAuthorization(HttpServletRequest request, HttpServletResponse response)
  {
    WPrincipal result = null;
    try {
      HttpSession httpSession = request.getSession();
      if(httpSession != null) {
        Object sessionAuth = httpSession.getAttribute(SESSION_ATTR);
        if(sessionAuth instanceof WPrincipal) {
          return (WPrincipal) sessionAuth;
        }
      }
      
      String authorization = request.getHeader("Authorization");
      if(authorization == null || authorization.length() == 0) {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        response.setHeader("WWW-Authenticate", AUTHENTICATE);
        return null;
      }
      
      result = checkAuthorization(authorization);
      
      if(result == null) {
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
      }
    }
    catch(Exception ex) {
      logger.severe("Exception in WAuthorization.checkAuthorization(request,response): " + ex);
    }
    return result;
  }
  
  public static
  WPrincipal checkAuthorization(String authorization)
  {
    logger.fine("checkAuthorization(" + authorization + ")...");
    
    if(authorization == null || authorization.length() == 0) {
      logger.warning("Invalid authorization (" + authorization + ")");
      return null;
    }
    
    try {
      byte[] basic = Base64.getDecoder().decode(authorization.substring(AUTH_SCHEME.length()).trim());
      if(basic == null || basic.length == 0) return null;
      
      String credentials = new String(basic);
      
      int sep = credentials.indexOf(':');
      if(sep < 0) return null;
      
      String username = credentials.substring(0,sep);
      String password = credentials.substring(sep+1);
      
      return checkAuthorization(username, password);
    }
    catch(Exception ex) {
      logger.severe("Exception in WAuthorization.checkAuthorization(" + authorization + "): " + ex);
    }
    
    return null;
  }
  
  public static
  WPrincipal checkAuthorization(String username, String password)
    throws Exception
  {
    logger.fine("checkAuthorization(" + username + ",*)...");
    
    if(username == null || password == null) {
      logger.warning("Invalid credentials (" + username + ",*)");
      return null;
    }
    
    try {
      if(username.equalsIgnoreCase(password)) {
        return new WPrincipal(username);
      }
      else {
        logger.warning("Invalid username/password (" + username + ")");
      }
    }
    catch(Exception ex) {
      logger.severe("Exception in WAuthorization.checkAuthorization(" + username + ",*): " + ex);
    }
    
    return null;
  }
}
