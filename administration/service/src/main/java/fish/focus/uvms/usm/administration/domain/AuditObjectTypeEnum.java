package fish.focus.uvms.usm.administration.domain;

public enum AuditObjectTypeEnum {
    USER("User"),
    CONTEXT("Context"),
    PASSWORD("Password"),
    CHALLENGE("Challenge"),
    SCOPE("Scope"),
    ROLE("Role"),
    POLICY("Policy"),
    CONTACT_DETAILS("Contact details"),
    CHANNEL("Channel"),
    ENDPOINT("Endpoint"),
    ORGANISATION("Organisation"),
    ENDPOINT_CONTACT("Endpoint contact");

    private String value;

    AuditObjectTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
