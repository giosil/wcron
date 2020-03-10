package org.dew.wcron;

import java.util.*;
import java.io.*;

import javax.servlet.http.*;
import javax.servlet.*;

import org.apache.log4j.PropertyConfigurator;

public
class Init extends HttpServlet
{
  private static final long serialVersionUID = -1685406178764891548L;

  private final static String sLOGGER_CFG = "logger.cfg";
  
  private Properties oLoggerCfg;
  private String sCheckInit;
  
  public
  void init()
    throws ServletException
  {
    try {
      InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(sLOGGER_CFG);
      if(is != null) {
        oLoggerCfg = new Properties();
        oLoggerCfg.load(is);
        changeLogFilePath(oLoggerCfg);
        PropertyConfigurator.configure(oLoggerCfg);
      }
    }
    catch (IOException ex) {
      sCheckInit = ex.toString();
      return;
    }
  }

  @SuppressWarnings("rawtypes")
  protected
  void changeLogFilePath(Properties properties)
  {
    if(properties == null) return;
    
    String sUserHome = System.getProperty("user.home");
    String sLogFilePath = sUserHome + File.separator + "log" + File.separator;
    
    try {
      File folder = new File(sLogFilePath);
      if(!folder.exists()) folder.mkdirs();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
    
    Iterator iterator = properties.keySet().iterator();
    while(iterator.hasNext()){
      String sKey   = iterator.next().toString();
      String sValue = properties.getProperty(sKey);
      if(sKey.endsWith(".File") && sValue != null) {
        if(!sValue.startsWith(".") && !sValue.startsWith("/")) {
          properties.put(sKey, sLogFilePath + sValue);
        }
      }
    }
  }

  public
  void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
  {
    response.setContentType("text/html");
    PrintWriter out = response.getWriter();
    out.println("<html>");
    out.println("<body>");
    out.println("<b>Logger initialization: " + sCheckInit + "</b>");
    out.println("</body>");
    out.println("</html>");
  }
}
