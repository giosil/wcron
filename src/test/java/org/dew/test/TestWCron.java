package org.dew.test;

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
  }
}
