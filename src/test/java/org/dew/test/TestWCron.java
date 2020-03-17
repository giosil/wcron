package org.dew.test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.dew.wcron.util.JobUtils;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestWCron extends TestCase {
  
  public TestWCron(String testName) {
    super(testName);
  }
  
  public static Test suite() {
    return new TestSuite(TestWCron.class);
  }
  
  public void testApp() throws Exception {
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put("greeting", "hello");
    
    Callable<?> jobInstance = JobUtils.createJobInstance("mock", 1, parameters);
    
    Object result = jobInstance.call();
    
    System.out.println(result);
  }
}
