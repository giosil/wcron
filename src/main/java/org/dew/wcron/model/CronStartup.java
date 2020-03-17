package org.dew.wcron.model;

import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.dew.wcron.api.ICronManager;
import org.dew.wcron.util.LoggerFactory;

@Startup
@Singleton
public 
class CronStartup 
{
  protected static Logger logger = LoggerFactory.getLogger(CronStartup.class);
  
  @EJB
  protected ICronManager cronManager;
  
  @PostConstruct
  public void init() {
    logger.fine("CronStartup.init");
    
    cronManager.load();
  }
  
  @PreDestroy
  public void destroy() {
    logger.fine("CronStartup.destroy");
  }
  
}
