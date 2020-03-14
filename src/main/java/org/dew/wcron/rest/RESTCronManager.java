package org.dew.wcron.rest;

import java.security.Principal;

import java.util.HashMap;
import java.util.Map;

import java.util.logging.Logger;

import javax.inject.Inject;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.dew.wcron.LoggerFactory;

import org.dew.wcron.auth.WSecure;

import org.dew.wcron.model.Activity;
import org.dew.wcron.model.ICronManager;
import org.dew.wcron.model.JobInfo;

@Path("/manager")
public 
class RESTCronManager 
{
  protected static Logger logger = LoggerFactory.getLogger(RESTCronManager.class);
  
  @Context
  UriInfo uriInfo;
  
  @Context 
  HttpHeaders httpHeaders;
  
  @Context
  SecurityContext securityContext;
  
  @Inject
  protected ICronManager cronManager;
  
  @GET
  @Path("/info")
  @Produces(MediaType.APPLICATION_JSON)
  @WSecure
  public 
  Map<String,Object> info() 
  {
    Principal principal = securityContext.getUserPrincipal();
    
    logger.fine(principal + "@" + uriInfo.getPath() + " RESTManager.info()...");
    
    Map<String,Object> mapResult = new HashMap<String, Object>();
    mapResult.put("name", RESTApp.NAME);
    mapResult.put("version", RESTApp.VER);
    return mapResult;
  }
  
  @GET
  @Path("/listActivities")
  @Produces(MediaType.APPLICATION_JSON)
  @WSecure
  public 
  Activity[] listActivities()
  {
    Principal principal = securityContext.getUserPrincipal();
    
    logger.fine(principal + "@" + uriInfo.getPath() + " RESTManager.listActivities()...");
    
    return cronManager.listActivities();
  }
  
  @POST
  @Path("/addActivity")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @WSecure
  public 
  boolean addActivity(Activity activity) 
  {
    Principal principal = securityContext.getUserPrincipal();
    
    logger.fine(principal + "@" + uriInfo.getPath() + " RESTManager.addActivity(" + activity + ")...");
    
    return cronManager.addActivity(activity);
  }
  
  @GET
  @Path("/removeActivity/{activityName}")
  @Produces(MediaType.APPLICATION_JSON)
  @WSecure
  public 
  boolean removeActivity(@PathParam("activityName") String activityName) 
  {
    Principal principal = securityContext.getUserPrincipal();
    
    logger.fine(principal + "@" + uriInfo.getPath() + " RESTManager.removeActivity(" + activityName + ")...");
    
    return cronManager.removeActivity(activityName);
  }
  
  @GET
  @Path("/schedule/{activityName}/{expression}")
  @Produces(MediaType.APPLICATION_JSON)
  @WSecure
  public 
  String schedule(@PathParam("activityName") String activityName, @PathParam("expression") String expression)
  {
    Principal principal = securityContext.getUserPrincipal();
    
    logger.fine(principal + "@" + uriInfo.getPath() + " RESTManager.schedule(" + activityName + "," + expression + ")...");
    
    return cronManager.schedule(activityName, expression);
  }
  
  @POST
  @Path("/schedule/{activityName}/{expression}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @WSecure
  public 
  String schedule(@PathParam("activityName") String activityName, @PathParam("expression") String expression, Map<String, Object> parameters)
  {
    Principal principal = securityContext.getUserPrincipal();
    
    logger.fine(principal + "@" + uriInfo.getPath() + " RESTManager.schedule(" + activityName + "," + expression + "," + parameters + ")...");
    
    return cronManager.schedule(activityName, expression, parameters);
  }
  
  @GET
  @Path("/removeJob/{jobId}")
  @Produces(MediaType.APPLICATION_JSON)
  @WSecure
  public 
  boolean removeJob(@PathParam("jobId") String jobId)
  {
    Principal principal = securityContext.getUserPrincipal();
    
    logger.fine(principal + "@" + uriInfo.getPath() + " RESTManager.removeJob(" + jobId + ")...");
    
    return cronManager.removeJob(jobId);
  }
  
  @GET
  @Path("/getJob/{jobId}")
  @Produces(MediaType.APPLICATION_JSON)
  @WSecure
  public 
  JobInfo getJob(@PathParam("jobId") String jobId)
  {
    Principal principal = securityContext.getUserPrincipal();
    
    logger.fine(principal + "@" + uriInfo.getPath() + " RESTManager.getJob(" + jobId + ")...");
    
    return cronManager.getJob(jobId);
  }
  
  @GET
  @Path("/listJobs")
  @Produces(MediaType.APPLICATION_JSON)
  @WSecure
  public 
  JobInfo[] listJobs()
  {
    Principal principal = securityContext.getUserPrincipal();
    
    logger.fine(principal + "@" + uriInfo.getPath() + " RESTManager.listJobs()...");
    
    return cronManager.listJobs();
  }
}
