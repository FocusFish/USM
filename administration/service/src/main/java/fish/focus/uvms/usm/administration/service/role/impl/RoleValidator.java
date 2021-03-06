package fish.focus.uvms.usm.administration.service.role.impl;

import fish.focus.uvms.usm.administration.domain.ComprehensiveRole;
import fish.focus.uvms.usm.administration.domain.Permission;
import fish.focus.uvms.usm.administration.domain.ServiceRequest;
import fish.focus.uvms.usm.administration.domain.USMFeature;
import fish.focus.uvms.usm.administration.service.RequestValidator;
import javax.ejb.Stateless;

/**
 * Provides operations for the validation and authorisation of Role related
 * service requests
 */
@Stateless
public class RoleValidator extends RequestValidator {

    public RoleValidator() {
    }

    public void assertValid(ServiceRequest<ComprehensiveRole> request, USMFeature feature, boolean isCreate) {
        assertValid(request, feature, "role");
        assertNotEmpty("roleName", request.getBody().getName());
        assertNotEmpty("status", request.getBody().getStatus());

        if (!isCreate) {
            assertNotNull("roleId", request.getBody().getRoleId());
        }
    }

    public void assertValidPermission(ServiceRequest<Permission> request, USMFeature feature) {
        assertValid(request, feature, "persmission");
        assertNotNull("roleId", request.getBody().getRoleId());
        assertNotNull("featureId", request.getBody().getFeatureId());
    }

}
