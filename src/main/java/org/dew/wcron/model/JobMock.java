package org.dew.wcron.model;

import java.util.Map;

import java.util.logging.Logger;

import org.dew.wcron.LoggerFactory;

public 
class JobMock implements IJob
{
  protected static Logger logger = LoggerFactory.getLogger(CronTrigger.class);
  
  @Override
  public void init(JobInfo jobInfo) throws Exception {
    logger.fine("JobMock.init(" + jobInfo + ")");
  }

  @Override
  public Object execute(Map<String, Object> parameters) throws Exception {
    Object result = System.currentTimeMillis();
    logger.fine("JobMock.execute(" + parameters + ") -> " + result);
    return result;
  }

  @Override
  public void destroy() throws Exception {
    logger.fine("JobMock.destroy()");
  }
}
