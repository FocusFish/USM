package eu.europa.ec.mare.usm.authentication.service;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Load-test for the AuthenticationService configured to authenticate
 * users against the USM database schema.
 */
@RunWith(Arquillian.class)
public class AuthenticationServiceLoadDBTest extends AuthenticationServiceDBTest {
  private static final int ITERATIONS = 32;
  
  /**
   * Creates a new instance
   */
  public AuthenticationServiceLoadDBTest() {
  }

  /**
   * Tests the authenticateUser method.
   */
  @Test
  @Override
  public void testAuthenticateUserPasswordExpired() 
  {
    for (int i = 0; i <ITERATIONS; i++) {
      super.testAuthenticateUserPasswordExpired();
    }
  }

  /**
   * Tests the authenticateUser method.
   */
  @Test
  @Override
  public void testAuthenticateUserTriggerLockOut() 
  {
    for (int i = 0; i <ITERATIONS; i++) {
      super.testAuthenticateUserTriggerLockOut();
    }
  }

  /**
   * Tests the authenticateUser method.
   */
  @Test
  @Override
  public void testAuthenticateUserSuccess() 
  {
    for (int i = 0; i <ITERATIONS; i++) {
      super.testAuthenticateUserSuccess();
    }
  }

  /**
   * Tests the authenticateUser method.
   */
  @Test
  @Override
  public void testAuthenticateUserFailureInvalidCredentials() 
  {
    for (int i = 0; i <ITERATIONS; i++) {
      super.testAuthenticateUserFailureInvalidCredentials();
      super.tearDown();
    }
  }

  /**
   * Tests the authenticateUser method.
   */
  @Test
  @Override
  public void testAuthenticateUserFailureAccountLocked() 
  {
    for (int i = 0; i <ITERATIONS; i++) {
      super.testAuthenticateUserFailureAccountLocked();
    }
  }

  /**
   * Tests the getUserChallenge method.
   */
  @Test
  @Override
  public void testGetUserChallengeSuccess() 
  {
    for (int i = 0; i <ITERATIONS; i++) {
      super.testGetUserChallengeSuccess();
    }
  }

  /**
   * Tests the getUserChallenge method.
   */
  @Test
  @Override
  public void testGetUserChallengeFailure() 
  {
    for (int i = 0; i <ITERATIONS; i++) {
      super.testGetUserChallengeFailure();
    }
  }

  /**
   * Tests the authenticateUser method.
   */
  @Test
  @Override
  public void testAuthenticateUserChallengeResponseSuccess() 
  {
    for (int i = 0; i <ITERATIONS; i++) {
      super.testAuthenticateUserChallengeResponseSuccess();
    }
  }

  /**
   * Tests the authenticateUser method.
   */
  @Test
  @Override
  public void testAuthenticateUserChallengeResponseFailure() 
  {
    for (int i = 0; i <ITERATIONS; i++) {
      super.testAuthenticateUserChallengeResponseFailure();
    }
  }
  /**
   * Tests the authenticateUser method.
   */
  @Test
  @Override
  public void testAuthenticateUserSuccessUserEnabled() 
  {
    for (int i = 0 ; i <ITERATIONS; i++) {
      super.testAuthenticateUserSuccessUserEnabled();
    }
  }

  /**
   * Tests the authenticateUser method.
   */
  @Test
  @Override
  public void testAuthenticateUserFailureUserDisabled() 
  {
    for (int i = 0 ; i <ITERATIONS; i++) {
      super.testAuthenticateUserFailureUserDisabled();
    }
  }

  /**
   * Tests the authenticateUser method.
   */
  @Test
  @Override
  public void testAuthenticateUserFailureUserLocked() 
  {
    for (int i = 0 ; i <ITERATIONS; i++) {
      super.testAuthenticateUserFailureUserLocked();
    }
  }

}