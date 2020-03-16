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

import org.dew.wcron.api.ActivityInfo;
import org.dew.wcron.api.JobInfo;

import org.dew.wcron.util.JSONUtils;

@Entity
@Table(name="JOBS")
@NamedQueries({
  @NamedQuery(name="Job.findAll", query="SELECT j FROM Job j"),
  @NamedQuery(name="Job.findById", query="SELECT j FROM Job j WHERE j.id = :id"),
  @NamedQuery(name="Job.findByActivityName", query="SELECT j FROM Job j WHERE j.activityName = :activityName")
})
public 
class Job 
{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @Column(name="ACTIVITY", nullable = false, length=50)
  private String activityName;
  
  @Column(name="PARAMS", nullable = true, length=255)
  private String parameters;
  
  @Column(nullable = false, length=50)
  private String expression;
  
  @Column(name="LAST_RESULT", nullable = true, length=255)
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
  
  public Job()
  {
  }
  
  public Job(long id)
  {
    this.id = id;
  }
  
  public Job(JobInfo jobInfo)
  {
    this.id = jobInfo.getId();
    ActivityInfo activityInfo = jobInfo.getActivity();
    if(activityInfo != null) {
      this.activityName = activityInfo.getName();
    }
    this.parameters = JSONUtils.stringify(jobInfo.getParameters());
    if(this.parameters != null && this.parameters.length() > 255) {
      this.parameters = this.parameters.substring(0, 255);
    }
    this.expression = jobInfo.getExpression();
    this.lastResult = jobInfo.getLastResult();
    if(this.lastResult != null && this.lastResult.length() > 255) {
      this.lastResult = this.lastResult.substring(0, 255);
    }
    this.lastError = jobInfo.getLastError();
    if(this.lastError != null && this.lastError.length() > 255) {
      this.lastError = this.lastError.substring(0, 255);
    }
    this.elapsed = jobInfo.getElapsed();
    this.insDate = jobInfo.getCreatedAt();
    if(this.insDate == null) {
      this.insDate = new Date();
    }
  }
  
  public Job(ActivityInfo activityInfo, String expression, Map<String,Object> parameters)
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
    if(object instanceof Job) {
      Long oId = ((Job) object).getId();
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
