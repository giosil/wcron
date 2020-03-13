package org.dew.wcron.auth;

import java.security.Principal;

import javax.ws.rs.core.SecurityContext;

public 
class WSecurityContext implements SecurityContext 
{
  private final Principal principal;
  private final boolean   secure;
  private final String    authenticationScheme;
  
  public WSecurityContext(Principal principal, boolean secure, String authenticationScheme) {
    this.principal = principal;
    this.secure = secure;
    this.authenticationScheme = authenticationScheme;
  }
  
  @Override
  public Principal getUserPrincipal() {
    return principal;
  }
  
  @Override
  public boolean isUserInRole(String role) {
    return true;
  }
  
  @Override
  public boolean isSecure() {
    return secure;
  }
  
  @Override
  public String getAuthenticationScheme() {
    return authenticationScheme;
  }
}