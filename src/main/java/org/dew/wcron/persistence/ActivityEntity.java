package org.dew.wcron.persistence;

import java.util.Date;

import javax.persistence.*;

import org.dew.wcron.api.Activity;

import org.dew.wcron.util.JSONUtils;

@Entity(name="Activity")
@Table(name="ACTIVITIES")
@NamedQueries({
  @NamedQuery(name="Activities.findAll", query="SELECT a FROM Activity a"),
  @NamedQuery(name="Activities.findById", query="SELECT a FROM Activity a WHERE a.name = :name")
})
public 
class ActivityEntity
{
  @Id
  @Column(nullable = false, length=50)
  private String name;
  
  @Column(nullable = false, length=100)
  private String uri;
  
  @Column(name="PARAMS", nullable = true, length=4000)
  private String parameters;
  
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name="INS_DATE", nullable = false)
  private Date insDate;
  
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name="UPD_DATE", nullable = true)
  private Date updDate;
  
  public ActivityEntity()
  {
  }
  
  public ActivityEntity(String name)
  {
    this.name = name;
  }
  
  public ActivityEntity(Activity activity)
  {
    this(activity, null);
  }
  
  public ActivityEntity(Activity activity, Date updDate)
  {
    this.name = activity.getName();
    this.uri = activity.getUri();
    this.parameters = JSONUtils.stringify(activity.getParameters());
    if(this.parameters != null && this.parameters.length() > 4000) {
      this.parameters = this.parameters.substring(0, 4000);
    }
    this.insDate = activity.getCreatedAt();
    if(this.insDate == null) {
      this.insDate = new Date();
    }
    this.updDate = updDate;
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

  public String getParameters() {
    return parameters;
  }

  public void setParameters(String parameters) {
    this.parameters = parameters;
  }

  public Date getInsDate() {
    return insDate;
  }

  public void setInsDate(Date insDate) {
    this.insDate = insDate;
  }

  public Date getUpdDate() {
    return updDate;
  }

  public void setUpdDate(Date updDate) {
    this.updDate = updDate;
  }

  @Override
  public boolean equals(Object object) {
    if(object instanceof ActivityEntity) {
      String sName = ((ActivityEntity) object).getName();
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
