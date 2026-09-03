package fish.focus.uvms.usm.administration.service;

import fish.focus.uvms.usm.administration.domain.ServiceRequest;
import fish.focus.uvms.usm.administration.domain.USMApplication;
import fish.focus.uvms.usm.administration.domain.USMFeature;
import fish.focus.uvms.usm.administration.domain.UnauthorisedException;
import fish.focus.uvms.usm.information.domain.Context;
import fish.focus.uvms.usm.information.domain.UserContext;
import fish.focus.uvms.usm.information.domain.UserContextQuery;
import fish.focus.uvms.usm.information.service.InformationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Provides operations for the validation and authorisation of service requests
 */
//@Stateless
public class RequestValidator {
    protected static final Logger LOGGER = LoggerFactory.getLogger(RequestValidator.class);

    @EJB
    private InformationService infoService;

    /**
     * Creates a new instance.
     */
    public RequestValidator() {
    }

    /**
     * Asserts that the provided service request is valid and optionally,
     * that the service requester is allowed to use the specified feature.
     *
     * @param input           the service request to be validated
     * @param requiredFeature the optional feature to which the service requester must
     *                        have been granted a right to use
     * @throws IllegalArgumentException if the service request is null, empty or
     *                                  incomplete
     * @throws UnauthorisedException    if the service requester is not allowed
     *                                  to use the specified feature
     */
    public void assertValid(ServiceRequest input, USMFeature requiredFeature)
            throws IllegalArgumentException, UnauthorisedException {
        assertNotNull("request", input);
        assertNotEmpty("requester", input.getRequester());

        if (requiredFeature == null) {
            return;
        }

        String applicationName = USMApplication.USM.name();
        UserContext userContext = getUserContext(input, applicationName);

        if (userContext == null || userContext.getContextSet() == null
                || userContext.getContextSet().getContexts() == null) {
            logAndThrowUnauthorizedException(input, requiredFeature);
        }

        for (Context context : userContext.getContextSet().getContexts()) {
            if (context.getRole() == null) {
                continue;
            }

            boolean hasRequestedRole = input.getRoleName() == null // no supplied role => check all roles
                    || input.getRoleName().equals(context.getRole().getRoleName());
            boolean hasRequestedScope = context.getScope() == null
                    || input.getScopeName() == null // no scoped down request => whole role is in scope
                    || context.getScope().getScopeName().equals(input.getScopeName());

            if (hasRequestedRole
                    && hasRequestedScope
                    && hasRequestedFeatureForApplication(context, requiredFeature, applicationName)) {
                return;
            }
        }

        logAndThrowUnauthorizedException(input, requiredFeature);
    }

    private UserContext getUserContext(ServiceRequest input, String applicationName) {
        UserContextQuery query = new UserContextQuery();
        query.setApplicationName(applicationName);
        query.setUserName(input.getRequester());

        return infoService.getUserContext(query);
    }

    private void logAndThrowUnauthorizedException(ServiceRequest input, USMFeature requiredFeature) {
        LOGGER.info("User {} is not authorised for {} using context with role {} and scope {}",
                input.getRequester(), requiredFeature, input.getRoleName(), input.getScopeName());

        throw new UnauthorisedException("Not authorised");
    }

    private boolean hasRequestedFeatureForApplication(Context context, USMFeature requiredFeature, String applicationName) {
        return context.getRole().getFeatures()
                .stream()
                .anyMatch(feature -> applicationName.equals(feature.getApplicationName()) &&
                        requiredFeature.name().equals(feature.getFeatureName())
                );
    }

    /**
     * Asserts that the provided service request is valid and optionally,
     * that the service requester is allowed to use the specified feature.
     *
     * @param input    the service request to be validated
     * @param feature  the optional feature to which the service requester must
     *                 have been granted a right to use
     * @param bodyName the logical name of the service-request body
     * @throws IllegalArgumentException if the service request is null, empty or
     *                                  incomplete
     * @throws UnauthorisedException    if the service requester is not allowed
     *                                  to use the specified feature
     */
    public void assertValid(ServiceRequest input, USMFeature feature, String bodyName)
            throws IllegalArgumentException, UnauthorisedException {
        assertValid(input, feature);
        assertNotNull(bodyName, input.getBody());
    }

    public void assertValid(ServiceRequest input, String bodyName, Set<USMFeature> features)
            throws IllegalArgumentException, UnauthorisedException {
        UnauthorisedException authException = null;
        for (USMFeature usmFeature : features) {
            try {
                assertValid(input, usmFeature);
                return;
            } catch (UnauthorisedException e) {
                // TODO: handle exception
                authException = e;
            }
        }
        if (authException != null) {
            throw authException;
        }
        assertNotNull(bodyName, input.getBody());
    }

    public void assertValidPeriod(String name, Date from, Date to) {
        if (from != null && to != null && to.before(from)) {
            throw new IllegalArgumentException(name + " dates are not in sequence");
        }
    }

    public void assertNotTooLong(String name, int maxLen, String value) {
        if (value != null && value.length() > maxLen) {
            throw new IllegalArgumentException(name + " is too long (max " + maxLen + ")");
        }
    }

    public void assertNotTooShort(String name, int minLen, String value) {
        if (value != null && value.length() < minLen) {
            throw new IllegalArgumentException(name + " is too short (min " + minLen + ")");
        }
    }

    public void assertNotEmpty(String name, String value) {
        assertNotNull(name, value);
        if (value.trim().length() == 0) {
            throw new IllegalArgumentException(name + " must be defined");
        }
    }

    public void assertNotNull(String name, Object value) {
        if (value == null) {
            throw new IllegalArgumentException(name + " must be defined");
        }
    }

    protected void assertInList(String name, String[] listOfValues, String value) {
        List<String> asList = Arrays.asList(listOfValues);
        assertInList(name, asList, value);
    }

    protected void assertInList(String name, List<String> listOfValues, String value) {
        if (value != null) {
            boolean inList = false;

            for (String v : listOfValues) {
                if (v.equals(value)) {
                    inList = true;
                    break;
                }
            }
            if (!inList) {
                throw new IllegalArgumentException(name + " (" + value + ") is not supported");
            }
        }
    }

}
