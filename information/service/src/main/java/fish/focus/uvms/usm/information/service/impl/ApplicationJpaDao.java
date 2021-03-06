package fish.focus.uvms.usm.information.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import fish.focus.uvms.usm.information.entity.ApplicationEntity;
import fish.focus.uvms.usm.information.entity.DatasetEntity;
import fish.focus.uvms.usm.information.entity.FeatureEntity;
import fish.focus.uvms.usm.information.entity.OptionEntity;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * JPA based data-access for the administration of Application related
 * information
 */
@Stateless
public class ApplicationJpaDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationJpaDao.class);

    @PersistenceContext(unitName = "USM-Administration")
    private EntityManager em;

    /**
     * Reads the Application with the provided identifier
     *
     * @param applicationId the id of the application
     * @return the matching Application if it exists, null otherwise
     */
    ApplicationEntity read(Long applicationId) {
        LOGGER.debug("read(" + applicationId + ") - (ENTER)");

        ApplicationEntity ret = null;

        try {
            Query q = em.createNamedQuery("ApplicationEntity.findByApplicationId");

            q.setParameter("applicationId", applicationId);
            ret = (ApplicationEntity) q.getSingleResult();
        } catch (NoResultException ex) {
            LOGGER.info("Application with id " + applicationId + " not found", ex);
        } catch (Exception ex) {
            handleException("read", ex);
        }

        LOGGER.debug("read() - (LEAVE): " + ret);
        return ret;
    }

    /**
     * Reads the Application with the provided name
     *
     * @param applicationName the name of the application
     * @return the matching Application if it exists, null otherwise
     */
    public ApplicationEntity read(String applicationName) {
        LOGGER.debug("read(" + applicationName + ") - (ENTER)");

        ApplicationEntity ret = null;

        try {
            Query q = em.createNamedQuery("ApplicationEntity.findByName");

            q.setParameter("name", applicationName);
            ret = (ApplicationEntity) q.getSingleResult();
        } catch (NoResultException ex) {
            LOGGER.info("Application with name " + applicationName + " not found");
        } catch (Exception ex) {
            handleException("read", ex);
        }

        LOGGER.debug("read() - (LEAVE): " + ret);
        return ret;
    }

    /**
     * Reads the Application with the provided name, together with all
     * its features, datasets and options
     *
     * @param applicationName the name of the application
     * @return the matching Application if it exists, null otherwise
     */
    public ApplicationEntity readApplication(String applicationName) {
        LOGGER.debug("readApplication(" + applicationName + ") - (ENTER)");

        ApplicationEntity ret = read(applicationName);

        if (ret != null) {
            // Read details
            try {
                ret.setDatasetList(readDetails(ret.getApplicationId(),
                        "DatasetEntity.findByApplicationId",
                        DatasetEntity.class));
                ret.setFeatureList(readDetails(ret.getApplicationId(),
                        "FeatureEntity.findByApplicationId",
                        FeatureEntity.class));
                ret.setOptionList(readDetails(ret.getApplicationId(),
                        "OptionEntity.findByApplicationId",
                        OptionEntity.class));
            } catch (Exception ex) {
                handleException("readApplication", ex);
            }
        }

        LOGGER.debug("readApplication() - (LEAVE): " + ret);
        return ret;
    }

    /**
     * Creates (or persists) the provided application.
     *
     * @param src the application to be created
     * @return the unique applicationId (PK) assigned to the application
     */
    public Long create(ApplicationEntity src) {
        LOGGER.debug("create(" + src + ") - (ENTER)");

        setValues(src);

        em.persist(src);
        em.flush();

        Long ret = src.getApplicationId();

        LOGGER.debug("create() - (LEAVE): " + ret);
        return ret;
    }

    /**
     * Updates the provided Application and all its details.
     *
     * @param src the Application to be updated
     */
    public void update(ApplicationEntity src) {
        LOGGER.debug("update(" + src + ") - (ENTER)");

        setValues(src);
        em.merge(src);
        em.flush();

        LOGGER.debug("update() - (LEAVE)");
    }

    private void setValues(ApplicationEntity src) {
        if (src.getFeatureList() != null) {
            for (FeatureEntity fe : src.getFeatureList()) {
                fe.setApplication(src);
            }
        }
        if (src.getDatasetList() != null) {
            for (DatasetEntity de : src.getDatasetList()) {
                de.setApplication(src);
            }
        }
        if (src.getOptionList() != null) {
            for (OptionEntity oe : src.getOptionList()) {
                oe.setApplication(src);
            }
        }
    }

    /**
     * Deletes the Application with the provided name.
     *
     * @param applicationName the Application name
     */
    public void delete(String applicationName) {
        LOGGER.debug("delete(" + applicationName + ") - (ENTER)");

        ApplicationEntity entity = readApplication(applicationName);
        if (entity != null) {
            try {
                em.remove(entity);
                em.flush();
            } catch (Exception ex) {
                handleException("delete", ex);
            }
        }
        LOGGER.debug("delete() - (LEAVE)");
    }

    /**
     * Delete specific details (features, datasets and/or options) of an
     * Application
     *
     * @param src the application details to be deleted
     */
    public void deleteDetails(ApplicationEntity src) {
        LOGGER.debug("deleteDetails(" + src + ") - (ENTER)");
        int ret = 0;

        if (src != null) {
            try {
                // Delete features
                if (src.getFeatureList() != null && !src.getFeatureList().isEmpty()) {
                    LOGGER.debug("Features to be deleted:" + src.getFeatureList().size());

                    Query feat = em.createQuery("delete from FeatureEntity e " +
                            " where e.featureId=:featureId");
                    for (FeatureEntity item : src.getFeatureList()) {
                        feat.setParameter("featureId", item.getFeatureId());
                        int cnt = feat.executeUpdate();
                        LOGGER.debug("deleted " + cnt + " row(s) for featureId " +
                                item.getFeatureId());
                        ret += cnt;
                    }
                }

                // Delete datasets
                if (src.getDatasetList() != null && !src.getDatasetList().isEmpty()) {
                    LOGGER.debug("Datasets to be deleted:" + src.getDatasetList().size());

                    Query dset = em.createQuery("delete from DatasetEntity e " +
                            " where e.datasetId=:datasetId");
                    for (DatasetEntity item : src.getDatasetList()) {
                        dset.setParameter("datasetId", item.getDatasetId());
                        int cnt = dset.executeUpdate();
                        LOGGER.debug("deleted " + cnt + " row(s) for datasetId " +
                                item.getDatasetId());
                        ret += cnt;
                    }
                }

                // Delete options, cascading any associated preferences
                if (src.getOptionList() != null && !src.getOptionList().isEmpty()) {
                    LOGGER.debug("Options to be deleted:" + src.getOptionList().size());

                    Query opt = em.createQuery("delete from OptionEntity e " +
                            " where e.optionId=:optionId");

                    Query pref = em.createQuery("delete from PreferenceEntity e " +
                            " where e.option.optionId=:optionId");

                    for (OptionEntity item : src.getOptionList()) {
                        pref.setParameter("optionId", item.getOptionId());
                        int cnt = pref.executeUpdate();
                        opt.setParameter("optionId", item.getOptionId());
                        cnt += opt.executeUpdate();
                        LOGGER.debug("deleted " + cnt + " row(s) for optionId " +
                                item.getOptionId());
                        ret += cnt;
                    }
                }
                em.flush();
            } catch (Exception ex) {
                handleException("deleteDetails", ex);
            }
        }

        LOGGER.debug("deleteDetails() - (LEAVE): " + ret);
    }

    private <T> List<T> readDetails(Long applicationId, String queryName, Class<T> type) {
        LOGGER.debug("readDetails(" + queryName + ") - (ENTER)");

        Query q = em.createNamedQuery(queryName, type);
        q.setParameter("applicationId", applicationId);
        List ret = q.getResultList();

        LOGGER.debug("readDetails() - (LEAVE): " + ret.size());
        return ret;
    }

    private void handleException(String operation, Exception ex) throws RuntimeException {
        String msg = "Error during " + operation + " operation: " + ex.getMessage();

        LOGGER.error(msg, ex);
        throw new RuntimeException(msg, ex);
    }

}
