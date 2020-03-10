package org.dew.wcron.model;

import javax.ejb.Local;

@Local
public 
interface ICronTrigger 
{
  public boolean schedule(String jobId, String expression);
  
  public boolean remove(String jobId);
}
