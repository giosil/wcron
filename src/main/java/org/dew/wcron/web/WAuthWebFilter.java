package org.dew.wcron.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import org.dew.wcron.auth.WHttpServletRequestWrapper;

@WebFilter(filterName = "wAuthWebFilter", urlPatterns = "/*", asyncSupported = true)
public 
class WAuthWebFilter implements Filter
{
  @Override
  public 
  void init(FilterConfig filterConfig) 
    throws ServletException
  {
  }
  
  @Override
  public 
  void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) 
    throws IOException, ServletException
  {
      HttpServletRequest httpRequest = (HttpServletRequest) request;
      
      WHttpServletRequestWrapper requestWrapper = new WHttpServletRequestWrapper(httpRequest);
      
      filterChain.doFilter(requestWrapper, response);
  }
  
  @Override
  public void destroy()
  {
  }
}
