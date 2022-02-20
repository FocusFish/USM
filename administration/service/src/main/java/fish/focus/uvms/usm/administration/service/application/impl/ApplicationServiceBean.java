package fish.focus.uvms.usm.administration.service.application.impl;

import fish.focus.uvms.usm.information.entity.FeatureEntity;
import fish.focus.uvms.usm.information.entity.RoleEntity;
import fish.focus.uvms.usm.administration.domain.*;
import fish.focus.uvms.usm.administration.service.application.ApplicationService;
import fish.focus.uvms.usm.administration.service.role.impl.FeatureJpaDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Stateless session bean implementation of the ApplicationService
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ApplicationServiceBean implements ApplicationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationServiceBean.class);

    @Inject
    private ApplicationJdbcDao jdbcDao;

    @Inject
    private ApplicationValidator validator;

    @Inject
    private FeatureJpaDao featureJpaDao;

    @Override
    public List<String> getApplicationNames(ServiceRequest<ApplicationQuery> request)
            throws IllegalArgumentException, UnauthorisedException, RuntimeException {
        LOGGER.debug("getApplicationNames(" + request + ") - (ENTER)");

        HashSet<USMFeature> featureSet = new HashSet<>();
        featureSet.add(USMFeature.viewApplications);
        featureSet.add(USMFeature.manageApplications);

        validator.assertValid(request, "query", featureSet);
        List<String> ret = jdbcDao.getApplicationNames();

        LOGGER.debug("getApplicationNames() - (LEAVE)");
        return ret;
    }

    @Override
    public List<Feature> getFeatureApplicationNames(ServiceRequest<String> request)
			throws IllegalArgumentException, UnauthorisedException, RuntimeException {
        LOGGER.debug("getFeatureApplicationNames(" + request + ") - (ENTER)");

        List<FeatureEntity> response = featureJpaDao.getFeaturesByApplication(request.getBody());
        List<Feature> ret = validateRequestAndAddFeaturesToResponse(request, response);

        LOGGER.debug("getFeatureApplicationNames() - (LEAVE)");
        return ret;
    }

    @Override
    public List<Feature> getAllFeatures(ServiceRequest<String> request)
			throws IllegalArgumentException, UnauthorisedException, RuntimeException {
        LOGGER.debug("getAllFeatures(" + request + ") - (ENTER)");

        List<FeatureEntity> response = featureJpaDao.getAllFeatures();
        List<Feature> ret = validateRequestAndAddFeaturesToResponse(request, response);

        LOGGER.debug("getAllFeatures() - (LEAVE)");
        return ret;
    }

    private List<Feature> validateRequestAndAddFeaturesToResponse(ServiceRequest<String> request, List<FeatureEntity> response) {
        HashSet<USMFeature> featureSet = new HashSet<>();
        featureSet.add(USMFeature.viewApplications);
        featureSet.add(USMFeature.manageApplications);

        validator.assertValid(request, "query", featureSet);
        List<Feature> ret = new ArrayList<>();
        for (FeatureEntity feature : response) {
            ret.add(convertEntityToFeatureDomain(feature));
        }
        return ret;
    }

    private Feature convertEntityToFeatureDomain(FeatureEntity entity) {
        Feature feature = new Feature();
        feature.setFeatureId(entity.getFeatureId());
        feature.setName(entity.getName());
        feature.setDescription(entity.getDescription());
        feature.setApplicationName(entity.getApplication().getName());
        feature.setGroup(entity.getGroupName());

        List<Role> roles = new ArrayList<>();
        List<RoleEntity> roleEntities = entity.getRoleList();
        for (RoleEntity roleEntity : roleEntities) {
            Role role = new Role();
            role.setRoleId(roleEntity.getRoleId());
            roles.add(role);
        }
        feature.setRoles(roles);
        return feature;
    }

    @Override
    public PaginationResponse<Application> findApplications(ServiceRequest<FindApplicationQuery> request)
            throws IllegalArgumentException, UnauthorisedException, RuntimeException {
        LOGGER.debug("findApplications(" + request + ") - (ENTER)");

        HashSet<USMFeature> featureSet = new HashSet<USMFeature>();
        featureSet.add(USMFeature.viewApplications);
        featureSet.add(USMFeature.manageApplications);

        validator.assertValid(request, "query", featureSet);

        PaginationResponse<Application> ret = jdbcDao.findApplications(request.getBody());

        LOGGER.debug("findApplications() - (LEAVE)");
        return ret;
    }

    @Override
    public List<String> getParentApplicationNames(ServiceRequest<GetParentApplicationQuery> request)
            throws IllegalArgumentException, UnauthorisedException, RuntimeException {
        LOGGER.debug("getApplicationParentNames(" + request + ") - (ENTER)");

        HashSet<USMFeature> featureSet = new HashSet<USMFeature>();
        featureSet.add(USMFeature.viewApplications);
        featureSet.add(USMFeature.manageApplications);

        validator.assertValid(request, "query", featureSet);

        List<String> ret = jdbcDao.getParentApplicationNames();

        LOGGER.debug("getApplicationParentNames() - (LEAVE)");
        return ret;
    }

}
