package org.dew.wcron.ejb;

import java.lang.reflect.Array;

import java.security.Principal;

import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;

import javax.ejb.EJBContext;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.dew.wcron.util.LoggerFactory;

public 
class AuditInterceptor 
{
  @Resource
  protected EJBContext ejbContext;
  
  @AroundInvoke
  public 
  Object intercept(InvocationContext invocationContext) 
    throws Exception 
  {
    Object target        = invocationContext.getTarget();
    String targetClass   = target != null ? target.getClass().getName() : "-";
    String methodName    = invocationContext.getMethod().getName();
    Object[] parameters  = invocationContext.getParameters();
    Principal principal  = ejbContext.getCallerPrincipal();
    String principalName = principal != null ? "<" + principal.getName() + ">" : "";
    
    Logger logger = LoggerFactory.getLogger(targetClass);
    logger.entering(targetClass, methodName + principalName, parameters);
    try {
      Object result = invocationContext.proceed();
      
      if(result != null && result.getClass().isArray()) {
        logger.exiting(targetClass, methodName + principalName, formatResult(result));
      }
      else if(result instanceof Collection) {
        logger.exiting(targetClass, methodName + principalName, formatResult(result));
      }
      else {
        logger.exiting(targetClass, methodName + principalName, result);
      }
      
      return result;
    }
    catch(Exception ex) {
      // throwing use Level.FINER.
      // logger.throwing(targetClass, methodName + principalName, ex);
      
      // Unexpected exceptions
      logger.logp(Level.SEVERE, targetClass, methodName + principalName, "[AuditInterceptor]", ex);
      
      throw ex;
    }
  }
  
  private static 
  String formatResult(Object result)
  {
    if(result == null) return "null";
    
    if(result instanceof byte[]) {
      return "byte[" + Array.getLength(result) + "]";
    }
    else if(result instanceof Collection) {
      String s = "";
      Object item0 = null;
      int iSize = ((Collection<?>) result).size();
      Iterator<?> iterator = ((Collection<?>) result).iterator();
      if(iterator.hasNext()) {
        item0 = iterator.next();
      }
      if(item0 != null) {
        s = " of " + item0.getClass().getCanonicalName() + " first=" + item0;
      }
      return result.getClass().getCanonicalName() + " [" + iSize + "]" + s;
    }
    else if(result.getClass().isArray()) {
      String s = "";
      int length = Array.getLength(result);
      if(length > 0) {
        Object item0 = Array.get(result, 0);
        if(item0 != null) {
          s = " of " + item0.getClass().getCanonicalName() + " [0]=" + item0;
        }
        if(length > 1) {
          int idxL = length - 1;
          Object itemL = Array.get(result, idxL);
          if(itemL != null) {
            s += " ... [" + idxL + "]=" + itemL;
          }
        }
      }
      return "Array [" + length + "]" + s;
    }
    
    return result.toString();
  }
}
