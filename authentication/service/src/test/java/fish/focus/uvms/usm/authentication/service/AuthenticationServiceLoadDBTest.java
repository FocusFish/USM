package fish.focus.uvms.usm.authentication.service;

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

    @Test
    @Override
    public void testAuthenticateUserPasswordExpired() {
        for (int i = 0; i < ITERATIONS; i++) {
            super.testAuthenticateUserPasswordExpired();
        }
    }

    @Test
    @Override
    public void testAuthenticateUserTriggerLockOut() {
        for (int i = 0; i < ITERATIONS; i++) {
            super.testAuthenticateUserTriggerLockOut();
        }
    }

    @Test
    @Override
    public void testAuthenticateUserSuccess() {
        for (int i = 0; i < ITERATIONS; i++) {
            super.testAuthenticateUserSuccess();
        }
    }

    @Test
    @Override
    public void testAuthenticateUserFailureInvalidCredentials() {
        for (int i = 0; i < ITERATIONS; i++) {
            super.testAuthenticateUserFailureInvalidCredentials();
            super.tearDown();
        }
    }

    @Test
    @Override
    public void testAuthenticateUserFailureAccountLocked() {
        for (int i = 0; i < ITERATIONS; i++) {
            super.testAuthenticateUserFailureAccountLocked();
        }
    }

    @Test
    @Override
    public void testGetUserChallengeSuccess() {
        for (int i = 0; i < ITERATIONS; i++) {
            super.testGetUserChallengeSuccess();
        }
    }

    @Test
    @Override
    public void testGetUserChallengeFailure() {
        for (int i = 0; i < ITERATIONS; i++) {
            super.testGetUserChallengeFailure();
        }
    }

    @Test
    @Override
    public void testAuthenticateUserChallengeResponseSuccess() {
        for (int i = 0; i < ITERATIONS; i++) {
            super.testAuthenticateUserChallengeResponseSuccess();
        }
    }

    @Test
    @Override
    public void testAuthenticateUserChallengeResponseFailure() {
        for (int i = 0; i < ITERATIONS; i++) {
            super.testAuthenticateUserChallengeResponseFailure();
        }
    }

    @Test
    @Override
    public void testAuthenticateUserSuccessUserEnabled() {
        for (int i = 0; i < ITERATIONS; i++) {
            super.testAuthenticateUserSuccessUserEnabled();
        }
    }

    @Test
    @Override
    public void testAuthenticateUserFailureUserDisabled() {
        for (int i = 0; i < ITERATIONS; i++) {
            super.testAuthenticateUserFailureUserDisabled();
        }
    }

    @Test
    @Override
    public void testAuthenticateUserFailureUserLocked() {
        for (int i = 0; i < ITERATIONS; i++) {
            super.testAuthenticateUserFailureUserLocked();
        }
    }

}
