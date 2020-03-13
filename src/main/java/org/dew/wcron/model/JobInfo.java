package org.dew.wcron.model;

import java.io.Serializable;
import java.util.Map;

public 
class JobInfo implements Serializable, Comparable<JobInfo>
{
  private static final long serialVersionUID = 431852672063546967L;
  
  private String id;
  private Activity activity;
  private String expression;
  private Map<String,Object> parameters;
  private boolean running;
  private boolean requestInterrupt;
  private String lastResult;
  private String lastException;
  
  public JobInfo()
  {
  }
  
  public JobInfo(String id, Activity activity, String expression)
  {
    this.id = id;
    this.activity = activity;
    this.expression = expression;
  }
  
  public JobInfo(String id, Activity activity, String expression, Map<String,Object> parameters)
  {
    this.id = id;
    this.activity = activity;
    this.expression = expression;
    this.parameters = parameters;
  }
  
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Activity getActivity() {
    return activity;
  }

  public void setActivity(Activity activity) {
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

  public String getLastException() {
    return lastException;
  }

  public void setLastException(String lastException) {
    this.lastException = lastException;
  }
  
  @Override
  public int compareTo(JobInfo object) {
    if(object == null) return -1;
    String sId = object.getId();
    if(id == null) {
      return sId == null ? 0 : 1;
    }
    return id.compareTo(sId);
  }
  
  @Override
  public boolean equals(Object object) {
    if(object instanceof JobInfo) {
      String sId = ((JobInfo) object).getId();
      if(sId == null && id == null) return true;
      return sId != null && sId.equals(id);
    }
    return false;
  }
  
  @Override
  public int hashCode() {
    if(id == null) return 0;
    return id.hashCode();
  }
  
  @Override
  public String toString() {
    if(activity != null && activity.getName() != null) {
      return id + ":" + activity.getName();
    }
    return id;
  }
}
