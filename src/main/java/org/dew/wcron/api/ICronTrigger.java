package org.dew.wcron.api;

import javax.ejb.Local;

@Local
public 
interface ICronTrigger 
{
  public boolean schedule(long jobId, String expression);
  
  public boolean cancel(long jobId);
  
  public boolean cancelAll();
}
