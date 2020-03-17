package org.dew.wcron.api;

import java.util.Map;

import java.util.concurrent.Callable;

import java.util.logging.Logger;

import org.dew.wcron.util.LoggerFactory;

public 
class JobMock implements Callable<Long> 
{
  protected static Logger logger = LoggerFactory.getLogger(JobMock.class);
  
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
    logger.fine("[" + id + "] JobMock.call() <parameters=" + parameters + "> -> " + result);
    return result;
  }
}
