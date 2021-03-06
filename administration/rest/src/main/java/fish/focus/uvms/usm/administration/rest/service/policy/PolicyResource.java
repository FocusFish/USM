package fish.focus.uvms.usm.administration.rest.service.policy;

import fish.focus.uvms.usm.administration.domain.FindPoliciesQuery;
import fish.focus.uvms.usm.administration.domain.NoBody;
import fish.focus.uvms.usm.administration.domain.Policy;
import fish.focus.uvms.usm.administration.domain.ServiceRequest;
import fish.focus.uvms.usm.administration.rest.ServiceArrayResponse;
import fish.focus.uvms.usm.administration.rest.common.ExceptionHandler;
import fish.focus.uvms.usm.administration.service.policy.PolicyService;
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
import javax.ws.rs.core.Response.Status;
import java.util.List;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@Path("policies")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PolicyResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(PolicyResource.class);

    @EJB
    private PolicyService service;

    @Context
    private HttpServletRequest servletRequest;

    /**
     * Updates an existing policy property's value
     *
     * @param jwtToken the JWT token identifying the service requester, optional
     *                 if the service requester is authenticated by the J2EE container
     * @param policy   the policy property which value will be updated
     * @return Response with status OK (200) in case of success otherwise status
     * NOT_MODIFIED or a BAD_REQUEST error code in case the provided input
     * incomplete, with an INTERNAL_SERVER_ERROR error code in case an internal
     * error prevented fulfilling the request or UnauthorisedException with an
     * FORBIDDEN error code in case the end user is not authorised to perform the
     * operation
     */
    @PUT
    public Response updatePolicy(@HeaderParam("authorization") String jwtToken,
                                 Policy policy) {
        LOGGER.debug("updatePolicy(" + policy + ") - (ENTER)");

        ServiceRequest<Policy> req = new ServiceRequest<>();
        req.setRequester(servletRequest.getRemoteUser());
        req.setBody(policy);

        Response ret;
        try {
            Policy e = service.updatePolicy(req);

            ret = Response.status(Response.Status.OK).entity(e).build();
        } catch (Exception e) {
            ret = ExceptionHandler.handleException(e);
        }

        LOGGER.debug("updatePolicy() - (LEAVE)");
        return ret;
    }

    /**
     * Retrieves a list of policy properties
     *
     * @param jwtToken the JWT token identifying the service requester, optional
     *                 if the service requester is authenticated by the J2EE container
     * @param name     the requested policy property name
     * @param subject  the requested policy property subject
     * @return an OK status and the list of policy properties that meet the criteria, or a
     * BAD_REQUEST error code in case the provided input incomplete, with an
     * INTERNAL_SERVER_ERROR error code in case an internal error prevented
     * fulfilling the request or UnauthorisedException with an FORBIDDEN error
     * code in case the end user is not authorised to perform the operation
     */
    @GET
    public Response findPolicies(@HeaderParam("authorization") String jwtToken,
                                 @QueryParam("name") String name,
                                 @QueryParam("subject") String subject) {
        LOGGER.debug("findPolicies() - (ENTER)");

        ServiceRequest<FindPoliciesQuery> req = new ServiceRequest<>();
        req.setRequester(servletRequest.getRemoteUser());
        FindPoliciesQuery query = new FindPoliciesQuery();
        query.setName(name);
        query.setSubject(subject);
        req.setBody(query);

        Response ret;
        try {
            List<Policy> resp = service.findPolicies(req);

            Status statusCode;
            if (resp == null || resp.isEmpty()) {
                statusCode = Response.Status.NO_CONTENT;
            } else {
                statusCode = Response.Status.OK;
            }
            ret = Response.status(statusCode).entity(resp).build();
        } catch (Exception exc) {
            ret = ExceptionHandler.handleException(exc);
        }

        LOGGER.debug("findPolicies() - (LEAVE)");
        return ret;
    }

    /**
     * Retrieves the list of all subjects
     *
     * @param jwtToken the JWT token identifying the service requester, optional
     *                 if the service requester is authenticated by the J2EE container
     * @return the list of the all subjects with an OK status, or a BAD_REQUEST error
     * code in case the provided input incomplete, with an INTERNAL_SERVER_ERROR error code
     * in case an internal error prevented fulfilling the request or UnauthorisedException with
     * an FORBIDEN error code in case the end user is not authorised to perform the operation
     */
    @GET
    @Path("subjects")
    public Response getSubjects(@HeaderParam("authorization") String jwtToken) {
        LOGGER.debug("getSubjects() - (ENTER)");

        ServiceRequest<NoBody> req = new ServiceRequest<>();
        req.setRequester(servletRequest.getRemoteUser());

        Response ret;
        try {
            List<String> lst = service.getSubjects(req);
            Status status;
            if (lst == null || lst.isEmpty()) {
                status = Response.Status.NO_CONTENT;
            } else {
                status = Response.Status.OK;
            }

            ServiceArrayResponse<String> sar = new ServiceArrayResponse<>();
            sar.setResults(lst);
            ret = Response.status(status).entity(sar).build();
        } catch (Exception e) {
            ret = ExceptionHandler.handleException(e);
        }

        LOGGER.debug("getSubjects() - (LEAVE)");
        return ret;
    }
}
