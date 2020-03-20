package org.dew.wcron.web;

import java.io.IOException;
import java.io.PrintWriter;

import java.security.Principal;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;

import javax.servlet.annotation.WebServlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dew.wcron.auth.WAuthorization;

import org.dew.wcron.util.JobUtils;
import org.dew.wcron.util.MockAsyncContext;

@WebServlet(name = "WebClean", loadOnStartup = 0, urlPatterns = { "/clean" }, asyncSupported = true)
public 
class WebClean extends HttpServlet 
{
  private static final long serialVersionUID = 1360832504420900067L;
  
  @Override
  protected 
  void service(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException
  {
    Principal principal = WAuthorization.checkAuthorization(request, response);
    if(principal == null) return;
    
    boolean asyncSupported = request.isAsyncSupported();
    
    PrintWriter writer = response.getWriter();
    
    final AsyncContext asyncContext = asyncSupported ? request.startAsync() : new MockAsyncContext();
    
    asyncContext.start(() -> {
      boolean result = JobUtils.cleanOutputFolder();
      
      writer.println(result);
      
      asyncContext.complete();
    });
  }
}

