package org.dew.wcron.api;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

public 
class JobInfo implements Serializable, Comparable<JobInfo>
{
  private static final long serialVersionUID = -941277052836157918L;
  
  private long id;
  private ActivityInfo activity;
  private String expression;
  private Map<String,Object> parameters;
  private boolean running;
  private boolean requestInterrupt;
  private String lastResult;
  private String lastError;
  private Date createdAt;
  private int elapsed;
  
  public JobInfo()
  {
  }
  
  public JobInfo(long id, ActivityInfo activity, String expression, Map<String,Object> parameters)
  {
    this.id = id;
    this.activity = activity;
    this.expression = expression;
    this.parameters = parameters;
  }
  
  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public ActivityInfo getActivity() {
    return activity;
  }

  public void setActivity(ActivityInfo activity) {
    this.activity = activity;
  }

  public String getExpression() {
    return expression;
  }

  public void setExpression(String expression) {
    this.expression = expression;
  }

  public Map<String, Object> getParameters() {
    return parameters;
  }

  public void setParameters(Map<String, Object> parameters) {
    this.parameters = parameters;
  }

  public boolean isRunning() {
    return running;
  }

  public void setRunning(boolean running) {
    this.running = running;
  }

  public boolean isRequestInterrupt() {
    return requestInterrupt;
  }

  public void setRequestInterrupt(boolean requestInterrupt) {
    this.requestInterrupt = requestInterrupt;
  }

  public String getLastResult() {
    return lastResult;
  }

  public void setLastResult(String lastResult) {
    this.lastResult = lastResult;
  }

  public String getLastError() {
    return lastError;
  }

  public void setLastError(String lastError) {
    this.lastError = lastError;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public int getElapsed() {
    return elapsed;
  }

  public void setElapsed(int elapsed) {
    this.elapsed = elapsed;
  }

  @Override
  public int compareTo(JobInfo object) {
    if(object == null) return -1;
    long objId = object.getId();
    if(id == objId) return 0;
    return id > objId ? 1 : -1;
  }
  
  @Override
  public boolean equals(Object object) {
    if(object instanceof JobInfo) {
      long objId = ((JobInfo) object).getId();
      return objId == id;
    }
    return false;
  }
  
  @Override
  public int hashCode() {
    return new Long(id).hashCode();
  }
  
  @Override
  public String toString() {
    String activityName = null;
    if(activity != null && activity.getName() != null) {
      activityName = activity.getName();
    }
    return activityName + "#" + id;
  }
}
