package org.dew.wcron.util;

import org.dew.wcron.api.Activity;
import org.dew.wcron.api.Job;
import org.dew.wcron.persistence.ActivityEntity;
import org.dew.wcron.persistence.JobEntity;

public 
class DataUtil 
{
  public static 
  Activity toActivity(ActivityEntity activityEntity) 
  {
    if(activityEntity == null) return null;
    
    Activity result = new Activity();
    result.setName(activityEntity.getName());
    result.setUri(activityEntity.getUri());
    result.setParameters(JSONUtils.parseObject(activityEntity.getParameters(), true));
    result.setCreatedAt(activityEntity.getInsDate());
    
    return result;
  }
  
  public static 
  Job toJob(ActivityEntity activityEntity, JobEntity jobEntity) 
  {
    if(activityEntity == null || jobEntity == null) return null;
    
    Activity activity = toActivity(activityEntity);
    
    Job result = new Job();
    result.setId(jobEntity.getId());
    result.setActivity(activity);
    result.setExpression(jobEntity.getExpression());
    result.setParameters(JSONUtils.parseObject(jobEntity.getParameters(), false));
    result.setLastResult(jobEntity.getLastResult());
    result.setLastError(jobEntity.getLastError());
    result.setElapsed(jobEntity.getElapsed());
    result.setCreatedAt(jobEntity.getInsDate());
    
    return result;
  }
}
