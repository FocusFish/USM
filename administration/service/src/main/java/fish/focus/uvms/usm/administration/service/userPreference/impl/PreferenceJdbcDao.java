package fish.focus.uvms.usm.administration.service.userPreference.impl;

import fish.focus.uvms.usm.information.entity.OptionEntity;
import fish.focus.uvms.usm.information.entity.PreferenceEntity;
import fish.focus.uvms.usm.administration.common.jdbc.BaseJdbcDao;
import fish.focus.uvms.usm.administration.common.jdbc.Query;
import fish.focus.uvms.usm.administration.common.jdbc.RowMapper;
import fish.focus.uvms.usm.administration.domain.Preference;
import fish.focus.uvms.usm.administration.domain.UserPreferenceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * JDBC based data access object for the retrieval of user-related information.
 * <p>
 * It uses a (container provided) JDBC data-source retrieved from the JNDI
 * context using JNDI name 'jdbc/USM2'.
 */
@Stateless
public class PreferenceJdbcDao extends BaseJdbcDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(PreferenceJdbcDao.class.getName());

    public PreferenceJdbcDao() {
    }

    private Query getContextPreferencesQuery(Long contextId) {
        Query query = new Query();
        String selection;

        selection = "SELECT OPTION_ID, OPTION_VALUE from PREFERENCE_T where 1=1 ";
        query.append(selection);

        if (contextId != null) {
            query.append(" and USER_CONTEXT_ID=" + contextId);
        }
        return query;
    }

    /**
     * Find context preferences list.
     *
     * @param contextId the identifier of the context
     * @return null if there is no preference associated with this context, or
     * the retrieved list otherwise
     */
    public List<PreferenceEntity> getContextPreferences(Long contextId) {

        LOGGER.debug("getContextPreferences(" + contextId + ") - (ENTER)");

        Query queryContextPreferencesQuery = getContextPreferencesQuery(contextId);

        LOGGER.info("query: " + queryContextPreferencesQuery);

        List<PreferenceEntity> prefList = queryForList(queryContextPreferencesQuery, new PreferencesMapper());

        LOGGER.debug("getContextPreferences() - (LEAVE): " + prefList);
        return prefList;
    }

    private static class PreferencesMapper implements RowMapper {

        public PreferencesMapper() {
        }

        @Override
        public Object mapRow(ResultSet rs) throws SQLException {
            PreferenceEntity ret = new PreferenceEntity();
            ret.setOptionValue(rs.getBytes("OPTION_VALUE"));

            OptionEntity option = new OptionEntity();
            option.setOptionId(rs.getLong("OPTION_ID"));

            ret.setOption(option);
            return ret;
        }
    }

    /**
     * Find user's preferences list.
     *
     * @param userName  the identifier of the user
     * @param groupName the identifier of the group
     * @return null if there is no preference associated with this user, or
     * the retrieved list of preferences
     */
    public UserPreferenceResponse getUserPreferences(String userName, String groupName) {
        LOGGER.debug("getUserPreferences(" + userName + ") - (ENTER)");

        Query userPreferencesQuery = getUserPreferencesQuery(userName, groupName);

        LOGGER.info("query: " + userPreferencesQuery);

        List<Preference> prefList = queryForList(userPreferencesQuery, new UserPreferencesMapper());
        UserPreferenceResponse ret = new UserPreferenceResponse();
        ret.setResults(prefList);

        LOGGER.debug("getUserPreferences() - (LEAVE): " + prefList);
        return ret;
    }

    private Query getUserPreferencesQuery(String userName, String groupName) {
        Query query = new Query();
        String selection;

        selection = "select OPTION_VALUE,NAME,DESCRIPTION,GROUP_NAME " +
                "from USER_T u " +
                "inner join USER_CONTEXT_T c on u.USER_ID = c.USER_ID " +
                "inner join PREFERENCE_T p on p.USER_CONTEXT_ID = c.USER_CONTEXT_ID " +
                "inner join OPTION_T o on o.OPTION_ID = p.OPTION_ID " +
                "where 1=1";

        query.append(selection);

        if (userName != null) {
            query.append(" and USER_NAME=?").add(userName);
        }

        if (groupName != null) {
            query.append(" and GROUP_NAME=?").add(groupName);
        }

        return query;
    }

    private static class UserPreferencesMapper implements RowMapper {

        public UserPreferencesMapper() {
        }

        @Override
        public Object mapRow(ResultSet rs) throws SQLException {
            Preference ret = new Preference();
            ret.setOptionName(rs.getString("NAME"));
            ret.setOptionDescription(rs.getString("DESCRIPTION"));
            ret.setOptionValue(rs.getBytes("OPTION_VALUE"));
            ret.setGroupName(rs.getString("GROUP_NAME"));
            return ret;
        }
    }
}
