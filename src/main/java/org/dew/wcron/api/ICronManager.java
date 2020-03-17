package org.dew.wcron.api;

import java.util.Date;
import java.util.Map;

import javax.ejb.Local;

@Local
public 
interface ICronManager 
{
  public Map<String, Object> load();
  
  public Activity[] listActivities();
  
  public int countActivities();
  
  public boolean addActivity(Activity activity);
  
  public boolean removeActivity(String activityName);
  
  public long schedule(String activityName, String expression);
  
  public long schedule(String activityName, String expression, Map<String, Object> parameters);
  
  public boolean removeJob(long jobId);
  
  public boolean cancelAll();
  
  public Job getJob(long jobId);
  
  public boolean notifyExecution(long jobId, Date lastExecution, String lastResult, String lastError, int elapsed);
  
  public Job[] listJobs();
  
  public int countJobs();
}
