package fish.focus.uvms.usm.authentication.rest;

import fish.focus.uvms.usm.policy.service.impl.PolicyProvider;
import fish.focus.uvms.usm.session.service.impl.SessionDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@Path("administration")
public class AdministrationResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdministrationResource.class);

    @EJB
    private PolicyProvider policyProvider;
    @EJB
    private SessionDao sessionDao;

    /**
     * Deletes all user sessions.
     *
     * @return an empty response with OK status, or an  INTERNAL_SERVER_ERROR
     * status in case an internal error prevented  processing the request.
     */
    @DELETE
    @Path("userSessions")
    @Produces("application/json")
    public Response deleteSessions() {
        LOGGER.debug("deleteSessions() - (ENTER)");

        Response response;
        try {
            sessionDao.deleteSessions();
            response = Response.ok().build();
        } catch (Exception exc) {
            response = ExceptionHandler.handleException(exc);
        }

        LOGGER.debug("deleteSessions() - (LEAVE)");
        return response;
    }

    /**
     * Clears the policy cache.
     *
     * @return an empty response with OK status, or an  INTERNAL_SERVER_ERROR
     * status in case an internal error prevented  processing the request.
     */
    @DELETE
    @Path("policyCache")
    @Produces("application/json")
    public Response clearPolicyCache() {
        LOGGER.debug("clearPolicyCache() - (ENTER)");

        Response response;
        try {
            policyProvider.reset();
            response = Response.ok().build();
        } catch (Exception exc) {
            response = ExceptionHandler.handleException(exc);
        }

        LOGGER.debug("clearPolicyCache() - (LEAVE)");
        return response;
    }

}
