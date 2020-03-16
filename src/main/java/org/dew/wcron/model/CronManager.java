package org.dew.wcron.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.ejb.Local;
import javax.ejb.Stateless;

import javax.inject.Inject;

import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.dew.wcron.api.ActivityInfo;
import org.dew.wcron.api.ICronManager;
import org.dew.wcron.api.ICronTrigger;
import org.dew.wcron.api.JobInfo;

import org.dew.wcron.persistence.Activity;
import org.dew.wcron.persistence.Job;

import org.dew.wcron.util.LoggerFactory;

import static org.dew.wcron.util.DataUtil.toActivityInfo;
import static org.dew.wcron.util.DataUtil.toJobInfo;

@Stateless
@Local(ICronManager.class)
@Interceptors(AuditInterceptor.class)
public 
class CronManager implements ICronManager 
{
  private final static ConcurrentHashMap<String, ActivityInfo> activities = new ConcurrentHashMap<String, ActivityInfo>();
  private final static ConcurrentHashMap<Long, JobInfo> jobs = new ConcurrentHashMap<Long, JobInfo>();
  
  protected static Logger logger = LoggerFactory.getLogger(CronManager.class);
  
  @PersistenceContext(unitName = "wcron-unit")
  EntityManager em;
  
  @Inject
  protected ICronTrigger cronTrigger;
  
  @Override
  public Map<String, Object> load() {
    logger.fine("CronManager.load()...");
    
    Map<String, Object> mapResult = new HashMap<String, Object>();
    
    activities.clear();
    jobs.clear();
    
    cronTrigger.cancelAll();
    
    TypedQuery<Job> findJobQuery = em.createNamedQuery("Job.findByActivityName", Job.class);
    
    List<Activity> listActivity = null;
    try {
      logger.fine("CronManager.load() Activity.findAll...");
      listActivity = em.createNamedQuery("Activity.findAll", Activity.class).getResultList();  
    }
    catch(Exception ex) {
      logger.fine("CronManager.load() Activity.findAll: " + ex);
    }
    
    if(listActivity == null) {
      listActivity = new ArrayList<Activity>(0);
    }
    
    Iterator<Activity> iterator = listActivity.iterator();
    while(iterator.hasNext()) {
      Activity activity = iterator.next();
      
      String name = activity.getName();
      
      activities.put(name, toActivityInfo(activity));
      
      findJobQuery.setParameter("activityName", name);
      
      Job job = null;
      try {
        logger.fine("CronManager.load() Job.findByActivityName[" + name + "]...");
        job = findJobQuery.getSingleResult();
      }
      catch(Exception ex) {
        logger.fine("CronManager.load() Job.findByActivityName[" + name + "]: " + ex);
      }
      if(job == null) continue;
      
      long jobId = job.getId();
      
      jobs.put(jobId, toJobInfo(activity, job));
      
      boolean scheduled = cronTrigger.schedule(jobId, job.getExpression());
      
      if(!scheduled) jobs.remove(jobId);
    }
    
    mapResult.put("activities", activities.size());
    mapResult.put("jobs", jobs.size());
    
    logger.fine("CronManager.load() -> " + mapResult);
    return mapResult;
  }
  
  @Override
  public ActivityInfo[] listActivities() {
    return activities.values().stream().sorted().toArray(ActivityInfo[]::new);
  }
  
  @Override
  public int countActivities() {
    return activities.size();
  }
  
  @Override
  public boolean addActivity(ActivityInfo activity) {
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
    
    em.persist(new Activity(activity));
    
    activities.put(name, activity);
    
    return true;
  }
  
  @Override
  public boolean removeActivity(String activityName) {
    if(activityName == null || activityName.length() == 0) {
      return false;
    }
    
    em.remove(new Activity(activityName));
    
    activities.remove(activityName);
    
    return true;
  }
  
  @Override
  public long schedule(String activityName, String expression) {
    return schedule(activityName, expression, null);
  }
  
  @Override
  public long schedule(String activityName, String expression, Map<String,Object> parameters) {
    if(activityName == null || activityName.length() == 0) {
      return 0;
    }
    if(expression == null || expression.length() == 0) {
      return 0;
    }
    expression = expression.replace('_', ' ');
    
    ActivityInfo activity = activities.get(activityName);
    
    if(activity == null) return 0;
    
    Job job = new Job(activity, expression, parameters);
    
    em.persist(job);
    
    // In order to obtain job.getId()
    em.flush();
    
    long jobId = job.getId();
    
    jobs.put(jobId, new JobInfo(jobId, activity, expression, parameters));
    
    boolean scheduled = cronTrigger.schedule(jobId, expression);
    
    if(!scheduled) {
      jobs.remove(jobId);
      return 0;
    }
    
    return jobId;
  }
  
  @Override
  public boolean removeJob(long jobId) {
    boolean cancelled = cronTrigger.cancel(jobId);
    
    if(cancelled) {
      jobs.remove(jobId);
      
      em.remove(new Job(jobId));
    }
    
    return cancelled;
  }
  
  @Override
  public boolean cancelAll() {
    return cronTrigger.cancelAll();
  }
  
  @Override
  public JobInfo getJob(long jobId) {
    return jobs.get(jobId);
  }
  
  @Override
  public JobInfo[] listJobs() {
    return jobs.values().stream().sorted().toArray(JobInfo[]::new);
  }
  
  @Override
  public int countJobs() {
    return jobs.size();
  }
}
