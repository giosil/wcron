package org.dew.wcron.util;

import org.dew.wcron.api.ActivityInfo;
import org.dew.wcron.api.JobInfo;
import org.dew.wcron.persistence.Activity;
import org.dew.wcron.persistence.Job;

public 
class DataUtil 
{
  public static 
  ActivityInfo toActivityInfo(Activity activity) 
  {
    if(activity == null) return null;
    
    ActivityInfo result = new ActivityInfo();
    result.setName(activity.getName());
    result.setUri(activity.getUri());
    result.setParameters(JSONUtils.parseObject(activity.getParameters(), true));
    result.setCreatedAt(activity.getInsDate());
    
    return result;
  }
  
  public static 
  JobInfo toJobInfo(Activity activity, Job job) 
  {
    if(activity == null || job == null) return null;
    
    ActivityInfo activityInfo = toActivityInfo(activity);
    
    JobInfo result = new JobInfo();
    result.setId(job.getId());
    result.setActivity(activityInfo);
    result.setExpression(job.getExpression());
    result.setParameters(JSONUtils.parseObject(job.getParameters(), false));
    result.setLastResult(job.getLastResult());
    result.setLastError(job.getLastError());
    result.setElapsed(job.getElapsed());
    result.setCreatedAt(job.getInsDate());
    
    return result;
  }
}
