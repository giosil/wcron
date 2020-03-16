package org.dew.wcron.model;

import java.io.Serializable;

import java.text.SimpleDateFormat;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import javax.annotation.Resource;

import javax.ejb.Local;
import javax.ejb.ScheduleExpression;
import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.ejb.Timer;

import javax.inject.Inject;

import org.dew.wcron.api.ActivityInfo;
import org.dew.wcron.api.ICronManager;
import org.dew.wcron.api.ICronTrigger;
import org.dew.wcron.api.IJob;
import org.dew.wcron.api.JobInfo;
import org.dew.wcron.api.JobMock;
import org.dew.wcron.util.JSONUtils;
import org.dew.wcron.util.LoggerFactory;

@Singleton
@Local(ICronTrigger.class)
public 
class CronTrigger implements ICronTrigger
{
  protected static Logger logger = LoggerFactory.getLogger(CronTrigger.class);
  
  @Resource
  private TimerService timerService;
  
  @Inject
  protected ICronManager cronManager;
  
  @Override
  public boolean schedule(long jobId, String expression) {
    logger.fine("CronTrigger.schedule(" + jobId + "," + expression + ")...");
    
    if(expression == null || expression.length() == 0) {
      return false;
    }
    
    boolean result = false;
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
        
        calendarTask(String.valueOf(jobId), scheduleExpression);
      }
      else if(countTokens == 2) {
        String token0 = stringTokenizer.nextToken();
        String token1 = stringTokenizer.nextToken();
        long interval = Long.parseLong(token1);
        if(token0.indexOf('-') > 0) {
          Date timeOut = simpleDateFormat.parse(token0);
          if(timeOut == null) return false;
          
          intervalTask(String.valueOf(jobId), timeOut, interval);
        }
        else {
          long timeOut = Long.parseLong(token0);
          
          intervalTask(String.valueOf(jobId), timeOut, interval);
        }
      }
      else {
        if(expression.indexOf('-') > 0) {
          Date timeOut = simpleDateFormat.parse(expression);
          if(timeOut == null) return false;
          
          singleTask(String.valueOf(jobId), timeOut);
        }
        else {
          long timeOut = Long.parseLong(expression);
          
          singleTask(String.valueOf(jobId), timeOut);
        }
      }
      
      result = true;
    }
    catch(Exception ex) {
      logger.severe("Exception in CronTrigger.schedule(" + jobId + "," + expression + "): " + ex);
    }
    
    logger.fine("CronTrigger.schedule(" + jobId + "," + expression + ") -> " + result);
    return result;
  }
  
  @Override
  public boolean cancel(long jobId) {
    logger.fine("CronTrigger.cancel(" + jobId + ")...");
    
    String sJobId = String.valueOf(jobId);
    
    Collection<Timer> timers = timerService.getAllTimers();
    
    boolean found = false;
    Iterator<Timer> iterator = timers.iterator();
    while(iterator.hasNext()) {
      Timer timer = iterator.next();
      
      Object info = timer.getInfo();
      if(info != null && info.equals(sJobId)) {
        timer.cancel();
        found = true;
      }
    }
    
    logger.fine("CronTrigger.cancel(" + jobId + ") -> " + found);
    return found;
  }
  
  @Override
  public boolean cancelAll() {
    logger.fine("CronTrigger.cancelAll()...");
    
    boolean result = false;
    try {
      Collection<Timer> timers = timerService.getAllTimers();
      Iterator<Timer> iterator = timers.iterator();
      while(iterator.hasNext()) {
        Timer timer = iterator.next();
        timer.cancel();
      }
      
      result = true;
    }
    catch(Exception ex) {
      logger.severe("Exception in CronTrigger.cancelAll(): " + ex);
    }
    
    logger.fine("CronTrigger.cancelAll() -> " + result);
    return true;
  }
  
  @Timeout
  public void timeout(Timer timer) {
    Serializable timerInfo = timer.getInfo();
    String sJobId = timerInfo != null ? timerInfo.toString() : "";
    
    long jobId = 0l;
    try {
      jobId = Long.parseLong(sJobId);
    }
    catch(Exception ex) {
    }
    
    JobInfo jobInfo = cronManager.getJob(jobId);
    if(jobInfo == null || jobInfo.getActivity() == null) {
      logger.warning("Invalid jobId " + jobId);
      return;
    }
    
    ActivityInfo activity = jobInfo.getActivity();
    
    String uri = activity.getUri();
    if(uri == null || uri.length() == 0) {
      logger.warning("Invalid activity " + activity.getName());
      return;
    }
    
    if(jobInfo.isRunning()) {
      logger.warning("Job " + jobId + " is running");
      return;
    }
    if(jobInfo.isRequestInterrupt()) {
      logger.warning("Job " + jobId + " is managing interrupt request");
      return;
    }
    
    IJob job = createJobInstance(uri);
    if(job == null) {
      logger.warning("Invalid uri activity " + activity.getName() + ", uri=" + uri);
      return;
    }
    
    Object result = null;
    jobInfo.setLastResult("");
    jobInfo.setLastError("");
    jobInfo.setRunning(true);
    try {
      logger.fine("[" + jobId + "].init(" + jobInfo + ")...");
      job.init(jobInfo);
      
      // Execution parameters
      Map<String,Object> parameters = new HashMap<String, Object>();
      
      // Activity parameters
      Map<String,Object> actParameters = activity.getParameters();
      if(actParameters != null && !actParameters.isEmpty()) {
        parameters.putAll(actParameters);
      }
      // Job parameters
      Map<String,Object> jobParameters = jobInfo.getParameters();
      if(jobParameters != null && !jobParameters.isEmpty()) {
        parameters.putAll(jobParameters);
      }
      
      logger.fine("[" + jobId + "].execute(" + parameters + ")...");
      result = job.execute(parameters);
      
      jobInfo.setLastResult(JSONUtils.stringify(result));
      jobInfo.setLastError("");
    }
    catch(Exception ex) {
      jobInfo.setLastResult("");
      jobInfo.setLastError(ex.toString());
    }
    finally {
      try {
        logger.fine("[" + jobId + "].destroy()...");
        job.destroy();
      }
      catch(Exception ex) {
        logger.warning("[" + jobId + "].destroy(): " + ex);
      }
      jobInfo.setRunning(false);
    }
  }
  
  protected void singleTask(String jobId, long timeout) {
    logger.fine("CronTrigger.singleTask(" + jobId + "," + timeout + ")...");
    
    TimerConfig timerConfig = new TimerConfig();
    timerConfig.setPersistent(false);
    timerConfig.setInfo(jobId);
    timerService.createSingleActionTimer(timeout, timerConfig);
  }
  
  protected void intervalTask(String jobId, long timeout, long interval) {
    logger.fine("CronTrigger.intervalTask(" + jobId + "," + timeout + "," + interval + ")...");
    
    TimerConfig timerConfig = new TimerConfig();
    timerConfig.setPersistent(false);
    timerConfig.setInfo(jobId);
    timerService.createIntervalTimer(timeout, interval, timerConfig);
  }
  
  protected void singleTask(String jobId, Date timeout) {
    logger.fine("CronTrigger.singleTask(" + jobId + "," + timeout + ")...");
    
    TimerConfig timerConfig = new TimerConfig();
    timerConfig.setPersistent(false);
    timerConfig.setInfo(jobId);
    timerService.createSingleActionTimer(timeout, timerConfig);
  }
  
  protected void intervalTask(String jobId, Date timeout, long interval) {
    logger.fine("CronTrigger.intervalTask(" + jobId + "," + timeout + "," + interval + ")...");
    
    TimerConfig timerConfig = new TimerConfig();
    timerConfig.setPersistent(false);
    timerConfig.setInfo(jobId);
    timerService.createIntervalTimer(timeout, interval, timerConfig);
  }
  
  protected void calendarTask(String jobId, ScheduleExpression scheduleExpression) {
    logger.fine("CronTrigger.calendarTask(" + jobId + "," + scheduleExpression + ")...");
    
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
      logger.severe("CronTrigger.createJobInstance(" + uri + "): " + ex);
      return null;
    }
    return null;
  }
}
