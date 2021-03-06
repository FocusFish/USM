package fish.focus.uvms.usm.administration.service.role.impl;

import fish.focus.uvms.audit.model.mapper.AuditLogModelMapper;
import fish.focus.uvms.usm.information.entity.FeatureEntity;
import fish.focus.uvms.usm.information.entity.RoleEntity;
import fish.focus.uvms.usm.information.entity.UserContextEntity;
import fish.focus.uvms.usm.administration.domain.*;
import fish.focus.uvms.usm.administration.service.AuditProducer;
import fish.focus.uvms.usm.administration.service.role.RoleService;
import fish.focus.uvms.usm.administration.service.user.impl.UserJpaDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * Stateless session bean implementation of the RoleService
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class RoleServiceBean implements RoleService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoleServiceBean.class);

    @Inject
    private RoleJdbcDao roleJdbcDao;
    @Inject
    private RoleJpaDao roleJpaDao;
    @Inject
    private FeatureJpaDao featureJpaDao;
    @Inject
    private FeatureJdbcDao featureJdbcDao;
    @Inject
    private UserJpaDao userJpaDao;
    @Inject
    private RoleValidator validator;
    @Inject
    private RoleConverter converter;
    @Inject
    private AuditProducer auditProducer;

    @Override
    public List<String> getRoleNames(ServiceRequest<RoleQuery> request) {
        LOGGER.debug("getRoleNames(" + request + ") - (ENTER)");

        HashSet<USMFeature> featureSet = new HashSet<USMFeature>();
        featureSet.add(USMFeature.viewRoles);
        featureSet.add(USMFeature.manageRoles);

        validator.assertValid(request, "query", featureSet);
        List<String> names = roleJdbcDao.getRoleNames();

        LOGGER.debug("getRoleNames() - (LEAVE)");
        return names;
    }

    @Override
    public Role getRole(ServiceRequest<GetRoleQuery> request) {
        LOGGER.debug("getRole(" + request + ") - (ENTER)");

        HashSet<USMFeature> featureSet = new HashSet<USMFeature>();
        featureSet.add(USMFeature.viewRoles);
        featureSet.add(USMFeature.manageRoles);

        validator.assertValid(request, "query", featureSet);
        RoleEntity result = roleJpaDao.read(request.getBody().getRoleId());
        Role role = converter.convert(result);
        if (role != null) {
            List<UserContextEntity> activeUsers = userJpaDao.findActiveUsers(request.getBody().getRoleId());
            role.setActiveUsers(activeUsers == null ? 0 : activeUsers.size());
        }

        LOGGER.debug("getRole() - (LEAVE)");
        return role;
    }

    @Override
    public PaginationResponse<ComprehensiveRole> findRoles(ServiceRequest<FindRolesQuery> request) {
        LOGGER.debug("findRoles(" + request + ") - (ENTER)");

        HashSet<USMFeature> featureSet = new HashSet<USMFeature>();
        featureSet.add(USMFeature.viewRoles);
        featureSet.add(USMFeature.manageRoles);

        validator.assertValid(request, "query", featureSet);

        PaginationResponse<ComprehensiveRole> ret = roleJdbcDao.findRoles(request.getBody());

        LOGGER.debug("findRoles() - (LEAVE)");
        return ret;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ComprehensiveRole createRole(ServiceRequest<ComprehensiveRole> request)
            throws IllegalArgumentException, UnauthorisedException, RuntimeException {
        LOGGER.debug("createRole( " + request + " ) - (ENTER)");

        validator.assertValid(request, USMFeature.manageRoles, true);

        if (roleJdbcDao.roleExists(request.getBody().getName())) {
            throw new IllegalArgumentException("Role already exists.");
        }
        RoleEntity entity = new RoleEntity();
        converter.updateEntity(entity, request.getBody());
        entity.setCreatedBy(request.getRequester());
        entity.setCreatedOn(new Date());
        entity = roleJpaDao.create(entity);

        ComprehensiveRole ret = converter.convertComprehensively(entity);

        String auditLog = AuditLogModelMapper.mapToAuditLog(USMApplication.USM.name(),
                AuditOperationEnum.CREATE.getValue(), AuditObjectTypeEnum.ROLE.getValue() + " " +
                        request.getBody().getName(), request.getBody().getDescription(), request.getRequester());
        auditProducer.sendModuleMessage(auditLog);

        LOGGER.debug("createRole() - (LEAVE)");
        return ret;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void updateRole(ServiceRequest<ComprehensiveRole> request)
            throws IllegalArgumentException, UnauthorisedException, RuntimeException {
        LOGGER.debug("updateRole( " + request + " ) - (ENTER)");

        validator.assertValid(request, USMFeature.manageRoles, false);

        RoleEntity entity = roleJpaDao.read(request.getBody().getRoleId());
        if (entity == null) {
            throw new IllegalArgumentException("Role does not exist");
        }

        converter.updateEntity(entity, request.getBody());
        entity.setModifiedBy(request.getRequester());
        entity.setModifiedOn(new Date());

        roleJpaDao.update(entity);


        String auditLog = AuditLogModelMapper.mapToAuditLog(USMApplication.USM.name(),
                AuditOperationEnum.UPDATE.getValue(), AuditObjectTypeEnum.ROLE.getValue() + " " +
                        request.getBody().getName(), request.getBody().getDescription(), request.getRequester());
        auditProducer.sendModuleMessage(auditLog);

        LOGGER.debug("updateRole() - (LEAVE)");
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteRole(ServiceRequest<Long> request)
            throws IllegalArgumentException, UnauthorisedException, RuntimeException {
        LOGGER.debug("deleteRole( " + request + " ) - (ENTER)");

        validator.assertValid(request, USMFeature.manageRoles, "roleId");
        roleJpaDao.delete(request.getBody());

        String auditLog = AuditLogModelMapper.mapToAuditLog(USMApplication.USM.name(),
                AuditOperationEnum.DELETE.getValue(), AuditObjectTypeEnum.ROLE.getValue() + " " +
                        request.getBody(), "" + request.getBody(), request.getRequester());
        auditProducer.sendModuleMessage(auditLog);

        LOGGER.debug("deleteRole() - (LEAVE)");
    }

    @Override
    public List<Feature> findFeaturesByApplication(ServiceRequest<String> request)
            throws IllegalArgumentException, UnauthorisedException, RuntimeException {
        LOGGER.debug("findFeaturesByApplication( " + request + " ) - (ENTER)");

        List<FeatureEntity> lst = featureJpaDao.getFeaturesByApplication(request.getBody());

        List<Feature> ret = new ArrayList<>();
        for (FeatureEntity entity : lst) {
            ret.add(converter.convertWithoutRoles(entity));
        }

        LOGGER.debug("findFeaturesByApplication() - (LEAVE)");
        return ret;
    }

    @Override
    public List<String> getGroupNames(ServiceRequest<String> request) {
        LOGGER.debug("getGroupNames(" + request + ") - (ENTER)");

        HashSet<USMFeature> featureSet = new HashSet<USMFeature>();
        featureSet.add(USMFeature.viewRoles);
        featureSet.add(USMFeature.manageRoles);

        validator.assertValid(request, "query", featureSet);

        List<String> ret = featureJdbcDao.getGroupNames();

        LOGGER.debug("getGroupNames() - (LEAVE)");
        return ret;
    }

    @Override
    public List<Feature> findPermissions(ServiceRequest<FindPermissionsQuery> request) {
        LOGGER.debug("findPermissions(" + request + ") - (ENTER)");

        HashSet<USMFeature> featureSet = new HashSet<USMFeature>();
        featureSet.add(USMFeature.viewRoles);
        featureSet.add(USMFeature.manageRoles);

        validator.assertValid(request, "query", featureSet);

        List<FeatureEntity> response = featureJpaDao.findFeatures(request.getBody());

        List<Feature> ret = new ArrayList<>();
        for (FeatureEntity feature : response) {
            ret.add(converter.convertWithRoles(feature));
        }

        LOGGER.debug("findPermissions() - (LEAVE)");
        return ret;
    }

    @Override
    public List<ComprehensiveRole> getRoles(ServiceRequest<RoleQuery> request)
            throws IllegalArgumentException, UnauthorisedException, RuntimeException {
        LOGGER.debug("getRoles(" + request + ") - (ENTER)");

        HashSet<USMFeature> featureSet = new HashSet<USMFeature>();
        featureSet.add(USMFeature.viewRoles);
        featureSet.add(USMFeature.manageRoles);

        validator.assertValid(request, "query", featureSet);
        List<ComprehensiveRole> ret = roleJdbcDao.getRoles();

        LOGGER.debug("getRoles() - (LEAVE)");
        return ret;
    }

}
