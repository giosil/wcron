package org.dew.wcron.ejb;

import java.io.Serializable;

import java.text.SimpleDateFormat;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.StringTokenizer;

import java.util.concurrent.Callable;

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

import org.dew.wcron.api.Activity;
import org.dew.wcron.api.ICronManager;
import org.dew.wcron.api.ICronTrigger;
import org.dew.wcron.api.Job;

import org.dew.wcron.util.JSONUtils;
import org.dew.wcron.util.JobUtils;
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
    
    Job job = cronManager.getJob(jobId);
    if(job == null || job.getActivity() == null) {
      logger.warning("Invalid jobId " + jobId);
      return;
    }
    
    Activity activity = job.getActivity();
    
    String uri = activity.getUri();
    if(uri == null || uri.length() == 0) {
      logger.warning("Invalid activity " + activity.getName());
      return;
    }
    
    if(job.isRunning()) {
      logger.warning("Job " + jobId + " is running");
      return;
    }
    if(job.isRequestInterrupt()) {
      logger.warning("Job " + jobId + " is managing interrupt request");
      return;
    }
    
    Date   lastExceution = new Date();
    String lastResult    = "";
    String lastError     = "";
    int    elapsed       = 0;
    long startTime = lastExceution.getTime();
    
    job.setLastResult("");
    job.setLastError("");
    job.setElapsed(0);
    job.setRunning(true);
    try {
      Callable<?> callable = JobUtils.createJobInstance(job);
      
      logger.fine("[" + jobId + "].call()...");
      Object result = callable.call();
      
      lastResult = JSONUtils.stringify(result);
      
      job.setLastResult(lastResult);
    }
    catch(Exception ex) {
      lastError = ex.toString();
      job.setLastError(lastError);
      logger.fine("[" + jobId + "].call(): " + lastError);
    }
    finally {
      elapsed = (int) (System.currentTimeMillis() - startTime);
      
      job.setElapsed(elapsed);
      job.setRunning(false);
    }
    
    logger.fine("notifyExecution(" + jobId + "," + lastExceution + "," + lastExceution + "," + lastError + "," + elapsed + ")...");
    cronManager.notifyExecution(jobId, lastExceution, lastResult, lastError, elapsed);
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
}
