package fish.focus.uvms.usm.administration.rest.service.role;

import fish.focus.uvms.usm.administration.domain.*;
import fish.focus.uvms.usm.administration.rest.ServiceArrayResponse;
import fish.focus.uvms.usm.administration.rest.common.ExceptionHandler;
import fish.focus.uvms.usm.administration.service.role.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Max;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.List;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@Path("roles")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class RoleResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoleResource.class);
    private static final String ALL = "all";

    @EJB
    private RoleService service;

    @Context
    private HttpServletRequest servletRequest;

    /**
     * Retrieves the list of role names
     *
     * @param jwtToken  the JWT token identifying the service requester, optional
     *                  if the service requester is authenticated by the J2EE container
     * @param roleName  the name of the Role of the selected UserContext (optional)
     * @param scopeName the name of the Scope of the selected UserContext
     *                  (optional)
     * @return the list of role names with an OK status, or a BAD_REQUEST error
     * code in case the provided input incomplete, with an INTERNAL_SERVER_ERROR
     * error code in case an internal error prevented fulfilling the request or
     * UnauthorisedException with an FORBIDDEN error code in case the end user is
     * not authorised to perform the operation
     */
    @GET
    @Path("names")
    public Response getRoleNames(@HeaderParam("authorization") String jwtToken,
                                 @HeaderParam("roleName") String roleName,
                                 @HeaderParam("scopeName") String scopeName) {
        LOGGER.debug("getRoleNames() - (ENTER)");

        ServiceRequest<RoleQuery> request = new ServiceRequest<>();
        request.setRequester(servletRequest.getRemoteUser());
        request.setRoleName(roleName);
        request.setScopeName(scopeName);
        request.setBody(new RoleQuery());

        Response ret;
        try {
            List<String> lst = service.getRoleNames(request);
            Status status;

            if (lst == null || lst.isEmpty()) {
                status = Response.Status.NO_CONTENT;
            } else {
                status = Response.Status.OK;
            }
            ServiceArrayResponse<String> sar = new ServiceArrayResponse<>();
            sar.setResults(lst);
            ret = Response.status(status).entity(sar).build();
        } catch (Exception exc) {
            ret = ExceptionHandler.handleException(exc);
        }

        LOGGER.debug("getRoleNames() - (LEAVE)");
        return ret;
    }

    /**
     * Retrieves a list of role(containing only the basic info) that meet the
     * provided criteria
     *
     * @param jwtToken        the JWT token identifying the service requester, optional
     *                        if the service requester is authenticated by the J2EE container
     * @param roleName        the name of the Role of the selected UserContext (optional)
     * @param scopeName       the name of the Scope of the selected UserContext
     *                        (optional)
     * @param offset          the pagination offset
     * @param limit           the pagination limit
     * @param sortColumn      the sort Column
     * @param sortDirection   the sort Direction
     * @param nameOfRole      the requested role
     * @param applicationName the requested application
     * @param status          the requested status of the role
     * @return an OK status and the list of roles that meet the criteria, or a
     * BAD_REQUEST error code in case the provided input incomplete, with an
     * INTERNAL_SERVER_ERROR error code in case an internal error prevented
     * fulfilling the request or UnauthorisedException with an FORBIDDEN error
     * code in case the end user is not authorised to perform the operation
     */
    @GET
    public Response findRoles(@HeaderParam("authorization") String jwtToken,
                              @HeaderParam("roleName") String roleName,
                              @HeaderParam("scopeName") String scopeName,
                              @DefaultValue("0") @QueryParam("offset") int offset,
                              @DefaultValue("10") @Max(100) @QueryParam("limit") int limit,
                              @DefaultValue("name") @QueryParam("sortColumn") String sortColumn,
                              @DefaultValue("DESC") @QueryParam("sortDirection") String sortDirection,
                              @QueryParam("role") String nameOfRole,
                              @QueryParam("application") String applicationName,
                              @QueryParam("status") String status) {
        LOGGER.debug("findRoles() - (ENTER)");

        // Setup paginator
        Paginator paginator = new Paginator();
        paginator.setOffset(offset);
        paginator.setLimit(limit);
        paginator.setSortColumn(sortColumn);
        paginator.setSortDirection(sortDirection);

        ServiceRequest<FindRolesQuery> request = new ServiceRequest<>();
        request.setRequester(servletRequest.getRemoteUser());
        request.setRoleName(roleName);
        request.setScopeName(scopeName);
        FindRolesQuery findRolesQuery = new FindRolesQuery();
        findRolesQuery.setRoleName(nameOfRole);
        findRolesQuery.setApplicationName(!"".equals(applicationName) ? applicationName : null);
        findRolesQuery.setStatus((status != null && !ALL.equals(status)) ? status : null);
        findRolesQuery.setPaginator(paginator);
        request.setBody(findRolesQuery);

        Response ret;
        try {
            PaginationResponse<ComprehensiveRole> pResponse = service.findRoles(request);

            Status statusCode;
            if (pResponse == null || pResponse.getResults().isEmpty()) {
                statusCode = Response.Status.NO_CONTENT;
            } else {
                statusCode = Response.Status.OK;
            }
            ret = Response.status(statusCode).entity(pResponse).build();
        } catch (Exception exc) {
            ret = ExceptionHandler.handleException(exc);
        }

        LOGGER.debug("findRoles() - (LEAVE)");
        return ret;
    }

    /**
     * Retrieves a role according to the provided roleId
     *
     * @param jwtToken  the JWT token identifying the service requester, optional
     *                  if the service requester is authenticated by the J2EE container
     * @param roleName  the name of the Role of the selected UserContext (optional)
     * @param scopeName the name of the Scope of the selected UserContext
     *                  (optional)
     * @param roleId    the requested role's id
     * @return an OK status and the requested role, or a BAD_REQUEST error code in
     * case the provided input incomplete, with an INTERNAL_SERVER_ERROR error
     * code in case an internal error prevented fulfilling the request or
     * UnauthorisedException with an UNAUTHORIZED error code in case the end user
     * is not authorised to perform the operation
     */
    @GET
    @Path("{roleId}")
    public Response getRole(@HeaderParam("authorization") String jwtToken,
                            @HeaderParam("roleName") String roleName,
                            @HeaderParam("scopeName") String scopeName,
                            @PathParam("roleId") Long roleId) {
        LOGGER.debug("getRole(" + roleId + ") - (ENTER)");

        ServiceRequest<GetRoleQuery> request = new ServiceRequest<>();
        request.setRequester(servletRequest.getRemoteUser());
        request.setRoleName(roleName);
        request.setScopeName(scopeName);
        GetRoleQuery getRoleQuery = new GetRoleQuery();
        getRoleQuery.setRoleId(roleId);
        request.setBody(getRoleQuery);

        Response ret;
        try {
            Role role = service.getRole(request);

            Status status;
            if (role == null) {
                status = Response.Status.NO_CONTENT;
            } else {
                status = Response.Status.OK;
            }
            ret = Response.status(status).entity(role).build();
        } catch (Exception exc) {
            ret = ExceptionHandler.handleException(exc);
        }

        LOGGER.debug("getRole() - (LEAVE)");
        return ret;
    }

    /**
     * Creates a new role
     *
     * @param jwtToken  the JWT token identifying the service requester, optional
     *                  if the service requester is authenticated by the J2EE container
     * @param roleName  the name of the Role of the selected UserContext (optional)
     * @param scopeName the name of the Scope of the selected UserContext
     *                  (optional)
     * @param role      the new role to be created
     * @return Response with status OK (200) in case of success otherwise status
     * NOT_MODIFIED or a BAD_REQUEST error code in case the provided input
     * incomplete, with an INTERNAL_SERVER_ERROR error code in case an internal
     * error prevented fulfilling the request or UnauthorisedException with an
     * FORBIDDEN error code in case the end user is not authorised to perform the
     * operation
     */
    @POST
    public Response createRole(@HeaderParam("authorization") String jwtToken,
                               @HeaderParam("roleName") String roleName,
                               @HeaderParam("scopeName") String scopeName,
                               ComprehensiveRole role) {
        LOGGER.debug("createRole(" + role + ") - (ENTER)");

        ServiceRequest<ComprehensiveRole> request = new ServiceRequest<>();
        request.setRequester(servletRequest.getRemoteUser());
        request.setRoleName(roleName);
        request.setScopeName(scopeName);
        request.setBody(role);

        Response ret;
        try {
            ComprehensiveRole createdRole = service.createRole(request);

            Status status;
            if (createdRole.getRoleId() != null) {
                status = Response.Status.OK;
            } else {
                status = Response.Status.NOT_MODIFIED;
            }
            ret = Response.status(status).entity(createdRole).build();

        } catch (Exception e) {
            ret = ExceptionHandler.handleException(e);
        }

        LOGGER.debug("createRole() - (LEAVE)");
        return ret;
    }

    /**
     * Updates an existing role
     *
     * @param jwtToken  the JWT token identifying the service requester, optional
     *                  if the service requester is authenticated by the J2EE container
     * @param roleName  the name of the Role of the selected UserContext (optional)
     * @param scopeName the name of the Scope of the selected UserContext
     *                  (optional)
     * @param role      the role to be updated
     * @return Response with status OK (200) in case of success otherwise status
     * NOT_MODIFIED or a BAD_REQUEST error code in case the provided input
     * incomplete, with an INTERNAL_SERVER_ERROR error code in case an internal
     * error prevented fulfilling the request or UnauthorisedException with an
     * FORBIDDEN error code in case the end user is not authorised to perform the
     * operation
     */
    @PUT
    public Response updateRole(@HeaderParam("authorization") String jwtToken,
                               @HeaderParam("roleName") String roleName,
                               @HeaderParam("scopeName") String scopeName,
                               ComprehensiveRole role) {
        LOGGER.debug("updateRole(" + role + ") - (ENTER)");

        ServiceRequest<ComprehensiveRole> request = new ServiceRequest<>();
        request.setRequester(servletRequest.getRemoteUser());
        request.setRoleName(roleName);
        request.setScopeName(scopeName);
        request.setBody(role);

        Response ret;
        try {
            service.updateRole(request);

            // Get the updated role
            ServiceRequest<GetRoleQuery> getRequest = new ServiceRequest<>();
            GetRoleQuery getRoleQuery = new GetRoleQuery();
            getRoleQuery.setRoleId(request.getBody().getRoleId());
            getRequest.setRequester(servletRequest.getRemoteUser());
            getRequest.setBody(getRoleQuery);
            Role updatedRole = service.getRole(getRequest);

            Status status = Response.Status.OK;
            ret = Response.status(status).entity(updatedRole).build();

        } catch (Exception e) {
            ret = ExceptionHandler.handleException(e);
        }

        LOGGER.debug("updateRole() - (LEAVE)");
        return ret;
    }

    /**
     * Deletes an existing role
     *
     * @param jwtToken  the JWT token identifying the service requester, optional
     *                  if the service requester is authenticated by the J2EE container
     * @param roleName  the name of the Role of the selected UserContext (optional)
     * @param scopeName the name of the Scope of the selected UserContext
     *                  (optional)
     * @param roleId    the identifier of the role to be deleted
     * @return Response with status OK (200) in case of success otherwise status
     * NOT_MODIFIED or a BAD_REQUEST error code in case the provided input
     * incomplete, with an INTERNAL_SERVER_ERROR error code in case an internal
     * error prevented fulfilling the request or UnauthorisedException with an
     * FORBIDDEN error code in case the end user is not authorised to perform the
     * operation
     */
    @DELETE
    @Path("{roleId}")
    public Response deleteRole(@HeaderParam("authorization") String jwtToken,
                               @HeaderParam("roleName") String roleName,
                               @HeaderParam("scopeName") String scopeName,
                               @PathParam("roleId") Long roleId) {
        LOGGER.debug("deleteRole(" + roleId + ") - (ENTER)");

        ServiceRequest<Long> request = new ServiceRequest<>();
        request.setRequester(servletRequest.getRemoteUser());
        request.setRoleName(roleName);
        request.setScopeName(scopeName);
        request.setBody(roleId);

        Response ret;
        try {
            service.deleteRole(request);
            Status status = Response.Status.OK;
            ret = Response.status(status).build();

        } catch (Exception e) {
            ret = ExceptionHandler.handleException(e);
        }

        LOGGER.debug("deleteRole() - (LEAVE)");
        return ret;
    }

    /**
     * Finds the feature group names
     *
     * @param jwtToken  the JWT token identifying the service requester, optional
     *                  if the service requester is authenticated by the J2EE container
     * @param roleName  the name of the Role of the selected UserContext (optional)
     * @param scopeName the name of the Scope of the selected UserContext
     *                  (optional)
     * @return the requested information with a status OK in case of success
     * otherwise status NOT_MODIFIED or a BAD_REQUEST error code in case the
     * provided input incomplete, with an INTERNAL_SERVER_ERROR error code in case
     * an internal error prevented fulfilling the request or a FORBIDDEN error
     * code in case the end user is not authorised to perform the operation
     */
    @GET
    @Path("features/group/names")
    public Response getFeatureGroupNames(@HeaderParam("authorization") String jwtToken,
                                         @HeaderParam("roleName") String roleName,
                                         @HeaderParam("scopeName") String scopeName) {
        LOGGER.debug("getFeatureGroupNames() - (ENTER)");

        ServiceRequest<String> request = new ServiceRequest<>();
        request.setRequester(servletRequest.getRemoteUser());
        request.setRoleName(roleName);
        request.setScopeName(scopeName);
        request.setBody("");

        Response ret;
        try {
            List<String> lst = service.getGroupNames(request);

            Status status;
            if (lst == null || lst.isEmpty()) {
                status = Response.Status.NO_CONTENT;
            } else {
                status = Response.Status.OK;
            }
            ServiceArrayResponse<String> sar = new ServiceArrayResponse<>();
            sar.setResults(lst);
            ret = Response.status(status).entity(sar).build();
        } catch (Exception exc) {
            ret = ExceptionHandler.handleException(exc);
        }

        LOGGER.debug("getFeatureGroupNames() - (LEAVE)");
        return ret;
    }

    /**
     * Finds the permissions matching the provided request.
     *
     * @param jwtToken    the JWT token identifying the service requester, optional
     *                    if the service requester is authenticated by the J2EE container
     * @param roleName    the name of the Role of the selected UserContext (optional)
     * @param scopeName   the name of the Scope of the selected UserContext
     *                    (optional)
     * @param application the application name
     * @param group       the group name
     * @return the requested information with a status OK in case of success
     * otherwise status NOT_MODIFIED or a BAD_REQUEST error code in case the
     * provided input incomplete, with an INTERNAL_SERVER_ERROR error code in case
     * an internal error prevented fulfilling the request or a FORBIDDEN error
     * code in case the end user is not authorised to perform the operation
     */
    @GET
    @Path("permissions")
    public Response findPermissions(@HeaderParam("authorization") String jwtToken,
                                    @HeaderParam("roleName") String roleName,
                                    @HeaderParam("scopeName") String scopeName,
                                    @QueryParam("application") String application,
                                    @QueryParam("group") String group) {
        LOGGER.debug("findPermissions() - (ENTER)");

        ServiceRequest<FindPermissionsQuery> request = new ServiceRequest<>();
        request.setRequester(servletRequest.getRemoteUser());
        request.setRoleName(roleName);
        request.setScopeName(scopeName);
        FindPermissionsQuery findPermissionsQuery = new FindPermissionsQuery();
        findPermissionsQuery.setApplication((application != null && !ALL.equals(application)) ? application : null);
        findPermissionsQuery.setGroup((group != null && !ALL.equals(group)) ? group : null);
        request.setBody(findPermissionsQuery);

        Response ret;
        try {
            List<Feature> permissions = service.findPermissions(request);

            Status status;
            if (permissions == null || permissions.isEmpty()) {
                status = Response.Status.NO_CONTENT;
            } else {
                status = Response.Status.OK;
            }
            ServiceArrayResponse<Feature> sar = new ServiceArrayResponse<>();
            sar.setResults(permissions);
            ret = Response.status(status).entity(sar).build();
        } catch (Exception exc) {
            ret = ExceptionHandler.handleException(exc);
        }

        LOGGER.debug("findPermissions() - (LEAVE)");
        return ret;
    }

    /**
     * Retrieves the list of roles (only the name,id and status)
     *
     * @param jwtToken  the JWT token identifying the service requester, optional
     *                  if the service requester is authenticated by the J2EE container
     * @param roleName  the name of the Role of the selected UserContext (optional)
     * @param scopeName the name of the Scope of the selected UserContext
     *                  (optional)
     * @return the list of roles with an OK status, or a BAD_REQUEST error code in
     * case the provided input incomplete, with an INTERNAL_SERVER_ERROR error
     * code in case an internal error prevented fulfilling the request or
     * UnauthorisedException with an FORBIDDEN error code in case the end user is
     * not authorised to perform the operation
     */
    @GET
    @Path("comprehensives")
    public Response getRoles(@HeaderParam("authorization") String jwtToken,
                             @HeaderParam("roleName") String roleName,
                             @HeaderParam("scopeName") String scopeName) {
        LOGGER.debug("getRoles() - (ENTER)");

        ServiceRequest<RoleQuery> request = new ServiceRequest<>();
        request.setRequester(servletRequest.getRemoteUser());
        request.setRoleName(roleName);
        request.setScopeName(scopeName);
        request.setBody(new RoleQuery());

        Response ret;
        try {
            List<ComprehensiveRole> lst = service.getRoles(request);
            Status status;

            if (lst == null || lst.isEmpty()) {
                status = Response.Status.NO_CONTENT;
            } else {
                status = Response.Status.OK;
            }
            ServiceArrayResponse<ComprehensiveRole> sar = new ServiceArrayResponse<>();
            sar.setResults(lst);
            ret = Response.status(status).entity(sar).build();
        } catch (Exception exc) {
            ret = ExceptionHandler.handleException(exc);
        }

        LOGGER.debug("getRoles() - (LEAVE)");
        return ret;
    }
}
