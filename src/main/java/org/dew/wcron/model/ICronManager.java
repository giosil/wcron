package org.dew.wcron.model;

import java.util.Map;

import javax.ejb.Local;

@Local
public 
interface ICronManager 
{
  public Activity[] listActivities();
  
  public boolean addActivity(Activity activity);
  
  public boolean removeActivity(String activityName);
  
  public String schedule(String activityName, String expression);
  
  public String schedule(String activityName, String expression, Map<String, Object> parameters);
  
  public boolean removeJob(String jobId);
  
  public JobInfo getJob(String jobId);
  
  public JobInfo[] listJobs();
}
