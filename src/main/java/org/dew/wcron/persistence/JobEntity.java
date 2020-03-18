package org.dew.wcron.persistence;

import java.util.Date;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.dew.wcron.api.Activity;

import org.dew.wcron.util.JSONUtils;

@Entity(name="Job")
@Table(name="JOBS")
@NamedQueries({
  @NamedQuery(name="Jobs.findAll", query="SELECT j FROM Job j"),
  @NamedQuery(name="Jobs.findById", query="SELECT j FROM Job j WHERE j.id = :id"),
  @NamedQuery(name="Jobs.findByActivityName", query="SELECT j FROM Job j WHERE j.activity.name = :activityName")
})
public 
class JobEntity 
{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "ACTIVITY")
  private ActivityEntity activity;
  
  @Column(name = "PARAMS", nullable = true, length = 4000)
  private String parameters;
  
  @Column(nullable = false, length = 50)
  private String expression;
  
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "LAST_EXECUTION", nullable = true)
  private Date lastExecution;
  
  @Column(name = "LAST_RESULT", nullable = true, length = 4000)
  private String lastResult;
  
  @Column(name = "LAST_ERROR", nullable = true, length = 255)
  private String lastError;
  
  @Column(nullable = false)
  private int elapsed;
  
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "INS_DATE", nullable = false)
  private Date insDate;
  
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "UPD_DATE", nullable = true)
  private Date updDate;
  
  public JobEntity()
  {
  }
  
  public JobEntity(long id)
  {
    this.id = id;
  }
  
  public JobEntity(Activity activity, String expression, Map<String,Object> parameters)
  {
    if(activity != null) {
      this.activity = new ActivityEntity(activity);
    }
    if(parameters != null) {
      this.parameters = JSONUtils.stringify(parameters);
    }
    if(this.parameters != null && this.parameters.length() > 255) {
      this.parameters = this.parameters.substring(0, 255);
    }
    this.expression = expression;
    this.insDate = new Date();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public ActivityEntity getActivity() {
    return activity;
  }

  public void setActivity(ActivityEntity activity) {
    this.activity = activity;
  }

  public String getParameters() {
    return parameters;
  }

  public void setParameters(String parameters) {
    this.parameters = parameters;
  }

  public String getExpression() {
    return expression;
  }

  public void setExpression(String expression) {
    this.expression = expression;
  }

  public Date getLastExecution() {
    return lastExecution;
  }

  public void setLastExecution(Date lastExecution) {
    this.lastExecution = lastExecution;
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

  public int getElapsed() {
    return elapsed;
  }

  public void setElapsed(int elapsed) {
    this.elapsed = elapsed;
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
    if(object instanceof JobEntity) {
      Long oId = ((JobEntity) object).getId();
      if(oId == null && id == null) return true;
      return oId != null && oId.equals(id);
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
    String activityName = null;
    if(activity != null) {
      activityName = activity.getName();
    }
    return activityName + "#" + id;
  }
}
