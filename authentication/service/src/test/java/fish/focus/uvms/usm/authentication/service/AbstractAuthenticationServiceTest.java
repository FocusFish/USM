package fish.focus.uvms.usm.authentication.service;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import fish.focus.uvms.usm.authentication.domain.AuthenticationQuery;
import fish.focus.uvms.usm.authentication.domain.AuthenticationRequest;
import fish.focus.uvms.usm.authentication.domain.AuthenticationResponse;
import fish.focus.uvms.usm.authentication.domain.ChallengeResponse;
import fish.focus.uvms.usm.authentication.service.AuthenticationService;
import javax.ejb.EJB;
import static fish.focus.uvms.usm.authentication.domain.AuthenticationResponse.*;
import static org.junit.Assert.*;

/**
 * Abstract unit-test for the AuthenticationService
 */
@RunWith(Arquillian.class)
public abstract class AbstractAuthenticationServiceTest {

    protected static final String POLICY_SUBJECT = "Authentication";

    //Tear-down: do a successful login to avoid account lockout
    protected static final String TEAR_DOWN_PASSWORD = "password";

    @EJB
    private AuthenticationService testSubject;

    protected AuthenticationResponse resp;
    protected ChallengeResponse challengeResp;
    protected boolean authenticateCalled = false;
    protected String invalidCredentialsUsername = null;

    @Before
    public void clearResp() {
        authenticateCalled = false;
        invalidCredentialsUsername = null;
        resp = null;
        challengeResp = null;
    }

    @After
    public void tearDown() {
        if (invalidCredentialsUsername != null) {
            AuthenticationRequest request = new AuthenticationRequest();
            request.setUserName(invalidCredentialsUsername);
            request.setPassword(TEAR_DOWN_PASSWORD);
            AuthenticationResponse tearDown = testSubject.authenticateUser(request);
            assertNotNull("Unexpected null response", tearDown);
            System.out.println("AuthenticationResponse: " + tearDown);
            assertTrue("Unexpected Unauthenticated response", tearDown.isAuthenticated());
        }
    }

    public AbstractAuthenticationServiceTest() {
    }

    /**
     * Set up and execute authenticateUser for the given user name and password, in case of invalid credentials
     * tear down is prepared to avoid account lockout
     */
    protected void authenticateUser(String userName, String password) {
        authenticateCalled = true;

        // Set-up
        AuthenticationRequest request = new AuthenticationRequest();
        request.setUserName(userName);
        request.setPassword(password);

        // Execute
        resp = testSubject.authenticateUser(request);

        if (resp.getStatusCode() == INVALID_CREDENTIALS) {
            invalidCredentialsUsername = userName;
        }
    }

    protected void authenticateUser(ChallengeResponse challengeTaken) {
        authenticateCalled = true;

        // Execute
        resp = testSubject.authenticateUser(challengeTaken);
    }

    protected boolean isLDAPEnabled() {
        return testSubject.isLDAPEnabled();
    }

    @Test
    public void testAuthenticateUserSuccess() {
        authenticateUser("quota_man_com", "password");
        verify(true, SUCCESS);
    }

    @Test
    public void testAuthenticateUserFailureAccountLocked() {
        authenticateUser("lockout", "password");
        verify(false, ACCOUNT_LOCKED);
    }

    @Test
    public void testAuthenticateUserFailureInvalidCredentials() {
        authenticateUser("quota_usr_com", "wrong password");
        verify(false, INVALID_CREDENTIALS);
    }

    @Test
    public void testGetUserChallengeSuccess() {
        // Set-up
        AuthenticationQuery query = new AuthenticationQuery();
        query.setUserName("quota_usr_com");

        // Execute
        challengeResp = testSubject.getUserChallenge(query);

        // Verify
        assertNotNull("Unexpected null response", challengeResp);
        assertEquals("Name of street where you grew up", challengeResp.getChallenge());
    }

    @Test
    public void testGetUserChallengeFailure() {
        // Set-up
        AuthenticationQuery query = new AuthenticationQuery();
        query.setUserName("wrong user name");

        challengeResp = testSubject.getUserChallenge(query);

        // Verify
        assertNull("Unexpected not-null response", challengeResp);
    }

    @Test
    public void testAuthenticateUserChallengeResponseSuccess() {
        // Set-up
        AuthenticationQuery query = new AuthenticationQuery();
        query.setUserName("quota_usr_com");
        ChallengeResponse request = testSubject.getUserChallenge(query);
        request.setResponse("Grand rue");

        authenticateUser(request);
        verify(true, SUCCESS);
    }

    @Test
    public void testAuthenticateUserChallengeResponseFailure() {
        AuthenticationQuery query = new AuthenticationQuery();
        query.setUserName("quota_man_com");
        ChallengeResponse request = testSubject.getUserChallenge(query);
        request.setResponse("wrong answer");

        authenticateUser(request);

        verify(false, INVALID_CREDENTIALS);
    }

    protected AuthenticationService getTestSubject() {
        return testSubject;
    }

    private void verify(boolean authenticated, int statusCode) {
        assertNotNull("Unexpected null response", resp);
        assertEquals("Unexpected Authenticated response", resp.isAuthenticated(), authenticated);
        assertEquals("Unexpected response StatusCode", statusCode, resp.getStatusCode());
    }
}
