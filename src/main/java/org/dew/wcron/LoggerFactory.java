package org.dew.wcron;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

import java.util.Calendar;

import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public 
class LoggerFactory 
{
  private static final String sLOGGER_NAME = "org.dew.wcron";
  private static final Level  lLOG_LEVEL   = Level.FINER;
  private static String sLOG_DATE = "";
  
  private static
  void checkLoggingConfig()
  {
    // Configurazione esterna
    String sLoggingConfigClass = System.getProperty("java.util.logging.config.class");
    if(sLoggingConfigClass != null && sLoggingConfigClass.length() > 0) {
      System.out.println("Loggin configured by class: " + sLoggingConfigClass);
      return;
    }
    String sLoggingConfigFile = System.getProperty("java.util.logging.config.file");
    if(sLoggingConfigFile != null && sLoggingConfigFile.length() > 0) {
      System.out.println("Loggin configured by file: " + sLoggingConfigFile);
      return;
    }
    
    // Configurazione di default
    Calendar cal = Calendar.getInstance();
    
    int iYear  = cal.get(Calendar.YEAR);
    int iMonth = cal.get(Calendar.MONTH) + 1;
    int iDay   = cal.get(Calendar.DATE);
    String sYear  = String.valueOf(iYear);
    String sMonth = iMonth < 10 ? "0" + iMonth : String.valueOf(iMonth);
    String sDay   = iDay   < 10 ? "0" + iDay   : String.valueOf(iDay);
    
    String sDate  = sYear + "-" + sMonth + "-" + sDay;
    if(sDate.equals(sLOG_DATE)) return;
    sLOG_DATE = sDate;
    
    try {
      String sLogFolder  = System.getProperty("user.home") + File.separator + "log";
      
      File fileLogFolder = new File(sLogFolder);
      if(!fileLogFolder.exists()) fileLogFolder.mkdirs();
      
      Logger logger = Logger.getLogger(sLOGGER_NAME);
      
      // Close and remove previous handler
      Handler[] handlers = logger.getHandlers();
      if(handlers != null && handlers.length > 0) {
        for(int i = 0; i < handlers.length; i++) {
          Handler h = handlers[i];
          if(h instanceof FileHandler) {
            try {
              h.close();
            }
            catch(Exception ex) {
            }
          }
          logger.removeHandler(h);
        }
      }
      
      Handler handler = new FileHandler(sLogFolder + File.separator + "wcron-" + sLOG_DATE + ".log", true);
      handler.setFormatter(new LogFormatter());
      
      logger.addHandler(handler);
      logger.setLevel(lLOG_LEVEL);
      logger.setUseParentHandlers(false);
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }
  
  public static
  Logger getLogger(String name)
  {
    checkLoggingConfig();
    
    return Logger.getLogger(name);
  }
  
  public static
  Logger getLogger(Class<?> c)
  {
    checkLoggingConfig();
    
    return Logger.getLogger(c.getName());
  }
  
  static class LogFormatter extends Formatter 
  {
    public String format(LogRecord record) {
      Calendar calendar = Calendar.getInstance();
      calendar.setTimeInMillis(record.getMillis());
      int iYear    = calendar.get(java.util.Calendar.YEAR);
      int iMonth   = calendar.get(java.util.Calendar.MONTH) + 1;
      int iDay     = calendar.get(java.util.Calendar.DAY_OF_MONTH);
      int iHour    = calendar.get(Calendar.HOUR_OF_DAY);
      int iMinute  = calendar.get(Calendar.MINUTE);
      int iSecond  = calendar.get(Calendar.SECOND);
      String sMonth  = iMonth  < 10 ? "0" + iMonth  : String.valueOf(iMonth);
      String sDay    = iDay    < 10 ? "0" + iDay    : String.valueOf(iDay);
      String sHour   = iHour   < 10 ? "0" + iHour   : String.valueOf(iHour);
      String sMinute = iMinute < 10 ? "0" + iMinute : String.valueOf(iMinute);
      String sSecond = iSecond < 10 ? "0" + iSecond : String.valueOf(iSecond);
      String sCurrentDateTime = iYear + "-" + sMonth + "-" + sDay + " " + sHour + ":" + sMinute + ":" + sSecond;
      
      StringBuilder sb = new StringBuilder();
      String message = formatMessage(record);
      sb.append(sCurrentDateTime);
      sb.append(" ");
      
      String sSourceClassName = record.getSourceClassName();
      if(sSourceClassName != null && sSourceClassName.length() > 0) {
        sb.append(sSourceClassName);
        sb.append(" ");
      }
      
      String sSourceMethodName = record.getSourceMethodName();
      if(sSourceMethodName != null && sSourceMethodName.length() > 0) {
        sb.append(sSourceMethodName);
        sb.append(" ");
      }
      
      sb.append(message);
      sb.append('\n');
      if (record.getThrown() != null) {
        try {
          StringWriter sw = new StringWriter();
          PrintWriter pw = new PrintWriter(sw);
          record.getThrown().printStackTrace(pw);
          pw.close();
          sb.append(sw.toString());
        } 
        catch (Exception ex) {
        }
      }
      return sb.toString();
    }
  }
}
