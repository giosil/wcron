package org.dew.wcron.web;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.URLConnection;

import java.security.Principal;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dew.wcron.auth.WAuthorization;
import org.dew.wcron.util.WCronConfig;

@WebServlet(name = "WebOutput", loadOnStartup = 0, urlPatterns = { "/out/*" })
public 
class WebOutput extends HttpServlet 
{
  private static final long serialVersionUID = -7570610930139532148L;
  
  @Override
  public
  void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException 
  {
    Principal principal = WAuthorization.checkAuthorization(request, response);
    if(principal == null) return;
    
    File file = getFile(request);
    
    if(file == null || !file.exists()) {
      sendMessage(request, response, "File not found");
      return;
    }
    
    if(file.isFile()) {
      URLConnection urlConnection = file.toURI().toURL().openConnection();
      if(urlConnection == null) {
        sendMessage(request, response, "File not available");
        return;
      }
      String mimeType = urlConnection.getContentType();
      if(mimeType == null || mimeType.length() == 0) {
        mimeType = URLConnection.guessContentTypeFromName(file.getName());
        if(mimeType == null || mimeType.length() == 0) {
          mimeType = "application/octet-stream";
        }
      }
      int length = urlConnection.getContentLength();
      if(length == 0) {
        length = (int) file.length();
      }
      response.setContentType(mimeType);
      response.setContentLength(length);
      response.addHeader("content-disposition", "attachment; filename=\"" + file.getName() + "\"");
      
      OutputStream outputStream = response.getOutputStream();
      InputStream inputStream = null;
      try {
        inputStream = urlConnection.getInputStream();
        byte[] buffer = new byte[1024];
        int n;
        while ((n = inputStream.read(buffer)) > 0) {
          outputStream.write(buffer, 0, n);
        }
        outputStream.flush();
      }
      catch(Exception ex) {
        sendMessage(request, response, "File not available");
        return;
      }
    }
    else {
      request.setAttribute("root",       getRoot(request));
      request.setAttribute("curr",       getCurrent(request));
      request.setAttribute("breadCrumb", getBreadCrumb(request));
      request.setAttribute("files",      file.listFiles());
      
      RequestDispatcher requestDispatcher = request.getRequestDispatcher("/files.jsp");
      requestDispatcher.forward(request, response);
    }
  }
  
  @Override
  public 
  void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException 
  {
    doGet(request, response);
  }
  
  @Override
  public 
  void doHead(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException 
  {
    File file = getFile(request);
    
    if(file == null || !file.exists()) {
      response.sendError(404); // Not Found
      return;
    }
    
    if(!file.isFile()) return;
    
    URLConnection urlConnection = file.toURI().toURL().openConnection();
    if(urlConnection == null) {
      response.sendError(404); // Not Found
      return;
    }
    
    String mimeType = urlConnection.getContentType();
    if(mimeType == null || mimeType.length() == 0) {
      mimeType = URLConnection.guessContentTypeFromName(file.getName());
      if(mimeType == null || mimeType.length() == 0) {
        mimeType = "application/octet-stream";
      }
    }
    int length = urlConnection.getContentLength();
    if(length == 0) {
      length = (int) file.length();
    }
    
    response.setContentType(mimeType);
    response.setContentLength(length);
    response.addHeader("content-disposition", "attachment; filename=\"" + file.getName() + "\"");
  }
  
  protected static
  void sendMessage(HttpServletRequest request, HttpServletResponse response, String message)
      throws ServletException, IOException
  {
    request.setAttribute("message", message);
    
    RequestDispatcher requestDispatcher = request.getRequestDispatcher("/files.jsp");
    requestDispatcher.forward(request, response);
  }
  
  protected static
  String getRoot(HttpServletRequest request)
  {
    String current = getCurrent(request);
    if(current == null || current.length() < 3) {
      return current;
    }
    int iFirstSep = current.indexOf('/', 1);
    if(iFirstSep < 0) return current;
    int iSecondSep = current.indexOf('/', iFirstSep + 1);
    if(iSecondSep < 0) return current;
    return current.substring(0, iSecondSep);
  }
  
  protected static
  String getCurrent(HttpServletRequest request)
  {
    String requestURI = request.getRequestURI();
    if(requestURI.startsWith("http://") || requestURI.startsWith("https://")) {
      int iStartCtx = requestURI.indexOf('/', 8);
      if(iStartCtx > 0) {
        return requestURI.substring(iStartCtx);
      }
      return "/";
    }
    if(requestURI.startsWith("/")) return requestURI;
    return "/" + requestURI;
  }
  
  protected static
  List<String> getBreadCrumb(HttpServletRequest request)
  {
    String pathInfo = request.getPathInfo();
    if(pathInfo == null || pathInfo.length() == 0) {
      return new ArrayList<String>(0);
    }
    if(pathInfo.startsWith("/")) {
      pathInfo = pathInfo.substring(1);
    }
    if(pathInfo.length() == 0) {
      return new ArrayList<String>(0);
    }
    StringTokenizer stringTokenizer = new StringTokenizer(pathInfo, "/");
    int countTokens = stringTokenizer.countTokens();
    List<String> listResult = new ArrayList<String>(countTokens);
    while(stringTokenizer.hasMoreTokens()) {
      listResult.add(stringTokenizer.nextToken());
    }
    return listResult;
  }
  
  protected static
  File getFile(HttpServletRequest request)
  {
    String pathInfo = request.getPathInfo();
    
    return WCronConfig.getOutputFile(pathInfo);
  }
}
