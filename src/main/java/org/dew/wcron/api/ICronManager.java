package org.dew.wcron.api;

import java.util.Map;

import javax.ejb.Local;

@Local
public 
interface ICronManager 
{
  public Map<String, Object> load();
  
  public ActivityInfo[] listActivities();
  
  public int countActivities();
  
  public boolean addActivity(ActivityInfo activity);
  
  public boolean removeActivity(String activityName);
  
  public long schedule(String activityName, String expression);
  
  public long schedule(String activityName, String expression, Map<String, Object> parameters);
  
  public boolean removeJob(long jobId);
  
  public boolean cancelAll();
  
  public JobInfo getJob(long jobId);
  
  public JobInfo[] listJobs();
  
  public int countJobs();
}
