package fish.focus.uvms.usm.administration.service.userPreference.impl;

import fish.focus.uvms.usm.administration.domain.*;
import fish.focus.uvms.usm.administration.service.userPreference.UserPreferenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import java.util.HashSet;

/**
 * Stateless session bean implementation of the UserPreferenceService
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class UserPreferenceServiceBean implements UserPreferenceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserPreferenceServiceBean.class.getName());

    @Inject
    private PreferenceJdbcDao userPreferenceJdbcDao;

    @Inject
    private UserPreferenceValidator validator;

    @Override
    public UserPreferenceResponse getUserPrefernces(ServiceRequest<FindUserPreferenceQuery> request)
            throws IllegalArgumentException, UnauthorisedException, RuntimeException {
        LOGGER.debug("getUserPrefernces(" + request + ") - (ENTER)");

        HashSet<USMFeature> featureSet = new HashSet<USMFeature>();
        featureSet.add(USMFeature.viewUsers);
        featureSet.add(USMFeature.manageUsers);

        validator.assertValid(request, "query", featureSet);
        UserPreferenceResponse ret = userPreferenceJdbcDao.getUserPreferences(request.getBody().getUserName(),
                request.getBody().getGroupName());

        LOGGER.debug("getUserPrefernces() - (LEAVE)");
        return ret;
    }

}
