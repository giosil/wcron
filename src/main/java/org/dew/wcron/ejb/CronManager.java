package org.dew.wcron.ejb;

import java.util.ArrayList;
import java.util.Date;
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

import org.dew.wcron.api.Activity;
import org.dew.wcron.api.ICronManager;
import org.dew.wcron.api.ICronTrigger;
import org.dew.wcron.api.Job;

import org.dew.wcron.jpa.ActivityEntity;
import org.dew.wcron.jpa.JobEntity;
import org.dew.wcron.util.JPAUtils;
import org.dew.wcron.util.LoggerFactory;

import static org.dew.wcron.util.DataUtil.toActivity;
import static org.dew.wcron.util.DataUtil.toJob;

@Stateless
@Local(ICronManager.class)
@Interceptors(AuditInterceptor.class)
public 
class CronManager implements ICronManager 
{
  private final static ConcurrentHashMap<String, Activity> activities = new ConcurrentHashMap<String, Activity>();
  private final static ConcurrentHashMap<Long, Job> jobs = new ConcurrentHashMap<Long, Job>();
  
  protected static Logger logger = LoggerFactory.getLogger(CronManager.class);
  
  @PersistenceContext(unitName = "wcron-unit")
  EntityManager em;
  
  @Inject
  protected ICronTrigger cronTrigger;
  
  @Override
  public 
  Map<String, Object> load() 
  {
    logger.fine("load()...");
    
    Map<String, Object> mapResult = new HashMap<String, Object>();
    
    activities.clear();
    jobs.clear();
    
    cronTrigger.cancelAll();
    
    TypedQuery<JobEntity> findJobQuery = em.createNamedQuery("Jobs.findByActivityName", JobEntity.class);
    
    List<ActivityEntity> listActivity = null;
    try {
      logger.fine("load() Activities.findAll...");
      listActivity = em.createNamedQuery("Activities.findAll", ActivityEntity.class).getResultList();
    }
    catch(Exception ex) {
      logger.fine("load() Activities.findAll: " + ex);
    }
    
    if(listActivity == null) {
      listActivity = new ArrayList<ActivityEntity>(0);
    }
    
    Iterator<ActivityEntity> iterator = listActivity.iterator();
    while(iterator.hasNext()) {
      ActivityEntity activityEntity = iterator.next();
      
      String name = activityEntity.getName();
      
      activities.put(name, toActivity(activityEntity));
      
      findJobQuery.setParameter("activityName", name);
      
      List<JobEntity> listOfjobEntity = null;
      try {
        listOfjobEntity = findJobQuery.getResultList();
      }
      catch(Exception ex) {
        logger.fine("load() findJobQuery by activityName=" + name + ": " + ex);
      }
      
      if(listOfjobEntity == null || listOfjobEntity.size() == 0) {
        continue;
      }
      
      listOfjobEntity.forEach(jobEntity -> {
        long jobId = jobEntity.getId();
        
        jobs.put(jobId, toJob(activityEntity, jobEntity));
        
        boolean scheduled = cronTrigger.schedule(jobId, jobEntity.getExpression());
        
        if(!scheduled) jobs.remove(jobId);
      });
    }
    
    mapResult.put("activities", activities.size());
    mapResult.put("jobs", jobs.size());
    
    logger.fine("load() -> " + mapResult);
    return mapResult;
  }
  
  @Override
  public 
  Activity[] listActivities() 
  {
    return activities.values().stream().sorted().toArray(Activity[]::new);
  }
  
  @Override
  public 
  String[] getActivityNames()
  {
    String[] result = null;
    try {
      result = JPAUtils.readArrayOfString(em, "SELECT NAME FROM ACTIVITIES ORDER BY NAME");
    }
    catch(Exception ex) {
      logger.severe("getActivityNames(): " + ex);
    }
    if(result == null) result = new String[0];
    return result;
  }
  
  @Override
  public 
  int countActivities() 
  {
    return activities.size();
  }
  
  @Override
  public 
  boolean addActivity(Activity activity) 
  {
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
    
    boolean exists = false;
    try {
      ActivityEntity activityEntity = em.find(ActivityEntity.class, name);
      exists = activityEntity != null;
      if(exists) {
        activity.setCreatedAt(activityEntity.getInsDate());
      }
      else {
        activity.setCreatedAt(new Date());
      }
    }
    catch(Exception ex) {
      logger.fine("addActivity(" + activity + ") exists=" + exists + " em.find: " + ex);
    }
    
    boolean result = false;
    try {
      if(exists) {
        em.merge(new ActivityEntity(activity, new Date()));
      }
      else {
        em.persist(new ActivityEntity(activity));
      }
      result = true;
    }
    catch(Exception ex) {
      logger.fine("addActivity(" + activity + ") failed: " + ex);
    }
    
    if(result) {
      activities.put(name, activity);
    }
    
    return result;
  }
  
  @Override
  public 
  boolean removeActivity(String activityName) 
  {
    if(activityName == null || activityName.length() == 0) {
      return false;
    }
    
    boolean result = false;
    try {
      TypedQuery<JobEntity> findJobQuery = em.createNamedQuery("Jobs.findByActivityName", JobEntity.class);
      
      findJobQuery.setParameter("activityName", activityName);
      
      List<JobEntity> listOfjobEntity = findJobQuery.getResultList();
      
      if(listOfjobEntity != null) {
        
        long countNotRemoved = listOfjobEntity.stream().filter(j -> !removeJob(j.getId())).count();
        
        if(countNotRemoved != 0) {
          return false;
        }
        
      }
      
      ActivityEntity activityEntity = em.find(ActivityEntity.class, activityName);
      
      em.remove(activityEntity);
      
      result = true;
    }
    catch(Exception ex) {
      logger.severe("Exception in removeActivity(" + activityName + "): " + ex);
    }
    
    activities.remove(activityName);
    
    return result;
  }
  
  @Override
  public 
  long schedule(String activityName, String expression) 
  {
    return schedule(activityName, expression, null);
  }
  
  @Override
  public 
  long schedule(String activityName, String expression, Map<String,Object> parameters) 
  {
    if(activityName == null || activityName.length() == 0) {
      return 0;
    }
    if(expression == null || expression.length() == 0) {
      return 0;
    }
    expression = expression.replace('_', ' ');
    
    Activity activity = activities.get(activityName);
    
    if(activity == null) return 0;
    
    JobEntity job = new JobEntity(activity, expression, parameters);
    
    em.persist(job);
    
    // In order to obtain job.getId()
    em.flush();
    
    long jobId = job.getId();
    
    jobs.put(jobId, new Job(jobId, activity, expression, parameters));
    
    boolean scheduled = cronTrigger.schedule(jobId, expression);
    
    if(!scheduled) {
      jobs.remove(jobId);
      return 0;
    }
    
    return jobId;
  }
  
  @Override
  public 
  boolean removeJob(long jobId) 
  {
    boolean cancelled = cronTrigger.cancel(jobId);
    
    if(cancelled) {
      jobs.remove(jobId);
      
      try {
        JobEntity jobEntity = em.find(JobEntity.class, jobId);
        
        if(jobEntity != null) em.remove(jobEntity);
      }
      catch(Exception ex) {
        logger.severe("Exception in removeJob(" + jobId + "): " + ex);
      }
    }
    
    return cancelled;
  }
  
  @Override
  public 
  boolean cancelAll() 
  {
    return cronTrigger.cancelAll();
  }
  
  @Override
  public 
  Job getJob(long jobId) 
  {
    return jobs.get(jobId);
  }
  
  @Override
  public 
  boolean notifyExecution(long jobId, Date lastExecution, String lastResult, String lastError, int elapsed) 
  {
    boolean result = false;
    
    JobEntity jobEntity = null;
    try {
      jobEntity = em.find(JobEntity.class, jobId);
    }
    catch(Exception ex) {
      logger.severe("Exception in notifyExecution(" + jobId + "): " + ex);
    }
    
    if(jobEntity == null) return result;
    
    if(lastResult != null && lastResult.length() > 4000) {
      lastResult = lastResult.substring(0, 4000);
    }
    if(lastError != null && lastError.length() > 255) {
      lastResult = lastError.substring(0, 255);
    }
    if(lastExecution == null) {
      lastExecution = new Date();
    }
    
    jobEntity.setLastExecution(lastExecution);
    jobEntity.setLastResult(lastResult);
    jobEntity.setLastError(lastError);
    jobEntity.setElapsed(elapsed);
    jobEntity.setUpdDate(new Date());
    
    return result;
  }
  
  @Override
  public 
  Job[] listJobs() 
  {
    return jobs.values().stream().sorted().toArray(Job[]::new);
  }
  
  @Override
  public 
  int countJobs() 
  {
    return jobs.size();
  }
}
