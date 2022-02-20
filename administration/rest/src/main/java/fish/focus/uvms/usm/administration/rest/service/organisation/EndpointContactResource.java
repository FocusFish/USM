package fish.focus.uvms.usm.administration.rest.service.organisation;

import fish.focus.uvms.usm.administration.domain.EndPointContact;
import fish.focus.uvms.usm.administration.domain.ServiceRequest;
import fish.focus.uvms.usm.administration.rest.common.ExceptionHandler;
import fish.focus.uvms.usm.administration.service.organisation.OrganisationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@Path("endpointcontact")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class EndpointContactResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(EndpointContactResource.class);

    @EJB
    private OrganisationService service;

    @Context
    private HttpServletRequest servletRequest;

    /**
     * Creates a new endPointContact
     *
     * @param jwtToken        the JWT token identifying the service requester, optional
     *                        if the service requester is authenticated by the J2EE container
     * @param roleName        the name of the Role of the selected UserContext (optional)
     * @param scopeName       the name of the Scope of the selected UserContext
     *                        (optional)
     * @param endPointContact the new endPointContact to be created
     * @return Response with status OK (200) in case of success otherwise status
     * NOT_MODIFIED or a BAD_REQUEST error code in case the provided input
     * incomplete, with an INTERNAL_SERVER_ERROR error code in case an internal
     * error prevented fulfilling the request or UnauthorisedException with an
     * FORBIDDEN error code in case the end user is not authorised to perform the
     * operation
     */
    @POST
    public Response assignContact(@HeaderParam("authorization") String jwtToken,
                                  @HeaderParam("roleName") String roleName,
                                  @HeaderParam("scopeName") String scopeName,
                                  EndPointContact endPointContact) {
        LOGGER.debug("assignContact() - (ENTER)");

        Response ret;

        try {
            ServiceRequest<EndPointContact> request = new ServiceRequest<>();
            request.setRequester(servletRequest.getRemoteUser());
            request.setRoleName(roleName);
            request.setScopeName(scopeName);
            request.setBody(endPointContact);
            EndPointContact assignedContact = service.assignContact(request);

            Response.Status status;
            if (assignedContact.getEndPointContactId() != null) {
                status = Response.Status.OK;
            } else {
                status = Response.Status.NOT_MODIFIED;
            }
            ret = Response.status(status).entity(assignedContact).build();

        } catch (Exception e) {
            ret = ExceptionHandler.handleException(e);
        }

        LOGGER.debug("assignContact() - (LEAVE)");
        return ret;
    }

    /**
     * Deletes an existing contact
     *
     * @param jwtToken          the JWT token identifying the service requester, optional
     *                          if the service requester is authenticated by the J2EE container
     * @param roleName          the name of the Role of the selected UserContext (optional)
     * @param scopeName         the name of the Scope of the selected UserContext
     *                          (optional)
     * @param endpointcontactId the identifier of the contact to be deleted
     * @return Response with status OK (200) in case of success otherwise status
     * NOT_MODIFIED or a BAD_REQUEST error code in case the provided input
     * incomplete, with an INTERNAL_SERVER_ERROR error code in case an internal
     * error prevented fulfilling the request or UnauthorisedException with an
     * FORBIDDEN error code in case the end user is not authorised to perform the
     * operation
     */
    @DELETE
    @Path("{endpointcontactId}")
    public Response removeContact(@HeaderParam("authorization") String jwtToken,
                                  @HeaderParam("roleName") String roleName,
                                  @HeaderParam("scopeName") String scopeName,
                                  @PathParam("endpointcontactId") Long endpointcontactId) {
        LOGGER.debug("removeContact(" + endpointcontactId + ") - (ENTER)");

        Response ret;

        try {
            EndPointContact endPointContact = new EndPointContact();
            endPointContact.setEndPointContactId(endpointcontactId);

            ServiceRequest<EndPointContact> request = new ServiceRequest<>();
            request.setRequester(servletRequest.getRemoteUser());
            request.setRoleName(roleName);
            request.setScopeName(scopeName);
            request.setBody(endPointContact);

            service.removeContact(request);
            Response.Status status = Response.Status.OK;
            ret = Response.status(status).build();

        } catch (Exception e) {
            ret = ExceptionHandler.handleException(e);
        }

        LOGGER.debug("removeContact() - (LEAVE)");

        return ret;
    }
}
