package fish.focus.uvms.usm.administration.domain;

import java.io.Serializable;

/**
 * Service request for the retrieval of a list of user roles.
 */
public class FindUserContextsQuery implements Serializable {
    private static final long serialVersionUID = 1L;
    private String userName;

    public FindUserContextsQuery() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "FindUserContextsQuery{" +
                "userName=" + userName +
                '}';
    }

}
