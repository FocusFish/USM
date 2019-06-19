package eu.europa.ec.mare.usm.session.rest.service;

import eu.europa.ec.mare.usm.session.domain.SessionInfo;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Properties;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for the REST implementation of the SessionTracker.
 */
public class SessionTrackerRestIT {
  private SessionTrackerRestClient testSubject = null;
  private final String endPoint;
  private String userSite = null;

  /**
   * Creates a new instance.
   * 
   * @throws IOException if class-path resource /test.properties can't be 
   * accessed
   */
  public SessionTrackerRestIT() 
  throws IOException 
  {
    userSite = InetAddress.getLocalHost().getHostAddress();

    InputStream is = getClass().getResourceAsStream("/test.properties");
    Properties props = new Properties();
    props.load(is);
    endPoint = props.getProperty("rest.endpoint");
  }

  @Before
  public void setUp()
  {
    testSubject = new SessionTrackerRestClient(endPoint);
  }

  
  /**
   * Tests the startSession method.
   */
  @Test
  public void testStartSessionOneSite() 
  {
    // Execute
    SessionInfo sessionId = createSessionInfo("testStartSession");
    
    String result = testSubject.startSession(sessionId);
    
    // Verify
    assertNotNull("Unexpected null result", result);
  }

  /**
   * Tests the getSession method.
   */
  @Test
  public void testGetSession() 
  {
    // Set-up
    SessionInfo sessionInfo = createSessionInfo("testGetSession");
    String uniqueId = testSubject.startSession(sessionInfo);

    // Execute
    SessionInfo result = testSubject.getSession(uniqueId);
    
    // Verify
    assertNotNull("Unexpected null result", result);
    assertEquals("Unexpected userName value", sessionInfo.getUserName(), result.getUserName());
    assertEquals("Unexpected userSite value", sessionInfo.getUserSite(), result.getUserSite());
  }
  
  /**
   * Tests the endSession method.
   */
  @Test
  public void testEndSession() 
  {
    // Set-up
    SessionInfo sessionInfo = createSessionInfo("testEndSession");
    String uniqueId = testSubject.startSession(sessionInfo);

    // Execute
    testSubject.endSession(uniqueId);
  }
  
  SessionInfo createSessionInfo(String userName)
  {
    return createSessionInfo(userName, userSite);
  }
  
  SessionInfo createSessionInfo(String userName, String userSite)
  {
    SessionInfo ret = new SessionInfo();
    
    ret.setUserName(userName);
    ret.setUserSite(userSite);
    
    return ret;
  }
 
}