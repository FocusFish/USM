package fish.focus.uvms.usm.administration.service.ldap;

import fish.focus.uvms.usm.administration.domain.GetUserQuery;
import fish.focus.uvms.usm.administration.domain.LdapUser;
import fish.focus.uvms.usm.administration.domain.ServiceRequest;
import fish.focus.uvms.usm.administration.domain.UnauthorisedException;

/**
 * Provides operations for the retrieval of user information from an
 * LDAP compatible Identity Management system.
 */
public interface LdapUserInfoService {
    /**
     * Checks whether the retrieval of user information from an
     * LDAP compatible Identity Management system is enabled,
     *
     * @return <i>true</i> if it is enabled, <i>false</i> otherwise
     */
    public boolean isEnabled();

    /**
     * Gets user details from the configured LDAP compatible Identity
     * Management system, for the user with the provided name.
     *
     * @param request holds the details of the user to be updated
     * @return the requested user details if the user exists, or <i>null</i>
     * if the feature is disabled or the user does not exist
     * @throws IllegalArgumentException in case the service request is null or
     *                                  incomplete
     * @throws UnauthorisedException    in case the service requester is not
     *                                  authorised to use the specific feature
     * @throws RuntimeException         in case an internal problem prevents processing
     *                                  the request
     */
    public LdapUser getLdapUserInfo(ServiceRequest<GetUserQuery> request)
            throws IllegalArgumentException, UnauthorisedException, RuntimeException;
}
