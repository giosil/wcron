package org.dew.wcron.api;

import java.util.Map;

public 
interface IJob 
{
  public void init(JobInfo jobInfo) throws Exception;
  
  public Object execute(Map<String,Object> parameters) throws Exception;
  
  public void destroy() throws Exception;
}
