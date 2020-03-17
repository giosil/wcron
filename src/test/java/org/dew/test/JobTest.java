package org.dew.test;

import java.util.Map;

import java.util.concurrent.Callable;

public 
class JobTest implements Callable<Long> 
{
  protected Map<String,Object> parameters = null;
  protected long id;
  
  public void setId(long id) {
    this.id = id;
  }
  
  public void setParameters(Map<String, Object> parameters) {
    this.parameters = parameters;
  }
  
  @Override
  public Long call() throws Exception {
    long result = System.currentTimeMillis();
    
    System.out.println("[" + id + "] JobMock.call() <parameters=" + parameters + "> -> " + result);
    
    return result;
  }
}
