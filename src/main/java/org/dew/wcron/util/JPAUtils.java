package org.dew.wcron.util;

import java.lang.reflect.Array;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.sql.DataSource;

public 
class JPAUtils 
{
  public static
  Connection getConnection(EntityManager em)
    throws Exception
  {
    if(em == null) {
      throw new Exception("Null pointer to entityManager");
    }
    
    String exceptionMessages = "";
    try {
      EntityManagerFactory emf = em.getEntityManagerFactory();
      
      Map<String, Object> properties = emf.getProperties();
      if(properties == null) return null;
      
      Object jtaDataSource = properties.get("javax.persistence.jtaDataSource");
      if(jtaDataSource instanceof DataSource) {
        return ((DataSource) jtaDataSource).getConnection();
      }
      else if(jtaDataSource instanceof String) {
        Context ctx = new InitialContext();
        DataSource dataSource = (DataSource) ctx.lookup((String) jtaDataSource);
        return dataSource.getConnection();
      }
    }
    catch(Exception ex) {
      exceptionMessages += ex.getMessage();
    }
    
    try {
      Connection conn = em.unwrap(Connection.class);
      if(conn != null) return conn;
    }
    catch(Exception ex) {
      if(exceptionMessages.length() > 0) exceptionMessages += ";";
      exceptionMessages += ex.getMessage();
    }
    
    throw new Exception(exceptionMessages);
  }
  
  public static
  int readInt(EntityManager em, String sSQL, Object... parameters)
    throws Exception
  {
    Query query = em.createNativeQuery(sSQL);
    
    if(parameters != null) {
      for(int i = 0; i < parameters.length; i++) {
        query.setParameter(i + 1, parameters[i]);
      }
    }
    
    Object singleResult = query.getSingleResult();
    
    return getInt(singleResult);
  }
  
  public static
  List<Integer> readListOfInteger(EntityManager em, String sSQL, Object... parameters)
    throws Exception
  {
    Query query = em.createNativeQuery(sSQL);
    
    if(parameters != null) {
      for(int i = 0; i < parameters.length; i++) {
        query.setParameter(i + 1, parameters[i]);
      }
    }
    
    List<?> queryResultList = query.getResultList();
    if(queryResultList == null || queryResultList.size() == 0) {
      return new ArrayList<Integer>(0);
    }
    
    List<Integer> listResult = new ArrayList<Integer>(queryResultList.size());
    for(int i = 0; i < queryResultList.size(); i++) {
      listResult.add(getInt(queryResultList.get(i)));
    }
    
    return listResult;
  }
  
  public static
  int[] readArrayOfInt(EntityManager em, String sSQL, Object... parameters)
    throws Exception
  {
    Query query = em.createNativeQuery(sSQL);
    
    if(parameters != null) {
      for(int i = 0; i < parameters.length; i++) {
        query.setParameter(i + 1, parameters[i]);
      }
    }
    
    List<?> queryResultList = query.getResultList();
    if(queryResultList == null || queryResultList.size() == 0) {
      return new int[0];
    }
    
    int[] aiResult = new int[queryResultList.size()];
    for(int i = 0; i < queryResultList.size(); i++) {
      aiResult[i] = getInt(queryResultList.get(i));
    }
    
    return aiResult;
  }
  
  public static
  String readString(EntityManager em, String sSQL, Object... parameters)
    throws Exception
  {
    Query query = em.createNativeQuery(sSQL);
    
    if(parameters != null) {
      for(int i = 0; i < parameters.length; i++) {
        query.setParameter(i + 1, parameters[i]);
      }
    }
    
    Object singleResult = query.getSingleResult();
    
    return getString(singleResult);
  }
  
  public static
  List<String> readListOfString(EntityManager em, String sSQL, Object... parameters)
    throws Exception
  {
    Query query = em.createNativeQuery(sSQL);
    
    if(parameters != null) {
      for(int i = 0; i < parameters.length; i++) {
        query.setParameter(i + 1, parameters[i]);
      }
    }
    
    List<?> queryResultList = query.getResultList();
    if(queryResultList == null || queryResultList.size() == 0) {
      return new ArrayList<String>(0);
    }
    
    List<String> listResult = new ArrayList<String>(queryResultList.size());
    for(int i = 0; i < queryResultList.size(); i++) {
      listResult.add(getString(queryResultList.get(i)));
    }
    
    return listResult;
  }
  
  public static
  String[] readArrayOfString(EntityManager em, String sSQL, Object... parameters)
    throws Exception
  {
    Query query = em.createNativeQuery(sSQL);
    
    if(parameters != null) {
      for(int i = 0; i < parameters.length; i++) {
        query.setParameter(i + 1, parameters[i]);
      }
    }
    
    List<?> queryResultList = query.getResultList();
    if(queryResultList == null || queryResultList.size() == 0) {
      return new String[0];
    }
    
    String[] asResult = new String[queryResultList.size()];
    for(int i = 0; i < queryResultList.size(); i++) {
      asResult[i] = getString(queryResultList.get(i));
    }
    
    return asResult;
  }
  
  public static
  int getInt(Object queryResult)
  {
    if(queryResult == null) return 0;
    
    if(queryResult instanceof Number) {
      return ((Number) queryResult).intValue();
    }
    else if(queryResult instanceof String) {
      try { 
        return Integer.parseInt(((String) queryResult).trim()); 
      } 
      catch(Exception ex) {
        System.err.println("readInt parseInt(" + queryResult + "): " + ex);
        return 0;
      }
    }
    else if(queryResult.getClass().isArray()) {
      int length = Array.getLength(queryResult);
      if(length == 0) return 0;
      
      Object result = Array.get(queryResult, 0);
      if(result instanceof Number) {
        return ((Number) result).intValue();
      }
      else if(result instanceof String) {
        try { 
          return Integer.parseInt(((String) result).trim()); 
        } 
        catch(Exception ex) {
          System.err.println("readInt parseInt(" + result + "): " + ex);
          return 0;
        }
      }
    }
    
    return 0;
  }
  
  public static
  String getString(Object queryResult)
  {
    if(queryResult == null) return null;
    
    if(queryResult.getClass().isArray()) {
      int length = Array.getLength(queryResult);
      if(length == 0) return null;
      
      Object result = Array.get(queryResult, 0);
      if(result != null) {
        return result.toString();
      }
    }
    
    return queryResult.toString();
  }
}
