package fish.focus.uvms.usm.authentication.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import fish.focus.uvms.usm.authentication.domain.StatusResponse;
import javax.ejb.EJBException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class ExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandler.class);

    /**
     * Logs the provided exception and generates a pertinent HTTP response.
     *
     * @param exc the exception to be handled.
     * @return an HTTP response with a FORBIDDEN status if the requester is
     * not authenticated; or an UNAUTHORIZED status if the requester is not
     * authorised to use the service; or a BAD_REQUEST status if the service
     * request is null, empty, incomplete or otherwise invalid; or an
     * INTERNAL_SERVER_ERROR status if an internal error prevented servicing the
     * request.
     */
    public static Response handleException(Exception exc) {
        Throwable cause = getCause(exc);

        StatusResponse msg = new StatusResponse();
        msg.setMessage(cause.getMessage());
        Status status = Response.Status.INTERNAL_SERVER_ERROR;

        if (cause instanceof IllegalArgumentException) {
            LOGGER.info("Bad request: " + cause.getMessage());
            status = Response.Status.BAD_REQUEST;
        } else if (cause instanceof IllegalStateException) {
            LOGGER.error("Illegal state: " + cause.getMessage(), cause);
            status = Response.Status.CONFLICT;
        } else if (cause instanceof RuntimeException) {
            LOGGER.error("Internal Server Error: " + cause.getMessage(), cause);
        } else {
            LOGGER.error("Unknown Error: " + exc.getMessage(), exc);
            msg.setMessage(exc.getMessage());
        }
        msg.setStatusCode(status.getStatusCode());
        return Response.status(status).entity(msg).build();
    }

    private static Throwable getCause(Throwable src) {
        Throwable ret = src;
        if (src instanceof EJBException && src.getCause() != null) {
            ret = getCause(src.getCause());
        }
        return ret;
    }
}
