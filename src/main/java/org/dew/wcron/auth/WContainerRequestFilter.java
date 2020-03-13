package org.dew.wcron.auth;

import java.io.IOException;

import java.util.Base64;
import java.util.logging.Logger;

import javax.annotation.Priority;

import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

import org.dew.wcron.LoggerFactory;

@WSecure // NameBinding
@Provider
@Priority(Priorities.AUTHORIZATION)
public 
class WContainerRequestFilter implements ContainerRequestFilter
{
  protected static Logger logger = LoggerFactory.getLogger(WContainerRequestFilter.class);
  
  private static final String AUTH_SCHEME = "Basic";
  private static final String REALM_NAME  = "WCron";
  
  @Override
  public 
  void filter(ContainerRequestContext requestContext) 
    throws IOException 
  {
    String authorization = requestContext.getHeaderString("Authorization");
    
    if(authorization == null || authorization.length() == 0) {
      Response resUnauthorized = Response.status(Response.Status.UNAUTHORIZED).header("WWW-Authenticate", "Basic realm=\"" + REALM_NAME + "\"").build();
      
      requestContext.abortWith(resUnauthorized);
      
      return;
    }
    
    WPrincipal principal = checkAuthorization(authorization);
    
    if (principal == null) {
      Response resForbidden = Response.status(Response.Status.FORBIDDEN).entity("Forbidden").build();
      
      requestContext.abortWith(resForbidden);
    }
    
    final SecurityContext securityContext = requestContext.getSecurityContext();
    
    requestContext.setSecurityContext(new WSecurityContext(principal, securityContext.isSecure(), AUTH_SCHEME));
  }
  
  protected
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
      
      if(username.equalsIgnoreCase(password)) {
        return new WPrincipal(username);
      }
      else {
        logger.warning("Invalid username/password (" + username + ")");
      }
    }
    catch(Exception ex) {
      logger.severe("Exception in WContainerRequestFilter.checkAuthorization(" + authorization + "): " + ex);
    }
    
    return null;
  }
}
