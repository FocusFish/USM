package fish.focus.uvms.usm.administration.service.person.impl;

import fish.focus.uvms.usm.administration.domain.ContactDetails;
import fish.focus.uvms.usm.administration.domain.Person;
import fish.focus.uvms.usm.administration.domain.ServiceRequest;
import fish.focus.uvms.usm.administration.domain.USMFeature;
import fish.focus.uvms.usm.administration.service.RequestValidator;
import javax.ejb.Stateless;

/**
 * Provides operations for the validation and authorisation of Person
 * related service requests
 */
@Stateless
public class PersonValidator extends RequestValidator {

    public PersonValidator() {
    }

    public void assertValid(ServiceRequest<Person> request, USMFeature feature, boolean isCreate) {
        assertValid(request, feature, "person");

        if (!isCreate) {
            assertNotNull("personId", request.getBody().getPersonId());
        }
    }

    public void assertValid(ServiceRequest<ContactDetails> request) {
        assertNotNull("request", request);
        assertNotEmpty("userName", request.getRequester());
        assertNotEmpty("password", request.getPassword());
        assertNotNull("contactDetails", request.getBody());
        assertNotEmpty("email", request.getBody().getEmail());
        assertNotTooLong("email", 64, request.getBody().getEmail());
        assertNotTooLong("faxNumber", 32, request.getBody().getFaxNumber());
        assertNotTooLong("phoneNumber", 32, request.getBody().getPhoneNumber());
        assertNotTooLong("mobileNumber", 32, request.getBody().getMobileNumber());
    }

}
