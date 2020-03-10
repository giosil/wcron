package org.dew.wcron.rest;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;

import org.dew.wcron.model.Activity;
import org.dew.wcron.model.ICronManager;
import org.dew.wcron.model.JobInfo;

@Path("/manager")
public 
class RESTCronManager 
{
  protected static Logger logger = Logger.getLogger(RESTCronManager.class);
  
  @Context
  protected UriInfo context;
  
  @Inject
  protected ICronManager cronManager;
  
  @GET
  @Path("/info")
  @Produces(MediaType.APPLICATION_JSON)
  public 
  Map<String,Object> info() 
  {
    logger.debug("RESTManager.info()...");
    
    Map<String,Object> mapResult = new HashMap<String, Object>();
    mapResult.put("name", RESTApp.NAME);
    mapResult.put("version", RESTApp.VER);
    return mapResult;
  }
  
  @GET
  @Path("/listActivities")
  @Produces(MediaType.APPLICATION_JSON)
  public 
  Activity[] listActivities() 
  {
    logger.debug("RESTManager.listActivities()...");
    
    return cronManager.listActivities();
  }
  
  @POST
  @Path("/addActivity")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public 
  boolean addActivity(Activity activity) 
  {
    logger.debug("RESTManager.addActivity(" + activity + ")...");
    
    return cronManager.addActivity(activity);
  }
  
  @GET
  @Path("/removeActivity/{activityName}")
  @Produces(MediaType.APPLICATION_JSON)
  public 
  boolean removeActivity(@PathParam("activityName") String activityName) 
  {
    logger.debug("RESTManager.removeActivity(" + activityName + ")...");
    
    return cronManager.removeActivity(activityName);
  }
  
  @GET
  @Path("/schedule/{activityName}/{expression}")
  @Produces(MediaType.APPLICATION_JSON)
  public 
  String schedule(@PathParam("activityName") String activityName, @PathParam("expression") String expression)
  {
    logger.debug("RESTManager.schedule(" + activityName + "," + expression + ")...");
    
    return cronManager.schedule(activityName, expression);
  }
  
  @GET
  @Path("/removeJob/{jobId}")
  @Produces(MediaType.APPLICATION_JSON)
  public 
  boolean removeJob(@PathParam("jobId") String jobId)
  {
    logger.debug("RESTManager.removeJob(" + jobId + ")...");
    
    return cronManager.removeJob(jobId);
  }
  
  @GET
  @Path("/getJob/{jobId}")
  @Produces(MediaType.APPLICATION_JSON)
  public 
  JobInfo getJob(@PathParam("jobId") String jobId)
  {
    logger.debug("RESTManager.getJob(" + jobId + ")...");
    
    return cronManager.getJob(jobId);
  }
  
  @GET
  @Path("/listJobs")
  @Produces(MediaType.APPLICATION_JSON)
  public 
  JobInfo[] listJobs()
  {
    logger.debug("RESTManager.listJobs()...");
    
    return cronManager.listJobs();
  }
}
