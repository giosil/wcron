package org.dew.wcron.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import java.util.concurrent.atomic.AtomicInteger;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

@Stateless
@Local(ICronManager.class)
@Interceptors(AuditInterceptor.class)
public 
class CronManager implements ICronManager 
{
  private final static ConcurrentHashMap<String, Activity> activities = new ConcurrentHashMap<String, Activity>();
  private final static ConcurrentHashMap<String, JobInfo> jobs = new ConcurrentHashMap<String, JobInfo>();
  private final static AtomicInteger atomicInteger = new AtomicInteger(0);
  
  @Inject
  protected ICronTrigger cronTrigger;
  
  @Override
  public Activity[] listActivities() {
    return activities.values().stream().sorted().toArray(Activity[]::new);
  }
  
  @Override
  public boolean addActivity(Activity activity) {
    if(activity == null) {
      return false;
    }
    
    String name = activity.getName();
    if(name == null || name.length() == 0) {
      return false;
    }
    
    String uri = activity.getUri();
    if(uri == null || uri.length() == 0) {
      return false;
    }
    
    activities.put(name, activity);
    
    return true;
  }
  
  @Override
  public boolean removeActivity(String activityName) {
    if(activityName == null || activityName.length() == 0) {
      return false;
    }
    
    activities.remove(activityName);
    
    return true;
  }
  
  @Override
  public String schedule(String activityName, String expression) {
    
    return schedule(activityName, expression, null);
  
  }
  
  @Override
  public String schedule(String activityName, String expression, Map<String,Object> parameters) {
    if(activityName == null || activityName.length() == 0) {
      return "";
    }
    if(expression == null || expression.length() == 0) {
      return "";
    }
    expression = expression.replace('_', ' ');
    
    Activity activity = activities.get(activityName);
    
    if(activity == null) return "";
    
    String jobId = nextId();
    
    jobs.put(jobId, new JobInfo(jobId, activity, expression, parameters));
    
    boolean scheduled = cronTrigger.schedule(jobId, expression);
    
    if(!scheduled) {
      jobs.remove(jobId);
      return "";
    }
    
    return jobId;
  }
  
  @Override
  public boolean removeJob(String jobId) {
    boolean removed = cronTrigger.remove(jobId);
    
    if(removed) jobs.remove(jobId);
    
    return removed;
  }

  @Override
  public JobInfo getJob(String jobId) {
    return jobs.get(jobId);
  }
  
  @Override
  public JobInfo[] listJobs() {
    return jobs.values().stream().sorted().toArray(JobInfo[]::new);
  }
  
  protected String nextId() {
    String id = String.valueOf(atomicInteger.incrementAndGet());
    int padding = 10 - id.length();
    StringBuilder sbResult = new StringBuilder();
    for(int i = 0; i < padding; i++) sbResult.append('0');
    sbResult.append(id);
    return sbResult.toString();
  }
}
