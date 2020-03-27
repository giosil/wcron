package org.dew.wcron.auth;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

public 
class WHttpServletRequestWrapper extends HttpServletRequestWrapper 
{
  public 
  WHttpServletRequestWrapper(HttpServletRequest request)
  {
    super(request);
  }
  
  @Override
  public 
  Principal getUserPrincipal()
  {
    Principal principal = super.getUserPrincipal();
    
    if(principal != null) {
      return principal;
    }
    
    HttpSession httpSession = super.getSession();
    if(httpSession != null) {
      Object sessionAuth = httpSession.getAttribute(WAuthorization.SESSION_ATTR);
      if(sessionAuth instanceof WPrincipal) {
        return (WPrincipal) sessionAuth;
      }
    }
    
    return principal;
  }
  
  @Override
  public 
  String getRemoteUser()
  {
    Principal principal = getUserPrincipal();
    
    return principal == null ? super.getRemoteUser() : principal.getName();
  }
}
