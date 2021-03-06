package fish.focus.uvms.usm.administration.service.scope.impl;

import fish.focus.uvms.audit.model.mapper.AuditLogModelMapper;
import fish.focus.uvms.usm.information.entity.DatasetEntity;
import fish.focus.uvms.usm.information.entity.ScopeEntity;
import fish.focus.uvms.usm.information.entity.UserContextEntity;
import fish.focus.uvms.usm.information.service.impl.DataSetJpaDao;
import fish.focus.uvms.usm.administration.domain.*;
import fish.focus.uvms.usm.administration.service.AuditProducer;
import fish.focus.uvms.usm.administration.service.scope.ScopeService;
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
 * Stateless session bean implementation of the ScopeService
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ScopeServiceBean implements ScopeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScopeServiceBean.class);

    @Inject
    private ScopeJdbcDao jdbcDao;

    @Inject
    private ScopeJpaDao jpaDao;

    @Inject
    private ScopeValidator validator;

    @Inject
    private DataSetJpaDao datasetJpaDao;
    @Inject
    private UserJpaDao userJpaDao;

    @Inject
    private AuditProducer auditProducer;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Scope createScope(ServiceRequest<Scope> request)
            throws IllegalArgumentException, UnauthorisedException, RuntimeException {
        LOGGER.debug("createScope(" + request + ") - (ENTER)");

        validator.assertValid(request, USMFeature.manageScopes, true);

        if (jdbcDao.scopeExists(request.getBody().getName())) {
            throw new IllegalArgumentException("Scope already exists.");
        }
        ScopeEntity scope = new ScopeEntity();
        convertRequestToEntity(scope, request.getBody());
        scope.setCreatedBy(request.getRequester());
        scope.setCreatedOn(new Date());
        scope = jpaDao.create(scope);

        Scope ret = convertEntityToResponse(scope);

        String auditLog = AuditLogModelMapper.mapToAuditLog(USMApplication.USM.name(),
                AuditOperationEnum.CREATE.getValue(), AuditObjectTypeEnum.SCOPE.getValue() + " " +
                        request.getBody().getName(), request.getBody().getDescription(), request.getRequester());
        auditProducer.sendModuleMessage(auditLog);

        LOGGER.debug("createScope() - (LEAVE)");
        return ret;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Scope updateScope(ServiceRequest<Scope> request)
            throws IllegalArgumentException, UnauthorisedException, RuntimeException {
        LOGGER.debug("updateScope(" + request + ") - (ENTER)");

        validator.assertValid(request, USMFeature.manageScopes, false);

        ScopeEntity scope = jpaDao.read(request.getBody().getScopeId());
        if (scope == null) {
            throw new IllegalArgumentException("Scope does not exist");
        }
        convertRequestToEntity(scope, request.getBody());
        scope.setModifiedBy(request.getRequester());
        scope.setModifiedOn(new Date());
        scope = jpaDao.update(scope);

        Scope ret = convertEntityToResponse(scope);

        String auditLog = AuditLogModelMapper.mapToAuditLog(USMApplication.USM.name(),
                AuditOperationEnum.UPDATE.getValue(), AuditObjectTypeEnum.SCOPE.getValue() + " " +
                        request.getBody().getName(), request.getBody().getDescription(), request.getRequester());
        auditProducer.sendModuleMessage(auditLog);

        LOGGER.debug("updateScope() - (LEAVE)");
        return ret;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteScope(ServiceRequest<Long> request)
            throws IllegalArgumentException, UnauthorisedException, RuntimeException {
        LOGGER.debug("deleteScope(" + request + ") - (ENTER)");

        validator.assertValid(request, USMFeature.manageScopes, "scopeId");
        jpaDao.delete(request.getBody());

        String auditLog = AuditLogModelMapper.mapToAuditLog(USMApplication.USM.name(),
                AuditOperationEnum.DELETE.getValue(), AuditObjectTypeEnum.SCOPE.getValue() + " " +
                        request.getBody(), "" + request.getBody(), request.getRequester());
        auditProducer.sendModuleMessage(auditLog);

        LOGGER.debug("deleteScope() - (LEAVE)");
    }

    @Override
    public Scope getScope(ServiceRequest<GetScopeQuery> request)
            throws IllegalArgumentException, UnauthorisedException, RuntimeException {
        LOGGER.debug("getScope(" + request + ") - (ENTER)");

        HashSet<USMFeature> featureSet = new HashSet<USMFeature>();
        featureSet.add(USMFeature.viewScopes);
        featureSet.add(USMFeature.manageScopes);

        validator.assertValid(request, "query", featureSet);

        Scope ret = jdbcDao.getScope(request.getBody());
        if (ret != null) {
            List<UserContextEntity> activeUsers = userJpaDao.findActiveUsersForScope(request.getBody().getScopeId());
            ret.setActiveUsers(activeUsers == null ? 0 : activeUsers.size());
        }
        LOGGER.debug("getScope() - (LEAVE)");
        return ret;
    }

    @Override
    public PaginationResponse<Scope> findScopes(ServiceRequest<FindScopesQuery> request)
            throws IllegalArgumentException, UnauthorisedException, RuntimeException {
        LOGGER.debug("findScopes(" + request + ") - (ENTER)");

        HashSet<USMFeature> featureSet = new HashSet<USMFeature>();
        featureSet.add(USMFeature.viewScopes);
        featureSet.add(USMFeature.manageScopes);

        validator.assertValid(request, "query", featureSet);

        PaginationResponse<Scope> ret = jdbcDao.findScopes(request.getBody());

        LOGGER.debug("findScopes() - (LEAVE)");
        return ret;
    }

    @Override
    public List<DataSet> findDataSet(ServiceRequest<FindDataSetQuery> request)
            throws IllegalArgumentException, UnauthorisedException, RuntimeException {

        LOGGER.debug("findDataSet(" + request + ") - (ENTER)");

        HashSet<USMFeature> featureSet = new HashSet<USMFeature>();
        featureSet.add(USMFeature.viewScopes);
        featureSet.add(USMFeature.manageScopes);

        validator.assertValid(request, "query", featureSet);

        List<DataSet> ret = jdbcDao.findDataSets(request.getBody());

        LOGGER.debug("findDataSet() - (LEAVE)");
        return ret;
    }

    private Scope convertEntityToResponse(ScopeEntity entity) {
        Scope response = new Scope();
        response.setScopeId(entity.getScopeId());
        response.setName(entity.getName());
        response.setDescription(entity.getDescription());
        response.setStatus(entity.getStatus());
        response.setActiveFrom(entity.getActiveFrom());
        response.setActiveTo(entity.getActiveTo());
        response.setDataFrom(entity.getDataFrom());
        response.setDataTo(entity.getDataTo());

        if (entity.getDatasetList() != null && !entity.getDatasetList().isEmpty()) {
            List<DataSet> list = new ArrayList<>();
            for (DatasetEntity dataset : entity.getDatasetList()) {
                DataSet element = new DataSet();
                element.setDatasetId(dataset.getDatasetId());
                element.setDescription(dataset.getDescription());
                element.setApplication(dataset.getApplication().getName());
                element.setCategory(dataset.getCategory());
                element.setName(dataset.getName());
                list.add(element);
            }
            response.setDataSets(list);
        }

        return response;
    }

    private void convertRequestToEntity(ScopeEntity entity, Scope scope) {
        entity.setName(scope.getName());
        entity.setDescription(scope.getDescription());
        entity.setActiveFrom(scope.getActiveFrom());
        entity.setActiveTo(scope.getActiveTo());
        entity.setDataFrom(scope.getDataFrom());
        entity.setDataTo(scope.getDataTo());
        entity.setStatus(scope.getStatus());
        entity.setScopeId(scope.getScopeId());

        //in case of take care as well of manage datasets
        if (scope.getUpdateDatasets()) {
            if (scope.getScopeId() != null) {
                List<DataSet> selectedDatasets = scope.getDataSets();
                List<DatasetEntity> datasetListEntity = entity.getDatasetList();
                List<DatasetEntity> actualList = new ArrayList<>();

                if (datasetListEntity != null && selectedDatasets != null) {
                    for (DatasetEntity dataset : datasetListEntity) {
                        DataSet comparedElement = new DataSet(dataset.getDatasetId());
                        if (selectedDatasets.contains(comparedElement)) {
                            actualList.add(dataset);
                            selectedDatasets.remove(comparedElement);
                        } else {
                            dataset.getScopeList().remove(entity);
                        }
                    }
                }

                if (selectedDatasets != null && !selectedDatasets.isEmpty()) {
                    for (DataSet dataset : selectedDatasets) {
                        DatasetEntity datasetEntity = datasetJpaDao.read(dataset.getDatasetId());
                        actualList.add(datasetEntity);
                    }
                }

                entity.setDatasetList(actualList);

                if (!actualList.isEmpty()) {
                    //add child to parent association
                    for (DatasetEntity element : actualList) {
                        element.getScopeList().add(entity);
                    }
                }
            }
        }
    }

    @Override
    public List<String> getCategoryNames(ServiceRequest<String> request)
            throws IllegalArgumentException, UnauthorisedException, RuntimeException {
        LOGGER.debug("getCategoryNames(" + request + ") - (ENTER)");

        HashSet<USMFeature> featureSet = new HashSet<USMFeature>();
        featureSet.add(USMFeature.viewScopes);
        featureSet.add(USMFeature.manageScopes);

        validator.assertValid(request, "query", featureSet);
        List<String> names = jdbcDao.getCategoryNames();

        LOGGER.debug("getCategoryNames() - (LEAVE)");
        return names;
    }

    @Override
    public List<DataSet> findDataSets(ServiceRequest<FindDataSetQuery> request)
            throws IllegalArgumentException, UnauthorisedException, RuntimeException {
        LOGGER.debug("findDataSets(" + request + ") - (ENTER)");

        HashSet<USMFeature> featureSet = new HashSet<USMFeature>();
        featureSet.add(USMFeature.viewScopes);
        featureSet.add(USMFeature.manageScopes);

        validator.assertValid(request, "query", featureSet);

        List<DatasetEntity> response = jpaDao.findDatasets(request.getBody());
        List<DataSet> ret = new ArrayList<>();
        for (DatasetEntity dataset : response) {
            ret.add(convertEntytyToDatasetDomain(dataset));
        }

        LOGGER.debug("findDataSets() - (LEAVE)");
        return ret;
    }

    private DataSet convertEntytyToDatasetDomain(DatasetEntity entity) {
        DataSet ret = new DataSet();
        ret.setDatasetId(entity.getDatasetId());
        ret.setName(entity.getName());
        ret.setDescription(entity.getDescription());
        ret.setApplication(entity.getApplication().getName());
        ret.setCategory(entity.getCategory());

        List<Scope> scopes = new ArrayList<>();
        List<ScopeEntity> scopeEntities = entity.getScopeList();
        for (ScopeEntity scopeEntity : scopeEntities) {
            Scope scope = new Scope();
            scope.setScopeId(scopeEntity.getScopeId());
            scopes.add(scope);
        }
        ret.setScopes(scopes);
        return ret;
    }

    @Override
    public List<ComprehensiveScope> getScopes(ServiceRequest<ScopeQuery> request)
            throws IllegalArgumentException, UnauthorisedException, RuntimeException {
        LOGGER.debug("getScopes(" + request + ") - (ENTER)");

        HashSet<USMFeature> featureSet = new HashSet<USMFeature>();
        featureSet.add(USMFeature.viewScopes);
        featureSet.add(USMFeature.manageScopes);

        validator.assertValid(request, "query", featureSet);
        List<ComprehensiveScope> ret = jdbcDao.getScopes();

        LOGGER.debug("getScopes() - (LEAVE)");
        return ret;
    }
}
