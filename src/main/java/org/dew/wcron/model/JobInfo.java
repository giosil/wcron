package org.dew.wcron.model;

import java.io.Serializable;

public 
class JobInfo implements Serializable, Comparable<JobInfo>
{
  private static final long serialVersionUID = 7155247851232441913L;
  
  protected String id;
  protected Activity activity;
  protected String expression;
  protected boolean running;
  protected boolean requestInterrupt;
  protected String lastResult;
  protected String lastException;
  
  public JobInfo()
  {
  }
  
  public JobInfo(String id, Activity activity, String expression)
  {
    this.id = id;
    this.activity = activity;
    this.expression = expression;
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
