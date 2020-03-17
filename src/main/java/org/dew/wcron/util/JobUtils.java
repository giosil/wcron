package org.dew.wcron.util;

import java.io.File;

import java.lang.reflect.Method;

import java.math.BigInteger;

import java.net.URL;
import java.net.URLClassLoader;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.dew.wcron.api.Activity;
import org.dew.wcron.api.Job;
import org.dew.wcron.api.JobMock;

public 
class JobUtils 
{
  protected static ClassLoader _classLoader;
  
  public static
  ClassLoader getJobClassLoader()
    throws Exception
  {
    if(_classLoader != null) return _classLoader;
    
    String classesFolderPath = System.getProperty("user.home") + File.separator + "wcron" + File.separator + "classes";
    
    File classesFolder = new File(classesFolderPath);
    if(!classesFolder.exists()) classesFolder.mkdirs();
    
    URL[] urls = new URL[] { classesFolder.toURI().toURL() };
    
    _classLoader = URLClassLoader.newInstance(urls, JobUtils.class.getClassLoader());
    
    return _classLoader;
  }
  
  public static
  Callable<?> createJobInstance(Job job)
    throws Exception
  {
    if(job == null) {
      throw new Exception("Null pointer to job instance");
    }
    
    Activity activity = job.getActivity();
    if(activity == null) {
      throw new Exception("No activity in job");
    }
    
    // Execution parameters
    Map<String,Object> parameters = new HashMap<String, Object>();
    
    // Activity parameters
    Map<String,Object> actParameters = activity.getParameters();
    if(actParameters != null && !actParameters.isEmpty()) {
      parameters.putAll(actParameters);
    }
    // Job parameters
    Map<String,Object> jobParameters = job.getParameters();
    if(jobParameters != null && !jobParameters.isEmpty()) {
      parameters.putAll(jobParameters);
    }
    
    String uri = activity.getUri();
    
    return createJobInstance(uri, job.getId(), parameters);
  }
  
  public static
  Callable<?> createJobInstance(String uri, long jobId, Map<String,Object> parameters)
    throws Exception
  {
    if(uri == null || uri.length() == 0) {
      throw new Exception("Invali uri");
    }
    
    if(parameters == null) {
      parameters = new HashMap<String, Object>();
    }
    
    Callable<?> result = null;
    
    if(uri.equalsIgnoreCase("mock")) {
      result = new JobMock();
    }
    else if(uri.startsWith("java:")) {
      Context ctx = new InitialContext();
      
      Object instance = ctx.lookup(uri);
      
      if(!(instance instanceof Callable)) {
        throw new Exception("Uri " + uri + " is not a java.util.concurrent.Callable object.");
      }
      
      result = (Callable<?>) instance;
    }
    else {
      ClassLoader classLoader = getJobClassLoader();
      if(classLoader == null) {
        throw new Exception("It is not possible load " + uri + ": classLoader not available");
      }
      
      Class<?> cls = classLoader.loadClass(uri);
      
      Object instance = cls.newInstance();
      
      if(!(instance instanceof Callable)) {
        throw new Exception("Uri " + uri + " is not a java.util.concurrent.Callable object.");
      }
      
      result = (Callable<?>) instance;
    }
    
    Method[] methods = result.getClass().getMethods();
    
    for(int i = 0; i < methods.length; i++) {
      Method method = methods[i];
      
      String methodName = method.getName();
      
      if(methodName.equals("setId")) {
        Class<?>[] types = method.getParameterTypes();
        if(types == null || types.length != 1) {
          continue;
        }
        Object param = getParameter(jobId, types[0].getName());
        if(param != null) {
          method.invoke(result, new Object[] { param });
        }
      }
      else if(methodName.equals("setParameters")) {
        Class<?>[] types = method.getParameterTypes();
        if(types == null || types.length != 1) {
          continue;
        }
        Object param = getParameter(parameters, types[0].getName());
        if(param != null) {
          method.invoke(result, new Object[] { param });
        }
      }
    }
    
    return result;
  }
  
  protected static
  Object getParameter(long value, String typeName)
  {
    if(typeName == null) return null;
    
    if(typeName.equals("java.lang.Long")) {
      return new Long(value);
    }
    else if(typeName.equals("long")) {
      return new Long(value);
    }
    else if(typeName.equals("java.lang.String")) {
      return String.valueOf(value);
    }
    else if(typeName.equals("int")) {
      return new Integer((int) value);
    }
    else if(typeName.equals("java.lang.Integer")) {
      return new Integer((int) value);
    }
    else if(typeName.equals("java.math.BigInteger")) {
      return new BigInteger(String.valueOf(value));
    }
    
    return null;
  }
  
  protected static
  Object getParameter(Map<String, Object> map, String typeName)
  {
    if(typeName == null) return null;
    
    if(typeName.equals("java.util.Map")) {
      return map;
    }
    else if(typeName.equals("java.util.HashMap")) {
      if(map instanceof HashMap) return map;
      return new HashMap<String, Object>(map);
    }
    else if(typeName.equals("java.util.Hashtable")) {
      if(map instanceof Hashtable) return map;
      return new Hashtable<String, Object>(map);
    }
    else if(typeName.equals("java.util.Properties")) {
      Properties properties = new Properties();
      properties.putAll(map);
      return properties;
    }
    else if(typeName.equals("java.util.concurrent.ConcurrentHashMap")) {
      ConcurrentHashMap<String, Object> concurrentHashMap = new ConcurrentHashMap<String, Object>(map);
      return concurrentHashMap;
    }
    
    return null;
  }
}
