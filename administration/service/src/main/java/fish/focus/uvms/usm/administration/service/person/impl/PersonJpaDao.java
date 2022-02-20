package fish.focus.uvms.usm.administration.service.person.impl;

import fish.focus.uvms.usm.information.entity.PersonEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Stateless
public class PersonJpaDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(PersonJpaDao.class);

    @PersistenceContext(unitName = "USM-Administration")
    private EntityManager em;

    public PersonJpaDao() {
    }

    /**
     * Reads the list of all persons from the database
     *
     * @return the List of all persons from the database, null if any error encountered
     */
    public List<PersonEntity> findAll() {
        LOGGER.debug("findAll() - (ENTER)");

        List<PersonEntity> ret = null;

        try {
            TypedQuery<PersonEntity> q = em.createNamedQuery("PersonEntity.findAll", PersonEntity.class);
            ret = q.getResultList();
        } catch (NoResultException exc) {
            LOGGER.debug("PersonEntity list could not be extracted");
        } catch (Exception ex) {
            handleException("read", ex);
        }

        LOGGER.debug("findAll() - (LEAVE)" + ret);
        return ret;
    }

    private void handleException(String operation, Exception ex)
            throws RuntimeException {
        String msg = "Error during " + operation + " organisation : " + ex.getMessage();
        LOGGER.error(msg, ex);
        throw new RuntimeException(msg, ex);
    }

    /**
     * Reads the Person with the provided (internal) unique identifier
     *
     * @param personId the end point (internal) unique identifier
     * @return the matching end point if it exists, null otherwise
     */
    public PersonEntity read(Long personId) {
        LOGGER.debug("read() - (ENTER)");

        PersonEntity ret = null;

        try {
            ret = em.find(PersonEntity.class, personId);
        } catch (NoResultException exc) {
            LOGGER.debug("PersonEntity " + personId + " not found");
        } catch (Exception ex) {
            handleException("read", ex);
        }

        LOGGER.debug("read() - (LEAVE)" + ret);
        return ret;
    }

    /**
     * Updates user's contact details.
     *
     * @param person the contact details to be updated
     * @return the updated contact details
     */
    public PersonEntity update(PersonEntity person) {
        LOGGER.debug("update(" + person + ") - (ENTER)");

        PersonEntity ret = null;
        try {
            ret = em.merge(person);
            em.flush();
        } catch (Exception ex) {
            String msg = "Failed to update contact details: " + ex.getMessage();
            LOGGER.error(msg, ex);
            throw new RuntimeException(msg, ex);
        }

        LOGGER.debug("update() - (LEAVE)");
        return ret;
    }

}
