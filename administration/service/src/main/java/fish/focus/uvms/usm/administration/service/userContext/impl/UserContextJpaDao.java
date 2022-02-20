package fish.focus.uvms.usm.administration.service.userContext.impl;

import fish.focus.uvms.usm.information.entity.UserContextEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 * JPA based data access of user context data.
 */
@Stateless
public class UserContextJpaDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserContextJpaDao.class);

    @PersistenceContext(unitName = "USM-Administration")
    private EntityManager em;

    public UserContextJpaDao() {
    }

    /**
     * Creates a user context.
     *
     * @param userContext the user context to be created
     * @return created userContext
     */
    public UserContextEntity create(UserContextEntity userContext) {
        LOGGER.debug("create(" + userContext + ") - (ENTER)");

        try {
            em.persist(userContext);
            em.flush();
        } catch (Exception ex) {
            handleException("create", ex);
        }

        LOGGER.debug("create() - (LEAVE)");
        return userContext;
    }

    /**
     * Updates an existing userContext.
     *
     * @param userContext the userContext to be updated
     * @return the updated userContext
     */
    public UserContextEntity update(UserContextEntity userContext) {
        LOGGER.debug("update(" + userContext + ") - (ENTER)");
        UserContextEntity ret = null;
        try {
            ret = em.merge(userContext);
            em.flush();
        } catch (Exception ex) {
            handleException("update", ex);
        }

        LOGGER.debug("update() - (LEAVE)");
        return ret;
    }

    /**
     * Retrieves a userContext by its unique userContextId
     *
     * @param userContextId the userContextId of the userContext to be found
     * @return null if nothing was found otherwise the existing userContext
     */
    public UserContextEntity read(Long userContextId) {
        LOGGER.debug("read(" + userContextId + ") - (ENTER)");

        UserContextEntity ret = null;

        try {
            TypedQuery<UserContextEntity> q = em.createNamedQuery("UserContextEntity.findByUserContextId",
                    UserContextEntity.class);

            q.setParameter("userContextId", userContextId);
            ret = q.getSingleResult();
        } catch (NoResultException exc) {
            LOGGER.debug("No entity was found with userContextId " + userContextId);
        }

        LOGGER.debug("read() - (LEAVE)");
        return ret;
    }

    /**
     * Deletes an existing use context.
     *
     * @param userContextId the id of the userContext
     */
    public void delete(Long userContextId) {
        LOGGER.debug("delete(" + userContextId + ") - (ENTER)");

        try {
            UserContextEntity entity = read(userContextId);
            if (entity != null) {
                em.remove(entity);
                em.flush();
                em.clear();
            }
        } catch (Exception ex) {
            handleException("delete", ex);
        }
        LOGGER.debug("delete() - (LEAVE)");
    }

    private void handleException(String attribute, Exception ex) throws RuntimeException {
        String msg = "Error during " + attribute + " user context : " + ex.getMessage();

        LOGGER.error(msg, ex);
        throw new RuntimeException(msg, ex);
    }

}
