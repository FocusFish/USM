package fish.focus.uvms.usm.administration.service.userContext.impl;

import fish.focus.uvms.usm.administration.domain.*;
import fish.focus.uvms.usm.administration.service.RequestValidator;
import javax.ejb.Stateless;

/**
 * Provides operations for the validation and authorisation of UserContext
 * related service requests
 */
@Stateless
public class UserContextValidator extends RequestValidator {

    public UserContextValidator() {
    }

    public void assertValid(ServiceRequest<UserContext> request, USMFeature feature, boolean isCreate) {
        assertValid(request, feature, "user role");
        assertNotEmpty("userName", request.getBody().getUserName());
        assertNotNull("roleId", request.getBody().getRoleId());

        if (!isCreate) {
            assertNotNull("userContextId", request.getBody().getRoleId());
        }
    }

    public void assertValidPermission(ServiceRequest<Permission> request, USMFeature feature) {
        assertValid(request, feature, "persmission");
        assertNotNull("roleId", request.getBody().getRoleId());
        assertNotNull("featureId", request.getBody().getFeatureId());
    }

    public void assertValidCopy(ServiceRequest<UserContextResponse> request, USMFeature feature, String toUserName) {
        assertValid(request, feature, "origin");
        if (request.getBody().getResults() != null) {
            for (ComprehensiveUserContext item : request.getBody().getResults()) {
                assertNotNull("userContextId", item.getUserContextId());
            }
        }
        assertNotEmpty("userName", toUserName);
    }
}
