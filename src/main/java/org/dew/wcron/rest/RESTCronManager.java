package org.dew.wcron.rest;

import java.security.Principal;

import java.util.HashMap;
import java.util.Map;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.util.logging.Logger;

import javax.inject.Inject;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.dew.wcron.api.Activity;
import org.dew.wcron.api.ICronManager;
import org.dew.wcron.api.Job;

import org.dew.wcron.auth.WSecure;

import org.dew.wcron.util.JobUtils;
import org.dew.wcron.util.LoggerFactory;

@Path("/manager")
public 
class RESTCronManager 
{
  protected static Logger logger = LoggerFactory.getLogger(RESTCronManager.class);
  
  @Context
  protected UriInfo uriInfo;
  
  @Context 
  protected HttpHeaders httpHeaders;
  
  @Context 
  protected HttpServletRequest httpServletRequest;
  
  @Context
  protected SecurityContext securityContext;
  
  @Inject
  protected  ICronManager cronManager;
  
  @GET
  @Path("/info")
  @Produces(MediaType.APPLICATION_JSON)
  @WSecure
  public 
  Map<String,Object> info() 
  {
    Principal principal = securityContext.getUserPrincipal();
    
    logger.fine(principal + "@" + uriInfo.getPath() + " info()...");
    
    Map<String,Object> mapResult = new HashMap<String, Object>();
    mapResult.put("name", RESTApp.NAME);
    mapResult.put("version", RESTApp.VER);
    mapResult.put("activities", cronManager.countActivities());
    mapResult.put("jobs", cronManager.countJobs());
    
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
    
    logger.fine(principal + "@" + uriInfo.getPath() + " listActivities()...");
    
    return cronManager.listActivities();
  }
  
  @GET
  @Path("/getActivityNames")
  @Produces(MediaType.APPLICATION_JSON)
  @WSecure
  public 
  String[] getActivityNames()
  {
    Principal principal = securityContext.getUserPrincipal();
    
    logger.fine(principal + "@" + uriInfo.getPath() + " getActivityNames()...");
    
    return cronManager.getActivityNames();
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
    
    logger.fine(principal + "@" + uriInfo.getPath() + " addActivity(" + activity + ")...");
    
    String activityName = activity.getName();
    if(activityName == null || activityName.length() < 2) {
      throw new WebApplicationException("Invalid activity name", 
          Response.serverError().entity("Invalid activity name").build());
    }
    
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
    
    logger.fine(principal + "@" + uriInfo.getPath() + " removeActivity(" + activityName + ")...");
    
    return cronManager.removeActivity(activityName);
  }
  
  @GET
  @Path("/schedule/{activityName}/{expression}")
  @Produces(MediaType.APPLICATION_JSON)
  @WSecure
  public 
  Long schedule(@PathParam("activityName") String activityName, @PathParam("expression") String expression)
  {
    Principal principal = securityContext.getUserPrincipal();
    
    logger.fine(principal + "@" + uriInfo.getPath() + " schedule(" + activityName + "," + expression + ")...");
    
    return cronManager.schedule(activityName, expression);
  }
  
  @POST
  @Path("/schedule/{activityName}/{expression}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @WSecure
  public 
  Long schedule(@PathParam("activityName") String activityName, @PathParam("expression") String expression, Map<String, Object> parameters)
  {
    Principal principal = securityContext.getUserPrincipal();
    
    logger.fine(principal + "@" + uriInfo.getPath() + " schedule(" + activityName + "," + expression + "," + parameters + ")...");
    
    return cronManager.schedule(activityName, expression, parameters);
  }
  
  @GET
  @Path("/removeJob/{jobId}")
  @Produces(MediaType.APPLICATION_JSON)
  @WSecure
  public 
  boolean removeJob(@PathParam("jobId") Long jobId)
  {
    Principal principal = securityContext.getUserPrincipal();
    
    logger.fine(principal + "@" + uriInfo.getPath() + " removeJob(" + jobId + ")...");
    
    return cronManager.removeJob(jobId);
  }
  
  @GET
  @Path("/getJob/{jobId}")
  @Produces(MediaType.APPLICATION_JSON)
  @WSecure
  public 
  Job getJob(@PathParam("jobId") Long jobId)
  {
    Principal principal = securityContext.getUserPrincipal();
    
    logger.fine(principal + "@" + uriInfo.getPath() + " getJob(" + jobId + ")...");
    
    Job job = cronManager.getJob(jobId);
    
    if(job == null) {
      throw new WebApplicationException("Job " + jobId + " not found", 
          Response.status(Response.Status.NOT_FOUND).entity("Job " + jobId + " not found").build());
    }
    
    return job;
  }
  
  @GET
  @Path("/listJobs")
  @Produces(MediaType.APPLICATION_JSON)
  @WSecure
  public 
  Job[] listJobs()
  {
    Principal principal = securityContext.getUserPrincipal();
    
    logger.fine(principal + "@" + uriInfo.getPath() + " listJobs()...");
    
    return cronManager.listJobs();
  }
  
  @GET
  @Path("/clean")
  @Produces(MediaType.APPLICATION_JSON)
  @WSecure
  public 
  boolean cleanOutputFolder(@Suspended final AsyncResponse asyncResponse) 
  {
    Principal principal = securityContext.getUserPrincipal();
    
    logger.fine(principal + "@" + uriInfo.getPath() + " cleanOutputFolder(" + asyncResponse + ")...");
    
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    
    executorService.execute(() -> {
      
      boolean result = JobUtils.cleanOutputFolder();
      
      asyncResponse.resume(result);
      
    });
    
    return true;
  }
}
