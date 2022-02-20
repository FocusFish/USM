package fish.focus.uvms.usm.administration.service.role.impl;

import fish.focus.uvms.usm.administration.common.jdbc.BaseJdbcDao;
import fish.focus.uvms.usm.administration.common.jdbc.Query;
import fish.focus.uvms.usm.administration.common.jdbc.RowMapper;
import fish.focus.uvms.usm.administration.domain.ComprehensiveRole;
import fish.focus.uvms.usm.administration.domain.FindRolesQuery;
import fish.focus.uvms.usm.administration.domain.PaginationResponse;
import fish.focus.uvms.usm.administration.domain.Paginator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * JDBC based data access object for the retrieval of Role related
 * information.
 * <p>
 * It uses a (container provided) JDBC data-source retrieved from the JNDI
 * context using JNDI name 'jdbc/USM2'.
 */
@Stateless
public class RoleJdbcDao extends BaseJdbcDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoleJdbcDao.class);

    public RoleJdbcDao() {
    }

    /**
     * Gets the name of all roles.
     *
     * @return the possibly-empty list of role names
     */
    public List<String> getRoleNames() {
        LOGGER.debug("getRoleNames() - (ENTER)");

        Query query = new Query("select NAME from ROLE_T order by NAME");
        List<String> names = queryForList(query, new StringMapper());

        LOGGER.debug("getRoleNames() - (LEAVE)");
        return names;
    }

    /**
     * Retrieves a list of roles according to the request
     *
     * @param request a FindRolesQuery request.
     * @return PaginationResponse<ComprehensiveRole> which contains the possibly-empty list of ComprehensiveRole
     * and the total number of results
     * @throws RuntimeException in case an internal error prevented fulfilling
     *                          the request
     */
    @SuppressWarnings("unchecked")
    public PaginationResponse<ComprehensiveRole> findRoles(FindRolesQuery request) {
        LOGGER.debug("findRoles(" + request + ") - (ENTER)");

        Paginator rPaginator = request.getPaginator();

        String paginationQuery = "select * from (select ROW_NUMBER() over(order by " +
                appendColumnNames(rPaginator.getSortColumn()) + " " + rPaginator.getSortDirection() +
                ") as rn, du.* from(";

        Query queryForTotalRecords = getFindRolesQuery(null, null, request);
        Query queryForFindRoles = getFindRolesQuery(paginationQuery, rPaginator, request);

        int totalRecords = queryForTotalRecords(queryForTotalRecords, new TotalRecordsMapper());
        List<ComprehensiveRole> rolesList = queryForList(queryForFindRoles, new ComprehensiveRoleMapper());
        PaginationResponse<ComprehensiveRole> ret = new PaginationResponse<>();
        ret.setResults(rolesList);
        ret.setTotal(totalRecords);

        LOGGER.debug("findRoles() - (LEAVE): " + ret);
        return ret;
    }

    /**
     * Checks whether a role exists with the provided name for the
     * specific application
     *
     * @param roleName the role name
     * @return <i>true</i> if the role exists, <i>false</i> otherwise
     */
    public boolean roleExists(String roleName) {
        LOGGER.debug("roleExists(" + roleName + ") - (ENTER)");

        Query query = new Query("select 1 from ROLE_T r" +
                " where  r.NAME=?");
        query.add(roleName);

        boolean ret = queryForExistence(query);

        LOGGER.debug("roleExists() - (LEAVE): " + ret);
        return ret;
    }

    private static class ComprehensiveRoleMapper implements RowMapper {
        @Override
        public ComprehensiveRole mapRow(ResultSet rs)
                throws SQLException {
            ComprehensiveRole ret = new ComprehensiveRole();

            ret.setRoleId(rs.getLong("ROLE_ID"));
            ret.setName(rs.getString("NAME"));
            ret.setDescription(rs.getString("DESCRIPTION"));
            ret.setStatus(rs.getString("STATUS"));
            ret.setActiveUsers(rs.getInt("USERS"));
            return ret;
        }
    }

    private Query getFindRolesQuery(String pagination, Paginator rPaginator, FindRolesQuery request) {
        Query query = new Query();
        String selection;

        if (pagination != null) {
            query.append(pagination);
            selection = "select DISTINCT " +
                    "r.ROLE_ID,r.NAME,r.DESCRIPTION,STATUS,(select count(DISTINCT USER_NAME) " +
                    "from active_user_role_v v where v.ROLE_ID=r.ROLE_ID) as USERS ";
        } else {
            selection = "select count(*) from (select DISTINCT r.NAME ";
        }

        String basicQuery = selection +
                "from ROLE_T r left join PERMISSION_T  p on p.ROLE_ID = r.ROLE_ID " +
                "left join FEATURE_T f on f.FEATURE_ID = p.FEATURE_ID " +
                "left join APPLICATION_T a on a.APPLICATION_ID=f.APPLICATION_ID where 1=1 ";

        query.append(basicQuery);

        if (request.getRoleName() != null) {
            query.append("and lower(r.name) like lower(?) ").add("%" + request.getRoleName() + "%");
        }
        if (request.getStatus() != null) {
            query.append("and status=? ").add(request.getStatus());
        }
        if (request.getApplicationName() != null) {
            query.append("and a.name=? ").add(request.getApplicationName());
        }

        if (rPaginator != null) {
            query.append(") du) us ");
            int limit = rPaginator.getLimit();
            if (limit != -1) {
                query.append("where rn between ? and ? order by rn");
                query.add(rPaginator.getOffset() + 1);
                query.add(rPaginator.getOffset() + limit);
            }
        } else {
            query.append(") du");
        }

        return query;

    }

    private String appendColumnNames(String column) {
        switch (column) {
            case "description":
            case "status":
                return column;
            default:
                return "NAME";
        }
    }

    private static class TotalRecordsMapper implements RowMapper {
        @Override
        public Object mapRow(ResultSet rs)
                throws SQLException {
            return rs.getInt(1);
        }
    }

    /**
     * Retrieves a list of comprehensive roles
     *
     * @return a list of comprehensive roles
     * @throws RuntimeException in case an internal error prevented fulfilling the request
     */
    public List<ComprehensiveRole> getRoles() {
        LOGGER.debug("getRoles() - (ENTER)");

        Query query = new Query("select ROLE_ID,NAME,STATUS from ROLE_T order by NAME");
        List<ComprehensiveRole> scopeNames = queryForList(query, new RolesMapper());

        LOGGER.debug("getRoles() - (LEAVE)");
        return scopeNames;
    }

    private static class RolesMapper implements RowMapper {
        @Override
        public ComprehensiveRole mapRow(ResultSet rs)
                throws SQLException {
            ComprehensiveRole ret = new ComprehensiveRole();

            ret.setRoleId(rs.getLong("ROLE_ID"));
            ret.setName(rs.getString("NAME"));
            ret.setStatus(rs.getString("STATUS"));
            return ret;
        }
    }

}
