package org.dew.wcron.model;

import javax.ejb.Local;

@Local
public 
interface ICronManager 
{
  public Activity[] listActivities();
  
  public boolean addActivity(Activity activity);
  
  public boolean removeActivity(String activityName);
  
  public String schedule(String activityName, String expression);
  
  public boolean removeJob(String jobId);
  
  public JobInfo getJob(String jobId);
  
  public JobInfo[] listJobs();
}
