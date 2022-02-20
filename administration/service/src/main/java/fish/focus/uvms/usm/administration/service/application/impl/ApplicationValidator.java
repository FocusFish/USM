package fish.focus.uvms.usm.administration.service.application.impl;

import fish.focus.uvms.usm.administration.domain.Application;
import fish.focus.uvms.usm.administration.domain.ServiceRequest;
import fish.focus.uvms.usm.administration.domain.USMFeature;
import fish.focus.uvms.usm.administration.service.RequestValidator;
import javax.ejb.Stateless;

/**
 * Provides operations for the validation and authorisation of Application
 * related service requests
 */
@Stateless
public class ApplicationValidator extends RequestValidator {

    public ApplicationValidator() {
    }

    public void assertValid(ServiceRequest<Application> request, USMFeature feature, boolean isCreate) {
        assertValid(request, feature, "application");
    }

}
