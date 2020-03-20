package org.dew.wcron.auth;

import java.io.IOException;

import java.security.Principal;

import javax.annotation.Priority;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

@WSecure // NameBinding
@Provider
@Priority(Priorities.AUTHORIZATION)
public 
class WContainerRequestFilter implements ContainerRequestFilter
{
  @Context 
  protected HttpServletRequest httpServletRequest;
  
  @Override
  public 
  void filter(ContainerRequestContext requestContext) 
    throws IOException 
  {
    Principal principal = WAuthorization.getUserPrincipal(httpServletRequest);
    
    if(principal != null) {
      final SecurityContext securityContext = requestContext.getSecurityContext();
      
      requestContext.setSecurityContext(new WSecurityContext(principal, securityContext.isSecure(), WAuthorization.AUTH_SESSION));
      
      return;
    }
    
    String authorization = requestContext.getHeaderString(WAuthorization.HEADER_AUTH);
    
    if(authorization == null || authorization.length() == 0) {
      Response resUnauthorized = Response.status(Response.Status.UNAUTHORIZED).header("WWW-Authenticate", WAuthorization.AUTHENTICATE).build();
      
      requestContext.abortWith(resUnauthorized);
      
      return;
    }
    
    principal = WAuthorization.checkAuthorization(authorization);
    
    if (principal == null) {
      Response resForbidden = Response.status(Response.Status.FORBIDDEN).entity("Forbidden").build();
      
      requestContext.abortWith(resForbidden);
    }
    
    final SecurityContext securityContext = requestContext.getSecurityContext();
    
    requestContext.setSecurityContext(new WSecurityContext(principal, securityContext.isSecure(), WAuthorization.AUTH_SCHEME));
  }
}
