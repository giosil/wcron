package org.dew.wcron.model;

import java.util.Map;

import org.apache.log4j.Logger;

public 
class JobMock implements IJob
{
  protected static Logger logger = Logger.getLogger(JobMock.class);
  
  @Override
  public void init(JobInfo jobInfo) throws Exception {
    logger.debug("JobMock.init(" + jobInfo + ")");
  }

  @Override
  public Object execute(Map<String, Object> parameters) throws Exception {
    Object result = System.currentTimeMillis();
    logger.debug("JobMock.execute(" + parameters + ") -> " + result);
    return result;
  }

  @Override
  public void destroy() throws Exception {
    logger.debug("JobMock.destroy()");
  }
}
