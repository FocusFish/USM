package fish.focus.uvms.usm.administration.domain;

import java.io.Serializable;

/**
 * Holds user details retrieved from an LDAP compatible Identity
 * Management system.
 */
public class LdapUser implements Serializable {
    private static final long serialVersionUID = 1L;

    private String userName;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String mobileNumber;
    private String faxNumber;
    private String email;

    public LdapUser() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getFaxNumber() {
        return faxNumber;
    }

    public void setFaxNumber(String faxNumber) {
        this.faxNumber = faxNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "LdapUser{"
                + "userName=" + userName
                + ", firstName=" + firstName
                + ", phoneNumber=" + phoneNumber
                + ", mobileNumber=" + mobileNumber
                + ", faxNumber=" + faxNumber
                + ", email=" + email
                + '}';
    }

}
