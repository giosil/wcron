package org.dew.wcron.api;

import java.io.Serializable;

import java.util.Date;
import java.util.Map;

public 
class Activity implements Serializable, Comparable<Activity>
{
  private static final long serialVersionUID = -2591580725283002720L;
  
  private String name;
  private String uri;
  private Map<String,Object> parameters;
  private Date createdAt;
  
  public Activity()
  {
  }
  
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public Map<String, Object> getParameters() {
    return parameters;
  }

  public void setParameters(Map<String, Object> parameters) {
    this.parameters = parameters;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  @Override
  public int compareTo(Activity object) {
    if(object == null) return 1;
    String sName = object.getName();
    if(name == null) {
      return sName == null ? 0 : -1;
    }
    return name.compareTo(sName);
  }
  
  @Override
  public boolean equals(Object object) {
    if(object instanceof Activity) {
      String sName = ((Activity) object).getName();
      if(sName == null && name == null) return true;
      return sName != null && sName.equals(name);
    }
    return false;
  }
  
  @Override
  public int hashCode() {
    if(name == null) return 0;
    return name.hashCode();
  }
  
  @Override
  public String toString() {
    return name;
  }
}
