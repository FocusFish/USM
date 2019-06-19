package eu.europa.ec.mare.usm.information.rest.common;

import javax.ejb.EJBException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.mare.usm.information.rest.service.StatusResponse;

public class ExceptionHandler {
  private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandler.class);

  /**
   * Logs the provided exception and generates a pertinent HTTP response.
   * 
   * @param exc the exception to be handled.
   * 
   * @return an HTTP response with a FORBIDDEN status if the requester is 
   * not authenticated; or an UNAUTHORIZED status if the requester is not 
   * authorised to use the service; or a BAD_REQUEST status if the service 
   * request is null, empty, incomplete or otherwise invalid; or an 
   * INTERNAL_SERVER_ERROR status if an internal error prevented servicing the 
   * request.
   */
  public static Response handleException(Exception exc) 
  {
    /*Throwable cause = getCause(exc);

    StatusResponse msg = new StatusResponse();
    msg.setMessage(cause.getMessage());
    Status status = Response.Status.INTERNAL_SERVER_ERROR;
    
   if (cause instanceof IllegalArgumentException) {
      LOGGER.info("Bad request: " + cause.getMessage());
      status = Response.Status.BAD_REQUEST;
    } else if (cause instanceof RuntimeException) {
      LOGGER.error("Internal Server Error: " + cause.getMessage(), cause);
    }else if (cause instanceof IllegalStateException) {
      LOGGER.error("Illegal request: " + cause.getMessage(), cause);
    } else {
      LOGGER.error("Unknown Error: " + exc.getMessage(), exc);
      msg.setMessage(exc.getMessage());
    }

    msg.setStatusCode(status.getStatusCode());
    Response ret = Response.status(status).entity(msg).build();

    return ret;*/
	  StatusResponse msg = new StatusResponse();
	    Response.Status status = Response.Status.INTERNAL_SERVER_ERROR;
	    
	    Throwable cause = exc;

	    if (exc.getCause() != null) {
	      cause = exc.getCause();
	    }

	    if (cause instanceof IllegalArgumentException) {
	      LOGGER.warn("Bad request: " + cause.getMessage());
	      msg.setMessage(cause.getMessage());
	      status = Response.Status.BAD_REQUEST;
	    } else if (cause instanceof RuntimeException) {
	      LOGGER.error("Internal Server Error: " + cause.getMessage(), cause);
	      msg.setMessage(cause.getMessage());
	    } else {
	      LOGGER.error("Unknown Error: " + exc.getMessage(), exc);
	      msg.setMessage(exc.getMessage());
	    }
	    
	    Response ret = Response.status(status).entity(msg).build();

	    return ret;
  }
  
  private static Throwable getCause(Throwable src)
  {
    Throwable ret = src;
    
    if (src != null && 
        src instanceof EJBException && 
        src.getCause() != null) {
      ret = getCause(src.getCause());
    }
    
    return ret;
  }
}