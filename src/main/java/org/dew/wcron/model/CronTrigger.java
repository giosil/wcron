package org.dew.wcron.model;

import java.io.Serializable;

import java.text.SimpleDateFormat;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.annotation.Resource;

import javax.ejb.Local;
import javax.ejb.ScheduleExpression;
import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.ejb.Timer;

import javax.inject.Inject;

import org.apache.log4j.Logger;

@Singleton
@Local(ICronTrigger.class)
public 
class CronTrigger implements ICronTrigger
{
  protected static Logger logger = Logger.getLogger(CronTrigger.class);
  
  @Resource
  private TimerService timerService;
  
  @Inject
  protected ICronManager cronManager;
  
  @Override
  public boolean schedule(String jobId, String expression) {
    logger.debug("CronTrigger.schedule(" + jobId + "," + expression + ")...");
    
    if(jobId == null || jobId.length() == 0) {
      return false;
    }
    if(expression == null || expression.length() == 0) {
      return false;
    }
    
    try {
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
      
      StringTokenizer stringTokenizer = new StringTokenizer(expression);
      int countTokens = stringTokenizer.countTokens();
      int index = 0;
      if(countTokens > 2) {
        ScheduleExpression scheduleExpression = new ScheduleExpression();
        while(stringTokenizer.hasMoreTokens()) {
          String token = stringTokenizer.nextToken();
          switch (index) {
          case 0:
            scheduleExpression = scheduleExpression.second(token);
            break;
          case 1:
            scheduleExpression = scheduleExpression.minute(token);
            break;
          case 2:
            scheduleExpression = scheduleExpression.hour(token);
            break;
          case 3:
            scheduleExpression = scheduleExpression.dayOfMonth(token);
            break;
          case 4:
            scheduleExpression = scheduleExpression.dayOfWeek(token);
            break;
          case 5:
            scheduleExpression = scheduleExpression.year(token);
            break;
          }
          index++;
        }
        
        calendarTask(jobId, scheduleExpression);
      }
      else if(countTokens == 2) {
        String token0 = stringTokenizer.nextToken();
        String token1 = stringTokenizer.nextToken();
        long interval = Long.parseLong(token1);
        if(token0.indexOf('-') > 0) {
          Date timeOut = simpleDateFormat.parse(token0);
          if(timeOut == null) return false;
          
          intervalTask(jobId, timeOut, interval);
        }
        else {
          long timeOut = Long.parseLong(token0);
          
          intervalTask(jobId, timeOut, interval);
        }
      }
      else {
        if(expression.indexOf('-') > 0) {
          Date timeOut = simpleDateFormat.parse(expression);
          if(timeOut == null) return false;
          
          singleTask(jobId, timeOut);
        }
        else {
          long timeOut = Long.parseLong(expression);
          
          singleTask(jobId, timeOut);
        }
      }
      
    }
    catch(Exception ex) {
      logger.error("Exception in schedule(" + jobId + "," + expression + ")", ex);
      return false;
    }
    
    return true;
  }
  
  @Override
  public boolean remove(String jobId) {
    Collection<Timer> timers = timerService.getAllTimers();
    
    boolean found = false;
    Iterator<Timer> iterator = timers.iterator();
    while(iterator.hasNext()) {
      Timer timer = iterator.next();
      Object info = timer.getInfo();
      if(info != null && info.equals(jobId)) {
        iterator.remove();
        found = true;
      }
    }
    
    return found;
  }
  
  @Timeout
  public void timeout(Timer timer) {
    Serializable timerInfo = timer.getInfo();
    String jobId = timerInfo != null ? timerInfo.toString() : "";
    
    JobInfo jobInfo = cronManager.getJob(jobId);
    if(jobInfo == null || jobInfo.getActivity() == null) {
      logger.warn("Invalid jobId " + jobId);
      return;
    }
    
    Activity activity = jobInfo.getActivity();
    
    String uri = activity.getUri();
    if(uri == null || uri.length() == 0) {
      logger.warn("Invalid activity " + activity.getName());
      return;
    }
    
    if(jobInfo.isRunning()) {
      logger.warn("Job " + jobId + " is running");
      return;
    }
    if(jobInfo.isRequestInterrupt()) {
      logger.warn("Job " + jobId + " is managing interrupt request");
      return;
    }
    
    IJob job = createJobInstance(uri);
    if(job == null) {
      logger.warn("Invalid uri activity " + activity.getName() + ", uri=" + uri);
      return;
    }
    
    Object result = null;
    jobInfo.setLastResult("");
    jobInfo.setLastException("");
    jobInfo.setRunning(true);
    try {
      logger.debug("[" + jobId + "].init(" + jobInfo + ")...");
      job.init(jobInfo);
      
      logger.debug("[" + jobId + "].execute(" + activity.getParameters() + ")...");
      result = job.execute(activity.getParameters());
      
      jobInfo.setLastResult(result != null ? result.toString() : "null");
      jobInfo.setLastException("");
    }
    catch(Exception ex) {
      jobInfo.setLastResult("");
      jobInfo.setLastException(ex.toString());
    }
    finally {
      try {
        logger.debug("[" + jobId + "].destroy()...");
        job.destroy();
      }
      catch(Exception ex) {
        logger.warn("[" + jobId + "].destroy(): " + ex);
      }
      jobInfo.setRunning(false);
    }
  }
  
  protected void singleTask(String jobId, long timeout) {
    logger.debug("CronTrigger.singleTask(" + jobId + "," + timeout + ")...");
    
    TimerConfig timerConfig = new TimerConfig();
    timerConfig.setPersistent(false);
    timerConfig.setInfo(jobId);
    timerService.createSingleActionTimer(timeout, timerConfig);
  }
  
  protected void intervalTask(String jobId, long timeout, long interval) {
    logger.debug("CronTrigger.intervalTask(" + jobId + "," + timeout + "," + interval + ")...");
    
    TimerConfig timerConfig = new TimerConfig();
    timerConfig.setPersistent(false);
    timerConfig.setInfo(jobId);
    timerService.createIntervalTimer(timeout, interval, timerConfig);
  }
  
  protected void singleTask(String jobId, Date timeout) {
    logger.debug("CronTrigger.singleTask(" + jobId + "," + timeout + ")...");
    
    TimerConfig timerConfig = new TimerConfig();
    timerConfig.setPersistent(false);
    timerConfig.setInfo(jobId);
    timerService.createSingleActionTimer(timeout, timerConfig);
  }
  
  protected void intervalTask(String jobId, Date timeout, long interval) {
    logger.debug("CronTrigger.intervalTask(" + jobId + "," + timeout + "," + interval + ")...");
    
    TimerConfig timerConfig = new TimerConfig();
    timerConfig.setPersistent(false);
    timerConfig.setInfo(jobId);
    timerService.createIntervalTimer(timeout, interval, timerConfig);
  }
  
  protected void calendarTask(String jobId, ScheduleExpression scheduleExpression) {
    logger.debug("CronTrigger.calendarTask(" + jobId + "," + scheduleExpression + ")...");
    
    TimerConfig timerConfig = new TimerConfig();
    timerConfig.setPersistent(false);
    timerConfig.setInfo(jobId);
    timerService.createCalendarTimer(scheduleExpression, timerConfig);
  }
  
  protected IJob createJobInstance(String uri) {
    if(uri == null || uri.length() == 0) {
      return null;
    }
    if(uri.equalsIgnoreCase("mock")) {
      return new JobMock();
    }
    try {
      Class<?> clazz = Class.forName(uri);
      if(clazz != null) {
        Object result = clazz.newInstance();
        if(result instanceof IJob) {
          return (IJob) result;
        }
      }
    }
    catch(Exception ex) {
      logger.debug("CronTrigger.createJobInstance(" + uri + "): " + ex);
      return null;
    }
    return null;
  }
}
