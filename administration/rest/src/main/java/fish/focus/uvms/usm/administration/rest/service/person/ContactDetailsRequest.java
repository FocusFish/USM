package fish.focus.uvms.usm.administration.rest.service.person;

import fish.focus.uvms.usm.administration.domain.ContactDetails;

/**
 * Holds a request to change a user contact details
 */
public class ContactDetailsRequest extends ContactDetails {
    private String userName;
    private String password;

    public ContactDetailsRequest() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
