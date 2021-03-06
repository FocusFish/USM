package fish.focus.uvms.usm.administration.service.organisation.impl;

import fish.focus.uvms.audit.model.mapper.AuditLogModelMapper;
import fish.focus.uvms.usm.information.entity.ChannelEntity;
import fish.focus.uvms.usm.information.entity.EndPointContactEntity;
import fish.focus.uvms.usm.information.entity.EndPointEntity;
import fish.focus.uvms.usm.information.entity.OrganisationEntity;
import fish.focus.uvms.usm.administration.domain.*;
import fish.focus.uvms.usm.administration.service.AuditProducer;
import fish.focus.uvms.usm.administration.service.organisation.OrganisationService;
import fish.focus.uvms.usm.administration.service.person.impl.PersonJpaDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * Stateless session bean implementation of the OrganisationService
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class OrganisationServiceBean implements OrganisationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrganisationServiceBean.class);

    @Inject
    private OrganisationJdbcDao jdbcDao;
    @Inject
    private OrganisationJpaDao jpaDao;
    @Inject
    private EndPointJpaDao endPointJpaDao;
    @Inject
    private EndPointContactJpaDao endPointContactJpaDao;
    @Inject
    private ChannelJpaDao channelJpaDao;
    @Inject
    private OrganisationValidator validator;
    @Inject
    private OrganisationConverter converter;
    @Inject
    private PersonJpaDao personJpaDao;
    @Inject
    private AuditProducer auditProducer;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Organisation createOrganisation(ServiceRequest<Organisation> request) {
        LOGGER.debug("createOrganisation(" + request + ") - (ENTER)");

        validator.assertValid(request, USMFeature.manageOrganisations, true);

        OrganisationEntity entity = jpaDao.read(request.getBody().getName());

        if (entity != null) {
            throw new IllegalArgumentException("The organisation with name " + request.getBody().getName() + " already exists");
        }
        entity = converter.convert(request.getBody());
        String parent = request.getBody().getParent();
        if (parent != null) {
            entity.setParentOrganisation(jpaDao.read(parent));
        }
        entity.setCreatedBy(request.getRequester());
        entity.setCreatedOn(new Date());
        entity = jpaDao.create(entity);

        String auditLog = AuditLogModelMapper.mapToAuditLog(USMApplication.USM.name(),
                AuditOperationEnum.CREATE.getValue(), AuditObjectTypeEnum.ORGANISATION.getValue() + " " +
                        request.getBody().getName(), request.getBody().getDescription(), request.getRequester());
        auditProducer.sendModuleMessage(auditLog);

        LOGGER.debug("createOrganisation() - (LEAVE)");
        return converter.convertEntityToDomain(entity);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Organisation updateOrganisation(ServiceRequest<Organisation> request) {
        LOGGER.debug("updateOrganisation(" + request + ") - (ENTER)");

        validator.assertValid(request, USMFeature.manageOrganisations, false);

        OrganisationEntity entity = jpaDao.read(request.getBody().getOrganisationId());

        if (entity == null) {
            throw new IllegalArgumentException("The organisation does not exist!");
        }

        if (!entity.getName().equals(request.getBody().getName()) && jdbcDao.organisationNameExists(request.getBody().getName())) {
            throw new IllegalArgumentException("Organisation name already exists.");
        }

        entity.setModifiedBy(request.getRequester());
        entity.setModifiedOn(new Date());
        converter.update(entity, request.getBody());

        if (request.getBody().getParent() != null) {
            OrganisationEntity parent = jpaDao.read(request.getBody().getParent());
            entity.setParentOrganisation(parent);
        } else { //delete parent
            entity.setParentOrganisation(null);
        }
        entity = jpaDao.update(entity);

        String auditLog = AuditLogModelMapper.mapToAuditLog(USMApplication.USM.name(),
                AuditOperationEnum.UPDATE.getValue(), AuditObjectTypeEnum.ORGANISATION.getValue() + " " +
                        request.getBody().getName(), request.getBody().getDescription(), request.getRequester());
        auditProducer.sendModuleMessage(auditLog);

        LOGGER.debug("updateOrganisation() - (LEAVE)");
        return converter.convertEntityToDomain(entity);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteOrganisation(ServiceRequest<Long> request) {
        LOGGER.debug("deleteOrganisation(" + request + ") - (ENTER)");

        validator.assertValid(request, USMFeature.manageOrganisations, "organisationId");
        OrganisationEntity entity = jpaDao.read(request.getBody());
        if (entity.getUserList() != null && !entity.getUserList().isEmpty()) {
            throw new IllegalArgumentException("This organisation is assigned to some users and cannot be deleted.");
        }
        if (entity.getChildOrganisationList() != null && !entity.getChildOrganisationList().isEmpty()) {
            throw new IllegalArgumentException("This organisation is parent to some organisations and cannot be deleted.");
        }
        jpaDao.delete(request.getBody());

        String auditLog = AuditLogModelMapper.mapToAuditLog(USMApplication.USM.name(), AuditOperationEnum.DELETE.getValue(),
                AuditObjectTypeEnum.ORGANISATION.getValue() + " " + request.getBody(), "" + request.getBody(), request.getRequester());
        auditProducer.sendModuleMessage(auditLog);

        LOGGER.debug("deleteOrganisation() - (LEAVE)");
    }

    @Override
    public Organisation getOrganisation(ServiceRequest<Long> request) {
        LOGGER.debug("getOrganisationById(" + request + ") - (ENTER)");

        HashSet<USMFeature> featureSet = new HashSet<USMFeature>();
        featureSet.add(USMFeature.viewOrganisations);
        featureSet.add(USMFeature.manageOrganisations);

        validator.assertValid(request, "organisationId", featureSet);

        OrganisationEntity entity = jpaDao.read(request.getBody());
        int assigned = 0;
        if (entity != null) {
            List<EndPointEntity> endPointList = endPointJpaDao.getEndPointsByOrganisationId(entity.getOrganisationId());
            entity.setEndPointList(endPointList);
            assigned = jdbcDao.getAssociatedUsers(entity.getOrganisationId());
        }
        Organisation ret = converter.convert(entity, assigned);

        LOGGER.debug("getOrganisationById() - (LEAVE): " + ret);
        return ret;
    }

    @Override
    public Organisation getOrganisationByName(ServiceRequest<String> request) {
        LOGGER.debug("getOrganisationByName(" + request + ") - (ENTER)");

        HashSet<USMFeature> featureSet = new HashSet<USMFeature>();
        featureSet.add(USMFeature.viewOrganisations);
        featureSet.add(USMFeature.manageOrganisations);

        validator.assertValid(request, "name", featureSet);

        OrganisationEntity entity = jpaDao.read(request.getBody());
        if (entity != null) {
            List<EndPointEntity> endPointList = endPointJpaDao.getEndPointsByOrganisationId(entity.getOrganisationId());
            entity.setEndPointList(endPointList);
        }
        Organisation ret = converter.convert(entity, 0);

        LOGGER.debug("getOrganisationByName) - (LEAVE): " + ret);
        return ret;
    }

    @Override
    public List<OrganisationNameResponse> getOrganisationNames(ServiceRequest<OrganisationQuery> request) {
        LOGGER.debug("getOrganisationNames(" + request + ") - (ENTER)");

        HashSet<USMFeature> featureSet = new HashSet<>();
        featureSet.add(USMFeature.viewOrganisations);
        featureSet.add(USMFeature.manageOrganisations);

        validator.assertValid(request, "query", featureSet);
        List<OrganisationNameResponse> ret = jdbcDao.getOrganisationNames();

        LOGGER.debug("getOrganisationNames() - (LEAVE)");
        return ret;
    }

    @Override
    public List<String> getOrganisationParentNames(ServiceRequest<Long> request) {
        LOGGER.debug("getOrganisationParentNames(" + request + ") - (ENTER)");

        HashSet<USMFeature> featureSet = new HashSet<>();
        featureSet.add(USMFeature.viewOrganisations);
        featureSet.add(USMFeature.manageOrganisations);

        validator.assertValid(request, "query", featureSet);

        List<String> ret;
        if (-1 != request.getBody()) {
            ret = jdbcDao.getOrganisationParentNames(request.getBody());
        } else {
            ret = jdbcDao.getOrganisationParentNames(null);
        }

        LOGGER.debug("getOrganisationParentNames() - (LEAVE)");
        return ret;
    }

    @Override
    public List<String> getNationNames(ServiceRequest<OrganisationQuery> request) {
        LOGGER.debug("getNationNames(" + request + ") - (ENTER)");

        HashSet<USMFeature> featureSet = new HashSet<USMFeature>();
        featureSet.add(USMFeature.viewOrganisations);
        featureSet.add(USMFeature.manageOrganisations);

        validator.assertValid(request, "query", featureSet);
        List<String> ret = jdbcDao.getNationNames();

        LOGGER.debug("getNationNames() - (LEAVE)");
        return ret;
    }

    @Override
    public PaginationResponse<Organisation> findOrganisations(ServiceRequest<FindOrganisationsQuery> request)
            throws IllegalArgumentException, UnauthorisedException, RuntimeException {
        LOGGER.debug("findOrganisations(" + request + ") - (ENTER)");

        HashSet<USMFeature> featureSet = new HashSet<USMFeature>();
        featureSet.add(USMFeature.viewOrganisations);
        featureSet.add(USMFeature.manageOrganisations);

        validator.assertValid(request, "query", featureSet);

        PaginationResponse<Organisation> ret = jdbcDao.findOrganisations(request.getBody());

        LOGGER.debug("findOrganisations() - (LEAVE)");
        return ret;
    }

    @Override
    public EndPoint getEndPoint(ServiceRequest<Long> request)
            throws IllegalArgumentException, UnauthorisedException, RuntimeException {
        LOGGER.debug("getEndPoint(" + request.getBody() + ") - (ENTER)");

        HashSet<USMFeature> featureSet = new HashSet<>();
        featureSet.add(USMFeature.viewOrganisations);
        featureSet.add(USMFeature.manageOrganisations);

        validator.assertValid(request, "query", featureSet);
        EndPoint ret = null;
        EndPointEntity entity = endPointJpaDao.read(request.getBody());
        if (entity != null) {
            entity.setChannel(channelJpaDao.findByEndPointId(entity.getEndPointId()));
            entity.setEndPointContact(endPointContactJpaDao.findContactByEndPointId(entity.getEndPointId()));
            ret = converter.convertEndPointEntityToDomain(entity, true);
        }
        LOGGER.debug("getEndPoint() - (LEAVE)");
        return ret;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public EndPoint createEndPoint(ServiceRequest<EndPoint> request)
            throws IllegalArgumentException, UnauthorisedException, RuntimeException {
        LOGGER.debug("createEndPoint(" + request + ") - (ENTER)");

        validator.assertValidEndPoint(request, USMFeature.manageOrganisations, true);
        EndPointEntity entity = endPointJpaDao.retrieveEndPointByOrganisation(request.getBody().getName(),
                request.getBody().getOrganisationName());

        if (entity != null) {
            throw new IllegalArgumentException("The name " + entity.getName() +
                    " of the enpoint is already associated with the organisation " + entity.getOrganisation().getName());
        }
        entity = new EndPointEntity();
        converter.convertEndPointDomainToEntity(entity, request.getBody(), false);
        entity.setOrganisation(jpaDao.read(request.getBody().getOrganisationName()));
        entity.setCreatedBy(request.getRequester());
        entity.setCreatedOn(new Date());
        entity = endPointJpaDao.create(entity);

        String auditLog = AuditLogModelMapper.mapToAuditLog(USMApplication.USM.name(),
                AuditOperationEnum.CREATE.getValue(), AuditObjectTypeEnum.ENDPOINT.getValue() + " " +
                        request.getBody().getName(), request.getBody().getDescription(), request.getRequester());
        auditProducer.sendModuleMessage(auditLog);

        LOGGER.debug("createEndPoint() - (LEAVE)");
        return converter.convertEndPointEntityToDomain(entity, true);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public EndPoint updateEndPoint(ServiceRequest<EndPoint> request)
            throws IllegalArgumentException, UnauthorisedException, RuntimeException {
        LOGGER.debug("updateEndPoint(" + request + ") - (ENTER)");

        validator.assertValidEndPoint(request, USMFeature.manageOrganisations, false);

        EndPointEntity entity = endPointJpaDao.retrieveEndPointByOrganisation(request.getBody().getName(),
                request.getBody().getOrganisationName());
        if (entity == null) {
            throw new IllegalArgumentException("There is no such " + request.getBody().getName() + " endpoint in the database associated with given organisation");
        }
        converter.convertEndPointDomainToEntity(entity, request.getBody(), true);
        entity.setModifiedBy(request.getRequester());
        entity.setModifiedOn(new Date());
        EndPoint ret = converter.convertEndPointEntityToDomain(endPointJpaDao.update(entity), false);

        String auditLog = AuditLogModelMapper.mapToAuditLog(USMApplication.USM.name(), AuditOperationEnum.UPDATE.getValue(),
                AuditObjectTypeEnum.ENDPOINT.getValue() + " " + request.getBody().getName(), request.getBody().getDescription(), request.getRequester());
        auditProducer.sendModuleMessage(auditLog);

        LOGGER.debug("updateEndPoint() - (LEAVE)");
        return ret;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteEndPoint(ServiceRequest<Long> request)
            throws IllegalArgumentException, UnauthorisedException, RuntimeException {
        LOGGER.debug("deleteEndPoint(" + request + ") - (ENTER)");

        validator.assertValid(request, USMFeature.manageOrganisations, "endpointId");
        endPointJpaDao.delete(request.getBody());

        String auditLog = AuditLogModelMapper.mapToAuditLog(USMApplication.USM.name(), AuditOperationEnum.DELETE.getValue(),
                AuditObjectTypeEnum.ENDPOINT.getValue() + " " + request.getBody(), "" + request.getBody(), request.getRequester());
        auditProducer.sendModuleMessage(auditLog);

        LOGGER.debug("deleteEndPoint() - (LEAVE)");
    }

    @Override
    public Channel getChannel(ServiceRequest<Long> request)
            throws IllegalArgumentException, UnauthorisedException, RuntimeException {
        LOGGER.debug("getChannel(" + request + ") - (ENTER)");

        HashSet<USMFeature> featureSet = new HashSet<USMFeature>();
        featureSet.add(USMFeature.viewOrganisations);
        featureSet.add(USMFeature.manageOrganisations);

        validator.assertValid(request, "query", featureSet);
        ChannelEntity entity = channelJpaDao.read(request.getBody());
        Channel ret = converter.convertChannelEntityToDomain(entity);

        LOGGER.debug("getChannel() - (LEAVE)");
        return ret;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Channel createChannel(ServiceRequest<Channel> request)
            throws IllegalArgumentException, UnauthorisedException, RuntimeException {
        LOGGER.debug("createChannel(" + request + ") - (ENTER)");

        validator.assertValidChannel(request, USMFeature.manageOrganisations, true);

        ChannelEntity entity = channelJpaDao.findByDataFlowServiceEndPoint(request.getBody().getDataflow(),
                request.getBody().getService(), request.getBody().getEndpointId());
        if (entity != null) {
            throw new IllegalArgumentException("The channel with the given dataflow, service, end point is already created!");
        }
        entity = new ChannelEntity();
        converter.convertChannelDomainToEntity(entity, request.getBody());
        entity.setEndPoint(endPointJpaDao.read(request.getBody().getEndpointId()));
        entity.setCreatedBy(request.getRequester());
        entity.setCreatedOn(new Date());
        entity = channelJpaDao.create(entity);

        String auditLog = AuditLogModelMapper.mapToAuditLog(USMApplication.USM.name(),
                AuditOperationEnum.CREATE.getValue(), AuditObjectTypeEnum.CHANNEL.getValue() + " " +
                        request.getBody().getEndpointId(), "" + request.getBody().getEndpointId(), request.getRequester());
        auditProducer.sendModuleMessage(auditLog);
        LOGGER.debug("createChannel() - (LEAVE)");
        return converter.convertChannelEntityToDomain(entity);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Channel updateChannel(ServiceRequest<Channel> request)
            throws IllegalArgumentException, UnauthorisedException, RuntimeException {
        LOGGER.debug("updateChannel(" + request + ") - (ENTER)");

        validator.assertValidChannel(request, USMFeature.manageOrganisations, false);
        Channel ret = new Channel();

        ChannelEntity entity = channelJpaDao.read(request.getBody().getChannelId());
        if (entity == null) {
            throw new IllegalArgumentException("The indicated channel does not exist anymore");
        }
        ChannelEntity uniqueEntity = channelJpaDao.findByDataFlowServiceEndPoint(request.getBody().getDataflow(),
                request.getBody().getService(), request.getBody().getEndpointId());
        if (uniqueEntity != null && !uniqueEntity.getChannelId().equals(entity.getChannelId())) {
            throw new IllegalArgumentException("The channel with the given dataflow, service, end point combination already exists!");
        }

        converter.convertChannelDomainToEntity(entity, request.getBody());
        if (!entity.getEndPoint().getEndPointId().equals(request.getBody().getEndpointId())) {
            entity.setEndPoint(endPointJpaDao.read(request.getBody().getEndpointId()));
        }
        entity.setModifiedBy(request.getRequester());
        entity.setModifiedOn(new Date());

        ret = converter.convertChannelEntityToDomain(channelJpaDao.update(entity));

        String auditLog = AuditLogModelMapper.mapToAuditLog(USMApplication.USM.name(),
                AuditOperationEnum.UPDATE.getValue(), AuditObjectTypeEnum.CHANNEL.getValue() + " " +
                        request.getBody().getEndpointId(), "" + request.getBody().getEndpointId(), request.getRequester());
        auditProducer.sendModuleMessage(auditLog);

        LOGGER.debug("updateChannel() - (LEAVE)");
        return ret;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteChannel(ServiceRequest<Long> request)
            throws IllegalArgumentException, UnauthorisedException, RuntimeException {
        LOGGER.debug("deleteChannel(" + request + ") - (ENTER)");

        validator.assertValid(request, USMFeature.manageOrganisations, "channelId");
        channelJpaDao.delete(request.getBody());

        String auditLog = AuditLogModelMapper.mapToAuditLog(USMApplication.USM.name(),
                AuditOperationEnum.DELETE.getValue(), AuditObjectTypeEnum.CHANNEL.getValue() + " " +
                        request.getBody(), "" + request.getBody(), request.getRequester());
        auditProducer.sendModuleMessage(auditLog);

        LOGGER.debug("deleteChannel() - (LEAVE)");
    }

    @Override
    public EndPointContact getContact(ServiceRequest<Long> request)
            throws IllegalArgumentException, UnauthorisedException, RuntimeException {
        LOGGER.debug("getContact(" + request + ") - (ENTER)");

        HashSet<USMFeature> featureSet = new HashSet<USMFeature>();
        featureSet.add(USMFeature.viewOrganisations);
        featureSet.add(USMFeature.manageOrganisations);

        validator.assertValid(request, "query", featureSet);
        EndPointContactEntity entity = endPointContactJpaDao.read(request.getBody());

        EndPointContact ret = converter.convertEndPointContactEntityToDomain(entity);

        LOGGER.debug("getContact() - (LEAVE)");
        return ret;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public EndPointContact assignContact(ServiceRequest<EndPointContact> request)
            throws IllegalArgumentException, UnauthorisedException, RuntimeException {
        LOGGER.debug("assignContact(" + request + ") - (ENTER)");

        validator.assertValidEndpoint(request, USMFeature.manageOrganisations, true);
        List<EndPointContactEntity> contactList = endPointContactJpaDao.findContactByEndPointId(request.getBody().getEndPointId());
        for (EndPointContactEntity element : contactList) {
            if (element.getPerson().getPersonId().equals(request.getBody().getPersonId())) {
                throw new IllegalArgumentException("This contact was already added to the current endpoint");
            }
        }
        EndPointContactEntity epcontact = new EndPointContactEntity();
        epcontact.setEndPoint(endPointJpaDao.read(request.getBody().getEndPointId()));
        epcontact.setPerson(personJpaDao.read(request.getBody().getPersonId()));

        epcontact = endPointContactJpaDao.create(epcontact);

        LOGGER.debug("assignContact() - (LEAVE)");
        return converter.convertEndPointContactEntityToDomain(epcontact);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void removeContact(ServiceRequest<EndPointContact> request)
            throws IllegalArgumentException, UnauthorisedException, RuntimeException {
        LOGGER.debug("removeContact(" + request + ") - (ENTER)");

        validator.assertValidEndpoint(request, USMFeature.manageOrganisations, false);
        EndPointContactEntity epcontact = endPointContactJpaDao.read(request.getBody().getEndPointContactId());
        if (epcontact != null) {
            endPointContactJpaDao.delete(epcontact);
        }

        String auditLog = AuditLogModelMapper.mapToAuditLog(USMApplication.USM.name(),
                AuditOperationEnum.REMOVE.getValue(), AuditObjectTypeEnum.ENDPOINT_CONTACT.getValue() + " " +
                        request.getBody().getEndPointContactId(), "" + request.getBody().getEndPointContactId(), request.getRequester());
        auditProducer.sendModuleMessage(auditLog);
        LOGGER.debug("removeContact() - (LEAVE)");
    }

}
