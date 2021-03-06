package fish.focus.uvms.usm.session.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import fish.focus.uvms.usm.policy.service.impl.PolicyProvider;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Timer;
import javax.ejb.*;
import java.util.*;
import java.util.Map.Entry;

/**
 * J2EE Singleton implementation of the SessionDao interface that
 * keeps active session in-memory.
 */
@Singleton
public class InMemorySessionDao implements SessionDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(InMemorySessionDao.class);

    private static final String POLICY_SUBJECT = "Account";
    private static final long ONE_SECOND = 1000L;
    private static final long FIVE_MINUTES = 300000L;

    @EJB
    private PolicyProvider policyProvider;

    private final Map<String, List<UserSession>> sessionMap;

    @Resource
    private TimerService timerService;

    public InMemorySessionDao() {
        Map<String, List<UserSession>> nakedMap = new HashMap<>();
        sessionMap = Collections.synchronizedMap(nakedMap);
    }

    @PostConstruct
    public void createTimer() {
        timerService.createTimer(FIVE_MINUTES, "InMemorySessionDao");
    }

    @Timeout
    public void timeout(Timer timer) {
        LOGGER.debug("timeout() - (ENTER)");

        Properties policy = policyProvider.getProperties(POLICY_SUBJECT);
        int ttlSession = policyProvider.getIntProperty(policy, "account.maxSessionDuration", 0);

        if (ttlSession > 0) {
            Date startedBefore = new Date(System.currentTimeMillis() -
                    (ttlSession * ONE_SECOND));
            LOGGER.info("startedBefore: " + startedBefore);

            List<String> expired = findExpiredSessions(startedBefore);
            for (String sessionId : expired) {

                LOGGER.info("deleteing expired session: " + sessionId);
                deleteSession(sessionId);
            }
        }

        LOGGER.debug("timeout() - (LEAVE)");
    }

    @Override
    public String createSession(UserSession session) {
        LOGGER.debug("createSession(" + session + ") - (ENTER)");

        String ret = null;

        if (session != null && session.getUserName() != null) {
            session.setUniqueId(UUID.randomUUID().toString());

            doCreate(session);

            ret = session.getUniqueId();
        }

        LOGGER.debug("createSession() - (LEAVE): " + ret);
        return ret;
    }

    @Override
    public UserSession readSession(String sessionId) {
        LOGGER.debug("readSession(" + sessionId + ") - (ENTER)");

        UserSession ret = null;

        if (sessionId != null) {
            ret = doReadSession(sessionId);
        }

        LOGGER.debug("readSession() - (LEAVE): " + ret);
        return ret;
    }

    @Override
    public List<UserSession> readSessions(String userName, Date startedAfter) {
        LOGGER.debug("readSessions(" + userName + ", " + startedAfter + ") - (ENTER)");

        List<UserSession> ret = new ArrayList<>();

        if (userName != null) {
            List<UserSession> lst = doReadSessions(userName);
            if (lst != null) {
                if (startedAfter == null) {
                    ret.addAll(lst);
                } else {
                    for (UserSession item : lst) {
                        if (startedAfter.before(item.getCreationTime())) {
                            ret.add(item);
                        }
                    }
                }
            }
        }

        LOGGER.debug("readSessions() - (LEAVE)");
        return ret;
    }

    @Override
    public void deleteSession(String sessionId) {
        LOGGER.debug("deleteSession(" + sessionId + ") - (ENTER)");

        if (sessionId != null) {
            doDelete(sessionId);
        }

        LOGGER.debug("deleteSession() - (LEAVE)");
    }

    @Override
    public void deleteSessions() throws RuntimeException {
        LOGGER.debug("deleteSessions() - (ENTER)");

        sessionMap.clear();

        LOGGER.debug("deleteSessions() - (LEAVE)");
    }

    private void doCreate(UserSession session) {
        List<UserSession> lst = sessionMap.get(session.getUserName());
        if (lst == null) {
            lst = new ArrayList<>();
        }
        lst.add(session);

        sessionMap.put(session.getUserName(), lst);
    }

    private List<UserSession> doReadSessions(String userName) {
        return sessionMap.get(userName);
    }

    private UserSession doReadSession(String sessionId) {
        UserSession ret = null;

        for (Entry<String, List<UserSession>> e : sessionMap.entrySet()) {
            for (UserSession s : e.getValue()) {
                if (sessionId.equals(s.getUniqueId())) {
                    ret = s;
                    break;
                }
            }
            if (ret != null) {
                break;
            }
        }

        return ret;
    }

    private void doDelete(String sessionId) {
        List<UserSession> lst = null;
        UserSession item = null;

        for (Entry<String, List<UserSession>> e : sessionMap.entrySet()) {
            for (UserSession s : e.getValue()) {
                if (sessionId.equals(s.getUniqueId())) {
                    lst = e.getValue();
                    item = s;
                    break;
                }
            }
            if (lst != null) {
                break;
            }
        }
        if (lst != null) {
            lst.remove(item);
        }
    }

    private List<String> findExpiredSessions(Date startedBefore) {
        List<String> ret = new ArrayList<>();

        for (Entry<String, List<UserSession>> entry : sessionMap.entrySet()) {
            for (UserSession item : entry.getValue()) {
                if (startedBefore.after(item.getCreationTime())) {
                    ret.add(item.getUniqueId());
                }
            }
        }

        return ret;
    }
}
