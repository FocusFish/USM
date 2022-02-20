package fish.focus.uvms.usm.administration.service.role.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import fish.focus.uvms.usm.administration.common.jdbc.BaseJdbcDao;
import fish.focus.uvms.usm.administration.common.jdbc.Query;
import javax.ejb.Stateless;
import java.util.List;

/**
 * JDBC based data access object for the retrieval of Feature related
 * information.
 * <p>
 * It uses a (container provided) JDBC data-source retrieved from the JNDI
 * context using JNDI name 'jdbc/USM2'.
 */
@Stateless
public class FeatureJdbcDao extends BaseJdbcDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(FeatureJdbcDao.class);

    public FeatureJdbcDao() {
    }

    /**
     * Gets the name of all roles.
     *
     * @return the possibly-empty list of role names
     */
    @SuppressWarnings("unchecked")
    public List<String> getGroupNames() {
        LOGGER.debug("getGroupNames() - (ENTER)");

        Query query = new Query(
                "SELECT DISTINCT group_name FROM feature_t WHERE group_name is not null ORDER BY group_name ASC");
        List<String> names = queryForList(query, new StringMapper());

        LOGGER.debug("getGroupNames() - (LEAVE)");
        return names;
    }

}
