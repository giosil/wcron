package org.dew.wcron.services;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.dew.wcron.auth.WContainerRequestFilter;

@ApplicationPath("/scheduler")
public 
class RESTApp extends Application 
{
  public static final String NAME = "wcron";
  public static final String VER  = "1.0.5";
  
  private Set<Class<?>> classes = new HashSet<Class<?>>();
  private Set<Object> singletons = new HashSet<Object>();
  
  public RESTApp() {
    // Filters
    classes.add(WContainerRequestFilter.class);
    
    // Services
    classes.add(RESTCronManager.class);
  }
  
  @Override
  public Set<Class<?>> getClasses() {
     return classes;
  }
  
  @Override
  public Set<Object> getSingletons() {
     return singletons;
  }
}
