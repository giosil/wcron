package org.dew.wcron.util;

import java.io.File;
import java.io.FileInputStream;
import java.net.URLDecoder;
import java.util.Properties;
import java.util.logging.Logger;

public 
class WCronConfig 
{
  protected static Logger logger = LoggerFactory.getLogger(WCronConfig.class);
  
  private static Properties config = new Properties();
  
  static {
    reload();
  }
  
  public static
  File getClassFolder()
  {
    String path = getProperty("wcron.classes");
    if(path == null || path.length() < 2) {
      path = System.getProperty("user.home") + File.separator + "wcron" + File.separator + "classes";
    }
    File result = new File(path);
    if(!result.exists()) result.mkdirs();
    return result;
  }
  
  public static
  File getClassFile(String pathInfo)
  {
    if(pathInfo == null || pathInfo.length() == 0) {
      return getClassFolder();
    }
    
    String path = getProperty("wcron.classes");
    if(path == null || path.length() < 2) {
      path = System.getProperty("user.home") + File.separator + "wcron" + File.separator + "classes";
    }
    
    if(pathInfo != null && pathInfo.length() > 0) {
      char c0 = pathInfo.charAt(0);
      if(c0 != '/' && c0 != '\\') path += File.separator;
      try {
        path += URLDecoder.decode(pathInfo, "UTF-8");
      }
      catch(Exception ex) {
        path += pathInfo;
      }
    }
    
    File result = new File(path);
    if(!result.exists()) return null;
    return result;
  }
  
  public static
  File getOutputFolder()
  {
    String path = getProperty("wcron.out");
    if(path == null || path.length() < 2) {
      path = System.getProperty("user.home") + File.separator + "wcron" + File.separator + "out";
    }
    File result = new File(path);
    if(!result.exists()) result.mkdirs();
    return result;
  }
  
  public static
  File getOutputFolder(long jobId)
  {
    String path = getProperty("wcron.out");
    if(path == null || path.length() < 2) {
      path = System.getProperty("user.home") + File.separator + "wcron" + File.separator + "out" + File.separator + jobId;
    }
    File result = new File(path);
    if(!result.exists()) result.mkdirs();
    return result;
  }
  
  public static
  File getOutputFile(String pathInfo)
  {
    if(pathInfo == null || pathInfo.length() == 0) {
      return getOutputFolder();
    }
    
    String path = getProperty("wcron.out");
    if(path == null || path.length() < 2) {
      path = System.getProperty("user.home") + File.separator + "wcron" + File.separator + "out";
    }
    
    if(pathInfo != null && pathInfo.length() > 0) {
      char c0 = pathInfo.charAt(0);
      if(c0 != '/' && c0 != '\\') path += File.separator;
      try {
        path += URLDecoder.decode(pathInfo, "UTF-8");
      }
      catch(Exception ex) {
        path += pathInfo;
      }
    }
    
    File result = new File(path);
    if(!result.exists()) return null;
    return result;
  }
  
  public static
  String getProperty(String key)
  {
    return config.getProperty(key);
  }
  
  public static
  String getProperty(String key, String defaultValue)
  {
    return config.getProperty(key, defaultValue);
  }
  
  public static
  int getIntProperty(String key)
  {
    String value = config.getProperty(key);
    if(value == null) return 0;
    value = value.trim();
    if(value.length() == 0) return 0;
    int result = 0;
    try {
      result = Integer.parseInt(value);
    }
    catch(Exception ex) {
      logger.severe("Exception in WCronConfig.getIntProperty(" + key + "): " + ex);
    }
    return result;
  }
  
  public static
  int getIntProperty(String key, int defaultValue)
  {
    String value = config.getProperty(key);
    if(value == null) return defaultValue;
    value = value.trim();
    if(value.length() == 0) return defaultValue;
    int result = defaultValue;
    try {
      result = Integer.parseInt(value);
    }
    catch(Exception ex) {
      logger.severe("Exception in WCronConfig.getIntProperty(" + key + "," + defaultValue + "): " + ex);
    }
    return result;
  }
  
  public static
  boolean getBooleanProperty(String key)
  {
    String value = config.getProperty(key);
    if(value == null) return false;
    value = value.trim();
    if(value.length() == 0) return false;
    char c0 = value.charAt(0);
    return "Tt1YySsJj".indexOf(c0) >= 0;
  }
  
  public static
  boolean getBooleanProperty(String key, boolean defaultValue)
  {
    String value = config.getProperty(key);
    if(value == null) return defaultValue;
    value = value.trim();
    if(value.length() == 0) return defaultValue;
    char c0 = value.charAt(0);
    return "Tt1YySsJj".indexOf(c0) >= 0;
  }
  
  public static 
  boolean reload() 
  {
    String filePath = System.getProperty("user.home") + File.separator + "cfg" + File.separator + "wcron.cfg";
    File configFile = new File(filePath);
    if(!configFile.exists()) {
      filePath = System.getProperty("user.home") + File.separator + "wcron" + File.separator + "wcron.cfg";
      configFile = new File(filePath);
      if(!configFile.exists()) {
        return false;
      }
    }
    FileInputStream fileInputStream = null;
    try {
      fileInputStream = new FileInputStream(configFile);
      
      config = new Properties();
      config.load(fileInputStream);
    }
    catch(Exception ex) {
      logger.severe("Exception in WCronConfig.reload() [" + configFile + "]: " + ex);
      return false;
    }
    finally {
      if(fileInputStream != null) try { fileInputStream.close(); } catch(Exception ignore) {}
    }
    return true;
  }
}
