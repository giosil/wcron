package org.dew.wcron.auth;

import java.util.Base64;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dew.wcron.util.LoggerFactory;

public 
class WAuthorization 
{
  protected static Logger logger = LoggerFactory.getLogger(WAuthorization.class);
  
  public static final String HEADER_AUTH  = "Authorization";
  public static final String AUTH_SCHEME  = "Basic";
  public static final String REALM_NAME   = "WCron";
  public static final String AUTHENTICATE = AUTH_SCHEME + " realm=\"" + REALM_NAME + "\"";
  
  public static
  WPrincipal checkAuthorization(HttpServletRequest request, HttpServletResponse response)
  {
    if(request == null) return null;
    try {
      String authorization = request.getHeader("Authorization");
      if(authorization == null || authorization.length() == 0) {
        response.sendError(401);
        response.setHeader("WWW-Authenticate", AUTHENTICATE);
        return null;
      }
      
      return checkAuthorization(authorization);
    }
    catch(Exception ex) {
      logger.severe("Exception in WAuthorization.checkAuthorization(request): " + ex);
    }
    return null;
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
      
      if(username.equalsIgnoreCase(password)) {
        return new WPrincipal(username);
      }
      else {
        logger.warning("Invalid username/password (" + username + ")");
      }
    }
    catch(Exception ex) {
      logger.severe("Exception in WAuthorization.checkAuthorization(" + authorization + "): " + ex);
    }
    
    return null;
  }
}
