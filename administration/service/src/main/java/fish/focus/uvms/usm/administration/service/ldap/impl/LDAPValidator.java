package fish.focus.uvms.usm.administration.service.ldap.impl;

import fish.focus.uvms.usm.administration.domain.GetUserQuery;
import fish.focus.uvms.usm.administration.domain.ServiceRequest;
import fish.focus.uvms.usm.administration.domain.USMFeature;
import fish.focus.uvms.usm.administration.service.RequestValidator;
import javax.ejb.Stateless;

/**
 * Provides operations for the validation and authorisation of LDAP
 * related service requests
 */
@Stateless
public class LDAPValidator extends RequestValidator {

    public LDAPValidator() {
    }

    public void assertValid(ServiceRequest<GetUserQuery> request) {
        assertValid(request, USMFeature.manageUsers, "query");
        String userName = request.getBody().getUserName();
        assertNotEmpty("userName", userName);
    }

}
