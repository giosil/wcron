package org.dew.wcron.util;

import java.lang.reflect.Array;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dew.wcron.api.Activity;
import org.dew.wcron.api.Job;

import org.dew.wcron.jpa.ActivityEntity;
import org.dew.wcron.jpa.JobEntity;

public 
class DataUtil 
{
  public static long STARTUP_TIME = System.currentTimeMillis();
  
  public static 
  Activity toActivity(ActivityEntity activityEntity) 
  {
    if(activityEntity == null) return null;
    
    Activity result = new Activity();
    result.setName(activityEntity.getName());
    result.setUri(activityEntity.getUri());
    result.setParameters(JSONUtils.parseObject(activityEntity.getParameters(), true));
    result.setCreatedAt(activityEntity.getInsDate());
    return result;
  }
  
  public static 
  Job toJob(ActivityEntity activityEntity, JobEntity jobEntity) 
  {
    if(activityEntity == null || jobEntity == null) return null;
    
    Activity activity = toActivity(activityEntity);
    
    Job result = new Job();
    result.setId(jobEntity.getId());
    result.setActivity(activity);
    result.setExpression(jobEntity.getExpression());
    result.setParameters(JSONUtils.parseObject(jobEntity.getParameters(), false));
    result.setLastExecution(jobEntity.getLastExecution());
    result.setLastResult(jobEntity.getLastResult());
    result.setLastError(jobEntity.getLastError());
    result.setElapsed(jobEntity.getElapsed());
    result.setCreatedAt(jobEntity.getInsDate());
    return result;
  }
  
  public static
  String repeat(String text, int times, String zeroTimesValue)
  {
    if(times <= 0) return zeroTimesValue;
    if(times == 1) return text;
    StringBuilder stringBuilder = new StringBuilder();
    for(int i = 0; i < times; i++) {
      stringBuilder.append(text);
    }
    return stringBuilder.toString();
  }
  
  @SuppressWarnings("unchecked")
  public static <T> 
  T[] expectArray(Object o, Class<T> itemClass)
  {
    if(o == null) {
      return null;
    }
    if(!o.getClass().isArray()) {
      return null;
    }
    int length = Array.getLength(o);
    if(length == 0) {
      return (T[]) o;
    }
    Object o0 = Array.get(o, 0);
    if(o0 == null) {
      return (T[]) o;
    }
    if(itemClass.isInstance(o0)) {
      return (T[]) o;
    }
    return null;
  }
  
  public static <T> 
  List<T> expectList(Object o, Class<T> itemClass)
  {
    return expectList(o, itemClass, false);
  }
  
  @SuppressWarnings("unchecked")
  public static <T> 
  List<T> expectList(Object o, Class<T> itemClass, boolean emptyListDefault)
  {
    if(o == null) {
      if(emptyListDefault) {
        return new ArrayList<T>();
      }
      return null;
    }
    if(!(o instanceof List)) {
      if(itemClass != null && itemClass.isInstance(o)) {
        List<T> listResult = new ArrayList<T>();
        listResult.add((T) o);
        return listResult;
      }
      if(emptyListDefault) {
        return new ArrayList<T>();
      }
      return null;
    }
    int size = ((List<?>) o).size();
    if(size == 0) {
      return (List<T>) o;
    }
    Object item0 = ((List<?>) o).get(0);
    if(item0 == null) {
      return (List<T>) o;
    }
    if(itemClass == null) {
      return (List<T>) o;
    }
    if(itemClass.isInstance(item0)) {
      return (List<T>) o;
    }
    if(emptyListDefault) {
      return new ArrayList<T>();
    }
    return null;
  }
  
  public static
  List<Map<String,Object>> expectListOfMap(Object o)
  {
    return expectListOfMap(o, false);
  }
  
  @SuppressWarnings("unchecked")
  public static
  List<Map<String,Object>> expectListOfMap(Object o, boolean emptyListDefault)
  {
    if(o == null) {
      if(emptyListDefault) {
        return new ArrayList<Map<String,Object>>();
      }
      return null;
    }
    if(!(o instanceof List)) {
      if(o instanceof Map) {
        List<Map<String,Object>> listResult = new ArrayList<Map<String,Object>>();
        listResult.add((Map<String,Object>) o);
        return listResult;
      }
      if(emptyListDefault) {
        return new ArrayList<Map<String,Object>>();
      }
      return null;
    }
    int size = ((List<?>) o).size();
    if(size == 0) {
      return (List<Map<String,Object>>) o;
    }
    Object item0 = ((List<?>) o).get(0);
    if(item0 == null) {
      return (List<Map<String,Object>>) o;
    }
    if(item0 instanceof Map) {
      return (List<Map<String,Object>>) o;
    }
    if(emptyListDefault) {
      return new ArrayList<Map<String,Object>>();
    }
    return null;
  }
  
  public static 
  Map<String,Object> expectMap(Object o)
  {
    return expectMap(o, false);
  }
  
  @SuppressWarnings("unchecked")
  public static 
  Map<String,Object> expectMap(Object o, boolean emptyMapDefault)
  {
    if(o == null) {
      if(emptyMapDefault) {
        return new HashMap<String, Object>();
      }
      return null;
    }
    if(!(o instanceof Map)) {
      if(emptyMapDefault) {
        return new HashMap<String, Object>();
      }
      return null;
    }
    return (Map<String,Object>) o;
  }
  
  @SuppressWarnings("unchecked")
  public static <T> 
  T expect(Object o, Class<T> itemClass)
  {
    if(o == null) {
      return null;
    }
    if(itemClass == null) {
      return (T) o;
    }
    if(itemClass.isInstance(o)) {
      return (T) o;
    }
    return null;
  }
  
  public static 
  String expectString(Object o)
  {
    if(o == null) {
      return null;
    }
    if(o instanceof String) {
      return (String) o;
    }
    return o.toString();
  }
  
  public static 
  String expectString(Object o, String defaultValue)
  {
    if(o == null) {
      return defaultValue;
    }
    if(o instanceof String) {
      return (String) o;
    }
    String result = o.toString();
    if(result == null) return defaultValue;
    return result;
  }
}
