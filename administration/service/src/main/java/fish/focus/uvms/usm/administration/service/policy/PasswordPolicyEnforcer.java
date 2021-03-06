package fish.focus.uvms.usm.administration.service.policy;

import java.util.Date;
import fish.focus.uvms.usm.administration.domain.ChangePassword;
import fish.focus.uvms.usm.administration.domain.ServiceRequest;

/**
 * Enforces password related policies.
 */
public interface PasswordPolicyEnforcer {

    /**
     * Asserts that the provided request to set the password of a user
     * complies with all applicable policies.
     *
     * @param request a request to set the password of a user
     * @return the password expiry date for the newly set password
     * @throws IllegalArgumentException in case the provided subject is null,
     *                                  empty or incomplete or if the user does not exist or if the password
     *                                  violates one or more of the applicable policies
     * @throws RuntimeException         in case an internal problem prevents processing the request
     */
    public Date assertValid(ServiceRequest<ChangePassword> request) throws IllegalArgumentException, RuntimeException;

    public String getPasswordPolicy();
}
