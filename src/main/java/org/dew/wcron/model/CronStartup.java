package org.dew.wcron.model;

import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.dew.wcron.LoggerFactory;

@Startup
@Singleton
public class CronStartup {
  
  protected static Logger logger = LoggerFactory.getLogger(CronTrigger.class);
  
  @PostConstruct
  public void init() {
    logger.fine("CronStartup.init");
  }
  
  @PreDestroy
  public void destroy() {
    logger.fine("CronStartup.destroy");
  }
  
}
