package eu.europa.ec.mare.usm.authentication.rest;

import eu.europa.ec.mare.usm.policy.service.impl.PolicyProvider;
import eu.europa.ec.mare.usm.session.service.impl.SessionDao;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 */
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
  public Response deleteSessions()
  {
		LOGGER.info("deleteSessions() - (ENTER)");
    
		Response ret;
    
		try {
      sessionDao.deleteSessions();
      
      ret = Response.status(Response.Status.OK).build();
		} catch (Exception exc) {
      ret = ExceptionHandler.handleException(exc);
		}
    
		LOGGER.info("deleteSessions() - (LEAVE)");
    return ret;
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
  public Response clearPolicyCache()
  {
		LOGGER.info("clearPolicyCache() - (ENTER)");
    
		Response ret;
    
		try {
      policyProvider.reset();
      
      ret = Response.status(Response.Status.OK).build();
		} catch (Exception exc) {
      ret = ExceptionHandler.handleException(exc);
		}
    
		LOGGER.info("clearPolicyCache() - (LEAVE)");
    return ret;
  }
  
  
}