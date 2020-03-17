package org.dew.wcron.persistence;

import java.util.Date;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.dew.wcron.api.Activity;
import org.dew.wcron.api.Job;

import org.dew.wcron.util.JSONUtils;

@Entity(name="Job")
@Table(name="JOBS")
@NamedQueries({
  @NamedQuery(name="Jobs.findAll", query="SELECT j FROM Job j"),
  @NamedQuery(name="Jobs.findById", query="SELECT j FROM Job j WHERE j.id = :id"),
  @NamedQuery(name="Jobs.findByActivityName", query="SELECT j FROM Job j WHERE j.activityName = :activityName")
})
public 
class JobEntity 
{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @Column(name="ACTIVITY", nullable = false, length=50)
  private String activityName;
  
  @Column(name="PARAMS", nullable = true, length=4000)
  private String parameters;
  
  @Column(nullable = false, length=50)
  private String expression;
  
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name="LAST_EXECUTION", nullable = true)
  private Date lastExecution;
  
  @Column(name="LAST_RESULT", nullable = true, length=4000)
  private String lastResult;
  
  @Column(name="LAST_ERROR", nullable = true, length=255)
  private String lastError;
  
  @Column(nullable = false)
  private int elapsed;
  
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name="INS_DATE", nullable = false)
  private Date insDate;
  
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name="UPD_DATE", nullable = true)
  private Date updDate;
  
  public JobEntity()
  {
  }
  
  public JobEntity(long id)
  {
    this.id = id;
  }
  
  public JobEntity(Job job)
  {
    this.id = job.getId();
    Activity activityInfo = job.getActivity();
    if(activityInfo != null) {
      this.activityName = activityInfo.getName();
    }
    this.parameters = JSONUtils.stringify(job.getParameters());
    if(this.parameters != null && this.parameters.length() > 4000) {
      this.parameters = this.parameters.substring(0, 4000);
    }
    this.expression = job.getExpression();
    this.lastResult = job.getLastResult();
    if(this.lastResult != null && this.lastResult.length() > 4000) {
      this.lastResult = this.lastResult.substring(0, 4000);
    }
    this.lastError = job.getLastError();
    if(this.lastError != null && this.lastError.length() > 255) {
      this.lastError = this.lastError.substring(0, 255);
    }
    this.elapsed = job.getElapsed();
    this.insDate = job.getCreatedAt();
    if(this.insDate == null) {
      this.insDate = new Date();
    }
  }
  
  public JobEntity(Activity activityInfo, String expression, Map<String,Object> parameters)
  {
    if(activityInfo != null) {
      this.activityName = activityInfo.getName();
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

  public String getActivityName() {
    return activityName;
  }

  public void setActivityName(String activityName) {
    this.activityName = activityName;
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
    return activityName + "#" + id;
  }
}
